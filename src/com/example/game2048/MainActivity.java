package com.example.game2048;

import java.util.LinkedList;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

/**
 * 主类，程序入口
 * @author Joysing
 * @E-mail: im@joysing.cc
 * @version CreateTime：2016年12月24日 上午8:03:02
 */
public class MainActivity extends Activity implements OnMenuItemClickListener {
	//面板对象
	private GridLayout broad;
	private int columnCount=4;//一行方格的总数
	private Item[][] items;//方格数组
	private int itemWidth;//面板里一个方格的宽度
	private int divider;//每个方格之间的边距
	private int margin;//方格的外边距
	private List<Point> pointList;//方格位置的指针数组
	private float downX;//执行滑动操作时，保存按下时屏幕坐标
	private float downY;//执行滑动操作时，保存按下时屏幕坐标
	private int lastNum=-1;//执行滑动操作时，保存后面的数字
	private List<Integer> nums;//执行滑动操作时，保存需要处理的数字
	private int maxScore=0;//最高分数
	private TextView tvCurrScore;//最高分数显示组件
	private TextView tvhistoryScore;//最高分数显示组件
	private Animation animScale;//数字出现的动画
	private Button btRestart;//重新开始按钮
	private Button btSelectLevel;//选择难度按钮
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//初始化需要使用的组件
		broad= (GridLayout) findViewById(R.id.main_broad);
		tvCurrScore=(TextView) findViewById(R.id.main_curr_max);
		tvhistoryScore=(TextView) findViewById(R.id.main_history_max);
		animScale=AnimationUtils.loadAnimation(this, R.anim.anim_scale_add_num);
		btRestart=(Button) findViewById(R.id.main_restart);
		btSelectLevel=(Button) findViewById(R.id.main_select);
		
