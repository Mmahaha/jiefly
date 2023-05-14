package array;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ArraySolutionTest {

    ArraySolution solution;
    @Before
    public void beforeClass() {
        solution = new ArraySolution();
    }


    @Test
    public void search() {
        assertEquals(solution.search(new int[]{1,3,5,5,6,8,9}, 4), -1);
        assertEquals(solution.search(new int[]{1,3,5,5,6,8,9}, 5), 3);
        assertEquals(solution.search(new int[]{-1,0,3,5,9,12}, 13), -1);
    }

    @Test
    public void searchRange() {
        assertArrayEquals(solution.searchRange(new int[]{5,7,7,8,8,10}, 8),new int[]{3,4});
        assertArrayEquals(solution.searchRange(new int[]{5,7,7,8,8,10}, 6),new int[]{-1,-1});
        assertArrayEquals(solution.searchRange(new int[]{}, 0),new int[]{-1,-1});
    }

    @Test
    public void removeElement() {
        assertArrayEquals(new int[]{0,1,9,16,100}, solution.sortedSquares(new int[]{-4,-1,0,3,10}));
        assertArrayEquals(new int[]{4,9,9,49,121}, solution.sortedSquares(new int[]{-7,-3,2,3,11}));
    }

    @Test
    public void minSubArrayLen() {
        assertEquals(2, solution.minSubArrayLen(7, new int[]{2,3,1,2,4,3}));
        assertEquals(1, solution.minSubArrayLen(4, new int[]{1,4,4}));
        assertEquals(0, solution.minSubArrayLen(11, new int[]{1,1,1,1,1,1,1,1}));
    }

    @Test
    public void totalFruit() {
        assertEquals(5, solution.totalFruit(new int[]{3,3,3,1,2,1,1,2,3,3,4}));
    }

    @Test
    public void myTest() {
        Map<String, Integer> map = new HashMap<>();
        map.put("123",1);

    }

    @Test
    public void minWindow() {
        assertEquals("BANC", solution.minWindow("ADOBECODEBANC", "ABC"));
        assertEquals("a", solution.minWindow("a", "a"));
        assertEquals("", solution.minWindow("a", "aa"));
    }

    @Test
    public void generateMatrix() {
        assertArrayEquals(new int[][] {{1,2,3},{8,9,4},{7,6,5}}, solution.generateMatrix(3));
    }
}