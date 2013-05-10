package spider.model;

public class MsgCreator implements IObjectCreate{
	private String order;
	public MsgCreator(String od)
	{
		this.order=od;
	}
	@Override
	public IValueSet createObject() {
		return new Msg(order);
	}

}
