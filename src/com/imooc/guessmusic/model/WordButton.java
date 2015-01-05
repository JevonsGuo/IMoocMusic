package com.imooc.guessmusic.model;

import android.widget.Button;

/**
 * Word Button ÎÄ×Ö°´Å¥
 * @author Jevons Guo
 *
 */
public class WordButton {

	public int mIndex;
	public boolean mIsVisiable;
	public String mWordString;
	
	public Button mViewButton;
	
	public WordButton() {
		mIsVisiable = true;
		mWordString = "";
	}
}
