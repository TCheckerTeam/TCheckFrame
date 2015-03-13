package Manager;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
 
public class StartManagerProc  extends JDialog implements ActionListener{
		private JTextField txtAnylinkIP, txtAnylinkPort, txtResponsePort;
		private JTextField txtUserID ;
		private JPasswordField txtPassword;
		private JLabel lblAnylinkIP, lblAnylinkPort, lblResponsePort;
		private JLabel lblUserID,lblPassword;
		private JButton btnOK, btnClose;
		private String tmpAnylinkIP = "";
		private String tmpAnylinkPort = "";
		private String tmpResponsePort = "";
		private String tmpUserID = "";
		private String tmpPassword = "";
	 
		public StartManagerProc() {
			GraphicsEnvironment ee = GraphicsEnvironment.getLocalGraphicsEnvironment();
	        GraphicsDevice[] gd = ee.getScreenDevices();
			Rectangle rect = ee.getMaximumWindowBounds();
			
			tmpAnylinkIP      = GetRegister("TCHECKER_ANYLINKIP");
			tmpAnylinkPort    = GetRegister("TCHECKER_ANYLINKPORT");
			tmpUserID         = GetRegister("TCHECKER_MANAGERID");
			tmpUserID         = "Administrator";
			
			this.setTitle("관리자접속");
			this.setModal(true);
			this.setLayout(null);
	 
			MyPanelFlash panMain = new MyPanelFlash();
			panMain.setLayout(null);
			panMain.setBounds(0,0,459,321);
 
 
	        panMain.add( lblAnylinkIP = new JLabel("서버 IP") ); lblAnylinkIP.setBounds(0, 0, 70, 24);
	        panMain.add( txtAnylinkIP = new JTextField(20) );   txtAnylinkIP.setBounds(70, 0, 130, 24);
 
	        panMain.add( lblAnylinkPort = new JLabel("서버 Port") ); lblAnylinkPort.setBounds(0, 0, 70, 24);
	        panMain.add( txtAnylinkPort = new JTextField(6) ) ;         txtAnylinkPort.setBounds(70, 0, 130, 24);
 
	        panMain.add( lblUserID = new JLabel("사용자 ID") );  lblUserID.setBounds(0, 0, 70, 24);
	        panMain.add( txtUserID = new JTextField(20) );        txtUserID.setBounds(70, 0, 100, 24);
 
	        panMain.add( lblPassword = new JLabel("비밀번호") );  lblPassword.setBounds(0, 0, 70, 24);
	        panMain.add( txtPassword = new JPasswordField(20) );     txtPassword.setBounds(70, 0, 100, 24);
	        
 
	        panMain.add (btnOK    = new JButton ("확인") ); btnOK.setBounds(70, 0, 60, 24);
	        panMain.add (btnClose = new JButton ("닫기") ); btnClose.setBounds(140, 0, 60, 24);
	   
	        int wgap = 270;
	        int hgap = 10;
	        lblAnylinkIP.setBounds(wgap, hgap, 300, 24); lblAnylinkIP.setForeground(new Color(179,16,17));
	        txtAnylinkIP.setBounds(wgap + 60, hgap, 100, 24); 
	        hgap += 28;
	        
	        lblAnylinkPort.setBounds(wgap, hgap, 300, 24); lblAnylinkPort.setForeground(new Color(179,16,17));
	        txtAnylinkPort.setBounds(wgap + 60, hgap, 50, 24); 
	        hgap += 28;
	        
	        lblUserID.setBounds(wgap, hgap, 300, 24); lblUserID.setForeground(new Color(179,16,17));
	        txtUserID.setBounds(wgap + 60, hgap, 100, 24); 
	        hgap += 28;
	        
	        lblPassword.setBounds(wgap, hgap, 300, 24); lblPassword.setForeground(new Color(179,16,17));
	        txtPassword.setBounds(wgap + 60, hgap, 100, 24); 
	        hgap += 34;
	        
	        btnOK.setBounds(wgap + 35, hgap, 60, 30); 
	        btnClose.setBounds(wgap + 98, hgap, 60, 30); 
 
	        add(panMain);
	        
	        btnOK.addActionListener(this);
	        btnClose.addActionListener(this);
	        
	        txtAnylinkIP.setText(tmpAnylinkIP);
	        txtAnylinkPort.setText(tmpAnylinkPort);
 
	        txtUserID.setText(tmpUserID);
 
	        this.setBounds(rect.width/2 - 230,rect.height/2 - 100,459,321);
	        this.setVisible(true);
 
		}
		@Override
		public void actionPerformed(ActionEvent e){
			String cmd = e.getActionCommand();
	    	
	    	if (cmd.equals("확인")){
	    		Preferences userRootPrefs = Preferences.userRoot();
	    		userRootPrefs.put("TCHECKER_ANYLINKIP", txtAnylinkIP.getText());
	    		userRootPrefs.put("TCHECKER_ANYLINKPORT", txtAnylinkPort.getText());
	    		userRootPrefs.put("TCHECKER_MANAGERID", txtUserID.getText());
	    		
	    		int baseport = Integer.parseInt(txtAnylinkPort.getText().trim());
	    		userRootPrefs.put("TCHECKER_SERVERREMOTESENDPORT", "" + (baseport + 1));
	    		userRootPrefs.put("TCHECKER_SERVERREMOTERECVPORT", "" + (baseport + 2));
	    		userRootPrefs.put("TCHECKER_RESPORTURL", "" + (baseport + 3));
	    		userRootPrefs.put("TCHECKER_RESPORTTCP", "" + (baseport + 4));
	    		
	    		if (CheckUserID() == true) {
	    			//To Next Stage ...
	    			new TCheckServerManager("TChecker");
	    			this.dispose();
	    		}
	    	}
	    	if (cmd.equals("닫기")){
	    		System.exit(0);
	    	}
		}
	    private String GetRegister(String pKey)
	    {
	    	Preferences userRootPrefs = Preferences.userRoot();
	    	if (userRootPrefs.get(pKey, null) != null) { 
	    		return userRootPrefs.get(pKey, "");
	    	}
	    	return "";
	    }
	    
