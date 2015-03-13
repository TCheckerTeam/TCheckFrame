package TCheckServer.Util;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class TCheckerLog {

	public static void WriteLog(String pstatus, String plogname, String pmsg) {
		SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
		String reg_dt = formatter.format(new java.util.Date()); //등록 일시
		formatter = new java.text.SimpleDateFormat("[HH:mm:ss]", java.util.Locale.KOREA);
		String reg_tm = formatter.format(new java.util.Date());  //등록시간 
		
		Exception e = new Exception();
		StackTraceElement ste[] = e.getStackTrace();
		String hmsg = "(" + pstatus + ") " + reg_tm ;
		hmsg = hmsg + "[" + ste[1].getFileName() + ":" + ste[1].getLineNumber() + "] ";
		hmsg = hmsg + pmsg;
		
		//hmsg = hmsg + ste[1].getClassName() + "." + ste[1].getMethodName() + "(";

		
		try{
			FileWriter out = new FileWriter("./Log/" + plogname + "_" + reg_dt + ".log",true);
			PrintWriter log  = new PrintWriter(out,true);
			log.println(hmsg );
			out.close();
			log.close();
		}catch(IOException io){}
	}
	
	public static void WriteLog(String pstatus, String plogname, Exception pe) {
		SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
		String reg_dt = formatter.format(new java.util.Date()); //등록 일시
		formatter = new java.text.SimpleDateFormat("[HH:mm:ss]", java.util.Locale.KOREA);
		String reg_tm = formatter.format(new java.util.Date());  //등록시간 
		
		Exception e = new Exception();
		StackTraceElement ste[] = e.getStackTrace();
		String hmsg = "(" + pstatus + ") " + reg_tm ;
		hmsg = hmsg + "[" + ste[1].getFileName() + ":" + ste[1].getLineNumber() + "] ";
		hmsg = hmsg + pe.getMessage();
 
		StackTraceElement[] ste_msg = pe.getStackTrace();
		for(int k=0;k < ste_msg.length;k++){
			hmsg = hmsg + reg_tm + ste_msg[k].toString() + "\n";
		}
		
		try{
			FileWriter out = new FileWriter("./Log/" + plogname + "_" + reg_dt + ".log",true);
 
			PrintWriter log  = new PrintWriter(out,true);
			log.println(hmsg );
			if (pe != null) pe.printStackTrace(log);
			out.close();
			log.close();
		}catch(IOException io){}
		
	}
    public boolean getLogLevel(String pstatus, String plogname)
    {
        try {
	        Properties properties = new Properties();
	        properties.load(new FileInputStream("./Properties/System.inf"));
	        String mgr = properties.getProperty("LOG_LEVEL_MGR", "E");
	        String db = properties.getProperty("LOG_LEVEL_DB", "E");
	        String tcp = properties.getProperty("LOG_LEVEL_TCP", "E");
	        String url = properties.getProperty("LOG_LEVEL_URL", "E");
	        
	        String loglevel = "";
	        if (plogname.equals("ManagerMain")) loglevel = mgr;
	        if (plogname.equals("DBManager"))   loglevel = db;
	        if (plogname.equals("TcpManager"))  loglevel = tcp;
	        if (plogname.equals("UrlManager"))  loglevel = url;
 
	        if (pstatus.equals("D")) {
	        	if (loglevel.equals("D")) return true;
	        	if (loglevel.equals("I")) return true;
	        	if (loglevel.equals("W")) return true;
	        	if (loglevel.equals("E")) return true;
	        }
	        if (pstatus.equals("I")) {
	        	if (loglevel.equals("I")) return true;
	        	if (loglevel.equals("W")) return true;
	        	if (loglevel.equals("E")) return true;
	        }
	        if (pstatus.equals("W")) {
	        	if (loglevel.equals("W")) return true;
	        	if (loglevel.equals("E")) return true;
	        }
	        if (pstatus.equals("E")) {
	        	if (loglevel.equals("E")) return true;
	        }
		} catch (Exception e) {
			return false;
		}
		return false;
    }
}
 
