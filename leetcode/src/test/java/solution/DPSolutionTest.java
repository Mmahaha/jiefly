package solution;

import org.junit.Before;
import org.junit.Test;
import solution.DPSolution;
import util.JUtils;

import java.util.Arrays;

import static org.junit.Assert.*;

public class DPSolutionTest {

    DPSolution solution;

    @Before
    public void beforeClass() {
        solution = new DPSolution();
    }

    @Test
    public void uniquePaths() {
        assertEquals(28, solution.uniquePaths(3, 7));
        assertEquals(3, solution.uniquePaths(3, 2));
        assertEquals(6, solution.uniquePaths(3, 3));
    }

    @Test
    public void uniquePathsWithObstacles() {
        assertEquals(2, solution.uniquePathsWithObstacles(new int[][]{{0, 0, 0}, {0, 1, 0}, {0, 0, 0}}));
        assertEquals(1, solution.uniquePathsWithObstacles(new int[][]{{0, 1}, {0, 0}}));
    }

    @Test
    public void minPathSum() {
        assertEquals(7, solution.minPathSum(new int[][]{{1, 3, 1}, {1, 5, 1}, {4, 2, 1}}));
        assertEquals(12, solution.minPathSum(new int[][]{{1, 2, 3}, {4, 5, 6}}));
    }

    @Test
    public void minimumTotal() {
        assertEquals(11, solution.minimumTotal(Arrays.asList(Arrays.asList(2), Arrays.asList(3, 4), Arrays.asList(6, 5, 7), Arrays.asList(4, 1, 8, 3))));
        assertEquals(-10, solution.minimumTotal(Arrays.asList(Arrays.asList(-10))));
        assertEquals(4, solution.minimumTotal(Arrays.asList(Arrays.asList(1), Arrays.asList(3, 5))));
    }

    @Test
    public void minFallingPathSum() {
        assertEquals(-59, solution.minFallingPathSum(new int[][]{{-19, 57}, {-40, -5}}));
    }

    @Test
    public void minFallingPathSum2() {
        assertEquals(13, solution.minFallingPathSum2(JUtils.resolveString("[[1,2,3],[4,5,6],[7,8,9]]")));
        assertEquals(7, solution.minFallingPathSum2(JUtils.resolveString("[[7]]")));
        assertEquals(200, solution.minFallingPathSum2(JUtils.resolveFile("1289case1.txt")));
    }
}