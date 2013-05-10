package spider.util;

import java.sql.Connection;
import java.sql.DriverManager;

import spider.model.Config;

public class ConnectDB {
	public static Connection getDBConnect() throws Exception
	{
		String user=Config.DATABASEUSERNAME;
		String password=Config.DATABASEPASSWORD;
		String connectStr=Config.DATABASEURL;
		String driver=Config.DATABASEDRIVER;
		Connection conn = null;
		Class.forName(driver).newInstance();
		conn = DriverManager.getConnection(connectStr, user, password);
		return conn;
	}
}
