import com.cyberspeed.scratchgame.dto.Config;
import com.cyberspeed.scratchgame.service.Reward;
import com.cyberspeed.scratchgame.service.ScratchGame;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ScratchGame.class)
public class ScratchGameTests {

    private ScratchGame scratchGame;
    private String[][] fixedMatrix;

    /*I m using power mockito to be able to mock the creation of the matrix which is private behaviour
    may be, I could have written the program differently so the population of the symbol counts map won't depend on the matrix creation logic
    I realized it only when I started writing the mockito tests*/

    @Before
    public void setUp() throws Exception {
        Config config = Config.loadConfig("src/main/resources/config.json");
        scratchGame = PowerMockito.spy(new ScratchGame(config));
    }

    @Test
    public void test_same_symbol_3_times() throws Exception {
        fixedMatrix = new String[][]{
                {"A", "B", "A"},
                {"C", "F", "B"},
                {"E", "C", "C"}
        };

        Map<String, Integer> symbolCounts = new HashMap<>();
        symbolCounts.put("C", 3);
        symbolCounts.put("A", 2);
        symbolCounts.put("B", 2);
        symbolCounts.put("F", 1);
        symbolCounts.put("E", 1);


        PowerMockito.doReturn(fixedMatrix).when(scratchGame, "generateRandomMatrix");
        PowerMockito.doReturn(symbolCounts).when(scratchGame, "getSymbolCounts");
        Reward reward = scratchGame.bet(100);
        assertEquals(1, reward.getAppliedWiningCombinations().keySet().size());
        assertEquals(1, reward.getAppliedWiningCombinations().values().stream()
                .mapToInt(List::size)
                .sum());
        assertEquals(1000, reward.getReward(), 0);
    }

    @Test
    public void test_same_symbol_4_times() throws Exception {
        fixedMatrix = new String[][]{
                {"A", "B", "C"},
                {"C", "F", "B"},
                {"E", "C", "C"}
        };

        Map<String, Integer> symbolCounts = new HashMap<>();
        symbolCounts.put("C", 4);
        symbolCounts.put("A", 1);
        symbolCounts.put("B", 2);
        symbolCounts.put("F", 1);
        symbolCounts.put("E", 1);


        PowerMockito.doReturn(fixedMatrix).when(scratchGame, "generateRandomMatrix");
        PowerMockito.doReturn(symbolCounts).when(scratchGame, "getSymbolCounts");
        Reward reward = scratchGame.bet(100);
        assertEquals(1, reward.getAppliedWiningCombinations().keySet().size());
        assertEquals(1, reward.getAppliedWiningCombinations().values().stream()
                .mapToInt(List::size)
                .sum());
        assertEquals(1500, reward.getReward(), 0);
    }

    @Test
    public void test_same_symbol_3_times_and_5x_bonus() throws Exception {
        fixedMatrix = new String[][]{
                {"A", "B", "A"},
                {"C", "F", "B"},
                {"5x", "C", "C"}
        };

        Map<String, Integer> symbolCounts = new HashMap<>();
        symbolCounts.put("C", 3);
        symbolCounts.put("A", 2);
        symbolCounts.put("B", 2);
        symbolCounts.put("F", 1);
        symbolCounts.put("5x", 1);


        PowerMockito.doReturn(fixedMatrix).when(scratchGame, "generateRandomMatrix");
        PowerMockito.doReturn(symbolCounts).when(scratchGame, "getSymbolCounts");
        Reward reward = scratchGame.bet(100);
        assertEquals(5000, reward.getReward(), 0);
        assertEquals(1, reward.getAppliedWiningCombinations().keySet().size());
        assertEquals(1, reward.getAppliedWiningCombinations().values().stream()
                .mapToInt(List::size)
                .sum());
        assertEquals(new ArrayList<>(Arrays.asList("5x")), reward.getAppliedBonusSymbols());
    }

    @Test
    public void test_same_symbol_3_times_and_plus1000_bonus() throws Exception {
        fixedMatrix = new String[][]{
                {"A", "B", "A"},
                {"C", "F", "B"},
                {"+1000", "C", "C"}
        };

        Map<String, Integer> symbolCounts = new HashMap<>();
        symbolCounts.put("C", 3);
        symbolCounts.put("A", 2);
        symbolCounts.put("B", 2);
        symbolCounts.put("F", 1);
        symbolCounts.put("+1000", 1);

        PowerMockito.doReturn(fixedMatrix).when(scratchGame, "generateRandomMatrix");
        PowerMockito.doReturn(symbolCounts).when(scratchGame, "getSymbolCounts");
        Reward reward = scratchGame.bet(100);
        assertEquals(2000, reward.getReward(), 0);
        assertEquals(1, reward.getAppliedWiningCombinations().keySet().size());
        assertEquals(1, reward.getAppliedWiningCombinations().values().stream()
                .mapToInt(List::size)
                .sum());
        assertEquals(new ArrayList<>(Arrays.asList("+1000")), reward.getAppliedBonusSymbols());
    }

