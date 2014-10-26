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

import java.util.List;

import com.tcl.lzhang1.mymusic.AppContext;
import com.tcl.lzhang1.mymusic.Contants;
import com.tcl.lzhang1.mymusic.MusicUtil;
import com.tcl.lzhang1.mymusic.R;
import com.tcl.lzhang1.mymusic.UIHelper;
import com.tcl.lzhang1.mymusic.model.SongModel;
import com.tcl.lzhang1.mymusic.model.SongsWrap;
import com.tcl.lzhang1.mymusic.service.MusicPlayService;
import com.tcl.lzhang1.mymusic.service.MusicPlayService.PlayState;
import com.tcl.lzhang1.mymusic.ui.MusicPlayActivity.PlayAction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * This is MiniPlayerBaseActivity,extends it,will display play ui. <BR/>
 * implements funtioncs as follows:<BR/>
 * <LI>1.play/pause music</LI><BR/>
 * <LI>2.play next/previous music</LI><BR/>
 * <LI>3.update play progress<LI/>
 * 
 * @author Administrator
 */
public class MiniPlayerBaseActivity extends BaseActivity implements OnClickListener {
    /**
     * the songs
     */
    protected List<SongModel> mSongModels;

    /**
     * the previous button
     */
    private ImageView mini_player_pre = null;

    /**
     * the pause or start button
     */
    private ImageView mini_player_start = null;

    /**
     * the next button android:paddingTop="5dip" android:paddingBottom="5dip"
     */
    private ImageView mini_player_next = null;

    /**
     * is playing
     */
    protected boolean isPlaying;
    /**
     * current song
     */
    private SongModel curSong;

    /**
     * 
     */
    private SeekBar playSeekBar = null;

    /**
     * the application context
     */
    private AppContext mAppContext = null;

    /**
     * the player ablum
     */
    private ImageView mini_player_ablum = null;

    /**
     * the main content
     */
    protected ViewGroup mViewGroup = null;
    /**
     * currSongName
     */
    private TextView currSongName = null;

    /**
     * currSongSinger
     */
    private TextView currSongSinger = null;

