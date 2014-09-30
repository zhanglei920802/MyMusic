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
    /**
     * the nav title
     */
    private TextView nav_title = null;

    /**
     * go list button
     */
    private TextView go_list = null;
    /**
     * textview of songname
     */
    private TextView play_song_name = null;

    /**
     * text view of ablum name
     */
    private TextView ablum_name = null;

    /**
     * the pre button
     */
    private ImageView preBtn = null;

    /**
     * the pause/play button
     */
    private ImageView pausebtn = null;
    /**
     * the next button
     */
    private ImageView nextBtn = null;
    /**
     * wrapper of songs
     */
    private SongsWrap mSongsWrap;
    /**
     * the current play index
     */
    private int index = 0;

    /**
     * current play time
     */
    private int time = 0;

    /**
     * the DataBase access interface
     */
    private DBOperator mDbOperator;

    /**
     * play mode button
     */
    private ImageView playmode = null;

    /**
     * is playing
     */
    private boolean isPlaying = false;

    /**
     * indicates is init
     */
    private boolean isInit = true;

    /**
     * current play mode
     */
    private int curMode = PlayMode.MODE_REPEAT_ALL;

    /**
     * have play time
     */
    private TextView play_time = null;

    /**
     * total play time
     */
    private TextView total_time = null;

    /**
     * play progress bar
     */
    private MySeekBar music_seek_bar = null;

    /**
     * current play song
     */
    private SongModel curSong = null;

    /**
     * the global application
     */
    private AppContext mAppContext = null;

    /**
     * fav button
     */
    private ImageView add_fav = null;

    // process music play state changed
    private BroadcastReceiver mMusicPlaySateChangedReciver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (null != intent && intent.getAction().equals(Contants.FILTER_PLAY_STATE_CHANGED)) {
                curSong = (SongModel) intent.getSerializableExtra("model");
                int time = intent.getIntExtra("time", 0);
                String errmsg = intent.getStringExtra("errmsg");
                switch (intent.getIntExtra("state", 0)) {
                    case PlayState.PLAY_END:

                        break;
                    case PlayState.PLAY_ERROR:

                        break;
                    case PlayState.PLAY_NEW:
                    {

                        play_song_name.setText(curSong.getSongName());
                        ablum_name.setText(curSong.getSingerName());
                        nav_title.setText(curSong.getSongName());
                        total_time.setText(MusicUtil.formatString(curSong.getMinutes(),
                                curSong.getSeconds()));
                        if (curSong.getFav() == 1) {
                            add_fav.setImageResource(R.drawable.add_fav_xml);
                        } else {
                            add_fav.setImageResource(R.drawable.add_fav_not_xml);
                        }
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
    /**
     * the timer
     */
    private Timer timer;

    /**
     * min & sec
     */
    private int min = 0, sec = 0;

    /**
     * timer task
     */
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

        /**
         * indicates pause play
         */
        public static final int ACTION_PAUSE = 1;

        /**
         * indicates stop play
         */
        public static final int ACTION_STOP = 2;

        /**
         * indicates start play
         */
        public static final int ACTION_START = 3;

        /**
         * indicates play next
         */
        public static final int ACTION_NEXT = 4;

        /**
         * indicates play previous
         */
        public static final int ACTION_PRE = 5;

        /**
         * indicates play new
         */
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
            add_fav = (ImageView) findViewById(R.id.add_fav);
            add_fav.setOnClickListener(this);
        }

        {
            registerReceiver(mMusicPlaySateChangedReciver, new IntentFilter(
                    Contants.FILTER_PLAY_STATE_CHANGED));
        }

        {
            curSong = mSongsWrap.getModels().get(index);
            play_song_name.setText(curSong.getSongName());
            ablum_name.setText(curSong.getSingerName());
            nav_title.setText(curSong.getSongName());
            total_time.setText(MusicUtil.formatString(curSong.getMinutes(),
                    curSong.getSeconds()));
            if (curSong.getFav() == 1) {
                add_fav.setImageResource(R.drawable.add_fav_xml);
            } else {
                add_fav.setImageResource(R.drawable.add_fav_not_xml);
            }
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
            case R.id.add_fav:

                if (curSong.getFav() == 1) {

                    mDbOperator.executeSQL("update songs set fav =0 where file='"
                            + curSong.getFile() + "' ");
                    add_fav.setImageResource(R.drawable.add_fav_not_xml);
                    curSong.setFav(0);
                    markFav(curSong.getFile(), false);
                    sendMarkBroadcast(curSong, false);
                } else {
                    mDbOperator.executeSQL("update songs set fav =1 where file='"
                            + curSong.getFile() + "' ");
                    add_fav.setImageResource(R.drawable.add_fav_xml);
                    curSong.setFav(1);
                    markFav(curSong.getFile(), true);
                    sendMarkBroadcast(curSong, true);
                }
                break;
            default:
                break;
        }
    }

    /**
     * set current play mode
     * 
     * @param playMode value to be set
     */
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

    /**
     * play music
     */
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

    /**
     * send broadcast to service in order to change play mode
     * 
     * @param mode
     */
    private void sendPlayModeBroad(int mode) {
        Intent intent = new Intent();
        intent.setAction(Contants.FILTER_PLAY_MODE);
        intent.putExtra("mode", mode);
        sendBroadcast(intent);
        intent = null;

    }

    /**
     * start play service
     */
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

    /**
     * mark music fav or not
     * 
     * @param path the index
     * @param value mark value
     */
    private void markFav(String path, boolean value) {
        List<SongModel> songs = mSongsWrap.getModels();
        for (SongModel songModel : songs) {
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

    /**
     * when mark one music favorite or not, in order to keep UI display all
     * right.send broadcast to other component
     * 
     * @param songModel the changed model
     * @param the value to be set
     */
    private void sendMarkBroadcast(SongModel songModel, boolean value) {
        Intent intent = new Intent();
        intent.setAction(Contants.FILTER_ACTION_MARK_FAV);
        Bundle bundle = new Bundle();
        bundle.putSerializable("song", songModel);
        intent.putExtras(bundle);
        intent.putExtra("value", value);
        sendBroadcast(intent);
        bundle = null;
        intent = null;
    }
}
