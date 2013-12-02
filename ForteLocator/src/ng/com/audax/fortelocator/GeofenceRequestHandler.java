package ng.com.audax.fortelocator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener;
import com.google.android.gms.location.LocationStatusCodes;

public class GeofenceRequestHandler implements OnAddGeofencesResultListener,
		ConnectionCallbacks, OnConnectionFailedListener {
	private final String TAG = "GeofenceRequestHandler";
	// Storage for a reference to the calling client
	private final Activity mActivity;

	// Stores the PendingIntent used to send geofence transitions back to the
	// app
	private PendingIntent mGeofencePendingIntent;

	// Stores the current list of geofences
	private ArrayList<Geofence> mCurrentGeofences;

	// Stores the current instantiation of the location client
	private LocationClient mLocationClient;

	/*
	 * Flag that indicates whether an add or remove request is underway. Check
	 * this flag before attempting to start a new request.
	 */
	private boolean mInProgress;

	public GeofenceRequestHandler(Activity activityContext) {
		// Save the context
		mActivity = activityContext;

		// Initialize the globals to null
		mGeofencePendingIntent = null;
		mLocationClient = null;
		mInProgress = false;
	}

	/**
	 * Set the "in progress" flag from a caller. This allows callers to re-set a
	 * request that failed but was later fixed.
	 * 
	 * @param flag
	 *            Turn the in progress flag on or off.
	 */
	public void setInProgressFlag(boolean flag) {
		// Set the "In Progress" flag.
		mInProgress = flag;
	}

	/**
	 * Get the current in progress status.
	 * 
	 * @return The current value of the in progress flag.
	 */
	public boolean getInProgressFlag() {
		return mInProgress;
	}

	/**
	 * Returns the current PendingIntent to the caller.
	 * 
	 * @return The PendingIntent used to create the current set of geofences
	 */
	public PendingIntent getRequestPendingIntent() {
		return createRequestPendingIntent();
	}

	public void addGeofences(List<Geofence> geofences)
			throws UnsupportedOperationException {

		/*
		 * Save the geofences so that they can be sent to Location Services once
		 * the connection is available.
		 */
		mCurrentGeofences = (ArrayList<Geofence>) geofences;

		// If a request is not already in progress
		if (!mInProgress) {

			// Toggle the flag and continue
			mInProgress = true;

			// Request a connection to Location Services
			requestConnection();

			// If a request is in progress
		} else {

			// Throw an exception and stop the request
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Request a connection to Location Services. This call returns immediately,
	 * but the request is not complete until onConnected() or
	 * onConnectionFailure() is called.
	 */
	private void requestConnection() {
		getLocationClient().connect();
	}

	/**
	 * Get the current location client, or create a new one if necessary.
	 * 
	 * @return A LocationClient object
	 */
	private GooglePlayServicesClient getLocationClient() {
		if (mLocationClient == null) {

			mLocationClient = new LocationClient(mActivity, this, this);
		}
		return mLocationClient;

	}

	/**
	 * Once the connection is available, send a request to add the Geofences
	 */
	private void continueAddGeofences() {

		// Get a PendingIntent that Location Services issues when a geofence
		// transition occurs
		mGeofencePendingIntent = createRequestPendingIntent();

		// Send a request to add the current geofences
		mLocationClient.addGeofences(mCurrentGeofences, mGeofencePendingIntent,
				this);
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		// Turn off the request flag
		mInProgress = false;

		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {

			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(mActivity,
						ForteConstants.CONNECTION_FAILURE_RESOLUTION_REQUEST);

				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}

			/*
			 * If no resolution is available, put the error code in an error
			 * Intent and broadcast it back to the main Activity. The Activity
			 * then displays an error dialog. is out of date.
			 */
		} else {

			Intent errorBroadcastIntent = new Intent(
					ForteConstants.ACTION_CONNECTION_ERROR);
			errorBroadcastIntent.addCategory(
					ForteConstants.CATEGORY_LOCATION_SERVICES).putExtra(
					ForteConstants.EXTRA_CONNECTION_ERROR_CODE,
					connectionResult.getErrorCode());
			LocalBroadcastManager.getInstance(mActivity).sendBroadcast(
					errorBroadcastIntent);
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		// If debugging, log the connection
		Log.d(TAG, mActivity.getString(R.string.connected));
		// Continue adding the geofences
		continueAddGeofences();
	}

	@Override
	public void onDisconnected() {
		// Turn off the request flag
		mInProgress = false;

		// In debug mode, log the disconnection
		Log.d(TAG, mActivity.getString(R.string.disconnected));

		// Destroy the current location client
		mLocationClient = null;
	}

	@Override
	public void onAddGeofencesResult(int statusCode, String[] geofenceRequestIds) {
		// Create a broadcast Intent that notifies other components of success
		// or failure
		Intent broadcastIntent = new Intent();
		Log.e(TAG, Arrays.toString(geofenceRequestIds));

		// Temp storage for messages
		String msg;

		// If adding the geocodes was successful
		if (LocationStatusCodes.SUCCESS == statusCode) {

			// Create a message containing all the geofence IDs added.
			msg = mActivity.getString(R.string.add_geofences_result_success,
					Arrays.toString(geofenceRequestIds));

			// In debug mode, log the result
			Log.d(TAG, msg);

			// Create an Intent to broadcast to the app
			broadcastIntent.setAction(ForteConstants.ACTION_GEOFENCES_ADDED)
					.addCategory(ForteConstants.CATEGORY_LOCATION_SERVICES)
					.putExtra(ForteConstants.EXTRA_GEOFENCE_STATUS, msg);
			
			// If adding the geofences failed
		} else {

			/*
			 * Create a message containing the error code and the list of
			 * geofence IDs you tried to add
			 */
			msg = mActivity.getString(R.string.add_geofences_result_failure,
					statusCode, Arrays.toString(geofenceRequestIds));

			// Log an error
			Log.e(TAG, msg);

			// Create an Intent to broadcast to the app
			broadcastIntent.setAction(ForteConstants.ACTION_GEOFENCE_ERROR)
					.addCategory(ForteConstants.CATEGORY_LOCATION_SERVICES)
					.putExtra(ForteConstants.EXTRA_GEOFENCE_STATUS, msg);
		}

		// Broadcast whichever result occurred
		LocalBroadcastManager.getInstance(mActivity).sendBroadcast(
				broadcastIntent);

		// Disconnect the location client
		requestDisconnection();
		((ForteApplication) mActivity.getApplication()).sharedPrefs.edit()
				.putBoolean(ForteConstants.KEY_PREFS_GEOFENCE_IMPORTED, true).commit();
	}

	/**
	 * Get a location client and disconnect from Location Services
	 */
	private void requestDisconnection() {

		// A request is no longer in progress
		mInProgress = false;

		getLocationClient().disconnect();
	}

	/**
	 * Get a PendingIntent to send with the request to add Geofences. Location
	 * Services issues the Intent inside this PendingIntent whenever a geofence
	 * transition occurs for the current list of geofences.
	 * 
	 * @return A PendingIntent for the IntentService that handles geofence
	 *         transitions.
	 */
	private PendingIntent createRequestPendingIntent() {

		// If the PendingIntent already exists
		if (null != mGeofencePendingIntent) {

			// Return the existing intent
			return mGeofencePendingIntent;

			// If no PendingIntent exists
		} else {

			// Create an Intent pointing to the IntentService
			Intent intent = new Intent(mActivity,
					ForteReceiveTransitionsService.class);
			/*
			 * Return a PendingIntent to start the IntentService. Always create
			 * a PendingIntent sent to Location Services with
			 * FLAG_UPDATE_CURRENT, so that sending the PendingIntent again
			 * updates the original. Otherwise, Location Services can't match
			 * the PendingIntent to requests made with it.
			 */
			return PendingIntent.getService(mActivity, 0, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
		}
	}

}
