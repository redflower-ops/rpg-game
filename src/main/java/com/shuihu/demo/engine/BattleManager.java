package com.shuihu.demo.engine;

import com.shuihu.demo.ai.AiController;
import com.shuihu.demo.ai.BattleContext;
import com.shuihu.demo.event.*;
import com.shuihu.demo.model.entity.*;
import com.shuihu.demo.model.item.Inventory;
import com.shuihu.demo.model.skill.DamageType;
import com.shuihu.demo.model.skill.EffectType;
import com.shuihu.demo.model.skill.Skill;
import com.shuihu.demo.model.skill.SkillType;
import com.shuihu.demo.model.skill.SkillEffect;
import com.shuihu.demo.model.status.StatusManager;
import com.shuihu.demo.model.status.StatusType;
import com.shuihu.demo.model.weapon.WeaponEffectHandler;

import java.util.*;

/**
 * 战斗管理器 —— 主循环
 */
public class BattleManager {
    private Player player;
    private Enemy boss;
    private List<SummonedEntity> summons;
    private TurnManager turnManager;
    private BattleContext context;
    private Scanner scanner;
    private boolean battleEnded;
    private EnvironmentManager envManager;

    public BattleManager(Scanner scanner) {
        this.scanner = scanner;
    }

    public void startBattle(Player player, Enemy boss) {
        this.player = player;
        this.boss = boss;
        this.summons = new ArrayList<>();
        this.turnManager = new TurnManager(player, List.of(boss));
        this.context = new BattleContext();
        this.battleEnded = false;
        this.envManager = new EnvironmentManager();

        // 重置玩家战斗临时状态（保留HP/MP）
        player.getBattleStats().resetTempStats();
        StatusManager.clearAll(player);
        player.setDefending(false);
        player.setDrinkValue(0);
        player.setChainPoints(0);

        // 发布战斗开始事件
        EventBus.publish(new BattleStartEvent(player, boss));

        // Boss开场台词
        System.out.println("\n========== ⚔️ " + boss.getName() + " ⚔️ ==========");
        String line = boss.getOpeningLine();
        if (line != null && !line.isBlank()) {
            System.out.println("【" + boss.getName() + "】" + line);
        }

        // 主循环
        while (!battleEnded) {
            executeTurn();
        }
    }

    private void executeTurn() {
        context.setRound(context.getRound() + 1);

        // 遍历行动队列
        while (true) {
            Entity actor = turnManager.nextActor();

            if (actor == null) {
                // 新回合开始
                break;
            }

            if (!actor.isAlive()) {
                checkBattleEnd();
                return;
            }

            // 环境效果
            envManager.applyTurnStartEffects(actor);

            // 回合开始
            actor.onTurnStart();
            EventBus.publish(new TurnStartEvent(actor));
            if (!actor.isAlive()) {
                checkBattleEnd();
                return;
            }

            if (actor instanceof Player p) {
                executePlayerTurn(p);
            } else if (actor instanceof Enemy e) {
                executeEnemyTurn(e);
            }

            actor.onTurnEnd();
            EventBus.publish(new TurnEndEvent(actor));

            checkBattleEnd();
            if (battleEnded) return;
        }

        // 回合结束：检测阶段转换
        checkPhaseTransition();
    }

