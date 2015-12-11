/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.mercandalli.android.apps.files.extras.physics.physics;

/**
 * Define thread behavior with time regulation
 *
 * @author Jonathan
 */
public class PhysicsThread extends Thread {

    private IPhysicsThreadContent content;
    private int timeLoop;
    private long tmp_time;
    private long time_sleep;
    public boolean isRunning = true;

    public PhysicsThread(IPhysicsThreadContent content, int timeLoop) {
        this.content = content;
        this.timeLoop = timeLoop;
    }

    @Override
    public void run() {
        try {
            while (isRunning) {
                tmp_time = System.currentTimeMillis();

                // Execute physic
                content.execute();

                tmp_time = System.currentTimeMillis() - tmp_time;

                // World Speed
                if ((time_sleep = timeLoop - tmp_time) > 0) {
                    sleep(time_sleep);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
