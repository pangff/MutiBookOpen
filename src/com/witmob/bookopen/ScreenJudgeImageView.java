package com.witmob.bookopen;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class ScreenJudgeImageView extends ImageView{

	float ratio = 1;
	public ScreenJudgeImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		ratio = BookUtils.VIEW_W_H ;
		Log.e("ScreenJudgeImageView","ratio:"+ratio);
	}
	
	/**
	 * 因为在书架显示，所以按图书宽度为标准
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		float height = MeasureSpec.getSize(heightMeasureSpec);
		float width = MeasureSpec.getSize(widthMeasureSpec);
		
		if(width/height>ratio){//如果书的宽高比大于屏幕宽高比，说明书是矮胖的，那么我们缩小书的宽度来适应屏幕比例
			int newWidthMeasuerSpace = MeasureSpec.makeMeasureSpec((int) (height*ratio), MeasureSpec.EXACTLY);
			measure(newWidthMeasuerSpace, heightMeasureSpec);
		}else if(width/height<ratio){//否则就是瘦高型,缩小书的高度
			int newHeightMeasuerSpace = MeasureSpec.makeMeasureSpec((int) (width/ratio), MeasureSpec.EXACTLY);
			measure(widthMeasureSpec, newHeightMeasuerSpace);
		}
	}

}
