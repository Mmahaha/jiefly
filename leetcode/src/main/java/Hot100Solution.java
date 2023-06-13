import linkedlist.LinkedListSolution;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static linkedlist.LinkedListSolution.ListNode;

public class Hot100Solution {

    // 146.LRU缓存
    class LRUCache {
        private final Map<Integer, Node> storeMap;
        private final int capacity;
        private Node head;
        private Node tail;
        public LRUCache(int capacity) {
            this.capacity = capacity;
            storeMap = new HashMap<>(capacity);
            head = new Node(null, null, -1, -1);
            tail = new Node(head, null, -1, -1);
            head.next = tail;
        }

        public int get(int key) {
            Node node = storeMap.get(key);
            if (node == null) {
                return -1;
            }
            // moveToHead
            moveToHead(node);
            return node.val;
        }

        public void put(int key, int value) {
            if (storeMap.containsKey(key)) {
                Node node = storeMap.get(key);
                node.val = value;
                moveToHead(node);
                return;
            }
            if (storeMap.size() < capacity) {
                createHeadNode(key, value);
                return;
            }
            // deleteTail && removeKey
            Node last = tail.prev;
            last.prev.next = tail;
            tail.prev = last.prev;
            storeMap.remove(last.key);
            createHeadNode(key, value);
        }

        private void moveToHead(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            node.prev = head;
            node.next = head.next;
            head.next.prev = node;
            head.next = node;
        }

        private void createHeadNode(int key, int value) {
            Node newNode = new Node(head, head.next, key, value);
            storeMap.put(key, newNode);
            head.next.prev = newNode;
            head.next = newNode;
        }
    }
    private static class Node {
        Node prev;
        Node next;
        Integer key;
        Integer val;

        public Node(Node prev, Node next, Integer key, Integer val) {
            this.prev = prev;
            this.next = next;
            this.key = key;
            this.val = val;
        }
    }

    // 15. 三数之和
    public List<List<Integer>> threeSum(int[] nums) {
        Set<List<Integer>> result = new HashSet<>();
        Arrays.sort(nums);
        for (int i = 0; i < nums.length - 2; i++) {
            int j = i + 1, k = nums.length - 1;
            while (j < k) {
                int sum = nums[i] + nums[j] + nums[k];
                if (sum == 0) {
                    List<Integer> list = Arrays.asList(nums[i], nums[j], nums[k]);
                    list.sort(Integer::compareTo);
                    result.add(list);
                    j++;
                } else if (sum < 0) {
                    j++;
                } else {
                    k--;
                }
            }
        }
        return new ArrayList<>(result);
    }

    // 11、盛最多水的容器
    public int maxArea(int[] height) {
        int result = -1, i = 0, j = height.length - 1;
        while (i < j) {
            result = Math.max(result, (j-i) * Math.min(height[j],height[i]));
            if (height[i] < height[j]) {
                i++;
            } else {
                j--;
            }
        }
        return result;
    }

    // 17. 电话号码的字母组合
    public List<String> letterCombinations(String digits) {
        Map<Character,List<String>> map = new HashMap<>();
        if (digits.isEmpty()) {
            return Collections.emptyList();
        }
        map.put('2', Arrays.asList("a","b","c"));map.put('3', Arrays.asList("d","e","f"));
        map.put('4', Arrays.asList("g","h","i"));map.put('5', Arrays.asList("j","k","l"));
        map.put('6', Arrays.asList("m","n","o"));map.put('7', Arrays.asList("p","q","r","s"));
        map.put('8', Arrays.asList("t","u","v"));map.put('9', Arrays.asList("w","x","y","z"));
        List<String> result = map.get(digits.charAt(0));
        BiFunction<List<String>,List<String>,List<String>> buildCombinations = (sList1,sList2) ->
                sList1.stream().flatMap(s1 -> sList2.stream().map(s2 -> s1+s2)).collect(Collectors.toList());
        for (int i = 1; i < digits.length(); i++) {
            result = buildCombinations.apply(result, map.get(digits.charAt(i)));
        }
        return result;
    }

    // 6. N 字形变换
    public String convert(String s, int numRows) {
        if (numRows == 1) {return s;}
        StringBuilder result = new StringBuilder();
        int i = -1, interval = (numRows - 1) * 2;
        while (++i < numRows && i < s.length()) {
            result.append(s.charAt(i));
            buildStr(s, interval - i * 2, i * 2, result, i);
        }
        return result.toString();
    }
    private void buildStr(String s, int interval1, int interval2, StringBuilder result, int index) {
        int[] loop = new int[]{interval1, interval2};
        for (int i = 0; ; i^=1) {
            int interval = loop[i];
            if (interval == 0) {continue;}
            if ((index+=interval) >= s.length()) {break;}
            result.append(s.charAt(index));
        }
    }

