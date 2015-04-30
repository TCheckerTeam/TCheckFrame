package TCheckServer.Engine;

 
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Properties;

import TCheckServer.UserClass.UserFunction;
 
public class ThreadManagerMainSub extends Thread{ 
	private CommData COMMDATA = null;
	private UserFunction     userfunction = new UserFunction();
    private String      gLicenseGubun = "";
    private String      gLicenseManGi = "";
    private String      gLicenseHostName = "";
    private String      gLicenseANYIP = "";
    private String      AnylinkIP = "";
    private String      AnylinkPort = "";
    private Socket      client = null;
    public ThreadManagerMainSub(Socket client, CommData commdata)
    { 
    
    	this.COMMDATA = commdata;
    	this.client = client;
    	getSystemInfo();
     
    }
	public void run()
	{  
        DataInputStream dis = null;
        DataOutputStream dos = null;
        
		while(!Thread.currentThread().isInterrupted()) {
      	    //데이타부 길이정보 읽기
			try{
				if (client == null) COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", "client Socket is null");
				if (client.getInputStream() == null) COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", "client.getInputStream() Socket is null");
				if (client.getOutputStream() == null) COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", "client.getOutputStream() is null");
	            dis = new DataInputStream(client.getInputStream());
	            dos = new DataOutputStream(client.getOutputStream());
	            
			}catch(Exception e1){
				COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", e1);
				break;
			}
            
			
			
            int    tmplen = 0;
            byte[] tmpbyte1 = new byte[8];
            try{
               client.setSoTimeout(10000);
               for(int i = 0 ; i < 8 ;i++) {
            	   tmpbyte1[i] = dis.readByte();  
            	   tmplen++;
            	   client.setSoTimeout(1000);
               }
            }catch(EOFException e1){ 
            	break; 
		    }catch(Exception e2){
		    }
		    
            if (tmplen == 0) continue;
            if (tmplen != 8) break;
     
            COMMDATA.GetTCheckerLog().WriteLog("D", "MgrManager", "");
            COMMDATA.GetTCheckerLog().WriteLog("D", "MgrManager", " ************ Receive Msg ************ ");
            
            String datalen = new String(tmpbyte1);
      	    COMMDATA.GetTCheckerLog().WriteLog("D", "MgrManager", " Rcv datalen : [" + datalen + "]");
 
      	    //Command부 길이정보 읽기
            tmplen = 0;
            byte[] tmpbyte2 = new byte[32];
            try{
               for(int i = 0 ; i < 32 ;i++) {
            	   tmpbyte2[i] = dis.readByte();  
            	   tmplen++;
               }
            }catch(EOFException e1){ 
            	break; 
		    }catch(Exception e2){
		 
		    }
            if (tmplen < 32) break;
 
      	    String datacmd = new String(tmpbyte2);
      	    COMMDATA.GetTCheckerLog().WriteLog("D", "MgrManager", " Rcv datacmd : [" + datacmd + "]");
      	  
            //데이타부 읽기
            tmplen = 0;
            byte[] tmpbyte3 = new byte[Integer.parseInt(datalen)];
            try{
               for(int i = 0 ; i < tmpbyte3.length ;i++) {
            	   tmpbyte3[i] = dis.readByte();  
            	   tmplen++;
               }
            }catch(EOFException e1){ 
            	break; 
		    }catch(Exception e2){}
 
      	    String databuf = new String(tmpbyte3);
      	    COMMDATA.GetTCheckerLog().WriteLog("D", "MgrManager", "Rcv databuf : [" + databuf + "]");
      	    
      	    //Proc Parsing
      	    
      	    //Command에 따른 결과 리턴
            String retdata = "";
             if (datacmd.trim().equals("READ_LINEINFO")) retdata = Proc_DBSearchLineInfo();
             if (datacmd.trim().equals("SAVE_LINEINFO_LEN")) retdata = Proc_DBUpdateLineInfo_LEN(databuf);
             if (datacmd.trim().equals("READ_KINDTX_OFFSET_SIZE")) retdata = Proc_DBSearchKindTxOffsetSize();
             if (datacmd.trim().equals("READ_TLINEINFO")) retdata = Proc_DBSearchTchecker_LineInfo();
             if (datacmd.trim().equals("READ_SENDPORT")) retdata = Proc_DBSearchSendPort(databuf);
             

             if (datacmd.trim().equals("READ_APPLINFO")) retdata = Proc_DBSearchApplInfo();
             if (datacmd.trim().equals("READ_APPLTXINFO")) retdata = Proc_DBSearchApplTxInfo(databuf);
             if (datacmd.trim().equals("READ_TXDETAIL")) retdata = Proc_DBSearchTxDetail(databuf);
             if (datacmd.trim().equals("SAVE_TXDETAIL")) retdata = Proc_DBUpdateTxDetail(databuf);
            
             if (datacmd.trim().equals("READ_RESLINK")) retdata = Proc_DBSearchResLink(databuf);
             if (datacmd.trim().equals("READ_RESLINKONE")) retdata = Proc_DBSearchResLinkOne(databuf);
            
             if (datacmd.trim().equals("SAVE_RESLINK")) retdata = Proc_DBUpdateResLink(databuf);
             if (datacmd.trim().equals("DELETE_RESLINK")) retdata = Proc_DBDeleteResLink(databuf);
             
             if (datacmd.trim().equals("READ_USER")) retdata = Proc_DBSearchUser(databuf);
             if (datacmd.trim().equals("SAVE_USER")) retdata = Proc_DBUpdateUser(databuf);
             if (datacmd.trim().equals("DELETE_USER")) retdata = Proc_DBDeleteUser(databuf);
            
             if (datacmd.trim().equals("READ_PERMIT")) retdata = Proc_DBSearchPermit(databuf);
             if (datacmd.trim().equals("SAVE_PERMIT")) retdata = Proc_DBUpdatePermit(databuf);
             if (datacmd.trim().equals("READ_PERMITAPPL")) retdata = Proc_DBSearchPermitAppl(databuf);
             
             //hmkim : 2015-04-25 added-start
             if (datacmd.trim().equals("USER_INOUTTCPAPPL")) retdata = Proc_DBSearchInOutBoundTcpAppl(databuf);
             if (datacmd.trim().equals("USER_INOUTURLAPPL")) retdata = Proc_DBSearchInOutBoundUrlAppl(databuf);
             if (datacmd.trim().equals("USER_KINDTXLIST")) retdata = Proc_DBSearchKindTxList(databuf);
             //hmkim : 2015-04-25 added-end
            
             if (datacmd.trim().equals("READ_ADMINIP")) retdata = Proc_DBSearchAdminIP();

             if (datacmd.trim().equals("CHECK_USERID")) retdata = Proc_DBCheck_UserID(databuf);
             
             if (datacmd.trim().equals("READ_ASYNCAPPLIST")) retdata = Proc_DBSearchAsyncApplList();
             if (datacmd.trim().equals("READ_TXMAPPING_LIST")) retdata = Proc_DBSearchTxMappingList(databuf);
             
             if (datacmd.trim().equals("LOAD_USERHEADER")) retdata = Proc_LoadUserHeader(databuf);
             if (datacmd.trim().equals("SAVE_USERHEADER")) retdata = Proc_SaveUserHeader(databuf);
             
             
             if (datacmd.trim().equals("READ_WIRELESSRES")) {
          	     String userpcip = client.getInetAddress().toString().replace("/", "");
          	      retdata = Proc_DBSearchWirelessRes(databuf, userpcip);
             }
             if (datacmd.trim().equals("READ_INBOUDNURL")) retdata = Proc_DBSearchInBoundURL(databuf);
             if (datacmd.trim().equals("READ_OUTBOUDNURL")) retdata = Proc_DBSearchOutBoundURL(databuf);
             if (datacmd.trim().equals("READ_INOUTTXURL")) retdata = Proc_DBSearchInOutBoundURL();
             if (datacmd.trim().equals("READ_INOUTTXTCP")) retdata = Proc_DBSearchInOutBoundTcp();
             if (datacmd.trim().equals("READ_TCPMSGREQ")) retdata = Proc_DBLoadTCPMSGREQ(databuf);
             if (datacmd.trim().equals("READ_TCPMSGRES")) retdata = Proc_DBLoadTCPMSGRES(databuf);
             if (datacmd.trim().equals("READ_TCPMSGRES_FILE")) retdata = Proc_DBLoadTCPMSGRES_File(databuf);
             if (datacmd.trim().equals("READ_TCPMSGREQ_COMMHEAD")) retdata = Proc_DBLoadTCPMSGREQ_CommonHead(databuf);
             if (datacmd.trim().equals("READ_TCPMSGRES_COMMHEAD")) retdata = Proc_DBLoadTCPMSGRES_CommonHead(databuf);
             
             if (datacmd.trim().equals("SAVE_TCPREQRES_COMMHEAD")) retdata = Proc_DBSave_TCPREQRES_COMMHEAD(databuf);
             if (datacmd.trim().equals("SAVE_TCPMSGREQ")) retdata = Proc_DBSaveTCPMSGREQ(databuf);
             if (datacmd.trim().equals("SAVE_TCPMSGRES")) retdata = Proc_DBSaveTCPMSGRES(databuf);
             if (datacmd.trim().equals("SEND_TCPMSG")) {
          	     String userpcip = client.getInetAddress().toString().replace("/", "");
          	     retdata = Proc_DBSendTCPMSG(databuf, userpcip);
             }
             if (datacmd.trim().equals("SEND_TCPNONTYPEMSG")) {
          	     String userpcip = client.getInetAddress().toString().replace("/", "");
          	     retdata = Proc_DBSendTCPNONTYPEMSG(databuf, userpcip);
             }
             if (datacmd.trim().equals("SEND_TCPMAPPERTYPEMSG")) {
          	     String userpcip = client.getInetAddress().toString().replace("/", "");
          	     retdata = Proc_DBSendTCPMAPPERTYPEMSG(databuf, userpcip);
             }
             if (datacmd.trim().equals("SEND_TCPMAPPERALLMSG")) {
          	     String userpcip = client.getInetAddress().toString().replace("/", "");
          	     retdata = Proc_DBSendTCPMAPPERALLMSG(databuf, userpcip);
             }
             if (datacmd.trim().equals("READ_TCPAPPLCODE")) retdata = Proc_DBSearchTcpApplInfo();
 
             if (datacmd.trim().equals("SAVE_URLMSGRES")) retdata = Proc_DBSaveURLMSGRES(databuf);
             if (datacmd.trim().equals("LOAD_URLMSGREQ")) retdata = Proc_DBLoadURLMSGREQ(databuf);
             if (datacmd.trim().equals("LOAD_URLMSGRES")) retdata = Proc_DBLoadURLMSGRES(databuf);
             if (datacmd.trim().equals("SEND_URLMSG")) {
          	     String userpcip = client.getInetAddress().toString().replace("/", "");
          	     retdata = Proc_DBSendURLMSG(databuf, userpcip);
             }
             if (datacmd.trim().equals("READ_INTERNALINFO")) retdata = Proc_DBSearchInternalInfo(databuf);
             if (!datacmd.trim().equals("UIUPGRADE")) {
             	//Response Data Creation & Send
                 String SendLen = String.format("%08d", retdata.getBytes().length);
                 String SendStr = SendLen + retdata;
                 
                 COMMDATA.GetTCheckerLog().WriteLog("D", "MgrManager", "");
                 COMMDATA.GetTCheckerLog().WriteLog("D", "MgrManager", "************ Response Msg ************ ");
                 COMMDATA.GetTCheckerLog().WriteLog("D", "MgrManager", "Response : \n [" + SendStr + "]");
                 
                 try{
                	 
    	             dos.write(SendStr.getBytes(),0,SendStr.getBytes().length);
    	             dos.flush();
    	             COMMDATA.GetTCheckerLog().WriteLog("D", "MgrManager", "************ Response Msg END datacmd: ["+datacmd.trim()+"]");
    	             
                 }catch(Exception e1){
                	 COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", e1);
                 }
             }
             else {
            	 COMMDATA.GetTCheckerLog().WriteLog("D", "MgrManager", "Version Upgrade & DownLoad start ");
            	 byte[] versioninfo = null;
      			String fname = "./Download/version.inf";
    			File ff = new File(fname);
    			if (ff.exists()) {
    				try{
    					DataInputStream fdis = new DataInputStream(new FileInputStream(ff));
        	            int len  = (int) ff.length();
        	            versioninfo = new byte[len];
        	            fdis.readFully(versioninfo);
        	            fdis.close();
        	            
        	            COMMDATA.GetTCheckerLog().WriteLog("D", "MgrManager", "Client Version : ["+databuf+"] Upgrade Version : ["+(new String(versioninfo).trim())+"]");
        	            
        	            if (!databuf.trim().equals(new String(versioninfo).trim())) {
        	            	
        	            	File ffdat = new File("./Download/TCheckerUI.jar." + new String(versioninfo).trim());
        	            	if (ffdat.exists()) {
        	            		DataInputStream fdisdat = new DataInputStream(new FileInputStream(ffdat));
                	            int lendat  = (int) ffdat.length();
                	            byte[] ReadData = new byte[lendat];
                	            fdisdat.readFully(ReadData);
                	            fdisdat.close();
                	            
                	            String SendLen = String.format("%08d", ReadData.length + 8);
                	            dos.write(SendLen.getBytes(),0,SendLen.getBytes().length);
                	            dos.write(versioninfo, 0, versioninfo.length );
                	            dos.write(ReadData, 0, ReadData.length );
               	                dos.flush();
               	                COMMDATA.GetTCheckerLog().WriteLog("D", "MgrManager", "Version Upgrade : " + new String(versioninfo) + ":" + ReadData.length);
        	            	}else{
        	            		COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", "./Download/TCheckerUI.jar." + new String(versioninfo).trim()+" not Found ");
        	            	}
        	            }
    				}catch (Exception eee){
    					try{
    						String SendLen = String.format("%08d", 8);
            	            dos.write(SendLen.getBytes(),0,SendLen.getBytes().length);
            	            dos.write(databuf.getBytes(), 0, databuf.getBytes().length );
           	                dos.flush();
           	                COMMDATA.GetTCheckerLog().WriteLog("D", "MgrManager", "Version Upgrade - 화일 읽기를 실패하였습니다. : Version : " + new String(versioninfo).trim() );
    					}catch(Exception eeeee1){}
        	            
    				}
    			}
    			else {
    				try{
        	            String SendLen = String.format("%08d", 8);
        	            dos.write(SendLen.getBytes(),0,SendLen.getBytes().length);
        	            dos.write(databuf.getBytes(), 0, databuf.getBytes().length );
       	                dos.flush();
       	                COMMDATA.GetTCheckerLog().WriteLog("D", "MgrManager", "Version Upgrade - 화일를 찾을 수 없습니다 : ./Download/version.inf" );
    				}catch(Exception eeeee1){}

    			}
             }
 
		}
        try{ if(client != null) client.close();}catch(Exception e){};
        try{ if(dos != null) dos.close();}catch(Exception e){};
        try{ if(dis != null) dis.close();}catch(Exception e){};
        
        COMMDATA.GetTCheckerLog().WriteLog("D", "MgrManager", "Thread End ");
	}
	private String Proc_DBSearchLineInfo()
	{
		String isql = " SELECT B.APPL_CODE, Z.APPL_NAME, A.SYMBNAME, A.GWNAME, A.LU_NAME,A.CONNECT_TYPE,    ";

	    isql = isql + "\n CASE WHEN D.MSG_LEN_TYPE = 1 THEN CASE WHEN D.COMM_HEAD_TYPE = 1 THEN 0 ";
	    isql = isql + "\n                                        WHEN D.COMM_HEAD_TYPE = 2 THEN 1 ";
	    isql = isql + "\n                                   END                                   ";
	    isql = isql + "\n      ELSE D.MSG_LEN_TYPE                                                ";
	    isql = isql + "\n END AS HEAD_TYPE,                                                       ";
	    isql = isql + "\n D.COMM_HEAD_SIZE ";
	    isql = isql + "\n FROM ALLINE A, ALGRPLINE B, ALGW D , ALAPPL Z                                     ";
	    isql = isql + "\n WHERE A.SYMBNAME = B.SYMBNAME                                                     ";
	    isql = isql + "\n AND A.STA_TYPE != 9                                                               ";
	    isql = isql + "\n AND D.GWNAME = A.GWNAME                                                           ";
	    isql = isql + "\n AND A.CONNECT_TYPE in (1,2,3,7,10)                                              ";
	    isql = isql + "\n AND B.APPL_CODE = Z.APPL_CODE                                                     ";
	    isql = isql + "\n ORDER BY B.APPL_CODE,A.GWNAME,A.SYMBNAME, A.LU_NAME                               ";
 
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
 
		if (retdata == null || retdata.equals("")) return "NOT-FOUND";

		return retdata;
	}
	private String Proc_DBSearchTchecker_LineInfo()
	{
		String isql = " SELECT T.APPL_CODE , T.LU_NAME , T.CONNECT_TYPE , T.STA_TYPE, T.COMM_HEAD_TYPE , T.COMM_HEAD_SIZE , ";
	    isql = isql + "        T.LEN_TYPE  , T.LEN_OFFST , T.LEN_SIZE ";
	    isql = isql + "\n FROM TCHECKER_LINEINFO T                                                           ";
	    isql = isql + "\n ORDER BY T.APPL_CODE , T.LU_NAME , T.CONNECT_TYPE                                       ";
 
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
 
		if (retdata == null || retdata.equals("")) return "NOT-FOUND";

		return retdata;
	}
	
