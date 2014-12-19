package com.witmob.bookopen;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class MainContainer extends FrameLayout{
	

	public MainContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		BookUtils.VIEW_W_H = ((float)(right-left))/ ((float)(bottom-top));
	}

}
