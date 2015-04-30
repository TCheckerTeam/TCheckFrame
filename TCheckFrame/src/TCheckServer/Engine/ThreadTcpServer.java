package TCheckServer.Engine;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import TCheckServer.UserClass.UserChkLen;
import TCheckServer.UserClass.UserFunction;
import TCheckServer.UserClass.UserShare;
import TCheckServer.Util.TCheckerMapping;
 
public class ThreadTcpServer extends Thread{ 
	private HashMap<String, SendInfo> hashsender = new HashMap<String, SendInfo>();
	private CommData         COMMDATA  = null;
 	private UserShare        usergongu    = null;
	private UserFunction     userfunction = null;
	private UserChkLen       userlength   = null;
	private String           recvdata  = "";
	private String           AnylinkIP = "";
	private int              Default_TimeOut = 10;
	private Socket           client    = new Socket(), clientResp= new Socket(), clientWork= null;
    private DataOutputStream dos       = null, dosResp = null, dosWork = null;
    private DataInputStream  dis       = null, disResp = null, disWork = null;	
    private String           UserID    = "", UserPCIP  = "", ApplCode  = "", KindCode  = "", TxCode    = "";
    private int              UserTcpResPort = 60004;
    private int              RecvCnt   = 0;
    private byte[]           RecvData  = new byte[10000];
    private ServerSocket     server    = null;
    private boolean          EOFChannel= false;
    private String[]         ApplInternalInfo = null;
    private long             LineStatus = 0;
    private String[]         ApplSharedInfo = null;
 