		//添加监听器
		btRestart.setOnClickListener(clickLis);
		btSelectLevel.setOnClickListener(clickLis);
		init();
		initGame();
	}
	 public boolean onCreateOptionsMenu(Menu menu){
         MenuItem item1= menu.add(1,1,1,"关于");
         item1.setOnMenuItemClickListener(this); //设置菜单项的单击事件，即触发onMenuItemClick方法 
         return true;
	 }
	 public boolean onMenuItemClick(MenuItem item){
			switch(item.getItemId()){
			case 1:
				Intent intent=new Intent(this,About.class);
				startActivity(intent);
	            break;
			default:break;
			}
	     	return false;
	  }
	 /**
	  * 初始化软件
	  * @author: Joysing
	  * @ModifyTime: 2016年12月24日 上午11:20:01
	  * @methodName: init
	  * returnType: void
	  */
	private void init(){
		divider=getResources().getDimensionPixelSize(R.dimen.divider);//2dp的间距
		margin=getResources().getDimensionPixelSize(R.dimen.activity_margin);//10dp边距
		pointList=new LinkedList<Point>();//初始化方格位置指针列表
		nums=new LinkedList<Integer>();
	}
	/**
	 * 初始化游戏面板
	 * 1、先移除原有的所有子项，否则点击重新开始时，broad里还有上一次游戏生成的残留
	 * 2、设置broad的列数、获取屏幕宽度，然后计算出游戏面板里一个方格的宽度
	 * 3、读取上一次的最高分并显示
	 * 4、for循环逐个创建方格，并且设置属性，加入一个默认数字0
	 * 5、在两个随机位置产生两个随机数字
	 * @author: Joysing
	 * @ModifyTime: 2016年12月24日 上午11:29:01
	 * @methodName: initGame
	 * returnType: void
	 */
	private void initGame(){
		broad.removeAllViews();//先移除原有的所有子项，否则点击重新开始时，broad里还有上一次游戏生成的残留
		broad.setColumnCount(columnCount);//设置broad的列数
		int screenWidth=getResources().getDisplayMetrics().widthPixels;//获取屏幕宽度
		itemWidth=(screenWidth-2*margin-(columnCount+1)*divider)/columnCount;//游戏面板里一个方格的宽度
		tvhistoryScore.setText(String.format("历史最高：%d",getMaxScore()));
		items =new Item[columnCount][columnCount];
		for(int i=0;i<columnCount;i++){
			for(int j=0;j<columnCount;j++){
				items[i][j]=new Item(this);//创建方格对象
				//设置方格属性
				items[i][j].setNum(0);
				items[i][j].setPoint(i,j);
				items[i][j].setSize(itemWidth,divider,columnCount);
				broad.addView(items[i][j]);
			}
		}
		addNum(2);//产生两个随机数
	}
	/**
	 * 两个按钮的点击事件
	 */
	private View.OnClickListener clickLis=new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.main_restart:restart();break;
			case R.id.main_select:selectLevel();break;
			}
		}
	};
	/**
	 * 点击选择难度后执行这个方法，这个方法弹出一个让你选择难度的对话框，把这个对话框设置监听事件
	 * @author: Joysing
	 * @ModifyTime: 2016年12月25日 上午10:09:40
	 * @methodName: selectLevel
	 * returnType: void
	 */
	private void selectLevel(){
		new AlertDialog.Builder(this)
		.setItems(new String[]{"4x4","5x5","6x6"},dialogClickLis)
		.setCancelable(true).show();
	}

	/**
	 * 这个方法在一个随机位置产生随机数
	 * 1、清空pointList、检查空的位置并且加入pointList中
	 * 2、随机生成一个小于pointList长度的数index，然后在index这个位置生成一个数（2或4）
	 * 3、为数字的出现添加动画，最后判断数字还能不能移动(有没有空位置)决定是否结束游戏
	 * @author: Joysing
	 * @ModifyTime: 2016年12月24日 下午1:34:51
	 * @methodName: addNum
	 * @param count 生成随机数的个数
	 * returnType: void
	 */
	private void addNum(int count){
		pointList.clear();
		checkNull();
		for(int i=0;i<count;i++){
			if(pointList.size()>0){
				int index=(int) (Math.random()*pointList.size());//产生一个随机位置index
				Point point =pointList.get(index);
				int num=Math.random()>0.1?2:4;//在2和4之间随机产生一个数
				items[point.y][point.x].setNum(num);//把产生的随机数加入index位置
				items[point.y][point.x].startAnimation(animScale);//给随机生成的数字添加动画
				pointList.remove(index);//上面加入后，把index这个位置从pointList中移除
			}
		}
		if(!canMove())gameOver();
	}
	/**
	 * for循环检查哪个方格是没有数字的（数字为0则是没有数字），获取这个位置的指针，然后把这个格子的指针加入pointList列表
	 * @author: Joysing
	 * @ModifyTime: 2016年12月24日 下午1:43:12
	 * @methodName: checkNull
	 * returnType: void
	 */
	private void checkNull(){
		for(int i=0;i<columnCount;i++){
			for(int j=0;j<columnCount;j++){
				if(items[i][j].getNum()==0){
					pointList.add(items[i][j].getPoint());
					}
				}
			}
	}
	/**
	 * 这个是滑动事件，在按下手指时调用一次，松开手指时又调用一次
	 * 1、按下时，记录按下的位置downX，downY
	 * 2、滑动后松手，x，y为松手的位置，dx，dy为滑动的距离（滑动偏移量）
	 * 3、如果偏移量小于50，就不做处理，大于50就判断数字能不能移动（不能移动则GAMEOVER）
	 * 4、判断手指滑动的方向，已执行相应的滑动方法，滑动数字后，生成一个新的随机数（2或4），记录一次分数
	 * @author: Joysing
	 * @ModifyTime: 2016年12月24日 下午2:23:19
	 * @methodName: onTouchEvent
	 * returnType: boolean
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		float x=event.getX();
		float y=event.getY();
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:downX=x;downY=y;break;//按下时，记录按下的位置
		case MotionEvent.ACTION_UP://滑动后松手，x，y为松手的位置，dx，dy为滑动的距离（滑动偏移量）
			float dx=x-downX;
			float dy=y-downY;
			if(Math.abs(dx)<50&&Math.abs(dy)<50)//偏移量小于50，不处理
				return super.onTouchEvent(event);
			if(canMove()){
				if(move(dx, dy)){
					addNum(1);
					tvCurrScore.setText(String.format("当前最高：%d", maxScore));//每滑动一次，记录一次
				}
			}else gameOver();
			break;
		}
		return super.onTouchEvent(event);
	}
	/**
	 * 游戏结束，弹出提示框，记录分数，提示框可以选择“重新开始”或者“退出”，并把这两个选择添加到监听器
	 * @author: Joysing
	 * @ModifyTime: 2016年12月25日 上午10:19:17
	 * @methodName: gameOver
	 * returnType: void
	 */
	private void gameOver(){
		String msg="";
		if(maxScore<256)msg="你这个辣鸡";
		else if (maxScore<512)msg="你这个童子鸡";
		else if (maxScore<2048)msg="你这个香油鸡";
		else if (maxScore<4096)msg="你这个蜜汁鸡";
		else if (maxScore<8192)msg="你这个拖拉机";
		else msg="你这个肯德鸡";
		new AlertDialog.Builder(this)
		.setMessage(msg)
		.setTitle("Game Over")
		.setNegativeButton("重新开始", dialogClickLis)
		.setNeutralButton("退出", dialogClickLis)
		.setCancelable(true).show();
		saveScore();
	}
	/**
	 * 重新开始游戏
	 * 1、读取历史最高分
	 * 2、for循环初始化每一个方格
	 * 3、在两个随机位置产生两个随机数（2或4）
	 * @author: Joysing
	 * @ModifyTime: 2016年12月25日 上午10:19:50
	 * @methodName: restart
	 * returnType: void
	 */
	private void restart(){
		saveScore();
		tvhistoryScore.setText(String.format("历史最高：%d",getMaxScore()));
		for(int i=0;i<columnCount;i++){
			for(int j=0;j<columnCount;j++){
				items[i][j].setNum(0);
			}
		}
		addNum(2);//产生两个随机数
	}
	/**
	 * 对话框的响应事件，
	 * 游戏结束时弹出的对话框：
	 * “重新开始”则执行restart()方法
	 * “退出”则关闭当前Activity，即退出程序（当前只有一个Activity）
	 * 选择难度：
	 * 选择第一项则把游戏列数设为4
	 * 选择第二项则把游戏列数设为5
	 * 选择第三项则把游戏列数设为6
	 */
	private DialogInterface.OnClickListener dialogClickLis=new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch(which){
			case DialogInterface.BUTTON_NEGATIVE:restart();break;
			case DialogInterface.BUTTON_NEUTRAL:finish();break;
			case 0:
				columnCount=4;
				initGame();break;
			case 1:
				columnCount=5;
				initGame();break;
			case 2:
				columnCount=6;
				initGame();break;
			}
			// TODO Auto-generated method stub
			
		}
	};
	/**
	 * 保存分数，如果大于历史最高就更新历史最高
	 * @author: Joysing
	 * @ModifyTime: 2016年12月25日 上午10:21:04
	 * @methodName: saveScore
	 * returnType: void
	 */
	private void saveScore(){
		int history=getMaxScore();
		if(history<maxScore){
			SharedPreferences.Editor e= getSharedPreferences("level", MODE_PRIVATE).edit();
			e.putInt(null, maxScore);
			e.commit();
		}
	}
	/**
	 * 读取最高分
	 * @author: Joysing
	 * @ModifyTime: 2016年12月25日 上午11:02:40
	 * @methodName: getMaxScore
	 * @return
	 * returnType: int
	 */
	private int getMaxScore(){
		return getSharedPreferences("level",MODE_PRIVATE).getInt(null, 0);
	}
	/**
	 * 判断手指滑动的方向，并执行相应的方法
	 * @author: Joysing
	 * @ModifyTime: 2016年12月24日 下午9:28:40
	 * @methodName: move
	 * @param x 水平（左右）滑动偏移量
	 * @param y 垂直（上下）滑动偏移量
	 * @return
	 * returnType: boolean
	 */
	private boolean move(float x,float y){
		if(Math.abs(x)>Math.abs(y)){
			if(x>0)return moveToRight();
			return moveToLeft();
		}else{
			if(y>0)return moveToBottom();
			return moveToTop();
		}
	}
	/**
	 * 判断游戏还能否移动，不能移动则GAMEOVER
	 * 1、for循环遍历每一个方格，有空方格（方格里数字是0）时返回true。
	 * 2、如果没有空方格了，判断一个数的上下左右，是否有数字和它相同（可以合并），有就返回true。
	 * 3、上面两条都不符合，返回false，GAMEOVER
	 * @author: Joysing
	 * @ModifyTime: 2016年12月24日 下午2:28:28
	 * @methodName: canMove
	 * @return
	 * returnType: boolean
	 */
	private boolean canMove(){
		for(int i=0;i<columnCount;i++){
			for(int j=0;j<columnCount;j++){
				int num=items[i][j].getNum();
				if(num==0)return true;
				if(i-1>=0&&num==items[i-1][j].getNum())return true;
				if(i+1<columnCount&&num==items[i+1][j].getNum())return true;
				if(j-1>=0&&num==items[i][j-1].getNum())return true;
				if(j+1<columnCount&&num==items[i][j+1].getNum())return true;
			}
		}
		return false;
	}
	/**
	 * 执行向下滑动的操作，不能向下滑动时返回false
	 * 1、从上往下遍历每一个方格（先遍历第一列，然后第二列，以此类推）。
	 * 2、把每个方格的数读出来，如果不为0，就把数保存在nums数组里，如果是0，用zeroIndex记录这个位置。
	 * 3、判断判断取出的数和上一次取出的是否相同（lastNum用来保存上一次取出的数，lastNum=-1说明现在取出的是第一个数或不能跟上一个数合并）。
	 * 4、如果取出的数和上一次取出的相同，则和上一个数合并后再加入nums数组。
	 * 5、最后按顺序取出nums的数，从下往上依次填入方格里。
	 * 6、清空nums，然后开始遍列第二列。。。
	 * @author: Joysing
	 * @ModifyTime: 2016年12月24日 下午2:34:44
	 * @methodName: moveToBottom
	 * @return
	 * returnType: boolean
	 */
	private boolean moveToBottom() {
		boolean f=false;//能不能向这个方向滑动
		int zeroIndex=-1;//记录0的位置，即空白的位置
		for(int j = 0;j<columnCount;j++){
			for(int i=columnCount-1;i>=0;i--){//从上往下遍历每一个方格
				int num=items[i][j].getNum();// 
				if(num!=0){//判断方格里有数时
					if(i<zeroIndex&&zeroIndex!=-1)
						f=true;
					if(lastNum==-1)
						lastNum=num;
					else if(lastNum==num){
						f=true;
						nums.add(num*2);
						lastNum=-1;
					}else{
						nums.add(lastNum);
						lastNum=num;
					}
				}else zeroIndex=j;
			}
			if(lastNum!=-1)
				nums.add(lastNum);
			for(int i=columnCount-1;i>=0;i--){//循环取出nums里的数，从左往右依次填入items方格
				if(columnCount-1-i<nums.size()){
					int num=nums.get(columnCount-1-i);
					if(maxScore<num)
						maxScore=num;
					items[i][j].setNum(num);
				}
				else items[i][j].setNum(0);//取出全部数后，后面空的items方格设为0
			}
			nums.clear();//每循环一行，清空一次nums
			lastNum=-1;
			zeroIndex=-1;
		}
		return f;
	}
	/**
	 * 执行向上滑动的操作，不能向上滑动时返回false
	 * 具体实现类似于moveToBottom方法
	 * Author：Joysing
	 * @return
	 * 2016年12月24日 下午2:34:41
	 */
	private boolean moveToTop() {
		boolean f=false;
		int zeroIndex=-1;//记录0的位置，即空白的位置
		for(int j = 0;j<columnCount;j++){
			for(int i=0;i<columnCount;i++){//从上往下遍历每一个方格
				int num=items[i][j].getNum();//num为方格里的数
				if(num!=0){//判断方格里有数时
					if(i>zeroIndex&&zeroIndex!=-1)
						f=true;
					if(lastNum==-1)
						lastNum=num;
					else if(lastNum==num){
						f=true;
						nums.add(num*2);
						lastNum=-1;
					}else{
						nums.add(lastNum);
						lastNum=num;
					}
				}else zeroIndex=j;
			}
			if(lastNum!=-1)
				nums.add(lastNum);
			for(int i=0;i<columnCount;i++){//循环取出nums里的数，从下往上依次填入items方格
				if(i<nums.size()){
					int num=nums.get(i);
					if(maxScore<num)
						maxScore=num;
					items[i][j].setNum(num);
				}
				else items[i][j].setNum(0);//取出全部数后，后面空的items方格设为0
			}
			nums.clear();//每循环一行，清空一次nums
			lastNum=-1;
			zeroIndex=-1;
		}
		return f;
	}
	/**
	 * 执行向右滑动的操作，不能向右滑动时返回false
	 * 具体实现类似于moveToBottom方法
	 * Author：Joysing
	 * @return
	 * 2016年12月24日 下午2:34:35
	 */
	private boolean moveToRight() {
		boolean f=false;
		int zeroIndex=-1;//记录0的位置，即空白的位置
		for(int i=0;i<columnCount;i++){
			for(int j=columnCount-1;j>=0;j--){//从右往左遍历每一个方格
				int num=items[i][j].getNum();//num为方格里的数
				if(num!=0){//判断方格里有数时
					if(j<zeroIndex&&zeroIndex!=-1)
						f=true;
					if(lastNum==-1)
						lastNum=num;
					else if(lastNum==num){
						f=true;
						nums.add(num*2);
						lastNum=-1;
					}else{
						nums.add(lastNum);
						lastNum=num;
					}
				}else zeroIndex=j;
			}
			if(lastNum!=-1)
				nums.add(lastNum);
			for(int j=columnCount-1;j>=0;j--){//循环取出nums里的数，从右往左依次填入items方格
				if(columnCount-1-j<nums.size()){
					int num=nums.get(columnCount-1-j);
					if(maxScore<num)
						maxScore=num;
					items[i][j].setNum(num);
				}
				else items[i][j].setNum(0);//取出全部数后，后面空的items方格设为0
			}
			nums.clear();//每循环一行，清空一次nums
			lastNum=-1;
			zeroIndex=-1;
		}
		return f;
	}
	/**
	 * 执行向左滑动的操作，不能向左滑动时返回false
	 * 具体实现类似于moveToBottom方法
	 * Author：Joysing
	 * @return
	 * 2016年12月24日 下午2:34:31
	 */
	private boolean moveToLeft() {
		boolean f=false;
		int zeroIndex=-1;//记录0的位置，即空白的位置
		for(int i=0;i<columnCount;i++){
			for(int j = 0;j<columnCount;j++){//从左往右遍历每一个方格
				int num=items[i][j].getNum();//num为方格里的数
				if(num!=0){//判断方格里有数时
					if(j>zeroIndex&&zeroIndex!=-1)
						f=true;
					if(lastNum==-1)
						lastNum=num;
					else if(lastNum==num){
						f=true;
						nums.add(num*2);
						lastNum=-1;
					}else{
						nums.add(lastNum);
						lastNum=num;
					}
				}else zeroIndex=j;
			}
			if(lastNum!=-1)
				nums.add(lastNum);
			for(int j=0;j<columnCount;j++){//循环取出nums里的数，从左往右依次填入items方格
				if(j<nums.size()){
					int num=nums.get(j);
					if(maxScore<num)
						maxScore=num;
					items[i][j].setNum(nums.get(j));
				}
				else items[i][j].setNum(0);//取出全部数后，后面空的items方格设为0
			}
			nums.clear();//每循环一行，清空一次nums
			lastNum=-1;
			zeroIndex=-1;
		}
		return f;
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 * 
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		saveScore();
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
