package com.rpg.item;

public interface Weapon {
    /** 武器名称*/
    String getName();
    /** 计算装备武器后的攻击力*/
    int calculateAttack(int baseAttack);
    /* 武器描述*/
    String getDescription();

}
