package com.shuihu.demo.model.entity;

import com.shuihu.demo.model.skill.Skill;
import com.shuihu.demo.model.stats.BattleStats;
import com.shuihu.demo.model.stats.Stats;
import com.shuihu.demo.model.status.Status;
import com.shuihu.demo.model.status.StatusManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 战斗实体抽象基类
 */
public abstract class Entity {
    protected String id;
    protected String name;
    protected int level;
    protected Stats baseStats;
    protected BattleStats battleStats;
    protected List<Skill> skills;
    protected List<Status> statusEffects;

    public Entity() {
        this.statusEffects = new ArrayList<>();
    }

    /** 回合开始：状态伤害 + 冷却-1 + 修饰器tick */
    public void onTurnStart() {
        StatusManager.tickDamage(this);
        StatusManager.expireCheck(this);
        for (Skill s : skills) {
            s.tickCooldown();
        }
        battleStats.tickModifiers();
    }

    /** 回合结束清理 */
    public void onTurnEnd() {
        // 子类可重写
    }

    /** 清空所有状态效果 */
    public void clearStatus() {
        this.statusEffects.clear();
    }

    public boolean isAlive() { return battleStats != null && battleStats.isAlive(); }

    // === Getter/Setter ===
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public Stats getBaseStats() { return baseStats; }
    public void setBaseStats(Stats stats) { this.baseStats = stats; }
    public BattleStats getBattleStats() { return battleStats; }
    public void setBattleStats(BattleStats battleStats) { this.battleStats = battleStats; }
    public List<Skill> getSkills() { return skills; }
    public void setSkills(List<Skill> skills) { this.skills = skills; }
    public List<Status> getStatusEffects() { return statusEffects; }
    public void setStatusEffects(List<Status> list) { this.statusEffects = list; }

    public String getOpeningLine() { return ""; }
}
