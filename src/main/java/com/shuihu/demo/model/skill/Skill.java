package com.shuihu.demo.model.skill;

import java.util.List;

/**
 * 技能定义
 */
public class Skill {
    private final String id;
    private final String name;
    private final SkillType type;
    private final int mpCost;
    private final int hpCost;
    private final int cooldown;       // 总冷却
    private int currentCd;            // 当前冷却
    private final DamageType damageType;
    private final List<SkillEffect> effects;
    private final String description; // 可选描述

    public Skill(String id, String name, SkillType type, int mpCost, int hpCost,
                 int cooldown, DamageType damageType, List<SkillEffect> effects) {
        this(id, name, type, mpCost, hpCost, cooldown, damageType, effects, "");
    }

    public Skill(String id, String name, SkillType type, int mpCost, int hpCost,
                 int cooldown, DamageType damageType, List<SkillEffect> effects, String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.mpCost = mpCost;
        this.hpCost = hpCost;
        this.cooldown = cooldown;
        this.currentCd = 0;
        this.damageType = damageType;
        this.effects = effects;
        this.description = description;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public SkillType getType() { return type; }
    public int getMpCost() { return mpCost; }
    public int getHpCost() { return hpCost; }
    public int getCooldown() { return cooldown; }
    public int getCurrentCd() { return currentCd; }
    public void setCurrentCd(int cd) { this.currentCd = cd; }
    public void resetCooldown() { this.currentCd = cooldown; }
    public void tickCooldown() { if (currentCd > 0) currentCd--; }
    public DamageType getDamageType() { return damageType; }
    public List<SkillEffect> getEffects() { return effects; }
    public String getDescription() { return description; }
}
