package com.shuihu.demo.engine;

import com.shuihu.demo.model.entity.Entity;

import java.util.*;
import java.util.function.Consumer;

/**
 * 战场环境管理器
 */
public class EnvironmentManager {
    private final Map<String, EnvironmentEffect> activeEnvironments = new HashMap<>();

    /** 创建风暴环境（公孙胜战） */
    public static EnvironmentEffect createStorm() {
        return new EnvironmentEffect(
            "storm", "呼风唤雨",
            entity -> {
                // 每回合造成 ATK×0.1 真实伤害
                int dmg = (int)(entity.getBaseStats().getAtk() * 0.1);
                entity.getBattleStats().takeDamage(dmg);
                System.out.println("【风暴】" + entity.getName() + "受到 " + dmg + " 点环境伤害");
            },
            entity -> {}
        );
    }

    public void activate(String envId, EnvironmentEffect effect) {
        activeEnvironments.put(envId, effect);
    }

    public void deactivate(String envId) {
        activeEnvironments.remove(envId);
    }

    public void applyTurnStartEffects(Entity entity) {
        for (EnvironmentEffect env : activeEnvironments.values()) {
            if (env.onTurnStart != null) env.onTurnStart.accept(entity);
        }
    }

    public void applyTurnEndEffects(Entity entity) {
        for (EnvironmentEffect env : activeEnvironments.values()) {
            if (env.onTurnEnd != null) env.onTurnEnd.accept(entity);
        }
    }

    public boolean isActive(String envId) {
        return activeEnvironments.containsKey(envId);
    }

    public record EnvironmentEffect(
        String id,
        String name,
        Consumer<Entity> onTurnStart,
        Consumer<Entity> onTurnEnd
    ) {}
}
