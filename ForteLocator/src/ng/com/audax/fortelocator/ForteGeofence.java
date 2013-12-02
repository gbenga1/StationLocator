package ng.com.audax.fortelocator;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.Geofence;

public class ForteGeofence implements Parcelable {

	// Instance variables
	private final String mId;
	private final double mLatitude;
	private final double mLongitude;
	private final float mRadius;
	private long mExpirationDuration;
	private int mTransitionType;
	private final String mAddress;

	public long getExpirationDuration() {
		return mExpirationDuration;
	}

	public void setExpirationDuration(long mExpirationDuration) {
		this.mExpirationDuration = mExpirationDuration;
	}

	public int getTransitionType() {
		return mTransitionType;
	}

	public void setTransitionType(int mTransitionType) {
		this.mTransitionType = mTransitionType;
	}

	public String getId() {
		return mId;
	}

	public double getLatitude() {
		return mLatitude;
	}

	public double getLongitude() {
		return mLongitude;
	}
	public String getmAddress() {
		return mAddress;
	}
	public float getRadius() {
		return mRadius;
	}

	/**
	 * @param geofenceId
	 *            The Geofence's request ID
	 * @param latitude
	 *            Latitude of the Geofence's center.
	 * @param longitude
	 *            Longitude of the Geofence's center.
	 * @param radius
	 *            Radius of the geofence circle.
	 * @param expiration
	 *            Geofence expiration duration
	 * @param transition
	 *            Type of Geofence transition.
	 * 
	 **/
	public ForteGeofence(String geofenceId, double latitude, double longitude,
			float radius, long expiration, int transition, String address) {
		// Set the instance fields from the constructor
		this.mId = geofenceId;
		this.mLatitude = latitude;
		this.mLongitude = longitude;
		this.mRadius = radius;
		this.mExpirationDuration = expiration;
		this.mTransitionType = transition;
		this.mAddress=address;
	}

	/**
	 * Creates a Location Services Geofence object from a ForteGeofence.
	 * 
	 * @return A Geofence object
	 */

	public Geofence toGeofence() {
		// Build a new Geofence object
		return new Geofence.Builder().setRequestId(getId())
				.setTransitionTypes(mTransitionType)
				.setCircularRegion(getLatitude(), getLongitude(), getRadius())
				.setExpirationDuration(mExpirationDuration).build();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public ForteGeofence(Parcel in) {
		this.mId = in.readString();
		this.mLatitude = in.readDouble();
		this.mLongitude = in.readDouble();
		this.mRadius = in.readFloat();
		this.mExpirationDuration = in.readLong();
		this.mTransitionType = in.readInt();
		this.mAddress=in.readString();
	}

	@Override
	public void writeToParcel(Parcel out, int arg1) {
		out.writeString(mId);
		out.writeDouble(mLatitude);
		out.writeDouble(mLongitude);
		out.writeFloat(mRadius);
		out.writeInt(mTransitionType);
		out.writeLong(mExpirationDuration);
		out.writeString(mAddress);
	}

	static final Parcelable.Creator<ForteGeofence> CREATOR = new Parcelable.Creator<ForteGeofence>() {

		@Override
		public ForteGeofence createFromParcel(Parcel in) {
			return new ForteGeofence(in);
		}

		@Override
		public ForteGeofence[] newArray(int size) {
			return new ForteGeofence[size];
		}
	};
}
