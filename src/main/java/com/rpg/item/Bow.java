package com.rpg.item;
import java.util.Random;
public class Bow implements  Weapon{
    private final  String name;

    Random rand=new Random();
    public Bow(){
        this.name="长弓";

    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public int calculateAttack(int baseAttack){
        return baseAttack+rand.nextInt(26)+20;
    }
    @Override
    public String getDescription(){
        return "一把长弓，攻击力+20~45";
    }
}
