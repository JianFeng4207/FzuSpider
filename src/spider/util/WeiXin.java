package spider.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WeiXin {
	protected HttpURLConnection conn;
	private String cookie = null;
	private String token=null;
	private URL	   url;
	private List<WeiXinUser>id_list;
	private List<String>reciver_list;
	private int cur_user_index;		//当前使用用户在id_list中的索引
	public WeiXin()
	{
		cur_user_index=0;
		id_list=new ArrayList<WeiXinUser>();
		reciver_list=new ArrayList<String>();
	}
	public boolean loadConfig(String file)
	{
		try {
			File f=new File(file);
			Document document=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
			NodeList userNodes=document.getElementsByTagName("user");
			for(int i=0;i<userNodes.getLength();++i)
			{
				NodeList lst=userNodes.item(i).getChildNodes();
				WeiXinUser user=new WeiXinUser();
				for(int j=0;j<lst.getLength();++j)
				{
					Node n=lst.item(j);
					if(n.getNodeType()==Node.ELEMENT_NODE)
					{
						Element e=(Element)n;
						if(e.getNodeName().equals("id"))
						{
							user.setId(e.getTextContent());
						}
						else
						{
							if(e.getNodeName().equals("password"))
							{
								user.setPassword(e.getTextContent());
							}
						}
					}
				}
				id_list.add(user);	
			}
			NodeList reciverNode=document.getElementsByTagName("reciver");
			NodeList idNodes=reciverNode.item(0).getChildNodes();
			for(int i=0;i<idNodes.getLength();++i)
			{
				Node n=idNodes.item(i);
				if(n.getNodeType()==Node.ELEMENT_NODE)
				{
					reciver_list.add(((Element)n).getTextContent());
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
		return true;
	}
	public boolean login()
	{
		//尝试登陆次数
		int err_cnt=0;
		while(this.loginEx()==false)
		{
			err_cnt++;
			++cur_user_index;
			cur_user_index%=id_list.size();
			if(err_cnt==id_list.size())
				return false;
		}
		return true;
	}
	private boolean loginEx()
	{
		try {
			WeiXinUser user=id_list.get(cur_user_index);
			this.creatPostConnection("http://mp.weixin.qq.com/cgi-bin/login?lang=zh_CN");
			String post_str = "username="
					+ URLEncoder.encode(user.getId(), "utf-8") + "&pwd="
					+ user.getPassword() + "&imgcode=&f=json";
					//+ "&pwd=9f99db9c1d600d005fbca4802099c620&imgcode=&f=json";
			String html=this.postString(post_str);
			this.getCookie(conn);
			this.getToken(html);
			//System.out.println(html);
			//System.out.println(cookie);
			//System.out.println(token);
			if(!html.contains("\"ErrCode\": 0"))
				return false;
		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * 发送消息，如果消息发送失败则尝试换账号登陆重新发送，如果还是失败则返回false
	 * @param msg要发送的消息，采用UTF8编码
	 * @return 如果发送成功返回true否则返回false
	 */
	public boolean sendMsg(String msg)
	{
		if(this.sendMsgEx(msg)==false)
		{
			if(this.login()==false)
				return false;
			return this.sendMsgEx(msg);
		}
		return true;
	}
	public boolean sendMsgEx(String msg)
	{
		try
		{	
			this.creatPostConnection("http://mp.weixin.qq.com/cgi-bin/singlesend?t=ajax-response&lang=zh_CN");
			String post_str = "type=1&content="
					+ URLEncoder.encode(msg, "utf-8")
					+ "&error=false&tofakeid=41676845&ajax=1";
			if(token!=null)
			{
				post_str=post_str+"&token="+token;
			}
			String html=this.postString(post_str);
			System.out.println(html);
			if(!html.contains("\"msg\":\"ok\""))
				return false;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	private void creatPostConnection(String addr) throws Exception
	{
		//url=new URL("http://mp.weixin.qq.com/cgi-bin/login?lang=zh_CN");
		url=new URL(addr);
		conn=(HttpURLConnection)url.openConnection();
		if(this.cookie!=null&&this.cookie.length()>0)
			conn.setRequestProperty("Cookie", cookie);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Referer", "http://mp.weixin.qq.com/cgi-bin/singlemsgpage");
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.connect();
	}
	private String postString(String str) throws Exception
	{
		DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		out.writeBytes(str);
		out.flush();
		String line = null;
		String response_html = "";
		InputStream in = conn.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
		while ((line = reader.readLine()) != null) {
			response_html += line;
		}
		out.close();
		reader.close();
		conn.disconnect();
		return response_html;
	}
	private void getCookie(HttpURLConnection conn)
	{
		Map<String,List<String>>headers=conn.getHeaderFields();
		List<String>cookie_list=headers.get("Set-Cookie");
		if(cookie_list==null||cookie_list.size()==0)
			return;
		this.cookie="";
		for(String s:cookie_list)
		{
			this.cookie+=s.substring(0, s.indexOf(";")+1);
		}
	}
	private void getToken(String json)
	{
		String regx="token=([^\"|^\\s|.]*)";
		Pattern p=Pattern.compile(regx);
		Matcher m=p.matcher(json);
		token=null;
		if(m.find())
		{
			token=m.group(1);
		}
	}
}
class WeiXinUser
{
	private String id;
	private String password;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
