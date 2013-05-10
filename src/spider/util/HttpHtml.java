package spider.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpHtml {
	/**
	 * ��ȡָ��URLҳ��
	 * @param urlҪ��ȡ��ҳ��
	 * @return ����HTML�ĵ����������ı��ĵ�
	 */
	private String chasetRex;
	private Pattern charsetPattern;
	private String contenttypeCharset;
	private	Pattern contenttypePattern;
	private String	charset;	//���ڵ���getHtml����ʶ������ı���
	private byte[]htmlBuf;
	public HttpHtml()
	{
		chasetRex="<meta[^>]*charset=([^\\s|^\"|^>|^/]+)[^>]*>";		//���HTML������������
		charsetPattern=Pattern.compile(chasetRex, Pattern.DOTALL|Pattern.MULTILINE);
		contenttypeCharset="charset=([^;|.]*)";
		contenttypePattern=Pattern.compile(contenttypeCharset,Pattern.DOTALL|Pattern.MULTILINE);
		htmlBuf=new byte[1024*1024*5];		//Ĭ�Ͽ�5M��HTML����
		charset=null;
	}
	public  String getHtml(String addr,String encoding)
	{
		String html=null;
		this.charset=encoding;
		try
		{
			URL url=new URL(addr);
			HttpURLConnection conn=(HttpURLConnection)url.openConnection();
			conn.setConnectTimeout(10*1000);	//10������ӳ�ʱ
			conn.setReadTimeout(20*1000);		//15��Ķ�ȡ��ʱ
			conn.setDoInput(true);
			conn.connect();
			/*
			 * ����������ı����ֶ�Ϊ����˵������վ�ǵ�һ����������δ֪�����������Ƚϸ���
			 */
			if(charset==null)
			{
				html=this.readWithoutEncoding(conn);
				conn.disconnect();
				return html;
			}
			else
			{
				html=this.readWithEncoding(conn);
				conn.disconnect();
				return html;
			}
		} catch (Exception e) {
			Log.writeLog("HttpHtml throw exception message:"+e.getMessage()+" URL:"+addr);
			System.out.println(Log.getLastLog());
		}
		return null;
	}
	/**
	 * �ڱ�����֪������½��ж�ȡHTML��Ĭ��charset�����Ϊ�ռ��ṩ����
	 * @param in
	 * @return
	 * @throws Exception
	 */
	private String readWithEncoding(HttpURLConnection conn) throws Exception
	{
		InputStream in=conn.getInputStream();
		BufferedReader buf=new BufferedReader(new InputStreamReader(in,this.charset));
		String str="";
		String tmp;
		while((tmp=buf.readLine())!=null)
			str+=tmp;
		in.close();
		return str;
			
	}
	/**
	 * �ڱ���δ֪�������ʶ�����Ȼ����ȷ��ȡ
	 * @return
	 */
	private String readWithoutEncoding(HttpURLConnection conn) throws Exception
	{
		InputStream in=conn.getInputStream();
		int readlen=0;
		int contentlen=0;
		String html=null;
		do
		{
			//һ�ζ�ȡ1KB���������ֵС��0������Ϊһ����ȡ���
			contentlen+=readlen;
			readlen=in.read(htmlBuf, contentlen, 1024);
		}while(readlen>0);
		charset = conn.getContentEncoding(); // �����û��content-encoding����ֶ�
		if (charset == null)
		{
			// ��ƥ��content-type����ֶ�
			Matcher m = this.contenttypePattern.matcher(conn.getContentType());
			if (m.find()) {
				charset = m.group(1);
				if (charset != null && charset.indexOf(";") >= 0) {
					charset = charset.substring(0, charset.indexOf(";"));
				}
				html = new String(htmlBuf, 0, contentlen + 1, charset);
			} else {
				// �����û��������GB2312�����ٴ�����meta�ֶ����ұ���
				html = new String(htmlBuf, 0, contentlen, "gb2312");
				m = this.charsetPattern.matcher(html);
				if (m.find())
					charset = m.group(1);
				if (charset != null && !charset.toLowerCase().equals("gb2312")) {
					html = new String(htmlBuf, 0, contentlen + 1, charset);
				}
				// �����û�У������վ�ĳ���Ա����ȥ����
			}
		} else
			html = new String(htmlBuf, 0, contentlen, charset);
		in.close();
		return html;
	}
	/*
	 * ��������Ƿ���������������Ҫ������֣���Ҷ�����ô�ɵ�
	 */
	public static boolean isNetAvailable()
	{
		URL url=null;
		try
		{
			url=new URL("http://www.baidu.com");
			InputStream in=url.openStream();
			in.close();
		}
		catch(Exception e)
		{
			return false;
		}
		return true;
	}
	public String getEncoding()
	{
		return this.charset;
	}
}
