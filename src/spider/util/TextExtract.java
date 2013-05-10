package spider.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import spider.model.IObjectCreate;
import spider.model.IValueSet;
import spider.model.Msg;

public class TextExtract {
	/**
	 * 根据regx从text中提取文本，讲结果保存到实现接口IValueSet列表中
	 * 根据查找到的字符串，依次将group中的数据填充到rs中
	 * @param text	源文本字符串
	 * @param regx	正则表达式
	 * @param rs	结果集
	 * @param cr	创建新元素接口
	 */
	public static void extractText(String text,String regx,List<Msg>rs,IObjectCreate cr)
	{
		Pattern p=Pattern.compile(regx, Pattern.MULTILINE|Pattern.DOTALL);
		Matcher m=p.matcher(text);
		while(m.find())
		{
			IValueSet ob=cr.createObject();
			int groupcnt=m.groupCount();
			for(int i=1;i<=groupcnt;++i)
			{
				String value=m.group(i).trim();
				ob.setValue(i-1, value);
			}
			rs.add((Msg)ob);
		}
	}
	public static String extractText(String text,String regx,int groupindex)
	{
		Pattern p=Pattern.compile(regx, Pattern.MULTILINE|Pattern.DOTALL);
		Matcher m=p.matcher(text);
		if(m.find())
			return m.group(groupindex);
		return null;
	}
	/**
	 * @param text		源文本字符串
	 * @param out_regx	外层正则表达式
	 * @param in_regx	内层正则表达式
	 * @param rs		结果集
	 */
	public static void extractTextEx(String text,String out_regx,String in_regx,List<IValueSet>rs)
	{
		
	}
}
