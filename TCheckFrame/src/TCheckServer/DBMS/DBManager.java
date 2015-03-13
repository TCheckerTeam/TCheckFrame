package TCheckServer.DBMS;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Properties;

import TCheckServer.Util.TCheckerLog;

public class DBManager {
	private Connection conn = null;
	private String gDbms_Type = "";
	private String gLog_Level = "";
	private TCheckerLog tcheckerlog = new TCheckerLog();
	public boolean ServerDBConnection(){
		try {
 
	        Properties properties = new Properties();
	        properties.load(new FileInputStream("./Properties/DBConnect.inf"));
	        String dbms_kind = properties.getProperty("DBMS_KIND");
	        String dbms_name = properties.getProperty("DBMS_NAME");
	        String dbms_ip = properties.getProperty("DBMS_IP");
	        String dbms_port = properties.getProperty("DBMS_PORT");
	        String dbms_id = properties.getProperty("DBMS_ID");
	        String dbms_pw = properties.getProperty("DBMS_PW");
	        String dbms_connection = "";
	        String dbms_driver = "";
 
            if (dbms_kind.toUpperCase().equals("ORACLE")){
            	gDbms_Type = "ORACLE";
            	dbms_driver = "oracle.jdbc.driver.OracleDriver";
            	dbms_connection = (new StringBuilder("jdbc:oracle:thin:@")).append(dbms_ip).append(":").append(dbms_port).append(":").append(dbms_name).toString();
            }
            if (dbms_kind.toUpperCase().equals("ORACLE11")){
            	gDbms_Type = "ORACLE";
            	dbms_driver = "oracle.jdbc.driver.OracleDriver";
            	dbms_connection = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=" + dbms_ip;
            	dbms_connection = dbms_connection + ")(PORT=" + dbms_port;
            	dbms_connection = dbms_connection + ")))(CONNECT_DATA=(service_name=" + dbms_name + ")))";
            	
            }    
            
            Properties props = new Properties();
            if (dbms_kind.toUpperCase().equals("ORACLE_UTF8")){
            	gDbms_Type = "ORACLE";
            	dbms_driver = "oracle.jdbc.driver.OracleDriver";
            	dbms_connection = (new StringBuilder("jdbc:oracle:thin:@")).append(dbms_ip).append(":").append(dbms_port).append(":").append(dbms_name).toString();

            	props.put("charSet","UTF-8");
            	props.put("user", dbms_id);
            	props.put("password",dbms_pw);
            	props.put("aaaa","UTF-8");
            	
            	
            }            
            if (dbms_kind.toUpperCase().equals("TIBERO")){
            	gDbms_Type = "TIBERO";
            	dbms_driver = "com.tmax.tibero.jdbc.TbDriver";
            	dbms_connection = (new StringBuilder("jdbc:tibero:thin:@")).append(dbms_ip).append(":").append(dbms_port).append(":").append(dbms_name).toString();
            }
            /* 2015.03.04 DB Type(DB2) 추가 */
            if (dbms_kind.toUpperCase().equals("DB2")){
            	gDbms_Type = "DB2";
            	dbms_driver = "com.ibm.db2.jcc.DB2Driver";
            	dbms_connection = (new StringBuilder("jdbc:db2://")).append(dbms_ip).append(":").append(dbms_port).append("/").append(dbms_name).toString();
            }
            
            tcheckerlog.WriteLog("I", "DBManager", "dbms_driver     = " + dbms_driver);
            tcheckerlog.WriteLog("I", "DBManager", "dbms_connection = " + dbms_connection);
	          
			Class.forName(dbms_driver);
			DriverManager.setLoginTimeout(10);
			
			if (dbms_kind.toUpperCase().equals("ORACLE_UTF8")){
				this.conn = DriverManager.getConnection(dbms_connection, props);
			}
			else {
			    this.conn = DriverManager.getConnection(dbms_connection, dbms_id, dbms_pw);
			}
			
 			if (this.conn.isClosed()) {
 				tcheckerlog.WriteLog("E", "DBManager", "dbms is Closed");
 				return false;
 			}
 			else {
 				this.conn.setAutoCommit(false);
 				tcheckerlog.WriteLog("I", "DBManager", "dbms is Connectioned");
 				return true;
 			}
 		}catch(Exception e) {
 			tcheckerlog.WriteLog("E", "DBManager", e);
 			return false;
		}
	}
	public void ServerDBDisconnect(){
		try{
			if (this.conn != null ) this.conn.close();
		}catch(Exception e){
			tcheckerlog.WriteLog("E", "DBManager", e);
		}
	}
	public void ServerDBCommit() {
		try {
		    this.conn.commit();
		}catch(Exception e){
			tcheckerlog.WriteLog("E", "DBManager", e);
		}
	}
	public void ServerDBRollback() {
		try {
		    this.conn.rollback();
		}catch(Exception e){
			tcheckerlog.WriteLog("E", "DBManager", e);
		}
	}
	
