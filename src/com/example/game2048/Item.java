package com.example.game2048;

import android.content.Context;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.TextView;

/**
 * ��������������Ϸ�������
 * @author Joysing
 * @E-mail: im@joysing.cc
 * @version CreateTime��2016��12��24�� ����10:11:46
 */
public class Item extends TextView {
	
	private int num;//����
	private int backgroundColor;//���ֵı�����ɫ
	private Point point;//����ָ��
	private GridLayout.LayoutParams lp;//���ӵĿ��
	/**
	 * ���캯��
	 * @param context null
	 */
	public Item(Context context) {
		super(context);
		//��ʼ��lp
		lp=new GridLayout.LayoutParams();
		setGravity(Gravity.CENTER);//������ʾ�ڷ������м�
		setLayoutParams(lp);
		// TODO Auto-generated constructor stub
	}
	/**
	 * ���ص�ǰ������
	 * @author: Joysing
	 * @ModifyTime: 2016��12��24�� ����12:35:16
	 * @methodName: getNum
	 * @return
	 * returnType: int
	 */
	public int getNum(){
		return this.num;
	}
	/**
	 * �������ֵ���ʾ
	 * @author: Joysing
	 * @ModifyTime: 2016��12��24�� ����12:45:27
	 * @methodName: setNum
	 * @param num Ҫ��ʾ������
	 * returnType: void
	 */
	public void setNum(int num){
		this.num=num;
		switch(num){//��ɫ�������ֲ�ͬ����ͬ
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
	 * ��ȡ���ֵ�����λ�ã����ָ꣩��
	 * @author: Joysing
	 * @ModifyTime: 2016��12��24�� ����10:50:35
	 * @methodName: getPoint
	 * @return
	 * returnType: Point
	 */
	public Point getPoint(){
		return point;
	}
	/**
	 * ���÷��������ָ��
	 * @author: Joysing
	 * @ModifyTime: 2016��12��24�� ����10:51:21
	 * @methodName: setPoint
	 * @param row �ڼ���
	 * @param column �ڼ���
	 * returnType: void
	 */
	public void setPoint(int row,int column){
		if(point==null)point=new Point();
		point.x=column;//�ڼ���
		point.y=row;//�ڼ���
		lp.rowSpec=GridLayout.spec(row);//��
		lp.columnSpec=GridLayout.spec(column);//��
	}
	/**
	 * ������Ϸ����﷽��Ĵ�С����߾�
	 * @author: Joysing
	 * @ModifyTime: 2016��12��24�� ����10:58:06
	 * @methodName: setSize
	 * @param size ����Ŀ��
	 * @param divider �����һ������ı߾�
	 * @param count һ�з��������
	 * returnType: void
	 */
	public void setSize(int size,int divider,int count){
		//����Ŀ��
		lp.width=size;
		lp.height=size;
		
		//�������߾�
		int helfDivider=(int) Math.ceil(divider/2f);//Ϊ�˳�����Ϊ0
		lp.leftMargin=helfDivider;
		lp.rightMargin=helfDivider;
		lp.topMargin=helfDivider;
		lp.bottomMargin=helfDivider;
		
		if(point.x==0)lp.leftMargin=divider;//�����������ʱ
		else if(point.x==count-1)lp.rightMargin=divider;//���������ұ�ʱ
		if(point.y==0)lp.topMargin=divider;//���������ϱ�ʱ
		else if(point.y==count-1)lp.bottomMargin=divider;//���������±�ʱ
		setTextSize(TypedValue.COMPLEX_UNIT_PX, size/3);//���ֵĴ�С
	}

}
