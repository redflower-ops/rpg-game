package com.rpg;

import com.rpg.game.BattleSystem;
import com.rpg.item.Sword;
import com.rpg.model.Player;
import com.rpg.model.Monster;
import com.rpg.model.Slime;
import com.rpg.model.Goblin;
import com.rpg.model.Dragon;

import java.util.*;

/**
 * 游戏入口 —— 把之前写的东西全部串起来
 *
 * 知识点：
 *  - List 集合：用来存放怪物列表，随机抽怪
 *  - Random 随机数
 *  - Scanner 用户输入
 *  - Map 集合：背包初始化
 */

public class Main {
    private static Monster createRandomMonster() {
        Random rand = new Random();
        int roll = rand.nextInt(3);
        return switch (roll) {
            case 0 -> new Slime();
            case 1 -> new Goblin();
            case 2 -> new Dragon();
            default -> new Slime();
        };
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // ── 1. 创建玩家 ──
        System.out.println("========================");
        System.out.println("   🎮 RPG 冒险游戏");
        System.out.println("========================");
        System.out.print("请输入你的名字：");
        String name = scanner.nextLine();

        // 初始化背包（空的）
        Map<String, Integer> bag = new HashMap<>();
        // 创建玩家（先给把铁剑）
        Player player = new Player(name, 1, 100, 0, 100, 20, 5, 0, new Sword(), bag);

        System.out.println("\n欢迎你，" + player.getName() + "！");
        System.out.println("前方出现了怪物，准备战斗吧！\n");

        // ── 2. 游戏主循环 ──
        boolean playing = true;
        while (playing && player.getCurrentHp() > 0) {
            // 随机选一个怪物
            Monster monster = createRandomMonster();

            // 开始战斗
            BattleSystem battle = new BattleSystem(player, monster, scanner);
            boolean survived = battle.start();

            if (!survived) {
                // 玩家死了
                System.out.println("\n游戏结束！");
                playing = false;
            } else {
                // 赢了，问问要不要继续
                System.out.print("\n继续冒险？(y/n): ");
                String input = scanner.nextLine();
                if (!input.equalsIgnoreCase("y")) {
                    playing = false;
                    System.out.println("\n感谢游玩，再见！");
                } else {
                    System.out.println("\n你继续向前探索...\n");
                }
            }
        }

        scanner.close();
    }
}