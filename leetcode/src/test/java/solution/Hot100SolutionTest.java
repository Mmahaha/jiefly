package solution;

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

    @Test
    public void search() {
        assertEquals(4, solution.search(new int[]{4,5,6,7,0,1,2}, 0));
        assertEquals(1, solution.search(new int[]{1,3}, 3));
        assertEquals(1, solution.search(new int[]{3,1}, 1));
        assertEquals(0, solution.search(new int[]{1,3,5}, 1));
    }

    @Test
    public void groupAnagrams() {
        System.out.println(solution.groupAnagrams(new String[]{"eat", "tea", "tan", "ate", "nat", "bat"}));
    }

    @Test
    public void maxSubArray() {
        assertEquals(6, solution.maxSubArray(new int[]{-2,1,-3,4,-1,2,1,-5,4}));
    }

    @Test
    public void canJump() {
        assertTrue(solution.canJump(new int[]{2,3,1,1,4}));
        assertFalse(solution.canJump(new int[]{3,2,1,0,4}));
        assertTrue(solution.canJump(new int[]{0}));
        assertFalse(solution.canJump(new int[]{0,1}));
    }

    @Test
    public void merge() {
        System.out.println(Arrays.deepToString(solution.mergeListNode(new int[][]{{1, 3}, {2, 6}, {8, 10}, {15, 18}})));
        System.out.println(Arrays.deepToString(solution.mergeListNode(new int[][]{{1, 4}, {2, 3}})));
    }

    @Test
    public void permute() {
        System.out.println(solution.permute(new int[]{0,1}));
    }

    @Test
    public void sortColors() {
        int[] ints = {1, 2, 0, 2, 1, 0, 2};
        solution.sortColors(ints);
        System.out.println(Arrays.toString(ints));
    }

    @Test
    public void hammingDistance() {
        assertEquals(2, solution.hammingDistance(1, 4));
        assertEquals(1, solution.hammingDistance(3, 1));
        assertEquals(2, solution.hammingDistance(93, 73));
    }

    @Test
    public void countBits() {
        assertArrayEquals(new int[]{0,1,1}, solution.countBits(2));
    }

    @Test
    public void minDistance() {
        assertEquals(3, solution.minDistance("horse", "ros"));
        assertEquals(5, solution.minDistance("intention", "execution"));
    }

    @Test
    public void nextPermutation() {
        solution.nextPermutation(new int[]{2,3,1});
    }

    @Test
    public void exist() {
        assertTrue(solution.exist(new char[][] {{'A','B','C','E'},{'S','F','C','S'},{'A','D','E','E'}}, "ABCCED"));
        assertTrue(solution.exist(new char[][] {{'A','B','C','E'},{'S','F','C','S'},{'A','D','E','E'}}, "SEE"));
        assertFalse(solution.exist(new char[][] {{'A','B','C','E'},{'S','F','C','S'},{'A','D','E','E'}}, "ABCB"));
        assertTrue(solution.exist(new char[][] {{'C','A','A'},{'A','A','A'},{'B','C','D'}}, "AAB"));
        assertTrue(solution.exist(new char[][] {{'A','B','C','E'},{'S','F','E','S'},{'A','D','E','E'}}, "ABCESEEEFS"));
    }

    @Test
    public void moveZeroes() {
        int[] array = new int[] {0,1,0,3,12};
        solution.moveZeroes(array);
        assertArrayEquals(new int[] {1,3,12,0,0}, array);
    }

    @Test
    public void subarraySum() {
        assertEquals(2, solution.subarraySum(new int[] {1,1,1}, 2));
        assertEquals(2, solution.subarraySum(new int[] {1,2,3}, 3));
        assertEquals(3, solution.subarraySum(new int[] {1,-1,0}, 0));
    }

    @Test
    public void findAnagrams() {
        assertEquals(Arrays.asList(0, 6), solution.findAnagrams("cbaebabacd", "abc"));
        assertEquals(Arrays.asList(0, 1, 2), solution.findAnagrams("abab", "ab"));
        assertEquals(Arrays.asList(), solution.findAnagrams("abab", "e"));
        assertEquals(Arrays.asList(0), solution.findAnagrams("e", "e"));
    }

    @Test
    public void setZeroes() {
        int[][] array = new int[][]{{0,1,2,0},{3,4,5,2},{1,3,1,5}};
        solution.setZeroes(array);
        assertArrayEquals(new int[][]{{0,0,0,0},{0,4,5,0},{0,3,1,0}}, array);
        int[][] array2 = new int[][]{{1,0,3}};
        solution.setZeroes(array2);
        assertArrayEquals(new int[][]{{0,0,0}}, array2);
    }

    @Test
    public void searchMatrix() {
        System.out.println(null != null);
        int[][] matrix = new int[][]{{1,4,7,11,15},{2,5,8,12,19},{3,6,9,16,22},{10,13,14,17,24},{18,21,23,26,30}};
        assertTrue(solution.searchMatrix(matrix, 5));
    }

    @Test
    public void sortList() {
        ListNode listNode = JUtils.buildListNode(new int[]{4, 2, 1, 3});
        ListNode sorted = solution.sortList(listNode);
        assertArrayEquals(new int[]{1, 2, 3, 4}, JUtils.nodesToArray(sorted));
    }

    @Test
    public void sortList2() {
        ListNode listNode = JUtils.buildListNode(new int[]{-1,5,3,4,0});
        ListNode sorted = solution.sortList2(listNode);
        assertArrayEquals(new int[]{-1,0,3,4,5}, JUtils.nodesToArray(sorted));
    }
}