    public boolean ServerDBCheck()
    {
    	ResultSet rs = null;
    	String isql = "select count(1) from TCHECKER_USER";
		int    tmpint = 0;
 
		try{
			  PreparedStatement pstmt = this.conn.prepareStatement(isql);
	          rs = pstmt.executeQuery();
	          if (rs.next()){
	        	  tmpint = rs.getInt(1);
	          }
	          if (rs != null) rs.close();
	          if (pstmt != null) pstmt.close();
              if (tmpint >= 1) return true;
		}catch(Exception e){
			  if (ServerDBConnection() == true) return true;
			  tcheckerlog.WriteLog("E", "DBManager", e);
		}
    	return false;
    }

	public String SearchData(String isql)
	{
		ResultSet rs = null;
		String tmp = "";
        String retdata = "";
        
		try{
 
			  if (this.gDbms_Type.equals("DB2")){
			      isql = isql.replace("NVL", "COALESCE");
			  }
			  
			  if (isql.indexOf("[NOLOGGING]") >= 0) {
				  isql = isql.replace("[NOLOGGING]", "");
			  }
			  else {
		          tcheckerlog.WriteLog("D", "DBManager", isql);
			  }
			  
			  if (ServerDBCheck() != true){
				  tcheckerlog.WriteLog("E", "DBManager", "DB Connection is false");
				  return null;
			  }
			  
			  

		 
			  
			  PreparedStatement pstmt = this.conn.prepareStatement(isql);
	          rs = pstmt.executeQuery();
	          rs.setFetchSize(1000);
	          
	          /* 컬럼명 및 타입을 구한다. */
	          ResultSetMetaData rsmd = rs.getMetaData();
	          int colcnt = rsmd.getColumnCount(); 
	     
	          /* 실재 Data를 구한다. */
	          while(rs.next())
	          {
		          	for(int i = 1; i <= colcnt; i++)
		          	{
		          	    switch(rsmd.getColumnType(i))
		          	    {
			          	    case Types.NUMERIC:
			          	    case Types.INTEGER: tmp = ""+rs.getInt(i);break;
			          	    case Types.FLOAT: tmp =  ""+rs.getFloat(i);break;
			          	    case Types.DOUBLE: tmp = ""+rs.getDouble(i);break;
			          	    case Types.DATE: tmp =   ""+rs.getDate(i);break;
			          	    case Types.CHAR:
			          	    default : tmp =   ""+rs.getString(i);
		          	    }
		          	    if ( tmp.equals("null") )  tmp = "";
		          	    if ( i == 1) retdata = retdata + tmp;
		          	    else retdata = retdata + "\t" + tmp;
		          	}
		          	retdata = retdata + "\n";
	          }
	          if (rs != null) rs.close();
	          if (pstmt != null) pstmt.close();


	          return retdata;
          
		}catch(SQLException e){
			tcheckerlog.WriteLog("E", "DBManager", e);
		}
        return null;
	}
	public Boolean UpdateData(String isql)
	{
		try{
			if (ServerDBCheck() != true){
			    tcheckerlog.WriteLog("E", "DBManager", "DB Connection is false");
				return null;
			}
			  
			tcheckerlog.WriteLog("D", "DBManager", isql);
		    Statement stmt = this.conn.createStatement();
		    int rc = stmt.executeUpdate(isql);
		    if (stmt != null) stmt.close();
		    
			if (rc > 0) return true;
			return false;
		}catch(SQLException e){
			tcheckerlog.WriteLog("E", "DBManager", e);
			try{
			   this.conn.rollback();
			}catch(Exception ee){}
		}
        return false;
	}
	
