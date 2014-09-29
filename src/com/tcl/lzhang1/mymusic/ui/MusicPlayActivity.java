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

import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.tcl.lzhang1.mymusic.AppContext;
import com.tcl.lzhang1.mymusic.Contants;
import com.tcl.lzhang1.mymusic.MusicUtil;
import com.tcl.lzhang1.mymusic.R;
import com.tcl.lzhang1.mymusic.UIHelper;
import com.tcl.lzhang1.mymusic.db.DBOperator;
import com.tcl.lzhang1.mymusic.db.imp.SongImp;
import com.tcl.lzhang1.mymusic.model.SongModel;
import com.tcl.lzhang1.mymusic.model.SongsWrap;
import com.tcl.lzhang1.mymusic.service.MusicPlayService;
import com.tcl.lzhang1.mymusic.service.MusicPlayService.PlayState;
import com.tcl.lzhang1.mymusic.ui.widget.MySeekBar;

public class MusicPlayActivity extends BaseActivity implements OnClickListener {
    private TextView nav_title = null;
    private TextView go_list = null;
    private TextView play_song_name = null;
    private TextView ablum_name = null;
    private ImageView preBtn = null;
    private ImageView pausebtn = null;
    private ImageView nextBtn = null;
    private SongsWrap mSongsWrap;
    private int index = 0;
    private int time = 0;
    private DBOperator mDbOperator;
    private ImageView playmode = null;
    private boolean isPlaying = false;
    private boolean isInit = true;
    private int curMode = PlayMode.MODE_REPEAT_ALL;
    private TextView play_time = null;
    private TextView total_time = null;
    private MySeekBar music_seek_bar = null;
    private SongModel curSong = null;
    private AppContext mAppContext = null;
    // process music play state changed
    private BroadcastReceiver mMusicPlaySateChangedReciver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (null != intent && intent.getAction().equals(Contants.FILTER_PLAY_STATE_CHANGED)) {
                SongModel model = curSong = (SongModel) intent.getSerializableExtra("model");
                int time = intent.getIntExtra("time", 0);
                String errmsg = intent.getStringExtra("errmsg");
                switch (intent.getIntExtra("state", 0)) {
                    case PlayState.PLAY_END:

                        break;
                    case PlayState.PLAY_ERROR:

                        break;
                    case PlayState.PLAY_NEW:
                    {

                        play_song_name.setText(model.getSongName());
                        ablum_name.setText(model.getSingerName());
                        nav_title.setText(model.getSongName());
                        total_time.setText(MusicUtil.formatString(model.getMinutes(),
                                model.getSeconds()));
                    }
                        break;
                    case PlayState.PLAY_PAUSED:
                        Log.d(TAG, "music play thread received broadcast,music was puased");
                        pausebtn.setBackground(getResources().getDrawable(R.drawable.playbtn_xml));
                        isPlaying = false;
                        break;
                    case PlayState.PLAY_RESUMED:

                        break;
                    case PlayState.PLAY_SEEK:

                        break;
                    case PlayState.PLAY_STOPED:

                        break;
                    default:
                        break;
                }
            }
        }
    };

    // timer to change seekbar
    private Timer timer;
    private int min = 0, sec = 0;
    private TimerTask mTimerTask = new TimerTask() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    sec++;
                    if (sec % 60 == 0) {
                        min++;
                        sec = 0;
                    }

                    play_time.setText(MusicUtil.formatString(min, sec));
                    music_seek_bar.setProgress(MusicUtil.getPercent(min, sec, curSong.getMinutes(),
                            curSong.getSeconds()));
                }
            });
        }
    };

    @Override
    public void getPreActivityData(Bundle bundle) {
        mSongsWrap = (SongsWrap) getIntent().getSerializableExtra("songs");
        index = getIntent().getIntExtra("index", 0);
        time = getIntent().getIntExtra("time", 0);
    }

    /**
     * this interface define action of play
     * 
     * @author leizhang
     */
    public interface PlayAction {
        public static final int ACTION_PAUSE = 1;
        public static final int ACTION_STOP = 2;
        public static final int ACTION_START = 3;
        public static final int ACTION_NEXT = 4;
        public static final int ACTION_PRE = 5;
        public static final int ACTION_NEW = 6;
    }

    /**
     * This interface define the mode of playing
     * 
     * @author leizhang
     */
    public interface PlayMode {

        /**
         * the music will be play by loop in all list
         */
        public static final int MODE_REPEAT_ALL = 3;
        /**
         * the music will be play by random & repeat
         */
        public static final int MODE_REPEAT_RANDOM = 4;
        /**
         * the music will be play by repeat in one music
         */
        public static final int MODE_REPEAT_SINGLE = 5;
    }

    @Override
    public void initView() {
        // TODO Auto-generated method stub
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.acitivity_music_play);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mAppContext = (AppContext) getApplication();
        {
            findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    finish();
                }
            });
            TAG = getPackageName() + "/" + MusicPlayActivity.class.getSimpleName();
            nav_title = (TextView) findViewById(R.id.nav_title);
            go_list = (TextView) findViewById(R.id.go_list);
            play_song_name = (TextView) findViewById(R.id.play_song_name);
            ablum_name = (TextView) findViewById(R.id.ablum_name);
            preBtn = (ImageView) findViewById(R.id.preBtn);
            preBtn.setOnClickListener(this);
            pausebtn = (ImageView) findViewById(R.id.pausebtn);
            pausebtn.setOnClickListener(this);
            nextBtn = (ImageView) findViewById(R.id.nextBtn);
            nextBtn.setOnClickListener(this);
            playmode = (ImageView) findViewById(R.id.playmode);
            playmode.setOnClickListener(this);
            play_time = (TextView) findViewById(R.id.play_time);
            total_time = (TextView) findViewById(R.id.total_time);
            music_seek_bar = (MySeekBar) findViewById(R.id.music_seek_bar);
            setPlayMode(mAppContext.getPlayMode());
        }

        {
            registerReceiver(mMusicPlaySateChangedReciver, new IntentFilter(
                    Contants.FILTER_PLAY_STATE_CHANGED));
        }

        {
            SongModel model = mSongsWrap.getModels().get(index);
            play_song_name.setText(model.getSongName());
            ablum_name.setText(model.getSingerName());
            nav_title.setText(model.getSongName());
            total_time.setText(MusicUtil.formatString(model.getMinutes(),
                    model.getSeconds()));
            boolean isPlayServiceRunning = MusicUtil.checkServiceIsRunning(this,
                    MusicPlayService.class.getName());
            if (!isPlayServiceRunning) {
                playMusic();
            } else {// if music is playing
                Intent intent = new Intent();
                intent.setAction(Contants.FILTER_PLAY_ACTION);
                intent.putExtra("action", PlayAction.ACTION_NEW);
                intent.putExtra("index", index);
                intent.putExtra("time", time);
                sendBroadcast(intent);
                pausebtn.setBackground(getResources().getDrawable(R.drawable.pausebtn_xml));
                isPlaying = true;
                isInit = false;
            }

        }
    }

    @Override
    public void initViewData() {
        // TODO Auto-generated method stub
        mDbOperator = new SongImp(this);

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stubisPlaying
        Intent intent = null;
        switch (v.getId()) {
            case R.id.preBtn: {
                intent = new Intent(Contants.FILTER_PLAY_ACTION);
                intent.putExtra("action", PlayAction.ACTION_PRE);
                sendBroadcast(intent);
                pausebtn.setBackground(getResources().getDrawable(R.drawable.playbtn_xml));
                isPlaying = false;
            }
                break;
            case R.id.pausebtn:
                if (!isPlaying) {
                    if (isInit) {
                        playMusic();
                    } else {
                        Log.i(TAG, "send broadcast to start play music");
                        intent = new Intent(Contants.FILTER_PLAY_ACTION);
                        intent.putExtra("action", PlayAction.ACTION_START);
                        sendBroadcast(intent);
                        pausebtn.setBackground(getResources().getDrawable(R.drawable.pausebtn_xml));
                        isPlaying = true;
                        isInit = false;
                    }

                } else {
                    intent = new Intent(Contants.FILTER_PLAY_ACTION);
                    intent.putExtra("action", PlayAction.ACTION_PAUSE);
                    sendBroadcast(intent);
                    pausebtn.setBackground(getResources().getDrawable(R.drawable.playbtn_xml));
                    isPlaying = false;
                }

                break;
            case R.id.nextBtn: {
                intent = new Intent(Contants.FILTER_PLAY_ACTION);
                intent.putExtra("action", PlayAction.ACTION_NEXT);
                sendBroadcast(intent);
                pausebtn.setBackground(getResources().getDrawable(R.drawable.playbtn_xml));
                isPlaying = false;
            }
                break;
            case R.id.playmode: {
                switch (curMode) {
                    case PlayMode.MODE_REPEAT_ALL:
                        curMode = PlayMode.MODE_REPEAT_RANDOM;
                        playmode.setImageResource(R.drawable.playmode_repeate_random);
                        UIHelper.toast(this,
                                String.format(getString(R.string.cur_play_mode), "随机播放"));
                        break;
                    case PlayMode.MODE_REPEAT_RANDOM:
                        curMode = PlayMode.MODE_REPEAT_SINGLE;
                        playmode.setBackground(null);
                        playmode.setImageResource(R.drawable.playmode_repeate_single);
                        UIHelper.toast(this,
                                String.format(getString(R.string.cur_play_mode), "单曲循环"));
                        break;
                    case PlayMode.MODE_REPEAT_SINGLE:
                        curMode = PlayMode.MODE_REPEAT_ALL;
                        playmode.setBackground(null);
                        playmode.setImageResource(R.drawable.playmode_repeate_all);
                        UIHelper.toast(this,
                                String.format(getString(R.string.cur_play_mode), "列表循环"));
                        break;
                    default:
                        break;
                }
                mAppContext.savePlayMode(curMode);
                sendPlayModeBroad(curMode);
            }
                break;
            case R.id.go_list:

                break;
            default:
                break;
        }
    }

    private void setPlayMode(int playMode) {
        switch (curMode) {
            case PlayMode.MODE_REPEAT_ALL:
                playmode.setImageResource(R.drawable.playmode_repeate_all);

                break;
            case PlayMode.MODE_REPEAT_RANDOM:

                playmode.setImageResource(R.drawable.playmode_repeate_random);
                break;
            case PlayMode.MODE_REPEAT_SINGLE:

                playmode.setImageResource(R.drawable.playmode_repeate_single);

                break;
            default:
                break;
        }
    }

    private void playMusic() {
        startSetvice();
        pausebtn.setBackground(getResources().getDrawable(R.drawable.pausebtn_xml));
        isPlaying = true;
        isInit = false;
        if (null == timer) {
            timer = new Timer();
        }
        timer.schedule(mTimerTask, 0, 1000);
    }

    private void sendPlayModeBroad(int mode) {
        Intent intent = new Intent();
        intent.setAction(Contants.FILTER_PLAY_MODE);
        intent.putExtra("mode", mode);
        sendBroadcast(intent);
        intent = null;

    }

    private void startSetvice() {
        Intent intent = new Intent(this, MusicPlayService.class);
        Bundle bundle = new Bundle();
        curSong = mSongsWrap.getModels().get(0);
        bundle.putSerializable("songs", mSongsWrap);
        bundle.putInt("index", index);
        bundle.putInt("time", time);
        intent.putExtras(bundle);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        unregisterReceiver(mMusicPlaySateChangedReciver);
        super.onDestroy();
    }

}
