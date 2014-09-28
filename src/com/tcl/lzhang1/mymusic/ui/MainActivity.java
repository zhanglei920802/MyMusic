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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.tcl.lzhang1.mymusic.MusicUtil;
import com.tcl.lzhang1.mymusic.R;
import com.tcl.lzhang1.mymusic.UIHelper;
import com.tcl.lzhang1.mymusic.db.DBOperator;
import com.tcl.lzhang1.mymusic.db.imp.SongImp;
import com.tcl.lzhang1.mymusic.exception.SDCardUnMoutedException;
import com.tcl.lzhang1.mymusic.model.SongModel;
import com.tcl.lzhang1.mymusic.model.SongsWrap;
import com.tcl.lzhang1.mymusic.ui.apdater.MineAdapter;
import com.tcl.lzhang1.mymusic.ui.apdater.MyPageAdapter;
import com.tcl.lzhang1.mymusic.ui.widget.PagerSlidingTabStrip;

/**
 * This is MainActivity.java
 * 
 * @author leizhang
 */
public class MainActivity extends BaseActivity implements OnItemClickListener, OnClickListener {

    private PagerSlidingTabStrip mPagerTabStrip = null;
    private ViewPager mViewPager = null;
    private List<View> mViews = null;
    private LayoutInflater mLayoutInflater = null;
    private String[] mMenuTitles = null;
    private MyPageAdapter mPageAdapter = null;

    // mine
    public static final int SCAN_MUSIC_SUCCESS = 1;
    public static final int SCAN_MUSIC_FAILD = 2;
    private View mine = null;
    private ListView mine_list = null;
    private List<HashMap<String, String>> mList;
    private MineAdapter mineAdapter = null;
    private List<SongModel> mSongModels;
    private DBOperator mDbOperator = null;
    private Button login = null;
    // music scan
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            // super.handleMessage(msg);
            if (null != msg) {
                switch (msg.what) {
                    case SCAN_MUSIC_SUCCESS:
                        Log.d(TAG, "hand message,upadte UI......");
                        mSongModels = (List<SongModel>) msg.obj;
                        if (null != mSongModels && !mSongModels.isEmpty()) {
                            Map<String, String> map = mList.get(0);
                            map.put("value", MusicUtil.formatString1(mSongModels.size()));
                        } else {
                            Map<String, String> map = mList.get(0);
                            map.put("value", MusicUtil.formatString1(0));
                        }

                        mineAdapter.notifyDataSetChanged();

                        break;
                    case SCAN_MUSIC_FAILD:

                        break;
                    default:
                        break;
                }
            }
        }
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

    }

    /*
     * (non-Javadoc)
     * @see com.tcl.lzhang1.mymusic.ui.AcitivityInit#initView()
     */
    @Override
    public void initView() {
        // TODO Auto-generated method stub * @see
        // com.tcl.lzhang1.mymusic.ui.AcitivityInit#initView()
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.main);
        TAG = MainActivity.class.getName();

        {
            mViewPager = (ViewPager) findViewById(R.id.menu_page);
            mPagerTabStrip = (PagerSlidingTabStrip) findViewById(R.id.pagerStrip);
            mViews = new ArrayList<View>();
            mLayoutInflater = getLayoutInflater();

            View view = mine = mLayoutInflater.inflate(R.layout.main_mine, null);
            mViews.add(view);
            view = mLayoutInflater.inflate(R.layout.main_recommand, null);
            mViews.add(view);
            view = mLayoutInflater.inflate(R.layout.main_libs, null);
            mViews.add(view);
            view = mLayoutInflater.inflate(R.layout.main_search, null);
            mViews.add(view);

            // mPagerTabStrip.setTextSpacing(50);
            // mPagerTabStrip.setTextColor(Color.BLACK);
            mPagerTabStrip.setIndicatorColor(Color.GREEN);
            mPagerTabStrip.setIndicatorHeight(10);
            mPageAdapter = new MyPageAdapter(this, mViews, mMenuTitles);
            mViewPager.setAdapter(mPageAdapter);

            mPagerTabStrip.setViewPager(mViewPager);
        }
        // mine
        {
            mine_list = (ListView) mine.findViewById(R.id.mine_list);
            mineAdapter = new MineAdapter(this, mList);
            mine_list.setAdapter(mineAdapter);
            mine_list.setOnItemClickListener(this);
            login = (Button) mine.findViewById(R.id.login);
            login.setOnClickListener(this);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.tcl.lzhang1.mymusic.ui.AcitivityInit#initViewData()
     */
    @Override
    public void initViewData() {
        // TODO Auto-generated method stub
        mMenuTitles = getResources().getStringArray(R.array.pageview_menu);
        mList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map = new HashMap<String, String>();

        map.put("name", getString(R.string.local_music));
        map.put("value", getString(R.string.refreshing_music));
        mList.add(map);

        map = new HashMap<String, String>();
        map.put("name", getString(R.string.fav_music));
        map.put("value", MusicUtil.formatString1(0));
        mList.add(map);

        map = new HashMap<String, String>();
        map.put("name", getString(R.string.download_music));
        map.put("value", MusicUtil.formatString1(0));
        mList.add(map);

        // init music
        {
            mDbOperator = new SongImp(this);
            scanMusic();
        }
    }

    /**
     * scan music
     */
    private void scanMusic() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    List<SongModel> models = MusicUtil.scanMusic();
                    Message message = mHandler.obtainMessage();
                    message.what = SCAN_MUSIC_SUCCESS;
                    message.obj = models;
                    mHandler.sendMessage(message);
                    Log.d(TAG, "scan music finished,send message .....");
                    mDbOperator.saveAll(models);
                } catch (SDCardUnMoutedException e) {
                    // TODO Auto-generated catch block
                    Log.d(TAG, "scan music failed:unmouted sdcard");
                    mHandler.sendEmptyMessage(SCAN_MUSIC_FAILD);
                } catch (Exception e) {
                    // TODO: handle exception
                    Log.d(TAG, "scan music error:" + e.getMessage());
                    mHandler.sendEmptyMessage(SCAN_MUSIC_FAILD);
                }

            }
        }).start();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        Bundle bundle = null;
        switch (arg0.getId()) {
            case R.id.mine_list:
            // menu list
            {
                HashMap<String, String> map = mList.get(arg2);
                String name = map.get("name");
                if (getString(R.string.local_music).equals(name)) {
                    bundle = new Bundle();
                    bundle.putSerializable("songs", new SongsWrap(mSongModels));
                    UIHelper.showMusicListActivity(this, bundle);
                    bundle = null;
                }
            }
                break;

            default:
                break;
        }
    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.login:
                UIHelper.showLoginActivity(this, null);
                break;

            default:
                break;
        }
    }

}
