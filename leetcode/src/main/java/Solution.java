import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

public class Solution {
    // 1768. 交替合并字符串
    public String mergeAlternately(String word1, String word2) {
        int index1 = 0, index2 = 0;
        int length1 = word1.length();
        int length2 = word2.length();
        StringBuilder sb = new StringBuilder();
        while(index1 < length1 || index2 < length2){
            if (index1 < length1){
                sb.append(word1.charAt(index1));
                index1++;
            }
            if (index2 < length2){
                sb.append(word2.charAt(index2));
                index2++;
            }
        }

        return sb.toString();
    }

    /**
     * 915. 分割数组
     */
    public int partitionDisjoint(int[] nums) {
        int n = nums.length;
        int leftMax = nums[0], leftPos = 0, curMax = nums[0];
        for (int i = 1; i < n - 1; i++) {
            curMax = Math.max(curMax, nums[i]);
            if (nums[i] < leftMax) {
                leftMax = curMax;
                leftPos = i;
            }
        }
        return leftPos + 1;
    }

//    481. 神奇字符串
    public int magicalString(int n) {
        if (n <= 3){
            return 1;
        }
        StringBuilder sb = new StringBuilder("122");
        int result = 1;
        int current = 1;
        int pointer = 2;
        while(sb.length() < n){
            int value = sb.charAt(pointer) - '0';
            if (value == 1){
                sb.append(current);
            }else {
                sb.append(current).append(current);
            }
            pointer++;
            if (current == 1){
                result += value;
                current = 2;
            }else {
                current = 1;
            }
        }
        if (current == 2 && sb.length() > n){
            result--;
        }
        return result;
    }

    // 1662. 检查两个字符串数组是否相等
    public boolean arrayStringsAreEqual(String[] word1, String[] word2) {
        return String.join("", word1).equals(String.join("", word2));
    }

    // 4. 寻找两个正序数组的中位数 TODO O(N)=log(m+n)
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int length = nums1.length + nums2.length;
        int pointer = 0, p1 = 0, p2 = 0;
        int leftIndex, rightIndex;
        if ((length & 1) == 1){
            leftIndex = (length - 1) / 2;
            rightIndex = leftIndex;
        } else {
            leftIndex = (length - 1) / 2;
            rightIndex = leftIndex + 1;
        }
        int curVal, leftValue = 0, rightValue=0;
        while(pointer <= rightIndex){
            int val1 = Integer.MAX_VALUE, val2 = Integer.MAX_VALUE;
            if (p1 < nums1.length){
                val1 = nums1[p1];
            }
            if (p2 < nums2.length){
                val2 = nums2[p2];
            }
            if (val1 < val2){
                curVal = val1;
                p1 ++;
            }else {
                curVal = val2;
                p2 ++;
            }
            if (pointer == leftIndex){
                leftValue = curVal;
            }
            if (pointer == rightIndex){
                rightValue = curVal;
            }
            pointer++;
        }
        return (leftValue + rightValue)/2d;
    }

    // 1668. 最大重复子字符串   TODO kmp
    public int maxRepeating(String sequence, String word) {
        StringBuilder sb = new StringBuilder(word);
        int result = 0;
        while (sequence.contains(sb)){
            result ++;
            sb.append(word);
        }
        return result;
    }

    // 1106. 解析布尔表达式
    public boolean parseBoolExpr(String expression) {
        Deque<Character> stack = new ArrayDeque<>();
        char processing;
        int p = -1, length = expression.length();
        while (++p < length){
            char c = expression.charAt(p);
            if(c != ')'){
                stack.push(c);
            } else {
                int t = 0, f = 0;
                // 开始解析一个括号内的表达式
                while ((processing = stack.pop()) != '('){
                    if (processing == 't'){
                        t++;
                    } else if (processing == 'f'){
                        f++;
                    }
                }
                switch (stack.pop()){
                    case '!':
                        stack.push(t == 1 ? 'f' : 't');
                        break;
                    case '&':
                        stack.push(f == 0 ? 't' : 'f');
                        break;
                    case '|':
                        stack.push(t == 0 ? 'f' : 't');
                        break;
                }
            }
        }
        return stack.pop() == 't';
    }



}
