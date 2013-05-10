package spider.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import spider.model.Msg;

/**
 * 如果有新的通知则通过微信发送到手机上
 * @author JianFeng
 *
 */
public class WeiXinLog {
	public static void SendMsgToWeiXin(List<Msg>msg,int end)
	{
		WeiXin weixin=new WeiXin();
		String path=System.getProperty("user.dir")+"\\config\\weixin-config.xml";
		boolean bok=weixin.loadConfig(path);
		if(bok&&weixin.login())
		{
			for(int i=0;i<end;++i)
			{
				Msg m=msg.get(i);
				DateFormat format=new SimpleDateFormat("MM-dd HH:mm:ss");
				String time_str=format.format(Calendar.getInstance().getTime());
				String msg_str=time_str+":"+m.getUrl()+m.getTitle();
				weixin.sendMsg(msg_str);
			}
		}
	}
	public static void SendException(String msg)
	{
		WeiXin weixin=new WeiXin();
		String path=System.getProperty("user.dir")+"\\config\\weixin-config.xml";
		boolean bok=weixin.loadConfig(path);
		if(bok&&weixin.login())
		{
				DateFormat format=new SimpleDateFormat("MM-dd HH:mm:ss");
				String time_str=format.format(Calendar.getInstance().getTime());
				String msg_str="error:"+time_str+msg;
				weixin.sendMsg(msg_str);
		}
	}
	public static void SendRunState(int count)
	{
		int hours=count/60;
		WeiXin weixin=new WeiXin();
		String path=System.getProperty("user.dir")+"\\config\\weixin-config.xml";
		boolean bok=weixin.loadConfig(path);
		if(bok&&weixin.login())
		{
				DateFormat format=new SimpleDateFormat("MM-dd HH:mm:ss");
				String time_str=format.format(Calendar.getInstance().getTime());
				String msg_str=time_str+":Spider have runed "+hours+"hours";
				weixin.sendMsg(msg_str);
		}
	}
}
