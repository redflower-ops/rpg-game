package com.rpg.model;

public class Dragon implements Monster {
    private int hp;
    private final int maxHp;
    private final int attack;
    private final int defense;
    private final int expReward;
    private final int goldReward;
    private boolean specialUsed = false;

    public Dragon() {
        this.hp = 100;
        this.maxHp = 100;
        this.attack = 20;
        this.defense = 10;
        this.expReward = 80;
        this.goldReward = 50;
    }

    @Override
    public String getName() { return "幼龙"; }

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
        if (specialUsed) return;
        specialUsed = true;
        int fireDamage = 15;
        System.out.println("🔥 幼龙喷出一股火焰，造成 " + fireDamage + " 点额外伤害！");
        player.takeDamage(fireDamage);
    }
}