	private String Proc_DBSearchSendPort(String pApplCode)
	{
		String isql = " select b.lu_name from algrpline a, alline b ";
	    isql = isql + "\n where a.symbname = b.symbname                  ";
	    isql = isql + "\n    and a.gwname = b.gwname                       ";
	    isql = isql + "\n    and a.appl_code = '" + pApplCode + "'         ";
	    isql = isql + "\n    and b.sta_type = 1                                  ";
	    isql = isql + "\n    and b.DIRECTION != 2                             ";
 
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
 
		if (retdata == null || retdata.equals("")) return "NOT-FOUND";

		return retdata;
	}
	
	private String Proc_DBUpdateLineInfo_LEN(String savedata)
	{
 
		//TCHECKER_LINEINFO 에 있는 모든 데이타를 삭제한다.
		COMMDATA.GetDBManager().UpdateData("delete from TCHECKER_LINEINFO");
 
		String[] arrdata = savedata.split("\n");
		for(int i=0;i < arrdata.length ;i++){
			if (arrdata[i].equals("")) break;
			String[] arrtmp = arrdata[i].split("\t");
			String uSql = "";
			String iSql = "";
            try {
    			uSql = "  Update TCHECKER_LINEINFO Set ";
    			uSql = uSql + "\n   LEN_TYPE   = " + arrtmp[6];
    			uSql = uSql + "\n , LEN_OFFST  = " + arrtmp[7];
    			uSql = uSql + "\n , LEN_SIZE   = " + arrtmp[8];
    			uSql = uSql + "\n , STA_TYPE   = " + arrtmp[9];
    			uSql = uSql + "\n  Where APPL_CODE    = '" + arrtmp[0] + "' ";
    			uSql = uSql + "\n    AND LU_NAME      = '" + arrtmp[2] + "' ";
    			uSql = uSql + "\n    AND CONNECT_TYPE =  " + arrtmp[3] + "  ";
 
    			
    			iSql = " Insert Into TCHECKER_LINEINFO (";
    			iSql = iSql + "\n APPL_CODE      , ";
                iSql = iSql + "\n LU_NAME        , ";
                iSql = iSql + "\n CONNECT_TYPE   , ";
                iSql = iSql + "\n COMM_HEAD_TYPE , ";
                iSql = iSql + "\n COMM_HEAD_SIZE , ";
                iSql = iSql + "\n LEN_TYPE       , ";
                iSql = iSql + "\n LEN_OFFST      , ";
                iSql = iSql + "\n LEN_SIZE       , ";
                iSql = iSql + "\n STA_TYPE       ) values ( ";
                iSql = iSql + "\n '" + arrtmp[0]  + "', ";
                iSql = iSql + "\n '" + arrtmp[2]  + "', ";
                iSql = iSql + "\n '" + arrtmp[3]  + "', ";
                iSql = iSql + "\n  " + arrtmp[4]  + " , ";
                iSql = iSql + "\n  " + arrtmp[5]  + " , ";
                iSql = iSql + "\n  " + arrtmp[6]  + " , ";
                iSql = iSql + "\n  " + arrtmp[7]  + " , ";
                iSql = iSql + "\n  " + arrtmp[8]  + " , ";
                iSql = iSql + "\n  " + arrtmp[9]  + ")  ";
    
            }catch(Exception e) {
            	COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", e);
            }
 
            Boolean UpdateFlag = COMMDATA.GetDBManager().UpdateData(uSql);
    		if (UpdateFlag != true) {
    			Boolean InsertFlag = COMMDATA.GetDBManager().UpdateData(iSql);
    			if (InsertFlag != true){
    				COMMDATA.GetDBManager().ServerDBRollback();
    				return "ERROR";
    			}
    		}
            
		}
		
		COMMDATA.GetDBManager().ServerDBCommit();
		return "OK";
	}
 
	private String Proc_DBSearchKindTxOffsetSize()
	{
		String isql = "";
	    isql = isql + "\n select  A.APPL_CODE               ";
	    isql = isql + "\n       , max(A.KIND_CODE_OFFSET)   ";
	    isql = isql + "\n       , max(A.KIND_CODE_LEN)      ";
	    isql = isql + "\n       , max(B.REQ_TX_CODE_OFFSET) ";
	    isql = isql + "\n       , max(B.TX_CODE_LEN)        ";
	    isql = isql + "\n from ALAPPL A , ALKIND B          ";
	    isql = isql + "\n where A.APPL_CODE = B.APPL_CODE   ";
	    isql = isql + "\n GROUP by A.APPL_CODE              ";
 
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
		if (retdata == null || retdata.equals("")) return "NOT-FOUND";
 
		return retdata;
	}
	
	private String Proc_DBSearchApplInfo()
	{
		String isql = " SELECT APPL_CODE,APPL_NAME,REQ_HEADER_SIZE,RES_HEADER_SIZE,";
		isql = isql + "\n      decode(REQ_HEAD_MAPP_CLASS,'','<NODATA>', REQ_HEAD_MAPP_CLASS) as REQ_HEAD_MAPP_CLASS,  ";
	    isql = isql + "\n      decode(RES_HEAD_MAPP_CLASS,'','<NODATA>', RES_HEAD_MAPP_CLASS) as RES_HEAD_MAPP_CLASS ,  ";
		isql = isql + "\n      KIND_CODE_OFFSET,KIND_CODE_LEN,TIMEOUT ";
	    isql = isql + "\n FROM ALAPPL                  ";
	    isql = isql + "\n WHERE STA_TYPE = 1 ";
	    isql = isql + "\n ORDER BY APPL_CODE,APPL_NAME ";

		String retdata = COMMDATA.GetDBManager().SearchData(isql);
 
		if (retdata == null || retdata.equals("")) return "NOT-FOUND";

		return retdata;
	}
	private String Proc_DBSearchTcpApplInfo()
	{
		String isql = "  select a.appl_code, c.appl_name from algrpline a, alline b, alappl c ";
	    isql = isql + "\n where a.symbname = b.symbname ";
	    isql = isql + "\n   and a.appl_code = c.appl_code ";
	    isql = isql + "\n group by a.appl_code, c.appl_name ";
	    isql = isql + "\n order by a.appl_code, c.appl_name ";

        String retdata = COMMDATA.GetDBManager().SearchData(isql);
		if (retdata == null || retdata.equals("")) return "NOT-FOUND";
		  
		return retdata;
	}
	private String Proc_DBSearchApplTxInfo(String pApplCode)
	{
		String isql = " SELECT C.REP_KIND_CODE, C.NAME, C.TX_CODE, C.ANAME, C.TIMEOUT, C.RES_FLAG, ";
	    isql = isql + "\n       decode(C.REQ_HEAD_MAPP_CLASS,'','<NODATA>', C.REQ_HEAD_MAPP_CLASS) as REQ_HEAD_MAPP_CLASS, decode(C.RES_HEAD_MAPP_CLASS,'','<NODATA>', C.RES_HEAD_MAPP_CLASS) as RES_HEAD_MAPP_CLASS, ";
	    isql = isql + "\n       decode(C.RES_BODY_MAPP_CLASS,'','<NODATA>', C.RES_BODY_MAPP_CLASS) as RES_BODY_MAPP_CLASS, decode(C.REQ_BODY_MAPP_CLASS,'','<NODATA>', C.REQ_BODY_MAPP_CLASS) as REQ_BODY_MAPP_CLASS, ";
	    isql = isql + "\n       C.BNAME, C.NORM_KIND_CODE, C.REQ_TX_CODE_OFFSET, C.TX_CODE_LEN, C.RES_TX_CODE_OFFSET, ";
	    isql = isql + "\n       NVL(T.INOUT_FLAG, 'N'), NVL(T.MAP_FLAG, 'B'), ";
	    isql = isql + "\n       NVL(T.KeyOffset1, 0 ), NVL(T.KeyLen1, 0 ), NVL(T.KeyVal1 ,'<NODATA>' ) ,";
	    isql = isql + "\n       NVL(T.KeyOffset2, 0 ), NVL(T.KeyLen2, 0 ), NVL(T.KeyVal2 ,'<NODATA>' ) ,";
	    isql = isql + "\n       NVL(T.KeyOffset3, 0 ), NVL(T.KeyLen3, 0 ), NVL(T.KeyVal3 ,'<NODATA>' )  ";

	    isql = isql + "\n       FROM ";
	    isql = isql + "\n (SELECT A.APPL_CODE, B.REP_KIND_CODE, B.NAME, A.TX_CODE, A.NAME ANAME,A.TIMEOUT,A.RES_FLAG, ";
	    isql = isql + "\n       decode(A.REQ_HEAD_MAPP_CLASS,'','<NODATA>', A.REQ_HEAD_MAPP_CLASS) as REQ_HEAD_MAPP_CLASS , decode(A.RES_HEAD_MAPP_CLASS,'','<NODATA>', A.RES_HEAD_MAPP_CLASS) as RES_HEAD_MAPP_CLASS, ";
	    isql = isql + "\n       decode(A.RES_BODY_MAPP_CLASS,'','<NODATA>', A.RES_BODY_MAPP_CLASS) as RES_BODY_MAPP_CLASS, decode(A.REQ_BODY_MAPP_CLASS,'','<NODATA>', A.REQ_BODY_MAPP_CLASS) as  REQ_BODY_MAPP_CLASS, ";
	    isql = isql + "\n       B.NAME BNAME,B.NORM_KIND_CODE,B.REQ_TX_CODE_OFFSET,B.TX_CODE_LEN,B.RES_TX_CODE_OFFSET ";
	    isql = isql + "\n  FROM ALTX A, ALKIND B, ALAPPL M ";
	    isql = isql + "\n   WHERE A.STA_TYPE = 1 ";
	    isql = isql + "\n    AND A.APPL_CODE = '" + pApplCode + "' ";
	    isql = isql + "\n    AND A.APPL_CODE = B.APPL_CODE ";
	    isql = isql + "\n    AND A.REP_KIND_CODE = B.REP_KIND_CODE ";
	    isql = isql + "\n    AND A.APPL_CODE = M.APPL_CODE ";
	    isql = isql + "\n    AND B.APPL_CODE = M.APPL_CODE ) C ";
	    isql = isql + "\n  LEFT JOIN TCHECKER_TXDETAIL T ";
	    isql = isql + "\n   ON (C.APPL_CODE = T.APPL_CODE ";
	    isql = isql + "\n   AND C.REP_KIND_CODE = T.KIND_CODE ";
	    isql = isql + "\n   AND C.TX_CODE = T.TX_CODE) ";
	    isql = isql + "\n ORDER BY C.REP_KIND_CODE,C.TX_CODE ";

		String retdata = COMMDATA.GetDBManager().SearchData(isql);
 
		if (retdata == null || retdata.equals("")) return "NOT-FOUND";

		return retdata;
	}
	private String Proc_DBSearchAsyncApplList()
	{
		String isql = "    SELECT APPL_CODE, APPL_NAME  FROM ALAPPL A ";
	    isql = isql + "\n WHERE APPL_CODE IN ( SELECT APPL_CODE FROM ALTX WHERE STA_TYPE = 1 AND RES_FLAG != 1)  ";
	    isql = isql + "\n    AND STA_TYPE = 1        ";
	    isql = isql + "\n ORDER BY A.APPL_CODE     ";
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
 
		if (retdata == null || retdata.equals("")) return "NOT-FOUND";

		return retdata;
	}
	private String Proc_DBSearchTxMappingList(String pApplCode)
	{
		/*
		 * TCheckPanelTxDetail, TCheckPanelResMap 에서 사용하는 함수
		 * TCheckPanelTxDetail pApplCode = <NODATA> 로 넘어옴.
		 */
		if(pApplCode.equals("<NODATA>")){
			pApplCode = "";
		}
		
		String isql = "   SELECT A.APPL_CODE, M.APPL_NAME, A.REP_KIND_CODE, B.NAME, A.TX_CODE, A.NAME  ";
	    isql = isql + "\n FROM ALTX A, ALKIND B, ALAPPL M            ";
	    isql = isql + "\n  WHERE A.APPL_CODE like '%" + pApplCode + "%'  ";
	    isql = isql + "\n    AND A.STA_TYPE = 1                      ";
	    isql = isql + "\n    AND A.APPL_CODE = B.APPL_CODE           ";
	    isql = isql + "\n    AND A.REP_KIND_CODE = B.REP_KIND_CODE   ";
	    isql = isql + "\n    AND A.APPL_CODE = M.APPL_CODE           ";
	    isql = isql + "\n    AND B.APPL_CODE = M.APPL_CODE           ";
	    if(!pApplCode.equals("")){
	    	isql = isql + "\n    AND A.RES_FLAG != 1                     ";
	    }
	    isql = isql + "\n ORDER BY A.APPL_CODE ,A.REP_KIND_CODE, A.TX_CODE         ";

		String retdata = COMMDATA.GetDBManager().SearchData(isql);
 
		if (retdata == null || retdata.equals("")) return "NOT-FOUND";

		return retdata;
	}
	private String Proc_DBSearchTxDetail(String pInData)
	{
		String isql = " SELECT APPL_CODE , KIND_CODE, TX_CODE , ";
	    isql = isql + "\n       NVL(INOUT_FLAG, 'N'), NVL(MAP_FLAG, 'B'), ";
	    isql = isql + "\n       NVL(KeyOffset1, 0 ), NVL(KeyLen1, 0 ), NVL(KeyVal1 ,'<NODATA>' ) ,";
	    isql = isql + "\n       NVL(KeyOffset2, 0 ), NVL(KeyLen2, 0 ), NVL(KeyVal2 ,'<NODATA>' ) ,";
	    isql = isql + "\n       NVL(KeyOffset3, 0 ), NVL(KeyLen3, 0 ), NVL(KeyVal3 ,'<NODATA>' )  ";
	    isql = isql + "\n FROM TCHECKER_TXDETAIL                         ";
        
        if (!pInData.equals("NODATA") && !pInData.equals("")){
            isql = isql + "\n ORDER BY APPL_CODE , KIND_CODE , TX_CODE       ";
        }
        else {
        	String[] arrtmp = pInData.split("\t");
            isql = isql + "\n WHERE APPL_CODE = '" + arrtmp[0] + "' ";
            isql = isql + "\n   AND KIND_CODE = '" + arrtmp[1] + "' ";
            isql = isql + "\n   AND TX_CODE   = '" + arrtmp[2] + "' ";
        }
 
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
 
		if (retdata == null || retdata.equals("")) return "NOT-FOUND";

		return retdata;
	}
 
	private String Proc_DBUpdateTxDetail(String savedata)
	{
 
 
		String[] arrdata = savedata.split("\n");
		for(int i=0;i < arrdata.length ;i++){
			if (arrdata[i].equals("")) break;
			String[] arrtmp = arrdata[i].split("\t");
			String uSql = "";
			String iSql = "";
            try {
    			uSql = "  Update TCHECKER_TXDETAIL Set ";
    			uSql = uSql + "\n  INOUT_FLAG = '" + arrtmp[5]  + "',";
    			uSql = uSql + "\n  MAP_FLAG   = '" + arrtmp[6]  + "', ";
    			uSql = uSql + "\n  KeyOffset1 =  " + arrtmp[7]  + " , ";
    			uSql = uSql + "\n  KeyLen1    =  " + arrtmp[8]  + " , ";
    			uSql = uSql + "\n  KeyVal1    = '" + arrtmp[9]  + "', ";
    			uSql = uSql + "\n  KeyOffset2 =  " + arrtmp[10] + " , ";
    			uSql = uSql + "\n  KeyLen2    =  " + arrtmp[11] + " , ";
    			uSql = uSql + "\n  KeyVal2    = '" + arrtmp[12] + "', ";
    			uSql = uSql + "\n  KeyOffset3 =  " + arrtmp[13] + " , ";
    			uSql = uSql + "\n  KeyLen3    =  " + arrtmp[14] + " , ";
    			uSql = uSql + "\n  KeyVal3    = '" + arrtmp[15] + "'  ";
    			uSql = uSql + "\n  Where APPL_CODE    = '" + arrtmp[0] + "' ";
    			uSql = uSql + "\n    AND KIND_CODE    = '" + arrtmp[1] + "' ";
    			uSql = uSql + "\n    AND TX_CODE      = '" + arrtmp[3] + "' ";
 
    			
    			iSql = " Insert Into TCHECKER_TXDETAIL (";
    			iSql = iSql + "\n APPL_CODE      , ";
                iSql = iSql + "\n KIND_CODE      , ";
                iSql = iSql + "\n TX_CODE        , ";
                iSql = iSql + "\n INOUT_FLAG     , ";
                iSql = iSql + "\n MAP_FLAG       , ";
                iSql = iSql + "\n KeyOffset1	, ";
                iSql = iSql + "\n KeyLen1     , ";
                iSql = iSql + "\n KeyVal1     , ";
                iSql = iSql + "\n KeyOffset2  , ";
                iSql = iSql + "\n KeyLen2     , ";
                iSql = iSql + "\n KeyVal2     , ";
                iSql = iSql + "\n KeyOffset3  , ";
                iSql = iSql + "\n KeyLen3     , ";
                iSql = iSql + "\n KeyVal3      ) values ( ";
                iSql = iSql + "\n '" + arrtmp[0]  + "', ";
                iSql = iSql + "\n '" + arrtmp[1]  + "', ";
                iSql = iSql + "\n '" + arrtmp[3]  + "', ";
                iSql = iSql + "\n '" + arrtmp[5]  + "', ";
                iSql = iSql + "\n '" + arrtmp[6]  + "', ";
                iSql = iSql + "\n  " + arrtmp[7]  + " , ";
                iSql = iSql + "\n  " + arrtmp[8]  + " , ";
                iSql = iSql + "\n '" + arrtmp[9]  + "', ";
                iSql = iSql + "\n  " + arrtmp[10]  + " , ";
                iSql = iSql + "\n  " + arrtmp[11]  + " , ";
                iSql = iSql + "\n '" + arrtmp[12]  + "', ";
                iSql = iSql + "\n  " + arrtmp[13]  + " , ";
                iSql = iSql + "\n  " + arrtmp[14]  + " , ";
                iSql = iSql + "\n '" + arrtmp[15]  + "') ";
 
            }catch(Exception e) {
            	COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", e);
            }
 
            Boolean UpdateFlag = COMMDATA.GetDBManager().UpdateData(uSql);
    		if (UpdateFlag != true) {
    			Boolean InsertFlag = COMMDATA.GetDBManager().UpdateData(iSql);
    			if (InsertFlag != true){
    				COMMDATA.GetDBManager().ServerDBRollback();
    				return "ERROR";
    			}
    		}
            
		}
		
		COMMDATA.GetDBManager().ServerDBCommit();
		return "OK";
	}
	
 
	
