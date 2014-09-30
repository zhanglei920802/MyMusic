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

package com.tcl.lzhang1.mymusic.db.imp;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.tcl.lzhang1.mymusic.db.DBHelper;
import com.tcl.lzhang1.mymusic.db.DBOperator;
import com.tcl.lzhang1.mymusic.model.BaseModel;
import com.tcl.lzhang1.mymusic.model.SongModel;

/**
 * This is DBHelper For read & write sqlite database
 * 
 * @author leizhang
 */
public class SongImp implements DBOperator {
    private final String INSERT_SQL = "insert into songs values(?,?,?,?,?,?,?,?,?,?,?,?,?) ";
    private final String UPDATE_SQL = "update songs set  type=?,name=?,singername=?,ablumname?,remark=?,file=?,singer_img=?,singer_img=?,ablum_img where id = ?";
    private final String DELETE_SQL_ID = "delete from songs where id = ?";
    private final String DELETE_SQL = "delete from songs where file = ?";
    private final String SELECT_SQL = "select * from songs where  id = ?";
    private final String FIND_ALL_SQL = "select * from songs";
    private final String GET_COUNT = " select count(*) from songs";

    private Context mContext = null;
    private DBHelper mDbHelper = null;

    private String LOG_TAG = "";

    public SongImp(Context context) {
        // TODO Auto-generated constructor stub
        mContext = context;
        mDbHelper = new DBHelper(mContext);
        LOG_TAG = mContext.getPackageName() + "/" + SongImp.class.getSimpleName();
    }

