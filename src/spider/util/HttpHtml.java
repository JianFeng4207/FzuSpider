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
	 * 获取指定URL页面
	 * @param url要获取的页面
	 * @return 返回HTML文档或者其他文本文档
	 */
	private String chasetRex;
	private Pattern charsetPattern;
	private String contenttypeCharset;
	private	Pattern contenttypePattern;
	private String	charset;	//对于调用getHtml程序识别出来的编码
	private byte[]htmlBuf;
	public HttpHtml()
	{
		chasetRex="<meta[^>]*charset=([^\\s|^\"|^>|^/]+)[^>]*>";		//检测HTML声明编码正则
		charsetPattern=Pattern.compile(chasetRex, Pattern.DOTALL|Pattern.MULTILINE);
		contenttypeCharset="charset=([^;|.]*)";
		contenttypePattern=Pattern.compile(contenttypeCharset,Pattern.DOTALL|Pattern.MULTILINE);
		htmlBuf=new byte[1024*1024*5];		//默认开5M的HTML缓存
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
			conn.setConnectTimeout(10*1000);	//10秒的连接超时
			conn.setReadTimeout(20*1000);		//15秒的读取超时
			conn.setDoInput(true);
			conn.connect();
			/*
			 * 如果传进来的编码字段为空则说明该网站是第一次爬，程序未知其编码则情况比较复杂
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
	 * 在编码已知的情况下进行读取HTML，默认charset如果不为空即提供编码
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
	 * 在编码未知的情况下识别编码然后正确读取
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
			//一次读取1KB，如果返回值小于0可以认为一定读取完毕
			contentlen+=readlen;
			readlen=in.read(htmlBuf, contentlen, 1024);
		}while(readlen>0);
		charset = conn.getContentEncoding(); // 检查有没有content-encoding这个字段
		if (charset == null)
		{
			// 再匹配content-type这个字段
			Matcher m = this.contenttypePattern.matcher(conn.getContentType());
			if (m.find()) {
				charset = m.group(1);
				if (charset != null && charset.indexOf(";") >= 0) {
					charset = charset.substring(0, charset.indexOf(";"));
				}
				html = new String(htmlBuf, 0, contentlen + 1, charset);
			} else {
				// 如果还没有则先用GB2312解码再从正文meta字段中找编码
				html = new String(htmlBuf, 0, contentlen, "gb2312");
				m = this.charsetPattern.matcher(html);
				if (m.find())
					charset = m.group(1);
				if (charset != null && !charset.toLowerCase().equals("gb2312")) {
					html = new String(htmlBuf, 0, contentlen + 1, charset);
				}
				// 如果还没有，则该网站的程序员可以去死了
			}
		} else
			html = new String(htmlBuf, 0, contentlen, charset);
		in.close();
		return html;
	}
	/*
	 * 检测网络是否正常。。。。不要觉得奇怪，大家都是这么干的
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
