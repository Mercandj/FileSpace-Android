/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.mercandalli.android.filespace.extras.physics.implementation;

import com.mercandalli.android.filespace.extras.physics.physics.Force;

/**
 * Define the forces you want (maybe) use (only World.class apply to the scene the forces you use)
 * For instance you have : Force (=ForceField), ForcePoint (=ForceField+Point)
 *
 * @author Jonathan
 */
public enum ENUM_Forces {

    GRAVITY(new Force(0, -1.0f, 0, 0.000003f, true)),
    GRAVITY_UP(new Force(0, 1.0f, 0, 0.000006f, true)),
    FORCE_MZ(new Force(0, 0, -1.0f, 0.0000005f, true)),
    FORCE_MX(new Force(-1.0f, 0, 0, 0.0000005f, true)),;

    public Force force;

    private ENUM_Forces(Force force) {
        this.force = force;
    }
}
