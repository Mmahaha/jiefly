import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * 剑指offer题库
 */
public class CodingInterviewSolution {
    // 剑指 Offer 03. 数组中重复的数字
    public int findRepeatNumber(int[] nums) {
        int i = 0, temp;
        while (i < nums.length){
            if (nums[i] == i){
                i++;
                continue;
            }
            if (nums[i] == nums[nums[i]]){
                return nums[i];
            }
            temp = nums[i];
            nums[i] = nums[temp];
            nums[temp] = temp;
        }
        return -1;
    }

    // 剑指 Offer 04. 二维数组中的查找
    public boolean findNumberIn2DArray(int[][] matrix, int target) {
        if (matrix.length == 0 || matrix[0].length == 0){
            return false;
        }
        int x = 0;
        int y = matrix[0].length - 1;
        while (x < matrix.length && y >= 0){
            int cur = matrix[x][y];
            if (cur == target){
                return true;
            }
            if (cur > target){
                y--;
            } else {
                x++;
            }
        }
        return false;
    }

    // 剑指 Offer 05. 替换空格，C++可以直接改原字符串（从后往前赋值），Java只能开一个新字符串了
    public String replaceSpace(String s) {
        StringBuilder result = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c == ' '){
                result.append("%20");
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    // 剑指 Offer 06. 从尾到头打印链表
    public int[] reversePrint(ListNode head) {
        Deque<Integer> stack = new LinkedList<>();
        int count = 0;
        for (ListNode p = head; p != null; p = p.next){
            count++;
        }
        int[] result = new int[count];
        for (int i = count - 1; i >= 0; i--) {
            result[i] = head.val;
            head = head.next;
        }
        return result;
    }

    public static class ListNode{
        int val;
        ListNode next;
        ListNode(int x) {val = x;}
    }

}
