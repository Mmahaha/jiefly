package linkedlist;

import org.junit.Before;
import org.junit.Test;
import util.JUtils;

import static org.junit.Assert.*;

public class LinkedListSolutionTest {

    LinkedListSolution solution;

    @Before
    public void before() {
        solution = new LinkedListSolution();
    }

    @Test
    public void removeElements() {
        assertArrayEquals(new int[]{1,2,3,4,5}, JUtils.nodesToArray(solution.removeElements(JUtils.buildListNode(new int[]{1,2,6,3,4,5,6}), 6)));
        assertArrayEquals(new int[]{}, JUtils.nodesToArray(solution.removeElements(JUtils.buildListNode(new int[]{}), 1)));
        assertArrayEquals(new int[]{}, JUtils.nodesToArray(solution.removeElements(JUtils.buildListNode(new int[]{7,7,7,7}), 7)));
    }

    @Test
    public void testMyLinkedList() {
        LinkedListSolution.MyLinkedList myLinkedList = new LinkedListSolution.MyLinkedList();
        myLinkedList.addAtHead(1);
        myLinkedList.addAtTail(3);
        myLinkedList.addAtIndex(1,2);
        myLinkedList.get(1);
        myLinkedList.deleteAtIndex(1);
        myLinkedList.get(1);
        myLinkedList.get(3);
        myLinkedList.deleteAtIndex(3);
        myLinkedList.deleteAtIndex(0);
        myLinkedList.get(0);
        myLinkedList.deleteAtIndex(0);
        myLinkedList.get(0);
    }

    @Test
    public void reverseList() {
        assertArrayEquals(new int[]{5,4,3,2,1}, JUtils.nodesToArray(solution.reverseList(JUtils.buildListNode(new int[]{1,2,3,4,5}))));
        assertArrayEquals(new int[]{1,2}, JUtils.nodesToArray(solution.reverseList(JUtils.buildListNode(new int[]{2,1}))));
        assertArrayEquals(new int[]{}, JUtils.nodesToArray(solution.reverseList(JUtils.buildListNode(new int[]{}))));
    }

    @Test
    public void swapPairs() {
        assertArrayEquals(new int[]{1,2}, JUtils.nodesToArray(solution.swapPairs(JUtils.buildListNode(new int[]{2,1}))));
        assertArrayEquals(new int[]{2,1,4,3}, JUtils.nodesToArray(solution.swapPairs(JUtils.buildListNode(new int[]{1,2,3,4}))));
        assertArrayEquals(new int[]{2,1,4,3,5}, JUtils.nodesToArray(solution.swapPairs(JUtils.buildListNode(new int[]{1,2,3,4,5}))));
        assertArrayEquals(new int[]{}, JUtils.nodesToArray(solution.swapPairs(JUtils.buildListNode(new int[]{}))));
        assertArrayEquals(new int[]{1}, JUtils.nodesToArray(solution.swapPairs(JUtils.buildListNode(new int[]{1}))));
    }
}