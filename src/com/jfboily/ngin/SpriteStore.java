package com.jfboily.ngin;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class SpriteStore 
{
	private Hashtable<Texture, ArrayList<Sprite>> sprites;
	private ArrayList<Sprite> allSprites;
	public SpriteStore()
	{
		sprites = new Hashtable<Texture, ArrayList<Sprite>>(20);
		allSprites = new ArrayList<Sprite>(20000);
	}
	
	public Sprite createSprite(Texture texture, int width, int height, int x, int y, int nbFrames)
	{
		Sprite sprite = new Sprite(width, height, x, y, texture, nbFrames);
		
		if(sprites.containsKey(texture))
		{
			ArrayList<Sprite> al = sprites.get(texture);
			al.add(sprite);
		}
		else
		{
			ArrayList<Sprite> al = new ArrayList<Sprite>(1000);
			al.add(sprite);
			sprites.put(texture, al);
		}
		allSprites.add(sprite);
		return sprite;
	}
	
	public Enumeration<Texture> getSpritesTextures()
	{
		return sprites.keys();
	}
	
	public ArrayList<Sprite> getSpritesWithTexture(Texture t)
	{
		return sprites.get(t);
	}

	public ArrayList<Sprite> getAllSprites()
	{
		return allSprites;
	}
}
