package com.example.fragment;

public class QuickSortUtil {
    public static <T extends Comparable<T>> T min(T t1, T t2) {
        if (t1.compareTo(t2) > 0)
            return t2;
        else
            return t1;
    }

    public interface minMethod<T> {
        public T min(T t1, T t2);
    }

    // 快速排序
    public static <T extends Comparable<T>> void sort(T v[], int left, int right) {

        if (left < right) {
            T key = v[left];
            int low = left;
            int high = right;
            while (low < high) {
                while (low < high && min(v[high], key) == key) {
                    high--;
                }
                v[low] = v[high];
                while (low < high && min(v[low], key) == v[low]) {
                    low++;
                }
                v[high] = v[low];
            }
            v[low] = key;
            sort(v, left, low - 1);
            sort(v, low + 1, right);
        }
    }

    // 快速排序
    public static <T> void sort(T v[], int left, int right, minMethod<T> method) {

        if (left < right) {
            T key = v[left];
            int low = left;
            int high = right;
            while (low < high) {
                while (low < high && method.min(v[high], key) == key) {
                    high--;
                }
                v[low] = v[high];
                while (low < high && method.min(v[low], key) == v[low]) {
                    low++;
                }
                v[high] = v[low];
            }
            v[low] = key;
            sort(v, left, low - 1, method);
            sort(v, low + 1, right, method);
        }
    }
}
