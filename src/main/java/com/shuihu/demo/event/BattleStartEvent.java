package com.shuihu.demo.event;

import com.shuihu.demo.model.entity.Player;
import com.shuihu.demo.model.entity.Enemy;

public class BattleStartEvent implements GameEvent {
    private final Player player;
    private final Enemy boss;

    public BattleStartEvent(Player player, Enemy boss) { this.player = player; this.boss = boss; }
    public Player getPlayer() { return player; }
    public Enemy getBoss() { return boss; }
}
