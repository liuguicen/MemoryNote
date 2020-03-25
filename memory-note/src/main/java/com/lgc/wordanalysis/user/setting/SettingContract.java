package com.lgc.wordanalysis.user.setting;

import android.net.Uri;

import com.lgc.baselibrary.baseComponent.BaseContract;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/12/30
 *      version : 1.0
 * <pre>
 */

public class SettingContract {
    public interface Presenter extends BaseContract.BasePresenter{

        void onClickAppGuide();

        void importDataPrepare(Uri uri);
    }

    public interface View extends BaseContract.BaseView{
        void showAppGuide();
        void hideAppGuide();

        void showToast(String string);

        void importWord_firstStep(String importPath, final String importStream);
    }
}
