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

import java.util.HashMap;
import java.util.List;

import com.tcl.lzhang1.mymusic.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author leizhang
 */
public class MineAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<HashMap<String, String>> mList;
    private Context mContext = null;

    /**
     * 
     */
    public MineAdapter(Context context, List<HashMap<String, String>> datas) {
        // TODO Auto-generated constructor stub
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mList = datas;
        this.mContext = context;
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mList.size();
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public HashMap<String, String> getItem(int position) {
        // TODO Auto-generated method stub
        return mList.get(position);
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        HashMap<String, String> map = null;
        SimpleViewHolder simpleViewHolder;
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.main_item_simple_list, null);
            simpleViewHolder = new SimpleViewHolder();
            simpleViewHolder.name = (TextView) convertView.findViewById(R.id.name);
            simpleViewHolder.value = (TextView) convertView.findViewById(R.id.value);
            convertView.setTag(simpleViewHolder);
        } else {
            simpleViewHolder = (SimpleViewHolder) convertView.getTag();
        }

        map = mList.get(position);
        String name = map.get("name");
        Drawable drawable = null;
        if (mContext.getString(R.string.fav_music).equals(name)) {
            drawable = mContext.getResources().getDrawable(R.drawable.my_music_folder);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            simpleViewHolder.name.setCompoundDrawables(drawable, null, null, null);

        } else if (mContext.getString(R.string.download_music).equals(name)) {
            drawable = mContext.getResources().getDrawable(R.drawable.my_music_downloaded);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            simpleViewHolder.name.setCompoundDrawables(drawable, null, null, null);
        } else if (mContext.getString(R.string.local_music).equals(name)) {
            drawable = mContext.getResources().getDrawable(R.drawable.my_music_all);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            simpleViewHolder.name.setCompoundDrawables(drawable, null, null, null);
        }
        simpleViewHolder.name.setText(name);
        simpleViewHolder.value.setText(map.get("value"));

        return convertView;
    }

    private final class SimpleViewHolder {
        TextView name;
        TextView value;
    }
}
