package mercandalli.com.filespace.extras.physics.objects;

import android.content.Context;

import mercandalli.com.filespace.extras.physics.lib.myVector3D;

public class Car extends myObject3D {
	
	public myVector3D forward;
	public float angleY = 0;

	public Car(Context context, myVector3D forward) {
		super(context);
		this.forward = forward;
	}

	public void updateForward() {
		this.forward.dX = (float) Math.cos(this.angleY);
		this.forward.dY = 0;	   
		this.forward.dZ = (float) Math.sin(this.angleY);	            			
		this.forward.normalize();
	}
}
