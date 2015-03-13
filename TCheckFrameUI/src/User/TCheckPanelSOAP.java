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
	private Font font12 = new Font("����ü",Font.BOLD,12);
	private Font font13 = new Font("����ü",Font.BOLD,13);
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
        txtMsg.setLineWrap(true); //������ �ʹ� ��� �ڵ����� �������� ����
        txtMsg.setColumns(120); //���� ũ��(����ũ��)
        txtMsg.setRows(0); //���� ũ��(����ũ��)
        JScrollPane myPanetxtMsg = new JScrollPane(txtMsg);
        myPanetxtMsg.setAutoscrolls(true);
        
		//TextArea Send
        txtSend = new JTextArea();
        txtSend.setLineWrap(true); //������ �ʹ� ��� �ڵ����� �������� ����
        txtSend.setColumns(120); //���� ũ��(����ũ��)
        txtSend.setRows(0); //���� ũ��(����ũ��)
        JScrollPane myPanetxtSend = new JScrollPane(txtSend);
        myPanetxtSend.setAutoscrolls(true);
        
		//TextArea Recv
        txtRecv = new JTextArea();
        txtRecv.setLineWrap(true); //������ �ʹ� ��� �ڵ����� �������� ����
        txtRecv.setColumns(120); //���� ũ��(����ũ��)
        txtRecv.setRows(0); //���� ũ��(����ũ��)
        JScrollPane myPanetxtRecv = new JScrollPane(txtRecv);
        myPanetxtRecv.setAutoscrolls(true);
 
        //ȭ�� Split
		mypanel.setLayout(new BorderLayout());
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT); 
        splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT); 
        splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT); 
        splitPane3 = new JSplitPane(JSplitPane.VERTICAL_SPLIT); 
        
        splitPane1.setContinuousLayout(true); //�������� ���̾ƿ� ��� Ȱ��ȭ
        splitPane1.setRightComponent(splitPane2);
        splitPane1.setLeftComponent(Init_Button());
        splitPane1.setDividerLocation(65);
        splitPane1.setDividerSize(4); //����̴�(�и���) ���� ����
        splitPane1.setBackground(Color.BLACK);
 
        splitPane2.setContinuousLayout(true); //�������� ���̾ƿ� ��� Ȱ��ȭ
        splitPane2.setRightComponent(myPanetxtRecv);
        splitPane2.setLeftComponent(myPanetxtSend);
        splitPane2.setDividerLocation(left_width);
        splitPane2.setDividerSize(4); //����̴�(�и���) ���� ����
        splitPane2.setBackground(Color.BLACK);
    
        //JSplitPane ����
 
        splitPane3.setContinuousLayout(true); //�������� ���̾ƿ� ��� Ȱ��ȭ
        splitPane3.setLeftComponent(splitPane1); //���� ������Ʈ ����
        splitPane3.setRightComponent(myPanetxtMsg); //���� ������Ʈ ����
        splitPane3.setDividerLocation(left_height); //����̴�(�и���) ��ġ ����      
        splitPane3.setDividerSize(4); //����̴�(�и���) ���� ����
        splitPane3.setBackground(Color.BLACK);
        
        splitPane.setContinuousLayout(true); //�������� ���̾ƿ� ��� Ȱ��ȭ
        splitPane.setRightComponent(splitPane3);
        splitPane.setLeftComponent(Init_Tree("INIT"));
        splitPane.setDividerLocation(300);
        splitPane.setDividerSize(4); //����̴�(�и���) ���� ����
        splitPane.setBackground(Color.BLACK);
        
        
 
        mypanel.add(splitPane, BorderLayout.CENTER);
        ThreadListenerRecv threadrecv = new ThreadListenerRecv(GetRegister("TCHECKER_RESPORTURL"), txtRecv, null,null,null,txtMsg);
        threadrecv.start();
  
    }
	private JPanel Init_Button()
	{
		int left_width  = (rect.width - 300) / 2;
		int left_height = rect.height / 100 * 80; 
		
    	//���� Command Button ����
    	JPanel myPaneSub1  = new JPanel(); myPaneSub1.setBackground(Color.WHITE);
    	
 
    	myPaneSub1.setLayout(null);
  
    	//------------ URL ------------------------------------------
    	JLabel labelURL = new JLabel("URL");
    	labelURL.setFont(font13);
    	labelURL.setSize(300, 20);   // setBounds�� �ƴϸ� setSize�� setLocation�� ���ÿ� ����ؾ���
        
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
 
    	btnSend = new JButton("��û���� ����",new ImageIcon("./Image/send.png")); 
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
		        
	            //Kindcode ��������
	            spos = txtSend.getText().indexOf("<HDR_DOC_CODE>");
	            epos = txtSend.getText().indexOf("</HDR_DOC_CODE>");
	            if (spos <= 0 || epos <= 0) {
	            	WriteMsg("�������� <HDR_DOC_CODE> �� ã�� �� �� �����ϴ�.");
	            	return;
	            }
	            else {
	            	pKindCode = txtSend.getText().substring(spos + "<HDR_DOC_CODE>".length(), epos);
	            }
	     
	            
	            //Txcode ��������
	            spos = txtSend.getText().indexOf("<HDR_BIZ_CODE>");
	            epos = txtSend.getText().indexOf("</HDR_BIZ_CODE>");
	            if (spos <= 0 || epos <= 0) {
	            	WriteMsg("�������� <HDR_BIZ_CODE> �� ã�� �� �� �����ϴ�.");
	            	return;
	            }
	            else {
	            	pTxCode = txtSend.getText().substring(spos + "<HDR_BIZ_CODE>".length(), epos);
	            }
	            
				SendMsgURL(GetRegister("TCHECKER_USERID"), gApplCode, pKindCode, pTxCode, (String)comburl.getSelectedItem(), txtSend.getText());
    		}
       	});
    	btnSend.setEnabled(false);
    	
    	btnressave = new JButton("�������� ����",new ImageIcon("./Image/save.gif")); 
    	myPaneSub1.add(btnressave);
    	mypanel.add(myPaneSub1,BorderLayout.NORTH);
    	btnressave.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				SaveResMsg(GetRegister("TCHECKER_USERID"), txtRecv.getText());
    		}
       	});
    	btnressave.setEnabled(false);
 
    	
    	//Request PopupMenu ����
        JMenuItem reqjumninit = new JMenuItem("�����ʱ�ȭ",new ImageIcon("./Image/refresh.gif"));
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
    	
    	//Response PopupMenu ����
        JMenuItem resjumninit = new JMenuItem("�����ʱ�ȭ",new ImageIcon("./Image/refresh.gif"));
        JMenuItem reswireless = new JMenuItem("������Ʈ��ũ ����Ȯ��",new ImageIcon("./Image/resmap.gif"));
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
   
    	Font font = new Font("����ü",Font.BOLD,14);
    	JLabel labelSend = new JLabel("��û����"); labelSend.setFont(font); labelSend.setForeground(Color.BLUE); myPaneSub1.add(labelSend);
    	JLabel labelRecv = new JLabel("��������"); labelRecv.setFont(font); labelRecv.setForeground(Color.BLUE); myPaneSub1.add(labelRecv);
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
    		JOptionPane.showMessageDialog(null,"�������� �Ƿڸ� ���� ���Ͽ����ϴ�.");
    		WriteMsg("�������� �Ƿڸ� ���� ���Ͽ����ϴ�.");
    	}	
    	else {
    		WriteMsg("�������� �Ƿڸ� �Ͽ����ϴ�.");
    	}
	}
	
	private void SaveResMsg(String pUesrID, String sendmsg)
	{
    	String RetData = Communication("SAVE_URLMSGRES", pUesrID + "\t" + gApplCode + "\t" + gKindCode + "\t" + gTxCode + "<DATAGUBUN>" + sendmsg);
    	if (!RetData.trim().equals("OK")) {
    		JOptionPane.showMessageDialog(null,"������������ ���� �Ƿڸ� ���� ���Ͽ����ϴ�.");
    		WriteMsg("������������ ���� �Ƿڸ� ���� ���Ͽ����ϴ�.");
    	}	
    	else {
    		String[] arrRetData = RetData.split("\n");
    		for(int i=0;i < arrRetData.length ;i++){
                txtRecv.append(arrRetData[i]);
    		}
    		WriteMsg("������������ ���� �Ƿڸ� �Ϸ��Ͽ����ϴ�.");
    	}
	}
	private void LoadReqMsg(String pUesrID, String pApplCode)
	{
    	String RetData = Communication("LOAD_URLMSGREQ", pUesrID + "\t" + gApplCode + "\t" + gKindCode + "\t" + gTxCode  );
    	if (RetData.trim().equals("")) {
    		WriteMsg("������ ��û������ �����ϴ�.");
    	}	
    	else {
    		txtSend.setText(RetData);
    		txtSend.setCaretPosition(0);
    		WriteMsg("������ ��û������ �ҷ����� ���� �Ϸ��Ͽ����ϴ�.");
    	}
	}
	private void LoadResMsg(String pUesrID, String sendmsg)
	{
    	String RetData = Communication("LOAD_URLMSGRES", pUesrID + "\t" + gApplCode + "\t" + gKindCode + "\t" + gTxCode );
    	if (RetData.trim().equals("")) {
    		WriteMsg("����� ���������� �����ϴ�.");
    	}	
    	else {
    		txtRecv.setText(RetData);
    		txtRecv.setCaretPosition(0);
    		WriteMsg("���������� �ҷ����� ���� �Ϸ��Ͽ����ϴ�.");
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
        
        //�ŷ������� ��쿡 ����ϱ� ���ؼ� ����+����+�ŷ��� ���� permit ���� �� hash�� �����Ѵ�.
    	final HashMap<String, String> hashpermit = new HashMap<String, String>();
		hashpermit.clear();
		
    	String RetPermitTx = Communication("READ_PERMIT", GetRegister("TCHECKER_USERID"));
    	String[] arrRetPermitTx = RetPermitTx.split("\n");
		for(int k=0;k < arrRetPermitTx.length ;k++){
			String[] arrtmppermit = arrRetPermitTx[k].split("\t");
			hashpermit.put(arrtmppermit[0]+"\t"+ arrtmppermit[2]+"\t"+arrtmppermit[4], arrtmppermit[6]);
		}
		
       
        root = new DefaultMutableTreeNode("��û �� ���� �������");
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
    		/* ���ֺ� ���� ���� ������ �������� ���� */
//    		if (UserPermit.equals("��ü����")) ApplPermit = true;
//            if (UserPermit.equals("��������")) {
    		
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
        			nodemain = new DefaultMutableTreeNode("��û����");
            		oldmain = arrtmpApplTxListSub[0];
            		root.add(nodemain);
            	}
        		if (arrtmpApplTxListSub[0].equals("2")) {
            		nodemain = new DefaultMutableTreeNode("�������");
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
        		//��ü����, �������� ����
                nodetx = new DefaultMutableTreeNode(arrtmpApplTxListSub[5] + " - " + arrtmpApplTxListSub[6]);
            	nodekind.add(nodetx);
        	}
        	else {
        		//�ŷ����� ����
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
                                
                                //�ش������ ���Ͽ� �������� ������ ������ �о�´�.
                                if (tmpapplgubun.trim().equals("��û����")) {
                                	txtMsg.setText("");
                                	txtSend.setText("");
                                	txtRecv.setText("");
                                	LoadReqMsg(GetRegister("TCHECKER_USERID"), gApplCode);
                                	btnSend.setEnabled(true);
                                	btnressave.setEnabled(false);
                                }
                                if (tmpapplgubun.trim().equals("�������")) {
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
        	//Request PopupMenu ����
            JMenuItem treejumninit = new JMenuItem("������� �����ϱ�",new ImageIcon("./Image/refresh.gif"));
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

		if (pInOut.trim().equals("��û����")) pActionName = "READ_INBOUDNURL";
		if (pInOut.trim().equals("�������")) pActionName = "READ_OUTBOUDNURL";
		
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
	        one_client.connect(new InetSocketAddress(GetRegister("TCHECKER_ANYLINKIP"), Integer.parseInt(GetRegister("TCHECKER_ANYLINKPORT"))), 3000);  //3�� ��ٸ�
            one_client.setSoTimeout(5000);
            
            dos = new DataOutputStream(one_client.getOutputStream());
            dis = new DataInputStream(one_client.getInputStream());
            
    		//Send
            senddata = senddata.replace("\r","");
            String cmd = cmdstr + "                                        ";
            String SendStr = String.format("%08d", senddata.getBytes().length) + cmd.substring(0,32) + senddata;
            dos.write(SendStr.getBytes(), 0, SendStr.getBytes().length);
            dos.flush();
 
      	    //����Ÿ�� �������� �б�
            int    tmplen = 0;
            byte[] tmpbyte1 = new byte[8];
            try{
               for(int i = 0 ; i < 8 ;i++) {
            	   tmpbyte1[i] = dis.readByte();  
            	   tmplen++;
               }
            }catch(Exception e2){}

            if (tmplen < 8)  {
      	    	JOptionPane.showMessageDialog(null, "���ŵ���Ÿ�� �����ϴ�.");
      	    	return "";
      	    }

            //����Ÿ�� �б�
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
		String reg_dt = formatter.format(new java.util.Date()); //��� �Ͻ�
		formatter = new java.text.SimpleDateFormat("[HH:mm:ss]", java.util.Locale.KOREA);
		String reg_tm = formatter.format(new java.util.Date());  //��Ͻð� 
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
