package sort;

import static util.JUtils.swap;

public interface ISort {

    void sort(int[] array);

    class SelectSort implements ISort {
        @Override
        public void sort(int[] array) {
            for (int i = 0; i < array.length; i++) {
                int minValue = array[i];
                int minIndex = i;
                for (int j = i; j < array.length; j++) {
                    if (array[j] < minValue) {
                        minValue = array[j];
                        minIndex = j;
                    }
                }
                swap(array, i, minIndex);
            }
        }
    }

    class InsertSort implements ISort {
        @Override
        public void sort(int[] array) {
            for (int i = 1; i < array.length; i++) {
                for (int j = i; j > 0; j--) {
                    if (array[j] < array[j - 1]) {
                        swap(array, j, j - 1);
                    } else {
                        break;
                    }
                }
            }
        }
    }

    class QuickSort implements ISort {
        @Override
        public void sort(int[] array) {
            quickSort(array, 0, array.length - 1);
        }

        private void quickSort(int[] array, int l, int r) {
            if (l < r) {
                int m = partition(array, l, r);
                quickSort(array, l, m);
                quickSort(array, m + 1, r);
            }
        }

        private int partition(int[] array, int l, int r) {
            int split = array[l];
            int i = l - 1;
            int j = r + 1;
            while (true) {
                while (array[--j] > split) ;
                while (array[++i] < split) ;
                if (i < j) {
                    swap(array, i, j);
                } else {
                    return j;
                }
            }
        }
    }
}
