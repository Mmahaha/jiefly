package solution.v2;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Hot100SolutionTest {

    Hot100Solution solution = new Hot100Solution();

    @Test
    void longestConsecutive() {

    }

    @Test
    void findAnagrams() {
        assertEquals(Arrays.asList(0, 6), solution.findAnagrams("cbaebabacd", "abc"));
    }

    @Test
    void findKthLargest() {
        assertEquals(5, solution.findKthLargest(new int[]{3, 2, 1, 5, 6, 4}, 2));
    }

    @Test
    public void lengthOfLIS() {
        assertEquals(4, solution.lengthOfLIS(new int[]{10, 9, 2, 5, 3, 7, 101, 18}));
    }

    @Test
    public void minWindow() {
        assertEquals("a", solution.minWindow("ab", "a"));
    }

    @Test
    public void canFinish() {
        assertFalse(solution.canFinish(8, new int[][]{{1,0},{2,6},{1,7},{5,1},{6,4},{7,0},{0,5}}));
    }
}