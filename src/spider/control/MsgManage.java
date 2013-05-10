package spider.control;

import java.util.ArrayList;
import java.util.List;

import spider.dao.MsgDAO;
import spider.model.Msg;
import spider.model.SpiderInfo;
import spider.util.ContentURLMaker;
import spider.util.HttpHtml;
import spider.util.TextExtract;
import spider.util.WeiXinLog;

public class MsgManage {
	private List<Msg>latestMsg;			//保存最新的一条消息，默认以这条消息作为下一次爬虫爬到的消息是否为最新的
	private MsgDAO dao;
	private String replace_regx="<([^iatb/]|(a[^\\s])|(/a\\w+)|(/t[^adhr])|(/[^at])|(i[^m])|(b[^r])|(t[^adhr]))[^><]*>";
	public MsgManage()
	{
		latestMsg=new ArrayList<Msg>();
		dao=new MsgDAO();
	}
	public void pushMsg(ArrayList<Msg>msglist,SpiderInfo sp,HttpHtml http) throws Exception
	{
		if(msglist.isEmpty())
			return;
		Msg msg=msglist.get(0);
		int mem_index=this.msgBinarySearch(latestMsg,msg.getRid());
		if(mem_index>=0)
		{
			int index=0;
			while (index < msglist.size())
			{
				msg=msglist.get(index);
				// 说明该消息的栏目ID已经存在内存数组中
				if (latestMsg.get(mem_index).getUrl().equals(msg.getUrl()))
				{
					// 将msgList中索引从0到index写入到数据库并跟新lateMsg
					if(index!=0)
					{
						System.out.println("有"+index+"条新消息:"+sp.getUrl());
						this.writeMsgToDB(msglist, index, sp, http);
						latestMsg.get(mem_index).setUrl(msglist.get(0).getUrl());
					}
					else
					{
						System.out.println("无新消息:"+sp.getUrl());
					}
					return;
				} else 
				{
					++index;
				}
			}
			//latestMsg.get(mem_index).setTitle(msglist.get(0).getTitle());
			//dao.writeMsgDB(msglist, 0, index);
			//this.writeMsgToDB(msglist, index, sp, http);
		}
		else
		{
			//如果该消息没有在内存数组中
			//创建该栏目ID对应的内存数组
			System.out.println("没找到,正在创建内存索引。。。");
			Msg newmsg=new Msg();
			newmsg.setRid(msg.getRid());
			newmsg.setUrl(msg.getUrl());
			latestMsg.add(newmsg);
			int index=0;
			while(index<msglist.size())
			{
				//查询数据中是否有该消息的记录
				//如果有就直接退出
				msg=msglist.get(index);
				if(dao.isMsgExist(msg))
				{
					this.writeMsgToDB(msglist, index, sp, http);
					return;
				}
				++index;
			}
			this.writeMsgToDB(msglist, index, sp, http);
		}
	}
	/**
	 * 二分搜索消息,只是为了提高搜索速度
	 * @param data
	 * @param value
	 * @return
	 */
	private int msgBinarySearch(List<Msg>data,int id)
	{
		if(data.isEmpty())
			return -1;
		int left=0;
		int right=data.size()-1;
		int mid=(left+right)/2;
		while(left<right&&data.get(mid).getRid()!=id)
		{
			if(data.get(mid).getRid()>id)
				right=mid-1;
			else
				left=mid+1;
			mid=(left+right)/2;
		}
		return data.get(mid).getRid()==id?mid:-1;
	}
	private void writeMsgToDB(List<Msg>msg,int end,SpiderInfo sp,HttpHtml http) throws Exception
	{
		if(end==0)
			return;
		this.getMsgContent(msg, end, sp, http);
		dao.writeMsgDB(msg, 0, end);
		WeiXinLog.SendMsgToWeiXin(msg, end);
	}
	private void getMsgContent(List<Msg>msg,int end,SpiderInfo sp,HttpHtml http)
	{
		String html;
		int index=0;
		for (int i=0;i<end;++i)
		{
			Msg m=msg.get(i);
		//	String contentUrl = ContentURLMaker.urlMake(
		//			sp.getUrl(), sp.getUrlpr(), m.getUrl());
			//m.setUrl(contentUrl);
			html = http.getHtml(m.getUrl(),sp.getcCharset());
			sp.setcCharset(http.getEncoding());
			System.out.println("url:" + index+m.getUrl());
			if (html != null) {
				html = TextExtract.extractText(html, sp.getCrex(), 1);
			}
			if (html != null) {
				html = html.trim();
				//html = html.replaceAll("(?s)(<.*?>)|(&nbsp;)", "");
				html=html.replaceAll(replace_regx, "");
			}
			m.setContent(html);
			++index;
		}
	}
}
