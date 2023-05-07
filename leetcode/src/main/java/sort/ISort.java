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
            int x = array[(l + r) >> 1];
            int i = l - 1;
            int j = r + 1;
            while (true) {
                while (array[--j] > x) ;
                while (array[++i] < x) ;
                if (i < j) {
                    swap(array, i, j);
                } else {
                    return j;
                }
            }
        }
    }

    class MergeSort implements ISort {
        @Override
        public void sort(int[] array) {
            mergeSort(array, 0, array.length - 1);
        }

        public void mergeSort(int[] array, int l, int r) {
            if (l == r) {
                return;
            }
            int m = (r + l) >> 1;
            mergeSort(array, l, m);
            mergeSort(array, m + 1, r);
            merge(array, l, r, m);
        }

        private void merge(int[] array, int l, int r, int m) {
            int p1 = l, p2 = m + 1, i = 0;
            int size = r - l + 1;
            int[] arraySort = new int[size];
            while (p1 <= m && p2 <= r) {
                if (array[p1] < array[p2]) {
                    arraySort[i++] = array[p1++];
                } else {
                    arraySort[i++] = array[p2++];
                }
            }
            while (p1 <= m) {
                arraySort[i++] = array[p1++];
            }
            while (p2 <= r) {
                arraySort[i++] = array[p2++];
            }
            System.arraycopy(arraySort, 0, array, l, size);
        }
    }

    class HeapSort implements ISort {
        @Override
        public void sort(int[] array) {
            int n = array.length;
            for (int i = n /2 - 1; i >= 0; i--) {
                heapify(array, i, n);
            }
            while (--n >= 0) {
                swap(array, 0, n);
                heapify(array, 0, n);
            }
        }

        /**
         * n长度下对i位置下沉处理
         */
        public void heapify(int[] array, int i, int n) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            int max = i;
            if (left < n && array[left] > array[max]) {
                max = left;
            }
            if (right < n && array[right] > array[max]) {
                max = right;
            }
            if (max != i) {
                swap(array, i, max);
                heapify(array, max, n);
            }
        }
    }
}
