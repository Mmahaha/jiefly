package solution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static linkedlist.LinkedListSolution.ListNode;
import static solution.MySolution.TreeNode;

public class Hot100Solution {

    private static void swap(int[] array, int x, int y) {
        int buf = array[x];
        array[x] = array[y];
        array[y] = buf;
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
            result = Math.max(result, (j - i) * Math.min(height[j], height[i]));
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
        Map<Character, List<String>> map = new HashMap<>();
        if (digits.isEmpty()) {
            return Collections.emptyList();
        }
        map.put('2', Arrays.asList("a", "b", "c"));
        map.put('3', Arrays.asList("d", "e", "f"));
        map.put('4', Arrays.asList("g", "h", "i"));
        map.put('5', Arrays.asList("j", "k", "l"));
        map.put('6', Arrays.asList("m", "n", "o"));
        map.put('7', Arrays.asList("p", "q", "r", "s"));
        map.put('8', Arrays.asList("t", "u", "v"));
        map.put('9', Arrays.asList("w", "x", "y", "z"));
        List<String> result = map.get(digits.charAt(0));
        BiFunction<List<String>, List<String>, List<String>> buildCombinations = (sList1, sList2) ->
                sList1.stream().flatMap(s1 -> sList2.stream().map(s2 -> s1 + s2)).collect(Collectors.toList());
        for (int i = 1; i < digits.length(); i++) {
            result = buildCombinations.apply(result, map.get(digits.charAt(i)));
        }
        return result;
    }

    // 6. N 字形变换
    public String convert(String s, int numRows) {
        if (numRows == 1) {
            return s;
        }
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
        for (int i = 0; ; i ^= 1) {
            int interval = loop[i];
            if (interval == 0) {
                continue;
            }
            if ((index += interval) >= s.length()) {
                break;
            }
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
        if (leftBracketCnt > n || rightBracketCnt > n || rightBracketCnt > leftBracketCnt) {
            return;
        }
        if (rightBracketCnt == n) {
            result.add(builder.toString());
            return;
        }
        builder.append('(');
        backtrack(leftBracketCnt + 1, rightBracketCnt, n, builder, result);
        builder.deleteCharAt(builder.length() - 1);
        builder.append(')');
        backtrack(leftBracketCnt, rightBracketCnt + 1, n, builder, result);
        builder.deleteCharAt(builder.length() - 1);
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
            if (sum + candidates[i] > tar) {
                break;
            }
            comb.add(candidates[i]);
            backtrack(result, comb, candidates, tar, sum + candidates[i], i);
            comb.removeLast();
        }
    }

    // 70. 爬楼梯
    public int climbStairs(int n) {
        if (n == 1) {
            return 1;
        }
        if (n == 2) {
            return 2;
        }
        int[] array = new int[n + 1];
        array[1] = 1;
        array[2] = 2;
        for (int i = 3; i <= n; i++) {
            array[i] = array[i - 1] + array[i - 2];
        }
        return array[n];
    }

