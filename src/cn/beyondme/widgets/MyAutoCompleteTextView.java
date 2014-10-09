/**   
 * @Title: ClearableEditText.java 
 * @Package cn.beyondme.widgets 
 * @Description: 
 *			声明:
 *			1)掌控校园是本人的大学的创业作品,也是本人的毕业设计
 *			2)本程序尚未进行开放源代码,所以禁止组织或者是个人泄露源码,否则将会追究其刑事责任
 *			3)编写本软件,我需要感谢彭老师以及其他鼓励和支持我的同学以及朋友
 *			4)本程序的最终所有权属于本人	
 * @author  张雷 794857063@qq.com
 * @date 2013-11-14 19:34:23 
 * @version V1.0   
 */

package cn.beyondme.widgets;

import com.tcl.lzhang1.mymusic.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.view.View.OnFocusChangeListener;

// TODO: Auto-generated Javadoc
/**
 * 带有删除按钮的文本框 </p> 如果您想要在输入内容被清空的时候做其他的事情,那么你必须通过
 * {@link #setClearListener(Listener)} 传递一个监听器回调{@link Listener},
 * <i><b>这在输入用户名或者密码时候尤为重要</b></i>.
 * 
 * @version 1.0
 * @author 张雷
 */
public class MyAutoCompleteTextView extends AutoCompleteTextView implements OnTouchListener,
        OnFocusChangeListener, TextWatcher {

    /** Log. @hide */
    private String TAG = "MyEditText";

    /** 清楚图片. */
    private Drawable mDrawableRight = null;

    /** The m touch listener. */
    private OnTouchListener mTouchListener = null;

    /** The m foucus change listener. */
    private OnFocusChangeListener mFoucusChangeListener = null;

    /** The m text watcher. */
    private TextWatcher mTextWatcher = null;

    /** The m listener. */
    private OnCliearListener mListener = null;

    /**
     * 当清楚按钮被按得时候,回调该接口.
     * 
     * @author 张雷
     */
    public interface OnCliearListener {

        /**
         * On clear text.
         */
        public void onClearText();
    }

    /**
     * 设置清楚监听器.
     * 
     * @param listener the new clear listener
     */
    public void setClearListener(OnCliearListener listener) {
        this.mListener = listener;
    }

    /**
     * 设置触摸监听器.
     * 
     * @param touchListener the new touch listener
     */
    public void setTouchListener(OnTouchListener touchListener) {
        this.mTouchListener = touchListener;
    }

    /**
     * 设置焦点监听器.
     * 
     * @param fouChangeListener the new focus change listener
     */
    public void setFocusChangeListener(OnFocusChangeListener fouChangeListener) {
        this.setmFoucusChangeListener(fouChangeListener);
    }

    /**
     * 输入监听器.
     * 
     * @param textWatcher the new text watcher
     */
    public void setTextWatcher(TextWatcher textWatcher) {
        this.setmTextWatcher(textWatcher);
    }

    /**
     * Instantiates a new clearable edit_text_uinedit text.
     * 
     * @param context the context
     * @param attrs the attrs
     * @param defStyle the def style
     */
    public MyAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        // TODO Auto-generated constructor stub
    }

    /**
     * Instantiates a new clearable edit text.
     * 
     * @param context the context
     * @param attrs the attrs
     */
    public MyAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        // TODO Auto-generated constructor stub
    }

    /**
     * Instantiates a new clearable edit text.
     * 
     * @param context the context
     */
    public MyAutoCompleteTextView(Context context) {
        super(context);
        init();
        // TODO Auto-generated constructor stub
    }

    /**
     * Init the Drawable Right icon.
     */
    private void init() {
        mDrawableRight = getCompoundDrawables()[2];
        if (null == mDrawableRight) {
            mDrawableRight = getResources().getDrawable(R.drawable.voice_search_clear_text_xml);

        }

        mDrawableRight.setBounds(0, 0, mDrawableRight.getIntrinsicWidth(),
                mDrawableRight.getIntrinsicHeight());
        setClearIconVisibility(false);

        super.setOnTouchListener(this);
        super.setOnFocusChangeListener(this);

    }

    /**
     * <p>
     * edit_text_uin Title: onTouch
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param v
     * @param event
     * @return
     * @see android.view.View.OnTouchListener#onTouch(android.view.View,
     *      android.view.MotionEvent)
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        if (getCompoundDrawables()[2] != null) {

            // 判定是否被点击
            boolean taped = event.getX() > (getWidth() - getPaddingRight() - getCompoundDrawables()[2]
                    .getIntrinsicWidth());

            if (taped) {
//                Log.d(TAG, "Drawable Right Clicked");
                // /清除输入并回调
                setText("");
                if (null != mListener) {
                    mListener.onClearText();
                }

                return true;
            }

        }

        // 如果设置了触摸监听,那么将事件往上层抛出
        if (mTouchListener != null) {
            return mTouchListener.onTouch(v, event);
        }
        return false;
    }

    /**
     * 当获取或者是设置焦点的时候将会被触发.
     * 
     * @param v the v
     * @param hasFocus the has focus
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        // TODO Auto-generated method stub
        if (hasFocus) {
            setClearIconVisibility(!TextUtils.isEmpty(getText().toString()));
        } else {
            setClearIconVisibility(false);
        }
    }

    /**
     * set hide or show right icon.
     * 
     * @param visibility the new clear icon visibility
     */
    protected void setClearIconVisibility(boolean visibility) {
//        Log.d(TAG, "visibility:" + visibility);
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], visibility ? mDrawableRight : null,
                getCompoundDrawables()[3]);
    }

    /**
     * <p>
     * Title: beforeTextChanged
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param s
     * @param start
     * @param count
     * @param after
     * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence,
     *      int, int, int)
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {
        // TODO Auto-generated method stub

    }

    /**
     * <p>
     * Title: afterTextChanged
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param s
     * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
     */
    @Override
    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub

    }

    /**
     * <p>
     * Title: onTextChanged
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param s
     * @param start
     * @param before
     * @param count
     * @see android.widget.TextView#onTextChanged(java.lang.CharSequence, int,
     *      int, int)
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (isFocused()) {
            setClearIconVisibility(!TextUtils.isEmpty(s));
        }
    }

    /**
     * Gets the m foucus change listener.
     * 
     * @return the m foucus change listener
     */
    public OnFocusChangeListener getmFoucusChangeListener() {
        return mFoucusChangeListener;
    }

    /**
     * Sets the m foucus change listener.
     * 
     * @param mFoucusChangeListener the new m foucus change listener
     */
    public void setmFoucusChangeListener(
            OnFocusChangeListener mFoucusChangeListener) {
        this.mFoucusChangeListener = mFoucusChangeListener;
    }

    /**
     * Gets the m text watcher.
     * 
     * @return the m text watcher
     */
    public TextWatcher getmTextWatcher() {
        return mTextWatcher;
    }

    /**
     * Sets the m text watcher.
     * 
     * @param mTextWatcher the new m text watcher
     */
    public void setmTextWatcher(TextWatcher mTextWatcher) {
        this.mTextWatcher = mTextWatcher;
    }
}
