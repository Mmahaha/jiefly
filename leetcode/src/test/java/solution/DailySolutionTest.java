package solution;

import org.junit.BeforeClass;
import org.junit.Test;
import solution.DailySolution;

import java.util.Arrays;

import static org.junit.Assert.*;

public class DailySolutionTest {

    static DailySolution solution;

    @BeforeClass
    public static void beforeClass() {
        solution = new DailySolution();
    }

    @Test
    public void mergeAlternately() {
        assertEquals("apbqcd", solution.mergeAlternately("abcd", "pq"));
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

    @Test
    public void arrayStringsAreEqual(){
        assertFalse(solution.arrayStringsAreEqual(new String[]{"a", "cb"}, new String[]{"ab", "c"}));
        assertTrue(solution.arrayStringsAreEqual(new String[]{"abc", "d", "defg"}, new String[]{"abcddefg"}));
    }

    @Test
    public void findMedianSortedArrays(){
        assertEquals(2, solution.findMedianSortedArrays(new int[]{1,3}, new int[]{2}), 0);
        assertEquals(2.5, solution.findMedianSortedArrays(new int[]{1,2}, new int[]{3,4}), 0);
        assertEquals(1, solution.findMedianSortedArrays(new int[]{1}, new int[]{}), 0);
        assertEquals(1.5, solution.findMedianSortedArrays(new int[]{1}, new int[]{2}), 0);
        assertEquals(100000.5, solution.findMedianSortedArrays(new int[]{100000}, new int[]{100001}), 0);
    }

    @Test
    public void maxRepeating(){
        assertEquals(2, solution.maxRepeating("ababc", "ab"));
        assertEquals(1, solution.maxRepeating("ababc", "ba"));
        assertEquals(0, solution.maxRepeating("ababc", "ac"));
        assertEquals(1, solution.maxRepeating("ac", "ac"));
        assertEquals(1, solution.maxRepeating("a", "a"));
        assertEquals(7, solution.maxRepeating("aaabaaaabaaabaaaabaaaabaaaabaaaabaaaabaaaabaaabaaaabaaaabaaaabaaaaba", "aaaba"));
    }

    @Test
    public void parseBoolExpr(){
        assertTrue(solution.parseBoolExpr("!(f)"));
        assertTrue(solution.parseBoolExpr("|(f,t)"));
        assertFalse(solution.parseBoolExpr("&(t,f)"));
        assertFalse(solution.parseBoolExpr("|(&(t,f,t),!(t))|(&(t,f,t),!(t))"));
    }

    @Test
    public void interpret() {
        assertEquals("Goal", solution.interpret("G()(al)"));
        assertEquals("Gooooal", solution.interpret("G()()()()(al)"));
        assertEquals("alGalooG", solution.interpret("(al)G(al)()()G"));
    }


    @Test
    public void match() {
//        System.out.println(solution.match("abcabcbdlabc", "bc"));
    }


    @Test
    public void buildNext() {
        System.out.println(solution.buildNext("ababc"));
    }

    @Test
    public void indexOf() {
        assertEquals(3, solution.indexOf("abaababcdd", "ababc"));
    }

    @Test
    public void ambiguousCoordinates() {
        System.out.println(solution.ambiguousCoordinates("(123)"));
        System.out.println(solution.ambiguousCoordinates("(0123)"));
        System.out.println(solution.ambiguousCoordinates("(00011)"));
        System.out.println(solution.ambiguousCoordinates("(100)"));
    }

    @Test
    public void longestPalindrome() {
        assertEquals("bab", solution.longestPalindrome("babad"));
        assertEquals("bb", solution.longestPalindrome("cbbd"));
        assertEquals("ff", solution.longestPalindrome("ff"));
        assertEquals("ffff", solution.longestPalindrome("ffff"));
        assertEquals("a", solution.longestPalindrome("a"));
        assertEquals("ccc", solution.longestPalindrome("acccb"));
    }

    @Test
    public void countConsistentStrings() {
        assertEquals(2, solution.countConsistentStrings("ab", new String[]{"ad","bd","aaab","baa","badab"}));
        assertEquals(7, solution.countConsistentStrings("abc", new String[]{"a","b","c","ab","ac","bc","abc"}));
        assertEquals(4, solution.countConsistentStrings("cad", new String[]{"cc","acd","b","ba","bac","bad","ac","d"}));
    }


    @Test
    public void customSortString() {
        assertEquals("cbad", solution.customSortString("cba", "abcd"));
        assertEquals("cbad", solution.customSortString("cbafg", "abcd"));
    }

    @Test
    public void subset() {
        assertTrue(solution.subset(new int[]{3, 34, 4, 12, 5, 2},5, 9));
        assertFalse(solution.subset(new int[]{3, 34, 4, 12, 5, 2},5, 13));
    }

    @Test
    public void isIdealPermutation() {
        assertTrue(solution.isIdealPermutation(new int[]{1,0,2}));
        assertFalse(solution.isIdealPermutation(new int[]{1,2,0}));
        assertTrue(solution.isIdealPermutation(new int[]{1,2,3}));
        assertFalse(solution.isIdealPermutation(new int[]{2,3,1}));
    }


    @Test
    public void numMatchingSubseq() {
        assertEquals(3, solution.numMatchingSubseq("abcde", new String[]{"a","bb","acd","ace"}));
        assertEquals(2, solution.numMatchingSubseq("dsahjpjauf", new String[]{"ahjpjau","ja","ahbwzgqnuk","tnmlanowax"}));
    }


    @Test
    public void largestAltitude() {
        assertEquals(1, solution.largestAltitude(new int[]{-5, 1, 5, 0, -7}));
        assertEquals(0, solution.largestAltitude(new int[]{0,-4,-7,-9,-10,-6,-3,-1}));
    }


    @Test
    public void champagneTower() {
        assertEquals(0.25, solution.champagneTower(4,2,0), 0);
    }

    @Test
    public void sumSubseqWidths() {
        assertEquals(6, solution.sumSubseqWidths(new int[]{2,1,3}));
        assertEquals(0, solution.sumSubseqWidths(new int[]{2}));
        assertEquals(857876214, solution.sumSubseqWidths(new int[]{5,69,89,92,31,16,25,45,63,40,16,56,24,40,75,82,40,12,50,62,92,44,67,38,92,22,91,24,26,21,100,42,23,56,64,43,95,76,84,79,89,4,16,94,16,77,92,9,30,13}));
    }

    @Test
    public void soupServings() {
        assertEquals(0.625, solution.soupServings(50), 0);
    }

    @Test
    public void countBalls() {
        assertEquals(2, solution.countBalls(1, 10));
        assertEquals(2, solution.countBalls(5, 15));
        assertEquals(2, solution.countBalls(19, 28));
    }

    @Test
    public void numSubarrayBoundedMax() {
        assertEquals(3, solution.numSubarrayBoundedMax(new int[]{2,1,4,3}, 2, 3));
        assertEquals(7, solution.numSubarrayBoundedMax(new int[]{2,9,2,5,6}, 2, 8));
        assertEquals(15, solution.numSubarrayBoundedMax(new int[]{40,63,99,87,3,86,81,94,85,45}, 11, 93));
    }

    @Test
    public void expressiveWords() {
        assertEquals(1, solution.expressiveWords("heeellooo", new String[]{"hello", "hi","helo"}));
        assertEquals(0, solution.expressiveWords("hello", new String[]{"hella"}));
        assertEquals(1, solution.expressiveWords("a", new String[]{"a"}));
    }

    @Test
    public void check() {
        assertTrue(solution.check(new int[]{3,4,5,1,2}));
        assertTrue(solution.check(new int[]{3,4,5,6,7}));
        assertTrue(solution.check(new int[]{3,3,3,3}));
        assertTrue(solution.check(new int[]{1}));
        assertFalse(solution.check(new int[]{3,2,1}));
        assertFalse(solution.check(new int[]{2,1,3,4}));
    }

    @Test
    public void minOperations() {
        assertEquals(1, solution.minOperations("0100"));
        assertEquals(0, solution.minOperations("01"));
        assertEquals(2, solution.minOperations("1111"));
    }

    @Test
    public void testFreqStack() {
        DailySolution.FreqStack freqStack = solution.new FreqStack();
        freqStack.push(5);
        freqStack.push(7);
        freqStack.push(5);
        freqStack.push(7);
        freqStack.push(4);
        freqStack.push(5);
        assertEquals(5, freqStack.pop());
        assertEquals(7, freqStack.pop());
        assertEquals(5, freqStack.pop());
        assertEquals(4, freqStack.pop());
    }

    @Test
    public void nearestValidPoint() {
        assertEquals(2, solution.nearestValidPoint(3,4, new int[][]{{1,2},{3,1},{2,4},{2,3},{4,4}}));
        assertEquals(0, solution.nearestValidPoint(3,4, new int[][]{{3,4}}));
        assertEquals(-1, solution.nearestValidPoint(3,4, new int[][]{{2,3}}));
    }

    @Test
    public void testMinOperations() {
        assertArrayEquals(new int[]{1,1,3}, solution.minOperations2("110"));
        assertArrayEquals(new int[]{11,8,5,4,3,4}, solution.minOperations2("001011"));
        assertArrayEquals(new int[]{0}, solution.minOperations2("1"));
        assertArrayEquals(new int[]{0}, solution.minOperations2("1"));
    }

    @Test
    public void secondHighest() {
        assertEquals(2, solution.secondHighest("dfa12321afd"));
        assertEquals(-1, solution.secondHighest("abc1111"));
        assertEquals(-1, solution.secondHighest("aa"));
    }

    @Test
    public void numDifferentIntegers() {
        assertEquals(3, solution.numDifferentIntegers("a123bc34d8ef34"));
        assertEquals(2, solution.numDifferentIntegers("leet1234code234"));
        assertEquals(1, solution.numDifferentIntegers("a1b01c001"));
        assertEquals(1, solution.numDifferentIntegers("a1a"));
        assertEquals(2, solution.numDifferentIntegers("1a2"));
    }

    @Test
    public void testMinOperations1() {
        assertEquals(3, solution.minOperations(new int[]{1,2,3,4,5,6}, new int[]{1,1,2,2,2,2}));
        assertEquals(-1, solution.minOperations(new int[]{1,1,1,1,1,1,1}, new int[]{6}));
        assertEquals(3, solution.minOperations(new int[]{6,6}, new int[]{1}));
    }

    @Test
    public void squareIsWhite() {
        assertFalse(solution.squareIsWhite("a1"));
        assertTrue(solution.squareIsWhite("h3"));
    }

    @Test
    public void checkPowersOfThree() {
        assertTrue(solution.checkPowersOfThree(12));
        assertTrue(solution.checkPowersOfThree(91));
        assertFalse(solution.checkPowersOfThree(21));
    }

    @Test
    public void testMinOperations2() {
        assertEquals(3, solution.minOperations(new int[]{1,1,1}));
        assertEquals(14, solution.minOperations(new int[]{1,5,2,4,1}));
        assertEquals(0, solution.minOperations(new int[]{8}));
    }

    @Test
    public void beautySum() {
        assertEquals(5, solution.beautySum("aabcb"));
        assertEquals(17, solution.beautySum("aabcbaa"));
        assertEquals(0, solution.beautySum("ab"));
    }

    @Test
    public void checkIfPangram() {
        assertTrue(solution.checkIfPangram("thequickbrownfoxjumpsoverthelazydog"));
        assertFalse(solution.checkIfPangram("thequickbrownfoxjumpsoverthelazydoff"));
    }

    @Test
    public void getLucky() {
        assertEquals(36, solution.getLucky("iiii", 1));
        assertEquals(6, solution.getLucky("leetcode", 2));
    }

    @Test
    public void minElements() {
        assertEquals(2, solution.minElements(new int[]{1, -1, 1}, 3, -4));
    }

    @Test
    public void minimumDeletions() {
        assertEquals(2, solution.minimumDeletions("aababbab"));
        assertEquals(2, solution.minimumDeletions("bbaaaaabb"));
        assertEquals(0, solution.minimumDeletions("aaa"));
        assertEquals(0, solution.minimumDeletions("bbb"));
        assertEquals(0, solution.minimumDeletions("aaabbb"));
    }

    @Test
    public void maxValue() {
        assertEquals(12, solution.maxValue(new int[][]{{1,3,1},{1,5,1},{4,2,1}}));
        assertEquals(3, solution.maxValue(new int[][]{{3}}));
        assertEquals(9, solution.maxValue(new int[][]{{1,2,5},{3,2,1}}));
    }

    @Test
    public void restoreMatrix() {
        System.out.println(Arrays.deepToString(solution.restoreMatrix(new int[]{5, 7, 10}, new int[]{8, 6, 8})));
        System.out.println(Arrays.deepToString(solution.restoreMatrix(new int[]{0}, new int[]{0})));
        System.out.println(Arrays.deepToString(solution.restoreMatrix(new int[]{3,8}, new int[]{4,7})));
    }

    @Test
    public void smallestEvenMultiple() {
        assertEquals(solution.smallestEvenMultiple(5), 10);
        assertEquals(solution.smallestEvenMultiple(6), 6);
    }
}