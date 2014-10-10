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

package com.tcl.lzhang1.mymusic.ui.appwidgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.tcl.lzhang1.mymusic.AppContext;
import com.tcl.lzhang1.mymusic.Contants;
import com.tcl.lzhang1.mymusic.MusicUtil;
import com.tcl.lzhang1.mymusic.R;
import com.tcl.lzhang1.mymusic.model.SongModel;
import com.tcl.lzhang1.mymusic.model.SongsWrap;
import com.tcl.lzhang1.mymusic.service.MusicPlayService;
import com.tcl.lzhang1.mymusic.service.MusicPlayService.PlayState;
import com.tcl.lzhang1.mymusic.ui.MusicPlayActivity.PlayAction;
import com.tcl.lzhang1.mymusic.ui.SpalashActivity;

/**
 * This class is desktop widgets. <br>
 * implements functions as follows:
 * <ul>
 * pause & resume play music
 * <ul/>
 * <ul>
 * play next & previous music
 * <ul/>
 * 
 * @author leizhang
 */
public class MusicAppWidget extends AppWidgetProvider {
    /**
     * the Log TAG
     */
    private final String LOG_TAG = "om.tcl.lzhang1.mymusic.ui.appwidgets/MusicAppWidget";
    /**
     * launch splash activity
     */
    public static final int REQUESET_CODE = 101;
    /**
     * code play next music
     */
    public static final int REQUEST_CODE_PLAY_NEXT = 102;
    /**
     * code play previous music
     */
    public static final int REQUEST_CODE_PLAY_PRE = 103;
    /**
     * code play music
     */
    public static final int REQUEST_CODE_PLAY = 104;
    /**
     * code pause music
     */
    public static final int REQUEST_CODE_PAUSE = 105;
    /**
     * code play music
     */
    public static final int REQUEST_CODE_STOP = 106;
    /**
     * the remote views
     */
    private RemoteViews remoteViews;
    /**
     * rece <ProgressBar android:id="@+id/widget_play_progress"
     * style="?android:attr/progressBarStyleHorizontal"
     * android:layout_width="242.0dip" android:layout_height="2.0dip"
     * android:layout_alignParentBottom="true"
     * android:layout_marginBottom="5.0dip" android:indeterminate="false"
     * android:max="1000" android:progress="0"
     * android:progressDrawable="@drawable/widgetprogressbar_xml"
     * android:secondaryProgress="0" android:visibility="visible" />ived the
     * mode,when music playing state is changed,music play service will send
     * broadcast
     */
    private SongModel mSongModel = null;
    /**
     * used to updating the desktop widgets
     */
    private AppWidgetManager mAppWidgetManager = null;

    private AppContext mAppContext = null;

    /**
     * the broadcast receiver. process music play state.when music play state
     * changed, the music play service will send broadcast to it's receiver
     */
    boolean isRunning;

