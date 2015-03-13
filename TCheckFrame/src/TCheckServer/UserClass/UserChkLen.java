package TCheckServer.UserClass;

import TCheckServer.Engine.CommData;

public class UserChkLen {
 
	/*----------------------- System Define Init Logic ---------------------------*/
	public UserChkLen(){
		//�ݵ�� �ش� �Լ��� �����ؾ� ��.
	}
	/*----------------------- User Define Logic ---------------------------*/
	public byte[] GetSendLength(CommData COMMDATA, String LU_NAME, String APPL_CODE, byte[] RECVDATA)
	{
    	//������ ���̹����� ũ�⸸ŭ byte ���۸� �����ϰ�, ���ǿ� �°� ���������� �����Ѵ�.
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
		
    	//RecvHead ���� ���������� �����ϰ�, Body �κ��� ���̸� �����Ͽ� �����Ѵ�.
    	if (APPL_CODE.equals("AP15")) {
    		byte[] lenbyte = new byte[4];
    		int    len = 0;
    		
    		System.arraycopy(RecvHead, 11, lenbyte, 0, lenbyte.length);
    		len = Integer.parseInt(new String(lenbyte));
    		
    		//Header�� ���������̹Ƿ�, Body ���̸� ����Ͽ� �����Ѵ�.
    		return len + 10 - RecvHead.length;
    	}
    	if (APPL_CODE.equals("AP16")) {
    		byte[] lenbyte = new byte[4];
    		int    len = 0;
    		
    		System.arraycopy(RecvHead, 0, lenbyte, 0, lenbyte.length);
    		len = Integer.parseInt(new String(lenbyte));
    		
    		//Header�� ���������̹Ƿ�, Body ���̸� ����Ͽ� �����Ѵ�.
    		return len + 10;
    	}
		return 0;
	}
}
