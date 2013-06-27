package com.witmob.bookopen;

import java.util.ArrayList;
import java.util.List;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.GridView;

@SuppressLint("NewApi")
public class MainActivity extends GlobalActivity {
	private PerspectiveView perspectiveView;
	private FrameLayout container;
	GridView bookGrid;
	BookShelfAdapter adapter;
	private View currentBookView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// progressDialog = CustomProgressDialog.createDialog(this);

		container = (FrameLayout) this.findViewById(R.id.containers);

		bookGrid = (GridView) this.findViewById(R.id.bookGrid);
		adapter = new BookShelfAdapter(this);
		bookGrid.setAdapter(adapter);

		perspectiveView = new PerspectiveView(this);
		container.addView(perspectiveView);

	}


	public void addPerspectiveView(final View view, float x, float y,
			int width, int height, int coverId, int innerId) {
		Bitmap cover = BitmapFactory.decodeResource(this.getResources(), coverId);
		Bitmap innerCover = BitmapFactory.decodeResource(this.getResources(), innerId);
		currentBookView = view;
		if(cover==null){
			cover = BitmapFactory.decodeResource(this.getResources(), R.drawable.arcturus);
		}
		if(innerCover==null){
			innerCover = BitmapFactory.decodeResource(this.getResources(), R.drawable.arcturus);
		}
		perspectiveView.setTextures(cover,innerCover,
				x + BookUtils.dip2px(this, 10f),
				x + BookUtils.dip2px(this, 10f) + width, y, y + height);
		
		final ObjectAnimator animator = ObjectAnimator.ofFloat(container,"alpha",0f,0.7f);
		animator.setDuration(800);
		animator.setInterpolator(new AccelerateDecelerateInterpolator());
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				perspectiveView.startAnimation();
				animator.start();
			}
		}, 100);

	
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				view.setVisibility(View.INVISIBLE);
			}
		}, 500);
		 
	}

	public void close() {
		perspectiveView.setVisibility(View.GONE);
	}


	@Override
	protected void onPause() {
		 if (perspectiveView != null) {
		 perspectiveView.onPause();
		 }
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
