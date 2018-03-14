package com.lgc.memorynote.data;


import android.widget.Toast;

import com.lgc.memorynote.base.MemoryNoteApplication;

public class Util {
    public static void Toast(String msg) {
        Toast.makeText(MemoryNoteApplication.appContext, "输入不合法", Toast.LENGTH_LONG).show();
    }
}
