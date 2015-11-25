/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.mercandalli.android.filespace.extras.physics.physics;

import java.util.List;

/**
 * Force "ponctuelle" : Physics Force
 *
 * @author Jonathan
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
