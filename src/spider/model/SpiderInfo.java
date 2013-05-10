package spider.model;

import spider.util.Log;

public class SpiderInfo {
	private int uid;		//标识每个URL url id
	private int cid;		//表示每个类别 content id
	private String url;
	private String hrex;	//获取标题和url正则也就是主页正则HOME REX
	private String crex;	//获取正文正则即content rex
	private String order;	//表示正则子模式对应的字段顺序U:URL T:TITLE，目前只用到这两个
	private String urlpr;	//每个url前缀，一个最终正文URL=host+正则抓取的内容+urlpr
	
	private int error=0;		//该URL错误次数,如果错误大于配置设置的最大错误数则将该错误打进LOG里
	
	private String hCharset=null;	//首页编码即url指向页面的编码
	private String cCharset=null;	//内容页面的编码方式
	public int getError() {
		return error;
	}
	public void errorTrigger()
	{
		++error;
		if(error==1000)
		{
			Log.writeLog(url+"长时间未响应");
		}
	}
	public void resetError()
	{
		error=0;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String gethCharset() {
		return hCharset;
	}
	public void sethCharset(String hCharset) {
		this.hCharset = hCharset;
	}
	public String getcCharset() {
		return cCharset;
	}
	public void setcCharset(String cCharset) {
		this.cCharset = cCharset;
	}
	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getHrex() {
		return hrex;
	}
	public void setHrex(String hrex) {
		this.hrex = hrex;
	}
	public String getCrex() {
		return crex;
	}
	public void setCrex(String crex) {
		this.crex = crex;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getUrlpr() {
		return urlpr;
	}
	public void setUrlpr(String urlpr) {
		this.urlpr = urlpr;
	}
}
