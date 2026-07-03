package com.shuihu.demo.ai;

import java.util.HashMap;
import java.util.Map;

/**
 * 战斗上下文 —— 传递给AI决策器
 */
public class BattleContext {
    private int round;
    private boolean lastAttackCrit;
    private double armorPierce;
    private boolean parried;
    private boolean playerDefendedLastTurn;
    private Map<String, Object> pendingSummon;

    public BattleContext() {
        this.round = 0;
        this.lastAttackCrit = false;
        this.armorPierce = 0;
        this.parried = false;
        this.playerDefendedLastTurn = false;
        this.pendingSummon = new HashMap<>();
    }

    public int getRound() { return round; }
    public void setRound(int round) { this.round = round; }
    public boolean isLastAttackCrit() { return lastAttackCrit; }
    public void setLastAttackCrit(boolean v) { this.lastAttackCrit = v; }
    public double getArmorPierce() { return armorPierce; }
    public void setArmorPierce(double v) { this.armorPierce = v; }
    public boolean isParried() { return parried; }
    public void setParried(boolean v) { this.parried = v; }
    public boolean isPlayerDefendedLastTurn() { return playerDefendedLastTurn; }
    public void setPlayerDefendedLastTurn(boolean v) { this.playerDefendedLastTurn = v; }
    public Map<String, Object> getPendingSummon() { return pendingSummon; }
    public void setPendingSummon(Map<String, Object> v) { this.pendingSummon = v; }
}
