/**
 * ESIEE OpenSource Project : OpenGL
 *
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package mercandalli.com.filespace.physics.lib;

import mercandalli.com.filespace.physics.objects.Entity;

public interface IFunctionEntity {
	public boolean condition(Entity entity);
	public void execute(Entity entity);
}
