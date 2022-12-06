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

}
