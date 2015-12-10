/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.mercandalli.android.apps.files.extras.physics.lib;

import com.mercandalli.android.apps.files.extras.physics.objects.Entity;

public interface IFunctionEntity {
    boolean condition(Entity entity);

    void execute(Entity entity);
}
