/**
 * ESIEE OpenSource Project : OpenGL
 *
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package mercandalli.com.filespace.physics.physics;

import java.util.List;

/**
 * Force "ponctuelle" : Physics Force 
 * @author Jonathan
 * 
 */
public class ForceToEntity extends Force {

	public int directionEntityId;

	public ForceToEntity(Force f, List<Integer> applyToObjectId, int directionEntityId) {
		super(f, applyToObjectId);
		this.directionEntityId = directionEntityId;
	}

	public ForceToEntity(Force f, int applyToObjectId, int directionEntityId) {
		super(f, applyToObjectId);
		this.directionEntityId = directionEntityId;
	}
}