	private String Proc_DBSearchResLink(String pApplCode)
	{
		String isql = " SELECT REQ_APPL_CODE, REQ_KIND_CODE, REQ_TX_CODE  , RES_APPL_CODE, RES_KIND_CODE, RES_TX_CODE  , RES_PORTNO ";
	    isql = isql + "\n FROM TCHECKER_RESLINK                         ";
	    isql = isql + "\n WHERE REQ_APPL_CODE = '" + pApplCode + "' ";
	    isql = isql + "\n ORDER BY REQ_APPL_CODE, REQ_KIND_CODE, REQ_TX_CODE";
 
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
 
		if (retdata == null || retdata.equals("")) return "NOT-FOUND";

		return retdata;
	}
	private String Proc_DBSearchResLinkOne(String readdata)
	{
		String[] arrtmp = readdata.split("\t");
	    String isql = "";
	    isql = isql + "   SELECT R.RES_APPL_CODE, A.APPL_NAME, R.RES_KIND_CODE, K.NAME, R.RES_TX_CODE, T.NAME ";
	    isql = isql + "\n FROM TCHECKER_RESLINK R, ALAPPL A, ALKIND K, ALTX T                                 ";
	    isql = isql + "\n WHERE R.REQ_APPL_CODE = '" + arrtmp[0] + "'                                         ";
	    isql = isql + "\n   AND R.REQ_KIND_CODE = '" + arrtmp[1] + "'                                         ";
	    isql = isql + "\n   AND R.REQ_TX_CODE   = '" + arrtmp[2] + "'                                         ";
	    isql = isql + "\n   AND R.REQ_APPL_CODE = A.APPL_CODE                                                 ";
	    isql = isql + "\n   AND R.REQ_APPL_CODE = K.APPL_CODE                                                 ";
	    isql = isql + "\n   AND R.REQ_KIND_CODE = K.REP_KIND_CODE                                             ";
	    isql = isql + "\n   AND R.REQ_APPL_CODE = T.APPL_CODE                                                 ";
	    isql = isql + "\n   AND R.REQ_KIND_CODE = T.REP_KIND_CODE                                             ";
	    isql = isql + "\n   AND R.REQ_TX_CODE   = T.TX_CODE                                                   ";
	    isql = isql + "\n   AND A.STA_TYPE = 1                                                                ";
	    isql = isql + "\n   AND K.STA_TYPE = 1                                                                ";
	    isql = isql + "\n   AND T.STA_TYPE = 1                                                                ";
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
 
		if (retdata == null || retdata.equals("")) return "NOT-FOUND";

		return retdata;
	}
	
	private String Proc_DBUpdateResLink(String savedata)
	{
 
		String[] arrdata = savedata.split("\n");
		for(int i=0;i < arrdata.length ;i++){
			if (arrdata[i].equals("")) break;
			String[] arrtmp = arrdata[i].split("\t");
			String uSql = "";
			String iSql = "";
            try {
    			uSql = "  Update TCHECKER_RESLINK Set ";
    			uSql = uSql + "\n  RES_APPL_CODE= '" + arrtmp[3] + "',";
    			uSql = uSql + "\n  RES_KIND_CODE= '" + arrtmp[4] + "',";
    			uSql = uSql + "\n  RES_TX_CODE  = '" + arrtmp[5] + "',";
    			uSql = uSql + "\n  RES_PORTNO   = '" + arrtmp[6] + "' ";
    			uSql = uSql + "\n  Where REQ_APPL_CODE  = '" + arrtmp[0] + "' ";
    			uSql = uSql + "\n    AND REQ_KIND_CODE  = '" + arrtmp[1] + "' ";
    			uSql = uSql + "\n    AND REQ_TX_CODE    = '" + arrtmp[2] + "' ";

     
    			iSql = " Insert Into TCHECKER_RESLINK (";
                iSql = iSql + "\n REQ_APPL_CODE      , ";
                iSql = iSql + "\n REQ_KIND_CODE      , ";
                iSql = iSql + "\n REQ_TX_CODE        , ";
                iSql = iSql + "\n RES_APPL_CODE      , ";
                iSql = iSql + "\n RES_KIND_CODE      , ";
                iSql = iSql + "\n RES_TX_CODE        , ";
                iSql = iSql + "\n RES_PORTNO           ";
                iSql = iSql + "\n ) values ( ";
                iSql = iSql + "\n '" + arrtmp[0] + "', ";
                iSql = iSql + "\n '" + arrtmp[1] + "', ";
                iSql = iSql + "\n '" + arrtmp[2] + "', ";
                iSql = iSql + "\n '" + arrtmp[3] + "', ";
                iSql = iSql + "\n '" + arrtmp[4] + "', ";
                iSql = iSql + "\n '" + arrtmp[5] + "', ";
                iSql = iSql + "\n '" + arrtmp[6] + "') ";
 
 
            }catch(Exception e) {
            	COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", e);
            }
 
            Boolean UpdateFlag = COMMDATA.GetDBManager().UpdateData(uSql);
    		if (UpdateFlag != true) {
    			Boolean InsertFlag = COMMDATA.GetDBManager().UpdateData(iSql);
    			if (InsertFlag != true){
    				COMMDATA.GetDBManager().ServerDBRollback();
    				return "ERROR";
    			}
    		}
            
		}
		COMMDATA.GetDBManager().ServerDBCommit();
		return "OK";
	}
	
	private String Proc_DBDeleteResLink(String savedata)
	{
 
		String[] arrdata = savedata.split("\n");
		for(int i=0;i < arrdata.length ;i++){
			if (arrdata[i].equals("")) break;
			String[] arrtmp = arrdata[i].split("\t");
			String uSql = "";
            try {
    			uSql = "  Delete From TCHECKER_RESLINK  ";
    			uSql = uSql + "\n  Where REQ_APPL_CODE  = '" + arrtmp[0].replace("@NO-DATA@", "") + "' ";
    			uSql = uSql + "\n    AND REQ_KIND_CODE  = '" + arrtmp[1].replace("@NO-DATA@", "") + "' ";
    			uSql = uSql + "\n    AND REQ_TX_CODE    = '" + arrtmp[2].replace("@NO-DATA@", "") + "' ";
    			uSql = uSql + "\n    AND RES_APPL_CODE  = '" + arrtmp[3].replace("@NO-DATA@", "") + "' ";
    			uSql = uSql + "\n    AND RES_KIND_CODE  = '" + arrtmp[4].replace("@NO-DATA@", "") + "' ";
    			uSql = uSql + "\n    AND RES_TX_CODE    = '" + arrtmp[5].replace("@NO-DATA@", "") + "' ";
 
    			 
            }catch(Exception e) {
            	COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", e);
            }
 
            Boolean DeleteFlag = COMMDATA.GetDBManager().UpdateData(uSql);
    		if (DeleteFlag != true) {
				COMMDATA.GetDBManager().ServerDBRollback();
				return "ERROR";
    		}
		}
		COMMDATA.GetDBManager().ServerDBCommit();
		return "OK";
	}
	
 
	private String Proc_DBSearchUser(String pUserID)
	{
		String isql = " SELECT USERID,PASSWORD,INFO,IP,PERMIT ";
	    isql = isql + "\n FROM TCHECKER_USER                         ";
        if (!pUserID.equals("NODATA") && !pUserID.equals("")){
        	isql = isql + "\n WHERE USERID = '" + pUserID + "' ";
        }
        isql = isql + "\n ORDER BY USERID ";
 
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
 
		if (retdata == null || retdata.equals("")) return "NOT-FOUND";

		return retdata;
	}
	
	private String Proc_DBUpdateUser(String savedata)
	{
 
		String[] arrdata = savedata.split("\n");
		for(int i=0;i < arrdata.length ;i++){
			if (arrdata[i].equals("")) break;
			String[] arrtmp = arrdata[i].split("\t");
			String uSql = "";
			String iSql = "";
            try {
    			uSql = "  Update TCHECKER_USER Set ";
    			uSql = uSql + "\n  PASSWORD = '" + arrtmp[1] + "',";
    			uSql = uSql + "\n  INFO     = '" + arrtmp[2] + "',";
    			uSql = uSql + "\n  IP       = '" + arrtmp[3] + "',";
    			uSql = uSql + "\n  PERMIT   = '" + arrtmp[4] + "' ";
    			uSql = uSql + "\n  Where USERID  = '" + arrtmp[0] + "' ";
 
     
    			
    			iSql = " Insert Into TCHECKER_USER (";
                iSql = iSql + "\n USERID      , ";
                iSql = iSql + "\n PASSWORD    , ";
                iSql = iSql + "\n INFO        , ";
                iSql = iSql + "\n IP          , ";
                iSql = iSql + "\n PERMIT        ";
                iSql = iSql + "\n ) values ( ";
                iSql = iSql + "\n '" + arrtmp[0] + "', ";
                iSql = iSql + "\n '" + arrtmp[1] + "', ";
                iSql = iSql + "\n '" + arrtmp[2] + "', ";
                iSql = iSql + "\n '" + arrtmp[3] + "', ";
                iSql = iSql + "\n '" + arrtmp[4] + "') ";
  
            }catch(Exception e) {
            	COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", e);
            }
 
            Boolean UpdateFlag = COMMDATA.GetDBManager().UpdateData(uSql);
    		if (UpdateFlag != true) {
    			Boolean InsertFlag = COMMDATA.GetDBManager().UpdateData(iSql);
    			if (InsertFlag != true){
    				COMMDATA.GetDBManager().ServerDBRollback();
    				return "ERROR";
    			}
    		}
		}
		COMMDATA.GetDBManager().ServerDBCommit();
		return "OK";
	}
	
	private String Proc_DBDeleteUser(String savedata)
	{
 
		String[] arrdata = savedata.split("\n");
		for(int i=0;i < arrdata.length ;i++){
			if (arrdata[i].equals("")) break;
			String[] arrtmp = arrdata[i].split("\t");
			String uSql = "";
			String dSql = "";
            try {
    			uSql = "  Delete From TCHECKER_USER  ";
    			uSql = uSql + "\n  Where USERID  = '" + arrtmp[0] + "' ";
    			
    			dSql = "  Delete From TCHECKER_PERMIT  ";
    			dSql = dSql + "\n  Where USERID  = '" + arrtmp[0] + "' ";
 
    			 
            }catch(Exception e) {
            	COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", e);
            }
 
            Boolean DeleteFlag = COMMDATA.GetDBManager().UpdateData(dSql);
            DeleteFlag = COMMDATA.GetDBManager().UpdateData(uSql);
    		if (DeleteFlag != true) {
				COMMDATA.GetDBManager().ServerDBRollback();
				return "ERROR";
    		}
        
		}
		COMMDATA.GetDBManager().ServerDBCommit();
		return "OK";
	}
	
	private String Proc_DBSearchPermit(String pUserID)
	{
		String isql = "  SELECT D.APPL_CODE, D.APPL_NAME, NVL(T.PERMIT,'N') ";
	    isql = isql + "\n FROM (SELECT C.APPL_CODE, C.APPL_NAME FROM ALAPPL C WHERE C.STA_TYPE = 1 ) D ";
	    isql = isql + "\n LEFT JOIN TCHECKER_PERMIT T ";
	    isql = isql + "\n ON ('" + pUserID + "' = T.USERID ";
	    isql = isql + "\n   AND D.APPL_CODE = T.APPL_CODE  ) ";
	    isql = isql + "\n ORDER BY D.APPL_CODE  ";
 
 
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
		if (retdata == null || retdata.equals("")) return "NOT-FOUND";

		return retdata;
	}
	private String Proc_DBSearchPermitAppl(String pUserID)
	{
		String isql = "";
		isql = isql + "   SELECT T.APPL_CODE,NVL(T.PERMIT,'N')   ";
	    isql = isql + "\n FROM TCHECKER_PERMIT T                  ";
	    isql = isql + "\n WHERE T.USERID = '" + pUserID + "'      ";
	    isql = isql + "\n   AND T.KIND_CODE = 'ALL'               ";
	    isql = isql + "\n   AND T.TX_CODE = 'ALL'                 ";
	    isql = isql + "\n ORDER BY T.APPL_CODE\t\t                ";
 
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
		if (retdata == null || retdata.equals("")) return "NOT-FOUND";

		return retdata;
	}
	private String Proc_DBUpdatePermit(String savedata)
	{
		long startTime = System.currentTimeMillis();
 
//		COMMDATA.GetDBManager().UpdateData("  DELETE FROM TCHECKER_PERMIT ");
// 
//		String[] arrdata = savedata.split("\n");
//				
//		for(int i=0;i < arrdata.length ;i++){
//			if (arrdata[i].equals("")) break;
//			String[] arrtmp = arrdata[i].split("\t");
//			String uSql = "";
//			String iSql = "";
//            try {
//            			
//    			iSql = " Insert Into TCHECKER_PERMIT (";
//                iSql = iSql + "\n USERID       , ";
//                iSql = iSql + "\n APPL_CODE    , ";
//                iSql = iSql + "\n KIND_CODE    , ";
//                iSql = iSql + "\n TX_CODE      , ";
//                iSql = iSql + "\n PERMIT         ";
//                iSql = iSql + "\n ) values ( ";
//                iSql = iSql + "\n '" + arrtmp[0] + "', ";
//                iSql = iSql + "\n '" + arrtmp[1] + "', ";
//                iSql = iSql + "\n '" + "ALL" + "', ";
//                iSql = iSql + "\n '" + "ALL" + "', ";
//                iSql = iSql + "\n '" + arrtmp[2] + "') ";
//  
//            }catch(Exception e) {
//            	COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", e);
//            }
//             
//			Boolean InsertFlag = COMMDATA.GetDBManager().UpdateData(iSql);
//			if (InsertFlag != true){
//				COMMDATA.GetDBManager().ServerDBRollback();
//				return "ERROR";
//			}
//		}
//		COMMDATA.GetDBManager().ServerDBCommit();
	 
		Boolean InsertFlag = COMMDATA.GetDBManager().UpdatePermitData(savedata);
		long endTime = System.currentTimeMillis();
		
		COMMDATA.GetTCheckerLog().WriteLog("D", "MgrManager", "Execute Time : ["+( endTime - startTime )/1000.0f +"초]");
		
 
		
		return "OK";
	}
	
	private String Proc_DBDeletePermit(String savedata)
	{
 
		String[] arrdata = savedata.split("\n");
		for(int i=0;i < arrdata.length ;i++){
			if (arrdata[i].equals("")) break;
			String[] arrtmp = arrdata[i].split("\t");
			String uSql = "";
            try {
    			uSql = "  Delete From TCHECKER_PERMIT  ";
    			uSql = uSql + "\n  Where USERID  = '" + arrtmp[0] + "' ";
     			 
            }catch(Exception e) {
            	COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", e);
            }
 
            Boolean DeleteFlag = COMMDATA.GetDBManager().UpdateData(uSql);
    		if (DeleteFlag != true) {
				COMMDATA.GetDBManager().ServerDBRollback();
				return "ERROR";
    		}
		}
		COMMDATA.GetDBManager().ServerDBCommit();
		return "OK";
	}
	private String Proc_DBSearchAdminIP()
	{
		String isql = " SELECT IP";
        isql = isql + "\n FROM TCHECKER_USER             ";
        isql = isql + "\n Where USERID = 'Administrator' ";
 
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
 
		if (retdata == null || retdata.equals("")) return "NOT-FOUND";

		return retdata;
	}
	 
	private String Proc_DBCheck_UserID(String instr)
	{
		String[] arrtmp = instr.split("\t");
		String isql = " SELECT 1";
        isql = isql + "\n FROM TCHECKER_USER             ";
        isql = isql + "\n Where USERID = '" + arrtmp[0] + "' ";
        isql = isql + "\n   and PASSWORD = '" + arrtmp[1] + "' ";
 
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
 
		if (retdata == null || retdata.equals("")) return "NOT-FOUND";

		return retdata;
	}
	
	//--------------- WirelessRes ---------------------------------
	private String Proc_DBSearchWirelessRes(String pInfoData, String pUserIP)
	{

		try {
			String fname = "./Response/Tcpmsg/" + pUserIP + "/message.dat" ;
			File f = new File(fname);
			if (f.exists()) {
	        	DataInputStream dis = new DataInputStream(new FileInputStream(f));
	            int len  = (int) f.length();
	            byte[] ReadData = new byte[len];
	            dis.readFully(ReadData);
	            dis.close();
	   
	            return new String(ReadData);
			}
		}catch(Exception e){}
		return "";
	 
	}
	
