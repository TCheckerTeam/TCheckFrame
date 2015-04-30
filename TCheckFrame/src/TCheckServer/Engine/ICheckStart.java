package TCheckServer.Engine;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Properties;

import TCheckServer.DBMS.DBManager;
import TCheckServer.Util.TCheckerLog;


public class ICheckStart {

	private static TCheckerLog   TCHECKERLOG  = null;
	private static DBManager     DBMANAGER    = null;
 
	public static void main(String[] args) {
		try{
			String procname = ManagementFactory.getRuntimeMXBean().getName(); 
			BufferedWriter bw = new BufferedWriter(new FileWriter(new String("./tdown.pid")));
			bw.write(procname.split("@")[0]);
	        bw.close();
	        
	        File dir1 = new File("./Log");
			if (!dir1.exists()) dir1.mkdir();
			
	        File dir2 = new File("./Download");
			if (!dir2.exists()) dir2.mkdir();
			
		}catch(Exception e){}
		
		LicenseInfo();
		TCHECKERLOG  = new TCheckerLog();
		DBMANAGER    = new DBManager();
 
		if (DBMANAGER.ServerDBConnection() != true) {
			System.out.println("DB Connection Error...");
			System.out.println("Check the DB Information...");
			return;
		}
		
		//Manager Port Listener Start
		BootManagerListener();
		
		//Inbound Port Start
		BootTcpListener();
		
		
		//SOAP(EBXML) Port Listener Start
		BootUrlListener();
 
		while(true){
			try{
				System.out.println("Processing...");
				Thread.sleep(10000);
			 
			}catch(Exception e){}
		}
		 
	}
	
	public static void BootManagerListener()
	{
        String Manager_Port = "";
		try {
	        Properties properties = new Properties();
	        properties.load(new FileInputStream("./Properties/System.inf"));
	        Manager_Port = properties.getProperty("MANAGER_PORT", "60000");
		} catch (Exception e) {
			if (Manager_Port.equals("")) Manager_Port = "60000";
		}
		
		CommData commdata = new CommData();
		commdata.SetTCheckerLog(TCHECKERLOG);
		commdata.SetDBManager(DBMANAGER);
 
		commdata.SetLU_NAME(Manager_Port);
		
		ThreadManagerMain thread = new ThreadManagerMain (commdata);
		thread.start();
	}
	public static void BootTcpListener()
	{
		CommData commdata = new CommData();
		commdata.SetTCheckerLog(TCHECKERLOG);
		commdata.SetDBManager(DBMANAGER);
 
		ThreadTcpMain threadtcpmain = new ThreadTcpMain (commdata);
		threadtcpmain.start();
	}
 
	public static void BootUrlListener()
	{
		CommData commdata = new CommData();
		commdata.SetTCheckerLog(TCHECKERLOG);
		commdata.SetDBManager(DBMANAGER);
 
		ThreadUrlMain threadurlmain = new ThreadUrlMain (commdata);
		threadurlmain.start();
	}
	private static void LicenseInfo()
	{
			try {
	 
				File dataDir = new File("./License/License.dat" );
				DataInputStream inn = new DataInputStream(new FileInputStream(dataDir));
		  		int len = (int) dataDir.length();
	  		    byte buf[] = new byte[len];
	  		    inn.readFully(buf);
	  		    inn.close();
	  		    
	  		    byte[] Data1 = hexToByteArray(new String(buf));
	  		    byte[] Data2 = hexToByteArray(new String(Data1));
	  	        String Data = new String(Data2);
	  	        String[] arrData = Data.split("\n");
	 
	  	        String[] arrTmp = arrData[2].split("\t");
	  	        if (arrTmp[1].equals("D")){
	  	        	System.out.println("Licesne 종류 : Demo License");
 
	  	        	arrTmp = arrData[5].split("\t");
	  	        	System.out.println("Licesne 기간 : " + arrTmp[1]);
	  	        	
	  	        	arrTmp = arrData[3].split("\t");
	  	        	System.out.println("Licesne 서버 : " + arrTmp[1]);
	  	        }
	  	        else {
	  	        	System.out.println("Licesne 종류 : Release License");
	  	        	System.out.println("Licesne 기간 : " + "9999-12-31");
	  	        	
	  	        	arrTmp = arrData[3].split("\t");
	  	        	System.out.println("Licesne 서버 : " + arrTmp[1]);
	  	        }
	   
 
			}catch(IOException e){
				System.out.println("License 정보 조회시 오류가 발생하였습니다. : ./License/License.dat");
				System.exit(0);
			}
	}
	private static byte[] hexToByteArray(String hex)
	{

	    int i = 0;

	    hex = hex.replace(" ","");
	    hex = hex.replace("-","");
	    hex = hex.replace("\n","");
	    
	    if (hex == null || hex.length() == 0 ) return null;
	    byte[] chk = hex.getBytes();
	    for(i = 0;i < chk.length; i++) {
	        if (  !((chk[i] >= '0' && chk[i] <= '9') || (chk[i] >= 'a' && chk[i] <= 'f') || (chk[i] >= 'A' && chk[i] <= 'F')) )
	        {
	              return null;
	        }
	    }

	    byte[] ba = new byte[hex.length()/2];

	    for(i = 0;i < ba.length; i++) {
	        ba[i] = (byte)Integer.parseInt(hex.substring(i*2,i*2+2),16);
	    }
	    return ba;
	}
}
