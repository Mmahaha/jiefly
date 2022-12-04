package util;

import org.junit.Test;

import java.util.Arrays;
import java.util.Objects;

public class JUtilsTest {

    @Test
    public void resolveFile() {
        System.out.println(JUtils.resolveFile("/Users/ysj/Documents/leetcode相关/1289case1.txt").length);
    }


    @Test
    public void resolveString() {
        System.out.println(Arrays.toString(JUtils.resolveString("[[1,2,3],[2,3,4]]")));
    }
}