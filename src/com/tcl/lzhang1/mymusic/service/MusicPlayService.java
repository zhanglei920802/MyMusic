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

package com.tcl.lzhang1.mymusic.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.tcl.lzhang1.mymusic.Contants;
import com.tcl.lzhang1.mymusic.MusicUtil;
import com.tcl.lzhang1.mymusic.model.BaseModel;
import com.tcl.lzhang1.mymusic.model.SongModel;
import com.tcl.lzhang1.mymusic.model.SongsWrap;
import com.tcl.lzhang1.mymusic.ui.MusicPlayActivity.PlayAction;
import com.tcl.lzhang1.mymusic.ui.MusicPlayActivity.PlayMode;

/**
 * paly music service <br>
 * 
 * @author leizhang
 */
public class MusicPlayService extends Service {
    private static String LOG_TAG = "";

    // play tools
    private MediaPlayer mMusicMediaPlayer = null;
    private int play_index = 0;// the special index to play
    private List<SongModel> mSongs = null;
    private int timeToPlay = 0;// the special time to play

    private class LocalBinder extends Binder {
        MusicPlayService getService() {
            return MusicPlayService.this;
        }
    }

    // notification
    private NotificationManager mNotificationManager = null;
    private final int notification_id = 19920802;
    private Handler mHandler = new Handler();

