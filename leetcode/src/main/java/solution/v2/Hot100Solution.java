package solution.v2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static linkedlist.LinkedListSolution.ListNode;

public class Hot100Solution {

    // 128. 最长连续序列，哈希表+省去无效遍历
    public int longestConsecutive(int[] nums) {
        if (nums.length == 0) {
            return 0;
        }
        Set<Integer> set = new HashSet<>(nums.length);
        for (int num : nums) {
            set.add(num);
        }
        int result = 1;
        for (Integer num : set) {
            int resBuf = 1;
            if (set.contains(num - 1)) {
                continue;
            }
            while (set.contains(++num)) {
                resBuf++;
            }
            result = Math.max(result, resBuf);
        }
        return result;
    }

    // 4. 寻找两个正序数组的中位数
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int[] merge = new int[nums1.length + nums2.length];
        int iter1 = 0, iter2 = 0, iter = 0;
        while (iter1 < nums1.length && iter2 < nums2.length) {
            int result;
            if (nums1[iter1] < nums2[iter2]) {
                result = nums1[iter1];
                iter1++;
            } else {
                result = nums2[iter2];
                iter2++;
            }
            merge[iter++] = result;
        }
        while (iter1 < nums1.length) {
            merge[iter++] = nums1[iter1++];
        }
        while (iter2 < nums2.length) {
            merge[iter++] = nums2[iter2++];
        }
        int mid = merge.length / 2;
        if (merge.length % 2 == 0) {
            return (merge[mid - 1] + merge[mid]) / 2f;
        }
        return merge[mid];
    }

    /**
     * 437. 路径总和 III：求二叉树中和为目标值的路径数量
     * key：dfs + 哈希表 + 前缀和
     */
    public int pathSum(TreeNode root, int targetSum) {
        Map<Long, Integer> prefixSumCount = new HashMap<>(); // 实时前缀和个数
        prefixSumCount.put(0L, 1);
        AtomicInteger result = new AtomicInteger(0);
        dfs(root, prefixSumCount, 0L, targetSum, result);
        return result.get();
    }

    private void dfs(TreeNode treeNode, Map<Long, Integer> prefixSumCount, long currentSum, int targetSum, AtomicInteger result) {
        if (treeNode == null) {
            return;
        }
        currentSum += treeNode.val;
        result.addAndGet(prefixSumCount.getOrDefault(currentSum - targetSum, 0));
        prefixSumCount.compute(currentSum, (k, v) -> v == null ? 1 : v + 1);
        dfs(treeNode.left, prefixSumCount, currentSum, targetSum, result);
        dfs(treeNode.right, prefixSumCount, currentSum, targetSum, result);
        prefixSumCount.compute(currentSum, (k, v) -> v - 1);
    }

    // 438.找到字符串中所有字母异位词    滑动窗口（固定窗口大小的）
    public List<Integer> findAnagrams(String s, String p) {
        List<Integer> result = new ArrayList<>();
        if (s.length() < p.length()) {
            return result;
        }
        int[] pCnt = new int[26];
        int letterCnt = 0;  // 有几种不同的字母
        for (char c : p.toCharArray()) {
            if (pCnt[c - 'a'] == 0) {
                letterCnt++;
            }
            pCnt[c - 'a']++;
        }
        for (int i = 0; i < s.length(); i++) {
            pCnt[s.charAt(i) - 'a']--;
            if (pCnt[s.charAt(i) - 'a'] == 0) {
                letterCnt--;
            }
            if (letterCnt == 0) {
                result.add(i - p.length() + 1);
            }
            if (i >= p.length() - 1) {
                if (pCnt[s.charAt(i - p.length() + 1) - 'a'] == 0) {
                    letterCnt++;
                }
                pCnt[s.charAt(i - p.length() + 1) - 'a']++;
            }
        }
        return result;
    }

    // 215. 数组中的第K个最大元素
    public int findKthLargest(int[] nums, int k) {
        return findKthLargest(nums, 0, nums.length - 1, k - 1);
    }

    private int findKthLargest(int[] nums, int l, int r, int k) {
        if (l >= r) {
            return nums[l];
        }
        int pivot = partition(nums, l, r);
        if (pivot == k) {
            return nums[k];
        } else if (pivot > k) {
            return findKthLargest(nums, l, pivot - 1, k);
        } else {
            return findKthLargest(nums, pivot + 1, r, k);
        }
    }

    private int partition(int[] nums, int l, int r) {
        int i = l;
        int x = nums[r];
        for (int j = l; j < r; j++) {
            if (nums[j] >= x) {
                swap(nums, i++, j);
            }
        }
        swap(nums, i, r);
        return i;
    }

    private void swap(int[] nums, int x, int y) {
        int z = nums[x];
        nums[x] = nums[y];
        nums[y] = z;
    }

    // 141.判断链表中是否存在环
    public boolean hasCycle(ListNode head) {
        ListNode quick = head;
        ListNode slow = head;
        while (quick != null && quick.next != null && slow != null) {
            quick = quick.next.next;
            slow = slow.next;
            if (quick == slow) {
                return true;
            }
        }
        return false;
    }

    // 46.全排列，递归回溯
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        dfs(nums, new LinkedList<>(), result);
        return result;
    }

    private void dfs(int[] nums, LinkedList<Integer> path, List<List<Integer>> result) {
        if (path.size() == nums.length) {
            result.add(new ArrayList<>(path));
            return;
        }
        for (int num : nums) {
            if (path.contains(num)) {
                continue;
            }
            path.addLast(num);
            dfs(nums, path, result);
            path.removeLast();
        }
    }

    // 62.不同路径
    public int uniquePaths(int m, int n) {
        int[][] dp = new int[m][n];
        for (int i = 0; i < m; i++) {
            dp[i][0] = 1;
        }
        for (int i = 0; i < n; i++) {
            dp[0][i] = 1;
        }
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                dp[i][j] = dp[i - 1][j] + dp[i][j - 1];
            }
        }
        return dp[m - 1][n - 1];
    }

    // 406.根据身高重建队列，先排再插
    public int[][] reconstructQueue(int[][] people) {
        Arrays.sort(people, Comparator.<int[]>comparingInt(p -> p[0]).reversed().thenComparing(p -> p[1]));
        LinkedList<int[]> result = new LinkedList<>();
        for (int[] person : people) {
            result.add(person[1], person);
        }
        return result.toArray(new int[0][0]);
    }

    // 300.最长递增子序列，动态规划/贪心+二分查找
    public int lengthOfLIS(int[] nums) {
        // 贪心+二分查找，不断的优化结果数组
        List<Integer> tail = new ArrayList<>(nums.length);
        for (int num : nums) {
            int left = 0, right = tail.size() - 1;
            while (left <= right) {
                int mid = (left + right) / 2;
                if (num > tail.get(mid)) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
            // 这个是举例子算出来的
            if (left == tail.size()) {
                // 2,3,4 插入 5
                tail.add(num);
            } else if (left == tail.size() - 1) {
                // 2,3,6 更新为 5
                // 或者是2,3,6 插入1，更新为1,3,6
                // 更新是为了防止后面存在更长的子数组，保留更多可能性（实际上对于2,3,6 这个子数组来说，前面已经固定是2,3,6）
                // 比如2,3,6 插入1,2,3,4 就会把整个数组更新为1,2,3,4
                tail.set(tail.size() - 1, num);
            }
        }
        return tail.size();
    }

    // 11.盛最多水的容器，贪心+双指针
    public int maxArea(int[] height) {
        int i = 0, j = height.length - 1;
        int result = -1;
        while (i <= j) {
            result = Math.max(result, (j - i) * Math.min(height[i], height[j]));
            if (height[i] < height[j]) {
                i++;
            } else {
                j--;
            }
        }
        return result;
    }

    // 76.最小覆盖子串
    public String minWindow(String s, String t) {
        int[] tCnt = new int['z' - 'A' + 1];
        int difLetterCnt = 0;
        for (char c : t.toCharArray()) {
            if (tCnt[c - 'A']++ == 0) {
                difLetterCnt++;
            }
        }
        int left = 0, right = 0;
        int curLetterCnt = 0;
        String result = "";
        while (right < s.length()) {
            while (curLetterCnt < difLetterCnt && right < s.length()) {
                char c = s.charAt(right++);
                if (--tCnt[c - 'A'] == 0) {
                    curLetterCnt++;
                }
            }
            if (curLetterCnt < difLetterCnt) {
                return result;
            }
            while (curLetterCnt == difLetterCnt) {
                char c = s.charAt(left++);
                if (++tCnt[c - 'A'] > 0) {
                    curLetterCnt--;
                }
            }
            if (result.length() > (right - left + 1) || result.isEmpty()) {
                result = s.substring(left - 1, right);
            }
        }
        return result;
    }

    // 309.买卖股票的最佳时机含冷冻期——多组动态规划结合计算
    public int maxProfit(int[] prices) {
        int length = prices.length;
        int[] hold = new int[length];
        hold[0] = -prices[0];
        int[] sold = new int[length];
        int[] freeze = new int[length];
        for (int i = 1; i < length; i++) {
            int price = prices[i];
            hold[i] = Math.max(hold[i - 1], freeze[i - 1] - price);
            sold[i] = Math.max(sold[i - 1], hold[i - 1] + price);
            freeze[i] = Math.max(freeze[i - 1], sold[i - 1]);
        }
        int result = -1;
        for (int i = 0; i < length; i++) {
            result = Math.max(result, Math.max(hold[i], Math.max(sold[i], freeze[i])));
        }
        return result;
    }

    // 226.翻转二叉树
    public TreeNode invertTree(TreeNode root) {
        if (root == null) {
            return null;
        }
        TreeNode buf = root.left;
        root.left = root.right;
        root.right = buf;
        invertTree(root.left);
        invertTree(root.right);
        return root;
    }


    // 461.汉明距离
    public int hammingDistance(int x, int y) {
        return Integer.bitCount(x ^ y);
    }

    // 279.完全平方数：动态规划——完全背包
    public int numSquares(int n) {
        int[] dp = new int[n + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;
        for (int i = 1; i <= n; i++) {
            int sqrt = (int) Math.sqrt(i);
            for (int j = 1; j <= sqrt; j++) {
                dp[i] = Math.min(dp[i], dp[i - j * j] + 1);
            }
        }
        return dp[n];
    }

    // 139. 单词拆分：完全背包问题
    public boolean wordBreak(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);
        boolean[] dp = new boolean[s.length() + 1];
        dp[0] = true;
        for (int i = 1; i <= s.length(); i++) {
            for (int j = 0; j <= i; j++) {
                if (dp[i] |= dp[j] && wordSet.contains(s.substring(j, i))) {
                    break;
                }
            }
        }
        return dp[s.length()];
    }

    // 560.和为K的子数组：前缀和+哈希表，愿称之为经典！
    public int subarraySum(int[] nums, int k) {
        Map<Integer, Integer> prefixSumCountMap = new HashMap<>();
        prefixSumCountMap.put(0, 1);
        int curSum = 0;
        int result = 0;
        for (int num : nums) {
            curSum += num;
            result += prefixSumCountMap.getOrDefault(curSum - k, 0);
            prefixSumCountMap.compute(curSum, (s, c) -> c == null ? 1 : c + 1);
        }
        return result;
    }

    // 543.二叉树的直径
    public int diameterOfBinaryTree(TreeNode root) {
        AtomicInteger result = new AtomicInteger();
        depth(root, result);
        return result.get();
    }

    public int depth(TreeNode root, AtomicInteger result) {
        if (root == null) {
            return 0;
        }
        int leftDepth = depth(root.left, result);
        int rightDepth = depth(root.right, result);
        if ((leftDepth + rightDepth) > result.get()) {
            result.set(leftDepth + rightDepth);
        }
        return 1 + Math.max(leftDepth, rightDepth);
    }

    // 1.两数之和：哈希
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> numIndexMap = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (numIndexMap.containsKey(target - nums[i])) {
                return new int[]{numIndexMap.get(target - nums[i]), i};
            }
            numIndexMap.put(nums[i], i);
        }
        return new int[0];
    }

    // 20.有效的括号
    public boolean isValid(String s) {
        LinkedList<Character> stack = new LinkedList<>();
        for (char c : s.toCharArray()) {
            if (c == '(' || c == '{' || c == '[') {
                stack.push(c);
            } else {
                if (stack.isEmpty()) {
                    return false;
                }
                Character pop = stack.pop();
                if ((c == ')' && pop == '(') || (c == '}' && pop == '{') || (c == ']' && pop == '[')) {
                    continue;
                } else {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }

    // 21.合并两个有序链表：简单指针
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode();
        ListNode iter1 = list1, iter2 = list2, resIter = dummy;
        while (iter1 != null && iter2 != null) {
            if (iter1.val < iter2.val) {
                resIter.next = iter1;
                iter1 = iter1.next;
            } else {
                resIter.next = iter2;
                iter2 = iter2.next;
            }
            resIter = resIter.next;
        }
        if (iter1 != null) {
            resIter.next = iter1;
        }
        if (iter2 != null) {
            resIter.next = iter2;
        }
        return dummy.next;
    }


    // 70.爬楼梯：简单dp
    public int climbStairs(int n) {
        int[] dp = new int[n + 1];
        dp[0] = 1;
        dp[1] = 1;
        for (int i = 2; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        return dp[n];
    }

    // 94.二叉树的中序遍历
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        _inorderTraversal(root, result);
        return result;
    }

    private void _inorderTraversal(TreeNode root, List<Integer> result) {
        if (root == null) {
            return;
        }
        _inorderTraversal(root.left, result);
        result.add(root.val);
        _inorderTraversal(root.right, result);
    }

    // 739.每日温度：单调栈 ～amazing
    public int[] dailyTemperatures(int[] temperatures) {
        LinkedList<int[]> stack = new LinkedList<>();
        int[] result = new int[temperatures.length];
        for (int i = 0; i < temperatures.length; i++) {
            if (stack.isEmpty()) {
                stack.push(new int[]{temperatures[i], i});
                continue;
            }
            while (!stack.isEmpty() && temperatures[i] > stack.peek()[0]) {
                // 破坏了递减性，需要弹出栈内元素并计算结果
                int[] pop = stack.pop();
                result[pop[1]] = i - pop[1];
            }
            // 入栈
            stack.push(new int[] {temperatures[i], i});
        }

        while (!stack.isEmpty()) {
            int[] pop = stack.pop();
            result[pop[1]] = 0;
        }
        return result;
    }

    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {
        }

        TreeNode(int val) {
            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

}
