package ng.com.audax.fortelocator;

import com.google.android.gms.location.Geofence;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class ForteGeofenceStore {
	private static final String TAG = "ForteGeofenceStore";
	private SQLiteDatabase mWritableDB;
	private SQLiteDatabase mReadableDB;
	private ForteApplication mApplication;

	// instantiate database object
	public ForteGeofenceStore createDB() throws SQLiteException {

		this.mReadableDB = mApplication.mDBHelper.getReadableDatabase();
		this.mWritableDB = mApplication.mDBHelper.getWritableDatabase();
		return this;
	}

	public ForteGeofenceStore(ForteApplication mApplication) {
		this.mApplication = mApplication;
	}

	/**
	 * Save geofence to database.
	 * 
	 * @param id
	 *            geofence unique identification no
	 * @param lat
	 *            geofence latitude
	 * @param lng
	 *            geofence longitude
	 * @return
	 */
	public boolean saveGeofenceToDB(String id, String lat, String lng) {
		ContentValues values = new ContentValues();
		values.put(GeofenceContract.GeofenceEntry.GEOFENCE_ID,
				Integer.parseInt(id));
		values.put(GeofenceContract.GeofenceEntry.LATITUDE,
				Double.parseDouble(lat));
		values.put(GeofenceContract.GeofenceEntry.LONGITUDE,
				Double.parseDouble(lng));
		/*
		 * values.put(GeofenceContract.GeofenceEntry.RADIUS,
		 * mForteGeofence.getRadius());
		 */
		// insert the new row
		try {
			this.mWritableDB.insertOrThrow(
					GeofenceContract.GeofenceEntry.TABLE_NAME, null, values);
			return true;
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage());
			return false;
		}

	}

	public ForteGeofence inflateForteGeofenceXML(String mGeofenceItemXML) {
		String[] parts = mGeofenceItemXML.split("_");
		Log.d(TAG, parts[0] + " " + parts[1] + " " + parts[2]+" "+parts[3]);
		float radius = mApplication.sharedPrefs.getInt(
				ForteConstants.KEY_RADIUS, ForteConstants.INVALID_INT_VALUE);
		int transitionType = mApplication.sharedPrefs.getInt(
				ForteConstants.KEY_TRANSITION_TYPE,
				ForteConstants.INVALID_INT_VALUE);
		long expires = Geofence.NEVER_EXPIRE;
		ForteGeofence fgeofence = new ForteGeofence(parts[0],
				Double.parseDouble(parts[1]), Double.parseDouble(parts[2]),
				radius, expires, transitionType,parts[3]);
		return fgeofence;
	}

	/*
	 * public List<ForteGeofence> getGeofenceFromDB(Feedback caller) { String
	 * desc = mApplication.getResources().getString(
	 * R.string.retrieving_geofence_objects); List<ForteGeofence> fGeofenceList
	 * = new ArrayList<ForteGeofence>(); Cursor c =
	 * mReadableDB.query(GeofenceContract.GeofenceEntry.TABLE_NAME, // the //
	 * Table // to // query GeofenceContract.PROJECTION, // Columns to return
	 * null, // null, // null, // null, // GeofenceContract.SORT_ORDER // The
	 * Sort order ); float radius = mApplication.sharedPrefs.getInt(
	 * ForteConstants.KEY_RADIUS, ForteConstants.INVALID_INT_VALUE); int
	 * transitionType = mApplication.sharedPrefs.getInt(
	 * ForteConstants.KEY_TRANSITION_TYPE, ForteConstants.INVALID_INT_VALUE);
	 * long expires = Geofence.NEVER_EXPIRE; int totalRow = 100 c.getCount() ;
	 * int treated = 0; if (totalRow < 1) { return null; }
	 * 
	 * while (treated < totalRow c.moveToNext() ) { treated++; try {
	 * Thread.sleep(100); } catch (InterruptedException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); }
	 * 
	 * int mGeoID = c .getInt(c
	 * .getColumnIndexOrThrow(GeofenceContract.GeofenceEntry .GEOFENCE_ID));
	 * double lat = c .getDouble(c
	 * .getColumnIndexOrThrow(GeofenceContract.GeofenceEntry.LATITUDE)); double
	 * lng = c .getDouble(c .getColumnIndexOrThrow(GeofenceContract
	 * .GeofenceEntry.LONGITUDE)); ForteGeofence fgeofence = new
	 * ForteGeofence(String.valueOf(mGeoID), lat, lng, radius, expires,
	 * transitionType); fGeofenceList.add(fgeofence);
	 * 
	 * caller.update(totalRow, treated, desc); }
	 * 
	 * return null;
	 * 
	 * }
	 */

	// close database
	public void closeDB() {
		if (mWritableDB != null) {
			mWritableDB.close();
		} else if (mReadableDB != null) {
			mReadableDB.close();
		}
	}

	/**
	 * 
	 * This interface must be implemented by the caller of this method to get
	 * the feedback of processed geofence from this class.
	 * 
	 */
	public static interface Feedback {
		public void update(float totalRow, float treatedRow, String description);

	}

}
