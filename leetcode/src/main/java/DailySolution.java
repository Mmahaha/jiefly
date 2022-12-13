import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DailySolution {
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

    // 1684. 统计一致字符串的数目
    public int countConsistentStrings(String allowed, String[] words) {
        BitSet bitSet = new BitSet();
        int result = words.length;
        for (char c: allowed.toCharArray()){
            bitSet.set(c-'a');
        }
        for (String word: words){
            for (char c: word.toCharArray()){
                if (!bitSet.get(c-'a')){
                    result--;
                    break;
                }
            }
        }
        return result;
    }

    // 791. 自定义字符串排序    todo优化
    public String customSortString(String order, String s) {
        Map<Character, Integer> sortSeq = new HashMap<>(26);
        int index = 0;
        for (char c: order.toCharArray()){
            sortSeq.put(c, index++);
        }
        index = 0;
        char[] result = new char[s.length()];
        for(Character c: s.chars().mapToObj(i -> (char) i).sorted(Comparator.comparingInt(ss->sortSeq.getOrDefault(ss, 999))).toArray(Character[]::new)){
            result[index++] = c;
        }
        return new String(result);
    }

    /**
     * 练习题：给定一个数组，判断数组中是否存在和为给定值的数
     * @param array 数组
     * @param i 范围[0,i]
     * @param sum   存在和为sum的值
     * @return  是否存在
     */
    public boolean subset(int[] array, int i, int sum){
        if (sum == 0){
            return true;
        } else if (i == 0) {
            return array[0] == sum;
        } else if (array[i] > sum){
            return subset(array, i-1, sum);
        } else {
            return subset(array, i-1, sum) || subset(array, i-1, sum-array[i]);
        }
    }

    /**
     * 775. 全局倒置与局部倒置
     */
    public boolean isIdealPermutation(int[] nums) {
        if (nums.length < 3){
            return true;
        }
        int max = -1;
        int p1 = -1, p2 = 1;
        while(++p1 < nums.length && ++p2 < nums.length){
            max = Math.max(max, nums[p1]);
            if (nums[p2] < max){
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    // 792. 匹配子序列的单词数
    public int numMatchingSubseq(String s, String[] words) {
        Queue<Integer>[] queueArray = new LinkedList[26];
        Iterator<Integer>[] iteratorBuf = new Iterator[26];
        int result = 0, last;
        Integer headBuf = -1;
        int index = 1;
        for (char c : s.toCharArray()) {
            if (queueArray[c - 'a'] == null) {
                Queue<Integer> queue = new LinkedList<>();
                queueArray[c - 'a'] = queue;
            }
            queueArray[c - 'a'].offer(index++);
        }

        outer:
        for (String word : words) {
            for (int i = 0; i < 26; i++) {
                iteratorBuf[i] = null;
            }
            last = -1;
            for (char c : word.toCharArray()) {
                if (iteratorBuf[c-'a'] == null){
                    if (queueArray[c-'a'] == null){
                        continue outer;
                    }
                    iteratorBuf[c-'a'] = queueArray[c-'a'].iterator();
                }
                Iterator<Integer> iterator = iteratorBuf[c - 'a'];
                if (!iterator.hasNext()){
                    continue outer;
                }
                //noinspection StatementWithEmptyBody
                while (iterator.hasNext() && (headBuf = iterator.next()) < last){
                }
                if (headBuf < last){
                    continue outer;
                }
                last = headBuf;
            }
            System.out.println(word);
            result ++;
        }
        return result;
    }

    // 1732. 找到最高海拔
    public int largestAltitude(int[] gain) {
        int result = 0, currentAltitude = 0;
        for (int subtract: gain){
            currentAltitude += subtract;
            result = Math.max(currentAltitude, result);
        }
        return result;
    }

    // 799. 香槟塔
    public double champagneTower(int poured, int query_row, int query_glass) {
        double[][] dp = new double[query_row+2][query_row+2];
        dp[0][1] = poured;
        for (int i = 1; i < query_row + 2; i++) {
            for (int j = 1; j < query_row + 2; j++) {
                dp[i][j] = Math.max(0, ((dp[i-1][j]-1))/2) +Math.max(0, ((dp[i-1][j-1]-1))/2);
            }
        }
        return Math.min(1,dp[query_row][query_glass + 1]);
    }

    // 891. 子序列宽度之和
    public int sumSubseqWidths(int[] nums) {
        Arrays.sort(nums);
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal mod = BigDecimal.valueOf(1e9 + 7);
        for (int i = 0; i < nums.length - 1; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                BigDecimal sub = new BigDecimal(nums[j] - nums[i]);
                BigDecimal pow = BigDecimal.valueOf(Math.pow(2, j - i - 1));
                result = result.add(pow.multiply(sub));
            }
        }
        return result.divideAndRemainder(mod)[1].intValue();
    }

    // 808. 分汤
    public double soupServings(int n) {
        if(n > 4450) {
            return 1;
        }
        n = (int) Math.ceil(n / 25d);
        // dp[x][y]表示A杯剩余x份，B份剩余y份的答案
        double[][] dp = new double[n+1][n+1];
        dp[0][0] = 0.5;   // 同时分完 0 + 1/2
        for (int i = 1; i <= n; i++) {
            dp[0][i] = 1;   // A为0了，此时A必定先分完
        }
        // 状态转移公式 dp[a][b] = 0.25*(dp[a-4][b]+dp[a-3][b-1]+dp[a-2][b-2]+dp[a-1][b-3])
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                dp[i][j] = 0.25 * (dp[Math.max(0, i-4)][j] + dp[Math.max(0, i-3)][j-1] + dp[Math.max(0, i-2)][Math.max(0, j-2)] + dp[Math.max(0, i-1)][Math.max(0, j-3)]);
            }
        }
        return dp[n][n];
    }

    // 1742. 盒子中小球的最大数量
    public int countBalls(int lowLimit, int highLimit) {
        int[] bucket = new int[46];
        int bufI;
        for (int i = lowLimit; i <= highLimit; i++) {
            bufI = i;
            int result = 0;
            while (bufI > 0) {
                result += bufI % 10;
                bufI /= 10;
            }
            bucket[result]++;
        }
        return IntStream.of(bucket).max().getAsInt();
    }

    // 795. 区间子数组个数，写复杂了
    public int numSubarrayBoundedMax(int[] nums, int left, int right) {
        BiFunction<Map<Integer, Integer>, Integer, Integer> calculateAndClear = (map, index) -> {
            int result = 0;
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                result += (index - entry.getKey()) * entry.getValue();
            }
            map.clear();
            return result;
        };
        Map<Integer, Integer> index2Times = new HashMap<>(16);
        int times = 1;
        int i;
        int result = 0;
        for (i = 0; i < nums.length; i++) {
            if (nums[i] < left){
                times ++;
            } else if (nums[i] >= left && nums[i] <= right){
                index2Times.put(i, times);
                times = 1;
            } else if (nums[i] > right){
                result += calculateAndClear.apply(index2Times, i);
                times = 1;
            }
        }
        result += calculateAndClear.apply(index2Times, i);
        return result;
    }

    // 809. 情感丰富的文字
    public int expressiveWords(String s, String[] words) {
        int count1, count2, p1, p2, result = 0;
        for (String word : words) {
            p1 = -1; p2 = -1;
            while (++p1 < s.length() & ++p2 < word.length()){
                if (s.charAt(p1) != word.charAt(p2)){
                    break;
                }
                count1 = 1; count2 = 1;
                while (p1+1 < s.length() && s.charAt(p1) == s.charAt(p1+1)){
                    p1++;
                    count1++;
                }
                while (p2+1 < word.length() && word.charAt(p2) == word.charAt(p2+1)){
                    p2++;
                    count2++;
                }
                if (count1 < count2 || (count1 > count2 && count1 < 3)){
                    break;
                }
            }
            if (p1 == s.length() && p2 == word.length()){
                result++;
            }
        }
        return result;
    }

    // 1752.检查数组是否经排序和轮转得到
    public boolean check(int[] nums) {
        if (nums.length <= 2){
            return true;
        }
        boolean alreadyIncrease = false;
        for (int i = 0; i < nums.length - 1; i++) {
            if (nums[i] > nums[i+1]){
                if (alreadyIncrease){
                    return false;
                } else {
                    alreadyIncrease = true;
                }
            }
        }
        return nums[0] >= nums[nums.length-1] || !alreadyIncrease;
    }

    // 1758. 生成交替二进制字符串的最少操作数
    public int minOperations(String s) {
        Function<Character,Character> flip = c ->  c=='1'?'0':'1';
        char head = '0';
        int result = 0;
        for (char c : s.toCharArray()) {
            if (c == head){
                result++;
            }
            head = flip.apply(head);
        }
        return Math.min(result, s.length() - result);
    }

    // 895. 最大频率栈
    @SuppressWarnings({"InnerClassMayBeStatic", "unchecked"})
    class FreqStack {
        LinkedList<Integer>[] detail = new LinkedList[2*(int)1e4];
        Map<Integer,Integer> frequency = new HashMap<>(20000);

        int maxTimes = 0;
        public FreqStack() {

        }

        public void push(int val) {
            Integer times = frequency.compute(val, (k, v) -> v == null ? 1 : v + 1);
            if (detail[times] == null){
                detail[times] = new LinkedList<>();
            }
            detail[times].addLast(val);
            maxTimes = Math.max(maxTimes, times);
        }

        public int pop() {
            Integer result = detail[maxTimes].removeLast();
            frequency.compute(result, (k,v) -> Objects.requireNonNull(v) - 1);
            maxTimes = detail[maxTimes].isEmpty() ? maxTimes - 1 : maxTimes;
            return result;
        }
    }

    // 1779. 找到最近的有相同 X 或 Y 坐标的点
    public int nearestValidPoint(int x, int y, int[][] points) {
        int availableIndex = -1;
        int minimumDistance = Integer.MAX_VALUE;
        int currentDistance;
        int curIndex = 0;
        for (int[] point : points) {
            int m = point[0];
            int n = point[1];
            if ((m == x || n == y) && ((currentDistance=Math.abs(m-x)+Math.abs(n-y)) < minimumDistance)){
                availableIndex = curIndex;
                minimumDistance = currentDistance;
            }
            curIndex++;
        }
        return availableIndex;
    }

    // 1769. 移动所有球到每个盒子所需的最小操作数
    public int[] minOperations2(String boxes) {
        char[] chars = boxes.toCharArray();
        int[] result = new int[chars.length];
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '1'){
                for (int i1 = 0; i1 < result.length; i1++) {
                    result[i1] += Math.abs(i - i1);
                }
            }
        }
        return result;
    }

    // 1796. 字符串中第二大的数字
    public int secondHighest(String s) {
        int secondHighest = -1;
        int firstHighest = -1;
        int cur;
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)){
                if ((cur = c-'0') > firstHighest){
                    secondHighest = firstHighest;
                    firstHighest = cur;
                } else if (cur > secondHighest && cur != firstHighest) {
                    secondHighest = cur;
                }
            }
        }
        return secondHighest;
    }

    // 1774. 最接近目标价格的甜点成本
    public int closestCost(int[] baseCosts, int[] toppingCosts, int target) {
        // 获取基料的最小值
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        int x = Arrays.stream(baseCosts).min().getAsInt();

        // 进行特判
        if (x >= target) {
            return x;
        }

        // 表示每个价位是否存在合法的方案, 从 0 到 target
        boolean[] can = new boolean[target + 1];

        // 用一个变量来记录“开销超过 target ”的情况
        // 当取到 2 * target - x 的时候， 根据题目中 "返回成本相对较低的一种"，x 与 2 * target - x 关于 target 对称， 那么一定返回 x
        // 所以如果 “开销超过 target ”的值取到超过 res, 那么比 x 距离 target 还远， 一定不成立
        int res = 2 * target - x;

        // 遍历基料数组, 初始化can数组, 修改 res
        for(int b : baseCosts) {
            if (b <= target) {
                can[b] = true;
            } else {
                res = Math.min(res, b);
            }
        }

        // 遍历辅料数组
        for(int t : toppingCosts) {
            // 每种基料可以加两种辅料
            for (int count = 0; count < 2; count++) {
                // 从要凑配的目标开始反向遍历
                for (int i = target; i > 0; i--) {
                    // 进行动态规划，如果当前位置 can 数组设置为 true, 即允许进行下一步操作的话
                    // 如果比 target 大
                    if (can[i] && i + t > target) {
                        res = Math.min(res, i + t);
                    }
                    // 因为任意一个合法方案加上一份配料一定也是合法方案，所以 can 数组的状态转义方程是 can[i] = can[i] | can[i - t]
                    // can[i - t] 就是去掉当前这份配料以后，有可能存在的上一份合法方案, 根据此更新 can 数组的值
                    if (i - t > 0) {
                        can[i] = can[i] | can[i - t];
                    }
                }
            }
        }
        // 先找比 target 小的方案 （且注意范围，遍历 res - target + 1 次，保证 Math.abs(target - i) <= Math.abs(target - res) ）
        for (int i = 0; i <= res - target; i++) {
            // 从 target 往前依次找是否有满足条件的方案
            if (can[target - i ]) {
                return target - i;
            }
        }
        // 若在范围内没找到比 target 小的，就返回“成本相对较低的”的 比 target 大的情况
        return res;
    }

    // 1805. 字符串中不同整数的数目
    public int numDifferentIntegers(String word) {
        Set<String> result = new HashSet<>();
        StringBuilder builderBuf = new StringBuilder();
        int i = -1;
        char current;
        while (i < word.length() - 1){
            if (Character.isDigit(word.charAt(++i))){
                boolean isZero = true;
                while (i < word.length() && Character.isDigit(current = word.charAt(i))){
                    if (!(isZero && current == '0')){
                        isZero = false;
                        builderBuf.append(current);
                    }
                    i++;
                }
                result.add(isZero ? "0" : builderBuf.toString());
                builderBuf.delete(0, builderBuf.length());
            }
        }
        return result.size();
    }

    // 1775. 通过最少操作次数使数组的和相等
    public int minOperations(int[] nums1, int[] nums2) {
        int[] count1 = new int[7];
        int[] count2 = new int[7];
        int sum1 = 0, sum2 = 0, result = 0;
        for (int i : nums1) {
            count1[i]++;
            sum1 += i;
        }
        for (int i : nums2) {
            count2[i]++;
            sum2 += i;
        }
        if (sum1 > sum2){
            int[] temp;
            temp = count1;
            count1 = count2;
            count2 = temp;
        }
        int subtract = Math.abs(sum1 - sum2);
        // count1.sum <= count2.sum
        for (int i = 1; i <= 6 ; i++) {
            int basic = 6 - i;
            // count1增加n个basic或count2减少n个basic
            int max = count1[i] + count2[7-i];
            int need = (int) Math.ceil(subtract * 1.0 / basic);
            if (need <= max){
                result += need;
                subtract = 0;
                break;
            }
            subtract -= basic * max;
            result += max;
        }
        return subtract == 0 ? result : -1;
    }

    // 1812. 判断国际象棋棋盘中一个格子的颜色
    public boolean squareIsWhite(String coordinates) {
        return (((coordinates.charAt(0) - 'a') + (coordinates.charAt(1) - '0')) & 1) == 0;
    }

    // 1780. 判断一个数字是否可以表示成三的幂的和
    public boolean checkPowersOfThree(int n) {
        for (int i = n; i > 0; i /= 3){
            if (i % 3 == 2){
                return false;
            }
        }
        return true;
    }

    // 1827. 最少操作使数组递增
    public int minOperations(int[] nums) {
        int result = 0;
        for (int i = 0; i < nums.length - 1; i++) {
            if (nums[i+1] <= nums[i]){
                result += nums[i] - nums[i+1] + 1;
                nums[i+1] = nums[i] + 1;
            }
        }
        return result;
    }

    // 1781. 所有子字符串美丽值之和
    public int beautySum(String s) {
        int result = 0;
        for (int i = 0; i < s.length() - 2; i++) {
            int[] count = new int[26];
            for (int j = i; j < s.length(); j++) {
                int min = Integer.MAX_VALUE, max = 0;
                count[s.charAt(j) - 'a']++;
                for (int k = 0; k < 26; k++) {
                    if (count[k] > 0) {
                        min = Math.min(min, count[k]);
                        max = Math.max(max, count[k]);
                    }
                }
                result += max - min;
            }
        }
        return result;
    }

    // 1832. 判断句子是否为全字母句
    public boolean checkIfPangram(String sentence) {
        if (sentence.length() < 26){
            return false;
        }
        BitSet bitSet = new BitSet(26);
        int length = 0;
        for (char c : sentence.toCharArray()) {
            if (!bitSet.get(c-'a')){
                bitSet.set(c-'a');
                length++;
                if (length == 26){
                    return true;
                }
            }
        }
        return false;
    }
}

