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
import android.util.Log;
import android.widget.RemoteViews;

import com.tcl.lzhang1.mymusic.Contants;
import com.tcl.lzhang1.mymusic.R;
import com.tcl.lzhang1.mymusic.model.SongModel;
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
     * received the mode,when music playing state is changed,music play service
     * will send broadcast
     */
    private SongModel mSongModel = null;
    /**
     * used to updating the desktop widgets
     */
    private AppWidgetManager mAppWidgetManager = null;

    /**
     * the broadcast receiver. process music play state.when music play state
     * changed, the music play service will send broadcast to it's receiver
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "appwidget receive broadcast..." + intent.getAction());

        if (intent != null && Contants.FILTER_PLAY_STATE_CHANGED.equals(intent.getAction())) {
            // instance layout
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.musicwidget);
            mSongModel = (SongModel) intent.getSerializableExtra("model");
            if (null == mSongModel) {
                remoteViews = null;
                return;
            }
            // change the title
            remoteViews.setTextViewText(R.id.title,
                    mSongModel.getSongName() + "-" + mSongModel.getSingerName());

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

        } else {// let super to process default action
            super.onReceive(context, intent);
        }
    }

    /**
     * process widget app widgets update event
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(LOG_TAG, "on update,appid'len:" + appWidgetIds.length);
        // judge appids
        if (null == appWidgetIds || appWidgetIds.length == 0) {
            return;
        }

        Intent intent = null;
        PendingIntent pendingIntent = null;
        RemoteViews remoteViews = null;
        // loop for appWidgetIDs to update appwidget
        for (int i = 0; i < appWidgetIds.length; i++) {
            intent = new Intent(context, SpalashActivity.class);
            pendingIntent = PendingIntent.getActivity(context, REQUESET_CODE, intent, 0);
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.musicwidget);
            // launch splash activity
            remoteViews.setOnClickPendingIntent(R.id.album_appwidget, pendingIntent);

            // play next
            intent = new Intent();
            intent.setAction(Contants.FILTER_WIDGET_PLAY_ACTION);
            intent.putExtra("action", PlayAction.ACTION_NEXT);
            pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_PLAY_NEXT, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.control_next, pendingIntent);

            // play pre
            intent.setAction(Contants.FILTER_WIDGET_PLAY_ACTION);
            intent.putExtra("action", PlayAction.ACTION_PRE);
            pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_PLAY_PRE, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.control_previous, pendingIntent);

            // pause | play
            intent.setAction(Contants.FILTER_WIDGET_PLAY_ACTION);
            intent.putExtra("action", PlayAction.ACTION_PAUSE);
            pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_PLAY, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.control_play, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);

            remoteViews = null;
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.d(LOG_TAG, "on onDeleted,appid'len:" + appWidgetIds.length);
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(LOG_TAG, "on onEnabled,appid'len:");
    }

    @Override
    public void onDisabled(Context context) {
        Log.d(LOG_TAG, "on onDisabled,appid'len:");
    }

}
