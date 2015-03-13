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
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
 
public class TCheckPanelSOAP {
	private JPanel mypanel = null;
	private JSplitPane splitPane, splitPane1, splitPane2, splitPane3;
	private JTextArea txtMsg, txtSend, txtRecv ;
	private JComboBox comburl;
	private Rectangle rect ;
	private boolean gLogOnFlag = false;
	private String gApplCode, gKindCode, gTxCode;
	private Font font12 = new Font("바탕체",Font.BOLD,12);
	private Font font13 = new Font("바탕체",Font.BOLD,13);
	private JPopupMenu popuptree = new JPopupMenu();
	private JPopupMenu popupreq = new JPopupMenu();
	private JPopupMenu popupres = new JPopupMenu();
	private JButton btnSend, btnressave ;
	
	public TCheckPanelSOAP(JPanel panel, String pUserID)
	{
		mypanel = panel;
		myPaneGongUInit();
	}
    public void myPaneGongUInit()
    {
		GraphicsEnvironment ee = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gd = ee.getScreenDevices();
		rect = ee.getMaximumWindowBounds();
		int left_width  = (rect.width - 300) / 2;
		int left_height = rect.height / 100 * 80; 
 
		//TextArea MSG
        txtMsg = new JTextArea();
        txtMsg.setLineWrap(true); //한줄이 너무 길면 자동으로 개행할지 설정
        txtMsg.setColumns(120); //열의 크기(가로크기)
        txtMsg.setRows(0); //행의 크기(세로크기)
        JScrollPane myPanetxtMsg = new JScrollPane(txtMsg);
        myPanetxtMsg.setAutoscrolls(true);
        
		//TextArea Send
        txtSend = new JTextArea();
        txtSend.setLineWrap(true); //한줄이 너무 길면 자동으로 개행할지 설정
        txtSend.setColumns(120); //열의 크기(가로크기)
        txtSend.setRows(0); //행의 크기(세로크기)
        JScrollPane myPanetxtSend = new JScrollPane(txtSend);
        myPanetxtSend.setAutoscrolls(true);
        
		//TextArea Recv
        txtRecv = new JTextArea();
        txtRecv.setLineWrap(true); //한줄이 너무 길면 자동으로 개행할지 설정
        txtRecv.setColumns(120); //열의 크기(가로크기)
        txtRecv.setRows(0); //행의 크기(세로크기)
        JScrollPane myPanetxtRecv = new JScrollPane(txtRecv);
        myPanetxtRecv.setAutoscrolls(true);
 
        //화면 Split
		mypanel.setLayout(new BorderLayout());
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT); 
        splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT); 
        splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT); 
        splitPane3 = new JSplitPane(JSplitPane.VERTICAL_SPLIT); 
        
        splitPane1.setContinuousLayout(true); //연속적인 레이아웃 기능 활성화
        splitPane1.setRightComponent(splitPane2);
        splitPane1.setLeftComponent(Init_Button());
        splitPane1.setDividerLocation(65);
        splitPane1.setDividerSize(4); //디바이더(분리대) 굵기 설정
        splitPane1.setBackground(Color.BLACK);
 
        splitPane2.setContinuousLayout(true); //연속적인 레이아웃 기능 활성화
        splitPane2.setRightComponent(myPanetxtRecv);
        splitPane2.setLeftComponent(myPanetxtSend);
        splitPane2.setDividerLocation(left_width);
        splitPane2.setDividerSize(4); //디바이더(분리대) 굵기 설정
        splitPane2.setBackground(Color.BLACK);
    
        //JSplitPane 설정
 
        splitPane3.setContinuousLayout(true); //연속적인 레이아웃 기능 활성화
        splitPane3.setLeftComponent(splitPane1); //좌측 컴포넌트 장착
        splitPane3.setRightComponent(myPanetxtMsg); //우측 컴포넌트 장착
        splitPane3.setDividerLocation(left_height); //디바이더(분리대) 위치 설정      
        splitPane3.setDividerSize(4); //디바이더(분리대) 굵기 설정
        splitPane3.setBackground(Color.BLACK);
        
        splitPane.setContinuousLayout(true); //연속적인 레이아웃 기능 활성화
        splitPane.setRightComponent(splitPane3);
        splitPane.setLeftComponent(Init_Tree("INIT"));
        splitPane.setDividerLocation(300);
        splitPane.setDividerSize(4); //디바이더(분리대) 굵기 설정
        splitPane.setBackground(Color.BLACK);
        
        
 
        mypanel.add(splitPane, BorderLayout.CENTER);
        ThreadListenerRecv threadrecv = new ThreadListenerRecv(GetRegister("TCHECKER_RESPORTURL"), txtRecv, null,null,null,txtMsg);
        threadrecv.start();
  
    }
	private JPanel Init_Button()
	{
		int left_width  = (rect.width - 300) / 2;
		int left_height = rect.height / 100 * 80; 
		
    	//상위 Command Button 설정
    	JPanel myPaneSub1  = new JPanel(); myPaneSub1.setBackground(Color.WHITE);
    	
 
    	myPaneSub1.setLayout(null);
  
    	//------------ URL ------------------------------------------
    	JLabel labelURL = new JLabel("URL");
    	labelURL.setFont(font13);
    	labelURL.setSize(300, 20);   // setBounds가 아니면 setSize와 setLocation을 동시에 사용해야함
        
        comburl = new JComboBox();
        comburl.setFont(font12);
        comburl.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				String cmd = ae.getActionCommand();
				if (cmd.equals("comboBoxChanged")){
					if (comburl.getSelectedItem() == null) return;
					String msg = (String)comburl.getSelectedItem();
				}
			}
    	});
 
    	myPaneSub1.add(labelURL );
    	myPaneSub1.add(comburl );
 
    	btnSend = new JButton("요청전문 전송",new ImageIcon("./Image/send.png")); 
    	myPaneSub1.add(btnSend);
    	mypanel.add(myPaneSub1,BorderLayout.NORTH);
    	btnSend.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				txtMsg.setText("");
				
				String sendmsg = "";
				String pUrl = ((String)comburl.getSelectedItem()).split("-")[0].trim();
				String pKindCode = "";
				String pTxCode = "";
		        int    spos = 0;
		        int    epos = 0;
		        
	            //Kindcode 가져오기
	            spos = txtSend.getText().indexOf("<HDR_DOC_CODE>");
	            epos = txtSend.getText().indexOf("</HDR_DOC_CODE>");
	            if (spos <= 0 || epos <= 0) {
	            	WriteMsg("전문에서 <HDR_DOC_CODE> 을 찾을 수 가 없습니다.");
	            	return;
	            }
	            else {
	            	pKindCode = txtSend.getText().substring(spos + "<HDR_DOC_CODE>".length(), epos);
	            }
	     
	            
	            //Txcode 가져오기
	            spos = txtSend.getText().indexOf("<HDR_BIZ_CODE>");
	            epos = txtSend.getText().indexOf("</HDR_BIZ_CODE>");
	            if (spos <= 0 || epos <= 0) {
	            	WriteMsg("전문에서 <HDR_BIZ_CODE> 을 찾을 수 가 없습니다.");
	            	return;
	            }
	            else {
	            	pTxCode = txtSend.getText().substring(spos + "<HDR_BIZ_CODE>".length(), epos);
	            }
	            
				SendMsgURL(GetRegister("TCHECKER_USERID"), gApplCode, pKindCode, pTxCode, (String)comburl.getSelectedItem(), txtSend.getText());
    		}
       	});
    	btnSend.setEnabled(false);
    	
    	btnressave = new JButton("응답전문 저장",new ImageIcon("./Image/save.gif")); 
    	myPaneSub1.add(btnressave);
    	mypanel.add(myPaneSub1,BorderLayout.NORTH);
    	btnressave.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				SaveResMsg(GetRegister("TCHECKER_USERID"), txtRecv.getText());
    		}
       	});
    	btnressave.setEnabled(false);
 
    	
    	//Request PopupMenu 설정
        JMenuItem reqjumninit = new JMenuItem("전문초기화",new ImageIcon("./Image/refresh.gif"));
    	popupreq.add(reqjumninit);
 
    	txtSend.addMouseListener(new MouseAdapter(){
    		public void mouseClicked(MouseEvent e){
    			if (e.getButton() == 3) {
    				popupreq.show(txtSend, e.getX(), e.getY());
    			}
    		}
    	});
    	
    	reqjumninit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
			     txtSend.setText("");
			}
		});
    	
    	//Response PopupMenu 설정
        JMenuItem resjumninit = new JMenuItem("전문초기화",new ImageIcon("./Image/refresh.gif"));
        JMenuItem reswireless = new JMenuItem("무선네트워크 응답확인",new ImageIcon("./Image/resmap.gif"));
    	popupres.add(resjumninit);
    	popupres.add(reswireless);
    	
    	txtRecv.addMouseListener(new MouseAdapter(){
    		public void mouseClicked(MouseEvent e){
    			if (e.getButton() == 3) {
    				popupres.show(txtRecv, e.getX(), e.getY());
    			}
    		}
    	});
    	
    	resjumninit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
			     txtRecv.setText("");
			}
		});
    	
    	reswireless.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
 
				String indata = GetWireLessResponse();
				txtRecv.setText(indata);
				txtRecv.setCaretPosition(0);
			}
		});
   
    	Font font = new Font("바탕체",Font.BOLD,14);
    	JLabel labelSend = new JLabel("요청전문"); labelSend.setFont(font); labelSend.setForeground(Color.BLUE); myPaneSub1.add(labelSend);
    	JLabel labelRecv = new JLabel("응답전문"); labelRecv.setFont(font); labelRecv.setForeground(Color.BLUE); myPaneSub1.add(labelRecv);
    	JLabel labelGubun = new JLabel("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"); 
    	labelGubun.setFont(font); 
    	labelGubun.setForeground(Color.GRAY); 
    	myPaneSub1.add(labelGubun);
    
    	labelURL.setBounds(10, 10, 30, 20);
    	comburl.setBounds(45, 10, 490, 20);
    	btnSend.setBounds(550, 7, 145, 25);
    	btnressave.setBounds(700, 7, 145, 25);
    	
    	labelGubun.setBounds(0, 27, 1500, 20);
    	
    	labelSend.setBounds(10, 44, 70, 20); 
    	labelRecv.setBounds(550, 44, 70, 20); 
    	
 
    	return myPaneSub1;
	}
	
	private void SendMsgURL(String pUesrID, String pApplCode, String pKindCode, String pTxCode,String pUrl, String sendmsg)
	{
	 
		txtRecv.setText("");
    	String RetData = Communication("SEND_URLMSG", pUesrID + "\t" + pApplCode + "\t" + pKindCode+ "\t" + pTxCode + "\t" + pUrl + "<DATAGUBUN>" + sendmsg);
    	if (!RetData.trim().equals("OK")) {
    		JOptionPane.showMessageDialog(null,"전문전송 의뢰를 하지 못하였습니다.");
    		WriteMsg("전문전송 의뢰를 하지 못하였습니다.");
    	}	
    	else {
    		WriteMsg("전문전송 의뢰를 하였습니다.");
    	}
	}
	
	private void SaveResMsg(String pUesrID, String sendmsg)
	{
    	String RetData = Communication("SAVE_URLMSGRES", pUesrID + "\t" + gApplCode + "\t" + gKindCode + "\t" + gTxCode + "<DATAGUBUN>" + sendmsg);
    	if (!RetData.trim().equals("OK")) {
    		JOptionPane.showMessageDialog(null,"응답전문전송 저장 의뢰를 하지 못하였습니다.");
    		WriteMsg("응답전문전송 저장 의뢰를 하지 못하였습니다.");
    	}	
    	else {
    		String[] arrRetData = RetData.split("\n");
    		for(int i=0;i < arrRetData.length ;i++){
                txtRecv.append(arrRetData[i]);
    		}
    		WriteMsg("응답전문전송 저장 의뢰를 완료하였습니다.");
    	}
	}
	private void LoadReqMsg(String pUesrID, String pApplCode)
	{
    	String RetData = Communication("LOAD_URLMSGREQ", pUesrID + "\t" + gApplCode + "\t" + gKindCode + "\t" + gTxCode  );
    	if (RetData.trim().equals("")) {
    		WriteMsg("마지막 요청전문이 없습니다.");
    	}	
    	else {
    		txtSend.setText(RetData);
    		txtSend.setCaretPosition(0);
    		WriteMsg("마지막 요청전문을 불러오는 것을 완료하였습니다.");
    	}
	}
	private void LoadResMsg(String pUesrID, String sendmsg)
	{
    	String RetData = Communication("LOAD_URLMSGRES", pUesrID + "\t" + gApplCode + "\t" + gKindCode + "\t" + gTxCode );
    	if (RetData.trim().equals("")) {
    		WriteMsg("저장된 응답전문이 없습니다.");
    	}	
    	else {
    		txtRecv.setText(RetData);
    		txtRecv.setCaretPosition(0);
    		WriteMsg("응답전문을 불러오는 것을 완료하였습니다.");
    	}
	}
	private JScrollPane Init_Tree(String pGubun)
	{
	    JTree  xTree = null;
		DefaultMutableTreeNode root, nodemain  = null, nodeappl = null, nodekind = null, nodetx = null;
		String oldmain = "";
		String oldappl = "";
        String oldkind = "";
        String pApplCode = "";
        String pKindCode = "";
        String pTxCode   = "";
        
        //거래권한일 경우에 사용하기 위해서 업무+종별+거래에 대한 permit 정보 을 hash에 저장한다.
    	final HashMap<String, String> hashpermit = new HashMap<String, String>();
		hashpermit.clear();
		
    	String RetPermitTx = Communication("READ_PERMIT", GetRegister("TCHECKER_USERID"));
    	String[] arrRetPermitTx = RetPermitTx.split("\n");
		for(int k=0;k < arrRetPermitTx.length ;k++){
			String[] arrtmppermit = arrRetPermitTx[k].split("\t");
			hashpermit.put(arrtmppermit[0]+"\t"+ arrtmppermit[2]+"\t"+arrtmppermit[4], arrtmppermit[6]);
		}
		
       
        root = new DefaultMutableTreeNode("요청 및 응답 업무목록");
        String ApplTxList = GetApplTxListInfo();
        String[] arrApplTxList = ApplTxList.split("\n");
        for(int i=0;i < arrApplTxList.length ;i++){
        	if (arrApplTxList[i].trim().equals("")) break;
        	String[] arrtmpApplTxListSub = arrApplTxList[i].split("\t");
    		pApplCode = arrtmpApplTxListSub[1];
    		pKindCode = arrtmpApplTxListSub[3];
    		pTxCode   = arrtmpApplTxListSub[5];
        	
        	boolean ApplPermit = false; 
    		String UserPermit = GetRegister("TCHECKER_USERPERMIT");
    		/* 대주보 관련 권한 설정을 영문으로 수정 */
//    		if (UserPermit.equals("전체권한")) ApplPermit = true;
//            if (UserPermit.equals("업무권한")) {
    		
    		if (UserPermit.equals("ALL")) ApplPermit = true;
            if (UserPermit.equals("APPL")) {
            	String RetPermitAppl = Communication("READ_PERMITAPPL", GetRegister("TCHECKER_USERID"));
            	if (!RetPermitAppl.trim().equals("") && !RetPermitAppl.trim().equals("NOT-FOUND")) {
            		String[] arrtmp = RetPermitAppl.split("\n");
            		for(int k=0;k < arrtmp.length ;k++){
            			String[] arrtmppermit = arrtmp[k].split("\t");
            			if (arrtmppermit[0].equals(pApplCode)  ) {
            				if (arrtmppermit[1].equals("Y")) ApplPermit = true;
            				break;
            			}
            		}
            	}
    		}
 
        	if (!oldmain.equals(arrtmpApplTxListSub[0])) {
        		//root
        		if (arrtmpApplTxListSub[0].equals("1")) {
        			nodemain = new DefaultMutableTreeNode("요청업무");
            		oldmain = arrtmpApplTxListSub[0];
            		root.add(nodemain);
            	}
        		if (arrtmpApplTxListSub[0].equals("2")) {
            		nodemain = new DefaultMutableTreeNode("응답업무");
            		oldmain = arrtmpApplTxListSub[0];
            		root.add(nodemain);
            	}
        		//appl
        		nodeappl = new DefaultMutableTreeNode(arrtmpApplTxListSub[1] + " - " + arrtmpApplTxListSub[2]);
        		oldappl = arrtmpApplTxListSub[1];
        		nodemain.add(nodeappl);
 
        		//kind
        		nodekind = new DefaultMutableTreeNode(arrtmpApplTxListSub[3] + " - " + arrtmpApplTxListSub[4]);
        		oldkind = arrtmpApplTxListSub[3];
        		nodeappl.add(nodekind);
 
        	}
        	if (!oldappl.equals(arrtmpApplTxListSub[1])) {
 
        		//appl
        		nodeappl = new DefaultMutableTreeNode(arrtmpApplTxListSub[1] + " - " + arrtmpApplTxListSub[2]);
        		oldappl = arrtmpApplTxListSub[1];
        		nodemain.add(nodeappl);
 
        		//kind
        		nodekind = new DefaultMutableTreeNode(arrtmpApplTxListSub[3] + " - " + arrtmpApplTxListSub[4]);
        		oldkind = arrtmpApplTxListSub[3];
        		nodeappl.add(nodekind);
 
        	}
        	
        	//kind
        	if (!oldkind.equals(arrtmpApplTxListSub[3])) {
 
        		nodekind = new DefaultMutableTreeNode(arrtmpApplTxListSub[3] + " - " + arrtmpApplTxListSub[4]);
        		oldkind = arrtmpApplTxListSub[3];
        		nodeappl.add(nodekind);
        	}
        	
        	if (ApplPermit == true) {
        		//전체권한, 업무권한 제어
                nodetx = new DefaultMutableTreeNode(arrtmpApplTxListSub[5] + " - " + arrtmpApplTxListSub[6]);
            	nodekind.add(nodetx);
        	}
        	else {
        		//거래권한 제어
        		String yn = hashpermit.get(pApplCode + "\t" + pKindCode + "\t" + pTxCode);
        		if (yn == null) yn = "N";
        		if (yn.equals("Y")){
                    nodetx = new DefaultMutableTreeNode(arrtmpApplTxListSub[5] + " - " + arrtmpApplTxListSub[6]);
                	nodekind.add(nodetx);
        		}
        	}
 
        }
       
        xTree = new JTree(root);
        xTree.addTreeSelectionListener( new TreeSelectionListener()
                {
                    public void valueChanged(TreeSelectionEvent e) {
                    	    String tmpapplgubun = "";
                            String tmppath = e.getPath().toString();
                            tmppath = tmppath.substring(1, tmppath.length() - 1);
                            
                            btnSend.setEnabled(false);
                            btnressave.setEnabled(false);
                            
                            String[] arrtmp = tmppath.toString().split(",");
                            if (arrtmp.length == 2) {
                            	tmpapplgubun = arrtmp[1];
                            }
                            if (arrtmp.length == 3) {
                            	tmpapplgubun = arrtmp[1];
                            	gApplCode  = arrtmp[2].split("-")[0].trim();
                            	RefreshURLList(gApplCode, tmpapplgubun);
                            }
                            if (arrtmp.length == 4) {
                            	tmpapplgubun = arrtmp[1];
                            	gApplCode = arrtmp[2].split("-")[0].trim();
                            	gKindCode = arrtmp[3].split("-")[0].trim();
                            	RefreshURLList(gApplCode, tmpapplgubun);
                            }
                            if (arrtmp.length == 5) {
                            	tmpapplgubun = arrtmp[1];
                            	gApplCode  = arrtmp[2].split("-")[0].trim();
                                gKindCode  = arrtmp[3].split("-")[0].trim();
                                gTxCode    = arrtmp[4].split("-")[0].trim();
                                RefreshURLList(gApplCode, tmpapplgubun);
                                
                                //해당업무에 대하여 마지막에 전송한 전문을 읽어온다.
                                if (tmpapplgubun.trim().equals("요청업무")) {
                                	txtMsg.setText("");
                                	txtSend.setText("");
                                	txtRecv.setText("");
                                	LoadReqMsg(GetRegister("TCHECKER_USERID"), gApplCode);
                                	btnSend.setEnabled(true);
                                	btnressave.setEnabled(false);
                                }
                                if (tmpapplgubun.trim().equals("응답업무")) {
                                	txtMsg.setText("");
                                	txtSend.setText("");
                                	txtRecv.setText("");
                                	LoadResMsg(GetRegister("TCHECKER_USERID"), gApplCode);
                                	btnSend.setEnabled(false);
                                	btnressave.setEnabled(true);
                                }
            					
                            }

                    }
                }
        );
        xTree.expandRow(1);
    
        JScrollPane myPaneSubtree1 = new JScrollPane(xTree);
        myPaneSubtree1.setAutoscrolls(true);
  

        //Tree Refresh
        if (pGubun.equals("INIT")) {
        	//Request PopupMenu 설정
            JMenuItem treejumninit = new JMenuItem("업무목록 갱신하기",new ImageIcon("./Image/refresh.gif"));
            popuptree.add(treejumninit);
            treejumninit.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent ae){
    				Init_Tree("REFRESH");
    			}
    		});
        }
        else {
        	splitPane.setLeftComponent(myPaneSubtree1);
        	splitPane.updateUI();
        }
        
        xTree.addMouseListener(new MouseAdapter(){
    		public void mouseClicked(MouseEvent e){
    			if (e.getButton() == 3) {
    				popuptree.show(splitPane, e.getX(), e.getY());
    			}
    		}
    	});
        
        return myPaneSubtree1;
	}
    private String GetApplTxListInfo()
    {
    	try {
    		String RetData = Communication("READ_INOUTTXURL", "NODATA");
        	if (RetData.trim().equals("") || RetData.trim().equals("NOT-FOUND")) {
        		return "";
        	}
        	return RetData;
    	}catch(Exception e) { 
    		return "";
    	}
    	
    }
 
	private void RefreshURLList(String pApplCode, String pInOut)
	{
		String pActionName = "";
		
		comburl.removeAllItems();

		if (pInOut.trim().equals("요청업무")) pActionName = "READ_INBOUDNURL";
		if (pInOut.trim().equals("응답업무")) pActionName = "READ_OUTBOUDNURL";
		
    	String RetData = Communication(pActionName, pApplCode);
    	if (RetData.trim().equals("") || RetData.trim().equals("NOT-FOUND")) {
 
    		return;
    	}	
    	else {
    		
    		String[] arrRetData = RetData.split("\n");
    		for(int i=0;i < arrRetData.length ;i++){
    			String[] arrtmp = arrRetData[i].split("\t");
    			comburl.addItem(arrtmp[0]);
    		}
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
    
    private void WriteMsg(String msg)
    {
		SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
		String reg_dt = formatter.format(new java.util.Date()); //등록 일시
		formatter = new java.text.SimpleDateFormat("[HH:mm:ss]", java.util.Locale.KOREA);
		String reg_tm = formatter.format(new java.util.Date());  //등록시간 
    	txtMsg.insert(reg_tm + msg + "\n", 0);
 
		txtMsg.setCaretPosition(0);
    }
    private String GetRegister(String pKey)
    {
    	Preferences userRootPrefs = Preferences.userRoot();
    	if (userRootPrefs.get(pKey, null) != null) { 
    		return userRootPrefs.get(pKey, "");
    	}
    	return "";
    }
    
    private String GetWireLessResponse()
    {
    	try {
    		String RetData = Communication("READ_WIRELESSRES", "NODATA");
        	if (RetData.trim().equals("") || RetData.trim().equals("NOT-FOUND")) {
        		return "";
        	}
        	return RetData;
    	}catch(Exception e) { 
    		return "";
    	}
    	
    }    
    
}
