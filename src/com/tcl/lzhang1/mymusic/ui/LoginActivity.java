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

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import cn.beyondme.widgets.ClearableEditText;
import cn.beyondme.widgets.ClearableEditText.OnCliearListener;

import com.tcl.lzhang1.mymusic.R;
import com.tcl.lzhang1.mymusic.UIHelper;

/**
 * @author leizhang
 */
public class LoginActivity extends BaseActivity implements OnClickListener, OnCliearListener {

    private TextView back = null;
    private TextView nav_title = null;

    private ClearableEditText edit_text_uin = null;

    private ClearableEditText edit_text_password = null;

    private TextView login_register_txt1 = null;
    private TextView login_register_txt2 = null;
    private Button button_login = null;

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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // TODO Auto-generated method stub
        TAG = getPackageName() + "/" + LoginActivity.class.getSimpleName();

        setContentView(R.layout.activity_login);
        {
            back = (TextView) findViewById(R.id.back);
            back.setOnClickListener(this);
            nav_title = (TextView) findViewById(R.id.nav_title);

            edit_text_uin = (ClearableEditText) findViewById(R.id.edit_text_uin);
            edit_text_uin.setClearListener(this);

            edit_text_password = (ClearableEditText) findViewById(R.id.edit_text_password);

            login_register_txt1 = (TextView) findViewById(R.id.login_register_txt1);
            login_register_txt2 = (TextView) findViewById(R.id.login_register_txt2);
            login_register_txt2.setOnClickListener(this);
            login_register_txt1.setOnClickListener(this);
            button_login = (Button) findViewById(R.id.button_login);
            button_login.setOnClickListener(this);
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
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.login_register_txt1:
            case R.id.login_register_txt2:
                UIHelper.showRegActivity(this, null);
                break;
            default:
                break;
        }
    }

    /*
     * (non-Javadoc)
     * @see cn.beyondme.widgets.ClearableEditText.OnCliearListener#onClearText()
     */
    @Override
    public void onClearText() {
        // TODO Auto-generated method stub
        edit_text_password.setText("");
    }

}
