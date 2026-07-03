package com.shuihu.demo.event;

import com.shuihu.demo.model.entity.Entity;

public class TurnEndEvent implements GameEvent {
    private final Entity entity;
    public TurnEndEvent(Entity entity) { this.entity = entity; }
    public Entity getEntity() { return entity; }
}
