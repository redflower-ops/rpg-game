package com.shuihu.demo.model.entity;

/**
 * 召唤物 —— 继承Enemy + 归属 + 存活回合限制
 */
public class SummonedEntity extends Enemy {
    private String ownerId;
    private int maxLifetime;   // -1 = 永久

    public SummonedEntity() {
        super();
        this.maxLifetime = -1;
    }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String id) { this.ownerId = id; }
    public int getMaxLifetime() { return maxLifetime; }
    public void setMaxLifetime(int v) { this.maxLifetime = v; }
}
