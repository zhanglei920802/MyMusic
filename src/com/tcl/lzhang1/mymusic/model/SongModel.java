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
    private Integer mTime = 0;// how long
    private String mSongName = "";// song name
    private int type = 0;
    private String file = "";
    private String singer_img = "";
    private String ablum_img = "";
    private String remark = "";

    private int version; // MPEG版本（2+，2，1，0表示保留）
    private int layer; // 层级(1,2,3,0表示保留)
    private int protect; // 是否受CRC校验保护(1为保护，0为未保护）
    private int frameSize; // 帧长度
    private int bitrate; // 速率,bps
    private int simplingRate; // 采样率
    private int paddingBits; // 填充位数
    private int channel; // 声道模式（1为立体声，0为单声道）

    private String fileComplete;
    private boolean hasID3Tag;
    private String id3Title;
    private String id3Artist;
    private String id3Album;
    private String id3Year;
    private String id3Comment;
    private byte id3TrackNumber;
    private byte id3Genre;
    private long FileLength;
    private int hours;
    private int minutes;
    private int seconds;

    public String getFileComplete() {
        return fileComplete;
    }

    public void setFileComplete(String fileComplete) {
        this.fileComplete = fileComplete;
    }

    public boolean isHasID3Tag() {
        return hasID3Tag;
    }

    public void setHasID3Tag(boolean hasID3Tag) {
        this.hasID3Tag = hasID3Tag;
    }

    public String getId3Title() {
        return id3Title;
    }

    public void setId3Title(String id3Title) {
        this.id3Title = id3Title;
    }

    public String getId3Artist() {
        return id3Artist;
    }

    public void setId3Artist(String id3Artist) {
        this.id3Artist = id3Artist;
    }

    public String getId3Album() {
        return id3Album;
    }

    public void setId3Album(String id3Album) {
        this.id3Album = id3Album;
    }

    public String getId3Year() {
        return id3Year;
    }

    public void setId3Year(String id3Year) {
        this.id3Year = id3Year;
    }

    public String getId3Comment() {
        return id3Comment;
    }

    public void setId3Comment(String id3Comment) {
        this.id3Comment = id3Comment;
    }

    public byte getId3TrackNumber() {
        return id3TrackNumber;
    }

    public void setId3TrackNumber(byte id3TrackNumber) {
        this.id3TrackNumber = id3TrackNumber;
    }

    public byte getId3Genre() {
        return id3Genre;
    }

    public void setId3Genre(byte id3Genre) {
        this.id3Genre = id3Genre;
    }

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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public int getProtect() {
        return protect;
    }

    public void setProtect(int protect) {
        this.protect = protect;
    }

    public int getFrameSize() {
        return frameSize;
    }

    public void setFrameSize(int frameSize) {
        this.frameSize = frameSize;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public int getSimplingRate() {
        return simplingRate;
    }

    public void setSimplingRate(int simplingRate) {
        this.simplingRate = simplingRate;
    }

    public int getPaddingBits() {
        return paddingBits;
    }

    public void setPaddingBits(int paddingBits) {
        this.paddingBits = paddingBits;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int CalcFrameSize()
    {
        // 计算帧长度的公式
        this.frameSize = ((this.version == 1 ? 144 : 72) * 1000 * this.bitrate
                / this.simplingRate) + this.paddingBits;

        return this.frameSize;
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
    public Integer getTime() {
        return mTime;
    }

    /**
     * @param mTime the mTime to set
     */
    public void setTime(Integer mTime) {
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

    @Override
    public String toString() {
        return "SongModel [mSongID=" + mSongID + ", mAblumID=" + mAblumID + ", mSingerID="
                + mSingerID + ", mSingerName=" + mSingerName + ", mAblumName=" + mAblumName
                + ", mTime=" + mTime + ", mSongName=" + mSongName + ", type=" + type + ", file="
                + file + ", singer_img=" + singer_img + ", ablum_img=" + ablum_img + ", remark="
                + remark + ", version=" + version + ", layer=" + layer + ", protect=" + protect
                + ", frameSize=" + frameSize + ", bitrate=" + bitrate + ", simplingRate="
                + simplingRate + ", paddingBits=" + paddingBits + ", channel=" + channel
                + ", fileComplete=" + fileComplete + ", hasID3Tag=" + hasID3Tag + ", id3Title="
                + id3Title + ", id3Artist=" + id3Artist + ", id3Album=" + id3Album + ", id3Year="
                + id3Year + ", id3Comment=" + id3Comment + ", id3TrackNumber=" + id3TrackNumber
                + ", id3Genre=" + id3Genre + ", FileLength=" + FileLength + ", hours=" + hours
                + ", minutes=" + minutes + ", seconds=" + seconds + "]";
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

}