    // start&pause's receiver
    private BroadcastReceiver mPlayActionBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "receive broadcast");
            // TODO Auto-generated method stub
            if (null != intent && Contants.FILTER_PLAY_ACTION.equals(intent.getAction())) {
                int action = intent.getIntExtra("action", 0);
                switch (action) {
                    case PlayAction.ACTION_PAUSE:
                        if (null != mMusicMediaPlayer) {
                            timeToPlay = mMusicMediaPlayer.getCurrentPosition();// save
                            Log.d(LOG_TAG, "request pause,cur pos is:" + timeToPlay);
                            mMusicMediaPlayer.pause();
                            sendBroadCast(null, PlayState.PLAY_PAUSED, 0, null);
                        }
                        break;
                    case PlayAction.ACTION_START:
                        if (null != mMusicMediaPlayer) {
                            Log.d(LOG_TAG, "recever broad cast to start play");
                            Log.d(LOG_TAG, "pause=start:isplaying:" + mMusicMediaPlayer.isPlaying());
                            mMusicMediaPlayer.start();
                            sendBroadCast(mSongs.get(play_index), PlayState.PLAY_RESUMED, 0, null);
                            // if (mMusicMediaPlayer.isPlaying())
                            // {
                            // Log.d(LOG_TAG, "pause=start:isplaying:" + true);
                            //
                            // } else {intent
                            // Log.d(LOG_TAG, "pause=start:isplaying:" + false);
                            // mMusicMediaPlayer.stop();
                            // mMusicMediaPlayer.reset();
                            // playMusic(play_index, timeToPlay);
                            // }

                        }
                        break;
                    case PlayAction.ACTION_STOP:
                        if (null != mMusicMediaPlayer) {
                            mMusicMediaPlayer.stop();
                            sendBroadCast(null, PlayState.PLAY_STOPED, 0, "play stoped");
                        }
                        break;
                    case PlayAction.ACTION_PRE:
                        if (null != mMusicMediaPlayer) {
                            if (mMusicMediaPlayer.isPlaying()) {
                                mMusicMediaPlayer.stop();
                                mMusicMediaPlayer.reset();
                                play_index--;
                                playMusic(play_index < 1 ? 0 : play_index, 0);
                            }
                        }
                        break;
                    case PlayAction.ACTION_NEXT:
                        if (null != mMusicMediaPlayer) {
                            if (mMusicMediaPlayer.isPlaying()) {
                                mMusicMediaPlayer.stop();
                                mMusicMediaPlayer.reset();
                                play_index++;
                                playMusic(play_index, 0);
                            }
                        }
                        break;
                    default:
                        break;
                }
            } else if (null != intent
                    && Contants.FILTER_WIDGET_PLAY_ACTION.equals(intent.getAction())) {
                /** app widgets */

                int action = intent.getIntExtra("action", 0);
                switch (action) {
                    case PlayAction.ACTION_PRE:
                        if (null != mMusicMediaPlayer) {
                            Log.d(LOG_TAG, "recever app widget's broad cast to start pre");
                            if (mMusicMediaPlayer.isPlaying()) {
                                mMusicMediaPlayer.stop();
                                mMusicMediaPlayer.reset();
                                play_index--;
                                playMusic(play_index < 1 ? 0 : play_index, 0);
                                sendBroadCast(mSongs.get(play_index), PlayState.PLAY_NEW, 0, null);
                            }
                        }
                        break;
                    case PlayAction.ACTION_NEXT:
                        if (null != mMusicMediaPlayer) {
                            Log.d(LOG_TAG, "recever app widget's broad cast to start next");
                            if (mMusicMediaPlayer.isPlaying()) {
                                mMusicMediaPlayer.stop();
                                mMusicMediaPlayer.reset();
                                play_index++;
                                playMusic(play_index, 0);
                                sendBroadCast(mSongs.get(play_index), PlayState.PLAY_NEW, 0, null);
                            }
                        }
                        break;
                    case PlayAction.ACTION_PAUSE:
                        if (null != mMusicMediaPlayer) {
                            // if is playing ,do pause it
                            if (mMusicMediaPlayer.isPlaying()) {
                                Log.d(LOG_TAG, "recever app widget's broad cast to start stop");
                                timeToPlay = mMusicMediaPlayer.getCurrentPosition();// save
                                Log.d(LOG_TAG, "request pause,cur pos is:" + timeToPlay);
                                mMusicMediaPlayer.pause();
                                sendBroadCast(mSongs.get(play_index), PlayState.PLAY_PAUSED, 0, null);
                            } else {

                                Log.d(LOG_TAG, "recever app widget's broad cast to start play");
                                mMusicMediaPlayer.start();
                                sendBroadCast(mSongs.get(play_index), PlayState.PLAY_RESUMED, 0,
                                        null);

                            }

                        }
                        break;
                    default:
                        break;
                }
            }
        }
    };

    // play mode receiver
    private int mPlayMode = PlayMode.MODE_REPEAT_ALL;
    private BroadcastReceiver mPlayModeBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (null != intent && Contants.FILTER_PLAY_MODE.equals(intent.getAction())) {
                switch (intent.getIntExtra("mode", PlayMode.MODE_REPEAT_ALL)) {
                    case PlayMode.MODE_REPEAT_ALL:
                        mPlayMode = PlayMode.MODE_REPEAT_ALL;
                        break;
                    case PlayMode.MODE_REPEAT_RANDOM:
                        if (mSongs == null || mSongs.isEmpty()) {
                            return;
                        }
                        mPlayMode = PlayMode.MODE_REPEAT_RANDOM;
                        int random = MusicUtil.getRandomInt(mSongs.size());
                        if (null != mMusicMediaPlayer) {
                            if (mMusicMediaPlayer.isPlaying()) {
                                mMusicMediaPlayer.stop();
                                mMusicMediaPlayer.reset();
                                play_index = random;
                                playMusic(random, 0);
                            }
                        }
                        break;
                    case PlayMode.MODE_REPEAT_SINGLE:
                        mPlayMode = PlayMode.MODE_REPEAT_SINGLE;
                        break;
                    default:
                        break;
                }
            }
        }
    };

    // play state
    /**
     * this interface define the music play state
     * 
     * @author leizhang
     */
    public interface PlayState {
        /**
         * music was stopped by user or music play service is crashed for other
         * causes
         */
        public static final int PLAY_STOPED = 1;
        /**
         * music was paused by user or new call is coming
         */
        public static final int PLAY_PAUSED = 2;
        /**
         * onCreate new music was played
         */
        public static final int PLAY_NEW = 3;
        /**
         * music was seeked,in case of UI update
         */
        public static final int PLAY_SEEK = 4;
        /**
         * music was played error
         */
        public static final int PLAY_ERROR = 5;
        /**
         * music was played end
         */
        public static final int PLAY_END = 6;
        /**
         * music playing was resumed
         */
        public static final int PLAY_RESUMED = 7;
    }

    // plug & unplug headset
    /**
     * this receiver is used to processing output devices unplug.
     */
    private BroadcastReceiver mHeadsetReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent != null
                    && AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                if (mMusicMediaPlayer.isPlaying()) {
                    Log.d(LOG_TAG, "external devices was output,will pause music playing .....");
                    mMusicMediaPlayer.pause();
                    // send paused broadcast
                    Log.d(LOG_TAG, "music playing was paused,send broadcast to UI thread......");
                    sendBroadCast(null, PlayState.PLAY_PAUSED, 0, null);
                }
            }
        }
    };

    /**
     * @author leizhang
     */
    private BroadcastReceiver mPhoneBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (Intent.ACTION_NEW_OUTGOING_CALL.equals(intent.getAction())) {

                Log.d(LOG_TAG, "receiver state :ACTION_NEW_OUTGOING_CALL");
            } else {
                TelephonyManager telephonyManager = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                switch (telephonyManager.getCallState()) {
                    case TelephonyManager.CALL_STATE_IDLE:// idle
                        Log.d(LOG_TAG, "receiver state :CALL_STATE_IDLE");
                        if (mMusicMediaPlayer != null) {
                            mMusicMediaPlayer.start();
                            sendBroadCast(mSongs.get(play_index), PlayState.PLAY_RESUMED, 0, null);
                        }

                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        if (mMusicMediaPlayer != null && mMusicMediaPlayer.isPlaying()) {
                            mMusicMediaPlayer.pause();
                            sendBroadCast(null, PlayState.PLAY_PAUSED, 0, null);
                        }
                        Log.d(LOG_TAG, "receiver state :CALL_STATE_OFFHOOK");
                        // calling
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:// ring
                        if (mMusicMediaPlayer.isPlaying()) {
                            mMusicMediaPlayer.pause();
                            sendBroadCast(null, PlayState.PLAY_PAUSED, 0, null);
                        }
                        Log.d(LOG_TAG, "receiver state :CALL_STATE_RINGING");
                        break;
                    default:

                        break;
                }
            }
        }
    };

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return new LocalBinder();
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        LOG_TAG = getPackageName() + "/" + MusicPlayService.class.getSimpleName();
        super.onCreate();
        mMusicMediaPlayer = new MediaPlayer();
        IntentFilter filter = new IntentFilter(Contants.FILTER_PLAY_ACTION);
        filter.addAction(Contants.FILTER_WIDGET_PLAY_ACTION);

        registerReceiver(mPlayActionBroadcastReceiver, filter);
        filter = new IntentFilter(Contants.FILTER_PLAY_MODE);
        registerReceiver(mPlayModeBroadcastReceiver, filter);
        registerReceiver(mHeadsetReceiver, new IntentFilter(
                AudioManager.ACTION_AUDIO_BECOMING_NOISY));
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        registerReceiver(mPhoneBroadcastReceiver, filter);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        unregisterReceiver(mPlayActionBroadcastReceiver);
        unregisterReceiver(mPlayModeBroadcastReceiver);
        unregisterReceiver(mHeadsetReceiver);
        unregisterReceiver(mPhoneBroadcastReceiver);
        if (mMusicMediaPlayer != null) {
            mMusicMediaPlayer.stop();
            mMusicMediaPlayer.reset();
            mMusicMediaPlayer.release();
        }
        sendBroadCast(null, PlayState.PLAY_STOPED, 0, "play stoped");
        super.onDestroy();
    }

    @Override
    @Deprecated
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub

        if (null == mMusicMediaPlayer) {
            mMusicMediaPlayer = new MediaPlayer();
        }
        if (null != intent) {
            mSongs = ((SongsWrap) intent.getSerializableExtra("songs")).getModels();
            play_index = intent.getIntExtra("index", 0);
            timeToPlay = intent.getIntExtra("time", 0);
        }
        if (null == mSongs) {
            mSongs = new ArrayList<SongModel>();
        }
        System.out.println("MusicPlayService.onStart()" + mSongs);
        // if (null != intent) {play_error
        // play_index = intent.getIntExtra("index", 0);
        // timeToPlay = intent.getIntExtra("time", 0);
        // }
        playMusic(play_index, timeToPlay);
        super.onStart(intent, startId);
    }

    @Override
    public void onRebind(Intent intent) {
        // TODO Auto-generated method stub
        super.onRebind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        return super.onUnbind(intent);
    }

    public void playMusic(final int position, final int time_position) {
        try {
            System.out.println("MusicPlayService.playMusic()   position:" + position);
            play_index = position;
            mMusicMediaPlayer.setDataSource(mSongs.get(position).getFile());
            mMusicMediaPlayer.prepareAsync();
            mMusicMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    mp.start();
                    sendBroadCast(mSongs.get(position), PlayState.PLAY_NEW, 0, null);
                    // createStatusBarNotification(mSongs.get(position).getSongName());
                    mp.seekTo(time_position);
                }
            });
            mMusicMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // TODO Auto-generated method stub
                    mp.reset();
                    mp.release();
                    sendBroadCast(null, PlayState.PLAY_ERROR, 0, "play error");
                    return false;
                }
            });
            mMusicMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    mp.reset();
                    if (mPlayMode == PlayMode.MODE_REPEAT_SINGLE) {
                        playMusic(position, 0);
                        sendBroadCast(mSongs.get(position), PlayState.PLAY_END, 0, null);
                    } else if (mPlayMode == PlayMode.MODE_REPEAT_ALL) {
                        playMusic(position + 1, 0);
                        sendBroadCast(mSongs.get(position + 1), PlayState.PLAY_END, 0, null);
                    } else if (mPlayMode == PlayMode.MODE_REPEAT_RANDOM) {
                        int randomIndex = MusicUtil.getRandomInt(mSongs.size());
                        playMusic(randomIndex, 0);
                        sendBroadCast(mSongs.get(randomIndex), PlayState.PLAY_END, 0, null);
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            sendBroadCast(null, PlayState.PLAY_ERROR, 0, "play error");
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            sendBroadCast(null, PlayState.PLAY_ERROR, 0, "play error");
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            sendBroadCast(null, PlayState.PLAY_ERROR, 0, "play error");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            sendBroadCast(null, PlayState.PLAY_ERROR, 0, "play error");
        }
    }

    /**
     * create notification
     * 
     * @param notificationContent
     */
    public void createStatusBarNotification(String notificationContent) {
        if (null == mNotificationManager) {
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        Notification notification = new Notification(0, notificationContent,
                System.currentTimeMillis());
        notification.defaults = Notification.DEFAULT_LIGHTS;
        notification.tickerText = notificationContent;
        mNotificationManager.notify(notification_id, notification);
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                mNotificationManager.cancel(notification_id);
            }
        }, 100 * 1000);
    }

    /**
     * send broadcast to other application,usually,was called when new music is
     * played or paused or stopped
     * 
     * @param model the new song model to be played
     * @param playState the new state {@linkplain PlayState}
     * @param time new time when user seeked
     */
    private void sendBroadCast(BaseModel model, int playState, int time, String errMsg) {
        Intent intent = new Intent(Contants.FILTER_PLAY_STATE_CHANGED);
        intent.putExtra("state", playState);
        if (null != model) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("model", model);
            intent.putExtras(bundle);
            bundle = null;
        }

        if (playState == PlayState.PLAY_SEEK) {
            intent.putExtra("time", time);
        }

        if (playState == PlayState.PLAY_ERROR) {
            intent.putExtra("errmsg", errMsg);
        }

        sendBroadcast(intent);
        intent = null;
    }

}
