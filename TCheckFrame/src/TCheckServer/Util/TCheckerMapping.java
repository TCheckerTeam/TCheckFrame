package TCheckServer.Util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import TCheckServer.Engine.CommData;
import TCheckServer.UserClass.UserChkLen;
import TCheckServer.UserClass.UserFunction;
import TCheckServer.UserClass.UserShare;
import TCheckServer.Util.DataModel.myTcpMapComm;

public class TCheckerMapping {
	private CommData               COMMDATA = null;
	private UserShare              usergongu    = null;
	private UserFunction           userfunction = null;
	private UserChkLen             userlength   = null;
	private String                 MsgBody = "";
	private String                 MsgBodyRecv = "";
	private String                 MsgBodySend = "";
	private int                    gIndex = 0;
	private boolean                Body_ByPass_Flag = false;
	private DefaultMutableTreeNode rootrecv = null, rootsend = null;
	private String                 gServerIP = "";
	private String                 gManagerPort = "";
	private String                 gAnylinkIP = "";
	private String                 gReadTreeData = "";
	private String                 GWTrace_Mode = "N"; 
	private String                 gApplCode = "";
	private String                 gKindCode = "";
	private String                 gTxCode = "";
	private String                 gMapping_Mode = "ENG";
	public TCheckerMapping(CommData commdata,String pApplCode, String pKindCode, String pTxCode)
	{
		 COMMDATA = commdata;
		 gApplCode = pApplCode;
		 gKindCode = pKindCode;
		 gTxCode = pTxCode;
		 rootrecv = new DefaultMutableTreeNode(new myTcpMapComm("Root", "", "", "","","",true));  
 		 rootsend = new DefaultMutableTreeNode(new myTcpMapComm("Root", "", "", "","","",true));  
 
		 getSystemInfo();
		 
		 usergongu    = new UserShare(COMMDATA);
		 userfunction = new UserFunction();
		 userlength   = new UserChkLen();
	}
    public byte[] MakeResponseMsg(String pRecvData)
    {
    	String RecvMsgMapInfo = "";
    	String SendMsgMapInfo = "";
    	String ResApplCode = "";
    	String ResKindCode = "";
    	String ResTxCode   = "";
    	String ResPortNo   = "";
    	byte[] tmpbyte        = null;
     
    	//공유업무를 점검하고, ApplCode, KindCode, TxCode 를 추출한다.
    	usergongu.SetRECVDATA(pRecvData);
    	
 
		usergongu.SetAPPL_CODE(gApplCode);
		usergongu.SetKIND_CODE(gKindCode);
		usergongu.SetTX_CODE(gTxCode);
		usergongu.GetGongUInfo();
  
  
        //Anylink로 부터 받은 데이타에 대한 수신매핑정보를 읽어온다.
        RecvMsgMapInfo = LoadMappingMsg(usergongu.GetAPPL_CODE(), usergongu.GetKIND_CODE(), usergongu.GetTX_CODE(), "REQUEST", "READ");
        MakeTreeData(usergongu.GetAPPL_CODE(), usergongu.GetKIND_CODE(), usergongu.GetTX_CODE(), RecvMsgMapInfo, rootrecv);
        MsgBodyRecv = MsgBody;
        COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "SERVER:" + ResApplCode + ":" + ResKindCode + ":" + ResTxCode + ":MsgBodyRecv[" + MsgBodyRecv + "]");
        
        //수신한 데이타를 tree에 셋팅한다.
        int idx = 0;
		final HashMap<String, String> hashinrt = new HashMap<String, String>();
		SetEditDataSub(rootrecv,rootrecv, hashinrt, idx, pRecvData);
        
		//응답없는 전문인지 확인한다.
    	if (isResponseMsg(usergongu.GetAPPL_CODE(), usergongu.GetKIND_CODE(), usergongu.GetTX_CODE()) != true ) {
    		String ResInfo = SearchResLinkOne(usergongu.GetAPPL_CODE(), usergongu.GetKIND_CODE(), usergongu.GetTX_CODE());
    		if (ResInfo.equals("")) return null;
    		
    		String[] arrResInfo = ResInfo.split("\t");
    		ResApplCode = arrResInfo[0];
    		ResKindCode = arrResInfo[2];
    		ResTxCode   = arrResInfo[4];
    		ResPortNo   = arrResInfo[6];
    	}
 
