package com.rpg.model;

import java.util.Random;

public class Goblin implements Monster {
    private int hp;
    private final int maxHp;
    private final int attack;
    private final int defense;
    private final int expReward;
    private final int goldReward;
    private boolean specialUsed = false;
    private final Random rand = new Random();

    public Goblin() {
        this.hp = 40;
        this.maxHp = 40;
        this.attack = 10;
        this.defense = 4;
        this.expReward = 25;
        this.goldReward = 15;
    }

    @Override
    public String getName() { return "贪财哥布林"; }

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
        int stolen = rand.nextInt(10) + 5;
        player.addGold(-stolen);
        System.out.println("⚡ 哥布林贼手偷走了你 " + stolen + " 金币！");
    }
}