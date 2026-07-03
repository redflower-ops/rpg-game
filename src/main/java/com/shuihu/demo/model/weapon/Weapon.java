package com.shuihu.demo.model.weapon;

import java.util.HashMap;
import java.util.Map;

/**
 * 武器定义
 */
public class Weapon {
    private final WeaponType type;
    private final double baseAtkMultiplier;   // 基础普攻倍率
    private final Map<String, Object> passiveEffects;

    public Weapon(WeaponType type, double baseAtkMultiplier) {
        this.type = type;
        this.baseAtkMultiplier = baseAtkMultiplier;
        this.passiveEffects = new HashMap<>();
    }

    public WeaponType getType() { return type; }
    public double getBaseAtkMultiplier() { return baseAtkMultiplier; }
    public Map<String, Object> getPassiveEffects() { return passiveEffects; }
}
