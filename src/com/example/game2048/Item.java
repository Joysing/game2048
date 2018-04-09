package com.example.game2048;

import android.content.Context;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.TextView;

/**
 * 该类用于生成游戏方格面板
 * @author Joysing
 * @E-mail: im@joysing.cc
 * @version CreateTime：2016年12月24日 上午10:11:46
 */
public class Item extends TextView {
	
	private int num;//数字
	private int backgroundColor;//数字的背景颜色
	private Point point;//行列指针
	private GridLayout.LayoutParams lp;//格子的宽高
	/**
	 * 构造函数
	 * @param context null
	 */
	public Item(Context context) {
		super(context);
		//初始化lp
		lp=new GridLayout.LayoutParams();
		setGravity(Gravity.CENTER);//数字显示在方格正中间
		setLayoutParams(lp);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 返回当前的数字
	 * @author: Joysing
	 * @ModifyTime: 2016年12月24日 下午12:35:16
	 * @methodName: getNum
	 * @return
	 * returnType: int
	 */
	public int getNum(){
		return this.num;
	}
	/**
	 * 设置数字的显示
	 * @author: Joysing
	 * @ModifyTime: 2016年12月24日 下午12:45:27
	 * @methodName: setNum
	 * @param num 要显示的数字
	 * returnType: void
	 */
	public void setNum(int num){
		this.num=num;
		switch(num){//颜色随着数字不同而不同
		case 0:backgroundColor=R.color.bg_0;break;
		case 2:backgroundColor=R.color.bg_2;break;
		case 4:backgroundColor=R.color.bg_4;break;
		case 8:backgroundColor=R.color.bg_8;break;
		case 16:backgroundColor=R.color.bg_16;break;
		case 32:backgroundColor=R.color.bg_32;break;
		case 64:backgroundColor=R.color.bg_64;break;
		case 128:backgroundColor=R.color.bg_128;break;
		case 256:backgroundColor=R.color.bg_256;break;
		case 512:backgroundColor=R.color.bg_512;break;
		case 1024:backgroundColor=R.color.bg_1024;break;
		case 2048:backgroundColor=R.color.bg_2048;break;
		case 4096:backgroundColor=R.color.bg_4096;break;
		case 8192:backgroundColor=R.color.bg_8192;break;
		case 16384:backgroundColor=R.color.bg_16384;break;
		default:backgroundColor=R.color.bg_0;break;
		}
		if(num!=0){
			setText(String.valueOf(num));
		}else setText("");
		setBackgroundResource(backgroundColor);
	}
	/**
	 * 获取数字的所在位置（坐标）指针
	 * @author: Joysing
	 * @ModifyTime: 2016年12月24日 上午10:50:35
	 * @methodName: getPoint
	 * @return
	 * returnType: Point
	 */
	public Point getPoint(){
		return point;
	}
	/**
	 * 设置方格的坐标指针
	 * @author: Joysing
	 * @ModifyTime: 2016年12月24日 上午10:51:21
	 * @methodName: setPoint
	 * @param row 第几行
	 * @param column 第几列
	 * returnType: void
	 */
	public void setPoint(int row,int column){
		if(point==null)point=new Point();
		point.x=column;//第几列
		point.y=row;//第几行
		lp.rowSpec=GridLayout.spec(row);//行
		lp.columnSpec=GridLayout.spec(column);//列
	}
	/**
	 * 设置游戏面板里方格的大小和外边距
	 * @author: Joysing
	 * @ModifyTime: 2016年12月24日 上午10:58:06
	 * @methodName: setSize
	 * @param size 方格的宽高
	 * @param divider 面板里一个方格的边距
	 * @param count 一行方格的总数
	 * returnType: void
	 */
	public void setSize(int size,int divider,int count){
		//方格的宽高
		lp.width=size;
		lp.height=size;
		
		//方格的外边距
		int helfDivider=(int) Math.ceil(divider/2f);//为了除二后不为0
		lp.leftMargin=helfDivider;
		lp.rightMargin=helfDivider;
		lp.topMargin=helfDivider;
		lp.bottomMargin=helfDivider;
		
		if(point.x==0)lp.leftMargin=divider;//方格在最左边时
		else if(point.x==count-1)lp.rightMargin=divider;//方格在最右边时
		if(point.y==0)lp.topMargin=divider;//方格在最上边时
		else if(point.y==count-1)lp.bottomMargin=divider;//方格在最下边时
		setTextSize(TypedValue.COMPLEX_UNIT_PX, size/3);//数字的大小
	}

}
