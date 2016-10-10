package com.example.mobilesafe1.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

public class SmsBackup {

	
	private static int index = 0;


	//备份短信方法
	public static void backup(Context context,String path, final CallBack callBack) {

		//需要用到的对象上下文环境,备份文件夹路径,进度条所在的对话框对象用于备份过程中进度的更新
		//1,获取备份短信写入的文件
		File file = new File(path);
		//2,获取内容解析器,获取短信数据库中数据
		final Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/"),
				new String[]{"address","date","type","body"}, null, null, null);
		FileOutputStream fos = null;
		//3,序列化数据库中读取的数据,放置到xml中
		XmlSerializer serializer = Xml.newSerializer();
		try {
			//4.获取存储备份短信文件的输出流
			fos = new FileOutputStream(file);
			//5,给次xml做相应设置
			serializer.setOutput(fos, "utf-8");
			//DTD(xml规范)
			serializer.startDocument("utf-8", true);
			serializer.startTag(null, "smss");
			//6,备份短信总数指定
			if(callBack!=null){
				callBack.setMax(cursor.getCount());
			}
			//7,读取数据库中的每一行的数据写入到xml中
			while(cursor.moveToNext()){
				serializer.startTag(null, "sms");
				
				serializer.startTag(null, "address");
				serializer.text(cursor.getString(0));
				serializer.endTag(null, "address");
				
				serializer.startTag(null, "date");
				serializer.text(cursor.getString(1));
				serializer.endTag(null, "date");
				
				serializer.startTag(null, "type");
				serializer.text(cursor.getString(2));
				serializer.endTag(null, "type");
				
				serializer.startTag(null, "body");
				serializer.text(cursor.getString(3));
				serializer.endTag(null, "body");
				
				serializer.endTag(null, "sms");
				
				//8,每循环一次就需要去让进度条叠加
				index ++;
				Thread.sleep(500);
				if(callBack!=null){
					callBack.setProgress(index);
				}
			}
			serializer.endTag(null, "smss");
			serializer.endDocument();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(cursor!=null&&fos!=null){
				cursor.close();
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	//观察者设计模式
	//回调
	//1.定义一个接口
	//2,定义接口中未实现的业务逻辑方法(短信总数设置,备份过程中短信百分比更新)
	//3.传递一个实现了此接口的类的对象(至备份短信的工具类中),接口的实现类,一定实现了上诉两个为实现方法(就决定了使用对话框,还是进度条)
	//4.获取传递进来的对象,在合适的地方(设置总数,设置百分比的地方)做方法的调用
	public interface CallBack{
		//短信总数设置为实现方法(由自己决定是用	对话框.setMax(max) 还是用	进度条.setMax(max))
		public void setMax(int max);
		//备份过程中短信百分比更新(由自己决定是用	对话框.setProgress(max) 还是用	进度条.setProgress(max))
		public void setProgress(int index);
	}
}
