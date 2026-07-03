package com.shuihu.demo.model.status;

public enum StatusType {
    BLEEDING("流血", "每回合损失ATK×0.1伤害"),
    POISON("中毒", "每回合损失最大HP×3%伤害"),
    BLIND("致盲", "命中率降低30%"),
    STUN("眩晕", "无法行动一回合"),
    BURN("灼烧", "每回合损失ATK×0.15伤害，可叠3层"),
    ARMOR_BREAK("破甲", "防御力降低50%"),
    WEAK("虚弱", "攻击力降低30%"),
    CONFUSE("混乱", "攻击随机目标"),
    ROOT("定身", "无法闪避/招架"),
    DEFEND("防御姿态", "所受伤害减半"),
    FURY("怒火", "每层+5%ATK（秦明）"),
    DRUNK("醉意", "武松醉意阶段");

    private final String displayName;
    private final String description;

    StatusType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}
