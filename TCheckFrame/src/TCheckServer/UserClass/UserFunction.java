package TCheckServer.UserClass;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
 
public class UserFunction {
	private byte[] FUNCTION  = null; 
	private byte[] RESULT    = null;
	private String APPL_CODE = "";  
	private String KIND_CODE = "";  
	private String TX_CODE   = ""; 
    private String[] EngineFuncList= null;
    private String[] UserFuncList= null;
    public  String[] GetEngineFuncList(){return this.EngineFuncList;}
    public  String[] GetUserFuncList(){return this.UserFuncList;}
    private String function = "";
    /*----------------------- System Define Init Logic ---------------------------*/
    public UserFunction()
    {
    	//반드시 해당 함수가 존재해야 함.
    }

	/*----------------------- System Define Parsing Logic ---------------------------*/
	public byte[] Parsing(byte[] FUNCTION, String APPL_CODE, String KIND_CODE, String TX_CODE)
	{
    	this.FUNCTION = FUNCTION;
    	this.APPL_CODE = APPL_CODE;
    	this.KIND_CODE = KIND_CODE;
    	this.TX_CODE = TX_CODE;
    	this.RESULT = FUNCTION;
    	this.function = new String(FUNCTION).toUpperCase();
 
		/* Default User Function Define - Start */
		this.EngineFuncList = new String[]{
				 "DATE(YYYYMMDD)"
				,"DATE(YYYYMM)"
				,"DATE(YYYY)"
				,"DATE(MM)"
				,"DATE(DD)"
				,"DATE(MMDD)"
				,"DATETIME(YYYYMMDDHHMMSS)"
				,"TIME(HHMMSS)"
				,"TIME(HHMM)"
				,"TIME(MMSS)"
				,"TIME(HH)"
				,"TIME(MM)"
				,"TIME(SS)"
				,"HEXTOBYTE@"
				,"BYTETOHEX@"};


		// Date Definition
		if (this.function.equals("DATE(YYYYMMDD)")) {
			SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
			String reg_dt = formatter.format(new java.util.Date()); //등록 일시
            return reg_dt.getBytes();
		}
		if (this.function.equals("DATE(YYYYMM)")) {
			SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
			String reg_dt = formatter.format(new java.util.Date()); //등록 일시
            return reg_dt.substring(0,6).getBytes();

		}
		if (this.function.equals("DATE(YYYY)")) {
			SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
			String reg_dt = formatter.format(new java.util.Date()); //등록 일시
            return reg_dt.substring(0,4).getBytes();
		}
		if (this.function.equals("DATE(MM)")) {
			SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
			String reg_dt = formatter.format(new java.util.Date()); //등록 일시
            return reg_dt.substring(4,6).getBytes();
		}
		if (this.function.equals("DATE(DD)")) {
			SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
			String reg_dt = formatter.format(new java.util.Date()); //등록 일시
            return reg_dt.substring(6,8).getBytes();
		}
		if (this.function.equals("DATE(MMDD)")) {
			SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
			String reg_dt = formatter.format(new java.util.Date()); //등록 일시
            return reg_dt.substring(4,8).getBytes();
		}
		
		// Time Definition
		if (this.function.equals("TIME(HHMMSS)")) {
			SimpleDateFormat formatter = new java.text.SimpleDateFormat("HHmmss", java.util.Locale.KOREA);
			String reg_tm = formatter.format(new java.util.Date());  //등록시간 
			return reg_tm.substring(0,6).getBytes();
		}
		if (this.function.equals("TIME(HHMM)")) {
			SimpleDateFormat formatter = new java.text.SimpleDateFormat("HHmmss", java.util.Locale.KOREA);
			String reg_tm = formatter.format(new java.util.Date());  //등록시간 
			return reg_tm.substring(0,4).getBytes();
		}
		if (this.function.equals("TIME(MMSS)")) {
			SimpleDateFormat formatter = new java.text.SimpleDateFormat("HHmmss", java.util.Locale.KOREA);
			String reg_tm = formatter.format(new java.util.Date());  //등록시간 
			return reg_tm.substring(2,6).getBytes();
		}
		if (this.function.equals("TIME(HH)")) {
			SimpleDateFormat formatter = new java.text.SimpleDateFormat("HHmmss", java.util.Locale.KOREA);
			String reg_tm = formatter.format(new java.util.Date());  //등록시간 
			return reg_tm.substring(0,2).getBytes();
		}
		if (this.function.equals("TIME(MM)")) {
			SimpleDateFormat formatter = new java.text.SimpleDateFormat("HHmmss", java.util.Locale.KOREA);
			String reg_tm = formatter.format(new java.util.Date());  //등록시간 
			return reg_tm.substring(2,4).getBytes();
		}
		if (this.function.equals("TIME(SS)")) {
			SimpleDateFormat formatter = new java.text.SimpleDateFormat("HHmmss", java.util.Locale.KOREA);
			String reg_tm = formatter.format(new java.util.Date());  //등록시간 
			return reg_tm.substring(4,6).getBytes();
		}
		if (this.function.equals("DATETIME(YYYYMMDDHHMMSS)")) {
			SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
			String reg_dt = formatter.format(new java.util.Date()); //등록 일시
    
			formatter = new java.text.SimpleDateFormat("HHmmss", java.util.Locale.KOREA);
			String reg_tm = formatter.format(new java.util.Date());  //등록시간 
			return (reg_dt + reg_tm).substring(0,14).getBytes();
		}
		if (this.function.indexOf("HEXTOBYTE@") >= 0) {
			byte[] tmpdata = this.function.replace("HEXTOBYTE@","").getBytes();
			byte[] tmpresult = new byte[tmpdata.length /2 ];
			int    tmpbyte1 = 0, tmpbyte2 = 0 ;
			try{
			    for(int i=0;i < tmpresult.length;i++){
			    	tmpresult[i] = 0x00;
			    	if (tmpdata[i * 2 + 0] >= '0' && tmpdata[i * 2 + 0] <= '9') tmpbyte1 = (tmpdata[i * 2 + 0] - 33);
			    	else if (tmpdata[i * 2 + 0] >= 'A' && tmpdata[i * 2 + 0] <= 'F') tmpbyte1 = (tmpdata[i * 2 + 0] - 65);
			    	 
			    	tmpresult[i] = (byte)((tmpresult[i] & 0x0f) << 4);
			    	
			    	if (tmpdata[i * 2 + 1] >= '0' && tmpdata[i * 2 + 1] <= '9') tmpbyte2 = (tmpdata[i * 2 + 0] - 33);
			    	else if (tmpdata[i * 2 + 1] >= 'A' && tmpdata[i * 2 + 1] <= 'F') tmpbyte2 = (tmpdata[i * 2 + 0] - 65);
			    	
			    	tmpresult[i] = (byte)((tmpbyte1 & 0x0f) << 4 + tmpbyte2);
			    }
			    return tmpresult;
			}catch(Exception e){}
			return this.RESULT;
		}
		if (this.function.indexOf("BYTETOHEX@") >= 0) {
			byte[] tmpdata = this.function.replace("BYTETOHEX@","").getBytes();
			byte[] tmpresult = new byte[tmpdata.length * 2 ];
			String hexstr = "";
			for(int i=0;i < tmpdata.length;i++){
				hexstr = String.format("%02X", tmpdata[i]);
				System.arraycopy(hexstr.getBytes(), 0, tmpresult, i * 2, 2);
			}
			return tmpresult;
		}
		/* Default User Function Define - End */
 
		byte[] resultBytes = UserModifyFunction();
 
		if (resultBytes == null) return this.RESULT;
 
        return resultBytes;
	}
 
	public byte[] UserModifyFunction()
	{
		this.UserFuncList = new String[]{
				 "SAMPLEFUNCTION1()"
				,"SAMPLEFUNCTION2()"};
		
		if (this.function.equals("SAMPLEFUNCTION1()")) {
 
			return "ABC1".getBytes();
		}
		if (this.function.equals("SAMPLEFUNCTION2()")) {
 
			return "ABC2".getBytes();
		}
		return null;
	}
 
	private void WriteLog(String mstr)
	{
		try{
			SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
			String reg_dt = formatter.format(new java.util.Date()); //등록 일시
			formatter = new java.text.SimpleDateFormat("[HH:mm:ss]", java.util.Locale.KOREA);
			String reg_tm = formatter.format(new java.util.Date());  //등록시간 
			
			FileWriter out = new FileWriter("./Log/UserFunction_" + reg_dt + ".log", true);
			PrintWriter log  = new PrintWriter(out,true);
			log.println(mstr);
			out.close();
			log.close();
	 
		}catch(Exception e){}
	}
}
