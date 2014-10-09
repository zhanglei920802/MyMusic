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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**
 * @author leizhang
 */
public class UIHelper {
    /** The Constant LISTVIEW_ACTION_INIT. */
    public final static int LISTVIEW_ACTION_INIT = 0x01;

    /** The Constant LISTVIEW_ACTION_REFRESH. */
    public final static int LISTVIEW_ACTION_REFRESH = 0x02;

    /** The Constant LISTVIEW_ACTION_SCROLL. */
    public final static int LISTVIEW_ACTION_SCROLL = 0x03;

    /** The Constant LISTVIEW_ACTION_CHANGE_CATALOG. */
    public final static int LISTVIEW_ACTION_CHANGE_CATALOG = 0x04;

    /** The Constant LISTVIEW_DATA_MORE. */
    public final static int LISTVIEW_DATA_MORE = 0x01;

    /** The Constant LISTVIEW_DATA_LOADING. */
    public final static int LISTVIEW_DATA_LOADING = 0x02;

    /** The Constant LISTVIEW_DATA_FULL. */
    public final static int LISTVIEW_DATA_FULL = 0x03;

    /** The Constant LISTVIEW_DATA_EMPTY. */
    public final static int LISTVIEW_DATA_EMPTY = 0x04;

    /**
     * the pagesize
     */
    public final static int PAGE_SIZE = 20;

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
    public static void showMusicListActivity(Activity context, Bundle bundle) {
        Intent intent = new Intent(context, MusicListAcitivity.class);
        if (null != bundle) {
            intent.putExtras(bundle);
        }

        context.startActivity(intent);
        context.overridePendingTransition(R.anim.push_up_in, R.anim.no_vertical_tanslation);
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
    public static void showRegActivity(Activity context, Bundle bundle) {
        Intent intent = new Intent(context, RegActivity.class);
        if (null != bundle) {
            intent.putExtras(bundle);
        }

        context.startActivity(intent);
        context.overridePendingTransition(R.anim.push_right_in, R.anim.no_horizontal_translation);
    }

    /**
     * @param context
     * @param bundle
     */
    public static void showLoginActivity(Activity context, Bundle bundle) {
        Intent intent = new Intent(context, LoginActivity.class);
        if (null != bundle) {
            intent.putExtras(bundle);
        }

        context.startActivity(intent);
        context.overridePendingTransition(R.anim.push_up_in, R.anim.no_vertical_tanslation);
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

    /**
     * 发送App异常崩溃报告.
     * 
     * @param cont the cont
     * @param crashReport the crash report
     */
    public static void sendAppCrashReport(final Context cont,
            final String crashReport) {
        AlertDialog.Builder builder = new AlertDialog.Builder(cont);

        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle("程序出现出现异常");
        builder.setMessage("很抱歉，应用程序出现错误，即将退出。\n请提交错误报告，我们会尽快修复这个问题！");
        builder.setPositiveButton("提交报告",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // 发送异常报告

                        Intent i = new Intent(Intent.ACTION_SEND);
                        // i.setType("text/plain"); //模拟器

                        i.setType("message/rfc822"); // 真机

                        i.putExtra(Intent.EXTRA_EMAIL,
                                new String[] {
                                    "794857063@qq.com"
                                });
                        i.putExtra(Intent.EXTRA_SUBJECT, " 错误报告");
                        i.putExtra(Intent.EXTRA_TEXT, crashReport);
                        cont.startActivity(Intent.createChooser(i, "发送错误报告"));
                        // 退出

                        AppManager.getInstance().AppExit(cont);
                    }
                });
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // 退出

                AppManager.getInstance().AppExit(cont);
            }
        });
        builder.create().show();
    }

    /**
     * send play list changed listener
     * 
     * @param intent
     * @param context
     */
    public static void sendPlayListChanedBroadCast(Intent intent, Context context) {
        context.sendBroadcast(intent);
    }
}
