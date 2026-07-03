package com.shuihu.demo;

import com.shuihu.demo.engine.BattleManager;
import com.shuihu.demo.event.*;
import com.shuihu.demo.factory.MockDataFactory;
import com.shuihu.demo.model.entity.*;
import com.shuihu.demo.model.status.*;

import java.util.Scanner;

/**
 * 水浒Roguelite · 控制台Demo入口
 *
 * Java SE 版战斗系统演示
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("========================");
        System.out.println("  水浒Roguelite · 异界行者");
        System.out.println("  战斗Demo · Java SE");
        System.out.println("========================");

        // 注册事件监听器
        registerEventListeners();

        while (true) {
            System.out.println("\n━━━ 选择对手 ━━━");
            System.out.println(" 1. 史进     (LV5)");
            System.out.println(" 2. 刘唐     (LV7)");
            System.out.println(" 3. 雷横     (LV6)");
            System.out.println(" 4. 林冲     (LV10)");
            System.out.println(" 5. 鲁智深   (LV11)");
            System.out.println(" 6. 武松     (LV8)");
            System.out.println(" 7. 杨志     (LV9)");
            System.out.println(" 8. 花荣     (LV8)");
            System.out.println(" 9. 公孙胜   (LV12)");
            System.out.println("10. 秦明     (LV9)");
            System.out.println("11. 关胜     (LV10)");
            System.out.println("12. 宋江     (LV12)");
            System.out.println(" 0. 退出");
            System.out.print("> ");

            int choice;
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            } catch (Exception e) {
                if (scanner.hasNextLine()) scanner.nextLine();
                System.out.println("输入无效。");
                continue;
            }

            if (choice == 0) {
                System.out.println("\n感谢游玩！");
                break;
            }

            // 创建玩家
            Player player = MockDataFactory.createMockPlayer();
            Enemy boss = createBoss(choice);

            if (boss == null) {
                System.out.println("无效选择。");
                continue;
            }

            // 开始战斗
            BattleManager bm = new BattleManager(scanner);
            bm.startBattle(player, boss);

            if (scanner.hasNextLine()) {
                System.out.print("\n按回车继续...");
                scanner.nextLine();
            }
        }

        scanner.close();
    }

    private static Enemy createBoss(int choice) {
        return switch (choice) {
            case 1 -> MockDataFactory.createShiJinBoss();
            case 2 -> MockDataFactory.createLiuTangBoss();
            case 3 -> MockDataFactory.createLeiHengBoss();
            case 4 -> MockDataFactory.createLinChongBoss();
            case 5 -> MockDataFactory.createLuZhiShenBoss();
            case 6 -> MockDataFactory.createWuSongBoss();
            case 7 -> MockDataFactory.createYangZhiBoss();
            case 8 -> MockDataFactory.createHuaRongBoss();
            case 9 -> MockDataFactory.createGongSunShengBoss();
            case 10 -> MockDataFactory.createQinMingBoss();
            case 11 -> MockDataFactory.createGuanShengBoss();
            case 12 -> MockDataFactory.createSongJiangBoss();
            default -> null;
        };
    }

    private static void registerEventListeners() {
        // 史进：龙纹护盾 — 每次受击减少1层
        EventBus.subscribe(AfterDamageEvent.class, event -> {
            if ("shi_jin".equals(event.getDefender().getId())) {
                Enemy shiJin = (Enemy) event.getDefender();
                int layers = (int) shiJin.getMechanicData().getOrDefault("dragon_scale_layers", 0);
                if (layers > 0) {
                    layers--;
                    shiJin.getMechanicData().put("dragon_scale_layers", layers);
                    double dmgReduction = layers * 0.05;
                    if (layers == 0) dmgReduction = -0.2; // 破体
                    shiJin.getBattleStats().setDmgReduction(dmgReduction);
                    System.out.println("【龙纹护盾】层数-" + (9 - layers) + "/9（免伤" + (layers * 5) + "%）");
                }
            }
        });

        // 战斗结束
        EventBus.subscribe(BattleEndEvent.class, event -> {
            if ("victory".equals(event.getResult())) {
                System.out.println("\n🏆 战斗胜利！击败了 " + event.getBoss().getName());
            } else {
                System.out.println("\n💀 被 " + event.getBoss().getName() + " 击败");
            }
        });

        // 状态附加
        EventBus.subscribe(StatusAppliedEvent.class, event -> {
            System.out.println("【状态】" + event.getTarget().getName() + " 被附加 " +
                    event.getType().getDisplayName() + " ×" + event.getStacks());
        });
    }
}
