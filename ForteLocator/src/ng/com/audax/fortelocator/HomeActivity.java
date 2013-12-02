package ng.com.audax.fortelocator;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class HomeActivity extends Activity {
	public boolean hideMenu=false;
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		this.getFragmentManager().putFragment(outState, "mapFragment", mapFragment);
		outState.putBoolean("hideMenu", hideMenu);
		super.onSaveInstanceState(outState);
	}

	final int RQS_GooglePlayServices = 1;
	private MapFragment mapFragment;
	private GoogleMap mMap;

	@Override
	protected void onResume() {
		super.onResume();

		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getApplicationContext());
		if (resultCode == ConnectionResult.SUCCESS) {
			if (mMap == null) {
				mMap = mapFragment.getMap();
				if (mMap != null) {
					mMap.setMyLocationEnabled(true);
					mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				} else {
					Log.i("HomeActivity", "Cannot get map handle");
				}

			} else {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						RQS_GooglePlayServices);
			}

		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.action_settings) {
			openPreferenceActivity();
			return true;
		} else if (itemId == R.id.showDirection) {
			showDirection();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	private void showDirection() {
		// TODO Auto-generated method stub

	}

	private void openPreferenceActivity() {
		
		FragmentManager fragmentMgr = getFragmentManager();
		FragmentTransaction fragmentTx = fragmentMgr.beginTransaction();
		FortePrefsFragment prefm = new FortePrefsFragment();
		// Replace whatever is in the fragment_container view with this
		// fragment,
		// and add the transaction to the back stack so the user can navigate
		// back
		fragmentTx.replace(R.id.map_container, prefm);
		fragmentTx.addToBackStack(null);
		fragmentTx.commit();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_activity);
		// Check that the activity is using the layout version with
		// the fragment_container FrameLayout
		if (findViewById(R.id.map_container) != null) {

			// However, if we're being restored from a previous state,
			// then we don't need to do anything and should return or else
			// we could end up with overlapping fragments.
			if (savedInstanceState != null) {
				hideMenu=savedInstanceState.getBoolean("hideMenu");
				mapFragment=(MapFragment)this.getFragmentManager().getFragment(savedInstanceState, "mapFragment");
				return;
			}
			/*GoogleMapOptions options = new GoogleMapOptions();
			options.mapType(GoogleMap.MAP_TYPE_NORMAL).compassEnabled(false)
					.rotateGesturesEnabled(false).tiltGesturesEnabled(false);
			// Create an instance of MyLocationMapFragment*/
			mapFragment = MapFragment.newInstance(/*options*/);

			// In case this activity was started with special instructions from
			// an Intent,
			// pass the Intent's extras to the fragment as arguments
			// firstFragment.setArguments(getIntent().getExtras());

			// Add the fragment to the 'fragment_container' FrameLayout
			getFragmentManager().beginTransaction().remove(mapFragment)
					.add(R.id.map_container, mapFragment).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map_menu, menu);
		if(this.hideMenu){
			menu.findItem(R.id.action_settings).setVisible(false);
			menu.findItem(R.id.showDirection).setVisible(false);
		}
		return true;
	}

}
