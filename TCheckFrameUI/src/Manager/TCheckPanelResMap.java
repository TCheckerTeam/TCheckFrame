package Manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.JXTreeTable;

import Manager.DataModel.myResMapComm;
import Manager.DataModel.myResMapModel;

public class TCheckPanelResMap { 
	private JPanel mypanel = null;
	private JSplitPane splitPane, splitPane0, splitPane1, splitPane2, splitPane3, splitPane4 ;
	private JXTreeTable myPaneTxTable;
	private String SelectedApplcode = "";
	private JLabel lblReqAppl = null;
	private JLabel lblReqKind = null;
	private JLabel lblReqTx = null;
	private JLabel lblResAppl = null;
	private JLabel lblResKind = null;
	private JLabel lblResTx = null;
	private JTextField txtResPortNo = null;
	private DefaultMutableTreeNode permitroot = null;
	private Font font12 = new Font("바탕체",Font.BOLD,12);
	private Font font13 = new Font("바탕체",Font.BOLD,13);
	private JComboBox combreqappl;
	private JComboBox combresappl;
	private String AsyncApplList = "";
	private JTree  xTreeReq = null;
	private JTree  xTreeRes = null;
	
	public TCheckPanelResMap(JPanel panel)
	{
		mypanel = panel;
		
		AsyncApplList = GetAsyncApplListInfo();
		myPaneResMapInit();
	}
	
