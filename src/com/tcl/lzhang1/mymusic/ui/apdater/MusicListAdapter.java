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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tcl.lzhang1.mymusic.MusicUtil;
import com.tcl.lzhang1.mymusic.R;
import com.tcl.lzhang1.mymusic.model.SongModel;

/**
 * The music list adapter
 * 
 * @author leizhang
 */
public class MusicListAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater = null;
    private List<SongModel> mSongModels = null;

    public MusicListAdapter(Context context, List<SongModel> songs) {
        this.mLayoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mSongModels = songs;
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mSongModels.size();
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public SongModel getItem(int position) {
        // TODO Auto-generated method stub
        // return null;

        return mSongModels.get(position);
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
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.music_list_item, null);
            holder.songName = (TextView) convertView.findViewById(R.id.music_item_name);
            holder.singerName = (TextView) convertView.findViewById(R.id.music_item_singer);
            holder.longOfSong = (TextView) convertView.findViewById(R.id.music_item_long);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SongModel model = mSongModels.get(position);
        holder.songName.setText(model.getSongName());
        holder.singerName.setText(model.getSingerName());
        holder.longOfSong.setText(MusicUtil.formatString(model.getMinutes(), model.getSeconds()));

        return convertView;
    }

    private final class ViewHolder {
        TextView songName = null;
        TextView singerName = null;
        TextView longOfSong = null;
    }

}
