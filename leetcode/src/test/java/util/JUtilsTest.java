package util;

import org.junit.Test;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.LockSupport;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JUtilsTest {

    @Test
    public void resolveFile() {
        System.out.println(JUtils.resolveFile("/Users/ysj/Documents/leetcode相关/1289case1.txt").length);
    }


    @Test
    public void resolveString() {
        System.out.println(Arrays.toString(JUtils.resolveString("[[1,2,3],[2,3,4]]")));
    }

    public static void main(String[] args) {
        String input = "asdlimit 50abc offset 20";
        String pattern = "limit\\s*(\\d+)";

        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(input);
        if (matcher.find()) {
            String limitValue = matcher.group(1);
            int limit = Integer.parseInt(limitValue);
            System.out.println("Extracted limit: " + limit);
        } else {
            System.out.println("No limit found in the input string.");
        }
    }
}