    @Test
    public void test_same_symbol_3_times_horizontal() throws Exception {
        fixedMatrix = new String[][]{
                {"A", "B", "A"},
                {"E", "F", "B"},
                {"C", "C", "C"}
        };

        Map<String, Integer> symbolCounts = new HashMap<>();
        symbolCounts.put("C", 3);
        symbolCounts.put("A", 2);
        symbolCounts.put("B", 2);
        symbolCounts.put("F", 1);
        symbolCounts.put("E", 1);

        PowerMockito.doReturn(fixedMatrix).when(scratchGame, "generateRandomMatrix");
        PowerMockito.doReturn(symbolCounts).when(scratchGame, "getSymbolCounts");
        Reward reward = scratchGame.bet(100);
        assertEquals(1, reward.getAppliedWiningCombinations().keySet().size());
        assertEquals(2, reward.getAppliedWiningCombinations().values().stream()
                .mapToInt(List::size)
                .sum());
        assertEquals(2000, reward.getReward(), 0);
    }

    @Test
    public void test_same_symbol_3_times_vertical() throws Exception {
        fixedMatrix = new String[][]{
                {"A", "B", "C"},
                {"E", "F", "C"},
                {"A", "B", "C"}
        };

        Map<String, Integer> symbolCounts = new HashMap<>();
        symbolCounts.put("C", 3);
        symbolCounts.put("A", 2);
        symbolCounts.put("B", 2);
        symbolCounts.put("F", 1);
        symbolCounts.put("E", 1);

        PowerMockito.doReturn(fixedMatrix).when(scratchGame, "generateRandomMatrix");
        PowerMockito.doReturn(symbolCounts).when(scratchGame, "getSymbolCounts");
        Reward reward = scratchGame.bet(100);
        assertEquals(1, reward.getAppliedWiningCombinations().keySet().size());
        assertEquals(2, reward.getAppliedWiningCombinations().values().stream()
                .mapToInt(List::size)
                .sum());
        assertEquals(2000, reward.getReward(), 0);
    }

    @Test
    public void test_same_symbol_3_times_diagonal_left_to_right() throws Exception {
        fixedMatrix = new String[][]{
                {"C", "B", "A"},
                {"E", "C", "F"},
                {"A", "B", "C"}
        };

        Map<String, Integer> symbolCounts = new HashMap<>();
        symbolCounts.put("C", 3);
        symbolCounts.put("A", 2);
        symbolCounts.put("B", 2);
        symbolCounts.put("F", 1);
        symbolCounts.put("E", 1);

        PowerMockito.doReturn(fixedMatrix).when(scratchGame, "generateRandomMatrix");
        PowerMockito.doReturn(symbolCounts).when(scratchGame, "getSymbolCounts");
        Reward reward = scratchGame.bet(100);
        assertEquals(1, reward.getAppliedWiningCombinations().keySet().size());
        assertEquals(2, reward.getAppliedWiningCombinations().values().stream()
                .mapToInt(List::size)
                .sum());
        assertEquals(5000, reward.getReward(), 0);
    }

    @Test
    public void test_same_symbol_3_times_diagonal_right_to_left() throws Exception {
        fixedMatrix = new String[][]{
                {"A", "B", "C"},
                {"E", "C", "F"},
                {"C", "B", "A"}
        };

        Map<String, Integer> symbolCounts = new HashMap<>();
        symbolCounts.put("C", 3);
        symbolCounts.put("A", 2);
        symbolCounts.put("B", 2);
        symbolCounts.put("F", 1);
        symbolCounts.put("E", 1);

        PowerMockito.doReturn(fixedMatrix).when(scratchGame, "generateRandomMatrix");
        PowerMockito.doReturn(symbolCounts).when(scratchGame, "getSymbolCounts");
        Reward reward = scratchGame.bet(100);
        assertEquals(1, reward.getAppliedWiningCombinations().keySet().size());
        assertEquals(2, reward.getAppliedWiningCombinations().values().stream()
                .mapToInt(List::size)
                .sum());
        assertEquals(5000, reward.getReward(), 0);
    }

}
