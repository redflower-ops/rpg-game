package com.shuihu.demo.engine;

import com.shuihu.demo.model.entity.Entity;
import com.shuihu.demo.model.entity.Player;
import com.shuihu.demo.model.entity.Enemy;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 回合管理器 —— 按速度排序行动队列
 */
public class TurnManager {
    private final List<Entity> allEntities;
    private int currentIndex;
    private int roundNumber;

    public TurnManager(Player player, List<Enemy> enemies) {
        this.roundNumber = 0;
        this.currentIndex = 0;
        this.allEntities = new ArrayList<>();
        this.allEntities.add(player);
        this.allEntities.addAll(enemies);
        sortBySpeed();
    }

    private void sortBySpeed() {
        allEntities.sort((a, b) -> {
            double aSpd = a.getBattleStats().getEffectiveSpd(a.getBaseStats().getSpd());
            double bSpd = b.getBattleStats().getEffectiveSpd(b.getBaseStats().getSpd());
            int cmp = Double.compare(bSpd, aSpd);
            if (cmp != 0) return cmp;
            return a instanceof Player ? -1 : 1;
        });
    }

    /** 获取下个行动的实体。返回null表示新回合开始 */
    public Entity nextActor() {
        // 跳过已死亡
        while (currentIndex < allEntities.size() && !allEntities.get(currentIndex).isAlive()) {
            currentIndex++;
        }

        if (currentIndex >= allEntities.size()) {
            // 新回合
            roundNumber++;
            currentIndex = 0;
            // 重新排序（SPD可能变化）
            allEntities.removeIf(e -> !e.isAlive());
            sortBySpeed();
            return null; // 表示新回合开始
        }

        Entity actor = allEntities.get(currentIndex);
        currentIndex++;
        return actor;
    }

    public int getRoundNumber() { return roundNumber; }
    public List<Entity> getAllEntities() { return allEntities; }

    /** 新增实体到行动队列 */
    public void addEntity(Entity e) {
        allEntities.add(e);
        sortBySpeed();
    }

    /** 获取玩家 */
    public Player getPlayer() {
        return (Player) allEntities.stream().filter(e -> e instanceof Player).findFirst().orElse(null);
    }
}
