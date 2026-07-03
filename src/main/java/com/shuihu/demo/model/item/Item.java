package com.shuihu.demo.model.item;

import com.shuihu.demo.model.skill.EffectType;
import java.util.Map;

/**
 * 道具定义
 */
public class Item {
    private final String id;
    private final String name;
    private final EffectType effectType;
    private final Map<String, Object> params;  // 效果参数
    private final String description;

    public Item(String id, String name, EffectType effectType, Map<String, Object> params, String description) {
        this.id = id;
        this.name = name;
        this.effectType = effectType;
        this.params = params;
        this.description = description;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public EffectType getEffectType() { return effectType; }
    public Map<String, Object> getParams() { return params; }
    public String getDescription() { return description; }
}
