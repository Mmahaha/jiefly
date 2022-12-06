import org.junit.Before;
import org.junit.Test;

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
}