	public void myPaneResMapInit()
    {
		GraphicsEnvironment ee = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gd = ee.getScreenDevices();
		Rectangle rect = ee.getMaximumWindowBounds();
		int left_width  = rect.width / 100 * 70;
		int left_height = rect.height / 100 * 50; 
 
		 
		mypanel.setLayout(new BorderLayout());
		
 
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT); 
        splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT); 
        splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT); 
        
        splitPane3 = new JSplitPane(JSplitPane.VERTICAL_SPLIT); 
        splitPane4 = new JSplitPane(JSplitPane.VERTICAL_SPLIT); 
 
        //요청 업무목록 및 거래목록
        splitPane3.setContinuousLayout(true); 
        splitPane3.setRightComponent(Init_Tree_Request_Tx());
        splitPane3.setLeftComponent(Init_Tree_Request_Appl());
        splitPane3.setDividerLocation(35);
        splitPane3.setDividerSize(0); //디바이더(분리대) 굵기 설정
        splitPane3.setBackground(Color.WHITE);
        
        //응답 업무목록 및 거래목록
        splitPane4.setContinuousLayout(true); 
        splitPane4.setRightComponent(Init_Tree_Response_Tx());
        splitPane4.setLeftComponent(Init_Tree_Response_Appl());
        splitPane4.setDividerLocation(35);
        splitPane4.setDividerSize(0); //디바이더(분리대) 굵기 설정
        splitPane4.setBackground(Color.WHITE);
        

        //상위 요청/응답 거래목록
        splitPane2.setContinuousLayout(true); //연속적인 레이아웃 기능 활성화
        splitPane2.setRightComponent(splitPane4);
        splitPane2.setLeftComponent(splitPane3);
        splitPane2.setDividerLocation(left_width / 2);
        splitPane2.setDividerSize(6); //디바이더(분리대) 굵기 설정
        splitPane2.setBackground(Color.WHITE);
        
        //상위 초기화버트  + 상위 요청/응답 거래목록
        splitPane1.setContinuousLayout(true); //연속적인 레이아웃 기능 활성화
        splitPane1.setRightComponent(Init_Button());
        splitPane1.setLeftComponent(splitPane2);
        splitPane1.setDividerLocation(left_width);
        splitPane1.setDividerSize(6); //디바이더(분리대) 굵기 설정
        splitPane1.setBackground(Color.WHITE);
        
        //JSplitPane 설정
 
        splitPane.setContinuousLayout(true); //연속적인 레이아웃 기능 활성화
        splitPane.setLeftComponent(splitPane1); //좌측 컴포넌트 장착
        splitPane.setRightComponent(Init_Table()); //우측 컴포넌트 장착
        splitPane.setDividerLocation(left_height); //디바이더(분리대) 위치 설정      
        splitPane.setDividerSize(6); //디바이더(분리대) 굵기 설정
        splitPane.setBackground(Color.WHITE);
        
        JScrollPane myPaneSub2 = new JScrollPane(splitPane);
        mypanel.add(myPaneSub2, BorderLayout.CENTER);
        myPaneSub2.setAutoscrolls(true);
  
    }
	private JLabel CreateLabel(String pTitle)
	{
    	JLabel tmplabel = new JLabel(pTitle);
    	tmplabel.setFont(font13);
        return tmplabel;
	}
	private JPanel Init_Tree_Request_Appl()
	{
    	//------------ Request Async Appl  ------------------------------------------
        combreqappl = new JComboBox();
        combreqappl.setFont(font12);
        combreqappl.setBackground(Color.WHITE);
        combreqappl.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				String cmd = ae.getActionCommand();
				System.out.println(" ReqMap : ["+cmd+"]");
				if (cmd.equals("comboBoxChanged")){
					if (combreqappl.getSelectedItem() == null) return;
					Init_Tree_Request_Tx();
					myPaneTableRefresh();
				}
			}
    	});
        
 
        String[] arrApplList = AsyncApplList.split("\n");
        for(int i=0;i < arrApplList.length ;i++){
        	if (arrApplList[i].trim().equals("")) break;
        	String[] arrtmp = arrApplList[i].split("\t");
        	combreqappl.addItem(arrtmp[0] + " - " + arrtmp[1]);
        }
        
		JPanel c = new JPanel();
		
        JPanel p1 = new JPanel();
        p1.setLayout(new BorderLayout());
        p1.add(new JButton("요청업무"), BorderLayout.WEST);
        p1.add(combreqappl, BorderLayout.CENTER);
 
        c.setLayout(new BorderLayout());
        c.add(p1, BorderLayout.NORTH);
 
		return c;
	}
	private JScrollPane Init_Tree_Request_Tx()
	{
	 
		DefaultMutableTreeNode root, node = null, nodekind = null, nodetx = null;
		String oldappl = "";
        String oldkind = "";
        String selectedapplcode = "";
        
        try{
            selectedapplcode = ((String)combreqappl.getSelectedItem()).split("-")[0].trim();
        }catch(Exception e){}
 
 
        root = new DefaultMutableTreeNode("요청거래");
        
        String txmappinglist = GetApplTxMappingListInfo(selectedapplcode);
        String[] arrtxmappinglist = txmappinglist.split("\n");
        for(int i=0;i < arrtxmappinglist.length ;i++){
        	if (arrtxmappinglist[i].trim().equals("")) break;
        	
        	String[] arrtmpsub = arrtxmappinglist[i].split("\t");
        	
        	if (!arrtmpsub[0].equals(oldappl)){
        		node = new DefaultMutableTreeNode(arrtmpsub[0] + " - " + arrtmpsub[1]);
        		root.add(node);

            	oldappl = arrtmpsub[0];
            	oldkind = "";
        	}
        	if (!arrtmpsub[2].equals(oldkind)){
        		nodekind = new DefaultMutableTreeNode(arrtmpsub[2] + " - " + arrtmpsub[3]);
            	node.add(nodekind);
            	
            	oldkind = arrtmpsub[2];
        	}

        	nodetx = new DefaultMutableTreeNode(arrtmpsub[4] + " - " + arrtmpsub[5]);
        	nodekind.add(nodetx);
        }
        
        if (xTreeReq == null) {
            xTreeReq = new JTree(root);
//            xTreeReq.addTreeSelectionListener( new TreeSelectionListener()
//                    {
//                        public void valueChanged(TreeSelectionEvent e) {
//                                String tmppath = e.getPath().toString();
//                                tmppath = tmppath.substring(1, tmppath.length() - 1);
//                                
//                                String[] arrtmp = tmppath.toString().split(",");
//                                lblReqAppl.setText("NO");
//                                lblReqKind.setText("NO");
//                                lblReqTx.setText("NO");
//                                if (arrtmp.length > 1) lblReqAppl.setText(arrtmp[1]);
//                                if (arrtmp.length > 2) lblReqKind.setText(arrtmp[2]);
//                                if (arrtmp.length > 3) lblReqTx.setText(arrtmp[3]);
//                        }
//                    }
//            );
        	xTreeReq.expandRow(1);
        	xTreeReq.expandRow(2);
        
            JScrollPane myPaneSubtree1 = new JScrollPane(xTreeReq);
            myPaneSubtree1.setAutoscrolls(true);
            return myPaneSubtree1;
        }
        else {
        	xTreeReq.removeAll();
        	xTreeReq = new JTree(root);
        	
        	/*수정 부분*/
        	xTreeReq.addTreeSelectionListener( new TreeSelectionListener()
            {
                public void valueChanged(TreeSelectionEvent e) {
                        String tmppath = e.getPath().toString();
                        tmppath = tmppath.substring(1, tmppath.length() - 1);
                        
                        String[] arrtmp = tmppath.toString().split(",");
                        lblReqAppl.setText("NO");
                        lblReqKind.setText("NO");
                        lblReqTx.setText("NO");
                        if (arrtmp.length > 1) lblReqAppl.setText(arrtmp[1]);
                        if (arrtmp.length > 2) lblReqKind.setText(arrtmp[2]);
                        if (arrtmp.length > 3) lblReqTx.setText(arrtmp[3]);
                }
            }
            );
        	/*수정 부분*/
        	
        	xTreeReq.expandRow(1);
        	xTreeReq.expandRow(2);
     
            JScrollPane myPaneSubtree1 = new JScrollPane(xTreeReq);
            myPaneSubtree1.setAutoscrolls(true);
        	splitPane3.setRightComponent(myPaneSubtree1);
        	splitPane3.updateUI();
	        
        }
    
        return null;
	}
	 
	private JPanel Init_Tree_Response_Appl()
	{
    	//------------ Response Async Appl  ------------------------------------------
        combresappl = new JComboBox();
        combresappl.setFont(font12);
        combresappl.setBackground(Color.WHITE);
        combresappl.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				String cmd = ae.getActionCommand();
				if (cmd.equals("comboBoxChanged")){
					if (combresappl.getSelectedItem() == null) return;
					Init_Tree_Response_Tx();
				}
			}
    	});
        
        String[] arrApplList = AsyncApplList.split("\n");
        for(int i=0;i < arrApplList.length ;i++){
        	if (arrApplList[i].trim().equals("")) break;
        	String[] arrtmp = arrApplList[i].split("\t");
        	combresappl.addItem(arrtmp[0] + " - " + arrtmp[1]);
        }
        
 
		JPanel c = new JPanel();
        JPanel p1 = new JPanel();
        p1.setLayout(new BorderLayout());
        p1.add(new JButton("응답업무"), BorderLayout.WEST);
        p1.add(combresappl, BorderLayout.CENTER);
  
        c.setLayout(new BorderLayout());
        c.add(p1, BorderLayout.NORTH);
  
		return c;
	}
	
	private JScrollPane Init_Tree_Response_Tx()
	{
	    JTree  xTree = null;
		DefaultMutableTreeNode root, node= null, nodekind = null, nodetx = null;
		String oldappl = "";
        String oldkind = "";
 
        String selectedapplcode = "";
        
        try{
            selectedapplcode = ((String)combresappl.getSelectedItem()).split("-")[0].trim();
        }catch(Exception e){}
        
        root = new DefaultMutableTreeNode("응답거래");
        String txmappinglist = GetApplTxMappingListInfo(selectedapplcode);
        String[] arrtxmappinglist = txmappinglist.split("\n");
        for(int i=0;i < arrtxmappinglist.length ;i++){
        	if (arrtxmappinglist[i].trim().equals("")) break;
        	
        	String[] arrtmpsub = arrtxmappinglist[i].split("\t");
        	
        	if (!arrtmpsub[0].equals(oldappl)){
        		node = new DefaultMutableTreeNode(arrtmpsub[0] + " - " + arrtmpsub[1]);
        		root.add(node);

            	oldappl = arrtmpsub[0];
            	oldkind = "";
        	}
        	if (!arrtmpsub[2].equals(oldkind)){
        		nodekind = new DefaultMutableTreeNode(arrtmpsub[2] + " - " + arrtmpsub[3]);
            	node.add(nodekind);
            	
            	oldkind = arrtmpsub[2];
        	}

        	nodetx = new DefaultMutableTreeNode(arrtmpsub[4] + " - " + arrtmpsub[5]);
        	nodekind.add(nodetx);
        }
        
        if (xTreeRes == null) {
        
        	xTreeRes = new JTree(root);
//        	xTreeRes.addTreeSelectionListener( new TreeSelectionListener()
//	                {
//	                    public void valueChanged(TreeSelectionEvent e) {
//	                    	System.out.println("xTreRes aaa 3");
//	                        String tmppath = e.getPath().toString();
//	                        tmppath = tmppath.substring(1, tmppath.length() - 1);
//	                        
//	                        String[] arrtmp = tmppath.toString().split(",");
//	                        lblResAppl.setText("NO");
//	                        lblResKind.setText("NO");
//	                        lblResTx.setText("NO");
//	                        if (arrtmp.length > 1) lblResAppl.setText(arrtmp[1]);
//	                        if (arrtmp.length > 2) lblResKind.setText(arrtmp[2]);
//	                        if (arrtmp.length > 3) lblResTx.setText(arrtmp[3]);
//	                    }
//	                }
//	        );
        	xTreeRes.expandRow(1);
        	xTreeRes.expandRow(2);
	    
	        JScrollPane myPaneSubtree1 = new JScrollPane(xTreeRes);
	        myPaneSubtree1.setAutoscrolls(true);
	  
	        return myPaneSubtree1;
        }
        else {
        
        	xTreeRes.removeAll();
        	xTreeRes = new JTree(root);
        	/* 테스트 추가 */
        	xTreeRes.addTreeSelectionListener( new TreeSelectionListener()
            {
                public void valueChanged(TreeSelectionEvent e) {
                	System.out.println("xTreRes aaa 3");
                    String tmppath = e.getPath().toString();
                    tmppath = tmppath.substring(1, tmppath.length() - 1);
                    
                    String[] arrtmp = tmppath.toString().split(",");
                    lblResAppl.setText("NO");
                    lblResKind.setText("NO");
                    lblResTx.setText("NO");
                    if (arrtmp.length > 1) lblResAppl.setText(arrtmp[1]);
                    if (arrtmp.length > 2) lblResKind.setText(arrtmp[2]);
                    if (arrtmp.length > 3) lblResTx.setText(arrtmp[3]);
                }
            }
    );
        	/* 테스트 추가 */
        	xTreeRes.expandRow(1);
        	xTreeRes.expandRow(2);
     
            JScrollPane myPaneSubtree1 = new JScrollPane(xTreeRes);
            myPaneSubtree1.setAutoscrolls(true);
        	splitPane4.setRightComponent(myPaneSubtree1);
        	splitPane4.updateUI();
	        
        }
       return null;
	}
	private JPanel Init_Button()
	{
    	//상위 Command Button 설정
    	JPanel myPaneSub1  = new JPanel(); myPaneSub1.setBackground(Color.WHITE);
    	JButton btnAllIn, btnAllOut, btnAllNone, btnSave, btnRefresh, btnDelete;

    	
    	myPaneSub1.setLayout(null);
 
    	
    	btnRefresh = new JButton("갱신하기",new ImageIcon("./Image/refresh.gif")); 
    	myPaneSub1.add(btnRefresh);
    	mypanel.add(myPaneSub1,BorderLayout.NORTH);
    	btnRefresh.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				myPaneTableRefresh();
    		}
       	});
    	
    	
    	btnSave = new JButton("저장하기", new ImageIcon("./Image/save.gif"));
    	myPaneSub1.add(btnSave);
   
    	mypanel.add(myPaneSub1,BorderLayout.NORTH);
    	btnSave.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				String savedata = "";
				String tmpstr = "";
				String[] arrtmpstr = null;
				
				if (txtResPortNo.getText().equals("")) txtResPortNo.setText("0");
				
				savedata = savedata + lblReqAppl.getText().split("-")[0].trim() + "\t";
				savedata = savedata + lblReqKind.getText().split("-")[0].trim() + "\t";
				savedata = savedata + lblReqTx.getText().split("-")[0].trim() + "\t";
				savedata = savedata + lblResAppl.getText().split("-")[0].trim() + "\t";
				savedata = savedata + lblResKind.getText().split("-")[0].trim() + "\t";
				savedata = savedata + lblResTx.getText().split("-")[0].trim() + "\t";
				savedata = savedata + txtResPortNo.getText();
	 
				SaveTcheckerResponseLinkInfo(savedata);
				
    		}
       	});
    	
    	btnDelete = new JButton("삭제하기", new ImageIcon("./Image/delete.gif"));
    	myPaneSub1.add(btnDelete);
   
    	mypanel.add(myPaneSub1,BorderLayout.NORTH);
    	btnDelete.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				String deldata = "";
 
				deldata = deldata + lblReqAppl.getText().split("-")[0].trim() + "\t";
				deldata = deldata + lblReqKind.getText().split("-")[0].trim() + "\t";
				deldata = deldata + lblReqTx.getText().split("-")[0].trim() + "\t";
				deldata = deldata + lblResAppl.getText().split("-")[0].trim() + "\t";
				deldata = deldata + lblResKind.getText().split("-")[0].trim() + "\t";
				deldata = deldata + lblResTx.getText().split("-")[0].trim() + "\t";
				deldata = deldata + txtResPortNo.getText();
	 
				DelTcheckerResponseLinkInfo(deldata);
				 
    		}
       	});
    	
    	JLabel lbltmp1 = new JLabel(" 요청업무 : ");myPaneSub1.add(lbltmp1);
    	lblReqAppl = new JLabel(""); 	myPaneSub1.add(lblReqAppl);
 
    	JLabel lbltmp2 = new JLabel(" 요청종별 : ");myPaneSub1.add(lbltmp2);
    	lblReqKind = new JLabel(""); myPaneSub1.add(lblReqKind);
    	
    	JLabel lbltmp3 = new JLabel(" 요청거래 : ");myPaneSub1.add(lbltmp3);
    	lblReqTx   = new JLabel(""); myPaneSub1.add(lblReqTx);
    	
    	JLabel lbltmp4 = new JLabel(" 응답업무 : ");myPaneSub1.add(lbltmp4);
    	lblResAppl = new JLabel(""); myPaneSub1.add(lblResAppl);
    	
    	JLabel lbltmp5 = new JLabel(" 응답종별 : ");myPaneSub1.add(lbltmp5);
    	lblResKind = new JLabel(""); myPaneSub1.add(lblResKind );
    	
    	JLabel lbltmp6 = new JLabel(" 응답거래 : ");myPaneSub1.add(lbltmp6);
    	lblResTx   = new JLabel(""); myPaneSub1.add(lblResTx);
    	
    	JLabel lbltmp   = new JLabel(" 응답포트 : "); myPaneSub1.add(lbltmp);
    	
    	txtResPortNo = new JTextField(5);
    	myPaneSub1.add(txtResPortNo);
    	 
        //컴포넌트 위치 고정
    	lbltmp1.setBounds(10, 5, 65, 20); lblReqAppl.setBounds(75, 5, 500, 20);
    	lbltmp2.setBounds(10, 28, 65, 20); lblReqKind.setBounds(75, 28, 500, 20);
    	lbltmp3.setBounds(10, 50, 65, 20); lblReqTx.setBounds(75, 50, 500, 20);
    	
    	lbltmp4.setBounds(10, 85, 65, 20); lblResAppl.setBounds(75, 85, 500, 20);
    	lbltmp5.setBounds(10, 108, 65, 20); lblResKind.setBounds(75, 108, 500, 20);
    	lbltmp6.setBounds(10, 130, 65, 20); lblResTx.setBounds(75, 130, 500, 20);
    	lbltmp.setBounds(10, 150, 380, 20);

    	txtResPortNo.setBounds(77, 152, 80, 20);
   
    	btnRefresh.setBounds(10, 180, 110, 28);
    	btnSave.setBounds(125, 180, 110, 28);
    	btnDelete.setBounds(240, 180, 110, 28);
    	
    	
    	JLabel lbltitle1   = new JLabel(" >>> Async 거래이고,  In 또는 Out-Bound 거래에 대하여 "); myPaneSub1.add(lbltitle1);
    	JLabel lbltitle2   = new JLabel(" >>> 기 작성된 응답전문을 바로 리턴 할 수 있도록 "); myPaneSub1.add(lbltitle2);
    	JLabel lbltitle3   = new JLabel(" >>> 연결하는 작업화면입니다. 그리고, "); myPaneSub1.add(lbltitle3);
    	JLabel lbltitle4   = new JLabel(" >>> 응답포트는 Out-Bound 거래에 대한 응답포드가"); myPaneSub1.add(lbltitle4);
    	JLabel lbltitle5   = new JLabel(" >>> 다를 경우에 설정합니다."); myPaneSub1.add(lbltitle5);
    	
    	lbltitle1.setBounds(10, 220, 500, 20);
    	lbltitle2.setBounds(10, 240, 500, 20);
    	lbltitle3.setBounds(10, 260, 500, 20);
    	lbltitle4.setBounds(10, 280, 500, 20);
    	lbltitle5.setBounds(10, 300, 500, 20);
   
    	return myPaneSub1;
	}
	
	private JScrollPane Init_Table()
	{
  	     // Table Component Setting 
		 DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new myResMapComm("Root", "", "", "", "", "", "", true));  

		 permitroot = rootNode;
		 myPaneTxTable = new JXTreeTable();
		 myPaneTxTable.setTreeTableModel(new myResMapModel(rootNode));  
		 myPaneTxTable.setEditable(true);
		 myPaneTxTable.setRootVisible(false);

		 //컬럼사이즈 설정
		 final int[] columnsWidth = {150, 150, 150, 150, 150, 150, 50};
		 for(int i=0; i < columnsWidth.length;i++){
			 myPaneTxTable.getColumnModel().getColumn(i).setPreferredWidth(columnsWidth[i]);
		 }
  
        ListSelectionModel rowSM = myPaneTxTable.getSelectionModel();
        rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rowSM.addListSelectionListener(new ListSelectionListener()
            {
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting())        {
                        ListSelectionModel source = (ListSelectionModel)e.getSource();
                    	lblReqAppl.setText((String)myPaneTxTable.getValueAt(source.getMinSelectionIndex(), 0));
                    	lblReqKind.setText((String)myPaneTxTable.getValueAt(source.getMinSelectionIndex(), 1));
                    	lblReqTx.setText((String)myPaneTxTable.getValueAt(source.getMinSelectionIndex(), 2));
                    	lblResAppl.setText((String)myPaneTxTable.getValueAt(source.getMinSelectionIndex(), 3));
                    	lblResKind.setText((String)myPaneTxTable.getValueAt(source.getMinSelectionIndex(), 4));
                    	lblResTx.setText((String)myPaneTxTable.getValueAt(source.getMinSelectionIndex(), 5));
                    	txtResPortNo.setText((String)myPaneTxTable.getValueAt(source.getMinSelectionIndex(), 6));
                    }
                }
            }
        );

        ListSelectionModel colSM = myPaneTxTable.getColumnModel().getSelectionModel();
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
         
        
        JScrollPane myPaneSub2 = new JScrollPane(myPaneTxTable);
        myPaneSub2.setAutoscrolls(true);
 
        
        myPaneTableRefresh();
        return myPaneSub2;
	}
    public void myPaneTableRefresh()
    {
        String selectedapplcode = "";
        try{
            selectedapplcode = ((String)combreqappl.getSelectedItem()).split("-")[0].trim();
        }catch(Exception e){
        	return;
        }
        
   	    if (permitroot == null) return;
    	permitroot.removeAllChildren();
    	
    	String[] gridData = new String[7];
        String applreslinkinfo = SearchTcheckerResponseLinkInfo(selectedapplcode);
        String[] arrtmp = applreslinkinfo.split("\n");
        for(int i=0;i < arrtmp.length ;i++){
        	if (arrtmp[i].trim().equals("")) break;
        	String[] arrtmpsub = arrtmp[i].split("\t");

        	String[] arrreq = FindContents(arrtmpsub[0],arrtmpsub[1],arrtmpsub[2]);
            gridData[0] = arrreq[0];  //요청업무
            gridData[1] = arrreq[1];  //요청종별
            gridData[2] = arrreq[2];  //요청거래
            
            String[] arrres = FindContents(arrtmpsub[3],arrtmpsub[4],arrtmpsub[5]);
            gridData[3] = arrres[0];  //응답업무
            gridData[4] = arrres[1];  //응답종별
            gridData[5] = arrres[2];  //응답거래
            
            gridData[6] = arrtmpsub[6];  //응답포트
            
            permitroot.add(new DefaultMutableTreeNode(new myResMapComm(gridData[0] , gridData[1], 
                    gridData[2] , gridData[3], 
                    gridData[4] , gridData[5],
                    gridData[6] , false)));
  
        }
        
        myPaneTxTable.setTreeTableModel(new myResMapModel(permitroot));
      	 
		//컬럼사이즈 설정
		final int[] columnsWidth = {150, 150, 150, 150, 150, 150, 50};
		for(int i=0; i < columnsWidth.length;i++){
			myPaneTxTable.getColumnModel().getColumn(i).setPreferredWidth(columnsWidth[i]);
		}
    }
    private String[] FindContents(String pApplCode, String pKindCode, String pTxCode)
    {
    	String[] retarr = new String[3];
    	boolean isBreak = false;
 
    	retarr[0] = "NO";
    	retarr[1] = "NO";
    	retarr[2] = "NO";
    	
    	//ApplCode Info
    	String applinfo = GetApplInfo();
        String[] arrapplinfo = applinfo.split("\n");
        for(int i=0;i < arrapplinfo.length ;i++){
        	String[] arrtmp = arrapplinfo[i].split("\t");
        	if (arrtmp[0].equals(pApplCode)) {
        		retarr[0] = arrtmp[0] + " - " + arrtmp[1];
        		
        		//종별코드명 및 거래코드명 추출
        		String txinfo = GetApplTxInfo(arrtmp[0]);
            	String[] arrtxinfo = txinfo.split("\n");
            	for(int j=0;j < arrtxinfo.length ;j++){
            		String[] arrtmp1 = arrtxinfo[j].split("\t");
            		if (arrtmp1[0].equals(pKindCode)) {
            			retarr[1] = arrtmp1[0] + " - " + arrtmp1[1];
                        if (pTxCode.trim().equals("")) {
                        	isBreak = true;
                			break;
                        }
            			
            		}
            		if (arrtmp1[0].equals(pKindCode) && arrtmp1[2].equals(pTxCode)) {
            			retarr[1] = arrtmp1[0] + " - " + arrtmp1[1];
            			retarr[2] = arrtmp1[2] + " - " + arrtmp1[3];
            			isBreak = true;
            			break;
            		}
            	}
        		
        	}
        	if (isBreak) break;
        }
        
        return retarr;
        
        
    }
    private String GetApplInfo()
    {
    	try {
    		String RetData = Communication("READ_APPLINFO", "NODATA");
        	if (RetData.trim().equals("") || RetData.trim().equals("NOT-FOUND")) {
        		JOptionPane.showMessageDialog(null,"업무정보를 가져오지 못했습니다.");
        		return "";
        	}
        	return RetData;
    	}catch(Exception e) { 
    		return "";
    	}
    	
    }
    private String GetApplTxInfo(String pApplCode)
    {
    	try {
    		String RetData = Communication("READ_APPLTXINFO", pApplCode);
        	if (RetData.trim().equals("") || RetData.trim().equals("NOT-FOUND")) {
        		JOptionPane.showMessageDialog(null,"업무코드[" + pApplCode + "]에 대한 정보를 가져오지 못했습니다.");
        		return "";
        	}
        	return RetData;
    	}catch(Exception e) { 
    		return "";
    	}
    	
    }
    
    private String GetAsyncApplListInfo()
    {
    	try {
    		String RetData = Communication("READ_ASYNCAPPLIST", "<NODATA>");
        	if (RetData.trim().equals("") || RetData.trim().equals("NOT-FOUND")) {
        		JOptionPane.showMessageDialog(null,"업무별 거래 정보를 가져오지 못했습니다.");
        		return "";
        	}
        	return RetData;
    	}catch(Exception e) { 
    		return "";
    	}
    	
    }
    private String GetApplTxMappingListInfo(String pApplCode)
    {
    	try {
    		String RetData = Communication("READ_TXMAPPING_LIST", pApplCode);
        	if (RetData.trim().equals("") || RetData.trim().equals("NOT-FOUND")) {
        		return "";
        	}
        	return RetData;
    	}catch(Exception e) { 
    		return "";
    	}
    	
    }
    
    private String SearchTcheckerResponseLinkInfo(String pApplCode)
    {
    	try {
    		String RetData = Communication("READ_RESLINK", pApplCode);
        	if (RetData.trim().equals("") || RetData.trim().equals("NOT-FOUND")) {
        		return "";
        	}
        	return RetData;
    	}catch(Exception e) { 
    		return "";
    	}
    }
    private void SaveTcheckerResponseLinkInfo(String savedata)
    {
    	String RetData = Communication("SAVE_RESLINK", savedata);
    	if (RetData.trim().equals("OK")) {
    		myPaneTableAddRow();
    		JOptionPane.showMessageDialog(null,"거래매핑 정보를 저장 완료하였습니다.");
    	}
    	else {
    		JOptionPane.showMessageDialog(null,"거래매핑 정보를 저장하지 못했습니다.");
    	}
    }
    private void DelTcheckerResponseLinkInfo(String savedata)
    {
    	String RetData = Communication("DELETE_RESLINK", savedata);
    	if (RetData.trim().equals("OK")) {
    		myPaneTableDelRow();
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
            one_client.setSoTimeout(60000);
            
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
    
    private void myPaneTableAddRow()
    {
    	//리스트에 동일한 row가 있으면, 포트번호만 Update 한다.
        for(int i=0;i < permitroot.getChildCount();i++){
        	DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) permitroot.getChildAt(i);  
        	myResMapComm data = (myResMapComm) dataNode.getUserObject();  
			
			if (data.getReqApplName().equals(lblReqAppl.getText())
			   && data.getReqKindName().equals(lblReqKind.getText())
			   && data.getReqTxName().equals(lblReqTx.getText())
			   && data.getResApplName().equals(lblResAppl.getText())
			   && data.getResKindName().equals(lblResKind.getText())
			   && data.getResTxName().equals(lblResTx.getText())
			) 
			{
				data.setPontNo( txtResPortNo.getText());
				myPaneTxTable.updateUI();
				return ;
			}
        }
 
    	permitroot.add(new DefaultMutableTreeNode(new myResMapComm(
    			lblReqAppl.getText().trim(),
    			lblReqKind.getText().trim(),
    			lblReqTx.getText().trim(),
    			lblResAppl.getText().trim(),
    		    lblResKind.getText().trim(),
    			lblResTx.getText().trim(),
    		    txtResPortNo.getText(), 
                false)));
       myPaneTxTable.setTreeTableModel(new myResMapModel(permitroot));
       myPaneTxTable.updateUI();
    }
    
    private void myPaneTableDelRow()
    {
    	//리스트에 동일한 row가 있으면, 포트번호만 Update 한다.
        for(int i=0;i < permitroot.getChildCount();i++){
        	DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) permitroot.getChildAt(i);  
        	myResMapComm data = (myResMapComm) dataNode.getUserObject();  
			
			if (data.getReqApplName().equals(lblReqAppl.getText())
			   && data.getReqKindName().equals(lblReqKind.getText())
			   && data.getReqTxName().equals(lblReqTx.getText())
			   && data.getResApplName().equals(lblResAppl.getText())
			   && data.getResKindName().equals(lblResKind.getText())
			   && data.getResTxName().equals(lblResTx.getText())
			) 
			{
				permitroot.remove(dataNode);
				myPaneTxTable.updateUI();
 
				return ;
			}
        }
 
    }
}
