package User;
 
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.JXTreeTable;

import User.DataModel.myTcpMapComm;
  
public class ThreadListenerRecvSub extends Thread{ 

	private Socket         one_client = null;
	private String         PortNo = "";
	private JTextArea      txtRecv ;
	private DefaultMutableTreeNode mytree ;
	private JXTreeTable    mytable;
	private JTextArea      txtMsgBody;
	private JTextArea      txtMsg;
	private int            gIndex = 0;
    public ThreadListenerRecvSub(Socket                 client, 
    		                     JTextArea              txtRecv, 
    		                     DefaultMutableTreeNode mytcptree, 
    		                     JXTreeTable            myPaneTcpResTable, 
    		                     JTextArea              txtMsgBody,
    		                     JTextArea              txtMsg)
    { 
    	this.one_client = client;
    	this.txtRecv    = txtRecv;
    	this.mytree     = mytcptree;
    	this.mytable    = myPaneTcpResTable;
    	this.txtMsgBody = txtMsgBody;
    	this.txtMsg     = txtMsg;
    }
  
	public void run()
	{  
    	DataOutputStream dos = null;
    	DataInputStream dis = null;
    	byte[] lenbyte  = new byte[8];
        byte[] cmdbyte  = new byte[10];
 
 		while(!Thread.currentThread().isInterrupted()) {
            try{
            	one_client.setSoTimeout(10000);
                dos = new DataOutputStream(one_client.getOutputStream());
                dis = new DataInputStream(one_client.getInputStream());
            	
            	int rc = dis.read(lenbyte);
            	if (rc != 8) {
            		setThreadSleep(100);
            		continue;
            	}
                
                //cmd 을 읽어오고, ID 및 PWD를 체크를 한다.
                dis.read(cmdbyte);
                String cmd = new String(cmdbyte);
 
                //Image Data 를 읽어온다.
                byte[] recvdata = new byte[Integer.parseInt(new String(lenbyte).trim())];
                for(int i = 0 ; i < recvdata.length ;i++){
               	    recvdata[i] = dis.readByte();
    	        }
 
    			if (cmd.indexOf("RESPONSEUL") >= 0) Proc_ResponseURL(new String(recvdata));
    			if (cmd.indexOf("RESPONSETP") >= 0) Proc_ResponseTCP(new String(recvdata));
    	 
            }catch(EOFException e1){
            	break;
            }catch(IOException e2){
            	setThreadSleep(1000);
            } 
 		}
	}
	
	private void Proc_ResponseURL(String recvdata)
	{
		txtRecv.setText(recvdata);
		txtRecv.setCaretPosition(0);
 
	}
    public void Proc_ResponseTCP(String recvdata)
    {
    	if (recvdata.substring(0,6).equals("ERROR:")) {
    		JOptionPane.showMessageDialog(null,recvdata.replace("ERROR:", ""));
    		return;
    	}
    	gIndex = 0;
    	
    	WriteMsg("수신 전문데이터를 출력\n[" + recvdata + "]\n");

    	
        //응답전문에 대한 데이타를 셋팅한다.
        int rowidx = 0;
		final HashMap<String, String> hashinrt = new HashMap<String, String>();
		SetEditDataSub(mytree,mytree, hashinrt, rowidx, recvdata);
		mytable.updateUI();
		mytable.updateUI();
		hashinrt.clear();
    }
    
    private int SetEditDataSub(DefaultMutableTreeNode myroot, DefaultMutableTreeNode myArrayNode, HashMap<String, String> hash, int idx, String indata  )
    {
    	for(int i=0;i < myArrayNode.getChildCount();i++){
    		DefaultMutableTreeNode WorkNode = (DefaultMutableTreeNode) myArrayNode.getChildAt(i);  
    		myTcpMapComm data = (myTcpMapComm) WorkNode.getUserObject();
  		    String item = data.getItem();
 
		    if (data.getType().indexOf("STRUCT-S") >= 0) continue;
		    if (data.getType().indexOf("ARRAY-S") >= 0 ) {
			    if (data.getLen().indexOf("#") >= 0) {
				  String lenval = hash.get(data.getLen());
				  data.setLen(lenval);
				  SetArrayHandle(myroot);
			    }
        		for(int j=0;j < WorkNode.getChildCount();j++){
        			idx = SetEditDataSub(myroot, (DefaultMutableTreeNode)WorkNode.getChildAt(j), hash, idx, indata);
        		}
        		continue;
		    }
    			  
        	byte[] tmpbyte = new byte[Integer.parseInt(data.getLen())];
    		System.arraycopy(indata.getBytes(), idx, tmpbyte, 0, Integer.parseInt(data.getLen()));
            idx += Integer.parseInt(data.getLen());
            data.setConts(new String(tmpbyte));
            
            //항목명에 ":#" 이 들어가 있는 항목에 대한 값을 추출하여, hash table에 put한다.
            if (data.getItem().indexOf(":#") > 0) {
          	    String[] arrtmpsub = data.getItem().split(":#");
          	    hash.put("#"+arrtmpsub[1], new String(tmpbyte));
            }
    	}
 
        return idx;
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
    	DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new myTcpMapComm(data.getItem(), data.getType(), data.getLen(), "","",data.getEng(),true));  
    	
    	for(int i=0;i < dataStructNode.getChildCount();i++)
    	{
    		DefaultMutableTreeNode tmpnode = (DefaultMutableTreeNode)dataStructNode.getChildAt(i);
    		data = (myTcpMapComm) tmpnode.getUserObject(); 
 
    		DefaultMutableTreeNode tmpnodenew = new DefaultMutableTreeNode(new myTcpMapComm(data.getItem(), data.getType(), data.getLen(), "","",data.getEng(),true));  
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
    private static void setThreadSleep(int time)
    {
   	    try{
            Thread.sleep(time);
        } catch(InterruptedException e) {}
    } 
    private void WriteMsg(String msg)
    {
		SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
		String reg_dt = formatter.format(new java.util.Date()); //등록 일시
		formatter = new java.text.SimpleDateFormat("[HH:mm:ss]", java.util.Locale.KOREA);
		String reg_tm = formatter.format(new java.util.Date());  //등록시간 

    	txtMsg.insert(reg_tm + msg + "\n", 0);
		txtMsg.setCaretPosition(0);
		
    }
   	   
}
