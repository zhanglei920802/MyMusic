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

import java.util.List;

import cn.beyondme.widgets.ClearableEditText;

import com.tcl.lzhang1.mymusic.AppContext;
import com.tcl.lzhang1.mymusic.MusicUtil;
import com.tcl.lzhang1.mymusic.R;
import com.tcl.lzhang1.mymusic.UIHelper;
import com.tcl.lzhang1.mymusic.db.DBOperator;
import com.tcl.lzhang1.mymusic.db.imp.UserImp;
import com.tcl.lzhang1.mymusic.model.UserModel;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * @author leizhang
 */
public class RegActivity extends BaseActivity implements OnClickListener {

    private ClearableEditText edit_text_email = null;

    private ClearableEditText edit_text_reg_tel = null;

    private ClearableEditText edit_text_password = null;

    private ClearableEditText edit_text_re_pwd = null;

    private ClearableEditText edit_text_username = null;

    private TextView back = null;

    private TextView nav_title = null;

    private LinearLayout reg_reg_ll = null;

    private Button button_reg = null;

    private DBOperator mDbOperator = null;

    private AppContext mAppContext = null;

    private CompoundButton show_pwd;

    private CheckBox show_re_pwd;

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
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.content_ll_fl);
        viewGroup.addView(getLayoutInflater().inflate(R.layout.activity_reg, null));
        TAG = getPackageName() + "/" + RegActivity.class.getSimpleName();
        // setContentView(R.layout.activity_reg);
        mAppContext = (AppContext) getApplication();

        {
            back = (TextView) findViewById(R.id.back);
            back.setOnClickListener(this);
            nav_title = (TextView) findViewById(R.id.nav_title);
            nav_title.setText(R.string.user_reg);

            edit_text_email = (ClearableEditText) findViewById(R.id.edit_text_email);
            edit_text_username = (ClearableEditText) findViewById(R.id.edit_text_username);
            edit_text_username.setClearListener(new ClearableEditText.OnCliearListener() {

                @Override
                public void onClearText() {
                    // TODO Auto-generated method stub
                    edit_text_reg_tel.setText("");
                    edit_text_password.setText("");
                    edit_text_re_pwd.setText("");
                    edit_text_email.setText("");

                }
            });
            reg_reg_ll = (LinearLayout) findViewById(R.id.reg_reg_ll);
            reg_reg_ll.setOnClickListener(this);
            edit_text_reg_tel = (ClearableEditText) findViewById(R.id.edit_text_reg_tel);
            edit_text_password = (ClearableEditText) findViewById(R.id.edit_text_password);
            edit_text_re_pwd = (ClearableEditText) findViewById(R.id.edit_text_re_pwd);
            button_reg = (Button) findViewById(R.id.button_reg);
            button_reg.setOnClickListener(this);
            show_pwd = (CheckBox) findViewById(R.id.show_pwd);
            show_pwd.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // TODO Auto-generated method stub
                    if (isChecked) {
                        edit_text_password
                                .setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    } else {
                        edit_text_password.setInputType(InputType.TYPE_CLASS_TEXT
                                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        if (TextUtils.isEmpty(edit_text_password.getText())) {
                            return;
                        }
                        edit_text_password.setSelection(edit_text_password.getText().length());
                    }
                }
            });

            show_re_pwd = (CheckBox) findViewById(R.id.show_re_pwd);
            show_re_pwd.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // TODO Auto-generated method stub
                    if (isChecked) {
                        edit_text_re_pwd
                                .setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    } else {
                        edit_text_re_pwd.setInputType(InputType.TYPE_CLASS_TEXT
                                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        if (TextUtils.isEmpty(edit_text_re_pwd.getText())) {
                            return;
                        }
                        edit_text_re_pwd.setSelection(edit_text_re_pwd.getText().length());
                    }
                }
            });

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
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        // super.onBackPressed();
        // setResult(33, new Intent());
        AppContext.sRegSuccess = false;
        onDestroy();
        overridePendingTransition(R.anim.no_horizontal_translation, R.anim.push_right_out);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        overridePendingTransition(R.anim.no_horizontal_translation, R.anim.push_right_out);
    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.reg_reg_ll:

            case R.id.back:
                // setResult(33, new Intent());
                AppContext.sRegSuccess = false;
                onDestroy();
                overridePendingTransition(R.anim.no_horizontal_translation, R.anim.push_right_out);
                break;
            case R.id.button_reg: {
                String username = edit_text_username.getText().toString();
                String tel = edit_text_reg_tel.getText().toString();
                String email = edit_text_email.getText().toString();
                String pwd = edit_text_password.getText().toString();
                String repwd = edit_text_re_pwd.getText().toString();

                // empty check
                if (TextUtils.isEmpty(username)) {
                    // edit_text_uin.setError(getString(R.string.username_empty));
                    UIHelper.toast(this, R.string.username_empty);
                    return;
                }

                if (TextUtils.isEmpty(tel)) {
                    UIHelper.toast(this, R.string.tel_empty);
                    // edit_text_password.setError(getString(R.string.password_empty));edit_text_password
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    // edit_text_uin.setError(getString(R.string.username_empty));
                    UIHelper.toast(this, R.string.email_empty);
                    overridePendingTransition(R.anim.no_horizontal_translation,
                            R.anim.push_right_out);
                    return;
                }

                if (TextUtils.isEmpty(pwd)) {
                    // edit_text_uin.setError(getString(R.string.username_empty));
                    UIHelper.toast(this, R.string.pwd_empty);
                    return;
                }
                if (TextUtils.isEmpty(repwd)) {
                    // edit_text_uin.setError(getString(R.string.username_empty));
                    UIHelper.toast(this, R.string.re_pwd_empty);
                    return;
                }

                // formart check
                if (!MusicUtil.isEmail(email)) {
                    UIHelper.toast(this, R.string.email_error_formart);
                    return;
                }

                if (!MusicUtil.isMobileNO(tel)) {
                    UIHelper.toast(this, R.string.tel_error_format);
                    return;
                }

                // pwd repeat check
                if (!pwd.equals(repwd)) {
                    UIHelper.toast(this, R.string.pwd_not_equal);
                    return;
                }

                // unique check of username
                if (null == mDbOperator) {
                    mDbOperator = new UserImp(this);
                }
                @SuppressWarnings("unchecked")
                List<UserModel> models = (List<UserModel>) mDbOperator
                        .findAll("select * from user where user_id='"
                                + username + "'");
                if (models != null && models.size() > 0) {
                    UIHelper.toast(this, R.string.user_exit);
                    return;
                }

                UserModel model = new UserModel();
                model.setUserName(username);
                model.setPassword(pwd);
                model.setIsLogin(1);
                model.setTel(tel);
                model.setEmail(email);
                mDbOperator.add(model);
                mAppContext.saveLoginUserPrefer(username);
                AppContext.sLoginUser = model;
                UIHelper.toast(this, R.string.reg_success);

                // setResult(33, new Intent());
                AppContext.sRegSuccess = true;
                onDestroy();
                overridePendingTransition(R.anim.no_horizontal_translation, R.anim.push_right_out);
            }
                break;
            default:
                break;
        }
    }

}
