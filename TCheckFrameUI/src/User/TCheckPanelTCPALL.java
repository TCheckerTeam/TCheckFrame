package User;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.JXTreeTable;

import User.DataModel.myTcpAllMapComm;
import User.DataModel.myTcpAllMapModel;
 
public class TCheckPanelTCPALL {
	private JPanel mypanel = null;
	private JSplitPane splitPane1;
	private JLabel    labelfname;
	private Rectangle rect ;
	private String gApplCode, gKindCode, gTxCode, gUserPCIP, gUserID, gSendPortNo;
	private DefaultMutableTreeNode roottcpall = null;
	private JXTreeTable myPaneTcpAllTable;
	private Font font12 = new Font("바탕체",Font.BOLD,12);
	private Font font13 = new Font("바탕체",Font.BOLD,13);
	private String gKindOffset, gKindSize, gTxOffset, gTxSize;
    private JProgressBar progressBar;
    private Timer  timer = null;
    private int    CurrentTask = 0;
    private boolean CurrentTaskBusy = false;
	public TCheckPanelTCPALL(JPanel panel, String pUserID)
	{
		mypanel = panel;
		gUserID = GetRegister("TCHECKER_USERID");
		myPaneGongUInit();
	}
    public void myPaneGongUInit()
    {
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        
        //화면 Split
		mypanel.setLayout(new BorderLayout());

		splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT); 
        splitPane1.setContinuousLayout(true); //연속적인 레이아웃 기능 활성화
        splitPane1.setRightComponent(Init_Table());
        splitPane1.setLeftComponent(Init_Button());
        splitPane1.setDividerLocation(35);
        splitPane1.setDividerSize(3); //디바이더(분리대) 굵기 설정
        splitPane1.setBackground(Color.BLACK);
        mypanel.add(splitPane1, BorderLayout.CENTER);
        mypanel.add(progressBar, BorderLayout.SOUTH);
        
        timer = new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	if (CurrentTaskBusy != true) SendMapperTypeMsg();
            	
