/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.mercandalli.android.apps.files.extras.physics.objects;

import com.mercandalli.android.apps.files.extras.physics.lib.WayPosition;
import com.mercandalli.android.apps.files.extras.physics.lib.myVector3D;
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

    public myVector3D edgeVerticeMin = null;
    public myVector3D edgeVerticeMax = null;

    public myVector3D position = new myVector3D(0, 0, 0);
    public myVector3D velocity = new myVector3D(0, 0, 0);
    public myVector3D acceleration = new myVector3D(0, 0, 0);

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
    protected myVector3D sum_force = new myVector3D(0, 0, 0);

    public abstract void computeForces(EntityGroup contacts);

    public abstract void applySumForces(EntityGroup contacts);

    protected List<Force> forces = new ArrayList<Force>();

    public abstract void addForce(Force force);

    protected List<Entity> entitiesContact;

}
