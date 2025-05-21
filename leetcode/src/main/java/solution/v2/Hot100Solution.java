package solution.v2;

import com.mysql.cj.exceptions.CJPacketTooBigException;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
                letterCnt ++;
            }
            pCnt[c - 'a']++;
        }
        for (int i = 0; i < s.length(); i++) {
            pCnt[s.charAt(i) - 'a']--;
            if (pCnt[s.charAt(i) - 'a'] == 0) {
                letterCnt --;
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
        return findKthLargest(nums, 0, nums.length - 1, nums.length - k);
    }

    private int findKthLargest(int[] nums, int l, int r, int k) {
        if (l > r) {
            return -1;
        }
        int pivot = partition(nums, l, r);
        if (pivot == k) {
            return nums[pivot];
        } else if (pivot > k) {
            return findKthLargest(nums, l, pivot, k);
        } else {
            return findKthLargest(nums, pivot + 1, r, k);
        }
    }

    private int partition(int[] nums, int l, int r) {
        int pivot = nums[(l + r) / 2];
        int m = l - 1;
        int n = r + 1;
        while (true) {
            while (nums[++m] < pivot) {}
            while (nums[--n] > pivot) {}
            if (m >= n) {
                return n;
            }
            swap(nums, m, n);
        }
    }

    private void swap(int[] nums, int x, int y) {
        int z = nums[x];
        nums[x] = nums[y];
        nums[y] = z;
    }

}