    // 78. 子集
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>((int) Math.pow(2, nums.length));
        backtrack(new LinkedList<>(), result, nums, 0);
        return result;
    }

    private void backtrack(LinkedList<Integer> path, List<List<Integer>> res, int[] nums, int startIndex) {
        res.add(new ArrayList<>(path));
        for (int i = startIndex; i < nums.length; i++) {
            path.add(nums[i]);
            backtrack(path, res, nums, i + 1);
            path.removeLast();
        }
    }

    // 31. 下一个排列
    public void nextPermutation(int[] nums) {
        int n = nums.length;
        int firstDescendIndex = -1;
        // 从右往左找第一个降序的数
        for (int i = n - 1; i > 0; i--) {
            if (nums[i] > nums[i - 1]) {
                firstDescendIndex = i - 1;
                break;
            }
        }
        // 降序数组，直接排序返回
        if (firstDescendIndex == -1) {
            Arrays.sort(nums);
            return;
        }
        // 找出比firstDescend大最小的数
        int largerIndex = firstDescendIndex + 1;
        int subtract = nums[largerIndex] - nums[firstDescendIndex], subtractBuf;
        for (int i = firstDescendIndex + 2; i < n; i++) {
            // 已经比第一个降序的数小的话，可以剪枝
            if (nums[i] <= nums[firstDescendIndex]) {break;}
            if ((subtractBuf = (nums[i] - nums[firstDescendIndex])) < subtract) {
                subtract = subtractBuf;
                largerIndex = i;
            }
        }
        int temp = nums[firstDescendIndex];
        nums[firstDescendIndex] = nums[largerIndex];
        nums[largerIndex] = temp;
        Arrays.sort(nums, firstDescendIndex + 1, n);    // 可以用双指针进行反转排序（原数组已为降序）
    }

    // 40. 组合总和 II
    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
        Arrays.sort(candidates);
        // 剪数组
        LinkedList<Integer> processList = IntStream.of(candidates).boxed().collect(Collectors.toCollection(LinkedList::new));
        Iterator<Integer> iterator = processList.iterator();
        int sum = 0, lastVal = -1;
        while (iterator.hasNext()) {
            Integer val = iterator.next();
            if (val != lastVal) {
                sum = val;
            } else if ((sum += val) > 30) {
                iterator.remove();
            }
            lastVal = val;
        }
        candidates = processList.stream().mapToInt(Integer::intValue).toArray();
        Set<List<Integer>> result = new HashSet<>(candidates.length);
        int[] suffixSum = new int[candidates.length + 1];
        for (int i = candidates.length - 1; i >= 0; i--) {
            suffixSum[i] = candidates[i] + suffixSum[i + 1];
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
            if (sum + candidates[i] > target) {
                break;
            }
            if (sum + suffixSum[i] < target) {
                break;
            }
            path.add(candidates[i]);
            backtrack2(result, path, candidates, target, sum + candidates[i], i + 1, suffixSum);
            path.removeLast();
        }
    }

    // 23. 合并 K 个升序链表
    public ListNode mergeKLists(ListNode[] lists) {
        return mergeKLists(lists, 0, lists.length - 1);
    }

    public ListNode mergeKLists(ListNode[] listNodes, int l, int r) {
        if (l == r) {
            return listNodes[l];
        }
        if (l > r) {
            return null;
        }
        int m = (l + r) >> 1;
        ListNode leftNode = mergeKLists(listNodes, l, m);
        ListNode rightNode = mergeKLists(listNodes, m + 1, r);
        return merge(leftNode, rightNode);
    }

    public ListNode merge(ListNode left, ListNode right) {
        ListNode dummy = new ListNode(-1), iterL = left, iterR = right, iterH = dummy;
        while (iterL != null && iterR != null) {
            if (iterL.val < iterR.val) {
                iterH.next = iterL;
                iterL = iterL.next;
            } else {
                iterH.next = iterR;
                iterR = iterR.next;
            }
            iterH = iterH.next;
        }
        if (iterL != null) {
            iterH.next = iterL;
        }
        if (iterR != null) {
            iterH.next = iterR;
        }
        return dummy.next;
    }

    // 33.搜索旋转排序数组
    public int search(int[] nums, int target) {
        return binarySearch(nums, target, 0, nums.length - 1, target >= nums[0]);
    }

    public int binarySearch(int[] nums, int target, int l, int r, boolean inLeft) {
        if (l > r) {
            return -1;
        }
        int m = (l + r) >> 1;
        if (nums[m] == target) {
            return m;
        }
        if (nums[m] >= nums[0] && !inLeft) {
            return binarySearch(nums, target, m + 1, r, inLeft);
        }
        if (nums[m] <= nums[nums.length - 1] && inLeft && nums[0] > nums[nums.length - 1]) {    // 需要排除原本就是递增的情况
            return binarySearch(nums, target, l, m - 1, inLeft);
        }
        if (nums[m] > target) {
            return binarySearch(nums, target, l, m - 1, inLeft);
        }
        return binarySearch(nums, target, m + 1, r, inLeft);
    }

    // 49. 字母异位词分组
    public List<List<String>> groupAnagrams(String[] strs) {
        return new ArrayList<>(Arrays.stream(strs)
                .collect(Collectors.groupingBy(s -> {
                    char[] charArray = s.toCharArray();
                    Arrays.sort(charArray);
                    return new String(charArray);
                })).values());
    }

    // 53.最大子数组和
    public int maxSubArray(int[] nums) {
        int[] dp = new int[nums.length];
        dp[0] = nums[0];
        int res = dp[0];
        for (int i = 1; i < nums.length; i++) {
            dp[i] = Math.max(dp[i - 1] + nums[i], nums[i]);
            res = Math.max(res, dp[i]);
        }
        return res;
    }

    // 55.跳跃游戏
    public boolean canJump(int[] nums) {
        int i = 0, maxLength = 0;
        while (i <= maxLength && i < nums.length) {
            maxLength = Math.max(maxLength, i + nums[i]);
            if (maxLength >= nums.length - 1) {
                return true;
            }
            i++;
        }
        return false;
    }

    // 56.合并区间
    public int[][] merge(int[][] intervals) {
        Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));
        LinkedList<int[]> result = new LinkedList<>();
        result.add(intervals[0]);
        for (int i = 1; i < intervals.length; i++) {
            int[] last = result.getLast();
            int[] cur = intervals[i];
            if (cur[0] <= last[1]) {
                last[1] = Math.max(last[1], cur[1]);
            } else {
                result.add(cur);
            }
        }
        return result.toArray(new int[0][0]);
    }

    // 46.全排列
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(nums, new LinkedList<>(), result);
        return result;
    }

    private void backtrack(int[] nums, LinkedList<Integer> path, List<List<Integer>> result) {
        if (path.size() == nums.length) {
            result.add(new ArrayList<>(path));
            return;
        }
        for (int num : nums) {
            if (path.contains(num)) {
                continue;
            }
            path.addLast(num);
            backtrack(nums, path, result);
            path.removeLast();
        }
    }

    // 75. 颜色分类
    public void sortColors(int[] nums) {
        int l = 0, r = nums.length - 1, i = 0;
        while (i <= r) {
            if (nums[i] == 2) {
                swap(nums, i, r);
                r--;
            } else if (nums[i] == 1) {
                i++;
            } else {
                swap(nums, i, l);
                i++;
                l++;
            }
        }
    }

    // 448. 找到所有数组中消失的数字
    public List<Integer> findDisappearedNumbers(int[] nums) {
        for (int num : nums) {
            int ori = Math.abs(num);
            if (nums[ori - 1] > 0) {
                nums[ori - 1] = -nums[ori - 1];
            }
        }
        List<Integer> result = new ArrayList<>(10);
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] > 0) {
                result.add(i + 1);
            }
        }
        return result;
    }

    // 617. 合并二叉树
    public TreeNode mergeTrees(TreeNode root1, TreeNode root2) {
        if (root1 == null) {
            return root2;
        }
        if (root2 == null) {
            return root1;
        }
        TreeNode root = new TreeNode(root1.val + root2.val);
        root.left = mergeTrees(root1.left, root2.left);
        root.right = mergeTrees(root1.right, root2.right);
        return root;
    }

    // 543.二叉树的直径
    public int diameterOfBinaryTree(TreeNode root) {
        AtomicInteger result = new AtomicInteger();
        dfs(root, result);
        return result.get();
    }

    public int dfs(TreeNode root, AtomicInteger maxDepth) {
        if (root == null) {
            return 0;
        }
        int leftDepth = dfs(root.left, maxDepth);
        int rightDepth = dfs(root.right, maxDepth);
        if (maxDepth.get() < leftDepth + rightDepth) {
            maxDepth.set(leftDepth + rightDepth);
        }
        return 1 + Math.max(leftDepth, rightDepth);
    }

    // 461.汉明距离
    public int hammingDistance(int x, int y) {
        return Integer.bitCount(x ^ y);
    }

    // 338.比特位计数
    public int[] countBits(int n) {
        int[] result = new int[n + 1];
        for (int i = 0; i <= n; i++) {
            result[i] = Integer.bitCount(i);
        }
        return result;
    }

    // 72.编辑距离
    public int minDistance(String word1, String word2) {
        int l1 = word1.length();
        int l2 = word2.length();
        int[][] dp = new int[l1 + 1][l2 + 1];
        for (int i = 0; i < l1 + 1; i++) {
            dp[i][0] = i;
        }
        for (int i = 0; i < l2 + 1; i++) {
            dp[0][i] = i;
        }
        for (int i = 1; i <= l1; i++) {
            for (int j = 1; j <= l2; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]) + 1;
                }
            }
        }
        return dp[l1][l2];
    }

    // 48. 旋转图像
    public void rotate(int[][] matrix) {
        int head = -1, n = matrix.length;
        while (++head < n / 2) {
            for (int i = 0; i < n - head * 2 - 1; i++) {
                int temp = matrix[head][head+i];
                matrix[head][head+i] = matrix[n-head-1-i][head];
                matrix[n-head-1-i][head] = matrix[n-head-1][n-head-1-i];
                matrix[n-head-1][n-head-1-i] = matrix[head+i][n-head-1];
                matrix[head+i][n-head-1] = temp;
            }
        }
    }

    // 79. 单词搜索
    public boolean exist(char[][] board, String word) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                StringBuilder path = new StringBuilder();
                boolean[][] visited = new boolean[board.length][board[0].length];
                if (backtrack(path, word, i, j, board, visited)) {return true;}
            }
        }
        return false;
    }

    private boolean backtrack(StringBuilder path, String word, int row, int col, char[][] board, boolean[][] visited) {
        if (row >= board.length || col >= board[0].length || row < 0 || col < 0 || visited[row][col]) {return false;}
        char curChar = board[row][col];
        if (word.charAt(path.length()) == curChar) {
            path.append(curChar);
            visited[row][col] = true;
            if (path.length() == word.length()) {
                return true;
            }
        } else {
            return false;
        }
        if (backtrack(path, word, row + 1, col, board, visited)) {return true;}
        if (backtrack(path, word, row - 1, col, board, visited)) {return true;}
        if (backtrack(path, word, row, col + 1, board, visited)) {return true;}
        if (backtrack(path, word, row, col - 1, board, visited)) {return true;}
        path.deleteCharAt(path.length() - 1);
        visited[row][col] = false;
        return false;
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


}
