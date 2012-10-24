package by.idev.asyncimageloader.generator;

import java.util.Random;

public class MockDataGenerator<DataType> {
	
	protected DataSaver<DataType> mDataSaver;
	protected DataFactory<DataType> mDataFactory;
	
	
	public MockDataGenerator<DataType> setDataSaver(DataSaver<DataType> dataSaver) {
		mDataSaver = dataSaver;
		
		return this;
	}
	
	public MockDataGenerator<DataType> setDataFactory(DataFactory<DataType> dataFactory) {
		mDataFactory = dataFactory;
		
		return this;
	}
	
	public void genarateRandom(int max) {
		if (max < 0) throw new IllegalArgumentException("Argument 'max' must not be nagative");
		Random random = new Random();
		
		int count = random.nextInt(max);
		
		generate(count);
	}

	public void generate(int count) {
		if (count < 0) throw new IllegalArgumentException("Argument 'count' must not be nagative");
		checkState();
		
		DataType data = null;
		for (int i =  0; i <  count; i++) {
			data = mDataFactory.create();
			mDataSaver.doSaveData(data);
		}
	}
	
	private void checkState() {
		String detailMessage = null;
		if (mDataFactory == null) {
			detailMessage = "DataFactory must not be null!";
		} else if (mDataSaver == null) {
			detailMessage = "DataSaver must not be null!";
		} else {
			return;
		}
		throw new IllegalStateException(detailMessage);
	}

	public static interface DataSaver<DataType> {
		
		public void doSaveData(DataType data);
		
	}
	
	public static interface DataFactory<DataType> {
		
		public DataType create();
		
	}
}
