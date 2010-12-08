package de.harm.android.couchone;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import de.harm.android.couchone.common.CouchConstants;

public class CouchContactClient extends ListActivity {

	/** Called when the activity is first created. */
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		// Create an array of Strings, that will be put to our ListActivity
		String[] names = this.connectToCouchOne();

		// tests
		this.test();

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
	public String[] connectToCouchOne() {
		// insert
		ContentValues values = new ContentValues();
		values.put(CouchConstants.Contact.FIRSTNAME, "Lüder");
		values.put(CouchConstants.Contact.LASTNAME, "Duda");
		ContentResolver cr = getContentResolver();
		String type = cr.getType(CouchConstants.CONTENT_URI);
		Uri uri = cr.insert(CouchConstants.CONTENT_URI, values);
		Cursor c = managedQuery(CouchConstants.CONTENT_URI, null, null,
				null, null);

		List<String> result = new ArrayList<String>();
		if (c.moveToFirst()) {
			do {
				result.add(c.getString(c.getColumnIndex("key")));
			} while (c.moveToNext());
		}
		return (String[]) result.toArray(new String[result.size()]);
	}

	private void test() {

		// insert
		ContentValues values = new ContentValues();
		values.put(CouchConstants.Contact.FIRSTNAME, "Lüder");
		values.put(CouchConstants.Contact.LASTNAME, "Duda");

		Uri uri = this.getContentResolver().insert(CouchConstants.CONTENT_URI,
				values);
		values.clear();
		if (uri == null) {
			throw new SQLException("Test: insert failed");
		}

		// query and update
		// update: nur zu ändernde Attribute übergeben
		// vom Provider werden alle Attribute laden, einige ge�ndert, dann
		// Contact
		// komplett neu schreiben
		Cursor cursor = this.managedQuery(CouchConstants.CONTENT_URI, null,
				null, new String[] { "Lüder" }, null);

		String query = this.testQuery(cursor);
		if (!query.equals("LüderDuda")) {
			throw new SQLException("Test: query for \"Lüder\" failed");
		}

		// Values von vorhandenen keys werden überschrieben
		values.put(CouchConstants.Contact.FIRSTNAME, "Gert Müller");
		values.put(CouchConstants.Contact.LASTNAME, "Friedrichs");

		int success = this.getContentResolver().update(uri, values, null, null);
		if (success != 1) {
			throw new SQLException("Test: update failed");
		}

		// query to verify update and delete
		cursor = this.managedQuery(CouchConstants.CONTENT_URI, null, null,
				new String[] { "Gert" }, null);

		query = this.testQuery(cursor);
		if (!query.equals("Gert MüllerFriedrichs")) {
			throw new SQLException("Test: query for \"Gert\" failed");
		}

		success = this.getContentResolver().delete(uri, null, null);
		if (success != 1) {
			throw new SQLException("Test: delete failed");
		}
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