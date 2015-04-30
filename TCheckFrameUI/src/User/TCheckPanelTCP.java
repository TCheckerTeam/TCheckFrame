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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.JXTreeTable;

import Manager.DataModel.myTxDetailComm;
import User.DataModel.myTcpMapComm;
import User.DataModel.myTcpMapModel;
 
public class TCheckPanelTCP {
	private JPanel mypanel = null;
	private JSplitPane splitPane, splitPane1, splitPane2, splitPane3, splitPane4, splitPane5, splitPane6, splitPane7;
	private JTextArea txtMsg , txtMsgBody;
	private JComboBox combSendPort;
	private JLabel lblAppl, lblKind, lblTx;
	private Rectangle rect ;
	private boolean gLogOnFlag = false;
	private String gApplCode, gKindCode, gTxCode, gUserPCIP, gUserID, gSendPortNo;
	private String[] gKindTxList = null;
	private DefaultMutableTreeNode roottcpreq = null, roottcpres = null;
	private JXTreeTable myPaneTcpReqTable, myPaneTcpResTable;
	private JPopupMenu popuptree = new JPopupMenu();
	private JPopupMenu popupreq = new JPopupMenu();
	private JPopupMenu popupres = new JPopupMenu();
	private Font font12 = new Font("����ü",Font.BOLD,12);
	private Font font13 = new Font("����ü",Font.BOLD,13);
	private int  gIndex = 0;
    private boolean Body_ByPass_Flag = false;
    private boolean NonType_flag = false;
    private String TxDetailInfo = "";
    private JCheckBox mappersend ;
    private JButton btnSend, btnRefresh, btnressave, btnCommHeader,btnSendTotLen, btnRecvTotLen;
   