    private void executePlayerTurn(Player player) {
        // 状态行
        System.out.println("\n━━━ 你的回合 ━━━");
        System.out.printf("HP: %d/%d  MP: %d/%d  酒意: %d/100  连击点: %d/5%n",
                player.getBattleStats().getCurrentHp(),
                player.getBaseStats().getMaxHp(),
                player.getBattleStats().getCurrentMp(),
                player.getBaseStats().getMaxMp(),
                player.getDrinkValue(),
                player.getChainPoints());

        // 显示Boss状态
        displayBossStatus();

        // 眩晕判定
        if (StatusManager.hasStatus(player, StatusType.STUN)) {
            System.out.println("【眩晕】你被眩晕了，无法行动！");
            StatusManager.remove(player, StatusType.STUN);
            return;
        }

        // 防御检测
        player.setDefending(false);

        // 显示菜单
        boolean validChoice = false;
        while (!validChoice && !battleEnded) {
            System.out.println("\n请选择行动：");
            System.out.println("1. 普通攻击");
            System.out.println("2. 使用技能");
            System.out.println("3. 使用道具");
            System.out.println("4. 防御");
            System.out.print("> ");

            int choice;
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            } catch (Exception e) {
                if (scanner.hasNextLine()) scanner.nextLine();
                System.out.println("无效输入，请输入数字。");
                continue;
            }

            switch (choice) {
                case 1 -> {
                    normalAttack();
                    validChoice = true;
                    context.setPlayerDefendedLastTurn(false);
                }
                case 2 -> {
                    validChoice = showSkillMenu();
                    context.setPlayerDefendedLastTurn(false);
                }
                case 3 -> {
                    validChoice = showItemMenu();
                    context.setPlayerDefendedLastTurn(false);
                }
                case 4 -> {
                    defend();
                    validChoice = true;
                    context.setPlayerDefendedLastTurn(true);
                }
                default -> System.out.println("无效选择。");
            }
        }

