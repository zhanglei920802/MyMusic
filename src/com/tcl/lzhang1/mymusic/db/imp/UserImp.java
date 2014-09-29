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

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tcl.lzhang1.mymusic.db.DBHelper;
import com.tcl.lzhang1.mymusic.db.DBOperator;
import com.tcl.lzhang1.mymusic.model.BaseModel;
import com.tcl.lzhang1.mymusic.model.SongModel;
import com.tcl.lzhang1.mymusic.model.UserModel;

/**
 * @author leizhang
 */
public class UserImp implements DBOperator {
    private static final String SELECT_SQL = "select * from user where user_id=?";

    private Context mContext = null;
    private DBHelper mDbHelper = null;

    private String LOG_TAG = "";
    private String INSERT_SQL = "insert into user values(?,?)";
    private String DELETE_SQL_ID = "delete from user where  user_id=? ";
    private String UPDATE_SQL = "update user set password=? where user_id";

    private String SELECT_SQL_LIKE = "select * from user where user_id like ?";

    public UserImp(Context context) {
        // TODO Auto-generated constructor stub
        mContext = context;
        mDbHelper = new DBHelper(mContext);
        LOG_TAG = mContext.getPackageName() + "/" + SongImp.class.getSimpleName();
    }

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
     * model.BaseModel)
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
    public UserModel find(Integer id) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        UserModel model = new UserModel();
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
    public BaseModel find(String sql) {
        // TODO Auto-generated method stub

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        UserModel model = new UserModel();
        if (null != db) {
            String[] selectionArgs = new String[] {
                "'%" + sql + "%'"
            };
            Cursor cursor = db.rawQuery(SELECT_SQL_LIKE, selectionArgs);

            cursor.moveToNext();
            // obtain datas

            cursor.close();
            cursor = null;
            db.close();
            db = null;
        }
        return null;

    }

    /* (non-Javadoc)
     * @see com.tcl.lzhang1.mymusic.db.DBOperator#findAll(java.lang.String)
     */
    @Override
    public List<? extends BaseModel> findAll(String sql) {
        // TODO Auto-generated method stub
        return null;
    }

}
