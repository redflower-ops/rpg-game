package com.rpg.model;

public interface Monster {
    String getName();
    int getHp();
    int getMaxHp();
    int getAttack();
    int getDefense();
    int getExpReward();
    int getGoldReward();

    /** 受伤 */
    void takeDamage(int damage);

    /** 是否还活着 */
    boolean isAlive();

    /** 特殊行为 —— 每场战斗触发一次 */
    void specialAction(Player player);
}