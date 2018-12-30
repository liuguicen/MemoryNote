package com.lgc.memorynote.user.setting;

import com.lgc.baselibrary.baseComponent.BaseContract;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/12/30
 *      version : 1.0
 * <pre>
 */

public class SettingContract {
    public interface Presenter{

        void onClickAppGuide();

    }

    public interface View extends BaseContract.BaseView{
        void showAppGuide();
        void hideAppGuide();
    }
}
