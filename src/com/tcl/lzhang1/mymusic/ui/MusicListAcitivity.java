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

import java.io.File;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tcl.lzhang1.mymusic.R;
import com.tcl.lzhang1.mymusic.UIHelper;
import com.tcl.lzhang1.mymusic.db.DBOperator;
import com.tcl.lzhang1.mymusic.db.imp.SongImp;
import com.tcl.lzhang1.mymusic.model.SongModel;
import com.tcl.lzhang1.mymusic.model.SongsWrap;
import com.tcl.lzhang1.mymusic.ui.apdater.MusicListAdapter;

/**
 * @author leizhang
 */
public class MusicListAcitivity extends BaseActivity implements OnClickListener,
        OnItemClickListener, OnItemLongClickListener {

    private ListView mMusicList = null;
    private MusicListAdapter musicListAdapter = null;
    private List<SongModel> mSongs = null;
    private RelativeLayout no_musics = null;
    private Button scan_music = null;
    private TextView back = null;
    private TextView nav_title = null;
    private TextView more = null;

    // list operate
    private DBOperator mDbOperator = null;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case -1:
                    UIHelper.toast(MusicListAcitivity.this, getString(R.string.delete_file_error));
                    break;
                case 1:
                    UIHelper.toast(MusicListAcitivity.this, getString(R.string.delete_file_success));
                    musicListAdapter.notifyDataSetChanged();
                    break;

                default:

                    break;
            }
        };
    };

    /*
     * (non-Javadoc)
     * @see
     * com.tcl.lzhang1.mymusic.ui.AcitivityInit#getPreActivityData(android.os
     * .Bundle)
     */
    @Override
    public void getPreActivityData(Bundle bundle) {
        // TODO Auto-generated method stub
        Intent intent = getIntent();
        if (null != intent) {
            SongsWrap songWrap = (SongsWrap) intent.getSerializableExtra("songs");
            if (null != songWrap) {
                mSongs = songWrap.getModels();
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.tcl.lzhang1.mymusic.ui.AcitivityInit#initView()
     */
    @Override
    public void initView() {
        // TODO Auto-generated method stub
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_music_list);
        mMusicList = (ListView) findViewById(R.id.music_list);
        mMusicList.setOnItemClickListener(this);
        musicListAdapter = new MusicListAdapter(this, mSongs);
        mMusicList.setAdapter(musicListAdapter);
        no_musics = (RelativeLayout) findViewById(R.id.no_musics);
        scan_music = (Button) findViewById(R.id.scan_music);
        scan_music.setOnClickListener(this);
        if (mSongs == null || mSongs.isEmpty()) {
            no_musics.setVisibility(View.VISIBLE);
            mMusicList.setVisibility(View.GONE);
        } else {
            no_musics.setVisibility(View.GONE);
            mMusicList.setVisibility(View.VISIBLE);
        }

        {
            back = (TextView) findViewById(R.id.back);
            back.setOnClickListener(this);
            nav_title = (TextView) findViewById(R.id.nav_title);
            nav_title.setText(R.string.local_music);
            more = (TextView) findViewById(R.id.more);
            more.setOnClickListener(this);
        }
        // delete dialog
        {
            mMusicList.setOnItemLongClickListener(this);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.tcl.lzhang1.mymusic.ui.AcitivityInit#initViewData()
     */
    @Override
    public void initViewData() {
        // TODO Auto-generated method stub
        mDbOperator = new SongImp(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.scan_music:
                UIHelper.showScanMusicActivity(this, null);
                break;
            case R.id.back:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        Bundle bundle;
        switch (arg0.getId()) {
            case R.id.music_list: {
                bundle = new Bundle();
                bundle.putSerializable("songs", new SongsWrap(mSongs));
                bundle.putInt("index", arg2);
                bundle.putInt("time", 0);
                UIHelper.showMusicPlayActivity(this, bundle);
                bundle = null;
            }
                break;

            default:
                break;
        }
    }

    /**
     * 
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        return super.onContextItemSelected(item);
    }

    /**
     * 
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        showMusicListDialog(((SongModel) parent.getAdapter().getItem(position)).getFile(),
                parent.getPositionForView(view));
        return true;
    }

    /**
     * when long click on listview ,it will be called
     */
    private void showMusicListDialog(final String filePath, final int position) {
        Builder builder = new Builder(this);
        builder.setTitle(getString(R.string.choose_operate));
        final String[] operates = getResources().getStringArray(R.array.music_list_operate);
        builder.setItems(operates,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Log.d(TAG, "clicked:" + operates[which]);
                        switch (which) {
                            case 0:// do delete
                            {
                                new Thread(new Runnable() {

                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        try {
                                            Log.d(TAG, "will delete file:"+filePath);
                                            // delete database record
                                            mDbOperator.delete(filePath.trim());
                                            File file = new File(filePath);
                                            if (file.isFile()) {// delete file
                                                file.delete();
                                            }
                                            // remove data from datasource
                                            mSongs.remove(position);

                                            // send message
                                            mHandler.sendEmptyMessage(1);
                                        } catch (Exception e) {
                                            // TODO: handle exception
                                            Log.d(TAG,
                                                    "delete music [" + filePath + "] faild:"
                                                            + e.getMessage());
                                            mHandler.sendEmptyMessage(-1);
                                            e.printStackTrace();
                                        }

                                    }
                                }).start();
                            }
                                break;
                            case 1:// view informations

                                break;
                            default:

                                break;
                        }
                    }
                });
        builder.create().show();

    }
}
