package spider.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import spider.model.Msg;
import spider.util.ConnectDB;

public class MsgDAO {
	private Connection conn;
	private PreparedStatement ps;
	public boolean isMsgExist(Msg m)
	{
		boolean bExist=false;
		try
		{
			conn=ConnectDB.getDBConnect();
			//如果两条消息出自同一个URL而且正文URL相同则可以判定这两条为同一条消息
			String sql="select _id from message where rid=? AND url=?";
			ps=conn.prepareStatement(sql);
			ps.setInt(1, m.getRid());
			ps.setString(2, m.getUrl());
			ResultSet rs=ps.executeQuery();
			bExist=rs.next();
			rs.close();
			ps.close();
			conn.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return bExist;
	}
	public void writeMsgDB(List<Msg>msglist,int start,int end)
	{
		try
		{
		if(msglist.isEmpty()||start==end)
			return;
		conn=ConnectDB.getDBConnect();
		String sql="insert message values(?,?,?,?,?,?)";
		ps=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		for(int i=start;i<end;++i)
		{
			Msg m=msglist.get(i);
			ps.setInt(1, 0);
			ps.setInt(2, m.getRid());
			ps.setString(3, m.getUrl());
			ps.setString(4, m.getTitle());
			ps.setString(5, m.getContent());
			ps.setDate(6, null);
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if(rs.next())
			{
				//在这边可以向服务器发送value让服务器知道新消息ID
				int value=rs.getInt(1);
				System.out.println("key:"+value);
			}
		}
		ps.close();
		conn.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
