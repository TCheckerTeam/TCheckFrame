package User;

import java.awt.BorderLayout;
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
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.Timer;
 
public class StartUserProc  extends JDialog implements ActionListener{
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
		private JProgressBar progressBar;
		public  ThreadDownload threadDownload = null;
		public  Timer timer = null;
		public StartUserProc() {
 			GraphicsEnvironment ee = GraphicsEnvironment.getLocalGraphicsEnvironment();
	        GraphicsDevice[] gd = ee.getScreenDevices();
			Rectangle rect = ee.getMaximumWindowBounds();
			
			tmpAnylinkIP      = GetRegister("TCHECKER_ANYLINKIP");
			tmpAnylinkPort    = GetRegister("TCHECKER_ANYLINKPORT");
			tmpUserID         = GetRegister("TCHECKER_USERID");
 
			this.setTitle("사용자접속");
			this.setModal(true);
			this.setLayout(null);
	 
			JPanel panMain = new MyPanel();
			panMain.setLayout(null);
			panMain.setBounds(0,0,459,321);
 
	        //ProgressBar 설정
	        progressBar = new JProgressBar(0, 100);
	        progressBar.setValue(0);
	        progressBar.setStringPainted(true);
	        progressBar.setBounds(100, 260, 336, 20);
	        progressBar.setVisible(false);
	     
	        panMain.add(progressBar, BorderLayout.SOUTH);
	        timer = new Timer(100, new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	if (threadDownload != null) {
	            		if (threadDownload.isSkip) {
	            			progressBar.setVisible(false);
	            			threadDownload.CurrentTaskSize = 0;
                			threadDownload.DownLoadFileSize = 0;
                			timer.stop();
                			
                			//To Next Stage ...
        	    			new TCheckServerManager("TChecker");
        	    			dispose();
	            		}
	            		else if (threadDownload.isDownload) {
	            			progressBar.setVisible(false);
        	    			Preferences userRootPrefs = Preferences.userRoot();
        	        		userRootPrefs.put("TCHECKER_VERSION", threadDownload.NewVerVersion);
        	        		
	                		JOptionPane.showMessageDialog(null, "신규 버젼으로 Upgrade 되였습니다. 적용하시려면 다시 시작하세요.[" + threadDownload.NewVerVersion + "]");
	                		System.exit(0);
                		}
	            		else {
	            			if (threadDownload.CurrentTaskSize > 0 && threadDownload.DownLoadFileSize > 0) {
			                	int percent =  (int)((float)threadDownload.CurrentTaskSize/(float)threadDownload.DownLoadFileSize * 100.);
			                	progressBar.setValue(percent);
			                	progressBar.updateUI();
			            	}
			            	else if (threadDownload.CurrentTaskSize == 0 && threadDownload.DownLoadFileSize > 0) {
			            		progressBar.setValue(0);
			            	}
                		}
	            		
	            	}
	            	
	            }    
	        });
	        timer.start();
	 
	        
 
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
	    		userRootPrefs.put("TCHECKER_USERID", txtUserID.getText());
	    		
	    		int baseport = Integer.parseInt(txtAnylinkPort.getText().trim());
	    		userRootPrefs.put("TCHECKER_USERREMOTESENDPORT", "" + (baseport + 2));
	    		userRootPrefs.put("TCHECKER_USERREMOTERECVPORT", "" + (baseport + 1));
	    		userRootPrefs.put("TCHECKER_RESPORTURL", "" + (baseport + 3));
	    		userRootPrefs.put("TCHECKER_RESPORTTCP", "" + (baseport + 4));
	    		
	    		if (CheckUserID() == true) {
	    			String VerAnylinkIP      = GetRegister("TCHECKER_ANYLINKIP");
	    			String VerAnylinkPort    = GetRegister("TCHECKER_ANYLINKPORT");
	    			String VerVersion        = GetRegister("TCHECKER_VERSION");
	    			
	    			if (VerVersion.trim().equals("")) VerVersion = "1.0.0.0";
	    			if (VerAnylinkIP.trim().equals("") || VerAnylinkPort.trim().equals("")) {
	    				JOptionPane.showMessageDialog(null, "서버에 대한 IP 및 Port 를 확인하세요.");
	    				return ;
	    			}
	    			
	    			progressBar.setVisible(true);
	    			threadDownload = new ThreadDownload(VerAnylinkIP, VerAnylinkPort, VerVersion);
	    			threadDownload.start();
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
 
	    	RetData = Communication("READ_USER", txtUserID.getText());

 

	    	String[] arrtmp = RetData.split("\n")[0].split("\t");
	    	 System.out.println("READ_USER:" + RetData + ":" + arrtmp.length);
	    	 
	    	Preferences userRootPrefs = Preferences.userRoot();
	    	userRootPrefs.put("TCHECKER_USERPERMIT", arrtmp[4]);
	    	
	    	return true;
		}
		
	    private String Communication(String cmdstr, String senddata)
	    {
			Socket one_client = null;
	    	DataOutputStream dos = null;
	    	DataInputStream dis = null;
	    	String recvdata = "";
	    	try {
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
	        new StartUserProc();
		}
	    class MyPanel extends JPanel implements DropTargetListener{
	        public void paintComponent(Graphics g){
				try {
		    		File f = new File("./Image/logon_user.jpg");
					BufferedImage image = ImageIO.read(f);
		    		super.paintComponent(g);
		            g.drawImage(image, 0, 0, null);
		            System.out.println(image.getWidth() + ":" + image.getHeight());
				}catch(Exception e1) {
				    e1.printStackTrace();
			    }
	       }
	        @Override
	        public void dragEnter(DropTargetDragEvent dtde) {
 
	        }
	        @Override
	        public void dragOver(DropTargetDragEvent dtde) {
	 
	        }
	        @Override
	        public void dropActionChanged(DropTargetDragEvent dtde) {
	 
	        }
	        @Override
	        public void dragExit(DropTargetEvent dte) {
	 
	        }
	        @Override
	        public void drop(DropTargetDropEvent dtde) {
 
	        }
	    }
 

 
}
