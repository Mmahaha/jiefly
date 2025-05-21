package solution.v2;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(4, solution.findKthLargest(new int[] {3,2,3,1,2,4,5,5,6}, 4));
    }
}