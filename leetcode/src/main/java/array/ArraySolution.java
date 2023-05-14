package array;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

public class ArraySolution {

    // 704 二分查找
    public int search(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        while (left <= right) {
            int m = (left + right) >> 1;
            if (nums[m] > target) {
                right = m - 1;
            } else if (nums[m] < target) {
                left = m + 1;
            } else {
                return m;
            }
        }
        return -1;
    }

    // 35. 搜索插入位置
    public int searchInsert(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        while (left <= right) {
            int m = (left + right) >> 1;
            if (nums[m] > target) {
                right = m - 1;
            } else if (nums[m] < target) {
                left = m + 1;
            } else {
                return m;
            }
        }
        return left;
    }

    // 34. 在排序数组中查找元素的第一个和最后一个位置
    public int[] searchRange(int[] nums, int target) {
        return new int[]{binarySearch(nums, target, true), binarySearch(nums, target, false)};
    }

    private int binarySearch(int[] nums, int target, boolean searchLeft) {
        int left = 0, right = nums.length - 1;
        int result = -1;
        while (left <= right) {
            int m = (left + right) >> 1;
            if (nums[m] > target) {
                right = m - 1;
            } else if (nums[m] < target) {
                left = m + 1;
            } else {
                result = m;
                if (searchLeft) {
                    right = m - 1;
                } else {
                    left = m + 1;
                }
            }
        }
        return result;
    }

    // 27. 移除元素
    public int removeElement(int[] nums, int val) {
        int n = nums.length - 1, i = 0;
        while (i <= n) {
            if (nums[i] == val) {
                int temp = nums[n];
                nums[n] = nums[i];
                nums[i] = temp;
                n--;
            } else {
                i++;
            }
        }
        return n + 1;
    }

    // 977. 有序数组的平方
    public int[] sortedSquares(int[] nums) {
        int left = 0, right = nums.length - 1, n = nums.length;
        int[] result = new int[nums.length];
        while (left <= right) {
            if (Math.abs(nums[left]) > Math.abs(nums[right])) {
                result[--n] = (int) Math.pow(nums[left++], 2);
            } else {
                result[--n] = (int) Math.pow(nums[right--], 2);
            }
        }
        return result;
    }

    // 209. 长度最小的子数组
    public int minSubArrayLen(int target, int[] nums) {
        int result = Integer.MAX_VALUE, j = 0, n = nums.length, sum = 0;
        for (int i = 0; i < nums.length; i++) {
            sum += nums[i];
            while (sum >= target) {
                result = Math.min(result, i - j + 1);
                sum -= nums[j++];
            }
        }
        return result == Integer.MAX_VALUE ? 0 : result;
    }

    // 904. 水果成篮
    public int totalFruit(int[] fruits) {
        int[] count = new int[fruits.length + 10];
        int result = 0, size = 0;
        for (int i = 0, j = 0; i < fruits.length; i++) {
            if (++count[fruits[i]] == 1) {
                size++;
            }
            while (size > 2) {
                if (--count[fruits[j++]] == 0) {
                    size--;
                }
            }

            result = Math.max(result, i - j + 1);
        }
        return result;
    }

    // 76. 最小覆盖子串
    public String minWindow(String s, String t) {
        Map<Character, Integer> tCnt = new HashMap<>(t.length());
        Map<Character, Integer> sCnt = new HashMap<>(t.length());
        StringBuilder sb = new StringBuilder();
        LinkedList<Integer> sbIndex = new LinkedList<>();
        for (char c : t.toCharArray()) {
            tCnt.compute(c, (k,v) -> v == null ? 1 : v + 1);
        }
        int typeCnt = tCnt.entrySet().size(), j = 0;
        int[] res = new int[]{0, Integer.MAX_VALUE};
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (tCnt.containsKey(c)) {
                sb.append(c);
                sbIndex.addLast(i);
                sCnt.compute(c, (k,v) -> v == null ? 1 : v + 1);
                if (typeCnt > 0 && Objects.equals(sCnt.get(c), tCnt.get(c))) {
                    --typeCnt;
                }
                if (typeCnt == 0) {
                    slide(i, sbIndex, res, sCnt, tCnt, sb);
                    typeCnt++;
                }
            }
        }
        return res[1] == Integer.MAX_VALUE ? "" : s.substring(res[0], res[1] + 1);
    }

    private void slide(int i, LinkedList<Integer> sbIndex, int[] res, Map<Character, Integer> sCnt, Map<Character, Integer> tCnt, StringBuilder sb) {
        if ((i - sbIndex.getFirst()) < (res[1] - res[0])) {
            res[0] = sbIndex.getFirst();
            res[1] = i;
        }
        char toDel = sb.charAt(0);
        sCnt.compute(toDel, (k, v) -> v - 1);
        sb.deleteCharAt(0);
        sbIndex.removeFirst();
        if (sCnt.get(toDel) >= tCnt.get(toDel)) {
            slide(i, sbIndex, res, sCnt, tCnt, sb);
        }
    }

    // 59. 螺旋矩阵 II
    public int[][] generateMatrix(int n) {
        int[][] result = new int[n][n];
        int i = 0, j = -1, num = 0;
        while (n > 0) {
            while (++j < n) {
                if (result[i][j] > 0) {break;}
                result[i][j] = ++num;
            }
            j--;
            while (++i < n) {
                if (result[i][j] > 0) {break;}
                result[i][j] = ++num;
            }
            i--;
            while (--j >= 0) {
                if (result[i][j] > 0) {break;}
                result[i][j] = ++num;
            }
            j++;
            while (--i >= 0) {
                if (result[i][j] > 0) {break;}
                result[i][j] = ++num;
            }
            i++;
            n--;
        }
        return result;
    }

}
