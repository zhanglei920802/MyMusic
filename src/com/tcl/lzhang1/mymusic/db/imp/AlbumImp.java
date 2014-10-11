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

import com.tcl.lzhang1.mymusic.db.DBHelper;
import com.tcl.lzhang1.mymusic.db.DBOperator;
import com.tcl.lzhang1.mymusic.model.AlbumModel;
import com.tcl.lzhang1.mymusic.model.BaseModel;

/**
 * @author leizhang
 */
public class AlbumImp implements DBOperator {
    private Context mContext = null;
    private DBHelper mDbHelper = null;

    private String LOG_TAG = "";

    public AlbumImp(Context context) {
        // TODO Auto-generated constructor stub
        mContext = context;
        mDbHelper = new DBHelper(mContext);
        LOG_TAG = mContext.getPackageName() + "/" + AlbumImp.class.getSimpleName();
    }

    /*
     * (non-Javadoc)
     * @see
     * com.tcl.lzhang1.mymusic.db.DBOperator#add(com.tcl.lzhang1.mymusic.model
     * .BaseModel)
     */
    @Override
    public void add(BaseModel addModel) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see com.tcl.lzhang1.mymusic.db.DBOperator#delete(java.lang.Integer)
     */
    @Override
    public void delete(Integer id) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see
     * com.tcl.lzhang1.mymusic.db.DBOperator#update(com.tcl.lzhang1.mymusic.
     * model.BaseModel)
     */
    @Override
    public void update(BaseModel updateModel) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see com.tcl.lzhang1.mymusic.db.DBOperator#find(java.lang.Integer)
     */
    @Override
    public BaseModel find(Integer id) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see com.tcl.lzhang1.mymusic.db.DBOperator#findAll()
     */
    @Override
    public List<? extends BaseModel> findAll() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see com.tcl.lzhang1.mymusic.db.DBOperator#getCount()
     */
    @Override
    public Integer getCount() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see com.tcl.lzhang1.mymusic.db.DBOperator#saveAll(java.util.List)
     */
    @Override
    public void saveAll(List<? extends BaseModel> models) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see com.tcl.lzhang1.mymusic.db.DBOperator#delete(java.lang.String)
     */
    @Override
    public void delete(String string) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see com.tcl.lzhang1.mymusic.db.DBOperator#find(java.lang.String)
     */
    @Override
    public List<? extends BaseModel> find(String sql) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see com.tcl.lzhang1.mymusic.db.DBOperator#findAll(java.lang.String)
     */
    @Override
    public List<? extends BaseModel> findAll(String sql) {
        // TODO Auto-generated method stub

        // TODO Auto-generated method stub
        List<AlbumModel> songs = new ArrayList<AlbumModel>();
        AlbumModel model;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        if (null != db) {
            Cursor datas = db.rawQuery(sql, null);
            // do iterator
            while (datas.moveToNext()) {
                // obtain datas
                model = new AlbumModel();
                {
                    model.setId(datas.getString(datas.getColumnIndex("ablumname")));
                    model.setAlbum_name(datas.getString(datas.getColumnIndex("ablumname")));
                    model.setSongCount(datas.getInt(datas.getColumnIndex("count")));

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

    }

    /*
     * (non-Javadoc)
     * @see com.tcl.lzhang1.mymusic.db.DBOperator#sliptPage(int, int,
     * java.lang.String)
     */
    @Override
    public List<? extends BaseModel> sliptPage(int pageIndex, int pageSize, String columnName) {
        // TODO Auto-generated method stub
        return null;
    }

}
