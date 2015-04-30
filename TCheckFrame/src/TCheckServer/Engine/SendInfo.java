package TCheckServer.Engine;

import java.beans.PropertyChangeSupport;

public class SendInfo {
	private String UserID;
	private String UserIP;
	private String SendMsg;
	private String ApplCode;
	private String KindCode;
	private String TxCode;
	private long   SendTime;
	
	public SendInfo() { }
  
	public String getUserID()     {return UserID;}
	public String getUserIP()     {return UserIP;}
	public String getSendMsg()    {return SendMsg;}
	public long   getSendTime()   {return SendTime;}
 
	public String getApplCode()     {return ApplCode;}
	public String getKindCode()    {return KindCode;}
	public String getTxCode()     {return TxCode;}
	
	
	public void setUserID(String UserID) { this.UserID = UserID; }
	public void setUserIP(String UserIP) { this.UserIP = UserIP; }
	public void setSendMsg(String SendMsg) { this.SendMsg = SendMsg; }
	public void setSendTime(long SendTime) { this.SendTime = SendTime; }
	public void setApplCode(String ApplCode) { this.ApplCode = ApplCode; }
	public void setKindCode(String KindCode) { this.KindCode = KindCode; }
	public void setTxCode(String TxCode) { this.TxCode = TxCode; }
	
}