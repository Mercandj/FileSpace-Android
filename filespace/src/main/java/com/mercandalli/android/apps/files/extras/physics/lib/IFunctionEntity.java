/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.mercandalli.android.apps.files.extras.physics.lib;

import com.mercandalli.android.apps.files.extras.physics.objects.Entity;

public interface IFunctionEntity {
    public boolean condition(Entity entity);

    public void execute(Entity entity);
}
