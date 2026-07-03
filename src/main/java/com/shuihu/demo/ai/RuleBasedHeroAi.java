package com.shuihu.demo.ai;

import com.shuihu.demo.model.entity.Enemy;
import com.shuihu.demo.model.entity.Entity;
import com.shuihu.demo.model.entity.Player;
import com.shuihu.demo.model.skill.Skill;
import com.shuihu.demo.model.skill.SkillType;
import com.shuihu.demo.model.status.StatusManager;
import com.shuihu.demo.model.status.StatusType;
import com.shuihu.demo.model.stats.StatModifier;

/**
 * 规则驱动的12好汉AI
 */
public class RuleBasedHeroAi implements AiController {
    private final String heroId;

    public RuleBasedHeroAi(String heroId) {
        this.heroId = heroId;
    }

    @Override
    public Skill decideNextMove(Enemy self, Player target, BattleContext context) {
        return switch (heroId) {
            case "shi_jin" -> shiJinAI(self, target, context);
            case "liu_tang" -> liuTangAI(self, target, context);
            case "lei_heng" -> leiHengAI(self, target, context);
            case "lin_chong" -> linChongAI(self, target, context);
            case "lu_zhi_shen" -> luZhiShenAI(self, target, context);
            case "wu_song" -> wuSongAI(self, target, context);
            case "yang_zhi" -> yangZhiAI(self, target, context);
            case "hua_rong" -> huaRongAI(self, target, context);
            case "gong_sun_sheng" -> gongSunShengAI(self, target, context);
            case "qin_ming" -> qinMingAI(self, target, context);
            case "guan_sheng" -> guanShengAI(self, target, context);
            case "song_jiang" -> songJiangAI(self, target, context);
            default -> defaultAI(self, target, context);
        };
    }

    // ========== 01. 史进 ==========
    private Skill shiJinAI(Enemy self, Player target, BattleContext context) {
        Skill longPatternDance = findSkill(self, "long_ban_luan_wu");
        Skill qingLongOut = findSkill(self, "qing_long_chu_shui");
        Skill normal = findSkill(self, "normal_attack");

        int hpPct = self.getBattleStats().getCurrentHp() * 100 / self.getBaseStats().getMaxHp();

        // 被动：拼命（HP<20%，ATK+30%，DEF归零）
        if (hpPct < 20 && !Boolean.TRUE.equals(self.getMechanicData().get("pin_ming_triggered"))) {
            self.getMechanicData().put("pin_ming_triggered", true);
            self.getBattleStats().addModifier(new StatModifier("atk", 0.3, -1));
            self.getBattleStats().addModifier(new StatModifier("def", -1.0, -1));
            System.out.println("【史进】……拼了！(ATK+30%，DEF归零)");
        }

        // 青龙出水（HP<50%且可用）
        if (hpPct < 50 && qingLongOut != null && qingLongOut.getCurrentCd() == 0
                && self.getBattleStats().getCurrentMp() >= qingLongOut.getMpCost()) {
            return qingLongOut;
        }

        // 棍棒乱舞
        if (longPatternDance != null && longPatternDance.getCurrentCd() == 0) {
            return longPatternDance;
        }

        return normal;
    }

    // ========== 02. 刘唐 ==========
    private Skill liuTangAI(Enemy self, Player target, BattleContext context) {
        Skill poFuChenZhou = findSkill(self, "po_fu_chen_zhou");
        Skill fengMoKanSha = findSkill(self, "feng_mo_kan_sha");
        Skill normal = findSkill(self, "normal_attack");

        int hpPct = self.getBattleStats().getCurrentHp() * 100 / self.getBaseStats().getMaxHp();

        // HP<40% → 破釜沉舟
        if (hpPct < 40 && poFuChenZhou != null && poFuChenZhou.getCurrentCd() == 0
                && self.getBattleStats().getCurrentMp() >= poFuChenZhou.getMpCost()) {
            return poFuChenZhou;
        }

        if (fengMoKanSha != null && fengMoKanSha.getCurrentCd() == 0) {
            return fengMoKanSha;
        }

        return normal;
    }

