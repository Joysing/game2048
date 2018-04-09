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
 * ���࣬�������
 * @author Joysing
 * @E-mail: im@joysing.cc
 * @version CreateTime��2016��12��24�� ����8:03:02
 */
public class MainActivity extends Activity implements OnMenuItemClickListener {
	//������
	private GridLayout broad;
	private int columnCount=4;//һ�з��������
	private Item[][] items;//��������
	private int itemWidth;//�����һ������Ŀ��
	private int divider;//ÿ������֮��ı߾�
	private int margin;//�������߾�
	private List<Point> pointList;//����λ�õ�ָ������
	private float downX;//ִ�л�������ʱ�����水��ʱ��Ļ����
	private float downY;//ִ�л�������ʱ�����水��ʱ��Ļ����
	private int lastNum=-1;//ִ�л�������ʱ��������������
	private List<Integer> nums;//ִ�л�������ʱ��������Ҫ���������
	private int maxScore=0;//��߷���
	private TextView tvCurrScore;//��߷�����ʾ���
	private TextView tvhistoryScore;//��߷�����ʾ���
	private Animation animScale;//���ֳ��ֵĶ���
	private Button btRestart;//���¿�ʼ��ť
	private Button btSelectLevel;//ѡ���ѶȰ�ť
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//��ʼ����Ҫʹ�õ����
		broad= (GridLayout) findViewById(R.id.main_broad);
		tvCurrScore=(TextView) findViewById(R.id.main_curr_max);
		tvhistoryScore=(TextView) findViewById(R.id.main_history_max);
		animScale=AnimationUtils.loadAnimation(this, R.anim.anim_scale_add_num);
		btRestart=(Button) findViewById(R.id.main_restart);
		btSelectLevel=(Button) findViewById(R.id.main_select);
		