	public TCheckPanelTCP(JPanel panel, String pUserID)
	{
		gUserID = GetRegister("TCHECKER_USERID");
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
 
 
		//TextArea txtMsgBody
        txtMsgBody = new JTextArea();
        txtMsgBody.setLineWrap(true); //������ �ʹ� ��� �ڵ����� �������� ����
        txtMsgBody.setColumns(120); //���� ũ��(����ũ��)
        txtMsgBody.setRows(1000); //���� ũ��(����ũ��)
        JScrollPane myPanetxtMsgBody = new JScrollPane(txtMsgBody);
        myPanetxtMsgBody.setAutoscrolls(true);
       
        //ȭ�� Split
		mypanel.setLayout(new BorderLayout());
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT); 
        splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT); 
        splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT); 
        splitPane3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT); 
        splitPane4 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT); 
        splitPane5 = new JSplitPane(JSplitPane.VERTICAL_SPLIT); 
        splitPane6 = new JSplitPane(JSplitPane.VERTICAL_SPLIT); 
        splitPane7 = new JSplitPane(JSplitPane.VERTICAL_SPLIT); 
        
        splitPane1.setContinuousLayout(true); //�������� ���̾ƿ� ��� Ȱ��ȭ
        splitPane1.setRightComponent(splitPane2);
        splitPane1.setLeftComponent(Init_Button());
        splitPane1.setDividerLocation(35);
        splitPane1.setDividerSize(3); //����̴�(�и���) ���� ����
        splitPane1.setBackground(Color.BLACK);
 
        splitPane2.setContinuousLayout(true); //�������� ���̾ƿ� ��� Ȱ��ȭ
        splitPane2.setRightComponent(Init_Table_Response());
        splitPane2.setLeftComponent(Init_Table_Request());
        splitPane2.setDividerLocation(left_width);
        splitPane2.setDividerSize(3); //����̴�(�и���) ���� ����
        splitPane2.setBackground(Color.BLACK);
    
        splitPane3.setContinuousLayout(true); //�������� ���̾ƿ� ��� Ȱ��ȭ
        splitPane3.setRightComponent(myPanetxtMsgBody);
        splitPane3.setLeftComponent(myPanetxtMsg);
        splitPane3.setDividerLocation(left_width);
        splitPane3.setDividerSize(4); //����̴�(�и���) ���� ����
        splitPane3.setBackground(Color.BLACK);
        
        JLabel lbl1 = new JLabel("    �ŷ���������"); lbl1.setFont(font13);lbl1.setForeground(Color.YELLOW);
        JLabel lbl2 = new JLabel("    Body�� ByPass ���ο� ���� Body �Է�����"); lbl2.setFont(font13);lbl2.setForeground(Color.YELLOW);
        
        splitPane4.setContinuousLayout(true); //�������� ���̾ƿ� ��� Ȱ��ȭ
        splitPane4.setRightComponent(lbl2);
        splitPane4.setLeftComponent(lbl1);
        splitPane4.setDividerLocation(left_width);
        splitPane4.setDividerSize(5); //����̴�(�и���) ���� ����
        splitPane4.setBackground(Color.BLACK);
        
        splitPane5.setContinuousLayout(true); //�������� ���̾ƿ� ��� Ȱ��ȭ
        splitPane5.setRightComponent(splitPane3);
        splitPane5.setLeftComponent(splitPane4);
        splitPane5.setDividerLocation(25);
        splitPane5.setDividerSize(3); //����̴�(�и���) ���� ����
        splitPane5.setBackground(Color.BLACK);
        //JSplitPane ����
 
        splitPane6.setContinuousLayout(true); //�������� ���̾ƿ� ��� Ȱ��ȭ
        splitPane6.setLeftComponent(splitPane1); //���� ������Ʈ ����
        splitPane6.setRightComponent(splitPane5); //���� ������Ʈ ����
        splitPane6.setDividerLocation(left_height); //����̴�(�и���) ��ġ ����      
        splitPane6.setDividerSize(2); //����̴�(�и���) ���� ����
        splitPane6.setBackground(Color.BLACK);
    
        splitPane7.setContinuousLayout(true); //�������� ���̾ƿ� ��� Ȱ��ȭ
        splitPane7.setLeftComponent(Init_Tree_TxList("INIT")); //���� ������Ʈ ����
        splitPane7.setRightComponent(Init_SelectPanel()); //���� ������Ʈ ����
        splitPane7.setDividerLocation(left_height); //����̴�(�и���) ��ġ ����      
        splitPane7.setDividerSize(2); //����̴�(�и���) ���� ����
        splitPane7.setBackground(Color.BLACK);
        
        splitPane.setContinuousLayout(true); //�������� ���̾ƿ� ��� Ȱ��ȭ
        splitPane.setRightComponent(splitPane6);
        splitPane.setLeftComponent(splitPane7);  //Init_Tree_TxList()
        splitPane.setDividerLocation(300);
        splitPane.setDividerSize(4); //����̴�(�и���) ���� ����
        splitPane.setBackground(Color.BLACK);
        
        mypanel.add(splitPane, BorderLayout.CENTER);
 
        ThreadListenerRecv threadrecv = new ThreadListenerRecv(GetRegister("TCHECKER_RESPORTTCP"), null, roottcpres, myPaneTcpResTable,txtMsgBody, txtMsg);
        threadrecv.start();
    }
	private JPanel Init_SelectPanel()
	{
    	//���� Command Button ����
    	JPanel myPaneSub1  = new JPanel(); myPaneSub1.setBackground(Color.WHITE);
    	myPaneSub1.setLayout(null);
 
    	//------------ ���� ------------------------------------------
    	JLabel labelAppl = new JLabel("���� :");
    	labelAppl.setFont(font13);
    	labelAppl.setSize(300, 20);   // setBounds�� �ƴϸ� setSize�� setLocation�� ���ÿ� ����ؾ���
    	labelAppl.setForeground(Color.BLUE);
    	myPaneSub1.add(labelAppl );
        
        lblAppl = new JLabel(" ����");
        lblAppl.setFont(font12);
        lblAppl.setSize(500, 20);
    	myPaneSub1.add(lblAppl );
    	
    	//------------ Kind ------------------------------------------
    	JLabel labelKind = new JLabel("���� :");
    	labelKind.setFont(font13);
    	labelKind.setSize(300, 20);   // setBounds�� �ƴϸ� setSize�� setLocation�� ���ÿ� ����ؾ���
    	labelKind.setForeground(Color.BLUE);
    	myPaneSub1.add(labelKind );
        
        lblKind = new JLabel(" ����");
        lblKind.setFont(font12);
        lblKind.setSize(500, 20);
 
    	myPaneSub1.add(lblKind );
 
    	//------------ Tx ------------------------------------------
    	JLabel labelTx = new JLabel("�ŷ� :");
    	labelTx.setFont(font13);
    	labelTx.setSize(300, 20);   // setBounds�� �ƴϸ� setSize�� setLocation�� ���ÿ� ����ؾ���
    	labelTx.setForeground(Color.BLUE);
    	myPaneSub1.add(labelTx );
        
        lblTx = new JLabel(" ����");
        lblTx.setFont(font12);
        lblTx.setSize(500, 20);
    	myPaneSub1.add(lblTx );		
 
    	//------------ SendPort ------------------------------------------
    	JLabel labelSendPort = new JLabel("��Ʈ :");
    	labelSendPort.setForeground(Color.BLUE);
    	labelSendPort.setFont(font13);
    	labelSendPort.setSize(300, 20);   // setBounds�� �ƴϸ� setSize�� setLocation�� ���ÿ� ����ؾ���
         
    	combSendPort = new JComboBox();
    	combSendPort.setFont(font12);
    	combSendPort.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				String cmd = ae.getActionCommand();
				if (cmd.equals("comboBoxChanged")){
					if (combSendPort.getSelectedItem() == null) return;
					gSendPortNo = ((String)combSendPort.getSelectedItem()).trim();
				}
			}
    	});
 
    	myPaneSub1.add(labelSendPort );
    	myPaneSub1.add(combSendPort );
    	
    	// �������
    	btnCommHeader = new JButton("�������");
    	myPaneSub1.add(btnCommHeader);
    	btnCommHeader.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				CommHeaderDialog commhead = new CommHeaderDialog(gApplCode,gKindCode, gTxCode);
			}
       	});
    	btnCommHeader.setVisible(false);
    	
   	
    	labelSendPort.setBounds(5, 10, 100, 20);
    	combSendPort.setBounds(60, 10, 100, 20);
    	btnCommHeader.setBounds(170, 10, 90, 20);
    	
    	labelAppl.setBounds(5, 30, 100, 20);
    	lblAppl.setBounds(50, 30, 300, 20);
    	
    	labelKind.setBounds(5, 50, 100, 20);
    	lblKind.setBounds(50, 50, 300, 20);
    	
    	labelTx.setBounds(5, 70, 100, 20);
    	lblTx.setBounds(50, 70, 300, 20);
    	

    	
    	return myPaneSub1;
	}
	private JPanel Init_Button()
	{
		int left_width  = (rect.width - 300) / 2;
		int left_height = rect.height / 100 * 80; 
		
    	//���� Command Button ����
    	JPanel myPaneSub1  = new JPanel(); myPaneSub1.setBackground(Color.WHITE);
    	
 
    	myPaneSub1.setLayout(null);
 
    	//---------------------- Button -------------------------------------------------------
    	btnSend = new JButton("��û���� ����",new ImageIcon("./Image/send.png")); 
    	btnSend.setFont(font13);
    	myPaneSub1.add(btnSend);
    	mypanel.add(myPaneSub1,BorderLayout.NORTH);
    	btnSend.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
 
				txtMsg.setText("");
				String senddata = "";
				for(int i=0;i < myPaneTcpReqTable.getRowCount();i++){
					String item  = (String)myPaneTcpReqTable.getValueAt(i, 0);
					String type  = (String)myPaneTcpReqTable.getValueAt(i, 1);
					String len   = (String)myPaneTcpReqTable.getValueAt(i, 2);
					String conts = (String)myPaneTcpReqTable.getValueAt(i, 3);
					String eng   = (String)myPaneTcpReqTable.getValueAt(i, 5);
					
					senddata = senddata + item + "\t";
					senddata = senddata + eng  + "\t";
					senddata = senddata + type + "\t";
					senddata = senddata + len  + "\t";
					if (conts.trim().equals("")) {
						  senddata = senddata + "<NODATA>" + "\n";
					}
					else {
						  senddata = senddata + conts + "\n";
					}
					  
				}
 
				if (Body_ByPass_Flag == true) {
					if (gSendPortNo.equals("��������")) {
						 
						SendMapperTypeMsg(senddata + "<BODYBYPASS>" + txtMsgBody.getText());
					}
					else {
						SendMsg(senddata + "<BODYBYPASS>" + txtMsgBody.getText());
					}
				}
				else {
					String item  = (String)myPaneTcpReqTable.getValueAt(0, 0);
					String conts = (String)myPaneTcpReqTable.getValueAt(0, 3);
					if (item.equals("����������Ÿ")) {
						SendNonTypeMsg(conts);
					}
					else {
						if (gSendPortNo.equals("��������")) {
							 
							SendMapperTypeMsg(senddata);
						}
						else {
						     SendMsg(senddata);
						}
					}
										
				}
    		}
       	});
 
 
    	btnressave = new JButton("�������� ����",new ImageIcon("./Image/save.gif")); 
    	btnressave.setFont(font13);
    	myPaneSub1.add(btnressave);
    	mypanel.add(myPaneSub1,BorderLayout.NORTH);
    	btnressave.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				String senddata = "";
				for(int i=0;i < myPaneTcpResTable.getRowCount();i++){
					String item  = (String)myPaneTcpResTable.getValueAt(i, 0);
					String type  = (String)myPaneTcpResTable.getValueAt(i, 1);
					String len   = (String)myPaneTcpResTable.getValueAt(i, 2);
					String conts = (String)myPaneTcpResTable.getValueAt(i, 3);
					String eng   = (String)myPaneTcpResTable.getValueAt(i, 5);
					
					senddata = senddata + item + "\t";
					senddata = senddata + eng  + "\t";
					senddata = senddata + type + "\t";
					senddata = senddata + len  + "\t";
					if (conts.trim().equals("")) {
						  senddata = senddata + "<NODATA>" + "\n";
					}
					else {
						  senddata = senddata + conts + "\n";
					}
					  
				}
				if (Body_ByPass_Flag == true) {
					SaveResMsg(senddata + "<BODYBYPASS>" + txtMsgBody.getText());
				}
				else {
					SaveResMsg(senddata);					
				}
 
    		}
       	});
    	
    	/*�ŷ� ���� ���� Ȯ�� ��ư �߰�*/
    	btnSendTotLen = new JButton("",new ImageIcon("./Image/tool.gif")); 
    	btnSendTotLen.setFont(font13);
    	myPaneSub1.add(btnSendTotLen);
    	mypanel.add(myPaneSub1,BorderLayout.NORTH);
    	btnSendTotLen.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
 
				txtMsg.setText("");
				
				int txTotLen = 0;
				String arrayFlag = "";
				
				for(int i=0;i < myPaneTcpReqTable.getRowCount();i++){
					
					/* �迭 ũ���� ���� ���ش�.*/
					String type = (String)myPaneTcpReqTable.getValueAt(i, 1);
					String colLen = (String)myPaneTcpReqTable.getValueAt(i, 2);
					
					if(type.equals("ARRAY-S")||type.equals("VARCHAR")){
						
						if(!(colLen.substring(0, 1).equals("#") || colLen.substring(0, 1).equals("_"))){
							txTotLen -= Integer.parseInt(colLen);
						}else{
							arrayFlag = " +";
							break;
						}						
					}
					
					txTotLen   += Integer.parseInt(colLen);			
					  
				}
				
				txtMsg.setText("��û���� �� ���� : ["+txTotLen + arrayFlag+"]");
				
				//���õ� Row�� ���� ũ����
			    int sumlen = 0;
			    int sublen = 0;
			    DefaultMutableTreeNode dataNode = null;
			    myTcpMapComm data = null;
			    
			    int[] selectedrows = myPaneTcpReqTable.getSelectedRows();
				for(int i=0;i < selectedrows.length;i++)
				{
					String type = (String)myPaneTcpReqTable.getValueAt(selectedrows[i], 1);
					String colLen = (String)myPaneTcpReqTable.getValueAt(selectedrows[i], 2);
 
				    if (type.toUpperCase().indexOf("ARRAY") >= 0 || type.toUpperCase().indexOf("STRUCT") >= 0) continue;
				    
				    try{
				    	sublen = Integer.parseInt(colLen);
				    }catch(Exception eee){
				    	sublen = 0;
				    }
				    
				    sumlen = sumlen + sublen;
	 
				}
				txtMsg.append("\n�����÷� �� ���� : ["+sumlen + arrayFlag+"]");
			}
    	});
    	
    	btnRecvTotLen = new JButton("",new ImageIcon("./Image/tool.gif")); 
    	btnRecvTotLen.setFont(font13);
    	myPaneSub1.add(btnRecvTotLen);
    	mypanel.add(myPaneSub1,BorderLayout.NORTH);
    	btnRecvTotLen.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
 
				txtMsgBody.setText("");
				
				int txTotLen = 0;
				String arrayFlag = "";
				
				for(int i=0;i < myPaneTcpResTable.getRowCount();i++){
					
					/* �迭 ũ���� ���� ���ش�.*/
					String type = (String)myPaneTcpResTable.getValueAt(i, 1);
					String colLen = (String)myPaneTcpResTable.getValueAt(i, 2);
					
					if(type.equals("ARRAY-S")||type.equals("VARCHAR")){
						
						if(!(colLen.substring(0, 1).equals("#") || colLen.substring(0, 1).equals("_"))){
							txTotLen -= Integer.parseInt(colLen);
						}else{
							arrayFlag = " +";
							break;
						}						
					}
					
					txTotLen   += Integer.parseInt(colLen);			
					  
				}
				
				txtMsgBody.setText("�������� �� ���� : ["+txTotLen + arrayFlag+"]");
				
				
				//���õ� Row�� ���� ũ����
			    int sumlen = 0;
			    int sublen = 0;
			    DefaultMutableTreeNode dataNode = null;
			    myTcpMapComm data = null;
			    
			    int[] selectedrows = myPaneTcpResTable.getSelectedRows();
				for(int i=0;i < selectedrows.length;i++)
				{
					String type = (String)myPaneTcpResTable.getValueAt(selectedrows[i], 1);
					String colLen = (String)myPaneTcpResTable.getValueAt(selectedrows[i], 2);
 
				    if (type.toUpperCase().indexOf("ARRAY") >= 0 || type.toUpperCase().indexOf("STRUCT") >= 0) continue;
				    
				    try{
				    	sublen = Integer.parseInt(colLen);
				    }catch(Exception eee){
				    	sublen = 0;
				    }
				    
				    sumlen = sumlen + sublen;
	 
				}
				txtMsgBody.append("\n�����÷� �� ���� : ["+sumlen + arrayFlag+"]");
				 
			}
    	});
		/*�ŷ� ���� ���� Ȯ�� ��ư �߰�*/
     	 
    	Font font = new Font("����ü",Font.BOLD,14);
    	JLabel labelSend = new JLabel("��û����"); labelSend.setFont(font); labelSend.setForeground(Color.BLUE); myPaneSub1.add(labelSend);
    	JLabel labelRecv = new JLabel("��������"); labelRecv.setFont(font); labelRecv.setForeground(Color.BLUE); myPaneSub1.add(labelRecv);
     	    	
    	labelSend.setBounds(10, 5, 70, 20); 
    	btnSend.setBounds(80, 5, 150, 25);
    	btnSendTotLen.setBounds(250, 5, 25, 25);
 
    	labelRecv.setBounds(left_width + 10 , 5, 70, 20); 
    	btnressave.setBounds(left_width + 80, 5, 150, 25);
    	btnRecvTotLen.setBounds(left_width + 250 , 5, 25, 25); 
    	    	
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
		 
		 
		 //�÷������� ����
		 SetColumnWidth(myPaneTcpReqTable);
 
 
        JScrollPane myPaneSub2 = new JScrollPane(myPaneTcpReqTable);
        myPaneSub2.setAutoscrolls(true);
 
    	//Request PopupMenu ����
        JMenuItem reqjumninit = new JMenuItem("�����ʱ�ȭ",new ImageIcon("./Image/refresh.gif"));
        JMenuItem reqdatainit = new JMenuItem("����Ÿ����",new ImageIcon("./Image/datainit.png"));
        JMenuItem reqdatainrt = new JMenuItem("����Ÿ����",new ImageIcon("./Image/headerdef.png"));
        JMenuItem reqarrayadd = new JMenuItem("�迭ũ������",new ImageIcon("./Image/add_row.png"));
        JMenuItem reqarraydel = new JMenuItem("����ü����",new ImageIcon("./Image/delete_row.png"));
        JMenuItem reqdatauser = new JMenuItem("���������",new ImageIcon("./Image/headerdef.png"));
        
    	popupreq.add(reqjumninit);
    	popupreq.addSeparator();
    	popupreq.add(reqdatainit);
    	popupreq.add(reqdatainrt);
    	popupreq.addSeparator();
    	popupreq.add(reqarrayadd);
    	popupreq.add(reqarraydel);
    	popupreq.addSeparator();
    	popupreq.add(reqdatauser);
    	
    	myPaneTcpReqTable.addMouseListener(new MouseAdapter(){
    		public void mouseClicked(MouseEvent e){
    			if (e.getButton() == 3) {
    				popupreq.show(myPaneTcpReqTable, e.getX(), e.getY());
    			}
    		}
    	});
    	
    	reqjumninit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				LoadMappingMsg(GetRegister("TCHECKER_USERID"), "REQUEST", "INIT");
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
				DataInsertDialog datains = new DataInsertDialog(200,200,400,350,"������ ����Ÿ �Է�");
				
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
    	
    	reqarrayadd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				String pType  = (String)myPaneTcpReqTable.getValueAt(myPaneTcpReqTable.getSelectedRow(), 1);
				String lenstr = (String)myPaneTcpReqTable.getValueAt(myPaneTcpReqTable.getSelectedRow(), 2);
				String pKeyNo = (String)myPaneTcpReqTable.getValueAt(myPaneTcpReqTable.getSelectedRow(), 4);
				 
				if (pType.equals("ARRAY-S")) {
					String pLen = JOptionPane.showInputDialog(null, "�迭ũ�⸦ �Է��ϼ���.");
                    if (pLen.trim().equals("")) return;
					setKeyNoInfo(roottcpreq, pKeyNo, pLen);
					int start = JOptionPane.showConfirmDialog(null, "���� �迭�� �������� �Էµ� ���� ũ��, STRUCT-S �� �� �߰��˴ϴ�.\n����Ͻðڽ��ϱ�?", "�迭ũ������", JOptionPane.YES_NO_OPTION);
  				    if( start == JOptionPane.YES_OPTION){
  				    	myPaneTcpReqTable.setTreeTableModel(new myTcpMapModel(roottcpreq));
  				        SetArrayHandle(roottcpreq);
  				        SetColumnWidth(myPaneTcpReqTable); //�÷������� ����
  				    }
				}
			}
		});
    	
    	reqarraydel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				String pType = (String)myPaneTcpReqTable.getValueAt(myPaneTcpReqTable.getSelectedRow(), 1);
				String pKeyNo = (String)myPaneTcpReqTable.getValueAt(myPaneTcpReqTable.getSelectedRow(), 4);
				
				if (pType.equals("STRUCT-S")) {
					int start = JOptionPane.showConfirmDialog(null, "�÷�Type�� STRUCT-S �̸�, �迭�� ������ Structure�� �� �������� �۾ƾ� ������ �����մϴ�.\n������ ����Ͻðڽ��ϱ�?", "����", JOptionPane.YES_NO_OPTION);
  				    if(start == JOptionPane.YES_OPTION){
  						DeleteKeyNoInfo(roottcpreq, pKeyNo);
  						myPaneTcpReqTable.setTreeTableModel(new myTcpMapModel(roottcpreq));
  				        SetArrayHandle(roottcpreq);
  				        SetColumnWidth(myPaneTcpReqTable); //�÷������� ����
  				    }
 	   
				}
 			}
		});
            	
    	reqdatauser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
                DataInsertDialog datains = new DataInsertDialog(200,200,600,350,"��������� ��û���� ������ �Է��ϼ���.(Tab���� ���� : �ѱ۸�/������/Ÿ��/����)");
				
				if (datains.getInData().equals("")) return;
				
				String savedata = "";
				String indata = datains.getInData();
				String[] arrindata = indata.replace("\r","").split("\n");
				
				for(int i=0;i < arrindata.length ;i++){
					String[] arrtmp = arrindata[i].replace("\t",",").split(",");
					if (arrtmp.length == 4){
						savedata = savedata + arrtmp[0].trim() + "\t";
						savedata = savedata + arrtmp[1].trim() + "\t";
						savedata = savedata + arrtmp[2].trim() + "\t";
						savedata = savedata + arrtmp[3].trim() + "\t";
						savedata = savedata + "<NODATA>" + "\n";
					}
                    if (arrtmp.length == 5){
						savedata = savedata + arrtmp[0].trim() + "\t";
						savedata = savedata + arrtmp[1].trim() + "\t";
						savedata = savedata + arrtmp[2].trim() + "\t";
						savedata = savedata + arrtmp[3].trim() + "\t";
						savedata = savedata + arrtmp[4].trim() + "\n";
					}
				}
				SaveReqMsg(savedata);
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
		 
		 //�÷������� ����
		 SetColumnWidth(myPaneTcpResTable);
 
        JScrollPane myPaneSub2 = new JScrollPane(myPaneTcpResTable);
        myPaneSub2.setAutoscrolls(true);
 
        //Response PopupMenu ����
        
        JMenuItem resjumninit = new JMenuItem("�����ʱ�ȭ",new ImageIcon("./Image/refresh.gif"));
        JMenuItem resdatainit = new JMenuItem("����Ÿ����",new ImageIcon("./Image/datainit.png"));
        JMenuItem resdatainrt = new JMenuItem("����Ÿ����",new ImageIcon("./Image/headerdef.png"));
        JMenuItem resarrayadd = new JMenuItem("�迭ũ������",new ImageIcon("./Image/add_row.png"));
        JMenuItem resarraydel = new JMenuItem("����ü����",new ImageIcon("./Image/delete_row.png"));
        JMenuItem resjumninfo = new JMenuItem("�������� ��������",new ImageIcon("./Image/property.gif"));
        JMenuItem reswireless = new JMenuItem("������Ʈ��ũ ����Ȯ��",new ImageIcon("./Image/resmap.gif"));
        JMenuItem resdatauser = new JMenuItem("���������",new ImageIcon("./Image/headerdef.png"));
    	popupres.add(resjumninit);
    	popupres.addSeparator();
    	popupres.add(resdatainit);
    	popupres.add(resdatainrt);
    	popupres.addSeparator();
    	popupres.add(resarrayadd);
    	popupres.add(resarraydel);
    	popupres.add(resjumninfo);
    	popupres.add(reswireless);
    	popupres.addSeparator();
    	popupres.add(resdatauser);
    	
    	myPaneTcpResTable.addMouseListener(new MouseAdapter(){
    		public void mouseClicked(MouseEvent e){
    			if (e.getButton() == 3) {
    				popupres.show(myPaneTcpResTable, e.getX(), e.getY());
    			}
    		}
    	});
    	
    	reswireless.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
		 
				String indata = GetWireLessResponse();
				try {
					final HashMap<String, String> hashinrt = new HashMap<String, String>();
					int idx = 0;
					SetEditDataSub(roottcpres,roottcpres, hashinrt, idx, indata);
					hashinrt.clear();
    
				}catch(Exception e){}
 
				myPaneTcpResTable.updateUI();
			}
		});
    	
    	
    	resjumninit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				LoadMappingMsg(GetRegister("TCHECKER_USERID"), "RESPONSE", "INIT");
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
				DataInsertDialog datains = new DataInsertDialog(left_width + 50,200,400,350,"������ ����Ÿ �Է�");
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
    	
    	resarrayadd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				String pType  = (String)myPaneTcpResTable.getValueAt(myPaneTcpResTable.getSelectedRow(), 1);
				String lenstr = (String)myPaneTcpResTable.getValueAt(myPaneTcpResTable.getSelectedRow(), 2);
				String pKeyNo = (String)myPaneTcpResTable.getValueAt(myPaneTcpResTable.getSelectedRow(), 4);
				 
				if (pType.equals("ARRAY-S")) {
					String pLen = JOptionPane.showInputDialog(null, "�迭ũ�⸦ �Է��ϼ���.");
                    if (pLen.trim().equals("")) return;
                    
                    int start = JOptionPane.showConfirmDialog(null, "���� �迭�� �������� �Էµ� ���� ũ��, STRUCT-S �� �� �߰��˴ϴ�.\n����Ͻðڽ��ϱ�?", "�迭ũ������", JOptionPane.YES_NO_OPTION);
  				    if( start == JOptionPane.YES_OPTION){
  						setKeyNoInfo(roottcpres, pKeyNo, pLen);
  						 
  						myPaneTcpResTable.setTreeTableModel(new myTcpMapModel(roottcpres));
  				        SetArrayHandle(roottcpres);
  				        SetColumnWidth(myPaneTcpResTable); //�÷������� ����
  				    }
 
				}
			}
		});
    	
    	resarraydel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				String pType = (String)myPaneTcpResTable.getValueAt(myPaneTcpResTable.getSelectedRow(), 1);
				String pKeyNo = (String)myPaneTcpResTable.getValueAt(myPaneTcpResTable.getSelectedRow(), 4);
				
				if (pType.equals("STRUCT-S")) {
					int start = JOptionPane.showConfirmDialog(null, "�÷�Type�� STRUCT-S �̸�, �迭�� ������ Structure�� �� �������� �۾ƾ� ������ �����մϴ�.\n������ ����Ͻðڽ��ϱ�?", "����", JOptionPane.YES_NO_OPTION);
  				    if( start == JOptionPane.YES_OPTION){
  						DeleteKeyNoInfo(roottcpres, pKeyNo);
  						myPaneTcpResTable.setTreeTableModel(new myTcpMapModel(roottcpreq));
  				        SetArrayHandle(roottcpres);
  				        SetColumnWidth(myPaneTcpResTable); //�÷������� ����
  				    }
				}
			}
		});
    	resjumninfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				String msginfo = "���������� ������ �����ϴ�.\n\n";
				
 				String RetData = Communication("READ_RESLINKONE", gApplCode + "\t" + gKindCode + "\t" + gTxCode );
				if (RetData.trim().indexOf("Error:") >= 0) {
					JOptionPane.showMessageDialog(null, "�������� �ŷ��� ã�µ� ������ �߻��Ͽ����ϴ�.:" + RetData);
		    	}
				else if (RetData.trim().equals("NOT-FOUND")) {
					msginfo = msginfo + "  ���� : " + lblAppl.getText() + "\n";
					msginfo = msginfo + "  ���� : " + lblKind.getText() + "\n";
					msginfo = msginfo + "  �ŷ� : " + lblTx.getText() + "\n";
			    }
				else {
					String[] arrtmp = RetData.split("\n")[0].split("\t");
					msginfo = msginfo + "  ���� : " + arrtmp[0] + " - " + arrtmp[1] + "\n";
					msginfo = msginfo + "  ���� : " + arrtmp[2] + " - " + arrtmp[3] + "\n";
					msginfo = msginfo + "  �ŷ� : " + arrtmp[4] + " - " + arrtmp[5] + "\n";
				}
			 
				JOptionPane.showMessageDialog(null, msginfo);
	    		 
			}
		});
 
    	resdatauser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				int left_width  = rect.width / 100 * 50;
				DataInsertDialog datains = new DataInsertDialog(200,200,600,350, "��������� �������� ������ �Է��ϼ���.(Tab���� ���� : �ѱ۸�/������/Ÿ��/����)");
				
				if (datains.getInData().equals("")) return;
				
				String savedata = "";
				String indata = datains.getInData();
				String[] arrindata = indata.replace("\r","").split("\n");
				
				for(int i=0;i < arrindata.length ;i++){
					String[] arrtmp = arrindata[i].replace("\t",",").split(",");
					if (arrtmp.length == 4){
						savedata = savedata + arrtmp[0].trim() + "\t";
						savedata = savedata + arrtmp[1].trim() + "\t";
						savedata = savedata + arrtmp[2].trim() + "\t";
						savedata = savedata + arrtmp[3].trim() + "\t";
						savedata = savedata + "<NODATA>" + "\n";
					}
                    if (arrtmp.length == 5){
						savedata = savedata + arrtmp[0].trim() + "\t";
						savedata = savedata + arrtmp[1].trim() + "\t";
						savedata = savedata + arrtmp[2].trim() + "\t";
						savedata = savedata + arrtmp[3].trim() + "\t";
						savedata = savedata + arrtmp[4].trim() + "\n";
					}
				}
				SaveResMsg(savedata);
			}
		});
        return myPaneSub2;
	}
	private JScrollPane Init_Tree_TxList(String pGubun)
	{
		JTree  xTreeMain = null;
		DefaultMutableTreeNode root, nodemain  = null, nodeappl = null;
		String oldmain = "";
		String oldappl = "";
        String pApplCode = ""; 
 
        root = new DefaultMutableTreeNode("��û �� ���� �������");
        String ApplTxList = GetInOutTcpAppl(gUserID);
        String[] arrApplTxList = ApplTxList.split("\n");
        for(int i=0;i < arrApplTxList.length ;i++){
        	if (arrApplTxList[i].trim().equals("")) break;
        	String[] arrtmpApplTxListSub = arrApplTxList[i].split("\t");
    		pApplCode = arrtmpApplTxListSub[1];
  
        	if (!oldmain.equals(arrtmpApplTxListSub[0])) {
        		//root
        		if (arrtmpApplTxListSub[0].equals("I") ) {
        			nodemain = new DefaultMutableTreeNode("��û����");
            		oldmain = arrtmpApplTxListSub[0];
            		root.add(nodemain);
            	}
        		if (arrtmpApplTxListSub[0].equals("O") ) {
            		nodemain = new DefaultMutableTreeNode("�������");
            		oldmain = arrtmpApplTxListSub[0];
            		root.add(nodemain);
            	}
        		if (arrtmpApplTxListSub[0].equals("N") ) {
        			System.out.println("arrtmpApplTxListSub[0]=N SKIP");
        			continue;
        		}
 
        		//appl
        		nodeappl = new DefaultMutableTreeNode(arrtmpApplTxListSub[1] + " - " + arrtmpApplTxListSub[2]);
        		oldappl = arrtmpApplTxListSub[1];
        		nodemain.add(nodeappl);
 
        	}
        	if (!oldappl.equals(arrtmpApplTxListSub[1])) {
 
        		//appl
        		nodeappl = new DefaultMutableTreeNode(arrtmpApplTxListSub[1] + " - " + arrtmpApplTxListSub[2]);
        		oldappl = arrtmpApplTxListSub[1];
        		nodemain.add(nodeappl);
        	}
        }
       
        xTreeMain = new JTree(root);
        xTreeMain.addTreeSelectionListener( new TreeSelectionListener()
                {
                    public void valueChanged(TreeSelectionEvent e) {
                    	    String tmpapplgubun = "";
                            String tmppath = e.getPath().toString();
                            tmppath = tmppath.substring(1, tmppath.length() - 1);
                            System.out.println("Tree Click : " + tmppath.toString());
                            
                            String[] arrtmp = tmppath.toString().split(",");
                            if (arrtmp.length == 2) {
                            	tmpapplgubun = arrtmp[1];
                            }
                            if (arrtmp.length == 3) {
                            	tmpapplgubun = arrtmp[1];
                            	gApplCode = arrtmp[2].split("-")[0].trim();
                            	
                            	DefaultMutableTreeNode selectedNode=(DefaultMutableTreeNode)e.getPath().getLastPathComponent();
                            	if (selectedNode.getChildCount() == 0){
                            		String tmpkindtxlist = "";
                            		if (tmpapplgubun.trim().equals("��û����"))  tmpkindtxlist = GetInOutKindTxList(gApplCode, "I");
                            		if (tmpapplgubun.trim().equals("�������"))  tmpkindtxlist = GetInOutKindTxList(gApplCode, "O");
                            		String[] arrtmpkindtxlist = tmpkindtxlist.split("\n");
                            		
                            		String oldkind = "";
                            		DefaultMutableTreeNode nodekind = null;
                            		
                            		for(int i=0;i < arrtmpkindtxlist.length ;i++){
                            			if (arrtmpkindtxlist[i].trim().equals("")) break;
                            			
                            			System.out.println(" ���� �ŷ� : ["+arrtmpkindtxlist[i]+"]");
                            			
                            			String[] arrtmpApplTxListSub = arrtmpkindtxlist[i].split("\t");
                            			
                            			//���� 2�� ������ ���� ���� ����
                            			System.out.println("oldKind : ["+oldkind+"] arrtmpApplTxListSub : ["+arrtmpApplTxListSub[0].trim()+"]");
                            			if (!oldkind.equals(arrtmpApplTxListSub[0].trim())) {
                            				//kind
                                    		nodekind = new DefaultMutableTreeNode(arrtmpApplTxListSub[0] + " - " +arrtmpApplTxListSub[1]);
                                    		oldkind = arrtmpApplTxListSub[0];
                            			}
                            			DefaultMutableTreeNode nodetx = new DefaultMutableTreeNode(arrtmpApplTxListSub[2] + " - " +arrtmpApplTxListSub[3]);
                                    	nodekind.add(nodetx);
                                    	selectedNode.add(nodekind);
                            		}
                            	}
 
                                roottcpreq.removeAllChildren();
                                roottcpres.removeAllChildren();
                                myPaneTcpReqTable.updateUI();
                				myPaneTcpResTable.updateUI();
                            }
                            if (arrtmp.length == 4) {
                            	tmpapplgubun = arrtmp[1];
                            	gApplCode = arrtmp[2].split("-")[0].trim();
                            	gKindCode = arrtmp[3].split("-")[0].trim();
                            	
                                roottcpreq.removeAllChildren();
                                roottcpres.removeAllChildren();
                                myPaneTcpReqTable.updateUI();
                				myPaneTcpResTable.updateUI();
                            }
                            if (arrtmp.length == 5) {
                            	tmpapplgubun = arrtmp[1];
                            	gApplCode  = arrtmp[2].split("-")[0].trim();
                                gKindCode  = arrtmp[3].split("-")[0].trim();
                                gTxCode    = arrtmp[4].split("-")[0].trim();

                                lblAppl.setText(arrtmp[2]);
                                lblKind.setText(arrtmp[3]);
                                lblTx.setText(arrtmp[4]);
                                
                                txtMsg.setText("");
                                txtMsgBody.setText("");
                                roottcpreq.removeAllChildren();
                                roottcpres.removeAllChildren();
                                myPaneTcpReqTable.updateUI();
                				myPaneTcpResTable.updateUI();
                				LoadMappingMsg(GetRegister("TCHECKER_USERID"), "REQUEST", "READ");
                				LoadMappingMsg(GetRegister("TCHECKER_USERID"), "RESPONSE", "READ");
                				
                				
                				btnSend.setEnabled(false);
                				btnressave.setEnabled(false);
                				System.out.println("[" + tmpapplgubun.trim() + "]");
                				if (tmpapplgubun.trim().equals("��û����")){
                					btnSend.setEnabled(true);
                				}
                				if (tmpapplgubun.trim().equals("�������")){
                					btnressave.setEnabled(true);
                				}
 
                            }
                            
        			    	//�ش������ ���� ��Ʈ������ �о�´�.
                            String tmpSendPortOld = gSendPortNo;

        			    	combSendPort.removeAllItems();
        			    	
        			    	
        			    	//Send Port ���
        			    	boolean ExistFG = false;
        			    	String portlist = GetSendPort(gApplCode);
        			    	String[] arrtportlist = portlist.split("\n");
        			    	for(int i=0;i < arrtportlist.length ;i++){
        			    		if (arrtportlist[i].trim().equals('"')) break;
        			    		for(int k=0;k < combSendPort.getItemCount();k++){
    			    				if (combSendPort.getItemAt(k).equals(arrtportlist[i])) continue;
    			    			}
    			    		    combSendPort.addItem(arrtportlist[i]);
    			    		    if (tmpSendPortOld != null && tmpSendPortOld.equals(tmpSendPortOld)) ExistFG = true;
        			    	}
        			     
        			    	combSendPort.addItem("��������");
        			    	if (ExistFG) combSendPort.setSelectedItem(tmpSendPortOld);
        			    	else combSendPort.setSelectedItem("��������");
                    }
                }
        );
        xTreeMain.expandRow(1);
    
        JScrollPane myPaneSubtree1 = new JScrollPane(xTreeMain);
        myPaneSubtree1.setAutoscrolls(true);
 
        //Tree Refresh
        if (pGubun.equals("INIT")) {
        	//Request PopupMenu ����
            JMenuItem treejumninit = new JMenuItem("������� �����ϱ�",new ImageIcon("./Image/refresh.gif"));
            popuptree.add(treejumninit);
            treejumninit.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent ae){
    				Init_Tree_TxList("REFRESH");
    			}
    		});
        }
        else {
	        splitPane7.setLeftComponent(myPaneSubtree1);
	        splitPane7.updateUI();
        }
        
        xTreeMain.addMouseListener(new MouseAdapter(){
    		public void mouseClicked(MouseEvent e){
    			if (e.getButton() == 3) {
    				popuptree.show(splitPane7, e.getX(), e.getY());
    			}
    		}
    	});
        return myPaneSubtree1;
	}
	 
	private void SendMsg(String sendmsg)
	{
		myPaneTcpResTable.removeAll();
		
		//UserID + UserPCIP + PortNo + ApplCode + KindCode + TxCode
    	String RetData = Communication("SEND_TCPMSG", gUserID + "\t" + gApplCode + "\t" + gKindCode + "\t" + gTxCode + "\t" + gSendPortNo + "<DATAGUBUN>" + sendmsg);
    	if (!RetData.trim().equals("OK")) {
    		JOptionPane.showMessageDialog(null,"�������� �Ƿڸ� ���� ���Ͽ����ϴ�.");
    		WriteMsg("�������� �Ƿڸ� ���� ���Ͽ����ϴ�.");
    	}	
    	else {
    		WriteMsg("�������� �Ƿڸ� �Ͽ����ϴ�.");
    	}
	}
	private void SendNonTypeMsg(String sendmsg)
	{
		myPaneTcpResTable.removeAll();
		
		//UserID + UserPCIP + PortNo + ApplCode + KindCode + TxCode
    	String RetData = Communication("SEND_TCPNONTYPEMSG", gUserID + "\t" + gApplCode + "\t" + gKindCode + "\t" + gTxCode + "\t" + gSendPortNo + "<DATAGUBUN>" + sendmsg);
    	if (RetData.trim().indexOf("Error:") >= 0) {
    		JOptionPane.showMessageDialog(null,RetData);
    		WriteMsg(RetData);
    	}	
    	else {
    		DefaultMutableTreeNode dataMemberNode = (DefaultMutableTreeNode)roottcpres.getChildAt(0);
			myTcpMapComm data = (myTcpMapComm) dataMemberNode.getUserObject(); 
			data.setConts(RetData);
			myPaneTcpResTable.updateUI();
    		WriteMsg("�������� �� ������ �Ϸ��Ͽ����ϴ�.");
 
    	}
	}
	private void SendMapperTypeMsg(String sendmsg)
	{
		myPaneTcpResTable.removeAll();
		
		//UserID + UserPCIP + PortNo + ApplCode + KindCode + TxCode
    	String RetData = Communication("SEND_TCPMAPPERTYPEMSG", gUserID + "\t" + gApplCode + "\t" + gKindCode + "\t" + gTxCode + "\t" + gSendPortNo + "<DATAGUBUN>" + sendmsg);
    	if (RetData.trim().indexOf("Error:") >= 0) {
    		if (RetData.trim().indexOf("Error:null") >= 0) {
    			WriteMsg("�����۽� �Ϸ�");
    			return;
    		}
    		
    		JOptionPane.showMessageDialog(null,RetData);
    		WriteMsg(RetData);
    	}	
    	else {
    		final HashMap<String, String> hashinrt = new HashMap<String, String>();
			int idx = 0;
			
			try {
			    SetEditDataSub(roottcpres,roottcpres, hashinrt, idx, RetData);
			    myPaneTcpResTable.expandAll();
			      
			    
			}catch(Exception e){}
			
			 
			myPaneTcpResTable.updateUI();
    		WriteMsg("�������� �� ������ �Ϸ��Ͽ����ϴ�.");
    		WriteMsg("���� ���������͸� ���\n[" + RetData + "]\n");
    		 
    	}
	}
	private String GetCommonHead(String ReqResGubun, String workGubun)
	{
		//��������� ������� �ʴ� ��쿡 ������� ������ �о�´�.
		//mapper ���۽� �ʿ��ϱ� ������
		
		String RetData = "";
        if (ReqResGubun.equals("REQUEST")) {
        	RetData = Communication("READ_TCPMSGREQ_COMMHEAD", gApplCode + "\t" + workGubun);
 
        	if (RetData.indexOf("NOTEXIST") >= 0) return "NOTEXIST";
        }
        if (ReqResGubun.equals("RESPONSE")) {
			String RetDataSub = Communication("READ_RESLINKONE", gApplCode + "\t" + gKindCode + "\t" + gTxCode );
			if (RetData.trim().indexOf("Error:") >= 0) {
				JOptionPane.showMessageDialog(null, "�������� �ŷ��� ã�µ� ������ �߻��Ͽ����ϴ�.:" + RetDataSub);
				return "";
	    	}
			else if (RetDataSub.trim().equals("NOT-FOUND")) {
				RetData = Communication("READ_TCPMSGRES_COMMHEAD", gApplCode + "\t" + workGubun);
				if (RetData.indexOf("NOTEXIST") >= 0) return "NOTEXIST";
		    }
			else {
				String[] arrtmp = RetData.split("\n")[0].split("\t");
				RetData = Communication("READ_TCPMSGREQ_COMMHEAD", arrtmp[0] + "\t" + workGubun);
				if (RetData.indexOf("NOTEXIST") >= 0) return "NOTEXIST";
			}
        }
 
    	if (RetData.trim().indexOf("Error:") >= 0) {
    		JOptionPane.showMessageDialog(null, RetData);
    		WriteMsg(RetData);
    		return "";
    	}	
    	return RetData;
	}
	
	private void SaveReqMsg( String sendmsg)
	{
    	String RetData = Communication("SAVE_TCPMSGREQ", gUserID + "\t" + gApplCode + "\t" + gKindCode + "\t" + gTxCode + "<DATAGUBUN>" + sendmsg);
    	if (!RetData.trim().equals("OK")) {
    		JOptionPane.showMessageDialog(null,"��û�������� ���� �Ƿڸ� ���� ���Ͽ����ϴ�.");
    		WriteMsg("��û�������� ���� �Ƿڸ� ���� ���Ͽ����ϴ�.");
    	}	
    	else {
    		LoadMappingMsg(gUserID, "REQUEST", "INIT");
    		WriteMsg("��û�������� ���� �Ƿڸ� �Ϸ��Ͽ����ϴ�.");
    	}
	}
	private void SaveResMsg( String sendmsg)
	{
    	String RetData = Communication("SAVE_TCPMSGRES", gUserID + "\t" + gApplCode + "\t" + gKindCode + "\t" + gTxCode + "<DATAGUBUN>" + sendmsg);
    	if (!RetData.trim().equals("OK")) {
    		JOptionPane.showMessageDialog(null,"������������ ���� �Ƿڸ� ���� ���Ͽ����ϴ�.");
    		WriteMsg("������������ ���� �Ƿڸ� ���� ���Ͽ����ϴ�.");
    	}	
    	else {
    		LoadMappingMsg(gUserID, "RESPONSE", "READ");
    		WriteMsg("������������ ���� �Ƿڸ� �Ϸ��Ͽ����ϴ�.");
    	}
	}
 
	private void LoadMappingMsg(String pUesrID, String pInOut, String pWork)
	{
		if (pInOut.equals("REQUEST")) {
			
			if (GetCommonHead(pInOut, "READ").indexOf("NOTEXIST") >= 0) {
				btnCommHeader.setVisible(false);
			}
			else {
				btnCommHeader.setVisible(true);
			}
			
			String RetData = Communication("READ_TCPMSGREQ", pUesrID + "\t" + gApplCode + "\t" + gKindCode + "\t" + gTxCode + "\t" + pWork );
	    	if (RetData.trim().indexOf("Error:") >= 0) {
        		WriteMsg("��û������ �ҷ����� ���Ͽ����ϴ�. "  + RetData);
        		if (roottcpreq == null) return;
        		roottcpreq.removeAllChildren();
	    	}
	    	else {
	    		Body_ByPass_Flag = false;
	       	    String[] arrpMsgMapInfo = RetData.split("<BODYBYPASS>");
	       	    if (RetData.indexOf("<BODYBYPASS>") >= 0) {
	       	    	Body_ByPass_Flag = true;
	       	    	WriteMsg("�����ϴܿ� <Body�� ByPass ���ο� ���� Body �Է�����> �� Body ������ �Է��ؾ� �ϴ� �ŷ��Դϴ�.");
	       	    	
	       	    	if (arrpMsgMapInfo.length > 1){
	       	    		RetData = arrpMsgMapInfo[0];
	       	    		txtMsgBody.setText(arrpMsgMapInfo[1]);
	       	    	}
	       	    }
	   	    	 
    		    myPaneTableTcpRefresh(myPaneTcpReqTable, roottcpreq, RetData);
	    	}
	    	
		}
		if (pInOut.equals("RESPONSE")) {
			if (!pWork.equals("INIT")) {
				//�ش� ����/����/�ŷ��� ���� �������� ȭ���� �д´�.
				//����, ������ �������� ���� ��ư�� �̿��ؼ� ����� ȭ���̹Ƿ� �ٷ� ȭ�鿡 �����ش�.
				String DataRes = Communication("READ_TCPMSGRES_FILE", gApplCode + "\t" + gKindCode + "\t" + gTxCode + "\t" + pWork );
				if (!DataRes.trim().equals("") && !DataRes.trim().equals("NOT-FOUND")) {
					myPaneTableTcpRefresh(myPaneTcpResTable, roottcpres, DataRes);
					return;
				}
			}
 
			//�ش� ����/����/�ŷ��� ���� �������� ȭ���� ����, ���� Load�� ���������� ���Ͽ� �ʱ�ȭ�� ������ �����´�.
			//����, ��������� �ǿ� �ִ� ��쿡�� ������� ����/����/�ŷ��� ���Ͽ� ����� ��û������ �о�´�.
			String RetData = "";
			String RetDataRes = Communication("READ_RESLINKONE", gApplCode + "\t" + gKindCode + "\t" + gTxCode );
			if (RetDataRes.trim().equals("NOT-FOUND")) {
				RetData = Communication("READ_TCPMSGRES", pUesrID + "\t" + gApplCode + "\t" + gKindCode + "\t" + gTxCode + "\t" + pWork );
			}
			else {
				String[] arrWrok = RetDataRes.split("\t");
				RetData = Communication("READ_TCPMSGREQ", pUesrID + "\t" + arrWrok[0] + "\t" + arrWrok[2] + "\t" + arrWrok[4] + "\t" + pWork );
			}
			if (RetData.trim().indexOf("Error:") >= 0) {
        		WriteMsg("���������� �ҷ����� ���Ͽ����ϴ�."  + RetData);
        		if (roottcpres == null) return;
        		roottcpres.removeAllChildren();
	    	}
			else {
	    	    myPaneTableTcpRefresh(myPaneTcpResTable, roottcpres, RetData);
			}
		}
	}
 
	private String GetInOutTcpAppl(String pApplCode)
	{
    	String RetData = Communication("USER_INOUTTCPAPPL",  pApplCode);
    	if (RetData.trim().equals("ERROR") || RetData.trim().equals("") || RetData.trim().equals("NOT-FOUND")) {
    		return "";
    	}	
        return RetData;
	}
	private String GetInOutKindTxList(String pApplCode, String pInOutFlag)
	{
    	String RetData = Communication("USER_KINDTXLIST",  pApplCode + "\t" + pInOutFlag );
    	if (RetData.trim().equals("ERROR") || RetData.trim().equals("") || RetData.trim().equals("NOT-FOUND")) {
    		return "";
    	}	
        return RetData;
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
            one_client.setSoTimeout(60000);
            
            dos = new DataOutputStream(one_client.getOutputStream());
            dis = new DataInputStream(one_client.getInputStream());
            
    		//Send
            senddata = senddata.replace("\r","");
            String cmd = cmdstr + "                                        ";
            String SendStr = String.format("%08d", senddata.getBytes().length) + cmd.substring(0,32) + senddata;
            
            System.out.println("PanelTCP Communication Send : [" + SendStr + "]");
            
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
      	    
      	    System.out.println("PanelTCP Communication Rcv : [" + recvdata + "]");
      	  
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
    public void myPaneTableTcpRefresh(JXTreeTable mytable, DefaultMutableTreeNode mytree, String pMsgMapInfo)
    {
    	//Array �� Struct �� ���� Work TreeNode �� �����Ѵ�.
    	DefaultMutableTreeNode Array1 = null;
    	DefaultMutableTreeNode Array2 = null;
    	DefaultMutableTreeNode Array3 = null;
    	DefaultMutableTreeNode Struct1 = null;
    	DefaultMutableTreeNode Struct2 = null;
    	DefaultMutableTreeNode Struct3 = null;

    	//Tree�� Null �̸�, �׳� �����Ѱ�, �׷��� ������ Tree�� ��� Child Node�� �����Ѵ�.
   	    if (mytree == null) return;
   	    mytree.removeAllChildren();

   	    //pMsgMapInfo ���� Body�� Bypass ���ο� ���� ������ ������� �� �����Ƿ� <BODYBYPASS> �� �����Ͽ�, MsgMap������ �и��Ѵ�.
   	    String[] gridData = new String[6];
   	    String[] arrpMsgMapInfo = pMsgMapInfo.split("<BODYBYPASS>");
   	    if (arrpMsgMapInfo.length > 1) {
   	    	txtMsgBody.setText(arrpMsgMapInfo[1]);
   	    	txtMsgBody.setCaretPosition(0);
   	    }
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
        		e.printStackTrace();
        		JOptionPane.showMessageDialog(null,"���������� Display �ϸ鼭 ������ �߻��Ͽ����ϴ�. �ش� ������ ���� ���α��������� Ȯ���ϼ���.");	
        	}
        	else {
        		JOptionPane.showMessageDialog(null,"���������� Display �ϸ鼭 ������ �߻��Ͽ����ϴ�. ���������� Ȯ���ϼ���.");
        	}
        	
        }

        
        
        mytable.setTreeTableModel(new myTcpMapModel(mytree));
        SetArrayHandle(mytree);
 
		//�÷������� ����
        SetColumnWidth(mytable);
        
        mytable.expandAll();  

    }
    private void SetColumnWidth(JXTreeTable mytable)
    {
		//�÷������� ����
		final int[] columnsWidth = {200, 30, 10, 200};
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
        mytable.getColumn("������").setWidth(0);
        mytable.getColumn("������").setMinWidth(0);
        mytable.getColumn("������").setMaxWidth(0);
        
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
    		System.arraycopy(indata.getBytes(), idx, tmpbyte, 0, Integer.parseInt(data.getLen()));
            idx += Integer.parseInt(data.getLen());
            data.setConts(new String(tmpbyte));
            
            //Arrany ���̺����� ���Ǵ� �׸��� �����ϱ� ���� ��� ���������� hash�� put
            hash.put(data.getEng(), new String(tmpbyte));
           
    	}
   
        return idx;
    }
    private String GetSendPort(String pApplCode)
    {
    	try {
    		String RetData = Communication("READ_SENDPORT",  pApplCode);
        	if (RetData.trim().equals("") || RetData.trim().equals("NOT-FOUND")) {
        		return "";
        	}
        	return RetData;
    	}catch(Exception e) { 
    		return "";
    	}
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
    

    private void getLocalIP()
    {
    	try { 
    		    InetAddress localMachine = InetAddress.getLocalHost(); 
    		    gUserPCIP = localMachine.getHostAddress(); 
        } 
    	catch(java.net.UnknownHostException uhe) { 
    		 
        }
    }

}
