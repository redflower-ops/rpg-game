package com.rpg.item;

public class Staff implements Weapon{
    private final String name;
    private final int physicalAttackBonus;
    public Staff(){
        this.name="法杖";
        this.physicalAttackBonus=15;
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public int calculateAttack(int baseAttack){
        int magicalAttackBonus= (int)(baseAttack*0.3);
        return baseAttack+physicalAttackBonus+magicalAttackBonus;
    }
    @Override
    public String getDescription(){
        return "一把法杖，攻击力+15，额外加"+"30%"+"法术伤害";
    }
}
