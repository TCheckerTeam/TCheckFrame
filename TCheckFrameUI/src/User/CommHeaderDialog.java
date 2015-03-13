package User;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.JXTreeTable;

import User.DataModel.myTcpMapComm;
import User.DataModel.myTcpMapModel;
 
class CommHeaderDialog extends JDialog implements ActionListener{
	private JPanel mypanel = null;
	private JSplitPane splitPane, splitPane1 ;
	private Rectangle rect ;
	private String gApplCode, gKindCode, gTxCode, gUserID ;
	private JPopupMenu popupreq = new JPopupMenu();
	private JPopupMenu popupres = new JPopupMenu();
	private DefaultMutableTreeNode roottcpreq = null, roottcpres = null;
	private JXTreeTable myPaneTcpReqTable, myPaneTcpResTable;
	private Font font12 = new Font("바탕체",Font.BOLD,12);
	private Font font13 = new Font("바탕체",Font.BOLD,13);
	private int  gIndex = 0;
	private JButton btnSave, btnClose;
 
	public CommHeaderDialog(String pApplCode, String pKindCode, String pTxCode) 
	{
		gApplCode = pApplCode;
		gKindCode = pKindCode;
		gTxCode   = pTxCode;
		
		this.setTitle("매퍼전송시 전문공통부 제외설정한 업무에 대한 전문공통부 입력");
		this.setModal(true);
		this.setLayout(new BorderLayout());

		mypanel = new JPanel();
		gUserID = GetRegister("TCHECKER_USERID");
		myPaneGongUInit();
		LoadCommHeader();
		
		this.add(mypanel);
        this.setBounds(300,100,800,500);

        this.setVisible(true);
 
	}
	@Override
	public void actionPerformed(ActionEvent e){
		String cmd = e.getActionCommand();
    	
    	if (cmd.equals("확인")){
     
    		dispose();
    	}
    	if (cmd.equals("닫기")){
     
    		dispose();
    	}
	}
 
	private void LoadCommHeader()
	{
		String reqmsg = GetCommonHead("REQUEST", "READ");
		String resmsg = GetCommonHead("RESPONSE", "READ");
		
		myPaneTableTcpRefresh(myPaneTcpReqTable, roottcpreq, reqmsg);
		myPaneTableTcpRefresh(myPaneTcpResTable, roottcpres, resmsg);
	}
	
