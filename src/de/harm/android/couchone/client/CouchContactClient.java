package de.harm.android.couchone.client;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class CouchContactClient extends ListActivity {

	private final Uri CONTENT_URI = Uri
			.parse("content://de.harm.android.couchone.provider.Provider/test2");

	/** Called when the activity is first created. */
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		// Create an array of Strings, that will be put to our ListActivity
		String[] names = this.connectToCouchOne();

		// Create an ArrayAdapter, that will actually make the Strings above
		// appear in the ListView
		this.setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_checked, names));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);

		// Get the item that was clicked
		Object o = this.getListAdapter().getItem(position);
		String keyword = o.toString();
		Toast.makeText(this, "You selected: " + keyword, Toast.LENGTH_LONG)
				.show();
	}

	/** Called when the activity is first created. */
	private String[] connectToCouchOne() {
		Cursor c = managedQuery(CONTENT_URI, null, null, null, null);

		List<String> result = new ArrayList<String>();
		if (c.moveToFirst()) {
			do {
				result.add(c.getString(c.getColumnIndex("key")));
			} while (c.moveToNext());
		}
		return (String[]) result.toArray(new String[result.size()]);
	}

	private String testQuery(Cursor cursor) {
		// 1. Treffer wählen und alle spalten durchlaufen
		ContentValues values = new ContentValues();
		cursor.moveToFirst();
		if (!cursor.isBeforeFirst()) {
			for (int i = 0; i < cursor.getColumnCount(); i++) {
				values.put(cursor.getColumnName(i), cursor.getString(i));
			}
		}
		String fn = "";
		String ln = "";
		if (values.containsKey("key") && values.containsKey("value")) {
			fn = values.getAsString("key");
			ln = values.getAsString("value");
		}
		values.clear();
		cursor.close();
		return fn + ln;
	}
}