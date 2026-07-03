package com.shuihu.demo.model.talent;

import com.shuihu.demo.model.skill.EffectType;
import java.util.List;
import java.util.Map;

/**
 * 天赋定义
 */
public class Talent {
    private final String id;
    private final String name;
    private final String description;
    private final List<TalentEffect> effects;

    public Talent(String id, String name, String description, List<TalentEffect> effects) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.effects = effects;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<TalentEffect> getEffects() { return effects; }

    /** 天赋效果内部类 */
    public static class TalentEffect {
        private final TriggerCondition trigger;
        private final EffectType effectType;
        private final Map<String, Object> params;

        public TalentEffect(TriggerCondition trigger, EffectType effectType, Map<String, Object> params) {
            this.trigger = trigger;
            this.effectType = effectType;
            this.params = params;
        }

        public TriggerCondition getTrigger() { return trigger; }
        public EffectType getEffectType() { return effectType; }
        public Map<String, Object> getParams() { return params; }
    }
}
