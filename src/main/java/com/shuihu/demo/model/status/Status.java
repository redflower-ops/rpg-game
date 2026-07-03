package com.shuihu.demo.model.status;

/**
 * 状态实例
 */
public class Status {
    private final StatusType type;
    private int stacks;
    private int remainingTurns;
    private final int maxStacks;
    private final String sourceId;

    public Status(StatusType type, int stacks, int duration, int maxStacks, String sourceId) {
        this.type = type;
        this.stacks = stacks;
        this.remainingTurns = duration;
        this.maxStacks = maxStacks;
        this.sourceId = sourceId;
    }

    public StatusType getType() { return type; }
    public int getStacks() { return stacks; }
    public void setStacks(int s) { this.stacks = Math.min(s, maxStacks); }
    public void addStacks(int s) { this.stacks = Math.min(this.stacks + s, maxStacks); }
    public int getRemainingTurns() { return remainingTurns; }
    public void setRemainingTurns(int t) { this.remainingTurns = t; }
    public void decreaseTurn() { if (remainingTurns > 0) remainingTurns--; }
    public boolean isExpired() { return remainingTurns <= 0; }
    public int getMaxStacks() { return maxStacks; }
    public String getSourceId() { return sourceId; }
}
