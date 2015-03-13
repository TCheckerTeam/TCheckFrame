package TCheckServer.UserClass;

import TCheckServer.Engine.CommData;


public class UserShare {
	private CommData  COMMDATA = null;
	private String APPL_CODE = "";  
	private String KIND_CODE = "";  
	private String TX_CODE   = ""; 
	private String RECVDATA  = "";
 
	public void SetAPPL_CODE (String parm0){ APPL_CODE = parm0; }
	public void SetKIND_CODE (String parm0){ KIND_CODE = parm0; }
	public void SetTX_CODE   (String parm0){ TX_CODE   = parm0; }
	public void SetRECVDATA  (String parm0){ RECVDATA  = parm0; }
 
	public String GetAPPL_CODE() {return APPL_CODE  ;}
	public String GetKIND_CODE() {return KIND_CODE  ;}
	public String GetTX_CODE  () {return TX_CODE    ;}
	public String GetRECVDATA () {return RECVDATA   ;}
	public UserShare(CommData commdata) {
		// TODO Auto-generated constructor stub
		COMMDATA = commdata;
	}
	public void GetGongUInfo()
	{
		byte[]    tmpbyte   = null;
		 
		COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "GetGongUInfo Start");
		
 		if (APPL_CODE.trim().equals("")) {
 			//�����ڵ带 �����Ѵ�.
 			SetAPPL_CODE(COMMDATA.GetAPPL_CODE());
 			
 	    	//�����ڵ带 �����Ѵ�.
 			try{
 		    	tmpbyte = new byte[Integer.parseInt(COMMDATA.GetKIND_LEN())];
 		        System.arraycopy(RECVDATA.getBytes(), Integer.parseInt(COMMDATA.GetKIND_OFFSET()), tmpbyte, 0, tmpbyte.length);
 		        SetKIND_CODE(new String(tmpbyte));
 		        COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", APPL_CODE+":KIND_INFO:"+ COMMDATA.GetKIND_OFFSET() + "," + COMMDATA.GetKIND_LEN() + "," + new String(tmpbyte));
 			}catch(Exception e){}
 	    	
 	    	//�ŷ��ڵ带 �����Ѵ�.
 			try{
 		    	tmpbyte = new byte[Integer.parseInt(COMMDATA.GetTX_LEN())];
 		        System.arraycopy(RECVDATA.getBytes(), Integer.parseInt(COMMDATA.GetTX_OFFSET()), tmpbyte, 0, tmpbyte.length);
 		        SetTX_CODE(new String(tmpbyte));
 		        COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", APPL_CODE+":TX_INFO:"+ COMMDATA.GetTX_OFFSET() + "," + COMMDATA.GetTX_LEN() + "," + new String(tmpbyte));
 			}catch(Exception e){}
 		}
 
        UserDefineLogic();
	}
	/*----------------------- User Define Logic ---------------------------*/
	public boolean UserDefineLogic()
	{
		/* Sample Code */
		COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", APPL_CODE+":"+KIND_CODE+":"+TX_CODE+":�������� ���˽���");
		
		if (APPL_CODE.equals("AP08") || APPL_CODE.equals("AP09")) {
			if (this.RECVDATA.substring(10,11).equals("S") || this.RECVDATA.substring(10,11).equals("R")) {
				APPL_CODE = "AP08";
			}
			if (this.RECVDATA.substring(10,11).equals("A") || this.RECVDATA.substring(10,11).equals("B")) {
				APPL_CODE = "AP09";
			}
			COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", APPL_CODE+":"+KIND_CODE+":"+TX_CODE+":�������� ��������(True)");
			return true;
		}
		COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", APPL_CODE+":"+KIND_CODE+":"+TX_CODE+":�������� ��������(False)");
		return false;
	}
}
