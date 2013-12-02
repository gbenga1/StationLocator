package ng.com.audax.fortelocator;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

public class FortePrefsFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		findPreference(ForteConstants.KEY_SERVICE_SWITCH)
				.setSummary(
						sharedPrefs.getBoolean(
								ForteConstants.KEY_SERVICE_SWITCH, false) ? "Service is ON"
								: "Service is OFF");
		findPreference(ForteConstants.KEY_RADIUS).setSummary(
				"Notify when "
						+ sharedPrefs.getInt(ForteConstants.KEY_RADIUS, 0)
						+ " Kilometre(s) away from Forte fuel station");
	}

	public static final String TAG = "FortePreference";

	private SharedPreferences sharedPrefs;
	private ForteApplication forteApps;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		forteApps = (ForteApplication) activity.getApplication();
		sharedPrefs = forteApps.sharedPrefs;
		activity.getActionBar().setTitle(R.string.prefs_settings_title);
		// activity.getActionBar().setIcon(R.drawable.ic_settings);
		((HomeActivity) activity).hideMenu = true;
		activity.invalidateOptionsMenu();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		((HomeActivity) this.getActivity()).hideMenu = false;
		((HomeActivity) this.getActivity()).getActionBar().setTitle(
				R.string.app_name);
		// ((HomeActivity)
		// this.getActivity()).getActionBar().setIcon(R.drawable.forte_icon);
		(this.getActivity()).invalidateOptionsMenu();

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addPreferencesFromResource(R.xml.forteprefs);

	}

	@Override
	public void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPrefs,
			String key) {
		if (key.equals(ForteConstants.KEY_SERVICE_SWITCH)) {
			findPreference(key).setSummary(
					sharedPrefs.getBoolean(key, false) ? "Service is ON"
							: "Service is OFF");
		} else if (key.equals(ForteConstants.KEY_RADIUS)) {
			findPreference(key).setSummary(
					"Notify when " + sharedPrefs.getInt(key, 0)
							+ " Kilometre(s) away from Forte fuel station");
		} else if (key.equals(ForteConstants.KEY_GEOFENCE_STATES)) {
			String summ = this.getResources().getString(
					R.string.list_prefs_summary);
			String state = sharedPrefs.getString(key, "Lagos");
			findPreference(key).setSummary(
					"Send notification when in " + state + " State");
			 //this.getResources().getString(R.string.list_prefs_summary));
		}

	}

}
