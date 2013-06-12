package com.jfboily.ngin;

import java.util.Random;

import com.jfboily.ngin.R;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class NginGame extends Activity 
{
	private GLSurfaceView glSurfaceView;
	private LogicThread logicThread = new LogicThread(this);
	private NginRenderer renderer;
	private static SpriteStore spriteStore;
	
	public static final int SCRW = 1280;
	public static final int SCRH = 800;
	public static boolean glRendererReady = false;
	
	private static final int NBSPRITES = 500;
	private static final int[] textures = new int[]{R.drawable.etoile, R.drawable.balle};
	
	private Texture tex1, tex2;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);
        renderer = new NginRenderer(this);
        glSurfaceView.setRenderer(renderer);
        spriteStore = new SpriteStore();
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        glRendererReady = false;
        setContentView(glSurfaceView);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_olg1, menu);
        return true;
    }
    
    @Override
    protected void onResume()
    {
    	super.onResume();
    	glSurfaceView.onResume();
    	if(!logicThread.isAlive())
    	{
    		logicThread = new LogicThread(this);
	    	logicThread.setRunning(true);
	    	logicThread.start();
    	}
    }
    
    @Override
    protected void onPause()
    {
    	super.onPause();
    	glSurfaceView.onPause();
    	NginGame.glRendererReady = false;
    	if(logicThread.isAlive())
    	{
    		logicThread.setRunning(false);
    		try
    		{
    			logicThread.join(5000);
    		}catch(Exception e){}
    	}
    }
    
    public static SpriteStore getSpriteStore()
    {
    	return spriteStore;
    }
    
    public NginRenderer getRenderer()
    {
    	return renderer;
    }
    
    
    public void loadTextures()
    {
		// load les textures		
		tex1 = new Texture(textures[0], this);
		tex2 = new Texture(textures[1], this);
    }
    
    public void initSprites()
    {
		Random r = new Random();
		
		// creation de sprites, ici
		for(int i = 0; i < NBSPRITES; i++)
		{
			Sprite s;
			//sprites[i] = new OGLSprite(textureHandle, 32, 32, r.nextInt(SCRW), r.nextInt(SCRH));
			if(i % 2 == 0)
			{
				s = spriteStore.createSprite(tex1, 32, 32, r.nextInt(SCRW), r.nextInt(SCRH), 1);
				s.setSpeed(r.nextInt(100) + 10, r.nextInt(100)+10);
			}
			else
			{
				s = spriteStore.createSprite(tex2, 32, 32, r.nextInt(SCRW), r.nextInt(SCRH), 4);
				s.setSpeed(r.nextInt(100) + 10, r.nextInt(100)+10);
				s.setFrameData(0, 0, 0);
				s.setFrameData(1, 32, 0);
				s.setFrameData(2, 64, 0);
				s.setFrameData(3, 96, 0);
				s.setFrame(0);
			}
			
			s.setAlpha(r.nextFloat());
			s.setScale(r.nextFloat() * 2.0f);
		}
    }
  
}
