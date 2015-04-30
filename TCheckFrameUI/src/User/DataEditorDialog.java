package User;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.JXTreeTable;

import User.DataModel.myDataEditorComm;
import User.DataModel.myDataEditorModel;
import User.DataModel.myTcpMapComm;
import User.DataModel.myTcpMapModel;
 
class DataEditorDialog extends JDialog implements ActionListener{
	private JButton btnOK, btnClose;
	private String  InData = "";
	private DefaultMutableTreeNode roottcpreq = null;
	private JXTreeTable myDataEditorTable = null;
	public DataEditorDialog(int x, int y, int width, int height, String title) {
		this.setTitle(title);
		this.setModal(true);
		this.setLayout(new BorderLayout());
 
        JScrollPane myPanetxtSend = new JScrollPane(Init_Table_Request());
        myPanetxtSend.setAutoscrolls(true);

        myPanetxtSend.setPreferredSize(new Dimension(270,200));
        this.add(myPanetxtSend, BorderLayout.CENTER );
        
        JPanel p1 = new JPanel();
 
        p1.add(btnOK    = new JButton ("Ȯ��")); 
        p1.add(btnClose = new JButton ("�ݱ�")); 
      
        this.add(p1, BorderLayout.SOUTH );

        
        btnOK.addActionListener(this);
        btnClose.addActionListener(this);
        this.setBounds(x, y, width, height);

        this.setVisible(true);
  
	}
	@Override
	public void actionPerformed(ActionEvent e){
		String cmd = e.getActionCommand();
    	
    	if (cmd.equals("Ȯ��")){
    		//InData = txtInData.getText();
    		dispose();
    	}
    	if (cmd.equals("�ݱ�")){
    		InData = "";
    		dispose();
    	}
	}
	public String getInData()
	{
		return InData;
	}
	private JScrollPane Init_Table_Request()
	{
   	     // Table Component Setting 
		 DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new myDataEditorComm("", "", "","","",true));  

		 roottcpreq = rootNode;
		 myDataEditorTable = new JXTreeTable();
		 myDataEditorTable.setTreeTableModel(new myDataEditorModel(rootNode));  
		 myDataEditorTable.setEditable(true);
		 myDataEditorTable.setRootVisible(false);
		 myDataEditorTable.setGridColor(Color.GRAY);
		 myDataEditorTable.setShowGrid(true, true);
		 myDataEditorTable.setAutoStartEditOnKeyStroke(true);
 
		 //�÷������� ����
		 SetColumnWidth(myDataEditorTable);
 
         JScrollPane myPaneSub2 = new JScrollPane(myDataEditorTable);
         myPaneSub2.setAutoscrolls(true);
 
         myPaneTableTcpRefresh(myDataEditorTable, roottcpreq, "");
         
        return myPaneSub2;
	}
	
    private void SetColumnWidth(JXTreeTable mytable)
    {
		//�÷������� ����
		final int[] columnsWidth = {5,200, 200, 50, 20};
		for(int i=0; i < columnsWidth.length;i++){
			
			mytable.getColumnModel().getColumn(i).setPreferredWidth(columnsWidth[i]);
			mytable.getColumnModel().getColumn(4).setResizable(false);
		    
		    if (i == 4 ){
				DefaultTableCellRenderer  renderer1 = new DefaultTableCellRenderer();
				renderer1.setBackground(new Color(217,255,217));
				mytable.getColumnModel().getColumn(i).setCellRenderer(renderer1);
			}
		}
 
        mytable.getColumn("No").setWidth(0);
        mytable.getColumn("No").setMinWidth(0);
        mytable.getColumn("No").setMaxWidth(0);
    
    }
    
    public void myPaneTableTcpRefresh(JXTreeTable mytable, DefaultMutableTreeNode mytree, String pMsgMapInfo)
    {
    	//Tree�� Null �̸�, �׳� �����Ѱ�, �׷��� ������ Tree�� ��� Child Node�� �����Ѵ�.
   	    if (mytree == null) return;
   	    mytree.removeAllChildren();
        
        //MsgMap ������ ���� TreeNode�� �����Ѵ�.
        try {
            for(int i=1;i <= 100 ;i++){
            	myDataEditorComm tmpclass = new myDataEditorComm(""+i , "a", "" , "", "",false);
                DefaultMutableTreeNode tmpnode = new DefaultMutableTreeNode(tmpclass);
                mytree.add(tmpnode);
            }
        }catch(Exception e) {
 
        }
 
        mytable.setTreeTableModel(new myDataEditorModel(mytree));
     
		//�÷������� ����
        SetColumnWidth(mytable);
        
        mytable.expandAll();  

    }
}