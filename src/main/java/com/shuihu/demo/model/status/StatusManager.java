package com.shuihu.demo.model.status;

import com.shuihu.demo.model.entity.Entity;
import java.util.List;

/**
 * 状态管理器 —— 所有状态操作的静态入口
 */
public class StatusManager {

    /** 附加状态（含免疫检测） */
    public static boolean apply(Entity target, StatusType type, int stacks, int duration) {
        if (isImmune(target, type)) return false;

        // 找已存在的同类状态
        Status existing = findStatus(target, type);
        if (existing != null) {
            existing.addStacks(stacks);
            existing.setRemainingTurns(Math.max(existing.getRemainingTurns(), duration));
        } else {
            int maxStacks = getDefaultMaxStacks(type);
            target.getStatusEffects().add(new Status(type, stacks, duration, maxStacks, target.getId()));
        }
        return true;
    }

    /** 移除指定状态 */
    public static void remove(Entity target, StatusType type) {
        target.getStatusEffects().removeIf(s -> s.getType() == type);
    }

    /** 移除所有状态 */
    public static void clearAll(Entity target) {
        target.getStatusEffects().clear();
    }

    /** 每回合结算伤害（流血/中毒/灼烧） */
    public static void tickDamage(Entity target) {
        for (Status s : List.copyOf(target.getStatusEffects())) {
            switch (s.getType()) {
                case BLEEDING -> {
                    int dmg = (int)(target.getBattleStats().getEffectiveAtk(target.getBaseStats().getAtk()) * 0.1 * s.getStacks());
                    target.getBattleStats().takeDamage(dmg);
                    System.out.println("【流血】" + target.getName() + "损失 " + dmg + " 点生命");
                }
                case POISON -> {
                    int dmg = (int)(target.getBattleStats().getMaxHpCache() * 0.03);
                    target.getBattleStats().takeDamage(dmg);
                    System.out.println("【中毒】" + target.getName() + "损失 " + dmg + " 点生命");
                }
                case BURN -> {
                    int dmg = (int)(target.getBattleStats().getEffectiveAtk(target.getBaseStats().getAtk()) * 0.15 * s.getStacks());
                    target.getBattleStats().takeDamage(dmg);
                    System.out.println("【灼烧】" + target.getName() + "损失 " + dmg + " 点生命");
                }
            }
        }
    }

    /** 减少持续时间并移除到期状态 */
    public static void expireCheck(Entity target) {
        List<Status> expired = new java.util.ArrayList<>();
        for (Status s : target.getStatusEffects()) {
            s.decreaseTurn();
            if (s.isExpired()) expired.add(s);
        }
        target.getStatusEffects().removeAll(expired);
    }

    /** 检查实体是否有指定状态 */
    public static boolean hasStatus(Entity target, StatusType type) {
        return target.getStatusEffects().stream().anyMatch(s -> s.getType() == type);
    }

    /** 获取状态层数 */
    public static int getStacks(Entity target, StatusType type) {
        Status s = findStatus(target, type);
        return s != null ? s.getStacks() : 0;
    }

    /** 免疫检测 */
    public static boolean isImmune(Entity target, StatusType type) {
        String id = target.getId();
        return switch (id) {
            case "lin_chong" -> type == StatusType.BLEEDING || type == StatusType.BLIND || type == StatusType.POISON;
            case "guan_sheng" -> type == StatusType.BLEEDING || type == StatusType.POISON || type == StatusType.BLIND;
            case "song_jiang" -> type == StatusType.STUN || type == StatusType.ROOT || type == StatusType.CONFUSE;
            default -> false;
        };
    }

    private static Status findStatus(Entity target, StatusType type) {
        return target.getStatusEffects().stream()
                .filter(s -> s.getType() == type)
                .findFirst().orElse(null);
    }

    private static int getDefaultMaxStacks(StatusType type) {
        return switch (type) {
            case BLEEDING -> 5;
            case BURN -> 3;
            case FURY -> 10;
            default -> 1;
        };
    }
}
