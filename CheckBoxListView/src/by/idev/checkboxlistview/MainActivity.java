package by.idev.checkboxlistview;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity {

	final static int COUNT = 20;
	List<ListItem> items = generateRandomListItems(COUNT);

	static class ListItem {
		public String text = "empty";
		public boolean isChecked = false;
	}

	static List<ListItem> generateRandomListItems(int count) {
		Random random = new Random();
		LinkedList<ListItem> list = new LinkedList<ListItem>();

		ListItem current = null;
		for (int i = 0; i < count; i++) {
			current = new ListItem();
			current.isChecked = random.nextInt() % 2 == 0;
			current.text = "Item_" + i;

			list.add(current);
		}

		return list;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setListAdapter(new CheckboxArrayAdapter(this, items));
	}

	class CheckboxArrayAdapter extends ArrayAdapter<ListItem> {

		public CheckboxArrayAdapter(Context context, List<ListItem> objects) {
			super(context, R.id.text, R.layout.list_item, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			ViewHolder holder = null;

			if (convertView == null) {
				view = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item,
						null);
				holder = new ViewHolder();
				holder.mCheckBox = (CheckBox) view.findViewById(R.id.checkbox);
				holder.mTextView = (TextView) view.findViewById(R.id.text);

				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}

			holder.mIndex = position;
			holder.mCheckBox.setChecked(items.get(position).isChecked);
			holder.mTextView.setText(items.get(position).text);

			holder.mCheckBox.setOnCheckedChangeListener(holder);
			
			view.setOnClickListener(holder);

			return view;
		}

		class ViewHolder implements OnCheckedChangeListener, OnClickListener {
			TextView mTextView;
			CheckBox mCheckBox;

			int mIndex;

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if (items.get(mIndex).isChecked != isChecked) {

					items.get(mIndex).isChecked = isChecked;
					// any other processing, add to db, collection etc
					Toast.makeText(MainActivity.this, "checked at position " + mIndex + " " + isChecked, 0).show();
					notifyDataSetChanged();
				}
			}

			@Override
			public void onClick(View v) {
				ViewHolder holder = (ViewHolder) v.getTag();
				Toast.makeText(MainActivity.this, "clicked at position " + holder.mIndex, 0).show();
				//Open you activity or whatever
			}

		}

	}
}
