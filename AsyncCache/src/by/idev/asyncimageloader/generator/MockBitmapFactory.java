package by.idev.asyncimageloader.generator;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import by.idev.asyncimageloader.generator.MockDataGenerator.DataFactory;

public class MockBitmapFactory implements DataFactory<Bitmap> {
	
	private int mWidth = 200;
	private int mHeight = 200;

	@Override
	public Bitmap create() {
		Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
		drawSome(bitmap);
		
		return bitmap;
	}
	
	public MockBitmapFactory setSize(int width, int height) {
		mWidth = width;
		mHeight = height;
		
		return this;
	}
	
	private void drawSome(Bitmap bitmap) {
		Canvas canvas = new Canvas(bitmap);
		int randColor = new Random().nextInt(0xFFFFFF);
		randColor |= 0xFF000000;
		
		canvas.drawColor(randColor);
	}

}
