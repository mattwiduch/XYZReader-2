package com.example.xyzreader.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Square ImageView.
 */

public class ArticleImageView extends ImageView {
    public ArticleImageView(Context context) {
        super(context);
    }

    public ArticleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ArticleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sixteenNineHeight = MeasureSpec.getSize(widthMeasureSpec);
        int sixteenNineHeightSpec = MeasureSpec.makeMeasureSpec(sixteenNineHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, sixteenNineHeightSpec);
    }
}
