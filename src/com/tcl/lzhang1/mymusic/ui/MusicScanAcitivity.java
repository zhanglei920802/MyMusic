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

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tcl.lzhang1.mymusic.MusicUtil;
import com.tcl.lzhang1.mymusic.MusicUtil.onMusicScanListener;
import com.tcl.lzhang1.mymusic.R;
import com.tcl.lzhang1.mymusic.db.DBOperator;
import com.tcl.lzhang1.mymusic.db.imp.SongImp;
import com.tcl.lzhang1.mymusic.exception.SDCardUnMoutedException;
import com.tcl.lzhang1.mymusic.model.SongModel;

/**
 * Music scan <br>
 * function list as follows: <br>
 * <li>filter music by size <li/>
 * 
 * @author leizhang
 */
public class MusicScanAcitivity extends BaseActivity implements OnClickListener,
        onMusicScanListener {

    private ProgressBar music_scan_progess = null;
    private RelativeLayout default_layout = null;
    private Button scan_music = null;
    private Button filter_music = null;
    private RelativeLayout scaning_music = null;
    private LinearLayout scaning_music_wrap = null;
    private TextView cur_music_name = null;
    private TextView scan_total = null;
    private Button stop_scan = null;

    private int mScanTotal = 0;
    private List<SongModel> mSongs = null;

    private DBOperator mDbOperator = null;

    @Override
    public void getPreActivityData(Bundle bundle) {
        // TODO Auto-generated method stub

    }

    @Override
    public void initView() {
        // TODO Auto-generated method stub
        setContentView(R.layout.activity_music_scan);

        music_scan_progess = (ProgressBar) findViewById(R.id.music_scan_progess);
        default_layout = (RelativeLayout) findViewById(R.id.default_layout);
        scan_music = (Button) findViewById(R.id.scan_music);
        filter_music = (Button) findViewById(R.id.filter_music);
        scaning_music = (RelativeLayout) findViewById(R.id.scaning_music);
        scaning_music_wrap = (LinearLayout) findViewById(R.id.scaning_music_wrap);
        cur_music_name = (TextView) findViewById(R.id.cur_music_name);
        scan_total = (TextView) findViewById(R.id.scan_total);
        stop_scan = (Button) findViewById(R.id.stop_scan);

        scan_music.setOnClickListener(this);
        filter_music.setOnClickListener(this);
        stop_scan.setOnClickListener(this);
        MusicUtil.setMusicListener(this);

        default_layout.setVisibility(View.VISIBLE);
        scaning_music.setVisibility(View.GONE);
    }

    @Override
    public void initViewData() {
        // TODO Auto-generated method stub
        mSongs = new ArrayList<SongModel>();
        TAG = getPackageName() + "/" + MusicScanAcitivity.class.getSimpleName();
        mDbOperator = new SongImp(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.stop_scan:
                cur_music_name.setText(R.string.scan_was_stoped);
                scan_total.setText(String.valueOf(mScanTotal));
                break;
            case R.id.filter_music:
                // show dialog
                break;
            case R.id.scan_music:

                // new thread to scan music
                default_layout.setVisibility(View.GONE);
                scaning_music.setVisibility(View.VISIBLE);

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        try {

                            MusicUtil.scanMusic();

                        } catch (SDCardUnMoutedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            default:

                break;
        }
    }

    @Override
    public void onMusicScaned(final SongModel song) {
        // TODO Auto-generated method stub
        if (null == song) {
            return;
        }

        mScanTotal++;
        mSongs.add(song);
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                cur_music_name.setText(song.getSongName());
                scan_total.setText(String.valueOf(mScanTotal));
                Log.d(TAG, "music scaned.file name is:" + song.getSongName());
            }
        });

    }

    @Override
    public void onMusicScanedFinish() {
        // TODO Auto-generated method stub
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                cur_music_name.setText(R.string.scan_finish);
                scan_total.setText(String.valueOf(mScanTotal));
                Log.d(TAG, "music scaned finish,total :" + mSongs.size());
                saveSongs();
            }
        });

    }

    /**
     * save songs by create new thread.it was called when scan music is finished
     */
    private void saveSongs() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (null == mSongs || mSongs.isEmpty()) {
                    Log.d(TAG, "nothig to save");
                    return;
                }

                mDbOperator.saveAll(mSongs);
            }
        }).start();
    }

}
