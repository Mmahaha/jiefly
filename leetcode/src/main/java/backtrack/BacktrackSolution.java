package backtrack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BacktrackSolution {

    // 77. 组合
    public List<List<Integer>> combine(int n, int k) {
        LinkedList<Integer> path = new LinkedList<>();
        List<List<Integer>> result = new ArrayList<>();
        backtrack(path, result, n, k, 1);
        return result;
    }

    private void backtrack(LinkedList<Integer> path, List<List<Integer>> result, int n, int k,  int startIndex) {
        if ((k - path.size()) > (n - startIndex + 1)) {
            return;
        }
        if (path.size() == k) {
            result.add(new ArrayList<>(path));
            return;
        }
        for (int i = startIndex; i <= n; i++) {
            path.addLast(i);
            backtrack(path, result, n, k, i+1);
            path.removeLast();
        }
    }
}
