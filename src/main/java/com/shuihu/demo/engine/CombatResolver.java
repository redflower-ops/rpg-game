package com.shuihu.demo.engine;

import com.shuihu.demo.ai.BattleContext;
import com.shuihu.demo.event.*;
import com.shuihu.demo.model.Formula;
import com.shuihu.demo.model.entity.Entity;
import com.shuihu.demo.model.entity.Player;
import com.shuihu.demo.model.entity.Enemy;
import com.shuihu.demo.model.skill.*;
import com.shuihu.demo.model.status.StatusManager;
import com.shuihu.demo.model.status.StatusType;
import com.shuihu.demo.model.weapon.WeaponEffectHandler;
import com.shuihu.demo.model.weapon.WeaponType;

/**
 * 伤害结算引擎 —— 无状态，全部静态方法
 * 8步伤害管线
 */
public class CombatResolver {

    /**
     * 执行技能效果
     */
    public static void executeSkill(Entity attacker, Entity defender, Skill skill, BattleContext context) {
        // 步骤0：扣除消耗
        if (skill.getMpCost() > 0) {
            attacker.getBattleStats().setCurrentMp(
                attacker.getBattleStats().getCurrentMp() - skill.getMpCost());
        }
        if (skill.getHpCost() > 0) {
            attacker.getBattleStats().setCurrentHp(
                attacker.getBattleStats().getCurrentHp() - skill.getHpCost());
        }

        // 设置冷却
        if (skill.getCooldown() > 0) {
            skill.resetCooldown();
        }

        // 遍历所有效果
        for (SkillEffect effect : skill.getEffects()) {
            if (Math.random() > effect.getChance()) continue;

            switch (effect.getType()) {
                case DAMAGE -> executeDamage(attacker, defender, skill, effect, context);
                case HEAL -> executeHeal(attacker, defender, effect);
                case APPLY_STATUS -> executeApplyStatus(defender, effect);
                case REMOVE_STATUS -> executeRemoveStatus(defender, effect);
                case MODIFY_STAT -> executeModifyStat(defender, effect);
                case STEAL_MP -> executeStealMp(attacker, defender, effect);
                case FORCE_HP -> executeForceHp(defender, effect);
                case SHIELD -> executeShield(defender, effect);
                case SUMMON -> {
                    if (context != null) context.setPendingSummon(effect.getParams());
                }
                default -> {}
            }
        }

        EventBus.publish(new AfterAttackEvent(attacker, defender, skill, context));
    }

    // ========== 8步伤害管线 ==========

    private static void executeDamage(Entity attacker, Entity defender, Skill skill, SkillEffect effect, BattleContext context) {
        // 1. 命中判定
        if (!isHit(attacker, defender, skill)) {
            System.out.println(attacker.getName() + "的攻击未能命中" + defender.getName() + "！");
            return;
        }

        // 2. 招架判定（仅玩家用剑且防御时）
        if (defender instanceof Player p && p.isDefending() && p.getWeapon() != null && p.getWeapon().getType() == WeaponType.SWORD) {
            double parryChance = 0.15;
            if (Math.random() < parryChance) {
                double rawDmg = calculateRawDamage(attacker, defender, skill, effect);
                int finalDmg = (int)(rawDmg * 0.5);
                defender.getBattleStats().takeDamage(finalDmg);
                // 反击
                double counterAtk = p.getBattleStats().getEffectiveAtk(p.getBaseStats().getAtk()) * 0.8;
                attacker.getBattleStats().takeDamage((int)counterAtk);
                System.out.println("【招架】" + p.getName() + "招架了攻击并反击！");
                EventBus.publish(new AfterDamageEvent(attacker, defender, finalDmg, skill));
                return;
            }
        }

        // 3. 原始伤害计算
        double rawDamage = calculateRawDamage(attacker, defender, skill, effect);

        // 4. 伤害修正
        double modifiedDamage = applyModifiers(attacker, defender, rawDamage, skill, context);

        // 5. 防御减免
        double finalDamage = applyDefenseReduction(defender, modifiedDamage, skill);

        // 6. 特殊拦截
        finalDamage = applySpecialIntercepts(defender, finalDamage);

        // 7. 伤害应用
        int actualDamage = defender.getBattleStats().takeDamage((int)finalDamage);

        // 8. 后效
        applyPostDamageEffects(attacker, defender, skill, context);

        // 发布事件
        EventBus.publish(new AfterDamageEvent(attacker, defender, actualDamage, skill));

        // 战斗日志
        System.out.printf("[战斗] %s 对 %s 使用「%s」，造成 %d 点伤害！%s 剩余 %d/%d HP%n",
                attacker.getName(), defender.getName(), skill.getName(),
                actualDamage, defender.getName(),
                defender.getBattleStats().getCurrentHp(),
                defender.getBaseStats().getMaxHp());
    }

