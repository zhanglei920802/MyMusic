/**
 * Copyright 2014 ZhangLei
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.tcl.lzhang1.mymusic.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tcl.lzhang1.mymusic.AppContext;
import com.tcl.lzhang1.mymusic.Contants;
import com.tcl.lzhang1.mymusic.MusicUtil;
import com.tcl.lzhang1.mymusic.R;
import com.tcl.lzhang1.mymusic.UIHelper;
import com.tcl.lzhang1.mymusic.db.DBOperator;
import com.tcl.lzhang1.mymusic.db.imp.SongImp;
import com.tcl.lzhang1.mymusic.exception.SDCardUnMoutedException;
import com.tcl.lzhang1.mymusic.model.SongModel;
import com.tcl.lzhang1.mymusic.model.SongsWrap;
import com.tcl.lzhang1.mymusic.service.MusicPlayService;
import com.tcl.lzhang1.mymusic.service.MusicPlayService.PlayState;
import com.tcl.lzhang1.mymusic.ui.MusicPlayActivity.PlayAction;
import com.tcl.lzhang1.mymusic.ui.apdater.MineAdapter;
import com.tcl.lzhang1.mymusic.ui.apdater.MyPageAdapter;
import com.tcl.lzhang1.mymusic.ui.widget.PagerSlidingTabStrip;

/**
 * This is MainActivity.java
 * 
 * @author leizhang
 */
public class MainActivity extends BaseActivity implements OnItemClickListener, OnClickListener {

    /**
     * title
     */
    private PagerSlidingTabStrip mPagerTabStrip = null;

    /**
     * the viewpager
     */
    private ViewPager mViewPager = null;

    /**
     * the views
     */
    private List<View> mViews = null;

    /**
     * the layout inflator
     */
    private LayoutInflater mLayoutInflater = null;

    /**
     * titles
     */
    private String[] mMenuTitles = null;

    /**
     * the adapter
     */
    private MyPageAdapter mPageAdapter = null;

    // mine
    /**
     * handler constants
     */
    public static final int SCAN_MUSIC_SUCCESS = 1;

    /**
     * handler constants
     */
    public static final int SCAN_MUSIC_FAILD = 2;

    /**
     * mine page
     */
    private View mine = null;

    /**
     * mine list view
     */
    private ListView mine_list = null;

    /**
     * list
     */
    private List<HashMap<String, String>> mList;

    /**
     * the adapter
     */
    private MineAdapter mineAdapter = null;

    /**
     * the songs
     */
    private List<SongModel> mSongModels;

    /**
     * database access API
     */
    private DBOperator mDbOperator = null;

    /**
     * login bnutton
     */
    private Button login = null;

    /**
     * is refresh
     */
    private boolean isRefreshing = true;

    /**
     * fav count
     */
    private int fav_count = 0;

    /**
     * the player ablum
     */
    private ImageView mini_player_ablum = null;

    /**
     * currSongName
     */
    private TextView currSongName = null;

    /**
     * currSongSinger
     */
    private TextView currSongSinger = null;

    /**
     * the previous button
     */
    private ImageView mini_player_pre = null;

    /**
     * the pause or start button
     */
    private ImageView mini_player_start = null;

    /**
     * the next button
     */
    private ImageView mini_player_next = null;

    /**
     * application context
     */
    private AppContext mAppContext = null;

