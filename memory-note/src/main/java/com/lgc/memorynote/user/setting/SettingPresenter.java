package com.lgc.memorynote.user.setting;

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

    public SettingPresenter(SettingContract.View view) {
        mView = view;
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
}
