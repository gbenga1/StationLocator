package ng.com.audax.fortelocator;

import android.provider.BaseColumns;

public class GeofenceContract {

	public static abstract class GeofenceEntry implements BaseColumns {
		public static final int DATABASE_VERSION = 1;
		public static final String DATABASE_NAME = "Forte.db";
		public static final String TABLE_NAME = "geofences";
		public static final String LONGITUDE = "longitude";
		public static final String LATITUDE = "latitude";
		public static final String GEOFENCE_ID = "geofenceID";
		public static final String RADIUS = "radius";
	}

	public static final String TEXT_TYPE = " TEXT ";
	public static final String DOUBLE_TYPE = " DOUBLE ";
	public static final String FLOAT_TYPE = " FLOAT ";
	public static final String INTEGER_TYPE = " INTEGER ";
	public static final String COMMA_SEP = ",";
	public static final String SQL_CREATE_GEOFENCE_TABLE = "CREATE TABLE "
			+ GeofenceEntry.TABLE_NAME + "(" + GeofenceEntry._ID
			+ " INTEGER PRIMARY KEY" + COMMA_SEP + GeofenceEntry.GEOFENCE_ID
			+ INTEGER_TYPE +COMMA_SEP+ GeofenceEntry.LONGITUDE + DOUBLE_TYPE + COMMA_SEP
			+ GeofenceEntry.LATITUDE + DOUBLE_TYPE +" )";
	public static final String SQL_DELETE_GEOFENCE_TABLE = "DROP TABLE IF EXISTS "
			+ GeofenceEntry.TABLE_NAME;

	public static final String[] PROJECTION = { GeofenceEntry.GEOFENCE_ID,
			GeofenceEntry.LATITUDE, GeofenceEntry.LONGITUDE };
	public static final String SORT_ORDER = GeofenceEntry.GEOFENCE_ID + " DESC";

}
