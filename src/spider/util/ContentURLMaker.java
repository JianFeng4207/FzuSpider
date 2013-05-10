package spider.util;

import java.net.URL;

public class ContentURLMaker {
	/**
	 * ��������URL������URL=host+ǰ׺+regurl
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
				//��������쳣˵��regurl��һ�����·������ֻ��һ����ʶ������
				//if(regurl.indexOf('/')!=0)
					//regurl="/"+regurl;
				regurl=hurl.getProtocol()+"://"+hurl.getHost()+pre+regurl;
			}catch(Exception ex)
			{
				System.out.println("����URL���ִ���:"+ex.getMessage());
			}
		}
		return regurl;
	}
}
