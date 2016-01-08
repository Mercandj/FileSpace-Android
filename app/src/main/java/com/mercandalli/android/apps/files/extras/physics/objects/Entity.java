/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.mercandalli.android.apps.files.extras.physics.objects;

import com.mercandalli.android.apps.files.extras.physics.lib.WayPosition;
import com.mercandalli.android.apps.files.extras.physics.lib.Vector3D;
import com.mercandalli.android.apps.files.extras.physics.physics.Force;
import com.mercandalli.android.apps.files.extras.physics.physics.PhysicsObjStats;

import java.util.ArrayList;
import java.util.List;

/**
 * Define the object's attributes
 *
 * @author Jonathan
 */
public abstract class Entity {

    protected static int count = 1;

    public int id;                    // identify entity in an EntityGroup

    public PhysicsObjStats physic = new PhysicsObjStats();

    public Vector3D edgeVerticeMin = null;
    public Vector3D edgeVerticeMax = null;

    public Vector3D position = new Vector3D(0, 0, 0);
    public Vector3D velocity = new Vector3D(0, 0, 0);
    public Vector3D acceleration = new Vector3D(0, 0, 0);

    public abstract void teleport(float x, float y, float z);

    public abstract void translate(float x, float y, float z);

    public abstract void rotate(float a, float x, float y, float z);

    public abstract void scale(float rate);

    public abstract Entity isInside(Entity object);

    public abstract void draw(float[] _mpMatrix, float[] _mvMatrix);

    // Move
    public WayPosition repetedWayPosition;

    public abstract void translateRepetedWayPosition();

    // Physics
    public boolean contactEnable = true;
    protected Vector3D sum_force = new Vector3D(0, 0, 0);

    public abstract void computeForces(EntityGroup contacts);

    public abstract void applySumForces(EntityGroup contacts);

    protected List<Force> forces = new ArrayList<Force>();

    public abstract void addForce(Force force);

    protected List<Entity> entitiesContact;

}
