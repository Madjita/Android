package com.softeq.buzz;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;

/**
 * Extension of SurfaceView which starts a camera preview and decode
 * video content on the native side.
 */
public class CameraView extends SurfaceView implements
                SurfaceHolder.Callback, Camera.PreviewCallback {
	

    
    
    static {
        System.loadLibrary("livecamera");

    }
    
    private Camera mCamera;
    private byte[] mVideoSource;
    private Bitmap mBackBuffer;
    private Paint mPaintBW = new Paint();
    private Paint mPaintBW1 = new Paint();
    
    private Paint mPaintLG = new Paint();
    private Paint mPaintLG1 = new Paint();
    private Paint mPaintLG2 = new Paint();
    
    private Paint mPaintPD = new Paint();
    private Paint mPaintPD1 = new Paint();
    private Paint mPaintPD2 = new Paint();

    
    private Paint mPaintText = new Paint();
    
    
    {
        
    	//Add BW filter
    	ColorMatrix mColorMatrixBW = new ColorMatrix();
    	mColorMatrixBW.setSaturation(0);
    	ColorMatrixColorFilter mColorMatrixColorFilter = new ColorMatrixColorFilter(mColorMatrixBW);
    	
    	ColorMatrix mColorMatrixBW1 = new ColorMatrix();
    	mColorMatrixBW1.setRotate(0, 90);
    	ColorMatrixColorFilter mColorMatrixColorFilter1 = new ColorMatrixColorFilter(mColorMatrixBW1);
    	
    	mPaintBW.setColorFilter(mColorMatrixColorFilter);
    	mPaintBW1.setColorFilter(mColorMatrixColorFilter1);
    	
    	mPaintLG.setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0x000000FF));
    	mPaintLG1.setColorFilter(new LightingColorFilter(0xFFFFFF00, 0x00000000));
    	mPaintLG2.setColorFilter(new LightingColorFilter(0xFFFF00F0, 0xFF000000));
    	
    	mPaintPD.setColorFilter(new PorterDuffColorFilter(0xFF00FF00, PorterDuff.Mode.DARKEN));
    	mPaintPD1.setColorFilter(new PorterDuffColorFilter(0xFF00FF00, PorterDuff.Mode.LIGHTEN));
    	mPaintPD2.setColorFilter(new PorterDuffColorFilter(0xFF00cccc, PorterDuff.Mode.SCREEN));
    	
    	mPaintText.setTextSize(40);
    	mPaintBW.setTypeface(Typeface.DEFAULT_BOLD);
    	mPaintText.setColor(Color.BLACK);
    }

    public native void decode(Bitmap pTarget, byte[] pSource);


    public CameraView(Context context) {
        super(context);

        // Registers current class so that it listens to surface
        // event (creation, destruction and changes).
        getHolder().addCallback(this);
        // Clears the flag keeping the surface from getting drawn.
        // Necessary when not drawing from a thread.
        setWillNotDraw(false);
    }
        
    
    public CameraView(Context context, AttributeSet set, int defStyle) {
    	this(context);
	}
    
    public CameraView(Context context, AttributeSet set) {
		this(context);
	}
    
    

    public void surfaceCreated(SurfaceHolder holder) {
    	

		
        try {
            // Acquires the default camera
        	int numberOfCameras = Camera.getNumberOfCameras();
            mCamera = Camera.open(numberOfCameras - 1);
            // Sets landscape mode to avoid complications related to
            // screen orientation handling.
            mCamera.setDisplayOrientation(0);
            // Registers callbacks. Automatic preview is deactivated
            // as we want to process data ourself (in a buffer).
            
            //TODO add foo holder here
            mCamera.setPreviewDisplay(getHolder());
            
            
            mCamera.setPreviewCallbackWithBuffer(this);
            
        } catch (IOException eIOException) {
            mCamera.release();
            mCamera = null;
            throw new IllegalStateException();
        }
    }

    public void surfaceChanged(SurfaceHolder pHolder, int pFormat, int pWidth, int pHeight) {
        mCamera.stopPreview();

        // Finds a suitable resolution.
        Size lSize = findBestResolution(pWidth, pHeight);
        

        // Prepares video source and back buffers.
        PixelFormat lPixelFormat = new PixelFormat();
        PixelFormat.getPixelFormatInfo(mCamera.getParameters().getPreviewFormat(), lPixelFormat);
        int lSourceSize = lSize.width * lSize.height * lPixelFormat.bitsPerPixel / 8;
        
        mVideoSource = new byte[lSourceSize];
        mBackBuffer = Bitmap.createBitmap(lSize.width, lSize.height, Bitmap.Config.ARGB_8888);

        // Set-up camera size and video format. YCbCr_420_SP
        // should be the default on Android anyway.
        Camera.Parameters lParameters = mCamera.getParameters();
        lParameters.setPreviewSize(lSize.width, lSize.height);
        lParameters.setPreviewFormat(PixelFormat.YCbCr_420_SP);
        mCamera.setParameters(lParameters);

        // Starts receiving pictures from the camera.
        mCamera.addCallbackBuffer(mVideoSource);
        mCamera.startPreview();
    }

    private Size findBestResolution(int pWidth, int pHeight) {
    	
        List<Size> lSizes = mCamera.getParameters() .getSupportedPreviewSizes();
        // Finds the biggest resolution which fits the screen.
        // Else, returns the first resolution found.
        Size lSelectedSize = mCamera.new Size(0, 0);
        
        for (Size lSize : lSizes) {
            if ((lSize.width <= 320)// pWidth)
                            && (lSize.height <= pHeight)
                            && (lSize.width >= lSelectedSize.width)
                            && (lSize.height >= lSelectedSize.height)) {
                lSelectedSize = lSize;
            }
        }
        // Previous code assume that there is a preview size smaller
        // than screen size. If not, hopefully the Android API
        // guarantees that at least one preview size is available.
        if ((lSelectedSize.width == 0)
                        || (lSelectedSize.height == 0)) {
            lSelectedSize = lSizes.get(0);
        }
        return lSelectedSize;
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Releases camera which is a shared resource.
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            // These variables can take a lot of memory. Gets rid of
            // them as fast as we can.
            mCamera = null;
            mVideoSource = null;
            mBackBuffer = null;
        }
    }

    public void onPreviewFrame(byte[] pData, Camera pCamera) {
        // New data has been received from camera. Processes it and
        // requests surface to be redrawn right after.
        decode(mBackBuffer, pData);
        invalidate();
        
        
        Log.d("LIVECAMERA", "frame preview: " + pCamera.toString() + " data: " + pData.length);
    }

    
    private long mLastUpdate = System.currentTimeMillis();
	private Rect mBouds = new Rect();
    @Override
    protected void onDraw(Canvas pCanvas) {
        if (mCamera != null) {
            // Draws resulting image at screen origin.
            pCanvas.drawBitmap(mBackBuffer, 0, 0, mPaintBW);
            pCanvas.drawBitmap(mBackBuffer, 0, mBackBuffer.getHeight(), mPaintLG);
            pCanvas.drawBitmap(mBackBuffer, 0, mBackBuffer.getHeight() * 2, mPaintPD);

            
            pCanvas.drawBitmap(mBackBuffer, mBackBuffer.getWidth(), 0, mPaintPD1);
            pCanvas.drawBitmap(mBackBuffer, mBackBuffer.getWidth() * 2, mBackBuffer.getHeight()*2, mPaintPD2);
            
            pCanvas.drawBitmap(mBackBuffer, mBackBuffer.getWidth(), mBackBuffer.getHeight(), mPaintLG1);
            pCanvas.drawBitmap(mBackBuffer, mBackBuffer.getWidth()*3, mBackBuffer.getHeight()*2, mPaintLG2);
            
            pCanvas.drawBitmap(mBackBuffer, mBackBuffer.getWidth(), mBackBuffer.getHeight() * 2, mPaintBW1);
            
            
            //Draw FPS
            double fps = (1e3/(System.currentTimeMillis() - mLastUpdate));
            mLastUpdate = System.currentTimeMillis();
            final String text = String.format("%.2f", fps);
            
            mPaintText.getTextBounds(text, 0, text.length(), mBouds);
            final int y = 2 * mBouds.height();
            final int x = pCanvas.getWidth() - mBouds.width() - 20;
            pCanvas.drawText(text, x, y, mPaintText);
            
            // Enqueues buffer again to get next image.
            mCamera.addCallbackBuffer(mVideoSource);
        }
    }
    
    
}
