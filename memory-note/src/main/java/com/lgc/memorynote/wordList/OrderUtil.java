package com.lgc.memorynote.wordList;

import com.lgc.memorynote.wordDetail.Word;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/02/01
 *      version : 1.0
 * <pre>
 */

public class OrderUtil {
    /**
     * 各种排序方式,会用startWith比较，所以一个命令不能是另一个的前面部分
     */
    public static final String STRANGE_DEGREE = "1_strange";
    public static final String LAST_REMEMBER_TIME = "2_last_remember";
    public static final String SIMILAR_NUMBER = "3_similar_number";
    public static final String DICTIONARY = "4_dictionary";
    public static final String LENGTH = "5_length";
    public static final String MEANING_NUMBER = "6_meaning_number";


    public static final String DEFAULT_ORDER_COMMAND = STRANGE_DEGREE;

    public static String getHingString() {
        return "请输入排序方式：" + STRANGE_DEGREE + "|" +  LAST_REMEMBER_TIME + "|" +
                SIMILAR_NUMBER + "|" +DICTIONARY + "|" + LENGTH  + "|" + MEANING_NUMBER;
    }

    public static List<String> parseOrderCommand(String command) {
        if (command == null) return null;
        command = command.trim();
        if (command.isEmpty()) return null;
        List<String> orderList = new ArrayList<>();
        String[] perhapsOrder = command.split("|");
        for (String order : perhapsOrder) {
            order = order.trim();
            orderList.add(order);
        }
        return orderList;
    }

    public static Comparator<Word> getComparator(List<String> orderList) {
        if (orderList == null) {
            orderList = new ArrayList<>();
        }
        if (orderList.isEmpty()) {
            orderList.add(DEFAULT_ORDER_COMMAND);
        }

        final List<String> finalOrderList = orderList;
        Comparator<Word> comparator = new Comparator<Word>() {
            @Override
            public int compare(Word o1, Word o2) {
                if (o1 == null && o2 == null) return 0;
                if (o1 == null) return 1;
                if (o2 == null) return -1;

                /*从前面往后一直遍历命令，先遍历到的就以这个命令的方式比较，
                 * 如果这种方式比较结果相等，那么继续遍历下一种方式
                 */
                int re = 0;
                for(String order : finalOrderList) {
                    if(STRANGE_DEGREE.contains(order)) {
                         re = Word.compareStrangeDegree(o1.getStrangeDegree(), o2.getStrangeDegree());
                    }  else if (LAST_REMEMBER_TIME.contains(order)) {
                        re = Word.compareRememberTime(o1.getLastRememberTime(), o2.getLastRememberTime());
                    } else if (SIMILAR_NUMBER.contains(order)) {
                        re = Word.compareSimilarNumber(o1.getSimilarWordList(), o2.getSimilarWordList());
                    } else if (DICTIONARY.contains(order)) {
                        re = Word.compareDictionary(o1.getWord(), o2.getWord());
                    } else if (LENGTH.contains(order)) {
                        re = Word.compareLength(o1.getWord(), o2.getWord());
                    }
                    if (re != 0) return re;
                }
                return re;
            }
        };
        return comparator;
    }
}
