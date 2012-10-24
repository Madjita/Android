package by.idev.asyncimageloader.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.os.Environment;
import by.idev.asyncimageloader.generator.MockDataGenerator.DataSaver;

public class SDCardBitmapWriter implements DataSaver<Bitmap> {

	private String mPath = "";
	private String mExt = null;
	private File mSdPath = Environment.getExternalStorageDirectory();

	@Override
	public void doSaveData(Bitmap data) {

		File fullPathDir = new File(mSdPath, mPath);
		fullPathDir.mkdirs();
		String name = "mock" + System.currentTimeMillis() + "." + (mExt == null ? "tmp" : mExt);
		File fullPathFile = new File(fullPathDir, name);

		FileOutputStream fileOutputStream = null;
		try {

			fileOutputStream = new FileOutputStream(fullPathFile);
			data.compress(Bitmap.CompressFormat.PNG, 50, fileOutputStream);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {

			if (fileOutputStream != null)
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	public SDCardBitmapWriter setPath(String path) {
		mPath = path;

		return this;
	}

	public SDCardBitmapWriter setExtension(String extenstion) {
		mExt = extenstion;

		return this;
	}

}
