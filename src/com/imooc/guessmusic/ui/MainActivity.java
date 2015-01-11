package com.imooc.guessmusic.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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
import android.widget.Toast;

import com.imooc.guessmusic.R;
import com.imooc.guessmusic.data.Const;
import com.imooc.guessmusic.model.IWordButtonClickListener;
import com.imooc.guessmusic.model.Song;
import com.imooc.guessmusic.model.WordButton;
import com.imooc.guessmusic.myui.MyGridView;
import com.imooc.guessmusic.util.MyLog;
import com.imooc.guessmusic.util.Util;


public class MainActivity extends Activity implements IWordButtonClickListener {

	public final static String TAG = "MainActivity";
	
	/** 答案状态 -- 正确  */
	public final static int STATUS_ANSWER_RIGHT = 1;
	/** 答案状态 -- 错误 */
	public final static int STATUS_ANSWER_WRONG = 2;
	/** 答案状态 -- 不完整  */
	public final static int STATUS_ANSWER_LACK = 3;
	/** 闪烁次数*/
	public final static int SPAKSH_TIME = 6;
	// 唱片相关动画
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
	
	// 过关界面
	private View mPassView;
	
	// 当前动画是否正在运行
	private boolean mIsRunning = false;
	
	// 文字框容器
	private ArrayList<WordButton> mAllWords;
	
	// 已选择文字框UI容器
	private LinearLayout mViewWordsContainer;
	private ArrayList<WordButton> mBtnSelectWords;
	
	private MyGridView mMyGridView;
	
	// 当前的歌曲
	private Song mCurrentSong;
	
	// 当前关的索引
	private int mCurrentStageIndex = -1;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // ��ʼ���ؼ�
        mViewPan = (ImageView)findViewById(R.id.imagePan);
        mViewPanBar = (ImageView)findViewById(R.id.imagePanBar);
        
        mMyGridView = (MyGridView)findViewById(R.id.gridview);
        
        //注册监听
        mMyGridView.registOnWordButtonClick(this);
        
        mViewWordsContainer = (LinearLayout)findViewById(R.id.word_select_container);
        //��ʼ������
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
    
    @Override
    public void onWordButtonClick(WordButton wordButton){
 //   	Toast.makeText(this, wordButton.mIndex+"", Toast.LENGTH_SHORT).show();
    	setSelectWord(wordButton);
    	// 获得答案状态
    	int checkResult = checkTheAnswer();
    	// 检查答案
    	if (checkResult == STATUS_ANSWER_RIGHT) {
    		// 获得相应的奖励，同时过关
    		handlePassEvent();
    	} else if (checkResult == STATUS_ANSWER_WRONG){
    		// 错误提示，闪烁文字并提示用户
    		sparkTheWords();
    		
    	} else if (checkResult == STATUS_ANSWER_LACK){
    		// 设置文字颜色为白色（Normal）
    		for (int i = 0;  i < mBtnSelectWords.size(); i++) {
				mBtnSelectWords.get(i).mViewButton.setTextColor(Color.WHITE);
			}
    	}
    }
    // 处理过关界面及事件
    private void handlePassEvent(){
    	mPassView = (LinearLayout)this.findViewById(R.id.pass_view);
    	mPassView.setVisibility(View.VISIBLE);
    }
    
    private void clearTheAnswer(WordButton wordButton) {
    	wordButton.mViewButton.setText("");
    	wordButton.mWordString ="";
    	wordButton.mIsVisiable = false;
    	
    	// 设置待选框可见性
    	setButtonVisible(mAllWords.get(wordButton.mIndex), View.VISIBLE);
    }
    
    
    /**
     * 设置答案
     * @param wordButton
     */
    private void setSelectWord(WordButton wordButton){
    	for (int i = 0; i < mBtnSelectWords.size(); i++ ){
    		if(mBtnSelectWords.get(i).mWordString.length() == 0){
    			// 设置答案文字框的内容及可见性
    			mBtnSelectWords.get(i).mViewButton.setText(wordButton.mWordString);
    			mBtnSelectWords.get(i).mIsVisiable = true;
    			mBtnSelectWords.get(i).mWordString = wordButton.mWordString;
    			// 记录索引
    			mBtnSelectWords.get(i).mIndex = wordButton.mIndex;
    			
    			// Log ......
    			MyLog.d(TAG, mBtnSelectWords.get(i).mIndex + "");
    			
    			// 设置待选框的可见性
    			setButtonVisible(wordButton, View.INVISIBLE);
    			break;
    		}
    	}
    }
    
    /**
     * 设置待选文字框是否可见
     * @param button
     * @param visibility
     */
    private void setButtonVisible(WordButton button, int visibility){
    	button.mViewButton.setVisibility(visibility);
    	button.mIsVisiable = (visibility == View.VISIBLE)? true : false;
    	
    	// Log
    	MyLog.d(TAG, button.mIsVisiable +"");
    }

    	/**
    	 * 处理圆盘中间的播放按钮，就是开始播放音乐
    	 */
    
