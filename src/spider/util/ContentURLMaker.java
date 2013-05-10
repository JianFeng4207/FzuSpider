package spider.util;

import java.net.URL;

public class ContentURLMaker {
	/**
	 * 生成正文URL，正文URL=host+前缀+regurl
	 * @param homeurl
	 * @param pre
	 * @param regurl
	 */
	public static String urlMake(String homeurl,String pre,String regurl)
	{
		URL hurl=null;
		try
		{	
			new URL(regurl);
		}
		catch(Exception e)
		{
			try
			{
				hurl=new URL(homeurl);
				//如果发生异常说明regurl是一个相对路径或者只是一个标识符而已
				//if(regurl.indexOf('/')!=0)
					//regurl="/"+regurl;
				regurl=hurl.getProtocol()+"://"+hurl.getHost()+pre+regurl;
			}catch(Exception ex)
			{
				System.out.println("解析URL出现错误:"+ex.getMessage());
			}
		}
		return regurl;
	}
}