	public ThreadTcpServer(CommData commdata, Socket pSocket)
    { 

    	COMMDATA = commdata;
    	clientResp = pSocket;
    	getSystemInfo();
    	GetApplInternalInfo();
    	GetSharedInfo();
    	setLastStatus();
    	
    	usergongu    = new UserShare(COMMDATA);
		userfunction = new UserFunction();
		userlength   = new UserChkLen();
 
		String errmsg = "SERVER:" + COMMDATA.GetAPPL_CODE()+ ":NONE:NONE:ȸ���� �����մϴ�.[" + COMMDATA.GetLU_NAME() + "]";
		COMMDATA.GetTCheckerLog().WriteLog("I", "TcpManager", errmsg);
    }
    public long getStatus()
    {
    	return LineStatus;
    }
    public void run()
    {
 
		/*----------------- ȸ�� Listener --------------------------------*/
		while(!Thread.currentThread().isInterrupted()) {
	
			/*--------------------- Port Listener ���� ---------------------------*/
	    	try {
				dosResp = new DataOutputStream(clientResp.getOutputStream());
	  			disResp = new DataInputStream(clientResp.getInputStream());
	  			
	  			//Client(MegaBox EOR) �� ��쿡, HandShaking Flow
	  			if (COMMDATA.GetCONNECT_TYPE().equals("7")){
	   			   byte[] AnyToSimul = new byte[] {(byte)0xFF, (byte)0xFD, (byte)0x19};
	        	   byte[] SimulToAny = new byte[] {(byte)0xFF, (byte)0xFB, (byte)0x00,(byte)0xFF,(byte)0xDF,(byte)0x00,
	        			                           (byte)0xFF, (byte)0xFB, (byte)0x19,(byte)0xFF,(byte)0xFD,(byte)0x19};
	 
	        	   dosResp.write(SimulToAny, 0, SimulToAny.length);
	        	   dosResp.flush();
		 
		   		   byte[] tmpbyte = new byte[3];
		   		   for(int i=0;i < tmpbyte.length ;i++) tmpbyte[i] = disResp.readByte();
	  			}
  
	    	}catch(Exception e) {
	    		String errmsg = "SERVER:" + COMMDATA.GetAPPL_CODE()+ ":NONE:NONE:ȸ���� �����ϴµ� �����Ͽ����ϴ�.[" + COMMDATA.GetLU_NAME() + "]";
	    		COMMDATA.GetTCheckerLog().WriteLog("I", "TcpManager", errmsg);
	    		setThreadSleep(1000);
	    	}
 
	    	/*------------------------------ �������� --------------------------------*/
	    	
	    	//�۽��ϱ� ���� Socket ����
	    	client = clientResp;
	    	dis    = disResp;
	    	dos    = dosResp;
 			
	    	//�����ϱ� ���� Socket ����
 			clientWork = clientResp;
 			disWork = disResp;
 			dosWork = dosResp;

 			while(!Thread.currentThread().isInterrupted()) {
 				setThreadSleep(200);
 		        if (SendReceiveMsg() != true) {
 		        	CommonSocketclose();
 		        	return;
 		        }
 			}	
 			
		}
		
	}
 
 
	private byte[] GetRecvTcpData()
	{
		byte[] HEAD = null;
		byte[] READDATA = null;
		int    MSGSIZE  = 0;
		String MSGDATA  = "";
    	int    tmplenval  = 0;
		byte[] tmplenbyte = null;
		
    	try {
    		EOFChannel = false;
    		
    		clientWork.setSoTimeout(1000);
    		
    		
            if (COMMDATA.GetCOMM_HEAD_TYPE().equals("0") ){
            	
             
            	//Head Buffer ���� �� �б�
            	HEAD = new byte[Integer.parseInt(COMMDATA.GetCOMM_HEAD_SIZE())];
            	for(int i=0;i < HEAD.length ;i++)  HEAD[i] = disWork.readByte();
 
            	for(int i=0;i < HEAD.length ;i++)  {
            		if (HEAD[i] == (byte)0) HEAD[i] = (byte)32;
            	}
 
            	//Body Buffer ���� �� �ϱ�
            	if (COMMDATA.GetLEN_TYPE().equals("10")){
 
            		tmplenbyte = new byte[Integer.parseInt(COMMDATA.GetLEN_SIZE())];
            		System.arraycopy(HEAD,Integer.parseInt(COMMDATA.GetLEN_OFFST()), tmplenbyte, 0, tmplenbyte.length);
            		int tmplen = Integer.parseInt(new String(tmplenbyte));
 
            		READDATA = new byte[tmplen];
            		for(int i=0;i < tmplen ;i++) {
            			READDATA[i] = disWork.readByte();
            		}
             	}
            	else if (COMMDATA.GetLEN_TYPE().equals("50")){
            		//�����ʵ� ������ ���̰�(Integer) : ��������� ��쿡 �޼����� ��üũ�⿡�� ���� Offset �� ���� Size �� �� ������� ���������� �����Ѵ�.
            		int tmplen = HEAD[Integer.parseInt(COMMDATA.GetLEN_OFFST()) + 0] * 256 + HEAD[Integer.parseInt(COMMDATA.GetLEN_OFFST()) + 1];
            		READDATA = new byte[tmplen];
            		for(int i=0;i < tmplen ;i++) READDATA[i] = disWork.readByte();
            	}
            	else if (COMMDATA.GetLEN_TYPE().equals("99")){
            		COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "SERVER:" + COMMDATA.GetAPPL_CODE()+ ":NONE:NONE:AAAAA");
            		int readlen = userlength.GetRecvLength(COMMDATA, COMMDATA.GetLU_NAME(), COMMDATA.GetAPPL_CODE(), HEAD);
            		COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "SERVER:" + COMMDATA.GetAPPL_CODE()+ ":NONE:NONE:BBBBB");
            			 
            		if (readlen > 0){
            			READDATA = new byte[readlen];
            			for(int i=0;i < readlen ;i++) READDATA[i] = disWork.readByte();
            		}
            	}
            	else {
            		String errmsg = "SERVER:" + COMMDATA.GetAPPL_CODE()+ ":NONE:NONE:������ �� ��쿡 ���̼�������� [10:�����ʵ� ������ ���̰�] [50:�����ʵ� ������ ���̰�(Integer)] �̾�� �մϴ�.";
            		COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", errmsg);
            		return null;
            	}
            }
            if (COMMDATA.GetCOMM_HEAD_TYPE().equals("1")){
            	//Head Buffer ���� �� �б�
            	HEAD = new byte[Integer.parseInt(COMMDATA.GetCOMM_HEAD_SIZE())];
            	for(int i=0;i < HEAD.length ;i++) HEAD[i] = disWork.readByte();

            	for(int i=0;i < HEAD.length ;i++)  {
            		if (HEAD[i] == (byte)0) HEAD[i] = (byte)32;
            	}
            	
            	//Head���� ���������� �����Ѵ�.
        		if (COMMDATA.GetLEN_TYPE().equals("10") || COMMDATA.GetLEN_TYPE().equals("11") || COMMDATA.GetLEN_TYPE().equals("12")){
            		tmplenbyte = new byte[Integer.parseInt(COMMDATA.GetLEN_SIZE())];
            		System.arraycopy(HEAD,Integer.parseInt(COMMDATA.GetLEN_OFFST()), tmplenbyte, 0, tmplenbyte.length);
            		tmplenval = Integer.parseInt(new String(tmplenbyte));
        		}
        		else {
        			tmplenval = HEAD[Integer.parseInt(COMMDATA.GetLEN_OFFST()) + 0] * 256 + HEAD[Integer.parseInt(COMMDATA.GetLEN_OFFST()) + 1];
        		}

            	//Body Buffer ���� �� �ϱ�
            	if (COMMDATA.GetLEN_TYPE().equals("10") || COMMDATA.GetLEN_TYPE().equals("50")){
            		//���������� �����ʵ� ������ ���̰�  : ��������� ��쿡 �޼����� ��üũ�⿡�� ���� Offset �� ���� Size �� �� ������� ���������� �����Ѵ�. 
            		//�����ʵ� ������ ���̰�(Integer) : ��������� ��쿡 �޼����� ��üũ�⿡�� ���� Offset �� ���� Size �� �� ������� ���������� �����Ѵ�.
            	    int tmplen = tmplenval + Integer.parseInt(COMMDATA.GetLEN_OFFST()) + Integer.parseInt(COMMDATA.GetLEN_SIZE());
            	    READDATA = new byte[tmplen];
            	    System.arraycopy(HEAD,0, READDATA, 0, HEAD.length);
            		for(int i=HEAD.length;i < READDATA.length ;i++) READDATA[i] = disWork.readByte();
            	}
            	else if (COMMDATA.GetLEN_TYPE().equals("11") || COMMDATA.GetLEN_TYPE().equals("51")){
            		//�����ʵ带 ������ ���̰�                    : ��������� ��쿡 �޼����� ��üũ�⸦ �����Ѵ�.
            		//�����ʵ带 ������ ���̰�(Integer) : ��������� ��쿡 �޼����� ��üũ�⸦ ���������� �����Ѵ�.
            	    int tmplen = tmplenval;
            	    READDATA = new byte[tmplen];
            	    System.arraycopy(HEAD,0, READDATA, 0, HEAD.length);
            		for(int i=HEAD.length;i < READDATA.length ;i++) READDATA[i] = disWork.readByte();
            		
            	}
            	else if (COMMDATA.GetLEN_TYPE().equals("12") || COMMDATA.GetLEN_TYPE().equals("52")){
            		//Body�� ���̰�                    : ��������� ��쿡 �޼����� ��üũ�⿡�� Head Size �� ������� ���������� �����Ѵ�.
            		//Body�� ���̰�(Integer) : ��������� ��쿡 �޼����� ��üũ�⿡�� Head Size �� ������� ���������� �����Ѵ�.
            	    int tmplen = tmplenval;
            	    READDATA = new byte[tmplen + HEAD.length];
            	    System.arraycopy(HEAD,0, READDATA, 0, HEAD.length);
            		for(int i=HEAD.length;i < READDATA.length  ;i++) READDATA[i] = disWork.readByte();
            	}
            	else if (COMMDATA.GetLEN_TYPE().equals("99")){
            		int readlen = userlength.GetRecvLength(COMMDATA, COMMDATA.GetLU_NAME(), COMMDATA.GetAPPL_CODE(), HEAD);
            		if (readlen > 0){
            			READDATA = new byte[readlen + HEAD.length];
            			System.arraycopy(HEAD,0, READDATA, 0, HEAD.length);
            			for(int i=0;i < readlen ;i++) READDATA[i + HEAD.length] = disWork.readByte();
            		}
            	}
            	else {
            		String errmsg = "SERVER:" + COMMDATA.GetAPPL_CODE()+ ":NONE:NONE:������� �� ��쿡 ���̼�������� [13:��������] [14:END����]�� ����� �� �����ϴ�.";
            		COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", errmsg);
            		return null;
            	}
            }
            if (COMMDATA.GetCOMM_HEAD_TYPE().equals("2")){
            	
            	//�������� �������� ����� ��� �������� ��ŭ�� ���۸� �Ҵ��Ͽ�, �޼����� �����Ѵ�.
        	    int tmplen = Integer.parseInt(COMMDATA.GetLEN_SIZE());
        	    READDATA = new byte[tmplen];
        		for(int i=0;i < READDATA.length ;i++) READDATA[i] = disWork.readByte();
        		
            }
            if (COMMDATA.GetCOMM_HEAD_TYPE().equals("3")){
            	//End���� ����� ��쿡 �޼����� �״�� �����Ѵ�.
            	
            	RecvCnt = 0;
            	try {
            		RecvData = new byte[50000];
	            	for(int i=0;i < RecvData.length ;i++) {
	            		RecvData[i] = disWork.readByte();
	            		RecvCnt++;
	            	}
            	}catch(EOFException e) {
            		String errmsg = "SERVER:" + COMMDATA.GetAPPL_CODE()+ ":NONE:NONE:Anylink���� ȸ���� �����߽��ϴ�.[" + COMMDATA.GetLU_NAME() + "]";
            		COMMDATA.GetTCheckerLog().WriteLog("W", "TcpManager", errmsg);
            		EOFChannel = true;
            		return null;
            	}catch(Exception e) {}
            	
            	if (RecvCnt < 10) return null;
            	
        		//���ŵ���Ÿ Parsing
        		READDATA = new byte[RecvCnt];
        	    System.arraycopy(RecvData,0, READDATA, 0, READDATA.length);
        	    getCurrentStatus();       	 
            } 
    	}catch(EOFException e) {
    		String errmsg = "SERVER:" + COMMDATA.GetAPPL_CODE()+ ":NONE:NONE:Anylink���� ȸ���� �����߽��ϴ�.[" + COMMDATA.GetLU_NAME() + "]";
    		COMMDATA.GetTCheckerLog().WriteLog("W", "TcpManager", errmsg);
    		EOFChannel = true;
    		return null;
    	}catch(Exception e) {
    		return null;
    	}
		return READDATA;
	}

	private boolean SendReceiveMsg()
	{
		byte[]   EOD = new byte[] {(byte)0xFF,(byte)0xEF};
		boolean  pSendFlag = false;
		boolean  isContinueFlag = false;
		
		//�����Ƿ��� ����Ÿ�� �����ͼ�, �Ѱ��� ������ Anylink�� �����Ѵ�.
		File[] filelist = new File("./Request/Tcpmsg/" + COMMDATA.GetLU_NAME()).listFiles();
 
		if (filelist != null && filelist.length > 0) {
			/*-------- �۽������� �����Ѵ�. ------------*/
			byte[] makeMsg = MakeSendMsg(filelist[0].getPath());
			if (makeMsg.length > 10){
				String tmpstr = new String(makeMsg);
				if (tmpstr.substring(0,6).equals("ERROR:")) {
					ErrorMsgSend(this.UserID, this.ApplCode, this.KindCode, this.TxCode, makeMsg);
				}
				else {
					 
					if (isResponseMsg(this.UserID, this.ApplCode, this.KindCode, this.TxCode)) {
						SendInfo sendinfo = new SendInfo();
						sendinfo.setUserID(this.UserID);
						sendinfo.setUserIP(this.UserPCIP);
						sendinfo.setApplCode(this.ApplCode);
						sendinfo.setKindCode(this.KindCode);
						sendinfo.setTxCode(this.TxCode);
						
		    			SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
		    			String reg_dt1 = formatter.format(new java.util.Date()); //��� �Ͻ�
		    			formatter = new java.text.SimpleDateFormat("HHmmss", java.util.Locale.KOREA);
		    			String reg_tm1 = formatter.format(new java.util.Date());  //��Ͻð� 
		    			formatter = new java.text.SimpleDateFormat("S", java.util.Locale.KOREA);
		    			String reg_milsec1 = formatter.format(new java.util.Date());  //��Ͻð� 
		    			
		    			int hh1 = Integer.parseInt(reg_tm1.substring(0,2));
		    			int mm1 = Integer.parseInt(reg_tm1.substring(2,4));
		    			int ss1 = Integer.parseInt(reg_tm1.substring(4,6));
		    			int ms1 = Integer.parseInt(reg_milsec1);
		    			int time1 = hh1 * 3600 + mm1 * 60 + ss1;
		    			sendinfo.setSendTime(time1);
		    			hashsender.put(sendinfo.getApplCode() + "\t" + sendinfo.getKindCode() + "\t" + sendinfo.getTxCode(), sendinfo);
		    			 
					}
					else {
						/* Async Inbound �ŷ��̰�, ����ŷ����ο� ��ϵǿ� �ִ� ��� ������ �ޱ� ���ؼ� ��û �ŷ��� ���� ���´�. */
						/* ��û ����/����/�ŷ�/User PC IP */
						COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "Async Inbound ���� �ŷ� :" + this.UserID + ":" + this.ApplCode + ":" + this.KindCode + ":" + this.TxCode + ":" + this.UserPCIP + ":" + this.COMMDATA.GetLU_NAME());
						Proc_InboundAsyncRes(this.ApplCode, this.KindCode, this.TxCode, this.UserPCIP);
								
					}
		    		/*----------- �������۽� ������ �߻��ϸ�, ������ Thread�� �����Ѵ�. -----------*/
				 	try {
				 		COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", this.UserID + ":" + this.ApplCode + ":" + this.KindCode + ":" + this.TxCode + ":Anylink�� ���� : \n  [" + tmpstr + "]");
				 		dos.write(makeMsg, 0, makeMsg.length);
				 		if (COMMDATA.GetCONNECT_TYPE().equals("7")){
				 			dos.write(EOD, 0, EOD.length);
				 			COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", this.UserID + ":" + this.ApplCode + ":" + this.KindCode + ":" + this.TxCode + "EOD ���� : 0xFF 0xEF ");
				 		}
				 		dos.flush();
				 		pSendFlag = true;
				 		setLastStatus();
				 	}catch(EOFException eofe){
				 		String errmsg = this.UserID + ":" + this.ApplCode + ":" + this.KindCode + ":" + this.TxCode + ":Anylink���� ȸ���� Down �Ͽ����ϴ�.[" + COMMDATA.GetLU_NAME() + "]" ;
			    		COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", errmsg);
			    		
			    		errmsg = "ERROR:Anylink���� ȸ���� Down �Ͽ����ϴ�.[" + COMMDATA.GetLU_NAME() + "]" ;
			    		ErrorMsgSend(this.UserID, this.ApplCode, this.KindCode, this.TxCode, errmsg.getBytes());
			    		return false;
				 	}catch(Exception e) {
				 		String errmsg = this.UserID + ":" + this.ApplCode + ":" + this.KindCode + ":" + this.TxCode + ":������ �����ϴµ� �����Ͽ����ϴ�.";
			    		COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", errmsg);
			    		COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", e);
			    		
			    		errmsg = "ERROR:������ �����ϴµ� �����Ͽ����ϴ�.[" + this.UserID + ":" + this.ApplCode + ":" + this.KindCode + ":" + this.TxCode + "]";
			    		ErrorMsgSend(this.UserID, this.ApplCode, this.KindCode, this.TxCode,errmsg.getBytes());
			    		return false;
				 	}
				}
			}
		}
   
	 	/*----------- ������ �����Ѵ�. ----------------*/
 		if (!COMMDATA.GetRESP_CONNECT_TYPE().equals("")) {
 			clientWork = clientResp;
 			disWork = disResp;
 			dosWork = dosResp;
 		}
 		else {
 			clientWork = client;
 			disWork = dis;
 			dosWork = dos;
 		}

        /*----------- ��û�� ���� ���������� �������� �ʴ� �ŷ��� �׳� SKIP �Ѵ�. -----------------*/
 	    // ������ Timeout ��ŭ Loop �� ���鼭, ���������� ���������̸� Loop �� �����, 
 		// Anylink������ ��û�����̸�, ���������� �����Ͽ� Anylink�� �����Ѵ�.

 		RecvData = GetRecvTcpData();
        if (EOFChannel == true) {
        	CommonSocketclose();
	 		return false;
        }
    	isContinueFlag = false;
	    if (RecvData == null) isContinueFlag = true;
	    else if (RecvData.length <= 30) isContinueFlag = true;
	    	
	    if (isContinueFlag){
            Set set = hashsender.keySet(); 
            for (Iterator iterator = set.iterator(); iterator.hasNext();) 
            { 
            	String findkey = (String) iterator.next(); 
            	SendInfo sendinfo = hashsender.get(findkey);
            
            	SimpleDateFormat formatter = new SimpleDateFormat("HHmmss", java.util.Locale.KOREA);
   		        String reg_tm2 = formatter.format(new java.util.Date());  //��Ͻð� 
   		        formatter = new java.text.SimpleDateFormat("S", java.util.Locale.KOREA);
   		        String reg_milsec2 = formatter.format(new java.util.Date());  //��Ͻð� 
   		
   			    int hh2 = Integer.parseInt(reg_tm2.substring(0,2));
   			    int mm2 = Integer.parseInt(reg_tm2.substring(2,4));
   		        int ss2 = Integer.parseInt(reg_tm2.substring(4,6));
   	            
   			    long exectime = (hh2 * 3600 + mm2 * 60 + ss2) - sendinfo.getSendTime();
   			    
   			    int TimeOutValue = getTimeOutInfo(sendinfo.getUserID(), sendinfo.getApplCode(), sendinfo.getKindCode(), sendinfo.getTxCode());
                if (TimeOutValue < exectime) {
                	hashsender.remove(findkey);
                	byte[] tmpbyte = "ERROR:�ŷ� Ÿ�Ӿƿ��� �߻��Ͽ����ϴ�.".getBytes();
         			SendResponseToUserPC(sendinfo.getUserID(), sendinfo.getUserIP(), sendinfo.getApplCode(), sendinfo.getKindCode(), sendinfo.getTxCode(),tmpbyte, tmpbyte.length );
       		        break;
                }
	         }
	    }
	    else {
	    	setLastStatus();
	    	
	    	boolean DirectionFlag = false;
	 		String tmpApplCode  = "";
	 		String tmpKindCode  = "";
	 		String tmpTxCode    = "";
	 		String tmpReqRes    = "";
	 		String tmpInOut_Flag= "";

    		COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "Recv From Anylink:\n[" + new String(RecvData) + "]");
    		
			//���ŵ���Ÿ���� Default �����ڵ忡 ���� ���� �� �ŷ��ڵ带 �����Ѵ�.
    		//Retrun : appl_code, kind_code, tx_code, INOUT_FLAG, MAP_FLAG
			String retstr = GetApplInternalKindTx(RecvData);
			if (retstr.equals("")) {
				COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "����/����/�ŷ��ڵ带 �Ǵ� �� �� ���� ����Ÿ�� �����Ͽ����ϴ�.");
				return true;
			}
			
			String[] worktmp = retstr.split("\t");
			tmpApplCode   = worktmp[0].trim();
			tmpKindCode   = worktmp[1].trim();
			tmpTxCode     = worktmp[2].trim();
			tmpInOut_Flag = worktmp[3];
		 		
		 	COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "Share Info:" + tmpApplCode + ":" + tmpKindCode + ":" + tmpTxCode + ":" + tmpInOut_Flag);
 
	 		//���������� ��û�� ���� ������������ �ƴϸ�, Anylink ���� ��û�� �ŷ����� �Ǵ��Ѵ�.
		 	if (isResponseMsg(this.UserID, tmpApplCode, tmpKindCode, tmpTxCode)) {
		 		/* Sync �ŷ��� ��� */
		 		if (tmpInOut_Flag.equals("O") || tmpTxCode.equals("")){
	 				/* Anylink���� ��û�� ������ ���Ͽ� ���������� �����ϴ� ��� */
		 			COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "SendResponseToAnylink:" + tmpApplCode + ":" + tmpKindCode + ":" + tmpTxCode + ":" + tmpInOut_Flag + ":" + "");
					SendResponseToAnylink(tmpApplCode, tmpKindCode, tmpTxCode, RecvData);	
		 		}
		 		else {
	 				/*-------- ���������� �����Ͽ� ����ڿ��� ���������� �����Ѵ�. ------------*/
	 				COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "User Sync Response"); 
	 				String findkey = tmpApplCode + "\t" + tmpKindCode + "\t" + tmpTxCode;
					if (!findkey.equals("")){
						SendInfo sendinfo = hashsender.get(findkey);
						if (sendinfo != null) {
							hashsender.remove(findkey);
			 			    SendResponseToUserPC(sendinfo.getUserID(), sendinfo.getUserIP(), tmpApplCode, tmpKindCode, tmpTxCode, RecvData, RecvData.length );
						}
					}	
		 		}
		 	}
		 	else {
 				/* Async �ŷ��̸�, ��, Anylink���� ��û�� ������ ���Ͽ� ������ �������� �ʴ� ������ �����ǿ� �����鼭, ������ο� ������ ���� */
 				/* TCHECKER_ASYNCINRES �� In-Bound ��û�ŷ��� ����� ������, User PC�� ������ �����ϰ�    */
		 		/* TCHECKER_ASYNCINRES �� In-Bound ��û�ŷ��� ����� ������, ���������� Anylink�� ������.  */
 				COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "User ASync Response");
	 			if ( Proc_InboundResLink(tmpApplCode, tmpKindCode, tmpTxCode, RecvData) != true ){
	 				/* Anylink���� ��û�� ������ ���Ͽ� ���������� �����ϴ� ��� */
		 			COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "SendResponseToAnylink:" + tmpApplCode + ":" + tmpKindCode + ":" + tmpTxCode + ":" + tmpInOut_Flag + ":" + "");
					SendResponseToAnylink(tmpApplCode, tmpKindCode, tmpTxCode, RecvData);	
	 			}
		 	}
		 	 	    	 
	    }
 
		return true;
	}
	public void CommonSocketclose()
	{
		try{if (disWork != null) disWork.close();}catch(Exception e1){}
		try{if (disResp != null) disResp.close();}catch(Exception e1){}
		try{if (dosResp != null) dosResp.close();}catch(Exception e1){}
		
		try{if (client != null) client.close();}catch(Exception e1){}
		try{if (dis != null) dis.close();}catch(Exception e1){}
		try{if (dos != null) dos.close();}catch(Exception e1){}
	}
	private byte[] MakeSendMsg(String fname)
	{
		byte[] HEAD = null;
		int    MSGSIZE = 0;
		byte[] MSGDATA = null;
		ByteArrayOutputStream baos = null;
		try {
			File f = new File(fname);
			if (f.exists()) {
	        	DataInputStream dis = new DataInputStream(new FileInputStream(f));
	            int len  = (int) f.length();
	            byte[] ReadData = new byte[len];
	            dis.readFully(ReadData);
	            dis.close();
	            f.delete();
 
	            //Header �������� : UserID + UserPCIP + ApplCode + KindCode + TxCode
	            String strdata = new String(ReadData);
	            String[] arrdata = strdata.split("<DATAGUBUN>");
	            String[] arrHead = arrdata[0].split("\t");
	            UserID   = arrHead[0];
	            UserPCIP = arrHead[1];
	            ApplCode = arrHead[2];
	            KindCode = arrHead[3];
	            TxCode   = arrHead[4];
	            
	            //Body�� ByPass ������ ��쿡 txtMsgBody�� �ش��ϴ� �޼����� �����Ѵ�.
	            String[] arrbody = arrdata[1].split("<BODYBYPASS>");
	            baos = new ByteArrayOutputStream();
	            try {
		            //��û�������� ����Ÿ�κи� �и��Ͽ� �����Ѵ�.
		            
		            String[] arrmsgdat = arrbody[0].split("\n");
		            for(int i=0;i < arrmsgdat.length ;i++){
 		            	
		            	String[] arrtmp = arrmsgdat[i].split("\t");
		            	if (arrtmp[2].trim().equals("ARRAY-S") || arrtmp[2].trim().equals("STRUCT-S")) continue;

		            	//�÷��� ũ�⸸ŭ�� ���۸� �Ҵ��Ͽ�, ����Ÿ�� �����Ѵ�.
		            	if (arrtmp[4].equals("<NODATA>")) arrtmp[4] = "";
		            	byte[] mapdata = userfunction.Parsing(arrtmp[4].getBytes(), ApplCode, KindCode, TxCode);
 
		            	if (mapdata == null){
		            		mapdata = new byte[Integer.parseInt(arrtmp[3].trim())];
		            		for(int j=0;j < Integer.parseInt(arrtmp[3].trim());j++) {
		            			mapdata[i] = 0x20;
		            		}
		            	}
		            
		             
		            	byte[] tmpdat = new byte[Integer.parseInt(arrtmp[3].trim())];
		            	for(int j=0;j < tmpdat.length ;j++) tmpdat[j] = 0x20;
		            	
		            	 
		            	if (mapdata.length >= Integer.parseInt(arrtmp[3].trim())){
		            		System.arraycopy(mapdata, 0, tmpdat, 0, Integer.parseInt(arrtmp[3].trim()));	
		            	}
		            	else {
		            		System.arraycopy(mapdata, 0, tmpdat, 0, mapdata.length);
		            	}
		            	 
		            	baos.write(tmpdat, 0, tmpdat.length); 
		            }
		            MSGDATA = baos.toByteArray();
	            }catch(Exception baoserr){
	                COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", baoserr); 
	            	if (baos != null) baos.close();
	            }

	            
	            //���̼�������� 00:�����ʿ� �̸�, ������ �����Ѵ�.
	            if (COMMDATA.GetLEN_TYPE().equals("00")){
	            	String errmsg = UserID + ":" + ApplCode + ":" + KindCode + ":" + TxCode + ":" + "���̼�������� �����ϼ���.";
	                COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", errmsg); 
	                return ("ERROR:" + errmsg).getBytes(); 
	            }
	            		 
	            /*-------- ���Ÿ���� ������ �� ��������� ��쿡 HEAD ���ۿ� ���������� �����Ѵ�. --------*/
	            if (arrbody.length == 2) {
	            	//Body�� ByPass ������ ��쿡 txtMsgBody�� �ش��ϴ� �޼����� MSGDATA�� Append�Ѵ�.
	            	baos.write(arrbody[1].getBytes(), 0, arrbody[1].getBytes().length); 
	            	MSGDATA = baos.toByteArray();
	            }
	            
	            if (baos != null) baos.close();  //ByteArray close
 
	            MSGSIZE = MSGDATA.length ;
 
	            
	            if (COMMDATA.GetCOMM_HEAD_TYPE().equals("0") ){
	            	HEAD = new byte[Integer.parseInt(COMMDATA.GetCOMM_HEAD_SIZE())];
	            	for(int i=0 ; i < HEAD.length ;i++) HEAD[i] = (byte)32;  //HEAD�� ���鹮�ڷ� �ʱ�ȭ
	            	if (COMMDATA.GetLEN_TYPE().equals("10")){
	            		//���������� �����ʵ� ������ ���̰� : �������� ��� �޼����� ��üũ�⸦ ���������� �����Ѵ�.
		    			String SendLenFmt = String.format("%%0%dd", Integer.parseInt(COMMDATA.GetLEN_SIZE()));
		    			String SendLen = String.format(SendLenFmt, MSGSIZE);
	            		System.arraycopy(SendLen.getBytes(), 0, HEAD, Integer.parseInt(COMMDATA.GetLEN_OFFST()), SendLen.getBytes().length);
	            	}
	            	else if (COMMDATA.GetLEN_TYPE().equals("50")){
	            		//�����ʵ� ������ ���̰�(Integer) : ��������� ��쿡 �޼����� ��üũ�⿡�� ���� Offset �� ���� Size �� �� ������� ���������� �����Ѵ�.
	            		int tmplen = MSGSIZE;
	            		HEAD[Integer.parseInt(COMMDATA.GetLEN_OFFST()) + 0] = (byte)(tmplen / 256);
	            		HEAD[Integer.parseInt(COMMDATA.GetLEN_OFFST()) + 1] = (byte)(tmplen % 256);	
	            	}
	            	else if (COMMDATA.GetLEN_TYPE().equals("99")){
	            		byte[] bytelen = userlength.GetSendLength(COMMDATA, COMMDATA.GetLU_NAME(), ApplCode, MSGDATA);
	            		if (bytelen != null){
	            			System.arraycopy(bytelen,0,HEAD, Integer.parseInt(COMMDATA.GetLEN_OFFST()), bytelen.length);
	            		}
	            	}
	            	else {
	            		String errmsg = UserID + ":" + ApplCode + ":" + KindCode + ":" + TxCode + ":" + "������ �� ��쿡 ���̼�������� [10:�����ʵ� ������ ���̰�] [50:�����ʵ� ������ ���̰�(Integer)] �̾�� �մϴ�.";
	            		COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", errmsg);
	            		return ("ERROR:" + errmsg).getBytes();
	            	}
	            	
            		//���۵���Ÿ ����
            		byte[] tmpdat = new byte[Integer.parseInt(COMMDATA.GetCOMM_HEAD_SIZE()) + MSGSIZE];
            		System.arraycopy(HEAD, 0, tmpdat, 0, HEAD.length);
            		System.arraycopy(MSGDATA, 0, tmpdat, HEAD.length, MSGDATA.length);
  
            		String datamsg = UserID + ":" + ApplCode + ":" + KindCode + ":" + TxCode + ":" + "MakeSendMsg OK \n[" + new String(tmpdat) + "]";
            		COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", datamsg);
            		
            		return tmpdat;
	            }
	            if (COMMDATA.GetCOMM_HEAD_TYPE().equals("1")){
	            	COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", UserID + ":" + ApplCode + ":" + KindCode + ":" + TxCode + ":" + "MSGSIZE = " + MSGSIZE);
	            	byte[] tmpdat = new byte[MSGSIZE];
	            	System.arraycopy(MSGDATA, 0, tmpdat, 0, MSGDATA.length);
	             
	            	if (COMMDATA.GetLEN_TYPE().equals("10")){
	            		//���������� �����ʵ� ������ ���̰� : ��������� ��쿡 �޼����� ��üũ�⿡�� ���� Offset �� ���� Size �� �� ������� ���������� �����Ѵ�.
		    			String SendLenFmt = String.format("%%0%dd", Integer.parseInt(COMMDATA.GetLEN_SIZE()));
		    			String SendLen = String.format(SendLenFmt, MSGSIZE - Integer.parseInt(COMMDATA.GetLEN_OFFST()) - Integer.parseInt(COMMDATA.GetLEN_SIZE()));
	            		System.arraycopy(SendLen.getBytes(), 0, tmpdat, Integer.parseInt(COMMDATA.GetLEN_OFFST()), SendLen.getBytes().length);
	            	}
	            	else if (COMMDATA.GetLEN_TYPE().equals("11")){
	            		//�����ʵ带 ������ ���̰� : ��������� ��쿡 �޼����� ��üũ�⸦ �����Ѵ�.
		    			String SendLenFmt = String.format("%%0%dd", Integer.parseInt(COMMDATA.GetLEN_SIZE()));
		    			String SendLen = String.format(SendLenFmt, MSGSIZE);
	            		System.arraycopy(SendLen.getBytes(), 0, tmpdat, Integer.parseInt(COMMDATA.GetLEN_OFFST()), SendLen.getBytes().length);
	            	}
	            	else if (COMMDATA.GetLEN_TYPE().equals("12")){
 
	            		//Body�� ���̰� : ��������� ��쿡 �޼����� ��üũ�⿡�� Head Size �� ������� ���������� �����Ѵ�.
		    			String SendLenFmt = String.format("%%0%dd", Integer.parseInt(COMMDATA.GetLEN_SIZE()));
		    			String SendLen = String.format(SendLenFmt, MSGSIZE - Integer.parseInt(COMMDATA.GetCOMM_HEAD_SIZE()));
		    			
		    			COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", UserID + ":" + ApplCode + ":" + KindCode + ":" + TxCode + ":" + "SendLen = " + SendLen);
	            		System.arraycopy(SendLen.getBytes(), 0, tmpdat, Integer.parseInt(COMMDATA.GetLEN_OFFST()), SendLen.getBytes().length);
	            	}
	            	else if (COMMDATA.GetLEN_TYPE().equals("50")){
	            		//�����ʵ� ������ ���̰�(Integer) : ��������� ��쿡 �޼����� ��üũ�⿡�� ���� Offset �� ���� Size �� �� ������� ���������� �����Ѵ�.
	            		int tmplen = MSGSIZE - Integer.parseInt(COMMDATA.GetLEN_OFFST()) - Integer.parseInt(COMMDATA.GetLEN_SIZE());
	            		tmpdat[Integer.parseInt(COMMDATA.GetLEN_OFFST()) + 0] = (byte)(tmplen / 256);
	            		tmpdat[Integer.parseInt(COMMDATA.GetLEN_OFFST()) + 1] = (byte)(tmplen % 256);	
	            	}
	            	else if (COMMDATA.GetLEN_TYPE().equals("51")){
	            		//�����ʵ带 ������ ���̰�(Integer) : ��������� ��쿡 �޼����� ��üũ�⸦ ���������� �����Ѵ�.
	            		int tmplen = MSGSIZE;
	            		tmpdat[Integer.parseInt(COMMDATA.GetLEN_OFFST()) + 0] = (byte)(tmplen / 256);
	            		tmpdat[Integer.parseInt(COMMDATA.GetLEN_OFFST()) + 1] = (byte)(tmplen % 256);	
	            	}
	            	else if (COMMDATA.GetLEN_TYPE().equals("52")){
	            		//Body�� ���̰�(Integer) : ��������� ��쿡 �޼����� ��üũ�⿡�� Head Size �� ������� ���������� �����Ѵ�.
	            		int tmplen = MSGSIZE - Integer.parseInt(COMMDATA.GetCOMM_HEAD_SIZE());
	            		tmpdat[Integer.parseInt(COMMDATA.GetLEN_OFFST()) + 0] = (byte)(tmplen / 256);
	            		tmpdat[Integer.parseInt(COMMDATA.GetLEN_OFFST()) + 1] = (byte)(tmplen % 256);	
	            	}
	            	else if (COMMDATA.GetLEN_TYPE().equals("99")){
   
	            		byte[] bytelen = userlength.GetSendLength(COMMDATA, COMMDATA.GetLU_NAME(), ApplCode, MSGDATA);
                   		if (bytelen != null){
	            			System.arraycopy(bytelen,0,tmpdat, Integer.parseInt(COMMDATA.GetLEN_OFFST()), bytelen.length);
	            		}
	            	}
	            	else {
	            		String errmsg = UserID + ":" + ApplCode + ":" + KindCode + ":" + TxCode + ":" + "������� �� ��쿡 ���̼�������� [13:��������] [14:END����]�� ����� �� �����ϴ�.";
	            		COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", errmsg);
	            		return ("ERROR:" + errmsg).getBytes();
	            	}
 
            		String datamsg = UserID + ":" + ApplCode + ":" + KindCode + ":" + TxCode + ":" + "MakeSendMsg OK \n[" + new String(tmpdat) + "]";
            		COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", datamsg);
            		
            	    return tmpdat;
	            }
	            if (COMMDATA.GetCOMM_HEAD_TYPE().equals("2")){
	            	COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", UserID + ":" + ApplCode + ":" + KindCode + ":" + TxCode + ":" + "��������=" + COMMDATA.GetLEN_SIZE());
	            	//�������� �������� ����� ��� �������� ��ŭ�� ���۸� �Ҵ��Ͽ�, �޼����� �����Ѵ�.
	            	byte[] tmpdat = new byte[Integer.parseInt(COMMDATA.GetLEN_SIZE())];
	            	System.arraycopy(MSGDATA, 0, tmpdat, 0, MSGDATA.length);
	            	
	            	String datamsg = UserID + ":" + ApplCode + ":" + KindCode + ":" + TxCode + ":" + "MakeSendMsg OK \n[" + new String(tmpdat) + "]";
            		COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", datamsg);
            		
	            	return tmpdat;
	            }
	            if (COMMDATA.GetCOMM_HEAD_TYPE().equals("3")){
	            	//End���� ����� ��쿡 �޼����� �״�� �����Ѵ�.
	            	
	            	String datamsg = UserID + ":" + ApplCode + ":" + KindCode + ":" + TxCode + ":" + "MakeSendMsg OK \n[" + new String(MSGDATA) + "]";
            		COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", datamsg);
            		
	            	return MSGDATA;
	            }
			}
		}catch(Exception e){
			COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", e);
		}
		
		String errmsg = UserID + ":" + ApplCode + ":" + KindCode + ":" + TxCode + ":" + "�� ���� ������� �� ���̼�������� Ȯ�ΰų� ��û������ Ȯ���ϼ���.";
		errmsg = errmsg + "COMM_HEAD_TYEP=" + COMMDATA.GetCOMM_HEAD_TYPE() + ":LEN_TYPE=" + COMMDATA.GetLEN_TYPE();
		COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", errmsg);
		return ("ERROR:" + errmsg).getBytes();
	  
	}
    private void SendResponseToUserPC(String pUserID, String pUserIP, String pApplCode, String pKindCode, String pTxCode, byte[] resmsg, int ressize)
    {
    	Socket user_client = new Socket();
    	DataOutputStream user_dos = null;
    	try {
    		byte[] tmprecv = new byte[ressize];
    		System.arraycopy(resmsg,0,tmprecv,0,ressize);
    		COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", pUserID + ":" + pApplCode + ":" + pKindCode + ":" + pTxCode + ":" + "Anylink���� ���� : \n    [" + new String(tmprecv) + "]");
    		
    		//Response File Write Start
			String   fname = "./Response/Tcpmsg/" + pUserIP + "/message.dat" ;
			 
			//Directory Check
			File dir1 = new File("./Response");
			if (!dir1.exists()) dir1.mkdir();
			
			File dir2 = new File("./Response/Tcpmsg");
			if (!dir2.exists()) dir2.mkdir();
			
			File dir3 = new File("./Response/Tcpmsg/" + pUserIP);
			if (!dir3.exists()) dir3.mkdir();
			
			DataOutputStream out1 = new DataOutputStream(new FileOutputStream(new File(fname)));
			out1.write(resmsg);
			out1.close();
			
			COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", pUserID + ":" + pApplCode + ":" + pKindCode + ":" + pTxCode + ":" + "���䳻���� �����Ͽ����ϴ�.[" + UserPCIP + ":" + UserTcpResPort + "]");
			//Response File Write End
			
    		user_client.connect(new InetSocketAddress(pUserIP, UserTcpResPort), 3000);  //3�� ��ٸ�
	 		user_dos = new DataOutputStream(user_client.getOutputStream());
	 		
 
        	String lenfmt = String.format("%08d", ressize);
        	user_dos.write(lenfmt.getBytes(), 0 , lenfmt.getBytes().length);
        	user_dos.write("RESPONSETP".getBytes(), 0 , "RESPONSETP".getBytes().length);
        	user_dos.write(resmsg, 0, ressize);
	        
            user_dos.flush();	
            setThreadSleep(10);
            user_client.close();
            
            COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", pUserID + ":" + pApplCode + ":" + pKindCode + ":" + pTxCode + ":" + "����ڿ��� ���������� ���� �Ͽ����ϴ�.[" + UserPCIP + ":" + UserTcpResPort + "]");
  
    	}catch(Exception e) {
    		try{if(user_client != null) user_client.close();}catch(Exception e1){}
    		COMMDATA.GetTCheckerLog().WriteLog("W", "TcpManager", pUserID + ":" + pApplCode + ":" + pKindCode + ":" + pTxCode + ":" + "����ڿ��� ���������� �������� ���߽��ϴ�.[" + UserPCIP + "]");
    		return;
    	}
    }
    
    private void SendResponseToAnylink(String pApplCode, String pKindCode, String pTxCode, byte[] resmsg)
    {
    	try{
    		byte[]   EOD = new byte[] {(byte)0xFF,(byte)0xEF};
 
    		TCheckerMapping mymapping = new TCheckerMapping(COMMDATA,pApplCode,pKindCode,pTxCode);
    		byte[] makeMsg = mymapping.MakeResponseMsg(new String(resmsg));
 
    		if (makeMsg != null) {
	    		dosWork.write(makeMsg,0, makeMsg.length );
	    		if (COMMDATA.GetCONNECT_TYPE().equals("7")){
	    			dosWork.write(EOD, 0, EOD.length);
		 		}
	    		dosWork.flush();
	    		COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "SERVER:" + pApplCode + ":" + pKindCode + ":" + pTxCode + ":" + "SendData To Anylink : " + new String(makeMsg));
    		}
    	}catch(Exception e){}
    }
	private void ErrorMsgSend(String pUserID, String pApplCode, String pKindCode, String pTxCode, byte[] resmsg)
	{
    	Socket user_client = new Socket();
    	DataOutputStream user_dos = null;
    	try {
    		user_client.connect(new InetSocketAddress(UserPCIP, UserTcpResPort), 3000);  //3�� ��ٸ�
	 		user_dos = new DataOutputStream(user_client.getOutputStream());
	 		
	 		String lenfmt = String.format("%08d", resmsg.length);
        	user_dos.write(lenfmt.getBytes(), 0 , lenfmt.getBytes().length);
        	user_dos.write("RESPONSETP".getBytes(), 0 , "RESPONSETP".getBytes().length);
	        user_dos.write(resmsg, 0, resmsg.length );
            user_dos.flush();	
            setThreadSleep(100);
            user_client.close();
    	}catch(Exception e) {
    		try{if(user_client != null) user_client.close();}catch(Exception e1){}
    		COMMDATA.GetTCheckerLog().WriteLog("W", "TcpManager", pUserID + ":" + pApplCode + ":" + pKindCode + ":" + pTxCode + ":" + "����ڿ��� ���������� �������� ���߽��ϴ�.[" + UserPCIP + "]");
    		return;
    	}	 	 
	}
 
    private boolean isResponseMsg(String pUserID, String pApplCode, String pKindCode, String pTxCode)
    {
     
		String isql = " select t.res_flag, 'NO' ";
		isql = isql + "\n from altx t ";
        isql = isql + "\n where t.appl_code = '" + pApplCode + "' ";
        isql = isql + "\n   and t.rep_kind_code = '" + pKindCode + "' ";
        isql = isql + "\n   and t.tx_code = '" + pTxCode + "' ";
 
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
		if (retdata == null || retdata.equals("")) {
			COMMDATA.GetTCheckerLog().WriteLog("W", "TcpManager", pUserID + ":" + pApplCode + ":" + pKindCode + ":" + pTxCode + ":" + "������ſ��� ������ �о���µ� �����߽��ϴ�.");
			return false;
		}
		
		//�ŷ��� ���� Ÿ�Ӿƿ��� �����ǿ� ������, �� �ŷ��� ���� Ÿ�Ӿƿ��� �����ϰ�, 
		//�׷��� ������, ������ ������ Ÿ�Ӿƿ��� �����Ͽ� 0 �̻��� �����ǿ� ������ ������ ���� Ÿ�Ӿƿ� �������� �����Ѵ�.
        String[] arrtmp = retdata.split("\t");
        if (arrtmp[0].trim().equals("1")) return true;

    	return false;
    }
    private int getTimeOutInfo(String pUserID, String pApplCode, String pKindCode, String pTxCode)
    {
		String isql = " select t.tx_time_limit, a.timeout, 'NO' ";
		isql = isql + "\n from alappl a, altx t ";
        isql = isql + "\n where a.appl_code = t.appl_code ";
        isql = isql + "\n   and t.appl_code = '" + pApplCode + "' ";
        isql = isql + "\n   and t.rep_kind_code = '" + pKindCode + "' ";
        isql = isql + "\n   and t.tx_code = '" + pTxCode + "' ";
 
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
		if (retdata == null || retdata.equals("")) {
			COMMDATA.GetTCheckerLog().WriteLog("W", "TcpManager", pUserID + ":" + pApplCode + ":" + pKindCode + ":" + pTxCode + ":" + "Ÿ�Ӿƿ� ������ �о���µ� �����߽��ϴ�.");
			return Default_TimeOut;
		}
		
		//�ŷ��� ���� Ÿ�Ӿƿ��� �����ǿ� ������, �� �ŷ��� ���� Ÿ�Ӿƿ��� �����ϰ�, 
		//�׷��� ������, ������ ������ Ÿ�Ӿƿ��� �����Ͽ� 0 �̻��� �����ǿ� ������ ������ ���� Ÿ�Ӿƿ� �������� �����Ѵ�.
        String[] arrtmp = retdata.split("\t");
        if (arrtmp[0].trim().equals("") || arrtmp[0].trim().equals("0")) {
        	if (!arrtmp[1].trim().equals("") && !arrtmp[1].trim().equals("0")) {
        		return Integer.parseInt(arrtmp[1]);
        	}
        }
        else {
        	return Integer.parseInt(arrtmp[0]);
        }
        return Default_TimeOut;
    }
 
    
    private void setThreadSleep(int time)
    {
   	    try{
            Thread.sleep(time);
        } catch(InterruptedException e) {}
    }
    public void getSystemInfo()
    {
        try {
	        Properties properties = new Properties();
	        properties.load(new FileInputStream("./Properties/System.inf"));
	        String default_timeout = properties.getProperty("DEFAULT_TIMEOUT", "30");
	        AnylinkIP = properties.getProperty("ANYLINK_IP", "xxx.xxx.xxx.xxx");
	        String managerport = properties.getProperty("MANAGER_PORT", "xxxxx");
	        
	        Default_TimeOut = Integer.parseInt(default_timeout.trim());
	      
	        UserTcpResPort = Integer.parseInt(managerport) + 4;
	        
	        
	        
		} catch (Exception e) {
			return ;
		}
    }
	private void GetSharedInfo()
	{
 
        //ȸ���������� ���ȸ������ ������ ȸ���� ���� ������ ������ ȸ�������� �о�´�.
		String isql = " SELECT DISTINCT G.APPL_CODE ";
        isql = isql + "\n FROM ALLINE L , ALGRPLINE G ";
        isql = isql + "\n WHERE L.SYMBNAME = G.SYMBNAME ";
        isql = isql + "\n   AND L.LU_NAME = '" + COMMDATA.GetLU_NAME() + "' ";        
        isql = isql + "\n   AND L.STA_TYPE = 1 ";
		
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
		if (retdata == null || retdata.equals("")) return;
		
		ApplSharedInfo = retdata.replace("\t","").split("\n");
	}
    private void GetApplInternalInfo()
    {
 
        //ȸ���������� ���ȸ������ ������ ȸ���� ���� ������ ������ ȸ�������� �о�´�.
		String isql = " SELECT T.APPL_CODE, T.KIND_CODE, T.TX_CODE, ";
		isql = isql + "\n       NVL(T.INOUT_FLAG, 'N'), NVL(T.MAP_FLAG, 'B'), ";
		isql = isql + "\n       NVL(T.KeyOffset1, 0 ), NVL(T.KeyLen1, 0 ), NVL(T.KeyVal1 ,'<NODATA>' ) ,";
		isql = isql + "\n       NVL(T.KeyOffset2, 0 ), NVL(T.KeyLen2, 0 ), NVL(T.KeyVal2 ,'<NODATA>' ) ,";
		isql = isql + "\n       NVL(T.KeyOffset3, 0 ), NVL(T.KeyLen3, 0 ), NVL(T.KeyVal3 ,'<NODATA>' )  ";
		isql = isql + "\n FROM TCHECKER_TXDETAIL T ";
        isql = isql + "\n ORDER BY T.APPL_CODE, T.KIND_CODE, T.TX_CODE ";
		
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
		if (retdata == null || retdata.equals("")) return;
 
    	//���ŵ���Ÿ����  ����/�ŷ� �ڵ尪�� �����´�.
		ApplInternalInfo = retdata.split("\n");
 
    	
    }
    
    private String GetApplInternalKindTx(byte[] RData)
    {
    	
    	if (ApplSharedInfo == null) {
    		COMMDATA.GetTCheckerLog().WriteLog("W", "TcpManager", "SERVER:NONE:NONE:NONE:ȸ������ ������ �����ϴ�. LU_NAME:" + COMMDATA.GetLU_NAME());
    		return "";
    	}
 
    	for(int i=0;i < ApplSharedInfo.length;i++)
    	{
    		for(int j=0;j < ApplInternalInfo.length ;j++){
    			String[] tmpWork2 = ApplInternalInfo[j].split("\t");
    			if (tmpWork2[0].trim().equals(ApplSharedInfo[i])) {
    				String   tmpApplCode   = tmpWork2[0].trim();
    	    		String   tmpKindCode   = tmpWork2[1].trim();
    	    		String   tmpTxCode     = tmpWork2[2].trim();
    	    		String   tmpInOut_Flag = tmpWork2[3].trim();
    	    		String   tmpMap_Flag   = tmpWork2[4].trim();
    	    		String   tmpKeyOffset1 = tmpWork2[5].trim();
    	    		String   tmpKeyLen1    = tmpWork2[6].trim();
    	    		String   tmpKeyVal1    = tmpWork2[7].trim().replace("<NODATA>", "");
    	    		String   tmpKeyOffset2 = tmpWork2[8].trim();
    	    		String   tmpKeyLen2    = tmpWork2[9].trim();
    	    		String   tmpKeyVal2    = tmpWork2[10].trim().replace("<NODATA>", "");
    	    		String   tmpKeyOffset3 = tmpWork2[11].trim();
    	    		String   tmpKeyLen3    = tmpWork2[12].trim();
    	    		String   tmpKeyVal3    = tmpWork2[13].trim().replace("<NODATA>", "");
    	    		
    	    		COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "SERVER:NONE:NONE:NONE:GetApplInternalKindTx tmpKeyVal:" + tmpKeyVal1+ ":" + tmpKeyVal2 + ":" + tmpKeyVal3);
    	    		
    	      		//�����ڵ� ���� �� ��
    	    		String CompKeyVal1 = "";
    	    		String CompKeyVal2 = "";
    	    		String CompKeyVal3 = "";
    	    		
    	    		if (!tmpKeyLen1.trim().equals("") && Integer.parseInt(tmpKeyLen1.trim()) > 0) {
    	    			byte[] tmpstr = new byte[Integer.parseInt(tmpKeyLen1.trim())];	
    	    			System.arraycopy(RData, Integer.parseInt(tmpKeyOffset1), tmpstr, 0, tmpstr.length );
    	    			CompKeyVal1 = new String(tmpstr);
    	    		}
    	    		if (!tmpKeyLen2.trim().equals("") && Integer.parseInt(tmpKeyLen2.trim()) > 0) {
    	    			byte[] tmpstr = new byte[Integer.parseInt(tmpKeyLen2.trim())];	
    	    			System.arraycopy(RData, Integer.parseInt(tmpKeyOffset2), tmpstr, 0, tmpstr.length );
    	    			CompKeyVal2 = new String(tmpstr);
    	    		}
    	    		if (!tmpKeyLen3.trim().equals("") && Integer.parseInt(tmpKeyLen3.trim()) > 0) {
    	    			byte[] tmpstr = new byte[Integer.parseInt(tmpKeyLen3.trim())];	
    	    			System.arraycopy(RData, Integer.parseInt(tmpKeyOffset3), tmpstr, 0, tmpstr.length );
    	    			CompKeyVal3 = new String(tmpstr);
    	    		}
    	    		
    	    		COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "SERVER:NONE:NONE:NONE:GetApplInternalKindTx CompKeyVal:" + CompKeyVal1+ ":" + CompKeyVal2 + ":" + CompKeyVal3);
    	    		
    	    		if (CompKeyVal1.equals(tmpKeyVal1)){
    	    			if (CompKeyVal2.equals(tmpKeyVal2)){
    	    				if (CompKeyVal3.equals(tmpKeyVal3)){
    	    					COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "SERVER:NONE:NONE:NONE:GetApplInternalKindTx Return:" + ApplSharedInfo[i] + "\t" + tmpKindCode + "\t" + tmpTxCode + "\t" + tmpInOut_Flag + "\t" + tmpMap_Flag);
    	    					
    	    					return ApplSharedInfo[i] + "\t" + tmpKindCode + "\t" + tmpTxCode + "\t" + tmpInOut_Flag + "\t" + tmpMap_Flag;
    	    				}
    	    			}
    	    		}
    			}
    		}
 
    	}
 
		return "";
    }
    
    public String GetFileContents(String fname)  {
        DataInputStream in = null;
        try {

               File f = new File(fname);
               in = new DataInputStream(new FileInputStream(f));

               int len = (int) f.length();
               byte buf[] = new byte[len];
               in.readFully(buf);
               in.close();
  
               return new String(buf);
            
            
        }catch (Exception e) {
               //e.printStackTrace();
        } 
        return "";
    }
    private void setLastStatus()
    {
		SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
		String reg_dt1 = formatter.format(new java.util.Date()); //��� �Ͻ�
		formatter = new java.text.SimpleDateFormat("HHmmss", java.util.Locale.KOREA);
		String reg_tm1 = formatter.format(new java.util.Date());  //��Ͻð� 
		formatter = new java.text.SimpleDateFormat("S", java.util.Locale.KOREA);
		String reg_milsec1 = formatter.format(new java.util.Date());  //��Ͻð� 
 
		int hh1 = Integer.parseInt(reg_tm1.substring(0,2));
		int mm1 = Integer.parseInt(reg_tm1.substring(2,4));
		int ss1 = Integer.parseInt(reg_tm1.substring(4,6));
		int ms1 = Integer.parseInt(reg_milsec1);
 
		long time1 = hh1 * 3600 + mm1 * 60 + ss1;

    	this.LineStatus = time1;
    }
    private long getCurrentStatus()
    {
		SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
		String reg_dt1 = formatter.format(new java.util.Date()); //��� �Ͻ�
		formatter = new java.text.SimpleDateFormat("HHmmss", java.util.Locale.KOREA);
		String reg_tm1 = formatter.format(new java.util.Date());  //��Ͻð� 
		formatter = new java.text.SimpleDateFormat("S", java.util.Locale.KOREA);
		String reg_milsec1 = formatter.format(new java.util.Date());  //��Ͻð� 
 
		int hh1 = Integer.parseInt(reg_tm1.substring(0,2));
		int mm1 = Integer.parseInt(reg_tm1.substring(2,4));
		int ss1 = Integer.parseInt(reg_tm1.substring(4,6));
		int ms1 = Integer.parseInt(reg_milsec1);
 
		long time1 = hh1 * 3600 + mm1 * 60 + ss1;

    	return time1;
    }
    
    private void Proc_InboundAsyncRes(String pApplCode, String pKindCode, String pTxCode, String pIP)
    {
    	String retstr = SearchResLinkOne(pApplCode,  pKindCode,  pTxCode);
    	if (retstr.trim().equals("")) return;
    	
    	String[] arrtmp = retstr.split("\t");
    	 
		SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
		String iDate = formatter.format(new java.util.Date()); //��û����
		
		formatter = new java.text.SimpleDateFormat("HHmmss", java.util.Locale.KOREA);
		String iTime = formatter.format(new java.util.Date());  //��û�ð�
 
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
    
	private boolean Proc_InboundResLink(String pApplCode, String pKindCode, String pTxCode, byte[] recvdata)
	{
		SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
		String iDate = formatter.format(new java.util.Date()); //��û����
		
		formatter = new java.text.SimpleDateFormat("HHmmss", java.util.Locale.KOREA);
		String iTime = formatter.format(new java.util.Date());  //��û�ð�
		
		String isql = "SELECT IP FROM TCHECKER_ASYNCINRES WHERE ";
		isql = isql + "\n TRAN_DATE = '" + iDate + "' ";
		isql = isql + "\n AND APPL_CODE = '" + pApplCode + "'  ";
		isql = isql + "\n AND KIND_CODE = '" + pKindCode + "' ";
		isql = isql + "\n AND TX_CODE   = '" + pTxCode + "'  ";
		isql = isql + "\n AND SEND_YN   = 'N' ";
 
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
		if (retdata == null || retdata.equals("")) {
			return false ;
		}
		
		String[] arrtmp = retdata.split("\n");
		for(int i=0;i < arrtmp.length ;i++){
			
	    	Socket user_client = new Socket();
	    	DataOutputStream user_dos = null;
	    	try {
	    		user_client.connect(new InetSocketAddress(arrtmp[i], UserTcpResPort), 3000);  //3�� ��ٸ�
		 		user_dos = new DataOutputStream(user_client.getOutputStream());
		 		
		 		String lenfmt = String.format("%08d", recvdata.length);
	        	user_dos.write(lenfmt.getBytes(), 0 , lenfmt.getBytes().length);
	        	user_dos.write("RESPONSETP".getBytes(), 0 , "RESPONSETP".getBytes().length);
		        user_dos.write(recvdata, 0, recvdata.length );
	            user_dos.flush();	
	            setThreadSleep(100);
	            user_client.close();
	            
	    		isql = "UPDATE TCHECKER_ASYNCINRES SET SEND_YN = 'Y' WHERE ";
	    		isql = isql + "\n TRAN_DATE = '" + iDate + "' ";
	    		isql = isql + "\n AND APPL_CODE = '" + pApplCode + "'  ";
	    		isql = isql + "\n AND KIND_CODE = '" + pKindCode + "' ";
	    		isql = isql + "\n AND TX_CODE   = '" + pTxCode + "'  ";
	    		isql = isql + "\n AND IP        = '" + arrtmp[i] + "'  ";
	    		isql = isql + "\n AND SEND_YN   = 'N' ";
	    		COMMDATA.GetDBManager().UpdateData(isql);
	    		COMMDATA.GetDBManager().ServerDBCommit();
	    		
				// �����PC�� ������ �����ϱ� ���ؼ� DB�� ������ �����Ѵ�.
				COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "Async Response Data : " + pApplCode + ":" + pKindCode + ":" + pTxCode + ":" + arrtmp[i] + ":" + new String(recvdata));
				
				
	    	}catch(Exception e) {
	    		try{if(user_client != null) user_client.close();}catch(Exception e1){}
	    	}
		}
        return true;
	}  
	
	private String SearchResLinkOne(String pApplCode, String pKindCode, String pTxCode)
	{
		String isql = "";
		isql = isql + "   SELECT R.RES_APPL_CODE, A.APPL_NAME, R.RES_KIND_CODE, K.NAME, R.RES_TX_CODE, T.NAME, R.RES_PORTNO, 'NO' ";
		isql = isql + "\n FROM TCHECKER_RESLINK R, ALAPPL A, ALKIND K, ALTX T                                 ";
		isql = isql + "\n WHERE R.REQ_APPL_CODE = '" + pApplCode + "'                                         ";
		isql = isql + "\n   AND R.REQ_KIND_CODE = '" + pKindCode + "'                                         ";
		isql = isql + "\n   AND R.REQ_TX_CODE   = '" + pTxCode   + "'                                         ";
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
 
		if (retdata == null || retdata.equals("")) return "";

 
		return retdata;
	}
	
 
}
