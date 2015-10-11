package mercandalli.com.filespace.physics.lib;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.filespace.physics.objects.Entity;

public abstract class Predicat {
	public List<Integer> list_int = new ArrayList<Integer>();
	public Entity entity = null;
	
	public abstract myVector3D isTrue(Entity entity);
}
