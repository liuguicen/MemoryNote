package com.lgc.wordanalysis.data;


import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/04/14
 *      version : 1.0
 * <pre>
 */
public class WordWithComparator implements Comparable, IWord {
    private Word mWord;
    private List<Comparable> comparatorList = new ArrayList<>();

    public WordWithComparator(Word word) {
        mWord = word;
    }


    /**
     * 必须保证用来比较的所有单词所加的比较器的顺序完全一样
     */
    public void addComparator(@NonNull Comparable comparator) {
        if (comparator == null) throw new NumberFormatException("can not be null");
        comparatorList.add(comparator);
    }

    @Override
    public int compareTo(@NonNull Object o) {
        if (o instanceof WordWithComparator) {
            List<Comparable> bComList = ((WordWithComparator) o).getComparatorList();
            int aLen = comparatorList.size();
            int bLen = bComList.size();
            int minLen = Math.min(aLen, bLen);
            for (int i = 0; i < minLen; i++) {
                Comparable oneA = comparatorList.get(i);
                Comparable oneB = bComList.get(i);
                if (oneA.getClass() == oneB.getClass()) {
                    int res = oneA.compareTo(oneB);
                    if (res != 0) {
                        return res;
                    }
                }
            }
            return aLen - bLen;
        }
        return 0;
    }

    public Word getWord() {
        return mWord;
    }

    public List<Comparable> getComparatorList() {
        return comparatorList;
    }

    @Override
    public String getName() {
        return mWord.getName();
    }

    @Override
    public boolean hasTag(String tag) {
        return mWord.hasTag(tag);
    }

    @Override
    public int getStrangeDegree() {
        return mWord.getStrangeDegree();
    }

    @Override
    public long getLastRememberTime() {
        return mWord.getLastRememberTime();
    }

    @Override
    public List<Word.SimilarWord> getSimilarWordList() {
        return mWord.getSimilarWordList();
    }
}
