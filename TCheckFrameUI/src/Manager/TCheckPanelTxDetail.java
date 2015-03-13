package Manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
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
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.JXTreeTable;

import Manager.DataModel.myComboxModel;
import Manager.DataModel.myTxDetailComm;
import Manager.DataModel.myTxDetailModel;

 
public class TCheckPanelTxDetail { 
	private JPanel mypanel = null;
	private JSplitPane splitPane, splitPane1;
	private JXTreeTable myPaneTxTable;
	private String SelectedApplcode = "";
	private JComboBox combinoutflagall,combmapflagall,comlogflagall;
	private DefaultMutableTreeNode permitroot = null;
	private JPopupMenu popuptree  = new JPopupMenu();
	private JPopupMenu popuptable = new JPopupMenu();
	private String[] ApplKindTxOffsetSizeInfo = null;
 
	public TCheckPanelTxDetail(JPanel panel)
	{
		mypanel = panel;
		myPaneTxDetailInit();
	}
	public void myPaneTxDetailInit()
    {
		mypanel.setLayout(new BorderLayout());
		
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT); 
        splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT); 
 
        splitPane1.setContinuousLayout(true); //�������� ���̾ƿ� ��� Ȱ��ȭ
        splitPane1.setRightComponent(Init_Table());
        splitPane1.setLeftComponent(Init_Button());
        splitPane1.setDividerLocation(40);
        splitPane1.setDividerSize(5); //����̴�(�и���) ���� ����
        splitPane1.setBackground(Color.BLACK);
 
        //JSplitPane ����
        splitPane.setContinuousLayout(true); //�������� ���̾ƿ� ��� Ȱ��ȭ
        splitPane.setLeftComponent(new JScrollPane(Init_Tree())); //���� ������Ʈ ����
        splitPane.setRightComponent(splitPane1); //���� ������Ʈ ����
        splitPane.setDividerLocation(300); //����̴�(�и���) ��ġ ����      
        splitPane.setDividerSize(6); //����̴�(�и���) ���� ����
        splitPane.setBackground(Color.BLACK);
        
        JScrollPane myPaneSub2 = new JScrollPane(splitPane);
        mypanel.add(myPaneSub2, BorderLayout.CENTER);
        myPaneSub2.setAutoscrolls(true);
        
        GetApplKindTxOffsetSizeInfo();
 
    }
	private JTree Init_Tree()
	{
	    JTree  xTree;
		DefaultMutableTreeNode root, node;

        root = new DefaultMutableTreeNode("��������");
        String applinfo = GetApplInfo();
        
        System.out.println(" �������� �ε� : ["+applinfo+"]");
        
        String[] arrtmp = applinfo.split("\n");
        for(int i=0;i < arrtmp.length ;i++){
        	if (arrtmp[i].trim().equals("")) break;
        	String[] arrtmpsub = arrtmp[i].split("\t");
        	node = new DefaultMutableTreeNode(arrtmpsub[0] + " - " + arrtmpsub[1]);
        	root.add(node);
        }
        
        xTree = new JTree(root);
        xTree.addTreeSelectionListener( new TreeSelectionListener()
                {
                    public void valueChanged(TreeSelectionEvent e) {
                            DefaultMutableTreeNode n = (DefaultMutableTreeNode)(e.getPath().getLastPathComponent());
                            String itemstr = (String)n.getUserObject();
                            SelectedApplcode = itemstr.split("-")[0].trim();
                            myPaneTableRefresh(SelectedApplcode);
                    }
                }
        );
        xTree.expandRow(1);
         
        return xTree;
	}
	private JPanel Init_Button()
	{
    	//���� Command Button ����
    	JPanel myPaneSub1  = new JPanel(); myPaneSub1.setBackground(Color.WHITE);
    	JButton  btnSave, btnRefresh, btnExternal, btnInternal;

    	
    	myPaneSub1.setLayout(new FlowLayout());
 
    	
    	btnRefresh = new JButton("�����ϱ�",new ImageIcon("./Image/refresh.gif")); 
    	myPaneSub1.add(btnRefresh);
    	mypanel.add(myPaneSub1,BorderLayout.NORTH);
    	btnRefresh.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				Init_Tree();
    		}
       	});
    	
    	
    	btnSave = new JButton("�����ϱ�", new ImageIcon("./Image/save.gif"));
    	myPaneSub1.add(btnSave);
   
    	mypanel.add(myPaneSub1,BorderLayout.NORTH);
    	btnSave.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				String savedata = "";
				 
				for(int i=0;i < permitroot.getChildCount();i++)
				{
					  DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) permitroot.getChildAt(i);  
					  myTxDetailComm data = (myTxDetailComm) dataNode.getUserObject();  
		 
		  
			          String tmpstr = SelectedApplcode + "\t";
			          tmpstr = tmpstr + data.getKindCode()               + "\t"; 
			          tmpstr = tmpstr + data.getKindName()               + "\t";
			          tmpstr = tmpstr + data.getTxCode()                 + "\t";
			          tmpstr = tmpstr + data.getTxName()                 + "\t";
			          tmpstr = tmpstr + data.getTxGubun ().split(":")[0] + "\t";
			          tmpstr = tmpstr + data.getMapGubun().split(":")[0] + "\t";
			          
			          //key1
			          if (data.getKeyOffset1().trim().equals("")) tmpstr = tmpstr + "0\t";
			          else tmpstr = tmpstr + data.getKeyOffset1().trim() + "\t";
			          
			          if (data.getKeyLen1().trim().equals("")) tmpstr = tmpstr + "0\t";
			          else tmpstr = tmpstr + data.getKeyLen1().trim()       + "\t";
			          
			          if (data.getKeyVal1().trim().equals("")) tmpstr = tmpstr + "<NODATA>\t";
			          else tmpstr = tmpstr + data.getKeyVal1().trim() + "\t";

			          //key2
			          if (data.getKeyOffset2().trim().equals("")) tmpstr = tmpstr + "0\t";
			          else tmpstr = tmpstr + data.getKeyOffset2().trim() + "\t";
			          
			          if (data.getKeyLen2().trim().equals("")) tmpstr = tmpstr + "0\t";
			          else tmpstr = tmpstr + data.getKeyLen2().trim()       + "\t";
	 
			          if (data.getKeyVal2().trim().equals("")) tmpstr = tmpstr + "<NODATA>\t";
			          else tmpstr = tmpstr + data.getKeyVal2().trim() + "\t";
			          
			          //key3
			          if (data.getKeyOffset3().trim().equals("")) tmpstr = tmpstr + "0\t";
			          else tmpstr = tmpstr + data.getKeyOffset3().trim() + "\t";
			          
			          if (data.getKeyLen3().trim().equals("")) tmpstr = tmpstr + "0\t";
			          else tmpstr = tmpstr + data.getKeyLen3().trim()       + "\t";
			          
			          if (data.getKeyVal3().trim().equals("")) tmpstr = tmpstr + "<NODATA>\n";
			          else tmpstr = tmpstr + data.getKeyVal3().trim() + "\n";
			          
					  savedata = savedata + tmpstr ;
				}
		 
				SaveTcheckertxdetailInfo(savedata);
				

    		}
       	});
    
    	btnExternal = new JButton("Ű����-External �⺻����",new ImageIcon("./Image/refresh.gif")); 
    	myPaneSub1.add(btnExternal);
    	mypanel.add(myPaneSub1,BorderLayout.NORTH);
    	btnExternal.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				 String KindOffset = "";
				 String KindLen    = "";
				 String TxOffset   = "";
				 String TxLen      = "";
				 
				 for(int i=0;i < ApplKindTxOffsetSizeInfo.length ;i++){
					 String[] arrtmp = ApplKindTxOffsetSizeInfo[i].split("\t");
					 if (arrtmp[0].equals(SelectedApplcode)){
						 KindOffset = arrtmp[1];
						 KindLen    = arrtmp[2];
						 TxOffset   = arrtmp[3];
						 TxLen      = arrtmp[4];
						 break;
					 }
				 }
				for(int i=0;i < permitroot.getChildCount();i++)
				{
				    DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) permitroot.getChildAt(i);  
				    myTxDetailComm data = (myTxDetailComm) dataNode.getUserObject();  
				    data.setKeyOffset1(KindOffset);
				    data.setKeyLen1(KindLen);
				    data.setKeyOffset2(TxOffset);
				    data.setKeyLen2(TxLen);
				    
				    String imsi = data.getTxGubun();
				    if (data.getTxGubun().indexOf("I:") >=0 ) {
				    	data.setKeyVal1(data.getResKindCode());	
				    }
				    else {
				    	data.setKeyVal1(data.getKindCode());	
				    }
				    
				    data.setKeyVal2(data.getTxCode());
				}
				myPaneTxTable.updateUI();
    		}
       	});
    	
    	btnInternal = new JButton("Ű����-Internal �⺻����",new ImageIcon("./Image/refresh.gif")); 
    	myPaneSub1.add(btnInternal);
    	mypanel.add(myPaneSub1,BorderLayout.NORTH);
    	btnInternal.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				String InternalInfo = GetInternalInfo(SelectedApplcode);
				String[] arrInternalInfo = InternalInfo.split("\n");
				for(int k=0;k < arrInternalInfo.length ;k++){
					if (arrInternalInfo[k].trim().equals("")) break;
					String[] arrtmp = arrInternalInfo[k].split("\t");
					
					for(int i=0;i < permitroot.getChildCount();i++)
					{
					    DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) permitroot.getChildAt(i);  
					    myTxDetailComm data = (myTxDetailComm) dataNode.getUserObject();  
					    
					    if (data.getKindCode().equals(arrtmp[1]) && data.getTxCode().equals(arrtmp[2])) {
						    data.setKeyOffset1(arrtmp[3].replace("-1", "0"));
						    data.setKeyLen1(arrtmp[4].replace("-1", "0"));
						    data.setKeyVal1(arrtmp[5].replace("<NODATA>", ""));
						    data.setKeyOffset2(arrtmp[6].replace("-1", "0"));
						    data.setKeyLen2(arrtmp[7].replace("-1", "0"));
						    data.setKeyVal2(arrtmp[8].replace("<NODATA>", ""));
						    data.setKeyOffset3(arrtmp[9].replace("-1", "0"));
						    data.setKeyLen3(arrtmp[10].replace("-1", "0"));
						    data.setKeyVal3(arrtmp[11].replace("<NODATA>", ""));
						    break;
					    }
					}
				}
				
				
    		}
       	});
    	
    	return myPaneSub1;
	}
	public void actionPerformed (ActionEvent e) {
		
	}
	private JScrollPane Init_Table()
	{
   	     // Table Component Setting 
		 DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new myTxDetailComm("Root", "", "", "","","", "","","", "","","", "","","","",true));  

		 permitroot = rootNode;
		 myPaneTxTable = new JXTreeTable();
		 myPaneTxTable.setTreeTableModel(new myTxDetailModel(rootNode));  
		 myPaneTxTable.setEditable(true);
		 myPaneTxTable.setRootVisible(false);
		 myPaneTxTable.setCellSelectionEnabled(true);

		 //�÷������� ����
		 final int[] columnsWidth = {80, 200, 50, 200, 150, 100,50,50,100,50,50,100,50,50,100,0};
		 for(int i=0; i < columnsWidth.length;i++){
			 myPaneTxTable.getColumnModel().getColumn(i).setPreferredWidth(columnsWidth[i]);
		 }
 
        JScrollPane myPaneSub2 = new JScrollPane(myPaneTxTable);
        myPaneSub2.setAutoscrolls(true);
        
    	//Request PopupMenu ����
        JMenuItem mtxgubun = new JMenuItem("�ŷ����� ����");
        JMenuItem mmapping = new JMenuItem("���α��� ����");
        JMenuItem mkeyoff1 = new JMenuItem("Ű�ɼ�1 ����");
        JMenuItem mkeylen1 = new JMenuItem("Ű����1 ����");
        JMenuItem mkeyval1 = new JMenuItem("�񱳰�1 ����");
        JMenuItem mkeyoff2 = new JMenuItem("Ű�ɼ�2 ����");
        JMenuItem mkeylen2 = new JMenuItem("Ű����2 ����");
        JMenuItem mkeyval2 = new JMenuItem("�񱳰�2 ����");
        JMenuItem mkeyoff3 = new JMenuItem("Ű�ɼ�3 ����");
        JMenuItem mkeylen3 = new JMenuItem("Ű����3 ����");
        JMenuItem mkeyval3 = new JMenuItem("�񱳰�3 ����");
        
        JMenuItem mtxupdat = new JMenuItem("�ŷ��ڵ� �ݿ�");

    	popuptable.add(mtxgubun);
    	popuptable.addSeparator();
    	popuptable.add(mmapping);
    	popuptable.addSeparator();
    	popuptable.add(mkeyoff1);
    	popuptable.add(mkeylen1);
    	popuptable.add(mkeyval1);
    	popuptable.addSeparator();
    	popuptable.add(mkeyoff2);
    	popuptable.add(mkeylen2);
    	popuptable.add(mkeyval2);
    	popuptable.addSeparator();
    	popuptable.add(mkeyoff3);
    	popuptable.add(mkeylen3);
    	popuptable.add(mkeyval3);
 
    	popuptable.addSeparator();
    	popuptable.add(mtxupdat);
    	
    	myPaneTxTable.addMouseListener(new MouseAdapter(){
    		public void mouseClicked(MouseEvent e){
    			if (e.getButton() == 3) {
    				popuptable.show(myPaneTxTable, e.getX(), e.getY());
    			}
    		}
    	});
    	
    	mtxgubun.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent ae){
	    		String subtitle = "   �Ʒ� �׸��� �����Ͽ� ������(N,I,O)�� �Է��ϼ���.\n\n";
	    		subtitle = subtitle + "    N : �����ʿ�\n";
	    		subtitle = subtitle + "    I : Simulator���� ��û�ŷ�\n";
	    		subtitle = subtitle + "    O : Anylink���� ��û�ŷ�\n";
 
				DataInsertDialog datains = new DataInsertDialog(200,200,400,240,"�ŷ�����", subtitle);
				
				if (datains.getInData().equals("")) return;
 
				String indata = datains.getInData().toUpperCase();
				for(int i=0;i < myPaneTxTable.getSelectedRows().length;i++)
				{
				    DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) permitroot.getChildAt(myPaneTxTable.getSelectedRows()[i]);  
				    myTxDetailComm data = (myTxDetailComm) dataNode.getUserObject();  
					if (indata.substring(0,1).equals("I")) data.setTxGubun("I:Simulator���� ��û�ŷ�");
					if (indata.substring(0,1).equals("O")) data.setTxGubun("O:Anylink���� ��û�ŷ�");
					if (indata.substring(0,1).equals("N")) data.setTxGubun("N:�����ʿ�");
				}
				myPaneTxTable.updateUI();
				
    		}
		});
    	mmapping.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent ae){
	    		String subtitle = "   �Ʒ� �׸��� �����Ͽ� ������(B,U,P,N,H)�� �Է��ϼ���.\n\n";
	    		subtitle = subtitle + "    B : �Ϲݸ���          \n";
	    		subtitle = subtitle + "    U : ����ڸ���        \n";
	    		subtitle = subtitle + "    P : Body�� ByPass���� \n";
	    		subtitle = subtitle + "    N : ����������        \n";
	    		subtitle = subtitle + "    H : ������� ���ܸ��� \n";
 
				DataInsertDialog datains = new DataInsertDialog(200,200,400,240,"���α���", subtitle);
				
				if (datains.getInData().equals("")) return;
 
				String indata = datains.getInData().toUpperCase();
				for(int i=0;i < myPaneTxTable.getSelectedRows().length;i++)
				{
				    DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) permitroot.getChildAt(myPaneTxTable.getSelectedRows()[i]);  
				    myTxDetailComm data = (myTxDetailComm) dataNode.getUserObject();  
					if (indata.substring(0,1).equals("B")) data.setMapGubun("B:�Ϲݸ���");
					if (indata.substring(0,1).equals("U")) data.setMapGubun("U:����ڸ���");
					if (indata.substring(0,1).equals("P")) data.setMapGubun("P:Body�� ByPass����");
					if (indata.substring(0,1).equals("N")) data.setMapGubun("N:����������");
					if (indata.substring(0,1).equals("H")) data.setMapGubun("H:������� ���ܸ���");
				}
				myPaneTxTable.updateUI();
    		}
		});
    	
    	mkeyoff1.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent ae){
	    		String subtitle = "   Ű�ɼ��� �Է��ϼ���.\n\n";
				DataInsertDialog datains = new DataInsertDialog(200,200,400,240,"Ű�ɼ� 1", subtitle);
				
				if (datains.getInData().equals("")) return;
 
				String indata = datains.getInData();
				for(int i=0;i < myPaneTxTable.getSelectedRows().length;i++)
				{
				    DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) permitroot.getChildAt(myPaneTxTable.getSelectedRows()[i]);  
				    myTxDetailComm data = (myTxDetailComm) dataNode.getUserObject();  
					data.setKeyOffset1(indata);
				}
				myPaneTxTable.updateUI();
    		}
		});
    	
    	mkeylen1.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent ae){
	    		String subtitle = "   Ű���̸�  �Է��ϼ���.\n\n";
				DataInsertDialog datains = new DataInsertDialog(200,200,400,240,"Ű���� 1", subtitle);
				
				if (datains.getInData().equals("")) return;
 
				String indata = datains.getInData();
				for(int i=0;i < myPaneTxTable.getSelectedRows().length;i++)
				{
				    DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) permitroot.getChildAt(myPaneTxTable.getSelectedRows()[i]);  
				    myTxDetailComm data = (myTxDetailComm) dataNode.getUserObject();  
					data.setKeyLen1(indata);
				}
				myPaneTxTable.updateUI();
    		}
		});
 
    	mkeyval1.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent ae){
	    		String subtitle = "   �񱳰���  �Է��ϼ���.\n\n";
				DataInsertDialog datains = new DataInsertDialog(200,200,400,240,"�񱳰� 1", subtitle);
				
				if (datains.getInData().equals("")) return;
 
				String indata = datains.getInData();
				for(int i=0;i < myPaneTxTable.getSelectedRows().length;i++)
				{
				    DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) permitroot.getChildAt(myPaneTxTable.getSelectedRows()[i]);  
				    myTxDetailComm data = (myTxDetailComm) dataNode.getUserObject();  
					data.setKeyVal1(indata);
				}
				myPaneTxTable.updateUI();
    		}
		});
    	mkeyoff2.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent ae){
	    		String subtitle = "   Ű�ɼ��� �Է��ϼ���.\n\n";
				DataInsertDialog datains = new DataInsertDialog(200,200,400,240,"Ű�ɼ� 2", subtitle);
				
				if (datains.getInData().equals("")) return;
 
				String indata = datains.getInData();
				for(int i=0;i < myPaneTxTable.getSelectedRows().length;i++)
				{
				    DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) permitroot.getChildAt(myPaneTxTable.getSelectedRows()[i]);  
				    myTxDetailComm data = (myTxDetailComm) dataNode.getUserObject();  
					data.setKeyOffset2(indata);
				}
				myPaneTxTable.updateUI();
    		}
		});
    	
    	mkeylen2.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent ae){
	    		String subtitle = "   Ű���̸�  �Է��ϼ���.\n\n";
				DataInsertDialog datains = new DataInsertDialog(200,200,400,240,"Ű���� 2", subtitle);
				
				if (datains.getInData().equals("")) return;
 
				String indata = datains.getInData();
				for(int i=0;i < myPaneTxTable.getSelectedRows().length;i++)
				{
				    DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) permitroot.getChildAt(myPaneTxTable.getSelectedRows()[i]);  
				    myTxDetailComm data = (myTxDetailComm) dataNode.getUserObject();  
					data.setKeyLen2(indata);
				}
				myPaneTxTable.updateUI();
    		}
		});
    	mkeyval2.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent ae){
	    		String subtitle = "   �񱳰���  �Է��ϼ���.\n\n";
				DataInsertDialog datains = new DataInsertDialog(200,200,400,240,"�񱳰� 2", subtitle);
				
				if (datains.getInData().equals("")) return;
 
				String indata = datains.getInData();
				for(int i=0;i < myPaneTxTable.getSelectedRows().length;i++)
				{
				    DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) permitroot.getChildAt(myPaneTxTable.getSelectedRows()[i]);  
				    myTxDetailComm data = (myTxDetailComm) dataNode.getUserObject();  
					data.setKeyVal2(indata);
				}
				myPaneTxTable.updateUI();
    		}
		});
    	mkeyoff3.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent ae){
	    		String subtitle = "   Ű�ɼ��� �Է��ϼ���.\n\n";
				DataInsertDialog datains = new DataInsertDialog(200,200,400,240,"Ű�ɼ� 3", subtitle);
				
				if (datains.getInData().equals("")) return;
 
				String indata = datains.getInData();
				for(int i=0;i < myPaneTxTable.getSelectedRows().length;i++)
				{
				    DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) permitroot.getChildAt(myPaneTxTable.getSelectedRows()[i]);  
				    myTxDetailComm data = (myTxDetailComm) dataNode.getUserObject();  
					data.setKeyOffset3(indata);
				}
				myPaneTxTable.updateUI();
    		}
		});
    	
    	mkeylen3.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent ae){
	    		String subtitle = "   Ű���̸�  �Է��ϼ���.\n\n";
				DataInsertDialog datains = new DataInsertDialog(200,200,400,240,"Ű���� 3", subtitle);
				
				if (datains.getInData().equals("")) return;
 
				String indata = datains.getInData();
				for(int i=0;i < myPaneTxTable.getSelectedRows().length;i++)
				{
				    DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) permitroot.getChildAt(myPaneTxTable.getSelectedRows()[i]);  
				    myTxDetailComm data = (myTxDetailComm) dataNode.getUserObject();  
					data.setKeyLen3(indata);
				}
				myPaneTxTable.updateUI();
    		}
		});
    	
    	mkeyval3.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent ae){
	    		String subtitle = "   �񱳰���  �Է��ϼ���.\n\n";
				DataInsertDialog datains = new DataInsertDialog(200,200,400,240,"�񱳰� 3", subtitle);
				
				if (datains.getInData().equals("")) return;
 
				String indata = datains.getInData();
				for(int i=0;i < myPaneTxTable.getSelectedRows().length;i++)
				{
				    DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) permitroot.getChildAt(myPaneTxTable.getSelectedRows()[i]);  
				    myTxDetailComm data = (myTxDetailComm) dataNode.getUserObject();  
					data.setKeyVal3(indata);
				}
				myPaneTxTable.updateUI();
    		}
		});
    	
    	mtxupdat.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent ae){
	    		String subtitle = "   Ű��2�� �ŷ��ڵ带 �ٿ��ֱ� �ϰڽ��ϱ�?(Y/N).\n\n";
				DataInsertDialog datains = new DataInsertDialog(200,200,400,240,"Ű��2�� �ŷ��ڵ� �ٿ��ֱ�", subtitle);
				
				if (datains.getInData().equals("")) return;
 
				String indata = datains.getInData().toUpperCase();
				for(int i=0;i < myPaneTxTable.getSelectedRows().length;i++)
				{
				    DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) permitroot.getChildAt(myPaneTxTable.getSelectedRows()[i]);  
				    myTxDetailComm data = (myTxDetailComm) dataNode.getUserObject();  
					data.setKeyVal2(data.getTxCode());
				}
				myPaneTxTable.updateUI();
    		}
		});
    	
        return myPaneSub2;
	}
    private String GetApplInfo()
    {
    	try {
    		String RetData = Communication("READ_APPLINFO", "NODATA");
    		
    		System.out.println("�������� GetApplInfo : ["+RetData+"]");
    		
        	if (RetData.trim().equals("") || RetData.trim().equals("NOT-FOUND")) {
        		JOptionPane.showMessageDialog(null,"���������� �������� ���߽��ϴ�.");
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
    		
    		System.out.println("TxDetail : ["+RetData+"]");
    		
        	if (RetData.trim().equals("") || RetData.trim().equals("NOT-FOUND")) {
        		JOptionPane.showMessageDialog(null,"�����ڵ�[" + pApplCode + "]�� ���� ������ �������� ���߽��ϴ�.");
        		return "";
        	}
        	return RetData;
    	}catch(Exception e) { 
    		return "";
    	}
    	
    }
    private void GetApplKindTxOffsetSizeInfo()
    {
    	try {
    		ApplKindTxOffsetSizeInfo = null;
    		String RetData = Communication("READ_KINDTX_OFFSET_SIZE", "<NODATA>");
        	if (RetData.trim().equals("") || RetData.trim().equals("NOT-FOUND")) {
        		JOptionPane.showMessageDialog(null,"��� �����ڵ忡 ���� ����/�ŷ��ڵ� ������ �������� ���߽��ϴ�.");
        	}
        	
        	//applcode,kindoffset,kindlen,txoffset,txlen
        	ApplKindTxOffsetSizeInfo = RetData.split("\n");
    	}catch(Exception e) {}
    }
    private String GetInternalInfo(String pApplcode)
    {
    	try {
    		String RetData = Communication("READ_INTERNALINFO", pApplcode);
        	if (RetData.trim().equals("") || RetData.trim().equals("NOT-FOUND")) {
        		JOptionPane.showMessageDialog(null,"Internal ���������� �������� �ʽ��ϴ�.");
        		return "";
        	}
        	
        	//applcode,kindcode,txcode,offset/len/val, offset/len/val, offset/len/val
        	return RetData;
    	}catch(Exception e) {}
    	return "";
    }
    
    private void SaveTcheckertxdetailInfo(String savedata)
    {
    	String RetData = Communication("SAVE_TXDETAIL", savedata);
    	if (RetData.trim().equals("")) {
    		JOptionPane.showMessageDialog(null,"�ŷ� �������� �������� ���߽��ϴ�.");
    	}
    }
 
    public void myPaneTableRefresh(String pApplCode)
    {
   	    if (permitroot == null) return;
    	permitroot.removeAllChildren();
    	
    	String[] gridData = new String[16];
        String appltxinfo = GetApplTxInfo(pApplCode.trim());
        String[] arrtmp = appltxinfo.split("\n");
        for(int i=0;i < arrtmp.length ;i++){
        	if (arrtmp[i].trim().equals("")) break;
        	String[] arrtmpsub = arrtmp[i].split("\t");

            gridData[0] = arrtmpsub[0];  //�����ڵ�
            gridData[1] = arrtmpsub[1];  //�����ڵ��
            gridData[2] = arrtmpsub[2];  //�ŷ��ڵ�
            gridData[3] = arrtmpsub[3];  //�ŷ��ڵ��
            
            gridData[4] = "N:�����ʿ�";   //�ŷ�����
            if (arrtmpsub[15].equals("I")) gridData[4] = "I:Simulator���� ��û�ŷ�";
            if (arrtmpsub[15].equals("O")) gridData[4] = "O:Anylink����     ��û�ŷ�";
 
 
            gridData[5] = "B:�Ϲݸ���";  //���α���
            if (arrtmpsub[16].equals("B")) gridData[5] = "B:�Ϲݸ���";
            if (arrtmpsub[16].equals("U")) gridData[5] = "U:����ڸ���";
            if (arrtmpsub[16].equals("P")) gridData[5] = "P:Body�� ByPass����";
            if (arrtmpsub[16].equals("N")) gridData[5] = "N:����������";
            if (arrtmpsub[16].equals("H")) gridData[5] = "H:������� ���ܸ���";
            
            gridData[6]  = arrtmpsub[17];  //KeyOffset1
            gridData[7]  = arrtmpsub[18];  //KeyLen1
            gridData[8]  = arrtmpsub[19].replace("<NODATA>", "");  //KeyVal1
            gridData[9]  = arrtmpsub[20];  //KeyOffset2
            gridData[10] = arrtmpsub[21];  //KeyLen2
            gridData[11] = arrtmpsub[22].replace("<NODATA>", "");  //KeyVal2
            gridData[12] = arrtmpsub[23];  //KeyOffset3
            gridData[13] = arrtmpsub[24];  //KeyLen3
            gridData[14] = arrtmpsub[25].replace("<NODATA>", "");  //KeyVal3
            
            gridData[15] = arrtmpsub[11]; //���������ڵ�;
            
            permitroot.add(new DefaultMutableTreeNode(new myTxDetailComm(gridData[0] , gridData[1], 
                    gridData[2] , gridData[3], 
                    gridData[4] , gridData[5],
                    gridData[6] , gridData[7],
                    gridData[8] , gridData[9],
                    gridData[10] , gridData[11],
                    gridData[12] , gridData[13],
                    gridData[14] , gridData[15],
                    false)));
        }

        myPaneTxTable.setTreeTableModel(new myTxDetailModel(permitroot));
        
		//�÷������� ����
		final int[] columnsWidth = {80, 200, 50, 200, 150, 100,50,50,50,50,50,50,50,50,50,0};
		for(int i=0; i < columnsWidth.length;i++){
		    myPaneTxTable.getColumnModel().getColumn(i).setPreferredWidth(columnsWidth[i]);
		}
		 
		for(int i=0;i < columnsWidth.length ;i++) {
			
        	if (i==4) {
        		//�ŷ����п� ���� Combo ����
        		JComboBox combtxdetail = new JComboBox();
        		combtxdetail.addItem("I:Simulator���� ��û�ŷ�");
        		combtxdetail.addItem("O:Anylink����     ��û�ŷ�");
        
        		myPaneTxTable.getColumnModel().getColumn(i).setCellEditor(new myComboxModel(combtxdetail));
        	}
        	if (i==5) {
        		//���α��п� ���� Combo ����
        		JComboBox combamp = new JComboBox();
        		combamp.addItem("B:�Ϲݸ���");
        		combamp.addItem("U:����ڸ���");
        		combamp.addItem("P:Body�� ByPass����");
        		combamp.addItem("N:����������");
        		combamp.addItem("H:������� ���ܸ���");
	 
	        	myPaneTxTable.getColumnModel().getColumn(i).setCellEditor(new myComboxModel(combamp));
	        }
 
			if (i >= 4 ){
				DefaultTableCellRenderer  renderer1 = new DefaultTableCellRenderer();
				renderer1.setBackground(new Color(217,255,217));
				myPaneTxTable.getColumnModel().getColumn(i).setCellRenderer(renderer1);
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
      
    private String GetRegister(String pKey)
    {
    	Preferences userRootPrefs = Preferences.userRoot();
    	if (userRootPrefs.get(pKey, null) != null) { 
    		return userRootPrefs.get(pKey, "");
    	}
    	return "";
    }
}


/*
String msg = JOptionPane.showInputDialog("�޼����� �Է��ϼ���");
System.out.println(msg);
 
*/