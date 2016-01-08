/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.mercandalli.android.apps.files.extras.physics.objects;

import android.content.Context;

import com.mercandalli.android.apps.files.extras.physics.physics.Force;
import com.mercandalli.android.apps.files.extras.physics.physics.PhysicsConst;

import java.util.ArrayList;
import java.util.List;

/**
 * Use to apply transformations : all in one
 *
 * @author Jonathan
 */
public class EntityGroup extends Entity {

    public List<Entity> entities;
    public final Context context;

    public EntityGroup(final Context context) {
        this.context = context;
        this.entities = new ArrayList<>();
    }

    public EntityGroup(final Context context, List<Entity> entities) {
        this.context = context;
        this.entities = entities;
        if (this.entities == null) {
            this.entities = new ArrayList<>();
        }
    }

    public void init() {
    }

    public int addEntity(Entity entity) {
        entities.add(entity);
        return entity.id;
    }

    public Entity getEntity(int id) {
        if (id < entities.size()) {
            return entities.get(id);
        }
        return null;
    }

    public Entity getEntityById(int id) {
        for (Entity ent : entities) {
            if (ent.id == id) {
                return ent;
            }
        }
        return null;
    }

    @Override
    public Entity isInside(Entity object) {
        Entity res = null;
        l:
        for (Entity entity : this.entities) {
            if ((res = entity.isInside(object)) != null) {
                break l;
            }
        }
        return res;
    }

    @Override
    public void teleport(float x, float y, float z) {
        for (Entity entity : this.entities) {
            entity.teleport(x, y, z);
        }
    }

    @Override
    public void translate(float x, float y, float z) {
        for (Entity entity : this.entities) {
            entity.translate(x, y, z);
        }
    }

    @Override
    public void rotate(float a, float x, float y, float z) {
        // TODO Auto-generated method stub
    }

    @Override
    public void draw(float[] _mpMatrix, float[] _mvMatrix) {
        for (Entity entity : this.entities) {
            entity.draw(_mpMatrix, _mvMatrix);
        }
    }

    @Override
    public void scale(float rate) {
        for (Entity entity : this.entities) {
            entity.scale(rate);
        }
    }

    @Override
    public void translateRepetedWayPosition() {
        for (Entity entity : this.entities) {
            entity.translateRepetedWayPosition();
        }
    }

    @Override
    public void computeForces(EntityGroup contacts) {
        for (Entity entity : this.entities) {
            entity.computeForces(contacts);
        }
    }

    @Override
    public void applySumForces(EntityGroup contacts) {
        for (Entity entity : this.entities) {
            entity.applySumForces(contacts);
        }
    }

    @Override
    public void addForce(Force force) {
        for (Entity entity : this.entities) {
            entity.forces.add(force);
        }
    }

    public void separeObject() {
        for (Entity ent : entities) {
            Entity entityContact = this.isInside(ent);
            if (entityContact != null) {
                if (PhysicsConst.REAL_LOOP_TIME * (this.velocity.dY + PhysicsConst.REAL_LOOP_TIME * this.acceleration.dY / 2) > 0) {
                    translate(0,
                            Math.abs(entityContact.position.dY - this.position.dY),
                            0);
                } else {
                    translate(0,
                            -Math.abs(entityContact.position.dY - this.position.dY),
                            0);
                }
            }
        }
    }
}
