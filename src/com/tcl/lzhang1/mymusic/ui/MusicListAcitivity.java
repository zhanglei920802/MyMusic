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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tcl.lzhang1.mymusic.Contants;
import com.tcl.lzhang1.mymusic.MusicUtil;
import com.tcl.lzhang1.mymusic.R;
import com.tcl.lzhang1.mymusic.UIHelper;
import com.tcl.lzhang1.mymusic.db.DBOperator;
import com.tcl.lzhang1.mymusic.db.imp.AlbumImp;
import com.tcl.lzhang1.mymusic.db.imp.SingerImp;
import com.tcl.lzhang1.mymusic.db.imp.SongImp;
import com.tcl.lzhang1.mymusic.model.AlbumModel;
import com.tcl.lzhang1.mymusic.model.SingerModel;
import com.tcl.lzhang1.mymusic.model.SongModel;
import com.tcl.lzhang1.mymusic.model.SongsWrap;
import com.tcl.lzhang1.mymusic.ui.apdater.AlbumListAdapter;
import com.tcl.lzhang1.mymusic.ui.apdater.MusicListAdapter;
import com.tcl.lzhang1.mymusic.ui.apdater.MyPageAdapter;
import com.tcl.lzhang1.mymusic.ui.apdater.SingerListAdapter;
import com.tcl.lzhang1.mymusic.ui.widget.PagerSlidingTabStrip;

/**
 * @author leizhang
 */
