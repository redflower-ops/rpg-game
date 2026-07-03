package com.shuihu.demo.model;

import com.shuihu.demo.model.entity.Entity;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Map;

/**
 * 公式求值器（简化版）
 * 支持变量: caster.atk/def/hp/maxHp/lostHp, target.*, context.*
 */
public class Formula {

    private static final ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");

    /**
     * 求值公式表达式
     * @param expr   公式字符串 "caster.atk * 1.2 + target.lostHp * 0.1"
     * @param caster 施法者
     * @param target 目标
     * @param ctx    额外上下文
     * @return 计算结果
     */
    public static double evaluate(String expr, Entity caster, Entity target, Map<String, Object> ctx) {
        if (expr == null || expr.isBlank()) return 0;
        try {
            String resolved = resolveVariables(expr, caster, target, ctx);
            Object result = engine.eval(resolved);
            return ((Number) result).doubleValue();
        } catch (Exception e) {
            // 回退：尝试简单解析
            return simpleEval(expr, caster, target, ctx);
        }
    }

    private static String resolveVariables(String expr, Entity caster, Entity target, Map<String, Object> ctx) {
        String s = expr;

        if (caster != null) {
            s = s.replace("caster.lostHp", String.valueOf(caster.getBaseStats().getMaxHp() - caster.getBattleStats().getCurrentHp()));
            s = s.replace("caster.maxHp", String.valueOf(caster.getBaseStats().getMaxHp()));
            s = s.replace("caster.atk", String.valueOf(caster.getBattleStats().getEffectiveAtk(caster.getBaseStats().getAtk())));
            s = s.replace("caster.def", String.valueOf(caster.getBaseStats().getDef()));
            s = s.replace("caster.hp", String.valueOf(caster.getBattleStats().getCurrentHp()));
            s = s.replace("caster.spd", String.valueOf(caster.getBaseStats().getSpd()));
        }
        if (target != null) {
            s = s.replace("target.lostHp", String.valueOf(target.getBaseStats().getMaxHp() - target.getBattleStats().getCurrentHp()));
            s = s.replace("target.maxHp", String.valueOf(target.getBaseStats().getMaxHp()));
            s = s.replace("target.atk", String.valueOf(target.getBaseStats().getAtk()));
            s = s.replace("target.def", String.valueOf(target.getBaseStats().getDef()));
            s = s.replace("target.hp", String.valueOf(target.getBattleStats().getCurrentHp()));
        }
        if (ctx != null) {
            for (var e : ctx.entrySet()) {
                s = s.replace("context." + e.getKey(), String.valueOf(e.getValue()));
            }
        }
        // 替换 × ÷ 为 * /
        s = s.replace("×", "*").replace("÷", "/");
        // 替换百分比
        s = s.replaceAll("(\\d+)%", "($1/100.0)");
        return s;
    }

    private static double simpleEval(String expr, Entity caster, Entity target, Map<String, Object> ctx) {
        // 极简fallback：只做变量替换，不执行运算
        String s = resolveVariables(expr, caster, target, ctx);
        // 只取数字部分（粗略）
        String[] parts = s.split("[^\\d.]");
        for (String p : parts) {
            if (!p.isBlank()) return Double.parseDouble(p);
        }
        return 0;
    }
}