    // ===== 子方法 =====

    private static boolean isHit(Entity attacker, Entity defender, Skill skill) {
        // 暗器必定命中
        if (attacker instanceof Player p && p.getWeapon() != null
                && p.getWeapon().getType() == WeaponType.HIDDEN_WEAPON) {
            return true;
        }
        double hitChance = 1.0 - defender.getBattleStats().getDodgeRate()
                + attacker.getBattleStats().getHitRate();
        if (StatusManager.hasStatus(attacker, StatusType.BLIND)) hitChance -= 0.3;
        return Math.random() < Math.max(hitChance, 0.3);
    }

    private static double calculateRawDamage(Entity attacker, Entity defender, Skill skill, SkillEffect effect) {
        double value = Formula.evaluate(effect.getFormula(), attacker, defender, null);
        double baseAtk = attacker.getBattleStats().getEffectiveAtk(attacker.getBaseStats().getAtk());

        return switch (skill.getDamageType()) {
            case PHYSICAL -> baseAtk * Math.max(value, 0) - defender.getBaseStats().getDef() * 0.5;
            case MAGIC -> baseAtk * Math.max(value, 0) - defender.getBaseStats().getMres() * 0.5;
            case TRUE -> baseAtk * Math.max(value, 0);
            case PERCENT -> defender.getBaseStats().getMaxHp() * Math.max(value, 0);
        };
    }

    private static double applyModifiers(Entity attacker, Entity defender, double damage, Skill skill, BattleContext context) {
        double modified = damage;

        // 等级压制
        int levelDiff = attacker.getLevel() - defender.getLevel();
        if (levelDiff < 0) modified *= Math.max(1.0 + levelDiff * 0.02, 0.8);
        else if (levelDiff > 0) modified *= Math.min(1.0 + levelDiff * 0.01, 1.15);

        // 暴击判定
        double critRate = attacker.getBattleStats().getCritRate();
        if (Math.random() < critRate) {
            modified *= 2.0;
            if (context != null) context.setLastAttackCrit(true);
            System.out.println("【暴击】" + attacker.getName() + "触发暴击！");
        }

        // 枪流派标记穿透
        if (attacker instanceof Player p && p.getWeapon() != null
                && p.getWeapon().getType() == WeaponType.SPEAR) {
            if (context != null) context.setArmorPierce(0.3);
        }

        // 拳套连击点加成（仅普攻）
        if (attacker instanceof Player p && skill.getType() == SkillType.NORMAL
                && p.getWeapon() != null && p.getWeapon().getType() == WeaponType.GAUNTLET) {
            double bonus = 1.0 + p.getChainPoints() * 0.05;
            modified *= bonus;
        }

        return modified;
    }

    private static double applyDefenseReduction(Entity defender, double damage, Skill skill) {
        double afterDef = Math.max(damage, 0);

        // 枪流派穿透30%防御（在rawDamage中表现为额外穿透，这里用简化方式）
        if (skill.getDamageType() == DamageType.PHYSICAL) {
            double defVal = defender.getBaseStats().getDef() * 0.5;
            afterDef = Math.max(damage - defVal, damage * 0.1);
        }

        // 如果已有破甲状态
        if (StatusManager.hasStatus(defender, StatusType.ARMOR_BREAK)) {
            afterDef *= 1.3; // 等效提升伤害（DEF减半）
        }

        // 减伤率（上限80%）
        double totalDmgReduction = defender.getBattleStats().getDmgReduction();
        if (totalDmgReduction > 0.8) totalDmgReduction = 0.8;
        afterDef *= (1 - totalDmgReduction);

        // 防御姿态减半
        if (StatusManager.hasStatus(defender, StatusType.DEFEND)) {
            afterDef *= 0.5;
        }

        return Math.max(afterDef, 1); // 至少造成1点伤害
    }

