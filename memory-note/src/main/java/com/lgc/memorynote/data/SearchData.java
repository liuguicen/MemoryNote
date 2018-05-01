package com.lgc.memorynote.data;

import java.util.ArrayList;
import java.util.List;

/**
 * 一次搜索对应的数据结构
 */
public class SearchData {
    public String inputCmd;
    public List<String> cmdList = new ArrayList<>();
    public int position;

    public SearchData(String inputCmd, List<String> cmdList, int position) {
        this.inputCmd = inputCmd;
        this.cmdList.addAll(cmdList);
    }

    public SearchData() {
    }
}
