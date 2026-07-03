package com.shuihu.demo.event;

import com.shuihu.demo.model.entity.Entity;
import com.shuihu.demo.model.status.StatusType;

public class StatusAppliedEvent implements GameEvent {
    private final Entity target;
    private final StatusType type;
    private final int stacks;

    public StatusAppliedEvent(Entity target, StatusType type, int stacks) {
        this.target = target; this.type = type; this.stacks = stacks;
    }
    public Entity getTarget() { return target; }
    public StatusType getType() { return type; }
    public int getStacks() { return stacks; }
}
