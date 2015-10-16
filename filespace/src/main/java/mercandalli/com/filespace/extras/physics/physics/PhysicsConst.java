/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package mercandalli.com.filespace.extras.physics.physics;

/**
 * Global world physic constants
 * @author Jonathan
 *
 */
public class PhysicsConst {

    public final static int WORLD_TIME_LOOP = 46; // m second	<10 Attention
    public final static int WORLD_PCT_TIME_SPEED = 100;

    public final static int WORLD_PCT_GRAVITY = 100;

    public final static int REAL_LOOP_TIME = (int) (WORLD_TIME_LOOP * (WORLD_PCT_TIME_SPEED * 1.0) / 100.0);

    public final static boolean HIGH_CAMERA_SPEED_TRANSLATION = true;

}