    // 19. 删除链表的倒数第 N 个结点
    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummyNode = new ListNode(-1, head), quickP = dummyNode, slowP = dummyNode, lastNode = null;
        while (--n >= 0) {
            quickP = quickP.next;
        }
        while (quickP != null) {
            quickP = quickP.next;
            lastNode = slowP;
            slowP = slowP.next;
        }
        lastNode.next = slowP.next;
        return dummyNode.next;
    }

    // 20. 有效的括号
    public boolean isValid(String s) {
        LinkedList<Character> stack = new LinkedList<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '{' || c == '[' || c == '(') {
                stack.push(c);
                continue;
            }
            if (c == '}' && !stack.isEmpty() && stack.peek() == '{') {
                stack.pop();
            } else if (c == ']' && !stack.isEmpty() && stack.peek() == '[') {
                stack.pop();
            } else if (c == ')' && !stack.isEmpty() && stack.peek() == '(') {
                stack.pop();
            } else {
                return false;
            }
        }
        return stack.isEmpty();
    }

    // 21. 合并两个有序链表，还有递归解法。。妙哉
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode dummyNode = new ListNode(-1, null), iter1 = list1, iter2 = list2, iter = dummyNode;
        while (iter1 != null && iter2 != null) {
            if (iter1.val < iter2.val) {
                iter.next = iter1;
                iter1 = iter1.next;
            } else {
                iter.next = iter2;
                iter2 = iter2.next;
            }
            iter = iter.next;
        }
        while (iter1 != null) {
            iter.next = iter1;
            iter1 = iter1.next;
            iter = iter.next;
        }
        while (iter2 != null) {
            iter.next = iter2;
            iter2 = iter2.next;
            iter = iter.next;
        }
        return dummyNode.next;
    }

    // 22. 括号生成
    public List<String> generateParenthesis(int n) {
        List<String> result = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        backtrack(0, 0, n, builder, result);
        return result;
    }

    private void backtrack(int leftBracketCnt, int rightBracketCnt, int n, StringBuilder builder, List<String> result) {
        if (leftBracketCnt > n || rightBracketCnt > n || rightBracketCnt > leftBracketCnt) {return;}
        if (rightBracketCnt == n) {
            result.add(builder.toString());
            return;
        }
        builder.append('(');
        backtrack(leftBracketCnt+1, rightBracketCnt, n, builder, result);
        builder.deleteCharAt(builder.length()-1);
        builder.append(')');
        backtrack(leftBracketCnt, rightBracketCnt+1, n, builder, result);
        builder.deleteCharAt(builder.length()-1);
    }


    // 39. 组合总和
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        Set<List<Integer>> res = new HashSet<>(150);
        Arrays.sort(candidates);
        backtrack(res, new LinkedList<>(), candidates, target, 0, 0);
        return new ArrayList<>(res);
    }

    private void backtrack(Set<List<Integer>> result, LinkedList<Integer> comb, int[] candidates, int tar, int sum, int startIndex) {
        if (tar == sum) {
            ArrayList<Integer> res = new ArrayList<>(comb);
            result.add(res);
            return;
        }
        for (int i = startIndex; i < candidates.length; i++) {
            if (sum + candidates[i] > tar) {break;}
            comb.add(candidates[i]);
            backtrack(result, comb, candidates, tar, sum + candidates[i], i);
            comb.removeLast();
        }
    }

    // 40. 组合总和 II
    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
        Arrays.sort(candidates);
        // 剪数组 todo
        Set<List<Integer>> result = new HashSet<>(candidates.length);
        int[] suffixSum = new int[candidates.length + 1];
        for (int i = candidates.length - 1; i >= 0; i--) {
            suffixSum[i] = candidates[i] + suffixSum[i+1];
        }
        backtrack2(result, new LinkedList<>(), candidates, target, 0, 0, suffixSum);
        return new ArrayList<>(result);
    }

    private void backtrack2(Set<List<Integer>> result, LinkedList<Integer> path, int[] candidates, int target, int sum, int startIndex, int[] suffixSum) {
        if (sum == target) {
            ArrayList<Integer> res = new ArrayList<>(path);
            res.sort(Integer::compareTo);
            result.add(res);
            return;
        }
        for (int i = startIndex; i < candidates.length; i++) {
            if (sum + candidates[i] > target) {break;}
            if (sum + suffixSum[i] < target) {break;}
            path.add(candidates[i]);
            backtrack2(result, path, candidates, target, sum + candidates[i], i + 1, suffixSum);
            path.removeLast();
        }
    }
}
