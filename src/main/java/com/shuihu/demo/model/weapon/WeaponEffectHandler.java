package com.shuihu.demo.model.weapon;

import com.shuihu.demo.model.entity.Entity;
import com.shuihu.demo.model.entity.Player;
import com.shuihu.demo.model.status.StatusManager;
import com.shuihu.demo.model.status.StatusType;

/**
 * 武器被动效果执行器（策略模式）
 */
public class WeaponEffectHandler {

    public static void applyWeaponPassive(WeaponType type, Entity attacker, Entity defender) {
        if (type == null) return;
        switch (type) {
            case BLADE -> {
                // 50%概率附加1层流血
                if (Math.random() < 0.5) {
                    if (StatusManager.apply(defender, StatusType.BLEEDING, 1, 3)) {
                        System.out.println("【流血】" + defender.getName() + "被附加流血状态！");
                    }
                }
            }
            case SWORD -> {
                // 20%概率连击 — 由BattleManager外部处理
                // 15%招架 — 在CombatResolver中处理
            }
            case SPEAR -> {
                // 无视30%防御 — CombatResolver中处理
            }
            case HIDDEN_WEAPON -> {
                // 40%概率随机Debuff
                if (Math.random() < 0.4) {
                    StatusType[] debuffs = {
                        StatusType.POISON, StatusType.BLIND,
                        StatusType.WEAK, StatusType.BLEEDING
                    };
                    StatusType r = debuffs[(int)(Math.random() * debuffs.length)];
                    if (StatusManager.apply(defender, r, 1, 2)) {
                        System.out.println("【淬毒】" + defender.getName() + "被附加" + r.getDisplayName() + "！");
                    }
                }
            }
            case GAUNTLET -> {
                // 积攒1层连击点
                if (attacker instanceof Player p) {
                    p.setChainPoints(p.getChainPoints() + 1);
                    System.out.println("【连击点】当前 " + p.getChainPoints() + "/5");
                }
            }
        }
    }
}
