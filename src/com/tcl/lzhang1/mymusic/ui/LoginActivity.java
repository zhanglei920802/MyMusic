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

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.TextView;
import cn.beyondme.widgets.ClearableEditText;
import cn.beyondme.widgets.MyAutoCompleteTextView;
import cn.beyondme.widgets.MyAutoCompleteTextView.OnCliearListener;

import com.tcl.lzhang1.mymusic.AppContext;
import com.tcl.lzhang1.mymusic.R;
import com.tcl.lzhang1.mymusic.UIHelper;
import com.tcl.lzhang1.mymusic.db.DBOperator;
import com.tcl.lzhang1.mymusic.db.imp.UserImp;
import com.tcl.lzhang1.mymusic.model.UserModel;
import com.tcl.lzhang1.mymusic.patterns.Origin;
import com.tcl.lzhang1.mymusic.patterns.Storage;

/**
 * @author leizhang
 */
public class LoginActivity extends BaseActivity implements OnClickListener, OnCliearListener {

    private TextView back = null;
    private TextView nav_title = null;

    private MyAutoCompleteTextView edit_text_uin = null;

    private ClearableEditText edit_text_password = null;

    private TextView login_register_txt1 = null;
    private TextView login_register_txt2 = null;
    private Button button_login = null;

    private DBOperator mDbOperator = null;

    private ArrayAdapter<String> mUserNameAdapter = null;

    private String[] mUserNames = null;

    private CheckBox show_pwd = null;

    /** The storage_tips. */
    private Storage storage = null;

    /** The origin. */
    private Origin origin = null;

    private List<UserModel> users = null;

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
     * <item android:state_focused="true"
     * android:drawable="@drawable/password_show_press" /> <item
     * android:state_selected="true"
     * android:drawable="@drawable/password_show_press" /> (non-Javadoc)
     * @see com.tcl.lzhang1.mymusic.ui.AcitivityInit#initView()
     */
    @Override
    public void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // TODO Auto-generated method stub
        TAG = getPackageName() + "/" + LoginActivity.class.getSimpleName();

        setContentView(R.layout.activity_login);
        {
            mDbOperator = new UserImp(this);
        }
        {
            back = (TextView) findViewById(R.id.back);
            back.setOnClickListener(this);
            nav_title = (TextView) findViewById(R.id.nav_title);

            edit_text_uin = (MyAutoCompleteTextView) findViewById(R.id.edit_text_uin);
            edit_text_uin.setClearListener(this);
            edit_text_uin.setThreshold(1);
            edit_text_uin.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // TODO Auto-generated method stub
                    // Log.d(TAG, "onTextChanged");
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // TODO Auto-generated method stub
                    // Log.d(TAG, "beforeTextChanged");
                }

                @SuppressWarnings("unchecked")
                @Override
                public void afterTextChanged(Editable s) {
                    // TODO Auto-generated method stub
                    Log.d(TAG, "afterTextChanged");

                    String userName = s.toString();
                    Log.d(TAG, "username:" + userName);
                    if (TextUtils.isEmpty(userName)) {
                        return;
                    }
                    // edit_text_uin.setOnItemSelectedListener(null);
                    // 创建备份show_pwd

                    origin = new Origin(users);
                    storage = new Storage(origin.createMemento());

                    users = (List<UserModel>) mDbOperator.find(userName);

                    // edit_text_uin.setOnItemClickListener(null);
                    if (DEBUG)
                        Log.d(TAG, "find users:" + users);
                    // overridePendingTransition(R.anim.no_vertical_tanslation,
                    // R.anim.push_down_out);
                    // 修改
                    origin.setValue(users);

                    mUserNames = new String[users.size()];
                    for (int i = 0; i < users.size(); i++) {
                        mUserNames[i] = users.get(i).getUserName();
                    }

                    if (null == mUserNameAdapter) {
                        mUserNameAdapter = new ArrayAdapter<String>(LoginActivity.this,
                                android.R.layout.simple_list_item_1, mUserNames);
                        edit_text_uin.setAdapter(mUserNameAdapter);
                    }
                    mUserNameAdapter.notifyDataSetChanged();
                    edit_text_uin.setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
                            if (null != origin) {
                                origin.restoreMemento(storage.getMemento());
                                users = (List<UserModel>) origin.getValue();
                                // edit_text_uin.addTextChangedListener(null);
                                edit_text_password.setText(users.get(position).getPassword());
                            }
                        }

                    });

                }
            });

            edit_text_password = (ClearableEditText) findViewById(R.id.edit_text_password);

            login_register_txt1 = (TextView) findViewById(R.id.login_register_txt1);
            login_register_txt2 = (TextView) findViewById(R.id.login_register_txt2);
            login_register_txt2.setOnClickListener(this);
            login_register_txt1.setOnClickListener(this);
            button_login = (Button) findViewById(R.id.button_login);
            button_login.setOnClickListener(this);

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
            case R.id.button_login: {
                String username = edit_text_uin.getText().toString();
                String password = edit_text_password.getText().toString();
                if (TextUtils.isEmpty(username)) {
                    // edit_text_uin.setError(getString(R.string.username_empty));
                    UIHelper.toast(this, R.string.username_empty);
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    UIHelper.toast(this, R.string.password_empty);
                    // edit_text_password.setError(getString(R.string.password_empty));
                    return;
                }

                @SuppressWarnings("unchecked")
                List<UserModel> users = (List<UserModel>) mDbOperator
                        .findAll("select * from user where user_id='" + username + "'");

                if (null == users || users.isEmpty()) {

                    UIHelper.toast(this, R.string.user_not_exit);
                    edit_text_password.setText("");
                    return;
                }

                assert (users.size() == 1);
                UserModel user = users.get(0);

                if (!password.equals(user.getPassword())) {
                    user.setIsLogin(0);
                    UIHelper.toast(this, R.string.password_incorrect);
                    edit_text_password.setText("");
                    return;
                }

                user.setIsLogin(1);
                AppContext.sLoginUser = user;
                mDbOperator.update(AppContext.sLoginUser);
                UIHelper.toast(this, R.string.login_success);
                onDestroy();

                break;
            }
            case R.id.login_register_txt2:

                UIHelper.showRegActivity(this, null);
                // startActivityForResult(new Intent(this, RegActivity.class),
                // 22);

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

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onActivityResult(int, int,
     * android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        // System.out.println("LoginActivity.onActivityResult()");
        switch (resultCode) {
            case 33:
                onDestroy();
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }

    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {

//        super.onBackPressed();
        onDestroy();
        overridePendingTransition(R.anim.no_vertical_tanslation, R.anim.push_down_out);
    }

    /*
     * (non-Javadoc)
     * @see com.tcl.lzhang1.mymusic.ui.BaseActivity#onResume()
     */
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        if (AppContext.sRegSuccess == true) {
            onDestroy();
            overridePendingTransition(R.anim.no_vertical_tanslation, R.anim.push_down_out);
        }
    }
}
