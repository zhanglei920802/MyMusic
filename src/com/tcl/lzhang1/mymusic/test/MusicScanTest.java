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

package com.tcl.lzhang1.mymusic.test;

import java.io.File;

import com.tcl.lzhang1.mymusic.MusicUtil;
import com.tcl.lzhang1.mymusic.db.DBOperator;
import com.tcl.lzhang1.mymusic.db.imp.SongImp;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.test.AndroidTestCase;

public class MusicScanTest extends AndroidTestCase {
    public void testFileScan() {
        MusicUtil.getMusicInfo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/test.mp3"));
    }

    public void testRuningService() {
        MusicUtil.checkServiceIsRunning(getContext(), getContext().getPackageName());
    }

    public void testFindAll() {
        DBOperator mDbOperator = new SongImp(getContext());
        mDbOperator.findAll("select * from songs where fav=1");
    }

    public void testGetMusicInfo() {
        ContentResolver contentResolver = getContext().getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] {
                    MediaStore.Audio.AudioColumns.DURATION
                }, " _data=?", new String[] {
                    Environment.getExternalStorageDirectory().getAbsolutePath()
                            + "/test.mp3"
                }, null);
        cursor.moveToFirst();
        long time = cursor.getLong(0);
        System.out.println("MusicScanTest.testGetMusicInfo(),time:" + time);
        cursor.close();
    }

    public void testHttp() {
        // byte[] data =
        // MusicUtil.doHttpPost(MusicUtil.getHttpPost("http://shopcgi.qqmusic.qq.com/fcgi-bin/shopsearch.fcg?",
        // ""));
    }
}
