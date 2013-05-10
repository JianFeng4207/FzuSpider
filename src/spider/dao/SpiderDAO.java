package spider.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import spider.model.SpiderInfo;
import spider.util.ConnectDB;

public class SpiderDAO {
	private Connection conn;
	private String sql_getspiderinf;
	private PreparedStatement ps;
	public List<SpiderInfo>getPageRecords()
	{
		List<SpiderInfo>lst=null;
		try
		{
			this.conn=ConnectDB.getDBConnect();
			this.sql_getspiderinf="select * from resource";
			ps=conn.prepareStatement(sql_getspiderinf);
			ResultSet rs=ps.executeQuery();
			lst=new ArrayList<SpiderInfo>();
			while(rs.next())
			{
				SpiderInfo sp=new SpiderInfo();
				sp.setUid(rs.getInt(1));
				sp.setCid(rs.getInt(4));
				sp.setOrder(rs.getString(5));
				sp.setUrl(rs.getString(6));
				sp.setUrlpr(rs.getString(7));
				sp.setHrex(rs.getString(8));
				sp.setCrex(rs.getString(9));
				lst.add(sp);
			}
			rs.close();
			ps.close();
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return lst;
	}
}
