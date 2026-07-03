package com.rpg.game;

import com.rpg.model.Player;
import com.rpg.model.Monster;
import java.util.Scanner;

/**
 * 战斗系统 —— 处理一次完整的战斗
 * 知识点：Scanner输入、循环控制、字符串格式化、条件分支
 */
public class BattleSystem {

    private final Player player;
    private final Monster monster;
    private final Scanner scanner;
    private boolean playerFled = false;

    public BattleSystem(Player player, Monster monster, Scanner scanner) {
        this.player = player;
        this.monster = monster;
        this.scanner = scanner;
    }

    /** 开始战斗，返回玩家是否存活 */
    public boolean start() {
        System.out.println("\n⚔️ 遭遇了 " + monster.getName() + "！");

        while (player.isAlive() && monster.isAlive() && !playerFled) {
            showStatus();
            playerTurn();
            if (!monster.isAlive()) {
                onMonsterDefeated();
                return true;
            }
            if (playerFled) {
                System.out.println("🏃 你成功逃跑了！");
                return true;
            }
            monsterTurn();
        }

        if (!player.isAlive()) {
            System.out.println("\n💀 你被 " + monster.getName() + " 杀死了...");
            return false;
        }

        return true;
    }

    /** 显示当前战况 */
    private void showStatus() {
        System.out.println("\n──────────────────────");
        System.out.printf("❤️  %s HP: %d/%d  |  💀 %s HP: %d/%d%n",
                player.getName(), player.getCurrentHp(), player.getMaxHp(),
                monster.getName(), monster.getHp(), monster.getMaxHp());
        System.out.println("──────────────────────");
    }

    /** 玩家回合 */
    private void playerTurn() {
        System.out.println("\n【你的回合】选择行动：");
        System.out.println("1. ⚔️  攻击");
        System.out.println("2. 🏃  逃跑");
        System.out.print("> ");

        int choice = scanner.nextInt();
        scanner.nextLine();  // 消耗换行符

        switch (choice) {
            case 1 -> playerAttack();
            case 2 -> tryFlee();
            default -> {
                System.out.println("无效选择，发呆了一回合...");
            }
        }
    }

    /** 玩家攻击 */
    private void playerAttack() {
        // 计算伤害：用当前武器
        int weaponAttack = player.getCurrentWeapon().calculateAttack(player.getAttack());
        int damage = weaponAttack - monster.getDefense() / 2;
        if (damage < 1) damage = 1;

        monster.takeDamage(damage);
        System.out.printf("🗡️  你使用 %s 攻击 %s，造成 %d 点伤害！%n",
                player.getCurrentWeapon().getName(), monster.getName(), damage);

        // 触发怪物特殊技能（第一次受伤时触发，设计上是开场触发一次）
        if (monster.isAlive()) {
            monster.specialAction(player);
        }
    }

    /** 尝试逃跑 */
    private void tryFlee() {
        this.playerFled = true;
    }

    /** 怪物回合 */
    private void monsterTurn() {
        System.out.println("\n【怪物回合】");

        int damage = monster.getAttack() - player.getDefense() / 2;
        if (damage < 1) damage = 1;

        player.takeDamage(damage);
        System.out.printf("💢  %s 攻击了你，造成 %d 点伤害！%n", monster.getName(), damage);
    }

    /** 怪物被击败 */
    private void onMonsterDefeated() {
        System.out.println("\n🎉 击败了 " + monster.getName() + "！");

        int expReward = monster.getExpReward();
        int goldReward = monster.getGoldReward();
        player.addExp(expReward);
        player.addGold(goldReward);
        System.out.printf("✨ +%d 经验值，💰 +%d 金币%n", expReward, goldReward);

        // 战胜后恢复少量生命
        int healAmount = 20;
        player.heal(healAmount);
        System.out.printf("🍞 休息恢复 %d 点生命（当前 HP: %d/%d）%n",
                healAmount, player.getCurrentHp(), player.getMaxHp());
    }
}