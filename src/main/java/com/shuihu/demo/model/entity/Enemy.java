package com.shuihu.demo.model.entity;

import com.shuihu.demo.ai.AiController;
import java.util.HashMap;
import java.util.Map;

/**
 * 敌人 —— 继承Entity + AI控制器 + 自定义机制数据
 */
public class Enemy extends Entity {
    private AiController aiController;
    private Map<String, Object> mechanicData;
    private boolean phaseTriggered;
    private String openingLine;

    public Enemy() {
        super();
        this.mechanicData = new HashMap<>();
        this.phaseTriggered = false;
        this.openingLine = "";
    }

    public AiController getAiController() { return aiController; }
    public void setAiController(AiController ai) { this.aiController = ai; }
    public Map<String, Object> getMechanicData() { return mechanicData; }
    public boolean isPhaseTriggered() { return phaseTriggered; }
    public void setPhaseTriggered(boolean v) { this.phaseTriggered = v; }
    public void setOpeningLine(String line) { this.openingLine = line; }
    @Override public String getOpeningLine() { return openingLine; }
}
