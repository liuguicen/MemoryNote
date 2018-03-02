package com.lgc.memorynote.wordList;

import com.lgc.memorynote.R;
import com.lgc.memorynote.base.MemoryNoteApplication;
import com.lgc.memorynote.data.Word;

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

public class SortUtil {

    public static final String DEFAULT_SORT_COMMAND = Command._stra;

    public static String getHingString() {
        return MemoryNoteApplication.appContext.getString(R.string.caommand_hint);
    }

    @Deprecated
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

    public static Comparator<Word> getComparator(List<String> commandList) {
        if (commandList == null) {
            commandList = new ArrayList<>();
        }
        return new WordComparator(commandList);
    }

    public static class WordComparator implements Comparator<Word> {
        /**
         * 排序命令的执行顺序
         */
        final List<Integer> sortList = new ArrayList<>();
        private final int _stra = 1;
        private final int _last = 2;
        private final int _sn = 3;
        private final int _dict = 4;
        private final int _len = 5;

        public int sortString2int(String sort) {
            if (Command._stra.equals(sort)) {
                return _stra;
            } else if (Command._last.equals(sort)) {
                return _last;
            } else if (Command._sn.equals(sort)) {
                return _sn;
            } else if (Command._dict.equals(sort)) {
                return _dict;
            } else if (Command._len.contains(sort)) {
                return _len;
            }
            return -1;
        }

        public WordComparator(List<String> finalOrderList) {
            // 找出需要比较的顺序
            for (String order : finalOrderList) {
                if (Command._stra.equals(order)) {
                    sortList.add(_stra);
                } else if (Command._last.equals(order)) {
                    sortList.add(_last);
                } else if (Command._sn.equals(order)) {
                    sortList.add(_sn);
                } else if (Command._dict.equals(order)) {
                    sortList.add(_dict);
                } else if (Command._len.equals(order)) {
                    sortList.add(_len);
                }
            }
        }

        @Override
        public int compare(Word o1, Word o2) {
            if (o1 == o2) return 0;
            if (o1 == null) return 1;
            if (o2 == null) return -1;

            if (sortList.size() == 0) { // 默认的，按不熟悉度比较
                return Word.compareStrangeDegree(o1.getStrangeDegree(), o2.getStrangeDegree());
            }

            /*
             * 从前面往后一直遍历命令，先遍历到的就以这个命令的方式比较，
             * 如果这种方式比较结果相等，那么继续遍历下一种方式
             */
            int re = 0;
            for (int sort : sortList) {
                switch (sort) {
                    case _stra:
                        re = Word.compareStrangeDegree(o1.getStrangeDegree(), o2.getStrangeDegree());
                        break;
                    case _last:
                        re = Word.compareRememberTime(o1.getLastRememberTime(), o2.getLastRememberTime());
                        break;
                    case _sn:
                        re = Word.compareSimilarNumber(o1.getSimilarWordList(), o2.getSimilarWordList());
                        break;
                    case _dict:
                        re = Word.compareDictionary(o1.getName(), o2.getName());
                        break;
                    case _len:
                        re = Word.compareLength(o1.getName(), o2.getName());
                        break;
                }
                if (re != 0) return re;
            }
            return re;
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    }
}