    // process music play state changed
    private BroadcastReceiver mMusicPlaySateChangedReciver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (null != intent && intent.getAction().equals(Contants.FILTER_PLAY_STATE_CHANGED)) {
                curSong = (SongModel) intent.getSerializableExtra("model");
                mAppContext.savePlayIndex(intent.getIntExtra("index", 0));
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

    /**
     * the receiver used to receive progress state.
     */
    private BroadcastReceiver mProgressReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (null != mProgressReceiver
                    && Contants.FILTER_ACTION_SEEK_UPDATED.equals(intent.getAction())) {

                int progress = (int) (intent.getFloatExtra("percent", 0.0f) * 100);
                curSong = (SongModel) intent.getSerializableExtra("song");
                currSongName.setText(curSong.getSongName());
                currSongSinger.setText(curSong.getSingerName());
                playSeekBar.setProgress(progress);
            }
        }
    };

    private boolean isUnregister = false;

    @Override
    public void getPreActivityData(Bundle bundle) {
        // TODO Auto-generated method stub

    }

    @Override
    public void initView() {
        // TODO Auto-generated method stub
        mViewGroup = (ViewGroup) findViewById(R.id.content_ll_fl);

        ((ViewStub) findViewById(R.id.mini_player_stub)).inflate();
        mAppContext = (AppContext) getApplication();

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
            playSeekBar = (SeekBar) findViewById(R.id.playSeekBar);

        }

        {
            registerReceiver(mMusicPlaySateChangedReciver, new IntentFilter(
                    Contants.FILTER_PLAY_STATE_CHANGED));
            registerReceiver(mProgressReceiver, new IntentFilter(
                    Contants.FILTER_ACTION_SEEK_UPDATED));
        }
    }

    @Override
    public void initViewData() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent = null;
        switch (v.getId()) {
            case R.id.login:
                UIHelper.showLoginActivity(this, null);
                break;
            case R.id.mini_player_ablum:
                if (mSongModels == null || mSongModels.isEmpty()) {
                    return;
                }
                shouMusicPlay(curSong, isPlaying);
                break;
            case R.id.mini_player_pre: {
                if (mSongModels == null || mSongModels.isEmpty()) {
                    return;
                }
                if (!MusicUtil.checkServiceIsRunning(this, MusicPlayService.class.getName())) {// service
                                                                                               // not
                                                                                               // running
                    playMusic(0);
                } else {
                    intent = new Intent(Contants.FILTER_PLAY_ACTION);
                    intent.putExtra("action", PlayAction.ACTION_PRE);
                    sendBroadcast(intent);
                }

                mini_player_start.setBackground(getResources().getDrawable(
                        R.drawable.mini_playbtn_xml));
                isPlaying = false;
            }
                break;
            case R.id.mini_player_next: {
                if (mSongModels == null || mSongModels.isEmpty()) {
                    return;
                }
                if (!MusicUtil.checkServiceIsRunning(this, MusicPlayService.class.getName())) {// service
                                                                                               // not
                                                                                               // running
                    playMusic(2);
                } else {// service not running
                    intent = new Intent(Contants.FILTER_PLAY_ACTION);
                    intent.putExtra("action", PlayAction.ACTION_NEXT);
                    sendBroadcast(intent);
                }

                mini_player_start.setBackground(getResources().getDrawable(
                        R.drawable.mini_playbtn_xml));
                isPlaying = false;
            }
                break;
            case R.id.mini_player_start:
                if (mSongModels == null || mSongModels.isEmpty()) {
                    return;
                }
                if (!isPlaying) {
                    if (!MusicUtil.checkServiceIsRunning(this, MusicPlayService.class.getName())) {
                        curSong = mSongModels.get(mAppContext.getPlayIndex());
                        playMusic(1);
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
     * get the valid index
     * 
     * @param index
     * @return
     */
    protected int getValidPlayIndex(int index) {
        if (index > 0 && index < mSongModels.size() - 1) {
            return index;
        } else {
            return 0;
        }
    }

    /**
     * start play service
     */
    protected void startSetvice(int tag) {
        Intent intent = new Intent(this, MusicPlayService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("songs", new SongsWrap(mSongModels));
        switch (tag) {
            case 0:
                bundle.putInt("index", getValidPlayIndex(mAppContext.getPlayIndex() - 1));
                break;
            case 1:
                bundle.putInt("index", mAppContext.getPlayIndex());
                break;
            case 2:
                bundle.putInt("index", getValidPlayIndex(mAppContext.getPlayIndex() + 1));
                break;
            default:
                break;
        }

        bundle.putInt("time", mAppContext.getPlayTime());
        intent.putExtras(bundle);
        startService(intent);
    }

    /**
     * play music
     */
    protected void playMusic(int tag) {
        startSetvice(tag);
        mini_player_start.setBackground(getResources().getDrawable(R.drawable.mini_pausebtn_xml));

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        if (!isUnregister) {
            Log.d(TAG, "un register");
            // unregisterReceiver(mFavBroadcastReceiver);
            unregisterReceiver(mMusicPlaySateChangedReciver);
            unregisterReceiver(mProgressReceiver);
            isUnregister = true;
        }

        super.onDestroy();
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
        curSong = model;

        currSongName.setText(model.getSongName());
        currSongSinger.setText(model.getSingerName());

    }

    /**
     * update mini player informations
     * 
     * @param index
     */
    protected void updateMiniPlayInfo(List<SongModel> models) {

        if (models.isEmpty()) {
            currSongName.setText(R.string.no_musics);
            currSongSinger.setVisibility(View.GONE);
        } else {
            SongModel model = models.get(mAppContext.getPlayIndex());
            curSong = model;
            currSongName.setText(model.getSongName());
            currSongSinger.setText(model.getSingerName());
        }

    }

    /**
     * show music play song
     * 
     * @param currentSong current song
     * @param isRunning indicates play is running
     */
    private void shouMusicPlay(SongModel currentSong, boolean isRunning) {
        if (null == currentSong) {
            currentSong = mSongModels.get(getValidPlayIndex(mAppContext.getPlayIndex()));

            if (null == currentSong) {
                currSongName.setText("N/A");
                currSongSinger.setText("N/A");
                return;
            }
        }

        Log.d(TAG, "shouMusicPlay.current play song :" + currentSong.getSongName());

        Bundle bundle = new Bundle();

        bundle.putSerializable("song", currentSong);
        bundle.putInt("index", MusicUtil.getIndexOfList(mSongModels, curSong.getFile()));
        bundle.putSerializable("songs", new SongsWrap(mSongModels));
        bundle.putBoolean("isrunning", isRunning);
        bundle.putInt("from", MusicPlayActivity.START_MODE_FROM_MINIPLAYER);
        UIHelper.showMusicPlayActivity(this, bundle);
        bundle = null;
    }
}
