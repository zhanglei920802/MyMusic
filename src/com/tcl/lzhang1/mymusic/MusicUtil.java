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

package com.tcl.lzhang1.mymusic;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.tcl.lzhang1.mymusic.exception.ErrorMusicLength;
import com.tcl.lzhang1.mymusic.exception.SDCardUnMoutedException;
import com.tcl.lzhang1.mymusic.exception.UnknownTagException;
import com.tcl.lzhang1.mymusic.model.SongModel;

/**
 * This is Music util
 * 
 * @author leizhang 503 case kEncodingShiftJIS: 504 enc = "shift-jis"; 505
 *         break; 506 case kEncodingGBK: 507 enc = "gbk"; 508 break; 509 case
 *         kEncodingBig5: 510 enc = "Big5"; 511 break; 512 case kEncodingEUCKR:
 *         513 enc = "EUC-KR"; 514 break;
 */
@SuppressLint("DefaultLocale")
public class MusicUtil {

    private final static String LOG_TAG = "MusicUtil";
    private static onMusicScanListener mScanListener = null;

    public static void setMusicListener(onMusicScanListener musicScanListener) {
        mScanListener = musicScanListener;
    }

    private static final String[] suffix = new String[] {
            ".mp3"
    };
    private static final String EMAIL_PATTERN = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
    private static final String MOBILE_PATTERN = "1([\\d]{10})|((\\+[0-9]{2,4})?\\(?[0-9]+\\)?-?)?[0-9]{7,8}";
    private static Context sContext = null;
    private static List<SongModel> mSongs = new ArrayList<SongModel>();

    /**
     * scan music by path <br>
     * if path is null or empty,scan all scard
     * 
     * @param path
     * @return
     * @throws SDCardUnMoutedException
     */
    public static List<SongModel> scanMusic(String path, Context context)
            throws SDCardUnMoutedException {
        if (TextUtils.isEmpty(path)) {
            scanMusic(context);
        }
        return null;
    }

