/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.mercandalli.android.filespace.extras.physics.physics;

/**
 * Physic stats : all entity is define with that
 *
 * @author Jonathan
 */
public class PhysicsObjStats {
    public float mass = 0;
    public boolean noContact = false;
    public int bounce = 100;

    public PhysicsObjStats() {
        mass = 0;
    }

}
