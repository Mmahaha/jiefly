package backtrack;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BacktrackSolutionTest {

    private BacktrackSolution solution;

    @Before
    public void before() {
        solution = new BacktrackSolution();
    }

    @Test
    public void combine() {
        System.out.println(solution.combine(10, 4).size());
    }
}