    /**
     * scan music in sdcard
     * 
     * @return
     * @throws SDCardUnMoutedException
     */
    public static List<SongModel> scanMusic(Context context) throws SDCardUnMoutedException {
        // onle scan sdcard path

        sContext = context;

        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_UNMOUNTED)) {
            throw new SDCardUnMoutedException("sorry sdcard was not mounted");
        }

        if (null != mSongs) {
            mSongs.clear();
        }
        File rootFile = Environment.getExternalStorageDirectory()
                .getAbsoluteFile();

        scanFile(rootFile);

        if (null != mSongs && mScanListener != null) {
            mScanListener.onMusicScanedFinish();
        }

        return mSongs;
    }

    /**
     * scan file by path
     * 
     * @param path
     */
    private static void scanFile(String path) {
        scanFile(new File(path));
    }

    /**
     * scan file by file
     * 
     * @param file
     */
    public static void scanFile(File file) {
        if (file == null) {
            return;
        }

        if (!file.isHidden()) {

            if (file.isFile()) {
                // do something
                if (!isMusic(file)) {
                    return;
                }

                SongModel model = getMusicInfo(file
                        .getAbsolutePath());
                if (null != mScanListener) {
                    Log.i(LOG_TAG, "music file:" + file.getName());
                    mScanListener.onMusicScaned(model);
                }
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File file2 : files) {
                    scanFile(file2);
                }
            }
        } else {

            return;

        }
    }

    /**
     * check current file is music file
     * 
     * @param file
     * @return
     */
    private static boolean isMusic(File file) {

        boolean isFind = false;
        String fileName = file.getName();
        for (int i = 0; i < suffix.length; i++) {
            if (fileName.endsWith(suffix[i])) {
                isFind = true;
                break;
            }
        }

        return isFind;
    }

    /**
     * @param musicPath
     * @return
     */
    public static SongModel getMusicInfo(String musicPath) {
        return getMusicInfo(new File(musicPath));
    }

    /**
     * get the music info
     * 
     * @param musicFile
     * @return
     */
    public static SongModel getMusicInfo(File musicFile) {
        SongModel model = new SongModel();
        // retrun null if music file is null or is or directory
        if (musicFile == null || !musicFile.isFile()) {
            return null;
        }

        byte[] buf = new byte[128];
        try {
            // tag_v1
            RandomAccessFile music = new RandomAccessFile(musicFile, "r");

            music.seek(music.length() - 128);
            music.read(buf);// read tag to buffer
            // tag_v2
            byte[] header = new byte[10];
            music.seek(0);
            music.read(header, 0, 10);
            // if ("ID3".equalsIgnoreCase(new String(header, 0, 3))) {
            // int ID3V2_frame_size = (int) (header[6] & 0x7F) * 0x200000
            // | (int) (header[7] & 0x7F) * 0x400
            // | (int) (header[8] & 0x7F) * 0x80
            // | (int) (header[9] & 0x7F);
            // byte[] FrameHeader = new byte[4];
            // music.seek(ID3V2_frame_size + 10);
            // music.read(FrameHeader, 0, 4);
            // model = getTimeInfo(FrameHeader, ID3V2_frame_size, musicFile);
            // } else {
            // byte[] FrameHeader = new byte[4];
            // music.read(FrameHeader, 0, 4);
            // model = getTimeInfo(FrameHeader, 0, musicFile);
            // }

            music.close();// close file
            // check length
            if (buf.length != 128) {
                throw new ErrorMusicLength(String.format("error music info length, length is:%i",
                        buf.length));
            }

            if (!"TAG".equalsIgnoreCase(new String(buf, 0, 3))) {
                throw new UnknownTagException("unknown tag exception");
            }
            String songName = null;
            // try {
            // songName = new String(buf, 3, 30, "gbk").trim();
            // } catch (UnsupportedEncodingException e) {
            // // TODO: handle exception
            // e.printStackTrace();
            // songName = new String(buf, 3, 30).trim();
            // }
            String singerName = "";
            // try {
            // singerName = new String(buf, 33, 30, "gbk").trim();
            // } catch (UnsupportedEncodingException e) {
            // // TODO: handle exception
            // singerName = new String(buf, 33, 30).trim();
            // }
            String ablum = "";
            // try {
            // ablum = new String(buf, 63, 30, "gbk").trim();
            // } catch (UnsupportedEncodingException e) {
            // // TODO: handle exception
            // ablum = new String(buf, 63, 30).trim();
            // }
            String year = "";
            // try {
            // year = new String(buf, 93, 4, "gbk").trim();
            // } catch (UnsupportedEncodingException e) {
            // year = new String(buf, 93, 4).trim();
            // // TODO: handle exception
            // }

            String reamrk = "";
            // try {
            // reamrk = new String(buf, 97, 28, "gbk").trim();
            // } catch (UnsupportedEncodingException e) {
            // // TODO: handle exception
            // reamrk = new String(buf, 97, 28).trim();
            // }
            // get the time len
            try {
                ContentResolver contentResolver = sContext.getContentResolver();
                Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        null, "_data=?", new String[] {
                            musicFile.getAbsolutePath()
                        }, null);
                cursor.moveToFirst();
                long secs = cursor.getLong(cursor
                        .getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
                model.setTime(secs);
                secs /= 1000;
                model.setHours((int) secs / 3600);
                model.setMinutes(((int) secs % 3600) / 60);
                model.setSeconds(((int) secs % 3600) % 60);
                if (TextUtils.isEmpty(songName)) {
                    songName = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
                    singerName = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
                    ablum = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM));
                    year = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.AudioColumns.YEAR));
                }
                cursor.close();

                //
                if (TextUtils.isEmpty(songName)) {
                    try {
                        songName = new String(buf, 3, 30, "gbk").trim();
                    } catch (UnsupportedEncodingException e) {
                        // TODO: handle exception
                        e.printStackTrace();
                        songName = new String(buf, 3, 30).trim();
                    }
                    try {
                        singerName = new String(buf, 33, 30, "gbk").trim();
                    } catch (UnsupportedEncodingException e) {
                        // TODO: handle exception
                        singerName = new String(buf, 33, 30).trim();
                    }
                    try {
                        ablum = new String(buf, 63, 30, "gbk").trim();
                    } catch (UnsupportedEncodingException e) {
                        // TODO: handle exception
                        ablum = new String(buf, 63, 30).trim();
                    }
                    try {
                        year = new String(buf, 93, 4, "gbk").trim();
                    } catch (UnsupportedEncodingException e) {
                        year = new String(buf, 93, 4).trim();
                        // TODO: handle exception
                    }

                    try {
                        reamrk = new String(buf, 97, 28, "gbk").trim();
                    } catch (UnsupportedEncodingException e) {
                        // TODO: handle exception
                        reamrk = new String(buf, 97, 28).trim();
                    }

                }
                // get the time len
            } catch (Exception e) {
                // TODO: handle exception
                model.setTime(0);
                e.printStackTrace();
            }

            model.setSongName(songName);
            model.setSingerName(singerName);
            model.setAblumName(ablum);
            model.setFile(musicFile.getAbsolutePath());
            model.setRemark("");

            mSongs.add(model);

            return model;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }

    /**
     * CallBack when music is scanning
     * 
     * @author leizhang
     */
    public interface onMusicScanListener {
        /**
         * called when when a new music is added to scanner list
         * 
         * @param musicName
         */
        void onMusicScaned(SongModel song);

        /**
         * called when scan music is finished
         */
        void onMusicScanedFinish();
    };

    /**
     * return int between 0 and n(not contains n)
     * 
     * @param seed
     * @return
     */
    public static int getRandomInt(int seed) {
        return new Random().nextInt(seed);
    }

    // public static SongModel getTimeInfo(byte[] FrameHeader, int
    // ID3V2_frame_size, File musicFile)
    // {
    // double playFramesPerSec = 38.461538461538461538461538461538;
    // SongModel model = new SongModel();
    // model.setFileLength(musicFile.length());
    // getFrameInfo(model, FrameHeader);
    // int frameSize = model.CalcFrameSize();
    //
    // long frameCount = (model.getFileLength() - ID3V2_frame_size - 128) /
    // frameSize;
    //
    // double secs = (double) frameCount / playFramesPerSec;
    // model.setHours((int) secs / 3600);
    // model.setMinutes(((int) secs % 3600) / 60);
    // model.setSeconds(((int) secs % 3600) % 60);
    // return model;
    // }
    //
    // /**
    // * @param model
    // * @param FrameHeader
    // */
    // public static void getFrameInfo(SongModel model, byte[] FrameHeader)
    // {
    // int[][] bitrateArray = new int[][] {
    // {
    // 0, 0, 0, 0, 0, 0
    // },
    // {
    // 32, 32, 32, 32, 32, 8
    // },
    // {
    // 64, 48, 40, 64, 48, 16
    // },
    // {
    // 64, 48, 40, 64, 48, 16
    // },
    // {
    // 96, 56, 48, 96, 56, 24
    // },
    // {
    // 128, 64, 56, 128, 64, 32
    // },
    // {
    // 160, 80, 64, 160, 80, 64
    // },
    // {
    // 192, 96, 80, 192, 96, 80
    // },
    // {
    // 224, 112, 96, 224, 112, 52
    // },
    // {
    // 256, 128, 112, 256, 128, 64
    // },
    // {
    // 288, 160, 128, 288, 160, 128
    // },
    // {
    // 320, 192, 160, 320, 192, 160
    // },
    // {
    // 352, 320, 192, 352, 320, 112
    // },
    // {
    // 384, 256, 224, 384, 256, 128
    // },
    // {
    // 448, 384, 320, 448, 384, 320
    // },
    // {
    // 0, 0, 0, 0, 0, 0
    // }
    // };
    //
    // int[][] simpArray = new int[][] {
    // {
    // 44100, 22050, 11025
    // },
    // {
    // 48000, 24000, 12000
    // },
    // {
    // 32000, 16000, 8000
    // },
    // {
    // 0, 0, 0
    // }
    // };
    // int version = 0;
    // switch ((FrameHeader[1] & 0x18) >> 3)
    // {
    // case 3: // MPEG version 1
    // version = 1;
    // break;
    // case 2: // MPEG version 2
    // version = 2;
    // break;
    // case 0: // MPEG version 2.5
    // version = 3;
    // break;
    // case 1: // Reserve
    // version = 0;
    // break;
    // }
    // model.setVersion(version);
    // int layer = 0;
    // switch ((FrameHeader[1] & 0x6) >> 1)
    // {
    // case 1: // Layer 3
    // layer = 3;
    // break;
    // case 2: // Layer 2
    // layer = 2;
    // break;
    // case 3: // Layer 1
    // layer = 1;
    // break;
    // case 0: // reserve
    // layer = 0;
    // break;
    // }
    // model.setLayer(layer);
    // model.setProtect(FrameHeader[1] & 0x1);
    //
    // int j = ((FrameHeader[2] & 0xf0) >> 4) + 1;
    // int i = 0; public static SongModel getTimeInfo(byte[] FrameHeader, int
    // ID3V2_frame_size, File musicFile)
    // {
    // double playFramesPerSec = 38.461538461538461538461538461538;
    // SongModel model = new SongModel();
    // model.setFileLength(musicFile.length());
    // getFrameInfo(model, FrameHeader);
    // int frameSize = model.CalcFrameSize();
    //
    // long frameCount = (model.getFileLength() - ID3V2_frame_size - 128) /
    // frameSize;
    //
    // double secs = (double) frameCount / playFramesPerSec;
    // model.setHours((int) secs / 3600);
    // model.setMinutes(((int) secs % 3600) / 60);
    // model.setSeconds(((int) secs % 3600) % 60);
    // return model;
    // }
    //
    // /**
    // * @param model
    // * @param FrameHeader
    // */
    // public static void getFrameInfo(SongModel model, byte[] FrameHeader)
    // {
    // int[][] bitrateArray = new int[][] {
    // {
    // 0, 0, 0, 0, 0, 0
    // },
    // {
    // 32, 32, 32, 32, 32, 8
    // },
    // {
    // 64, 48, 40, 64, 48, 16
    // },
    // {
    // 64, 48, 40, 64, 48, 16
    // },
    // {
    // 96, 56, 48, 96, 56, 24
    // },
    // {
    // 128, 64, 56, 128, 64, 32
    // },
    // {
    // 160, 80, 64, 160, 80, 64
    // },
    // {
    // 192, 96, 80, 192, 96, 80
    // },
    // {
    // 224, 112, 96, 224, 112, 52
    // },
    // {
    // 256, 128, 112, 256, 128, 64
    // },
    // {
    // 288, 160, 128, 288, 160, 128
    // },
    // {
    // 320, 192, 160, 320, 192, 160
    // },
    // {
    // 352, 320, 192, 352, 320, 112
    // },
    // {
    // 384, 256, 224, 384, 256, 128
    // },
    // {
    // 448, 384, 320, 448, 384, 320
    // },
    // {
    // 0, 0, 0, 0, 0, 0
    // }
    // };
    //
    // int[][] simpArray = new int[][] {
    // {
    // 44100, 22050, 11025
    // },
    // {
    // 48000, 24000, 12000
    // },
    // {
    // 32000, 16000, 8000
    // },
    // {
    // 0, 0, 0
    // }
    // };
    // int version = 0;
    // switch ((FrameHeader[1] & 0x18) >> 3)
    // {
    // case 3: // MPEG version 1
    // version = 1;
    // break;
    // case 2: // MPEG version 2
    // version = 2;
    // break;
    // case 0: // MPEG version 2.5
    // version = 3;
    // break;
    // case 1: // Reserve
    // version = 0;
    // break;
    // }
    // model.setVersion(version);
    // int layer = 0;
    // switch ((FrameHeader[1] & 0x6) >> 1)
    // {
    // case 1: // Layer 3
    // layer = 3;
    // break;
    // case 2: // Layer 2
    // layer = 2;
    // break;
    // case 3: // Layer 1
    // layer = 1;
    // break;
    // case 0: // reserve
    // layer = 0;
    // break;
    // }
    // model.setLayer(layer);
    // model.setProtect(FrameHeader[1] & 0x1);
    //
    // int j = ((FrameHeader[2] & 0xf0) >> 4) + 1;
    // int i = 0;
    // switch (version)
    // {
    // case 1:
    // switch (layer)
    // {
    // case 1:
    // i = 0;
    // break;
    // case 2:
    // i = 1;
    // break;
    // case 3:
    // i = 2;
    // break;
    // }
    // break;
    // case 2:
    // case 3:
    // switch (layer)
    // {
    // case 1:
    // i = 3;
    // break;
    // case 2:
    // i = 4;
    // break;
    // case 3:
    // i = 5;
    // break;
    // }
    // break;
    // }
    // model.setBitrate(bitrateArray[j][i]);
    //
    // j = ((FrameHeader[2] & 0xc) >> 2);
    // switch (version)
    // {
    // case 1:
    // i = 0;
    // break;
    // case 2:
    // i = 1;
    // break;
    // case 3:
    // i = 2;
    // break;
    // }
    // model.setSimplingRate(simpArray[j][i]);
    //
    // model.setPaddingBits((FrameHeader[2] & 0x2) >> 1);
    //
    // model.setChannel(((FrameHeader[3] & 0xc0) >> 6) < 3 ? 1 : 0);
    //
    // }

    // switch (version)
    // {
    // case 1:
    // switch (layer)
    // {
    // case 1:
    // i = 0;
    // break;
    // case 2:
    // i = 1;
    // break;
    // case 3:
    // i = 2;
    // break;
    // }
    // break;
    // case 2:
    // case 3:
    // switch (layer)
    // {
    // case 1:
    // i = 3;
    // break;
    // case 2:
    // i = 4;
    // break;
    // case 3:
    // i = 5;
    // break;
    // }
    // break;
    // }
    // model.setBitrate(bitrateArray[j][i]);
    //
    // j = ((FrameHeader[2] & 0xc) >> 2);
    // switch (version)
    // {
    // case 1:
    // i = 0;
    // break;
    // case 2:
    // i = 1;
    // break;
    // case 3:
    // i = 2;
    // break;
    // }
    // model.setSimplingRate(simpArray[j][i]);
    //
    // model.setPaddingBits((FrameHeader[2] & 0x2) >> 1);
    //
    // model.setChannel(((FrameHeader[3] & 0xc0) >> 6) < 3 ? 1 : 0);
    //
    // }

    /**
     * format time
     * 
     * @param min minutes
     * @param sec seconds
     * @return
     */
    public static String formatString(int min, int sec) {
        return String.format("%02d:%02d", min, sec);
    }

    /**
     * calculate percent
     * 
     * @param curMin now play minutes
     * @param curSec now play seconds
     * @param totalMin total play minutes
     * @param totalSec total play seconds
     * @return
     */
    public static int getPercent(int curMin, int curSec, int totalMin, int totalSec) {

        return (int) (((curMin * 60 + curSec) / (Float.parseFloat((totalMin * 60 + totalSec) + ""))) * 100);
    }

    /**
     * format a integer value
     * 
     * @return
     */
    public static String formatString1(int intvalue) {
        return String.format("%d 首", intvalue);
    }

    /**
     * check running service
     * 
     * @param context
     * @param serviceName
     * @return
     */
    public static boolean checkServiceIsRunning(Context context, String serviceName) {
        // Log.d(LOG_TAG, "will check service :" + serviceName);
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> mRunningServiceInfos = mActivityManager
                .getRunningServices(1000);
        for (RunningServiceInfo runningServiceInfo : mRunningServiceInfos) {
            if (null == runningServiceInfo) {
                continue;
            }

            if (serviceName.equals(runningServiceInfo.service.getClassName())) {
                // Log.d(LOG_TAG, "service[" + serviceName + "] is running");
                return true;
            }

        }

        return false;
    }

    /**
     * ] is in main thread
     * 
     * @return
     */
    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    /**
     * filed to record previous time stamp
     */
    private static long sPreviosTime = 0;

    /**
     * the slot time
     */
    public static final long SLOTTIME = 2000;

    /**
     * exit the application when back menu was double clicked.
     * 
     * @param timeStamp the latest time stamp
     * @return return true if slop time is satisfied else return false
     */
    public static boolean canExit(long timeStamp) {
        if (sPreviosTime == 0) {
            sPreviosTime = timeStamp;
            return false;
        }
        boolean needExit = timeStamp - sPreviosTime <= SLOTTIME;
        sPreviosTime = timeStamp;
        return needExit;
    }

    public static boolean isEmail(String string) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }

    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern
                .compile(MOBILE_PATTERN);
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 对象转整数.
     * 
     * @param obj the obj
     * @return 转换异常返回 0
     */
    public static int toInt(Object obj) {
        if (obj == null)
            return 0;
        return toInt(obj.toString(), 0);
    }

    /**
     * 字符串转整数.
     * 
     * @param str the str
     * @param defValue the def value
     * @return the int
     */
    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
        }

        return defValue;
    }

    /**
     * get the index of play list
     * 
     * @param list
     * @param key
     * @return
     */
    public static int getIndexOfList(List<SongModel> list, String key) {
        if (null == list || list.isEmpty()) {
            return 0;
        }

        for (int i = 0; i < list.size(); i++) {
            SongModel model = list.get(i);
            if (key.equals(model.getFile())) {
                return i;
            }
        }

        return 0;
    }

    /**
     * 获取一个HttpPost对象
     * 
     * @param url 请求Url
     * @param json 发送数据
     * @return
     * @throws AppException
     */
    public static HttpPost getHttpPost(String url, JSONObject json) {
        HttpPost httpPost = null;

        HttpEntity enity = null;

        try {

            httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type",
                    "application/json; charset=utf-8");
            enity = new StringEntity(json.toString(), "utf-8");
            httpPost.setEntity(enity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        } finally {

            if (null != enity) {
                try {
                    enity.consumeContent();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                enity = null;
            }

        }

        return httpPost;
    }

    /**
     * 发送一个Post请求
     * 
     * @param post
     * @return
     * @throws AppException
     */
    public static byte[] doHttpPost(HttpPost post) throws AppException {
        DefaultHttpClient httpclient = new DefaultHttpClient();

        if (null == post) {
            return null;

        }
        HttpResponse response;
        HttpEntity httpentity = null;
        InputStream ins = null;
        ByteArrayOutputStream baos = null;
        try {
            response = httpclient.execute(post);
            httpentity = response.getEntity();
            ins = httpentity.getContent();

            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;

            while ((len = ins.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }

            baos.flush();
            httpentity.consumeContent();
            ins.close();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof AppException) {
                throw (AppException) e;
            }
        } finally {
            ins = null;
            httpentity = null;
            httpclient = null;

        }

        return baos.toByteArray();
    }
    
    
}
