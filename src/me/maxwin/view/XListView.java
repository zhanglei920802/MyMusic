/**   
 * @Title: XListView.java 
 * @Package me.maxwin.view 
 * @Description: 
 *			声明:
 *			1)掌控校园是本人的大学的创业作品,也是本人的毕业设计
 *			2)本程序尚未进行开放源代码,所以禁止组织或者是个人泄露源码,否则将会追究其刑事责任
 *			3)编写本软件,我需要感谢彭老师以及其他鼓励和支持我的同学以及朋友
 *			4)本程序的最终所有权属于本人	
 * @author  张雷 794857063@qq.com
 * @date 2013-11-14 19:34:44 
 * @version V1.0   
 */

package me.maxwin.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.tcl.lzhang1.mymusic.R;

// TODO: Auto-generated Javadoc
/**
 * The Class XListView.
 */
public class XListView extends ListView implements OnScrollListener {

    /**
     * implements this interface to get refresh/load more event.
     * 
     * @see IXListViewEvent
     */
    public interface IXListViewListener {

        /**
         * On load more.
         */
        public void onLoadMore();

        /**
         * On refresh.
         */
        public void onRefresh();
    }

    /**
     * you can listen ListView.OnScrollListener or this one. it will invoke
     * onXScrolling when header/footer scroll back.
     * 
     * @see OnXScrollEvent
     */
    public interface OnXScrollListener extends OnScrollListener {

        /**
         * On x scrolling.
         * 
         * @param view the view
         */
        public void onXScrolling(View view);
    }

    /** The Constant LOAD_ALL. */
    public static final int LOAD_ALL = 1;

    /** The m last y. */
    private float mLastY = -1; // save event y

    /** The m scroller. */
    private Scroller mScroller; // used for scroll back

    /** The m scroll listener. */
    private OnScrollListener mScrollListener; // user's scroll listener
    // the interface to trigger refresh and load more.
    /** The m list view listener. */
    private IXListViewListener mListViewListener;
    // -- header view
    /** The m header view. */
    private XListViewHeader mHeaderView;
    // header view content, use it to calculate the Header's height. And hide it
    // when disable pull refresh.
    /** The m header view content. */
    private RelativeLayout mHeaderViewContent;

    /** The m header time view. */
    private TextView mHeaderTimeView;

    /** The m header view height. */
    private int mHeaderViewHeight; // header view's height

    /** The m enable pull refresh. */
    private boolean mEnablePullRefresh = true;

    /** The m pull refreshing. */
    private boolean mPullRefreshing = false; // is refreashing.
    // -- footer view
    /** The m footer view. */
    private XListViewFooter mFooterView;

    /** The m enable pull load. */
    private boolean mEnablePullLoad;

    /** The m pull loading. */
    private boolean mPullLoading;

    /** The m is footer ready. */
    private boolean mIsFooterReady = false;
    // total list items, used to detect is at the bottom of listview.
    /** The m total item count. */
    private int mTotalItemCount;
    // for mScroller, scroll back from header or footer.
    /** The m scroll back. */
    private int mScrollBack;

    /** The Constant SCROLLBACK_HEADER. */
    private final static int SCROLLBACK_HEADER = 0;

    /** The Constant SCROLLBACK_FOOTER. */
    private final static int SCROLLBACK_FOOTER = 1;

    /** The Constant SCROLL_DURATION. */
    private final static int SCROLL_DURATION = 400; // scroll back duration

    /** The Constant PULL_LOAD_MORE_DELTA. */
    private final static int PULL_LOAD_MORE_DELTA = 50; // when pull up >= 50px

    // at bottom, trigger
    // load more.
    /** The Constant OFFSET_RADIO. */
    private final static float OFFSET_RADIO = 1.8f; // support iOS like pull
                                                    // feature.

    /**
     * Instantiates a new x list view.
     * 
     * @param context the context
     */
    public XListView(Context context) {
        super(context);
        initWithContext(context);
    }

