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
import android.view.Window;
import android.widget.Button;
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
    private TextView back = null;
    private TextView nav_title = null;
    private Button scan_music = null;
    private TextView cur_music_name = null;

    private List<SongModel> mSongs = null;

    private DBOperator mDbOperator = null;

    private int mScanState = ScanState.STATE_INIT;

    public interface ScanState {
        public final int STATE_INIT = 0;
        public final int STATE_SCANING = 1;
        public final int STATE_FINISH = 2;
    }

    @Override
    public void getPreActivityData(Bundle bundle) {
        // TODO Auto-generated method stub

    }

    @Override
    public void initView() {
        // TODO Auto-generated method stub'
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_music_scan);
        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(this);
        nav_title = (TextView) findViewById(R.id.nav_title);
        nav_title.setText(R.string.scan_music);
        scan_music = (Button) findViewById(R.id.scan_music);
        cur_music_name = (TextView) findViewById(R.id.cur_music_name);
        cur_music_name.setVisibility(View.GONE);
        scan_music.setOnClickListener(this);
        findViewById(R.id.more).setVisibility(View.GONE);
        MusicUtil.setMusicListener(this);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                onDestroy();
            }
        });

    }

    /*
     * (non-Javadoc)
     * @see com.tcl.lzhang1.mymusic.ui.BaseActivity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        // super.onBackPressed();
        onDestroy();
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

            case R.id.scan_music:

                // new thread to scan music
                mSongs.clear();
                mScanState = ScanState.STATE_SCANING;
                scan_music.setEnabled(false);
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        try {

                            MusicUtil.scanMusic(getApplication());

                        } catch (SDCardUnMoutedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.back:

                onDestroy();
                break;
            default:

        }
    }

    @Override
    public void onMusicScaned(final SongModel song) {
        // TODO Auto-generated method stub
        if (null == song) {
            return;
        }

        mSongs.add(song);
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                cur_music_name.setVisibility(View.VISIBLE);
                cur_music_name.setText(song.getSongName());

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
                cur_music_name.setText(String.format(getString(R.string.scan_finished),
                        mSongs.size() + ""));
                Log.d(TAG, "music scaned finish,total :" + mSongs.size());
                mScanState = ScanState.STATE_FINISH;
                saveSongs();
                finish();
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

    /*
     * (non-Javadoc)
     * @see com.tcl.lzhang1.mymusic.ui.BaseActivity#onDestroy()
     */
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub

        super.onDestroy();
        overridePendingTransition(R.anim.no_horizontal_translation, R.anim.push_right_out);
    }
}
