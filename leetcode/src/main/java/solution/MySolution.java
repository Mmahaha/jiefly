package solution;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class MySolution {

    // 猜字谜，对每个字符串排序+去重
    public String wordPuzzle(String words, String solution) {
        List<String> result = new ArrayList<>(20);
        String[] wordArr = words.split(",");
        // 对字符串进行排序操作
        Function<String, String> sortFunc = s -> {
            char[] charArray = s.toCharArray();
            Arrays.sort(charArray);
            return new String(charArray);
        };
        // 对字符串进行去重操作
        Function<String, String> distinctFunc = s -> {
            StringBuilder sb = new StringBuilder();
            sb.append(s.charAt(0));
            for (int i = 1; i < s.length(); i++) {
                if (s.charAt(i) != sb.charAt(sb.length()-1)) {
                    sb.append(s.charAt(i));
                }
            }
            return sb.toString();
        };
        Set<String> sortedWordSet = new HashSet<>(wordArr.length);
        Set<String> distinctWordSet = new HashSet<>(wordArr.length);
        for (String word : wordArr) {
            sortedWordSet.add(sortFunc.apply(word));
            distinctWordSet.add(distinctFunc.apply(distinctFunc.apply(word)));
        }
        for (String sol : solution.split(",")) {
            if (sortedWordSet.contains(sortFunc.apply(sol)) || distinctWordSet.contains(distinctFunc.apply(sol))) {
                result.add(sol);
            }
        }
        return result.isEmpty() ? "not found" : String.join(",", result);
    }

    // 获取最大软件版本号，拆分逐个对比
    public String getMaxVersion(String v1, String v2) {
        // 去除前导0
        Function<String,String> delPreZeroFunc = s -> {
            int firstAvailableNumber = 0;
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) != '0') {firstAvailableNumber = i;break;}
            }
            return s.substring(firstAvailableNumber);
        };
        // 比较两个数字版本号
        BiFunction<String,String,Integer> compareFunc = (n1,n2) -> {
            String num1 = delPreZeroFunc.apply(n1);
            String num2 = delPreZeroFunc.apply(n2);
            if (num1.length() > num2.length()) {return 1;}
            else if (num1.length() < num2.length()) {return -1;}
            else {return num1.compareTo(num2);}
        };
        // 从完整版本号提取纯数字版本号和里程碑号
        Function<String,String[]> extractNumFunc = s -> {
            int index = s.indexOf("-");
            if (index == -1) {return new String[]{s, null};}
            return new String[]{s.substring(0, index), s.substring(index)};
        };
        // start
        String[] v1Arr = extractNumFunc.apply(v1);
        String[] v2Arr = extractNumFunc.apply(v2);
        Iterator<String> num1Iter = Arrays.asList(v1Arr[0].split("\\.")).iterator();
        Iterator<String> num2Iter = Arrays.asList(v2Arr[0].split("\\.")).iterator();
        while (num1Iter.hasNext() && num2Iter.hasNext()) {
            String splitV1 = num1Iter.next();
            String splitV2 = num2Iter.next();
            Integer compare = compareFunc.apply(splitV1, splitV2);
            if (compare > 0) {return v1;}
            else if (compare < 0) {return v2;}
        }
        // 增量 > 无增量
        if (num1Iter.hasNext()) {return v1;}
        else if (num2Iter.hasNext()) {return v2;}
        // 数字版本号相同，比较里程碑号
        if (v1Arr[1] == null && v2Arr[1] == null) {return v1;}  // 版本号相同，输出第一个
        if (v1Arr[1] == null) {return v2;}  // v2存在里程碑
        if (v2Arr[1] == null) {return v1;}  // v1存在里程碑
        // 均存在里程碑
        int milestoneCompare = v1Arr[1].compareTo(v2Arr[1]);
        if (milestoneCompare > 0) {return v1;}
        if (milestoneCompare < 0) {return v2;}
        return v1;  // 连里程碑也一样，返回v1
    }

    // 数组的中心位置
    public int getArrayCenter(int[] array) {
        double mulR = 1d, mulL = 1d;
        for (int num : array) {
            mulR *= num;
        }
        for (int i = 0; i < array.length; i++) {
            mulR /= array[i];
            if (i != 0) {mulL *= array[i-1];}
            if (mulL == mulR) {return i;}
        }
        return -1;
    }

    // 组成最大数
    public static String makeUpLargestNumber(String numbers) {
        if (numbers.isEmpty()) {
            return numbers;
        }
        String[] split = numbers.split(",");
        Arrays.sort(split, (o1, o2) -> {
            String o1o2 = o1 + o2;
            String o2o1 = o2 + o1;
            return -o1o2.compareTo(o2o1);
        });
        return String.join("", split);
    }

    public String longestCommonPostfix (String[] strs) {
        BinaryOperator<String> func = (s1, s2) -> {
            StringBuilder res = new StringBuilder();
            int i = s1.length(), j = s2.length();
            while (--i >= 0 && --j >= 0) {
                if (s1.charAt(i) == s2.charAt(j)) {
                    res.append(s1.charAt(i));
                } else {
                    break;
                }
            }
            String result = res.reverse().toString();
            return result;
        };
        String res = Arrays.stream(strs).reduce(func).get();
        return res.isEmpty() ? "@Zero" : res;
    }


    public Integer[] getPath(TreeNode rootNode, int tar) {
        return dfs(new LinkedList<>(), rootNode, tar).toArray(new Integer[0]);
    }

    private List<Integer> dfs(LinkedList<Integer> path, TreeNode cur, int tar) {
        if (cur == null) {
            return Collections.emptyList();
        }
        path.add(cur.val);
        if (cur.val == tar) {
            return path;
        }
        List<Integer> resBuf;
        if (!(resBuf = dfs(path, cur.left, tar)).isEmpty()) {
            return resBuf;
        }
        if (!(resBuf = dfs(path, cur.right, tar)).isEmpty()) {
            return resBuf;
        }
        path.removeLast();
        return Collections.emptyList();
    }

    public static class TreeNode {
        protected TreeNode left;
        protected TreeNode right;
        protected int val;

        public TreeNode(int val) {
            this.val = val;
        }

        public TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }


    public static void main(String[] args) {
        TreeNode node1 = new TreeNode(1);
        TreeNode node8 = new TreeNode(8);
        TreeNode node9 = new TreeNode(9);
        TreeNode node4 = new TreeNode(4);
        TreeNode node5 = new TreeNode(5);
        node1.left = node8;
        node1.right = node9;
        node8.right = node4;
        node9.left = node5;
        System.out.println(Arrays.toString(new MySolution().getPath(node1, 5)));
    }


    // 415. 字符串相加
    public String addStrings(String num1, String num2) {
        int i = num1.length() - 1, j = num2.length() - 1;
        int carry = 0, number1, number2;
        StringBuilder reverseResult = new StringBuilder();
        while (i >= 0 || j >= 0) {
            number1 = i < 0 ? 0 : num1.charAt(i) - '0';
            number2 = j < 0 ? 0 : num2.charAt(i) - '0';
            int sum = number1 + number2 + carry;
            if(sum >= 10) {
                carry = 1;
                sum -= 10;
            } else {
                carry = 0;
            }
            reverseResult.append(sum);
            i--;j--;
        }
        return reverseResult.reverse().toString();
    }

}
