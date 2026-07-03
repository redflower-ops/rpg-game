package com.shuihu.demo.model.stats;

/**
 * 基础面板 —— 战斗中不可变
 */
public class Stats {
    private final int maxHp;
    private final int maxMp;
    private final int atk;
    private final int def;
    private final int spd;
    private final int mres;  // 法抗

    public Stats(int maxHp, int maxMp, int atk, int def, int spd, int mres) {
        this.maxHp = maxHp;
        this.maxMp = maxMp;
        this.atk = atk;
        this.def = def;
        this.spd = spd;
        this.mres = mres;
    }

    public int getMaxHp() { return maxHp; }
    public int getMaxMp() { return maxMp; }
    public int getAtk() { return atk; }
    public int getDef() { return def; }
    public int getSpd() { return spd; }
    public int getMres() { return mres; }
}
