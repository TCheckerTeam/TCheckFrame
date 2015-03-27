package TCheckServer.Engine;

public class JMapperCallTest {

	public void jmappercall(String InstCode, String pApplCode, String pKindCode, String pTxCode, String pSendData)
	{
		//WebtCall-Start
		WebtSender jc = new WebtSender();
 		byte[] msg;
		byte[] rcvmsg;
		byte[] rcvdata;
		msg = jc.makeHeader(InstCode, pApplCode, pKindCode, pTxCode, pSendData.getBytes());  
		rcvmsg = jc.MapperCall("192.168.37.30", 8888, msg, "JMAPPER2");

	}
	public static void main(String[] args) {
		String InstCode = "SIM";
		String pApplCode = "D005";
		String pKindCode = "0200";
		String pTxCode = "055100";
		String pSendData = "";
		
        String body = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
 
        String head = "TRXID0000SYS" + String.format("%05d", 160 + 11 + body.getBytes().length) + "T003010050200055100S00000020150321154345BANK0000INS00000201503210000hmkim          BANK01                        INS01                         ";
   
        pSendData = head + body;
        
		WebtSender jc = new WebtSender();
 		byte[] msg;
		byte[] rcvmsg;
		byte[] rcvdata;
		msg = jc.makeHeader(InstCode, pApplCode, pKindCode, pTxCode, pSendData.getBytes());  
		rcvmsg = jc.MapperCall("192.168.37.30", 8888, msg, "JMAPPER2");

		System.out.println("rcvmsg=[" + new String(rcvmsg));
		
	}

}
