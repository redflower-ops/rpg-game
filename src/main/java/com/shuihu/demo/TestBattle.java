package com.shuihu.demo;

import com.shuihu.demo.engine.CombatResolver;
import com.shuihu.demo.ai.BattleContext;
import com.shuihu.demo.factory.MockDataFactory;
import com.shuihu.demo.model.entity.*;
import com.shuihu.demo.model.skill.Skill;
import com.shuihu.demo.model.status.StatusManager;
import com.shuihu.demo.model.status.StatusType;

/**
 * 战斗逻辑快速验证
 */
public class TestBattle {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("========== 战斗逻辑测试 ==========\n");

        testNormalAttack();
        testSkillDamage();
        testHeal();
        testShiJinPinMingPassive();
        testStatusEffect();
        testDefenseReducesDamage();
        testShieldAbsorbsDamage();
        testBattleDeath();

        System.out.println("\n========== 结果 ==========");
        System.out.println("通过: " + passed + ", 失败: " + failed);
        if (failed > 0) {
            System.exit(1);
        }
        System.out.println("全部通过！");
    }

    static void check(String name, Runnable test) {
        try {
            test.run();
            System.out.println("[PASS] " + name);
            passed++;
        } catch (AssertionError e) {
            System.out.println("[FAIL] " + name + " — " + e.getMessage());
            failed++;
        } catch (Exception e) {
            System.out.println("[FAIL] " + name + " — 异常: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            failed++;
        }
    }

    static void assertEquals(int expected, int actual, String msg) {
        if (expected != actual) throw new AssertionError(msg + " expected=" + expected + " actual=" + actual);
    }

    static void assertTrue(boolean condition, String msg) {
        if (!condition) throw new AssertionError(msg);
    }

    // ========== 测试用例 ==========

    static void testNormalAttack() {
        check("普通攻击造成伤害", () -> {
            Player p = MockDataFactory.createMockPlayer();
            Enemy boss = MockDataFactory.createShiJinBoss();
            int beforeHp = boss.getBattleStats().getCurrentHp();
            Skill normal = p.getSkills().get(0); // normal_attack
            CombatResolver.executeSkill(p, boss, normal, new BattleContext());
            int afterHp = boss.getBattleStats().getCurrentHp();
            assertTrue(afterHp < beforeHp, "Boss血量应减少，before=" + beforeHp + " after=" + afterHp);
        });
    }

    static void testSkillDamage() {
        check("技能造成伤害且消耗MP", () -> {
            Player p = MockDataFactory.createMockPlayer();
            Enemy boss = MockDataFactory.createShiJinBoss();
            int beforeMp = p.getBattleStats().getCurrentMp();
            Skill skill = p.getSkills().get(1); // 破绽打击
            CombatResolver.executeSkill(p, boss, skill, new BattleContext());
            assertTrue(p.getBattleStats().getCurrentMp() < beforeMp, "应消耗MP");
        });
    }

    static void testHeal() {
        check("治疗技能恢复HP", () -> {
            Player p = MockDataFactory.createMockPlayer();
            p.getBattleStats().setCurrentHp(80); // 受伤
            Skill heal = p.getSkills().get(2); // 凝神纳气
            BattleContext ctx = new BattleContext();
            CombatResolver.executeSkill(p, p, heal, ctx);
            assertTrue(p.getBattleStats().getCurrentHp() > 80, "HP应恢复");
        });
    }

    static void testShiJinPinMingPassive() {
        check("史进拼命被动触发(HP<20%)", () -> {
            Enemy boss = MockDataFactory.createShiJinBoss();
            boss.getBattleStats().setCurrentHp(50); // 300*0.166 < 20%
            Player p = MockDataFactory.createMockPlayer();
            var ai = boss.getAiController();
            Skill skill = ai.decideNextMove(boss, p, new BattleContext());
            assertTrue(skill != null, "应返回技能");
            assertTrue(boss.getMechanicData().containsKey("pin_ming_triggered"), "应有拼命标记");
        });
    }

    static void testStatusEffect() {
        check("Status附加与移除", () -> {
            Player p = MockDataFactory.createMockPlayer();
            StatusManager.apply(p, StatusType.BLEEDING, 2, 3);
            assertTrue(StatusManager.hasStatus(p, StatusType.BLEEDING), "应有流血状态");
            assertEquals(2, StatusManager.getStacks(p, StatusType.BLEEDING), "流血层数");
            StatusManager.remove(p, StatusType.BLEEDING);
            assertTrue(!StatusManager.hasStatus(p, StatusType.BLEEDING), "流血应移除");
        });
    }

    static void testDefenseReducesDamage() {
        check("防御姿态减伤", () -> {
            Player p = MockDataFactory.createMockPlayer();
            Enemy boss = MockDataFactory.createShiJinBoss();
            p.setDefending(true);
            int before = p.getBattleStats().getCurrentHp();
            Skill normal = boss.getSkills().get(0);
            CombatResolver.executeSkill(boss, p, normal, new BattleContext());
            int after = p.getBattleStats().getCurrentHp();
            assertTrue(after < before, "防御下仍应受到伤害");
            assertTrue(before - after < 50, "防御下伤害应较低"); // 粗略检查
        });
    }

    static void testShieldAbsorbsDamage() {
        check("护盾吸收伤害", () -> {
            Enemy boss = MockDataFactory.createShiJinBoss();
            boss.getBattleStats().setShield(50);
            int beforeShield = boss.getBattleStats().getShield();
            Player p = MockDataFactory.createMockPlayer();
            Skill normal = p.getSkills().get(0);
            CombatResolver.executeSkill(p, boss, normal, new BattleContext());
            assertTrue(boss.getBattleStats().getShield() < beforeShield, "护盾应减少");
        });
    }

    static void testBattleDeath() {
        check("实体死亡检测", () -> {
            Player p = MockDataFactory.createMockPlayer();
            p.getBattleStats().setCurrentHp(0);
            assertTrue(!p.isAlive(), "HP=0时应死亡");
        });
    }
}