    /**
     * is playing
     */
    private boolean isPlaying;
    /**
     * current song
     */
    private SongModel curSong;
    // process music play state changed
    private BroadcastReceiver mMusicPlaySateChangedReciver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (null != intent && intent.getAction().equals(Contants.FILTER_PLAY_STATE_CHANGED)) {
                curSong = (SongModel) intent.getSerializableExtra("model");
                try {
                    Log.d(TAG, "main activity receive action:" + Contants.FILTER_PLAY_STATE_CHANGED
                            + "play music[" + curSong != null ? curSong.getSongName() : "" + "]");
                } catch (Exception e) {
                    // TODO: handle exception
                    // e.printStackTrace();
                }

                int time = intent.getIntExtra("time", 0);
                String errmsg = intent.getStringExtra("errmsg");
                switch (intent.getIntExtra("state", 0)) {
                    case PlayState.PLAY_END:
                        Log.d(TAG, "PLAY_END");
                        break;
                    case PlayState.PLAY_ERROR:
                        Log.d(TAG, "PLAY_ERROR");
                        break;
                    case PlayState.PLAY_NEW:
                    {
                        Log.d(TAG, "PLAY_NEW");
                        updateMiniPlayInfo(curSong);
                        mini_player_start.setBackground(getResources().getDrawable(
                                R.drawable.mini_pausebtn_xml));
                        isPlaying = true;
                    }
                        break;
                    case PlayState.PLAY_PAUSED:
                        Log.d(TAG, "PLAY_PAUSED");
                        Log.d(TAG, "main activity received broadcast,music was puased");
                        mini_player_start.setBackground(getResources().getDrawable(
                                R.drawable.mini_playbtn_xml));
                        isPlaying = false;
                        break;
                    case PlayState.PLAY_RESUMED:
                        Log.d(TAG, "PLAY_RESUMED");
                        mini_player_start.setBackground(getResources().getDrawable(
                                R.drawable.mini_pausebtn_xml));
                        isPlaying = true;
                        break;
                    case PlayState.PLAY_SEEK:
                        Log.d(TAG, "PLAY_SEEK");
                        break;
                    case PlayState.PLAY_STOPED:
                        Log.d(TAG, "PLAY_STOPED");
                        mini_player_start.setBackground(getResources().getDrawable(
                                R.drawable.mini_playbtn_xml));
                        isPlaying = false;
                        break;
                    default:
                        break;
                }
            }
        }
    };

    // music scan
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            // super.handleMessage(msg);
            if (null != msg) {
                switch (msg.what) {
                    case SCAN_MUSIC_SUCCESS:
                        Log.d(TAG, "hand message,upadte UI......");
                        mSongModels = (List<SongModel>) msg.obj;

                        for (SongModel songModel : mSongModels) {
                            if (songModel.getFav() == 1) {
                                fav_count++;
                            }

                        }

                        if (null != mSongModels && !mSongModels.isEmpty()) {
                            Map<String, String> map = mList.get(0);
                            map.put("value", MusicUtil.formatString1(mSongModels.size()));
                            map = mList.get(1);
                            map.put("value", MusicUtil.formatString1(fav_count));
                        } else {
                            Map<String, String> map = mList.get(0);
                            map.put("value", MusicUtil.formatString1(0));
                            mSongModels = new ArrayList<SongModel>();
                        }

                        // when obtain informations done , update music
                        updateMiniPlayInfo(mSongModels.get(mAppContext.getPlayIndex()));

                        isRefreshing = false;
                        mineAdapter.notifyDataSetChanged();

                        break;
                    case SCAN_MUSIC_FAILD:

                        break;
                    default:
                        break;
                }
            }
        }
    };
    /**
     * broadcast receiver to process mark fav or not
     */
    private BroadcastReceiver mFavBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (null != intent && Contants.FILTER_ACTION_MARK_FAV.equals(intent.getAction())) {
                SongModel model = (SongModel) intent.getSerializableExtra("song");
                boolean value = intent.getBooleanExtra("value", false);
                if (model != null) {
                    markFav(model.getFile(), value);
                }
            }
        };
    };

    /**
     * mark music fav or not
     * 
     * @param path the index
     * @param value mark value
     */
    private void markFav(String path, boolean value) {

        for (SongModel songModel : mSongModels) {
            if (songModel.getFile().equals(path)) {
                if (value) {
                    songModel.setFav(1);
                } else {
                    songModel.setFav(0);
                }
                return;
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.tcl.lzhang1.mymusic.ui.AcitivityInit#getPreActivityData(android.os
     * .Bundle)
     */
    @Override
    public void getPreActivityData(Bundle bundle) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see com.tcl.lzhang1.mymusic.ui.AcitivityInit#initView()
     */
    @Override
    public void initView() {
        // TODO Auto-generated method stub * @see
        // com.tcl.lzhang1.mymusic.ui.AcitivityInit#initView()
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.main);
        mAppContext = (AppContext) getApplication();
        TAG = MainActivity.class.getName();
        {
            mini_player_ablum = (ImageView) findViewById(R.id.mini_player_ablum);
            mini_player_ablum.setOnClickListener(this);
            mini_player_pre = (ImageView) findViewById(R.id.mini_player_pre);
            mini_player_pre.setOnClickListener(this);
            mini_player_next = (ImageView) findViewById(R.id.mini_player_next);
            mini_player_next.setOnClickListener(this);
            mini_player_start = (ImageView) findViewById(R.id.mini_player_start);
            mini_player_start.setOnClickListener(this);
            currSongName = (TextView) findViewById(R.id.currSongName);
            currSongSinger = (TextView) findViewById(R.id.currSongSinger);
        }

        {
            mViewPager = (ViewPager) findViewById(R.id.menu_page);
            mPagerTabStrip = (PagerSlidingTabStrip) findViewById(R.id.pagerStrip);
            mViews = new ArrayList<View>();
            mLayoutInflater = getLayoutInflater();

            View view = mine = mLayoutInflater.inflate(R.layout.main_mine, null);
            mViews.add(view);
            view = mLayoutInflater.inflate(R.layout.main_recommand, null);
            mViews.add(view);
            view = mLayoutInflater.inflate(R.layout.main_libs, null);
            mViews.add(view);
            view = mLayoutInflater.inflate(R.layout.main_search, null);
            mViews.add(view);

            // mPagerTabStrip.setTextSpacing(50);
            // mPagerTabStrip.setTextColor(Color.BLACK);
            mPagerTabStrip.setIndicatorColor(Color.GREEN);
            mPagerTabStrip.setIndicatorHeight(10);
            mPageAdapter = new MyPageAdapter(this, mViews, mMenuTitles);
            mViewPager.setAdapter(mPageAdapter);

            mPagerTabStrip.setViewPager(mViewPager);
        }
        // mine
        {
            mine_list = (ListView) mine.findViewById(R.id.mine_list);
            mineAdapter = new MineAdapter(this, mList);
            mine_list.setAdapter(mineAdapter);
            mine_list.setOnItemClickListener(this);
            login = (Button) mine.findViewById(R.id.login);
            login.setOnClickListener(this);
        }

        {
            registerReceiver(mFavBroadcastReceiver, new IntentFilter(
                    Contants.FILTER_ACTION_MARK_FAV));
            registerReceiver(mMusicPlaySateChangedReciver, new IntentFilter(
                    Contants.FILTER_PLAY_STATE_CHANGED));
        }
    }

    /*
     * (non-Javadoc)
     * @see com.tcl.lzhang1.mymusic.ui.BaseActivity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        unregisterReceiver(mFavBroadcastReceiver);
        unregisterReceiver(mMusicPlaySateChangedReciver);
        super.onDestroy();
    }

    /*
     * (non-Javadoc)
     * @see com.tcl.lzhang1.mymusic.ui.AcitivityInit#initViewData()
     */
    @Override
    public void initViewData() {
        // TODO Auto-generated method stub
        mMenuTitles = getResources().getStringArray(R.array.pageview_menu);
        mList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map = new HashMap<String, String>();

        map.put("name", getString(R.string.local_music));
        map.put("value", getString(R.string.refreshing_music));
        mList.add(map);

        map = new HashMap<String, String>();
        map.put("name", getString(R.string.fav_music));
        map.put("value", MusicUtil.formatString1(0));
        mList.add(map);

        map = new HashMap<String, String>();
        map.put("name", getString(R.string.download_music));
        map.put("value", MusicUtil.formatString1(0));
        mList.add(map);

        // init music
        {
            mDbOperator = new SongImp(this);
            scanMusic();
        }
    }

    /**
     * scan musictext
     */
    private void scanMusic() {
        new Thread(new Runnable() {

            @SuppressWarnings("unchecked")
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {

                    List<SongModel> models;
                    Message message = mHandler.obtainMessage();
                    models = (List<SongModel>) mDbOperator.findAll();
                    if (models != null && !models.isEmpty()) {
                        Log.d(TAG, "data base have datas");
                        message.what = SCAN_MUSIC_SUCCESS;
                        message.obj = models;
                        mHandler.sendMessage(message);
                        return;
                    }
                    models = MusicUtil.scanMusic(getApplication());

                    message.what = SCAN_MUSIC_SUCCESS;
                    message.obj = models;
                    mHandler.sendMessage(message);
                    Log.d(TAG, "scan music finished,send message .....");
                    mDbOperator.saveAll(models);
                } catch (SDCardUnMoutedException e) {
                    // TODO Auto-generated catch block
                    Log.d(TAG, "scan music failed:unmouted sdcard");
                    mHandler.sendEmptyMessage(SCAN_MUSIC_FAILD);
                } catch (Exception e) {
                    // TODO: handle exception
                    Log.d(TAG, "scan music error:" + e.getMessage());
                    mHandler.sendEmptyMessage(SCAN_MUSIC_FAILD);
                }

            }
        }).start();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        Bundle bundle = null;
        switch (arg0.getId()) {
            case R.id.mine_list:
            // menu list
            {
                if (isRefreshing) {
                    UIHelper.toast(this, R.string.refreshing_music_stand_by);
                    return;
                }

                HashMap<String, String> map = mList.get(arg2);
                String name = map.get("name");
                if (getString(R.string.local_music).equals(name)) {
                    bundle = new Bundle();
                    bundle.putInt("startmode", MusicListAcitivity.START_MODE_LOCAL);
                    bundle.putSerializable("songs", new SongsWrap(mSongModels));
                    UIHelper.showMusicListActivity(this, bundle);
                    bundle = null;
                } else if (getString(R.string.fav_music).equals(name)) {
                    bundle = new Bundle();
                    bundle.putInt("startmode", MusicListAcitivity.START_MODE_FAV);
                    bundle.putSerializable("songs", new SongsWrap(mSongModels));
                    UIHelper.showMusicListActivity(this, bundle);
                    bundle = null;
                }
            }
                break;

            default:
                break;
        }
    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent = null;
        switch (v.getId()) {
            case R.id.login:
                UIHelper.showLoginActivity(this, null);
                break;
            case R.id.mini_player_ablum:
                shouMusicPlay(curSong, isPlaying);
                break;
            case R.id.mini_player_pre: {
                intent = new Intent(Contants.FILTER_PLAY_ACTION);
                intent.putExtra("action", PlayAction.ACTION_PRE);
                sendBroadcast(intent);
                mini_player_start.setBackground(getResources().getDrawable(
                        R.drawable.mini_playbtn_xml));
                isPlaying = false;
            }
                break;
            case R.id.mini_player_next: {
                intent = new Intent(Contants.FILTER_PLAY_ACTION);
                intent.putExtra("action", PlayAction.ACTION_NEXT);
                sendBroadcast(intent);
                mini_player_start.setBackground(getResources().getDrawable(
                        R.drawable.mini_playbtn_xml));
                isPlaying = false;
            }
                break;
            case R.id.mini_player_start:
                if (!isPlaying) {
                    if (!MusicUtil.checkServiceIsRunning(this, MusicPlayService.class.getName())) {
                        curSong = mSongModels.get(mAppContext.getPlayIndex());
                        playMusic();
                        isPlaying = true;
                    } else {
                        Log.i(TAG, "send broadcast to start play music");
                        intent = new Intent(Contants.FILTER_PLAY_ACTION);
                        intent.putExtra("action", PlayAction.ACTION_START);
                        sendBroadcast(intent);
                        mini_player_start.setBackground(getResources().getDrawable(
                                R.drawable.mini_pausebtn_xml));
                        isPlaying = true;

                    }

                } else {
                    intent = new Intent(Contants.FILTER_PLAY_ACTION);
                    intent.putExtra("action", PlayAction.ACTION_PAUSE);

                    sendBroadcast(intent);
                    mini_player_start.setBackground(getResources().getDrawable(
                            R.drawable.mini_playbtn_xml));
                    isPlaying = false;
                }
                break;
            default:
                break;
        }
    }

    /**
     * update mini player informations
     * 
     * @param index
     */
    private void updateMiniPlayInfo(SongModel model) {

        if (null == model) {
            return;
        }

        currSongName.setText(model.getSongName());
        currSongSinger.setText(model.getSingerName());

    }

    /**
     * play music
     */
    private void playMusic() {
        startSetvice();
        mini_player_start.setBackground(getResources().getDrawable(R.drawable.mini_pausebtn_xml));

    }

    /**
     * start play service
     */
    private void startSetvice() {
        Intent intent = new Intent(this, MusicPlayService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("songs", new SongsWrap(mSongModels));
        bundle.putInt("index", mAppContext.getPlayIndex());
        bundle.putInt("time", mAppContext.getPlayTime());
        intent.putExtras(bundle);
        startService(intent);
    }

    /**
     * show music play song
     * 
     * @param currentSong current song
     * @param isRunning indicates play is running
     */
    private void shouMusicPlay(SongModel currentSong, boolean isRunning) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("song", currentSong);
        bundle.putSerializable("songs", new SongsWrap(mSongModels));
        bundle.putBoolean("isrunning", isRunning);
        bundle.putInt("from", MusicPlayActivity.START_MODE_FROM_MINIPLAYER);
        UIHelper.showMusicPlayActivity(this, bundle);
        bundle = null;
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        // super.onBackPressed();
        if (!MusicUtil.canExit(System.currentTimeMillis())) {
            Log.d(TAG, "can not exit");
            UIHelper.toast(this, R.string.exit_when_click_again);
        } else {

            Log.d(TAG, "isPlaying:" + isPlaying);
            // if is paused , stop service
            if (!isPlaying) {
                Intent intent = new Intent(this, MusicPlayService.class);
                stopService(intent);
                intent = null;
                Log.d(TAG, "music playing is paused,stop service");
            }

            // finish activity
            super.onBackPressed();
        }

    }
}
