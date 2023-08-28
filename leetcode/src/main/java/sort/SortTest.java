package sort;

import java.util.Random;

public class SortTest {
    public static void main(String[] args) {
        for (int i = 0; i < 500; i++) {
            int[] array = generateArray();
            long start = System.currentTimeMillis();
            new ISort.QuickSort().sort(array);
//            Arrays.sort(array);
            System.out.println(String.format("cost %sms", System.currentTimeMillis() - start));
            check(array);
        }
    }

    static int[] generateArray() {
        int LENGTH = 1000_0000;
        int[] result = new int[LENGTH];
        for (int i = 0; i < LENGTH; i++) {
            result[i] = new Random().nextInt(999999999);
        }
        return result;
    }

    static void check(int[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] > array[i + 1]) {
                throw new IllegalStateException("not asc");
            }
        }
        System.out.println("check success");
    }
}
