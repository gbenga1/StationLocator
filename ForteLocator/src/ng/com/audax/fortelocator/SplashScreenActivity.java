package ng.com.audax.fortelocator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ng.com.audax.fortelocator.ForteConstants.REQUEST_TYPE;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SplashScreenActivity extends Activity implements
		GooglePlayServicesClient.OnConnectionFailedListener {
	@Override
	protected void onDestroy() {
		Log.e(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		Log.e(TAG, "onStop");
		super.onStop();
	}

	private final String TAG = "SplashScreenActivity";
	// Store the current request
	private REQUEST_TYPE mRequestType;
	public ProgressBar mProgress;
	private Intent mServiceIntent;
	private TextView mTextView;
	// Store a list of geofences to add
	private List<Geofence> mCurrentGeofences;
	// Add geofences handler
	private GeofenceRequestHandler mGeofenceRequestHandler;
	/*
	 * An instance of an inner class that receives broadcasts from listeners and
	 * from the IntentService that receives geofence transition events
	 */
	private ForteGeofenceReceiver mForteGeofenceReceiver;

	// An intent filter for the broadcast receiver
	private IntentFilter mIntentFilter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Create a new broadcast receiver to receive updates from the listeners
		// and service
		mForteGeofenceReceiver = new ForteGeofenceReceiver();
		// Create an intent filter for the broadcast receiver
		mIntentFilter = new IntentFilter();

		// Action for broadcast Intents that report successful addition of
		// geofences
		mIntentFilter.addAction(ForteConstants.ACTION_GEOFENCES_ADDED);

		// Action for broadcast Intents that report successful removal of
		// geofences
		mIntentFilter.addAction(ForteConstants.ACTION_GEOFENCES_REMOVED);

		// Action for broadcast Intents containing various types of geofencing
		// errors
		mIntentFilter.addAction(ForteConstants.ACTION_GEOFENCE_ERROR);
		mIntentFilter.addAction(ForteConstants.ACTION_LOADER);
		mIntentFilter.addAction(ForteConstants.ACTION_LOADER_DATA);

		// All Location Services sample apps use this category
		mIntentFilter.addCategory(ForteConstants.CATEGORY_LOCATION_SERVICES);

		// Instantiate the current List of geofences
		mCurrentGeofences = new ArrayList<Geofence>();
		// Instantiate a Geofence requester
		mGeofenceRequestHandler = new GeofenceRequestHandler(this);

		servicesConnected();
		setContentView(R.layout.splashscreenforte);
		mProgress = (ProgressBar) findViewById(R.id.progressBarSplash);
		mTextView = (TextView) findViewById(R.id.txtSplash);
		PreferenceManager.setDefaultValues(
				((ForteApplication) getApplication()), R.xml.forteprefs, false);
		/*
		 * Creates a new Intent to start the GeofenceLoaderService
		 * IntentService. Passes a URI in the Intent's "data" field.
		 */
		mServiceIntent = new Intent(this, GeofenceCreatorService.class);
		Log.e(TAG, "OnCreate: mCurrentGeofences-" + mCurrentGeofences.size());
		if (!(((ForteApplication) getApplication()).sharedPrefs.getBoolean(
				ForteConstants.KEY_GEOFENCE_STARTED, false))) {
			Log.e(TAG, "Starting Intent Service");
			startService(mServiceIntent);
		} else {
			mProgress.setIndeterminate(true);
			mTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f);
		}

	}

	public void updateInitializationProgress(int progress, String description,
			boolean finishedLoading) {
		mProgress.setProgress(progress);
		mTextView.setText(description + " " + progress + "% complete");
		if (finishedLoading) {
			Log.e(TAG, "Adding Geofences, mCurrentGeofences-"
					+ mCurrentGeofences.size());
			this.addGeofences();
		}
	}

	@Override
	protected void onResume() {
		Log.e(TAG, "OnResume");
		LocalBroadcastManager.getInstance(this).registerReceiver(
				mForteGeofenceReceiver, this.mIntentFilter);
		super.onResume();
		if ((((ForteApplication) getApplication()).sharedPrefs.getBoolean(
				ForteConstants.KEY_PREFS_GEOFENCE_IMPORTED, false))) {
			
			Intent mMapIntent = new Intent(SplashScreenActivity.this,
					HomeActivity.class);
			startActivity(mMapIntent);
			SplashScreenActivity.this.finish();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// outState.putInt(ForteConstants.PROGRESS_STATUS, mProgressStatus);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {
		Log.e(TAG, "onPause");
		LocalBroadcastManager.getInstance(this).unregisterReceiver(
				mForteGeofenceReceiver);
		super.onPause();
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			try {

				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this,
						ForteConstants.CONNECTION_FAILURE_RESOLUTION_REQUEST);

				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */

			} catch (IntentSender.SendIntentException e) {

				// Log the error
				e.printStackTrace();
			}
		} else {

			// If no resolution is available, display a dialog to the user with
			// the error.
			showErrorDialog(connectionResult.getErrorCode());
		}
	}

	/**
	 * Show a dialog returned by Google Play services for the connection error
	 * code
	 * 
	 * @param errorCode
	 *            An error code returned from onConnectionFailed
	 */
	private void showErrorDialog(int errorCode) {

		// Get the error dialog from Google Play services
		Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode,
				this, ForteConstants.CONNECTION_FAILURE_RESOLUTION_REQUEST);

		// If Google Play services can provide an error dialog
		if (errorDialog != null) {

			// Create a new DialogFragment in which to show the error dialog
			ErrorDialogFragment errorFragment = new ErrorDialogFragment();

			// Set the dialog in the DialogFragment
			errorFragment.setDialog(errorDialog);

			// Show the error dialog in the DialogFragment
			errorFragment.show(getFragmentManager(), "SplashScreen Activity");
		}
	}

	/*
	 * Handle results returned to this Activity by other Activities started with
	 * startActivityForResult(). In particular, the method onConnectionFailed()
	 * in GeofenceRemover and GeofenceRequester may call
	 * startResolutionForResult() to start an Activity that handles Google Play
	 * services problems. The result of this call returns here, to
	 * onActivityResult. calls
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		// Choose what to do based on the request code
		switch (requestCode) {

		// If the request code matches the code sent in onConnectionFailed
		case ForteConstants.CONNECTION_FAILURE_RESOLUTION_REQUEST:

			switch (resultCode) {
			// If Google Play services resolved the problem
			case Activity.RESULT_OK:

				// If the request was to add geofences
				if (ForteConstants.REQUEST_TYPE.ADD == mRequestType) {

					// Toggle the request flag and send a new request
					mGeofenceRequestHandler.setInProgressFlag(false);

					// Restart the process of adding the current geofences
					mGeofenceRequestHandler.addGeofences(mCurrentGeofences);

					// If the request was to remove geofences
				} else if (ForteConstants.REQUEST_TYPE.REMOVE == mRequestType) {

					/*
					 * // Toggle the removal flag and send a new removal request
					 * mGeofenceRemover.setInProgressFlag(false);
					 * 
					 * // If the removal was by Intent if
					 * (GeofenceUtils.REMOVE_TYPE.INTENT == mRemoveType) {
					 * 
					 * // Restart the removal of all geofences for the //
					 * PendingIntent mGeofenceRemover
					 * .removeGeofencesByIntent(mGeofenceRequester
					 * .getRequestPendingIntent());
					 */

					// If the removal was by a List of geofence IDs
				} else {

					/*
					 * // Restart the removal of the geofence list
					 * mGeofenceRemover
					 * .removeGeofencesById(mGeofenceIdsToRemove);
					 */
				}
				break;

			// If any other result was returned by Google Play services
			default:

				// Report that Google Play services was unable to resolve the
				// problem.
				Log.d(TAG, getString(R.string.no_resolution));
			}

			// If any other request code was received
		default:
			// Report that this Activity received an unknown requestCode
			Log.d(TAG,
					getString(R.string.unknown_activity_request_code,
							requestCode));

			break;
		}
	}

	/**
	 * Verify that Google Play services is available before making a request.
	 * 
	 * @return true if Google Play services is available, otherwise false
	 */
	private boolean servicesConnected() {

		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);

		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d("SpalshScreenActivity", "Google Play Services Available");

			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			// Display an error dialog
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode,
					this, 0);
			if (dialog != null) {
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
				errorFragment.setDialog(dialog);
				errorFragment.show(getFragmentManager(),
						"SplashScreen Activity");
			}
			return false;
		}
	}

	/**
	 * Define a DialogFragment to display the error dialog generated in
	 * showErrorDialog.
	 */
	public static class ErrorDialogFragment extends DialogFragment {

		// Global field to contain the error dialog
		private Dialog mDialog;

		/**
		 * Default constructor. Sets the dialog field to null
		 */
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}

		/**
		 * Set the dialog to display
		 * 
		 * @param dialog
		 *            An error dialog
		 */
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}

		/*
		 * This method must return a Dialog to the DialogFragment.
		 */
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}

	private void addGeofences() {
		/*
		 * Record the request as an ADD. If a connection error occurs, the app
		 * can automatically restart the add request if Google Play services can
		 * fix the error
		 */
		mRequestType = ForteConstants.REQUEST_TYPE.ADD;
		/*
		 * Check for Google Play services. Do this after setting the request
		 * type. If connecting to Google Play services fails, onActivityResult
		 * is eventually called, and it needs to know what type of request was
		 * in progress.
		 */
		if (!servicesConnected()) {

			return;
		}
		// Start the request. Fail if there's already a request in progress
		try {
			// Try to add geofences
			mGeofenceRequestHandler.addGeofences(mCurrentGeofences);
		} catch (UnsupportedOperationException e) {
			// Notify user that previous request hasn't finished.
			Toast.makeText(this,
					R.string.add_geofences_already_requested_error,
					Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Inner class BroadcastReceiver to receive notification from
	 * ForteReceiveTransitionsService
	 * 
	 * @param context
	 * @param intent
	 */
	public class ForteGeofenceReceiver extends BroadcastReceiver {
		/*
		 * Define the required method for broadcast receivers This method is
		 * invoked when a broadcast Intent triggers the receiver
		 */
		@Override
		public void onReceive(Context context, Intent intent) {

			// Check the action code and determine what to do
			String action = intent.getAction();

			// Intent contains information about errors in adding or removing
			// geofences
			if (TextUtils.equals(action, ForteConstants.ACTION_GEOFENCE_ERROR)) {

				handleGeofenceError(context, intent);

				// Intent contains information about successful addition or
				// removal of geofences
			} else if (TextUtils.equals(action,
					ForteConstants.ACTION_GEOFENCES_ADDED)
					|| TextUtils.equals(action,
							ForteConstants.ACTION_GEOFENCES_REMOVED)) {

				handleGeofenceStatus(context, intent);

				// Intent contains information about a geofence transition
			} else if (TextUtils.equals(action,
					ForteConstants.ACTION_GEOFENCE_TRANSITION)) {

				handleGeofenceTransition(context, intent);

				// The Intent contained an invalid action
			} else if (TextUtils.equals(action, ForteConstants.ACTION_LOADER)
					|| TextUtils.equals(action,
							ForteConstants.ACTION_LOADER_DATA)) {
				updateInitializationProgress(
						intent.getIntExtra(
								ForteConstants.EXTENDED_PROGRESS_LEVEL, 0),
						intent.getStringExtra(ForteConstants.EXTENDED_PROGRESS_DESCRIPTION),
						intent.getBooleanExtra(
								ForteConstants.CLOSE_SPLASHSCREEN, true));
				if (TextUtils.equals(action, ForteConstants.ACTION_LOADER_DATA)) {
					GeofenceTransporter gt = (GeofenceTransporter) intent
							.getParcelableExtra(ForteConstants.EXTRA_PARCELABLE_DATA);
					for (ForteGeofence f : gt.getListGeofence()) {
						mCurrentGeofences.add(f.toGeofence());
					}
					((ForteApplication) getApplication()).sharedPrefs
							.edit()
							.putBoolean(
									ForteConstants.KEY_SPLASH_PACKET_RECEIVED,
									true).commit();
				}
			} else {
				Log.e(TAG, getString(R.string.invalid_action_detail, action));
				Toast.makeText(context, R.string.invalid_action,
						Toast.LENGTH_LONG).show();
			}
		}

		/**
		 * If you want to display a UI message about adding or removing
		 * geofences, put it here.
		 * 
		 * @param context
		 *            A Context for this component
		 * @param intent
		 *            The received broadcast Intent
		 */
		private void handleGeofenceStatus(Context context, Intent intent) {
			/*String msg = intent
					.getStringExtra(ForteConstants.EXTRA_GEOFENCE_STATUS);
			mTextView.setText(msg);*/
			//SplashScreenActivity.this.mProgress.setVisibility(View.GONE);
			Intent mMapIntent = new Intent(SplashScreenActivity.this,
					HomeActivity.class);
			startActivity(mMapIntent);
			SplashScreenActivity.this.finish();

		}

		/**
		 * Report geofence transitions to the UI
		 * 
		 * @param context
		 *            A Context for this component
		 * @param intent
		 *            The Intent containing the transition
		 */
		private void handleGeofenceTransition(Context context, Intent intent) {
			/*
			 * If you want to change the UI when a transition occurs, put the
			 * code here. The current design of the app uses a notification to
			 * inform the user that a transition has occurred.
			 */
		}

		/**
		 * Report addition or removal errors to the UI, using a Toast
		 * 
		 * @param intent
		 *            A broadcast Intent sent by ReceiveTransitionsIntentService
		 */
		private void handleGeofenceError(Context context, Intent intent) {
			String msg = intent
					.getStringExtra(ForteConstants.EXTRA_GEOFENCE_STATUS);
			Log.e(TAG, msg);
			Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
		}
	}

}