    	//Anylink로 응답송신할 매핑정보를 읽어온다. 송신전문을 tree에 셋팅한다.
        SendMsgMapInfo = LoadMappingMsg(usergongu.GetAPPL_CODE(), usergongu.GetKIND_CODE(), usergongu.GetTX_CODE(), "RESPONSE", "READ");
        MakeTreeData(usergongu.GetAPPL_CODE(), usergongu.GetKIND_CODE(), usergongu.GetTX_CODE(), SendMsgMapInfo,rootsend);
        COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "SERVER:" + ResApplCode + ":" + ResKindCode + ":" + ResTxCode + ":MsgBody[" + MsgBody + "]");
        
        if (Body_ByPass_Flag == true && !MsgBody.trim().equals("")) {
        	MsgBodySend = MsgBody;
        }
        COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "SERVER:" + ResApplCode + ":" + ResKindCode + ":" + ResTxCode + ":MsgBodySend[" + MsgBodySend + "]");
        
        //SendMsg를 생성한다.
        byte[] makeMsg = MakeSendMsg(usergongu.GetAPPL_CODE(), usergongu.GetKIND_CODE(), usergongu.GetTX_CODE());
        if (makeMsg.length > 10){
			String tmpstr = new String(makeMsg);
			if (tmpstr.substring(0,6).equals("ERROR:")) {
	    		COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", tmpstr);
	    		return null;
			}
			else {
				if (ResPortNo.trim().equals("") || ResPortNo.trim().equals("0")) return makeMsg;
				
				//다른 업무/포트에 전송 의뢰
				COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "SERVER:" + ResApplCode + ":" + ResKindCode + ":" + ResTxCode + ":Async요청에 대한 응답전문 송신를 의뢰하였습니다.[" + ResPortNo + "]");
				SendAsyncTcpMsg(ResApplCode, ResKindCode, ResTxCode, ResPortNo, gReadTreeData);
				return null;
			}
        }
        else {
        	COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", usergongu.GetAPPL_CODE() + ":" + usergongu.GetKIND_CODE() + ":" + usergongu.GetTX_CODE() + ":응답전문을 생성하는데 오류가 발생하였습니다.");
        }
    	return null;
    }
    private void MakeTreeData(String pApplCode, String pKindCode, String pTxCode, String pMsgMapInfo, DefaultMutableTreeNode mytree)
    {
    	//Body부분에 대한 Bypass 매핑인지 점검한다.
    	Body_ByPass_Flag = false;
    	String TxDetailInfo = GetApplTxInfo(pApplCode,pKindCode,pTxCode);
		String[] arrtmp = TxDetailInfo.split("\n")[0].split("\t");
		if (arrtmp[1].equals("P")) Body_ByPass_Flag = true;

		COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", pApplCode + ":" + pKindCode + ":" + pTxCode + ":" + TxDetailInfo + " --> " + arrtmp[1]);
				 
		
    	//Array 및 Struct 에 대한 Work TreeNode 를 선언한다.
    	DefaultMutableTreeNode Array1 = null;
    	DefaultMutableTreeNode Array2 = null;
    	DefaultMutableTreeNode Array3 = null;
    	DefaultMutableTreeNode Struct1 = null;
    	DefaultMutableTreeNode Struct2 = null;
    	DefaultMutableTreeNode Struct3 = null;
    	MsgBody = "";

    	//Tree가 Null 이면, 그냥 리턴한고, 그렇지 않으면 Tree의 모든 Child Node를 삭제한다.
   	    if (mytree == null) return ;
   	    mytree.removeAllChildren();

   	    //pMsgMapInfo 에는 Body부 Bypass 매핑에 대한 정보가 들어있을 수 있으므로 <BODYBYPASS> 로 구분하여, MsgMap정보를 분리한다.
   	    String[] gridData = new String[6];
   	    String[] arrpMsgMapInfo = pMsgMapInfo.split("<BODYBYPASS>");
   	    if (arrpMsgMapInfo.length > 1) MsgBody = arrpMsgMapInfo[1];
   	    String   MapInfo = arrpMsgMapInfo[0];
   	    String[] arrMainData = MapInfo.replace("\r","").split("\n");
   	 
        //MsgMap 정보에서 Array Len 에 #이 들어가 있는 항목을 추출한다.
   	    String   arraycnt_colnamelist = "";
        for(int i=0;i < arrMainData.length ;i++){
        	if (arrMainData[i].trim().equals("")) break;
        	String[] arrtmpsub = arrMainData[i].split("\t");
        	
        	if (arrtmpsub[2].indexOf("ARRAY-S") >= 0) {
        		if (arrtmpsub[3].trim().indexOf("#") == 0) {
        			arraycnt_colnamelist = arraycnt_colnamelist + arrtmpsub[3].trim().replace("#", "") + "\t";
        		}
        	}
        }
 
        //MsgMap 정보에 대한 TreeNode를 구성한다.
        try {
            for(int i=0;i < arrMainData.length ;i++){
            	if (arrMainData[i].trim().equals("")) break;
            	String[] arrtmpsub = arrMainData[i].split("\t");
                
            	if (arrtmpsub[2].indexOf("STRUCT-E") >= 0) continue;
            	if (arrtmpsub[2].indexOf("ARRAY-E") >= 0) continue;
            	if (arrtmpsub[2].indexOf("ARRAY-S") >= 0) {
            		for(int k=0;k < gridData.length ;k++) gridData[k] = "";
     
                    gridData[0] = arrtmpsub[0];
                	gridData[1] = arrtmpsub[2];
                	gridData[2] = arrtmpsub[3];
                	gridData[3] = arrtmpsub[4];
                	gridData[4] = "";
                	gridData[5] = arrtmpsub[1];
                	myTcpMapComm tmpclass = new myTcpMapComm(gridData[0] , gridData[1], gridData[2] , gridData[3], gridData[4], gridData[5],false);
                	if (gridData[2].indexOf("#") >= 0) {
                		tmpclass.setArrayCntName(gridData[2]);
                		gridData[2] = "0";
                	}
                	
                	DefaultMutableTreeNode tmpnode = new DefaultMutableTreeNode(tmpclass);
                	
                	int cnt = arrtmpsub[1].replace(".","@").split("@").length;
                	if (cnt == 1) {
                		mytree.add(tmpnode);
                		Array1 = tmpnode;
                	}
                	if (cnt == 2) {
                		Struct1.add(tmpnode);
                		Array2 = tmpnode;
                	}
                	if (cnt == 3) {
                		Struct2.add(tmpnode);
                		Array3 = tmpnode;
                	}
                	
            	}
            	else if (arrtmpsub[2].indexOf("STRUCT-S") >= 0) {
            		for(int k=0;k < gridData.length ;k++) gridData[k] = "";
     
                    gridData[0] = arrtmpsub[0];
                	gridData[1] = arrtmpsub[2];
                	gridData[2] = arrtmpsub[3];
                	gridData[3] = arrtmpsub[4];
                	gridData[4] = "";
                	gridData[5] = arrtmpsub[1];
                	DefaultMutableTreeNode tmpnode = new DefaultMutableTreeNode(new myTcpMapComm(gridData[0] , gridData[1], gridData[2] , gridData[3],gridData[4], gridData[5], false));
                	
                	int cnt = arrtmpsub[1].replace(".","@").split("@").length;
                	if (cnt == 1) {
                		Array1.add(tmpnode);
                		Struct1 = tmpnode;
                	}
                	if (cnt == 2) {
                		Array2.add(tmpnode);
                		Struct2 = tmpnode;
                	}
                	if (cnt == 3) {
                		Array3.add(tmpnode);
                		Struct3 = tmpnode;
                	}
     
            	}
            	else {
            		for(int k=0;k < gridData.length ;k++) gridData[k] = "";
            		int cnt = arrtmpsub[1].replace(".","@").split("@").length;
            		gridData[0] = arrtmpsub[0];
                	gridData[1] = arrtmpsub[2];
                	gridData[2] = arrtmpsub[3];
                	gridData[3] = arrtmpsub[4];
                	gridData[4] = "";
                	gridData[5] = arrtmpsub[1];
                	DefaultMutableTreeNode tmpnode = new DefaultMutableTreeNode(new myTcpMapComm(gridData[0] , gridData[1], gridData[2] , gridData[3], gridData[4], gridData[5],false));
                	
                	if (cnt == 1) {
                		mytree.add(tmpnode);
                	}
                	if (cnt == 2) {
                		Struct1.add(tmpnode);
                	}
                	if (cnt == 3) {
                		Struct2.add(tmpnode);
                	}
                	if (cnt == 4) {
                		Struct3.add(tmpnode);
                	}
            	}
            }
        }catch(Exception e) {
        	if (Body_ByPass_Flag != true) {
        		COMMDATA.GetTCheckerLog().WriteLog("W", "TcpManager", pApplCode + ":" + pKindCode + ":" + pTxCode + ":해당 거래에 대한 매핑구분정보를 확인하세요.");
        	}
        	else {
        		COMMDATA.GetTCheckerLog().WriteLog("W", "TcpManager", pApplCode + ":" + pKindCode + ":" + pTxCode + ":전문정보를 확인하세요");
        	}
        	
        }
 
        SetArrayHandle(mytree);
 
        return ;
    }
 
    private void SetArrayHandle(DefaultMutableTreeNode mytree)
    {
		try {
			//1차 배열의 Count를 점검하여, 그 갯수만큼 Structure 를 늘려준다.
			for(int i=0;i < mytree.getChildCount();i++)
			{
				  DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) mytree.getChildAt(i);  
				  myTcpMapComm data = (myTcpMapComm) dataNode.getUserObject();  
				  if (data.getType().equals("ARRAY-S") && data.getLen().length() > 0 && !data.getLen().substring(0,1).equals("#")){
					  int childcnt = dataNode.getChildCount();
					  int loopcnt =  Integer.parseInt(data.getLen()) - childcnt;
					  
					  for(int j=0;j < loopcnt;j++){
					      DefaultMutableTreeNode tmpnode = getTravelChildNodes((DefaultMutableTreeNode)dataNode.getChildAt(0));
					      dataNode.add(tmpnode);
					  }
					  
				  }
			}
			
			//2차 배열의 Count를 점검하여, 그 갯수만큼 Structure 를 늘려준다.
			for(int i=0;i < mytree.getChildCount();i++)
			{
				  DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) mytree.getChildAt(i);  
				  for(int j=0;j < dataNode.getChildCount();j++){
					  //1차배열에 있는 Struct
					  DefaultMutableTreeNode dataStructNode = (DefaultMutableTreeNode)dataNode.getChildAt(j);
					  for(int k=0;k < dataStructNode.getChildCount();k++) {
						  //1차배열에 있는 Member
						  DefaultMutableTreeNode dataMemberNode = (DefaultMutableTreeNode)dataStructNode.getChildAt(k);
						  myTcpMapComm data = (myTcpMapComm) dataMemberNode.getUserObject(); 
						  
						  if (data.getType().equals("ARRAY-S") && data.getLen().length() > 0 && !data.getLen().substring(0,1).equals("#")){
							  int childcnt = dataMemberNode.getChildCount();
							  int loopcnt =  Integer.parseInt(data.getLen()) - childcnt;
	 
							  for(int m=0;m < loopcnt;m++){
							      DefaultMutableTreeNode tmpnode = getTravelChildNodes((DefaultMutableTreeNode)dataMemberNode.getChildAt(0));
							      dataMemberNode.add(tmpnode);
							  }
						  }
					  }
				  }
			}
			
			//3차 배열의 Count를 점검하여, 그 갯수만큼 Structure 를 늘려준다.
			for(int i=0;i < mytree.getChildCount();i++)
			{
				  DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) mytree.getChildAt(i);  
				  for(int j=0;j < dataNode.getChildCount();j++){
					  //1차배열에 있는 Struct
					  DefaultMutableTreeNode dataStructNode = (DefaultMutableTreeNode)dataNode.getChildAt(j);
					  for(int k=0;k < dataStructNode.getChildCount();k++) {
						  //1차배열에 있는 Member
						  DefaultMutableTreeNode dataMemberNode = (DefaultMutableTreeNode)dataStructNode.getChildAt(k);
						  
						  for(int m=0;m < dataMemberNode.getChildCount();m++){
							 //2차배열에 있는 Struct  
							  
							  DefaultMutableTreeNode dataNode1 = (DefaultMutableTreeNode) dataMemberNode.getChildAt(m); 
							  for(int n=0;n < dataNode1.getChildCount();n++){
								  //2차배열에 있는 Member
								  DefaultMutableTreeNode dataMemberNode1 = (DefaultMutableTreeNode)dataNode1.getChildAt(n);
								  myTcpMapComm data = (myTcpMapComm) dataMemberNode1.getUserObject(); 
								  if (data.getType().equals("ARRAY-S") && data.getLen().length() > 0 && !data.getLen().substring(0,1).equals("#")){
		 						      int childcnt = dataMemberNode1.getChildCount();
									  int loopcnt =  Integer.parseInt(data.getLen()) - childcnt;
									  
									  DefaultMutableTreeNode dataStructNode1 = (DefaultMutableTreeNode)dataMemberNode1.getChildAt(0);
									  for(int z=0;z < loopcnt;z++){
									      DefaultMutableTreeNode tmpnode = getTravelChildNodes((DefaultMutableTreeNode)dataStructNode1);
									      dataMemberNode1.add(tmpnode);
									  }
									 
								  }
							  }
						  }
						 
					  }
				  }
			}
			
			//모든 항목에 No 를 부여한다.
			gIndex = 0;
			setTravelKeyNodes(mytree);
		}catch(Exception e){}
    }
    private DefaultMutableTreeNode getTravelChildNodes(DefaultMutableTreeNode dataStructNode)
    {
    	myTcpMapComm data = (myTcpMapComm) dataStructNode.getUserObject(); 
    	DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new myTcpMapComm(data.getItem(), data.getType(), data.getLen(), "","",data.getEng(), true));  
    	
    	for(int i=0;i < dataStructNode.getChildCount();i++)
    	{
    		DefaultMutableTreeNode tmpnode = (DefaultMutableTreeNode)dataStructNode.getChildAt(i);
    		data = (myTcpMapComm) tmpnode.getUserObject(); 
 
    		DefaultMutableTreeNode tmpnodenew = new DefaultMutableTreeNode(new myTcpMapComm(data.getItem(), data.getType(), data.getLen(), "","",data.getEng(), true));  
    		rootNode.add(tmpnodenew);
    		
    		if (tmpnode.getChildCount() > 0 ) {
                DefaultMutableTreeNode tmpnodesub = getTravelChildNodes((DefaultMutableTreeNode)tmpnode.getChildAt(0));
    			tmpnodenew.add(tmpnodesub);
    		}
    	}
    	return rootNode;
    }
    private void setTravelKeyNodes(DefaultMutableTreeNode dataNode)
    {
 
    	myTcpMapComm data = (myTcpMapComm) dataNode.getUserObject(); 
    	for(int i=0;i < dataNode.getChildCount();i++)
    	{
    		DefaultMutableTreeNode tmpnode = (DefaultMutableTreeNode)dataNode.getChildAt(i);
    		data = (myTcpMapComm) tmpnode.getUserObject(); 
    	 
    		data.setNo(++gIndex + "");
       		if (tmpnode.getChildCount() > 0 ) {
    			setTravelKeyNodes(tmpnode);
    		}
    	}
    }
    private boolean setKeyNoInfo(DefaultMutableTreeNode dataNode, String pKeyNo, String pLen)
    {
    	myTcpMapComm data = (myTcpMapComm) dataNode.getUserObject(); 
    	for(int i=0;i < dataNode.getChildCount();i++)
    	{
    		DefaultMutableTreeNode tmpnode = (DefaultMutableTreeNode)dataNode.getChildAt(i);
    		data = (myTcpMapComm) tmpnode.getUserObject(); 
    	    if (data.getNo().equals(pKeyNo)) {
    	    	data.setLen(pLen);
    	    	return true;
    	    }
       		if (tmpnode.getChildCount() > 0 ) {
    			boolean rcflag = setKeyNoInfo(tmpnode,pKeyNo,pLen);
    			if (rcflag == true) return true;
    		}
    	}
    	return false;
    }

    private int SetEditDataSub(DefaultMutableTreeNode myroot, DefaultMutableTreeNode myArrayNode, HashMap<String, String> hash, int idx, String indata  )
    {
    	try {
 
	    	for(int i=0;i < myArrayNode.getChildCount();i++){
	    		DefaultMutableTreeNode WorkNode = (DefaultMutableTreeNode) myArrayNode.getChildAt(i);  
	    		myTcpMapComm data = (myTcpMapComm) WorkNode.getUserObject();
	  		    String item = data.getItem();
 
			    if (data.getType().indexOf("STRUCT-S") >= 0) continue;
			    if (data.getType().indexOf("ARRAY-S") >= 0 ) {
				    if (data.getLen().indexOf("#") >= 0) {
					  String lenval = hash.get(data.getLen().replace("#", ""));
					  data.setLen(lenval);
					  SetArrayHandle(myroot);
				    }
	        		for(int j=0;j < WorkNode.getChildCount();j++){
	        			idx = SetEditDataSub(myroot, (DefaultMutableTreeNode)WorkNode.getChildAt(j), hash, idx, indata);
	        		}
	        		continue;
			    }
 	  
	        	byte[] tmpbyte = new byte[Integer.parseInt(data.getLen())];
	        	if (indata.getBytes().length <= Integer.parseInt(data.getLen()) ) {
	        		System.arraycopy(indata.getBytes(), idx, tmpbyte, 0, indata.getBytes().length);
	        	}
	        	else {
	        		System.arraycopy(indata.getBytes(), idx, tmpbyte, 0, Integer.parseInt(data.getLen()));
	        	}
	    		
	            idx += Integer.parseInt(data.getLen());
	            data.setConts(new String(tmpbyte));
	         
	            //Arrany 길이변수로 사용되는 항목을 추출하기 위해 모든 영문명으로 hash에 put
	            hash.put(data.getEng(), new String(tmpbyte));
	            
	           // COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "SERVER:SERVER:SERVER:SERVER:요청항목/값=.[" + data.getEng() + ":" + new String(tmpbyte));
	            
	    	}
    	}catch(Exception e){
    		COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", e); 
    	}
        return idx;
    }
    private void getTravelData(DefaultMutableTreeNode dataNode)
    {
 
    	myTcpMapComm data = (myTcpMapComm) dataNode.getUserObject(); 
    	for(int i=0;i < dataNode.getChildCount();i++)
    	{
    		DefaultMutableTreeNode tmpnode = (DefaultMutableTreeNode)dataNode.getChildAt(i);
    		data = (myTcpMapComm) tmpnode.getUserObject(); 
            gReadTreeData = gReadTreeData + data.getItem() + "\t";
            gReadTreeData = gReadTreeData + data.getType() + "\t";
            gReadTreeData = gReadTreeData + data.getLen() + "\t";
            gReadTreeData = gReadTreeData + data.getConts() + "\t";
            gReadTreeData = gReadTreeData + data.getNo() + "\t";
            gReadTreeData = gReadTreeData + data.getEng() + "\n";
            
       		if (tmpnode.getChildCount() > 0 ) {
       			getTravelData(tmpnode);
    		}
    	}
    }
    
	private byte[] MakeSendMsg(String pApplCode, String pKindCode, String pTxCode )
	{
		byte[] HEAD = null;
		int    MSGSIZE = 0;
		byte[] MSGDATA = null;
		ByteArrayOutputStream baos = null;
		String[] arrSendData = null; 
		String[] arrRecvData = null; 
		try {
			
			COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "RESP_KINDCODE :" + COMMDATA.GetRESP_KINDCODE()); 
            COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "COMM_HEAD_TYPE:" + COMMDATA.GetCOMM_HEAD_TYPE()); 
            COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "LEN_TYPE      :" + COMMDATA.GetLEN_TYPE());
            COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "LEN_OFFS      :" + COMMDATA.GetLEN_OFFST());
            COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "LEN_SIZE      :" + COMMDATA.GetLEN_SIZE());
            
	        //응답매핑 처리를 한다.
			gReadTreeData = "";
			getTravelData(rootrecv);
			arrRecvData = gReadTreeData.split("\n");
			COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager",  "SERVER:" + pApplCode + ":" + pKindCode + ":" + pTxCode + ":arrRecvData:" + gReadTreeData);
			
			gReadTreeData = "";
			getTravelData(rootsend);
			arrSendData = gReadTreeData.split("\n");
			COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "SERVER:" + pApplCode + ":" + pKindCode + ":" + pTxCode + ":arrSendData:" + gReadTreeData);
	 
			int CurOffset = 0;
			baos = new ByteArrayOutputStream();
			for(int i=0;i < arrSendData.length ;i++)
			{
	            if (arrSendData[i].trim().equals("")) break;
	            String[] arrtmp = arrSendData[i].split("\t");
	            
            	if (arrtmp[1].trim().equals("ARRAY-S") || arrtmp[1].trim().equals("STRUCT-S")) continue;

            	//컬럼의 크기만큼의 버퍼를 할당하여, 데이타를 셋팅한다.
            	if (arrtmp[3].equals("<NODATA>")) arrtmp[3] = "";
            	byte[] mapdata = userfunction.Parsing(arrtmp[3].getBytes(), pApplCode, pKindCode, pTxCode);
            	if (mapdata == null ){
        		    //송신할 항목의 값이 공백이면, Anylink로 부터 수신받은 데이타에서 동일한 항목값을 추출하여, 설정을 함.
        		    mapdata = GetFindMappingValue(arrRecvData, arrtmp[0], arrtmp[5]);
        		    COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "SERVER:" + pApplCode + ":" + pKindCode + ":" + pTxCode + ":Mapping:" + arrtmp[5] + "=" + mapdata);
            	}
            	else {
            		if (mapdata.length == 0) {
            		    //송신할 항목의 값이 공백이면, Anylink로 부터 수신받은 데이타에서 동일한 항목값을 추출하여, 설정을 함.
            		    mapdata = GetFindMappingValue(arrRecvData, arrtmp[0], arrtmp[5]);
            		    COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "SERVER:" + pApplCode + ":" + pKindCode + ":" + pTxCode + ":Mapping:" + arrtmp[5] + "=" + new String(mapdata));
            		}
            	}
   
            	byte[] tmpdat = new byte[Integer.parseInt(arrtmp[2].trim())];
            	for(int j=0;j < tmpdat.length ;j++) tmpdat[j] = 32;
            	
            	if (mapdata.length >= Integer.parseInt(arrtmp[2].trim())){
            		System.arraycopy(mapdata, 0, tmpdat, 0, Integer.parseInt(arrtmp[2].trim()));	
            	}
            	else {
            		System.arraycopy(mapdata, 0, tmpdat, 0, mapdata.length);
            	}
            	baos.write(tmpdat, 0, tmpdat.length); 

            	if (i == 0 && arrtmp[0].equals("비정형데이타")){
            		 String workstr = new String(mapdata);
	            	 int searchidx = workstr.indexOf("<EOF>");
	            	 
	            	 baos.write(workstr.substring(0, searchidx).getBytes(), 0, workstr.substring(0, searchidx).getBytes().length); 
	            	 
	            	 break;
	            }
	            CurOffset = CurOffset + tmpdat.length ;
			}
		}catch(Exception e) {
			COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", e);
		}
		
		try {
			MSGDATA = baos.toByteArray();
			
			COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager",  "SERVER:" + pApplCode + ":" + pKindCode + ":" + pTxCode + ":MsgBodySend:" + MsgBodySend);
			
			if (Body_ByPass_Flag == true) {
				COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager",  "SERVER:" + pApplCode + ":" + pKindCode + ":" + pTxCode + ":MsgBodySend Add:" + MsgBodySend);
				baos.write(MsgBodySend.getBytes(), 0, MsgBodySend.getBytes().length); 
            	MSGDATA = baos.toByteArray();
            	COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager",  "SERVER:" + pApplCode + ":" + pKindCode + ":" + pTxCode + ":MsgBodySend MSGDATA-A:" + new String(MSGDATA));
			}
			if (baos != null) baos.close();  //ByteArray close
			
	        //길이설정방법이 00:설정필요 이면, 오류로 리턴한다.
            if (COMMDATA.GetLEN_TYPE().equals("00")){
            	String errmsg = "SERVER:" + pApplCode + ":" + pKindCode + ":" + pTxCode + ":" + "길이성정방법을 설정하세요.";
                COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", errmsg); 
                return ("ERROR:" + errmsg).getBytes(); 
            }
    
            /*-------- 헤더타입이 통신헤더 및 공통헤더일 경우에 HEAD 버퍼에 길이정보를 셋팅한다. --------*/
 
            MSGSIZE = MSGDATA.length ;
            
            if (COMMDATA.GetCOMM_HEAD_TYPE().equals("0") ){
            	try {
            		HEAD = new byte[Integer.parseInt(COMMDATA.GetCOMM_HEAD_SIZE())];
            		for(int i = 0;i < HEAD.length;i++) HEAD[i] = (byte)32;
            		
	            	if (COMMDATA.GetLEN_TYPE().equals("10")){
	            		//길이정보가 길이필드 이후의 길이값 : 통신헤더일 경우 메세지의 전체크기를 길이정보로 설정한다.
		    			String SendLenFmt = String.format("%%0%dd", Integer.parseInt(COMMDATA.GetLEN_SIZE()));
		    			String SendLen = String.format(SendLenFmt, MSGSIZE);
	            		System.arraycopy(SendLen.getBytes(), 0, HEAD, Integer.parseInt(COMMDATA.GetLEN_OFFST()), SendLen.getBytes().length);
	            	}
	            	else if (COMMDATA.GetLEN_TYPE().equals("50")){
	            		//길이필드 이후의 길이값(Integer) : 공통헤더일 경우에 메세지의 전체크기에서 길이 Offset 및 길이 Size 를 뺀 결과값을 길이정보로 설정한다.
	            		int tmplen = MSGSIZE;
	            		HEAD[Integer.parseInt(COMMDATA.GetLEN_OFFST()) + 0] = (byte)(tmplen / 256);
	            		HEAD[Integer.parseInt(COMMDATA.GetLEN_OFFST()) + 1] = (byte)(tmplen % 256);	
	            	}
	            	else if (COMMDATA.GetLEN_TYPE().equals("99")){
	            		byte[] bytelen = userlength.GetSendLength(COMMDATA, COMMDATA.GetLU_NAME(), pApplCode, MSGDATA);
	            		if (bytelen != null){
	            			System.arraycopy(bytelen,0,HEAD, Integer.parseInt(COMMDATA.GetLEN_OFFST()), bytelen.length);
	            		}
	            	}
	            	else {
	            		String errmsg = "SERVER:" + pApplCode + ":" + pKindCode + ":" + pTxCode + ":" + "통신헤더 일 경우에 길이설정방법은 [10:길이필드 이후의 길이값] [50:길이필드 이후의 길이값(Integer)] 이어야 합니다.";
	            		COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", errmsg);
	            		return ("ERROR:" + errmsg).getBytes();
	            	}
	            	
	        		//전송데이타 조립
            	
	        		byte[] tmpdat = new byte[Integer.parseInt(COMMDATA.GetCOMM_HEAD_SIZE()) + MSGSIZE];
	        		System.arraycopy(HEAD, 0, tmpdat, 0, HEAD.length);
	        		System.arraycopy(MSGDATA, 0, tmpdat, HEAD.length, MSGDATA.length);
 
	        		return tmpdat;
            	}catch(Exception e1){
            		COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", e1);
            	}
            	 
	       }
		 
	       if (COMMDATA.GetCOMM_HEAD_TYPE().equals("1")){
	    	   try{
	    		    byte[] tmpdat = new byte[MSGSIZE];
	            	System.arraycopy(MSGDATA, 0, tmpdat, 0, MSGDATA.length);
	            	
	            	if (COMMDATA.GetLEN_TYPE().equals("10")){
	            		//길이정보가 길이필드 이후의 길이값 : 공통헤더일 경우에 메세지의 전체크기에서 길이 Offset 및 길이 Size 를 뺀 결과값을 길이정보로 설정한다.
		    			String SendLenFmt = String.format("%%0%dd", Integer.parseInt(COMMDATA.GetLEN_SIZE()));
		    			String SendLen = String.format(SendLenFmt, MSGSIZE - Integer.parseInt(COMMDATA.GetLEN_OFFST()) - Integer.parseInt(COMMDATA.GetLEN_SIZE()));
	            		System.arraycopy(SendLen.getBytes(), 0, tmpdat, Integer.parseInt(COMMDATA.GetLEN_OFFST()), SendLen.getBytes().length);
	            	}
	            	else if (COMMDATA.GetLEN_TYPE().equals("11")){
	            		//길이필드를 포함한 길이값 : 공통헤더일 경우에 메세지의 전체크기를 설정한다.
		    			String SendLenFmt = String.format("%%0%dd", Integer.parseInt(COMMDATA.GetLEN_SIZE()));
		    			String SendLen = String.format(SendLenFmt, MSGSIZE);
	            		System.arraycopy(SendLen.getBytes(), 0, tmpdat, Integer.parseInt(COMMDATA.GetLEN_OFFST()), SendLen.getBytes().length);
	            	}
	            	else if (COMMDATA.GetLEN_TYPE().equals("12")){
	            		//Body부 길이값 : 공통헤더일 경우에 메세지의 전체크기에서 Head Size 뺀 결과값을 길이정보로 설정한다.
		    			String SendLenFmt = String.format("%%0%dd", Integer.parseInt(COMMDATA.GetLEN_SIZE()));
		    			String SendLen = String.format(SendLenFmt, MSGSIZE - Integer.parseInt(COMMDATA.GetCOMM_HEAD_SIZE()));
	            		System.arraycopy(SendLen.getBytes(), 0, tmpdat, Integer.parseInt(COMMDATA.GetLEN_OFFST()), SendLen.getBytes().length);
	            	}
	            	else if (COMMDATA.GetLEN_TYPE().equals("50")){
	            		//길이필드 이후의 길이값(Integer) : 공통헤더일 경우에 메세지의 전체크기에서 길이 Offset 및 길이 Size 를 뺀 결과값을 길이정보로 설정한다.
	            		int tmplen = MSGSIZE - Integer.parseInt(COMMDATA.GetLEN_OFFST()) - Integer.parseInt(COMMDATA.GetLEN_SIZE());
	            		tmpdat[Integer.parseInt(COMMDATA.GetLEN_OFFST()) + 0] = (byte)(tmplen / 256);
	            		tmpdat[Integer.parseInt(COMMDATA.GetLEN_OFFST()) + 1] = (byte)(tmplen % 256);	
	            	}
	            	else if (COMMDATA.GetLEN_TYPE().equals("51")){
	            		//길이필드를 포함한 길이값(Integer) : 공통헤더일 경우에 메세지의 전체크기를 길이정보로 설정한다.
	            		int tmplen = MSGSIZE;
	            		tmpdat[Integer.parseInt(COMMDATA.GetLEN_OFFST()) + 0] = (byte)(tmplen / 256);
	            		tmpdat[Integer.parseInt(COMMDATA.GetLEN_OFFST()) + 1] = (byte)(tmplen % 256);	
	            	}
	            	else if (COMMDATA.GetLEN_TYPE().equals("52")){
	            		//Body부 길이값(Integer) : 공통헤더일 경우에 메세지의 전체크기에서 Head Size 뺀 결과값을 길이정보로 설정한다.
	            		int tmplen = MSGSIZE - Integer.parseInt(COMMDATA.GetCOMM_HEAD_SIZE());
	            		tmpdat[Integer.parseInt(COMMDATA.GetLEN_OFFST()) + 0] = (byte)(tmplen / 256);
	            		tmpdat[Integer.parseInt(COMMDATA.GetLEN_OFFST()) + 1] = (byte)(tmplen % 256);	
	            	}
	            	else if (COMMDATA.GetLEN_TYPE().equals("99")){
	            		byte[] bytelen = userlength.GetSendLength(COMMDATA, COMMDATA.GetLU_NAME(), pApplCode, MSGDATA);
	            		if (bytelen != null){
	            			System.arraycopy(bytelen,0,tmpdat, Integer.parseInt(COMMDATA.GetLEN_OFFST()), bytelen.length);
	            		}
	            	}
	            	else {
	            		String errmsg = "SERVER:" + pApplCode + ":" + pKindCode + ":" + pTxCode + ":" + "공통헤더 일 경우에 길이설정방법은 [13:고정길이] [14:END문자]을 사용할 수 없습니다.";
	            		COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", errmsg);
	            		return ("ERROR:" + errmsg).getBytes();
	            	}
 
	        	    return tmpdat;
	    	   }catch(Exception e1){
           		    COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", e1);
           	   }
            }
            if (COMMDATA.GetCOMM_HEAD_TYPE().equals("2")){
            	try{
	            	//통신헤더가 고정길이 방식일 경우 고정길이 만큼을 버퍼를 할당하여, 메세지를 셋팅한다.
	            	byte[] tmpdat = new byte[Integer.parseInt(COMMDATA.GetLEN_SIZE())];
	            	System.arraycopy(MSGDATA, 0, tmpdat, 0, MSGDATA.length);
	            	return tmpdat;
 	    	    }catch(Exception e1){
          		    COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", e1);
          	    }
            }
            if (COMMDATA.GetCOMM_HEAD_TYPE().equals("3")){
            	//End문자 방식일 경우에 메세지를 그대로 리턴한다.
            	return MSGDATA;
            }
		 
		}catch(Exception e){}
		
		String errmsg = "SERVER:" + pApplCode + ":" + pKindCode + ":" + pTxCode + ":" + "에 대한 공통헤더 및 길이설정방법을 확인거나 요청전문을 확인하세요.";
		COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", errmsg);
		return ("ERROR:" + errmsg).getBytes();
	  
	}
 
	private byte[] GetFindMappingValue(String[] arrdata, String pKorName, String pEngName)
	{
		String data  = "";
        String item  = "";
		String type  = "";
		String conts = "";
		String eng   = "";
   
	 
		for(int i=0;i < arrdata.length ;i++){
            String[] arrtmp = arrdata[i].split("\t");
            if (arrtmp.length >= 6) {
                item  = arrtmp[0];
    			type  = arrtmp[1];
    			conts = arrtmp[3];
    			eng   = arrtmp[5];
    			if (gMapping_Mode.equals("KOR")) {
    				if (item.equals(pKorName)) {
        				if (conts.trim().equals("")) continue;
        				arrdata[i] = "";
        				return conts.getBytes();
        			}
    			}
    			else {
    				if (eng.equals(pEngName)) {
        				if (conts.trim().equals("")) continue;
        				arrdata[i] = "";
        				return conts.getBytes();
        			}
    			}
    			
            }
		}
 
		return data.getBytes();
	}
	
    private String GetGongUInfo()
    {
		String isql = " SELECT LU_NAME,APPL_CODE,COMP_OFFST,COMP_LEN,COMP_VAL1,COMP_VAL2,COMP_VAL3,COMP_VAL4,COMP_VAL5,BASE_YN ";
        isql = isql + "\n FROM TCHECKER_GONGU                         ";
        isql = isql + "\n WHERE LU_NAME = '" + COMMDATA.GetLU_NAME() + "' ";
 
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
 
		if (retdata == null || retdata.equals("")) return "";
        return retdata;
    }
    private String GetApplTxInfo(String pApplCode, String pKindCode, String pTxCode)
    {
		String isql = " SELECT NVL(T.INOUT_FLAG,'N'), NVL(T.MAP_FLAG,'B') ";
        isql = isql + "\n FROM TCHECKER_TXDETAIL T ";
        isql = isql + "\n WHERE T.APPL_CODE = '" + pApplCode + "' ";
        isql = isql + "\n   AND T.KIND_CODE = '" + pKindCode + "' ";
        isql = isql + "\n   AND T.TX_CODE = '" + pTxCode + "' ";
 
        String retdata = COMMDATA.GetDBManager().SearchData(isql);
        
		if (retdata == null || retdata.equals("")) {
			return "";
		}

		return retdata;
    	 
    }
	private String LoadMappingMsg(String pApplCode, String pKindCode, String pTxCode, String pInOut, String pWork)
	{
		if (pInOut.equals("REQUEST")) {
			String RetData = Communication("READ_TCPMSGREQ", "NOUSER" + "\t" + pApplCode + "\t" + pKindCode + "\t" + pTxCode + "\t" + pWork );
	    	if (RetData.trim().indexOf("Error:") >= 0) {
	    		COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", "SERVER:" + pApplCode + ":" + pKindCode + ":" + pTxCode + ":" + "요청전문을 불러오지 못하였습니다.:" + RetData);
                return "";
	    	}
	    	else {
    		    return RetData;
	    	}
		}
		if (pInOut.equals("RESPONSE")) {
			String RetData = Communication("READ_TCPMSGRES", "NOUSER" + "\t" + pApplCode + "\t" + pKindCode + "\t" + pTxCode + "\t" + pWork );
			if (RetData.trim().indexOf("Error:") >= 0) {
				COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", "SERVER:" + pApplCode + ":" + pKindCode + ":" + pTxCode + ":" + "응답전문을 불러오지 못하였습니다.:" + RetData);
				return "";
	    	}
			else {
				return RetData;
			}
		}
		return "";
	}
	
    private String Communication(String cmdstr, String senddata)
    {
    	SocketChannel socketChannel = null;
    	String recvdata = "";
    	try {
    		//NIO Connect
    		SocketAddress addr = new InetSocketAddress(gServerIP, Integer.parseInt(gManagerPort));
    		socketChannel = SocketChannel.open(addr);
    		
    		//NIO Send
            String cmd = cmdstr + "                                        ";
            String SendStr = String.format("%08d", senddata.getBytes().length) + cmd.substring(0,32) + senddata;
 
            ByteBuffer writebuffer = ByteBuffer.allocate(SendStr.getBytes().length);
            writebuffer.clear();
      	    writebuffer.put(SendStr.getBytes());
      	    writebuffer.flip();
      	    while(writebuffer.hasRemaining()){
      	    	socketChannel.write(writebuffer);
      	    }
 
      	    //데이타부 길이정보 읽기
      	    byte[] tmpbyte1 = new byte[8];
      	    ByteBuffer readbuffer1 = ByteBuffer.allocate(8);
      	    int readcnt1 = socketChannel.read(readbuffer1);
      	    if (readcnt1 <= 0 ) {
      	    	return "";
      	    }
      	    readbuffer1.rewind();
    	    readbuffer1.get(tmpbyte1);
      	  
      	    //NIO Recv
      	    int    tmplen  = Integer.parseInt(new String(tmpbyte1));
      	    byte[] tmpbyte = new byte[tmplen];
      	    ByteBuffer readbuffer = ByteBuffer.allocate(tmplen);
            socketChannel.read(readbuffer);
            readbuffer.rewind();
            readbuffer.get(tmpbyte);
      	    recvdata = new String(tmpbyte);
      	 
            readbuffer.clear();
            socketChannel.close();
	        return recvdata;
      
    	}
    	catch(Exception e)
    	{
    		if (socketChannel != null) try{socketChannel.close();}catch(Exception e1){}
    		JOptionPane.showMessageDialog(null, e.getMessage());
    	}
    	return "";
    }	
    
    public void getSystemInfo()
    {
        try {
	        Properties properties = new Properties();
	        properties.load(new FileInputStream("./Properties/System.inf"));
	        gAnylinkIP = properties.getProperty("ANYLINK_IP", "xxx.xxx.xxx.xxx");
	        gServerIP = properties.getProperty("SERVER_IP", "xxx.xxx.xxx.xxx");
	        gManagerPort = properties.getProperty("MANAGER_PORT", "xxxxx");
	        GWTrace_Mode = properties.getProperty("GWTRACE_MODE", "N");
	        gMapping_Mode = properties.getProperty("MAPPING_MODE", "ENG");
	        GWTrace_Mode = GWTrace_Mode.trim().toUpperCase();
	    
		} catch (Exception e) {
			gAnylinkIP = "xxx.xxx.xxx.xxx";
			gServerIP = "xxx.xxx.xxx.xxx";
			gManagerPort = "xxxxx";
		}
	 
    }
    private boolean isResponseMsg(String pApplCode, String pKindCode, String pTxCode)
    {
		String isql = " select t.res_flag, 'NO' ";
		isql = isql + "\n from altx t ";
        isql = isql + "\n where t.appl_code = '" + pApplCode + "' ";
        isql = isql + "\n   and t.rep_kind_code = '" + pKindCode + "' ";
        isql = isql + "\n   and t.tx_code = '" + pTxCode + "' ";
 
		String retdata = COMMDATA.GetDBManager().SearchData(isql);
		if (retdata == null || retdata.equals("")) {
			return false;
		}
 
        String[] arrtmp = retdata.split("\t");
        if (arrtmp[0].trim().equals("1")) return true;

    	return false;
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
	private void SendAsyncTcpMsg(String pApplCode, String pKindCode, String pTxCode, String pSendPortNo, String pSendData )
	{
		//확인완료
		try {
 
			String[] arrSendData = pSendData.split("\n");
			for(int i=0;i < arrSendData.length ;i++){
				if (arrSendData[i].trim().equals("")) break;
				String[] arrtmp = arrSendData[i].split("\t");
				if (arrtmp.length == 5) {
					arrSendData[i] = arrtmp[0] + "\t" + arrtmp[4] + "\t" + arrtmp[1] + "\t" + arrtmp[2] + "\t" + "<NODATA>";
				}
				else {
					if (arrtmp[3].trim().equals("")) arrtmp[3] = "<NODATA>";
					arrSendData[i] = arrtmp[0] + "\t" + arrtmp[5] + "\t" + arrtmp[1] + "\t" + arrtmp[2] + "\t" + arrtmp[3]  ;
				}
			}
			
			SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
			String reg_dt = formatter.format(new java.util.Date()); //등록 일시
			formatter = new java.text.SimpleDateFormat("HHmmss", java.util.Locale.KOREA);
			String reg_tm = formatter.format(new java.util.Date());  //등록시간 
			formatter = new java.text.SimpleDateFormat("S", java.util.Locale.KOREA);
			String reg_milsec = formatter.format(new java.util.Date());  //등록시간 
 
			String   fname = "./Request/Tcpmsg/" + pSendPortNo + "/" + reg_dt + "_" + reg_tm + "_" + reg_milsec ;
			
			//UserID + UserPCIP + ApplCode + KindCode + TxCode
			String   header = "Administrator" + "\tNOIPARRESS\t" + pApplCode + "\t" + pKindCode + "\t" + pTxCode + "<DATAGUBUN>";  
			
			//Directory Check
			File dir1 = new File("./Request");
			if (!dir1.exists()) dir1.mkdir();
			
			File dir2 = new File("./Request/Tcpmsg");
			if (!dir2.exists()) dir2.mkdir();
			
			File dir3 = new File("./Request/Tcpmsg/" + pSendPortNo);
			if (!dir3.exists()) dir3.mkdir();
			
			//tcpmsg 디렉토리에 송신할 전문을 저장한다.
			String SendData = "";
			for(int i=0;i < arrSendData.length ;i++){
				if (arrSendData[i].trim().equals("")) break;
				SendData = SendData + arrSendData[i] + "\n";
			}
			
			DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(fname)));
			out.write(header.getBytes());
			out.write(SendData.getBytes());
			
			if (!MsgBodySend.trim().equals("")){
				out.write("<BODYBYPASS>".getBytes());
				out.write(MsgBodySend.getBytes());
			}
			out.close();
 	
			//COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", SendData);
			
		}catch(Exception e) {
			COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", e);
		}
 
	} 
}

