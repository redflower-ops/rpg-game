package com.shuihu.demo.model.stats;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态面板 —— 战斗中变化
 * 管理当前HP/MP、护盾、临时修正
 */
public class BattleStats {
    private int currentHp;
    private int currentMp;
    private int shield;
    private double atkMultiplier;     // 攻击倍率修正（默认1.0）
    private double defMultiplier;     // 防御倍率修正（默认1.0）
    private double dmgReduction;      // 额外减伤率（0.0~0.8）
    private double critRate;          // 暴击率
    private double dodgeRate;         // 闪避率
    private double hitRate;           // 命中率修正
    private List<StatModifier> modifiers;
    private int maxHpCache;           // 来自Stats.maxHp，战斗中可被永久改变
    private int maxMpCache;

    public BattleStats(int maxHp, int maxMp) {
        this.currentHp = maxHp;
        this.currentMp = maxMp;
        this.maxHpCache = maxHp;
        this.maxMpCache = maxMp;
        this.shield = 0;
        this.atkMultiplier = 1.0;
        this.defMultiplier = 1.0;
        this.dmgReduction = 0.0;
        this.critRate = 0.0;
        this.dodgeRate = 0.0;
        this.hitRate = 0.0;
        this.modifiers = new ArrayList<>();
    }

    // === 核心方法 ===

    /** 扣血（护盾优先抵扣），返回实际扣血量 */
    public int takeDamage(int amount) {
        int remaining = amount;
        if (shield > 0) {
            int absorbed = Math.min(remaining, shield);
            shield -= absorbed;
            remaining -= absorbed;
        }
        if (remaining > 0) {
            currentHp = Math.max(0, currentHp - remaining);
        }
        return amount - remaining; // 实际扣血量
    }

    /** 回血 */
    public void heal(int amount) {
        currentHp = Math.min(currentHp + amount, maxHpCache);
    }

    /** 是否存活 */
    public boolean isAlive() { return currentHp > 0; }

    /** 战斗间重置：清理所有临时战斗状态，保留HP/MP */
    public void resetTempStats() {
        this.shield = 0;
        this.atkMultiplier = 1.0;
        this.defMultiplier = 1.0;
        this.dmgReduction = 0.0;
        this.critRate = 0.0;
        this.dodgeRate = 0.0;
        this.hitRate = 0.0;
        this.modifiers.clear();
    }

    // === 属性计算 ===

    public double getEffectiveAtk(double baseAtk) {
        double val = baseAtk * atkMultiplier;
        for (StatModifier m : modifiers) {
            if ("atk".equals(m.getStatName())) val += baseAtk * m.getValue();
        }
        return Math.max(val, baseAtk * 0.1); // 不低于10%
    }

    public double getEffectiveDef(double baseDef) {
        double val = baseDef * defMultiplier;
        for (StatModifier m : modifiers) {
            if ("def".equals(m.getStatName())) val += baseDef * m.getValue();
        }
        return Math.max(val, 0);
    }

    public double getEffectiveSpd(double baseSpd) {
        double val = baseSpd;
        for (StatModifier m : modifiers) {
            if ("spd".equals(m.getStatName())) val += baseSpd * m.getValue();
        }
        return Math.max(val, baseSpd * 0.5);
    }

    // === Modifier管理 ===

    public void addModifier(StatModifier mod) { modifiers.add(mod); }

    public void removeModifiers(String statName) {
        modifiers.removeIf(m -> m.getStatName().equals(statName));
    }

    public void tickModifiers() {
        List<StatModifier> expired = new ArrayList<>();
        for (StatModifier m : modifiers) {
            m.decreaseTurn();
            if (m.isExpired()) expired.add(m);
        }
        modifiers.removeAll(expired);
    }

    public void clearModifiers() { modifiers.clear(); }

    // === Getter/Setter ===

    public int getCurrentHp() { return currentHp; }
    public void setCurrentHp(int hp) { this.currentHp = Math.max(0, hp); }

    public int getCurrentMp() { return currentMp; }
    public void setCurrentMp(int mp) { this.currentMp = Math.max(0, mp); }

    public int getShield() { return shield; }
    public void setShield(int shield) { this.shield = Math.max(0, shield); }
    public void addShield(int amount) { this.shield += amount; }

    public double getAtkMultiplier() { return atkMultiplier; }
    public void setAtkMultiplier(double v) { this.atkMultiplier = v; }

    public double getDefMultiplier() { return defMultiplier; }
    public void setDefMultiplier(double v) { this.defMultiplier = v; }

    public double getDmgReduction() { return dmgReduction; }
    public void setDmgReduction(double v) { this.dmgReduction = Math.min(v, 0.8); }

    public double getCritRate() { return critRate; }
    public void setCritRate(double v) { this.critRate = v; }

    public double getDodgeRate() { return dodgeRate; }
    public void setDodgeRate(double v) { this.dodgeRate = Math.min(v, 0.5); }

    public double getHitRate() { return hitRate; }
    public void setHitRate(double v) { this.hitRate = v; }

    public List<StatModifier> getModifiers() { return modifiers; }

    public int getMaxHpCache() { return maxHpCache; }
    public void setMaxHpCache(int v) { this.maxHpCache = Math.max(1, v); }

    public int getMaxMpCache() { return maxMpCache; }
    public void setMaxMpCache(int v) { this.maxMpCache = Math.max(0, v); }
}
