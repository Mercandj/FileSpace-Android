/**
 * ESIEE OpenSource Project : OpenGL
 *
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package mercandalli.com.filespace.extras.physics.lib;

import mercandalli.com.filespace.extras.physics.objects.Entity;

public interface IFunctionEntity {
	public boolean condition(Entity entity);
	public void execute(Entity entity);
}
