package linkedlist;

import solution.Hot100Solution;

import java.util.HashMap;
import java.util.Map;

public class LinkedListSolution {

    // 203. 移除链表元素
    public ListNode removeElements(ListNode head, int val) {
        ListNode dummyListNode = new ListNode(0, head), iter = dummyListNode;
        while (iter.next != null) {
            if (iter.next.val == val) {
                iter.next = iter.next.next;
            } else {
                iter = iter.next;
            }
        }
        return dummyListNode.next;
    }

    // 707. 设计链表
    static class MyLinkedList {

        private ListNode head;

        public MyLinkedList() {

        }

        public int get(int index) {
            int i = 0;
            ListNode iter = head;
            while (iter != null && i < index) {iter = iter.next; i++;}
            return iter != null ? iter.val : -1;
        }

        public void addAtHead(int val) {
            ListNode newHead = new ListNode(val);
            if (head != null) {
                newHead.next = head;
            }
            head = newHead;
        }

        public void addAtTail(int val) {
            ListNode newTail = new ListNode(val);
            if (head == null) {
                head = newTail;
            } else {
                ListNode iter = head;
                while ((iter.next) != null){iter = iter.next;}
                iter.next = newTail;
            }
        }

        public void addAtIndex(int index, int val) {
            if (index == 0) {
                addAtHead(val);
                return;
            }
            int i = 0;
            ListNode iter = head;
            while (iter != null && i < index - 1) {iter = iter.next; i++;}
            if (iter != null) {
                iter.next = new ListNode(val, iter.next);
            }
        }

        public void deleteAtIndex(int index) {
            if (index == 0 && head != null) {
                head = head.next;
            }
            int i = 0;
            ListNode iter = head;
            while (iter != null && i < index - 1) {iter = iter.next; i++;}
            if (iter != null && iter.next != null) {
                iter.next = iter.next.next;
            }
        }

    }

    // 206. 反转链表
    public ListNode reverseList(ListNode head) {
        ListNode iter = head, prev = null, next;
        while (iter != null) {
            next = iter.next;
            iter.next = prev;
            prev = iter;
            iter = next;
        }
        return prev;
    }

    // 24. 两两交换链表中的节点
    public ListNode swapPairs(ListNode head) {
        ListNode result = (head != null && head.next != null) ? head.next : head;
        ListNode prev = null, iter = head, next;
        while (iter != null && iter.next != null) {
            next = iter.next;
            iter.next = next.next;
            next.next = iter;
            if (prev != null) {prev.next = next;}
            prev = iter;
            iter = iter.next;
        }
        return result;
    }

    //todo 19.删除链表的倒数第N个节点
    public ListNode removeNthFromEnd(ListNode head, int n) {
        return null;
    }


    // 138. 随机链表的复制
    private final Map<Node, Node> nodeMap = new HashMap<>(16);
    public Node copyRandomList(Node head) {
        if (head == null) {
            return null;
        }
        if (nodeMap.containsKey(head)) {
            return nodeMap.get(head);
        }
        Node newNode = new Node(head.val);
        nodeMap.put(head, newNode);
        newNode.next = copyRandomList(head.next);
        newNode.random = copyRandomList(head.random);
        return newNode;
    }

    class Node {
        int val;
        Node next;
        Node random;

        public Node(int val) {
            this.val = val;
            this.next = null;
            this.random = null;
        }
    }

    public static class ListNode {
        public int val;
        public ListNode next;

        public ListNode() {
        }

        public ListNode(int val) {
            this.val = val;
        }

        public ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }
}
