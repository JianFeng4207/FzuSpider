package spider.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Calendar;

import spider.model.Config;

public class Log {
	private static String lastlog=null;
	public static void writeLog(String log)
	{
		try {
			PrintStream printStream=new PrintStream(new FileOutputStream(Config.LOGFILE,true));
			Calendar c=Calendar.getInstance();
			String time = "  " + c.get(Calendar.YEAR) + "-"
					+ (1 + c.get(Calendar.MONTH)) + "-"
					+ c.get(Calendar.DAY_OF_MONTH) + "  "
					+ c.get(Calendar.HOUR_OF_DAY) + ":"
					+ c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);
			lastlog=log+time;
			WeiXinLog.SendException(lastlog);
			printStream.println(lastlog);
			printStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public static String getLastLog()
	{
		return Log.lastlog;
	}
}
