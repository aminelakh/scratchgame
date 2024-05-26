package com.cyberspeed.scratchgame.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Probabilities {
    @JsonProperty("standard_symbols")
    private StandardSymbols standard_symbols;

    @JsonProperty("bonus_symbols")
    private BonusSymbols bonus_symbols;

    public StandardSymbols getStandardSymbols() { return standard_symbols; }
    public void setStandardSymbols(StandardSymbols standard_symbols) { this.standard_symbols = standard_symbols; }

    public BonusSymbols getBonusSymbols() { return bonus_symbols; }
    public void setBonusSymbols(BonusSymbols bonus_symbols) { this.bonus_symbols = bonus_symbols; }
}
