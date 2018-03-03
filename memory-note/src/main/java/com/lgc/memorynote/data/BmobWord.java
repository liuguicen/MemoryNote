package com.lgc.memorynote.data;

import android.util.Log;

import com.google.gson.Gson;
import com.lgc.memorynote.base.network.NetWorkUtil;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/02/19
 *      version : 1.0
 * <pre>
 */

public class BmobWord extends BmobObject {
    String name;
    String jsonData;
    String userID;

    public BmobWord(Word word) {
        this.name = word.getName();
        jsonData = new Gson().toJson(word);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public String getJsonData() {
        return jsonData;
    }

    public Word toWord() {
        return new Gson().fromJson(jsonData, Word.class);
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public void selfUpdate(String objectId, final NetWorkUtil.UploadListener uploadListener) {
        update(objectId, new UpdateListener() {

            @Override
            public void done(BmobException innerE) {
                if (innerE == null) {
                    if (uploadListener != null)
                        uploadListener.uploadSuccess();
                } else {
                    Log.e("bmob", "更新失败：" + innerE.getMessage() + "," + innerE.getErrorCode());
                    if (uploadListener != null)
                        uploadListener.uploadFailed(innerE);
                }
            }
        });
    }

    public void selfSave(final NetWorkUtil.UploadListener uploadListener) {
        save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (e == null) {
                    if (uploadListener != null)
                        uploadListener.uploadSuccess();
                } else {
                    Log.d("bmob", "服务器保存失败：" + e.getMessage() + "," + e.getErrorCode());
                    if (uploadListener != null)
                        uploadListener.uploadFailed(e);
                }
            }
        });
    }

    public void selfDelete(final NetWorkUtil.UploadListener uploadListener) {
        delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    if (uploadListener != null)
                        uploadListener.uploadSuccess();
                } else {
                    Log.d("bmob", "服务器删除失败：" + e.getMessage() + "," + e.getErrorCode());
                    if (uploadListener != null)
                        uploadListener.uploadFailed(e);
                }
            }
        });

    }
}