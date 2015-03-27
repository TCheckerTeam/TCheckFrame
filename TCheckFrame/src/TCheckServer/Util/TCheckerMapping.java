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
     
    	//���������� �����ϰ�, ApplCode, KindCode, TxCode �� �����Ѵ�.
    	usergongu.SetRECVDATA(pRecvData);
    	
 
		usergongu.SetAPPL_CODE(gApplCode);
		usergongu.SetKIND_CODE(gKindCode);
		usergongu.SetTX_CODE(gTxCode);
		usergongu.GetGongUInfo();
  
  
        //Anylink�� ���� ���� ����Ÿ�� ���� ���Ÿ��������� �о�´�.
        RecvMsgMapInfo = LoadMappingMsg(usergongu.GetAPPL_CODE(), usergongu.GetKIND_CODE(), usergongu.GetTX_CODE(), "REQUEST", "READ");
        MakeTreeData(usergongu.GetAPPL_CODE(), usergongu.GetKIND_CODE(), usergongu.GetTX_CODE(), RecvMsgMapInfo, rootrecv);
        MsgBodyRecv = MsgBody;
        COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "SERVER:" + ResApplCode + ":" + ResKindCode + ":" + ResTxCode + ":MsgBodyRecv[" + MsgBodyRecv + "]");
        
        //������ ����Ÿ�� tree�� �����Ѵ�.
        int idx = 0;
		final HashMap<String, String> hashinrt = new HashMap<String, String>();
		SetEditDataSub(rootrecv,rootrecv, hashinrt, idx, pRecvData);
        
		//������� �������� Ȯ���Ѵ�.
    	if (isResponseMsg(usergongu.GetAPPL_CODE(), usergongu.GetKIND_CODE(), usergongu.GetTX_CODE()) != true ) {
    		String ResInfo = SearchResLinkOne(usergongu.GetAPPL_CODE(), usergongu.GetKIND_CODE(), usergongu.GetTX_CODE());
    		if (ResInfo.equals("")) return null;
    		
    		String[] arrResInfo = ResInfo.split("\t");
    		ResApplCode = arrResInfo[0];
    		ResKindCode = arrResInfo[2];
    		ResTxCode   = arrResInfo[4];
    		ResPortNo   = arrResInfo[6];
    	}
 
    	//Anylink�� ����۽��� ���������� �о�´�. �۽������� tree�� �����Ѵ�.
        SendMsgMapInfo = LoadMappingMsg(usergongu.GetAPPL_CODE(), usergongu.GetKIND_CODE(), usergongu.GetTX_CODE(), "RESPONSE", "READ");
        MakeTreeData(usergongu.GetAPPL_CODE(), usergongu.GetKIND_CODE(), usergongu.GetTX_CODE(), SendMsgMapInfo,rootsend);
        COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "SERVER:" + ResApplCode + ":" + ResKindCode + ":" + ResTxCode + ":MsgBody[" + MsgBody + "]");
        
        if (Body_ByPass_Flag == true && !MsgBody.trim().equals("")) {
        	MsgBodySend = MsgBody;
        }
        COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "SERVER:" + ResApplCode + ":" + ResKindCode + ":" + ResTxCode + ":MsgBodySend[" + MsgBodySend + "]");
        
        //SendMsg�� �����Ѵ�.
        byte[] makeMsg = MakeSendMsg(usergongu.GetAPPL_CODE(), usergongu.GetKIND_CODE(), usergongu.GetTX_CODE());
        if (makeMsg.length > 10){
			String tmpstr = new String(makeMsg);
			if (tmpstr.substring(0,6).equals("ERROR:")) {
	    		COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", tmpstr);
	    		return null;
			}
			else {
				if (ResPortNo.trim().equals("") || ResPortNo.trim().equals("0")) return makeMsg;
				
				//�ٸ� ����/��Ʈ�� ���� �Ƿ�
				COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "SERVER:" + ResApplCode + ":" + ResKindCode + ":" + ResTxCode + ":Async��û�� ���� �������� �۽Ÿ� �Ƿ��Ͽ����ϴ�.[" + ResPortNo + "]");
				SendAsyncTcpMsg(ResApplCode, ResKindCode, ResTxCode, ResPortNo, gReadTreeData);
				return null;
			}
        }
        else {
        	COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", usergongu.GetAPPL_CODE() + ":" + usergongu.GetKIND_CODE() + ":" + usergongu.GetTX_CODE() + ":���������� �����ϴµ� ������ �߻��Ͽ����ϴ�.");
        }
    	return null;
    }
    private void MakeTreeData(String pApplCode, String pKindCode, String pTxCode, String pMsgMapInfo, DefaultMutableTreeNode mytree)
    {
    	//Body�κп� ���� Bypass �������� �����Ѵ�.
    	Body_ByPass_Flag = false;
    	String TxDetailInfo = GetApplTxInfo(pApplCode,pKindCode,pTxCode);
		String[] arrtmp = TxDetailInfo.split("\n")[0].split("\t");
		if (arrtmp[1].equals("P")) Body_ByPass_Flag = true;

		COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", pApplCode + ":" + pKindCode + ":" + pTxCode + ":" + TxDetailInfo + " --> " + arrtmp[1]);
				 
		
    	//Array �� Struct �� ���� Work TreeNode �� �����Ѵ�.
    	DefaultMutableTreeNode Array1 = null;
    	DefaultMutableTreeNode Array2 = null;
    	DefaultMutableTreeNode Array3 = null;
    	DefaultMutableTreeNode Struct1 = null;
    	DefaultMutableTreeNode Struct2 = null;
    	DefaultMutableTreeNode Struct3 = null;
    	MsgBody = "";

    	//Tree�� Null �̸�, �׳� �����Ѱ�, �׷��� ������ Tree�� ��� Child Node�� �����Ѵ�.
   	    if (mytree == null) return ;
   	    mytree.removeAllChildren();

   	    //pMsgMapInfo ���� Body�� Bypass ���ο� ���� ������ ������� �� �����Ƿ� <BODYBYPASS> �� �����Ͽ�, MsgMap������ �и��Ѵ�.
   	    String[] gridData = new String[6];
   	    String[] arrpMsgMapInfo = pMsgMapInfo.split("<BODYBYPASS>");
   	    if (arrpMsgMapInfo.length > 1) MsgBody = arrpMsgMapInfo[1];
   	    String   MapInfo = arrpMsgMapInfo[0];
   	    String[] arrMainData = MapInfo.replace("\r","").split("\n");
   	 
        //MsgMap �������� Array Len �� #�� �� �ִ� �׸��� �����Ѵ�.
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
 
        //MsgMap ������ ���� TreeNode�� �����Ѵ�.
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
        		COMMDATA.GetTCheckerLog().WriteLog("W", "TcpManager", pApplCode + ":" + pKindCode + ":" + pTxCode + ":�ش� �ŷ��� ���� ���α��������� Ȯ���ϼ���.");
        	}
        	else {
        		COMMDATA.GetTCheckerLog().WriteLog("W", "TcpManager", pApplCode + ":" + pKindCode + ":" + pTxCode + ":���������� Ȯ���ϼ���");
        	}
        	
        }
 
        SetArrayHandle(mytree);
 
        return ;
    }
 
    private void SetArrayHandle(DefaultMutableTreeNode mytree)
    {
		try {
			//1�� �迭�� Count�� �����Ͽ�, �� ������ŭ Structure �� �÷��ش�.
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
			
			//2�� �迭�� Count�� �����Ͽ�, �� ������ŭ Structure �� �÷��ش�.
			for(int i=0;i < mytree.getChildCount();i++)
			{
				  DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) mytree.getChildAt(i);  
				  for(int j=0;j < dataNode.getChildCount();j++){
					  //1���迭�� �ִ� Struct
					  DefaultMutableTreeNode dataStructNode = (DefaultMutableTreeNode)dataNode.getChildAt(j);
					  for(int k=0;k < dataStructNode.getChildCount();k++) {
						  //1���迭�� �ִ� Member
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
			
			//3�� �迭�� Count�� �����Ͽ�, �� ������ŭ Structure �� �÷��ش�.
			for(int i=0;i < mytree.getChildCount();i++)
			{
				  DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) mytree.getChildAt(i);  
				  for(int j=0;j < dataNode.getChildCount();j++){
					  //1���迭�� �ִ� Struct
					  DefaultMutableTreeNode dataStructNode = (DefaultMutableTreeNode)dataNode.getChildAt(j);
					  for(int k=0;k < dataStructNode.getChildCount();k++) {
						  //1���迭�� �ִ� Member
						  DefaultMutableTreeNode dataMemberNode = (DefaultMutableTreeNode)dataStructNode.getChildAt(k);
						  
						  for(int m=0;m < dataMemberNode.getChildCount();m++){
							 //2���迭�� �ִ� Struct  
							  
							  DefaultMutableTreeNode dataNode1 = (DefaultMutableTreeNode) dataMemberNode.getChildAt(m); 
							  for(int n=0;n < dataNode1.getChildCount();n++){
								  //2���迭�� �ִ� Member
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
			
			//��� �׸� No �� �ο��Ѵ�.
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
	         
	            //Arrany ���̺����� ���Ǵ� �׸��� �����ϱ� ���� ��� ���������� hash�� put
	            hash.put(data.getEng(), new String(tmpbyte));
	            
	           // COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "SERVER:SERVER:SERVER:SERVER:��û�׸�/��=.[" + data.getEng() + ":" + new String(tmpbyte));
	            
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
            
	        //������� ó���� �Ѵ�.
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

            	//�÷��� ũ�⸸ŭ�� ���۸� �Ҵ��Ͽ�, ����Ÿ�� �����Ѵ�.
            	if (arrtmp[3].equals("<NODATA>")) arrtmp[3] = "";
            	byte[] mapdata = userfunction.Parsing(arrtmp[3].getBytes(), pApplCode, pKindCode, pTxCode);
            	if (mapdata == null ){
        		    //�۽��� �׸��� ���� �����̸�, Anylink�� ���� ���Ź��� ����Ÿ���� ������ �׸��� �����Ͽ�, ������ ��.
        		    mapdata = GetFindMappingValue(arrRecvData, arrtmp[0], arrtmp[5]);
        		    COMMDATA.GetTCheckerLog().WriteLog("D", "TcpManager", "SERVER:" + pApplCode + ":" + pKindCode + ":" + pTxCode + ":Mapping:" + arrtmp[5] + "=" + mapdata);
            	}
            	else {
            		if (mapdata.length == 0) {
            		    //�۽��� �׸��� ���� �����̸�, Anylink�� ���� ���Ź��� ����Ÿ���� ������ �׸��� �����Ͽ�, ������ ��.
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

            	if (i == 0 && arrtmp[0].equals("����������Ÿ")){
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
			
	        //���̼�������� 00:�����ʿ� �̸�, ������ �����Ѵ�.
            if (COMMDATA.GetLEN_TYPE().equals("00")){
            	String errmsg = "SERVER:" + pApplCode + ":" + pKindCode + ":" + pTxCode + ":" + "���̼�������� �����ϼ���.";
                COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", errmsg); 
                return ("ERROR:" + errmsg).getBytes(); 
            }
    
            /*-------- ���Ÿ���� ������ �� ��������� ��쿡 HEAD ���ۿ� ���������� �����Ѵ�. --------*/
 
            MSGSIZE = MSGDATA.length ;
            
            if (COMMDATA.GetCOMM_HEAD_TYPE().equals("0") ){
            	try {
            		HEAD = new byte[Integer.parseInt(COMMDATA.GetCOMM_HEAD_SIZE())];
            		for(int i = 0;i < HEAD.length;i++) HEAD[i] = (byte)32;
            		
	            	if (COMMDATA.GetLEN_TYPE().equals("10")){
	            		//���������� �����ʵ� ������ ���̰� : �������� ��� �޼����� ��üũ�⸦ ���������� �����Ѵ�.
		    			String SendLenFmt = String.format("%%0%dd", Integer.parseInt(COMMDATA.GetLEN_SIZE()));
		    			String SendLen = String.format(SendLenFmt, MSGSIZE);
	            		System.arraycopy(SendLen.getBytes(), 0, HEAD, Integer.parseInt(COMMDATA.GetLEN_OFFST()), SendLen.getBytes().length);
	            	}
	            	else if (COMMDATA.GetLEN_TYPE().equals("50")){
	            		//�����ʵ� ������ ���̰�(Integer) : ��������� ��쿡 �޼����� ��üũ�⿡�� ���� Offset �� ���� Size �� �� ������� ���������� �����Ѵ�.
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
	            		String errmsg = "SERVER:" + pApplCode + ":" + pKindCode + ":" + pTxCode + ":" + "������ �� ��쿡 ���̼�������� [10:�����ʵ� ������ ���̰�] [50:�����ʵ� ������ ���̰�(Integer)] �̾�� �մϴ�.";
	            		COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", errmsg);
	            		return ("ERROR:" + errmsg).getBytes();
	            	}
	            	
	        		//���۵���Ÿ ����
            	
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
	            		//���������� �����ʵ� ������ ���̰� : ��������� ��쿡 �޼����� ��üũ�⿡�� ���� Offset �� ���� Size �� �� ������� ���������� �����Ѵ�.
		    			String SendLenFmt = String.format("%%0%dd", Integer.parseInt(COMMDATA.GetLEN_SIZE()));
		    			String SendLen = String.format(SendLenFmt, MSGSIZE - Integer.parseInt(COMMDATA.GetLEN_OFFST()) - Integer.parseInt(COMMDATA.GetLEN_SIZE()));
	            		System.arraycopy(SendLen.getBytes(), 0, tmpdat, Integer.parseInt(COMMDATA.GetLEN_OFFST()), SendLen.getBytes().length);
	            	}
	            	else if (COMMDATA.GetLEN_TYPE().equals("11")){
	            		//�����ʵ带 ������ ���̰� : ��������� ��쿡 �޼����� ��üũ�⸦ �����Ѵ�.
		    			String SendLenFmt = String.format("%%0%dd", Integer.parseInt(COMMDATA.GetLEN_SIZE()));
		    			String SendLen = String.format(SendLenFmt, MSGSIZE);
	            		System.arraycopy(SendLen.getBytes(), 0, tmpdat, Integer.parseInt(COMMDATA.GetLEN_OFFST()), SendLen.getBytes().length);
	            	}
	            	else if (COMMDATA.GetLEN_TYPE().equals("12")){
	            		//Body�� ���̰� : ��������� ��쿡 �޼����� ��üũ�⿡�� Head Size �� ������� ���������� �����Ѵ�.
		    			String SendLenFmt = String.format("%%0%dd", Integer.parseInt(COMMDATA.GetLEN_SIZE()));
		    			String SendLen = String.format(SendLenFmt, MSGSIZE - Integer.parseInt(COMMDATA.GetCOMM_HEAD_SIZE()));
	            		System.arraycopy(SendLen.getBytes(), 0, tmpdat, Integer.parseInt(COMMDATA.GetLEN_OFFST()), SendLen.getBytes().length);
	            	}
	            	else if (COMMDATA.GetLEN_TYPE().equals("50")){
	            		//�����ʵ� ������ ���̰�(Integer) : ��������� ��쿡 �޼����� ��üũ�⿡�� ���� Offset �� ���� Size �� �� ������� ���������� �����Ѵ�.
	            		int tmplen = MSGSIZE - Integer.parseInt(COMMDATA.GetLEN_OFFST()) - Integer.parseInt(COMMDATA.GetLEN_SIZE());
	            		tmpdat[Integer.parseInt(COMMDATA.GetLEN_OFFST()) + 0] = (byte)(tmplen / 256);
	            		tmpdat[Integer.parseInt(COMMDATA.GetLEN_OFFST()) + 1] = (byte)(tmplen % 256);	
	            	}
	            	else if (COMMDATA.GetLEN_TYPE().equals("51")){
	            		//�����ʵ带 ������ ���̰�(Integer) : ��������� ��쿡 �޼����� ��üũ�⸦ ���������� �����Ѵ�.
	            		int tmplen = MSGSIZE;
	            		tmpdat[Integer.parseInt(COMMDATA.GetLEN_OFFST()) + 0] = (byte)(tmplen / 256);
	            		tmpdat[Integer.parseInt(COMMDATA.GetLEN_OFFST()) + 1] = (byte)(tmplen % 256);	
	            	}
	            	else if (COMMDATA.GetLEN_TYPE().equals("52")){
	            		//Body�� ���̰�(Integer) : ��������� ��쿡 �޼����� ��üũ�⿡�� Head Size �� ������� ���������� �����Ѵ�.
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
	            		String errmsg = "SERVER:" + pApplCode + ":" + pKindCode + ":" + pTxCode + ":" + "������� �� ��쿡 ���̼�������� [13:��������] [14:END����]�� ����� �� �����ϴ�.";
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
	            	//�������� �������� ����� ��� �������� ��ŭ�� ���۸� �Ҵ��Ͽ�, �޼����� �����Ѵ�.
	            	byte[] tmpdat = new byte[Integer.parseInt(COMMDATA.GetLEN_SIZE())];
	            	System.arraycopy(MSGDATA, 0, tmpdat, 0, MSGDATA.length);
	            	return tmpdat;
 	    	    }catch(Exception e1){
          		    COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", e1);
          	    }
            }
            if (COMMDATA.GetCOMM_HEAD_TYPE().equals("3")){
            	//End���� ����� ��쿡 �޼����� �״�� �����Ѵ�.
            	return MSGDATA;
            }
		 
		}catch(Exception e){}
		
		String errmsg = "SERVER:" + pApplCode + ":" + pKindCode + ":" + pTxCode + ":" + "�� ���� ������� �� ���̼�������� Ȯ�ΰų� ��û������ Ȯ���ϼ���.";
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
	    		COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", "SERVER:" + pApplCode + ":" + pKindCode + ":" + pTxCode + ":" + "��û������ �ҷ����� ���Ͽ����ϴ�.:" + RetData);
                return "";
	    	}
	    	else {
    		    return RetData;
	    	}
		}
		if (pInOut.equals("RESPONSE")) {
			String RetData = Communication("READ_TCPMSGRES", "NOUSER" + "\t" + pApplCode + "\t" + pKindCode + "\t" + pTxCode + "\t" + pWork );
			if (RetData.trim().indexOf("Error:") >= 0) {
				COMMDATA.GetTCheckerLog().WriteLog("E", "TcpManager", "SERVER:" + pApplCode + ":" + pKindCode + ":" + pTxCode + ":" + "���������� �ҷ����� ���Ͽ����ϴ�.:" + RetData);
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
 
      	    //����Ÿ�� �������� �б�
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
		//Ȯ�οϷ�
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
			String reg_dt = formatter.format(new java.util.Date()); //��� �Ͻ�
			formatter = new java.text.SimpleDateFormat("HHmmss", java.util.Locale.KOREA);
			String reg_tm = formatter.format(new java.util.Date());  //��Ͻð� 
			formatter = new java.text.SimpleDateFormat("S", java.util.Locale.KOREA);
			String reg_milsec = formatter.format(new java.util.Date());  //��Ͻð� 
 
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
			
			//tcpmsg ���丮�� �۽��� ������ �����Ѵ�.
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

