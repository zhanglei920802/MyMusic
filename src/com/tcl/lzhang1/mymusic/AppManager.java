/**   
* @Title: AppManager.java 
* @Package cn.cdut.app 
* @Description: 
*			声明:
*			1)掌控校园是本人的大学的创业作品,也是本人的毕业设计
*			2)本程序尚未进行开放源代码,所以禁止组织或者是个人泄露源码,否则将会追究其刑事责任
*			3)编写本软件,我需要感谢彭老师以及其他鼓励和支持我的同学以及朋友
*			4)本程序的最终所有权属于本人	
* @author  张雷 794857063@qq.com
* @date 2013-11-14 19:45:00 
* @version V1.0   
*/
package com.tcl.lzhang1.mymusic;

import java.util.Stack;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

// TODO: Auto-generated Javadoc
/**
 * The Class AppManager.
 */
public class AppManager {
	
	/** The activity stack. */
	private static Stack<Activity> activityStack = null;

	/** The m app manger. */
	public static AppManager mAppManger = null;

	/**
	 * Gets the activity stack.
	 * 
	 * @return the activity stack
	 */
	public static Stack<Activity> getActivityStack() {
		return activityStack;
	}

	/**
	 * Gets the single instance of AppManager.
	 * 
	 * @return single instance of AppManager
	 */
	public static AppManager getInstance() {
		if (null == mAppManger) {
			mAppManger = new AppManager();
		}
		return mAppManger;
	}

	/**
	 * Adds the activity.
	 * 
	 * @param activity
	 *            the activity
	 */
	public void addActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}

	/**
	 * App exit.
	 * 
	 * @param context
	 *            the context
	 */
	public void AppExit(Context context) {
		try {
			finishAllActivity();
			ActivityManager activityMgr = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			activityMgr.restartPackage(context.getPackageName());

			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Current activity.
	 * 
	 * @return the activity
	 */
	public Activity currentActivity() {
		Activity activity = activityStack.lastElement();
		return activity;
	}

	/**
	 * Finish activity.
	 */
	public void finishActivity() {
		Activity activity = activityStack.lastElement();
		finishActivity(activity);
	}

	/**
	 * Finish activity.
	 * 
	 * @param activity
	 *            the activity
	 */
	public void finishActivity(Activity activity) {

		if (activity != null) {
			activityStack.remove(activity);
			activity.finish();

		}

	}

	/**
	 * Finish all activity.
	 */
	public void finishAllActivity() {
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (null != activityStack.get(i)) {
				activityStack.get(i).finish();
			}
		}
		activityStack.clear();

	}

}
