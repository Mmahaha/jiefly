import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class SolutionTest {

    static Solution solution;

    @BeforeClass
    public static void beforeClass() {
        solution = new Solution();
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
        assertEquals(136988321, solution.sumSubseqWidths(new int[]{96,87,191,197,40,101,108,35,169,50,168,182,95,80,144,43,18,60,174,13,77,173,38,46,80,117,13,19,11,6,13,118,39,80,171,36,86,156,165,190,53,49,160,192,57,42,97,35,124,200,84,70,145,180,54,141,159,42,66,66,25,95,24,136,140,159,71,131,5,140,115,76,151,137,63,47,69,164,60,172,153,183,6,70,40,168,133,45,116,188,20,52,70,156,44,27,124,59,42,172}));
    }

}