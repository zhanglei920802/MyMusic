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

package com.tcl.lzhang1.mymusic.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author leizhang
 */
public class DBHelper extends SQLiteOpenHelper {

    private int DB_VERSION = 1;
    private String CREATE_SQL = "CREATE TABLE [songs] (  [id] INT,   [type] INT DEFAULT 2,   [name] NVARCHAR2 NOT NULL,   [singername] NVARCHAR2 NOT NULL,   [ablumname] NVARCHAR2 NOT NULL,  [remark] NVARCHAR2,  [file] NVARCHAR2 NOT NULL,  [singer_img] NVARCHAR2,  [ablum_img] CHAR,[hours] int ,  [minutes] int , [seconds] int ,CONSTRAINT [] PRIMARY KEY ([id]) ON CONFLICT ABORT)";
    private String CREATE_USER_SQL = "CREATE TABLE [user] (  [user_id] NVARCHAR2,   [password] NVARCHAR2,   CONSTRAINT [] PRIMARY KEY ([user_id]))";

    /**
	 * 
	 */
    public DBHelper(Context context) {
        // TODO Auto-generated constructor stub
        super(context, "music.db", null, 1);

    }

    /**
     * @param context
     * @param name
     * @param factory
     * @param version
     */
    public DBHelper(Context context, String name, CursorFactory factory,
            int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * @see
     * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
     * .SQLiteDatabase)
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(CREATE_SQL);
        db.execSQL(CREATE_USER_SQL);
    }

    /*
     * (non-Javadoc)
     * @see
     * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
     * .SQLiteDatabase, int, int)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

}