        // 后处理
        player.addDrinkValue(8); // 行动+8酒意
    }

    private void normalAttack() {
        Skill normalAttack = player.getSkills().get(0);
        // 找Boss或召唤物中存活的
        Entity target = findTarget();
        if (target == null) return;

        Skill actualSkill = new Skill(normalAttack.getId(), normalAttack.getName(),
                normalAttack.getType(), normalAttack.getMpCost(), normalAttack.getHpCost(),
                normalAttack.getCooldown(), normalAttack.getDamageType(),
                normalAttack.getEffects());
        CombatResolver.executeSkill(player, target, actualSkill, context);
        WeaponEffectHandler.applyWeaponPassive(
                player.getWeapon() != null ? player.getWeapon().getType() : null,
                player, target);
        player.addDrinkValue(5); // 普攻命中+5
    }

    private boolean showSkillMenu() {
        List<Skill> available = player.getSkills().stream()
                .filter(s -> s.getType() != SkillType.PASSIVE)
                .filter(s -> s.getCurrentCd() == 0)
                .filter(s -> player.getBattleStats().getCurrentMp() >= s.getMpCost())
                .filter(s -> player.getBattleStats().getCurrentHp() > s.getHpCost())
                .toList();

        if (available.isEmpty()) {
            System.out.println("所有技能都在冷却或MP不足。");
            return false;
        }

        System.out.println("\n可用技能：");
        for (int i = 0; i < available.size(); i++) {
            Skill s = available.get(i);
            String desc = s.getDescription();
            System.out.printf("%d. %s (MP:%d, 冷却:%d)%n", i + 1, s.getName(), s.getMpCost(), s.getCooldown());
            if (desc != null && !desc.isBlank()) {
                System.out.println("   └ " + desc);
            }
        }
        System.out.println("0. 返回");
        System.out.print("> ");

        int choice;
        try {
            choice = scanner.nextInt();
            scanner.nextLine();
        } catch (Exception e) {
            if (scanner.hasNextLine()) scanner.nextLine();
            return false;
        }
        if (choice <= 0 || choice > available.size()) return false;

        Skill chosen = available.get(choice - 1);
        Entity target = findTarget();
        if (target == null) return false;

        CombatResolver.executeSkill(player, target, chosen, context);
        return true;
    }

    private boolean showItemMenu() {
        Inventory inv = player.getInventory();
        if (inv.getItems().isEmpty()) {
            System.out.println("背包为空。");
            return false;
        }

        System.out.println("\n背包：");
        var items = inv.getItems();
        for (int i = 0; i < items.size(); i++) {
            System.out.printf("%d. %s — %s%n", i + 1, items.get(i).getName(), items.get(i).getDescription());
        }
        System.out.println("0. 返回");
        System.out.print("> ");

        int choice;
        try {
            choice = scanner.nextInt();
            scanner.nextLine();
        } catch (Exception e) {
            if (scanner.hasNextLine()) scanner.nextLine();
            return false;
        }
        if (choice <= 0 || choice > items.size()) return false;

        var item = inv.use(items.get(choice - 1).getId());
        if (item != null) {
            System.out.println("使用了 " + item.getName());
            if (item.getEffectType() == EffectType.HEAL) {
                Object val = item.getParams().get("amount");
                if (val instanceof Number n) {
                    player.getBattleStats().heal(n.intValue());
                    System.out.println("恢复 " + n.intValue() + " HP");
                }
            }
        }
        return true;
    }

    private void defend() {
        StatusManager.apply(player, StatusType.DEFEND, 1, 1);
        player.setDefending(true);
        System.out.println("【防御】你进入防御姿态，本回合受到伤害减半。");
    }

    private void displayBossStatus() {
        System.out.printf("【敌】%s HP: %d/%d",
                boss.getName(), boss.getBattleStats().getCurrentHp(), boss.getBaseStats().getMaxHp());
        if (boss.getBattleStats().getShield() > 0) {
            System.out.printf(" (护盾:%d)", boss.getBattleStats().getShield());
        }
        System.out.println();
        for (SummonedEntity s : summons) {
            if (s.isAlive()) {
                System.out.printf("   ├ %s HP: %d/%d%n",
                        s.getName(), s.getBattleStats().getCurrentHp(), s.getBaseStats().getMaxHp());
            }
        }
    }

    /** 执行敌人回合 */
    private void executeEnemyTurn(Enemy enemy) {
        // 眩晕检查
        if (StatusManager.hasStatus(enemy, StatusType.STUN)) {
            System.out.println("【眩晕】" + enemy.getName() + "被眩晕了！");
            StatusManager.remove(enemy, StatusType.STUN);
            return;
        }

        // 环境效果
        envManager.applyTurnStartEffects(enemy);

        // AI决策
        AiController ai = enemy.getAiController();
        if (ai != null) {
            Skill chosen = ai.decideNextMove(enemy, player, context);
            if (chosen != null) {
                Entity target = player;
                CombatResolver.executeSkill(enemy, target, chosen, context);
            }
        }
    }

    /** 检测胜负 */
    private void checkBattleEnd() {
        if (!player.isAlive()) {
            battleEnded = true;
            System.out.println("\n========== 💀 败北 💀 ==========");
            System.out.println("你被 " + boss.getName() + " 击败了……");
            EventBus.publish(new BattleEndEvent(player, boss, "defeat"));
            return;
        }
        if (!boss.isAlive() && summons.stream().noneMatch(Entity::isAlive)) {
            battleEnded = true;
            System.out.println("\n========== 🏆 胜利 🏆 ==========");
            System.out.println("你击败了 " + boss.getName() + "！");
            EventBus.publish(new BattleEndEvent(player, boss, "victory"));
        }
    }

    /** 检测Boss阶段转换 */
    private void checkPhaseTransition() {
        if (boss.isPhaseTriggered()) return;

        int hpPercent = boss.getBattleStats().getCurrentHp() * 100 / boss.getBaseStats().getMaxHp();

        // 宋江召唤阶段
        if ("song_jiang".equals(boss.getId())) {
            if (hpPercent <= 75 && hpPercent > 50) {
                triggerSummonPhase(1);
            } else if (hpPercent <= 50 && hpPercent > 25) {
                triggerSummonPhase(2);
            } else if (hpPercent <= 25) {
                triggerSummonPhase(3);
            }
        }

        // 关胜半血觉醒
        if ("guan_sheng".equals(boss.getId()) && hpPercent < 50 && !boss.isPhaseTriggered()) {
            boss.setPhaseTriggered(true);
            System.out.println("【关羽觉醒】青龙偃月刀——觉醒！");
            EventBus.publish(new PhaseTransitionEvent(boss, "青龙觉醒"));
        }
    }

    private void triggerSummonPhase(int phase) {
        boss.setPhaseTriggered(true);
        System.out.println("【宋江】兄弟们，现身吧！");
        // 简化：打印召唤信息
        System.out.println("(召唤阶段 " + phase + " 触发)");
        EventBus.publish(new PhaseTransitionEvent(boss, "召唤阶段" + phase));
    }

    private Entity findTarget() {
        // 优先攻击召唤物
        for (SummonedEntity s : summons) {
            if (s.isAlive()) return s;
        }
        return boss;
    }
}
