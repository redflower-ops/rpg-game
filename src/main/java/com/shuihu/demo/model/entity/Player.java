package com.shuihu.demo.model.entity;

import com.shuihu.demo.model.item.Inventory;
import com.shuihu.demo.model.stats.Stats;
import com.shuihu.demo.model.talent.Talent;
import com.shuihu.demo.model.weapon.Weapon;

/**
 * 主角 —— 继承Entity + 武器/天赋/背包/酒意/连击点
 */
public class Player extends Entity {
    private Weapon weapon;
    private Talent talent;
    private Inventory inventory;
    private int drinkValue;       // 酒意值 0~100
    private int chainPoints;      // 连击点 0~5（拳套）
    private boolean isDefending;
    private int skillLevel1;      // 【破绽打击】等级 1~3
    private int skillLevel2;      // 【凝神纳气】等级 1~3
    private int skillLevel3;      // 【殊死一搏】等级 1~3
    private int exp;              // 当前经验
    private int gold;             // 金币

    public Player() {
        super();
        this.inventory = new Inventory(5);
        this.drinkValue = 0;
        this.chainPoints = 0;
        this.isDefending = false;
        this.skillLevel1 = 1;
        this.skillLevel2 = 1;
        this.skillLevel3 = 1;
        this.exp = 0;
        this.gold = 0;
    }

    /** 每回合开始：加5酒意 */
    @Override
    public void onTurnStart() {
        super.onTurnStart();
        drinkValue = Math.min(100, drinkValue + 5);
    }

    /** 添加经验并检测升级 */
    public void addExp(int amount) {
        this.exp += amount;
        int needed = getExpToLevelUp();
        while (this.exp >= needed && level < 20) {
            this.exp -= needed;
            levelUp();
            needed = getExpToLevelUp();
        }
    }

    /** 升一级 */
    private void levelUp() {
        level++;
        // 属性成长
        int oldMaxHp = baseStats.getMaxHp();
        int oldMaxMp = baseStats.getMaxMp();
        baseStats = new Stats(
            oldMaxHp + 20,
            oldMaxMp + 8,
            baseStats.getAtk() + 3,
            baseStats.getDef() + 2,
            baseStats.getSpd() + 1,
            baseStats.getMres() + 1
        );
        // 同步战斗面板的maxHpCache/maxMpCache并回满
        battleStats.setMaxHpCache(baseStats.getMaxHp());
        battleStats.setMaxMpCache(baseStats.getMaxMp());
        battleStats.setCurrentHp(baseStats.getMaxHp());
        battleStats.setCurrentMp(baseStats.getMaxMp());
        System.out.println("🎉 恭喜升级！当前等级 " + level + "，全属性提升！");
    }

    public int getExpToLevelUp() {
        return level * 50;
    }

    public int getExp() { return exp; }
    public void setExp(int v) { this.exp = Math.max(0, v); }
    public int getGold() { return gold; }
    public void setGold(int v) { this.gold = Math.max(0, v); }
    public void addGold(int v) { this.gold += v; }

    public Weapon getWeapon() { return weapon; }
    public void setWeapon(Weapon weapon) { this.weapon = weapon; }
    public Talent getTalent() { return talent; }
    public void setTalent(Talent talent) { this.talent = talent; }
    public Inventory getInventory() { return inventory; }
    public void setInventory(Inventory inv) { this.inventory = inv; }
    public int getDrinkValue() { return drinkValue; }
    public void setDrinkValue(int v) { this.drinkValue = Math.min(100, Math.max(0, v)); }
    public void addDrinkValue(int v) { this.drinkValue = Math.min(100, this.drinkValue + v); }
    public int getChainPoints() { return chainPoints; }
    public void setChainPoints(int v) { this.chainPoints = Math.min(5, Math.max(0, v)); }
    public boolean isDefending() { return isDefending; }
    public void setDefending(boolean v) { this.isDefending = v; }
    public int getSkillLevel1() { return skillLevel1; }
    public void setSkillLevel1(int v) { this.skillLevel1 = v; }
    public int getSkillLevel2() { return skillLevel2; }
    public void setSkillLevel2(int v) { this.skillLevel2 = v; }
    public int getSkillLevel3() { return skillLevel3; }
    public void setSkillLevel3(int v) { this.skillLevel3 = v; }
}