                if (CurrentTask >= roottcpall.getChildCount()) {
                    timer.stop();
                }
            }    
        });
    }
 
	private JPanel Init_Button()
	{
 
    	//상위 Command Button 설정
    	JPanel myPaneSub1  = new JPanel(); myPaneSub1.setBackground(Color.WHITE);
    	JButton btnLoad, btnAllSend, btnStop  ;
 
    	myPaneSub1.setLayout(null);
 
    	//---------------------- Button -------------------------------------------------------
    	btnLoad = new JButton("화일선택",new ImageIcon("./Image/load.gif")); 
    	btnLoad.setFont(font13);
    	myPaneSub1.add(btnLoad);
    	mypanel.add(myPaneSub1,BorderLayout.NORTH);
    	btnLoad.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				try {
		            JFileChooser fc = new JFileChooser();
		            fc.setCurrentDirectory(new File("./"));
		            fc.setMultiSelectionEnabled(false);
		            int returnVal = fc.showOpenDialog(null);
		            if (returnVal == JFileChooser.APPROVE_OPTION) {
		            	File f = fc.getSelectedFile();
		            	DataInputStream dis = new DataInputStream(new FileInputStream(f));
                        int len = (int) f.length();
                        byte[] RData = new byte[len];
                        dis.readFully(RData);
                        dis.close();
             
                        String[] arrtmp = f.getName().split("_");
                        GetApplInfo(arrtmp[0]);
                        gApplCode = arrtmp[0];
                        labelfname.setText(f.getName());
                        
                    	//Tree가 Null 이면, 그냥 리턴한고, 그렇지 않으면 Tree의 모든 Child Node를 삭제한다.
                   	    if (roottcpall == null) return;
                   	    roottcpall.removeAllChildren();
                 
                   	    String[] arrMainData = new String(RData).replace("\r","").split("\n");
                 
                        //MsgMap 정보에 대한 TreeNode를 구성한다.
                        try {
                        	byte[] KindInfo = new byte[Integer.parseInt(gKindSize)];
                        	byte[] TxInfo = new byte[Integer.parseInt(gTxSize)];
                            
                            for(int i=0;i < arrMainData.length ;i++){
                            	if (arrMainData[i].trim().equals("")) break;
                            	
                            	if(gKindOffset.trim().equals("")) {
                            	   DefaultMutableTreeNode tmpnode = new DefaultMutableTreeNode(new myTcpAllMapComm("" + (i+1) , "", "" , "", arrMainData[i], "",false));
                            	   roottcpall.add(tmpnode);
                            	}
                            	else {
                             	   System.arraycopy(arrMainData[i].getBytes(), Integer.parseInt(gKindOffset), KindInfo, 0, KindInfo.length);
                            	   System.arraycopy(arrMainData[i].getBytes(), Integer.parseInt(gTxOffset), TxInfo, 0, TxInfo.length);
                            	   DefaultMutableTreeNode tmpnode = new DefaultMutableTreeNode(new myTcpAllMapComm("" + (i+1) , new String(KindInfo), new String(TxInfo) , "", arrMainData[i], "",false));
                            	   roottcpall.add(tmpnode);
                            	}
                            }
                        }catch(Exception e) {
                            JOptionPane.showMessageDialog(null,"전문정보를 Display 하면서 오류가 발생하였습니다. 전문정보를 확인하세요.");
                        }
                 
                        myPaneTcpAllTable.setTreeTableModel(new myTcpAllMapModel(roottcpall));
                        myPaneTcpAllTable.expandAll();
                        
                		//컬럼사이즈 설정
                        SetColumnWidth(myPaneTcpAllTable);
		            }
				}catch(Exception e){}
    		}
       	});
 
    	btnAllSend = new JButton("전문전송",new ImageIcon("./Image/send.png")); 
    	btnAllSend.setFont(font13);
    	myPaneSub1.add(btnAllSend);
    	mypanel.add(myPaneSub1,BorderLayout.NORTH);
    	btnAllSend.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				for(int i=0;i < roottcpall.getChildCount();i++){
					DefaultMutableTreeNode WorkNode = (DefaultMutableTreeNode) roottcpall.getChildAt(i);  
					myTcpAllMapComm data = (myTcpAllMapComm) WorkNode.getUserObject();
					data.setTime("");
                    data.setRecv("");
				}
				myPaneTcpAllTable.updateUI();
				
				CurrentTask = 0;
				CurrentTaskBusy = false;
				timer.start();
    		}
       	});
 
    	btnStop = new JButton("전송중지",new ImageIcon("./Image/send.png")); 
    	btnStop.setFont(font13);
    	myPaneSub1.add(btnStop);
    	mypanel.add(myPaneSub1,BorderLayout.NORTH);
    	btnStop.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
 
				timer.stop();
    		}
       	});
    	
    	Font font = new Font("바탕체",Font.BOLD,14);
    	JLabel label1 = new JLabel("화일명"); label1.setFont(font); label1.setForeground(Color.BLUE); myPaneSub1.add(label1);
    	labelfname = new JLabel("선택화일 없음"); labelfname.setFont(font); labelfname.setBorder(new EtchedBorder(EtchedBorder.LOWERED)); myPaneSub1.add(labelfname);
    	 
    	label1.setBounds(10, 5, 60, 20); 
    	labelfname.setBounds(60, 5, 600, 25);
    	
    	btnLoad.setBounds(670, 5, 150, 25);
    	btnAllSend.setBounds(830, 5, 150, 25);
    	btnStop.setBounds(990, 5, 150, 25);
    	
    	return myPaneSub1;
	}
	private JScrollPane Init_Table()
	{
   	     // Table Component Setting 
		 DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new myTcpAllMapComm("Root", "", "", "","","",true));  

		 roottcpall = rootNode;
		 myPaneTcpAllTable = new JXTreeTable();
		 myPaneTcpAllTable.setTreeTableModel(new myTcpAllMapModel(rootNode));  
		 myPaneTcpAllTable.setEditable(true);
		 myPaneTcpAllTable.setRootVisible(false);
		 myPaneTcpAllTable.setGridColor(Color.GRAY);
		 myPaneTcpAllTable.setShowGrid(true, true);
		 myPaneTcpAllTable.setAutoStartEditOnKeyStroke(true);
 
		 //컬럼사이즈 설정
		 SetColumnWidth(myPaneTcpAllTable);
 
         JScrollPane myPaneSub2 = new JScrollPane(myPaneTcpAllTable);
         myPaneSub2.setAutoscrolls(true);
 	
         return myPaneSub2;
	 
	}
 
	private void SendMapperTypeMsg()
	{
 
		CurrentTaskBusy = true;
		
		SimpleDateFormat formatter = null;
		int hh1, hh2;
		int mm1, mm2;
		int ss1, ss2;
		int ms1, ms2;
 
	 
		String senddata = "";
 
		DefaultMutableTreeNode WorkNode = (DefaultMutableTreeNode) roottcpall.getChildAt(CurrentTask);  
		myTcpAllMapComm data = (myTcpAllMapComm) WorkNode.getUserObject();
		
		gKindCode = data.getKind();
		gTxCode = data.getTx();
		senddata = data.getSend();

		//송신전 시간
		formatter = new SimpleDateFormat("HHmmss", java.util.Locale.KOREA);
		String reg_tm1 = formatter.format(new java.util.Date());  //등록시간 
		formatter = new java.text.SimpleDateFormat("S", java.util.Locale.KOREA);
		String reg_milsec1 = formatter.format(new java.util.Date());  //등록시간 
		
    	String RetData = Communication("SEND_TCPMAPPERALLMSG", gUserID + "\t" + gApplCode + "\t" + gKindCode + "\t" + gTxCode + "\t" + "99999" + "<DATAGUBUN>" + senddata);
    	
    	//수신후 시간
		formatter = new SimpleDateFormat("HHmmss", java.util.Locale.KOREA);
		String reg_tm2 = formatter.format(new java.util.Date());  //등록시간 
		formatter = new java.text.SimpleDateFormat("S", java.util.Locale.KOREA);
		String reg_milsec2 = formatter.format(new java.util.Date());  //등록시간 
		
		hh1 = Integer.parseInt(reg_tm1.substring(0,2));
		mm1 = Integer.parseInt(reg_tm1.substring(2,4));
		ss1 = Integer.parseInt(reg_tm1.substring(4,6));
		ms1 = Integer.parseInt(reg_milsec1);
		
		hh2 = Integer.parseInt(reg_tm2.substring(0,2));
		mm2 = Integer.parseInt(reg_tm2.substring(2,4));
		ss2 = Integer.parseInt(reg_tm2.substring(4,6));
		ms2 = Integer.parseInt(reg_milsec2);
		
		int time1 = (hh2 * 3600 + mm2 * 60 + ss2) - (hh1 * 3600 + mm1 * 60 + ss1);
		int time2 = ms2 - ms1;
		if (time2 < 0) {
			time2 = 1000 + time2;
			time1--;
		}
		data.setTime(time1 + "." + time2);
		data.setRecv(RetData);
	 
    	myPaneTcpAllTable.updateUI();
    	CurrentTask++;
    	int percent =  (int)((float)CurrentTask/(float)roottcpall.getChildCount() * 100.);
    	System.out.println(CurrentTask + ":" + CurrentTask/roottcpall.getChildCount() * 100);
    	
    	progressBar.setValue(percent);
    	CurrentTaskBusy = false;
	}
    private void GetApplInfo(String pApplCode)
    {
    	try {
			gKindOffset = "";
			gKindSize   = "";
			gTxOffset   = "";
			gTxSize     = "";
			
    		String RetData = Communication("READ_KINDTX_OFFSET_SIZE", pApplCode);
        	if (RetData.trim().equals("") || RetData.trim().equals("NOT-FOUND")) {
        		JOptionPane.showMessageDialog(null,"업무에 대한 정보를 가져오지 못했습니다.");
        		return ;
        	}
        	String[] arrtmp = RetData.split("\n");
        	for(int i=0;i < arrtmp.length ;i++){
        		if (arrtmp[i].trim().equals("")) break;
        		String[] arrtmpsub = arrtmp[i].split("\t");
        		if (arrtmpsub[0].equals(pApplCode)) {
        			gKindOffset = arrtmpsub[1].trim();
        			gKindSize   = arrtmpsub[2].trim();
        			gTxOffset   = arrtmpsub[3].trim();
        			gTxSize     = arrtmpsub[4].trim();
        			break;
        		}
        	}
    	}catch(Exception e) { }
    	
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
    private String GetRegister(String pKey)
    {
    	Preferences userRootPrefs = Preferences.userRoot();
    	if (userRootPrefs.get(pKey, null) != null) { 
    		return userRootPrefs.get(pKey, "");
    	}
    	return "";
    }
  
    private void SetColumnWidth(JXTreeTable mytable)
    {
		//컬럼사이즈 설정
		final int[] columnsWidth = {100, 100, 100, 100, 700,700};
		for(int i=0; i < columnsWidth.length;i++){
			
			mytable.getColumnModel().getColumn(i).setPreferredWidth(columnsWidth[i]);
			mytable.getColumnModel().getColumn(4).setResizable(false);
		    
		    if (i == 3 ){
				DefaultTableCellRenderer  renderer1 = new DefaultTableCellRenderer();
				renderer1.setBackground(new Color(217,255,217));
				mytable.getColumnModel().getColumn(i).setCellRenderer(renderer1);
			}
		}
  
    }
  
}
