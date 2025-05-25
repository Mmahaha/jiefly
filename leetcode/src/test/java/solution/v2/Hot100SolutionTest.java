package solution.v2;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}