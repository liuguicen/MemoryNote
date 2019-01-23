package com.lgc.wordanalysis.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/04/14
 *      version : 1.0
 * <pre>
 */

public class AlgorithmUtil {

    public static class StringAg {

        public static final double EQUAL_SIMILAR = 1;
        public static final double START_SIMILAR = 0.6;
        public static final double CONTAIN_SIMILAR = 0.3;
        public static final double NOT_SIMILAR = -0;

        /**
         * 比较字符串的相似度，src为主串
         *
         * @param src
         * @param target
         * @return
         */
        public static double stringSimilar(String src, String target) {
            if (src == null || target == null) return NOT_SIMILAR;
            if (src.equals(target)) return EQUAL_SIMILAR;
            if (src.startsWith(target)) return START_SIMILAR;
            if (src.contains(target)) return CONTAIN_SIMILAR;
            return NOT_SIMILAR;
        }

        /**
         * 获取中文的词
         *
         * @return
         */
        public static List<String> splitChineseWord(String msg) {
            List<String> chineseWordList = new ArrayList<>();
            if (msg != null && !msg.isEmpty()) {
                String[] split = msg.split("[ ,，；;.。、()=\\n]");
                for (String s : split) {
                    s=s.trim();
                    if (!s.isEmpty()) {
                        if (s.charAt(0) <= 126)
                            continue;
                        chineseWordList.add(s);
                    }
                }
            }
            return chineseWordList;
        }

        /**
         * 某些对象不能使用内部数据，需要额外的Key进行排序，使用这个
         */
        public static class KeyValueSorter<K extends Comparable, V> {
            private class KeyValue {
                K key;
                V value;

                KeyValue(K key, V value) {
                    this.key = key;
                    this.value = value;
                }
            }

            private class KeyValueComparator implements Comparator<KeyValue> {
                @Override
                public int compare(KeyValue o1, KeyValue o2) {
                    return o1.key.compareTo(o2.key);
                }
            }

            ArrayList<KeyValue> keyWordList = new ArrayList<>();

            public void add(K key, V value) {
                keyWordList.add(new KeyValue(key, value));
            }

            public List<V> sort() {
                return sort(new ArrayList<V>(), false);
            }

            public List<V> sort(List<V> valueList) {
                return sort(valueList, false);
            }

            /**
             * 排序并返回排序后的数组
             */
            public List<V> sort(List<V> valueList, boolean isReverse) {
                if (valueList == null) throw new NullPointerException();
                if (!isReverse) {
                    Collections.sort(keyWordList, new KeyValueComparator());
                } else {
                    Collections.sort(keyWordList, Collections.reverseOrder(new KeyValueComparator()));
                }
                for (KeyValue keyValue : keyWordList) {
                    valueList.add(keyValue.value);
                }
                keyWordList = null;
                return valueList;
            }
        }
    }
}