    private static double applySpecialIntercepts(Entity defender, double damage) {
        // 未来：金钟罩/天罡星护体等
        return damage;
    }

    private static void applyPostDamageEffects(Entity attacker, Entity defender, Skill skill, BattleContext context) {
        // 武器被动触发（仅普攻）
        if (attacker instanceof Player p && p.getWeapon() != null && skill.getType() == SkillType.NORMAL) {
            WeaponEffectHandler.applyWeaponPassive(p.getWeapon().getType(), attacker, defender);
        }
    }

    // ===== 其他效果类型 =====

    private static void executeHeal(Entity caster, Entity target, SkillEffect effect) {
        double amount = Formula.evaluate(effect.getFormula(), caster, target, null);
        target.getBattleStats().heal((int)amount);
        System.out.println("【治疗】" + target.getName() + "恢复 " + (int)amount + " 点生命");
    }

    private static void executeApplyStatus(Entity target, SkillEffect effect) {
        if (effect.getParams() == null) return;
        String statusStr = (String) effect.getParams().getOrDefault("statusType", "BLEEDING");
        int stacks = (int) effect.getParams().getOrDefault("stacks", 1);
        int duration = (int) effect.getParams().getOrDefault("duration", 3);
        try {
            StatusType type = StatusType.valueOf(statusStr);
            StatusManager.apply(target, type, stacks, duration);
        } catch (Exception e) {
            // 静默忽略
        }
    }

    private static void executeRemoveStatus(Entity target, SkillEffect effect) {
        if (effect.getParams() != null && effect.getParams().containsKey("statusType")) {
            String s = (String) effect.getParams().get("statusType");
            if ("ALL".equals(s)) {
                StatusManager.clearAll(target);
            } else {
                try {
                    StatusManager.remove(target, StatusType.valueOf(s));
                } catch (Exception ignored) {}
            }
        } else {
            StatusManager.clearAll(target);
        }
    }

    private static void executeModifyStat(Entity target, SkillEffect effect) {
        // 简化实现：直接使用Formula值作为修正倍率
        String stat = (String) effect.getParams().getOrDefault("stat", "atk");
        double value = Double.parseDouble(effect.getFormula());
        int duration = (int) effect.getParams().getOrDefault("duration", 2);
        com.shuihu.demo.model.stats.StatModifier mod =
                new com.shuihu.demo.model.stats.StatModifier(stat, value, duration);
        target.getBattleStats().addModifier(mod);
    }

    private static void executeStealMp(Entity attacker, Entity defender, SkillEffect effect) {
        int stealAmount = (int) Formula.evaluate(effect.getFormula(), attacker, defender, null);
        int actualSteal = Math.min(stealAmount, defender.getBattleStats().getCurrentMp());
        defender.getBattleStats().setCurrentMp(defender.getBattleStats().getCurrentMp() - actualSteal);
        attacker.getBattleStats().setCurrentMp(attacker.getBattleStats().getCurrentMp() + actualSteal * 2);
        System.out.println(attacker.getName() + "吸取了" + defender.getName() + " " + actualSteal + " MP");

        if (defender.getBattleStats().getCurrentMp() < stealAmount) {
            int hpPenalty = stealAmount - defender.getBattleStats().getCurrentMp();
            defender.getBattleStats().takeDamage(hpPenalty);
            System.out.println(defender.getName() + "MP不足，额外损失 " + hpPenalty + " HP");
            defender.getBattleStats().setCurrentMp(0);
        }
    }

    private static void executeForceHp(Entity target, SkillEffect effect) {
        if (effect.getParams() == null) return;
        Object val = effect.getParams().get("value");
        if (val instanceof Number n) {
            target.getBattleStats().setCurrentHp(n.intValue());
        }
        if (effect.getParams().containsKey("destroyItem")) {
            System.out.println("一个道具被摧毁了！");
        }
    }

    private static void executeShield(Entity target, SkillEffect effect) {
        // 简化：直接用公式计算结果作为护盾值
        double value = Formula.evaluate(effect.getFormula(), target, target, null);
        target.getBattleStats().addShield((int)value);
        System.out.println("【护盾】" + target.getName() + "获得 " + (int)value + " 点护盾");
    }
}
