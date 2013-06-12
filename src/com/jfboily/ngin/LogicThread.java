package com.jfboily.ngin;

import java.util.ArrayList;


public class LogicThread extends Thread
{
	private boolean run;
	private NginGame activity;
	private SpriteStore spriteStore;
	private long startTime, endTime, deltaTime;
	
	public LogicThread(NginGame activity)
	{
		this.activity = activity;
	}
	
	@Override
	public void run() 
	{
		boolean initSprites = true;
		
		startTime = System.currentTimeMillis();
		spriteStore = activity.getSpriteStore();
		while(run)
		{
			endTime = startTime;
			startTime = System.currentTimeMillis();
			deltaTime = startTime - endTime;
			
			if(deltaTime == 0)
			{
				deltaTime = 1;
			}

			
			// update stuff
			if(NginGame.glRendererReady)
			{

				ArrayList<Sprite> sprites = spriteStore.getAllSprites();
				
				for(Sprite s : sprites)
				{
					s.move(deltaTime);
				}
			}
			// pause for 1/1000 sec
			try
			{
				Thread.sleep(1);
			}catch(Exception e){}
		}
		
	}
	
	public void setRunning(boolean run)
	{
		this.run = run;
	}

}
