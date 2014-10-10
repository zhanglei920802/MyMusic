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

package com.tcl.lzhang1.mymusic.model;

/**
 * @author leizhang
 */
public class SongModel extends BaseModel {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private Integer mSongID = 0;// song id
    private Integer mAblumID = 0;// ablum ID
    private Integer mSingerID = 0;// Singer ID
    private String mSingerName = "";// singer name
    private String mAblumName = "";// Ablulm name
    private long mTime = 0;// how long
    private String mSongName = "";// song name
    private int type = 0;
    private String file = "";
    private String singer_img = "";
    private String ablum_img = "";
    private String remark = "";
    private int fav = 0;// 我喜欢的,1为真,0为假
    
    private long FileLength;
    private int hours;
    private int minutes;
    private int seconds;

    public long getFileLength() {
        return FileLength;
    }

    public void setFileLength(long fileLength) {
        FileLength = fileLength;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    /**
     * @return the mSongID
     */
    public Integer getSongID() {
        return mSongID;
    }

    /**
     * @param mSongID the mSongID to set
     */
    public void setSongID(Integer mSongID) {
        this.mSongID = mSongID;
    }

    /**
     * @return the mAblumID
     */
    public Integer getAblumID() {
        return mAblumID;
    }

    /**
     * @param mAblumID the mAblumID to set
     */
    public void setAblumID(Integer mAblumID) {
        this.mAblumID = mAblumID;
    }

    /**
     * @return the mSingerID
     */
    public Integer getSingerID() {
        return mSingerID;
    }

    /**
     * @param mSingerID the mSingerID to set
     */
    public void setSingerID(Integer mSingerID) {
        this.mSingerID = mSingerID;
    }

    /**
     * @return the mSingerName
     */
    public String getSingerName() {
        return mSingerName;
    }

    /**
     * @param mSingerName the mSingerName to set
     */
    public void setSingerName(String mSingerName) {
        this.mSingerName = mSingerName;
    }

    /**
     * @return the mAblumName
     */
    public String getAblumName() {
        return mAblumName;
    }

    /**
     * @param mAblumName the mAblumName to set
     */
    public void setAblumName(String mAblumName) {
        this.mAblumName = mAblumName;
    }

    /**
     * @return the mTime
     */
    public long getTime() {
        return mTime;
    }

    /**
     * @param mTime the mTime to set
     */
    public void setTime(long mTime) {
        this.mTime = mTime;
    }

    /**
     * @return the mSongName
     */
    public String getSongName() {
        return mSongName;
    }

    /**
     * @param mSongName the mSongName to set
     */
    public void setSongName(String mSongName) {
        this.mSongName = mSongName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getSinger_img() {
        return singer_img;
    }

    public void setSinger_img(String singer_img) {
        this.singer_img = singer_img;
    }

    public String getAblum_img() {
        return ablum_img;
    }

    public void setAblum_img(String ablum_img) {
        this.ablum_img = ablum_img;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    /**
     * @return the fav
     */
    public int getFav() {
        return fav;
    }

    /**
     * @param fav the fav to set
     */
    public void setFav(int fav) {
        this.fav = fav;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark the remark to set
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "SongModel [mSongID=" + mSongID + ", mAblumID=" + mAblumID + ", mSingerID="
                + mSingerID + ", mSingerName=" + mSingerName + ", mAblumName=" + mAblumName
                + ", mTime=" + mTime + ", mSongName=" + mSongName + ", type=" + type + ", file="
                + file + ", singer_img=" + singer_img + ", ablum_img=" + ablum_img + ", remark="
                + remark + ", fav=" + fav + ", FileLength=" + FileLength + ", hours=" + hours
                + ", minutes=" + minutes + ", seconds=" + seconds + "]";
    }

}
