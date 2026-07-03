package com.shuihu.demo.event;

import com.shuihu.demo.model.entity.Entity;
import com.shuihu.demo.model.skill.Skill;
import com.shuihu.demo.ai.BattleContext;

public class AfterAttackEvent implements GameEvent {
    private final Entity attacker;
    private final Entity defender;
    private final Skill skill;
    private final BattleContext context;

    public AfterAttackEvent(Entity attacker, Entity defender, Skill skill, BattleContext context) {
        this.attacker = attacker; this.defender = defender; this.skill = skill; this.context = context;
    }
    public Entity getAttacker() { return attacker; }
    public Entity getDefender() { return defender; }
    public Skill getSkill() { return skill; }
    public BattleContext getContext() { return context; }
}
