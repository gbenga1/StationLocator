package ng.com.audax.fortelocator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ForteDbHelper extends SQLiteOpenHelper {
	// If you change the database schema, you must increment the database
	// version.

	public ForteDbHelper(Context context) {
		super(context, GeofenceContract.GeofenceEntry.DATABASE_NAME, null,
				GeofenceContract.GeofenceEntry.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		Log.e("ForteDBHelper","Creating new Database: "+GeofenceContract.GeofenceEntry.DATABASE_NAME);
		database.execSQL(GeofenceContract.SQL_CREATE_GEOFENCE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		database.execSQL(GeofenceContract.SQL_DELETE_GEOFENCE_TABLE);
		onCreate(database);
	}

}
