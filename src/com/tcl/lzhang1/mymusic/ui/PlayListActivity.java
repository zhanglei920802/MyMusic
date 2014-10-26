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

import java.util.List;

import me.maxwin.view.XListView;

import com.tcl.lzhang1.mymusic.AppContext;
import com.tcl.lzhang1.mymusic.Contants;
import com.tcl.lzhang1.mymusic.R;
import com.tcl.lzhang1.mymusic.UIHelper;
import com.tcl.lzhang1.mymusic.model.BaseModel;
import com.tcl.lzhang1.mymusic.model.SongModel;
import com.tcl.lzhang1.mymusic.model.SongsWrap;
import com.tcl.lzhang1.mymusic.service.MusicPlayService.PlayState;
import com.tcl.lzhang1.mymusic.ui.MusicPlayActivity.PlayAction;
import com.tcl.lzhang1.mymusic.ui.apdater.MusicListAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class PlayListActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

    private AppContext mAppContext = null;

    private TextView back = null;

    private TextView nav_title = null;

    /**
     * the list view
     */
    private ListView play_list = null;

    private TextView no_play_list = null;

    /**
     * the list adapter
     */
    private MusicListAdapter musicListAdapter = null;

    /**
     * the songs
     */
    private List<SongModel> mSongs = null;

    @Override
    public void getPreActivityData(Bundle bundle) {
        // TODO Auto-generated method stub
        if (null == bundle) {
            onDestroy();

        }

        mSongs = ((SongsWrap) bundle.getSerializable("songs")).getModels();
    }

    @Override
    public void initView() {
        // TODO Auto-generated method stub
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.content_ll_fl);
        viewGroup.addView(getLayoutInflater().inflate(R.layout.activity_play_list, null));
        mAppContext = (AppContext) getApplication();
//        setContentView(R.layout.activity_play_list);
        {
            back = (TextView) findViewById(R.id.back);
            back.setOnClickListener(this);
            nav_title = (TextView) findViewById(R.id.nav_title);
            nav_title.setText(R.string.play_list);

        }

        {
            play_list = (ListView) findViewById(R.id.play_list);
            play_list.setOnItemClickListener(this);
            no_play_list = (TextView) findViewById(R.id.no_play_list);
            if (mSongs == null || mSongs.isEmpty()) {
                no_play_list.setVisibility(View.GONE);
                play_list.setVisibility(View.VISIBLE);
            }
            musicListAdapter = new MusicListAdapter(this, mSongs);
            play_list.setAdapter(musicListAdapter);
        }

    }

    @Override
    public void initViewData() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        // super.onBackPressed();
        onDestroy();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        overridePendingTransition(R.anim.no_vertical_tanslation, R.anim.push_down_out);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.back:

                onDestroy();

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
            case R.id.play_list: {
                if (null == mSongs || mSongs.isEmpty()) {
                    return;
                }
                bundle = new Bundle();
                bundle.putSerializable("songs", new SongsWrap(mSongs));

                sendPlayNewBroadCast(arg2, 0, mSongs.get(arg2));
                onDestroy();
                bundle = null;
            }
                break;

            default:
                break;
        }
    }

    /**
     * play new music
     * 
     * @param playIndex
     * @param time
     */
    public void sendPlayNewBroadCast(int playIndex, int time, BaseModel baseModel) {
        Intent intent = new Intent();
        intent.putExtra("index", playIndex);
        intent.putExtra("time", time);
        Bundle bundle = new Bundle();
        bundle.putSerializable("model", baseModel);
        intent.putExtras(bundle);
        intent.setAction(Contants.FILTER_PLAY_ACTION);
        intent.putExtra("action", PlayAction.ACTION_NEW);
        sendBroadcast(intent);
        intent = null;
        bundle = null;
    }
}
