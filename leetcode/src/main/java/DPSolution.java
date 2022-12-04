import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 动态规划专题
 */
public class DPSolution {
    // 62. 不同路径
    public int uniquePaths(int m, int n) {
        int[][] dp = new int[m][n];
        for (int i = 0; i < m; i++) {
            dp[i][0] = 1;
        }
        for (int i = 0; i < n; i++) {
            dp[0][i] = 1;
        }
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                dp[i][j] = dp[i][j - 1] + dp[i - 1][j];
            }
        }
        return dp[m - 1][n - 1];
    }

    // 63. 不同路径 II
    public int uniquePathsWithObstacles(int[][] obstacleGrid) {
        int m = obstacleGrid.length;
        int n = obstacleGrid[0].length;
        int[][] dp = new int[m][n];
        dp[0][0] = 1;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (obstacleGrid[i][j] == 1) {
                    dp[i][j] = 0;
                } else if (i > 0 && j > 0) {
                    dp[i][j] = dp[i][j - 1] + dp[i - 1][j];
                } else if (i > 0) {
                    dp[i][j] = dp[i - 1][0];
                } else if (j > 0) {
                    dp[i][j] = dp[i][j - 1];
                }
            }
        }
        return dp[m - 1][n - 1];
    }

    // 64. 最小路径和
    public int minPathSum(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        // dp定义的是走到[i][j]格子的最小距离
        int[][] dp = new int[m][n];
        dp[0][0] = grid[0][0];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (i > 0 && j > 0) {
                    dp[i][j] = Math.min(dp[i][j - 1], dp[i - 1][j]) + grid[i][j];
                } else if (i > 0) {
                    dp[i][j] = dp[i - 1][j] + grid[i][j];
                } else if (j > 0) {
                    dp[i][j] = dp[i][j - 1] + grid[i][j];
                }
            }
        }
        return dp[m - 1][n - 1];
    }

    // 120. 三角形最小路径和
    public int minimumTotal(List<List<Integer>> triangle) {
        int m = triangle.size();
        int result = Integer.MAX_VALUE;
        // dp定义的是走到i,j的最小路径和，这里用滚动数组
        int[][] dp = new int[2][m];
        dp[0][0] = triangle.get(0).get(0);
        if (triangle.size() == 1) {
            return dp[0][0];
        }
        for (int i = 1; i < m; i++) {
            for (int j = 0; j <= i; j++) {
                if (j == 0) {
                    dp[i & 1][j] = dp[(i - 1) & 1][j] + triangle.get(i).get(j);
                } else if (j == i) {
                    dp[i & 1][j] = dp[(i - 1) & 1][j - 1] + triangle.get(i).get(j);
                } else {
                    dp[i & 1][j] = Math.min(dp[(i - 1) & 1][j], dp[(i - 1) & 1][j - 1]) + triangle.get(i).get(j);
                }
                if (i == m - 1) {
                    result = Math.min(result, dp[i & 1][j]);
                }
            }
        }
        return result;
    }

    // 931. 下降路径最小和
    public int minFallingPathSum(int[][] matrix) {
        int m = matrix.length;
        int[][] dp = new int[2][m];
        dp[0][0] = matrix[0][0];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                if (i == 0) {
                    dp[i & 1][j] = matrix[i][j];
                } else if (j == 0) {
                    dp[i & 1][j] = Math.min(dp[(i - 1) & 1][j], dp[(i - 1) & 1][j + 1]) + matrix[i][j];
                } else if (j == m - 1) {
                    dp[i & 1][j] = Math.min(dp[(i - 1) & 1][j], dp[(i - 1) & 1][j - 1]) + matrix[i][j];
                } else {
                    dp[i & 1][j] = Math.min(Math.min(dp[(i - 1) & 1][j], dp[(i - 1) & 1][j + 1]), dp[(i - 1) & 1][j - 1]) + matrix[i][j];
                }
            }
        }
        int result = Integer.MAX_VALUE;
        for (int i : dp[(m - 1) & 1]) {
            result = Math.min(result, i);
        }
        return result;
    }

    // 1289. 下降路径最小和 II
    public int minFallingPathSum2(int[][] grid) {
        // 存储上一行和当前行的第一小值和第二小值
        int lastMin1, lastMin2, curMin1 = Integer.MAX_VALUE, curMin2 = Integer.MAX_VALUE;
        int m = grid.length;
        int[][] dp = new int[m][m];
        for (int i = 0; i < m; i++) {
            lastMin1 = curMin1; lastMin2 = curMin2;
            curMin1 = Integer.MAX_VALUE; curMin2 = Integer.MAX_VALUE;
            for (int j = 0; j < m; j++) {
                if (i == 0){
                    dp[i][j] = grid[i][j];
                } else {
                    dp[i][j] = (dp[i-1][j] == lastMin1 ? lastMin2 : lastMin1) + grid[i][j];
                }
                if (dp[i][j] < curMin1){
                    curMin2 = curMin1;
                    curMin1 = dp[i][j];
                } else if (dp[i][j] < curMin2) {
                    curMin2 = dp[i][j];
                }
            }
        }
        int result = Integer.MAX_VALUE;
        for (int i : dp[m - 1]) {
            result = Math.min(result, i);
        }
        return result;
    }
}
