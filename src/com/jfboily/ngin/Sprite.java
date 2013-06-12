package com.jfboily.ngin;

import java.nio.FloatBuffer;

import android.util.FloatMath;

public class Sprite 
{
	private float[] vertex1;
	private float[] vertex2;
	private float[] vertex3;
	private float[] vertex4;
	private float[] colorData;
	
	private float alpha;
	
	private float angle = 0.0f;
	
	private float scale = 1.0f;
	
	//private final float[] texCoordsData;
	private float x, y;
	private float w2, h2;
	private float speedX;
	private float speedY;
	private Texture texture;
	private int nbFrames;
	private final float[][] frameTexCoordsData;
	private int curFrame = 0;
	private int width, height;
	private long animTimer;
	
	public Sprite(int width, int height, int x, int y, Texture texture, int nbFrames)
	{
		this.width = width;
		this.height = height;
		w2 = (float)width / 2;
		h2 = (float)height / 2;
		this.x = x;
		this.y = y;
		this.texture = texture;
		this.nbFrames = nbFrames;
		
		// r g b a
		colorData = new float[] {1.0f, 1.0f, 1.0f, 1.0f };
	
		// tl
		vertex1 = new float[] {-w2, h2, 0};
		
		// bl
		vertex2 = new float[] {-w2, -h2, 0};
		
		// tr
		vertex3 = new float[] {w2, h2, 0};
		
		// br
		vertex4 = new float[] {w2, -h2, 0};
		
		
//		verticesData = new float[] {
//				// x y x
//				// r g b a
//				
//				//tl
//				-w2+x, h2+y, 0.0f,
//				
//				//bl
//				-w2+x, -h2+y, 0.0f,
//				
//				//tr
//				w2+x, h2+y, 0.0f,
//
//				//bl
//				-w2+x, -h2+y, 0.0f,
//				
//				//br
//				w2+x, -h2+y, 0.0f,
//				
//				//tr
//				w2+x, h2+y, 0.0f,
//
//		};
		 
		 frameTexCoordsData = new float[nbFrames][12];
		 if(nbFrames == 1)
		 {
			 frameTexCoordsData[0] = new float[]{
						0.0f, 0.0f,
				        0.0f, 1.0f,
				        1.0f, 0.0f,
				        0.0f, 1.0f,
				        1.0f, 1.0f,
				        1.0f, 0.0f,
				};
		 }
		 
		 animTimer = 0;
	}
	
	public void setPos(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	public int render(FloatBuffer vert, FloatBuffer tex)
	{
		
		// transformation des vertices
		//initVertices();
		
		// rotation
		float cos = FloatMath.cos(angle);
		float sin = FloatMath.sin(angle);
		
		vertex1[0] = -w2 * cos - (h2 * sin);
		vertex1[1] = -w2 * sin + (h2 * cos);

		vertex2[0] = -w2 * cos - (-h2 * sin);
		vertex2[1] = -w2 * sin + (-h2 * cos);
		
		vertex3[0] = w2 * cos - (h2 * sin);
		vertex3[1] = w2 * sin + (h2 * cos);
		
		vertex4[0] = w2 * cos - (-h2 * sin);
		vertex4[1] = w2 * sin + (-h2 * cos);
		
		// scale
		vertex1[0] *= scale;
		vertex1[1] *= scale;

		vertex2[0] *= scale;
		vertex2[1] *= scale;
		
		vertex3[0] *= scale;
		vertex3[1] *= scale;
		
		vertex4[0] *= scale;
		vertex4[1] *= scale;
		
		// translation
		
		vertex1[0] += x;
		vertex1[1] += y;
		
		vertex2[0] += x;
		vertex2[1] += y;
		
		vertex3[0] += x;
		vertex3[1] += y;
		
		vertex4[0] += x;
		vertex4[1] += y;
		
		// couleur (alpha)
		colorData[3] = alpha;
		
		// envoie dans le FloatBuffer
		//tl
		vert.put(vertex1);
		vert.put(colorData);
		
		//bl
		vert.put(vertex2);
		vert.put(colorData);
		
		//tr
		vert.put(vertex3);
		vert.put(colorData);
		
		//bl
		vert.put(vertex2);
		vert.put(colorData);
		
		//br
		vert.put(vertex4);
		vert.put(colorData);
		
		//tr
		vert.put(vertex3);
		vert.put(colorData);
		
		if(curFrame >= nbFrames)
		{
			curFrame = 0;
		}
		tex.put(frameTexCoordsData[curFrame]);
		
		return 1;
	}
	
	public void setSpeed(float speedX, float speedY)
	{
		this.speedX = speedX;
		this.speedY = speedY;
	}
	
	public void move(long deltaTime)
	{
		// update rotation
		angle += 0.01;
		if(angle >= 2 * Math.PI)
		{
			angle = 0.0f;
		}
		
		// update scale
//		scale += 0.0001;
//		
//		if(scale >= 2.0f)
//		{
//			scale = 0.1f;
//		}
		
		//update position
		
		float dx = (deltaTime / 1000.0f) * speedX;
		float dy = (deltaTime / 1000.0f) * speedY;
		
		x += dx;
		y += dy;
		
		if(x > NginGame.SCRW)
		{
			x = NginGame.SCRW;
			speedX = -speedX;
		}
		
		if(x < 0)
		{
			x = 0;
			speedX = -speedX;
		}
		
		if(y < 0)
		{
			y = 0;
			speedY = -speedY;
		}
		
		if(y > NginGame.SCRH)
		{
			y = NginGame.SCRH;
			speedY = -speedY;
		}
		
	
		// update anim
		
		animTimer -= deltaTime;
		
		if(animTimer <= 0)
		{
			animTimer = 200;
			if(curFrame < nbFrames - 1)
			{
				curFrame++;
			}
			else
			{
				curFrame = 0;
			}
		}
		
	}
	
	public void setAlpha(float a)
	{
		this.alpha = a;
	}
	
	public void setScale(float s)
	{
		this.scale = s;
	}
	
	public void setFrameData(int frame, int x, int y)
	{
		float[] texRegion = texture.getTexRegion(x, y, width, height);
//		0.0f, 0.0f,
//        0.0f, 1.0f,
//        1.0f, 0.0f,
//        0.0f, 1.0f,
//        1.0f, 1.0f,
//        1.0f, 0.0f,
		frameTexCoordsData[frame] = new float[]{
			texRegion[0], texRegion[1],
			texRegion[0], texRegion[3],
			texRegion[2], texRegion[1],
			texRegion[0], texRegion[3],
			texRegion[2], texRegion[3],
			texRegion[2], texRegion[1],
		};
	}
	
	public void setFrame(int frame)
	{
		curFrame = frame;
	}
}
