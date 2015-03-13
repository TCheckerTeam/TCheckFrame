package Manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.prefs.Preferences;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.JXTreeTable;

import Manager.DataModel.myComboxModel;
import Manager.DataModel.myUserPermitComm;
import Manager.DataModel.myUserPermitModel;

public class TCheckPanelUser { 
	private JPanel mypanel = null;
	private JSplitPane splitPane, splitPane1, splitPane2;
	private JTable myPaneUserTable;
	private JXTreeTable myPanePermitTable;
	private JComboBox btnDisplay;
	private String gSelectedUserID = "";
	private DefaultMutableTreeNode permitroot = null;
	public TCheckPanelUser(JPanel panel)
	{
		mypanel = panel;
		myPaneUserInit();
	}
	
	public void myPaneUserInit()
    {
		mypanel.setLayout(new BorderLayout());
		
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT); 
        splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT); 
        splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT); 
 
        splitPane1.setContinuousLayout(true); //연속적인 레이아웃 기능 활성화
        splitPane1.setRightComponent(Init_Table_User());
        splitPane1.setLeftComponent(Init_Button_User());
        splitPane1.setDividerLocation(40);
        splitPane1.setDividerSize(0); //디바이더(분리대) 굵기 설정
        splitPane1.setBackground(Color.BLACK);
        
        splitPane2.setContinuousLayout(true); //연속적인 레이아웃 기능 활성화
        splitPane2.setRightComponent(Init_Table_Permit());
        splitPane2.setLeftComponent(Init_Button_Permit());
        splitPane2.setDividerLocation(40);
        splitPane2.setDividerSize(0); //디바이더(분리대) 굵기 설정
        splitPane2.setBackground(Color.BLACK);
 
        //JSplitPane 설정
        splitPane.setContinuousLayout(true); //연속적인 레이아웃 기능 활성화
        splitPane.setLeftComponent(splitPane1); //좌측 컴포넌트 장착
        splitPane.setRightComponent(splitPane2); //우측 컴포넌트 장착
        splitPane.setDividerLocation(500); //디바이더(분리대) 위치 설정      
        splitPane.setDividerSize(6); //디바이더(분리대) 굵기 설정
        splitPane.setBackground(Color.BLACK);
        
        JScrollPane myPaneSub2 = new JScrollPane(splitPane);
        mypanel.add(myPaneSub2, BorderLayout.CENTER);
        myPaneSub2.setAutoscrolls(true);
 
        myPaneTableUserRefresh();
        myPaneTablePermitRefresh();
    }
	private JScrollPane Init_Table_User()
	{
    	// Table Component Setting 
    	final String[] colNames = {"사용자ID","비밀번호","사용자정보","PC IPAddress","권한등급"};
    	final int[] columnsWidth = {60, 60, 150, 120, 40};
 
    	DefaultTableModel tabModel = new DefaultTableModel(colNames,0);
    	myPaneUserTable = new JTable(tabModel);
    	myPaneUserTable.setRowSelectionAllowed(true);
    	myPaneUserTable.setColumnSelectionAllowed(true);
    	myPaneUserTable.setCellSelectionEnabled(true);
    	myPaneUserTable.setFillsViewportHeight(true);
     
        
        for(int i=0;i < columnsWidth.length;i++){
        	myPaneUserTable.getColumnModel().getColumn(i).setMinWidth(columnsWidth[i]);
        	myPaneUserTable.getTableHeader().getColumnModel().getColumn(i).setMinWidth(columnsWidth[i]);
     
        	if (i==4) {
        		//매핑구분에 대한 Combo 설정
        		JComboBox combpermit = new JComboBox();
//        		combpermit.addItem("전체권한");
//        		combpermit.addItem("업무권한");
//        		combpermit.addItem("거래권한");
        		
        		combpermit.addItem("ALL");
        		combpermit.addItem("APPL");
        		combpermit.addItem("TX");
 
        		myPaneUserTable.getColumnModel().getColumn(i).setCellEditor(new DefaultCellEditor(combpermit));
        		DefaultTableCellRenderer  renderer = new DefaultTableCellRenderer();
        		renderer.setToolTipText("Click 하면, 콤보박스로 변경됩니다");
        		myPaneUserTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        	}
        	
        }
 
        ListSelectionModel rowSM = myPaneUserTable.getSelectionModel();
        rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rowSM.addListSelectionListener(new ListSelectionListener()
            {
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting())        {
                        ListSelectionModel source = (ListSelectionModel)e.getSource();
                        gSelectedUserID = (String)myPaneUserTable.getValueAt(source.getMinSelectionIndex(), 0);
                        myPaneTablePermitRefresh();
                    }
                }
            }
        );

        ListSelectionModel colSM = myPaneUserTable.getColumnModel().getSelectionModel();
        colSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        colSM.addListSelectionListener(new ListSelectionListener()
            {
                public void valueChanged(ListSelectionEvent e) {
                    ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                    if (!e.getValueIsAdjusting()) {

                     
                    }
                }
            }
        );
         
        
        JScrollPane myPaneSub2 = new JScrollPane(myPaneUserTable);
        myPaneSub2.setAutoscrolls(true);

        //1000 개의 신규 row를 생성한다.
        String[] gridData = new String[5];
        for(int i=0;i < tabModel.getColumnCount();i++)gridData[i] = "";
        for(int i=0;i < 1000;i++){
            tabModel.addRow(gridData);
        }
        
        //myPaneTableRefresh();
        return myPaneSub2;
	}
	private JScrollPane Init_Table_Permit()
	{
		 DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new myUserPermitComm("aaa", "", "", "", "", "", "",true));  
		 permitroot = rootNode;
		 myPanePermitTable = new JXTreeTable();
		 myPanePermitTable.setTreeTableModel(new myUserPermitModel(rootNode)); 
		 myPanePermitTable.setEditable(true);
		 myPanePermitTable.setRootVisible(false);
 
		 myPanePermitTable.getColumnModel().getColumn(0).setPreferredWidth(100);
		 myPanePermitTable.getColumnModel().getColumn(1).setPreferredWidth(150);
		 myPanePermitTable.getColumnModel().getColumn(2).setPreferredWidth(40);
		 myPanePermitTable.getColumnModel().getColumn(3).setPreferredWidth(150);
		 myPanePermitTable.getColumnModel().getColumn(4).setPreferredWidth(40);
		 myPanePermitTable.getColumnModel().getColumn(5).setPreferredWidth(150);
		 myPanePermitTable.getColumnModel().getColumn(6).setPreferredWidth(40);
  
		 JScrollPane myPaneSub2 = new JScrollPane(myPanePermitTable);
         myPaneSub2.setAutoscrolls(true);
        
        return myPaneSub2;
	}
 
	private JPanel Init_Button_User()
	{
    	//상위 Command Button 설정
    	JPanel myPaneSub1  = new JPanel(); myPaneSub1.setBackground(Color.WHITE);
    	JButton btnSave, btnRefresh, btnDelete;
 
    	myPaneSub1.setLayout(new FlowLayout());
 
    	JLabel tmpLabel = new JLabel(" 사용자정보              ");
    	myPaneSub1.add(tmpLabel);
    	
    	
    	btnRefresh = new JButton("갱신하기",new ImageIcon("./Image/refresh.gif")); 
    	myPaneSub1.add(btnRefresh);
    	mypanel.add(myPaneSub1,BorderLayout.NORTH);
    	btnRefresh.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
 
				myPaneTableUserRefresh();
    		}
       	});
    	
    	
    	btnSave = new JButton("저장하기", new ImageIcon("./Image/save.gif"));
    	myPaneSub1.add(btnSave);
   
    	mypanel.add(myPaneSub1,BorderLayout.NORTH);
    	btnSave.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
 
				DefaultTableModel tabModel = (DefaultTableModel)myPaneUserTable.getModel();
				int rowcnt = tabModel.getRowCount();
				int colcnt = tabModel.getColumnCount();
				String savedata = "";
				for(int i=0;i < rowcnt;i++){
					String compstr = (String)tabModel.getValueAt(i, 0);
					if (compstr.trim().equals("")) break;
					
					String tmpstrsub = "";
					for(int j=0;j < colcnt;j++){
						//업무명필드만 제외하고 모두 조합을 한다.
			            String tmpstr = (String)tabModel.getValueAt(i, j);
			            if (j==0) tmpstrsub = tmpstr;
						else tmpstrsub = tmpstrsub + "\t" + tmpstr;
					}
					savedata = savedata + tmpstrsub + "\n";
				}
		 
				SaveTcheckerUserInfo(savedata);
    		}
       	});
    	
    	btnDelete = new JButton("삭제하기", new ImageIcon("./Image/delete.gif"));
    	myPaneSub1.add(btnDelete);
   
    	mypanel.add(myPaneSub1,BorderLayout.NORTH);
    	btnDelete.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				DelTcheckerUserInfo(gSelectedUserID);
    		}
       	});
 
    	return myPaneSub1;
	}
	
	private JPanel Init_Button_Permit()
	{
    	//상위 Command Button 설정
    	JPanel myPaneSub1  = new JPanel(); myPaneSub1.setBackground(Color.WHITE);
    	JButton btnSave, btnRefresh;
 
    	myPaneSub1.setLayout(new FlowLayout());
 
    	JLabel tmpLabel = new JLabel(" 허용거래정보              ");
    	myPaneSub1.add(tmpLabel);
    	
    	btnRefresh = new JButton("갱신하기",new ImageIcon("./Image/refresh.gif")); 
    	myPaneSub1.add(btnRefresh);
    	mypanel.add(myPaneSub1,BorderLayout.NORTH);
    	btnRefresh.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				myPaneTablePermitRefresh();
    		}
       	});
    	
    	
    	btnSave = new JButton("저장하기", new ImageIcon("./Image/save.gif"));
    	myPaneSub1.add(btnSave);
   
    	mypanel.add(myPaneSub1,BorderLayout.NORTH);
    	btnSave.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
 
				String savedata = "";
				for(int i=0;i < permitroot.getChildCount();i++)
				{
					DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) permitroot.getChildAt(i);  
					myUserPermitComm data = (myUserPermitComm) dataNode.getUserObject();  
 
			        String tmpstr = "";
			        tmpstr = tmpstr + gSelectedUserID       + "\t";
			        tmpstr = tmpstr + data.getApplCode()    + "\t"; 
			        tmpstr = tmpstr + "ALL"                 + "\t";
			        tmpstr = tmpstr + "ALL"                 + "\t";
			        
			        if (data.getPermitFlag().trim().equals("")) {
			        	tmpstr = tmpstr + "N\n";
			        }
			        else {
			        	tmpstr = tmpstr + data.getPermitFlag()  + "\n";	
			        }
			        
			         
			        for(int j=0;j < permitroot.getChildAt(i).getChildCount();j++){
			        	DefaultMutableTreeNode dataNodesub = (DefaultMutableTreeNode) permitroot.getChildAt(i).getChildAt(j);  
						myUserPermitComm datasub = (myUserPermitComm) dataNodesub.getUserObject();
						tmpstr = tmpstr + gSelectedUserID          + "\t";
				        tmpstr = tmpstr + datasub.getApplCode()    + "\t"; 
				        tmpstr = tmpstr + datasub.getKindCode()    + "\t";
				        tmpstr = tmpstr + datasub.getTxCode()      + "\t";
				        tmpstr = tmpstr + datasub.getPermitFlag()  + "\n";
				        savedata = savedata + tmpstr ;
			        }
				}
		        System.out.println(savedata);
				SaveTcheckerPermitInfo(savedata);
    		}
       	});
  
    	return myPaneSub1;
	}
	
    public void myPaneTableUserRefresh()
    {
   
    	DefaultTableModel tabModel = (DefaultTableModel)myPaneUserTable.getModel();
 
    	//기존 RowData 모두 삭제
    	try {
			for(int i=0;i < tabModel.getRowCount();i++){
				for(int j=0;j < tabModel.getColumnCount();j++){
	    		    tabModel.setValueAt("", i, j);
				}
	    	}
    	}catch(Exception e){}
 
    	String[] gridData = new String[5];
        String applreslinkinfo = SearchTcheckerUserInfo();
        
        System.out.println("1. Read_Data : ["+applreslinkinfo+"]");
        
        String[] arrtmp = applreslinkinfo.split("\n");
        
        for(int i=0;i < arrtmp.length ;i++){
        	if (arrtmp[i].trim().equals("")) break;
        	String[] arrtmpsub = arrtmp[i].split("\t");
        	
        	for(int s=0; s<arrtmpsub.length; s++){
        		System.out.println("2. Read_Data : ["+arrtmpsub[s]+"]");
        	}
        	
        	
            gridData[0] = arrtmpsub[0];  //사용자ID
            gridData[1] = arrtmpsub[1];  //비밀번호
            gridData[2] = arrtmpsub[2];  //사용자정보
            gridData[3] = arrtmpsub[3];  //PC IPAddress
            gridData[4] = arrtmpsub[4];  //권한등급
 
            for(int j=0;j < tabModel.getColumnCount();j++){
            	tabModel.setValueAt(gridData[j],i, j);
            }
  
        }
 
    }
    
    public void myPaneTablePermitRefresh()
    {
    
   	    if (permitroot == null) return;
    	permitroot.removeAllChildren();

    	if (gSelectedUserID.trim().equals("")) return;
 
    	DefaultMutableTreeNode ApplNode = null;
    	String tmpApplName = "";
        String applreslinkinfo = SearchTcheckerPermitInfo(gSelectedUserID);
        String[] arrtmp = applreslinkinfo.split("\n");
        for(int i=0;i < arrtmp.length ;i++){
        	if (arrtmp[i].trim().equals("")) break;
        	String[] arrtmpsub = arrtmp[i].split("\t");
 
            if (!tmpApplName.equals(arrtmpsub[0] + " - " + arrtmpsub[1]))
            {
            	if (!tmpApplName.equals("")) permitroot.add(ApplNode);
            	
            	//Appl에 대한 권한 정보
            	String appl_permitflag = "N";
            	String applreslinkinfoappl = SearchTcheckerPermitInfoAppl(gSelectedUserID);
            	String[] arrapplreslinkinfoappl = applreslinkinfoappl.split("\n");
            	for(int j=0;j < arrapplreslinkinfoappl.length ;j++){
            		String[] arrtmpappl = arrapplreslinkinfoappl[j].split("\t");
            		if (arrtmpappl[0].equals(arrtmpsub[0])) {
            			appl_permitflag = arrtmpappl[1];
            			break;
            		}
            	}
            	
            	ApplNode = new DefaultMutableTreeNode(new myUserPermitComm(arrtmpsub[0] , arrtmpsub[1], "", "","", "", appl_permitflag, true)); 
            	tmpApplName = arrtmpsub[0] + " - " + arrtmpsub[1];
            }
   
            ApplNode.add(new DefaultMutableTreeNode(new myUserPermitComm(arrtmpsub[0] , arrtmpsub[1], 
													            		 arrtmpsub[2] , arrtmpsub[3], 
													            		 arrtmpsub[4] , arrtmpsub[5],
													            		 arrtmpsub[6] , false)));      
        }
	 
        if (ApplNode != null) permitroot.add(ApplNode);
  
	    myPanePermitTable.setTreeTableModel(new myUserPermitModel(permitroot)); 
 
	    //컬럼크기 설정
		myPanePermitTable.getColumnModel().getColumn(0).setPreferredWidth(80);
		myPanePermitTable.getColumnModel().getColumn(1).setPreferredWidth(150);
		myPanePermitTable.getColumnModel().getColumn(2).setPreferredWidth(40);
		myPanePermitTable.getColumnModel().getColumn(3).setPreferredWidth(150);
		myPanePermitTable.getColumnModel().getColumn(4).setPreferredWidth(40);
		myPanePermitTable.getColumnModel().getColumn(5).setPreferredWidth(150);
		myPanePermitTable.getColumnModel().getColumn(6).setPreferredWidth(40);
 
        
		//권한(Y/N) 에 대한 랜더링
 		JComboBox combpermit = new JComboBox();
		combpermit.addItem("Y");
		combpermit.addItem("N");
	 
		myPanePermitTable.getColumnModel().getColumn(6).setCellEditor(new myComboxModel(combpermit) );
		DefaultTableCellRenderer  renderer = new DefaultTableCellRenderer();
		renderer.setBackground(new Color(217,255,217));
		myPanePermitTable.getColumnModel().getColumn(6).setCellRenderer(renderer);
		 
    }
 
    private String SearchTcheckerUserInfo()
    {
    	try {
    		String RetData = Communication("READ_USER", "NODATA");
        	if (RetData.trim().equals("") || RetData.trim().equals("NOT-FOUND")) {
        		return "";
        	}
        	return RetData;
    	}catch(Exception e) { 
    		return "";
    	}
    }
    private String SearchTcheckerPermitInfo(String pUserID)
    {
    	try {
    		String RetData = Communication("READ_PERMIT", pUserID);
        	if (RetData.trim().equals("") || RetData.trim().equals("NOT-FOUND")) {
        		return "";
        	}
        	return RetData;
    	}catch(Exception e) { 
    		return "";
    	}
    }
    private String SearchTcheckerPermitInfoAppl(String pUserID)
    {
    	try {
    		String RetData = Communication("READ_PERMITAPPL", pUserID);
        	if (RetData.trim().equals("") || RetData.trim().equals("NOT-FOUND")) {
        		return "";
        	}
        	return RetData;
    	}catch(Exception e) { 
    		return "";
    	}
    }
    
    private void SaveTcheckerUserInfo(String savedata)
    {
    	String RetData = Communication("SAVE_USER", savedata);
    	if (RetData.trim().equals("")) {
    		JOptionPane.showMessageDialog(null,"사용자정보를 저장하지 못했습니다.");
    	}
    	else {
    		JOptionPane.showMessageDialog(null,"사용자정보를 저장 완료하였습니다.");
    	}
    }
    private void SaveTcheckerPermitInfo(String savedata)
    {
    	String RetData = Communication("SAVE_PERMIT", savedata);
    	if (RetData.trim().equals("")) {
    		JOptionPane.showMessageDialog(null,"사용자 권한정보를 저장하지 못했습니다.");
    	}
    	else {
    		JOptionPane.showMessageDialog(null,"사용자 권한정보를 저장 완료하였습니다.");
    	}
    }
    private void DelTcheckerUserInfo(String savedata)
    {
    	String RetData = Communication("DELETE_USER", savedata);
    	if (RetData.trim().equals("OK")) {
    		myPaneTableUserRefresh();
    		myPaneTablePermitRefresh();
    		JOptionPane.showMessageDialog(null,"거래매핑 정보를 삭제 완료하였습니다.");
    	}
    	else {
    		JOptionPane.showMessageDialog(null,"거래매핑 정보를 삭제하지 못했습니다.");
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
}
 