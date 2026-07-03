package com.shuihu.demo.model.weapon;

public enum WeaponType {
    BLADE("刀"), SWORD("剑"), SPEAR("枪"),
    HIDDEN_WEAPON("暗器"), GAUNTLET("拳套");

    private final String displayName;
    WeaponType(String name) { this.displayName = name; }
    public String getDisplayName() { return displayName; }
}
