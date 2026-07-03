package com.shuihu.demo.ai;

import com.shuihu.demo.model.entity.Enemy;
import com.shuihu.demo.model.entity.Player;
import com.shuihu.demo.model.skill.Skill;

/**
 * AI接口 —— 策略模式
 */
public interface AiController {
    Skill decideNextMove(Enemy self, Player target, BattleContext context);
}
