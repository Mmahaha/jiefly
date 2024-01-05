package util;

import linkedlist.LinkedListSolution.ListNode;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static solution.MySolution.TreeNode;

public class JUtils {
    /**
     * 解析一个文本文件为二维数组，格式为lc里的格式，形如[[1,2,3],[2,3,4]]
     * @param filePath  文件路径
     * @return  解析后的int二维数组
     */
    public static int[][] resolveFile(String filePath){
        try (BufferedInputStream in = new BufferedInputStream(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(filePath)))){
            byte[] bytes = new byte[2048000];
            int n = -1;
            if ((n = in.read(bytes, 0, bytes.length)) != -1){
                return resolveString(new String(bytes, 0, n, "GBK"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new int[0][0];
    }


    public static int[][] resolveString(String str){
        List<int[]> result = new ArrayList<>();
        String substring = str.substring(1, str.length() - 1);
        Pattern pattern = Pattern.compile("\\[.*?]");
        Matcher matcher = pattern.matcher(substring);
        while (matcher.find()){
            String res = matcher.group();
            result.add(Arrays.stream(res.substring(1, res.length() - 1).split(",")).mapToInt(Integer::parseInt).toArray());
        }
        return result.toArray(new int[0][0]);
    }

    public static void swap(int[] array, int x, int y) {
        int buf = array[x];
        array[x] = array[y];
        array[y] = buf;
    }

    public static ListNode buildListNode(int[] valArray) {
        ListNode result = null, lastNode = null;
        for (int val : valArray) {
            ListNode node = new ListNode(val);
            result = result == null ? node : result;
            if (lastNode != null) {lastNode.next = node;}
            lastNode = node;
        }
        return result;
    }

    public static int[] nodesToArray(ListNode node) {
        if (node == null) {
            return new int[0];
        }
        LinkedList<Integer> valList = new LinkedList<>();
        while (node != null) {
            valList.add(node.val);
            node = node.next;
        }
        return Stream.of(valList.toArray()).mapToInt(i-> (int) i).toArray();
    }

    public static TreeNode arrayToTreeNode(Integer[] array) {
        if (array == null || array.length == 0 || array[0] == null) {
            return null;
        }

        Queue<TreeNode> queue = new LinkedList<>();
        TreeNode root = new TreeNode(array[0]);
        queue.offer(root);

        int i = 1;
        while (i < array.length) {
            TreeNode current = queue.poll();

            Integer leftValue = array[i++];
            if (leftValue != null) {
                current.left = new TreeNode(leftValue);
                queue.offer(current.left);
            }

            if (i < array.length) {
                Integer rightValue = array[i++];
                if (rightValue != null) {
                    current.right = new TreeNode(rightValue);
                    queue.offer(current.right);
                }
            }
        }

        return root;
    }
}