public class MusicListAcitivity extends MiniPlayerBaseActivity implements OnClickListener,
        OnItemClickListener, OnItemLongClickListener, IXListViewListener {

    /**
     * 
     */
    public static final int LOAD_SINGER_SUCCESS = 10;

    /**
     * 
     */
    public static final int LOAD_ALBUM_SUCCESS = 11;

    /**
     * title
     */
    private PagerSlidingTabStrip mPagerTabStrip = null;

    /**
     * the viewpager
     */
    private ViewPager mViewPager = null;

    /**
     * the views
     */
    private List<View> mViews = null;

    /**
     * the layout inflator
     */
    private LayoutInflater mLayoutInflater = null;

    /**
     * the all music
     */
    private View all = null;

    /**
     * all singer
     */
    private View singer = null;

    /**
     * all album
     */
    private View album = null;

    /**
     * titles
     */
    private String[] mMenuTitles = null;

    /**
     * the adapter
     */
    private MyPageAdapter mPageAdapter = null;

    /**
     * start mode local music ,default.
     */
    public static final int START_MODE_LOCAL = 1;

    /**
     * start mode my favorite music
     */
    public static final int START_MODE_FAV = 2;

    /**
     * 
     */
    public static final int START_MODE_SINGER = 3;

    /**
     * 
     */
    public static final int START_MODE_ALBUM = 4;
    /**
     * current start mode
     */
    private int curStartMode = START_MODE_LOCAL;

    /**
     * the list view
     */
    private XListView mMusicList = null;

    /**
     * 
     */
    private ListView mMusicListv1 = null;
    /**
     * the list adapter
     */
    private MusicListAdapter musicListAdapter = null;

    /**
     * the songs
     */
    private List<SongModel> mSongs = null;

    /**
     * no music
     */
    private RelativeLayout no_musics = null;

    /**
     * scan music button
     */
    private Button scan_music = null;

    /**
     * 
     */
    private ListView music_list_singer = null;

    /**
     * the list adapter
     */
    private SingerListAdapter singerListAdapter = null;

    /**
     * the songs
     */
    private List<SingerModel> mSingers = null;

    /**
     * no music
     */
    private RelativeLayout no_musics_singer = null;

    /**
     * scan music mSongsbutton
     */
    private Button scan_music_singer = null;

    /**
     * 
     */
    private ListView music_list_album = null;

    /**
     * the list adapter
     */
    private AlbumListAdapter albumListAdapter = null;

    /**
     * the songs
     */
    private List<AlbumModel> mAblums = null;

    /**
     * no music
     */
    private RelativeLayout no_musics_album = null;

    /**
     * scan music button
     */
    private Button scan_music__album = null;
    /**
     * back button
     */
    private TextView back = null;

    /**
     * navigation title
     */
    private TextView nav_title = null;

    /**
     * more
     */
    private TextView more = null;

    // list operate
    /**
     * DB access API
     */
    private DBOperator mDbOperator = null;

    /**
     * filed indicates load data success
     */
    public static final int LOADDATA_SUCCESS = 6;

    /**
     * filed indicates load data failed
     */
    public static final int LOADDATA_FAILED = 7;

    private boolean isUnregister = false;

    /**
     * 
     */
    private String mAlbumname = "";

    /**
     * 
     */
    private String mSingerName = "";

    /**
     * 
     */
    private DBOperator mSingerOperate = null;

    /**
     * 
     */
    private DBOperator mAlbumOperate = null;

    private String orderCol = null;

    /**
     * the hander
     */
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unchecked")
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case -1:
                    UIHelper.toast(MusicListAcitivity.this, getString(R.string.delete_file_error));
                    break;
                case 1:
                    UIHelper.toast(MusicListAcitivity.this, getString(R.string.delete_file_success));
                    musicListAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    if (null != mSongs) {
                        mSongs.clear();
                        List<SongModel> models = (List<SongModel>) msg.obj;

                        if (models == null || models.isEmpty()) {
                            no_musics.setVisibility(View.VISIBLE);
                            mMusicList.setVisibility(View.GONE);
                        } else {
                            mSongs.clear();
                            mSongs.addAll(models);
                        }

                        musicListAdapter.notifyDataSetChanged();
                    }
                    break;
                case LOADDATA_SUCCESS:
                    if (null != msg) {

                        if (msg.arg2 == UIHelper.LISTVIEW_ACTION_REFRESH) {
                            mSongs.clear();
                        }

                        if (msg.arg1 < UIHelper.PAGE_SIZE) {// load finish
                            mSongs.addAll((Collection<? extends SongModel>) msg.obj);
                            mMusicList.setTag(UIHelper.LISTVIEW_DATA_FULL);
                            musicListAdapter.notifyDataSetChanged();
                            mMusicList.setPullLoadEnable(false);
                        } else if (msg.arg1 == UIHelper.PAGE_SIZE) {
                            mSongs.addAll((Collection<? extends SongModel>) msg.obj);
                            mMusicList.setTag(UIHelper.LISTVIEW_DATA_MORE);
                            musicListAdapter.notifyDataSetChanged();

                        }

                        if (musicListAdapter.getCount() < 1) {
                            mMusicList.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
                            no_musics.setVisibility(View.VISIBLE);
                            mMusicList.setVisibility(View.GONE);
                        }

                        if (msg.arg2 == UIHelper.LISTVIEW_ACTION_REFRESH) {
                            mMusicList.setRefreshTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .format(Calendar.getInstance().getTime()));
                            mMusicList.setSelection(0);
                        }
                        mMusicList.stopLoadMore();
                        mMusicList.stopRefresh();
                        sendPlayListChanedBroadCast();

                    }
                    break;
                case LOADDATA_FAILED:
                    mMusicList.setTag(UIHelper.LISTVIEW_DATA_MORE);
                    mMusicList.setVisibility(View.VISIBLE);
                    mMusicList.stopLoadMore();
                    mMusicList.stopRefresh();
                    break;
                case LOAD_ALBUM_SUCCESS:
                    if (null != msg) {
                        if (mAblums == null || mAblums.isEmpty()) {
                            no_musics_album.setVisibility(View.VISIBLE);
                            music_list_album.setVisibility(View.GONE);
                            Log.d(TAG, "no alumbs.");
                        } else {
                            // Log.d(TAG, "alumbs:" + mAblums);
                            no_musics_album.setVisibility(View.GONE);
                            music_list_album.setVisibility(View.VISIBLE);
                            albumListAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
                case LOAD_SINGER_SUCCESS:
                    if (null != msg) {

                        if (mSingers == null || mSingers.isEmpty()) {
                            Log.d(TAG, "no singers");
                            no_musics_singer.setVisibility(View.VISIBLE);
                            music_list_singer.setVisibility(View.GONE);

                        } else {
                            // Log.d(TAG, "singers:" + mSingers);
                            no_musics_singer.setVisibility(View.GONE);
                            music_list_singer.setVisibility(View.VISIBLE);
                            singerListAdapter.notifyDataSetChanged();
                        }

                    }
                    break;
                default:

                    break;
            }
        };
    };

    /**
     * the popup window
     */
    private PopupWindow moreMenu = null;

    /**
     * the contentview
     */
    private View moreView = null;
    /**
     * the data source
     */
    private String[] moreStringArray = null;
    /**
     * list
     */
    private ListView music_list_more = null;

    /**
     * the list adpter
     */
    private ArrayAdapter<String> mMoreAdapter = null;

    /**
     * 
     */
    private void showListMore() {
        if (null != moreMenu) {
            moreMenu.dismiss();
            moreMenu = null;
            return;
        }
        moreMenu = new PopupWindow(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        // moreMenu.setBackgroundDrawable(new );
        moreMenu.setOutsideTouchable(false);
        moreMenu.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        moreView = getLayoutInflater().inflate(R.layout.activity_music_scan_more, null);
        music_list_more = (ListView) moreView.findViewById(R.id.music_list_more);
        moreStringArray = getResources().getStringArray(R.array.music_list_more);
        mMoreAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                moreStringArray);
        music_list_more.setAdapter(mMoreAdapter);
        music_list_more.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                if (null != moreMenu) {
                    moreMenu.dismiss();
                }
                switch (position) {
                    case 0:
                        /*
                         * Log.d(TAG, "sort by song"); new Thread(new Runnable()
                         * {
                         * @SuppressWarnings("unchecked")
                         * @Override public void run() { // TODO Auto-generated
                         * method stub List<SongModel> models = null; if
                         * (curStartMode == START_MODE_LOCAL) { models =
                         * (List<SongModel>) mDbOperator
                         * .findAll("select * from songs order by name"); } else
                         * if (curStartMode == START_MODE_FAV) { models =
                         * (List<SongModel>) mDbOperator
                         * .findAll("select * from songs where fav=1 order by name "
                         * ); } if (null != models) { Message message =
                         * mHandler.obtainMessage(); message.what = 2;
                         * message.obj = models; mHandler.sendMessage(message);
                         * } } }).start();
                         */
                        mMusicList.setPullLoadEnable(true);
                        orderCol = "name";
                        loadData(1, UIHelper.PAGE_SIZE, UIHelper.LISTVIEW_ACTION_REFRESH, "name",
                                mHandler);

                        break;
                    case 1:
                        /*
                         * Log.d(TAG, "sort by singer"); new Thread(new
                         * Runnable() {
                         * @SuppressWarnings({ "unchecked" })
                         * @Override public void run() { // TODO Auto-generated
                         * method stub List<SongModel> models = null; if
                         * (curStartMode == START_MODE_LOCAL) { models =
                         * (List<SongModel>) mDbOperator
                         * .findAll("select * from songs order by singername");
                         * } else if (curStartMode == START_MODE_FAV) { models =
                         * (List<SongModel>) mDbOperator .findAll(
                         * "select * from songs where fav=1  order by singername"
                         * ); } if (null != models) { Message message =
                         * mHandler.obtainMessage(); message.what = 2;
                         * message.obj = models; mHandler.sendMessage(message);
                         * } } }).start();
                         */
                        mMusicList.setPullLoadEnable(true);
                        orderCol = "singername";
                        loadData(1, UIHelper.PAGE_SIZE, UIHelper.LISTVIEW_ACTION_REFRESH,
                                "singername", mHandler);
                        break;
                    case 2:
                        UIHelper.showScanMusicActivity(MusicListAcitivity.this, null);
                        break;
                    default:

                        break;
                }
            }
        });
        moreMenu.setContentView(moreView);
        moreMenu.showAsDropDown(more);
    }

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
            curStartMode = intent.getIntExtra("startmode", 0);
            if (curStartMode == 0) {
                Log.d(TAG, "you should provide the start mode value");
                finish();
                return;
            }

            if (null != songWrap) {
                mSongs = songWrap.getModels();
            }

            //
            List<SongModel> mSongModels = new ArrayList<SongModel>();
            if (curStartMode == START_MODE_FAV) {

                for (SongModel songModel : mSongs) {
                    if (songModel.getFav() == 1) {
                        mSongModels.add(songModel);
                    }
                }

                mSongs = mSongModels;
                mSongModels = null;
            } else if (curStartMode == START_MODE_ALBUM) {

                mAlbumname = bundle.getString("albumname");

            } else if (curStartMode == START_MODE_SINGER) {

                mSingerName = bundle.getString("singername");

            }

        }
    }

    /*
     * (non-Javadoc)
     * @see com.tcl.lzhang1.mymusic.ui.BaseActivity#onDestroy()
     */
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        System.out.println("MusicListAcitivity.onDestroy()");
        if (!isUnregister) {
            Log.d(TAG, "un register");
            unregisterReceiver(mFavBroadcastReceiver);
            isUnregister = true;
        }

        super.onDestroy();
        overridePendingTransition(R.anim.no_vertical_tanslation, R.anim.push_down_out);
    }

    /**
     * broadcast receiver to process mark fav or not
     */
    private BroadcastReceiver mFavBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (null != intent && Contants.FILTER_ACTION_MARK_FAV.equals(intent.getAction())) {
                SongModel model = (SongModel) intent.getSerializableExtra("song");
                boolean value = intent.getBooleanExtra("value", false);
                if (model != null) {
                    markFav(model.getFile(), value);
                }
            }
        };
    };

    /**
     * mark music fav or not
     * 
     * @param path the index
     * @param value mark value
     */
    private void markFav(String path, boolean value) {

        for (SongModel songModel : mSongs) {
            if (songModel.getFile().equals(path)) {
                if (value) {
                    songModel.setFav(1);
                } else {
                    songModel.setFav(0);
                }
                return;
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
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.initView();
        if (curStartMode == START_MODE_LOCAL) {/*
                                                * <<<<<<< HEAD
                                                * mMusicList.setPullLoadEnable
                                                * (true);
                                                * mMusicList.setPullRefreshEnable
                                                * (true);
                                                * mMusicList.setXListViewListener
                                                * (this);
                                                * mMusicList.setTag(UIHelper
                                                * .LISTVIEW_DATA_FULL);
                                                * mMusicList
                                                * .setPullLoadEnable(false); }
                                                * else {
                                                * mMusicList.setPullLoadEnable
                                                * (false);
                                                * mMusicList.setPullRefreshEnable
                                                * (false); } ======= >>>>>>>
                                                * c37d08196b48ed4b02dc814a229c1fde8d09d22b
                                                */
            mViewGroup.addView(getLayoutInflater().inflate(R.layout.activity_music_list, null));
            //setContentView(R.layout.activity_music_list);
            // wrap
            {

                mViewPager = (ViewPager) findViewById(R.id.menu_page);
                mPagerTabStrip = (PagerSlidingTabStrip) findViewById(R.id.pagerStrip);
                mViews = new ArrayList<View>();
                mLayoutInflater = getLayoutInflater();

                all = mLayoutInflater.inflate(R.layout.activity_music_list_all, null);
                mViews.add(all);
                singer = mLayoutInflater.inflate(R.layout.activity_music_list_singer, null);
                mViews.add(singer);
                album = mLayoutInflater.inflate(R.layout.activity_music_list_album, null);
                mViews.add(album);
                mMenuTitles = getResources().getStringArray(R.array.music_menu);

                // all begin
                {
                    mMusicList = (XListView) all.findViewById(R.id.music_list);
                    if (curStartMode == START_MODE_LOCAL) {
                        mMusicList.setPullLoadEnable(false);
                        mMusicList.setPullRefreshEnable(true);
                        mMusicList.setXListViewListener(this);
                        mMusicList.setTag(UIHelper.LISTVIEW_DATA_FULL);
                    } else {
                        mMusicList.setPullLoadEnable(false);
                        mMusicList.setPullRefreshEnable(false);
                    }

                    mMusicList.setOnItemClickListener(this);
                    musicListAdapter = new MusicListAdapter(this, mSongs);
                    mMusicList.setAdapter(musicListAdapter);
                    no_musics = (RelativeLayout) all.findViewById(R.id.no_musics);
                    scan_music = (Button) all.findViewById(R.id.scan_music);
                    scan_music.setOnClickListener(this);
                    if (mSongs == null || mSongs.isEmpty()) {
                        no_musics.setVisibility(View.VISIBLE);
                        mMusicList.setVisibility(View.GONE);
                    } else {
                        no_musics.setVisibility(View.GONE);
                        mMusicList.setVisibility(View.VISIBLE);
                    }

                    // delete dialog
                    {
                        mMusicList.setOnItemLongClickListener(this);
                    }
                }// all end

                // singer begin
                {

                    music_list_singer = (ListView) singer.findViewById(R.id.music_list_singer);
                    music_list_singer.setOnItemClickListener(this);
                    singerListAdapter = new SingerListAdapter(this, mSingers);
                    music_list_singer.setAdapter(singerListAdapter);
                    no_musics_singer = (RelativeLayout) singer.findViewById(R.id.no_musics_singer);
                    scan_music_singer = (Button) singer.findViewById(R.id.scan_music_singer);
                    scan_music_singer.setOnClickListener(this);
                    if (mSingers == null || mSingers.isEmpty()) {
                        no_musics_singer.setVisibility(View.VISIBLE);
                        music_list_singer.setVisibility(View.GONE);
                    } else {
                        no_musics_singer.setVisibility(View.GONE);
                        music_list_singer.setVisibility(View.VISIBLE);
                    }

                    // delete dialog
                    {
                        // music_list_singer.setOnItemLongClickListener(this);
                    }

                }// singer end

                // ablum begin
                {

                    music_list_album = (ListView) album.findViewById(R.id.music_list_alubm);

                    music_list_album.setOnItemClickListener(this);
                    albumListAdapter = new AlbumListAdapter(this, mAblums);
                    music_list_album.setAdapter(albumListAdapter);
                    no_musics_album = (RelativeLayout) album.findViewById(R.id.no_musics_album);
                    scan_music__album = (Button) album.findViewById(R.id.scan_music_album);
                    scan_music__album.setOnClickListener(this);
                    if (mAblums == null || mAblums.isEmpty()) {
                        no_musics_album.setVisibility(View.VISIBLE);
                        music_list_album.setVisibility(View.GONE);
                    } else {
                        no_musics_album.setVisibility(View.GONE);
                        music_list_album.setVisibility(View.VISIBLE);
                    }

                    // delete dialog
                    {
                        // music_list_album.setOnItemLongClickListener(this);
                    }

                }// ablum end

                // mPagerTabStrip.setTextSpacing(50);
                // mPagerTabStrip.setTextColor(Color.BLACK);
                mPagerTabStrip.setIndicatorColorResource(R.color.main_tab_clolor);
                mPagerTabStrip.setUnderlineColor(Color.WHITE);
                mPagerTabStrip.setTextColor(Color.BLACK);
                mPagerTabStrip.setIndicatorHeight(7);
                // mPagerTabStrip.setTabBackground(android.R.color.white);
                mPageAdapter = new MyPageAdapter(this, mViews, mMenuTitles);
                mViewPager.setAdapter(mPageAdapter);

                mPagerTabStrip.setViewPager(mViewPager);

                // loadSingerAndAlbum();
            }
        } else {
            mViewGroup.addView(getLayoutInflater().inflate(R.layout.activity_music_list_v1, null));
         //   setContentView(R.layout.activity_music_list_v1);

            {
                mMusicListv1 = (ListView) findViewById(R.id.music_list);

                mMusicListv1.setOnItemClickListener(this);
                musicListAdapter = new MusicListAdapter(this, mSongs);
                mMusicListv1.setAdapter(musicListAdapter);
                no_musics = (RelativeLayout) findViewById(R.id.no_musics);
                scan_music = (Button) findViewById(R.id.scan_music);
                scan_music.setOnClickListener(this);
                if (mSongs == null || mSongs.isEmpty()) {
                    no_musics.setVisibility(View.VISIBLE);
                    mMusicListv1.setVisibility(View.GONE);
                } else {
                    no_musics.setVisibility(View.GONE);
                    mMusicListv1.setVisibility(View.VISIBLE);
                }

                // delete dialog
                {
                    mMusicListv1.setOnItemLongClickListener(this);
                }

            }// all end

        }

        // common
        {
            {
                back = (TextView) findViewById(R.id.back);
                back.setOnClickListener(this);
                nav_title = (TextView) findViewById(R.id.nav_title);
                if (curStartMode == START_MODE_LOCAL) {
                    nav_title.setText(R.string.local_music);
                } else if (curStartMode == START_MODE_FAV) {
                    nav_title.setText(R.string.fav_music);
                } else if (curStartMode == START_MODE_SINGER) {
                    nav_title.setText(mSingerName);
                } else if (curStartMode == START_MODE_ALBUM) {
                    nav_title.setText(mAlbumname);
                }

                more = (TextView) findViewById(R.id.more);
                more.setOnClickListener(this);
            }
        }
        {
            registerReceiver(mFavBroadcastReceiver, new IntentFilter(
                    Contants.FILTER_ACTION_MARK_FAV));
        }
    }

    /*
     * (non-Javadoc)
     * @see com.tcl.lzhang1.mymusic.ui.AcitivityInit#initViewData()
     */
    @SuppressWarnings("unchecked")
    @Override
    public void initViewData() {
        // TODO Auto-generated method stub
        mDbOperator = new SongImp(this);
        mSingerOperate = new SingerImp(this);
        mAlbumOperate = new AlbumImp(this);
        mSingers = (List<SingerModel>) mSingerOperate
                .findAll("select count(*) as count,singername from songs group by singername");
        mAblums = (List<AlbumModel>) mAlbumOperate
                .findAll("select count(*) as count,ablumname from songs group by ablumname");

        if (curStartMode == START_MODE_ALBUM) {
            mSongs = (List<SongModel>) mDbOperator.findAll("select * from songs where ablumname='"
                    + mAlbumname + "'");
        } else if (curStartMode == START_MODE_SINGER) {
            mSongs = (List<SongModel>) mDbOperator.findAll("select * from songs where singername='"
                    + mSingerName + "'");
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.scan_music_album:
            case R.id.scan_music_singer:
            case R.id.scan_music:
                UIHelper.showScanMusicActivity(this, null);
                break;
            case R.id.back:
                onDestroy();
                break;
            case R.id.more:
                if (curStartMode == START_MODE_LOCAL && mPagerTabStrip != null
                        && mPagerTabStrip.getCurSelection() == 0) {
                    showListMore();
                }

                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        Bundle bundle = null;
        switch (arg0.getId()) {
            case R.id.music_list: {
                if (null == mSongs || mSongs.isEmpty()) {
                    return;
                }
                bundle = new Bundle();
                bundle.putSerializable("songs", new SongsWrap(mSongs));
                if (curStartMode == START_MODE_LOCAL)
                    bundle.putInt("index", arg2 - 1);
                else
                {
                    bundle.putInt("index", arg2);
                    sendPlayListChanedBroadCast();
                }

                bundle.putInt("time", 0);
                // Log.d(TAG, "msongs:" + mSongs.toString());
                UIHelper.showMusicPlayActivity(this, bundle);
                bundle = null;
            }
                break;
            case R.id.music_list_singer:
                if (mSingers == null || mSingers.isEmpty()) {
                    return;
                }
                bundle = new Bundle();
                bundle.putInt("startmode", START_MODE_SINGER);
                SingerModel model = mSingers.get(arg2);
                if (null == model) {
                    return;
                }
                bundle.putString("singername", model.getSinger_name());
                UIHelper.showMusicListActivity(this, bundle);
                break;

            case R.id.music_list_alubm:

                if (mAblums == null || mAblums.isEmpty()) {
                    return;
                }
                bundle = new Bundle();
                bundle.putInt("startmode", START_MODE_ALBUM);
                AlbumModel model1 = mAblums.get(arg2);
                if (null == model1) {
                    return;
                }
                bundle.putString("albumname", model1.getAlbum_name());
                UIHelper.showMusicListActivity(this, bundle);
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
        if (curStartMode == START_MODE_LOCAL)
            showMusicListDialog(mSongs.get(position - 1).getFile(),
                    position - 1);
        else
            showMusicListDialog(mSongs.get(position).getFile(),
                    position);
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
                                            Log.d(TAG, "will delete file:" + filePath);
                                            // delete database record
                                            mDbOperator.delete(filePath.trim());
                                            File file = new File(filePath);
                                            if (file.isFile()) {// delete file
                                                file.delete();
                                            }
                                            // remove data from data source
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
                                showMusicInfoDialog(filePath, position);
                                break;
                            default:

                                break;
                        }
                    }
                });
        builder.create().show();

    }

    private void showMusicInfoDialog(final String filePath, final int position) {
        Builder builder = new Builder(this);
        builder.setTitle(getString(R.string.song_info));
        StringBuilder sb = new StringBuilder();
        SongModel model = mSongs.get(position);
        sb.append("歌曲: " + model.getSongName());
        sb.append("\n");
        sb.append("歌手:" + model.getSingerName());
        sb.append("\n");
        sb.append("专辑:" + model.getAblumName());
        sb.append("\n");
        sb.append("备注:" + model.getRemark());
        sb.append("\n");
        sb.append("时长:" + MusicUtil.formatString(model.getMinutes(), model.getSeconds()));
        sb.append("\n");
        sb.append("路径:" + filePath);
        builder.setMessage(sb.toString());
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                return;
            }
        });

        builder.create().show();
    }

    /*
     * (non-Javadoc)
     * @see me.maxwin.view.XListView.IXListViewListener#onLoadMore()
     */
    @Override
    public void onLoadMore() {
        // TODO Auto-generated method stub
        int lvDataState = MusicUtil.toInt(mMusicList.getTag());
        if (UIHelper.LISTVIEW_DATA_MORE == lvDataState) {
            mMusicList.setTag(UIHelper.LISTVIEW_DATA_LOADING);
            int pageIndex = mSongs.size() / UIHelper.PAGE_SIZE + 1;
            loadData(pageIndex, UIHelper.PAGE_SIZE, UIHelper.LISTVIEW_ACTION_SCROLL, orderCol, mHandler);
        }
    }

    /*
     * (non-Javadoc)
     * @see me.maxwin.view.XListView.IXListViewListener#onRefresh()
     */
    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        // mMusicList.setRefreshTime(new
        // SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
        mMusicList.setPullLoadEnable(true);
        
        loadData(1, UIHelper.PAGE_SIZE, UIHelper.LISTVIEW_ACTION_REFRESH, orderCol, mHandler);

    }

    /**
     * load data
     * 
     * @param pageIndex
     * @param pageSize
     * @param tag
     * @param orderCol
     * @param handler
     */
    public void loadData(final int pageIndex, final int pageSize, final int tag,
            final String orderCol, final Handler handler) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message message = handler.obtainMessage();
                try {
                    if (tag == UIHelper.LISTVIEW_ACTION_REFRESH) {// scan music
                        
                    }

                    @SuppressWarnings("unchecked")
                    List<SongModel> msongs = (List<SongModel>) mDbOperator.sliptPage(pageIndex,
                            pageSize, orderCol);
                    message.what = LOADDATA_SUCCESS;
                    message.obj = msongs;
                    message.arg1 = msongs.size();
                    message.arg2 = tag;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                    handler.sendEmptyMessage(LOADDATA_FAILED);
                }

            }
        }).start();
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        // super.onBackPressed();
        onDestroy();
        overridePendingTransition(R.anim.no_vertical_tanslation, R.anim.push_down_out);
    }

    /**
     * send play list changed listener
     * 
     * @param intent
     * @param context
     */
    public void sendPlayListChanedBroadCast() {
        SongsWrap songsWrap = new SongsWrap(mSongs);
        Intent intent = new Intent(Contants.FILTER_ACTION_PLAY_LIST_CHANGED);
        Bundle bundle = new Bundle();
        bundle.putSerializable("songs", songsWrap);
        intent.putExtras(bundle);
        bundle = null;
        sendBroadcast(intent);
        intent = null;
    }

    /**
     * load singer and album data
     */
    public void loadSingerAndAlbum() {
        if (null == mSingerOperate) {
            mSingerOperate = new SingerImp(this);
        }

        if (null == mAlbumOperate) {
            mAlbumOperate = new AlbumImp(this);
        }

        new Thread(new Runnable() {

            @SuppressWarnings("unchecked")
            @Override
            public void run() {
                // TODO Auto-generated method stub
                mSingers = (List<SingerModel>) mSingerOperate
                        .findAll("select count(*) as count,singername from songs group by singername");
                Message msg = mHandler.obtainMessage();
                msg.what = LOAD_SINGER_SUCCESS;
                msg.obj = mSingers;
                mHandler.sendMessage(msg);

                mAblums = (List<AlbumModel>) mAlbumOperate
                        .findAll("select count(*) as count,ablumname from songs group by ablumname");
                msg = mHandler.obtainMessage();
                msg.what = LOAD_ALBUM_SUCCESS;
                msg.obj = mAblums;
                mHandler.sendMessage(msg);
            }
        }).start();
    }
}
