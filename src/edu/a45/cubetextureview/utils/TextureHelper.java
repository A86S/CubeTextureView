package edu.a45.cubetextureview.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLUtils.texImage2D;

/**
 * Created by w1 on 12/11/2014.
 */

/**
 * Handle loading texture
 */
public class TextureHelper {
    private static final String TAG = "TextureHelper";

    /**
     * create texture handle from resources with label
     * @param context activity context
     * @param resourceId id of image resource as background
     * @param text label to put on
     * @param width target width
     * @param height target height
     * @param useDimension use width/height to normalize text
     * @return texture OpenGL handle
     */
    public static int createTexture(
            Context context, int resourceId, String text,
            int width, int height, boolean useDimension
    ){
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);

        if(textureObjectIds[0] == 0){
            if(LoggerConfig.ON){
                Log.w(TAG, "Could not generate a new OpenGL texture object.");
            }
            return 0;
        }
        if(!useDimension){
            width = 256;
            height = 256;
        }
        // create bitmap with background and label
        Bitmap bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        bitmap.eraseColor(0);
        Drawable background = context.getResources().getDrawable(resourceId);
        background.setBounds(0,0,width,height);
        background.draw(canvas);
        Paint textPaint = new Paint();
        textPaint.setTextSize(32);
        textPaint.setAntiAlias(true);
        textPaint.setARGB(0xff,0x00,0xff,0x00);
        Rect bounds = new Rect();
        textPaint.getTextBounds(text,0,text.length(),bounds);
        int x = bitmap.getWidth()/2-bounds.width()/2;
        int y = bitmap.getHeight()/2+bounds.height()/2;
        canvas.drawText(text,x,y,textPaint);

        if(bitmap == null){
            if(LoggerConfig.ON){
                Log.w(TAG, "Resource ID "+resourceId+" could not be decoded.");
            }
            glDeleteTextures(1, textureObjectIds,0);
            return 0;
        }
        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);

        // filtering must be set
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // load
        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

        // this call may produce error in some device
        // that does not support non square mipmap generation
        // if error were to happen because of HardwareMipGen
        // squash the image source into square.
        glGenerateMipmap(GL_TEXTURE_2D);

        // recycle since data already loaded
        bitmap.recycle();

        // unbind from the texture
        // so that we don't accidentally make changes to this texture
        glBindTexture(GL_TEXTURE_2D, 0);

        return textureObjectIds[0];
    }
}
