package spider.control;

import java.util.ArrayList;
import java.util.List;

import spider.dao.SpiderDAO;
import spider.model.Config;
import spider.model.Msg;
import spider.model.MsgCreator;
import spider.model.SpiderInfo;
import spider.util.ContentURLMaker;
import spider.util.HttpHtml;
import spider.util.Log;
import spider.util.TextExtract;
import spider.util.WeiXinLog;

public class Spider {
	private MsgManage msgManage;
	private SpiderDAO dao = new SpiderDAO();
	private ArrayList<Msg> msg = new ArrayList<Msg>();
	private List<SpiderInfo> url_list;
	private MsgCreator msgCreator;
	private HttpHtml http;

	public boolean prepare() {
		if (false == Config.Init(System.getProperty("user.dir")
				+ "/config/spider.xml")) {
			System.out.println("读取配置文件出错，请确认配置文件是否配置正确");
			return false;
		}
		msgManage = new MsgManage();
		url_list = dao.getPageRecords(); // 爬虫要抓取的URL
		http = new HttpHtml();
		return true;
	}

	public void work() {
		if (false == this.prepare())
			return;
		int index = 0;
		int null_cnt = 0; // 检测网络状态使用
		while (true) {
			try {
				null_cnt = 0;
				// SpiderInfo sp=url_list.get(3);
				// System.out.println(sp.getUrl());
				for (SpiderInfo sp : url_list) {
					// {
					String html = http.getHtml(sp.getUrl(), sp.gethCharset());
					if (html == null) {
						++null_cnt;
						sp.errorTrigger();
						// 如果连续三个URL都出错则开始检测是否是网络不正常
						if (null_cnt >= 3 && HttpHtml.isNetAvailable() == false) {
							throw new Exception("网络不可用，请检查网络");
						}
						continue;
					}
					sp.resetError(); // 如果没错误则重置错误
					sp.sethCharset(http.getEncoding());
					msgCreator = new MsgCreator(sp.getOrder());
					msg.clear();
					TextExtract.extractText(html, sp.getHrex(), msg, msgCreator);
					for (Msg m : msg) {
						m.setRid(sp.getUid());
						String contentUrl = ContentURLMaker.urlMake(
								sp.getUrl(), sp.getUrlpr(), m.getUrl());
						m.setUrl(contentUrl);
					}
					try {
						msgManage.pushMsg(msg, sp, http);
					} catch (Exception ex) {
						Log.writeLog("pushMsg throw exception");
					}
				}
				System.out.println("complete" + index);
				index++;
				System.out.println("Sleeping....");
				if (index % 60 == 0)
				{
					WeiXinLog.SendRunState(index);
					url_list = dao.getPageRecords();	//重新读取url
				}
				Thread.sleep(1000 * 20);
			} catch (Exception e) {
				Log.writeLog("spider error:" + e.getMessage());
				System.out.println(Log.getLastLog());
				try {
					System.out.println("Sleeping....");
					Thread.sleep(1000 * 60);
				} catch (Exception e1) {}
			}
		}
	}
}
