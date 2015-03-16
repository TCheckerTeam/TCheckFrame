package Manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.JXTreeTable;

import Manager.DataModel.myComboxModel;
import Manager.DataModel.myLineInfoComm;
import Manager.DataModel.myLineInfoModel;


public class TCheckPanelLine {
	private JPanel mypanel = null;
	private JXTreeTable myPaneLineTable;
	private DefaultMutableTreeNode permitroot = null; 

	public TCheckPanelLine(JPanel panel)
	{
		mypanel = panel;
		myPaneLineInit();
  
	}
    public void myPaneLineInit()
    {
 
    	//회선정보 화면에 대한 초기 Component 설정
    	mypanel.setLayout(new BorderLayout());
        
    	//상위 Command Button 설정
    	JPanel myPaneLineSub1  = new JPanel(); myPaneLineSub1.setBackground(Color.WHITE);
    	JButton btnRefresh, btnSave;
    	btnRefresh = new JButton("갱신하기",new ImageIcon("./Image/refresh.gif")); 
    	btnSave = new JButton("저장하기", new ImageIcon("./Image/save.gif"));
    	
    	myPaneLineSub1.setLayout(new FlowLayout());
    	myPaneLineSub1.add(btnRefresh);
    	myPaneLineSub1.add(btnSave);
   
    	mypanel.add(myPaneLineSub1,BorderLayout.NORTH);
    	
    	btnRefresh.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				myPaneLineRefresh();
			}
    	});
    	btnSave.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				String savedata = "";
 
				for(int i=0;i < permitroot.getChildCount();i++)
				{
					DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) permitroot.getChildAt(i);  
		            myLineInfoComm data = (myLineInfoComm) dataNode.getUserObject();  
		 
                    String tmpstr = "";
                    tmpstr = tmpstr + data.getApplCode()                   + "\t"; 
                    tmpstr = tmpstr + data.getApplName()                   + "\t";
                    tmpstr = tmpstr + data.getPortNo()                     + "\t";
                    tmpstr = tmpstr + data.getConnMethod().split(":")[0]   + "\t";
                    tmpstr = tmpstr + data.getHeaderType().split(":")[0]   + "\t";
                    tmpstr = tmpstr + data.getHeaderSize()                 + "\t";
                    tmpstr = tmpstr + data.getLenMethod().split(":")[0]    + "\t";
                    tmpstr = tmpstr + data.getLenOffset()                  + "\t";
                    tmpstr = tmpstr + data.getLenSize()                    + "\t";
                    tmpstr = tmpstr + data.getUserYN().split(":")[0]       + "\n";
					savedata = savedata + tmpstr ;
				}
				 
				SaveTcheckerLineInfo_LEN(savedata);
    		}
       	});
   
    	 // Table Component Setting 
		 DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new myLineInfoComm("Root", "", "", "", "","", "", "", "", "", true));  
 
		 permitroot = rootNode;
		 myPaneLineTable = new JXTreeTable();
		 myPaneLineTable.setTreeTableModel(new myLineInfoModel(rootNode));  
		 myPaneLineTable.setEditable(true);
		 myPaneLineTable.setRootVisible(false);
		 myPaneLineTable.setCellSelectionEnabled(true);
 
	 
 
		 //컬럼사이즈 설정
		 final int[] columnsWidth = {80, 180, 80, 140, 80, 80, 160, 80, 80, 80};
		 for(int i=0; i < columnsWidth.length;i++){
			 myPaneLineTable.getColumnModel().getColumn(i).setPreferredWidth(columnsWidth[i]);
		 }
 
         JScrollPane myPaneLineSub2 = new JScrollPane(myPaneLineTable);
         mypanel.add(myPaneLineSub2, BorderLayout.CENTER);
         myPaneLineSub2.setAutoscrolls(true);
  
         
         // Table Row Data 셋팅
         myPaneLineRefresh();
    }
    public void myPaneLineRefresh()
    {
 
   	    if (permitroot == null) return;
    	permitroot.removeAllChildren();
    	
    	//업무별 Kind/Tx에 대한 Offset/Size를 읽어온다.
    	String OffsetSizeData = GetKindTx_OffsetSize();
    	String[] arrOffsetSizeData = null;
    	if (OffsetSizeData != null) {
    		arrOffsetSizeData = OffsetSizeData.split("\n");
    	}
    	
    	//TCHECKER_LINEINFO 전체 Read
    	String TcheckerLineInfoData = GetTcheckerLineInfoData();
    	String[] arrTcheckerLineInfoData = null;
    	if (TcheckerLineInfoData != null) {
    		arrTcheckerLineInfoData = TcheckerLineInfoData.split("\n");
 
    	}
 
    	//업무별 회선정보를 읽어온다.
    	String RData = GetLineInfo();
    	
    	System.out.println("회선정보 : ["+RData+"]");
    	
    	String[] arrRData = null;
    	if (RData != null) {
            arrRData = RData.split("\n");
     	}
    	
    	// 신규 RowData를 셋팅한다.
    	if (RData != null) {
    		String[] gridData = new String[10];
    		
            arrRData = RData.split("\n");
            for(int i=0;i < arrRData.length;i++){
            	if (arrRData[i].trim().equals("") || arrRData[i] == null) break;
                String[] rowData = arrRData[i].split("\t");
 
        		
                gridData[0] = rowData[0];  //업무코드
                gridData[1] = rowData[1];  //업무명
                gridData[2] = rowData[4];  //Port No
                
                /*------------ 연결 방식 ----------------------*/
                if ( rowData[5].equals("1"))  gridData[3] = rowData[5] + ":" + "Server";
	    	    if ( rowData[5].equals("2"))  gridData[3] = rowData[5] + ":" + "Client";
	    	    if ( rowData[5].equals("3"))  gridData[3] = rowData[5] + ":" + "Client(수시)";
	    	    if ( rowData[5].equals("7"))  gridData[3] = rowData[5] + ":" + "Client(MegaBox EOR)";
	    	    if ( rowData[5].equals("10")) gridData[3] = rowData[5] + ":" + "Server(MegaBox EOR)";
		    	
	    	    /*------------- 헤더타입 -------------------*/
	    	    if ( rowData[6].equals("0")) gridData[4] = rowData[6] + ":" + "통신헤더";
		    	if ( rowData[6].equals("1")) gridData[4] = rowData[6] + ":" + "공통헤더";
		    	if ( rowData[6].equals("2")) gridData[4] = rowData[6] + ":" + "고정길이";
		    	if ( rowData[6].equals("3")) gridData[4] = rowData[6] + ":" + "END문자";
		    	
		    	gridData[5] = rowData[7];  //헤더크기
	 
		    	/*------------- 길이설정방법 -------------------*/
		    	gridData[6] = "00:설정필요";
		    	gridData[7] = "0";  //길이 Offset
    	    	gridData[8] = "0";  //길이 Size
    	    	gridData[9] = "2:미사용";

		    	
		    	for(int j=0;j < arrTcheckerLineInfoData.length;j++){
		    		if (arrTcheckerLineInfoData[j].trim().equals("") || arrTcheckerLineInfoData[j] == null) break;
		    		String[] arrtmp = arrTcheckerLineInfoData[j].split("\t");
		    		if (arrtmp[0].equals(rowData[0]) && arrtmp[1].equals(rowData[4]) && arrtmp[2].equals(rowData[5])) {
		    	    	/*------------- 길이설정방법 -------------------*/
		    	    	if ( arrtmp[6].equals("0") || arrtmp[6].equals(""))  {
		    	    		gridData[6] = "00:설정필요";
		    	    		if ( rowData[6].equals("0")) gridData[6] = "10:길이필드 이후의 길이";
		    	    		if ( rowData[6].equals("1")) gridData[6] = "11:길이필드를 포함한 길이";
		    	    		if ( rowData[6].equals("2")) gridData[6] = "13:고정길이";
		    	    		if ( rowData[6].equals("3")) gridData[6] = "14:END문자";
		    	    	}
		    	    	if ( arrtmp[6].equals("10")) gridData[6] = "10:길이필드 이후의 길이";
		    	    	if ( arrtmp[6].equals("11")) gridData[6] = "11:길이필드를 포함한 길이";
		    	    	if ( arrtmp[6].equals("12")) gridData[6] = "12:Body부 길이";
		    	    	if ( arrtmp[6].equals("13")) gridData[6] = "13:고정길이";
		    	    	if ( arrtmp[6].equals("14")) gridData[6] = "14:END문자";
		    	    	if ( arrtmp[6].equals("50")) gridData[6] = "50:길이필드 이후의 길이(Integer)";
		    	    	if ( arrtmp[6].equals("51")) gridData[6] = "51:길이필드를 포함한 길이(Integer)";
		    	    	if ( arrtmp[6].equals("52")) gridData[6] = "52:Body부 길이(Integer)";
		    	    	if ( arrtmp[6].equals("99")) gridData[6] = "99:사용자정의";
		    	    	
		    	    	gridData[7] = arrtmp[7];  //길이 Offset
		    	    	gridData[8] = arrtmp[8];  //길이 Size
		    	    	if (arrtmp[3].equals("1")){
		    	    		gridData[9] = "1:사용";
		    	    	}
 
		    		}
		    	}
	            permitroot.add(new DefaultMutableTreeNode(new myLineInfoComm(gridData[0] , gridData[1], 
                        gridData[2] , gridData[3], 
                        gridData[4] , gridData[5],
                        gridData[6] , gridData[7], 
                        gridData[8] , gridData[9],			
                        false)));
            }
            
            CheckLineInfo();
     	}

    	myPaneLineTable.setTreeTableModel(new myLineInfoModel(permitroot));
    	 
		//컬럼사이즈 설정
		final int[] columnsWidth = {80, 180, 80, 140, 80, 80, 160, 80, 80, 80 };
		for(int i=0; i < columnsWidth.length;i++){
		    myPaneLineTable.getColumnModel().getColumn(i).setPreferredWidth(columnsWidth[i]);
		}
 
		for(int i=0;i < columnsWidth.length ;i++) {
			
			if (i==6) {
 
				//길이 Type에 대한 Combo 설정
				JComboBox comblentype = new JComboBox();
			 
				comblentype.addItem("00:설정필요");
				comblentype.addItem("10:길이필드 이후의 길이");
				comblentype.addItem("11:길이필드 포함한 길이");
				comblentype.addItem("12:Body부 길이");
				comblentype.addItem("13:고정길이");
				comblentype.addItem("14:END문자");
				comblentype.addItem("50:길이필드 이후의 길이(Int)");
				comblentype.addItem("51:길이필드 포함한 길이(Int)");
				comblentype.addItem("52:Body부 길이(Int)");
				comblentype.addItem("99:사용자정의");
				
		         myPaneLineTable.getColumnModel().getColumn(i).setCellEditor(new myComboxModel(comblentype));
			}
			if (i==9) {
				//사용여부 대한 Combo 설정
				JComboBox combuserflag = new JComboBox();
				combuserflag.addItem("1:사용");
				combuserflag.addItem("2:미사용");
		 
				myPaneLineTable.getColumnModel().getColumn(i).setCellEditor(new myComboxModel(combuserflag));
			}
			if (i >= 6 ){
				DefaultTableCellRenderer  renderer1 = new DefaultTableCellRenderer();
				renderer1.setBackground(new Color(217,255,217));
				myPaneLineTable.getColumnModel().getColumn(i).setCellRenderer(renderer1);
			}
		}
 
    }
    
    private String GetLineInfo()
    {
    	try {
    		String RetData = Communication("READ_LINEINFO", "NODATA");
        	if (RetData.trim().equals("") || RetData.trim().equals("NOT-FOUND")) {
        		JOptionPane.showMessageDialog(null,"회선정보를 가져오지 못했습니다.");
        		return "";
        	}
        	return RetData;
    	}catch(Exception e) { 
    		return "";
    	}
    }
    
    private String GetKindTx_OffsetSize()
    {
    	try {
	    	String RetData = Communication("READ_KINDTX_OFFSET_SIZE", "NODATA");
	    	if (RetData.trim().equals("") || RetData.trim().equals("NOT-FOUND")) {
	    		JOptionPane.showMessageDialog(null,"Kind/Tx에 대한 Offset/Size를 가져오지 못했습니다.");
	    		return "";
	    	}
	    	return RetData;
    	}catch(Exception e) { 
    		return "";
    	}
 
    }
    private String GetTcheckerLineInfoData()
    {
    	try {
	    	String RetData = Communication("READ_TLINEINFO", "NODATA");
	    	if (RetData.trim().equals("") || RetData.trim().equals("NOT-FOUND")) {
	    		return "";
	    	}
	    	return RetData;
    	}catch(Exception e) { 
    		return "";
    	}
 
    }
    private void SaveTcheckerLineInfo_LEN(String savedata)
    {
    	String RetData = Communication("SAVE_LINEINFO_LEN", savedata);
    	if (RetData.trim().equals("")) {
    		JOptionPane.showMessageDialog(null,"회선정보를 저장하지 못했습니다.");
    	}
    }	
    
    private String Communication(String cmdstr, String senddata)
    {
		Socket one_client = null;
    	DataOutputStream dos = null;
    	DataInputStream dis = null;
    	String recvdata = "";
    	try {
    		one_client = new Socket();
	        one_client.connect(new InetSocketAddress(GetRegister("TCHECKER_ANYLINKIP"), Integer.parseInt(GetRegister("TCHECKER_ANYLINKPORT"))), 3000);  //3초 기다림
            one_client.setSoTimeout(5000);
            
            dos = new DataOutputStream(one_client.getOutputStream());
            dis = new DataInputStream(one_client.getInputStream());
            
    		//Send
            senddata = senddata.replace("\r","");
            String cmd = cmdstr + "                                        ";
            String SendStr = String.format("%08d", senddata.getBytes().length) + cmd.substring(0,32) + senddata;
            dos.write(SendStr.getBytes(), 0, SendStr.getBytes().length);
            dos.flush();
            
            System.out.println("Communication:" + SendStr);
            
 
      	    //데이타부 길이정보 읽기
            int    tmplen = 0;
            byte[] tmpbyte1 = new byte[8];
            try{
               for(int i = 0 ; i < 8 ;i++) {
            	   tmpbyte1[i] = dis.readByte();  
            	   tmplen++;
               }
            }catch(Exception e2){}

            if (tmplen < 8)  {
      	    	JOptionPane.showMessageDialog(null, "수신데이타가 없습니다.");
      	    	return "";
      	    }

            //데이타부 읽기
            tmplen = 0;
            byte[] tmpbyte2 = new byte[Integer.parseInt(new String(tmpbyte1))];
            try{
               for(int i = 0 ; i < tmpbyte2.length ;i++) {
            	   tmpbyte2[i] = dis.readByte();  
            	   tmplen++;
               }
            }catch(Exception e2){}
      	    recvdata = new String(tmpbyte2);
	        return recvdata;
      
    	}
    	catch(Exception e)
    	{
    		JOptionPane.showMessageDialog(null, e.getMessage());
    	}finally{
            try{ if(one_client != null) one_client.close();}catch(Exception e){};
            try{ if(dos != null) dos.close();}catch(Exception e){};
            try{ if(dis != null) dis.close();}catch(Exception e){};
    	}
    	
    	return "";
    	
    	 
    }	
    private void setThreadSleep(int time)
    {
   	    try{
            Thread.sleep(time);
        } catch(InterruptedException e) {}
    }    
    private String GetRegister(String pKey)
    {
    	Preferences userRootPrefs = Preferences.userRoot();
    	if (userRootPrefs.get(pKey, null) != null) { 
    		return userRootPrefs.get(pKey, "");
    	}
    	return "";
    }
 
    private void CheckLineInfo()
    {
    	
    	try {
        	// Clinet 와 Server 인 회선을 각각 분류한다.
        	String server = "";
        	String client = "";
        	
    		for(int i=0;i < permitroot.getChildCount();i++)
    		{
    			DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) permitroot.getChildAt(i);  
                myLineInfoComm data = (myLineInfoComm) dataNode.getUserObject();  
     
                String tmpstr = "";
                tmpstr = tmpstr + data.getApplCode()                   + "\t"; 
                tmpstr = tmpstr + data.getApplName()                   + "\t";
                tmpstr = tmpstr + data.getPortNo()                     + "\n";

                if (data.getConnMethod().split(":")[0].equals("1") || data.getConnMethod().split(":")[0].equals("1")){
                	server = server + tmpstr ;
                }
                else {
                	client = client + tmpstr ;
                }
    			
    		}
 
    		String   result = "";
    		try {
        		//client 를 기준으로 동일한 server port가 있는지 점검한다.
        		String[] arrserver = server.split("\n");
        		String[] arrclient = client.split("\n");

        		for(int i=0;i < arrclient.length ;i++){
        			String[] arrclientsub = arrclient[i].split("\t");
        			for(int j=0;j < arrserver.length ;j++){
        				String[] arrserversub = arrserver[j].split("\t");
        				
        				if (arrclientsub[2].equals(arrserversub[2])){
        					result = result + arrclient[i] + "\n";
        				}
        			}
        		}
    		}catch(Exception e1){}

    		
    		//동일한 포트에 대하여 화면에 표시한다.
    		if (result.length() > 10)
    		{
    			String msg = "Anylink 와 Simulator(TChecker Frame)이 동일한 시스템에 존재할 때,\n";
    			msg = msg + "Client/Server에 대하여 동일한 포트를 사용할 수 없습니다. \n";
    			msg = msg + "\n";
    			msg = msg + result.replace("\t", " : ");
    			
    			DataOutputStream out = new DataOutputStream(new FileOutputStream(new File("./CheckLine.txt")));
    			out.write(msg.getBytes());
    			out.close();
     
    		}
    	}catch(Exception e){}
 	
    }
}

