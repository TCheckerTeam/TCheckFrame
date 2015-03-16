package TCheckServer.Engine;

import java.text.SimpleDateFormat;
import java.util.HashMap;

 
public class ThreadTcpMain extends Thread{ 
	private CommData COMMDATA = null;
	final HashMap<String, String> hashmapclient = new HashMap<String, String>();
	final HashMap<String, String> hashmapserver = new HashMap<String, String>();
	public ThreadTcpMain(CommData commdata)
    { 
    	COMMDATA = commdata;
    }
  
	public void run()
	{
		while(!Thread.currentThread().isInterrupted()) {
			try {

		          String lineinfo = Proc_DBGetTchecker_LineInfo();
		          if (!lineinfo.equals("NOT-FOUND")) {
		        	   	        	  
		        	  String[] arrlineinfo = lineinfo.split("\n");
		        	  for(int i=0;i < arrlineinfo.length ;i++){
		        		  String[] arrtmp = arrlineinfo[i].split("\t");
	
		        		  if (arrtmp[2].equals("1") || arrtmp[2].equals("10")) {
		        			  String ExecFlag = hashmapclient.get(arrtmp[1]);
		        			  if (ExecFlag == null) ExecFlag = "N";
		        			  if (!ExecFlag.equals("Y")){
		        				  CommData commdata = new CommData();
			        			  commdata.SetDBManager(COMMDATA.GetDBManager());
			        			  commdata.SetTCheckerLog(COMMDATA.GetTCheckerLog());
			        			  commdata.SetAPPL_CODE     (arrtmp[0]);
			        			  commdata.SetLU_NAME       (arrtmp[1]);
  
			        			  COMMDATA.GetTCheckerLog().WriteLog("I", "TcpManager", "SERVER:NONE:NONE:NONE:Execute Thread ThreadInTcp - PORTNO[" + arrtmp[1] + "]");
			        			  ThreadTcpClient threadtcpclient = new ThreadTcpClient(commdata);
			        			  threadtcpclient.start();
			        			  hashmapclient.put(arrtmp[1], "Y");
		        			  }
		        		  }
		        		  else {
		        			  //Listener ���

		        			  //ThreadInTcp�� Down�� �����̸�,���ο� Thread ����
		        			  String ExecFlag = hashmapserver.get(arrtmp[1]);
		        			  if (ExecFlag == null) ExecFlag = "N";
		        			  if (!ExecFlag.equals("Y"))
		        			  {
			        			  CommData commdata = new CommData();
			        			  commdata.SetDBManager(COMMDATA.GetDBManager());
			        			  commdata.SetTCheckerLog(COMMDATA.GetTCheckerLog());
			        			  commdata.SetAPPL_CODE     (arrtmp[0]);
			        			  commdata.SetLU_NAME       (arrtmp[1]);
 
			        			  COMMDATA.GetTCheckerLog().WriteLog("I", "TcpManager", "SERVER:NONE:NONE:NONE:Execute Thread ThreadTcpServerMain Start - PORTNO[" + arrtmp[1] + "]");
			        			  try{
			        				  ThreadTcpServerMain threadtcpserver = new ThreadTcpServerMain(commdata);
				        			  threadtcpserver.start();
				        			  hashmapserver.put(arrtmp[1], "Y");
			        			  }catch(Exception e10){COMMDATA.GetTCheckerLog().WriteLog("I", "TcpManager", e10);}
			        			  COMMDATA.GetTCheckerLog().WriteLog("I", "TcpManager", "SERVER:NONE:NONE:NONE:Execute Thread ThreadTcpServerMain End  - PORTNO[" + arrtmp[1] + "]");
		        		      }
		        		  }
		        	  }
		          }
		           
		          setThreadSleep(10000);
	  		}catch(Exception e){
	  			  COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", e);
	  			  setThreadSleep(10000);
	  		}

		}
	}
	private String Proc_DBGetTchecker_LineInfo()
	{
        //ȸ���������� ���ȸ������ ������ ȸ���� ���� ������ ������ ȸ�������� �о�´�.
		String isql = "[NOLOGGING] SELECT APPL_CODE , LU_NAME , CONNECT_TYPE, 'NO'   ";
        isql = isql + "\n FROM TCHECKER_LINEINFO              ";
        
        /* Inbound Async �ŷ��� ���� In/Out ��� �������� �Ʒ� 1�� Comment ó���� */
        // isql = isql + "\n WHERE LU_NAME not in(select lu_name from  alline where symbname in (select out_symbname from alline where out_symbname is not null))";
 
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
		if (retdata == null || retdata.equals("")) {
			COMMDATA.GetTCheckerLog().WriteLog("W", "TcpManager", "SERVER:NONE:NONE:NONE:Anylink�� ������ ȸ�������� �����ϴ�.");
			return "NOT-FOUND";
		}

		/* �������ڰ� �ƴ� ��� Inbound Async ��û ���� �ŷ� ���� */
		SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
		String iDate = formatter.format(new java.util.Date()); //��û����
		isql = "DELETE FROM TCHECKER_ASYNCINRES WHERE TRAN_DATE != '" + iDate + "' ";
		COMMDATA.GetDBManager().UpdateData(isql);
		COMMDATA.GetDBManager().ServerDBCommit();
		
		return retdata;
	}
 
 
    private void setThreadSleep(int time)
    {
   	    try{
            Thread.sleep(time);
        } catch(InterruptedException e) {}
    } 
 
}
 