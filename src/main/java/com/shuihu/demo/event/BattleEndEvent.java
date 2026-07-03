package com.shuihu.demo.event;

import com.shuihu.demo.model.entity.Player;
import com.shuihu.demo.model.entity.Enemy;

public class BattleEndEvent implements GameEvent {
    private final Player player;
    private final Enemy boss;
    private final String result; // "victory" / "defeat"

    public BattleEndEvent(Player player, Enemy boss, String result) {
        this.player = player; this.boss = boss; this.result = result;
    }
    public Player getPlayer() { return player; }
    public Enemy getBoss() { return boss; }
    public String getResult() { return result; }
}
