import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
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
}