package TCheckServer.Engine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import TCheckServer.UserClass.UserFunction;
 
public class ThreadUrlServer extends Thread{ 
	private CommData         COMMDATA = null;
	private UserFunction     userfunction = new UserFunction();
	private Socket           client = null;
	public ThreadUrlServer(CommData commdata, Socket pSocket)
    { 
    	COMMDATA = commdata;
    	client   = pSocket;

    }
  
	public void run()
	{
        DataOutputStream dos       = null;
        DataInputStream  dis       = null;
        int              recvlen   = 0;
        byte[]           RecvBytes = new byte[100000];
        try{ 
            dos = new DataOutputStream(client.getOutputStream());
            dis = new DataInputStream(client.getInputStream());

            //데이타수신
            try {
                recvlen = 0;
                client.setSoTimeout(10000);
                for(int i = 0 ; i < 100000 ;i++){
                    RecvBytes[i] = dis.readByte();
                    client.setSoTimeout(100);
                    recvlen++;
                }
            }catch(Exception e1){}
            
        }catch(IOException e2){
        	COMMDATA.GetTCheckerLog().WriteLog("E", "UrlManager", e2);
            setThreadSleep(100);
            try{ client.close(); }catch(Exception e){}
            return;
        } 
        
        
    	String UrlInfo = Proc_getUrlInfo(RecvBytes);
    	String[] arrUrlInfo = UrlInfo.split("\t");
    	String pApplCode = arrUrlInfo[0];
    	String pEBXml = arrUrlInfo[1];
    	String pUrl = arrUrlInfo[2];
 
		if (pEBXml.equals("EBXML")) {
			COMMDATA.GetTCheckerLog().WriteLog("D", "UrlManager", "SERVER:" + pApplCode + ":" + pUrl + ":Recv From Anylink:\n" + new String(RecvBytes));
			EBXmlProc(pApplCode, recvlen, RecvBytes);
		}
		else {
			COMMDATA.GetTCheckerLog().WriteLog("D", "UrlManager", "SERVER:" + pApplCode + ":" + pUrl + ":Recv From Anylink:\n" + new String(RecvBytes));
			SoapProc(pApplCode, recvlen, RecvBytes);
		}
 
	}
    private void SoapProc(String pApplCode, int recvlen, byte[] RecvBytes)
    {
        DataOutputStream dos       = null;
        String           DataStart = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body>" ;
        String           DataEnd   = "</soap:Body></soap:Envelope>" ;
        String           KindCode  = "";
        String           TxCode    = "";
        int              spos      = 0;
        int              epos      = 0;
        try{ 

            byte[] tmpbyte = new byte[recvlen];
            System.arraycopy(RecvBytes, 0, tmpbyte, 0, tmpbyte.length );
            String strRecvData = new String(tmpbyte);
            COMMDATA.GetTCheckerLog().WriteLog("D", "UrlManager", "SERVER:" + pApplCode + ":NONE:RecvData From Anylink : [" + strRecvData + "]");
    
            //Kindcode 가져오기
            spos = strRecvData.indexOf("<HDR_DOC_CODE>");
            epos = strRecvData.indexOf("</HDR_DOC_CODE>");
            if (spos <= 0 || epos <= 0) {
            	COMMDATA.GetTCheckerLog().WriteLog("E", "UrlManager", "SERVER:" + pApplCode + ":NONE:전문에서 <HDR_DOC_CODE> 을 찾을 수 가 없습니다.");
            	try{ client.close(); }catch(Exception e){}
            	return;
            }
            else {
            	KindCode = strRecvData.substring(spos + "<HDR_DOC_CODE>".length(), epos);
            }
            COMMDATA.GetTCheckerLog().WriteLog("D", "UrlManager", "SERVER:" + pApplCode + ":NONE:KindCode : " + KindCode + "");
            
            //Txcode 가져오기
            spos = strRecvData.indexOf("<HDR_BIZ_CODE>");
            epos = strRecvData.indexOf("</HDR_BIZ_CODE>");
            if (spos <= 0 || epos <= 0) {
            	COMMDATA.GetTCheckerLog().WriteLog("E", "UrlManager", "SERVER:" + pApplCode + ":NONE:전문에서 <HDR_BIZ_CODE> 을 찾을 수 가 없습니다.");
            	try{ client.close(); }catch(Exception e){}
            	return;
            }
            else {
            	TxCode = strRecvData.substring(spos + "<HDR_BIZ_CODE>".length(), epos);
            }
            COMMDATA.GetTCheckerLog().WriteLog("D", "UrlManager", "SERVER:" + pApplCode + ":NONE:TxCode : " + TxCode + "");
 
            //데이타송신
            String fname = "./Response/Urlmap/" + pApplCode + "_" + KindCode + "_" + TxCode;
            File f = new File(fname);
            if (f.exists()) {
                DataInputStream fdis = new DataInputStream(new FileInputStream(f));
                int len  = (int) f.length();
                byte[] ReadData = new byte[len];
                fdis.readFully(ReadData);
                fdis.close();

                String SOAPXML = new String(ReadData); 
                
            	//User Function Proc - Start
            	if (userfunction.GetEngineFuncList() != null){
    	        	for(int i=0;i < userfunction.GetEngineFuncList().length ;i++){
    	        		byte[] mapdata = userfunction.Parsing(userfunction.GetEngineFuncList()[i].getBytes(), pApplCode, KindCode, TxCode);
    	        		SOAPXML = SOAPXML.replace(userfunction.GetEngineFuncList()[i], new String(mapdata));
    	        	}
            	}
            	if (userfunction.GetUserFuncList() != null){
    	        	for(int i=0;i < userfunction.GetUserFuncList().length ;i++){
    	        		byte[] mapdata = userfunction.Parsing(userfunction.GetUserFuncList()[i].getBytes(), pApplCode, KindCode, TxCode);
    	        		SOAPXML = SOAPXML.replace(userfunction.GetUserFuncList()[i], new String(mapdata));
    	        	}
            	}
            	//User Function Proc - End
            	
            	dos = new DataOutputStream(client.getOutputStream());
                dos.writeBytes("HTTP/1.0 200 OK\r\n");
                dos.writeBytes("Content-Type: text/xml\r\n");
                dos.writeBytes("Content-Length: " + "100000" + "\r\n\r\n");
                dos.writeBytes(new String(SOAPXML.getBytes(),"utf-8"));

                dos.flush();
                client.setSoTimeout(10);
                
                COMMDATA.GetTCheckerLog().WriteLog("D", "UrlManager", "SERVER:" + pApplCode + ":NONE:Send To Anylink : [" + SOAPXML + "]");
       
            }
            client.close();

        }catch(IOException e2){
        	COMMDATA.GetTCheckerLog().WriteLog("E", "UrlManager", e2);
            setThreadSleep(100);
            try{ client.close(); }catch(Exception e){}
        } 
 
    }
    private void EBXmlProc(String pApplCode, int recvlen, byte[] RecvBytes)
    {
    	String EBXML_CPAID = COMMDATA.GetCPAID();
    	String EBXML_CPAFrom = COMMDATA.GetCPAFrom();
    	String EBXML_CPATo = COMMDATA.GetCPATo();
    	String EBXML_SERVICE = COMMDATA.GetCPASERVCE();
    	String EBXML_FROM_ROLE = "http://www.ibk.co.kr/bancassurance#INSR";
    	String EBXML_TO_ROLE = "http://www.ibk.co.kr/bancassurance#Bank";
    	String EBXML_REQUEST = "@_Request";
    	String EBXML_RESPONSE = "@_Response";
  	    String SoapDataStart = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<Bancassurance>\n" ;
 	    String SoapDataEnd = "\n</Bancassurance>\n" ;
 	   
        DataOutputStream dos        = null;
        String           CommHeader = "";
        String           strRecvData= "";
        int              spos       = 0;
        int              epos       = 0;
        
        try{ 
  
        	int search_idx = 0;
    	    String ReadStr = new String(RecvBytes);
    	    String[] arrReadStr = ReadStr.split("\n");
    	    for(int i = 0;i < arrReadStr.length ;i++){
    		   CommHeader = CommHeader + arrReadStr[i] + "\n";
    		   if (arrReadStr[i].trim().equals("")) {
    			   search_idx = i;
    			   break;
    		   }

    	    }
    	    for(int i = search_idx + 1;i < arrReadStr.length ;i++){
    	    	strRecvData = strRecvData + arrReadStr[i] + "\n";
    	    }
    	    
    	    COMMDATA.GetTCheckerLog().WriteLog("D", "UrlManager", "SERVER:" + pApplCode + ":NONE:RecvData From Anylink : [" + strRecvData + "]");
    	    
    	    
        	//Extract ConversationId 	   
      	    String ConversationId = GetParsingName(pApplCode, strRecvData, "ConversationId>");
      	    if (ConversationId.equals("")) return;
 
        	//Extract MessageId 	   
      	    String MessageId = GetParsingName(pApplCode, strRecvData, "MessageId>");
      	    if (MessageId.equals("")) return;
      	    
        	//Extract Timestamp 	   
      	    String Timestamp = GetParsingName(pApplCode, strRecvData, "Timestamp>");
      	    if (Timestamp.equals("")) return;
      	    
        	//Extract Service : urn:bancassurance
      	    String Service = GetParsingName(pApplCode, strRecvData, "Service>");
      	    if (Service.equals("")) return;
      	    
        	//Extract RefToMessageId 	   
      	    String RefToMessageId = MessageId;
      	    
        	//KindCode	   
      	    String KindCode = GetParsingName(pApplCode, strRecvData, "HDR_DOC_CODE>");
      	    if (KindCode.equals("")) return;
 
        	//TxCode	   
      	    String TxCode = GetParsingName(pApplCode, strRecvData, "HDR_BIZ_CODE>");
      	    if (TxCode.equals("")) return;
 
            //EBXmal 전문 조립
 
            MessageFactory mf = MessageFactory.newInstance();                
            SOAPMessage resmsg = mf.createMessage();
            SOAPPart sp = resmsg.getSOAPPart(); 
            SOAPEnvelope se = sp.getEnvelope(); 
            sp.setContentId("<SOAPPART>");               
            MimeHeaders headers = resmsg.getMimeHeaders();
            headers.addHeader("SOAPAction", "ebXML");
            headers.addHeader("Content-Transfer-Encoding", "binary");
 
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
            qname = sh.createQName("id", "SOAP"); elsub01 = el.addAttribute(qname, "SyncReply-ID");
            qname = sh.createQName("actor", "SOAP"); elsub01 = el.addAttribute(qname, "http://schemas.xmlsoap.org/soap/actor/next");
             
            //MessageHeader
            qname = sh.createQName("MessageHeader", "eb");
            el = sh.addChildElement(qname);
            qname = sh.createQName("mustUnderstand", "SOAP"); el.addAttribute(qname, "1");  //HMKIM
            qname = sh.createQName("id", "eb"); el.addAttribute(qname, "MessageHeader");//HMKIM
            qname = sh.createQName("version", "eb"); el.addAttribute(qname, "2.0");
            
            qname = sh.createQName("From", "eb"); elsub = el.addChildElement(qname);
            qname = sh.createQName("PartyId", "eb"); elsub01 = elsub.addChildElement(qname); elsub01.setTextContent(EBXML_CPATo);
            qname = sh.createQName("type", "eb"); elsub02 = elsub01.addAttribute(qname, "BANK_CD");
            
            qname = sh.createQName("Role", "eb"); elsub01 = elsub.addChildElement(qname); elsub01.setTextContent(EBXML_TO_ROLE);
            
            qname = sh.createQName("To", "eb"); elsub = el.addChildElement(qname);
            qname = sh.createQName("PartyId", "eb"); elsub01 = elsub.addChildElement(qname); elsub01.setTextContent(EBXML_CPAFrom);
            qname = sh.createQName("type", "eb"); elsub02 = elsub01.addAttribute(qname, "INSR_CD");
            
            qname = sh.createQName("Role", "eb"); elsub01 = elsub.addChildElement(qname); elsub01.setTextContent(EBXML_FROM_ROLE);

            qname = sh.createQName("CPAId", "eb"); elsub = el.addChildElement(qname); elsub.setTextContent(EBXML_CPAID);
            qname = sh.createQName("ConversationId", "eb"); elsub = el.addChildElement(qname); elsub.setTextContent(ConversationId);
            qname = sh.createQName("Service", "eb"); elsub = el.addChildElement(qname); elsub.setTextContent(EBXML_SERVICE);
            qname = sh.createQName("Action", "eb"); elsub = el.addChildElement(qname); elsub.setTextContent(EBXML_RESPONSE.replace("@", TxCode));

            qname = sh.createQName("MessageData", "eb"); elsub = el.addChildElement(qname);
            qname = sh.createQName("MessageId", "eb"); elsub01 = elsub.addChildElement(qname); elsub01.setTextContent(MessageId);
            qname = sh.createQName("Timestamp", "eb"); elsub01 = elsub.addChildElement(qname); elsub01.setTextContent(Timestamp);
            qname = sh.createQName("RefToMessageId", "eb"); elsub01 = elsub.addChildElement(qname); elsub01.setTextContent(RefToMessageId);

            //Body-start
            sb.addAttribute(schemaLocName,"http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd");
            
            qname = sb.createQName("Manifest", "eb"); el = sb.addChildElement(qname);
            qname = sb.createQName("id", "eb"); el.addAttribute(qname, "Manifest");  
            qname = sb.createQName("version", "eb"); el.addAttribute(qname, "2.0");
            
            qname = sb.createQName("Reference", "eb"); elsub = el.addChildElement(qname);
            qname = sb.createQName("id", "eb"); elsub.addAttribute(qname, "Reference-ID0");  
            qname = sb.createQName("type", "xlink"); elsub.addAttribute(qname, "simple"); 
            qname = sb.createQName("href", "xlink"); elsub.addAttribute(qname, "cid:ebXML-Payload-0"); 
            
            //Body-End

      	    String fname = "./Response/Urlmap/" + pApplCode + "_" + KindCode + "_" + TxCode;
      	    String SOAPXML = SoapDataStart + GetFileContents(fname) + SoapDataEnd;
      	    
        	//User Function Proc - Start
        	if (userfunction.GetEngineFuncList() != null){
	        	for(int i=0;i < userfunction.GetEngineFuncList().length ;i++){
	        		byte[] mapdata = userfunction.Parsing(userfunction.GetEngineFuncList()[i].getBytes(), pApplCode, KindCode, TxCode);
	        		SOAPXML = SOAPXML.replace(userfunction.GetEngineFuncList()[i], new String(mapdata));
	        	}
        	}
        	if (userfunction.GetUserFuncList() != null){
	        	for(int i=0;i < userfunction.GetUserFuncList().length ;i++){
	        		byte[] mapdata = userfunction.Parsing(userfunction.GetUserFuncList()[i].getBytes(), pApplCode, KindCode, TxCode);
	        		SOAPXML = SOAPXML.replace(userfunction.GetUserFuncList()[i], new String(mapdata));
	        	}
        	}
        	//User Function Proc - End
        	
 
            AttachmentPart attachment = resmsg.createAttachmentPart(); 
            attachment.setRawContent(new ByteArrayInputStream(SOAPXML.getBytes()),"binary");
            attachment.setContentId("<ebXML-Payload-0>");
            resmsg.addAttachmentPart(attachment); 
            
            resmsg.getSOAPPart().getEnvelope().setPrefix("SOAP");
            resmsg.getSOAPHeader().setPrefix("SOAP");
            resmsg.getSOAPBody().setPrefix("SOAP");
            resmsg.saveChanges();
            
            dos = new DataOutputStream(client.getOutputStream());
            dos = new DataOutputStream(this.client.getOutputStream());
		    dos.writeBytes("HTTP/1.0 200 OK\r\n");
		    dos.writeBytes("SOAPAction: ebXML\r\n");
		    dos.writeBytes("Content-Type: text/xml\r\n");
		    dos.writeBytes("Content-Length: " + "100000" + "\r\n\r\n");
 
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    resmsg.writeTo(baos);
            String SendXML = new String(baos.toByteArray());
            byte[] SendBytes = SendXML.getBytes("utf-8");
            dos.write(SendBytes);
		    dos.flush();
            client.close();
            
            COMMDATA.GetTCheckerLog().WriteLog("D", "UrlManager", "SERVER:" + pApplCode + ":NONE:Send To Anylink : [" + SOAPXML + "]");

        }catch(SOAPException esoap){
        	COMMDATA.GetTCheckerLog().WriteLog("E", "UrlManager", esoap);
            setThreadSleep(100);
            try{ client.close(); }catch(Exception e){}
        }catch(IOException e2){
        	COMMDATA.GetTCheckerLog().WriteLog("E", "UrlManager", e2);
            setThreadSleep(100);
            try{ client.close(); }catch(Exception e){}
        }
 	   
    }
    
