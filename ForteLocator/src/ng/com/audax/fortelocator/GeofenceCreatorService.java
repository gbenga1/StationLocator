package ng.com.audax.fortelocator;

import java.util.ArrayList;
import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;

public class GeofenceCreatorService extends IntentService implements
		ForteGeofenceStore.Feedback {
	ForteApplication mForteApplication;

	@Override
	public void onCreate() {
		super.onCreate();
		mForteApplication = (ForteApplication) this.getApplication();
		mForteApplication.sharedPrefs.edit()
				.putBoolean(ForteConstants.KEY_GEOFENCE_STARTED, true).commit();
	}

	private final String TAG = "GeofenceLoaderService";
	public List<ForteGeofence> mForteGeofencelist = new ArrayList<ForteGeofence>();

	public GeofenceCreatorService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public GeofenceCreatorService() {
		super("Geofence");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		ForteGeofenceStore mStore = new ForteGeofenceStore(mForteApplication);
		/* must be called to instantiate mForteGeofencelist */
		loadGeofencesFromXML(mStore);
		/* transfer created ForteGeofence */
		ArrayList<ForteGeofence> fg = transferForteGeofence();
		GeofenceTransporter tf = new GeofenceTransporter(fg);
		// close the splash screen
		sendStatus(100, "Completed", true, tf);
		mForteApplication.sharedPrefs.edit()
				.putBoolean(ForteConstants.KEY_SEND_GEOFENCE_COMPLETED, true)
				.commit();

	}

	public void loadGeofencesFromXML(ForteGeofenceStore store) {
		String desc = getApplication().getResources().getString(
				R.string.inflating_geofence_objects);
		Resources res = getResources();
		String[] geofences = res.getStringArray(R.array.geofeceobjects);
		int noRows = geofences.length;
		int prRows = 0;
		for (String geofence : geofences) {
			mForteGeofencelist.add(store.inflateForteGeofenceXML(geofence));
			prRows++;
			update(noRows, prRows, desc);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public ArrayList<ForteGeofence> transferForteGeofence() {
		String desc = getApplication().getResources().getString(
				R.string.sending_geofence_objects);
		int size = mForteGeofencelist.size();
		int prRows = 0;
		ArrayList<ForteGeofence> list = new ArrayList<ForteGeofence>();
		for (ForteGeofence mForteGeofence : mForteGeofencelist) {
			prRows++;
			int progress = (int) ((prRows / (float) size) * 100);
			list.add(mForteGeofence);
			this.sendStatus(progress, desc, false);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}

	private void sendStatus(int progress, String description,
			boolean closeSplashScreen) {
		Intent localIntent = new Intent(ForteConstants.ACTION_LOADER)
				// Puts the progress level into the Intent
				.putExtra(ForteConstants.EXTENDED_PROGRESS_LEVEL, progress)
				// put the progress description into intent
				.putExtra(ForteConstants.EXTENDED_PROGRESS_DESCRIPTION,
						description)
				.putExtra(ForteConstants.CLOSE_SPLASHSCREEN, closeSplashScreen);
		// Broadcasts the Intent to receivers in this app.
		LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
	}

	private void sendStatus(int progress, String description,
			boolean closeSplashScreen, Parcelable data) {
		Intent localIntent = new Intent(ForteConstants.ACTION_LOADER_DATA)
				// Puts the progress level into the Intent
				.putExtra(ForteConstants.EXTENDED_PROGRESS_LEVEL, progress)
				// put the progress description into intent
				.putExtra(ForteConstants.EXTENDED_PROGRESS_DESCRIPTION,
						description)
				.putExtra(ForteConstants.CLOSE_SPLASHSCREEN, closeSplashScreen)
				.putExtra(ForteConstants.EXTRA_PARCELABLE_DATA, data);
		// Broadcasts the Intent to receivers in this app.
		LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
	}

	@Override
	public void update(float totalRow, float treatedRow, String description) {
		int progress = (int) ((treatedRow / totalRow) * 100);
		sendStatus(progress, description, false);
	}

}