    /**
     * Instantiates a new x list view.
     * 
     * @param context the context
     * @param attrs the attrs
     */
    public XListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWithContext(context);
    }

    /**
     * Instantiates a new x list view.
     * 
     * @param context the context
     * @param attrs the attrs
     * @param defStyle the def style
     */
    public XListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initWithContext(context);
    }

    /**
     * <p>
     * Title: computeScroll
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @see android.view.View#computeScroll()
     */
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScrollBack == SCROLLBACK_HEADER) {
                mHeaderView.setVisiableHeight(mScroller.getCurrY());
            } else {
                mFooterView.setBottomMargin(mScroller.getCurrY());
            }
            postInvalidate();
            invokeOnScrolling();
        }
        super.computeScroll();
    }

    /**
     * Inits the with context.
     * 
     * @param context the context
     */
    private void initWithContext(Context context) {
        mScroller = new Scroller(context, new DecelerateInterpolator());
        // XListView need the scroll event, and it will dispatch the event to
        // user's listener (as a proxy).
        super.setOnScrollListener(this);

        // init header view
        mHeaderView = new XListViewHeader(context);
        mHeaderViewContent = (RelativeLayout) mHeaderView
                .findViewById(R.id.xlistview_header_content);
        mHeaderTimeView = (TextView) mHeaderView
                .findViewById(R.id.xlistview_header_time);
        addHeaderView(mHeaderView);

        // init footer view
        mFooterView = new XListViewFooter(context);

        // init header height
        mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mHeaderViewHeight = mHeaderViewContent.getHeight();
                        getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                    }
                });
    }

    /**
     * Invoke on scrolling.
     */
    private void invokeOnScrolling() {
        if (mScrollListener instanceof OnXScrollListener) {
            OnXScrollListener l = (OnXScrollListener) mScrollListener;
            l.onXScrolling(this);
        }
    }

    // 已经加载全部
    /**
     * Load all.
     */
    public void LoadAll() {
        mFooterView.loadAll();
        AlphaAnimation anim = new AlphaAnimation(1.0f, 0f);
        anim.setDuration(2000);
        anim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                mFooterView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }
        });

        mFooterView.startAnimation(anim);
        mFooterView.setTag(LOAD_ALL);

    }

    /**
     * <p>
     * Title: onScroll
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param view
     * @param firstVisibleItem
     * @param visibleItemCount
     * @param totalItemCount
     * @see android.widget.AbsListView.OnScrollListener#onScroll(android.widget.AbsListView,
     *      int, int, int)
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        // send to user's listener
        mTotalItemCount = totalItemCount;
        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
                    totalItemCount);
        }
    }

    /**
     * <p>
     * Title: onScrollStateChanged
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param view
     * @param scrollState
     * @see android.widget.AbsListView.OnScrollListener#onScrollStateChanged(android.widget.AbsListView,
     *      int)
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    /**
     * <p>
     * Title: onTouchEvent
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param ev
     * @return
     * @see android.widget.AbsListView#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();// 获取从屏幕顶端到MotionEvent点的y轴距离
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:// 按下
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;// y轴拖拽的距离
                mLastY = ev.getRawY();// 更新mlastY
                if (getFirstVisiblePosition() == 0
                        && (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {
                    // the first item is showing, header has shown or pull down.
                    updateHeaderHeight(deltaY / OFFSET_RADIO);// 更新Header高度
                    invokeOnScrolling();// 触发滚动
                } else if (getLastVisiblePosition() == mTotalItemCount - 1
                        && (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
                    // last item, already pulled up or want to pull up.
                    updateFooterHeight(-deltaY / OFFSET_RADIO);// 更新Footer
                }
                break;
            default:
                mLastY = -1; // reset
                if (getFirstVisiblePosition() == 0) {
                    // invoke refresh
                    if (mEnablePullRefresh
                            && mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                        mPullRefreshing = true;
                        mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
                        if (mListViewListener != null) {
                            mListViewListener.onRefresh();
                        }
                    }
                    resetHeaderHeight();
                } else if (getLastVisiblePosition() == mTotalItemCount - 1) {
                    // invoke load more.
                    if (mEnablePullLoad
                            && mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
                        startLoadMore();
                    }
                    resetFooterHeight();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * Reset footer height.
     */
    private void resetFooterHeight() {
        int bottomMargin = mFooterView.getBottomMargin();
        if (bottomMargin > 0) {
            mScrollBack = SCROLLBACK_FOOTER;
            mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,
                    SCROLL_DURATION);
            invalidate();
        }
    }

    /**
     * reset header view's height.
     */
    private void resetHeaderHeight() {
        int height = mHeaderView.getVisiableHeight();
        if (height == 0) // not visible.
            return;
        // refreshing and header isn't shown fully. do nothing.
        if (mPullRefreshing && height <= mHeaderViewHeight) {
            return;
        }
        int finalHeight = 0; // default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
        if (mPullRefreshing && height > mHeaderViewHeight) {
            finalHeight = mHeaderViewHeight;
        }
        mScrollBack = SCROLLBACK_HEADER;
        mScroller.startScroll(0, height, 0, finalHeight - height,
                SCROLL_DURATION);
        // trigger computeScroll
        invalidate();
    }

    /**
     * <p>
     * Title: setAdapter
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param adapter
     * @see android.widget.ListView#setAdapter(android.widget.ListAdapter)
     */
    @Override
    public void setAdapter(ListAdapter adapter) {
        // make sure XListViewFooter is the last footer view, and only add once.
        if (mIsFooterReady == false) {
            mIsFooterReady = true;
            addFooterView(mFooterView);
        }
        super.setAdapter(adapter);
    }

    /**
     * <p>
     * Title: setOnScrollListener
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param l
     * @see android.widget.AbsListView#setOnScrollListener(android.widget.AbsListView.OnScrollListener)
     */
    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mScrollListener = l;
    }

    /**
     * enable or disable pull up load more feature.
     * 
     * @param enable the new pull load enable
     */
    public void setPullLoadEnable(boolean enable) {
        mEnablePullLoad = enable;
        if (!mEnablePullLoad) {
            mFooterView.hide();
            mFooterView.setOnClickListener(null);
        } else {
            mPullLoading = false;
            mFooterView.show();
            mFooterView.setState(XListViewFooter.STATE_NORMAL);
            // both "pull up" and "click" will invoke load more.
            mFooterView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startLoadMore();
                }
            });
        }
    }

    /**
     * enable or disable pull down refresh feature.
     * 
     * @param enable the new pull refresh enable
     */
    public void setPullRefreshEnable(boolean enable) {
        mEnablePullRefresh = enable;
        if (!mEnablePullRefresh) { // disable, hide the content
            mHeaderViewContent.setVisibility(View.INVISIBLE);
        } else {
            mHeaderViewContent.setVisibility(View.VISIBLE);
        }
    }

    /**
     * set last refresh time.
     * 
     * @param time the new refresh time
     */
    public void setRefreshTime(String time) {
        mHeaderTimeView.setText(time);
    }

    /**
     * Sets the x list view listener.
     * 
     * @param l the new x list view listener
     */
    public void setXListViewListener(IXListViewListener l) {
        mListViewListener = l;
    }

    /**
     * Start load more.
     */
    private void startLoadMore() {
        mPullLoading = true;
        mFooterView.setState(XListViewFooter.STATE_LOADING);
        if (mListViewListener != null) {
            mListViewListener.onLoadMore();
        }
    }

    /**
     * stop load more, reset footer view.
     */
    public void stopLoadMore() {
        if (mPullLoading == true) {
            mPullLoading = false;
            mFooterView.setState(XListViewFooter.STATE_NORMAL);
        }
    }

    /**
     * stop refresh, reset header view.
     */
    public void stopRefresh() {
        if (mPullRefreshing == true) {
            mPullRefreshing = false;
            resetHeaderHeight();
        }
    }

    /**
     * Update footer height.
     * 
     * @param delta the delta
     */
    private void updateFooterHeight(float delta) {
        int height = mFooterView.getBottomMargin() + (int) delta;
        if (mEnablePullLoad && !mPullLoading) {
            if (height > PULL_LOAD_MORE_DELTA) { // height enough to invoke load
                                                 // more.
                mFooterView.setState(XListViewFooter.STATE_READY);
            } else {
                mFooterView.setState(XListViewFooter.STATE_NORMAL);
            }
        }
        mFooterView.setBottomMargin(height);

        // setSelection(mTotalItemCount - 1); // scroll to bottom
    }

    /**
     * Update header height.
     * 
     * @param delta the delta
     */
    private void updateHeaderHeight(float delta) {// 开始滚动
        mHeaderView.setVisiableHeight((int) delta
                + mHeaderView.getVisiableHeight());// 设置可见高度
        if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
            if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                mHeaderView.setState(XListViewHeader.STATE_READY);
            } else {
                mHeaderView.setState(XListViewHeader.STATE_NORMAL);
            }
        }
        setSelection(0); // scroll to top each time
    }
}
