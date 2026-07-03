package com.rpg.model;

import com.rpg.item.Weapon;
import java.util.Map;

public class Player {
    private String name;
    private int level;
    private int exp;
    private int maxHp;
    private int currentHp;
    private int attack;
    private int defense;
    private int gold;
    private Weapon currentWeapon;
    private Map<String, Integer> bag;

    public Player(String name, int level, int maxHp, int exp, int currentHp, int attack, int defense, int gold, Weapon currentWeapon, Map<String, Integer> bag) {
        this.name = name;
        this.level = level;
        this.maxHp = maxHp;
        this.exp = exp;
        this.currentHp = currentHp;
        this.attack = attack;
        this.defense = defense;
        this.gold = gold;
        this.currentWeapon = currentWeapon;
        this.bag = bag;
    }
    // === Getter ===
    public String getName() { return name; }
    public int getCurrentHp() { return currentHp; }
    public int getMaxHp() { return maxHp; }
    public int getLevel() { return level; }
    public int getExp() { return exp; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getGold() { return gold; }
    public Weapon getCurrentWeapon() { return currentWeapon; }
    public Map<String, Integer> getBag() { return bag; }

    // === Setter ===
    public void setCurrentWeapon(Weapon currentWeapon) { this.currentWeapon = currentWeapon; }

    // === 行为方法 ===

    /** 受伤 */
    public void takeDamage(int damage) {
        this.currentHp -= damage;
        if (this.currentHp < 0) {
            this.currentHp = 0;
        }
    }

    /** 是否存活 */
    public boolean isAlive() {
        return currentHp > 0;
    }

    /** 回血（不超过最大血量） */
    public void heal(int amount) {
        this.currentHp += amount;
        if (this.currentHp > this.maxHp) {
            this.currentHp = this.maxHp;
        }
    }

    /** 加金币 */
    public void addGold(int amount) {
        this.gold += amount;
        if (this.gold < 0) this.gold = 0;  // 防止被偷成负数
    }

    /** 加经验，达到阈值自动升级 */
    public void addExp(int amount) {
        this.exp += amount;
        int threshold = getExpThreshold();
        while (this.exp >= threshold) {
            this.exp -= threshold;
            levelUp();
            threshold = getExpThreshold();
        }
    }

    /** 升一级需要多少经验 */
    private int getExpThreshold() {
        return level * 30;
    }

    /** 升级 */
    private void levelUp() {
        level++;
        maxHp += 20;
        attack += 3;
        defense += 2;
        currentHp = maxHp;  // 升级满血
        System.out.printf("🎉 升级！当前等级：%d，全属性提升！%n", level);
    }
}