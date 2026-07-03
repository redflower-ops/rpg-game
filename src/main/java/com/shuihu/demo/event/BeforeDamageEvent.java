package com.shuihu.demo.event;

import com.shuihu.demo.model.entity.Entity;
import com.shuihu.demo.model.skill.Skill;

public class BeforeDamageEvent implements GameEvent {
    private final Entity attacker;
    private final Entity defender;
    private final Skill skill;
    private int damage;

    public BeforeDamageEvent(Entity attacker, Entity defender, Skill skill, int damage) {
        this.attacker = attacker; this.defender = defender; this.skill = skill; this.damage = damage;
    }
    public Entity getAttacker() { return attacker; }
    public Entity getDefender() { return defender; }
    public Skill getSkill() { return skill; }
    public int getDamage() { return damage; }
    public void setDamage(int damage) { this.damage = damage; }
}
