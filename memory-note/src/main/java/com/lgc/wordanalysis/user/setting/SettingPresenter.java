package com.lgc.wordanalysis.user.setting;

import android.content.Context;
import android.net.Uri;

import com.lgc.baselibrary.utils.Logcat;
import com.lgc.baselibrary.utils.SimpleObserver;
import com.lgc.wordanalysis.R;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


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
        Observable
                .create((ObservableOnSubscribe<String>) emitter -> {

                        final String importPath = "/storage/emulated/0/tencent/QQfile_recv/MemoryData_20190903162616.txt";
                        Logcat.d("File Path: " + importPath);
                        //首先，判断文件格式是否符合
                        if (importPath == null) {
                            emitter.onError(
                                    new Exception(
                                            mContext.getString(R.string.file_path_error)));
                            return;
                        } else {
                            int id = importPath.lastIndexOf(".");
                            String fileType = "";
                            if (id != -1) {
                                fileType = importPath.substring(id, importPath.length());
                            }
                            if (!".txt".equalsIgnoreCase(fileType)) {
                                emitter.onError(
                                        new Exception(mContext.getString(R.string.file_type_error)));
                                return;
                            }
                            emitter.onNext(importPath);
                        }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SimpleObserver<String>() {

                            @Override
                            public void onComplete() {

                            }

                            @Override
                            public void onError(Throwable throwable) {
                                throwable.printStackTrace();
                            }

                            @Override
                            public void onNext(String importPath) {
                                mView.importWord_firstStep(importPath, null);
                            }
                        });
        // final String importPath = FileUtil.getFileFromUri(uri,mContext);

    }

    @Override
    public void start() throws Exception {

    }
}
