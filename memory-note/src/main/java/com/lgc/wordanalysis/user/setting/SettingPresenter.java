package com.lgc.wordanalysis.user.setting;

import android.content.Context;
import android.net.Uri;

import com.lgc.baselibrary.utils.FileUtil;
import com.lgc.baselibrary.utils.Logcat;
import com.lgc.wordanalysis.R;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/12/30
 *      version : 1.0
 * <pre>
 */

public class SettingPresenter implements SettingContract.Presenter {
    SettingContract.View mView;
    boolean isShowAppGuide = false;
    Context mContext;
//    public static final ArrayList<String> FILE_TYPES_SUPPORT = Arrays.asList("txt");


    public SettingPresenter(Context context, SettingContract.View view) {
        mView = view;
        mContext = context;
    }

    @Override
    public void onClickAppGuide() {
        isShowAppGuide = !isShowAppGuide;
        if (isShowAppGuide) {
            mView.showAppGuide();
        } else {
            mView.hideAppGuide();
        }
    }

    @Override
    public void importDataPrepare(Uri uri) {
        Logcat.d("File Uri: " + uri.toString());
        final String importPath = FileUtil.parsePathFromUri(mContext, uri);
        Logcat.d("File Path: " + importPath);
        //首先，判断文件格式是否符合
        if(importPath == null) {
            mView.showToast(mContext.getString(R.string.file_path_error));
            return;
        }
        else {
            int id = importPath.lastIndexOf(".");
            String fileType = "";
            if (id != -1 ) {
                fileType = importPath.substring(id, importPath.length());
            }
            if (!".txt".equalsIgnoreCase(fileType)) {
                mView.showToast(mContext.getString(R.string.file_type_error));
                return;
            }
        }
        mView.showImportDialog(importPath);
    }

    @Override
    public void start() throws Exception {

    }
}
