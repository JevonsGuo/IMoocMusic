package com.imooc.guessmusic.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.imooc.guessmusic.R;
import com.imooc.guessmusic.model.WordButton;
import com.imooc.guessmusic.myui.MyGridView;
import com.imooc.guessmusic.util.Util;


public class MainActivity extends Activity {

	//唱片相关动画
	private Animation mPanAnim;
	private LinearInterpolator mPanLin;
	
	private Animation mBarInAnim;
	private LinearInterpolator mBarInLin;
	
	private Animation mBarOutAnim;
	private LinearInterpolator mBarOutLin;
	
	private ImageView mViewPan;
	private ImageView mViewPanBar;
	
	// Play 按键事件
	private ImageButton mBtnPlayStart;
	
	// Is Animation running?
	private boolean mIsRunning = false;
	
	// 文字框容器
	private ArrayList<WordButton> mAllWords;
	
	private ArrayList<WordButton> mBtnSelectWords;
	
	private MyGridView mMyGridView;
	
	// 已选择文字框UI容器
	private LinearLayout mViewWordsContainer;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化控件
        mViewPan = (ImageView)findViewById(R.id.imagePan);
        mViewPanBar = (ImageView)findViewById(R.id.imagePanBar);
        
        mMyGridView = (MyGridView)findViewById(R.id.gridview);
        
        mViewWordsContainer = (LinearLayout)findViewById(R.id.word_select_container);
        //初始化动画
        mPanAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        mPanLin = new LinearInterpolator();
        mPanAnim.setInterpolator(mPanLin);
        mPanAnim.setAnimationListener(new AnimationListener(){
        	
        	@Override
        	public void onAnimationStart(Animation animation){
        		
        	}
        	
        	@Override
        	public void onAnimationEnd(Animation animation){
        		mViewPanBar.startAnimation(mBarOutAnim);
        	}
        	
        	@Override
        	public void onAnimationRepeat(Animation animation){
        		
        	}
        });
        
        mBarInAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_45);
        mBarInLin = new LinearInterpolator();
        mBarInAnim.setFillAfter(true);
        mBarInAnim.setInterpolator(mBarInLin);
        mBarInAnim.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				mViewPan.startAnimation(mPanAnim);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        
        mBarOutAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_d_45);
        mBarOutLin = new LinearInterpolator();
        mBarOutAnim.setFillAfter(true);
        mBarOutAnim.setInterpolator(mBarOutLin);
        mBarOutAnim.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				
				mIsRunning = false;
				mBtnPlayStart.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        
        mBtnPlayStart = (ImageButton)findViewById(R.id.btn_play_start);
        mBtnPlayStart.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				handlePlayButton();
			}
		});
        //初始化游戏数据
        initCurrentStageData();
    }

    
    private void handlePlayButton() {
    	if (mViewPanBar != null) {
        	if (!mIsRunning) {
        		mIsRunning = true;
        		//开始拨杆进入动画
        		mViewPanBar.startAnimation(mBarInAnim);
        		mBtnPlayStart.setVisibility(View.INVISIBLE);
        	}   
    	}		
    }
    
    @Override
    public void onPause() {
    	mViewPan.clearAnimation();
    	
    	super.onPause();
    }
    
    private void initCurrentStageData(){
    	// 初始化已选择框
    	mBtnSelectWords = initWordSelect();
    	LayoutParams params = new LayoutParams(110,110);
    	for (int i = 0; i < mBtnSelectWords.size(); i++){
    		mViewWordsContainer.addView(mBtnSelectWords.get(i).mViewButton,params);
    	}
    	// Get Data
    	mAllWords = initAllWord();
    	// Update Data - MyGridView
    	mMyGridView.updateData(mAllWords);
    }
    /**
     * 
     * 初始化所有待选文字
     */
    private ArrayList<WordButton> initAllWord(){
    	ArrayList<WordButton> data = new ArrayList<WordButton>();
    	// Get all available words
    	for (int i = 0; i < MyGridView.COUNTS_WORDS; i++){
    		WordButton button = new WordButton();
    		button.mWordString = "" + i;
    		data.add(button);
    	}
    	return data;
    }
    
    /**
     * 初始化已选择文字框
     */
    private ArrayList<WordButton> initWordSelect(){
    	ArrayList<WordButton> data = new ArrayList<WordButton>();
    	
    	for (int i =0; i < 4; i++){
    		View view = Util.getView(MainActivity.this, R.layout.self_ui_gridview_item);
    	
    		WordButton holder = new WordButton();
    		holder.mViewButton = (Button)view.findViewById(R.id.item_btn);
    		holder.mViewButton.setTextColor(Color.WHITE);
    		holder.mViewButton.setText("");
    		holder.mIsVisiable = false;
    		
    		holder.mViewButton.setBackgroundResource(R.drawable.game_wordblank);
    		
    		data.add(holder);
    	}
    	return data;	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