	//--------------- Soap ---------------------------------
	private String Proc_DBSearchInBoundURL(String pApplCode)
	{
		String isql = " SELECT a.URL   ";
        isql = isql + "\n FROM alurl a ";
        isql = isql + "\n Where a.URL_TYPE = '1' ";
        isql = isql + "\n AND a.sta_type = 1 ";
        isql = isql + "\n AND a.APPL_CODE = '" + pApplCode + "' ";
 
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
		if (retdata == null || retdata.equals("")) return "NOT-FOUND";
		return retdata;
	}
	private String Proc_DBSearchOutBoundURL(String pApplCode)
	{
		String isql = " SELECT a.URL   ";
        isql = isql + "\n FROM alurl a ";
        isql = isql + "\n Where a.URL_TYPE = '2' ";
        isql = isql + "\n AND a.sta_type = 1 ";
        isql = isql + "\n AND a.APPL_CODE = '" + pApplCode + "' ";
 
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
		if (retdata == null || retdata.equals("")) return "NOT-FOUND";
		return retdata;
	}
	private String Proc_DBSearchInOutBoundURL()
	{
		String isql = "   select u.url_type, t.appl_code, a.appl_name, t.rep_kind_code, k.name, t.tx_code,t.name    ";
	    isql = isql + "\n from altx t, alurl u, alappl a, alkind k                                                  ";
	    isql = isql + "\n where t.appl_code = u.appl_code                                                           ";
	    isql = isql + "\n   and t.appl_code = a.appl_code                                                           ";
	    isql = isql + "\n   and t.rep_kind_code = k.rep_kind_code                                                   ";
	    isql = isql + "\n   and t.appl_code = k.appl_code                                                           ";
	    isql = isql + "\n   and t.sta_type = 1                                                                      ";
	    isql = isql + "\n   and a.sta_type = 1                                                                      ";
	    isql = isql + "\n   and k.sta_type = 1                                                                      ";
	    isql = isql + "\n   and u.sta_type = 1                                                                      ";
	    isql = isql + "\n group by u.url_type, t.appl_code, a.appl_name, t.rep_kind_code, k.name, t.tx_code,t.name  ";
	    isql = isql + "\n order by u.url_type, t.appl_code, a.appl_name, t.rep_kind_code, k.name, t.tx_code,t.name  ";
 
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
 
		if (retdata == null || retdata.equals("")) return "NOT-FOUND";

		return retdata;
	}
	private String Proc_DBSendURLMSG(String pRecvData, String pUserIP)
	{
		try {
			try{
				String   delfname = "./Response/Tcpmsg/" + pUserIP + "/message.dat" ;
				File delfile = new File(delfname);
				if (delfile.exists()) delfile.delete();
			}catch(Exception e1){}
			
			SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
			String reg_dt = formatter.format(new java.util.Date()); //등록 일시
			formatter = new java.text.SimpleDateFormat("HHmmss", java.util.Locale.KOREA);
			String reg_tm = formatter.format(new java.util.Date());  //등록시간 
			formatter = new java.text.SimpleDateFormat("S", java.util.Locale.KOREA);
			String reg_milsec = formatter.format(new java.util.Date());  //등록시간 
	 
			String[] arrpRecvData = pRecvData.split("<DATAGUBUN>");
			String[] arrtmp = arrpRecvData[0].split("\t");
			String   pUserID   = arrtmp[0];
			String   pApplCode = arrtmp[1];
			String   pKindCode = arrtmp[2];
			String   pTxCode   = arrtmp[3];
			String   pUrl      = arrtmp[4];
			String   header    = pUserIP + "\t" + pUserID + "\t" + pApplCode + "\t" + pUrl + "<DATAGUBUN>";  //UserPCIP + UserID + ApplCode + URL
			String   pPortNo   = "";
			String[] arrUrl    = pUrl.split(":");
			if (arrUrl.length == 1) pPortNo = "80";
			if (arrUrl.length == 2) {
				String[] arrtmpsub = arrUrl[1].replace("/", "@").split("@");
				pPortNo = arrtmpsub[0];
			}
		 
			//Directory Check
			File dir1 = new File("./Request");
			if (!dir1.exists()) dir1.mkdir();
			
			File dir2 = new File("./Request/Urlmsg");
			if (!dir2.exists()) dir2.mkdir();
			
			File dir3 = new File("./Request/Urlmsg/" + pPortNo);
			if (!dir3.exists()) dir3.mkdir();
			
			//Request Data Write
			
			String   fname = "./Request/Urlmsg/" + pPortNo + "/" + reg_dt + "_" + reg_tm + "_" + reg_milsec;
			DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(fname)));
			out.write(header.getBytes());
			out.write(arrpRecvData[1].getBytes());
			out.close();
			
			//soapmap 디렉토리에 마지막 송신 전문을 저장한다.
			File dir4 = new File("./Request/Urlmap");
			if (!dir4.exists()) dir4.mkdir();
			
			File dir5 = new File("./Request/Urlmap/" + pUserID);
			if (!dir5.exists()) dir5.mkdir();
			
