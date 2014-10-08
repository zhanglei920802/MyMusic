/**   
 * @Title: XListViewFooter.java 
 * @Package me.maxwin.view 
 * @Description: 
 *			声明:
 *			1)掌控校园是本人的大学的创业作品,也是本人的毕业设计
 *			2)本程序尚未进行开放源代码,所以禁止组织或者是个人泄露源码,否则将会追究其刑事责任
 *			3)编写本软件,我需要感谢彭老师以及其他鼓励和支持我的同学以及朋友
 *			4)本程序的最终所有权属于本人	
 * @author  张雷 794857063@qq.com
 * @date 2013-11-14 19:34:10 
 * @version V1.0   
 */

package me.maxwin.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tcl.lzhang1.mymusic.R;

// TODO: Auto-generated Javadoc
/**
 * The Class XListViewFooter.
 */
public class XListViewFooter extends LinearLayout {

    /** The Constant STATE_NORMAL. */
    public final static int STATE_NORMAL = 0;// 正常

    /** The Constant STATE_READY. */
    public final static int STATE_READY = 1;// 开始

    /** The Constant STATE_LOADING. */
    public final static int STATE_LOADING = 2;// 加载中

    /** The m context. */
    private Context mContext;

    /** The m content view. */
    private View mContentView;

    /** The m progress bar. */
    private View mProgressBar;

    /** The m hint view. */
    private TextView mHintView;

    /**
     * Instantiates a new x list view footer.
     * 
     * @param context the context
     */
    public XListViewFooter(Context context) {
        super(context);
        initView(context);
    }

    /**
     * Instantiates a new x list view footer.
     * 
     * @param context the context
     * @param attrs the attrs
     */
    public XListViewFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    /**
     * Gets the bottom margin.
     * 
     * @return the bottom margin
     */
    public int getBottomMargin() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView
                .getLayoutParams();
        return lp.bottomMargin;
    }

    /**
     * hide footer when disable pull load more.
     */
    public void hide() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView
                .getLayoutParams();
        lp.height = 0;
        mContentView.setLayoutParams(lp);
    }

    /**
     * Inits the view.
     * 
     * @param context the context
     */
    private void initView(Context context) {
        mContext = context;
        LinearLayout moreView = (LinearLayout) LayoutInflater.from(mContext)
                .inflate(R.layout.xlistview_footer, null);
        addView(moreView);
        moreView.setLayoutParams(new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

        mContentView = moreView.findViewById(R.id.xlistview_footer_content);
        mProgressBar = moreView.findViewById(R.id.xlistview_footer_progressbar);
        mHintView = (TextView) moreView
                .findViewById(R.id.xlistview_footer_hint_textview);
    }

    // 已经加载全部数据
    /**
     * Load all.
     */
    public void loadAll() {
        mHintView.setVisibility(View.VISIBLE);
        mHintView.setText("已加载全部");

    }

    /**
     * loading status.
     */
    public void loading() {
        mHintView.setVisibility(View.VISIBLE);
        mHintView.setText("正在加载中");
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * normal status.
     */
    public void normal() {
        mHintView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * Sets the bottom margin.
     * 
     * @param height the new bottom margin
     */
    public void setBottomMargin(int height) {
        if (height < 0)
            return;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView
                .getLayoutParams();
        lp.bottomMargin = height;
        mContentView.setLayoutParams(lp);
    }

    /**
     * Sets the state.
     * 
     * @param state the new state
     */
    public void setState(int state) {
        mHintView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mHintView.setVisibility(View.INVISIBLE);
        if (state == STATE_READY) {// //拖拽中
            mHintView.setVisibility(View.VISIBLE);
            mHintView.setText(R.string.xlistview_footer_hint_ready);
        } else if (state == STATE_LOADING) {// 家中中
            mProgressBar.setVisibility(View.VISIBLE);
            mHintView.setVisibility(VISIBLE);
            mHintView.setText(R.string.xlistview_header_hint_loading);
        } else {
            mHintView.setVisibility(View.VISIBLE);
            mHintView.setText(R.string.xlistview_footer_hint_normal);
        }
    }

    /**
     * show footer.
     */
    public void show() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView
                .getLayoutParams();
        lp.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
        mContentView.setLayoutParams(lp);
    }

}