	public void DBDataExport()
	{
		/* NOT USER */
		int i = 0;
		int j = 0;
		int colcnt = 0;
		String tmp = "";
		String data = "";
		ResultSet rs = null;
		String[] tbname = { "ALAPPL"
				,"ALGRP"
				,"ALGRPLINE"
				,"ALGW"
				,"ALGWSUB"
				,"ALINST"
				,"ALINSTMAP"
				,"ALKIND"
				,"ALKINDMAP"
				,"ALLINE"
				,"ALLINESUB"
				,"ALMAPPER"
			 	,"ALMSG"
			 	,"ALMSGFLD"
			 	,"ALMSGNO"
			 	,"ALMSGRSC"
				,"ALNODE"
				,"ALPAUSE"
				,"ALPROPS"
				,"ALREASONCODE"
				,"ALRESCODE"
				,"ALTX"
				,"ALTXCALL"
				,"ALTXFLOW"
				,"ALTXMAP"
			 	,"ALTXMAPFLD"
				,"ALTXPARM"
				,"ALTXPROTOCOL"
				,"ALURL"
				,"ALUSER"
				,"ALUSERLOG"
				,"ALUSERROLE"
				,"TCHECKER_LINEINFO"
				,"TCHECKER_PERMIT"
				,"TCHECKER_RESLINK"
				,"TCHECKER_TXDETAIL"
				,"TCHECKER_USER"
				,null};
 
		String DIRNAME="./DBExport";
 
		try{
            File file = new File(DIRNAME);
            if (!file.exists()) file.mkdir();
            
			for(int k = 0 ; ;k++) {
				if (tbname[k] == null) break;
				String isql = "select * from " + tbname[k];
				PreparedStatement lstmt = this.conn.prepareStatement(isql);
	              rs = lstmt.executeQuery();
	              rs.setFetchSize(1000);
				  
				  /* 컬럼명 및 타입을 구한다. */
				  ResultSetMetaData rsmd = rs.getMetaData();
				  colcnt = rsmd.getColumnCount(); 
 
		      	  /* 실재 Data를 구한다. */
				  BufferedWriter bw = new BufferedWriter(new FileWriter(DIRNAME + "/" + tbname[k] + ".dat"));
			      for(j = 1; j <= colcnt; j++)
			      {
			    	  bw.write(rsmd.getColumnName(j) + "\t");
			      }
			      bw.write("\n");
			      
			      for(j = 1; j <= colcnt; j++)
			      {
			    	  rsmd.getColumnName(1);
			    	  if (rsmd.getColumnType(j) == Types.VARCHAR || rsmd.getColumnType(j) == Types.CHAR) {
			    		  bw.write("C\t");
			    	  }
			    	  else {
			    		  bw.write("N\t");
			    	  }
			      }
			      bw.write("\n");
			      
			      while(rs.next())
			      {
			      	data = "";
			      	for(j = 1; j <= colcnt; j++)
			      	{
			      	    switch(rsmd.getColumnType(j))
			      	    {
				      	    case Types.NUMERIC:
				      	    case Types.INTEGER: tmp = ""+rs.getInt(j);break;
				      	    case Types.FLOAT:   tmp = ""+rs.getFloat(j);break;
				      	    case Types.DOUBLE:  tmp = ""+rs.getDouble(j);break;
				      	    case Types.DATE:    tmp = ""+rs.getDate(j);break;
				      	    case Types.CHAR:
				      	    default : tmp = ""+rs.getString(j);
			      	    }
			      	    if ( tmp.trim().equals("null") )  tmp = "<NODATA>";
			      	    if (tmp.trim().equals("")) tmp = "<NODATA>";
			      	    
	                    data = data + tmp + "\t";
			      	    
	                    System.out.println(j + ":" + rsmd.getColumnType(j) + ":" + tmp);
			      	}
			      	bw.write(data + "\n");
			       
			      }
			      bw.close();
			      if (rs != null) rs.close();	
			      if (lstmt != null) rs.close();
			}
 
		}catch(SQLException e){
			System.out.println(" SQLException : " + e.getMessage());
			 
			StackTraceElement[] ste = e.getStackTrace();
			for(int k=0;k < ste.length;k++){
				System.out.println(" SQLException : " + ste[k].toString());
			}
		}catch(IOException e){
			System.out.println(" SQLException : " + e.getMessage());
			 
			StackTraceElement[] ste = e.getStackTrace();
			for(int k=0;k < ste.length;k++){
				System.out.println(" SQLException : " + ste[k].toString());
			}
		}
  
	}
	public void DBDataImport()
	{
		String DIRNAME="./DBImport";
		String sql1 = "";
		String sql2 = "";
		 
		try{
			File file = new File(DIRNAME);
            if (!file.exists()) file.mkdir();
            
			String buffer = "";
            File[] filelist = new File("./DBExport").listFiles();
            for(int i=0;i < filelist.length ;i++){
    			File dataDir = new File(filelist[i].getPath());
     
    			BufferedReader in = new BufferedReader( new FileReader ( dataDir ));
    			String[] collist = in.readLine().split("\t");
    			String[] typlist = in.readLine().split("\t");
    			String[] tnamelist = filelist[i].getPath().replace("\\", "\t").split("\t");
    			
    			sql1 = "INSERT INTO " + tnamelist[tnamelist.length - 1].replace(".dat","")  + "(";
    			for(int j=0;j < collist.length ;j++){
    				if (collist[j].trim().equals("")) break;
    				if (j > 0) sql1 = sql1 + ",";
    				sql1 = sql1 + collist[j].trim();
    			}
    			sql1 = sql1 + ") VALUES (";
    			
    			BufferedWriter bw = new BufferedWriter(new FileWriter(filelist[i].getPath().replace("DBExport","DBImport")));
				while( (buffer = in.readLine()) != null ) {
					 if (buffer.trim().equals("")) break;
					 String[] arrtmp = buffer.split("\t");
					 
					 sql2 = "";
					 for(int k=0;k < arrtmp.length ;k++){
						 if (arrtmp[k].trim().equals("")) break;
						 
						 if (k > 0) sql2 = sql2 + ",";
						 if (typlist[k].equals("C")) sql2 = sql2 + "'";
						 sql2 = sql2 + arrtmp[k].trim().replace("<NODATA>","");
						 if (typlist[k].equals("C")) sql2 = sql2 + "'";
					 }
					 sql2 = sql2 + ");\n";
					 
					 sql2 = sql2.replace("iristech2","xrois-fep1");
					 sql2 = sql2.replace("121.126.79.29","192.168.37.30");
					 
					 bw.write(sql1 + sql2);
				}
				bw.close();
				in.close();
            }
		}catch(Exception e){
			e.printStackTrace();
		}
	}
 
	public static void main(String[] args) {
		//String parm0 = args[0];
		String parm0 = "IMP";
		
		if (parm0.toUpperCase().equals("EXP")){ 
			DBManager db = new DBManager();
			db.ServerDBConnection();
			db.DBDataExport();
		}
		if (parm0.toUpperCase().equals("IMP")){ 
			DBManager db = new DBManager();
			db.ServerDBConnection();
			db.DBDataImport();
		}
	}
}