    private void myPaneGongUInit()
    {
 
		int left_width  = 400;
		int left_height = 250; 
 
 
        //화면 Split
		mypanel.setLayout(new BorderLayout());
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT); 
        splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT); 
    
        splitPane1.setContinuousLayout(true); //연속적인 레이아웃 기능 활성화
        splitPane1.setRightComponent(Init_Table_Response());
        splitPane1.setLeftComponent(Init_Table_Request());
        splitPane1.setDividerLocation(left_width);
        splitPane1.setDividerSize(3); //디바이더(분리대) 굵기 설정
        splitPane1.setBackground(Color.BLACK);
    
        splitPane.setContinuousLayout(true); //연속적인 레이아웃 기능 활성화
        splitPane.setRightComponent(splitPane1);
        splitPane.setLeftComponent(Init_Button());
        splitPane.setDividerLocation(35);
        splitPane.setDividerSize(3); //디바이더(분리대) 굵기 설정
        splitPane.setBackground(Color.BLACK);
  
        mypanel.add(splitPane, BorderLayout.CENTER);
 
    }
    
    private JPanel Init_Button()
	{
		int left_width  =  400;
 
    	//상위 Command Button 설정
    	JPanel myPaneSub1  = new JPanel(); myPaneSub1.setBackground(Color.WHITE);
 
    	myPaneSub1.setLayout(null);
 
    	//---------------------- Button -------------------------------------------------------
    	btnSave = new JButton("저장",new ImageIcon("./Image/save.gif")); 
    	btnSave.setFont(font13);
    	myPaneSub1.add(btnSave);
    	mypanel.add(myPaneSub1,BorderLayout.NORTH);
    	btnSave.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				String pReqmsg = "";
				String pResMsg = "";
 
				for(int i=0;i < myPaneTcpReqTable.getRowCount();i++){
					String item  = (String)myPaneTcpReqTable.getValueAt(i, 0);
					String type  = (String)myPaneTcpReqTable.getValueAt(i, 1);
					String len   = (String)myPaneTcpReqTable.getValueAt(i, 2);
					String conts = (String)myPaneTcpReqTable.getValueAt(i, 3);
					String eng   = (String)myPaneTcpReqTable.getValueAt(i, 5);
					
					pReqmsg = pReqmsg + item + "\t";
					pReqmsg = pReqmsg + eng  + "\t";
					pReqmsg = pReqmsg + type + "\t";
					pReqmsg = pReqmsg + len  + "\t";
					if (conts.trim().equals("")) {
						pReqmsg = pReqmsg + "<NODATA>" + "\n";
					}
					else {
						pReqmsg = pReqmsg + conts + "\n";
					}
			    }
				
				for(int i=0;i < myPaneTcpResTable.getRowCount();i++){
					String item  = (String)myPaneTcpResTable.getValueAt(i, 0);
					String type  = (String)myPaneTcpResTable.getValueAt(i, 1);
					String len   = (String)myPaneTcpResTable.getValueAt(i, 2);
					String conts = (String)myPaneTcpResTable.getValueAt(i, 3);
					String eng   = (String)myPaneTcpResTable.getValueAt(i, 5);
					
					pResMsg = pResMsg + item + "\t";
					pResMsg = pResMsg + eng  + "\t";
					pResMsg = pResMsg + type + "\t";
					pResMsg = pResMsg + len  + "\t";
					if (conts.trim().equals("")) {
						pResMsg = pResMsg + "<NODATA>" + "\n";
					}
					else {
						pResMsg = pResMsg + conts + "\n";
					}
			    }
				
				SaveReqResMsg( pReqmsg, pResMsg);
    		}
       	});
 
 
    	btnClose = new JButton("닫기",new ImageIcon("./Image/close.gif")); 
    	btnClose.setFont(font13);
    	myPaneSub1.add(btnClose);
    	mypanel.add(myPaneSub1,BorderLayout.NORTH);
    	btnClose.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				dispose();
    		}
       	});
 
    	Font font = new Font("바탕체",Font.BOLD,14);
    	JLabel labelSend = new JLabel("송신공통전문"); labelSend.setFont(font); labelSend.setForeground(Color.BLUE); myPaneSub1.add(labelSend);
    	JLabel labelRecv = new JLabel("응답공통전문"); labelRecv.setFont(font); labelRecv.setForeground(Color.BLUE); myPaneSub1.add(labelRecv);
 
    	labelSend.setBounds(10, 5, 100, 20); 
    	btnSave.setBounds(left_width - 95, 5, 90, 25);
 
    	labelRecv.setBounds(left_width * 2 - 120 , 5, 100, 20); 
    	btnClose.setBounds(left_width + 5 , 5, 90, 25);
    	 
    	return myPaneSub1;
	}
	private JScrollPane Init_Table_Request()
	{
  	     // Table Component Setting 
		 DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new myTcpMapComm("Root", "", "", "","","",true));  

		 roottcpreq = rootNode;
		 myPaneTcpReqTable = new JXTreeTable();
		 myPaneTcpReqTable.setTreeTableModel(new myTcpMapModel(rootNode));  
		 myPaneTcpReqTable.setEditable(true);
		 myPaneTcpReqTable.setRootVisible(false);
		 myPaneTcpReqTable.setGridColor(Color.GRAY);
		 myPaneTcpReqTable.setShowGrid(true, true);
		 myPaneTcpReqTable.setAutoStartEditOnKeyStroke(true);

		 //컬럼사이즈 설정
		 SetColumnWidth(myPaneTcpReqTable);
 
        JScrollPane myPaneSub2 = new JScrollPane(myPaneTcpReqTable);
        myPaneSub2.setAutoscrolls(true);
 
    	//Request PopupMenu 설정
        JMenuItem reqjumninit = new JMenuItem("전문초기화",new ImageIcon("./Image/refresh.gif"));
        JMenuItem reqdatainit = new JMenuItem("데이타삭제",new ImageIcon("./Image/datainit.png"));
        JMenuItem reqdatainrt = new JMenuItem("데이타삽입",new ImageIcon("./Image/headerdef.png"));
         
     	popupreq.add(reqjumninit);
    	popupreq.addSeparator();
    	popupreq.add(reqdatainit);
    	popupreq.add(reqdatainrt);
    	
       	myPaneTcpReqTable.addMouseListener(new MouseAdapter(){
    		public void mouseClicked(MouseEvent e){
    			if (e.getButton() == 3) {
    				popupreq.show(myPaneTcpReqTable, e.getX(), e.getY());
    			}
    		}
    	});
       	
    	reqjumninit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				String reqmsg = GetCommonHead("REQUEST", "INIT");
				myPaneTableTcpRefresh(myPaneTcpReqTable, roottcpreq, reqmsg);

			}
		});
    	
    	reqdatainit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				for(int i=0;i < roottcpreq.getChildCount();i++)
				{
					  DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) roottcpreq.getChildAt(i);  
					  myTcpMapComm data = (myTcpMapComm) dataNode.getUserObject();  
					  data.setConts("");
					  
				}
				myPaneTcpReqTable.updateUI();
			}
		});
    	
    	reqdatainrt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				DataInsertDialog datains = new DataInsertDialog(200,200,400,350,"편집할 데이타 입력");
				
				if (datains.getInData().equals("")) return;
				String indata = datains.getInData().replace("\n", "");
				
				try {
					final HashMap<String, String> hashinrt = new HashMap<String, String>();
					int idx = 0;
					SetEditDataSub(roottcpreq,roottcpreq, hashinrt, idx, indata);
					 
				}catch(Exception e){}
				myPaneTcpReqTable.updateUI();
			}
		});
    	
        return myPaneSub2;
	}
	private JScrollPane Init_Table_Response()
	{
  	     // Table Component Setting 
		 DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new myTcpMapComm("Root", "", "", "","","",true));  

		 roottcpres = rootNode;
		 myPaneTcpResTable = new JXTreeTable();
		 myPaneTcpResTable.setTreeTableModel(new myTcpMapModel(rootNode));  
		 myPaneTcpResTable.setEditable(true);
		 myPaneTcpResTable.setRootVisible(false);
		 myPaneTcpResTable.setGridColor(Color.GRAY);
		 myPaneTcpResTable.setShowGrid(true, true);
		 myPaneTcpResTable.setAutoStartEditOnKeyStroke(true);
		 
		 //컬럼사이즈 설정
		 SetColumnWidth(myPaneTcpResTable);

        JScrollPane myPaneSub2 = new JScrollPane(myPaneTcpResTable);
        myPaneSub2.setAutoscrolls(true);
 
        //Response PopupMenu 설정
        
        JMenuItem resjumninit = new JMenuItem("전문초기화",new ImageIcon("./Image/refresh.gif"));
        JMenuItem resdatainit = new JMenuItem("데이타삭제",new ImageIcon("./Image/datainit.png"));
        JMenuItem resdatainrt = new JMenuItem("데이타삽입",new ImageIcon("./Image/headerdef.png"));
         
     	popupres.add(resjumninit);
    	popupres.addSeparator();
    	popupres.add(resdatainit);
    	popupres.add(resdatainrt);
    	
       	myPaneTcpResTable.addMouseListener(new MouseAdapter(){
    		public void mouseClicked(MouseEvent e){
    			if (e.getButton() == 3) {
    				popupres.show(myPaneTcpResTable, e.getX(), e.getY());
    			}
    		}
    	});
    	
    	resjumninit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				String resmsg = GetCommonHead("RESPONSE", "INIT");
				myPaneTableTcpRefresh(myPaneTcpResTable, roottcpres, resmsg);
			}
		});
    	
    	resdatainit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				for(int i=0;i < roottcpres.getChildCount();i++)
				{
					  DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) roottcpres.getChildAt(i);  
					  myTcpMapComm data = (myTcpMapComm) dataNode.getUserObject();  
					  data.setConts("");
				}
				myPaneTcpReqTable.updateUI();
			}
		});
    	
    	resdatainrt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				int left_width  = rect.width / 100 * 50;
				DataInsertDialog datains = new DataInsertDialog(left_width + 50,200,400,350,"편집할 데이타 입력");
				if (datains.getInData().equals("")) return;
				String indata = datains.getInData().replace("\n", "");
 
				try {
					final HashMap<String, String> hashinrt = new HashMap<String, String>();
					int idx = 0;
					SetEditDataSub(roottcpres,roottcpres, hashinrt, idx, indata);
					 
				}catch(Exception e){}
				myPaneTcpReqTable.updateUI();
				myPaneTcpResTable.updateUI();
			}
		});
    	

         return myPaneSub2;
	}

    private void SetColumnWidth(JXTreeTable mytable)
    {
		//컬럼사이즈 설정
		final int[] columnsWidth = {200, 40, 40, 200};
		for(int i=0; i < columnsWidth.length;i++){
			
			mytable.getColumnModel().getColumn(i).setPreferredWidth(columnsWidth[i]);
			mytable.getColumnModel().getColumn(4).setResizable(false);
		    
		    if (i == 3 ){
				DefaultTableCellRenderer  renderer1 = new DefaultTableCellRenderer();
				renderer1.setBackground(new Color(217,255,217));
				mytable.getColumnModel().getColumn(i).setCellRenderer(renderer1);
			}
		}
		
        mytable.getColumn("No").setWidth(0);
        mytable.getColumn("No").setMinWidth(0);
        mytable.getColumn("No").setMaxWidth(0);
        mytable.getColumn("영문명").setWidth(0);
        mytable.getColumn("영문명").setMinWidth(0);
        mytable.getColumn("영문명").setMaxWidth(0);
        
    }
    private String GetRegister(String pKey)
    {
    	Preferences userRootPrefs = Preferences.userRoot();
    	if (userRootPrefs.get(pKey, null) != null) { 
    		return userRootPrefs.get(pKey, "");
    	}
    	return "";
    }
    
	private String GetCommonHead(String ReqResGubun, String workGubun)
	{
		//공통헤더를 사용하지 않는 경우에 공통헤더 정보를 읽어온다.
		//mapper 전송시 필요하기 때문임
		
		String RetData = "";
        if (ReqResGubun.equals("REQUEST")) {
        	RetData = Communication("READ_TCPMSGREQ_COMMHEAD", gApplCode + "\t" + workGubun );
        }
        if (ReqResGubun.equals("RESPONSE")) {
			String RetDataSub = Communication("READ_RESLINKONE", gApplCode + "\t" + gKindCode + "\t" + gTxCode );
			if (RetData.trim().indexOf("Error:") >= 0) {
				JOptionPane.showMessageDialog(null, "응답전문 거래를 찾는데 오류가 발생하였습니다.:" + RetDataSub);
				return "";
	    	}
			else if (RetDataSub.trim().equals("NOT-FOUND")) {
				RetData = Communication("READ_TCPMSGRES_COMMHEAD", gApplCode + "\t" + workGubun);
		    }
			else {
				String[] arrtmp = RetData.split("\n")[0].split("\t");
				RetData = Communication("READ_TCPMSGREQ_COMMHEAD", arrtmp[0] + "\t" + workGubun);
			}
        }
 
    	if (RetData.trim().indexOf("Error:") >= 0) {
    		JOptionPane.showMessageDialog(null, RetData);
    		return "";
    	}	
    	return RetData;
	}
    public void myPaneTableTcpRefresh(JXTreeTable mytable, DefaultMutableTreeNode mytree, String pMsgMapInfo)
    {
    	//Array 및 Struct 에 대한 Work TreeNode 를 선언한다.
    	DefaultMutableTreeNode Array1 = null;
    	DefaultMutableTreeNode Array2 = null;
    	DefaultMutableTreeNode Array3 = null;
    	DefaultMutableTreeNode Struct1 = null;
    	DefaultMutableTreeNode Struct2 = null;
    	DefaultMutableTreeNode Struct3 = null;

    	//Tree가 Null 이면, 그냥 리턴한고, 그렇지 않으면 Tree의 모든 Child Node를 삭제한다.
   	    if (mytree == null) return;
   	    mytree.removeAllChildren();

   	    //pMsgMapInfo 에는 Body부 Bypass 매핑에 대한 정보가 들어있을 수 있으므로 <BODYBYPASS> 로 구분하여, MsgMap정보를 분리한다.
   	    String[] gridData = new String[6];
   	    String[] arrpMsgMapInfo = pMsgMapInfo.split("<BODYBYPASS>");
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
        }catch(Exception e) {
     
    		e.printStackTrace();
    		JOptionPane.showMessageDialog(null,"전문정보를 Display 하면서 오류가 발생하였습니다. 해당 업무에 대한 매핑구분정보를 확인하세요.");	
        }
 
        mytable.setTreeTableModel(new myTcpMapModel(mytree));
        mytable.expandAll();
        
		//컬럼사이즈 설정
        SetColumnWidth(mytable);

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
    private boolean DeleteKeyNoInfo(DefaultMutableTreeNode dataNode, String pKeyNo)
    {
    	myTcpMapComm data = (myTcpMapComm) dataNode.getUserObject(); 
    	for(int i=0;i < dataNode.getChildCount();i++)
    	{
    		DefaultMutableTreeNode tmpnode = (DefaultMutableTreeNode)dataNode.getChildAt(i);
    		data = (myTcpMapComm) tmpnode.getUserObject(); 
    	    if (data.getNo().equals(pKeyNo)) {
    	    	tmpnode.removeFromParent();
    	    	return true;
    	    }
       		if (tmpnode.getChildCount() > 0 ) {
    			boolean rcflag = DeleteKeyNoInfo(tmpnode,pKeyNo);
    			if (rcflag == true) return true;
    		}
    	}
    	return false;
    }
    
    private int SetEditDataSub(DefaultMutableTreeNode myroot, DefaultMutableTreeNode myArrayNode, HashMap<String, String> hash, int idx, String indata  )
    {
    	 
    	for(int i=0;i < myArrayNode.getChildCount();i++){
    		DefaultMutableTreeNode WorkNode = (DefaultMutableTreeNode) myArrayNode.getChildAt(i);  
    		myTcpMapComm data = (myTcpMapComm) WorkNode.getUserObject();
  		    String item = data.getItem();
 
        	byte[] tmpbyte = new byte[Integer.parseInt(data.getLen())];
    		System.arraycopy(indata.getBytes(), idx, tmpbyte, 0, Integer.parseInt(data.getLen()));
            idx += Integer.parseInt(data.getLen());
            data.setConts(new String(tmpbyte));
            
            //Arrany 길이변수로 사용되는 항목을 추출하기 위해 모든 영문명으로 hash에 put
            hash.put(data.getEng(), new String(tmpbyte));
           
    	}
   
        return idx;
    }
	private void SaveReqResMsg( String pReqmsg, String pResMsg)
	{
    	String RetData = Communication("SAVE_TCPREQRES_COMMHEAD", gApplCode + "<DATAGUBUN>" + pReqmsg + "<DATAGUBUN>" + pResMsg);
    	if (!RetData.trim().equals("OK")) {
    		JOptionPane.showMessageDialog(null,"공통부전문  저장을 하지 못하였습니다.");
    	}	
    	else {
    		JOptionPane.showMessageDialog(null,"공통부전문 저장을 완료하였습니다.");
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
}