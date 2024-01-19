package solution;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
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
            if (nums[i] <= nums[firstDescendIndex]) {
                break;
            }
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
    public int[][] mergeListNode(int[][] intervals) {
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
                int temp = matrix[head][head + i];
                matrix[head][head + i] = matrix[n - head - 1 - i][head];
                matrix[n - head - 1 - i][head] = matrix[n - head - 1][n - head - 1 - i];
                matrix[n - head - 1][n - head - 1 - i] = matrix[head + i][n - head - 1];
                matrix[head + i][n - head - 1] = temp;
            }
        }
    }

    // 79. 单词搜索
    public boolean exist(char[][] board, String word) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                StringBuilder path = new StringBuilder();
                boolean[][] visited = new boolean[board.length][board[0].length];
                if (backtrack(path, word, i, j, board, visited)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean backtrack(StringBuilder path, String word, int row, int col, char[][] board, boolean[][] visited) {
        if (row >= board.length || col >= board[0].length || row < 0 || col < 0 || visited[row][col]) {
            return false;
        }
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
        if (backtrack(path, word, row + 1, col, board, visited)) {
            return true;
        }
        if (backtrack(path, word, row - 1, col, board, visited)) {
            return true;
        }
        if (backtrack(path, word, row, col + 1, board, visited)) {
            return true;
        }
        if (backtrack(path, word, row, col - 1, board, visited)) {
            return true;
        }
        path.deleteCharAt(path.length() - 1);
        visited[row][col] = false;
        return false;
    }

    // 94. 二叉树的中序遍历
    public List<Integer> inorderTraversal(TreeNode root) {
        LinkedList<TreeNode> stack = new LinkedList<>();
        List<Integer> res = new ArrayList<>(100);
        while (root != null || !stack.isEmpty()) {
            while (root != null) {
                stack.push(root);
                root = root.left;
            }
            res.add((root = stack.pop()).val);
            root = root.right;
        }
        return res;
    }

    // 96. 不同的二叉搜索树
//    public int numTrees(int n) {
//
//    }


    // 128. 最长连续序列
    public int longestConsecutive(int[] nums) {
        Set<Integer> set = Arrays.stream(nums).boxed().collect(Collectors.toSet());
        int res = 0;
        for (int num : nums) {
            if (set.contains(num - 1)) {
                continue;
            }
            int resBuf = 1;
            while (set.remove(++num)) {
                resBuf++;
            }
            res = Math.max(res, resBuf);
        }
        return res;
    }

    // 283. 移动零
    public void moveZeroes(int[] nums) {
        int head = 0, cur = -1;
        while (++cur < nums.length) {
            if (nums[cur] == 0) {
                continue;
            }
            int temp = nums[head];
            nums[head++] = nums[cur];
            nums[cur] = temp;
        }
    }



    // 560. 和为 K 的子数组
    public int subarraySum(int[] nums, int k) {
        int[] sum = new int[nums.length];
        Map<Integer, Integer> sumCountMap = new HashMap<>();
        int res = 0;
        sum[0] = nums[0];
        sumCountMap.put(sum[0], 1);
        res += sum[0] == k ? 1 : 0;
        for (int i = 1; i < nums.length; i++) {
            sum[i] = sum[i - 1] + nums[i];
            if (sum[i] == k) {
                res += 1;

            }
            res += sumCountMap.getOrDefault(sum[i] - k, 0);
            sumCountMap.compute(sum[i], (s,c) -> c == null ? 1 : c + 1);
        }
        return res;
    }

    // 438. 找到字符串中所有字母异位词
    public List<Integer> findAnagrams(String s, String p) {
        int[] targetCount = new int[26];
        int targetLength = p.length(), curLength = 0;
        for (char c : p.toCharArray()) {
            targetCount[c - 'a']++;
        }
        int p1 = 0, p2 = 0;
        List<Integer> res = new ArrayList<>();
        while (p1 <= s.length() - targetLength && p2 < s.length()) {
            char c = s.charAt(p2);
            if (targetCount[c - 'a'] > 0) {
                targetCount[c - 'a']--;
                p2++;
                curLength++;
                if (curLength == targetLength) {
                    res.add(p1);
                    targetCount[s.charAt(p1) - 'a']++;
                    curLength--;
                    p1++;
                }
            } else {
                while (s.charAt(p1) != c) {
                    targetCount[s.charAt(p1) - 'a']++;
                    curLength--;
                    p1++;
                }
                p1++;
                p2++;
            }
        }
        return res;
    }

    // 189. 轮转数组    todo need review
    public void rotate(int[] nums, int k) {
        k %= nums.length;
        if (k == 0) {return;}
        reverse(nums, 0, nums.length - 1);
        reverse(nums, 0, k - 1);
        reverse(nums, k, nums.length - 1);
    }

    private void reverse(int[] nums, int start, int end) {
        while (start < end) {
            int temp = nums[start];
            nums[start] = nums[end];
            nums[end] = temp;
            start++;
            end--;
        }
    }

    public void rotate2(int[] nums, int k) {
        int n = nums.length;
        k %= n;
        int cnt = 0, start = 0, temp, cur, next, prev;
        while (cnt < n) {
            cur = start;
            prev = nums[cur];
            do {
                next = (cur + k) % n;
                temp = nums[next];
                nums[next] = prev;
                prev = temp;
                cur = next;
                cnt++;
            } while (cur != start);
            start++;
        }
    }

    // 238. 除自身以外数组的乘积
    public int[] productExceptSelf(int[] nums) {
        int[] left = new int[nums.length];
        left[0] = 1;
        for (int i = 1; i < nums.length; i++) {
            left[i] = left[i - 1] * nums[i - 1];
        }
        int[] right = new int[nums.length];
        right[nums.length - 1] = 1;
        for (int i = nums.length - 2; i >= 0; i--) {
            right[i] = right[i + 1] * nums[i + 1];
        }
        int[] res = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            res[i] = left[i] * right[i];
        }
        return res;
    }

    // 73.矩阵置零
    public void setZeroes(int[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;
        boolean firstColumnZero = false, firstRowZero = false;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] == 0) {
                    matrix[i][0] = 0;
                    matrix[0][j] = 0;
                    firstColumnZero |= (j == 0);
                    firstRowZero |= (i == 0);
                }
            }
        }

        for (int i = 1; i < m; i++) {
            if (matrix[i][0] == 0) {
                for (int j = 1; j < n; j++) {
                    matrix[i][j] = 0;
                }
            }
        }

        for (int i = 1; i < n; i++) {
            if (matrix[0][i] == 0) {
                for (int j = 1; j < m; j++) {
                    matrix[j][i] = 0;
                }
            }
        }
        if (firstColumnZero) {
            for (int i = 0; i < m; i++) {
                matrix[i][0] = 0;
            }
        }
        if (firstRowZero) {
            Arrays.fill(matrix[0], 0);
        }
    }

    // 54. 螺旋矩阵
    public List<Integer> spiralOrder(int[][] matrix) {
        int curLength = matrix[0].length, curWidth = matrix.length, resCnt = curLength * curWidth;
        List<Integer> res = new ArrayList<>(resCnt);
        int row = 0, col = -1;
        while (true) {
            for (int i = 0; i < curLength; i++) {
                res.add(matrix[row][++col]);
            }
            if (res.size() == resCnt) {break;}
            for (int i = 0; i < curWidth - 1; i++) {
                res.add(matrix[++row][col]);
            }
            if (res.size() == resCnt) {break;}
            for (int i = 0; i < curLength - 1; i++) {
                res.add(matrix[row][--col]);
            }
            if (res.size() == resCnt) {break;}
            for (int i = 0; i < curWidth - 2; i++) {
                res.add(matrix[--row][col]);
            }
            if (res.size() == resCnt) {break;}
            curLength -= 2;
            curWidth -= 2;
        }
        return res;
    }

    // 240. 搜索二维矩阵 II
    public boolean searchMatrix(int[][] matrix, int target) {
        int row = 0, col = matrix[0].length - 1;
        while (row < matrix.length && col >= 0) {
            if (matrix[row][col] == target) {
                return true;
            }
            if (matrix[row][col] > target) {
                col --;
            } else {
                row ++;
            }
        }
        return false;
    }

    private boolean _searchMatrix(int[][] matrix, int target, int x, int y) {
        if (x >= matrix.length || y < 0) {
            return false;
        }
        if (matrix[x][y] == target) {
            return true;
        }
        if (matrix[x][y] > target) {
            return _searchMatrix(matrix, target, x, y - 1);
        }
        return _searchMatrix(matrix, target, x + 1, y);
    }

    // 160. 相交链表
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        ListNode iterA = headA, iterB = headB;
        while (iterA != iterB) {
            iterA = iterA == null ? headB : iterA.next;
            iterB = iterB == null ? headA : iterB.next;
        }
        return iterA;
    }

    // 234. 回文链表
    public boolean isPalindrome(ListNode head) {
        ListNode fast = head, slow = head;
        while (fast != null && fast.next != null) {
            fast = fast.next.next;
            slow = slow.next;
        }
        ListNode secHead;
        if (fast == null) {
            secHead = reverseLinkedList(slow);
        } else {
            secHead = reverseLinkedList(slow.next);
        }
        ListNode iter1 = head, iter2 = secHead;
        while (iter1 != null && iter2 != null) {
            if (iter1.val != iter2.val) {
                return false;
            }
            iter1 = iter1.next;
            iter2 = iter2.next;
        }
        return true;
    }

    private ListNode reverseLinkedList(ListNode head) {
        ListNode prev = null, cur = head, next;
        while (cur != null) {
            next = cur.next;
            cur.next = prev;
            prev = cur;
            cur = next;
        }
        return prev;
    }

    // 141. 环形链表
    public boolean hasCycle(ListNode head) {
        ListNode fast = head, slow = head;
        while (fast != null && fast.next != null) {
            fast = fast.next.next;
            slow = slow.next;
            if (fast == slow) {
                return true;
            }
        }
        return false;
    }

    // 142. 环形链表 II
    public ListNode detectCycle(ListNode head) {
        ListNode fast = head, slow = head, meet = null;
        while (fast != null && fast.next != null) {
            fast = fast.next.next;
            slow = slow.next;
            if (fast == slow) {
                meet = fast;
                break;
            }
        }
        while (meet != null && head != null) {
            if (meet == head) {
                return meet;
            }
            meet = meet.next;
            head = head.next;
        }
        return null;
    }

    // 148. 排序链表
    public ListNode sortList(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        ListNode midNode = findMidNode(head);
        ListNode right = midNode.next;
        midNode.next = null;
        ListNode leftPartSorted = sortList(head);
        ListNode rightPartSorted = sortList(right);
        return mergeListNode(leftPartSorted, rightPartSorted);
    }

    private ListNode mergeListNode(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(-1);
        ListNode iter = dummy;
        while (l1 != null && l2 != null) {
            if (l1.val < l2.val) {
                iter.next = l1;
                l1 = l1.next;
            } else {
                iter.next = l2;
                l2 = l2.next;
            }
            iter = iter.next;
        }
        if (l1 != null) {
            iter.next = l1;
        }
        if (l2 != null) {
            iter.next = l2;
        }
        return dummy.next;
    }

    private ListNode findMidNode(ListNode head) {
        ListNode fast = head, slow = head;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

    // 148. 排序链表    迭代法
    public ListNode sortList2(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        int length = 0;
        ListNode iter = head, dummy = new ListNode(-1);
        dummy.next = head;
        while (iter != null) {
            length++;
            iter = iter.next;
        }
        for (int step = 1; step < length; step <<= 1) {
            ListNode cur = dummy.next;
            ListNode dummyIter = dummy;
            while (cur != null) {
                ListNode left = cur;
                ListNode right = split(left, step);
                cur = split(right, step);
                dummyIter.next = mergeListNode(left, right);
                while (dummyIter.next != null) {
                    dummyIter = dummyIter.next;
                }
            }
        }
        return dummy.next;
    }

    // 切割链表head
    private ListNode split(ListNode head, int maxCnt) {
        if (head == null) {return null;}
        while (head.next != null && --maxCnt > 0) {
            head = head.next;
        }
        ListNode res = head.next;
        head.next = null;
        return res;
    }

    // 104. 二叉树的最大深度
    public int maxDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int leftDepth = maxDepth(root.left);
        int rightDepth = maxDepth(root.right);
        return 1 + Math.max(leftDepth, rightDepth);
    }


    // 104. 二叉树的最大深度，循环版本
    public int maxDepth2(TreeNode root) {
        Queue<TreeNode> queue = new LinkedList<>();
        int res = 0;
        if (root != null) {
            queue.offer(root);
        }
        while (!queue.isEmpty()) {
            int curLevelNodeCnt = queue.size();
            while (--curLevelNodeCnt >= 0) {
                TreeNode node = queue.poll();
                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }
            res++;
        }
        return res;
    }

    // 226. 翻转二叉树
    public TreeNode invertTree(TreeNode root) {
        if (root == null) {
            return null;
        }
        root.left = invertTree(root.left);
        root.right = invertTree(root.right);
        TreeNode buf = root.left;
        root.left = root.right;
        root.right = buf;
        return root;
    }

    // 101. 对称二叉树
    public boolean isSymmetric(TreeNode root) {
        if (root == null) {
            return false;
        }
        return _isSymmetric(root.left, root.right);
    }

    public boolean _isSymmetric(TreeNode left, TreeNode right) {
        if (left == null && right == null) {
            return true;
        }
        if (left != null && right != null) {
            return left.val == right.val &&
                    _isSymmetric(left.left, right.right) && _isSymmetric(left.right, right.left);
        }
        return false;
    }

    // 102. 二叉树的层序遍历
    public List<List<Integer>> levelOrder(TreeNode root) {
        Queue<TreeNode> queue = new LinkedList<>();
        List<List<Integer>> res = new ArrayList<>(10);
        if (root != null) {
            queue.offer(root);
        }
        while (!queue.isEmpty()) {
            int curLevelCnt = queue.size();
            List<Integer> subList = new ArrayList<>(curLevelCnt);
            while (--curLevelCnt >= 0) {
                TreeNode pollNode = queue.poll();
                subList.add(pollNode.val);
                if (pollNode.left != null) {
                    queue.offer(pollNode.left);
                }
                if (pollNode.right != null) {
                    queue.offer(pollNode.right);
                }
            }
            res.add(subList);
        }
        return res;
    }

    // 108. 将有序数组转换为二叉搜索树
    public TreeNode sortedArrayToBST(int[] nums) {
        return _sortedArrayToBST(nums, 0, nums.length - 1);
    }

    public TreeNode _sortedArrayToBST(int[] nums, int start, int end) {
        if (start > end) {
            return null;
        }
        int mid = (start + end) >> 1;
        TreeNode curNode = new TreeNode(nums[mid]);
        curNode.left = _sortedArrayToBST(nums, start, mid - 1);
        curNode.right = _sortedArrayToBST(nums, mid + 1, end);
        return curNode;
    }

    // 98. 验证二叉搜索树
    public boolean isValidBST(TreeNode root) {
        return _isValidBST(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    private boolean _isValidBST(TreeNode root, long min, long max) {
        if (root == null) {
            return true;
        }
        return _isValidBST(root.left, min, root.val) && _isValidBST(root.right, root.val, max)
                && root.val > min && root.val < max;
    }

    // 230. 二叉搜索树中第K小的元素
    private int cur = 0;
    public int kthSmallest(TreeNode root, int k) {
        if (root == null) {
            return -1;
        }
        int resBuf;
        if ((resBuf = kthSmallest(root.left, k)) >= 0) {return resBuf;}
        if (++cur == k) {return root.val;}
        return kthSmallest(root.right, k);
    }

    // 199. 二叉树的右视图
    public List<Integer> rightSideView(TreeNode root) {
        List<Integer> res = new ArrayList<>(10);
        Queue<TreeNode> queue = new LinkedList<>();
        if (root != null) {
            queue.offer(root);
        }
        while (!queue.isEmpty()) {
            int size = queue.size();
            TreeNode node = null;
            while (--size >= 0) {
                node = queue.poll();
                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }
            res.add(node.val);
        }
        return res;
    }

    // 114. 二叉树展开为链表
    public void flatten(TreeNode root) {
        LinkedList<TreeNode> stack = new LinkedList<>();
        List<TreeNode> list = new ArrayList<>();
        if (root != null) {
            stack.push(root);
        }
        TreeNode prev = null;
        while (!stack.isEmpty()) {
            TreeNode node = stack.poll();
            if (prev != null) {
                prev.left = null;
                prev.right = node;
            }
            list.add(node);
            if (node.right != null) {
                stack.push(node.right);
            }
            if (node.left != null) {
                stack.push(node.left);
            }
            prev = node;
        }
    }

    // 105. 从前序与中序遍历序列构造二叉树
    private Map<Integer, Integer> valIndexMap = new HashMap<>(2048);
    public TreeNode buildTree(int[] preorder, int[] inorder) {
        for (int i = 0; i < inorder.length; i++) {
           valIndexMap.put(inorder[i], i);
        }
        return _buildTree(preorder, inorder, 0, preorder.length - 1, 0, inorder.length - 1);
    }

    private TreeNode _buildTree(int[] preorder, int[] inorder, int preStartIndex, int preEndIndex,
                               int inStartIndex, int inEndIndex) {
        if (preStartIndex > preEndIndex || inStartIndex > inEndIndex) {
            return null;
        }
        int curVal = preorder[preStartIndex];
        TreeNode cur = new TreeNode(curVal);
        int rootIndex = valIndexMap.get(curVal);
        int leftSize = rootIndex - inStartIndex;
        cur.left = _buildTree(preorder, inorder, preStartIndex + 1, preEndIndex + leftSize,
                inStartIndex, inStartIndex + leftSize - 1);
        cur.right = _buildTree(preorder, inorder, preStartIndex + 1 + leftSize, preEndIndex,
                rootIndex + 1, inEndIndex);
        return cur;
    }

    // 437. 路径总和 III
    private int pathSumRes = 0;
    public int pathSum(TreeNode root, int targetSum) {
        dfs1(root, targetSum);
        return pathSumRes;
    }

    public void dfs1(TreeNode root, int targetSum) {
        if (root == null) {
            return;
        }
        dfs2(root, 0, targetSum);
        dfs1(root.left, targetSum);
        dfs1(root.right, targetSum);
    }

    public void dfs2(TreeNode root, long sum, int targetSum) {
        if (root == null) {
            return;
        }
        sum += root.val;
        if (sum == targetSum) {
            pathSumRes ++;
        }
        dfs2(root.left, sum, targetSum);
        dfs2(root.right, sum, targetSum);
    }

    private final Map<Long, Integer> sumCountMap = new HashMap<>(16);
    private int pathSum2Res = 0;
    private long targetSum = -1;
    public int pathSum2(TreeNode root, int _targetSum) {
        if (root == null) {return 0;}
        targetSum = _targetSum;
        sumCountMap.put(0L, 1);
        _pathSum2(root, root.val);
        return pathSum2Res;
    }

    public void _pathSum2(TreeNode root, long curSum) {
        pathSum2Res += sumCountMap.getOrDefault((curSum - targetSum), 0);
        sumCountMap.compute(curSum, (sum, cnt) -> cnt == null ? 1 : cnt + 1);
        if (root.left != null) {
            _pathSum2(root.left, curSum + root.left.val);
        }
        if (root.right != null) {
            _pathSum2(root.right, curSum + root.right.val);
        }
        sumCountMap.compute(curSum, (sum, cnt) -> cnt - 1);
    }


    // 236. 二叉树的最近公共祖先
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        LinkedList<TreeNode> pPath = new LinkedList<>();
        LinkedList<TreeNode> qPath = new LinkedList<>();
        _findPath(root, p, q, pPath, qPath, new AtomicBoolean(false),new AtomicBoolean(false));
        ListIterator<TreeNode> pPathIter = pPath.listIterator(pPath.size());
        ListIterator<TreeNode> qPathIter = qPath.listIterator(qPath.size());
        TreeNode res = null;
        while (pPathIter.hasPrevious() && qPathIter.hasPrevious()) {
            TreeNode pNext = pPathIter.previous();
            TreeNode qNext = qPathIter.previous();
            if (pNext == qNext) {
                res = pNext;
            } else {
                break;
            }
        }
        return res;
    }

    private void _findPath(TreeNode cur, TreeNode p, TreeNode q, LinkedList<TreeNode> pPath,
                           LinkedList<TreeNode> qPath, AtomicBoolean pFound, AtomicBoolean qFound) {
        if (cur == null) {
            return;
        }
        if (!pFound.get()) {
            pPath.push(cur);
            if (cur == p) {
                pFound.set(true);
            }
        }
        if (!qFound.get()) {
            qPath.push(cur);
            if (cur == q) {
                qFound.set(true);
            }
        }
        if (pFound.get() && qFound.get()) {
            return;
        }
        _findPath(cur.left, p, q, pPath, qPath, pFound, qFound);
        _findPath(cur.right, p, q, pPath, qPath, pFound, qFound);
        if (!pFound.get()) {
            pPath.pop();
        }
        if (!qFound.get()) {
            qPath.pop();
        }
    }

    // 118. 杨辉三角
    public List<List<Integer>> generate(int numRows) {
        List<List<Integer>> res = new ArrayList<>();
        res.add(Collections.singletonList(1));
        for (int i = 1; i < numRows; i++) {
            List<Integer> lastRow = res.get(i - 1);
            List<Integer> curRow = new ArrayList<>(i + 1);
            for (int j = 0; j < i + 1; j++) {
                if (j == 0 || j == i) {
                    curRow.add(1);
                } else {
                    curRow.add(lastRow.get(j - 1) + lastRow.get(j));
                }
            }
            res.add(curRow);
        }
        return res;
    }

    // 136. 只出现一次的数字
    public int singleNumber(int[] nums) {
        int res = 0;
        for (int num : nums) {
            res ^= num;
        }
        return res;
    }

    // 169. 多数元素
    public int majorityElement(int[] nums) {
        int res = 0, cnt = 0;
        for (int num : nums) {
            if (cnt == 0) {
                res = num;
                cnt ++;
            } else if (num == res) {
                cnt ++;
            } else {
                cnt --;
            }
        }
        return res;
    }

    // 74. 搜索二维矩阵
    public boolean searchMatrix1(int[][] matrix, int target) {
        int m = matrix.length;
        int n = matrix[0].length;
        int left = 0, right = m * n - 1;
        while (left <= right) {
            int mid = (left + right) >> 1;
            int x = mid / n, y = mid % n;
            if (matrix[x][y] == target) {
                return true;
            }
            if (matrix[x][y] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return false;
    }

    // 121. 买卖股票的最佳时机
    public int maxProfit(int[] prices) {
        int min = prices[0], res = 0;
        for (int i = 1; i < prices.length; i++) {
            res = Math.max(res, prices[i] - min);
            min = Math.min(min, prices[i]);
        }
        return res;
    }

    // 45. 跳跃游戏 II
    public int jump(int[] nums) {
        int end = 0, maxPosition = 0, res = 0;
        for (int i = 0; i < nums.length - 1; i++) {
            maxPosition = Math.max(maxPosition, i + nums[i]);
            if (i == end) {
                res++;
                end = maxPosition;
            }
        }
        return res;
    }

    // 198.打家劫舍
    public int rob(int[] nums) {
        if (nums.length == 1) {
            return nums[0];
        }
        int[] dp = new int[nums.length];
        dp[0] = nums[0];
        dp[1] = Math.max(nums[0], nums[1]);
        for (int i = 2; i < nums.length; i++) {
            dp[i] = Math.max(dp[i-2] + nums[i], dp[i-1]);
        }
        return dp[nums.length - 1];
    }

    // 279. 完全平方数
    public int numSquares(int n) {
        int[] dp = new int[n+1];
        for (int i = 0; i <= n; i++) {
            dp[i] = i;
            for (int j = 1; j * j <= i; j++) {
                dp[i] = Math.min(dp[i], dp[i - j * j] + 1);
            }
        }
        return dp[n];
    }

    // 322. 零钱兑换
    public int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        for (int i = 1; i <= amount; i++) {
            dp[i] = -1;
            for (int coin : coins) {
                if (coin > i) {
                    continue;
                }
                if (dp[i-coin] != -1) {
                    dp[i] = dp[i] == -1 ? dp[i-coin] + 1 : Math.min(dp[i], dp[i-coin] + 1);
                }
            }
        }
        return dp[amount];
    }

    // 139. 单词拆分
    public boolean wordBreak(String s, List<String> wordDict) {
        HashSet<String> wordDictSet = new HashSet<>(wordDict);
        boolean[] dp = new boolean[s.length() + 1];
        dp[0] = true;
        for (int i = 1; i <= s.length(); i++) {
            for (int j = 0; j < i; j++) {
                if (dp[i] |= dp[j] && wordDictSet.contains(s.substring(j, i))) {
                    break;
                }
            }
        }
        return dp[s.length()];
    }

    // 300. 最长递增子序列
    public int lengthOfLIS(int[] nums) {
        // dp[i] -> 以i为结尾的LIS长度
        int[] dp = new int[nums.length];
        Arrays.fill(dp, 1);
        int res = -1;
        for (int i = 0; i < nums.length; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
            res = Math.max(res, dp[i]);
        }
        return res;
    }

    public int lengthOfLIS2(int[] nums) {
        int[] dp = new int[nums.length];
        int len = 0;
        for (int num : nums) {
            int index = Arrays.binarySearch(dp, 0, len, num);
            if (index < 0) {
                index = -index - 1;
            }
            dp[index] = num;
            if (index == len) {
                len++;
            }
        }
        return len;
    }

    // 152. 乘积最大子数组
    public int maxProduct(int[] nums) {
        int[] dpMax = new int[nums.length];
        int[] dpMin = new int[nums.length];
        dpMax[0] = nums[0];
        dpMin[0] = nums[0];
        int res = nums[0];
        for (int i = 1; i < nums.length; i++) {
            dpMax[i] = Math.max(dpMin[i-1] * nums[i], Math.max(dpMax[i-1] * nums[i], nums[i]));
            dpMin[i] = Math.min(dpMax[i-1] * nums[i], Math.min(dpMin[i-1] * nums[i], nums[i]));
            res = Math.max(dpMax[i], res);
        }
        return res;
    }

    // 416. 分割等和子集
    public boolean canPartition(int[] nums) {
        int sum = 0;
        for (int num : nums) {
            sum += num;
        }
        if ((sum & 1) == 1) {
            return false;
        }
        int targetSum = sum >> 1;
        boolean[] dp = new boolean[targetSum + 1];
        dp[0] = true;
        for (int num : nums) {
            for (int i = targetSum; i >= num; i--) {
                dp[i] |= dp[i - num];
            }
        }
        return dp[targetSum];
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
