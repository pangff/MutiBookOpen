package com.witmob.bookopen;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-5-6
 * Time: 下午3:36
 * To change this template use File | Settings | File Templates.
 */
@SuppressLint("NewApi")
public class PerspectiveView extends GLSurfaceView implements GLSurfaceView.Renderer {

    //投影矩阵
    private float[] projectionMatrix = new float[16];

    //视图矩阵
    private float[] viewMatrix = new float[16];

    //模型矩阵
    private float[] modelMatrix = new float[16];

    //模型视图投影矩阵
    private float[] mvpMatrix = new float[16];

    private TextureMesh coverMesh, contentMesh, spineMesh;

    private float distance = 10.5f;

    private float width, height, ratio, factor;

    private long duration = 2800;

    private Bitmap coverTexture, spineTexture, contentTexture;
    
    public ManimatorListener animatorListener;
    
    float left = -ratio;
    float right = ratio;
    float top = 1;
    float bottom = -1;
    
    
    float origin_left = -ratio;
    float origin_right = ratio;
    float origin_top = 1;
    float origin_bottom = -1;
    
    float x_scale = 1;
    float y_scale = 1;
    
    float newleft = 0;
	float newright = 0;
	float newtop = 0;
	float newbottom = 0;
    
    public void setManimatorListener(ManimatorListener animatorListener) {
		this.animatorListener = animatorListener;
	}

	public interface ManimatorListener{
    	public void onAnimationEnd();
    }
	
    public PerspectiveView(Context context) {
        super(context);
        this.init();
    }
    

    public void setTextures(Bitmap coverTexture, Bitmap spineTexture, Bitmap contentTexture) {
        this.coverTexture = coverTexture;
        this.spineTexture = spineTexture;
        this.contentTexture = contentTexture;
        
        
    }
    
    public void setTextures(Bitmap coverTexture, Bitmap contentTexture,float left,float right,float top,float bottom) {
         
    		this.coverTexture = coverTexture;
        this.contentTexture = contentTexture;
        
        Log.v("ARC", contentTexture+"::::::::");
        this.hasTexture = false;
        
        this.origin_left = left;
        this.origin_right = right;
        this.origin_top = top;
        this.origin_bottom = bottom;
        
        this.left = BookUtils.toGLX(origin_left, ratio,this.width);
        this.right = BookUtils.toGLX(origin_right, ratio,this.width);
        this.bottom = BookUtils.toGLY(origin_bottom,this.height);
        this.top = BookUtils.toGLY(origin_top,this.height);
        
    }

    private void init() {
        this.setEGLContextClientVersion(2);

        this.setEGLConfigChooser(8, 8, 8, 8, 0, 0);
        this.setZOrderOnTop(true);
        this.getHolder().setFormat(PixelFormat.TRANSPARENT);

        this.setRenderer(this);
        this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(0, 0, 0, 0);

        //照相机位置
        float eyeX = 0.0f;
        float eyeY = 0.0f;
        float eyeZ = distance;
//        float eyeZ=1f;

        //照相机拍照方向
        float lookX = 0.0f;
        float lookY = 0.0f;
        float lookZ = -1.0f;

        //照相机的垂直方向
        float upX = 0.0f;
        float upY = 1.0f;
        float upZ = 0.0f;

        Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        coverMesh = new TextureMesh(getContext());
        contentMesh = new TextureMesh(getContext());
        if(spineTexture!=null){
        	 spineMesh = new TextureMesh(getContext());
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        this.width = width;
        this.height = height;
        glViewport(0, 0, width, height);


        ratio = (float) width / height;
        
        Log.v("ARC", "调用onSurfaceChanged");

    }

    float nr_x,nr_y, nd, barW;
    @Override
    public void onDrawFrame(GL10 gl10) {
    		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    		if(coverTexture==null){
    			return;
    		}
        float near = distance;
        
        float far = 250;//ratio*2*near/(right-left);
        
        float alpha = (float) Math.atan(ratio/near);
        float alphb = (float) Math.atan(1/near);
        
        
        nd = (near*ratio*2)/(right-left)-near;
		nr_x = (float) ((nd+near)*Math.tan(alpha));
		nr_y = (float) ((nd+near)*Math.tan(alphb));
		
		
		newleft = left*(nd+near)/near;
		newright = right*(nd+near)/near;
		newtop = top*(nd+near)/near;
		newbottom = bottom*(nd+near)/near;
		
		
		//nd = (50+near)*ratio/(newright-newleft);
		
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, near, far);

        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);


