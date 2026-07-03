package com.shuihu.demo.event;

import com.shuihu.demo.model.entity.Entity;

public class TurnStartEvent implements GameEvent {
    private final Entity entity;
    public TurnStartEvent(Entity entity) { this.entity = entity; }
    public Entity getEntity() { return entity; }
}
