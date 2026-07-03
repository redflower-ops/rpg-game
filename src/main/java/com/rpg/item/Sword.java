package com.rpg.item;

public class Sword implements Weapon{
    private final String name;
    private final int attackBonus;//攻击力加成
    public Sword(){
        this.name="铁剑";
        this.attackBonus=30;
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public int calculateAttack(int baseAttack){
        return baseAttack+attackBonus;
    }
    @Override
    public String getDescription(){
        return "一把锋利的铁剑，攻击力+" +attackBonus;
    }
}
