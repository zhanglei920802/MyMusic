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

package com.tcl.lzhang1.mymusic;

import java.util.List;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.tcl.lzhang1.mymusic.db.DBOperator;
import com.tcl.lzhang1.mymusic.db.imp.SongImp;
import com.tcl.lzhang1.mymusic.db.imp.UserImp;
import com.tcl.lzhang1.mymusic.exception.SDCardUnMoutedException;
import com.tcl.lzhang1.mymusic.model.SongModel;
import com.tcl.lzhang1.mymusic.model.UserModel;
import com.tcl.lzhang1.mymusic.ui.MusicPlayActivity.PlayMode;

/**
 * Global Context <br>
 * For saving some global vars
 * 
 * @author leizhang
 */
public class AppContext extends Application {

    public static AppManager mAppManger;

    private List<SongModel> models;

    private String TAG = "AppContext";

    private int mPlayIndex = 0;

    private int mPlayTime = 0;

    private int playMode = PlayMode.MODE_REPEAT_ALL;

    public static UserModel sLoginUser = null;

    private DBOperator mUserOperator = null;

    public static boolean sRegSuccess = false;

    public List<SongModel> getSongs() {
        return models;
    }

    public int getPlayIndex() {
        return mPlayIndex;
    }

    public int getPlayMode() {
        return playMode;
    }

    /**
     * save play index
     * 
     * @param playIndex
     */
    public void savePlayIndex(int playIndex) {

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreferences.edit().putInt("playindex", playIndex).commit();
        mSharedPreferences = null;

    }

    public int getPlayTime() {
        return mPlayTime;
    }

    /**
     * save time of playing
     * 
     * @param playTime
     */
    public void savePlayTime(int playTime) {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreferences.edit().putInt("playtime", playTime).commit();
        mSharedPreferences = null;
    }

    public void savePlayMode(int playMode) {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.playMode = playMode;
        mSharedPreferences.edit().putInt("playmode", playMode).commit();
        mSharedPreferences = null;
    } //

    /*
     * (non-Javadoc)
     * @see android.app.Application#onCreate()
     */
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        Log.d(TAG, "Application creaed");
        super.onCreate();
        mAppManger = AppManager.getInstance();
        mUserOperator = new UserImp(this);
        scanMusic();
        readConfigs();

        Thread.setDefaultUncaughtExceptionHandler(AppException
                .getAppExceptionHandler());
    }

    /**
     * read configs
     */
    private void readConfigs() {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mPlayIndex = mSharedPreferences.getInt("playindex", 0);
        mPlayTime = mSharedPreferences.getInt("playtime", 0);
        playMode = mSharedPreferences.getInt("playmode", 0);
        String username = getLoginedUser();
        if (null != username) {
            @SuppressWarnings("unchecked")
            List<UserModel> users = (List<UserModel>) mUserOperator
                    .findAll("select * from user where user_id='" + username + "'");

            assert (users.size() == 1);
            sLoginUser = users.get(0);
            Log.d(TAG, "user[" + sLoginUser.getUserName() + "] have logined");
        }
        mSharedPreferences = null;
    }

    @SuppressWarnings("unchecked")
    private void scanMusic() {

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    // TODO Auto-generated method stub
                    DBOperator mDbOperator = new SongImp(AppContext.this);
                    models = (List<SongModel>) mDbOperator.sliptPage(1, UIHelper.PAGE_SIZE, null);
                    if (models != null && !models.isEmpty()) {
                        Log.d(TAG, "data base have datas");
                        return;
                    }
                    models = MusicUtil.scanMusic(AppContext.this);

                    Log.d(TAG, "scan music finished,send message .....");
                    mDbOperator.saveAll(models);
                } catch (SDCardUnMoutedException e) {
                    Log.d(TAG, "scan music failed:unmouted sdcard");
                } catch (Exception e) {
                    Log.d(TAG, "scan music error:" + e.getMessage());
                }
            }
        }).start();

    }

    /**
     * @return
     */
    public PackageInfo getPackageInfo() {
        // TODO Auto-generated method stub
        return getPackageInfo();
    }

    public void saveLoginUserPrefer(String user_id) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString("login_id", user_id);
        editor.commit();
        editor = null;
    }

    public String getLoginedUser() {
        return PreferenceManager.getDefaultSharedPreferences(this).getString("login_id", null);
    }

    public static boolean isLogined() {
        return sLoginUser != null
                && !TextUtils.isEmpty(sLoginUser.getUserName()) && sLoginUser.getIsLogin() == 1;
    }
}
