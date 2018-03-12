package com.lgc.memorynote.wordList;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.lgc.memorynote.R;
import com.lgc.memorynote.base.Logcat;
import com.lgc.memorynote.base.MemoryNoteApplication;
import com.lgc.memorynote.base.Util;
import com.lgc.memorynote.data.GlobalData;
import com.lgc.memorynote.data.Word;
import com.lgc.memorynote.user.setting.SettingActivity;
import com.lgc.memorynote.wordDetail.WordDetailActivity;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;

public class WordListActivity extends AppCompatActivity implements WordListContract.View{

    public static final int WORD_DETAIL_ACTIVITY_ONE = 1;
    public static final String IS_REFRESH_LIST = "is_research";

    private RecyclerView mWordListView;
    private WordListAdapter mWordListAdapter;
    private LinearLayoutManager linearLayoutManager;
    private TextView mTvCommandList;
    private AutoCompleteTextView mTvCommandFrame;
    private WordListPresenter mPresenter;
    private boolean mIsNewClick = true;
    private ArrayAdapter<String> recentCmdAdapter;

    private GlobalData mGlobalData;
    boolean isRefreshOnReturn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initApp();
        setContentView(R.layout.activity_word_list);
        mPresenter = new WordListPresenter(this);
//        test();
        intView();
        initData();
    }

    private void initApp() {
        permission();
        Bmob.initialize(this, "63ab0dfdd965aa92efbfce03fd10d082");//再是网络初始化
        startBackgroundService();
        Log.e("------------", "init: 应用初始化成功");
    }

    @Override
    protected void onRestart() {
        if (isRefreshOnReturn) {
            onClickSearch();
        } else {
            isRefreshOnReturn = true;
        }
        mIsNewClick = true;
        super.onRestart();
    }

    private void startActivityWordDetail(String wordName) {
        Intent intent = WordDetailActivity.getStartIntent(WordListActivity.this);
        intent.putExtra(WordDetailActivity.INTENT_EXTRA_IS_ADD, false);
        intent.putExtra(WordDetailActivity.INTENT_EXTRA_WORD_NAME, wordName);
        startActivityForResult(intent, WORD_DETAIL_ACTIVITY_ONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WORD_DETAIL_ACTIVITY_ONE) {
            if (data != null) {
                isRefreshOnReturn = data.getBooleanExtra(IS_REFRESH_LIST, true);
            } else {
                isRefreshOnReturn = true;
            }
        }
    }

    /**
     * 启动后台服务，
     * <p>1.在后台发送用户使用信息
     */
    private static void startBackgroundService() {
        Intent intent = new Intent("initDate");
        intent.setAction("a.memorynote.common.appInfo.AppIntentService");
        MemoryNoteApplication.appContext.startService(intent);
    }

    //android 6.0权限请求
    String[] mPermissionList = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE
    };

    private void permission() {
        //权限请求
        PackageManager pm = getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getPackageName()));
        if (!permission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(mPermissionList, 100);
            }
        }
    }

    private void test() {
//        Intent intent  = WordDetailActivity.getStartIntent(WordListActivity.this);
//        intent.putExtra(WordDetailActivity.INTENT_EXTRA_IS_ADD, true);
//        startActivity(intent);
        Logcat.d("start setting activity");
        startActivity(new Intent(this, SettingActivity.class));
    }

    private void intView() {
        // base view widget
        initTvInputCmd();

        mTvCommandList = (TextView)findViewById(R.id.tv_command);
        findViewById(R.id.add_word).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = WordDetailActivity.getStartIntent(WordListActivity.this);
                intent.putExtra(WordDetailActivity.INTENT_EXTRA_IS_ADD, true);
                String inputName = mTvCommandFrame.getText().toString().trim();
                if (Word.isLegalWordName(inputName) && mWordListAdapter.getItemCount() == 0) { // 单词合法，并且没有找到结果，自动添加
                    intent.putExtra(WordDetailActivity.INTENT_EXTRA_ADD_NAME, inputName);
                }
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_expand_command_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTvCommandList.getMaxLines() == 1) {
                    mTvCommandList.setMaxLines(100);
                } else {
                    mTvCommandList.setMaxLines(1);
                }
                updateCommandText(Command.commandList, mPresenter.getChoseCommand());
                mTvCommandList.setVisibility(View.GONE);
                mTvCommandList.setVisibility(View.VISIBLE);
                mTvCommandList.requestLayout();
            }
        });

        findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Util.RepetitiveEventFilter.isRepetitive(1000)) {
                    onClickSearch();
                    mIsNewClick = true;
                } else {
                    Logcat.d("repetitive click");
                }
            }
        });

        // about recyclerView
        mWordListView = (RecyclerView) findViewById(R.id.lv_word_list);
        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        mWordListView.setLayoutManager(linearLayoutManager);
    }

    private void initTvInputCmd() {
        mTvCommandFrame = (AutoCompleteTextView) findViewById(R.id.et_command_frame);
        mTvCommandFrame.setHint(SortUtil.getHingString());
        mTvCommandFrame.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    onClickSearch();
                    return true;
                }
                return false;
            }
        });

        mTvCommandFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsNewClick || mTvCommandFrame.getText().toString().trim().isEmpty()) {
                    mTvCommandFrame.showDropDown();
                }
                if (mIsNewClick) {
                    mTvCommandFrame.setSelection(0, mTvCommandFrame.getText().length());
                    mIsNewClick = false;
                }
            }
        });

        //创建一个ArrayAdapter，封装数组
        recentCmdAdapter = new ArrayAdapter<String>(this
                ,android.R.layout.simple_dropdown_item_1line, GlobalData.getInstance().getRecentCmd());
        mTvCommandFrame.setAdapter(recentCmdAdapter);
        mTvCommandFrame.setThreshold(0);
    }

    private void onClickSearch() {
        mIsNewClick = true;
        try {
            mPresenter.reorderWordList();
            recentCmdAdapter = new ArrayAdapter<String>(this
                    ,android.R.layout.simple_dropdown_item_1line, GlobalData.getInstance().getRecentCmd());
            mTvCommandFrame.setAdapter(recentCmdAdapter);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "输入数字格式错误", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public @Nullable String getInputCmd() {
        if (mTvCommandFrame == null) return null;
        return mTvCommandFrame.getText().toString();
    }

    @Override
    public void clearCommandEt() {
        mTvCommandFrame.setText("");
    }

    private void initData() {
        mWordListAdapter = new WordListAdapter(this);
        mWordListAdapter.setItemClickListener(new WordListAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View v, WordListAdapter.ItemHolder itemHolder) {
                int position = itemHolder.getAdapterPosition();
                switch (v.getId()) {
                    case R.id.lv_item_layout:
                         startActivityWordDetail(mPresenter.getWordName(position));
                         break;
                    case R.id.lv_item_word_add_strange:
                        mPresenter.addStrange(position);
                        mWordListAdapter.notifyItemChanged(position);
                        break;
                    case R.id.lv_item_word_reduce_strange:
                        mPresenter.reduceStrange(position);
                        mWordListAdapter.notifyItemChanged(position);
                        break;
                }
            }
        });
        try {
            mPresenter.start();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        mWordListView.setAdapter(mWordListAdapter);
        mWordListView.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL));
        mGlobalData = GlobalData.getInstance();
    }

    @Override
    public void updateCommandText(List<String> commandList, List<String> chosenList) {
        // 转换层UI上的字符串
        List<Pair<String, String>> UICommandList = new ArrayList<>();
        int maxNumber = commandList.size();
        if (mTvCommandList.getMaxLines() <= 1) {
            maxNumber = Math.min(maxNumber, 4);
        }

        for (int i = 0; i < maxNumber; i++) {
            String one  = commandList.get(i);
            UICommandList.add(
                    new Pair<>(Command.UICommandMap.get(one), one));
        }

        // 构造出所有命令的字符串
        StringBuilder sb = new StringBuilder();
        sb.append("  ");
        for (int i = 0; i < UICommandList.size(); i++) {
            sb.append(UICommandList.get(i).first);
            if (i < UICommandList.size() -1) {
                if (i % 4 == 3) {
                    sb.append("\n  ");
                } else {
                    sb.append("     ");
                }
            }
        }
        String commandString = sb.toString();

        // 用Spanable装饰
        SpannableString ss = new SpannableString(commandString);

        for (int i = 0; i < maxNumber; i++) {
            Pair<String, String> pair = UICommandList.get(i);
            String UICommand = pair.first;
            String command = pair.second;
            // i 对应于commanList中的
            int startId = commandString.indexOf(UICommand);
            int endId = startId + UICommand.length();
            if (startId >= 0 && endId <= commandString.length()) {
                ss.setSpan(new clickSpan(command, chosenList.contains(command)),
                        startId, endId, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }
        mTvCommandList.setMovementMethod(LinkMovementMethod.getInstance());
        mTvCommandList.setText(ss);
    }

    @Override
    public void hideMeaning(boolean isHideMeaning) {
        mWordListAdapter.setHideMeaning(isHideMeaning);
    }

    public void hideWord(boolean isHideWord) {
        mWordListAdapter.setHideWord(isHideWord);
    }

    @Override
    public void setSettingActivity() {
        startActivity(new Intent(this, SettingActivity.class));
    }

    @Override
    public void showWordNumber() {
        mTvCommandFrame.setText("");
        mTvCommandFrame.setHint("当前单词数 = " + mWordListAdapter.getItemCount());
    }

    @Override
    public void setPresenter(WordListContract.Presenter presenter) {

    }

    /**
     * 进入界面，并且获取到图片信息之后开始显示
     * @param mCurShowWordList
     */
    @Override
    public void showWordList(List<Word> mCurShowWordList) {
        if (mWordListAdapter != null) {
            mWordListAdapter.setWordList(mCurShowWordList);
        }
    }

    @Override
    public void deleteOneWord(String s) {

    }

    @Override
    public void onTogglePicList(WordListAdapter wordListAdapter) {

    }

    /**
     * picAdapter通知数据更新了,刷新图片列表视图
     *
     * @param resultList
     */
    @Override
    public void refreshWordList(List<Word> resultList) {
        mWordListAdapter.setWordList(resultList);
    }

    @Override
    public int getListPosition() {
        return linearLayoutManager.findFirstVisibleItemPosition();
    }

    public void restorePosition(int position) {
        linearLayoutManager.scrollToPosition(position);
    }

    public class clickSpan extends ClickableSpan {
        private final String mCommand;
        private final boolean mIsChosen;

        public clickSpan(String command, boolean isChosen) {
            this.mCommand = command;
            mIsChosen = isChosen;
        }

        @Override
        public void onClick(View widget) {
            mPresenter.switchOneCommand(mCommand);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false); //去掉下划线
            if (mIsChosen) {
                ds.setColor(getColor(R.color.command_chosen));
            } else {
                ds.setColor(getColor(R.color.command_not_choosed));
            }
        }
    }

    @Override
    protected void onStop() {
        mGlobalData.saveRecentCmd();
        super.onStop();
    }
}
