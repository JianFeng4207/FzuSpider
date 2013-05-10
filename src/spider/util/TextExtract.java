package spider.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import spider.model.IObjectCreate;
import spider.model.IValueSet;
import spider.model.Msg;

public class TextExtract {
	/**
	 * ����regx��text����ȡ�ı�����������浽ʵ�ֽӿ�IValueSet�б���
	 * ���ݲ��ҵ����ַ��������ν�group�е�������䵽rs��
	 * @param text	Դ�ı��ַ���
	 * @param regx	������ʽ
	 * @param rs	�����
	 * @param cr	������Ԫ�ؽӿ�
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
	 * @param text		Դ�ı��ַ���
	 * @param out_regx	���������ʽ
	 * @param in_regx	�ڲ�������ʽ
	 * @param rs		�����
	 */
	public static void extractTextEx(String text,String out_regx,String in_regx,List<IValueSet>rs)
	{
		
	}
}
