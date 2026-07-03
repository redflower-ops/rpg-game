package com.rpg.model;

public class Slime implements Monster {
    private int hp;
    private final int maxHp;
    private final int attack;
    private final int defense;
    private final int expReward;
    private final int goldReward;

    public Slime() {
        this.hp = 20;
        this.maxHp = 20;
        this.attack = 5;
        this.defense = 2;
        this.expReward = 10;
        this.goldReward = 5;
    }

    @Override
    public String getName() { return "史莱姆"; }

    @Override
    public int getHp() { return hp; }

    @Override
    public int getMaxHp() { return maxHp; }

    @Override
    public int getAttack() { return attack; }

    @Override
    public int getDefense() { return defense; }

    @Override
    public int getExpReward() { return expReward; }

    @Override
    public int getGoldReward() { return goldReward; }

    @Override
    public void takeDamage(int damage) {
        this.hp -= damage;
        if (this.hp < 0) this.hp = 0;
    }

    @Override
    public boolean isAlive() { return hp > 0; }

    @Override
    public void specialAction(Player player) {
        // 史莱姆没有特殊技能
    }
}