		private boolean CheckUserID()
		{
 
	    	String RetData = Communication("CHECK_USERID", txtUserID.getText() + "\t" + txtPassword.getText());
	    	if (RetData.trim().equals("") || RetData.trim().equals("NOT-FOUND")) {
	    		JOptionPane.showMessageDialog(null,"사용자ID 및 비밀번호를 확인하세요.");
	    		
	    		return false;
	    	}	
	    	return true;
		}
		
	    private String Communication(String cmdstr, String senddata)
	    {
			Socket one_client = null;
	    	DataOutputStream dos = null;
	    	DataInputStream dis = null;
	    	String recvdata = "";
	    	try {
	    		System.out.println(txtAnylinkIP.getText() +":" +Integer.parseInt(txtAnylinkPort.getText()));
	    		one_client = new Socket();
		        one_client.connect(new InetSocketAddress(txtAnylinkIP.getText(), Integer.parseInt(txtAnylinkPort.getText())), 3000);  //3초 기다림
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
		            try{ if(one_client != null) one_client.close();}catch(Exception e){};
		            try{ if(dos != null) dos.close();}catch(Exception e){};
		            try{ if(dis != null) dis.close();}catch(Exception e){};
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
	    
		/**
		 * @param args
		 */
		public static void main(String[] args) {
			// TODO Auto-generated method stub
	        new StartManagerProc();
		}
	    class MyPanelFlash extends JPanel{
 
	        public void paintComponent(Graphics g){
				try {
		    		File f = new File("./Image/logon_manager.jpg");
					BufferedImage image = ImageIO.read(f);
		    		super.paintComponent(g);
		            g.drawImage(image, 0, 0, null);
		            System.out.println(image.getWidth() + ":" + image.getHeight());
				}catch(Exception e1) {
				    e1.printStackTrace();
			    }
	       }
 
	    }
}