    @Override
    public void onReceive(Context context, Intent intent) {

        isRunning = MusicUtil.checkServiceIsRunning(context,
                MusicPlayService.class.getName());
        if (intent != null
                && (Contants.FILTER_PLAY_STATE_CHANGED.equals(intent.getAction()) || Contants.FILTER_ACTION_SEEK_UPDATED
                        .equals(intent.getAction()))) {
            Log.d(LOG_TAG, "appwidget receive broadcast..." + intent.getAction());
            // instance layout
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.musicwidget);
            mSongModel = (SongModel) intent.getSerializableExtra("model");

            // change the title
            if (null != mSongModel) {
                remoteViews.setTextViewText(R.id.title,
                        mSongModel.getSongName() + "-" + mSongModel.getSingerName());

            }

            if (Contants.FILTER_ACTION_SEEK_UPDATED
                    .equals(intent.getAction())) {
                int progress = (int) (intent.getFloatExtra("percent", 0.0f) * 100);
                Log.d(LOG_TAG, "progress:" + progress);
                mSongModel = (SongModel) intent.getSerializableExtra("song");
                remoteViews.setProgressBar(R.id.widget_play_progress, 100, progress, false);
                remoteViews.setTextViewText(R.id.title,
                        mSongModel.getSongName() + "-" + mSongModel.getSingerName());
            }

            // change button state,according to the state value
            int state = intent.getIntExtra("state", PlayState.PLAY_NEW);
            switch (state) {
                case PlayState.PLAY_PAUSED:
                    remoteViews.setImageViewResource(R.id.control_play,
                            R.drawable.app_widget_playbutton);
                    break;
                case PlayState.PLAY_STOPED:

                    break;
                case PlayState.PLAY_NEW:
                case PlayState.PLAY_RESUMED:
                    remoteViews.setImageViewResource(R.id.control_play,
                            R.drawable.app_widget_pausebutton);
                    break;

                default:
                    break;
            }

            // update widgets
            if (null == mAppWidgetManager)
                mAppWidgetManager = AppWidgetManager.getInstance(context);
            mAppWidgetManager.updateAppWidget(new ComponentName(context, MusicAppWidget.class),
                    remoteViews);
            remoteViews = null;
            int[] appWidgetIds = mAppWidgetManager.getAppWidgetIds(new ComponentName(context,
                    MusicAppWidget.class));
            onUpdate(context, mAppWidgetManager, appWidgetIds);
        } else {// let super to process default action
            super.onReceive(context, intent);
        }
    }

    /**
     * process widget app widgets update event
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Log.d(LOG_TAG, "on update,appid'len:" + appWidgetIds.length);
        // judge appids
        if (null == appWidgetIds || appWidgetIds.length == 0) {
            return;
        }

        mAppContext = (AppContext) context.getApplicationContext();

        Intent intent = null;
        PendingIntent pendingIntent = null;
        RemoteViews remoteViews = null;
        isRunning = MusicUtil.checkServiceIsRunning(context,
                MusicPlayService.class.getName());
        // loop for appWidgetIDs to update app widget
        for (int i = 0; i < appWidgetIds.length; i++) {
            // launch splash activity begin
            {
                intent = new Intent(context, SpalashActivity.class);
                pendingIntent = PendingIntent.getActivity(context, REQUESET_CODE, intent, 0);
                remoteViews = new RemoteViews(context.getPackageName(), R.layout.musicwidget);

                remoteViews.setOnClickPendingIntent(R.id.album_appwidget, pendingIntent);
            }
            // launch splash activity end

            // play next begin
            {
                if (isRunning) {
                    intent = new Intent();
                    intent.setAction(Contants.FILTER_WIDGET_PLAY_ACTION);
                    intent.putExtra("action", PlayAction.ACTION_NEXT);

                    pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_PLAY_NEXT,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                } else {

                    intent = new Intent(context, MusicPlayService.class);
                    int playIndex = mAppContext.getPlayIndex();
                    intent.putExtra("index", playIndex == (mAppContext.getSongs().size() - 1) ? 0
                            : playIndex++);
                    int time = mAppContext.getPlayTime();
                    intent.putExtra("time", time);
                    if (null != mAppContext && mAppContext.getSongs() != null) {
                        Log.d(LOG_TAG, "Application has global songs");
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("songs", new SongsWrap(mAppContext.getSongs()));
                        intent.putExtras(bundle);
                        bundle = null;
                    }
                    pendingIntent = PendingIntent.getService(context, 0,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                }
                remoteViews.setOnClickPendingIntent(R.id.control_next, pendingIntent);
            }
            // play next end

            // play pre begin
            {

                if (isRunning) {
                    intent = new Intent();
                    intent.setAction(Contants.FILTER_WIDGET_PLAY_ACTION);
                    intent.putExtra("action", PlayAction.ACTION_PRE);

                    pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_PLAY_PRE,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                } else
                {

                    intent = new Intent(context, MusicPlayService.class);
                    int playIndex = mAppContext.getPlayIndex();
                    intent.putExtra("index", playIndex > 1 ? playIndex : 0);
                    int time = mAppContext.getPlayTime();
                    intent.putExtra("time", time);
                    if (null != mAppContext && mAppContext.getSongs() != null) {
                        Log.d(LOG_TAG, "Application has global songs");
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("songs", new SongsWrap(mAppContext.getSongs()));
                        intent.putExtras(bundle);
                        bundle = null;
                    }
                    pendingIntent = PendingIntent.getService(context, 0, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                }
                remoteViews.setOnClickPendingIntent(R.id.control_previous, pendingIntent);
            }
            // play pre end
            // Log.d(LOG_TAG, "global songs:" + mAppContext.getSongs());
            // pause | play begin
            {
                intent = new Intent();

                if (isRunning) {
                    intent.setAction(Contants.FILTER_WIDGET_PLAY_ACTION);
                    intent.putExtra("action", PlayAction.ACTION_PAUSE);
                    pendingIntent = PendingIntent.getBroadcast(context,
                            REQUEST_CODE_PLAY, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                } else {

                    intent = new Intent(context, MusicPlayService.class);
                    int playIndex = mAppContext.getPlayIndex();
                    intent.putExtra("index", playIndex);
                    int time = mAppContext.getPlayTime();
                    intent.putExtra("time", time);
                    if (null != mAppContext && mAppContext.getSongs() != null) {
                        Log.d(LOG_TAG, "Application has global songs");
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("songs", new SongsWrap(mAppContext.getSongs()));
                        intent.putExtras(bundle);
                        bundle = null;
                    }
                    intent.setClass(context, MusicPlayService.class);
                    pendingIntent = PendingIntent.getService(context, 0, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                }
                remoteViews.setOnClickPendingIntent(R.id.control_play, pendingIntent);
            }
            // pause | play end

            appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);

            remoteViews = null;
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // Log.d(LOG_TAG, "on onDeleted,appid'len:" + appWidgetIds.length);
    }

    @Override
    public void onEnabled(Context context) {
        // Log.d(LOG_TAG, "on onEnabled,appid'len:");
    }

    @Override
    public void onDisabled(Context context) {
        // Log.d(LOG_TAG, "on onDisabled,appid'len:");
    }

}
