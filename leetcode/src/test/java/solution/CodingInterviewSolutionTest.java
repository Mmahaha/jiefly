package solution;

import org.junit.Before;
import org.junit.Test;
import solution.CodingInterviewSolution;
import util.JUtils;

import static org.junit.Assert.*;

public class CodingInterviewSolutionTest {

    CodingInterviewSolution solution;

    @Before
    public void beforeClass(){
        solution = new CodingInterviewSolution();
    }

    @Test
    public void findRepeatNumber() {
        assertEquals(2, solution.findRepeatNumber(new int[]{2, 3, 1, 0, 2, 5, 3}));
    }

    @Test
    public void findNumberIn2DArray() {
        assertTrue(solution.findNumberIn2DArray(JUtils.resolveString("[[1,4,7,11,15],[2,5,8,12,19],[3,6,9,16,22],[10,13,14,17,24],[18,21,23,26,30]]"), 5));
        assertFalse(solution.findNumberIn2DArray(JUtils.resolveString("[[1,4,7,11,15],[2,5,8,12,19],[3,6,9,16,22],[10,13,14,17,24],[18,21,23,26,30]]"), 20));
    }

    @Test
    public void replaceSpace() {
        assertEquals("We%20are%20happy.", solution.replaceSpace("We are happy."));
    }
}