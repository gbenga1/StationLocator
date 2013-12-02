package ng.com.audax.fortelocator;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class GeofenceTransporter implements Parcelable {
	public List<ForteGeofence> getListGeofence() {
		return listGeofence;
	}

	private List<ForteGeofence> listGeofence;

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public GeofenceTransporter(List<ForteGeofence> list){
		this.listGeofence=list;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeTypedList(listGeofence);
		
	}
	public GeofenceTransporter(Parcel in) {
		this.listGeofence = new ArrayList<ForteGeofence>();
		in.readTypedList(listGeofence, ForteGeofence.CREATOR);
       	}
	
	static final Parcelable.Creator<GeofenceTransporter> CREATOR = new Parcelable.Creator<GeofenceTransporter>() {

		@Override
		public GeofenceTransporter createFromParcel(Parcel in) {
			return new GeofenceTransporter(in);
		}

		@Override
		public GeofenceTransporter[] newArray(int size) {
			return new GeofenceTransporter[size];
		}
	};

}
