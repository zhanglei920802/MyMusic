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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;

import org.apache.http.HttpException;
import org.apache.http.conn.HttpHostConnectException;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Environment;
import android.os.Looper;
import android.widget.Toast;

/**
 * @author leizhang
 */
public class AppException extends Exception implements UncaughtExceptionHandler {

    /** The Constant TAG. */
    public static final String TAG = "AppException";

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5529262667754665366L;

    /** The Constant TYPE_NETWORK. */
    public final static byte TYPE_NETWORK = 0x01;

    /** The Constant TYPE_SOCKET. */
    public final static byte TYPE_SOCKET = 0x02;

    /** The Constant TYPE_HTTP_CODE. */
    public final static byte TYPE_HTTP_CODE = 0x03;

    /** The Constant TYPE_HTTP_ERROR. */
    public final static byte TYPE_HTTP_ERROR = 0x04;

    /** The Constant TYPE_XML. */
    public final static byte TYPE_XML = 0x05;

    /** The Constant TYPE_IO. */
    public final static byte TYPE_IO = 0x06;

    /** The Constant TYPE_RUN. */
    public final static byte TYPE_RUN = 0x07;

    /**
     * Gets the app exception handler.
     * 
     * @return the app exception handler
     */
    public static AppException getAppExceptionHandler() {
        return new AppException();
    }

    /**
     * Http.
     * 
     * @param e the e
     * @return the app exception
     */
    public static AppException http(Exception e) {
        return new AppException(e, 0, TYPE_HTTP_ERROR);
    }

    /**
     * Http.
     * 
     * @param code the code
     * @return the app exception
     */
    public static AppException http(int code) {
        return new AppException(null, code, TYPE_HTTP_CODE);
    }

    /**
     * Io.
     * 
     * @param e the e
     * @return the app exception
     */
    public static AppException IO(Exception e) {
        if (e instanceof UnknownHostException || e instanceof ConnectException) {
            return new AppException(e, 0, TYPE_NETWORK);
        } else if (e instanceof IOException) {
            return new AppException(e, 0, TYPE_IO);
        }
        return run(e);
    }

    /**
     * Network.
     * 
     * @param e the e
     * @return the app exception
     */
    public static AppException network(Exception e) {
        if (e instanceof UnknownHostException || e instanceof ConnectException
                || e instanceof HttpHostConnectException) {
            return new AppException(e, 0, TYPE_NETWORK);
        } else if (e instanceof HttpException) {
            return http(e);
        } else if (e instanceof SocketException) {
            return socket(e);
        }
        return http(e);
    }

    /**
     * Run.
     * 
     * @param e the e
     * @return the app exception
     */
    public static AppException run(Exception e) {
        return new AppException(e, 0, TYPE_RUN);
    }

    /**
     * Socket.
     * 
     * @param e the e
     * @return the app exception
     */
    public static AppException socket(Exception e) {
        return new AppException(e, 0, TYPE_SOCKET);
    }

    /** The mtype. */
    private byte mtype;

    /** The mcode. */
    private int mcode;

    /** The m default handler. */
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    /**
     * Instantiates a new app exception.
     */
    private AppException() {
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    /**
     * Instantiates a new app exception.
     * 
     * @param exp the exp
     * @param code the code
     * @param type the type
     */
    public AppException(Exception exp, int code, byte type) {
        super(exp);
        this.mcode = code;
        this.mtype = type;

    }

    /**
     * Gets the code.
     * 
     * @return the code
     */
    public int getCode() {
        return this.mcode;
    }

    /**
     * Gets the crash report.
     * 
     * @param context the context
     * @param ex the ex
     * @return the crash report
     */
    private String getCrashReport(Context context, Throwable ex) {
        PackageInfo pinfo = ((AppContext) context.getApplicationContext())
                .getPackageInfo();
        StringBuffer exceptionStr = new StringBuffer();
        exceptionStr.append("Version: " + pinfo.versionName + "("
                + pinfo.versionCode + ")\n");
        exceptionStr.append("Android: " + android.os.Build.VERSION.RELEASE
                + "(" + android.os.Build.MODEL + ")\n");
        exceptionStr.append("Exception: " + ex.getMessage() + "\n");
        StackTraceElement[] elements = ex.getStackTrace();
        for (int i = 0; i < elements.length; i++) {
            exceptionStr.append(elements[i].toString() + "\n");
        }
        return exceptionStr.toString();

    }

    /**
     * Gets the type.
     * 
     * @return the type
     */
    public int getType() {
        return this.mtype;
    }

    /**
     * Handle exception.
     * 
     * @param ex the ex
     * @return true, if successful
     */
    private boolean handleException(final Throwable ex) {

        if (ex == null) {

            return false;
        } else {
            System.out
                    .println("================================AppException.handleException()====BEGIN================");
            ex.printStackTrace();
            System.out
                    .println("================AppException.handleException()========================END==================");

        }

        final Context context = AppManager.getInstance().currentActivity();

        if (context == null) {
            return false;
        }

        final String crashReport = getCrashReport(context, ex);

        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                saveLog(ex);
                UIHelper.sendAppCrashReport(context, crashReport);
                Looper.loop();
            }

        }.start();

        return true;
    }

    /**
     * Make toast.
     * 
     * @param ctx the ctx
     */
    public void makeToast(Context ctx) {
        switch (this.getType()) {
            case TYPE_HTTP_CODE:
                String err = ctx.getString(R.string.http_status_code_error,
                        this.getCode());
                Toast.makeText(ctx, err, Toast.LENGTH_SHORT).show();
                break;
            case TYPE_HTTP_ERROR:
                Toast.makeText(ctx, R.string.http_exception_error,
                        Toast.LENGTH_SHORT).show();
                break;
            case TYPE_SOCKET:
                Toast.makeText(ctx, R.string.socket_exception_error,
                        Toast.LENGTH_SHORT).show();
                break;
            case TYPE_NETWORK:
                Toast.makeText(ctx, R.string.network_not_connected,
                        Toast.LENGTH_SHORT).show();
                break;
            case TYPE_XML:
                Toast.makeText(ctx, R.string.xml_parser_failed, Toast.LENGTH_SHORT)
                        .show();
                break;
            case TYPE_IO:
                Toast.makeText(ctx, R.string.io_exception_error, Toast.LENGTH_SHORT)
                        .show();
                break;
            case TYPE_RUN:
                Toast.makeText(ctx, R.string.app_run_code_error, Toast.LENGTH_SHORT)
                        .show();
                break;
        }
    }

    /**
     * Save log.
     * 
     * @param ex the ex
     */
    private void saveLog(Throwable ex) {
        String errorlog = "errorlog.txt";
        String savePath = "";
        String logFilePath = "";
        FileWriter fw = null;
        PrintWriter pw = null;
        try {

            String storageState = Environment.getExternalStorageState();
            if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                savePath = Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + "/Android/data/cn.cdut.app/Log/";
                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                logFilePath = savePath + errorlog;
            }

            if (logFilePath == "") {
                return;
            }
            File logFile = new File(logFilePath);
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            fw = new FileWriter(logFile, true);
            pw = new PrintWriter(fw);
            pw.println("--------------------" + (new Date().toLocaleString())
                    + "---------------------");
            ex.printStackTrace(pw);
            pw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                }
            }
        }

    }

    /**
     * <p>
     * Title: uncaughtException
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param thread
     * @param ex
     * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread,
     *      java.lang.Throwable)
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ex.printStackTrace();
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        }

    }
}
