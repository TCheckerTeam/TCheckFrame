package TCheckServer.UserClass;

import java.text.SimpleDateFormat;

public class UserIODefine {
	private String LU_NAME   = ""; 
	private String RECVDATA  = "";
	private String APPL_CODE = "";  
	private String KIND_CODE = "";  
	private String TX_CODE   = ""; 
	private String RESULT    = "";
  
	/*----------------------- System Define Init Logic ---------------------------*/
	public UserIODefine()
	{
		//반드시 해당 함수가 존재해야 함.
	}
	/*----------------------- User Define Logic ---------------------------*/
	public boolean UserModifyDefine(String LU_NAME, String APPL_CODE, String KIND_CODE, String TX_CODE, String RECVDATA)
	{
    	this.LU_NAME = LU_NAME;
    	this.APPL_CODE = APPL_CODE;
    	this.KIND_CODE = KIND_CODE;
    	this.TX_CODE = TX_CODE;
    	this.RECVDATA = RECVDATA;
 
    	//OutBound 거래일 경우 즉, Anylink 로 응답을 줘야 하는 경우에는 True 를 리턴한다.
    	
    	
		return false;
	}
 
}
