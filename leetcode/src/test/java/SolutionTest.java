import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class SolutionTest {

    static Solution solution;

    @BeforeClass
    public static void beforeClass() {
        solution = new Solution();
    }

    @Test
    public void mergeAlternately() {
    }

    @Test
    public void partitionDisjoint() {
        assertEquals(3, solution.partitionDisjoint(new int[]{5,0,3,8,6}));
        assertEquals(4, solution.partitionDisjoint(new int[]{1,1,1,0,6,12}));
        assertEquals(1, solution.partitionDisjoint(new int[]{1,1}));
        assertEquals(7, solution.partitionDisjoint(new int[]{32,57,24,19,0,24,49,67,87,87}));
    }

    @Test
    public void magicalString() {
        assertEquals(3, solution.magicalString(6));
        assertEquals(1, solution.magicalString(1));
        assertEquals(3, solution.magicalString(5));
        assertEquals(2, solution.magicalString(4));
        assertEquals(4, solution.magicalString(7));
        assertEquals(6, solution.magicalString(13));
        assertEquals(7, solution.magicalString(14));

    }


}