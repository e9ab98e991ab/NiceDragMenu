package com.e9ab98e991ab.drag;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


import java.util.ArrayList;
import java.util.List;

/**
 * @author gaoxin 2020/9/9 14:42
 * @version V1.0.0
 * @name DragMenuRelativeLayout
 * @mail godfeer@aliyun.com
 * @description  拖动菜单栏实现类
 */
public class DragMenuRelativeLayout extends RelativeLayout {

    private AnimateImageView topImageView;
    private List<AnimateImageView> imageViewList = new ArrayList<AnimateImageView>();
    private int[] imageIds = {R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4, R.drawable.image5, R.drawable.image6};
    private int[] buttonColors = {R.color.float_background1, R.color.float_background2, R.color.float_background3,R.color.float_background4, R.color.float_background5, R.color.float_background6};
    private int marginBottom = 40;
    private int marginRight = 40;
    //默认间距
    private int defaultSpacing = 200;

    /* 拖拽工具类 */
    private final ViewDragHelper mDragHelper;
    private GestureDetectorCompat gestureDetector;
    private ViewDragController viewDragController;

    public DragMenuRelativeLayout(Context context) {
        this(context, null);
    }

    public DragMenuRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragMenuRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        marginBottom = (int) getResources().getDimension(R.dimen.float_marginBottom);
        marginRight = (int) getResources().getDimension(R.dimen.float_marginRight);

        mDragHelper = ViewDragHelper.create(this, 10f, new DragHelperCallback());
        mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_BOTTOM);
        gestureDetector = new GestureDetectorCompat(context,
                new MoveDetector());

        viewDragController = ViewDragController.create();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // 底部的圆形按钮 动态添加到布局中
        final int len = imageIds.length;
        Resources res = getResources();
        for (int i = 0; i < len; i++) {
            // AnimateImageView的父类是FloatingActionButton，在android 5.0以下的手机中，会有重影现象
            // 其实父类不重要，只要能实现那个圆形的效果就好。
            // 美工扎实的同学，可以直接用ImageView去实现圆形按钮
            AnimateImageView imageView = new AnimateImageView(getContext());
            imageView.setImageResource(imageIds[i]);
            imageView.setBackgroundTintList(res.getColorStateList(buttonColors[i]));
            imageView.setTag(i);
            imageViewList.add(imageView);

            // 添加到RelativeLayout中去
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, marginRight, marginBottom);
            lp.addRule(ALIGN_PARENT_BOTTOM);
            lp.addRule(ALIGN_PARENT_RIGHT);
            addView(imageView, lp);

            // 如果不是最顶层的view，可以去除阴影
            if (i == len - 1) {
                topImageView = imageView;
                topImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getTag() !=null && v.getTag().equals(1)){
                            v.setTag(0);
                            topImageView.setImageResource(R.drawable.image1);
                            viewDragController.onReleaseOriginal();
                        }else{
                            v.setTag(1);
                            topImageView.setImageResource(R.drawable.image6);
                            setOriginPosClick();
                        }
                    }
                });
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    imageView.setElevation(0);
                }
                imageViewList.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.onClick(v,imageViewList.indexOf(v));
                    }
                });
            }
        }

        // 初始化viewTrackController
        viewDragController.init(imageViewList);
    }

    class MoveDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx,
                                float dy) {
            return Math.abs(dy) + Math.abs(dx) > 5;
        }

        @Override
        public boolean onDown(MotionEvent e) {

            return super.onDown(e);
        }
    }

    /**
     * 这是拖拽效果的主要逻辑
     */
    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public void onViewPositionChanged(View changedView, int left, int top,
                                          int dx, int dy) {
            viewDragController.onTopViewPosChanged(left, top);
        }

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            // 只跟踪最顶层的view
            if (child == topImageView) {
                topImageView.stopAnimation();
                return true;
            }

            return false;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            // 这个用来控制拖拽过程中松手后，自动滑行的速度
            return 1;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            // 滑动松开后，交给ViewTrackController去处理
            viewDragController.onRelease();
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            // 手指拖到哪是哪
            return top;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            // 手指拖到哪是哪
            return left;
        }
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        viewDragController.setOriginPos(topImageView.getLeft(), topImageView.getTop());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // onClick的时候会有异常，在最初的时候，mDragHelper先释放一下
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mDragHelper.abort();
        }
        return super.dispatchTouchEvent(ev);
    }

    /* touch事件的拦截与处理都交给mDraghelper来处理 */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean yScroll = gestureDetector.onTouchEvent(ev);
        boolean shouldIntercept = mDragHelper.shouldInterceptTouchEvent(ev);
        int action = ev.getActionMasked();

        if (action == MotionEvent.ACTION_DOWN) {
            mDragHelper.processTouchEvent(ev);
        }

        return shouldIntercept && yScroll;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // 统一交给mDragHelper处理，由DragHelperCallback实现拖动效果
        try {
            mDragHelper.processTouchEvent(e); // 该行代码可能会抛异常，正式发布时请将这行代码加上try catch
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return true;
    }
    private OnItemClickListener clickListener;

    public void setOriginPosClick() {
        final int len = imageViewList.size()-1;
        for (int i = 0; i < len; i++) {
            imageViewList.get(i).animRefresh((int) (defaultSpacing * (len - i)));
            Log.e("***", "setOriginPosClick: "+(viewDragController.getResetPosX() / (len - i)) );
        }
    }

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface OnItemClickListener {
        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        void onClick(View v,int position);
    }
}
