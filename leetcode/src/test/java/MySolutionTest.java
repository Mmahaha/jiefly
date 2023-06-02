import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MySolutionTest {

    MySolution solution;

    @Before
    public void beforeClass() {
        solution = new MySolution();
    }

    @Test
    public void wordPuzzle() {
        System.out.println(solution.wordPuzzle("nsew,isld,asldf,qrrw,vzxn", "adssbd,dlsi,vxzn,gwr"));
    }

    @Test
    public void getMaxVersion() {
        assertEquals("2.5.1-C", solution.getMaxVersion("2.5.1-C", "1.4.2-D"));
        assertEquals("1.3.11-S2", solution.getMaxVersion("1.3.11-S2", "1.3.11-S13"));
        assertEquals("1.05.1", solution.getMaxVersion("1.05.1", "1.5.01"));
        assertEquals("1.5.0", solution.getMaxVersion("1.5", "1.5.0"));
        assertEquals("1.5.1-a", solution.getMaxVersion("1.5.1-A", "1.5.1-a"));
    }

    @Test
    public void getArrayCenter() {
        assertEquals(3, solution.getArrayCenter(new int[]{2,5,3,6,5,6}));
        assertEquals(1, solution.getArrayCenter(new int[]{1,2,1}));
        assertEquals(0, solution.getArrayCenter(new int[]{5}));
        assertEquals(2, solution.getArrayCenter(new int[]{5,3,6,1,15}));
        assertEquals(1, solution.getArrayCenter(new int[]{1,3}));
        assertEquals(0, solution.getArrayCenter(new int[]{3,1}));
    }
}