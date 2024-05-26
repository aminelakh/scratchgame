package com.cyberspeed.scratchgame.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Symbol {
    @JsonProperty("reward_multiplier")
    private Double rewardMultiplier;
    private String type;
    private Integer extra;
    private String impact;

    public Double getRewardMultiplier() { return rewardMultiplier; }
    public void setRewardMultiplier(Double rewardMultiplier) { this.rewardMultiplier = rewardMultiplier; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getExtra() { return extra; }
    public void setExtra(Integer extra) { this.extra = extra; }

    public String getImpact() { return impact; }
    public void setImpact(String impact) { this.impact = impact; }

    public boolean isStandardSymbol(){
        return "standard".equals(type);
    }
    public boolean isBonusSymbol(){
        return "bonus".equals(type);
    }
}
