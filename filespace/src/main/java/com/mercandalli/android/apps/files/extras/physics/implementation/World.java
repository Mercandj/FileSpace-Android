/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.mercandalli.android.apps.files.extras.physics.implementation;

import android.content.Context;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.extras.physics.lib.myVector3D;
import com.mercandalli.android.apps.files.extras.physics.objects.Camera;
import com.mercandalli.android.apps.files.extras.physics.objects.EntityGroup;
import com.mercandalli.android.apps.files.extras.physics.objects.myObject3D;
import com.mercandalli.android.apps.files.extras.physics.objects.myTexture;
import com.mercandalli.android.apps.files.extras.physics.physics.Force;
import com.mercandalli.android.apps.files.extras.physics.physics.ForceToEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * SPECIFIC world : Place and define all objects
 *
 * @author Jonathan
 */
public class World extends EntityGroup {

    Camera camera;
    public static int carId;

    public World(Context context, Camera camera) {
        super(context);
        this.camera = camera;
    }

    public class Planet {
        public myVector3D position;
        public float vZ;
        public int resourceId;

        public Planet(myVector3D position, float vZ, int resourceId) {
            this.position = position;
            this.vZ = vZ;
            this.resourceId = resourceId;
        }
    }

    @Override
    public void init() {

        /********* INIT OBJECTS *********/

        /*
        List<Integer> objVerticalGravityId = new ArrayList<>();

		GLFragment.progress_length = (ENUM_Obj.values().length);
		
		int borne_apple = 1; float step_apple = 3.0f;
		myObject3D sphere = null;
		for(int z=0; z<step_apple; z++) {		
			for(float i=-borne_apple; i<=borne_apple; i++) {
				for(float j=-borne_apple; j<=borne_apple; j++) {
                    sphere = new myObject3D(context);
                    sphere.readMeshLocal(ENUM_Obj.SPHERE.getIndicesVertices(context));
                    sphere.scale(0.2f);
                    sphere.computeNormals();
                    sphere.computeSphereTexture();
                    sphere.createBuffers();
                    sphere.translate(0.0f+i, (j+i)/3+5.0f + z*2.0f, -3.5f+j);
					if(z%3==0)
                        sphere.texture = new myTexture(context, R.drawable.color_green);
					else if(z%3==1)
                        sphere.texture = new myTexture(context, R.drawable.color_red);
					else
                        sphere.texture = new myTexture(context, R.drawable.color_white);
                    sphere.physic.mass = 0.11f;
                    objVerticalGravityId.add(this.addEntity(sphere));
				}			
			}
		}
		*/

        List<Integer> objSunGravityId = new ArrayList<>();


        myObject3D sun = new myObject3D(context);
        sun.readMeshLocal(ENUM_Obj.SPHERE.getIndicesVertices(context));
        sun.scale(10.0f);
        sun.computeNormals();
        sun.computeSphereTexture();
        sun.createBuffers();
        sun.translate(0.0f, 30.0f, -30.0f);
        sun.texture = new myTexture(context, R.drawable.color_yellow);
        sun.physic.mass = 0f;
        this.addEntity(sun);


        List<Planet> planets = new ArrayList<>();
        planets.add(new Planet(new myVector3D(32.0f, 30.0f, 25.0f), -0.020f, R.drawable.color_green));
        planets.add(new Planet(new myVector3D(35.0f, 30.0f, 5.0f), -0.025f, R.drawable.color_blue));
        planets.add(new Planet(new myVector3D(30.0f, 30.0f, 33.0f), -0.029f, R.drawable.color_red));
        planets.add(new Planet(new myVector3D(32.0f, 30.0f, 26.0f), -0.012f, R.drawable.color_white));
        planets.add(new Planet(new myVector3D(34.0f, 30.0f, 28.0f), -0.01f, R.drawable.color_yellow));
        planets.add(new Planet(new myVector3D(33.0f, 30.0f, 0.0f), -0.007f, R.drawable.color_purple));

        for (Planet planet : planets) {
            myObject3D earth = new myObject3D(context);
            earth.readMeshLocal(ENUM_Obj.SPHERE.getIndicesVertices(context));
            earth.scale(1.5f);
            earth.computeNormals();
            earth.computeSphereTexture();
            earth.createBuffers();
            earth.translate(planet.position);
            earth.texture = new myTexture(context, planet.resourceId);
            earth.physic.mass = 3f;
            earth.velocity = new myVector3D(0, 0, planet.vZ);
            objSunGravityId.add(this.addEntity(earth));
        }

        /*
        myObject3D apple = new myObject3D(context);
		apple.readMeshLocal(ENUM_Obj.APPLE.getIndicesVertices(context));
		apple.scale(0.5f);
		apple.computeNormals();
		apple.computeSphereTexture();
		apple.createBuffers();
		apple.translate(0.0f, 1.0f, 0.0f);
		apple.texture = new myTexture(context, R.drawable.color_green);
		apple.physic.mass = 0.0f;		
		apple.repetedWayPosition = new WayPosition();		
		apple.repetedWayPosition.initCubeWabHorizontal(0, 2.0f, 0, 20.0f, 0.4f, true);
        this.addEntity(apple);
        
        apple = new myObject3D(context);
        apple.readMeshLocal(ENUM_Obj.APPLE.getIndicesVertices(context));
        apple.scale(0.5f);
        apple.computeNormals();
        apple.computeSphereTexture();
		apple.createBuffers();
		apple.translate(0.0f, 1.0f, 0.0f);
		apple.texture = new myTexture(context, R.drawable.color_red);
		apple.physic.mass = 0.0f;		
		apple.repetedWayPosition = new WayPosition();		
		apple.repetedWayPosition.initCubeWabHorizontal(0,2.0f,0, 20.0f, 0.4f, false);
        this.addEntity(apple);
        */

        myObject3D floor = new myObject3D(context);
        floor.generateGrid(100, 100);
        floor.scale(50f);
        floor.computeNormals();
        floor.computePlaneTexture();
        floor.createBuffers();
        floor.rotate(-90, 1, 0, 0);
        floor.texture = new myTexture(context, R.drawable.color_white);
        this.addEntity(floor);


        /********* INIT FORCES *********/

        //this.addForce(new Force(ENUM_Forces.GRAVITY.force, objVerticalGravityId));

        //this.addForce(new Force(new Force(0, 0, -1.0f, 0.000003f, true), earth.id));

        this.addForce(new ForceToEntity(new Force(0, 0, -1.0f, 0.000008f, true), objSunGravityId, sun.id));

    }
}
