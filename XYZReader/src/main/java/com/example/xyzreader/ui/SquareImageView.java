package com.example.xyzreader.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Square ImageView.
 */

public class SquareImageView extends ImageView {
    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sixteenNineHeight = MeasureSpec.getSize(widthMeasureSpec);
        int sixteenNineHeightSpec = MeasureSpec.makeMeasureSpec(sixteenNineHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, sixteenNineHeightSpec);
    }
}
