package spider.model;

public class Msg implements IValueSet{
	private String title;
	private String url;
	private String content;
	private String charset;
	private String date;
	private char[] order;	//赋值顺序
//	private int uid;	//标识是属于那个URL的消息
//	private int sid;	//标识是属于哪个类别的消息
	private int rid;	//源ID
	public String getCharset() {
		return charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}

	public int getRid() {
		return rid;
	}
	public void setRid(int rid) {
		this.rid = rid;
	}
	public char[] getOrder() {
		return order;
	}
	public void setOrder(char[] order) {
		this.order = order;
	}

	public Msg(String od)
	{
		od=od.toUpperCase();
		this.order=od.toCharArray();
	}
	public Msg()
	{
		String od="ut";
		this.order=od.toCharArray();
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	@Override
	public void setValue(int index, Object value) {
		if(index>=order.length)
		{
			return;
		}
		char id=order[index];
		switch(id)
		{
		case 'U':	//URL
			this.setUrl((String)value);
			break;
		case 'T':	//TITLE
			this.setTitle((String)value);
			break;
		case 'C':	//CONTENT
			this.setContent((String)value);
			break;
		case 'D':	//DATE
			this.setDate((String)value);
			break;
		default:break;
		}
	}
}