    /*
     * (non-Javadoc)
     * @see
     * com.tcl.lzhang1.mymusic.db.DBOperator#add(com.tcl.lzhang1.mymusic.mode
     * .BaseModel)
     */
    @Override
    public void add(BaseModel addModel) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        if (null != db) {
            Object[] bindArgs = new Object[] {};
            db.execSQL(INSERT_SQL, bindArgs);

            db.close();
            db = null;
        }
    }

    /*
     * (non-Javadoc)
     * @see com.tcl.lzhang1.mymusic.db.DBOperator#delete(java.lang.Integer)
     */
    @Override
    public void delete(Integer id) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        if (null != db) {
            Object[] deleteArgs = new Object[] {
                    id
            };
            db.execSQL(DELETE_SQL_ID, deleteArgs);

            db.close();
            db = null;
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.tcl.lzhang1.mymusic.db.DBOperator#update(com.tcl.lzhang1.mymusic.
     * mode.BaseModel)
     */
    @Override
    public void update(BaseModel updateModel) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        if (null != db) {
            Object[] updateArgs = new Object[] {};
            db.execSQL(UPDATE_SQL, updateArgs);

            db.close();
            db = null;
        }
    }

    /*
     * (non-Javadoc)
     * @see com.tcl.lzhang1.mymusic.db.DBOperator#find(java.lang.Integer)
     */
    @Override
    public BaseModel find(Integer id) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        SongModel model = new SongModel();
        if (null != db) {
            String[] selectionArgs = new String[] {};
            Cursor cursor = db.rawQuery(SELECT_SQL, selectionArgs);

            cursor.moveToNext();
            // obtain datas

            cursor.close();
            cursor = null;
            db.close();
            db = null;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see com.tcl.lzhang1.mymusic.db.DBOperator#findAll()
     */
    @Override
    public List<SongModel> findAll() {
        // TODO Auto-generated method stub
        List<SongModel> songs = new ArrayList<SongModel>();
        SongModel model;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        if (null != db) {
            Cursor datas = db.rawQuery(FIND_ALL_SQL, null);
            // do iterator
            while (datas.moveToNext()) {
                // obtain datas
                model = new SongModel();
                {
                    model.setSongID(datas.getInt(datas.getColumnIndex("id")));
                    model.setType(datas.getInt(datas.getColumnIndex("type")));
                    model.setSongName(datas.getString(datas.getColumnIndex("name")));
                    model.setSingerName(datas.getString(datas.getColumnIndex("singername")));
                    model.setAblumName(datas.getString(datas.getColumnIndex("ablumname")));
                    model.setRemark(datas.getString(datas.getColumnIndex("remark")));
                    model.setFile(datas.getString(datas.getColumnIndex("file")));
                    model.setSinger_img(datas.getString(datas.getColumnIndex("singer_img")));
                    model.setAblum_img(datas.getString(datas.getColumnIndex("ablum_img")));
                    model.setHours(datas.getInt(datas.getColumnIndex("hours")));
                    model.setMinutes(datas.getInt(datas.getColumnIndex("minutes")));
                    model.setSeconds(datas.getInt(datas.getColumnIndex("seconds")));
                    model.setFav(datas.getInt(datas.getColumnIndex("fav")));
                }
                songs.add(model);
            }

            datas.close();
            datas = null;
        }
        return songs;
    }

    /*
     * (non-Javadoc)
     * @see com.tcl.lzhang1.mymusic.db.DBOperator#getCount()
     */
    @Override
    public Integer getCount() {
        // TODO Auto-generated method stub
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        if (null != db) {
            Cursor cursor = db.rawQuery(GET_COUNT, null);
            cursor.moveToNext();
            int count = cursor.getInt(0);
            cursor.close();
            cursor = null;
            return count;
        }
        return 0;
    }

    @Override
    public void saveAll(List<? extends BaseModel> models) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        if (null != db) {
            Object[] bindArgs = null;
            SongModel model = null;
            db.beginTransaction();
            try {
                // delete all
                db.execSQL("delete from songs");
                for (int i = 0; i < models.size(); i++) {
                    model = (SongModel) models.get(i);
                    bindArgs = new Object[] {
                            i + 1, model.getType(), model.getSongName(), model.getSingerName(),
                            model.getAblumName(), model.getRemark(), model.getFile().trim(),
                            model.getSinger_img(), model.getAblum_img(), model.getHours(),
                            model.getMinutes(), model.getSeconds(), 0
                    };
                    db.execSQL(INSERT_SQL, bindArgs);
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            db.endTransaction();
            db.close();
            db = null;
        }
        Log.i(LOG_TAG, "save musics finish ,totals :" + models.size());
    }

    /*
     * (non-Javadoc)
     * @see com.tcl.lzhang1.mymusic.db.DBOperator#delete(java.lang.String)
     */
    @Override
    public void delete(String string) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        if (null != db) {
            Object[] deleteArgs = new Object[] {
                    string
            };
            db.execSQL(DELETE_SQL, deleteArgs);

            db.close();
            db = null;
        }
    }

    /*model.setFav(0);
     * (non-Javadoc)
     * @see com.tcl.lzhang1.mymusic.db.DBOperator#find(java.lang.String)
     */
    @Override
    public BaseModel find(String sql) {
        return null;
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see com.tcl.lzhang1.mymusic.db.DBOperator#findAll(java.lang.String)
     */
    @Override
    public List<? extends BaseModel> findAll(String sql) {
        // TODO Auto-generated method stub
        List<SongModel> songs = new ArrayList<SongModel>();
        SongModel model;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        if (null != db) {
            Cursor datas = db.rawQuery(sql, null);
            // do iterator
            while (datas.moveToNext()) {
                // obtain datas
                model = new SongModel();
                {
                    model.setSongID(datas.getInt(datas.getColumnIndex("id")));
                    model.setType(datas.getInt(datas.getColumnIndex("type")));
                    model.setSongName(datas.getString(datas.getColumnIndex("name")));
                    model.setSingerName(datas.getString(datas.getColumnIndex("singername")));
                    model.setAblumName(datas.getString(datas.getColumnIndex("ablumname")));
                    model.setRemark(datas.getString(datas.getColumnIndex("remark")));
                    model.setFile(datas.getString(datas.getColumnIndex("file")));
                    model.setSinger_img(datas.getString(datas.getColumnIndex("singer_img")));
                    model.setAblum_img(datas.getString(datas.getColumnIndex("ablum_img")));
                    model.setHours(datas.getInt(datas.getColumnIndex("hours")));
                    model.setMinutes(datas.getInt(datas.getColumnIndex("minutes")));
                    model.setSeconds(datas.getInt(datas.getColumnIndex("seconds")));
                    model.setFav(datas.getInt(datas.getColumnIndex("fav")));
                }
                songs.add(model);
            }

            datas.close();
            datas = null;
        }
        return songs;
    }

    /*
     * (non-Javadoc)
     * @see com.tcl.lzhang1.mymusic.db.DBOperator#executeSQL(java.lang.String)
     */
    @Override
    public void executeSQL(String sql) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        if (null != db) {
            db.execSQL(sql);
            db.close();
            db = null;
        }
    }
}
