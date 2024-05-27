package com.cyberspeed.scratchgame.service;

import com.cyberspeed.scratchgame.dto.Config;
import com.cyberspeed.scratchgame.dto.Symbol;
import com.cyberspeed.scratchgame.dto.WinCombination;

import java.util.*;
import java.util.stream.Collectors;

public class ScratchGame {

    private final Config config;
    private int rows;
    private int columns;
    private static final int DEFAULT_ROWS = 3;
    private static final int DEFAULT_COLUMNS = 3;
    private Map<String, Symbol> symbols;
    private Map<String, WinCombination> winCombinations;
    private Map<String, Integer> standardProbabilities;
    private Map<String, Integer> bonusProbabilities;
    private Random random = new Random();
    private Map<String, Integer> symbolCounts = new HashMap<>();
    private Set<String> horizontalLines = new HashSet<>();
    private Set<String> verticalLines = new HashSet<>();
    private Set<String> leftToRightDiagonal = new HashSet<>();
    private Set<String> rightToLeftDiagonal = new HashSet<>();


    public ScratchGame(Config config) {
        this.config = config;
        this.symbols = config.getSymbols();
        this.standardProbabilities = config.getProbabilities().getStandardSymbols().getSymbols();
        this.bonusProbabilities = config.getProbabilities().getBonusSymbols().getSymbols();
        this.rows = Integer.valueOf(config.getRows()) == null ? DEFAULT_ROWS : config.getRows();
        this.columns = Integer.valueOf(config.getColumns()) == null ? DEFAULT_COLUMNS : config.getColumns();
        this.winCombinations = config.getWinCombinations();
    }

