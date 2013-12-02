package ng.com.audax.fortelocator;

public final class ForteConstants {
	// A string of length 0, used to clear out input fields
	public static final String EMPTY_STRING = new String();
	public static final CharSequence GEOFENCE_ID_DELIMITER = ",";

	// Used to track what type of geofence removal request was made.
	public enum REMOVE_TYPE {
		INTENT, LIST
	}

	// Used to track what type of request is in process
	public enum REQUEST_TYPE {
		ADD, REMOVE
	}

	// Defines the key for the status "extra" in an Intent
	public static final String EXTENDED_PROGRESS_LEVEL = "ng.com.audax.fortelocator.PROGRESS_LEVEL";
	public static final String EXTENDED_PROGRESS_DESCRIPTION = "ng.com.audax.fortelocator.PROGRESS_DESCRIPTION";
	public static final String PROGRESS_STATUS = "status";
	public static final String CLOSE_SPLASHSCREEN = "ng.com.audax.fortelocator.CLOSE_SPLASHSCREEN";
	public static final String EXTRA_GEOFENCE_STATUS = "ng.com.audax.fortelocator.EXTRA_GEOFENCE_STATUS";
	public static final String EXTRA_CONNECTION_CODE = "com.example.android.EXTRA_CONNECTION_CODE";
	public static final String EXTRA_CONNECTION_ERROR_CODE = "ng.com.audax.fortelocator.EXTRA_CONNECTION_ERROR_CODE";
	public static final String EXTRA_CONNECTION_ERROR_MESSAGE = "ng.com.audax.fortelocator.EXTRA_CONNECTION_ERROR_MESSAGE";
	public static final String EXTRA_PARCELABLE_DATA = "ng.com.audax.fortelocator.EXTRA_PARCELABLE_DATA";
	// The Intent category used by all Location Services
	public static final String CATEGORY_LOCATION_SERVICES = "ng.com.audax.fortelocator.CATEGORY_LOCATION_SERVICES";
	// intents actions
	public static final String ACTION_GEOFENCE_ERROR = "ng.com.audax.fortelocator.ACTION_GEOFENCES_ERROR";
	public static final String ACTION_GEOFENCES_ADDED = "ng.com.audax.fortelocator.ACTION_GEOFENCES_ADDED";
	public static final String ACTION_CONNECTION_ERROR = "ng.com.audax.fortelocator.ACTION_CONNECTION_ERROR";
	public static final String ACTION_GEOFENCES_REMOVED = "ng.com.audax.fortelocator.ACTION_GEOFENCES_DELETED";
	public static final String ACTION_GEOFENCE_TRANSITION_ERROR = "ng.com.audax.fortelocator.ACTION_GEOFENCE_TRANSITION_ERROR";
	public static final String ACTION_GEOFENCE_TRANSITION = "ng.com.audax.fortelocator.ACTION_GEOFENCE_TRANSITION";
	public static final String ACTION_LOADER_DATA = "ng.com.audax.fortelocator.ACTION_LOADER_DATA";
	public static final String ACTION_LOADER = "ng.com.audax.fortelocator.ACTION_LOADER";

	// preference name
	// Keys for flattened geofences stored in SharedPreferences
	public static final String KEY_RADIUS = "ng.com.audax.fortelocator.geofence.KEY_GEOFENCE_RADIUS";
	public static final String KEY_EXPIRATION_DURATION = "ng.com.audax.fortelocator.geofence.KEY_EXPIRATION_DURATION";
	public static final String KEY_TRANSITION_TYPE = "ng.com.audax.fortelocator.geofence.KEY_TRANSITION_TYPE";
	public static final String PREFS_NAME = "config";
	public static final String KEY_GEOFENCE_STATES = "ng.com.audax.fortelocator.geofence.KEY_GEOFENCE_STATES";
	public static final String KEY_PREFS_GEOFENCE_IMPORTED = "ng.com.audax.fortelocator.geofence.KEY_PREFS_GEOFENCE_IMPORTED";
	public static final String KEY_GEOFENCE_RADIUS = "ng.com.audax.fortelocator.geofence.KEY_GEOFENCE_RADIUS";
	public static final String KEY_SERVICE_SWITCH = "ng.com.audax.fortelocator.geofence.KEY_SERVICE_SWITCH";
	public static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	/*
	 * Invalid values, used to test geofence storage when retrieving geofences
	 */
	public static final long INVALID_LONG_VALUE = -999l;
	public static final float INVALID_FLOAT_VALUE = -999.0f;
	public static final int INVALID_INT_VALUE = -999;
	public static final String KEY_GEOFENCE_STARTED = "ng.com.audax.fortelocator.geofence.KEY_GEOFENCE_STARTED";
	public static final String KEY_SEND_GEOFENCE_COMPLETED = "ng.com.audax.fortelocator.geofence.KEY_SEND_GEOFENCE_COMPLETED";
	public static final String KEY_SPLASH_PACKET_RECEIVED = "ng.com.audax.fortelocator.geofence.KEY_SPLASH_PACKET_RECEIVED";
}
