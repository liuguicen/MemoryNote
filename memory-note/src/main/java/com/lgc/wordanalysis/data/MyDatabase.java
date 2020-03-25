package com.lgc.wordanalysis.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lgc.wordanalysis.base.AppConfig;
import com.lgc.baselibrary.utils.Logcat;
import com.lgc.wordanalysis.base.WordAnalysisApplication;

import java.io.IOException;
import java.util.List;


/**
 * 一定注意，使用这个类时，使用完了关闭数据库
 */
public class MyDatabase {
    private static MyDatabase myDatabase;
    private static MySQLiteOpenHandler dbHelper;
    private static SQLiteDatabase db;
    public static final boolean isTest = false;


    private MyDatabase(Context context) {
        dbHelper = new MySQLiteOpenHandler(context, AppConfig.getDatabaseVersion());
        db = dbHelper.getWritableDatabase();
    }

    public static MyDatabase getInstance() {
        if (myDatabase == null || dbHelper == null || db == null)
            myDatabase = new MyDatabase(WordAnalysisApplication.appContext);
        return myDatabase;
    }

    /**
     * usedpic(path text primary key,time varchar(20))
     * inert时如果存在就替换，使用replace，不然就会出错，
     * 这样就不需要update了
     */
    public void insertWord(String word, String data) throws IOException {
        String sql = "replace into " + DataBaseConstant.table_word + "(" + DataBaseConstant.word + "," + DataBaseConstant.data + ") values(?,?) ";
        db.execSQL(sql, new Object[]{word, data});
        if (isTest)
            Logcat.d(sql);
    }

    /**
     * 删除
     * usedpic(path text primary key,time varchar(20))
     */
    public void deleteWord(String word) throws IOException {
        String sql = "delete from " + DataBaseConstant.table_word + " where " + DataBaseConstant.word + " = ?";
        Logcat.d(sql);
        db.execSQL(sql, new Object[]{word});
        if (isTest) Logcat.d(sql);
    }

    /**
     * 删除所有，谨慎使用
     */
    public void deleteAllWord() throws IOException {
        String sql = "delete  from " + DataBaseConstant.table_word;
        Logcat.d(sql);
        db.execSQL(sql);
        if (isTest) Logcat.d(sql);
    }

    /**
     * 直接将所有的单词数据读到内存中
     */
    public void queryAllWord(List<String> jasonDateList) throws IOException {
        Cursor cursor = db.rawQuery("select * from " + DataBaseConstant.table_word, new String[]{});
        while (cursor.moveToNext()) {
            String jsonData = cursor.getString(1);
            jasonDateList.add(jsonData);
        }
    }

    public void close() {
        if (db != null) {
            db.close();
            db = null;
        }
        if (dbHelper != null) {
            dbHelper.close();
            dbHelper = null;
        }

    }
}
