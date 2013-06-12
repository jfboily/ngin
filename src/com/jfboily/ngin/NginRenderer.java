package com.jfboily.ngin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.jfboily.ngin.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

public class NginRenderer implements GLSurfaceView.Renderer
{
	SpriteStore spriteStore;// = new SpriteStore();
	
	
	
	//private final FloatBuffer triangle1Vertices;
	private float[] viewMatrix = new float[16];
	private final String vertexShader;
	private final String fragmentShader;
	private int programHandle;
	
	private final int BYTESPERFLOAT = 4;
	
	private int mvpMatrixHandle;
	private int positionHandle;
	private int colorHandle;
	
	private float[] projectionMatrix = new float[16];
	private float[] modelMatrix = new float[16];
	private float[] mvpMatrix = new float[16];
	
	private final int positionOffset = 0;
	private final int positionDataSize = 3;
	private final int colorOffset = 3;
	private final int colorDataSize = 4;
	private final int strideBytes = 7 * BYTESPERFLOAT;
	
	//private int textureHandle;
	//private int textureHandle2;
	//private final FloatBuffer textureCoordinates;
	
	private int textureUniformHandle;
	private int textureCoordinateHandle;
	private final int textureCoordinateDataSize = 2;
	
	private final Context context;

	
	private final int SCRW = NginGame.SCRW;
	private final int SCRH = NginGame.SCRH;
	
	private int fps = 0;
	private long endTime = System.currentTimeMillis();
	private long deltaTime;
	private long startTime = System.currentTimeMillis();
	private long fpsTime = System.currentTimeMillis() + 10000;
	
	//private OGLSprite[] sprites = new OGLSprite[NBSPRITES];
	

	
	private Random random = new Random();
	
	
	private final FloatBuffer vertices;
	private final FloatBuffer texCoords;
	//private final int[] textureHandles = new int[textures.length];
	
	public NginRenderer(Context context)
	{		
		this.context = context;
		
		//load les shaders
		vertexShader = this.readTextFileFromRawResource(context, R.raw.vshader);
		fragmentShader = readTextFileFromRawResource(context, R.raw.fshader);
		
		// allocation des FloatBuffers
		vertices = ByteBuffer.allocateDirect(42 * BYTESPERFLOAT * 10000).order(ByteOrder.nativeOrder()).asFloatBuffer();
		texCoords = ByteBuffer.allocateDirect(12 * BYTESPERFLOAT * 10000).order(ByteOrder.nativeOrder()).asFloatBuffer();
		
		
	}

	//@Override
	public void onDrawFrame(GL10 gl) 
	{
		// clear l'ecran
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		
		endTime = startTime;
		startTime = System.currentTimeMillis();
		deltaTime = startTime - endTime;
		fps++;
		if(startTime > fpsTime)
		{
			fpsTime = startTime + 10000;
			Log.d("OGL1", "FPS : " + fps/10 + " (" + "???" +" sprites)");
			fps = 0;
		}
		
		// update (move) tous les sprites
		ArrayList<Sprite> al = spriteStore.getAllSprites();
		for(Sprite s : al)
		{
			s.move(deltaTime);
		}

		
		
		
		textureUniformHandle = GLES20.glGetUniformLocation(programHandle, "u_Texture");
	    textureCoordinateHandle = GLES20.glGetAttribLocation(programHandle, "a_TexCoordinate");
		mvpMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
		positionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
		colorHandle = GLES20.glGetAttribLocation(programHandle, "a_Color");
		

	    GLES20.glUniform1i(textureUniformHandle, 0);
	    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);	    
	    GLES20.glUseProgram(programHandle);	 
    
	    //vertices.position(0);
	    //texCoords.position(0);
	    
	    //Enumeration<Integer> texEnum = spriteStore.getSpritesTextures();
	    
	    
    	int tex;
    	//ArrayList<OGLSprite> al;
    	int nbSpritesRendered = 0;
	    
	    
	    //for(int i = 0; i < textureHandles.length; i++)
    	Enumeration<Texture> texs = spriteStore.getSpritesTextures();
    	while(texs.hasMoreElements())
	    {
    		Texture t = texs.nextElement();
	    	nbSpritesRendered = 0;
	    	
	    	al = spriteStore.getSpritesWithTexture(t);
	    	
	    	// reset les FloatBuffers
	    	vertices.position(0);
	    	texCoords.position(0);
	    	
	    	for(Sprite s : al)
	    	{
	    		nbSpritesRendered += s.render(vertices, texCoords);
	    	}
	    	
		    // vertex
			vertices.position(0);
			GLES20.glVertexAttribPointer(positionHandle, positionDataSize, GLES20.GL_FLOAT, false, strideBytes, vertices);
			GLES20.glEnableVertexAttribArray(positionHandle);
			
			// couleur
			vertices.position(colorOffset);
			GLES20.glVertexAttribPointer(colorHandle, colorDataSize, GLES20.GL_FLOAT, false, strideBytes, vertices);
			GLES20.glEnableVertexAttribArray(colorHandle);
			
			// textures
			texCoords.position(0);
	        GLES20.glVertexAttribPointer(textureCoordinateHandle, textureCoordinateDataSize, GLES20.GL_FLOAT, false, 0, texCoords);
	        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);
			
