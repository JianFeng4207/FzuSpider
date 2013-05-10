package spider.model;

import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

public class Config {
	public static String DATABASEURL;
	public static String DATABASEDRIVER;
	public static String DATABASEUSERNAME;
	public static String DATABASEPASSWORD;
	public static String LOGFILE;
	public static boolean Init(String xmlpath)
	{
		try {
			Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(new File(xmlpath));
			XPath xpath = XPathFactory.newInstance().newXPath();
			Config.DATABASEURL = Config.getValue(doc,xpath,"//database/url/text()");
			Config.DATABASEDRIVER = Config.getValue(doc,xpath,"//database/driver/text()");
			Config.DATABASEUSERNAME = Config.getValue(doc,xpath,"//database/username/text()");
			Config.DATABASEPASSWORD = Config.getValue(doc,xpath,"//database/password/text()");
			Config.LOGFILE=Config.getValue(doc, xpath, "//log/path/text()");
			doc=null;
			xpath=null;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	private static String getValue(Document document,XPath xpath,String xpr) throws Exception {
		if (document == null || xpath == null)
			return null;
		String value = null;
		XPathExpression expr = xpath.compile(xpr);
		value = (String) expr.evaluate(document, XPathConstants.STRING);
		return value;
	}
}
