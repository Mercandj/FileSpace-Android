/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.mercandalli.android.apps.files.extras.physics.physics;

import com.mercandalli.android.apps.files.extras.physics.lib.Predicat;
import com.mercandalli.android.apps.files.extras.physics.lib.MyVector3D;
import com.mercandalli.android.apps.files.extras.physics.objects.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Force like gravity
 * Physics force : One direction, non specific application point (use ForcePoint if you want)
 *
 * @author Jonathan
 */
public class Force {

    // Apply to this objects (all if null)
    private List<Integer> applyToObjectId;

    // Force vector : contains directions and intensity
    protected MyVector3D vector;

    // Apply to object with mass
    public boolean dotMass = false;

    public Predicat isApplyForce = new Predicat() {
        @Override
        public MyVector3D isTrue(Entity entity) {
            return new MyVector3D(1.0f, 1.0f, 1.0f);
        }
    };

    public MyVector3D getForceV(Entity entity) {
        return vector.mult(isApplyForce.isTrue(entity));
    }

    public Force(float intensity, boolean dotMass) {
        super();
        this.vector = new MyVector3D(intensity, intensity, intensity);
        this.dotMass = dotMass;
    }

    public Force(float intensity, boolean dotMass, Predicat isApplyForce) {
        super();
        this.vector = new MyVector3D(intensity, intensity, intensity);
        this.dotMass = dotMass;
        this.isApplyForce = isApplyForce;
    }

    public Force(float x, float y, float z, float intensity, boolean dotMass) {
        super();
        this.vector = new MyVector3D(x * intensity, y * intensity, z * intensity);
        this.dotMass = dotMass;
    }

    public Force(float x, float y, float z, float intensity, boolean dotMass, Predicat isApplyForce) {
        super();
        this.vector = new MyVector3D(x * intensity, y * intensity, z * intensity);
        this.dotMass = dotMass;
        this.isApplyForce = isApplyForce;
    }

    public Force(Force f, List<Integer> applyToObjectId) {
        super();
        this.applyToObjectId = applyToObjectId;
        this.vector = f.vector;
        this.dotMass = f.dotMass;
    }

    public Force(Force f, int applyToObjectId) {
        super();
        this.applyToObjectId = new ArrayList<>();
        this.applyToObjectId.add(applyToObjectId);
        this.vector = f.vector;
        this.dotMass = f.dotMass;
    }

    public boolean isApplied(int idEntity) {
        if (this.applyToObjectId == null) {
            return true;
        }
        for (Integer i : this.applyToObjectId) {
            if (i == idEntity) {
                return true;
            }
        }
        return false;
    }
}
