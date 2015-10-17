/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package mercandalli.com.filespace.extras.physics.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * Object's Texture use with myObject3D
 *
 * @author Jonathan
 */
public class myTexture {
    int[] texName = new int[1];

    public myTexture(Context context, int resourceId) {
        GLES30.glGenTextures(1, texName, 0);

        // Better way to get Bitmap from resource
        int textureId = resourceId;
        InputStream is = context.getResources().openRawResource(textureId);
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                // Ignore.
            }
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Buffer data = ByteBuffer.allocateDirect(width * height * 4);
        bitmap.copyPixelsToBuffer(data);
        data.position(0);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texName[0]);

        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT);

        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);

        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);

        bitmap.recycle();

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
    }

    public myTexture(Context context, int resourceId, int bump) {
        GLES30.glGenTextures(1, texName, 0);

        // Better way to get Bitmap from resource
        int textureId = resourceId;
        InputStream is = context.getResources().openRawResource(textureId);
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                // Ignore.
            }
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Buffer data = ByteBuffer.allocateDirect(width * height * 4);
        bitmap.copyPixelsToBuffer(data);
        data.position(0);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texName[0]);

        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT);

        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);

        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);

        bitmap.recycle();

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);

    }
}
