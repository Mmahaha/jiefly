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

}