    /**
     *
     * @param amount
     * @return
     * @throws Exception
     */
    public Reward bet(double amount) throws Exception {

        Reward reward = new Reward();
        Map<String, List<String>> appliedWiningCombinations = new HashMap<>();
        List<String> appliedBonusSymbols = new ArrayList<>();
        String[][] matrix = generateRandomMatrix();

        /*It is not clear in the requirements if the rule of min 3 occurrences rule should be applied to standard symbols and bonus symbols too
         * or only standard symbols, I have made the choice to apply it to basic symbols only as It makes more sense to me */

        Map<String, Integer> standardWiningSymbolsMap = getSymbolCounts().entrySet()
                .stream()
                .filter(entry -> entry.getValue() >= 3)
                .filter(entry -> config.getSymbols().get(entry.getKey()).isStandardSymbol())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        for (Map.Entry<String, Integer> entry : standardWiningSymbolsMap.entrySet()) {
            String symbol = entry.getKey();
            Integer count = entry.getValue();
            List<String> combinations = new ArrayList<>();
            for (Map.Entry<String, WinCombination> c : winCombinations.entrySet()) {
                String combination = c.getKey();
                WinCombination winCombination = c.getValue();
                if ("same_symbols".equals(winCombination.getGroup()) && count.equals(winCombination.getCount())) {
                    combinations.add(combination);
                    appliedWiningCombinations.put(symbol, combinations);
                    break;
                }
            }
        }

        Map<String, Integer> bonusWiningSymbolsMap = getSymbolCounts().entrySet()
                .stream()
                .filter(entry -> config.getSymbols().get(entry.getKey()).isBonusSymbol())
                .filter(entry -> !config.getSymbols().get(entry.getKey()).getImpact().equals("miss"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        /*Bonus symbols might occur multiple times, this will be considered in the reward calculation as I m keeping a map with their counters,
        however only unique symbols will be stored below*/

        appliedBonusSymbols.addAll(bonusWiningSymbolsMap.keySet());
        getHorizontalLines(matrix);
        getVerticalLines(matrix);

        /* we can only check diagonals for square matrix*/
        if (rows == columns) {
            getLeftToRightDiagonal(matrix);
            getRightToLeftDiagonal(matrix);
        }

        /* For a symbol to be eligible win in the groups : same_symbols_horizontally, same_symbols_vertically, same_symbols_diagonally_left_to_right
        or same_symbols_diagonally_right_to_left. It has at least occur 3 times, this is why I m iterating on same_symbols map only */
        for (Map.Entry<String, List<String>> entry : appliedWiningCombinations.entrySet()) {
            String symbol = entry.getKey();
            List<String> combinations = entry.getValue();

            if (horizontalLines.contains(symbol)) {
                combinations.add("same_symbols_horizontally");
            }
            if (verticalLines.contains(symbol)) {
                combinations.add("same_symbols_vertically");
            }
            if (leftToRightDiagonal.contains(symbol)) {
                combinations.add("same_symbols_diagonally_left_to_right");
            }
            if (rightToLeftDiagonal.contains(symbol)) {
                combinations.add("same_symbols_diagonally_right_to_left");
            }
        }

        /*If I had more time, I could have used builder design pattern here or added a dependency to Lombok and use @Builder annotation*/
        reward.setMatrix(matrix);
        reward.setAppliedWiningCombinations(appliedWiningCombinations);
        reward.setAppliedBonusSymbols(appliedBonusSymbols);
        reward.setReward(calculateTotalReward(appliedWiningCombinations, appliedBonusSymbols, amount));
        return reward;
    }

    /**
     *
     * @param appliedWiningCombinations
     * @param appliedBonusSymbols
     * @param amount
     * @return
     */
    private double calculateTotalReward(Map<String, List<String>> appliedWiningCombinations, List<String> appliedBonusSymbols, double amount) {

        double totalReward = 0.0;

        if (appliedWiningCombinations.size() > 0) {
            for (Map.Entry<String, List<String>> entry : appliedWiningCombinations.entrySet()) {
                double rewardBySymbol = 1.0;
                String symbol = entry.getKey();
                List<String> combinations = entry.getValue();
                Double symbolReward = symbols.get(symbol).getRewardMultiplier();
                rewardBySymbol = rewardBySymbol * symbolReward;
                for (String combination : combinations) {
                    WinCombination winCombination = winCombinations.get(combination);
                    double combinationReward = winCombination.getRewardMultiplier();
                    rewardBySymbol = rewardBySymbol * combinationReward;
                }
                totalReward = totalReward + rewardBySymbol * amount;
            }
        }

        if (!appliedBonusSymbols.isEmpty()) {
            /* Bonus symbols might occur multiple times in general, also same bonus symbol might appear multiple times
            I have made the choice to apply all multiply rewards first then extra ones */
            double allMultiplyRewards = 1.0;
            double allExtraRewards = 0.0;
            for (String bonus : appliedBonusSymbols) {
                Symbol symbol = symbols.get(bonus);
                if ("multiply_reward".equals(symbol.getImpact())) {
                    Double rewardMultiplier = symbol.getRewardMultiplier() * getSymbolCounts().get(bonus);
                    allMultiplyRewards = allMultiplyRewards * rewardMultiplier;
                }
            }
            for (String bonus : appliedBonusSymbols) {
                Symbol symbol = symbols.get(bonus);
                if ("extra_bonus".equals(symbol.getImpact())) {
                    Integer extra = symbol.getExtra() * getSymbolCounts().get(bonus);
                    allExtraRewards = allExtraRewards + extra;
                }
            }
            if (totalReward > 0) {
                totalReward = totalReward * allMultiplyRewards + allExtraRewards;
            }
        }
        return totalReward;
    }

    /**
     *
     * @return matrix
     * @throws Exception
     */
    private String[][] generateRandomMatrix() throws Exception {
        Map<String, Double> normalizedProbabilities = getNormalizedProbabilities();
        String[][] matrix = new String[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                String symbol = getRandomSymbol(normalizedProbabilities, random);
                matrix[i][j] = symbol;
                symbolCounts.put(symbol, symbolCounts.getOrDefault(symbol, 0) + 1);
            }
        }
        return matrix;
    }


    /**
     * @param normalizedProbabilities
     * @param random
     * @return
     * @throws Exception
     */
    private String getRandomSymbol(Map<String, Double> normalizedProbabilities, Random random) throws Exception {

        double r = random.nextDouble();
        double cumulativeProbability = 0.0;

        for (Map.Entry<String, Double> entry : normalizedProbabilities.entrySet()) {
            String symbol = entry.getKey();
            Double probability = entry.getValue();
            cumulativeProbability = cumulativeProbability + probability;
            if (r <= cumulativeProbability) {
                return symbol;
            }
        }
        throw new Exception("Error while generating Random Symbol");
    }

    /**
     *
     * @return normalizedProbabilities
     */
    private Map<String, Double> getNormalizedProbabilities() {

        Map<String, Double> normalizedProbabilities = new HashMap<>();
        Integer sumStandardProbabilities = standardProbabilities.values().stream().mapToInt(Integer::intValue).sum();
        Integer sumBonusProbabilities = bonusProbabilities.values().stream().mapToInt(Integer::intValue).sum();
        for (Map.Entry<String, Integer> entry : standardProbabilities.entrySet()) {
            String symbol = entry.getKey();
            Integer probability = entry.getValue();
            Double normalizedProbability = probability.doubleValue() / sumStandardProbabilities.doubleValue();
            normalizedProbabilities.put(symbol, normalizedProbability);
        }
        for (Map.Entry<String, Integer> entry : bonusProbabilities.entrySet()) {
            String symbol = entry.getKey();
            Integer probability = entry.getValue();
            Double normalizedProbability = probability.doubleValue() / sumBonusProbabilities.doubleValue();
            normalizedProbabilities.put(symbol, normalizedProbability);
        }
        return normalizedProbabilities;
    }

    /**
     *
     * @param matrix
     */
    private void getHorizontalLines(String[][] matrix) {
        int rowsNumber = matrix.length;
        int cols = matrix[0].length;

        for (int i = 0; i < rowsNumber; i++) {
            String symbol = matrix[i][0];
            boolean isHorizontalLine = true;
            for (int j = 1; j < cols; j++) {
                if (!matrix[i][j].equals(symbol)) {
                    isHorizontalLine = false;
                    break;
                }
            }
            if (isHorizontalLine) {
                horizontalLines.add(symbol);
            }
        }
    }

    /**
     * @param matrix
     */
    private void getVerticalLines(String[][] matrix) {
        int rowsNumber = matrix.length;
        int cols = matrix[0].length;
        for (int j = 0; j < cols; j++) {
            String symbol = matrix[0][j];
            boolean isVerticalLine = true;
            for (int i = 1; i < rowsNumber; i++) {
                if (!matrix[i][j].equals(symbol)) {
                    isVerticalLine = false;
                    break;
                }
            }
            if (isVerticalLine) {
                verticalLines.add(symbol);
            }
        }
    }

    /**
     *
     * @param matrix
     */
    private void getLeftToRightDiagonal(String[][] matrix) {
        int rowsNumber = matrix.length;
        String symbol = matrix[0][0];
        boolean isDiagonalLine = true;
        for (int i = 1; i < rowsNumber; i++) {
            if (!matrix[i][i].equals(symbol)) {
                isDiagonalLine = false;
                break;
            }
        }
        if (isDiagonalLine) {
            leftToRightDiagonal.add(symbol);
        }
    }

    /**
     *
     * @param matrix
     */
    private void getRightToLeftDiagonal(String[][] matrix) {
        int rowsNumber = matrix.length;
        int cols = matrix[0].length;
        String symbol = matrix[0][cols - 1];
        boolean isDiagonalLine = true;

        for (int i = 1; i < rowsNumber; i++) {
            if (!matrix[i][cols - 1 - i].equals(symbol)) {
                isDiagonalLine = false;
                break;
            }
        }
        if (isDiagonalLine) {
            rightToLeftDiagonal.add(symbol);
        }
    }
    private Map<String, Integer> getSymbolCounts() {
        return this.symbolCounts;
    }

}
