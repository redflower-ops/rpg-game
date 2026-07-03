package com.shuihu.demo.engine;

import com.shuihu.demo.factory.MockDataFactory;
import com.shuihu.demo.model.entity.Enemy;
import com.shuihu.demo.model.entity.Player;

import java.util.*;

/**
 * 闯关管理器 —— 梁山寨爬塔
 *
 * 5层，难度递增：
 *   第1层：小兵×2  → Boss(弱：史进/刘唐/雷横)
 *   第2层：小兵×2  → Boss(中：武松/杨志/花荣)
 *   第3层：小兵×3  → Boss(中强：林冲/秦明)
 *   第4层：小兵×3  → Boss(强：鲁智深/关胜)
 *   第5层：小兵×4  → Boss(最终：宋江)
 * 小兵属性逐层放大（每层+30%）
 */
public class GameManager {
    private final Player player;
    private final Scanner scanner;
    private int currentFloor;
    private static final int MAX_FLOOR = 5;

    public GameManager(Player player, Scanner scanner) {
        this.player = player;
        this.scanner = scanner;
    }

    public void start() {
        System.out.println("\n═══════════════════════════════");
        System.out.println("  梁山寨 · 闯关开始");
        System.out.println("  共 " + MAX_FLOOR + " 层，击败全部好汉即为通关");
        System.out.println("═══════════════════════════════");

        for (currentFloor = 1; currentFloor <= MAX_FLOOR; currentFloor++) {
            if (!executeFloor()) {
                System.out.println("\n💀 闯关失败，倒在 第" + currentFloor + " 层……");
                System.out.println("  到达第" + currentFloor + "层 | 等级" + player.getLevel() + " | 金币" + player.getGold());
                return;
            }
        }

        System.out.println("\n═══════════════════════════════");
        System.out.println("  🏆 恭喜通关梁山寨！");
        System.out.println("  最终等级 " + player.getLevel() + "，赚取 " + player.getGold() + " 金币");
        System.out.println("═══════════════════════════════");
    }

    /** 执行一层，返回是否通过 */
    private boolean executeFloor() {
        int minionWaves = getMinionWaveCount(currentFloor);
        double minionScale = 1.0 + (currentFloor - 1) * 0.3; // 每层+30%

        System.out.println("\n───────────────────────────────");
        System.out.println("  🌊 第 " + currentFloor + " 层  (小兵×" + minionWaves + " → Boss)");
        System.out.println("───────────────────────────────");

        // 小兵波次
        for (int wave = 1; wave <= minionWaves; wave++) {
            System.out.println("\n—— 小兵 " + wave + "/" + minionWaves + " ——");
            Enemy minion = MockDataFactory.createFloorMinion(currentFloor, minionScale);
            System.out.println("【" + minion.getName() + "】挥舞兵器冲了上来！");

            BattleManager bm = new BattleManager(scanner);
            bm.startBattle(player, minion);
            if (!player.isAlive()) return false;

            // 小兵战后微量回复
            int heal = player.getBaseStats().getMaxHp() * 15 / 100;
            int mpHeal = player.getBaseStats().getMaxMp() * 15 / 100;
            player.getBattleStats().heal(heal);
            player.getBattleStats().setCurrentMp(
                Math.min(player.getBaseStats().getMaxMp(), player.getBattleStats().getCurrentMp() + mpHeal));
            System.out.println("【休整】恢复 " + heal + " HP / " + mpHeal + " MP");

            // 小兵奖励
            int waveGold = currentFloor * 2;
            int waveExp = currentFloor * 3;
            player.addGold(waveGold);
            player.addExp(waveExp);
            System.out.println("💰 +" + waveGold + " 金币 +" + waveExp + " 经验");
        }

        // Boss战
        Enemy boss = selectFloorBoss(currentFloor);
        System.out.println("\n✦ Boss战 —— " + boss.getName() + " ✦");
        System.out.println("【" + boss.getName() + "】" + boss.getOpeningLine());

        BattleManager bm = new BattleManager(scanner);
        bm.startBattle(player, boss);
        if (!player.isAlive()) return false;

        // Boss奖励（逐层递增）
        int bossGold = 10 + currentFloor * 5;
        int bossExp = 15 + currentFloor * 10;
        if (currentFloor == MAX_FLOOR) {
            bossGold *= 2;
            bossExp *= 2;
        }
        player.addGold(bossGold);
        player.addExp(bossExp);
        System.out.printf("💰 击败 %s！获得 %d 金币, %d 经验 (Lv.%d %d/%d)%n",
                boss.getName(), bossGold, bossExp, player.getLevel(), player.getExp(), player.getExpToLevelUp());

        // 层间回满
        player.getBattleStats().setCurrentHp(player.getBaseStats().getMaxHp());
        player.getBattleStats().setCurrentMp(player.getBaseStats().getMaxMp());
        System.out.println("💤 第 " + currentFloor + " 层通关！状态回满，准备下一层……");

        return true;
    }

    /** 每层小兵波次：1→2, 2→2, 3→3, 4→3, 5→4 */
    private static int getMinionWaveCount(int floor) {
        if (floor <= 2) return 2;
        if (floor <= 4) return 3;
        return 4;
    }

    /** 根据楼层抽取Boss（1-4层随机，5层固定宋江） */
    private Enemy selectFloorBoss(int floor) {
        return switch (floor) {
            case 1 -> pickRandom(
                MockDataFactory.createShiJinBoss(),
                MockDataFactory.createLiuTangBoss(),
                MockDataFactory.createLeiHengBoss()
            );
            case 2 -> pickRandom(
                MockDataFactory.createWuSongBoss(),
                MockDataFactory.createYangZhiBoss(),
                MockDataFactory.createHuaRongBoss()
            );
            case 3 -> pickRandom(
                MockDataFactory.createLinChongBoss(),
                MockDataFactory.createQinMingBoss()
            );
            case 4 -> pickRandom(
                MockDataFactory.createLuZhiShenBoss(),
                MockDataFactory.createGuanShengBoss()
            );
            case 5 -> MockDataFactory.createSongJiangBoss();
            default -> MockDataFactory.createShiJinBoss();
        };
    }

    @SafeVarargs
    private static <T> T pickRandom(T... items) {
        return items[new Random().nextInt(items.length)];
    }
}