    private void handlePlayButton() {
    	if (mViewPanBar != null) {
        	if (!mIsRunning) {
        		mIsRunning = true;
        		//��ʼ���˽��붯��
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
    
    private Song loadStageSongInfo(int stageIndex) {
    	Song song = new Song();
    	
    	String[] stage = Const.SONG_INFO[stageIndex];
    	song.setSongFileName(stage[Const.INDEX_FILE_NAME]);
    	song.setSongName(stage[Const.INDEX_SONG_NAME]);
    	
    	return song;
    }
    
    private void initCurrentStageData(){
    	// 读取当前关的歌曲信息
    	mCurrentSong = loadStageSongInfo(++mCurrentStageIndex);
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
     * ��ʼ�����д�ѡ����
     */
    private ArrayList<WordButton> initAllWord(){
    	ArrayList<WordButton> data = new ArrayList<WordButton>();
    	// Get all available words
    	String [] words = generateWords();
    			
    	for (int i = 0; i < MyGridView.COUNTS_WORDS; i++){
    		WordButton button = new WordButton();
    		button.mWordString = words[i];
    		data.add(button);
    	}
    	return data;
    }
    
    /**
     * ��ʼ����ѡ�����ֿ�
     */
    private ArrayList<WordButton> initWordSelect(){
    	ArrayList<WordButton> data = new ArrayList<WordButton>();
    	
    	for (int i = 0; i < mCurrentSong.getNameLength(); i++){
    		View view = Util.getView(MainActivity.this, R.layout.self_ui_gridview_item);
    	
    		final WordButton holder = new WordButton();
    		
    		holder.mViewButton = (Button)view.findViewById(R.id.item_btn);
    		holder.mViewButton.setTextColor(Color.WHITE);
    		holder.mViewButton.setText("");
    		holder.mIsVisiable = false;
    		
    		holder.mViewButton.setBackgroundResource(R.drawable.game_wordblank);
    		holder.mViewButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					clearTheAnswer(holder);
				}
			});
    		
    		data.add(holder);
    	}
    	return data;	
    }
    
    /**
     * 生成所有的待选文字
     * @return
     */
    private String[] generateWords(){
    	Random random = new Random();
    	String[] words = new String[MyGridView.COUNTS_WORDS];
    	
    	//存入歌名
    	for (int i = 0; i < mCurrentSong.getNameLength(); i++){
    		words[i] = mCurrentSong.getNameCharacters()[i] + "";
    	}
    	//获取随机文字并存入数组
    	for (int i = mCurrentSong.getNameLength(); i< MyGridView.COUNTS_WORDS; i++){
    		words[i] = getRandomChar() + "";
    	}
    	// 打乱文字顺序，首先从所有元素中随机选取一个与第一个元素进行交换
    	// 然后在第二个之后选择一个元素与第二个交换，直到最后一个元素。
    	// 这个算法能够保证每个元素在每个位置的概率都是1/n。
    	
    	for (int i = MyGridView.COUNTS_WORDS - 1; i >= 0;  i--){
    		int index = random.nextInt(i + 1);
    		
    		String buf = words [index];
    		words[index] = words[i];
    		words[i] = buf;
    	}
    	
    	
    	return words;
    }
    /**
     * 生成随机汉字
     * @return
     */
    private char getRandomChar() {
    	String str = "";
    	int highPos;
    	int lowPos;
    	
    	Random random = new Random();
    	
    	highPos = (176 + Math.abs(random.nextInt(39)));
    	lowPos = (161 + Math.abs(random.nextInt(93)));
    	
    	byte[] b = new byte[2];
    	b[0] = (Integer.valueOf(highPos)).byteValue();
    	b[1] = (Integer.valueOf(lowPos)).byteValue();
    	
    	try {
			str = new String(b, "GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return str.charAt(0);
    }
    
    /**
     * 检查答案
     */
    private int checkTheAnswer(){
    	// 先检查已选答案长度
    	for (int i =0; i < mBtnSelectWords.size(); i++) {
    		// 如果有空的，说明答案还不完整
    		if(mBtnSelectWords.get(i).mWordString.length() == 0){
    			return STATUS_ANSWER_LACK; 			
    		}
    	}
    	// 答案完整，继续检查正确性
    	StringBuffer sb = new StringBuffer();
    	for (int i = 0; i < mBtnSelectWords.size(); i++) {
    		sb.append(mBtnSelectWords.get(i).mWordString);
    	}
    	
    	return (sb.toString().equals(mCurrentSong.getSongName())) ? STATUS_ANSWER_RIGHT:STATUS_ANSWER_WRONG;
    	
    }
    
    /**
     * 文字闪烁
     */
    private void sparkTheWords() {
    	// 定时器相关
    	TimerTask task = new TimerTask(){
    		boolean mChange = false;
    		int mSpardTimes = 0;
			@Override
			public void run() {
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						// 显示闪烁的次数
						if (++mSpardTimes > SPAKSH_TIME){
							return;
						}
						// 执行闪烁逻辑，交替显示红色和白色文字
						for (int i = 0;  i < mBtnSelectWords.size(); i++) {
							mBtnSelectWords.get(i).mViewButton.setTextColor(mChange ? Color.RED:Color.WHITE);
						}
						mChange = !mChange;
					}
					
				});
			}
    		
    	};
    	
    	Timer timer = new Timer();
    	timer.schedule(task, 1, 150);
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
