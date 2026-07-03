package com.shuihu.demo.model.stats;

/**
 * 属性修正器 —— 用于Buff/Debuff临时改属性
 */
public class StatModifier {
    private final String statName;   // "atk" / "def" / "spd" / "mres"
    private final double value;      // +0.2 = +20%, -0.1 = -10%
    private int remainingTurns;      // -1 = 永久（本局），N = 持续N回合

    public StatModifier(String statName, double value, int remainingTurns) {
        this.statName = statName;
        this.value = value;
        this.remainingTurns = remainingTurns;
    }

    public String getStatName() { return statName; }
    public double getValue() { return value; }
    public int getRemainingTurns() { return remainingTurns; }
    public void decreaseTurn() { if (remainingTurns > 0) remainingTurns--; }
    public boolean isExpired() { return remainingTurns == 0; }
}
