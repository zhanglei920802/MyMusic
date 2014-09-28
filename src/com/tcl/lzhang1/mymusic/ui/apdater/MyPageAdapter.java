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

package com.tcl.lzhang1.mymusic.ui.apdater;

import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * This Menu Adapter For PageViews
 * 
 * @author leizhang
 */
public class MyPageAdapter extends PagerAdapter {

    private List<View> mViews = null;
    private String[] mTitles = null;
    private Context mContext = null;

    public MyPageAdapter(Context context) {
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public MyPageAdapter(Context context, List<View> views, String[] strings) {
        this.mContext = context;
        this.mTitles = strings;
        this.mViews = views;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mViews.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        // TODO Auto-generated method stub
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO Auto-generated method stub
        // super.destroyItem(container, position, object);
        container.removeView(mViews.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // TODO Auto-generated method stub
        // return super.instantiateItem(container, position);
        container.addView(mViews.get(position));
        return mViews.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // TODO Auto-generated method stub
        // return super.getPageTitle(position);
        return mTitles[position];
    }

}
