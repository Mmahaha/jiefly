import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Hot100SolutionTest {

    private Hot100Solution solution;

    @Before
    public void before() {
        solution = new Hot100Solution();
    }

    @Test
    public void testLRU() {
        Hot100Solution.LRUCache lruCache = solution.new LRUCache(2);
        System.out.println(lruCache.get(2));
        lruCache.put(2, 6);
    }

    @Test
    public void threeSum() {
        System.out.println(solution.threeSum(new int[]{-1, 0, 1, 2, -1, -4}));
    }

    @Test
    public void myTest() {
        System.out.println("beta-3".compareTo("beta-11"));
    }

    @Test
    public void maxArea() {
        assertEquals(49, solution.maxArea(new int[]{1,8,6,2,5,4,8,3,7}));
        assertEquals(1, solution.maxArea(new int[]{1,1}));
    }
}