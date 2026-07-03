package com.shuihu.demo.event;

import com.shuihu.demo.model.entity.Enemy;

public class PhaseTransitionEvent implements GameEvent {
    private final Enemy boss;
    private final String phaseName;

    public PhaseTransitionEvent(Enemy boss, String phaseName) { this.boss = boss; this.phaseName = phaseName; }
    public Enemy getBoss() { return boss; }
    public String getPhaseName() { return phaseName; }
}
