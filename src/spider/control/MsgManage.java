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
	private List<Msg>latestMsg;			//�������µ�һ����Ϣ��Ĭ����������Ϣ��Ϊ��һ��������������Ϣ�Ƿ�Ϊ���µ�
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
				// ˵������Ϣ����ĿID�Ѿ������ڴ�������
				if (latestMsg.get(mem_index).getUrl().equals(msg.getUrl()))
				{
					// ��msgList��������0��indexд�뵽���ݿⲢ����lateMsg
					if(index!=0)
					{
						System.out.println("��"+index+"������Ϣ:"+sp.getUrl());
						this.writeMsgToDB(msglist, index, sp, http);
						latestMsg.get(mem_index).setUrl(msglist.get(0).getUrl());
					}
					else
					{
						System.out.println("������Ϣ:"+sp.getUrl());
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
			//�������Ϣû�����ڴ�������
			//��������ĿID��Ӧ���ڴ�����
			System.out.println("û�ҵ�,���ڴ����ڴ�����������");
			Msg newmsg=new Msg();
			newmsg.setRid(msg.getRid());
			newmsg.setUrl(msg.getUrl());
			latestMsg.add(newmsg);
			int index=0;
			while(index<msglist.size())
			{
				//��ѯ�������Ƿ��и���Ϣ�ļ�¼
				//����о�ֱ���˳�
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
	 * ����������Ϣ,ֻ��Ϊ����������ٶ�
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