    // ========== 03. 雷横 ==========
    private Skill leiHengAI(Enemy self, Player target, BattleContext context) {
        Skill mengHuXiaShan = findSkill(self, "meng_hu_xia_shan");
        Skill chaChiNanTao = findSkill(self, "cha_chi_nan_tao");
        Skill normal = findSkill(self, "normal_attack");

        // 上回合闪避 → 猛虎下山翻倍
        if (context != null && context.isParried() && mengHuXiaShan != null && mengHuXiaShan.getCurrentCd() == 0) {
            return mengHuXiaShan;
        }

        if (mengHuXiaShan != null && mengHuXiaShan.getCurrentCd() == 0) {
            return mengHuXiaShan;
        }

        // 每3回合插翅难逃
        if (context != null && context.getRound() % 3 == 0 && chaChiNanTao != null
                && chaChiNanTao.getCurrentCd() == 0) {
            return chaChiNanTao;
        }

        return normal;
    }

    // ========== 04. 林冲 ==========
    private Skill linChongAI(Enemy self, Player target, BattleContext context) {
        Skill huiMaQiang = findSkill(self, "hui_ma_qiang");
        Skill nuHuoZhongShao = findSkill(self, "nu_huo_zhong_shao");
        Skill jiFengBaoYu = findSkill(self, "ji_feng_bao_yu_ci");
        Skill normal = findSkill(self, "normal_attack");

        // 检查蓄力释放
        if (Boolean.TRUE.equals(self.getMechanicData().get("charging"))) {
            self.getMechanicData().put("charging", false);
            boolean playerDefended = context != null && context.isPlayerDefendedLastTurn();
            if (playerDefended) {
                System.out.println("【林冲】回马枪被你勉强架开！");
            } else {
                System.out.println("【致命】林冲的回马枪结结实实命中了你！！！");
            }
            return huiMaQiang;
        }

        int hpPct = self.getBattleStats().getCurrentHp() * 100 / self.getBaseStats().getMaxHp();

        // 每6回合回马枪蓄力
        if (huiMaQiang != null && huiMaQiang.getCurrentCd() == 0
                && context != null && context.getRound() % 6 == 0) {
            self.getMechanicData().put("charging", true);
            System.out.println("【致命警告】林冲正在蓄力【回马枪】——下回合必须防御！");
            return huiMaQiang;
        }

        // 有2个以上Debuff → 怒火中烧
        if (self.getStatusEffects().size() >= 2 && nuHuoZhongShao != null
                && nuHuoZhongShao.getCurrentCd() == 0) {
            return nuHuoZhongShao;
        }

        if (jiFengBaoYu != null && jiFengBaoYu.getCurrentCd() == 0) {
            return jiFengBaoYu;
        }

        return normal;
    }

    // ========== 05. 鲁智深 ==========
    private Skill luZhiShenAI(Enemy self, Player target, BattleContext context) {
        Skill jinGangNuMu = findSkill(self, "jin_gang_nu_mu");
        Skill daoBaChuiYang = findSkill(self, "dao_ba_chui_yang_liu");
        Skill chanZhangSao = findSkill(self, "chan_zhang_sao");
        Skill normal = findSkill(self, "normal_attack");

        // 护盾=0 → 金刚怒目
        if (self.getBattleStats().getShield() == 0 && jinGangNuMu != null
                && jinGangNuMu.getCurrentCd() == 0) {
            return jinGangNuMu;
        }

        int hpPct = self.getBattleStats().getCurrentHp() * 100 / self.getBaseStats().getMaxHp();

        // 蓄力释放
        if (Boolean.TRUE.equals(self.getMechanicData().get("charging"))) {
            self.getMechanicData().put("charging", false);
            return daoBaChuiYang;
        }

        // HP<50%且护盾>0 → 蓄力
        if (hpPct < 50 && self.getBattleStats().getShield() > 0
                && daoBaChuiYang != null && daoBaChuiYang.getCurrentCd() == 0) {
            self.getMechanicData().put("charging", true);
            System.out.println("【鲁智深】倒拔垂杨柳——看招！");
            return daoBaChuiYang;
        }

        if (chanZhangSao != null && chanZhangSao.getCurrentCd() == 0) {
            return chanZhangSao;
        }

        return normal;
    }