        setMeshTexture();
        Vertex[] vertexes = getTextureVertexes();

        coverMesh.setVertexes(vertexes);
        if(spineTexture!=null){
        	  spineMesh.setVertexes(new Vertex[]{
                      new Vertex(vertexes[0].positionX, vertexes[0].positionY, 0, 1f),
                      new Vertex(vertexes[1].positionX, vertexes[1].positionY, 0, 1f),
                      new Vertex(vertexes[0].positionX, vertexes[0].positionY, 0, 1f),
                      new Vertex(vertexes[1].positionX, vertexes[1].positionY, 0, 1f)
              });
        }
      
        contentMesh.setVertexes(getTextureVertexes());

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, (newleft+Math.abs(newright-newleft)/2)*(1-factor),(newtop-Math.abs(newbottom-newtop)/2)*(1-factor),-nd*(1-factor));
       
        
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
        contentMesh.draw(mvpMatrix);

        if(spineMesh!=null){
        		spineMesh.draw(mvpMatrix);
        }
       
        
        Log.v("ARC", "宽高比例:"+(right-left)/ratio/2);

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, (newleft+Math.abs(newright-newleft)/2)*(1-factor), (newtop-Math.abs(newbottom-newtop)/2)*(1-factor),-nd*(1-factor));
       
        Matrix.translateM(modelMatrix, 0, -ratio, 0, 0f); 
        Matrix.rotateM(modelMatrix, 0, -90 * factor, 0, 1, 0);
        Matrix.translateM(modelMatrix, 0, ratio, 0, 0);
        
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);

        long time = System.currentTimeMillis();
        coverMesh.draw(mvpMatrix);
        Log.d("origami4", "time: " + (System.currentTimeMillis() - time));
    }

    boolean hasTexture;

    private void setMeshTexture() {
        if (!hasTexture && coverTexture!=null) {
        	
            Bitmap bitmap = Bitmap.createBitmap(coverTexture);
//        		int screenWidth = ArtBookUtils.getScreenWidth(this.getContext());
//        		int screenHeight = ArtBookUtils.getScreenHeight(this.getContext());
//        		if(screenWidth/screenHeight>(right-left)/(bottom-top)){//宽度小，宽留黑边
//        			//宽度小按高度算
//        			//int b_height ＝
//        			
//        		}else if(screenWidth/screenHeight<(right-left)/(bottom-top)){//高度小，高度方向留黑
//        			
//        		}
//            Bitmap bitmap = Bitmap.createScaledBitmap(coverTexture,(int)(right-left),(int)(bottom-top), false);
            //Bitmap.createBitmap (bitmap, int x, int y, int width, int height);
            coverMesh.setTexture(bitmap);
            
            bitmap = Bitmap.createBitmap(contentTexture);
            contentMesh.setTexture(bitmap);


            if(spineTexture!=null){
                bitmap = Bitmap.createBitmap(spineTexture);
                spineMesh.setTexture(bitmap);
            }
            hasTexture = true;
        }

    }

    private Vertex[] getTextureVertexes() {
    	
    		
    	    
        Vertex[] vertexes = new Vertex[]{
                new Vertex(-Math.abs(newright-newleft)/2, Math.abs(newbottom-newtop)/2,0f, 1f),
                new Vertex(-Math.abs(newright-newleft)/2, -Math.abs(newbottom-newtop)/2,0f, 1f),
                new Vertex(Math.abs(newright-newleft)/2, Math.abs(newbottom-newtop)/2, 0f, 1f),
                new Vertex(Math.abs(newright-newleft)/2, -Math.abs(newbottom-newtop)/2, 0f, 1f)
        };
        return vertexes;
    }


    public void startAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(duration);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                final float f = (Float) valueAnimator.getAnimatedValue();
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        factor = f;
                        requestRender();
                    }
                });
            }
        });

        animator.start();
        animator.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				// TODO Auto-generated method stub
				if(animatorListener!=null){
					animatorListener.onAnimationEnd();
				}
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub
				
			}
		});
    }
}
