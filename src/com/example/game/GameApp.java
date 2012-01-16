package com.example.game;

import com.playtomic.android.api.Playtomic;

import android.app.Application;

public class GameApp extends Application {
	private Playtomic playtomic = null;
	
	@Override
	public void onCreate(){
		super.onCreate();
		
		try {
		
			playtomic = Playtomic.getInstance(5829, "e775f0e4b3534db3", "79004d16379440c5bd058285f554c9", this);
			
/*
			playtomic = Playtomic.getInstance(4748, "3893d4cf173a429d", "aba2af63c5d54d3f920103b277e696", this);
*/
			Playtomic.Log().view();
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		

	}
}
