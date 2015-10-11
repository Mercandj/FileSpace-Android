/**
 * ESIEE OpenSource Project : OpenGL
 *
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package mercandalli.com.filespace.physics.implementation;

import android.content.Context;

import mercandalli.com.filespace.physics.GLFragment;
import mercandalli.com.filespace.R;
import mercandalli.com.filespace.physics.lib.IndicesVertices;
import mercandalli.com.filespace.physics.lib.lib;

/**
 * Define the non trivial objects you want (maybe) use (only World.class  apply to the scene the object you use)
 * @author Jonathan
 *
 */
public enum ENUM_Obj {

	SPHERE		(R.raw.obj_sphere),
	APPLE		(R.raw.obj_apple),
	;
	
	private int id;
	private IndicesVertices object;
	
	private ENUM_Obj(int id) {
		this.id = id;
	}
	
	public IndicesVertices getIndicesVertices(Context context) {
		if(object==null) {
			object = lib.readMeshLocalNomalizedOpti(context, id);
			GLFragment.progress = GLFragment.progress+1;
		}
		return new IndicesVertices(object);
	}
}
