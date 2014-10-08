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

import com.tcl.lzhang1.mymusic.ui.LoginActivity;
import com.tcl.lzhang1.mymusic.ui.MainActivity;
import com.tcl.lzhang1.mymusic.ui.MusicListAcitivity;
import com.tcl.lzhang1.mymusic.ui.MusicPlayActivity;
import com.tcl.lzhang1.mymusic.ui.MusicScanAcitivity;
import com.tcl.lzhang1.mymusic.ui.RegActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**
 * @author leizhang
 */
public class UIHelper {

    /**
     * show Main Activity
     * 
     * @param activity
     * @param bundle
     */
    public static void showMainActivity(Activity activity, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * show Music Scan Activity
     * 
     * @param contex
     * @param bundle
     */
    public static void showScanMusicActivity(Context contex, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(contex, MusicScanAcitivity.class);
        contex.startActivity(intent);
    }

    /**
     * show Music List Activity
     * 
     * @param context
     * @param bundle
     */
    public static void showMusicListActivity(Context context, Bundle bundle) {
        Intent intent = new Intent(context, MusicListAcitivity.class);
        if (null != bundle) {
            intent.putExtras(bundle);
        }

        context.startActivity(intent);
    }

    /**
     * show Music Play activity
     * 
     * @param context
     * @param bundle
     */
    public static void showMusicPlayActivity(Context context, Bundle bundle) {
        Intent intent = new Intent(context, MusicPlayActivity.class);
        if (null != bundle) {
            intent.putExtras(bundle);
        }

        context.startActivity(intent);
    }

    /**
     * @param context
     * @param bundle
     */
    public static void showRegActivity(Context context, Bundle bundle) {
        Intent intent = new Intent(context, RegActivity.class);
        if (null != bundle) {
            intent.putExtras(bundle);
        }

        context.startActivity(intent);
    }

    /**
     * @param context
     * @param bundle
     */
    public static void showLoginActivity(Context context, Bundle bundle) {
        Intent intent = new Intent(context, LoginActivity.class);
        if (null != bundle) {
            intent.putExtras(bundle);
        }

        context.startActivity(intent);
    }

    /**
     * toast
     * 
     * @param context context
     * @param res_id res_id
     */
    public static void toast(Context context, int res_id) {
        Toast.makeText(context, res_id, Toast.LENGTH_SHORT).show();

    }

    /**
     * toast
     * 
     * @param context context
     * @param text the text
     */
    public static void toast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}