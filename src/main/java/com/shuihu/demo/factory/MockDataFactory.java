package com.shuihu.demo.factory;

import com.shuihu.demo.ai.RuleBasedHeroAi;
import com.shuihu.demo.ai.SimpleMinionAi;
import com.shuihu.demo.model.entity.Enemy;
import com.shuihu.demo.model.entity.Player;
import com.shuihu.demo.model.item.Inventory;
import com.shuihu.demo.model.item.Item;
import com.shuihu.demo.model.skill.*;
import com.shuihu.demo.model.stats.BattleStats;
import com.shuihu.demo.model.stats.Stats;
import com.shuihu.demo.model.talent.Talent;
import com.shuihu.demo.model.talent.TriggerCondition;
import com.shuihu.demo.model.weapon.Weapon;
import com.shuihu.demo.model.weapon.WeaponType;

import java.util.List;
import java.util.Map;

/**
 * Mock数据工厂 —— 纯内存硬编码
 */
public class MockDataFactory {

    // ========== 玩家 ==========
    public static Player createMockPlayer() {
        Player p = new Player();
        p.setId("player_001");
        p.setName("异界行者");
        p.setLevel(5);
        p.setBaseStats(new Stats(160, 70, 36, 18, 14, 9));
        p.setBattleStats(new BattleStats(160, 70));
        p.setWeapon(createBladeWeapon());
        p.setSkills(createPlayerSkills());
        p.setTalent(createDefaultTalent());
        p.setInventory(new Inventory(5));
        // 给一个初始道具
        p.getInventory().add(new Item("hp_potion", "回血药(小)", EffectType.HEAL,
                Map.of("amount", 48), "恢复30%HP"));
        return p;
    }

    // ========== 5种武器 ==========
    public static Weapon createBladeWeapon() {
        Weapon w = new Weapon(WeaponType.BLADE, 1.0);
        w.getPassiveEffects().put("bleedChance", 0.5);
        w.getPassiveEffects().put("bleedStacksPerHit", 1);
        w.getPassiveEffects().put("maxBleedStacks", 5);
        return w;
    }

    public static Weapon createSwordWeapon() {
        Weapon w = new Weapon(WeaponType.SWORD, 0.9);
        w.getPassiveEffects().put("comboChance", 0.2);
        w.getPassiveEffects().put("parryChance", 0.15);
        return w;
    }

    public static Weapon createSpearWeapon() {
        Weapon w = new Weapon(WeaponType.SPEAR, 1.0);
        w.getPassiveEffects().put("armorPierce", 0.3);
        return w;
    }

    public static Weapon createHiddenWeapon() {
        Weapon w = new Weapon(WeaponType.HIDDEN_WEAPON, 0.7);
        w.getPassiveEffects().put("debuffChance", 0.4);
        return w;
    }

    public static Weapon createGauntletWeapon() {
        Weapon w = new Weapon(WeaponType.GAUNTLET, 1.0);
        w.getPassiveEffects().put("chainPointPerHit", 1);
        w.getPassiveEffects().put("maxChainPoints", 5);
        return w;
    }

    // ========== 主角技能 ==========
    public static List<Skill> createPlayerSkills() {
        return List.of(
            new Skill("normal_attack", "普通攻击", SkillType.NORMAL, 0, 0, 0, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "1.0", 1.0, null))),

