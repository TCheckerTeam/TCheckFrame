package Manager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
 
class DataMultiDialog extends JDialog implements ActionListener{
	JTextArea txtInData;
	private JButton btnLoad, btnOK, btnClose;
	private String  InData = "";
	private JComboBox typeFlag;
	private String appl_code;

 
	public DataMultiDialog(int x, int y, int width, int height, String title, String pAppl) {
		this.setTitle(title);
		this.setModal(true);
		this.setLayout(new BorderLayout());

        txtInData = new JTextArea();
        txtInData.setLineWrap(true); //한줄이 너무 길면 자동으로 개행할지 설정
        txtInData.setColumns(120); //열의 크기(가로크기)
        txtInData.setRows(1000); //행의 크기(세로크기)

        JScrollPane myPanetxtSend = new JScrollPane(txtInData);
        myPanetxtSend.setAutoscrolls(true);

        myPanetxtSend.setPreferredSize(new Dimension(270,200));
        this.add(myPanetxtSend, BorderLayout.CENTER );
        
        JPanel p1 = new JPanel();
        
        appl_code = pAppl;
		 
    /*
	  * JComboBox 추가
	  */
        
        p1.add(typeFlag = new JComboBox());
        typeFlag.addItem("all");
        typeFlag.addItem("req");
        typeFlag.addItem("res");

        
        p1.add(btnLoad  = new JButton ("가져오기"));
        p1.add(btnOK    = new JButton ("저장")); 
        p1.add(btnClose = new JButton ("닫기")); 
      
        this.add(p1, BorderLayout.SOUTH );

        btnLoad.addActionListener(this);
        btnOK.addActionListener(this);
        btnClose.addActionListener(this);
        this.setBounds(x, y, width, height);

        this.setVisible(true);
  
	}
	@Override
	public void actionPerformed(ActionEvent e){
		String cmd = e.getActionCommand();
    	
		if(cmd.equals("가져오기")){

			String RetData = Communication("LOAD_USERHEADER", appl_code+"."+typeFlag.getSelectedItem());
			txtInData.setText(RetData.replace("<NODATA>", ""));
			
		}
    	if (cmd.equals("저장")){
    		
    		String savedata = "";
			String indata = txtInData.getText();
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
    		
    		String RetData = Communication("SAVE_USERHEADER", appl_code+"."+typeFlag.getSelectedItem() + "<DATAGUBUN>" + savedata);
        	System.out.println("사용자헤더 저장 : ["+RetData+"]");
        	if (!RetData.trim().equals("OK")) {
        		JOptionPane.showMessageDialog(null,"사용자헤더를 저장하지 못하였습니다.");
        	}	
        	else {
        		JOptionPane.showMessageDialog(null,"사용자헤더를 저장하였습니다.");
        	}
    		dispose();
    	}
    	if (cmd.equals("닫기")){
    		InData = "";
    		dispose();
    	}
	}
	public String getInData()
	{
		return InData;
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
            System.out.println("1.전송데이터 : ["+senddata+"]");
            senddata = senddata.replace("\r","");
            String cmd = cmdstr + "                                        ";
            String SendStr = String.format("%08d", senddata.getBytes().length) + cmd.substring(0,32) + senddata;
            
            System.out.println("전송데이터 : ["+SendStr+"]");
            
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