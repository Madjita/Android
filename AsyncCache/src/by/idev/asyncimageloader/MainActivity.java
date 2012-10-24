package by.idev.asyncimageloader;

import by.idev.asyncimageloader.generator.MockBitmapFactory;
import by.idev.asyncimageloader.generator.MockDataGenerator;
import by.idev.asyncimageloader.generator.SDCardBitmapWriter;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        findViewById(R.id.button).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doWork();
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    private void doWork() {
    	
    	MockDataGenerator<Bitmap> dataGenerator = new MockDataGenerator<Bitmap>();
    	SDCardBitmapWriter cardWriter = new SDCardBitmapWriter();
    	MockBitmapFactory bitmapFactory = new MockBitmapFactory();
    	
    	cardWriter.setExtension("png").setPath("mockdata");
    	dataGenerator.setDataFactory(bitmapFactory).setDataSaver(cardWriter);
    	
    	dataGenerator.genarateRandom(100);
    }
}
