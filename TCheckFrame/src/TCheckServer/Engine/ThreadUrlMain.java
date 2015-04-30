package TCheckServer.Engine;

import java.util.HashMap;

 
public class ThreadUrlMain extends Thread{ 
	private CommData COMMDATA = null;
 
	final HashMap<String, ThreadUrlClient> hashmapclient = new HashMap<String, ThreadUrlClient>();
	final HashMap<String, ThreadUrlServerMain> hashmapservermain = new HashMap<String, ThreadUrlServerMain>();
	public ThreadUrlMain(CommData commdata)
    { 
    	COMMDATA = commdata;
    }
   
	public void run()
	{
		while(!Thread.currentThread().isInterrupted()) {
			try {
				   
		          String lineinfo = Proc_DBGetInOutBoundURL();
		          if (!lineinfo.equals("NOT-FOUND")) {
		        	  String[] arrlineinfo = lineinfo.split("\n");
		        	  for(int i=0;i < arrlineinfo.length ;i++){
		        		  //a.appl_code, a.url, a.url_type, g.EB_CPAID, g.EB_FROM_PARTY, g.EB_TO_PARTY, g.EB_SVCNAME, g.REQ_ENCODING, g.RES_ENCODING
		        		  if (arrlineinfo[i].trim().equals("")) break;
		        		  String[] arrtmp = arrlineinfo[i].split("\t");
		        		  
		        		  String pPortNo = "80";
			        	  String[] arrUrl    = arrtmp[1].replace("http:","").split(":");
			  			  if (arrUrl.length == 2) {
			  				  String[] arrtmpsub = arrUrl[1].replace("/", "@").split("@");
			  				  pPortNo = arrtmpsub[0];
			  			  }
 
			  			  
			  			  if (arrtmp[2].equals("1")) {
			  				  //URL_TYPE=1 �̸�, Inbound �ŷ�
			  				  
			  				  ThreadUrlClient threadurlclient = hashmapclient.get(pPortNo);
			        		  
		        			  //ThreadInSoap�� Down�� �����̸�,���ο� Thread ����
			        		  if (threadurlclient == null || !threadurlclient.isAlive()){
	 
			        			  CommData commdata = new CommData();
			        			  commdata.SetDBManager(COMMDATA.GetDBManager());
			        			  commdata.SetTCheckerLog(COMMDATA.GetTCheckerLog());
			        			  commdata.SetLU_NAME(pPortNo);
			        			  commdata.SetURL_LIST(arrlineinfo);
			        			  commdata.SetAPPL_CODE(arrtmp[0]);
			        			  commdata.SetEBXML_GUBUN (false);
			        			  if (arrtmp.length > 3) commdata.SetEBXML_GUBUN (true);
			        			  
	                              COMMDATA.GetTCheckerLog().WriteLog("I", "UrlManager", "SERVER:NONE:NONE:Execute ThreadUrlClient - PORTNO[" + pPortNo + "]");
	                              threadurlclient = new ThreadUrlClient(commdata);
	                              threadurlclient.start();
			        			  
			        			  hashmapclient.put(pPortNo, threadurlclient);
			        			  threadurlclient = hashmapclient.get(pPortNo);
			        			  
		        		      }
			  			  }
			  			  else {
			  				  //URL_TYPE=2 �̸�, Outbound �ŷ�
			  				  ThreadUrlServerMain threadurlservermain = hashmapservermain.get(pPortNo);
			        		  
		        			  //ThreadInSoap�� Down�� �����̸�,���ο� Thread ����
			        		  if (threadurlservermain == null || !threadurlservermain.isAlive()){
		 
			        			  CommData commdata = new CommData();
			        			  commdata.SetDBManager(COMMDATA.GetDBManager());
			        			  commdata.SetTCheckerLog(COMMDATA.GetTCheckerLog());
			        			  commdata.SetLU_NAME(pPortNo);
			        			  commdata.SetURL_LIST(arrlineinfo);
			        			  commdata.SetAPPL_CODE(arrtmp[0]);
			        			  commdata.SetEBXML_GUBUN (false);
			        			  if (arrtmp.length > 3) commdata.SetEBXML_GUBUN (true);
			        	 
	                              COMMDATA.GetTCheckerLog().WriteLog("I", "UrlManager", "SERVER:NONE:NONE:Execute ThreadUrlServerMain - PORTNO[" + pPortNo + "]");
	                              threadurlservermain = new ThreadUrlServerMain(commdata);
	                              threadurlservermain.start();
			        			  
	                              hashmapservermain.put(pPortNo, threadurlservermain);
	                              threadurlservermain = hashmapservermain.get(pPortNo);
		        		      }
			  			  }
			  			  
		        	  }
		          }
		           
		          setThreadSleep(30000);
	  		}catch(Exception e){}

		}
	}
	private String Proc_DBGetInOutBoundURL()
	{
        //ȸ���������� ���ȸ������ ������ ȸ���� ���� ������ ������ ȸ�������� �о�´�.
		/* ���ֺ� DB2 ���� ǥ�� SQL�� ����
		String isql = " SELECT a.appl_code, a.url, a.URL_TYPE, g.EB_CPAID, g.EB_FROM_PARTY, g.EB_TO_PARTY, g.EB_SVCNAME, g.REQ_ENCODING, g.RES_ENCODING";
		isql = isql + "\n FROM alurl a right outer join algrp g ";
        isql = isql + "\n      on  a.appl_code = g.appl_code ";
        isql = isql + "\n Where a.sta_type = 1  ";
        isql = isql + "\n   and g.sta_type = 1  ";
 
        */
		
		String isql = "    SELECT a.appl_code, a.url, a.URL_TYPE, g.EB_CPAID, g.EB_FROM_PARTY, g.EB_TO_PARTY, g.EB_SVCNAME, g.REQ_ENCODING, g.RES_ENCODING ";
		isql = isql + "\n  FROM alurl a LEFT OUTER JOIN algrp g ";
		isql = isql + "\n  ON a.appl_code = g.appl_code ";
		isql = isql + "\n  Where a.sta_type = 1 ";  
		isql = isql + "\n    and g.sta_type = 1 "; 		
  
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
		if (retdata == null || retdata.equals("")) {
			COMMDATA.GetTCheckerLog().WriteLog("W", "UrlManager", "SERVER:NONE:NONE:Anylink�� ������ ȸ�������� �����ϴ�.");
			return "NOT-FOUND";
		}

		return retdata;
	}
 
 
    private void setThreadSleep(int time)
    {
   	    try{
            Thread.sleep(time);
        } catch(InterruptedException e) {}
    } 
}
 