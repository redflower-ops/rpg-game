package com.shuihu.demo.event;

import com.shuihu.demo.model.entity.Entity;
import com.shuihu.demo.model.skill.Skill;

public class AfterDamageEvent implements GameEvent {
    private final Entity attacker;
    private final Entity defender;
    private final int actualDamage;
    private final Skill skill;

    public AfterDamageEvent(Entity attacker, Entity defender, int actualDamage, Skill skill) {
        this.attacker = attacker; this.defender = defender; this.actualDamage = actualDamage; this.skill = skill;
    }
    public Entity getAttacker() { return attacker; }
    public Entity getDefender() { return defender; }
    public int getActualDamage() { return actualDamage; }
    public Skill getSkill() { return skill; }
}
