package TCheckServer.Engine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.text.SimpleDateFormat;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import TCheckServer.UserClass.UserFunction;
 
public class ThreadUrlClient extends Thread{ 
	private CommData         COMMDATA = null;
	private UserFunction     userfunction = new UserFunction();
	private int              Default_TimeOut = 30;
	private int              UserSoapResPort = 60003;
	public ThreadUrlClient(CommData commdata)
    { 
    	COMMDATA = commdata;
    	getSystemInfo();
    }
 
	public void run()
	{
		while(!Thread.currentThread().isInterrupted()) {
 
			File[] filelist = new File("./Request/Urlmsg/" + COMMDATA.GetLU_NAME()).listFiles();
			
			if (filelist == null || filelist.length <= 0) {
				setThreadSleep(1000);
				continue;
			}
 			
			for(int i=0;i < filelist.length ;i++){
				try {
					//송신할 전문 Read
					String   fname = filelist[i].getPath();
					File f = new File(fname);
		        	DataInputStream dis = new DataInputStream(new FileInputStream(f));
		            int len  = (int) f.length();
		            byte[] ReadData = new byte[len];
		            dis.readFully(ReadData);
		            dis.close();
		            
		            f.delete();  //거래 전문 삭제
		            
		            if (len <= 10) {
		            	continue;
		            }
		            
		            String   strreaddata = new String(ReadData);
					String[] arrstrreaddata = strreaddata.split("<DATAGUBUN>");
					String[] arrtmp = arrstrreaddata[0].split("\t");
					String   UserPCIP = arrtmp[0];
					String   UserID   = arrtmp[1];
					String   ApplCode = arrtmp[2];
					String   URL      = arrtmp[3];
    
					String[] arrurllist = COMMDATA.GetURL_LIST();
					for(int x=0;x < arrurllist.length ;x++){
						String[] arrtmpsub = arrurllist[x].split("\t");
			 
						if (arrtmpsub[1].equals(URL)) {
							if (arrtmpsub[3].trim().equals("")) {
								SoapSend(UserID, UserPCIP, ApplCode, URL, arrstrreaddata[1]);
							}
							else {
								//EBXml 방식은 방카슈랑스  업무에서 기업은행 Style만 적용하며, 그 외에는 추가 패치 형태로 제공하기로 함.  
				                String   CPAID   = arrtmpsub[3];
				                String   CPAFrom = arrtmpsub[4];
				                String   CPATo   = arrtmpsub[5];
				                String   Service = arrtmpsub[6];
								EBXmlSoapSend(UserID, UserPCIP, ApplCode, URL, CPAID, CPAFrom, CPATo, Service, arrstrreaddata[1]);
							}
							break;
						}
					}
   
				}catch(Exception e) {
					COMMDATA.GetTCheckerLog().WriteLog("E", "UrlManager", e);
				}
			}
 			
		}
		
	}
    private void SoapSend(String pUserID, String pUserPCIP, String pApplCode, String pUrl, String SendData )
    {
		SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
		String reg_dt = formatter.format(new java.util.Date()); //등록 일시
		formatter = new java.text.SimpleDateFormat("HHmmss", java.util.Locale.KOREA);
		String reg_tm = formatter.format(new java.util.Date());  //등록시간 
		formatter = new java.text.SimpleDateFormat("S", java.util.Locale.KOREA);
		String reg_milsec = formatter.format(new java.util.Date());  //등록시간 
		int spos = 0;
		int epos = 0;
		String KindCode = "";
		String TxCode = "";
        try
        {
     		
            //Kindcode 가져오기
            spos = SendData.indexOf("<HDR_DOC_CODE>");
            epos = SendData.indexOf("</HDR_DOC_CODE>");
            if (spos >= 0 && epos > 0) {
            	KindCode = SendData.substring(spos + "<HDR_DOC_CODE>".length(), epos);
            }
  
            //Txcode 가져오기
            spos = SendData.indexOf("<HDR_BIZ_CODE>");
            epos = SendData.indexOf("</HDR_BIZ_CODE>");
            if (spos >= 0 && epos > 0) {
            	TxCode = SendData.substring(spos + "<HDR_BIZ_CODE>".length(), epos);
            }
            
        	//User Function Proc - Start
        	if (userfunction.GetEngineFuncList() != null){
	        	for(int i=0;i < userfunction.GetEngineFuncList().length ;i++){
	        		byte[] mapdata = userfunction.Parsing(userfunction.GetEngineFuncList()[i].getBytes(), pApplCode, KindCode, TxCode);
	        		SendData = SendData.replace(userfunction.GetEngineFuncList()[i], new String(mapdata));
	        	}
        	}
        	if (userfunction.GetUserFuncList() != null){
	        	for(int i=0;i < userfunction.GetUserFuncList().length ;i++){
	        		byte[] mapdata = userfunction.Parsing(userfunction.GetUserFuncList()[i].getBytes(), pApplCode, KindCode, TxCode);
	        		SendData = SendData.replace(userfunction.GetUserFuncList()[i], new String(mapdata));
	        	}
        	}
        	//User Function Proc - End
        	
        	
            //HDR_BAK_DOCSEQ 의 값을 자동으로 셋팅
            spos = SendData.indexOf("<HDR_BAK_DOCSEQ>");
            epos = SendData.indexOf("</HDR_BAK_DOCSEQ>");
            if (spos > 0 && epos > 0) {
            	String old_HDR_DOC_SEQ = "<HDR_BAK_DOCSEQ>" + SendData.substring(spos + 10 + 2, epos) + "</HDR_BAK_DOCSEQ>";
            	String new_HDR_DOC_SEQ = "<HDR_BAK_DOCSEQ>" + reg_tm + "</HDR_BAK_DOCSEQ>";
            	SendData = SendData.replace(old_HDR_DOC_SEQ, new_HDR_DOC_SEQ);
            }
 
            byte[] SendXML = SendData.getBytes("utf-8");
            if (pUrl.indexOf("http://") < 0){
            	pUrl = "http://" + pUrl.trim();
            }
 
            COMMDATA.GetTCheckerLog().WriteLog("D", "UrlManager", pUserID + ":" + pApplCode + ":" + pUrl + ":" + ":Send To Anylink : " + SendData);
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);						
            DocumentBuilder builder = dbf.newDocumentBuilder();
            ByteArrayInputStream bais = new ByteArrayInputStream(SendXML);
            Document ReqDoc = builder.parse( bais );
 
            Element env  = ReqDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soap:Envelope");
            Element body = ReqDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soap:Body");
            Node n = ReqDoc.getFirstChild();
            ReqDoc.removeChild(n);
            ReqDoc.appendChild(env);
            env.appendChild(body);
            body.appendChild(n);
     
            MessageFactory mf = null;
            mf = MessageFactory.newInstance();                
            SOAPMessage msg = mf.createMessage();
            msg.getSOAPPart().setContent(new DOMSource(ReqDoc));
            msg.saveChanges();
 
            SOAPConnectionFactory connectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = connectionFactory.createConnection();
 
            URL endpoint =
                new URL(new URL(pUrl), "",
                        new URLStreamHandler() {
                          @Override
                          protected URLConnection openConnection(URL url) throws IOException {
                            URL target = new URL(url.toString());
                            URLConnection connection = target.openConnection();
                            // Connection settings
                            connection.setConnectTimeout(3000); // 3 sec
                            connection.setReadTimeout(5000);  
                            return(connection);
                          }
                        });
  
            SOAPMessage resmsg = soapConnection.call(msg, endpoint);
            soapConnection.close();
 
            Source src = resmsg.getSOAPPart().getContent();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            DOMResult result = new DOMResult();
            transformer.transform(src, result);
            Document resultDoc = (Document)result.getNode();
 
      	    TransformerFactory transfac = TransformerFactory.newInstance();
      	    Transformer trans = transfac.newTransformer();
      	    trans.setOutputProperty("indent", "yes");
      	   
      	    StringWriter sw = new StringWriter();
      	    StreamResult streamresult = new StreamResult(sw);
      	    DOMSource domsource = new DOMSource(resultDoc);
      	    trans.transform(domsource, streamresult);
      	    
      	    //UserPC에 응답전문 전송
      	    SendResponseToUserPC(pUserID, pUserPCIP, pApplCode, pUrl, sw.toString().getBytes(), sw.toString().getBytes().length);
   
        }
        catch(Exception ex)
        {
        	COMMDATA.GetTCheckerLog().WriteLog("E", "UrlManager", ex);
        	
        	//UserPC에 응답전문 전송
        	String errmsg = "Error:" + ex.getMessage();
        	SendResponseToUserPC(pUserID, pUserPCIP, pApplCode, pUrl, errmsg.getBytes(), errmsg.getBytes().length);
 
        }
 
    }
    private void EBXmlSoapSend(String pUserID, 
    		                   String pUserPCIP, 
    		                   String pApplCode, 
    		                   String pUrl,
    		                   String pCPAID,
    		                   String pCPAFrom,
    		                   String pCPATo,
    		                   String pService,
    		                   String SendData )
    {
    	String EBXML_CPAID = pCPAID;
    	String EBXML_CPAFrom = pCPAFrom;
    	String EBXML_CPATo = pCPATo;
    	String EBXML_SERVICE = pService;
    	String EBXML_FROM_ROLE = "http://www.ibk.co.kr/bancassurance#Bank";
    	String EBXML_TO_ROLE = "http://www.ibk.co.kr/bancassurance#INSR";
    	String EBXML_REQUEST = "@_Request";
    	String EBXML_RESPONSE = "@_Response";
    	String EBXML_TxCode = "";
    	
		SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
		String reg_dt = formatter.format(new java.util.Date()); //등록 일시
		formatter = new java.text.SimpleDateFormat("HHmmss", java.util.Locale.KOREA);
		String reg_tm = formatter.format(new java.util.Date());  //등록시간 
		formatter = new java.text.SimpleDateFormat("S", java.util.Locale.KOREA);
		String reg_milsec = formatter.format(new java.util.Date());  //등록시간 
		int spos = 0;
		int epos = 0;
		String KindCode = "";
		String TxCode = "";
		
        try
        {
        	COMMDATA.GetTCheckerLog().WriteLog("D", "UrlManager", "Send EBXml : Start");
        	
            //Kindcode 가져오기
            spos = SendData.indexOf("<HDR_DOC_CODE>");
            epos = SendData.indexOf("</HDR_DOC_CODE>");
            if (spos >= 0 && epos > 0) {
            	KindCode = SendData.substring(spos + "<HDR_DOC_CODE>".length(), epos);
            }
  
            //Txcode 가져오기
            spos = SendData.indexOf("<HDR_BIZ_CODE>");
            epos = SendData.indexOf("</HDR_BIZ_CODE>");
            if (spos >= 0 && epos > 0) {
            	TxCode = SendData.substring(spos + "<HDR_BIZ_CODE>".length(), epos);
            }
            
        	//User Function Proc - Start
        	if (userfunction.GetEngineFuncList() != null){
	        	for(int i=0;i < userfunction.GetEngineFuncList().length ;i++){
	        		byte[] mapdata = userfunction.Parsing(userfunction.GetEngineFuncList()[i].getBytes(), pApplCode, KindCode, TxCode);
	        		SendData = SendData.replace(userfunction.GetEngineFuncList()[i], new String(mapdata));
	        	}
        	}
        	if (userfunction.GetUserFuncList() != null){
	        	for(int i=0;i < userfunction.GetUserFuncList().length ;i++){
	        		byte[] mapdata = userfunction.Parsing(userfunction.GetUserFuncList()[i].getBytes(), pApplCode, KindCode, TxCode);
	        		SendData = SendData.replace(userfunction.GetUserFuncList()[i], new String(mapdata));
	        	}
        	}
        	//User Function Proc - End
 
            //Txcode 가져오기
            if (TxCode.trim().equals("")) {
            	COMMDATA.GetTCheckerLog().WriteLog("E", "UrlManager", pUserID + ":" + pApplCode + ":" + pUrl + ":" + "전문에서 <HDR_BIZ_CODE> 을 찾을 수 가 없습니다.");
            	
            	//UserPC에 응답전문 전송
            	String errmsg = "Error:" + "전문에서 <HDR_BIZ_CODE> 을 찾을수 가 없습니다.";
            	SendResponseToUserPC(pUserID, pUserPCIP, pApplCode, pUrl, errmsg.getBytes(), errmsg.getBytes().length);
            	return;
            }
          
            EBXML_TxCode = TxCode;
            
            
            
            //해당 업무에 대한 EBXml 정보를 얻어온다. 
			String microTime = String.format("%05d", System.currentTimeMillis() % 1000);
			
			String ConversationId = reg_dt.replace("-", "") + "-" + reg_tm.replace(":","") + "-" + microTime ;
			String MessageId = reg_dt.replace("-", "") + "-" + reg_tm.replace(":","") + "-" + microTime ;
			String RefToMessageId = reg_dt.replace("-", "") + "-" + reg_tm.replace(":","") + "-" + microTime + "@e-iris.com";
		    String Timestamp = reg_dt + "T" + reg_tm + "Z";
		    
		    //한화생명
		    Timestamp = reg_dt.substring(0,4) + "-" + reg_dt.substring(4,6) + "-" + reg_dt.substring(6,8);
		    Timestamp = Timestamp + "T" + reg_tm.substring(0,2) + ":" + reg_tm.substring(2,4) + ":" + reg_tm.substring(4,6) + ".000";
		    
		    //test : MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
            MessageFactory mf = MessageFactory.newInstance();                
            SOAPMessage msg = mf.createMessage();
            SOAPPart sp = msg.getSOAPPart(); 
            SOAPEnvelope se = sp.getEnvelope(); 
            
            sp.setMimeHeader("Content-Transfer-Encoding", "binary"); 
            sp.setContentId("<SOAPPART>");              
            sp.setMimeHeader("Content-Length", "1820"); 
            
            MimeHeaders headers = msg.getMimeHeaders();
            headers.addHeader("SOAPAction", "ebXML");
 
            //schemaLocName은 xsi:schemaLocation 이라는 이름을 나타내는 Name, 또는 QName 객체
            Name schemaLocName = SOAPFactory.newInstance().createName("schemaLocation", "xsi", "http://www.w3.org/2001/XMLSchema-instance"); 
            se.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
            se.addNamespaceDeclaration("eb", "http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd");
            se.addNamespaceDeclaration("xlink", "http://www.w3.org/1999/xlink");
            se.addAttribute(schemaLocName, "http://schema.xmlsoap.org/soap/envelope http://www.oasis-open.org/committees/ebxml-msg/schema/envelope.xsd"); 
            se.addNamespaceDeclaration("SOAP", "http://schemas.xmlsoap.org/soap/envelope/");
           
            SOAPHeader sh = se.getHeader(); 
            SOAPBody sb = se.getBody(); 
 
            SOAPElement el = null;
            SOAPElement elsub = null;
            SOAPElement elsub01 = null;
            SOAPElement elsub02 = null;
            
            sh.addAttribute(schemaLocName,"http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd");
             
            QName qname = sh.createQName("SyncReply", "eb"); 
            el = sh.addChildElement(qname);
            elsub = el.addChildElement(qname);
            qname = sh.createQName("version", "eb"); elsub01 = el.addAttribute(qname, "2.0");
            qname = sh.createQName("mustUnderstand", "SOAP"); elsub01 = el.addAttribute(qname, "1");
 
            qname = sh.createQName("actor", "SOAP"); elsub01 = el.addAttribute(qname, "http://schemas.xmlsoap.org/soap/actor/next");
            
            //MessageHeader
            qname = sh.createQName("MessageHeader", "eb");
            el = sh.addChildElement(qname);
            qname = sh.createQName("mustUnderstand", "SOAP"); el.addAttribute(qname, "1");  //HMKIM
            qname = sh.createQName("id", "eb"); el.addAttribute(qname, "MessageHeader");//HMKIM
            qname = sh.createQName("version", "eb"); el.addAttribute(qname, "2.0");
            
            qname = sh.createQName("From", "eb"); elsub = el.addChildElement(qname);
            qname = sh.createQName("PartyId", "eb"); elsub01 = elsub.addChildElement(qname); elsub01.setTextContent(EBXML_CPAFrom);
            qname = sh.createQName("type", "eb"); elsub02 = elsub01.addAttribute(qname, "BANK_CD");
            
            
            qname = sh.createQName("Role", "eb"); elsub01 = elsub.addChildElement(qname); elsub01.setTextContent(EBXML_FROM_ROLE);
          
            qname = sh.createQName("To", "eb"); elsub = el.addChildElement(qname);
            qname = sh.createQName("PartyId", "eb"); elsub01 = elsub.addChildElement(qname); elsub01.setTextContent(EBXML_CPATo);
            qname = sh.createQName("type", "eb"); elsub02 = elsub01.addAttribute(qname, "INSR_CD");
            
            qname = sh.createQName("Role", "eb"); elsub01 = elsub.addChildElement(qname); elsub01.setTextContent(EBXML_TO_ROLE);
            
            qname = sh.createQName("CPAId", "eb"); elsub = el.addChildElement(qname); elsub.setTextContent(EBXML_CPAID);
            qname = sh.createQName("ConversationId", "eb"); elsub = el.addChildElement(qname); elsub.setTextContent(ConversationId);
            qname = sh.createQName("Service", "eb"); elsub = el.addChildElement(qname); elsub.setTextContent(EBXML_SERVICE);
            qname = sh.createQName("Action", "eb"); elsub = el.addChildElement(qname); elsub.setTextContent(EBXML_REQUEST.replace("@", EBXML_TxCode));
           
            qname = sh.createQName("MessageData", "eb"); elsub = el.addChildElement(qname);
            qname = sh.createQName("MessageId", "eb"); elsub01 = elsub.addChildElement(qname); elsub01.setTextContent(MessageId);
            qname = sh.createQName("Timestamp", "eb"); elsub01 = elsub.addChildElement(qname); elsub01.setTextContent(Timestamp);
            
            //imsi-start
            //qname = sh.createQName("DuplicateElimination", "eb"); elsub = el.addChildElement(qname);
            //qname = sh.createQName("Description", "eb"); elsub = el.addChildElement(qname); elsub.setTextContent("This is a ebXML message.");
            //qname = sh.createQName("lang", "xml"); elsub.addAttribute(qname, "en-US");
            //qname = sh.createQName("xml", "xmlns"); elsub.addAttribute(qname, "http://www.w3.org/XML/1998/namespace");
            //imsi-end
      
            //Body-start
            sb.addAttribute(schemaLocName,"http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd");
            
            qname = sb.createQName("Manifest", "eb"); el = sb.addChildElement(qname);
            qname = sb.createQName("id", "eb"); el.addAttribute(qname, "Manifest");  
            qname = sb.createQName("version", "eb"); el.addAttribute(qname, "2.0");
            
            qname = sb.createQName("Reference", "eb"); elsub = el.addChildElement(qname);
            qname = sb.createQName("id", "eb"); elsub.addAttribute(qname, "Reference-0");  
            qname = sb.createQName("type", "xlink"); elsub.addAttribute(qname, "simple"); 
            qname = sb.createQName("href", "xlink"); elsub.addAttribute(qname, "cid:Payload-0"); 
            
            //Body-End
 
            //URLPoint 및 데이타를 얻어온다.
            byte[] SendXML = SendData.getBytes("utf-8");
            if (pUrl.indexOf("http://") < 0){
            	pUrl = "http://" + pUrl.trim();
            }
  
            //HDR_BAK_DOCSEQ 의 값을 자동으로 셋팅
            spos = SendData.indexOf("<HDR_BAK_DOCSEQ>");
            epos = SendData.indexOf("</HDR_BAK_DOCSEQ>");
            if (spos > 0 && epos > 0) {
            	String old_HDR_DOC_SEQ = "<HDR_BAK_DOCSEQ>" + SendData.substring(spos + 10 + 2, epos) + "</HDR_BAK_DOCSEQ>";
            	String new_HDR_DOC_SEQ = "<HDR_BAK_DOCSEQ>" + reg_tm + "</HDR_BAK_DOCSEQ>";
            	SendData = SendData.replace(old_HDR_DOC_SEQ, new_HDR_DOC_SEQ);
            }
            
            COMMDATA.GetTCheckerLog().WriteLog("D", "UrlManager", "Send To Anylink : " + SendData);
            
            AttachmentPart attachment = msg.createAttachmentPart(); 
            attachment.setRawContent(new ByteArrayInputStream(SendXML),"application/octet-stream");
            attachment.setContentId("<Payload-0>");
            attachment.setMimeHeader("Content-Transfer-Encoding", "binary"); //HMKIM
            attachment.setMimeHeader("Content-Length", "" +  SendXML.length  ); //HMKIM
            msg.addAttachmentPart(attachment); 
 
            msg.getSOAPPart().getEnvelope().setPrefix("SOAP");
            msg.getSOAPHeader().setPrefix("SOAP");
            msg.getSOAPBody().setPrefix("SOAP");
            msg.saveChanges();
 
            //전문전송 요청
            SOAPConnectionFactory connectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = connectionFactory.createConnection();
            SOAPMessage resmsg = soapConnection.call(msg, pUrl);
            soapConnection.close();

            //수신 응답전문 parsing
            boolean isfind = false;
            String responsedata = "";
        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        	resmsg.writeTo(baos);
            String[] arrRecvData = baos.toString("utf-8").split("\n");
            COMMDATA.GetTCheckerLog().WriteLog("D", "UrlManager", "Receive From Anylink FullData:" + baos.toString("utf-8"));
            baos.close();
 
            for(int i=0;i < arrRecvData.length;i++){
             
            	if(arrRecvData[i].indexOf("<Bancassurance>") >= 0) {
            		responsedata = arrRecvData[i] + "\n";
            		isfind = true;
            	}
            	else if(arrRecvData[i].indexOf("</Bancassurance>") >= 0) {
            		responsedata = responsedata + arrRecvData[i] + "\n";
            		break;
            	}
            	else {
            		if (isfind) responsedata = responsedata + arrRecvData[i] + "\n";
            	}
            }
            
      	    //UserPC에 응답전문 전송
            SendResponseToUserPC(pUserID, pUserPCIP, pApplCode, pUrl, responsedata.getBytes(), responsedata.getBytes().length);
      	 
        }
        catch(Exception ex)
        {
        	COMMDATA.GetTCheckerLog().WriteLog("E", "UrlManager", ex);
        	
        	//UserPC에 응답전문 전송
        	String errmsg = "Error:" + ex.getMessage();
        	SendResponseToUserPC(pUserID, pUserPCIP, pApplCode, pUrl, errmsg.getBytes(), errmsg.getBytes().length);

        }
 
    }
    private void SendResponseToUserPC(String pUserID, String pUserPCIP, String pApplCode, String UrlInfo, byte[] resmsg, int ressize)
    {
    	Socket user_client = new Socket();
    	DataOutputStream user_dos = null;
    	try {
    		byte[] tmprecv = new byte[ressize];
    		System.arraycopy(resmsg,0,tmprecv,0,ressize);
    		
    		COMMDATA.GetTCheckerLog().WriteLog("D", "UrlManager", pUserID + ":" + pApplCode + ":" + UrlInfo + ":" + "RecvData From Anylink : " + new String(tmprecv));
    		
    		//Response File Write Start
			String   fname = "./Response/Tcpmsg/" + pUserPCIP + "/message.dat"  ;
			 
			//Directory Check
			File dir1 = new File("./Response");
			if (!dir1.exists()) dir1.mkdir();
			
			File dir2 = new File("./Response/Tcpmsg");
			if (!dir2.exists()) dir2.mkdir();
			
			File dir3 = new File("./Response/Tcpmsg/" + pUserPCIP);
			if (!dir3.exists()) dir3.mkdir();
			
			DataOutputStream out1 = new DataOutputStream(new FileOutputStream(new File(fname)));
			out1.write(resmsg);
			out1.close();
			COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", pUserID + ":" + pApplCode + ":" + UrlInfo + ":" + "응답내역을 저장하였습니다.[" + pUserPCIP + "]");
			
			//Response File Write End
			
    		user_client.connect(new InetSocketAddress(pUserPCIP, UserSoapResPort), 3000);  //3초 기다림
	 		user_dos = new DataOutputStream(user_client.getOutputStream());
	 		
 
        	String lenfmt = String.format("%08d", ressize);
        	user_dos.write(lenfmt.getBytes(), 0 , lenfmt.getBytes().length);
        	user_dos.write("RESPONSEUL".getBytes(), 0 , "RESPONSEUL".getBytes().length);
        	user_dos.write(resmsg, 0, ressize);
	        
            user_dos.flush();	
            setThreadSleep(10);
            user_client.close();
            
            COMMDATA.GetTCheckerLog().WriteLog("D", "UrlManager", pUserID + ":" + pApplCode + ":" + UrlInfo + ":" + "사용자에게 응답전문을 전송 하였습니다.[" + pUserPCIP + ":" + UserSoapResPort + "]");
  
    	}catch(Exception e) {
    		try{if(user_client != null) user_client.close();}catch(Exception e1){}
    		COMMDATA.GetTCheckerLog().WriteLog("W", "UrlManager", pUserID + ":" + pApplCode + ":" + UrlInfo + ":" + "사용자에게 응답전문을 전송하지 못했습니다.[" + pUserPCIP + ":" + UserSoapResPort + "]");
    		return;
    	}
    }
 
    public void setThreadSleep(int time)
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
	        String managerport = properties.getProperty("MANAGER_PORT", "60000");
	        Default_TimeOut    = Integer.parseInt(default_timeout.trim());
 
	        UserSoapResPort = Integer.parseInt(managerport) + 3;
		} catch (Exception e) {
			return ;
		}
    }
}
