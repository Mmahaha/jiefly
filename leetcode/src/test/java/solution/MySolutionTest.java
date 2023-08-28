package solution;

import org.junit.Before;
import org.junit.Test;
import solution.MySolution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MySolutionTest {

    MySolution solution;

    @Before
    public void beforeClass() {
        solution = new MySolution();
    }

    @Test
    public void wordPuzzle() {
        System.out.println(solution.wordPuzzle("nsew,isld,asldf,qrrw,vzxn", "adssbd,dlsi,vxzn,gwr"));
    }

    @Test
    public void getMaxVersion() {
        assertEquals("2.5.1-C", solution.getMaxVersion("2.5.1-C", "1.4.2-D"));
        assertEquals("1.3.11-S2", solution.getMaxVersion("1.3.11-S2", "1.3.11-S13"));
        assertEquals("1.05.1", solution.getMaxVersion("1.05.1", "1.5.01"));
        assertEquals("1.5.0", solution.getMaxVersion("1.5", "1.5.0"));
        assertEquals("1.5.1-a", solution.getMaxVersion("1.5.1-A", "1.5.1-a"));
    }

    @Test
    public void getArrayCenter() {
        assertEquals(3, solution.getArrayCenter(new int[]{2,5,3,6,5,6}));
        assertEquals(1, solution.getArrayCenter(new int[]{1,2,1}));
        assertEquals(0, solution.getArrayCenter(new int[]{5}));
        assertEquals(2, solution.getArrayCenter(new int[]{5,3,6,1,15}));
        assertEquals(1, solution.getArrayCenter(new int[]{1,3}));
        assertEquals(0, solution.getArrayCenter(new int[]{3,1}));
    }

    @Test
    public void longestCommonPostfix() {
        assertEquals("c", solution.longestCommonPostfix(new String[]{"abc","bbc","c"}));
    }

    @Test
    public void detectCycle() {
//        ListNode node4 = new ListNode(4);
//        ListNode node3 = new ListNode(3);
//        ListNode node2 = new ListNode(2);
//        ListNode node1 = new ListNode(1);
//        node1.next = node2; node2.next = node3; node3.next = node4; node4.next = node2;
//        ListNode listNode = solution.detectCycle(node1);
//        assertEquals(2, listNode.val);



    }

    @Test
    public void getPath() {
    }

    @Test
    public void addStrings() {
        assertEquals("533", solution.addStrings("456", "77"));
        assertEquals("134", solution.addStrings("11", "123"));
        assertEquals("0", solution.addStrings("0", "0"));
    }
}