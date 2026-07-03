package com.shuihu.demo.model.skill;

import java.util.Map;

/**
 * 技能效果定义
 */
public class SkillEffect {
    private final EffectType type;
    private final String formula;   // "caster.atk * 1.2 + target.lostHp * 0.1"
    private final double chance;    // 触发概率 0.5 = 50%
    private final Map<String, Object> params; // 额外参数

    public SkillEffect(EffectType type, String formula, double chance, Map<String, Object> params) {
        this.type = type;
        this.formula = formula;
        this.chance = chance;
        this.params = params;
    }

    public EffectType getType() { return type; }
    public String getFormula() { return formula; }
    public double getChance() { return chance; }
    public Map<String, Object> getParams() { return params; }
}
