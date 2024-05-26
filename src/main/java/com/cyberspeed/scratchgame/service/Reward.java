package com.cyberspeed.scratchgame.service;

import com.cyberspeed.scratchgame.dto.WinCombination;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Reward {

    String[][] matrix;
    double reward;
    Map<String, List<String>> appliedWiningCombinations;
    List<String> appliedBonusSymbols;

    public Map<String, List<String>> getAppliedWiningCombinations() {
        return appliedWiningCombinations;
    }

    public void setAppliedWiningCombinations(Map<String, List<String>> appliedWiningCombinations) {
        this.appliedWiningCombinations = appliedWiningCombinations;
    }

    public String[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(String[][] matrix) {
        this.matrix = matrix;
    }

    public double getReward() {
        return reward;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

      public List<String> getAppliedBonusSymbols() {
        return appliedBonusSymbols;
    }

    public void setAppliedBonusSymbols(List<String> appliedBonusSymbols) {
        this.appliedBonusSymbols = appliedBonusSymbols;
    }

    @Override
    public String toString() {
        return "Reward{" +
                "matrix=" + Arrays.toString(matrix) +
                ", reward=" + reward +
                ", appliedWiningCombinations=" + appliedWiningCombinations +
                ", appliedBonusSymbols=" + appliedBonusSymbols +
                '}';
    }
}
