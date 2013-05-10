package spider.model;

import spider.util.Log;

public class SpiderInfo {
	private int uid;		//��ʶÿ��URL url id
	private int cid;		//��ʾÿ����� content id
	private String url;
	private String hrex;	//��ȡ�����url����Ҳ������ҳ����HOME REX
	private String crex;	//��ȡ��������content rex
	private String order;	//��ʾ������ģʽ��Ӧ���ֶ�˳��U:URL T:TITLE��Ŀǰֻ�õ�������
	private String urlpr;	//ÿ��urlǰ׺��һ����������URL=host+����ץȡ������+urlpr
	
	private int error=0;		//��URL�������,�����������������õ����������򽫸ô�����LOG��
	
	private String hCharset=null;	//��ҳ���뼴urlָ��ҳ��ı���
	private String cCharset=null;	//����ҳ��ı��뷽ʽ
	public int getError() {
		return error;
	}
	public void errorTrigger()
	{
		++error;
		if(error==1000)
		{
			Log.writeLog(url+"��ʱ��δ��Ӧ");
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
