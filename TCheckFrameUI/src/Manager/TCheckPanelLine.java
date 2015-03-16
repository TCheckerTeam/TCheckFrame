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
 
    	//ȸ������ ȭ�鿡 ���� �ʱ� Component ����
    	mypanel.setLayout(new BorderLayout());
        
    	//���� Command Button ����
    	JPanel myPaneLineSub1  = new JPanel(); myPaneLineSub1.setBackground(Color.WHITE);
    	JButton btnRefresh, btnSave;
    	btnRefresh = new JButton("�����ϱ�",new ImageIcon("./Image/refresh.gif")); 
    	btnSave = new JButton("�����ϱ�", new ImageIcon("./Image/save.gif"));
    	
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
 
	 
 
		 //�÷������� ����
		 final int[] columnsWidth = {80, 180, 80, 140, 80, 80, 160, 80, 80, 80};
		 for(int i=0; i < columnsWidth.length;i++){
			 myPaneLineTable.getColumnModel().getColumn(i).setPreferredWidth(columnsWidth[i]);
		 }
 
         JScrollPane myPaneLineSub2 = new JScrollPane(myPaneLineTable);
         mypanel.add(myPaneLineSub2, BorderLayout.CENTER);
         myPaneLineSub2.setAutoscrolls(true);
  
         
         // Table Row Data ����
         myPaneLineRefresh();
    }
    public void myPaneLineRefresh()
    {
 
   	    if (permitroot == null) return;
    	permitroot.removeAllChildren();
    	
    	//������ Kind/Tx�� ���� Offset/Size�� �о�´�.
    	String OffsetSizeData = GetKindTx_OffsetSize();
    	String[] arrOffsetSizeData = null;
    	if (OffsetSizeData != null) {
    		arrOffsetSizeData = OffsetSizeData.split("\n");
    	}
    	
    	//TCHECKER_LINEINFO ��ü Read
    	String TcheckerLineInfoData = GetTcheckerLineInfoData();
    	String[] arrTcheckerLineInfoData = null;
    	if (TcheckerLineInfoData != null) {
    		arrTcheckerLineInfoData = TcheckerLineInfoData.split("\n");
 
    	}
 
    	//������ ȸ�������� �о�´�.
    	String RData = GetLineInfo();
    	
    	System.out.println("ȸ������ : ["+RData+"]");
    	
    	String[] arrRData = null;
    	if (RData != null) {
            arrRData = RData.split("\n");
     	}
    	
    	// �ű� RowData�� �����Ѵ�.
    	if (RData != null) {
    		String[] gridData = new String[10];
    		
            arrRData = RData.split("\n");
            for(int i=0;i < arrRData.length;i++){
            	if (arrRData[i].trim().equals("") || arrRData[i] == null) break;
                String[] rowData = arrRData[i].split("\t");
 
        		
                gridData[0] = rowData[0];  //�����ڵ�
                gridData[1] = rowData[1];  //������
                gridData[2] = rowData[4];  //Port No
                
                /*------------ ���� ��� ----------------------*/
                if ( rowData[5].equals("1"))  gridData[3] = rowData[5] + ":" + "Server";
	    	    if ( rowData[5].equals("2"))  gridData[3] = rowData[5] + ":" + "Client";
	    	    if ( rowData[5].equals("3"))  gridData[3] = rowData[5] + ":" + "Client(����)";
	    	    if ( rowData[5].equals("7"))  gridData[3] = rowData[5] + ":" + "Client(MegaBox EOR)";
	    	    if ( rowData[5].equals("10")) gridData[3] = rowData[5] + ":" + "Server(MegaBox EOR)";
		    	
	    	    /*------------- ���Ÿ�� -------------------*/
	    	    if ( rowData[6].equals("0")) gridData[4] = rowData[6] + ":" + "������";
		    	if ( rowData[6].equals("1")) gridData[4] = rowData[6] + ":" + "�������";
		    	if ( rowData[6].equals("2")) gridData[4] = rowData[6] + ":" + "��������";
		    	if ( rowData[6].equals("3")) gridData[4] = rowData[6] + ":" + "END����";
		    	
		    	gridData[5] = rowData[7];  //���ũ��
	 
		    	/*------------- ���̼������ -------------------*/
		    	gridData[6] = "00:�����ʿ�";
		    	gridData[7] = "0";  //���� Offset
    	    	gridData[8] = "0";  //���� Size
    	    	gridData[9] = "2:�̻��";

		    	
		    	for(int j=0;j < arrTcheckerLineInfoData.length;j++){
		    		if (arrTcheckerLineInfoData[j].trim().equals("") || arrTcheckerLineInfoData[j] == null) break;
		    		String[] arrtmp = arrTcheckerLineInfoData[j].split("\t");
		    		if (arrtmp[0].equals(rowData[0]) && arrtmp[1].equals(rowData[4]) && arrtmp[2].equals(rowData[5])) {
		    	    	/*------------- ���̼������ -------------------*/
		    	    	if ( arrtmp[6].equals("0") || arrtmp[6].equals(""))  {
		    	    		gridData[6] = "00:�����ʿ�";
		    	    		if ( rowData[6].equals("0")) gridData[6] = "10:�����ʵ� ������ ����";
		    	    		if ( rowData[6].equals("1")) gridData[6] = "11:�����ʵ带 ������ ����";
		    	    		if ( rowData[6].equals("2")) gridData[6] = "13:��������";
		    	    		if ( rowData[6].equals("3")) gridData[6] = "14:END����";
		    	    	}
		    	    	if ( arrtmp[6].equals("10")) gridData[6] = "10:�����ʵ� ������ ����";
		    	    	if ( arrtmp[6].equals("11")) gridData[6] = "11:�����ʵ带 ������ ����";
		    	    	if ( arrtmp[6].equals("12")) gridData[6] = "12:Body�� ����";
		    	    	if ( arrtmp[6].equals("13")) gridData[6] = "13:��������";
		    	    	if ( arrtmp[6].equals("14")) gridData[6] = "14:END����";
		    	    	if ( arrtmp[6].equals("50")) gridData[6] = "50:�����ʵ� ������ ����(Integer)";
		    	    	if ( arrtmp[6].equals("51")) gridData[6] = "51:�����ʵ带 ������ ����(Integer)";
		    	    	if ( arrtmp[6].equals("52")) gridData[6] = "52:Body�� ����(Integer)";
		    	    	if ( arrtmp[6].equals("99")) gridData[6] = "99:���������";
		    	    	
		    	    	gridData[7] = arrtmp[7];  //���� Offset
		    	    	gridData[8] = arrtmp[8];  //���� Size
		    	    	if (arrtmp[3].equals("1")){
		    	    		gridData[9] = "1:���";
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
    	 
		//�÷������� ����
		final int[] columnsWidth = {80, 180, 80, 140, 80, 80, 160, 80, 80, 80 };
		for(int i=0; i < columnsWidth.length;i++){
		    myPaneLineTable.getColumnModel().getColumn(i).setPreferredWidth(columnsWidth[i]);
		}
 
		for(int i=0;i < columnsWidth.length ;i++) {
			
			if (i==6) {
 
				//���� Type�� ���� Combo ����
				JComboBox comblentype = new JComboBox();
			 
				comblentype.addItem("00:�����ʿ�");
				comblentype.addItem("10:�����ʵ� ������ ����");
				comblentype.addItem("11:�����ʵ� ������ ����");
				comblentype.addItem("12:Body�� ����");
				comblentype.addItem("13:��������");
				comblentype.addItem("14:END����");
				comblentype.addItem("50:�����ʵ� ������ ����(Int)");
				comblentype.addItem("51:�����ʵ� ������ ����(Int)");
				comblentype.addItem("52:Body�� ����(Int)");
				comblentype.addItem("99:���������");
				
		         myPaneLineTable.getColumnModel().getColumn(i).setCellEditor(new myComboxModel(comblentype));
			}
			if (i==9) {
				//��뿩�� ���� Combo ����
				JComboBox combuserflag = new JComboBox();
				combuserflag.addItem("1:���");
				combuserflag.addItem("2:�̻��");
		 
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
        		JOptionPane.showMessageDialog(null,"ȸ�������� �������� ���߽��ϴ�.");
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
	    		JOptionPane.showMessageDialog(null,"Kind/Tx�� ���� Offset/Size�� �������� ���߽��ϴ�.");
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
    		JOptionPane.showMessageDialog(null,"ȸ�������� �������� ���߽��ϴ�.");
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
            
            System.out.println("Communication:" + SendStr);
            
 
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
        	// Clinet �� Server �� ȸ���� ���� �з��Ѵ�.
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
        		//client �� �������� ������ server port�� �ִ��� �����Ѵ�.
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

    		
    		//������ ��Ʈ�� ���Ͽ� ȭ�鿡 ǥ���Ѵ�.
    		if (result.length() > 10)
    		{
    			String msg = "Anylink �� Simulator(TChecker Frame)�� ������ �ý��ۿ� ������ ��,\n";
    			msg = msg + "Client/Server�� ���Ͽ� ������ ��Ʈ�� ����� �� �����ϴ�. \n";
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
 * ByteBuffer bb1 = ByteBuffer.allocate(256);       //�Ϲ� ���� ����
 * ByteBuffer bb1 = ByteBuffer.allocateDirect(256); //Ŀ�ι��ۿ� ���� �����ϴ� ���ۻ���
 * position : ���� �б⳪ ���⸦ �� ��ġ�� ����Ų��. �б⳪ ���Ⱑ ����ɶ� position ���� �ڵ� �̵��Ѵ�.
 * limit    : ���� ByteBuffer�� �б�/���⸦ �� �� �ִ� ��ġ�� �Ѱ谪
 * capacity : ByteBuffer�� �뷮(�׻� ������ �������� ����Ŵ)
 * mark     : ����ڰ� ������ ��ġ
 * 
 *
 * rewind() : ������ �����͸� �ٽ� �б� ���� positoino ���� 0���� �����Ѵ�.
 *            ������ ����X, position = 0, mark = ����
 * flip()   : Buffer�� �����忡�� �б� ���� ����Ī�Ѵ�, 
 *            limit = position, postion = 0, mark = ����
 * clear()  : positon�� 0���� �����ϰ�, limit���� capacity�� �����Ѵ�.
 *            ������ ����X, position = 0, mark = ����, limit = capacity
 * reset()  : position ���� mark�� �ǵ�����.
 *            ������ ����X, position = mark, (��, mark < position �϶��� ���� �� �ܿ� �����߻�)
 * remaining(): (limit - position) �� ����
 * position() : ���� position �� ����
 * position(int pos) : position ����
 * limit()    : ���� limit �� ����
 * mark()     : ���� position �� mark �� ����
 * compact()  : ���� position ���� limit�� ���̿� �ִ� ����Ÿ�� buffer�� ���� ������ �̵���Ű��,
 *              position�� �������� ������ �κ��� ����Ű��, limit = capacity, mark = ����
 *              (������ �̵���Ű�� ���� �ںκ� �����͵��� 0���� �ʱ�ȭ���� �ʱ� ������ ������ �����Ͱ� ��������)
 *              
 *
*/