		//��Ӽ�����
		btRestart.setOnClickListener(clickLis);
		btSelectLevel.setOnClickListener(clickLis);
		init();
		initGame();
	}
	 public boolean onCreateOptionsMenu(Menu menu){
         MenuItem item1= menu.add(1,1,1,"����");
         item1.setOnMenuItemClickListener(this); //���ò˵���ĵ����¼���������onMenuItemClick���� 
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
	  * ��ʼ�����
	  * @author: Joysing
	  * @ModifyTime: 2016��12��24�� ����11:20:01
	  * @methodName: init
	  * returnType: void
	  */
	private void init(){
		divider=getResources().getDimensionPixelSize(R.dimen.divider);//2dp�ļ��
		margin=getResources().getDimensionPixelSize(R.dimen.activity_margin);//10dp�߾�
		pointList=new LinkedList<Point>();//��ʼ������λ��ָ���б�
		nums=new LinkedList<Integer>();
	}
	/**
	 * ��ʼ����Ϸ���
	 * 1�����Ƴ�ԭ�е�����������������¿�ʼʱ��broad�ﻹ����һ����Ϸ���ɵĲ���
	 * 2������broad����������ȡ��Ļ��ȣ�Ȼ��������Ϸ�����һ������Ŀ��
	 * 3����ȡ��һ�ε���߷ֲ���ʾ
	 * 4��forѭ������������񣬲����������ԣ�����һ��Ĭ������0
	 * 5�����������λ�ò��������������
	 * @author: Joysing
	 * @ModifyTime: 2016��12��24�� ����11:29:01
	 * @methodName: initGame
	 * returnType: void
	 */
	private void initGame(){
		broad.removeAllViews();//���Ƴ�ԭ�е�����������������¿�ʼʱ��broad�ﻹ����һ����Ϸ���ɵĲ���
		broad.setColumnCount(columnCount);//����broad������
		int screenWidth=getResources().getDisplayMetrics().widthPixels;//��ȡ��Ļ���
		itemWidth=(screenWidth-2*margin-(columnCount+1)*divider)/columnCount;//��Ϸ�����һ������Ŀ��
		tvhistoryScore.setText(String.format("��ʷ��ߣ�%d",getMaxScore()));
		items =new Item[columnCount][columnCount];
		for(int i=0;i<columnCount;i++){
			for(int j=0;j<columnCount;j++){
				items[i][j]=new Item(this);//�����������
				//���÷�������
				items[i][j].setNum(0);
				items[i][j].setPoint(i,j);
				items[i][j].setSize(itemWidth,divider,columnCount);
				broad.addView(items[i][j]);
			}
		}
		addNum(2);//�������������
	}
	/**
	 * ������ť�ĵ���¼�
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
	 * ���ѡ���ѶȺ�ִ����������������������һ������ѡ���ѶȵĶԻ��򣬰�����Ի������ü����¼�
	 * @author: Joysing
	 * @ModifyTime: 2016��12��25�� ����10:09:40
	 * @methodName: selectLevel
	 * returnType: void
	 */
	private void selectLevel(){
		new AlertDialog.Builder(this)
		.setItems(new String[]{"4x4","5x5","6x6"},dialogClickLis)
		.setCancelable(true).show();
	}

	/**
	 * ���������һ�����λ�ò��������
	 * 1�����pointList�����յ�λ�ò��Ҽ���pointList��
	 * 2���������һ��С��pointList���ȵ���index��Ȼ����index���λ������һ������2��4��
	 * 3��Ϊ���ֵĳ�����Ӷ���������ж����ֻ��ܲ����ƶ�(��û�п�λ��)�����Ƿ������Ϸ
	 * @author: Joysing
	 * @ModifyTime: 2016��12��24�� ����1:34:51
	 * @methodName: addNum
	 * @param count ����������ĸ���
	 * returnType: void
	 */
	private void addNum(int count){
		pointList.clear();
		checkNull();
		for(int i=0;i<count;i++){
			if(pointList.size()>0){
				int index=(int) (Math.random()*pointList.size());//����һ�����λ��index
				Point point =pointList.get(index);
				int num=Math.random()>0.1?2:4;//��2��4֮���������һ����
				items[point.y][point.x].setNum(num);//�Ѳ��������������indexλ��
				items[point.y][point.x].startAnimation(animScale);//��������ɵ�������Ӷ���
				pointList.remove(index);//�������󣬰�index���λ�ô�pointList���Ƴ�
			}
		}
		if(!canMove())gameOver();
	}
	/**
	 * forѭ������ĸ�������û�����ֵģ�����Ϊ0����û�����֣�����ȡ���λ�õ�ָ�룬Ȼ���������ӵ�ָ�����pointList�б�
	 * @author: Joysing
	 * @ModifyTime: 2016��12��24�� ����1:43:12
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
	 * ����ǻ����¼����ڰ�����ָʱ����һ�Σ��ɿ���ָʱ�ֵ���һ��
	 * 1������ʱ����¼���µ�λ��downX��downY
	 * 2�����������֣�x��yΪ���ֵ�λ�ã�dx��dyΪ�����ľ��루����ƫ������
	 * 3�����ƫ����С��50���Ͳ�����������50���ж������ܲ����ƶ��������ƶ���GAMEOVER��
	 * 4���ж���ָ�����ķ�����ִ����Ӧ�Ļ����������������ֺ�����һ���µ��������2��4������¼һ�η���
	 * @author: Joysing
	 * @ModifyTime: 2016��12��24�� ����2:23:19
	 * @methodName: onTouchEvent
	 * returnType: boolean
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		float x=event.getX();
		float y=event.getY();
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:downX=x;downY=y;break;//����ʱ����¼���µ�λ��
		case MotionEvent.ACTION_UP://���������֣�x��yΪ���ֵ�λ�ã�dx��dyΪ�����ľ��루����ƫ������
			float dx=x-downX;
			float dy=y-downY;
			if(Math.abs(dx)<50&&Math.abs(dy)<50)//ƫ����С��50��������
				return super.onTouchEvent(event);
			if(canMove()){
				if(move(dx, dy)){
					addNum(1);
					tvCurrScore.setText(String.format("��ǰ��ߣ�%d", maxScore));//ÿ����һ�Σ���¼һ��
				}
			}else gameOver();
			break;
		}
		return super.onTouchEvent(event);
	}
	/**
	 * ��Ϸ������������ʾ�򣬼�¼��������ʾ�����ѡ�����¿�ʼ�����ߡ��˳���������������ѡ����ӵ�������
	 * @author: Joysing
	 * @ModifyTime: 2016��12��25�� ����10:19:17
	 * @methodName: gameOver
	 * returnType: void
	 */
	private void gameOver(){
		String msg="";
		if(maxScore<256)msg="���������";
		else if (maxScore<512)msg="�����ͯ�Ӽ�";
		else if (maxScore<2048)msg="��������ͼ�";
		else if (maxScore<4096)msg="�������֭��";
		else if (maxScore<8192)msg="�����������";
		else msg="������ϵ¼�";
		new AlertDialog.Builder(this)
		.setMessage(msg)
		.setTitle("Game Over")
		.setNegativeButton("���¿�ʼ", dialogClickLis)
		.setNeutralButton("�˳�", dialogClickLis)
		.setCancelable(true).show();
		saveScore();
	}
	/**
	 * ���¿�ʼ��Ϸ
	 * 1����ȡ��ʷ��߷�
	 * 2��forѭ����ʼ��ÿһ������
	 * 3�����������λ�ò��������������2��4��
	 * @author: Joysing
	 * @ModifyTime: 2016��12��25�� ����10:19:50
	 * @methodName: restart
	 * returnType: void
	 */
	private void restart(){
		saveScore();
		tvhistoryScore.setText(String.format("��ʷ��ߣ�%d",getMaxScore()));
		for(int i=0;i<columnCount;i++){
			for(int j=0;j<columnCount;j++){
				items[i][j].setNum(0);
			}
		}
		addNum(2);//�������������
	}
	/**
	 * �Ի������Ӧ�¼���
	 * ��Ϸ����ʱ�����ĶԻ���
	 * �����¿�ʼ����ִ��restart()����
	 * ���˳�����رյ�ǰActivity�����˳����򣨵�ǰֻ��һ��Activity��
	 * ѡ���Ѷȣ�
	 * ѡ���һ�������Ϸ������Ϊ4
	 * ѡ��ڶ��������Ϸ������Ϊ5
	 * ѡ������������Ϸ������Ϊ6
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
	 * ������������������ʷ��߾͸�����ʷ���
	 * @author: Joysing
	 * @ModifyTime: 2016��12��25�� ����10:21:04
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
	 * ��ȡ��߷�
	 * @author: Joysing
	 * @ModifyTime: 2016��12��25�� ����11:02:40
	 * @methodName: getMaxScore
	 * @return
	 * returnType: int
	 */
	private int getMaxScore(){
		return getSharedPreferences("level",MODE_PRIVATE).getInt(null, 0);
	}
	/**
	 * �ж���ָ�����ķ��򣬲�ִ����Ӧ�ķ���
	 * @author: Joysing
	 * @ModifyTime: 2016��12��24�� ����9:28:40
	 * @methodName: move
	 * @param x ˮƽ�����ң�����ƫ����
	 * @param y ��ֱ�����£�����ƫ����
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
	 * �ж���Ϸ���ܷ��ƶ��������ƶ���GAMEOVER
	 * 1��forѭ������ÿһ�������пշ��񣨷�����������0��ʱ����true��
	 * 2�����û�пշ����ˣ��ж�һ�������������ң��Ƿ������ֺ�����ͬ�����Ժϲ������оͷ���true��
	 * 3�����������������ϣ�����false��GAMEOVER
	 * @author: Joysing
	 * @ModifyTime: 2016��12��24�� ����2:28:28
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
	 * ִ�����»����Ĳ������������»���ʱ����false
	 * 1���������±���ÿһ�������ȱ�����һ�У�Ȼ��ڶ��У��Դ����ƣ���
	 * 2����ÿ����������������������Ϊ0���Ͱ���������nums����������0����zeroIndex��¼���λ�á�
	 * 3���ж��ж�ȡ����������һ��ȡ�����Ƿ���ͬ��lastNum����������һ��ȡ��������lastNum=-1˵������ȡ�����ǵ�һ�������ܸ���һ�����ϲ�����
	 * 4�����ȡ����������һ��ȡ������ͬ�������һ�����ϲ����ټ���nums���顣
	 * 5�����˳��ȡ��nums���������������������뷽���
	 * 6�����nums��Ȼ��ʼ���еڶ��С�����
	 * @author: Joysing
	 * @ModifyTime: 2016��12��24�� ����2:34:44
	 * @methodName: moveToBottom
	 * @return
	 * returnType: boolean
	 */
	private boolean moveToBottom() {
		boolean f=false;//�ܲ�����������򻬶�
		int zeroIndex=-1;//��¼0��λ�ã����հ׵�λ��
		for(int j = 0;j<columnCount;j++){
			for(int i=columnCount-1;i>=0;i--){//�������±���ÿһ������
				int num=items[i][j].getNum();// 
				if(num!=0){//�жϷ���������ʱ
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
			for(int i=columnCount-1;i>=0;i--){//ѭ��ȡ��nums�����������������������items����
				if(columnCount-1-i<nums.size()){
					int num=nums.get(columnCount-1-i);
					if(maxScore<num)
						maxScore=num;
					items[i][j].setNum(num);
				}
				else items[i][j].setNum(0);//ȡ��ȫ�����󣬺���յ�items������Ϊ0
			}
			nums.clear();//ÿѭ��һ�У����һ��nums
			lastNum=-1;
			zeroIndex=-1;
		}
		return f;
	}
	/**
	 * ִ�����ϻ����Ĳ������������ϻ���ʱ����false
	 * ����ʵ��������moveToBottom����
	 * Author��Joysing
	 * @return
	 * 2016��12��24�� ����2:34:41
	 */
	private boolean moveToTop() {
		boolean f=false;
		int zeroIndex=-1;//��¼0��λ�ã����հ׵�λ��
		for(int j = 0;j<columnCount;j++){
			for(int i=0;i<columnCount;i++){//�������±���ÿһ������
				int num=items[i][j].getNum();//numΪ���������
				if(num!=0){//�жϷ���������ʱ
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
			for(int i=0;i<columnCount;i++){//ѭ��ȡ��nums�����������������������items����
				if(i<nums.size()){
					int num=nums.get(i);
					if(maxScore<num)
						maxScore=num;
					items[i][j].setNum(num);
				}
				else items[i][j].setNum(0);//ȡ��ȫ�����󣬺���յ�items������Ϊ0
			}
			nums.clear();//ÿѭ��һ�У����һ��nums
			lastNum=-1;
			zeroIndex=-1;
		}
		return f;
	}
	/**
	 * ִ�����һ����Ĳ������������һ���ʱ����false
	 * ����ʵ��������moveToBottom����
	 * Author��Joysing
	 * @return
	 * 2016��12��24�� ����2:34:35
	 */
	private boolean moveToRight() {
		boolean f=false;
		int zeroIndex=-1;//��¼0��λ�ã����հ׵�λ��
		for(int i=0;i<columnCount;i++){
			for(int j=columnCount-1;j>=0;j--){//�����������ÿһ������
				int num=items[i][j].getNum();//numΪ���������
				if(num!=0){//�жϷ���������ʱ
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
			for(int j=columnCount-1;j>=0;j--){//ѭ��ȡ��nums�����������������������items����
				if(columnCount-1-j<nums.size()){
					int num=nums.get(columnCount-1-j);
					if(maxScore<num)
						maxScore=num;
					items[i][j].setNum(num);
				}
				else items[i][j].setNum(0);//ȡ��ȫ�����󣬺���յ�items������Ϊ0
			}
			nums.clear();//ÿѭ��һ�У����һ��nums
			lastNum=-1;
			zeroIndex=-1;
		}
		return f;
	}
	/**
	 * ִ�����󻬶��Ĳ������������󻬶�ʱ����false
	 * ����ʵ��������moveToBottom����
	 * Author��Joysing
	 * @return
	 * 2016��12��24�� ����2:34:31
	 */
	private boolean moveToLeft() {
		boolean f=false;
		int zeroIndex=-1;//��¼0��λ�ã����հ׵�λ��
		for(int i=0;i<columnCount;i++){
			for(int j = 0;j<columnCount;j++){//�������ұ���ÿһ������
				int num=items[i][j].getNum();//numΪ���������
				if(num!=0){//�жϷ���������ʱ
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
			for(int j=0;j<columnCount;j++){//ѭ��ȡ��nums�����������������������items����
				if(j<nums.size()){
					int num=nums.get(j);
					if(maxScore<num)
						maxScore=num;
					items[i][j].setNum(nums.get(j));
				}
				else items[i][j].setNum(0);//ȡ��ȫ�����󣬺���յ�items������Ϊ0
			}
			nums.clear();//ÿѭ��һ�У����һ��nums
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
