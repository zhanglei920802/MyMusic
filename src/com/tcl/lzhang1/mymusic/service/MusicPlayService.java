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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.tcl.lzhang1.mymusic.AppContext;
import com.tcl.lzhang1.mymusic.Contants;
import com.tcl.lzhang1.mymusic.MusicUtil;
import com.tcl.lzhang1.mymusic.R;
import com.tcl.lzhang1.mymusic.model.BaseModel;
import com.tcl.lzhang1.mymusic.model.SongModel;
import com.tcl.lzhang1.mymusic.model.SongsWrap;
import com.tcl.lzhang1.mymusic.ui.MusicPlayActivity.PlayAction;
import com.tcl.lzhang1.mymusic.ui.MusicPlayActivity.PlayMode;
import com.tcl.lzhang1.mymusic.ui.SpalashActivity;

/**
 * play music service <br>
 * <br/>
 * <b> update play progress <b/> Because service always run long time
 * whileLog.d(LOG_TAG, "min=" + min + " sec=" + sec + ""); activity will exit
 * when user click back menu,so I create a timer & timer task, it will be
 * trigger the run method in period of 1s,it will send broadcast to UI
 * component,so UI component can update. <br/>
 * <br/>
 * <b>music play list changed process<b/> Because play list is displayed by
 * split,so,when a new page was added or a refresh operation occurred,we need to
 * broadcast to other component in case of exception occurred.so I registered a
 * broadcast in component which need to know music play list's state.
 * 
 * @author leizhang
 */
public class MusicPlayService extends Service {
	/**
	 * the log_tag
	 */
	private static String LOG_TAG = "";

	// play tools
	/**
	 * the media player
	 */
	private MediaPlayer mMusicMediaPlayer = null;

	/**
	 * current play index
	 */
	private static int play_index = 0;// the special index to play

	/**
	 * Log.d(LOG_TAG, "min=" + min + " sec=" + sec + ""); the songs to be play
	 */
	private List<SongModel> mSongs = null;

	/**
	 * the time to play
	 */
	private int timeToPlay = 0;// the special time to play

	/**
	 * local binder
	 * 
	 * @author leizhang
	 */
	private class LocalBinder extends Binder {
		MusicPlayService getService() {
			return MusicPlayService.this;
		}
	}

	// notification
	/**
	 * notification manager
	 */
	private NotificationManager mNotificationManager = null;

	/**
	 * notification id
	 */
	private final int notification_id = 19920802;

