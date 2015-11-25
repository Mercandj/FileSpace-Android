/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.mercandalli.android.filespace.extras.physics.implementation;

import android.content.Context;

import com.mercandalli.android.filespace.extras.physics.GLFragment;
import com.mercandalli.android.filespace.extras.physics.lib.IndicesVertices;
import com.mercandalli.android.filespace.extras.physics.lib.lib;

import com.mercandalli.android.filespace.R;

/**
 * Define the non trivial objects you want (maybe) use (only World.class  apply to the scene the object you use)
 *
 * @author Jonathan
 */
public enum ENUM_Obj {

    SPHERE(R.raw.obj_sphere),
    APPLE(R.raw.obj_apple),;

    private int id;
    private IndicesVertices object;

    private ENUM_Obj(int id) {
        this.id = id;
    }

    public IndicesVertices getIndicesVertices(Context context) {
        if (object == null) {
            object = lib.readMeshLocalNomalizedOpti(context, id);
            GLFragment.progress = GLFragment.progress + 1;
        }
        return new IndicesVertices(object);
    }
}