            new Skill("po_zhan_da_ji", "破绽打击", SkillType.ACTIVE, 15, 0, 3, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "caster.atk * 1.2 + target.lostHp * 0.1", 1.0, null)),
                "根据已损HP追加伤害"),

            new Skill("ning_shen_na_qi", "凝神纳气", SkillType.ACTIVE, 0, 0, 4, DamageType.PHYSICAL,
                List.of(
                    new SkillEffect(EffectType.HEAL, "30", 1.0, null),
                    new SkillEffect(EffectType.MODIFY_STAT, "0.3", 1.0, Map.of("stat", "def", "duration", 2))
                ), "回蓝+加防"),

            new Skill("shu_si_yi_bo", "殊死一搏", SkillType.ACTIVE, 0, 15, 5, DamageType.TRUE,
                List.of(new SkillEffect(EffectType.DAMAGE, "caster.atk * 2.5", 1.0, null)),
                "消耗HP造成真实伤害")
        );
    }

    // ========== 默认天赋 ==========
    public static Talent createDefaultTalent() {
        return new Talent("yi_jie_zhi_ren", "异界之人",
            "击败好汉后恢复20%HP/MP",
            List.of(new Talent.TalentEffect(TriggerCondition.ON_BOSS_DEFEATED, EffectType.HEAL, Map.of("amount", "20%"))));
    }

    // ========== 史进Boss ==========
    public static Enemy createShiJinBoss() {
        Enemy boss = new Enemy();
        boss.setId("shi_jin");
        boss.setName("九纹龙·史进");
        boss.setLevel(5);
        boss.setBaseStats(new Stats(300, 40, 28, 15, 12, 8));
        boss.setBattleStats(new BattleStats(300, 40));
        boss.setSkills(List.of(
            new Skill("normal_attack", "普攻·青龙棍", SkillType.NORMAL, 0, 0, 0, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "1.0", 1.0, null))),
            new Skill("long_ban_luan_wu", "棍棒乱舞", SkillType.NORMAL, 0, 0, 2, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "0.6", 1.0, Map.of("hitCount", 3)))),
            new Skill("qing_long_chu_shui", "青龙出水", SkillType.ACTIVE, 15, 0, 4, DamageType.PHYSICAL,
                List.of(
                    new SkillEffect(EffectType.DAMAGE, "1.5", 1.0, null),
                    new SkillEffect(EffectType.HEAL, "caster.maxHp * 0.1", 1.0, null)
                ))
        ));
        boss.setAiController(new RuleBasedHeroAi("shi_jin"));
        boss.getMechanicData().put("dragon_scale_layers", 9);
        boss.setOpeningLine("俺这九条青龙，还没尝过外乡人的血！");
        return boss;
    }

    // ========== 林冲Boss ==========
    public static Enemy createLinChongBoss() {
        Enemy boss = new Enemy();
        boss.setId("lin_chong");
        boss.setName("豹子头·林冲");
        boss.setLevel(10);
        boss.setBaseStats(new Stats(450, 80, 38, 20, 15, 15));
        boss.setBattleStats(new BattleStats(450, 80));
        boss.setSkills(List.of(
            new Skill("normal_attack", "普攻·寒星枪", SkillType.NORMAL, 0, 0, 0, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "1.0", 1.0, null),
                        new SkillEffect(EffectType.STEAL_MP, "5", 0.5, null))),
            new Skill("ji_feng_bao_yu_ci", "疾风暴雨刺", SkillType.NORMAL, 0, 0, 2, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "1.4", 1.0, null))),
            new Skill("nu_huo_zhong_shao", "怒火中烧", SkillType.ACTIVE, 20, 0, 4, DamageType.PHYSICAL,
                List.of(
                    new SkillEffect(EffectType.REMOVE_STATUS, "", 1.0, Map.of("statusType", "ALL")),
                    new SkillEffect(EffectType.MODIFY_STAT, "1.0", 1.0, Map.of("stat", "critRate", "duration", 1))
                )),
            new Skill("hui_ma_qiang", "回马枪", SkillType.CHARGE, 0, 0, 6, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "3.0", 1.0, null)))
        ));
        boss.setAiController(new RuleBasedHeroAi("lin_chong"));
        boss.getMechanicData().put("charging", false);
        boss.setOpeningLine("林冲本不愿与阁下为敌，但各为其主，得罪了！");
        return boss;
    }

    // ========== 刘唐Boss ==========
    public static Enemy createLiuTangBoss() {
        Enemy boss = new Enemy();
        boss.setId("liu_tang");
        boss.setName("赤发鬼·刘唐");
        boss.setLevel(7);
        boss.setBaseStats(new Stats(250, 30, 35, 8, 14, 5));
        boss.setBattleStats(new BattleStats(250, 30));
        boss.setSkills(List.of(
            new Skill("normal_attack", "普攻·嗜血", SkillType.NORMAL, 0, 0, 0, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "1.0", 1.0, null))),
            new Skill("feng_mo_kan_sha", "疯魔砍杀", SkillType.NORMAL, 0, 0, 1, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "1.2", 1.0, null))),
            new Skill("po_fu_chen_zhou", "破釜沉舟", SkillType.ACTIVE, 15, 0, 5, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "2.5", 1.0, null)))
        ));
        boss.setAiController(new RuleBasedHeroAi("liu_tang"));
        boss.setOpeningLine("嘿嘿，老子这口刀很久没喝人血了！");
        return boss;
    }

    // ========== 雷横Boss ==========
    public static Enemy createLeiHengBoss() {
        Enemy boss = new Enemy();
        boss.setId("lei_heng");
        boss.setName("插翅虎·雷横");
        boss.setLevel(6);
        boss.setBaseStats(new Stats(280, 50, 25, 12, 18, 10));
        boss.setBattleStats(new BattleStats(280, 50));
        boss.setSkills(List.of(
            new Skill("normal_attack", "普攻", SkillType.NORMAL, 0, 0, 0, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "1.0", 1.0, null))),
            new Skill("meng_hu_xia_shan", "猛虎下山", SkillType.NORMAL, 0, 0, 2, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "1.3", 1.0, null))),
            new Skill("cha_chi_nan_tao", "插翅难逃", SkillType.ACTIVE, 10, 0, 3, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.APPLY_STATUS, "", 1.0,
                    Map.of("statusType", "BLIND", "stacks", 1, "duration", 2))))
        ));
        boss.setAiController(new RuleBasedHeroAi("lei_heng"));
        boss.setOpeningLine("我雷横这双铁掌，从不打无名之辈——报上名来！");
        return boss;
    }

    // ========== 鲁智深Boss ==========
    public static Enemy createLuZhiShenBoss() {
        Enemy boss = new Enemy();
        boss.setId("lu_zhi_shen");
        boss.setName("花和尚·鲁智深");
        boss.setLevel(11);
        boss.setBaseStats(new Stats(600, 30, 42, 30, 8, 20));
        boss.setBattleStats(new BattleStats(600, 30));
        boss.setSkills(List.of(
            new Skill("normal_attack", "普攻·禅杖", SkillType.NORMAL, 0, 0, 0, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "1.0", 1.0, null))),
            new Skill("chan_zhang_sao", "禅杖横扫", SkillType.NORMAL, 0, 0, 1, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "1.1", 1.0, null))),
            new Skill("jin_gang_nu_mu", "金刚怒目", SkillType.ACTIVE, 15, 0, 3, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.SHIELD, "caster.atk * 2", 1.0, null))),
            new Skill("dao_ba_chui_yang_liu", "倒拔垂杨柳", SkillType.CHARGE, 0, 0, 5, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "4.0", 1.0, null)))
        ));
        boss.setAiController(new RuleBasedHeroAi("lu_zhi_shen"));
        boss.setOpeningLine("阿弥陀佛！洒家今天要破戒了！");
        return boss;
    }

    // ========== 武松Boss ==========
    public static Enemy createWuSongBoss() {
        Enemy boss = new Enemy();
        boss.setId("wu_song");
        boss.setName("行者·武松");
        boss.setLevel(8);
        boss.setBaseStats(new Stats(350, 60, 40, 10, 16, 8));
        boss.setBattleStats(new BattleStats(350, 60));
        boss.setSkills(List.of(
            new Skill("normal_attack", "普攻·醉拳", SkillType.NORMAL, 0, 0, 0, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "1.0", 1.0, null))),
            new Skill("da_hu_yi_shi", "打虎遗式", SkillType.ACTIVE, 10, 0, 2, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "1.8", 1.0, null))),
            new Skill("zui_jiu", "醉酒", SkillType.ACTIVE, 5, 0, 2, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.MODIFY_STAT, "0.2", 1.0, Map.of("stat", "atk", "duration", 2)))),
            new Skill("zui_ba_xian_luan_da", "醉八仙乱打", SkillType.ULTIMATE, 0, 0, 6, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "0.8", 1.0, Map.of("hitCount", 5))))
        ));
        boss.setAiController(new RuleBasedHeroAi("wu_song"));
        boss.getMechanicData().put("inebriation", 0);
        boss.setOpeningLine("呵呵呵……再来一碗！");
        return boss;
    }

    // ========== 杨志Boss ==========
    public static Enemy createYangZhiBoss() {
        Enemy boss = new Enemy();
        boss.setId("yang_zhi");
        boss.setName("青面兽·杨志");
        boss.setLevel(9);
        boss.setBaseStats(new Stats(400, 70, 36, 18, 14, 12));
        boss.setBattleStats(new BattleStats(400, 70));
        boss.setSkills(List.of(
            new Skill("normal_attack", "普攻·兵器", SkillType.NORMAL, 0, 0, 0, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "1.0", 1.0, null))),
            new Skill("yang_jia_qiang_fa", "杨家枪法", SkillType.ACTIVE, 10, 0, 2, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "1.5", 1.0, null))),
            new Skill("pu_dao_zhan", "朴刀斩", SkillType.ACTIVE, 12, 0, 2, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "1.6", 1.0, null))),
            new Skill("leng_jian", "冷箭", SkillType.ACTIVE, 8, 0, 2, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "1.3", 1.0, null)))
        ));
        boss.setAiController(new RuleBasedHeroAi("yang_zhi"));
        boss.setOpeningLine("杨志乃将门之后，岂容你这厮放肆！");
        return boss;
    }

    // ========== 花荣Boss ==========
    public static Enemy createHuaRongBoss() {
        Enemy boss = new Enemy();
        boss.setId("hua_rong");
        boss.setName("小李广·花荣");
        boss.setLevel(8);
        boss.setBaseStats(new Stats(300, 80, 42, 10, 20, 10));
        boss.setBattleStats(new BattleStats(300, 80));
        boss.setSkills(List.of(
            new Skill("normal_attack", "普攻·穿云箭", SkillType.NORMAL, 0, 0, 0, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "1.0", 1.0, null))),
            new Skill("lian_zhu_jian", "连珠箭", SkillType.ACTIVE, 12, 0, 3, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "0.7", 1.0, Map.of("hitCount", 3)))),
            new Skill("luo_ri_gong", "落日弓", SkillType.ULTIMATE, 20, 0, 4, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "2.8", 1.0, null))),
            new Skill("man_tian_jian_yu", "漫天箭雨", SkillType.ACTIVE, 15, 0, 5, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "0.5", 1.0, Map.of("hitCount", 6))))
        ));
        boss.setAiController(new RuleBasedHeroAi("hua_rong"));
        boss.setOpeningLine("花荣在此，看箭！");
        return boss;
    }

    // ========== 公孙胜Boss ==========
    public static Enemy createGongSunShengBoss() {
        Enemy boss = new Enemy();
        boss.setId("gong_sun_sheng");
        boss.setName("入云龙·公孙胜");
        boss.setLevel(12);
        boss.setBaseStats(new Stats(350, 120, 30, 20, 12, 30));
        boss.setBattleStats(new BattleStats(350, 120));
        boss.setSkills(List.of(
            new Skill("normal_attack", "普攻·拂尘", SkillType.NORMAL, 0, 0, 0, DamageType.MAGIC,
                List.of(new SkillEffect(EffectType.DAMAGE, "0.8", 1.0, null))),
            new Skill("zhang_xin_lei", "掌心雷", SkillType.ACTIVE, 15, 0, 2, DamageType.MAGIC,
                List.of(new SkillEffect(EffectType.DAMAGE, "1.4", 1.0, null))),
            new Skill("zhi_ren_ti_shen", "纸人替身", SkillType.ACTIVE, 20, 0, 4, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.SHIELD, "300", 1.0, null))),
            new Skill("wu_lei_tian_xin", "五雷天心正法", SkillType.CHARGE, 0, 0, 6, DamageType.MAGIC,
                List.of(new SkillEffect(EffectType.DAMAGE, "4.0", 1.0, null)))
        ));
        boss.setAiController(new RuleBasedHeroAi("gong_sun_sheng"));
        boss.getMechanicData().put("charging", false);
        boss.setOpeningLine("无量天尊——阁下何不放下屠刀？");
        return boss;
    }

    // ========== 秦明Boss ==========
    public static Enemy createQinMingBoss() {
        Enemy boss = new Enemy();
        boss.setId("qin_ming");
        boss.setName("霹雳火·秦明");
        boss.setLevel(9);
        boss.setBaseStats(new Stats(380, 50, 44, 12, 14, 10));
        boss.setBattleStats(new BattleStats(380, 50));
        boss.setSkills(List.of(
            new Skill("normal_attack", "普攻·狼牙棒", SkillType.NORMAL, 0, 0, 0, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "1.0", 1.0, null))),
            new Skill("lang_ya_po_zhen", "狼牙破阵", SkillType.ACTIVE, 12, 0, 2, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "1.6", 1.0, null))),
            new Skill("fen_shen_yi_huo", "焚身以火", SkillType.ACTIVE, 15, 10, 4, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.MODIFY_STAT, "0.5", 1.0, Map.of("stat", "atk", "duration", 3)))),
            new Skill("lei_ting_zhen_nu", "雷霆震怒", SkillType.ULTIMATE, 25, 0, 6, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "3.0", 1.0, null)))
        ));
        boss.setAiController(new RuleBasedHeroAi("qin_ming"));
        boss.getMechanicData().put("fury", 0);
        boss.setOpeningLine("气死我也！吃我一棒！");
        return boss;
    }

    // ========== 关胜Boss ==========
    public static Enemy createGuanShengBoss() {
        Enemy boss = new Enemy();
        boss.setId("guan_sheng");
        boss.setName("大刀·关胜");
        boss.setLevel(10);
        boss.setBaseStats(new Stats(500, 60, 48, 25, 10, 15));
        boss.setBattleStats(new BattleStats(500, 60));
        boss.setSkills(List.of(
            new Skill("normal_attack", "普攻·青龙刀", SkillType.NORMAL, 0, 0, 0, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "1.0", 1.0, null))),
            new Skill("qing_long_yanyue_zhan", "青龙偃月斩", SkillType.ACTIVE, 15, 0, 3, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "2.0", 1.0, null))),
            new Skill("dao_qi_zong_heng", "刀气纵横", SkillType.ACTIVE, 12, 0, 2, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "1.3", 1.0, null))),
            new Skill("wu_sheng_jiang_shi", "武圣降世", SkillType.ULTIMATE, 30, 0, 7, DamageType.TRUE,
                List.of(new SkillEffect(EffectType.DAMAGE, "3.5", 1.0, null)))
        ));
        boss.setAiController(new RuleBasedHeroAi("guan_sheng"));
        boss.getMechanicData().put("wuStacks", 0);
        boss.setOpeningLine("关某在此，还不速速退下！");
        return boss;
    }

    // ========== 宋江Boss ==========
    public static Enemy createSongJiangBoss() {
        Enemy boss = new Enemy();
        boss.setId("song_jiang");
        boss.setName("及时雨·宋江");
        boss.setLevel(12);
        boss.setBaseStats(new Stats(700, 100, 25, 20, 14, 18));
        boss.setBattleStats(new BattleStats(700, 100));
        boss.setSkills(List.of(
            new Skill("normal_attack", "普攻·仁义", SkillType.NORMAL, 0, 0, 0, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "0.8", 1.0, null))),
            new Skill("ti_tian_xing_dao", "替天行道", SkillType.ULTIMATE, 30, 0, 4, DamageType.PERCENT,
                List.of(new SkillEffect(EffectType.DAMAGE, "0.15", 1.0, null)))
        ));
        boss.setAiController(new RuleBasedHeroAi("song_jiang"));
        boss.setOpeningLine("宋江代天行道，尔等受死！");
        return boss;
    }

    // ========== 关卡小兵 ==========

    /** 根据楼层和缩放系数创建小兵 */
    public static Enemy createFloorMinion(int floor, double scale) {
        int baseHp = (int)(80 * scale);
        int baseMp = (int)(20 * scale);
        int baseAtk = (int)(15 * scale);
        int baseDef = (int)(8 * scale);
        int baseSpd = (int)(8 * scale);
        int baseMres = (int)(5 * scale);

        String[] names = {"游勇", "庄客", "土兵", "山贼", "草寇"};
        String[] weapons = {"朴刀", "棍棒", "短剑", "铁叉", "板斧"};
        int idx = Math.min(floor - 1, names.length - 1);

        Enemy minion = new Enemy();
        minion.setId("minion_f" + floor);
        minion.setName(names[idx] + "·" + weapons[idx]);
        minion.setLevel(1 + floor);
        minion.setBaseStats(new Stats(baseHp, baseMp, baseAtk, baseDef, baseSpd, baseMres));
        minion.setBattleStats(new BattleStats(baseHp, baseMp));
        minion.setSkills(List.of(
            new Skill("normal_attack", "劈砍", SkillType.NORMAL, 0, 0, 0, DamageType.PHYSICAL,
                List.of(new SkillEffect(EffectType.DAMAGE, "1.0", 1.0, null)))
        ));
        minion.setAiController(new SimpleMinionAi());
        minion.setOpeningLine("站住！此路不通！");
        return minion;
    }
}
