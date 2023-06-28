import linkedlist.LinkedListSolution;
import org.junit.Before;
import org.junit.Test;
import util.JUtils;

import java.util.Arrays;

import static org.junit.Assert.*;
import static linkedlist.LinkedListSolution.ListNode;

public class Hot100SolutionTest {

    private Hot100Solution solution;

    @Before
    public void before() {
        solution = new Hot100Solution();
    }

    @Test
    public void testLRU() {
        Hot100Solution.LRUCache lruCache = solution.new LRUCache(2);
        System.out.println(lruCache.get(2));
        lruCache.put(2, 6);
    }

    @Test
    public void threeSum() {
        System.out.println(solution.threeSum(new int[]{-1, 0, 1, 2, -1, -4}));
    }

    @Test
    public void myTest() {
        System.out.println("beta-3".compareTo("beta-11"));
    }

    @Test
    public void maxArea() {
        assertEquals(49, solution.maxArea(new int[]{1,8,6,2,5,4,8,3,7}));
        assertEquals(1, solution.maxArea(new int[]{1,1}));
    }

    @Test
    public void letterCombinations() {
        System.out.println(solution.letterCombinations("2689"));
    }

    @Test
    public void convert() {
        assertEquals("PAHNAPLSIIGYIR", solution.convert("PAYPALISHIRING", 3));
        assertEquals("PINALSIGYAHRPI", solution.convert("PAYPALISHIRING", 4));
        assertEquals("A", solution.convert("A", 1));
    }

    @Test
    public void removeNthFromEnd() {
        assertArrayEquals(new int[]{1,2,3,5}, JUtils.nodesToArray(solution.removeNthFromEnd(JUtils.buildListNode(new int[]{1, 2, 3, 4, 5}), 2)));
        assertArrayEquals(new int[]{}, JUtils.nodesToArray(solution.removeNthFromEnd(JUtils.buildListNode(new int[]{1}), 1)));
        assertArrayEquals(new int[]{1}, JUtils.nodesToArray(solution.removeNthFromEnd(JUtils.buildListNode(new int[]{1,2}), 1)));
    }

    @Test
    public void isValid() {
        assertTrue(solution.isValid("()"));
    }

    @Test
    public void mergeTwoLists() {
        assertArrayEquals(new int[]{1,1,2,3,4,4}, JUtils.nodesToArray(solution.mergeTwoLists(JUtils.buildListNode(new int[]{1,2,4}), JUtils.buildListNode(new int[]{1,3,4}))));
        assertArrayEquals(new int[]{}, JUtils.nodesToArray(solution.mergeTwoLists(JUtils.buildListNode(new int[]{}), JUtils.buildListNode(new int[]{}))));
        assertArrayEquals(new int[]{0}, JUtils.nodesToArray(solution.mergeTwoLists(JUtils.buildListNode(new int[]{}), JUtils.buildListNode(new int[]{0}))));
    }

    @Test
    public void generateParenthesis() {
        System.out.println(solution.generateParenthesis(4));
    }

    @Test
    public void combinationSum() {
        System.out.println(solution.combinationSum(new int[]{2, 3, 6, 7}, 4));
        System.out.println(solution.combinationSum(new int[]{2, 3, 6, 7}, 7));
        System.out.println(solution.combinationSum(new int[]{2, 3, 5}, 8));
        System.out.println(solution.combinationSum(new int[]{2}, 1));
    }

    @Test
    public void combinationSum2() {
        System.out.println(solution.combinationSum2(new int[]{10,1,2,7,6,1,5}, 8));
        System.out.println(solution.combinationSum2(new int[]{2,5,2,1,2}, 5));
        System.out.println(solution.combinationSum2(new int[]{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,3,3}, 30));
    }

    @Test
    public void climbStairs() {
        assertEquals(2, solution.climbStairs(2));
        assertEquals(3, solution.climbStairs(3));
    }

    @Test
    public void trans() {
//        solution.trans("12",2);
    }

    @Test
    public void subsets() {
        System.out.println(solution.subsets(new int[]{1,2,3}));
        System.out.println(solution.subsets(new int[]{0}));
    }

    @Test
    public void mergeKLists() {
        System.out.println(Arrays.toString(JUtils.nodesToArray(solution.mergeKLists(new ListNode[]{
                JUtils.buildListNode(new int[]{1, 4, 5}),
                JUtils.buildListNode(new int[]{1, 3, 4}),
                JUtils.buildListNode(new int[]{2, 6})
        }))));
        solution.mergeKLists(new ListNode[]{new ListNode()});
        solution.mergeKLists(new ListNode[]{null});
    }

}