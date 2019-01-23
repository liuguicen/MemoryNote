package com.lgc.wordanalysis.data;

import java.util.ArrayList;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/05/01
 *      version : 1.0
 * <pre>
 */

public class CustomList<T> extends ArrayList{
    public void removeEnd() {
        if (size() > 1) {
            remove(size() - 1);
        }
    }

    public void removeFirst() {
        if (size() > 0) {
            remove(0);
        }
    }


}
