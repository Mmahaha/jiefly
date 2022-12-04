package util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JUtils {
    /**
     * 解析一个文本文件为二维数组，格式为lc里的格式，形如[[1,2,3],[2,3,4]]
     * @param filePath  文件路径
     * @return  解析后的int二维数组
     */
    public static int[][] resolveFile(String filePath){
        try (BufferedInputStream in = new BufferedInputStream(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(filePath)))){
            byte[] bytes = new byte[2048000];
            int n = -1;
            if ((n = in.read(bytes, 0, bytes.length)) != -1){
                return resolveString(new String(bytes, 0, n, "GBK"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new int[0][0];
    }


    public static int[][] resolveString(String str){
        List<int[]> result = new ArrayList<>();
        String substring = str.substring(1, str.length() - 1);
        Pattern pattern = Pattern.compile("\\[.*?]");
        Matcher matcher = pattern.matcher(substring);
        while (matcher.find()){
            String res = matcher.group();
            result.add(Arrays.stream(res.substring(1, res.length() - 1).split(",")).mapToInt(Integer::parseInt).toArray());
        }
        return result.toArray(new int[0][0]);
    }
}
