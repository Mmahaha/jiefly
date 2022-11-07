import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    // 1678. 设计 Goal 解析器
    public String interpret(String command) {
        StringBuilder sb = new StringBuilder();
        int index = -1;
        while (++index < command.length()){
            char c = command.charAt(index);
            if (c == 'G'){
                sb.append(c);
            } else if (c == '('){
                if (command.charAt(++index) == ')'){
                    sb.append('o');
                } else {
                    index += 2;
                    sb.append("al");
                }
            }
        }
        return sb.toString();
    }


    /**
     * 通过kmp实现String.indexOf()
     * @param expression    字符串的完整表达式
     * @param pattern   要匹配的内容
     * @return  返回第一个下标
     */
    public int indexOf(String expression, String pattern){
        List<Integer> next = buildNext(pattern);
        for (int i = 0, j = 0; i < expression.length(); i++){
            while (j > 0 && expression.charAt(i) != pattern.charAt(j)){
                j = next.get(j);
            }
            if (expression.charAt(i) == pattern.charAt(j)){
                j++;
            }
            if (j == pattern.length()){
                return i - pattern.length() + 1;
            }
        }
        return -1;
    }

    /**
     * 根据pattern构造next数组
     * 譬如 ababc，会构造出(0,0,0,1,2)
     * 原理：拿pattern和向右shift一位的pattern进行比较
     * @param pattern   要构造的字符串
     * @return  返回next数组，下标i表示长度小于i的最长相同前后缀长度
     */
    public List<Integer> buildNext(String pattern){
        List<Integer> next = Stream.of(0, 0).collect(Collectors.toList());
        for (int i = 1, j = 0; i< pattern.length(); i++){
            while (j > 0 && pattern.charAt(i) != pattern.charAt(j)){
                j = next.get(j);
            }
            if (pattern.charAt(i) == pattern.charAt(j)){
                j++;
            }
            next.add(j);
        }
        return next;
    }

    // 816. 模糊坐标
    Map<String, List<String>> cache = new HashMap<>(16);
    public List<String> ambiguousCoordinates(String s) {
        List<String> result = new ArrayList<>();
        // 先去掉两个括号
        String numbers = s.substring(1, s.length() - 1);
        int length = numbers.length();
        int p = 0;
        while (++p < length){
            String n1 = numbers.substring(0, p);
            List<String> xList = splitNumToDecimals(n1);
            String n2 = numbers.substring(p, length);
            List<String> yList = splitNumToDecimals(n2);
            // 结果中加入两者的笛卡尔积
            result.addAll(xList.stream().flatMap(x -> yList.stream().map(y -> String.format("(%s, %s)", x ,y))).collect(Collectors.toList()));
        }
        return result;
    }

    public List<String> splitNumToDecimals(String number2){
        return cache.computeIfAbsent(number2, number -> {
            List<String> result = new ArrayList<>();
            if (!number.startsWith("0") || number.length() == 1){
                result.add(number);
            }
            int p = 0;
            int length = number.length();
            while (++p < length){
                String integerPart = number.substring(0, p);
                if (integerPart.startsWith("0") && integerPart.length() > 1){
                    continue;
                }
                String decimalPart = number.substring(p, length);
                if (decimalPart.endsWith("0")){
                    continue;
                }
                result.add(String.join(".", integerPart, decimalPart));
            }
            return result;
        });

    }

    // 5. 最长回文子串，这里用的是中心扩张法
    public String longestPalindrome(String s) {
        int p = -1, p1, left = 0, right = 0;
        int[] p2Array;
        while (++p < s.length() - 1){
            if (s.charAt(p) == s.charAt(p+1)){
                p2Array = new int[]{p, p+1};
            } else {
                p2Array = new int[]{p};
            }
            for (int p2: p2Array){
                p1 = p;
                //noinspection StatementWithEmptyBody
                while(((--p1 >= 0) & (++p2 < s.length())) && s.charAt(p1) == s.charAt(p2)){}
                if ((right - left) < (p2 - p1 - 2)){
                    left = p1 + 1;
                    right = p2 - 1;
                }
            }

        }
        return s.substring(left, right + 1);
    }

}