	        // matrices
	        Matrix.setIdentityM(modelMatrix, 0);
			Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);
			Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
			
			GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
			
			// set texture
			//GLES20.glBindTexture(GLES20.GL_TEXTURE0, 1);

		    
			t.bind();
			
			// DRAW!!
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, nbSpritesRendered * 4);
			
			//GLES20.glBindTexture(GLES20.GL_TEXTURE0, 1);
	    }	    
	}

	//@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) 
	{
		GLES20.glViewport(0,  0,  width, height);
		
		final float ratio = (float)width / height;
		final float left = -ratio * 320.0f;
		final float right = ratio * 320.0f;
		final float bottom = -320.0f;
		final float top = 320.0f;
		final float near = 1.0f;
		final float far = 10.0f;
		
		//Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
		//Matrix.frustumM(projectionMatrix, 0, -width/2, width/2, width/2, -width/2, near, far);
		Matrix.orthoM(projectionMatrix, 0, 0, SCRW, 0, SCRH, 0.0f, 2000.0f);
		Log.d("onSurfaceChanged", "Width:"+width+" Height:"+height+" Left:"+left+" Right:"+right);
	}

	//@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) 
	{
		
		spriteStore = ((NginGame)context).getSpriteStore();
		
		
		// TODO Auto-generated method stub
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		
		final float eyeX = 0.0f;
		final float eyeY = 0.0f;
		final float eyeZ = 1.5f;
		
		final float centerX = 0.0f;
		final float centerY = 0.0f;
		final float centerZ = -0.5f;
		
		
		final float upX = 0.0f;
		final float upY = 1.0f;
		final float upZ = 0.0f;
		
		Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
		
		int vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
		
		if(vertexShaderHandle != 0)
		{
			GLES20.glShaderSource(vertexShaderHandle, vertexShader);
			GLES20.glCompileShader(vertexShaderHandle);
			
			final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
			
			if(compileStatus[0] == 0)
			{
				GLES20.glDeleteShader(vertexShaderHandle);
				vertexShaderHandle = 0;
			}
		}
		
		if(vertexShaderHandle == 0)
		{
			throw new RuntimeException("Erreur a la creation du vertex shader");
		}
		
		int fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
		
		if(fragmentShaderHandle != 0)
		{
			GLES20.glShaderSource(fragmentShaderHandle, fragmentShader);
			GLES20.glCompileShader(fragmentShaderHandle);
			
			final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(fragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
			
			if(compileStatus[0] == 0)
			{
				GLES20.glDeleteShader(fragmentShaderHandle);
				fragmentShaderHandle = 0;
			}
		}
		
		if(fragmentShaderHandle == 0)
		{
			throw new RuntimeException("Erreur a la creation du fragment shader");
		}
		
		programHandle = GLES20.glCreateProgram();
		
		if(programHandle != 0)
		{
			GLES20.glAttachShader(programHandle, vertexShaderHandle);
			GLES20.glAttachShader(programHandle, fragmentShaderHandle);
			
			GLES20.glBindAttribLocation(programHandle, 0, "a_Position");
			GLES20.glBindAttribLocation(programHandle, 1, "a_Color");
			
			GLES20.glLinkProgram(programHandle);
			
			final int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
			
			if(linkStatus[0] == 0)
			{
				GLES20.glDeleteProgram(programHandle);
				programHandle = 0;
			}
			
			if(programHandle == 0)
			{
				throw new RuntimeException("Erreur a la creation du programme GLES20");
			}
		}
		

		
		
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		GLES20.glDisable(GLES20.GL_ALPHA);
		
		((NginGame)context).loadTextures();
		
		if(NginGame.getSpriteStore().getAllSprites().size() == 0)
		{
			((NginGame)context).initSprites();
		}
		
		NginGame.glRendererReady = true;
	}
	
	
//	public static int loadTexture(final Context context, final int resourceId)
//	{
//	    final int[] textureHandle = new int[1];
//	 
//	    GLES20.glGenTextures(1, textureHandle, 0);
//	 
//	    if (textureHandle[0] != 0)
//	    {
//	        final BitmapFactory.Options options = new BitmapFactory.Options();
//	        options.inScaled = false;   // No pre-scaling
//	 
//	        // Read in the resource
//	        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
//	 
//	        // Bind to the texture in OpenGL
//	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
//	 
//	        // Set filtering
//	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
//	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
//	 
//	        // Load the bitmap into the bound texture.
//	        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
//	 
//	        // Recycle the bitmap, since its data has been loaded into OpenGL.
//	        bitmap.recycle();
//	    }
//	 
//	    if (textureHandle[0] == 0)
//	    {
//	        throw new RuntimeException("Error loading texture.");
//	    }
//	 
//	    int e = GLES20.glGetError();
//	    return textureHandle[0];
//	}
//	
	public static String readTextFileFromRawResource(final Context context,
            final int resourceId)
    {
        final InputStream inputStream = context.getResources().openRawResource(
                resourceId);
        final InputStreamReader inputStreamReader = new InputStreamReader(
                inputStream);
        final BufferedReader bufferedReader = new BufferedReader(
                inputStreamReader);
 
        String nextLine;
        final StringBuilder body = new StringBuilder();
 
        try
        {
            while ((nextLine = bufferedReader.readLine()) != null)
            {
                body.append(nextLine);
                body.append('\n');
            }
        }
        catch (IOException e)
        {
            return null;
        }
 
        return body.toString();
    }

}