	/**
	 * the hander
	 */
	private Handler mHandler = new Handler() {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				// Log.d(LOG_TAG, "thread ID:" +
				// Thread.currentThread().getId());
				// Log.d(LOG_TAG, "min=" + min + " sec=" + sec + "");
				// send broadcast
				if (mMusicMediaPlayer != null && mMusicMediaPlayer.isPlaying()) {
					// sec++;
					setSec(getSec() + 1);
					if (getSec() % 60 == 0) {
						setMin(getMin() + 1);

						setSec(0);
					}

					SongModel curSong = mSongs.get(play_index);
					int playedTime = getMin() * 60 + getSec();
					float percent = (playedTime)
							/ ((float) (curSong.getTime() / 1000));
					sendProgressedChangedBroadcast(playedTime, curSong,
							percent, MusicUtil.formatString(getMin(), getSec()));
				}
				break;

			default:
				break;
			}

		}

	};

	// configs
	/**
	 * the application context
	 */
	private AppContext mAppContext = null;
	// start&pause's receiver
	private BroadcastReceiver mPlayActionBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "receive broadcast");
			// TODO Auto-generated method stub
			if (null != intent
					&& Contants.FILTER_PLAY_ACTION.equals(intent.getAction())) {
				int action = intent.getIntExtra("action", 0);
				switch (action) {
				case PlayAction.ACTION_PAUSE:
					if (null != mMusicMediaPlayer) {
						timeToPlay = mMusicMediaPlayer.getCurrentPosition();// save
						Log.d(LOG_TAG, "request pause,cur pos is:" + timeToPlay);
						mMusicMediaPlayer.pause();
						sendStateBroadCast(MusicPlayService.this,
								mSongs.get(play_index), PlayState.PLAY_PAUSED,
								0, null);
						createStatusBarNotification(false);
					}
					break;
				case PlayAction.ACTION_NEW:
					if (null != mMusicMediaPlayer) {
						if (mMusicMediaPlayer.isPlaying()) {
							mMusicMediaPlayer.stop();
							mMusicMediaPlayer.reset();
							play_index = intent.getIntExtra("index", 0);
							timeToPlay = intent.getIntExtra("time", 0);
							playMusic(play_index, 0);
						}
						createStatusBarNotification(true);
					}
					break;

				case PlayAction.ACTION_START:
					if (null != mMusicMediaPlayer) {
						Log.d(LOG_TAG, "recever broad cast to start play");
						Log.d(LOG_TAG, "pause=start:isplaying:"
								+ mMusicMediaPlayer.isPlaying());
						mMusicMediaPlayer.start();
						createStatusBarNotification(true);
						sendStateBroadCast(MusicPlayService.this,
								mSongs.get(play_index), PlayState.PLAY_RESUMED,
								0, null);
						// if (mMusicMediaPlayer.isPlaying())
						// {
						// Log.d(LOG_TAG, "pause=start:isplaying:" + true);
						//
						// } else {intent
						// Log.d(LOG_TAGPLAY_SEEK, "pause=start:isplaying:"
						// + false);
						// mMusicMediaPlayer.stop();
						// mMusicMediaPlayer.reset();
						// playMusic(play_index, timeToPlay);
						// }

					}
					break;
				case PlayAction.ACTION_STOP:
					if (null != mMusicMediaPlayer) {
						mMusicMediaPlayer.stop();
						sendStateBroadCast(MusicPlayService.this, null,
								PlayState.PLAY_STOPED, 0, "play stoped");
						mNotificationManager.cancelAll();
						sendAppExitBroadCast();
					}
					break;
				case PlayAction.ACTION_PRE:
					if (null != mMusicMediaPlayer) {
						// no condition to play next
						// if (mMusicMediaPlayer.isPlaying()) {
						mMusicMediaPlayer.stop();
						mMusicMediaPlayer.reset();
						switch (mPlayMode) {
						case PlayMode.MODE_REPEAT_ALL:
							play_index--;
							break;
						case PlayMode.MODE_REPEAT_RANDOM:// random
															// play
															// music

							// break;
						case PlayMode.MODE_REPEAT_SINGLE:// random
															// play
															// music
							play_index = MusicUtil.getRandomInt(mSongs.size());
							break;
						default:
							break;
						}
						createStatusBarNotification(true);
						playMusic(play_index < 1 ? 0 : play_index, 0);
						// }
					}
					break;
				case PlayAction.ACTION_NEXT:
					if (null != mMusicMediaPlayer) {
						// no condition to play next
						// if (mMusicMediaPlayer.isPlaying()) {
						mMusicMediaPlayer.stop();
						mMusicMediaPlayer.reset();
						switch (mPlayMode) {
						case PlayMode.MODE_REPEAT_ALL:
							play_index++;
							break;
						case PlayMode.MODE_REPEAT_RANDOM:

							// break;
						case PlayMode.MODE_REPEAT_SINGLE:
							play_index = MusicUtil.getRandomInt(mSongs.size());
							break;
						default:
							break;
						}
						createStatusBarNotification(true);
						playMusic(getValidPlayIndex(play_index), 0);
						// }
					}
					break;
				case PlayAction.ACTION_SEEK:// seek the play
				{
					// Log.d(LOG_TAG, "thread ID:" +
					// Thread.currentThread().getId());
					Log.d(LOG_TAG, "min=" + min + " sec=" + sec + "");
					if (null != mMusicMediaPlayer) {
						int seekedValue = intent.getIntExtra("seekvalue", 0);
						Log.d(LOG_TAG, "ACTION_SEEK.seekedValue=" + seekedValue);
						if (0 == seekedValue)
							return;
						int playedTime = intent.getIntExtra("playedtime", 0);
						Log.d(LOG_TAG, "playedTime:" + playedTime);
						setMin(((int) playedTime % 3600) / 60);
						setSec(((int) playedTime % 3600) % 60);
						mMusicMediaPlayer.seekTo(seekedValue);
						createStatusBarNotification(true);
					}
				}
					break;
				default:
					break;
				}
			} else if (null != intent
					&& Contants.FILTER_WIDGET_PLAY_ACTION.equals(intent
							.getAction())) {
				/** app widgets */

				int action = intent.getIntExtra("action", 0);
				switch (action) {
				case PlayAction.ACTION_PRE:
					if (null != mMusicMediaPlayer) {
						Log.d(LOG_TAG,
								"recever app widget's broad cast to start pre");
						// no condition to play next
						// if (mMusicMediaPlayer.isPlaying()) {
						mMusicMediaPlayer.stop();
						mMusicMediaPlayer.reset();
						switch (mPlayMode) {
						case PlayMode.MODE_REPEAT_ALL:
							play_index--;
							break;
						case PlayMode.MODE_REPEAT_RANDOM:
							play_index = MusicUtil.getRandomInt(mSongs.size());
							break;
						case PlayMode.MODE_REPEAT_SINGLE:

							break;
						default:
							break;
						}

						playMusic(play_index < 1 ? 0 : play_index, 0);
						// sendBroadCast(mSongs.get(play_index),
						// PlayState.PLAY_NEW, 0, null);
						// }
						createStatusBarNotification(true);
					}
					break;
				case PlayAction.ACTION_NEXT:
					if (null != mMusicMediaPlayer) {
						Log.d(LOG_TAG,
								"recever app widget's broad cast to start next");
						// no condition to play next
						// if (mMusicMediaPlayer.isPlaying()) {
						mMusicMediaPlayer.stop();
						mMusicMediaPlayer.reset();
						switch (mPlayMode) {
						case PlayMode.MODE_REPEAT_ALL:
							play_index++;
							break;
						case PlayMode.MODE_REPEAT_RANDOM:
							play_index = MusicUtil.getRandomInt(mSongs.size());
							break;
						case PlayMode.MODE_REPEAT_SINGLE:

							break;
						default:
							break;
						}

						playMusic(play_index, 0);
						// sendBroadCast(mSongs.get(play_index),
						// PlayState.PLAY_NEW, 0, null);
						// }
						createStatusBarNotification(true);
					}
					if (null != mMusicMediaPlayer) {
						int seekedValue = intent.getIntExtra("seekvalue", 0);
						Log.d(LOG_TAG, "ACTION_SEEK.seekedValue=" + seekedValue);
						if (0 == seekedValue)
							return;
						int playedTime = intent.getIntExtra("playedtime", 0);
						Log.d(LOG_TAG, "playedTime:" + playedTime);
						setMin(((int) playedTime % 3600) / 60);
						setSec(((int) playedTime % 3600) % 60);
						mMusicMediaPlayer.seekTo(seekedValue);
						createStatusBarNotification(true);
					}

					break;
				case PlayAction.ACTION_PAUSE:
					if (null != mMusicMediaPlayer) {
						// if is playing ,do pause it
						if (mMusicMediaPlayer.isPlaying()) {
							Log.d(LOG_TAG,
									"recever app widget's broad cast to start stop");
							timeToPlay = mMusicMediaPlayer.getCurrentPosition();// save
							Log.d(LOG_TAG, "request pause,cur pos is:"
									+ timeToPlay);
							mMusicMediaPlayer.pause();
							sendStateBroadCast(MusicPlayService.this,
									mSongs.get(play_index),
									PlayState.PLAY_PAUSED, 0, null);
							createStatusBarNotification(false);
						} else {

							Log.d(LOG_TAG,
									"recever app widget's broad cast to start play");
							mMusicMediaPlayer.start();
							sendStateBroadCast(MusicPlayService.this,
									mSongs.get(play_index),
									PlayState.PLAY_RESUMED, 0, null);
							Log.d(LOG_TAG, "views:" + views);
							createStatusBarNotification(true);

						}
						if (null != mMusicMediaPlayer) {
							int seekedValue = intent
									.getIntExtra("seekvalue", 0);
							Log.d(LOG_TAG, "ACTION_SEEK.seekedValue="
									+ seekedValue);
							if (0 == seekedValue)
								return;
							int playedTime = intent
									.getIntExtra("playedtime", 0);
							Log.d(LOG_TAG, "playedTime:" + playedTime);
							setMin(((int) playedTime % 3600) / 60);
							setSec(((int) playedTime % 3600) % 60);
							mMusicMediaPlayer.seekTo(seekedValue);
							createStatusBarNotification(true);
						}

					}
					break;
				case PlayAction.ACTION_STOP:
					if (null != mMusicMediaPlayer) {
						mMusicMediaPlayer.stop();
						sendStateBroadCast(MusicPlayService.this, null,
								PlayState.PLAY_STOPED, 0, "play stoped");
						mNotificationManager.cancelAll();
						sendAppExitBroadCast();
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
			if (null != intent
					&& Contants.FILTER_PLAY_MODE.equals(intent.getAction())) {
				switch (intent.getIntExtra("mode", PlayMode.MODE_REPEAT_ALL)) {
				case PlayMode.MODE_REPEAT_ALL:
					mPlayMode = PlayMode.MODE_REPEAT_ALL;
					break;
				case PlayMode.MODE_REPEAT_RANDOM:
					mPlayMode = PlayMode.MODE_REPEAT_RANDOM;

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

	/**
     * 
     */
	private void randomPlay() {
		if (mSongs == null || mSongs.isEmpty()) {
			return;
		}

		int random = MusicUtil.getRandomInt(mSongs.size());
		if (null != mMusicMediaPlayer) {
			if (mMusicMediaPlayer.isPlaying()) {
				mMusicMediaPlayer.stop();
				mMusicMediaPlayer.reset();
				play_index = random;
				playMusic(random, 0);
			}
		}
	}

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
		/**
		 * the play list has no songs
		 */
		public static final int NO_SONGS = 8;
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
					&& AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent
							.getAction())) {
				if (mMusicMediaPlayer.isPlaying()) {
					Log.d(LOG_TAG,
							"external devices was output,will pause music playing .....");
					mMusicMediaPlayer.pause();
					// send paused broadcast
					Log.d(LOG_TAG,
							"music playing was paused,send broadcast to UI thread......");
					sendStateBroadCast(MusicPlayService.this, null,
							PlayState.PLAY_PAUSED, 0, null);
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
						sendStateBroadCast(MusicPlayService.this,
								mSongs.get(play_index), PlayState.PLAY_RESUMED,
								0, null);
					}

					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					if (mMusicMediaPlayer != null
							&& mMusicMediaPlayer.isPlaying()) {
						mMusicMediaPlayer.pause();
						sendStateBroadCast(MusicPlayService.this, null,
								PlayState.PLAY_PAUSED, 0, null);
					}
					Log.d(LOG_TAG, "receiver state :CALL_STATE_OFFHOOK");
					// calling
					break;
				case TelephonyManager.CALL_STATE_RINGING:// ring
					if (mMusicMediaPlayer.isPlaying()) {
						mMusicMediaPlayer.pause();
						sendStateBroadCast(MusicPlayService.this, null,
								PlayState.PLAY_PAUSED, 0, null);
					}
					Log.d(LOG_TAG, "receiver state :CALL_STATE_RINGING");
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
			if (null != intent
					&& Contants.FILTER_ACTION_MARK_FAV.equals(intent
							.getAction())) {
				SongModel model = (SongModel) intent
						.getSerializableExtra("song");
				boolean value = intent.getBooleanExtra("value", false);
				if (model != null) {
					markFav(model.getFile(), value);
				}
			}
		};
	};

	/**
	 * the timer
	 */
	private Timer mTimer;

	/**
	 * min & sec
	 */
	private int min = 0, sec = 0;

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		// Log.d(LOG_TAG, "old value:" + getMin() + "  new value:" + min + "");
		this.min = min;
	}

	public int getSec() {
		return sec;
	}

	public void setSec(int sec) {
		// Log.d(LOG_TAG, "old value:" + getSec() + "  new value:" + sec + "");
		this.sec = sec;
	}

	/**
	 * timer task
	 */
	private TimerTask mTimerTask = new TimerTask() {

		@Override
		public void run() {
			mHandler.sendEmptyMessage(1);
		}
	};

	/**
     * 
     */
	private BroadcastReceiver mPlayListChanedReciever = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (null != intent
					&& Contants.FILTER_ACTION_PLAY_LIST_CHANGED.equals(intent
							.getAction())) {
				SongsWrap songsWrap = (SongsWrap) intent
						.getSerializableExtra("songs");
				if (null != songsWrap) {
					mSongs.clear();
					mSongs.addAll(songsWrap.getModels());
				}

				if (mSongs.isEmpty()) {
					stopSelf();
				}
			}
		}
	};
	
	/**
	 * this receiver is created for app exit
	 */
	private BroadcastReceiver mAppExitReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent != null
					&& Contants.FILTER_ACTION_APP_EXIT.equals(intent
							.getAction())) {
				AppContext.mAppManger.AppExit(getApplication());
				NotificationManager manager =(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
				manager.cancelAll();
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
		Log.d(LOG_TAG, "service create");
		LOG_TAG = getPackageName() + "/"
				+ MusicPlayService.class.getSimpleName();
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
		registerReceiver(mFavBroadcastReceiver, new IntentFilter(
				Contants.FILTER_ACTION_MARK_FAV));
		registerReceiver(mPlayListChanedReciever, new IntentFilter(
				Contants.FILTER_ACTION_PLAY_LIST_CHANGED));
		registerReceiver(mAppExitReceiver, new IntentFilter(Contants.FILTER_ACTION_APP_EXIT));
		
		mAppContext = (AppContext) getApplication();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub

		unregisterReceiver(mPlayActionBroadcastReceiver);
		unregisterReceiver(mPlayModeBroadcastReceiver);
		unregisterReceiver(mHeadsetReceiver);
		unregisterReceiver(mPhoneBroadcastReceiver);
		unregisterReceiver(mFavBroadcastReceiver);
		unregisterReceiver(mPlayListChanedReciever);
		unregisterReceiver(mAppExitReceiver);
		
		sendStateBroadCast(MusicPlayService.this, null, PlayState.PLAY_STOPED,
				0, "play stoped");
		if (mMusicMediaPlayer != null) {
			mMusicMediaPlayer.stop();
			mMusicMediaPlayer.reset();
			mMusicMediaPlayer.release();
			mMusicMediaPlayer = null;
		}

		mAppContext.savePlayIndex(play_index);
		mAppContext.savePlayTime(timeToPlay);
		mAppContext.savePlayMode(mPlayMode);

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
			mSongs = ((SongsWrap) intent.getSerializableExtra("songs"))
					.getModels();
			Log.d(LOG_TAG, "music play service created:");
			play_index = intent.getIntExtra("index", 0);
			timeToPlay = intent.getIntExtra("time", 0);
		}
		if (null == mSongs || mSongs.isEmpty()) {
			Log.d(LOG_TAG, "empty play list");
			sendStateBroadCast(MusicPlayService.this, null, PlayState.NO_SONGS,
					0, null);
			stopSelf();
			return;
		}

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

	/**
	 * play music
	 * 
	 * @param position
	 * @param time_position
	 */
	public void playMusic(final int position, final int time_position) {
		try {
			Log.d(LOG_TAG, "play song:" + mSongs.get(position));
			play_index = getValidPlayIndex(position);

			mMusicMediaPlayer.setDataSource(mSongs.get(position).getFile());
			mMusicMediaPlayer.prepareAsync();
			mMusicMediaPlayer
					.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

						@Override
						public void onPrepared(MediaPlayer mp) {

							// TODO Auto-generated method stub
							mp.start();
							sendStateBroadCast(MusicPlayService.this,
									mSongs.get(position), PlayState.PLAY_NEW,
									0, null);
							// createStatusBarNotification(mSongs.get(position).getSongName());
							mp.seekTo(time_position);

							// progress
							if (null == mTimer) {
								mTimer = new Timer();
								mTimer.schedule(mTimerTask, 0, 1000);
							} else {
								min = 0;
								sec = 0;
							}
							createStatusBarNotification(true);
						}
					});
			mMusicMediaPlayer
					.setOnErrorListener(new MediaPlayer.OnErrorListener() {

						@Override
						public boolean onError(MediaPlayer mp, int what,
								int extra) {
							// TODO Auto-generated method stub
							mp.reset();
							mp.release();
							sendStateBroadCast(MusicPlayService.this, null,
									PlayState.PLAY_ERROR, 0, "play error");
							return false;
						}
					});
			mMusicMediaPlayer
					.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

						@Override
						public void onCompletion(MediaPlayer mp) {
							// TODO Auto-generated method stub
							mp.reset();

							// play mode
							if (mPlayMode == PlayMode.MODE_REPEAT_SINGLE) {
								playMusic(position, 0);
								sendStateBroadCast(
										MusicPlayService.this,
										mSongs.get(getValidPlayIndex(position)),
										PlayState.PLAY_END, 0, null);
							} else if (mPlayMode == PlayMode.MODE_REPEAT_ALL) {
								playMusic(getValidPlayIndex(position + 1), 0);
								sendStateBroadCast(MusicPlayService.this,
										mSongs.get(position + 1),
										PlayState.PLAY_END, 0, null);
							} else if (mPlayMode == PlayMode.MODE_REPEAT_RANDOM) {
								int randomIndex = MusicUtil.getRandomInt(mSongs
										.size());
								playMusic(randomIndex, 0);
								sendStateBroadCast(MusicPlayService.this,
										mSongs.get(randomIndex),
										PlayState.PLAY_END, 0, null);
							}

						}
					});
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sendStateBroadCast(MusicPlayService.this, null,
					PlayState.PLAY_ERROR, 0, "play error");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sendStateBroadCast(MusicPlayService.this, null,
					PlayState.PLAY_ERROR, 0, "play error");
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sendStateBroadCast(MusicPlayService.this, null,
					PlayState.PLAY_ERROR, 0, "play error");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sendStateBroadCast(MusicPlayService.this, null,
					PlayState.PLAY_ERROR, 0, "play error");
		}
	}

	private RemoteViews views = null;

	/**
	 * create notification
	 * 
	 * @param notificationContent
	 */
	@SuppressWarnings("deprecation")
	public void createStatusBarNotification(boolean isPlaying) {
		if (null == mNotificationManager) {
			mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		}
		SongModel model = mSongs.get(play_index);
		if (null == mNotificationManager) {
			mNotificationManager.cancelAll();
			return;
		}

		Log.d(LOG_TAG, "song :" + model.toString());
		Notification notification = new Notification(
				R.drawable.icon_notification, model.getSongName() + "-"
						+ model.getSingerName(), System.currentTimeMillis());
		views = new RemoteViews(getPackageName(), R.layout.statusbar_new);
		notification.contentView = views;
		views.setTextViewText(R.id.trackname,
				model.getSongName() + "-" + model.getSingerName());
		views.setTextViewText(R.id.artistalbum, model.getAblumName());

		// launch
		Intent intent = new Intent(this, SpalashActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, 0);
		views.setOnClickPendingIntent(R.id.toppart, pendingIntent);

		// play next
		intent = new Intent();
		intent.setAction(Contants.FILTER_WIDGET_PLAY_ACTION);
		intent.putExtra("action", PlayAction.ACTION_NEXT);
		pendingIntent = PendingIntent.getBroadcast(this, 101, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		if (isPlaying)
			views.setImageViewResource(R.id.btnPause,
					R.drawable.notification_pause);
		else
			views.setImageViewResource(R.id.btnPause,
					R.drawable.notification_play);
		views.setOnClickPendingIntent(R.id.btnNext, pendingIntent);

		// pause & start
		intent.setAction(Contants.FILTER_WIDGET_PLAY_ACTION);
		intent.putExtra("action", PlayAction.ACTION_PAUSE);
		pendingIntent = PendingIntent.getBroadcast(MusicPlayService.this, 103,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.btnPause, pendingIntent);

		// exit
		intent.setAction(Contants.FILTER_WIDGET_PLAY_ACTION);
		intent.putExtra("action", PlayAction.ACTION_STOP);
		pendingIntent = PendingIntent.getBroadcast(MusicPlayService.this, 104,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.btnExit, pendingIntent);

		// notify
		pendingIntent = PendingIntent.getBroadcast(MusicPlayService.this, 102,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.flags = Notification.FLAG_NO_CLEAR;
		mNotificationManager.notify(notification_id, notification);
	}

	/**
	 * send broadcast to other application,usually,was called when new music is
	 * played or paused or stopped
	 * 
	 * @param model
	 *            the new song model to be played
	 * @param playState
	 *            the new state {@linkplain PlayState}
	 * @param time
	 *            new time when user seeked
	 */
	public static void sendStateBroadCast(Context context, BaseModel model,
			int playState, int time, String errMsg) {
		Intent intent = new Intent(Contants.FILTER_PLAY_STATE_CHANGED);
		intent.putExtra("state", playState);
		if (null != model) {
			Bundle bundle = new Bundle();
			bundle.putSerializable("model", model);
			intent.putExtras(bundle);
			bundle = null;
		}
		intent.putExtra("index", play_index);
		if (playState == PlayState.PLAY_SEEK) {
			intent.putExtra("time", time);
		}

		if (playState == PlayState.PLAY_ERROR) {
			intent.putExtra("errmsg", errMsg);
		}

		context.sendBroadcast(intent);

		Log.d(LOG_TAG, "send play state changed broadcast.");
		intent = null;
	}

	/**
	 * mark music fav or not
	 * 
	 * @param path
	 *            the index
	 * @param value
	 *            mark value
	 */
	private void markFav(String path, boolean value) {

		for (SongModel songModel : mSongs) {
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
	 * send broadcast to other component to update UI when progress changed
	 * 
	 * @param currentPlayTime
	 *            time have played
	 * @param cursong
	 *            the song current played
	 * @param curPercent
	 *            the percent have played
	 * @param displayStr
	 *            the str which will displayed in start textview
	 */
	private void sendProgressedChangedBroadcast(int currentPlayTime,
			BaseModel cursong, float curPercent, String displayStr) {
		Intent intent = new Intent();
		intent.setAction(Contants.FILTER_ACTION_SEEK_UPDATED);

		Bundle bundle = new Bundle();
		bundle.putSerializable("song", cursong);
		bundle.putInt("playtime", currentPlayTime);
		bundle.putFloat("percent", curPercent);
		bundle.putString("displaystr", displayStr);
		intent.putExtras(bundle);
		bundle = null;

		sendBroadcast(intent);
		intent = null;
	}

	/**
	 * because play list will changed for refresh or load.so check the play
	 * index
	 * 
	 * @param index
	 *            the index will be played
	 * @return if index is valid return it ,return 0 if not
	 */
	public int getValidPlayIndex(int index) {
		if (index > 0 && index < mSongs.size() - 1) {
			return index;
		} else {
			return 0;
		}
	}

	/**
	 * send application exit broadcast
	 */
	public void sendAppExitBroadCast() {
		Intent intent = new Intent();
		intent.setAction(Contants.FILTER_ACTION_APP_EXIT);
		sendBroadcast(intent);
	}
}
