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

import cn.beyondme.widgets.ClearableEditText;

import com.tcl.lzhang1.mymusic.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

/**
 * @author leizhang
 */
public class RegActivity extends BaseActivity implements OnClickListener {
    private ClearableEditText edit_text_email = null;
    private ClearableEditText edit_text_reg_tel = null;
    private ClearableEditText edit_text_password = null;
    private ClearableEditText edit_text_re_pwd = null;
    private TextView back = null;
    private TextView nav_title = null;

    /*
     * (non-Javadoc)
     * @see
     * com.tcl.lzhang1.mymusic.ui.AcitivityInit#getPreActivityData(android.os
     * .Bundle)user_reg
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
        // TODO Auto-generated method stub
        TAG = getPackageName() + "/" + RegActivity.class.getSimpleName();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_reg);
        {
            back = (TextView) findViewById(R.id.back);
            back.setOnClickListener(this);
            nav_title = (TextView) findViewById(R.id.nav_title);
            nav_title.setText(R.string.user_reg);

            edit_text_email = (ClearableEditText) findViewById(R.id.edit_text_email);
            edit_text_email.setClearListener(new ClearableEditText.OnCliearListener() {

                @Override
                public void onClearText() {
                    // TODO Auto-generated method stub
                    edit_text_reg_tel.setText("");
                    edit_text_password.setText("");
                    edit_text_re_pwd.setText("");
                }
            });

            edit_text_reg_tel = (ClearableEditText) findViewById(R.id.edit_text_reg_tel);
            edit_text_password = (ClearableEditText) findViewById(R.id.edit_text_password);
            edit_text_re_pwd = (ClearableEditText) findViewById(R.id.edit_text_re_pwd);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.tcl.lzhang1.mymusic.ui.AcitivityInit#initViewData()
     */
    @Override
    public void initViewData() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }

}