    private String GetParsingName(String pApplCode, String recvdata, String pTagname)
    {
    	int start = recvdata.indexOf(pTagname + ">");
  	    int stop  = recvdata.indexOf("<" , start);
  	    if (start <= 0 || stop <= 0) {
      	    COMMDATA.GetTCheckerLog().WriteLog("E", "UrlManager", "SERVER:" + pApplCode + ":NONE:전문에서 <" + pTagname + "> 을 찾을 수 가 없습니다.");
      	    try{ client.close(); }catch(Exception e){}
      	    return "";
        }
  	    return recvdata.substring(start + (pTagname + ">").getBytes().length , stop);
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
    public void setThreadSleep(int time)
    {
    	try{
             Thread.sleep(time);
        } catch(InterruptedException e) {}
    }
	private String Proc_getUrlInfo(byte[] pRecvBytes)
	{
		//수신데이타에서 URL 를 추출한다.
		String pUrl = "";
		String pWorkData = new String(pRecvBytes);
		String[] arrtmpwork = pWorkData.split("\n");
		for(int i=0;i < arrtmpwork.length;i++){
			if (arrtmpwork[i].toUpperCase().indexOf("POST") >= 0 && arrtmpwork[i].toUpperCase().indexOf("HTTP") >= 0){
				String[] arrtmp = arrtmpwork[i].split(" ");
				pUrl = arrtmp[1];
				break;
			}
		}
		if (pUrl.equals("")){
			COMMDATA.GetTCheckerLog().WriteLog("E", "UrlManager", "SERVER:NONE:NONE:수신된 전문에서 URL 를 추출하지 못하였습니다");
			return "";
		}
		
        //회선정보에서 출력회선으로 설정된 회선에 대한 정보를 제외한 회선정보를 읽어온다.
		/* 대주보 DB2 관련 표준 SQL로 수정
		String isql = " SELECT a.appl_code, a.url, a.URL_TYPE, g.EB_CPAID, g.EB_FROM_PARTY, g.EB_TO_PARTY, g.EB_SVCNAME, g.REQ_ENCODING, g.RES_ENCODING";
        isql = isql + "\n FROM alurl a right outer join algrp g ";
        isql = isql + "\n      on  a.appl_code = g.appl_code ";
        isql = isql + "\n Where a.sta_type = 1  ";
        isql = isql + "\n   and g.sta_type = 1  ";
        isql = isql + "\n   and a.url like '%" + pUrl + "%'";
        */
		
		String isql = "    SELECT a.appl_code, a.url, a.URL_TYPE, g.EB_CPAID, g.EB_FROM_PARTY, g.EB_TO_PARTY, g.EB_SVCNAME, g.REQ_ENCODING, g.RES_ENCODING ";
		isql = isql + "\n  FROM alurl a LEFT OUTER JOIN algrp g ";
		isql = isql + "\n  ON a.appl_code = g.appl_code ";
		isql = isql + "\n  Where a.sta_type = 1 ";  
		isql = isql + "\n    and g.sta_type = 1 ";  
		isql = isql + "\n    and a.url like '%" + pUrl + "%' ";
 
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
		if (retdata == null || retdata.equals("")) {
			return "";
		}
		
		String[] arrtmp = retdata.split("\n");
		String[] arrtmpsub = arrtmp[0].split("\t");
  
		if (arrtmpsub[3].trim().equals("")) return arrtmpsub[0] + "\tSOAP\t" + pUrl;
		return arrtmpsub[0] + "\tEBXML\t" + pUrl;
	}
}

/*
POST /soap/wooriOnline HTTP/1.1
Host: 192.168.10.20:40001
*/
