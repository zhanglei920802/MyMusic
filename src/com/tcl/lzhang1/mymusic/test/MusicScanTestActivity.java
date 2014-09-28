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

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.tcl.lzhang1.mymusic.ui.BaseActivity;

public class MusicScanTestActivity extends BaseActivity {

    @Override
    public void getPreActivityData(Bundle bundle) {
        // TODO Auto-generated method stub

    }

    @Override
    public void initView() {
        // TODO Auto-generated method stub
        MediaScannerConnection.scanFile(this,
                new String[] {
                    "/storage/sdcard0"
                },
                new String[] {
                    "*/music"
                },
                new MediaScannerConnection.OnScanCompletedListener() {

                    @Override
                    public void onScanCompleted(String path, final Uri uri) {
                        // TODO Auto-generated method stub
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                Toast.makeText(MusicScanTestActivity.this, uri.toString(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
    }

    @Override
    public void initViewData() {
        // TODO Auto-generated method stub

    }

}
