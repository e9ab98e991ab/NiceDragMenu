package com.e9ab98e991ab.drag;

import java.util.List;

/**
 * @author gaoxin 2020/9/9 14:42
 * @version V1.0.0
 * @name ViewDragController
 * @mail godfeer@aliyun.com
 * @description  View 控制器
 */
public class ViewDragController {
    private List<AnimateImageView> imageViewList;
    private AnimateImageView topView;
    private AnimateImageView topFollowerView;
    private int resetPosX, resetPosY;

    private ViewDragController() {
    }

    public static ViewDragController create() {
        return new ViewDragController();
    }

    public void init(List<AnimateImageView> imageViewList) {
        this.imageViewList = imageViewList;

        int len = imageViewList.size();
        this.topView = imageViewList.get(len - 1);
        this.topFollowerView = imageViewList.get(len - 2);

        for (int i = 1; i < len; i++) {
            AnimateImageView view1 = imageViewList.get(i - 1);
            AnimateImageView view2 = imageViewList.get(i);
            view2.getSpringX().addListener(view1.getFollowerListenerX());
            view2.getSpringY().addListener(view1.getFollowerListenerY());
        }
    }

    /**
     * 拖动view的位置改变，后面的view会自动跟着变
     */
    public void onTopViewPosChanged(int xPos, int yPos) {
        // 第一个跟随者移动了，后面的跟随者会自动移动
        topFollowerView.animTo(xPos, yPos);
    }

    /**
     * 手指松开的时候调用
     */
    public void onRelease() {
        topView.onRelease(resetPosX, resetPosY);
    }

    /***
     * 设置到原始位置
     */
    public void onReleaseOriginal() {
        topView.onRelease(resetPosX, resetPosY);
        for (int i = 0; i < imageViewList.size(); i++) {
            imageViewList.get(i).animRefreshStop(0);
        }
    }

    public int getResetPosY() {
        return resetPosY;
    }

    public int getResetPosX() {
        return resetPosX;
    }

    /**
     * 设置view最初的原始位置
     */
    public void setOriginPos(int xPos, int yPos) {
        resetPosX = xPos;
        resetPosY = yPos;

        int len = imageViewList.size();
        for (int i = 0; i < len; i++) {
            imageViewList.get(i).setCurrentSpringPos(xPos, yPos);
        }
    }
}