    // ========== 06. 武松 ==========
    private Skill wuSongAI(Enemy self, Player target, BattleContext context) {
        // 醉意系统
        int inebriation = (int) self.getMechanicData().getOrDefault("inebriation", 0);
        inebriation = Math.min(100, inebriation + 15);
        self.getMechanicData().put("inebriation", inebriation);

        Skill normal = findSkill(self, "normal_attack");
        Skill zuiJiu = findSkill(self, "zui_jiu");
        Skill daHuYiShi = findSkill(self, "da_hu_yi_shi");
        Skill zuiBaXian = findSkill(self, "zui_ba_xian_luan_da");

        if (inebriation >= 71 && zuiBaXian != null && zuiBaXian.getCurrentCd() == 0) {
            return zuiBaXian;
        }
        if (inebriation >= 31 && inebriation <= 70 && zuiJiu != null && zuiJiu.getCurrentCd() == 0) {
            return zuiJiu;
        }
        if (inebriation < 31 && daHuYiShi != null && daHuYiShi.getCurrentCd() == 0) {
            return daHuYiShi;
        }
        return normal;
    }

    // ========== 07. 杨志 ==========
    private Skill yangZhiAI(Enemy self, Player target, BattleContext context) {
        int weaponPhase = (context != null ? context.getRound() : 0) % 9;
        Skill yangJiaQiang = findSkill(self, "yang_jia_qiang_fa");
        Skill puDaoZhan = findSkill(self, "pu_dao_zhan");
        Skill lengJian = findSkill(self, "leng_jian");
        Skill normal = findSkill(self, "normal_attack");

        int hpPct = self.getBattleStats().getCurrentHp() * 100 / self.getBaseStats().getMaxHp();
        if (hpPct < 30) {
            // 背水一战：锁定枪形态
            return yangJiaQiang != null && yangJiaQiang.getCurrentCd() == 0 ? yangJiaQiang : normal;
        }

        if (weaponPhase < 3) return yangJiaQiang != null && yangJiaQiang.getCurrentCd() == 0 ? yangJiaQiang : normal;
        if (weaponPhase < 6) return puDaoZhan != null && puDaoZhan.getCurrentCd() == 0 ? puDaoZhan : normal;
        return lengJian != null && lengJian.getCurrentCd() == 0 ? lengJian : normal;
    }

    // ========== 08. 花荣 ==========
    private Skill huaRongAI(Enemy self, Player target, BattleContext context) {
        Skill lianZhuJian = findSkill(self, "lian_zhu_jian");
        Skill luoRiGong = findSkill(self, "luo_ri_gong");
        Skill manTianJianYu = findSkill(self, "man_tian_jian_yu");
        Skill normal = findSkill(self, "normal_attack");

        boolean oddRound = context != null && context.getRound() % 2 == 1;

        if (luoRiGong != null && luoRiGong.getCurrentCd() == 0) return luoRiGong;
        if (oddRound && manTianJianYu != null && manTianJianYu.getCurrentCd() == 0) return manTianJianYu;
        if (lianZhuJian != null && lianZhuJian.getCurrentCd() == 0) return lianZhuJian;
        return normal;
    }

    // ========== 09. 公孙胜 ==========
    private Skill gongSunShengAI(Enemy self, Player target, BattleContext context) {
        Skill zhangXinLei = findSkill(self, "zhang_xin_lei");
        Skill zhiRenTiShen = findSkill(self, "zhi_ren_ti_shen");
        Skill wuLei = findSkill(self, "wu_lei_tian_xin");
        Skill normal = findSkill(self, "normal_attack");

        // 吟唱检测
        if (Boolean.TRUE.equals(self.getMechanicData().get("charging"))) {
            self.getMechanicData().put("charging", false);
            System.out.println("【公孙胜】五雷天心正法——！");
            return wuLei;
        }

        int hpPct = self.getBattleStats().getCurrentHp() * 100 / self.getBaseStats().getMaxHp();

        // HP<50% → 五雷天心正法吟唱
        if (hpPct < 50 && wuLei != null && wuLei.getCurrentCd() == 0) {
            self.getMechanicData().put("charging", true);
            System.out.println("【警告】公孙胜开始吟唱五雷天心正法！下回合必须打断！");
            return wuLei;
        }

        // 纸人替身
        if (zhiRenTiShen != null && zhiRenTiShen.getCurrentCd() == 0) {
            return zhiRenTiShen;
        }

        if (zhangXinLei != null && zhangXinLei.getCurrentCd() == 0) return zhangXinLei;
        return normal;
    }

