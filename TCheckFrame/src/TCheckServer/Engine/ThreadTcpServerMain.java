package TCheckServer.Engine;

import java.net.ServerSocket;
import java.net.Socket;
 
public class ThreadTcpServerMain extends Thread{ 
	private CommData         COMMDATA  = null;
    private ServerSocket     server    = null;
 
	public ThreadTcpServerMain(CommData commdata)
    { 
 
    	COMMDATA = commdata;
    
    }
    public void run()
    {
		/*----------------- 회선 Listener --------------------------------*/
		while(!Thread.currentThread().isInterrupted()) {
			/*--------------------- Port Listener 실행 ---------------------------*/
	    	try {
		    	  if (server == null) server = new ServerSocket(Integer.parseInt(COMMDATA.GetLU_NAME()));
  	        	  Socket client = server.accept();
  	        	
  	        	  
		          String   lineinfo = Proc_DBGetTchecker_LineInfo();
  	        	  String[] arrtmp = lineinfo.split("\t");
 
  	        	  COMMDATA.SetAPPL_CODE     (arrtmp[0]);
  	        	  COMMDATA.SetLU_NAME       (arrtmp[1]);
  	        	  COMMDATA.SetCONNECT_TYPE  (arrtmp[2]);
  	        	  COMMDATA.SetSTA_TYPE      (arrtmp[3]);
  	              COMMDATA.SetCOMM_HEAD_TYPE(arrtmp[4]);
  	        	  COMMDATA.SetCOMM_HEAD_SIZE(arrtmp[5]);
  	        	  COMMDATA.SetLEN_TYPE      (arrtmp[6]);
  	        	  COMMDATA.SetLEN_OFFST     (arrtmp[7]);
  	        	  COMMDATA.SetLEN_SIZE      (arrtmp[8]);

  	        	  String   outlineinfo = Proc_DBGetOut_LineInfo();
    			  Proc_SetOut_LineInfo(COMMDATA, outlineinfo, arrtmp[1]);
				  ThreadTcpServer tmpthread = new ThreadTcpServer(COMMDATA, client);
				  tmpthread.start();
  	    			 
  			}catch(Exception e) {
  				setThreadSleep(1000);
  			}
		}
	}
    private void setThreadSleep(int time)
    {
    	try{
             Thread.sleep(time);
        } catch(InterruptedException e) {}
    }
	private String Proc_DBGetTchecker_LineInfo()
	{
		/*
        //회선정보에서 출력회선으로 설정된 회선에 대한 정보를 제외한 회선정보를 읽어온다.
		String isql = "[NOLOGGING] SELECT APPL_CODE , LU_NAME , CONNECT_TYPE , STA_TYPE, COMM_HEAD_TYPE , COMM_HEAD_SIZE , ";
		isql = isql + "        LEN_TYPE  , LEN_OFFST , LEN_SIZE , 'NO' ";
        isql = isql + "\n FROM TCHECKER_LINEINFO              ";
        isql = isql + "\n WHERE LU_NAME not in(select lu_name from  alline where symbname in (select out_symbname from alline where out_symbname is not null))";
        isql = isql + "\n   AND LU_NAME   = '" + COMMDATA.GetLU_NAME() + "' ";
        isql = isql + "\n   AND APPL_CODE = '" + COMMDATA.GetAPPL_CODE() + "' ";
        */
		
        //회선정보에서 출력회선으로 설정된 회선에 대한 정보를 제외한 회선정보를 읽어온다.
		String isql = "[NOLOGGING] SELECT APPL_CODE , LU_NAME , CONNECT_TYPE , STA_TYPE, COMM_HEAD_TYPE , COMM_HEAD_SIZE , ";
		isql = isql + "        LEN_TYPE  , LEN_OFFST , LEN_SIZE , 'NO' ";
        isql = isql + "\n FROM TCHECKER_LINEINFO              ";
        isql = isql + "\n WHERE LU_NAME   = '" + COMMDATA.GetLU_NAME() + "' ";
        isql = isql + "\n   AND APPL_CODE = '" + COMMDATA.GetAPPL_CODE() + "' ";
        
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
		if (retdata == null || retdata.equals("")) {
			COMMDATA.GetTCheckerLog().WriteLog("W", "TcpManager", "SERVER:NONE:NONE:NONE:Anylink로 연결할 회선정보가 없습니다.");
			return "NOT-FOUND";
		}

		return retdata;
	}
	private String Proc_DBGetOut_LineInfo()
	{
		//회선정보에서 출력회선으로 설정된 회선에 대한 정보를 읽어온다.
        String isql = "";
		isql = isql + "[NOLOGGING]   select a.lu_name sendport, b.lu_name recvport, b.connect_type recv_connect_type, 'NO' "; 
		isql = isql + "\n from alline a, (                                                                       ";
		isql = isql + "\n     select symbname,lu_name,connect_type                                               ";
		isql = isql + "\n     from  alline                                                                       ";
		isql = isql + "\n     where symbname in (select out_symbname from alline where out_symbname is not null) ";
		isql = isql + "\n ) b                                                                                    ";
		isql = isql + "\n where a.out_symbname = b.symbname                                                      ";
        isql = isql + "\n   AND a.LU_NAME   = '" + COMMDATA.GetLU_NAME() + "' ";
        isql = isql + "\n   AND a.connect_type = " + COMMDATA.GetCONNECT_TYPE() + "  ";
 
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
		if (retdata == null || retdata.equals("")) {
			return "";
		}

		return retdata;
	}
	private void Proc_SetOut_LineInfo(CommData commdata, String outlineinfo, String sendport)
	{
		String[] arrtmp = outlineinfo.split("\n");
		for(int i=0;i < arrtmp.length ;i++){
			if (arrtmp[i].trim().equals("")) break;
			
			String[] arrtmpsub = arrtmp[i].split("\t");
			if (arrtmpsub[0].equals(sendport)) {
				commdata.SetRESP_LU_NAME(arrtmpsub[1]);
				commdata.SetRESP_CONNECT_TYPE(arrtmpsub[2]);
				return;
			}
		}
		commdata.SetRESP_LU_NAME("");
		commdata.SetRESP_CONNECT_TYPE("");
	}
}
