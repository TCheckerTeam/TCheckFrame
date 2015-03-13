package TCheckServer.UserClass;

import TCheckServer.Engine.CommData;

public class UserChkLen {
 
	/*----------------------- System Define Init Logic ---------------------------*/
	public UserChkLen(){
		//반드시 해당 함수가 존재해야 함.
	}
	/*----------------------- User Define Logic ---------------------------*/
	public byte[] GetSendLength(CommData COMMDATA, String LU_NAME, String APPL_CODE, byte[] RECVDATA)
	{
    	//리턴할 길이버퍼의 크기만큼 byte 버퍼를 생성하고, 조건에 맞게 길이정보를 셋팅한다.
    	if (APPL_CODE.equals("AP15")) {
    		String lenstr = String.format("%04d", RECVDATA.length - 10);
    		return lenstr.getBytes();
    	}
    	if (APPL_CODE.equals("AP16")) {
    		String lenstr = String.format("%04d", RECVDATA.length - 10);
    		return lenstr.getBytes();
    	}
    	String defaultlen = "" + RECVDATA.length;
		return defaultlen.getBytes();
	}
	public int GetRecvLength(CommData COMMDATA, String LU_NAME, String APPL_CODE, byte[] RecvHead)
	{
		COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "GetRecvLength : " + APPL_CODE + ":" + new String(RecvHead) );
		
    	//RecvHead 에서 길이정보를 추출하고, Body 부분의 길이를 가공하여 리턴한다.
    	if (APPL_CODE.equals("AP15")) {
    		byte[] lenbyte = new byte[4];
    		int    len = 0;
    		
    		System.arraycopy(RecvHead, 11, lenbyte, 0, lenbyte.length);
    		len = Integer.parseInt(new String(lenbyte));
    		
    		//Header는 읽은상태이므로, Body 길이만 계산하여 리턴한다.
    		return len + 10 - RecvHead.length;
    	}
    	if (APPL_CODE.equals("AP16")) {
    		byte[] lenbyte = new byte[4];
    		int    len = 0;
    		
    		System.arraycopy(RecvHead, 0, lenbyte, 0, lenbyte.length);
    		len = Integer.parseInt(new String(lenbyte));
    		
    		//Header는 읽은상태이므로, Body 길이만 계산하여 리턴한다.
    		return len + 10;
    	}
		return 0;
	}
}
