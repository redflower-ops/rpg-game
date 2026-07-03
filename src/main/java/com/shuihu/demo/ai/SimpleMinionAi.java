package com.shuihu.demo.ai;

import com.shuihu.demo.model.entity.Enemy;
import com.shuihu.demo.model.entity.Player;
import com.shuihu.demo.model.skill.Skill;
import com.shuihu.demo.model.skill.SkillType;

/**
 * 小兵简易AI
 */
public class SimpleMinionAi implements AiController {
    @Override
    public Skill decideNextMove(Enemy self, Player target, BattleContext context) {
        Skill skill = self.getSkills().stream()
                .filter(s -> s.getCurrentCd() == 0 && s.getType() != SkillType.PASSIVE)
                .filter(s -> self.getBattleStats().getCurrentMp() >= s.getMpCost())
                .findFirst().orElse(null);

        if (skill != null && Math.random() < 0.6) {
            return skill;
        }
        return self.getSkills().get(0); // 普攻兜底
    }
}