			fname = "./Request/Urlmap/" + pUserID + "/" + pApplCode + "_" + pKindCode + "_" + pTxCode;
			DataOutputStream out1 = new DataOutputStream(new FileOutputStream(new File(fname)));
			out1.write(arrpRecvData[1].getBytes());
			out1.close();
		}catch(Exception e) {
			COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", e);
			return "Error:" + e.getMessage();
		}
		return "OK";
	}
	private String Proc_DBSaveURLMSGRES(String pRecvData)
	{
		try {
			CheckLicense();
			//Directory Check
			File dir1 = new File("./Response");
			if (!dir1.exists()) dir1.mkdir();
			
			File dir2 = new File("./Response/Urlmap");
			if (!dir2.exists()) dir2.mkdir();
			
            //응답전문을 Write한다.
 
			String[] arrpRecvData = pRecvData.split("<DATAGUBUN>");
			String[] arrtmp = arrpRecvData[0].split("\t");
			String   pUserID   = arrtmp[0];
			String   pApplCode = arrtmp[1];
			String   pKindCode = arrtmp[2];
			String   pTxCode   = arrtmp[3];
 
			
			String   fname = "./Response/Urlmap/" + pApplCode + "_" + pKindCode + "_" + pTxCode;
			
			DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(fname)));
			out.write(arrpRecvData[1].getBytes());
			out.close();
		}catch(Exception e) {
			COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", e);
			return "Error:" + e.getMessage();
		}
		return "OK";

	}
	private String Proc_DBLoadURLMSGRES(String pRecvData)
	{
		try {
			CheckLicense();
			
			//Directory Check
			File dir1 = new File("./Response");
			if (!dir1.exists()) dir1.mkdir();
			
			File dir2 = new File("./Response/Urlmap");
			if (!dir2.exists()) dir2.mkdir();
			
			String   savedata = "";
			String[] arrtmp = pRecvData.split("\t");
			String   pUserID   = arrtmp[0];
			String   pApplCode = arrtmp[1];
			String   pKindCode = arrtmp[2];
			String   pTxCode   = arrtmp[3];
 
			String   fname = "./Response/Urlmap/" + pApplCode + "_" + pKindCode + "_" + pTxCode ;
		 
			File f = new File(fname);
			if (f.exists()) {
	        	DataInputStream dis = new DataInputStream(new FileInputStream(f));
	            int len  = (int) f.length();
	            byte[] ReadData = new byte[len];
	            dis.readFully(ReadData);
	            dis.close();
	   
	            return new String(ReadData);
			}
		 
            return "";
		}catch(Exception e) {
			COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", e);
			return "Error:" + e.getMessage();
		}
	}
	private String Proc_DBLoadURLMSGREQ(String pRecvData)
	{
		try {
			CheckLicense();
			
			//Directory Check
			File dir1 = new File("./Request");
			if (!dir1.exists()) dir1.mkdir();
			
			File dir2 = new File("./Request/Urlmap");
			if (!dir2.exists()) dir2.mkdir();
			
			String   savedata = "";
			String[] arrtmp = pRecvData.split("\t");
			String   pUserID   = arrtmp[0];
			String   pApplCode = arrtmp[1];
			String   pKindCode = arrtmp[2];
			String   pTxCode   = arrtmp[3];
			
			File dir3 = new File("./Request/Urlmap/" + pUserID);
			if (!dir3.exists()) dir3.mkdir();
			
			String fname = "./Request/Urlmap/" + pUserID + "/" + pApplCode + "_" + pKindCode + "_" + pTxCode;
			
			File f = new File(fname);
			if (f.exists()) {
	        	DataInputStream dis = new DataInputStream(new FileInputStream(f));
	            int len  = (int) f.length();
	            byte[] ReadData = new byte[len];
	            dis.readFully(ReadData);
	            dis.close();
	      
	            return new String(ReadData);
			}
 
            return "";
		}catch(Exception e) {
			COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", e);
			return "Error:" + e.getMessage();
		}
	}
 
	//--------------- TCP ---------------------------------
	private String Proc_DBSearchMapperName(String pApplCode)
	{
		String isql = "select INST_CODE, SVCNAME from algrp where sta_type = 1 and appl_code = '" + pApplCode + "' ";
	    String retdata = this.COMMDATA.GetDBManager().SearchData(isql);
	    if ((retdata == null) || (retdata.equals(""))) return "NOT-FOUND";

	    String[] arrtmp = retdata.split("\n");

		COMMDATA.GetTCheckerLog().WriteLog("D", "MgrManager", "매퍼 서비스명 : ["+retdata.trim()+"]");
		return arrtmp[0];
	}
	
	private String Proc_DBSearchInOutBoundTcp()
	{
		String isql = "   select x.INOUT_FLAG, t.appl_code, a.appl_name, t.rep_kind_code, k.name, t.tx_code,t.name  ";
	    isql = isql + "\n from altx t, alappl a, alkind k, TCHECKER_TXDETAIL x                                      ";
	    isql = isql + "\n where t.appl_code = a.appl_code                                                           ";
	    isql = isql + "\n   and t.rep_kind_code = k.rep_kind_code                                                   ";
	    isql = isql + "\n   and t.appl_code = x.appl_code                                                           ";
	    isql = isql + "\n   and t.appl_code = x.appl_code                                                           ";
	    isql = isql + "\n   and t.tx_code = x.tx_code                                                               ";
	    isql = isql + "\n   and t.rep_kind_code = x.kind_code                                                       ";
	    isql = isql + "\n   and t.appl_code = k.appl_code                                                           ";
	    isql = isql + "\n   and t.sta_type = 1                                                                      ";
	    isql = isql + "\n   and a.sta_type = 1                                                                      ";
	    isql = isql + "\n   and k.sta_type = 1                                                                      ";
	    isql = isql + "\n   and t.appl_code not in (select u.appl_code from alurl u where u.sta_type = 1)           ";
	    isql = isql + "\n group by x.INOUT_FLAG,t.appl_code, a.appl_name, t.rep_kind_code, k.name, t.tx_code,t.name ";
	    isql = isql + "\n order by x.INOUT_FLAG,t.appl_code, a.appl_name, t.rep_kind_code, k.name, t.tx_code,t.name "; 
		
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
 
		if (retdata == null || retdata.equals("")) return "NOT-FOUND";

		return retdata;
	}
 
	private String Proc_DBSendTCPMSG(String pRecvData, String pUserPCIP )
	{
		//확인완료
		try {
			try{
				String   delfname = "./Response/Tcpmsg/" + pUserPCIP + "/message.dat" ;
				File delfile = new File(delfname);
				if (delfile.exists()) delfile.delete();
			}catch(Exception e1){}
			
			SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
			String reg_dt = formatter.format(new java.util.Date()); //등록 일시
			formatter = new java.text.SimpleDateFormat("HHmmss", java.util.Locale.KOREA);
			String reg_tm = formatter.format(new java.util.Date());  //등록시간 
			formatter = new java.text.SimpleDateFormat("S", java.util.Locale.KOREA);
			String reg_milsec = formatter.format(new java.util.Date());  //등록시간 
	 
			String[] arrpRecvData = pRecvData.split("<DATAGUBUN>");
			String[] arrtmp = arrpRecvData[0].split("\t");
			String   pUserID = arrtmp[0];
			String   pApplCode = arrtmp[1];
			String   pKindCode = arrtmp[2];
			String   pTxCode = arrtmp[3];
			String   pSendPort = arrtmp[4];
			
			String   fname = "./Request/Tcpmsg/" + pSendPort + "/" + reg_dt + "_" + reg_tm + "_" + reg_milsec ;
			
			//UserID + UserPCIP + ApplCode + KindCode + TxCode
			String   header = pUserID + "\t" + pUserPCIP + "\t" + pApplCode + "\t" + pKindCode + "\t" + pTxCode + "<DATAGUBUN>";  
			
			//Directory Check
			File dir1 = new File("./Request");
			if (!dir1.exists()) dir1.mkdir();
			
			File dir2 = new File("./Request/Tcpmsg");
			if (!dir2.exists()) dir2.mkdir();
			
			File dir3 = new File("./Request/Tcpmsg/" + pSendPort);
			if (!dir3.exists()) dir3.mkdir();
			
			//tcpmsg 디렉토리에 송신할 전문을 저장한다.
			DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(fname)));
			out.write(header.getBytes());
			out.write(arrpRecvData[1].getBytes());
			out.close();
			
			//tcpmap 디렉토리에 마지막 송신 전문을 저장한다.
			File dir4 = new File("./Request/Tcpmap");
			if (!dir4.exists()) dir4.mkdir();
			
			File dir5 = new File("./Request/Tcpmap/" + pUserID);
			if (!dir5.exists()) dir5.mkdir();
			
			fname = "./Request/Tcpmap/" + pUserID + "/" + pApplCode + "_" + pKindCode + "_" + pTxCode ;
			DataOutputStream out1 = new DataOutputStream(new FileOutputStream(new File(fname)));
			out1.write(arrpRecvData[1].getBytes());
			out1.close();
 
			
		}catch(Exception e) {
			COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", e);
			return "Error:" + e.getMessage();
		}
		return "OK";
	}    
	private String Proc_DBSendTCPNONTYPEMSG(String pRecvData, String pUserPCIP )
	{
		//확인완료
		try {
			try{
				String   delfname = "./Response/Tcpmsg/" + pUserPCIP + "/message.dat" ;
				File delfile = new File(delfname);
				if (delfile.exists()) delfile.delete();
			}catch(Exception e1){}
			
			SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
			String reg_dt = formatter.format(new java.util.Date()); //등록 일시
			formatter = new java.text.SimpleDateFormat("HHmmss", java.util.Locale.KOREA);
			String reg_tm = formatter.format(new java.util.Date());  //등록시간 
			formatter = new java.text.SimpleDateFormat("S", java.util.Locale.KOREA);
			String reg_milsec = formatter.format(new java.util.Date());  //등록시간 
			
			String   savedata = "";
			String[] arrpRecvData = pRecvData.split("<DATAGUBUN>");
			String[] arrtmp = arrpRecvData[0].split("\t");
			String   pUserID = arrtmp[0];
			String   pApplCode = arrtmp[1];
			String   pKindCode = arrtmp[2];
			String   pTxCode = arrtmp[3];
			String   pSendPort = arrtmp[4];
            
			int      searchidx = arrpRecvData[1].indexOf("<EOF>");
			if (searchidx < 0) {
				COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", "입력한 비정형 데이타의 마지막에 <EOF> 을 추가하세요.");
				return "Error:" + "입력한 비정형 데이타의 마지막에 <EOF> 을 추가하세요.";
			}
			String   pSendData = arrpRecvData[1].substring(0, searchidx);
            
			//tcpmap 디렉토리에 마지막 송신 전문을 저장한다.
			File dir4 = new File("./Request/Tcpmap");
			if (!dir4.exists()) dir4.mkdir();
			
			File dir5 = new File("./Request/Tcpmap/" + pUserID);
			if (!dir5.exists()) dir5.mkdir();
			
			
			String fname = "./Request/Tcpmap/" + pUserID + "/" + pApplCode + "_" + pKindCode + "_" + pTxCode ;
			DataOutputStream out1 = new DataOutputStream(new FileOutputStream(new File(fname)));
			out1.write("비정형데이타\t비정형데이타\tCHAR\t10000\t".getBytes());
			out1.write(arrpRecvData[1].getBytes());
			out1.close();
			
 
			//직접 매퍼로 데이타 전송 및 수신
			String tmpstr = Proc_DBSearchMapperName(pApplCode);
			String[] arrtmpstr = tmpstr.split("\t");
			String InstCode = arrtmpstr[0];
			String MapperName = arrtmpstr[1];
			
			//WebtCall-Start
			WebtSender jc = new WebtSender();
	 		byte[] msg;
			byte[] rcvmsg;
			byte[] rcvdata;
			msg = jc.makeHeader(InstCode, pApplCode, pKindCode, pTxCode, pSendData.getBytes()); // anylink헤더120byte+시스템헤더+데이터헤더+데이터
			rcvmsg = jc.MapperCall(AnylinkIP, Integer.parseInt(AnylinkPort), msg, MapperName);
 
			/* Async Inbound 거래이고, 응답거래매핑에 등록되여 있는 경우 응답을 받기 위해서 요청 거래를 남겨 놓는다. */
			/* 요청 업무/종별/거래/User PC IP */
			Proc_InboundAsyncRes(pApplCode, pKindCode, pTxCode, pUserPCIP);
			
			rcvdata = new byte[rcvmsg.length - 120];
			for(int i=120; i<rcvmsg.length; i++){
				rcvdata[i-120]=rcvmsg[i];
			}
			

			
			return new String(rcvdata);
			//WebtCall-End
			
			//String RetStr = COMMDATA.GetTCheckerJNI().SendMapper(InstCode, pApplCode, pKindCode, pTxCode, MapperName, COMMDATA.GetTCheckerJNI().FuncStringToHex(pSendData));
			//return COMMDATA.GetTCheckerJNI().FuncHexToString(RetStr);
		}catch(Exception e) {
			COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", e);
			return "Error:" + e.getMessage();
		}
 
	}
	private String Proc_DBSendTCPMAPPERTYPEMSG(String pRecvData, String pUserPCIP )
	{
		//확인완료
		try {
			try{
				String   delfname = "./Response/Tcpmsg/" + pUserPCIP + "/message.dat" ;
				File delfile = new File(delfname);
				if (delfile.exists()) delfile.delete();
			}catch(Exception e1){}
			
			SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
			String reg_dt = formatter.format(new java.util.Date()); //등록 일시
			formatter = new java.text.SimpleDateFormat("HHmmss", java.util.Locale.KOREA);
			String reg_tm = formatter.format(new java.util.Date());  //등록시간 
			formatter = new java.text.SimpleDateFormat("S", java.util.Locale.KOREA);
			String reg_milsec = formatter.format(new java.util.Date());  //등록시간 
			
			String   savedata = "";
			String[] arrpRecvData = pRecvData.split("<DATAGUBUN>");
			String[] arrtmp = arrpRecvData[0].split("\t");
			String   pUserID = arrtmp[0];
			String   pApplCode = arrtmp[1];
			String   pKindCode = arrtmp[2];
			String   pTxCode = arrtmp[3];
			String   pSendPort = arrtmp[4];
			String   pSendData = arrpRecvData[1];
            
			//tcpmap 디렉토리에 마지막 송신 전문을 저장한다.
			File dir4 = new File("./Request/Tcpmap");
			if (!dir4.exists()) dir4.mkdir();
			
			File dir5 = new File("./Request/Tcpmap/" + pUserID);
			if (!dir5.exists()) dir5.mkdir();
 
			String fname = "./Request/Tcpmap/" + pUserID + "/" + pApplCode + "_" + pKindCode + "_" + pTxCode ;
			DataOutputStream out1 = new DataOutputStream(new FileOutputStream(new File(fname)));
			out1.write(arrpRecvData[1].getBytes());
			out1.close();
			
			//Body분 ByPass 매핑일 경우에 txtMsgBody에 해당하는 메세지를 추출한다.
            String[] arrbody = pSendData.split("<BODYBYPASS>");
            
            //공통헤더부 제외설정에 대한 공통부 삽입
            String comminforeq = "";
            String comminfores = "";
            
            comminforeq = Proc_DBLoadTCPMSGREQ_CommonHead(pApplCode + "\tREAD");
            if (comminforeq.indexOf("NOTEXIST") < 0) {
            	String reslinkinfo = Proc_DBSearchResLinkOne(pApplCode + "\t" + pKindCode + "\t" + pTxCode);
            	if (reslinkinfo.trim().indexOf("Error:") >= 0) {
            		COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager","응답전문 거래를 찾는데 오류가 발생하였습니다.:" + reslinkinfo);
    				return "";
    	    	}
    			else if (reslinkinfo.trim().equals("NOT-FOUND")) {
    				comminfores = Proc_DBLoadTCPMSGREQ_CommonHead(pApplCode + "\tREAD");
    				if (comminfores.indexOf("NOTEXIST") >= 0) comminfores = "";
    		    }
    			else {
    				String[] arrtmpsub = reslinkinfo.split("\n")[0].split("\t");
    				comminfores = Proc_DBLoadTCPMSGREQ_CommonHead(arrtmp[0] + "\tREAD");
    				if (comminfores.indexOf("NOTEXIST") >= 0) comminfores = "";
    			}
          
            }
            else {
  
            	comminforeq = "";
            }
            
            COMMDATA.GetTCheckerLog().WriteLog("D", "MgrManager","요청공통부:" + comminforeq);
            COMMDATA.GetTCheckerLog().WriteLog("D", "MgrManager","응답공통부:" + comminfores);
            
     
			//Body부 전송전문 조립
			int    MSGSIZE = 0;
			byte[] MSGDATA = null;
			ByteArrayOutputStream baos = null;
			baos = new ByteArrayOutputStream();
			
            String[] arrmsgdat = arrbody[0].split("\n");
            try{
                for(int i=0;i < arrmsgdat.length ;i++){
                	arrtmp = arrmsgdat[i].split("\t");
                	if (arrtmp[2].trim().equals("ARRAY-S") || arrtmp[2].trim().equals("STRUCT-S")) continue;

                	//컬럼의 크기만큼의 버퍼를 할당하여, 데이타를 셋팅한다.
                	if (arrtmp[4].equals("<NODATA>")) arrtmp[4] = "";
 
                	byte[] mapdata = userfunction.Parsing(arrtmp[4].getBytes(), pApplCode, pKindCode, pTxCode);
                	if (mapdata == null){
                		mapdata = new byte[Integer.parseInt(arrtmp[3].trim())];
                		for(int j=0;j < Integer.parseInt(arrtmp[3].trim());j++) {
                			mapdata[i] = 0x20;
                		}
                	}
                	    
                	
                	byte[] tmpdat = new byte[Integer.parseInt(arrtmp[3].trim())];
                	for(int j=0;j < tmpdat.length ;j++) tmpdat[j] = 32;
                	
                	if (mapdata.length >= Integer.parseInt(arrtmp[3].trim())){
                		System.arraycopy(mapdata, 0, tmpdat, 0, Integer.parseInt(arrtmp[3].trim()));	
                	}
                	else {
                		System.arraycopy(mapdata, 0, tmpdat, 0, mapdata.length);
                	}
                	
 
                	baos.write(tmpdat, 0, tmpdat.length); 
                	comminforeq = comminforeq.replace("BODY(" + arrtmp[0].trim() + ")", new String(tmpdat));
 
                }
     		 
                /*-------- 헤더타입이 통신헤더 및 공통헤더일 경우에 HEAD 버퍼에 길이정보를 셋팅한다. --------*/
                if (arrbody.length == 2) {
                	//Body부 ByPass 매핑일 경우에 txtMsgBody에 해당하는 메세지를 MSGDATA에 Append한다.
                	baos.write(arrbody[1].getBytes(), 0, arrbody[1].getBytes().length); 
                }
                MSGDATA = baos.toByteArray();
                if (baos != null) baos.close();  //ByteArray close
            }catch(Exception baoserr){
                COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", baoserr); 
            	if (baos != null) baos.close();
            }
 
 
            //------------
            baos = new ByteArrayOutputStream();
            byte[] MSGDATACOMM = null;
            comminforeq = comminforeq.replace("BASE(KINDCODE)", pKindCode);
            comminforeq = comminforeq.replace("BASE(TXCODE)", pTxCode);
            String[] arrcomminforeq = comminforeq.split("\n");
            for(int i=0;i < arrcomminforeq.length ;i++){
            	if (arrcomminforeq[i].trim().equals("")) break;
            	String[] arrtmpsub = arrcomminforeq[i].split("\t");
 
            	//컬럼의 크기만큼의 버퍼를 할당하여, 데이타를 셋팅한다.
            	if (arrtmpsub[4].equals("<NODATA>")) arrtmpsub[4] = "";
            	
            	byte[] mapdata = userfunction.Parsing(arrtmpsub[4].getBytes(), pApplCode, pKindCode, pTxCode);
            	if (mapdata == null){
            		mapdata = new byte[Integer.parseInt(arrtmp[3].trim())];
            		for(int j=0;j < Integer.parseInt(arrtmp[3].trim());j++) {
            			mapdata[i] = 0x20;
            		}
            	}
            	
            	byte[] tmpdat = new byte[Integer.parseInt(arrtmpsub[3].trim())];
            	for(int j=0;j < tmpdat.length ;j++) tmpdat[j] = 32;
            	
            	if (mapdata.length >= Integer.parseInt(arrtmpsub[3].trim())){
            		System.arraycopy(mapdata, 0, tmpdat, 0, Integer.parseInt(arrtmpsub[3].trim()));	
            	}
            	else {
            		System.arraycopy(mapdata, 0, tmpdat, 0, mapdata.length);
            	}
            	baos.write(tmpdat, 0, tmpdat.length); 
            }
            MSGDATACOMM = baos.toByteArray();
            if (baos != null) baos.close();
           
            //응답공통헤더의 길이를 계산한다.
            int rescommsize = 0;
            String[] arrcomminfores = comminfores.split("\n");
            for(int i=0;i < arrcomminfores.length ;i++){
            	if (arrcomminfores[i].trim().equals("")) break;
            	String[] arrtmpsub = arrcomminfores[i].split("\t");
            	rescommsize = rescommsize + Integer.parseInt(arrtmpsub[3].trim());
            }
 
 
			//직접 매퍼로 데이타 전송 및 수신
			String tmpstr = Proc_DBSearchMapperName(pApplCode);
			String[] arrtmpstr = tmpstr.split("\t");
			String InstCode = arrtmpstr[0];
			String MapperName = arrtmpstr[1];
			
			//WebtCall-Start
			WebtSender jc = new WebtSender();
	 		byte[] msg = null;;
			byte[] rcvmsg = null;
			byte[] rcvdata = null;
			byte[] mergebytes = null;
 
			if (MSGDATACOMM != null) {
				mergebytes = new byte[MSGDATACOMM.length + MSGDATA.length];
				System.arraycopy(MSGDATACOMM, 0, mergebytes, 0, MSGDATACOMM.length);
				System.arraycopy(MSGDATA, 0, mergebytes, MSGDATACOMM.length, MSGDATA.length);
			}
			else {
				mergebytes = new byte[MSGDATA.length];
				System.arraycopy(MSGDATA, 0, mergebytes, 0, MSGDATA.length);
			}
 
			msg = jc.makeHeader(InstCode, pApplCode, pKindCode, pTxCode, mergebytes); // anylink헤더120byte+시스템헤더+데이터헤더+데이터
			rcvmsg = jc.MapperCall(AnylinkIP, Integer.parseInt(AnylinkPort), msg, MapperName);
 
			/* Async Inbound 거래이고, 응답거래매핑에 등록되여 있는 경우 응답을 받기 위해서 요청 거래를 남겨 놓는다. */
			/* 요청 업무/종별/거래/User PC IP */
			Proc_InboundAsyncRes(pApplCode, pKindCode, pTxCode, pUserPCIP);
			 
			rcvdata = new byte[rcvmsg.length - 120];
			for(int i=120 + rescommsize; i<rcvmsg.length; i++){
				rcvdata[i-120-rescommsize]=rcvmsg[i];
			}
		 


			return new String(rcvdata);
			//WebtCall-End
			
			//String RetStr = COMMDATA.GetTCheckerJNI().SendMapper(InstCode, pApplCode, pKindCode, pTxCode, MapperName, COMMDATA.GetTCheckerJNI().FuncStringToHex(MSGDATA));
			//return COMMDATA.GetTCheckerJNI().FuncHexToString(RetStr);
		}catch(Exception e) {
			COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", e);
			return "Error:" + e.getMessage();
		}
 
	}
	private String Proc_DBSendTCPMAPPERALLMSG(String pRecvData, String pUserPCIP )
	{
		try {
 
			String   savedata = "";
			String[] arrpRecvData = pRecvData.split("<DATAGUBUN>");
			String[] arrtmp = arrpRecvData[0].split("\t");
			String   pUserID = arrtmp[0];
			String   pApplCode = arrtmp[1];
			String   pKindCode = arrtmp[2];
			String   pTxCode = arrtmp[3];
			String   pSendPort = arrtmp[4];
			String   pSendData = arrpRecvData[1];
      
			//직접 매퍼로 데이타 전송 및 수신
			String tmpstr = Proc_DBSearchMapperName(pApplCode);
			String[] arrtmpstr = tmpstr.split("\t");
			String InstCode = arrtmpstr[0];
			String MapperName = arrtmpstr[1];
			
			//WebtCall-Start
			WebtSender jc = new WebtSender();
	 		byte[] msg;
			byte[] rcvmsg;
			byte[] rcvdata;
			msg = jc.makeHeader(InstCode, pApplCode, pKindCode, pTxCode, pSendData.getBytes()); // anylink헤더120byte+시스템헤더+데이터헤더+데이터
			rcvmsg = jc.MapperCall(AnylinkIP, Integer.parseInt(AnylinkPort), msg, MapperName);
 
			rcvdata = new byte[rcvmsg.length - 120];
			for(int i=120; i<rcvmsg.length; i++){
				rcvdata[i-120]=rcvmsg[i];
			}
			return new String(rcvdata);
			//WebtCall-End
			
			//String RetStr = COMMDATA.GetTCheckerJNI().SendMapper(InstCode, pApplCode, pKindCode, pTxCode, MapperName, COMMDATA.GetTCheckerJNI().FuncStringToHex(pSendData));
			//return COMMDATA.GetTCheckerJNI().FuncHexToString(RetStr);
		}catch(Exception e) {
			COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", e);
			return "Error:" + e.getMessage();
		}
 
	}
	
	private String Proc_DBSave_TCPREQRES_COMMHEAD(String pRecvData)
	{ 
		//확인완료
		try {
			//gApplCode + "<DATAGUBUN>" + pReqmsg + "<DATAGUBUN>" + pResMsg
			
			
			//Directory Check
			File dir1 = new File("./Request");
			if (!dir1.exists()) dir1.mkdir();
			
			File dir2 = new File("./Request/Tcpmap");
			if (!dir2.exists()) dir2.mkdir();
			
			File dir3 = new File("./Request/Tcpmap/Administrator");
			if (!dir3.exists()) dir3.mkdir();
			
			File dir4 = new File("./Response");
			if (!dir4.exists()) dir4.mkdir();
			
			File dir5 = new File("./Response/Tcpmap");
			if (!dir5.exists()) dir5.mkdir();
			
			File dir6 = new File("./Response/Tcpmap/Administrator");
			if (!dir6.exists()) dir6.mkdir();
 
            //응답전문을 Write한다.
			String   savedata = "";
			String[] arrpRecvData = pRecvData.split("<DATAGUBUN>");
 
			String   pApplCode = arrpRecvData[0];
			String   pReqMsg   = arrpRecvData[1];
			String   pResMsg   = arrpRecvData[2];
 
			String   fname1 = "./Request/Tcpmap/Administrator/" + pApplCode ;
			DataOutputStream out1 = new DataOutputStream(new FileOutputStream(new File(fname1)));
			out1.write(pReqMsg.getBytes());
			out1.close();
			
			String fname2 = "./Response/Tcpmap/Administrator/" + pApplCode ;
			DataOutputStream out2 = new DataOutputStream(new FileOutputStream(new File(fname2)));
			out2.write(pReqMsg.getBytes());
			out2.close();
		}catch(Exception e) {
			COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", e);
			return "Error:" + e.getMessage();
		}
		return "OK";
	}
	
	private String Proc_DBSaveTCPMSGREQ(String pRecvData)
	{ 
		//확인완료
		try {
			CheckLicense();
			
			//Directory Check
			File dir1 = new File("./Request");
			if (!dir1.exists()) dir1.mkdir();
			
			File dir2 = new File("./Request/Tcpmap");
			if (!dir2.exists()) dir2.mkdir();
			
			File dir3 = new File("./Request/Tcpmap/Administrator");
			if (!dir3.exists()) dir3.mkdir();
			
            //응답전문을 Write한다.
			String   savedata = "";
			String[] arrpRecvData = pRecvData.split("<DATAGUBUN>");
			String[] arrtmp = arrpRecvData[0].split("\t");
 
			String   pUserID   = arrtmp[0];
			String   pApplCode = arrtmp[1];
			String   pKindCode = arrtmp[2];
			String   pTxCode   = arrtmp[3];
 
			String   fname = "./Request/Tcpmap/Administrator/" + pApplCode + "_" + pKindCode + "_" + pTxCode;
			
			DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(fname)));
			out.write(arrpRecvData[1].getBytes());
			out.close();
		}catch(Exception e) {
			COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", e);
			return "Error:" + e.getMessage();
		}
		return "OK";
	}
	
	private String Proc_DBSaveTCPMSGRES(String pRecvData)
	{ 
		//확인완료
		try {
			CheckLicense();
			
			//Directory Check
			File dir1 = new File("./Response");
			if (!dir1.exists()) dir1.mkdir();
			
			File dir2 = new File("./Response/Tcpmap");
			if (!dir2.exists()) dir2.mkdir();
			
            //응답전문을 Write한다.
			String   savedata = "";
			String[] arrpRecvData = pRecvData.split("<DATAGUBUN>");
			String[] arrtmp = arrpRecvData[0].split("\t");
 
			String   pUserID   = arrtmp[0];
			String   pApplCode = arrtmp[1];
			String   pKindCode = arrtmp[2];
			String   pTxCode   = arrtmp[3];
 
			String   fname = "./Response/Tcpmap/" + pApplCode + "_" + pKindCode + "_" + pTxCode;
			
			DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(fname)));
			out.write(arrpRecvData[1].getBytes());
			out.close();
		}catch(Exception e) {
			COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", e);
			return "Error:" + e.getMessage();
		}
		return "OK";
	}
 
	private String Proc_DBLoadTCPMSGREQ(String pRecvData)
	{
		//확인완료
		try {
 
			CheckLicense();
			
			//Directory Check
			File dir1 = new File("./Request");
			if (!dir1.exists()) dir1.mkdir();
			
			File dir2 = new File("./Request/Tcpmap");
			if (!dir2.exists()) dir2.mkdir();
			
			String   savedata = "";
			String[] arrtmp = pRecvData.split("\t");
			String   pUserID   = arrtmp[0];
			String   pApplCode = arrtmp[1];
			String   pKindCode = arrtmp[2];
			String   pTxCode   = arrtmp[3];
			String   pWork     = arrtmp[4];
			
			File dir3 = new File("./Request/Tcpmap/" + pUserID);
			if (!dir3.exists()) dir3.mkdir();
 
            /*-------- 전문매핑정보를 DB에서 Read하여, 매핑정보를 리턴한다. ------------*/
            String   INOUT_FLAG = "";
            String   MAP_FLAG = "";
            String   HEAD_MAP_CLASS = "";
            String   BODY_MAP_CLASS = "";
            String   HEAD_CLASS_NAME = "";
            String   BODY_CLASS_NAME = "";
            
			//해당거래가 Inbound 또는 Outbound 전문인지 체크한다.
            String isql = " SELECT NVL(INOUT_FLAG, 'N'), NVL(MAP_FLAG, 'B')  ";
            isql = isql + "\n FROM TCHECKER_TXDETAIL                         ";
            isql = isql + "\n Where APPL_CODE    = '" + pApplCode + "' ";
            isql = isql + "\n   AND KIND_CODE    = '" + pKindCode + "' ";
            isql = isql + "\n   AND TX_CODE      = '" + pTxCode + "' ";
	 
			String retdata = COMMDATA.GetDBManager().SearchData(isql);
			if (retdata == null || retdata.equals("")) {
				COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", "Error:해당거래가 Inbound 또는 Outbound 전문인지 체크하세요.");
				return "Error:해당거래가 Inbound 또는 Outbound 전문인지 체크하세요.";
			}
			arrtmp = retdata.split("\n");
			String[] arrtmpsub = arrtmp[0].split("\t");
			
			INOUT_FLAG = arrtmpsub[0];
			MAP_FLAG = arrtmpsub[1];
			
			// <BODYBYPASS> 일 경우에 기존 화일에 <BODYBYPASS> 이 있으면 기 존재화일 내용을 그대로 리턴 
			// 없으면, <BODYBYPASS> 을 기 존재화일 내용에 추가하여 리턴한다.
			// 만약, <BODYBYPASS> 이 아닌데도 기존 화일에 존재하면 해당 내용을 제거하여 리턴
			String   fname = "./Request/Tcpmap/" + pUserID + "/" + pApplCode + "_" + pKindCode + "_" + pTxCode ;
			File f = new File(fname);
			if (f.exists() && pWork.equals("READ")) {
				String fileRData = ReadFileData(fname);
				if (MAP_FLAG.equals("P")) {
					if (fileRData.indexOf("<BODYBYPASS>") >= 0 ) return fileRData;
					else return fileRData + "<BODYBYPASS>";
				}
				else {
					if (fileRData.indexOf("<BODYBYPASS>") >= 0 ) {
						 String[] arrWork = fileRData.split("<BODYBYPASS>");
						 return arrWork[0];
					}
					else  return fileRData;
				}
			}
			
            if (MAP_FLAG.equals("N")) {
            	return "비정형데이타\t비정형데이타\tCHAR\t10000\t<NODATA>\n";
            }
 
			if (MAP_FLAG.equals("U")){
				String RData = ReadFileData(fname);
	            if (pWork.equals("INIT") || RData.trim().equals("")) {
					fname = "./Request/Tcpmap/" + "Administrator" + "/" + pApplCode + "_" + pKindCode + "_" + pTxCode ;
					RData = ReadFileData(fname);
	            }
 
	            return RData;
			}
			
			//Mapping 명을 읽어온다.
			  isql = "   select         decode(C.REQ_HEAD_MAPP_CLASS,'','<NODATA>', C.REQ_HEAD_MAPP_CLASS) as REQ_HEAD_MAPP_CLASS ";
		      isql = isql + "\n       , decode(C.RES_HEAD_MAPP_CLASS,'','<NODATA>', C.RES_HEAD_MAPP_CLASS) as RES_HEAD_MAPP_CLASS ";
		      isql = isql + "\n       , decode(A.REQ_BODY_MAPP_CLASS,'','<NODATA>', A.REQ_BODY_MAPP_CLASS) as REQ_BODY_MAPP_CLASS ";
		      isql = isql + "\n       , decode(A.RES_BODY_MAPP_CLASS,'','<NODATA>', A.RES_BODY_MAPP_CLASS) as RES_BODY_MAPP_CLASS ";
		      isql = isql + "\n from ALTX A , ALKIND B , ALAPPL C                         ";
		      isql = isql + "\n where A.APPL_CODE = '" + pApplCode + "'                   ";
		      isql = isql + "\n   and A.REP_KIND_CODE = '" + pKindCode + "'               ";
		      isql = isql + "\n   and A.TX_CODE = '" + pTxCode + "'                       ";
		      isql = isql + "\n   and A.APPL_CODE = C.APPL_CODE                           ";
		      isql = isql + "\n   and A.REP_KIND_CODE = B.REP_KIND_CODE                   ";
		      isql = isql + "\n   and B.APPL_CODE = C.APPL_CODE                           ";
		      isql = isql + "\n   and A.STA_TYPE = '1'                                    ";
			
			retdata = COMMDATA.GetDBManager().SearchData(isql);
			if (retdata == null || retdata.equals("")) {
				COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", "Error:해당거래에 대한 매핑클래스 정보를 읽어오지 못했습니다.");
				return "Error:해당거래에 대한 매핑클래스 정보를 읽어오지 못했습니다.";
			}
			arrtmp = retdata.split("\n");
			arrtmpsub = arrtmp[0].split("\t");
			
			if (MAP_FLAG.equals("U")) {
				INOUT_FLAG = "I";
			}

			try{
				
				COMMDATA.GetTCheckerLog().WriteLog("I", "MgrManager", "HEAD_MAPP_CLASS:" + arrtmpsub[0] + ":" + arrtmpsub[1]);
				COMMDATA.GetTCheckerLog().WriteLog("I", "MgrManager", "BODY_MAPP_CLASS:" + arrtmpsub[2] + ":" + arrtmpsub[3]);
			}catch(Exception eeeee){}
			 
			HEAD_MAP_CLASS = arrtmpsub[0];
			BODY_MAP_CLASS = arrtmpsub[2];
 
			COMMDATA.GetTCheckerLog().WriteLog("I", "MgrManager", "INOUT_FLAG:" + INOUT_FLAG + ":" + HEAD_MAP_CLASS + ":" + BODY_MAP_CLASS);
			
			if (INOUT_FLAG.equals("I") || INOUT_FLAG.equals("O")){
 
				isql =        "select CLASS_NAME,SRC_CLASS_NAME,DST_CLASS_NAME,MAP_NAME from ALTXMAP ";
				isql = isql + "\n where  CLASS_NAME in ('" + HEAD_MAP_CLASS + "', '" + BODY_MAP_CLASS + "') ";
				retdata = COMMDATA.GetDBManager().SearchData(isql);
				if (retdata == null || retdata.equals("")) {
					COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", "Error:해당거래에 대한 매핑클래스명 정보를 읽어오지 못했습니다.");
					return "Error:해당거래에 대한 매핑클래스명 정보를 읽어오지 못했습니다.";
				}
		 
				arrtmp = retdata.split("\n");
				for(int i=0;i < arrtmp.length ;i++){
					arrtmpsub = arrtmp[i].split("\t");
					if (arrtmpsub[0].equals(HEAD_MAP_CLASS)) {
						if (INOUT_FLAG.equals("I")) HEAD_CLASS_NAME = arrtmpsub[1];
						if (INOUT_FLAG.equals("O")) HEAD_CLASS_NAME = arrtmpsub[2];
						
						COMMDATA.GetTCheckerLog().WriteLog("I", "MgrManager", "HEAD_CLASS_NAME:" + arrtmpsub[1] + ":" + arrtmpsub[2] );
					}
                    if (arrtmpsub[0].equals(BODY_MAP_CLASS)) {
                    	if (INOUT_FLAG.equals("I")) BODY_CLASS_NAME = arrtmpsub[1];
                    	if (INOUT_FLAG.equals("O")) BODY_CLASS_NAME = arrtmpsub[2];
                    	COMMDATA.GetTCheckerLog().WriteLog("I", "MgrManager", "BODY_CLASS_NAME:" + arrtmpsub[1] + ":" + arrtmpsub[2] );
					}
				}
				COMMDATA.GetTCheckerLog().WriteLog("I", "MgrManager", "CLASS_NAME:" + HEAD_CLASS_NAME + ":" + BODY_CLASS_NAME );
				
				//매핑에 대한 컬럼정보를 읽어온다.
				isql = "          with B as (select CLASS_NAME, max(VERSION_NO) as VERSION_NO from ALMSG where sta_type = '1'  group by CLASS_NAME)                       ";
		        isql = isql + "\n select A.CLASS_NAME, A.SEQNO, NVL(A.FLD_KOR_NAME,A.FLD_NAME) , A.FLD_NAME,                                                             ";
		        isql = isql + "\n        CASE WHEN A.TYPE = '4' THEN 'CHAR'          ";
		        isql = isql + "\n             WHEN A.TYPE = '9' THEN 'CHAR'          ";
		        isql = isql + "\n             WHEN A.TYPE = '17' THEN 'CHAR'         ";
		        isql = isql + "\n             WHEN A.TYPE = '25' THEN 'CHAR'         ";
		        isql = isql + "\n             WHEN A.TYPE = '11' THEN 'ARRAY-S'      ";
		        isql = isql + "\n             WHEN A.TYPE = '13' THEN 'STRUCT-S'     ";
		        isql = isql + "\n             WHEN A.TYPE = '14' THEN 'STRUCT-E'     ";
		        isql = isql + "\n             WHEN A.TYPE = '12' THEN 'ARRAY-E'      ";
		        isql = isql + "\n      ELSE 'NUM'                             ";
		        isql = isql + "\n END AS TYPE                                 ";
		        isql = isql + "\n      , A.LEN,A.ARRAY_SIZE                                                                                                               ";
		        isql = isql + "\n from ALMSGFLD A, B                                                                                                                      ";
		        isql = isql + "\n where A.CLASS_NAME = B.CLASS_NAME                                                                                                       ";
		        isql = isql + "\n   and A.VERSION_NO = B.VERSION_NO                                                                                                       ";
		        isql = isql + "\n   and A.CLASS_NAME in ('" + HEAD_CLASS_NAME + "', '" + BODY_CLASS_NAME + "') ";
		        isql = isql + "\n order by A.CLASS_NAME,A.SEQNO, A.VERSION_NO                                                                                             ";
				
				retdata = COMMDATA.GetDBManager().SearchData(isql);
				if (retdata == null || retdata.equals("")) {
					COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", "Error:해당거래에 대한 매핑 컬럼정보를 읽어오지 못했습니다.");
					return "Error:해당거래에 대한 매핑 컬럼정보를 읽어오지 못했습니다.";
				}
				
				//최종 매핑컬럼정보를 조합하여 결과 리턴
				String headstr = "";
				String bodystr = "";
				
				arrtmp = retdata.split("\n");
				for(int i=0;i < arrtmp.length;i++){
					arrtmpsub = arrtmp[i].split("\t");
					if (arrtmpsub[0].equals(HEAD_CLASS_NAME)) {
						if (arrtmpsub[4].equals("ARRAY-S")) arrtmpsub[5] = arrtmpsub[6];
						headstr = headstr + arrtmpsub[2] + "\t" + arrtmpsub[3]  + "\t" +  arrtmpsub[4]  + "\t" +  arrtmpsub[5] + "\t<NODATA>\n";
					}
					if (arrtmpsub[0].equals(BODY_CLASS_NAME)) {
						if (arrtmpsub[4].equals("ARRAY-S")) arrtmpsub[5] = arrtmpsub[6];
						bodystr = bodystr + arrtmpsub[2] + "\t" + arrtmpsub[3]  + "\t" +  arrtmpsub[4]  + "\t" +  arrtmpsub[5] + "\t<NODATA>\n";
					}
				}
		 
				//User가 Header를 변경해야 하는 경우에 로직 처리
				String ConvHead = UserHeader(pApplCode, "REQ");
				if (!ConvHead.equals("")) headstr = ConvHead;
				
				if (MAP_FLAG.equals("B")) return headstr + bodystr;
				if (MAP_FLAG.equals("P")) return headstr + "<BODYBYPASS>";
				if (MAP_FLAG.equals("H")) return bodystr;
			}
			else {
				COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", "Error:해당거래가 Inbound 또는 Outbound 전문인지 체크하세요.");
				return "Error:해당거래가 Inbound 또는 Outbound 전문인지 체크하세요.";
			}
			
		}catch(Exception e) {
			COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", e);
			return "Error:" + e.getMessage();
		}
		return "";
	}
	private String Proc_DBLoadTCPMSGREQ_CommonHead(String pRecvData)
	{
		//확인완료
		try {
			String[] arrtmp = pRecvData.split("\t");
			String   pApplCode   = arrtmp[0];
			String   pWork       = arrtmp[1];
			String   INOUT_FLAG  = "I";
 
			//공통헤더 제외여부 정보를 읽음
			String isql = " SELECT count(*)                           ";
		      isql = isql + "\n FROM TCHECKER_TXDETAIL T, ALTX A        ";
		      isql = isql + "\n Where T.APPL_CODE = '" + pApplCode + "' ";
		      isql = isql + "\n   and T.MAP_FLAG  = 'H'                 ";
		      isql = isql + "\n   and T.APPL_CODE = A.APPL_CODE         ";
		      isql = isql + "\n   and T.KIND_CODE = A.REP_KIND_CODE     ";
		      isql = isql + "\n   and T.TX_CODE   = A.TX_CODE           ";
		      isql = isql + "\n   and A.STA_TYPE = '1'                  ";
			String retdata = COMMDATA.GetDBManager().SearchData(isql);
			if (retdata == null || retdata.equals("")) {
				COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", "Error:공통헤더 제외여부 정보를 읽어오는데 실패하였습니다.");
				return "Error:공통헤더 제외여부 정보를 읽어오는데 실패하였습니다.";
			}
			arrtmp = retdata.split("\n");
			if (Integer.parseInt(arrtmp[0]) <= 0) return "NOTEXIST";
			
			
			String   fname = "./Request/Tcpmap/Administrator/" + pApplCode  ;
			File f = new File(fname);
			if (f.exists() && pWork.equals("READ")) {
				return ReadFileData(fname);
			}
			
            /*-------- 전문매핑정보를 DB에서 Read하여, 매핑정보를 리턴한다. ------------*/
            String   HEAD_MAP_CLASS  = "";
            String   HEAD_CLASS_NAME = "";
			
			//Mapping 명을 읽어온다.
			isql = "   select         decode(C.REQ_HEAD_MAPP_CLASS,'','<NODATA>', C.REQ_HEAD_MAPP_CLASS) as REQ_HEAD_MAPP_CLASS ";
		    isql = isql + "\n       , decode(C.RES_HEAD_MAPP_CLASS,'','<NODATA>', C.RES_HEAD_MAPP_CLASS) as RES_HEAD_MAPP_CLASS  ";
            isql = isql + "\n from  ALAPPL C                                            ";
            isql = isql + "\n where C.APPL_CODE = '" + pApplCode + "'                   ";
            isql = isql + "\n   and C.STA_TYPE = '1'                                    ";
			
			retdata = COMMDATA.GetDBManager().SearchData(isql);
			if (retdata == null || retdata.equals("")) {
				COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", "Error:해당거래에 대한 매핑클래스 정보를 읽어오지 못했습니다.");
				return "Error:해당거래에 대한 매핑클래스 정보를 읽어오지 못했습니다.";
			}
			
			arrtmp = retdata.split("\n");
			String[] arrtmpsub = arrtmp[0].split("\t");
			HEAD_MAP_CLASS = arrtmpsub[0];
			
			if (INOUT_FLAG.equals("I") || INOUT_FLAG.equals("O")){
 
				isql = "select CLASS_NAME,SRC_CLASS_NAME,DST_CLASS_NAME,MAP_NAME from ALTXMAP ";
		        isql = isql + "\n where  CLASS_NAME in ('" + HEAD_MAP_CLASS + "') ";
				retdata = COMMDATA.GetDBManager().SearchData(isql);
				if (retdata == null || retdata.equals("")) {
					COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", "Error:해당거래에 대한 매핑클래스명 정보를 읽어오지 못했습니다.");
					return "Error:해당거래에 대한 매핑클래스명 정보를 읽어오지 못했습니다.";
				}
		 
				arrtmp = retdata.split("\n");
				for(int i=0;i < arrtmp.length ;i++){
					arrtmpsub = arrtmp[i].split("\t");
					if (arrtmpsub[0].equals(HEAD_MAP_CLASS)) {
						if (INOUT_FLAG.equals("I")) HEAD_CLASS_NAME = arrtmpsub[1];
						if (INOUT_FLAG.equals("O")) HEAD_CLASS_NAME = arrtmpsub[2];
					}
				}
 
				//매핑에 대한 컬럼정보를 읽어온다.
				isql = "          with B as (select CLASS_NAME, max(VERSION_NO) as VERSION_NO from ALMSG where sta_type = '1'  group by CLASS_NAME)                       ";
		        isql = isql + "\n select A.CLASS_NAME, A.SEQNO, NVL(A.FLD_KOR_NAME,A.FLD_NAME) , A.FLD_NAME ,                                                                              ";

		        isql = isql + "\n        CASE WHEN A.TYPE = '4' THEN 'CHAR'          ";
		        isql = isql + "\n             WHEN A.TYPE = '9' THEN 'CHAR'          ";
		        isql = isql + "\n             WHEN A.TYPE = '17' THEN 'CHAR'         ";
		        isql = isql + "\n             WHEN A.TYPE = '25' THEN 'CHAR'         ";
		        isql = isql + "\n             WHEN A.TYPE = '11' THEN 'ARRAY-S'      ";
		        isql = isql + "\n             WHEN A.TYPE = '13' THEN 'STRUCT-S'     ";
		        isql = isql + "\n             WHEN A.TYPE = '14' THEN 'STRUCT-E'     ";
		        isql = isql + "\n             WHEN A.TYPE = '12' THEN 'ARRAY-E'      ";
		        isql = isql + "\n      ELSE 'NUM'                             ";
		        isql = isql + "\n END AS TYPE                                 ";

		        isql = isql + "\n      , A.LEN,A.ARRAY_SIZE                                                                                                               ";
		        isql = isql + "\n from ALMSGFLD A, B                                                                                                                      ";
		        isql = isql + "\n where A.CLASS_NAME = B.CLASS_NAME                                                                                                       ";
		        isql = isql + "\n   and A.VERSION_NO = B.VERSION_NO                                                                                                       ";
		        isql = isql + "\n   and A.CLASS_NAME in ('" + HEAD_CLASS_NAME + "') ";
		        isql = isql + "\n order by A.CLASS_NAME,A.SEQNO, A.VERSION_NO                                                                                             ";
				
				retdata = COMMDATA.GetDBManager().SearchData(isql);
				if (retdata == null || retdata.equals("")) {
					COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", "Error:해당거래에 대한 매핑 컬럼정보를 읽어오지 못했습니다.");
					return "Error:해당거래에 대한 매핑 컬럼정보를 읽어오지 못했습니다.";
				}
				
				//최종 매핑컬럼정보를 조합하여 결과 리턴
				String headstr = "";
				String bodystr = "";
				
				arrtmp = retdata.split("\n");
				for(int i=0;i < arrtmp.length;i++){
					arrtmpsub = arrtmp[i].split("\t");
					if (arrtmpsub[0].equals(HEAD_CLASS_NAME)) {
						if (arrtmpsub[4].equals("ARRAY-S")) arrtmpsub[5] = arrtmpsub[6];
						headstr = headstr + arrtmpsub[2] + "\t" + arrtmpsub[3]  + "\t" +  arrtmpsub[4]  + "\t" +  arrtmpsub[5] + "\t<NODATA>\n";
					}
				}
 
				//User가 Header를 변경해야 하는 경우에 로직 처리
				String ConvHead = UserHeader(pApplCode, "REQ");
				if (!ConvHead.equals("")) headstr = ConvHead;
				
				return headstr;
			}
			else {
				COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", "Error:해당거래가 Inbound 또는 Outbound 전문인지 체크하세요.");
				return "Error:해당거래가 Inbound 또는 Outbound 전문인지 체크하세요.";
			}
			
		}catch(Exception e) {
			COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", e);
			return "Error:" + e.getMessage();
		}
 
	}
	
	private String Proc_DBLoadTCPMSGRES(String pRecvData)
	{
		try {
			CheckLicense();
			
			//Directory Check
			File dir1 = new File("./Response");
			if (!dir1.exists()) dir1.mkdir();
			
			File dir2 = new File("./Response/Tcpmap");
			if (!dir2.exists()) dir2.mkdir();
			
			String   savedata = "";
			String[] arrtmp = pRecvData.split("\t");
			String   pUserID   = arrtmp[0];
			String   pApplCode = arrtmp[1];
			String   pKindCode = arrtmp[2];
			String   pTxCode   = arrtmp[3];
			String   pWork     = arrtmp[4];
 
            /*-------- 전문매핑정보를 DB에서 Read하여, 매핑정보를 리턴한다. ------------*/
            String   INOUT_FLAG = "";
            String   MAP_FLAG = "";
            String   HEAD_MAP_CLASS = "";
            String   BODY_MAP_CLASS = "";
            String   HEAD_CLASS_NAME = "";
            String   BODY_CLASS_NAME = "";
            
			//해당거래가 Inbound 또는 Outbound 전문인지 체크한다.
            String isql = " SELECT NVL(INOUT_FLAG, 'N'), NVL(MAP_FLAG, 'B') ";
            isql = isql + "\n FROM TCHECKER_TXDETAIL                         ";
            isql = isql + "\n Where APPL_CODE    = '" + pApplCode + "' ";
            isql = isql + "\n   AND KIND_CODE    = '" + pKindCode + "' ";
            isql = isql + "\n   AND TX_CODE      = '" + pTxCode + "' ";
	 
			String retdata = COMMDATA.GetDBManager().SearchData(isql);
			if (retdata == null || retdata.equals("")) {
				COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", "Error:해당거래가 Inbound 또는 Outbound 전문인지 체크하세요.");
				return "Error:해당거래가 Inbound 또는 Outbound 전문인지 체크하세요.";
			}
 
			arrtmp = retdata.split("\n");
			String[] arrtmpsub = arrtmp[0].split("\t");
 
			INOUT_FLAG = arrtmpsub[0];
			MAP_FLAG = arrtmpsub[1];
			
			// <BODYBYPASS> 일 경우에 기존 화일에 <BODYBYPASS> 이 있으면 기 존재화일 내용을 그대로 리턴 
			// 없으면, <BODYBYPASS> 을 기 존재화일 내용에 추가하여 리턴한다.
			// 만약, <BODYBYPASS> 이 아닌데도 기존 화일에 존재하면 해당 내용을 제거하여 리턴
			String   fname = "./Response/Tcpmap/" + pApplCode + "_" + pKindCode + "_" + pTxCode ;
			File f = new File(fname);
			if (f.exists() && pWork.equals("READ")) {
				String fileRData = ReadFileData(fname);
				if (MAP_FLAG.equals("P")) {
					if (fileRData.indexOf("<BODYBYPASS>") >= 0 ) return fileRData;
					else return fileRData + "<BODYBYPASS>";
				}
				else {
					if (fileRData.indexOf("<BODYBYPASS>") >= 0 ) {
						 String[] arrWork = fileRData.split("<BODYBYPASS>");
						 return arrWork[0];
					}
					else  return fileRData;
				}
			}
			if (MAP_FLAG.equals("N")) {
            	return "비정형데이타\t비정형데이타\tCHAR\t10000\t<NODATA>\n";
            }
 
			if (MAP_FLAG.equals("U")){

				String RData = ReadFileData(fname);
	            if (pWork.equals("INIT")) {
					fname = "./Response/Tcpmap/" + "Administrator" + "/" + pApplCode + "_" + pKindCode + "_" + pTxCode ;
					RData = ReadFileData(fname);
	            }
	            return RData;

			}
			
			//Mapping 명을 읽어온다.
			isql = "   select           decode(C.REQ_HEAD_MAPP_CLASS,'','<NODATA>', C.REQ_HEAD_MAPP_CLASS) as REQ_HEAD_MAPP_CLASS ";
		      isql = isql + "\n       , decode(C.RES_HEAD_MAPP_CLASS,'','<NODATA>', C.RES_HEAD_MAPP_CLASS) as RES_HEAD_MAPP_CLASS";
		      isql = isql + "\n       , decode(A.REQ_BODY_MAPP_CLASS,'','<NODATA>', A.REQ_BODY_MAPP_CLASS) as REQ_BODY_MAPP_CLASS";
		      isql = isql + "\n       , decode(A.RES_BODY_MAPP_CLASS,'','<NODATA>', A.RES_BODY_MAPP_CLASS) as RES_BODY_MAPP_CLASS";
		      isql = isql + "\n from ALTX A , ALKIND B , ALAPPL C                         ";
		      isql = isql + "\n where A.APPL_CODE = '" + pApplCode + "'                   ";
		      isql = isql + "\n   and A.REP_KIND_CODE = '" + pKindCode + "'               ";
		      isql = isql + "\n   and A.TX_CODE = '" + pTxCode + "'                       ";
		      isql = isql + "\n   and A.APPL_CODE = C.APPL_CODE                           ";
		      isql = isql + "\n   and A.REP_KIND_CODE = B.REP_KIND_CODE                   ";
		      isql = isql + "\n   and B.APPL_CODE = C.APPL_CODE                           ";
		      isql = isql + "\n   and A.STA_TYPE = '1'                                    ";
			
			retdata = COMMDATA.GetDBManager().SearchData(isql);
			if (retdata == null || retdata.equals("")) {
				COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", "Error:해당거래에 대한 매핑클래스 정보를 읽어오지 못했습니다.");
				return "Error:해당거래에 대한 매핑클래스 정보를 읽어오지 못했습니다.";
			}
			arrtmp = retdata.split("\n");
			arrtmpsub = arrtmp[0].split("\t");
			
			if (MAP_FLAG.equals("U")) {
				INOUT_FLAG = "I";
			}
			
 
			
			HEAD_MAP_CLASS = arrtmpsub[1];
			BODY_MAP_CLASS = arrtmpsub[3];
			if (INOUT_FLAG.equals("I") || INOUT_FLAG.equals("O")){
 
				isql =        "select CLASS_NAME,SRC_CLASS_NAME,DST_CLASS_NAME,MAP_NAME from ALTXMAP ";
				isql = isql + "\n where  CLASS_NAME in ('" + HEAD_MAP_CLASS + "', '" + BODY_MAP_CLASS + "') ";
				retdata = COMMDATA.GetDBManager().SearchData(isql);
				if (retdata == null || retdata.equals("")) {
					COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", "Error:해당거래에 대한 매핑클래스명 정보를 읽어오지 못했습니다.");
					return "Error:해당거래에 대한 매핑클래스명 정보를 읽어오지 못했습니다.";
				}
				arrtmp = retdata.split("\n");
				for(int i=0;i < arrtmp.length ;i++){
					arrtmpsub = arrtmp[i].split("\t");
					if (arrtmpsub[0].equals(HEAD_MAP_CLASS)) {
						if (INOUT_FLAG.equals("I")) HEAD_CLASS_NAME = arrtmpsub[2];
						if (INOUT_FLAG.equals("O")) HEAD_CLASS_NAME = arrtmpsub[1];
					}
                    if (arrtmpsub[0].equals(BODY_MAP_CLASS)) {
                    	if (INOUT_FLAG.equals("I")) BODY_CLASS_NAME = arrtmpsub[2];
                    	if (INOUT_FLAG.equals("O")) BODY_CLASS_NAME = arrtmpsub[1];
					}
 
				}
				
				//매핑에 대한 컬럼정보를 읽어온다.
				isql = "          with B as (select CLASS_NAME, max(VERSION_NO) as VERSION_NO from ALMSG where sta_type = '1'  group by CLASS_NAME)                       ";
		        isql = isql + "\n select A.CLASS_NAME, A.SEQNO, NVL(A.FLD_KOR_NAME,A.FLD_NAME) , A.FLD_NAME,                                                                               ";

		        isql = isql + "\n        CASE WHEN A.TYPE = '4' THEN 'CHAR'          ";
		        isql = isql + "\n             WHEN A.TYPE = '9' THEN 'CHAR'          ";
		        isql = isql + "\n             WHEN A.TYPE = '17' THEN 'CHAR'         ";
		        isql = isql + "\n             WHEN A.TYPE = '25' THEN 'CHAR'         ";
		        isql = isql + "\n             WHEN A.TYPE = '11' THEN 'ARRAY-S'      ";
		        isql = isql + "\n             WHEN A.TYPE = '13' THEN 'STRUCT-S'     ";
		        isql = isql + "\n             WHEN A.TYPE = '14' THEN 'STRUCT-E'     ";
		        isql = isql + "\n             WHEN A.TYPE = '12' THEN 'ARRAY-E'      ";
		        isql = isql + "\n      ELSE 'NUM'                                    ";
		        isql = isql + "\n      END AS TYPE                                   ";

		        isql = isql + "\n      , A.LEN,A.ARRAY_SIZE                                                                                                               ";
		        isql = isql + "\n from ALMSGFLD A, B                                                                                                                      ";
		        isql = isql + "\n where A.CLASS_NAME = B.CLASS_NAME                                                                                                       ";
		        isql = isql + "\n   and A.VERSION_NO = B.VERSION_NO                                                                                                       ";
		        isql = isql + "\n   and A.CLASS_NAME in ('" + HEAD_CLASS_NAME + "', '" + BODY_CLASS_NAME + "') ";
		        isql = isql + "\n order by A.CLASS_NAME,A.SEQNO, A.VERSION_NO                                                                                             ";
		
				retdata = COMMDATA.GetDBManager().SearchData(isql);
				if (retdata == null || retdata.equals("")) {
					COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", "Error:해당거래에 대한 매핑 컬럼정보를 읽어오지 못했습니다.");
					return "Error:해당거래에 대한 매핑 컬럼정보를 읽어오지 못했습니다.";
				}
				
				//최종 매핑컬럼정보를 조합하여 결과 리턴
				String headstr = "";
				String bodystr = "";
				
				arrtmp = retdata.split("\n");
				for(int i=0;i < arrtmp.length;i++){
					arrtmpsub = arrtmp[i].split("\t");
					if (arrtmpsub[0].equals(HEAD_CLASS_NAME)) {
						if (arrtmpsub[4].equals("ARRAY-S")) arrtmpsub[5] = arrtmpsub[6];
						headstr = headstr + arrtmpsub[2] + "\t" + arrtmpsub[3]  + "\t" +  arrtmpsub[4]  + "\t" +  arrtmpsub[5] + "\t<NODATA>\n";
					}
					if (arrtmpsub[0].equals(BODY_CLASS_NAME)) {
						if (arrtmpsub[4].equals("ARRAY-S")) arrtmpsub[5] = arrtmpsub[6];
						bodystr = bodystr + arrtmpsub[2] + "\t" + arrtmpsub[3]  + "\t" +  arrtmpsub[4]  + "\t" +  arrtmpsub[5] + "\t<NODATA>\n";
					}
				}
				
				//User가 Header를 변경해야 하는 경우에 로직 처리
				String ConvHead = UserHeader(pApplCode, "RES");
				if (!ConvHead.equals("")) headstr = ConvHead;
				
				if (MAP_FLAG.equals("B")) return headstr + bodystr;
				if (MAP_FLAG.equals("P")) return headstr + "<BODYBYPASS>";
				if (MAP_FLAG.equals("H")) return bodystr;
			}
			else {
				COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", "Error:해당거래가 Inbound 또는 Outbound 전문인지 체크하세요.");
				return "Error:해당거래가 Inbound 또는 Outbound 전문인지 체크하세요.";
			}
			
		}catch(Exception e) {
			COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", e);
			return "Error:" + e.getMessage();
		}
		return "";
	}
	
	private String Proc_DBLoadTCPMSGRES_File(String pRecvData)
	{
		try {
			
			//Directory Check
			File dir1 = new File("./Response");
			if (!dir1.exists()) dir1.mkdir();
			
			File dir2 = new File("./Response/Tcpmap");
			if (!dir2.exists()) dir2.mkdir();
			
			String[] arrtmp = pRecvData.split("\t");
			String   pApplCode = arrtmp[0];
			String   pKindCode = arrtmp[1];
			String   pTxCode   = arrtmp[2];
 
			
			String   fname = "./Response/Tcpmap/" + pApplCode + "_" + pKindCode + "_" + pTxCode ;
			
			File f = new File(fname);
			if (f.exists() ) {
	            return ReadFileData(fname);
			}
 
		}catch(Exception e) {}
		
		return "";
	}
	private String Proc_DBLoadTCPMSGRES_CommonHead(String pRecvData)
	{
		try {
 
			String[] arrtmp = pRecvData.split("\t");
			String   pApplCode   = arrtmp[0];
			String   pWork       = arrtmp[1];
			String   INOUT_FLAG  = "O";
 
			//공통헤더 제외여부 정보를 읽음
			String isql = " SELECT count(*)                           ";
		      isql = isql + "\n FROM TCHECKER_TXDETAIL T, ALTX A        ";
		      isql = isql + "\n Where T.APPL_CODE = '" + pApplCode + "' ";
		      isql = isql + "\n   and T.MAP_FLAG  = 'H'                 ";
		      isql = isql + "\n   and T.APPL_CODE = A.APPL_CODE         ";
		      isql = isql + "\n   and T.KIND_CODE = A.REP_KIND_CODE     ";
		      isql = isql + "\n   and T.TX_CODE   = A.TX_CODE           ";
		      isql = isql + "\n   and A.STA_TYPE = '1'                  ";
			String retdata = COMMDATA.GetDBManager().SearchData(isql);
			if (retdata == null || retdata.equals("")) {
				COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", "Error:공통헤더 제외여부 정보를 읽어오는데 실패하였습니다.");
				return "Error:공통헤더 제외여부 정보를 읽어오는데 실패하였습니다.";
			}
			arrtmp = retdata.split("\n");
			if (Integer.parseInt(arrtmp[0]) <= 0) return "NOTEXIST";
			
			String   fname = "./Response/Tcpmap/Administrator/" + pApplCode  ;
			File f = new File(fname);
			if (f.exists() && pWork.equals("READ")) {
				return ReadFileData(fname);
			}
			
            /*-------- 전문매핑정보를 DB에서 Read하여, 매핑정보를 리턴한다. ------------*/
            String   HEAD_MAP_CLASS  = "";
            String   HEAD_CLASS_NAME = "";

			//Mapping 명을 읽어온다.
			isql = "   select         decode(C.REQ_HEAD_MAPP_CLASS,'','<NODATA>', C.REQ_HEAD_MAPP_CLASS) as REQ_HEAD_MAPP_CLASS";
		    isql = isql + "\n       , decode(C.RES_HEAD_MAPP_CLASS,'','<NODATA>', C.RES_HEAD_MAPP_CLASS) as RES_HEAD_MAPP_CLASS";
            isql = isql + "\n from  ALAPPL C                                            ";
            isql = isql + "\n where C.APPL_CODE = '" + pApplCode + "'                   ";
            isql = isql + "\n   and C.STA_TYPE = '1'                                    ";
			
            retdata = COMMDATA.GetDBManager().SearchData(isql);
			if (retdata == null || retdata.equals("")) {
				COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", "Error:해당거래에 대한 매핑클래스 정보를 읽어오지 못했습니다.");
				return "Error:해당거래에 대한 매핑클래스 정보를 읽어오지 못했습니다.";
			}
			arrtmp = retdata.split("\n");
			String[] arrtmpsub = arrtmp[0].split("\t");
 
			HEAD_MAP_CLASS = arrtmpsub[1];
			if (INOUT_FLAG.equals("I") || INOUT_FLAG.equals("O")){
 
				isql =        "select CLASS_NAME,SRC_CLASS_NAME,DST_CLASS_NAME,MAP_NAME from ALTXMAP ";
				isql = isql + "\n where  CLASS_NAME in ('" + HEAD_MAP_CLASS + "' ) ";
				retdata = COMMDATA.GetDBManager().SearchData(isql);
				if (retdata == null || retdata.equals("")) {
					COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", "Error:해당거래에 대한 매핑클래스명 정보를 읽어오지 못했습니다.");
					return "Error:해당거래에 대한 매핑클래스명 정보를 읽어오지 못했습니다.";
				}
				arrtmp = retdata.split("\n");
				for(int i=0;i < arrtmp.length ;i++){
					arrtmpsub = arrtmp[i].split("\t");
					if (arrtmpsub[0].equals(HEAD_MAP_CLASS)) {
						if (INOUT_FLAG.equals("I")) HEAD_CLASS_NAME = arrtmpsub[2];
						if (INOUT_FLAG.equals("O")) HEAD_CLASS_NAME = arrtmpsub[1];
					}
 
				}
				
				//매핑에 대한 컬럼정보를 읽어온다.
				isql = "          with B as (select CLASS_NAME, max(VERSION_NO) as VERSION_NO from ALMSG where sta_type = '1'  group by CLASS_NAME)                       ";
		        isql = isql + "\n select A.CLASS_NAME, A.SEQNO, NVL(A.FLD_KOR_NAME,A.FLD_NAME)  , A.FLD_NAME ,                                                                              ";

		        isql = isql + "\n        CASE WHEN A.TYPE = '4' THEN 'CHAR'          ";
		        isql = isql + "\n             WHEN A.TYPE = '9' THEN 'CHAR'          ";
		        isql = isql + "\n             WHEN A.TYPE = '17' THEN 'CHAR'         ";
		        isql = isql + "\n             WHEN A.TYPE = '25' THEN 'CHAR'         ";
		        isql = isql + "\n             WHEN A.TYPE = '11' THEN 'ARRAY-S'      ";
		        isql = isql + "\n             WHEN A.TYPE = '13' THEN 'STRUCT-S'     ";
		        isql = isql + "\n             WHEN A.TYPE = '14' THEN 'STRUCT-E'     ";
		        isql = isql + "\n             WHEN A.TYPE = '12' THEN 'ARRAY-E'      ";
		        isql = isql + "\n      ELSE 'NUM'                                    ";
		        isql = isql + "\n      END AS TYPE                                   ";

		        isql = isql + "\n      , A.LEN,A.ARRAY_SIZE                                                                                                               ";
		        isql = isql + "\n from ALMSGFLD A, B                                                                                                                      ";
		        isql = isql + "\n where A.CLASS_NAME = B.CLASS_NAME                                                                                                       ";
		        isql = isql + "\n   and A.VERSION_NO = B.VERSION_NO                                                                                                       ";
		        isql = isql + "\n   and A.CLASS_NAME in ('" + HEAD_CLASS_NAME + "' ) ";
		        isql = isql + "\n order by A.CLASS_NAME,A.SEQNO, A.VERSION_NO                                                                                             ";
	
				retdata = COMMDATA.GetDBManager().SearchData(isql);
				if (retdata == null || retdata.equals("")) {
					COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", "Error:해당거래에 대한 매핑 컬럼정보를 읽어오지 못했습니다.");
					return "Error:해당거래에 대한 매핑 컬럼정보를 읽어오지 못했습니다.";
				}
				
				//최종 매핑컬럼정보를 조합하여 결과 리턴
				String headstr = "";
				String bodystr = "";
				
				arrtmp = retdata.split("\n");
				for(int i=0;i < arrtmp.length;i++){
					arrtmpsub = arrtmp[i].split("\t");
					if (arrtmpsub[0].equals(HEAD_CLASS_NAME)) {
						if (arrtmpsub[4].equals("ARRAY-S")) arrtmpsub[5] = arrtmpsub[6];
						headstr = headstr + arrtmpsub[2] + "\t" + arrtmpsub[3]  + "\t" +  arrtmpsub[4]  + "\t" +  arrtmpsub[5] + "\t<NODATA>\n";
					}
 
				}
 
				//User가 Header를 변경해야 하는 경우에 로직 처리
				String ConvHead = UserHeader(pApplCode, "REQ");
				if (!ConvHead.equals("")) headstr = ConvHead;
				
				return headstr;
			}
			else {
				COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", "Error:해당거래가 Inbound 또는 Outbound 전문인지 체크하세요.");
				return "Error:해당거래가 Inbound 또는 Outbound 전문인지 체크하세요.";
			}
			
		}catch(Exception e) {
			COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", e);
			return "Error:" + e.getMessage();
		}
	 
	}
	
	private String Proc_DBSearchInternalInfo(String pApplCode)
	{
		String isql = " select A.APPL_CODE,T.REP_KIND_CODE,T.TX_CODE, ";
	    isql = isql + "\n      A.KEY_OFFSET1,A.KEY_LEN1, NVL(T.KEY_VAL1, '<NODATA>' ) , ";
	    isql = isql + "\n      A.KEY_OFFSET2,A.KEY_LEN2, NVL(T.KEY_VAL2, '<NODATA>' ) , ";
	    isql = isql + "\n      A.KEY_OFFSET3,A.KEY_LEN3, NVL(T.KEY_VAL3, '<NODATA>' )  ";
	    isql = isql + "\n from   ALAPPL A, ALTX T ";
	    isql = isql + "\n WHERE  A.APPL_CODE IN (SELECT DISTINCT APPL_CODE FROM ALGRPLINE WHERE GWNAME IN (SELECT GWNAME FROM ALGW WHERE EXT_TYPE = 2))  ";
	    isql = isql + "\n   AND A.APPL_CODE = '" + pApplCode + "'";
	    isql = isql + "\n   AND A.APPL_CODE = T.APPL_CODE        ";
	    isql = isql + "\n ORDER BY T.REP_KIND_CODE,T.TX_CODE     ";
 
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
 
		if (retdata == null || retdata.equals("")) return "NOT-FOUND";

		return retdata;
	}
	
    private void Proc_InboundAsyncRes(String pApplCode, String pKindCode, String pTxCode, String pIP)
    {
    	String reslinkinfo = Proc_DBSearchResLinkOne(pApplCode + "\t" + pKindCode + "\t" + pTxCode);
        if (reslinkinfo.trim().equals("") || reslinkinfo.trim().equals("NOT-FOUND")) return;
    	
    	String[] arrtmp = reslinkinfo.split("\t");
    	
    	
		SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
		String iDate = formatter.format(new java.util.Date()); //요청일자
		
		formatter = new java.text.SimpleDateFormat("HHmmss", java.util.Locale.KOREA);
		String iTime = formatter.format(new java.util.Date());  //요청시간
		
		String isql = "INSERT INTO TCHECKER_ASYNCINRES(TRAN_DATE,TRAN_TIME,APPL_CODE,KIND_CODE,TX_CODE,IP,SEND_YN) VALUES(  ";
		isql = isql + "\n '" + iDate + "', ";
		isql = isql + "\n '" + iTime + "', ";
		isql = isql + "\n '" + arrtmp[0] + "', ";
		isql = isql + "\n '" + arrtmp[2] + "', ";
		isql = isql + "\n '" + arrtmp[4] + "', ";
		isql = isql + "\n '" + pIP + "', ";
		isql = isql + "\n 'N') ";

		COMMDATA.GetDBManager().UpdateData(isql);
		COMMDATA.GetDBManager().ServerDBCommit();
    }
	
	public void CheckLicense()
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
	  	        	gLicenseGubun = arrTmp[1];
	  	        	
	  	        	arrTmp = arrData[5].split("\t");
	  	        	gLicenseManGi = arrTmp[1].replace("-", "");
	  	        	gLicenseManGi = gLicenseManGi.replace("/", "");
	  	        	
	  	        	SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
	  				String reg_dt = formatter.format(new java.util.Date()); //등록 일시
	  				
	  				if (Integer.parseInt(reg_dt) > Integer.parseInt(gLicenseManGi)) {
	  					COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", "Error:데모 라이센스가 만료되였습니다. 만기일:" + gLicenseManGi);
	  					System.exit(0);
	  				}
	  				return;
	  	        }
	  	        arrTmp = arrData[3].split("\t");
	  	        gLicenseHostName = arrTmp[1];
  
	  			try{
	  				String procname = ManagementFactory.getRuntimeMXBean().getName(); 
                    String hostname = procname.split("@")[1];
                    if (!gLicenseHostName.equals(hostname)) {
                    	COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", "Error:HostName을 확인하세요. 라이센스[" + gLicenseHostName + " Hostname[" + gLicenseHostName + "]");
	  					System.exit(0);
                    }
	  			}catch(Exception e){}
 
			}catch(IOException e){
				COMMDATA.GetTCheckerLog().WriteLog("E", "MgrManager", "Error:HostName을 확인하세요. 라이센스[" + gLicenseHostName + " Hostname[" + gLicenseHostName + "]");
				System.exit(0);
			}
	}
	public byte[] hexToByteArray(String hex)
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
	
    public void getSystemInfo()
    {
        try {
	        Properties properties = new Properties();
	        properties.load(new FileInputStream("./Properties/System.inf"));
	        String default_timeout = properties.getProperty("DEFAULT_TIMEOUT", "30");
	        AnylinkIP = properties.getProperty("ANYLINK_IP", "xxx.xxx.xxx.xxx");
	        AnylinkPort = properties.getProperty("ANYLINK_PORT", "xxxxx");
	 
	        
		} catch (Exception e) {
			
			return ;
		}
    }
    private String ReadFileData(String fname)
    {
        try{
    		File f = new File(fname);
    		if (f.exists()) {
            	DataInputStream dis = new DataInputStream(new FileInputStream(f));
                int len  = (int) f.length();
                byte[] ReadData = new byte[len];
                dis.readFully(ReadData);
                dis.close();
                
                return new String(ReadData);
    		}
        }catch(Exception e){}
 
		return "";
    }
    
    private String UserHeader(String pApplCode, String gGubun)
    {
    	String  fname = "";
    	String  RData = "";
    	
    	COMMDATA.GetTCheckerLog().WriteLog("D", "MgrManager", "UserHeader Start : " + pApplCode + ":" + gGubun);
    	
    	//Req 및 Res 헤더가 모두 동일한 경우
    	try{
        	fname = "./UserHeader/" + pApplCode + ".all"  ;
        	RData = ReadFileData(fname);
        	COMMDATA.GetTCheckerLog().WriteLog("D", "MgrManager", "UserHeader Start-all : " + pApplCode + ":" + gGubun + ":" + RData);
    	}catch (Exception e){}

    	if (RData.trim().equals("")) {
        	//Req 헤더
        	if (gGubun.equals("REQ")) {
            	try{
                	fname = "./UserHeader/" + pApplCode + ".req"  ;
                	RData = ReadFileData(fname);
                	COMMDATA.GetTCheckerLog().WriteLog("D", "MgrManager", "UserHeader Start-req : " + pApplCode + ":" + gGubun + ":" + RData);
            	}catch (Exception e){}
        	}
        	
        	//Res 헤더
        	if (gGubun.equals("RES")) {
            	try{
                	fname = "./UserHeader/" + pApplCode + ".res"  ;
                	RData = ReadFileData(fname);
                	COMMDATA.GetTCheckerLog().WriteLog("D", "MgrManager", "UserHeader Start-res : " + pApplCode + ":" + gGubun + ":" + RData);
            	}catch (Exception e){}
        	}
    	}

    	//주석처리된 항목 및 공백라인 제거
    	String retstr = "";
    	String[] arrtmp = RData.replace("\r\n","\n").split("\n");
    	for(int i=0;i < arrtmp.length ;i++)
    	{
    		if (arrtmp[i].trim().length() > 10) {
    			if (arrtmp[i].trim().indexOf("#") != 0 ) {
    				String[] arrsub = arrtmp[i].replace(",", "\t").split("\t");
    				
    				//한글명 + 영문영 + 타임 + 길이
    			    retstr = retstr + arrsub[0] + "\t" +  arrsub[1] + "\t" +  arrsub[2] + "\t" +  arrsub[3] + "\t" + "\t<NODATA>\n";
    			}
    		}
    	}
    	
    	COMMDATA.GetTCheckerLog().WriteLog("D", "MgrManager", "UserHeader Start : " + pApplCode + ":" + gGubun + ":" + retstr);
    	return retstr;
    }
    
	private String Proc_LoadUserHeader(String pRecvData)
	{
		COMMDATA.GetTCheckerLog().WriteLog("D", "MgrManager", "Load UserHeader Start : " + pRecvData);
		String RData = ReadFileData("./UserHeader/" + pRecvData);
		COMMDATA.GetTCheckerLog().WriteLog("D", "MgrManager", "Load UserHeader Result : " + RData);
		return RData;
	}
	private String Proc_SaveUserHeader(String pRecvData)
	{
		//확인완료
		try {
			String[] arrtmp = pRecvData.split("<DATAGUBUN>");
			DataOutputStream out = new DataOutputStream(new FileOutputStream(new File("./UserHeader/" + arrtmp[0])));
			out.write(arrtmp[1].getBytes());
			out.close();
			return "OK";
		}catch(Exception e) {
            return "ERROR";
		}
	}
 
	private String Proc_DBSearchInOutBoundTcpAppl(String pUserID)
	{
		String isql = "  select x.INOUT_FLAG, x.appl_code, a.appl_name                                     ";  
		isql = isql + "\n from alappl a, TCHECKER_TXDETAIL x                                              ";
		isql = isql + "\n where x.appl_code = a.appl_code                                                   ";     
		isql = isql + "\n   and a.sta_type = 1                                                                   ";
		isql = isql + "\n   and x.appl_code not in (select appl_code from alurl where sta_type = 1) ";
		
		if (!pUserID.trim().equals("NODATA")) {
			String userpermit = COMMDATA.GetDBManager().SearchData("select PERMIT from tchecker_user where userid = '" + pUserID + "' ");
			if (userpermit.indexOf("APPL") >= 0) {
				isql = isql + "\n   and x.appl_code in (select appl_code from  TCHECKER_PERMIT ";
				isql = isql + "\n                            where userid = '" + pUserID + "' ";
				isql = isql + "\n                               and kind_code = 'ALL' and tx_code = 'ALL' and PERMIT = 'Y') ";
		    }
		}
		
		isql = isql + "\n group by x.INOUT_FLAG,x.appl_code, a.appl_name                               ";
		isql = isql + "\n order by x.INOUT_FLAG,x.appl_code, a.appl_name                               ";
		
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
		if (retdata == null || retdata.equals("")) return "NOT-FOUND";
		return retdata;
	}	
	private String Proc_DBSearchInOutBoundUrlAppl(String pUserID)
	{
		String isql = "  select x.INOUT_FLAG, x.appl_code, a.appl_name                                     ";  
		isql = isql + "\n from alappl a, TCHECKER_TXDETAIL x                                              ";
		isql = isql + "\n where x.appl_code = a.appl_code                                                   ";     
		isql = isql + "\n   and a.sta_type = 1                                                                   ";
		isql = isql + "\n   and x.appl_code in (select appl_code from alurl where sta_type = 1) ";
		
		if (!pUserID.trim().equals("NODATA")) {
			String userpermit = COMMDATA.GetDBManager().SearchData("select PERMIT from tchecker_user where userid = '" + pUserID + "' ");
			if (userpermit.indexOf("APPL") >= 0) {
				isql = isql + "\n   and x.appl_code in (select appl_code from  TCHECKER_PERMIT ";
				isql = isql + "\n                            where userid = '" + pUserID + "' ";
				isql = isql + "\n                               and kind_code = 'ALL' and tx_code = 'ALL' and PERMIT = 'Y') ";
		    }
		}
		
		isql = isql + "\n group by x.INOUT_FLAG,x.appl_code, a.appl_name                               ";
		isql = isql + "\n order by x.INOUT_FLAG,x.appl_code, a.appl_name                               ";
		
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
		if (retdata == null || retdata.equals("")) return "NOT-FOUND";
		return retdata;
	}
	private String Proc_DBSearchKindTxList(String pRecvData)
	{
		String[] arrtmp = pRecvData.split("\t");
		String   pApplCode   = arrtmp[0];
		String   pInoutFlag    = arrtmp[1];
 
		String isql = " select t.rep_kind_code, k.name, t.tx_code, t.name  ";
		isql = isql + "\n from altx t, alkind k, TCHECKER_TXDETAIL x         ";
		isql = isql + "\n where t.appl_code = '" + pApplCode + "'   ";
		isql = isql + "\n   and x.INOUT_FLAG = '" + pInoutFlag + "'   ";
		isql = isql + "\n   and t.appl_code = k.appl_code                    ";
		isql = isql + "\n   and k.rep_kind_code = x.kind_code                    ";
		isql = isql + "\n   and t.rep_kind_code = k.rep_kind_code            ";
		isql = isql + "\n   and t.sta_type = 1                               ";
		isql = isql + "\n   and k.sta_type = 1                               ";
		isql = isql + "\n   and t.appl_code = x.appl_code                    ";
		isql = isql + "\n   and t.appl_code = x.appl_code                    ";
		isql = isql + "\n   and t.tx_code = x.tx_code                        ";
		
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
		if (retdata == null || retdata.equals("")) return "NOT-FOUND";

		return retdata;
	}
 
}
