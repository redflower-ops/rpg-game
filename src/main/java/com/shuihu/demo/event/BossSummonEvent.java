package com.shuihu.demo.event;

import com.shuihu.demo.model.entity.Enemy;
import com.shuihu.demo.model.entity.SummonedEntity;

public class BossSummonEvent implements GameEvent {
    private final Enemy boss;
    private final SummonedEntity summoned;

    public BossSummonEvent(Enemy boss, SummonedEntity summoned) { this.boss = boss; this.summoned = summoned; }
    public Enemy getBoss() { return boss; }
    public SummonedEntity getSummoned() { return summoned; }
}