/*----------------------- NIO ------------------------------*/
/*
 * ByteBuffer bb1 = ByteBuffer.allocate(256);       //일반 버퍼 생성
 * ByteBuffer bb1 = ByteBuffer.allocateDirect(256); //커널버퍼에 직접 접근하는 버퍼생성
 * position : 현재 읽기나 쓰기를 할 위치를 가르킨다. 읽기나 쓰기가 진행될때 position 값도 자동 이동한다.
 * limit    : 현재 ByteBuffer에 읽기/쓰기를 할 수 있는 위치의 한계값
 * capacity : ByteBuffer의 용량(항상 버퍼의 마지막을 가르킴)
 * mark     : 사용자가 지정한 위치
 * 
 *
 * rewind() : 버퍼의 데이터를 다시 읽기 위해 positoino 값을 0으로 설정한다.
 *            데이터 삭제X, position = 0, mark = 제거
 * flip()   : Buffer를 쓰기모드에서 읽기 모드로 스위칭한다, 
 *            limit = position, postion = 0, mark = 제거
 * clear()  : positon을 0으로 설정하고, limit값을 capacity로 설정한다.
 *            데이터 삭제X, position = 0, mark = 제거, limit = capacity
 * reset()  : position 값을 mark로 되돌린다.
 *            데이터 삭제X, position = mark, (단, mark < position 일때만 가능 그 외에 오류발생)
 * remaining(): (limit - position) 값 리턴
 * position() : 현재 position 값 리턴
 * position(int pos) : position 지정
 * limit()    : 현재 limit 값 리턴
 * mark()     : 현재 position 을 mark 로 지정
 * compact()  : 현재 position 부터 limit의 사이에 있는 데이타를 buffer의 가장 앞으로 이동시키고,
 *              position은 데이터의 마지막 부분을 가리키고, limit = capacity, mark = 제거
 *              (앞으로 이동시키고 남은 뒤부분 데이터들은 0으로 초기화하지 않기 때문에 쓰레기 데이터가 ㅏㄴㅁ음)
 *              
 *
*/