
package com.anp.bdmt;

/**
 * @author anp
 * Created by Jung-Hum Cho on 2016. 7. 14..
 */

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Special thanks to Daniel López Lacalle for his response
 * (http://stackoverflow.
 * com/questions/8394681/android-i-am-unable-to-have-viewpager
 * -wrap-content/20784791#20784791)
 */
public class WrapContentViewPager extends ViewPager {

    private int mCurrentPagePosition = 0;

    public WrapContentViewPager(Context context) {
        super(context);
    }

    public WrapContentViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // @Override
    // protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    // try {
    // View child = getChildAt(mCurrentPagePosition);
    // if (child != null) {
    // child.measure(widthMeasureSpec,
    // MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
    // int h = child.getMeasuredHeight();
    // heightMeasureSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY);
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    // }
    //
    // public void reMeasureCurrentPage(int position) {
    // mCurrentPagePosition = position;
    // requestLayout();
    // }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int height = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec,
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if (h > height)
                height = h;
        }

        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