    // ========== 10. 秦明 ==========
    private Skill qinMingAI(Enemy self, Player target, BattleContext context) {
        int fury = (int) self.getMechanicData().getOrDefault("fury", 0);
        fury = Math.min(10, fury + 1);
        self.getMechanicData().put("fury", fury);

        Skill leiTing = findSkill(self, "lei_ting_zhen_nu");
        Skill fenShen = findSkill(self, "fen_shen_yi_huo");
        Skill langYa = findSkill(self, "lang_ya_po_zhen");
        Skill normal = findSkill(self, "normal_attack");

        // 怒火≥5时溅射效果在CombatResolver中处理
        // 怒火≥8时自损
        if (fury >= 8) {
            int selfDmg = self.getBaseStats().getMaxHp() * 2 / 100;
            self.getBattleStats().takeDamage(selfDmg);
            System.out.println("【秦明】怒火攻心，自损 " + selfDmg + " HP");
        }

        if (fury >= 8 && leiTing != null && leiTing.getCurrentCd() == 0) return leiTing;
        if (fury < 5 && langYa != null && langYa.getCurrentCd() == 0) return langYa;

        int hpPct = self.getBattleStats().getCurrentHp() * 100 / self.getBaseStats().getMaxHp();
        if (hpPct < 30 && fenShen != null && fenShen.getCurrentCd() == 0) return fenShen;

        if (langYa != null && langYa.getCurrentCd() == 0) return langYa;
        return normal;
    }

    // ========== 11. 关胜 ==========
    private Skill guanShengAI(Enemy self, Player target, BattleContext context) {
        // 武圣威压（每回合开始）
        int wuStacks = (int) self.getMechanicData().getOrDefault("wuStacks", 0);
        if (wuStacks < 6) {
            wuStacks++;
            self.getMechanicData().put("wuStacks", wuStacks);
            target.getBattleStats().addModifier(new StatModifier("atk", -0.05, -1));
            target.getBattleStats().addModifier(new StatModifier("def", -0.05, -1));
            System.out.println("【武圣威压】玩家ATK/DEF-5%（当前-" + (wuStacks * 5) + "%）");
        }

        Skill qingLong = findSkill(self, "qing_long_yanyue_zhan");
        Skill daoQi = findSkill(self, "dao_qi_zong_heng");
        Skill wuSheng = findSkill(self, "wu_sheng_jiang_shi");
        Skill normal = findSkill(self, "normal_attack");

        // 半血后优先武圣降世
        int hpPct = self.getBattleStats().getCurrentHp() * 100 / self.getBaseStats().getMaxHp();
        boolean awakened = self.isPhaseTriggered();

        if (!awakened && hpPct < 50) {
            self.setPhaseTriggered(true);
            System.out.println("【关胜】青龙觉醒！");
        }

        if (awakened && wuSheng != null && wuSheng.getCurrentCd() == 0) return wuSheng;
        if (qingLong != null && qingLong.getCurrentCd() == 0) return qingLong;
        if (daoQi != null && daoQi.getCurrentCd() == 0) return daoQi;
        return normal;
    }

    // ========== 12. 宋江 ==========
    private Skill songJiangAI(Enemy self, Player target, BattleContext context) {
        Skill tiTianXingDao = findSkill(self, "ti_tian_xing_dao");
        Skill normal = findSkill(self, "normal_attack");

        // 替天行道
        if (tiTianXingDao != null && tiTianXingDao.getCurrentCd() == 0) return tiTianXingDao;
        return normal;
    }

    // ========== 默认 ==========
    private Skill defaultAI(Enemy self, Player target, BattleContext context) {
        return self.getSkills().stream()
                .filter(s -> s.getCurrentCd() == 0)
                .filter(s -> self.getBattleStats().getCurrentMp() >= s.getMpCost())
                .findFirst()
                .orElse(self.getSkills().get(0));
    }

    private Skill findSkill(Entity entity, String skillId) {
        return entity.getSkills().stream()
                .filter(s -> s.getId().equals(skillId))
                .findFirst().orElse(null);
    }
}
