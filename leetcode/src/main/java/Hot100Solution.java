import java.util.*;

public class Hot100Solution {

    // 146.LRU缓存
    class LRUCache {
        private final Map<Integer, Node> storeMap;
        private final int capacity;
        private Node head;
        private Node tail;
        public LRUCache(int capacity) {
            this.capacity = capacity;
            storeMap = new HashMap<>(capacity);
            head = new Node(null, null, -1, -1);
            tail = new Node(head, null, -1, -1);
            head.next = tail;
        }

        public int get(int key) {
            Node node = storeMap.get(key);
            if (node == null) {
                return -1;
            }
            // moveToHead
            moveToHead(node);
            return node.val;
        }

        public void put(int key, int value) {
            if (storeMap.containsKey(key)) {
                Node node = storeMap.get(key);
                node.val = value;
                moveToHead(node);
                return;
            }
            if (storeMap.size() < capacity) {
                createHeadNode(key, value);
                return;
            }
            // deleteTail && removeKey
            Node last = tail.prev;
            last.prev.next = tail;
            tail.prev = last.prev;
            storeMap.remove(last.key);
            createHeadNode(key, value);
        }

        private void moveToHead(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            node.prev = head;
            node.next = head.next;
            head.next.prev = node;
            head.next = node;
        }

        private void createHeadNode(int key, int value) {
            Node newNode = new Node(head, head.next, key, value);
            storeMap.put(key, newNode);
            head.next.prev = newNode;
            head.next = newNode;
        }
    }
    private static class Node {
        Node prev;
        Node next;
        Integer key;
        Integer val;

        public Node(Node prev, Node next, Integer key, Integer val) {
            this.prev = prev;
            this.next = next;
            this.key = key;
            this.val = val;
        }
    }

    // 15. 三数之和
    public List<List<Integer>> threeSum(int[] nums) {
        Set<List<Integer>> result = new HashSet<>();
        Arrays.sort(nums);
        for (int i = 0; i < nums.length - 2; i++) {
            int j = i + 1, k = nums.length - 1;
            while (j < k) {
                int sum = nums[i] + nums[j] + nums[k];
                if (sum == 0) {
                    List<Integer> list = Arrays.asList(nums[i], nums[j], nums[k]);
                    list.sort(Integer::compareTo);
                    result.add(list);
                    j++;
                } else if (sum < 0) {
                    j++;
                } else {
                    k--;
                }
            }
        }
        return new ArrayList<>(result);
    }

    // 11、盛最多水的容器
    public int maxArea(int[] height) {
        int result = -1, i = 0, j = height.length - 1;
        while (i < j) {
            result = Math.max(result, (j-i) * Math.min(height[j],height[i]));
            if (height[i] < height[j]) {
                i++;
            } else {
                j--;
            }
        }
        return result;
    }

    // 17. 电话号码的字母组合
    public List<String> letterCombinations(String digits) {
        Map<Character,List<String>> map = new HashMap<>();
        return null;
    }

}
