package com.jfboily.ngin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class Texture 
{
	public int width, height;
	public int id;
	
	
	public Texture(int resID, Context context)
	{
		id = loadTexture(context, resID);
	}
	
	public void bind()
	{
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id);
	}
	
	public void unbind()
	{
		
	}

	private int loadTexture(final Context context, final int resourceId)
	{
	    final int[] textureHandle = new int[1];
	 
	    GLES20.glGenTextures(1, textureHandle, 0);
	 
	    if (textureHandle[0] != 0)
	    {
	        final BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inScaled = false;   // No pre-scaling
	 
	        // Read in the resource
	        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
	 
	        this.width = bitmap.getWidth();
	        this.height = bitmap.getHeight();
	        
	        // Bind to the texture in OpenGL
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
	 
	        // Set filtering
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
	 
	        // Load the bitmap into the bound texture.
	        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
	 
	        // Recycle the bitmap, since its data has been loaded into OpenGL.
	        bitmap.recycle();
	    }
	 
	    if (textureHandle[0] == 0)
	    {
	        throw new RuntimeException("Error loading texture.");
	    }
	 
	    int e = GLES20.glGetError();
	    return textureHandle[0];
	}
	
	float[] getTexRegion(int x, int y, int width, int height)
	{
		float x1, x2, y1, y2;
		
		x1 = (float)x / (float)this.width;
		y1 = (float)y / (float)this.height;
		
		x2 = (float)(x + width) / (float)this.width;
		y2 = (float)(y + height) / (float)this.height;
		
		return new float[]{x1, y1, x2, y2};
	}
}
