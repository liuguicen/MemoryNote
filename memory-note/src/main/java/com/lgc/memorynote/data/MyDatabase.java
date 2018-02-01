package com.lgc.memorynote.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import com.lgc.wordnote.base.AppConfig;

import java.io.File;
import java.io.IOException;
import java.util.List;


/**
 * 一定注意，使用这个类时，使用完了关闭数据库
 */
public class MyDatabase {
    private static MyDatabase myDatabase;
    private static MySQLiteOpenHandler dbHelper;
    private static SQLiteDatabase db;


    private MyDatabase(Context context) {
        dbHelper = new MySQLiteOpenHandler(context, AppConfig.getDatabaseVersion());
        db = dbHelper.getWritableDatabase();
    }

    public static MyDatabase getInstance(Context context) {
        if (myDatabase == null || dbHelper == null || db == null)
            myDatabase = new MyDatabase(context);
        return myDatabase;
    }

    /**
     * usedpic(path text primary key,time varchar(20))
     * inert时如果存在就替换，使用replace，不然就会出错，
     * 这样就不需要update了
     */
    public void insertUsedPic(String path, long time) throws IOException {
        db.execSQL("replace into usedpic(path,time) values(?,?) ", new Object[]{path, String.valueOf(time)});
    }

    /**
     * 超出最多图片数时，删除最早添加进去的图片
     * usedpic(path text primary key,time varchar(20))
     */
    public void deleteOdlestUsedPic() throws IOException {
/*        Cursor cursor = db.rawQuery("select path from usedpic where time = ( select min(time) from usedpic ) ", new String[]{});
        cursor.moveToNext();String path = cursor.getString(0);
        return path;
        */
        db.execSQL("delete from usedpic where time = ( select min(time) from usedpic )", new Object[]{});
    }

    /**
     * 删除
     * usedpic(path text primary key,time varchar(20))
     *
     * @param path
     */
    public void deleteUsedPic(String path) throws IOException {
        db.execSQL("delete from usedpic where path = ?", new Object[]{path});
    }

    /**
     * usedpic(path text primary key,time varchar(20))
     *
     * @param path
     * @param time
     */
    public void updateUsedPic(String path, long time) throws IOException {
        insertUsedPic(path, time);
    }

    /**
     * 获取存入数据库的所有使用过得图片
     * usedpic(path text primary key,time varchar(20))
     *
     * @param pathList
     */
    //    有两个返回值，不能直接返回，传入应用获取
    public void queryAllUsedPic(List<String> pathList) throws IOException {
        Cursor cursor = db.rawQuery("select path from usedpic order by time desc ", new String[]{});
        while (cursor.moveToNext()) {
            String path = cursor.getString(0);
            //从数据库读出文件路径时就检测是否已被删除，删除了则不添加，并且从数据库删除，
            if (!(new File(path).exists())) {
                deleteUsedPic(path);
            } else
                pathList.add(path);
        }
    }

    /**
     * 查询图片路径加上时间
     */
    public void queryAllUsedPicWithTime(List<String> pathList) throws IOException {
        Cursor cursor = db.rawQuery("select * from usedpic order by time desc ", new String[]{});
        while (cursor.moveToNext()) {
            String path = cursor.getString(0);
            if (!(new File(path).exists()))
                deleteUsedPic(path);
            else
                pathList.add(path);
        }
    }

    /**
     * usualypic(path text primary key,time varchar(20))
     * inert时如果存在就替换，使用replace，不然就会出错，
     * 这样就不需要update了
     */
    public void insertPreferPic(String path, long time) throws IOException {
        db.execSQL("replace into usualypic(path,time) values(?,?) ", new Object[]{path, String.valueOf(time)});
    }

    /**
     * 删除一张喜爱的图片
     *
     * @param path 路径
     * @throws IOException
     * @see MySQLiteOpenHandler usualypic(path text primary key,time varchar(20))
     */
    public void deletePreferPicPath(String path) throws IOException {
        db.execSQL("delete from usualypic where path = ?", new Object[]{path});
    }


    /**
     * 获取存入数据库的所有的选择出常用的得图片
     * usualypic(path text primary key,time varchar(20))
     *
     * @param pathList
     */
    //    有两个返回值，不能直接返回，传入应用获取
    public void queryAllPreferPic(List<String> pathList) throws IOException {
        Cursor cursor = db.rawQuery("select path from usualypic order by time desc ", new String[]{});
        while (cursor.moveToNext()) {
            String path = cursor.getString(0);
            if (!(new File(path).exists()))
                deletePreferPicPath(path);
            else
                pathList.add(path);
        }
    }

    /**
     * 获取所有的优先分享的ac的title
     * "create table  IF NOT EXISTS prefer_share(title text primary key,time varchar(50))"
     * 按时间倒序，即越前面优先级越高
     */
    //    有两个返回值，不能直接返回，传入应用获取
    public void queryAllPreferShare(List<Pair<String, String>> titleList) throws IOException {
        Cursor cursor = db.rawQuery("select packageName,title from prefer_share order by time desc ", new String[]{});
        while (cursor.moveToNext()) {
            String packageName = cursor.getString(0);
            String title = cursor.getString(1);
            titleList.add(new Pair<>(packageName, title));
        }
    }


    /**
     * db.execSQL("create table  IF NOT EXISTS prefer_share(packageName text,title text,time varchar(50))");
     * inert时如果存在就替换，使用replace，不然就会出错，
     * 这样就不需要update了
     */
    public void insertPreferShare(String packageName, String title, long time) throws IOException {
    /*    Cursor cursor = db.rawQuery("select * from sqlite_master where type= ? and name= ? ",new String[]{"table","prefer_share"});
        while (cursor.moveToNext()) {
            cursor.moveToNext();
        }*/
        db.execSQL("replace into prefer_share(packageName,title,time) values(?,?,?) ", new Object[]{packageName, title, String.valueOf(time)});
    }

    /**
     * db.execSQL("create table  IF NOT EXISTS prefer_share(packageName text,title text,time varchar(50))");
     *
     * @param packageName ac的包名     * @param title ac的title
     */
    public void deletePreferShare(String packageName, String title) throws IOException {
        db.execSQL("delete from prefer_share where packageName = ? and title = ? ", new Object[]{packageName, title});
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
