package ng.com.audax.fortelocator;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

public class ForteApplication extends Application implements
		OnSharedPreferenceChangeListener {
	public SharedPreferences sharedPrefs;
	public ForteDbHelper mDBHelper;
	/**
	 * a flag to indicate that geofences have been imported
	 */
	public boolean geofencesImported;

	@Override
	public void onCreate() {
		super.onCreate();
		this.mDBHelper = new ForteDbHelper(this);
		this.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.sharedPrefs.registerOnSharedPreferenceChangeListener(this);
		/*Editor edit=this.sharedPrefs.edit();
		edit.putBoolean(ForteConstants.KEY_SPLASH_PACKET_RECEIVED, false);
		edit.putBoolean(ForteConstants.KEY_GEOFENCE_STARTED, false);
		edit.putBoolean(ForteConstants.KEY_SEND_GEOFENCE_COMPLETED, false);
		edit.putBoolean(ForteConstants.KEY_PREFS_GEOFENCE_IMPORTED, false);
		edit.apply();*/
		
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}

	public boolean InsertGeofences(ForteGeofence geofence) {
		return true;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		// TODO Auto-generated method stub

	}

}
