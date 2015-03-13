package User;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.util.Properties;
import java.util.prefs.Preferences;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
 
  
public class TCheckPanelRemote {
	private JPanel mypanel = null;
	private CommData commdata = new CommData();
	private ThreadManagerSplit threadmanagersplit = null;
	private ThreadManagerCmd  threadmanagercmd = null;
	private Socket   one_client = null;
	
	public TCheckPanelRemote(JPanel panel)
	{
		File file = new File("./Send");
		if (!file.exists()) file.mkdir();
		mypanel = panel;
	}
 
	public void myPaneRemoteClose()
	{
	    threadmanagersplit.ThreadStop();
	    threadmanagercmd.ThreadStop();
	}
    public void myPaneRemoteInit()
    {
    	DataOutputStream dos     = null;
    	DataInputStream  dis     = null;
    	byte[]           lenbyte = new byte[8];
        byte[]           cmdbyte = new byte[10];
		Robot            robot   = null;
        Rectangle        rect    = new Rectangle(0,0,0,0);
        long             imagefullsize = 0;
 
		//������͸��� ��쿡 Sub Screen �� ��ȭ���� �������� ��/������ ��� �ִ��� üũ�Ѵ�.
		//ȭ��ĸ���� �ִ� width �� height �� ���Ѵ�.
		GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gd = e.getScreenDevices();
        
        if (gd.length == 1){
        	rect.width  = gd[0].getDefaultConfiguration().getBounds().width;
        	rect.height = gd[0].getDefaultConfiguration().getBounds().height;
        	commdata.SetGraphicCnt(1);
        	commdata.SetGraphicLeftRight(1);
        	commdata.SetGraphicDiff(0);
        }
        if (gd.length == 2){
        	try{
        		commdata.SetGraphicCnt(2);
        		commdata.SetGraphicLeftRight(1);
        		commdata.SetGraphicDiff(0);
        		
        		PointerInfo oldMouseInfo = MouseInfo.getPointerInfo(); //����Ŀ����ġ ����
        	    if (robot == null) robot = new Robot();
        	    robot.mouseMove(-100, -100);
        	    PointerInfo newMouseInfo = MouseInfo.getPointerInfo();
        	    robot.mouseMove(oldMouseInfo.getLocation().x, oldMouseInfo.getLocation().y); //����Ŀ����ġ����
        	    
        	    if(newMouseInfo.getLocation().x < 0) {
        	        //Sub Screen�� ��ȭ���� �������� ������ ���� ��쿡�� x ���� �����Ѵ�.
	        	    	rect.x = gd[1].getDefaultConfiguration().getBounds().width * -1;
	        	    	rect.y = gd[0].getDefaultConfiguration().getBounds().height - gd[1].getDefaultConfiguration().getBounds().height;
	        	    	commdata.SetGraphicLeftRight(-1);
	        	    	commdata.SetGraphicDiff(gd[1].getDefaultConfiguration().getBounds().width);
        	    }
        	 
        	    for(int i=0;i < gd.length;i++){
		        	rect.width  += gd[i].getDefaultConfiguration().getBounds().width;
	        		if (rect.height <= gd[i].getDefaultConfiguration().getBounds().height) {
	        			rect.height = gd[i].getDefaultConfiguration().getBounds().height;
	        		}
		        }
      
	        }catch(Exception ee){}
	    }
        
        //ȭ��ĸ���� �̹����� ���� ��üũ�⸦ �˱� ���Ͽ� �Ʒ� ������ �����Ѵ�.
        try {
        	if (robot == null) robot = new Robot();
    	    BufferedImage image = robot.createScreenCapture(rect);
			ImageIO.write(image, "bmp", new File("./Send/NewImage.bmp"));
  			 
            // Read NewImage.bmp
            File f1 = new File("./Send/NewImage.bmp");
            DataInputStream in = new DataInputStream(new FileInputStream(f1));
            imagefullsize = (int) f1.length();
            in.close();
        }catch(Exception ee){}
   
        //ĸ���� �̹��� ������ ���� Socket Channel Connect
 
		String RetData = Communication("READ_ADMINIP", "NODATA");
    	if (RetData.trim().equals("") || RetData.trim().equals("NOT-FOUND")) {
    		JOptionPane.showMessageDialog(null,"Administrator�� ���� ���������� �������� ���߽��ϴ�.");
    		return ;
    	}
 
    	String[] arrRetData = RetData.split("\r")[0].split("\n")[0].split(":");
        
        commdata.SetRemoteIP(arrRetData[0]);
        commdata.SetRemoteSendPort(GetRegister("TCHECKER_USERREMOTESENDPORT"));
        commdata.SetRemoteRecvPort(GetRegister("TCHECKER_USERREMOTERECVPORT"));
     
        //ȭ��ĸ�� Thread ����
 
    	threadmanagersplit = new ThreadManagerSplit(commdata, rect);
		threadmanagersplit.start();

        //Ű���� �� ���콺 Event�� �����ϱ� ���� Command Thread ����
    	threadmanagercmd = new ThreadManagerCmd(commdata);
	    threadmanagercmd.start();
    
 
        JOptionPane.showMessageDialog(null, "���������� ������ ������ �ǿ����ϴ�.");
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
    
    private void setThreadSleep(int time)
    {
   	    try{
            Thread.sleep(time);
        } catch(InterruptedException e) {}
    } 
    
    public class CommData {
    	private String UserID = "";
    	private String ChannelID = "";
    	private String RemoteIP = "";
    	private String RemoteSendPort = "";
    	private String RemoteRecvPort = "";
    	private BufferedImage IMG = null;
    	
    	private int   graphic_cnt   = 1;
    	private int   graphic_diff  = 0;
    	private int   graphic_leftright = 1;  //1:����, -1:����
    	private String CtrlCStr = "";
    	private Boolean CtrlCStrStatus = false;
     
    	public void SetUserID(String parm0){ UserID = parm0; }
    	public String GetUserID() {return UserID;}
    	
    	public void SetChannelID(String parm0){ ChannelID = parm0; }
    	public String GetChannelID() {return ChannelID;}
    	
    	public void SetRemoteIP(String parm0){ RemoteIP = parm0; }
    	public String GetRemoteIP() {return RemoteIP;}
    	public void SetRemoteSendPort(String parm0){ RemoteSendPort = parm0; }
    	public String GetRemoteSendPort() {return RemoteSendPort;}
    	public void SetRemoteRecvPort(String parm0){ RemoteRecvPort = parm0; }
    	public String GetRemoteRecvPort() {return RemoteRecvPort;}
    	
    	public void SetIMG(BufferedImage parm0){ IMG = parm0; }
    	public BufferedImage GetIMG() {return IMG;}
     
    	public void SetGraphicCnt(int parm0){ graphic_cnt = parm0; }
    	public int  GetGraphicCnt() {return graphic_cnt;}
    	
    	public void SetGraphicLeftRight(int parm0){ graphic_leftright = parm0; }
    	public int  GetGraphicLeftRight() {return graphic_leftright;}
     
    	public void SetGraphicDiff(int parm0){ graphic_diff = parm0; }
    	public int  GetGraphicDiff() {return graphic_diff;}
    	
    	public void SetCtrlCStr(String parm0){ CtrlCStr = parm0; }
    	public String GetCtrlCStr() {return CtrlCStr;}
    	
    	public void SetCtrlCStrStatus(Boolean parm0){ CtrlCStrStatus = parm0; }
    	public Boolean GetCtrlCStrStatus() {return CtrlCStrStatus;}
    }

    public class ThreadManagerCmd extends Thread{ 
    	private CommData COMMDATA = null;
    	private Robot robot = null;
    	private Socket one_client    = null;
    	private DataInputStream  dis = null;
    	private DataOutputStream dos = null;
        public ThreadManagerCmd(CommData commdata)
        { 
         	COMMDATA = commdata;
            try {
                robot = new Robot();
            }catch(AWTException e1) {}

        }
        public void ThreadStop()
        {
    		try {
    			System.out.println("Thread Interrupt Exit.");
	    		if (one_client != null) one_client.close();
	    		if (dis != null) dis.close();
	    		if (dos != null) dos.close();
    		}catch(Exception e){}
        }
    	public void run()
    	{  
        	byte[] lenbyte = new byte[8];
            byte[] cmdbyte = new byte[10];
            byte[] recvdata = null;
     
    		try{
     
    			one_client = new Socket(COMMDATA.GetRemoteIP(), Integer.parseInt(COMMDATA.GetRemoteRecvPort()));
    	        one_client.setSoTimeout(1000);
    	        dos = new DataOutputStream(one_client.getOutputStream());
                dis = new DataInputStream(one_client.getInputStream());
     
    	 		while(!Thread.currentThread().isInterrupted()) {
    	            try {
    	 
    	            	dis = new DataInputStream(one_client.getInputStream());
    	                dis.read(lenbyte);
    	                dis.read(cmdbyte);
    	 
    	                try {
    	                    recvdata = new byte[Integer.parseInt(new String(lenbyte).trim())];
    	                }catch(Exception e1) {
    	                	dis.reset();
    	                	System.out.println("Exception Data : " + new String(lenbyte));
    	                	continue;
    	                }
    	                for(int i = 0 ; i < recvdata.length ;i++){
    	               	    recvdata[i] = dis.readByte();
    	   	            }
    	                
    	                //Command Parsing
                        String cmd = new String(cmdbyte);
                        if (cmd.trim().indexOf("COMMANDSVR") >= 0) Proc_ConnectCmd(new String(recvdata));
                        if (cmd.trim().indexOf("FILESNDSVR") >= 0) Proc_FileSend(recvdata);
    	            }
    	            catch(EOFException e) {
    	            	e.printStackTrace();
    	            	break;
    	            }
    	            catch(IOException e) {
    	            	
    	            }
    	 		}
    		}catch(IOException e){
            	e.printStackTrace();
                return;              
            } 
    		try {
    			System.out.println("Thread Interrupt Exit.");
	    		if (one_client != null) one_client.close();
	    		if (dis != null) dis.close();
	    		if (dos != null) dos.close();
    		}catch(Exception e){}
    	}
    	private void Proc_ConnectCmd(String recvdata)
    	{
    		if (recvdata.equals("")) return;
    		
    		String[] arrcmd = recvdata.split(":");
    		if (arrcmd == null) return;
    		
    		try {
       
    			if (arrcmd[0].equals("Pressed")) {
     
    				if (arrcmd[3].equals("1")) robot.mousePress(InputEvent.BUTTON1_MASK);
    				if (arrcmd[3].equals("2")) robot.mousePress(InputEvent.BUTTON2_MASK);
    				if (arrcmd[3].equals("3")) robot.mousePress(InputEvent.BUTTON3_MASK);
    			}
    			if (arrcmd[0].equals("Released")) {
    				if (arrcmd[3].equals("1")) robot.mouseRelease(InputEvent.BUTTON1_MASK);
    				if (arrcmd[3].equals("2")) robot.mouseRelease(InputEvent.BUTTON2_MASK);
    				if (arrcmd[3].equals("3")) robot.mouseRelease(InputEvent.BUTTON3_MASK);

    			}
    			if (arrcmd[0].equals("Moved")) {
     
    				if (COMMDATA.GetGraphicCnt() == 1) {
    					 
    					robot.mouseMove( Integer.parseInt(arrcmd[1]) , Integer.parseInt(arrcmd[2]) );
    				}
    				if (COMMDATA.GetGraphicCnt() == 2) {
    					int x = Integer.parseInt(arrcmd[1]) - COMMDATA.GetGraphicDiff();
    					int y = Integer.parseInt(arrcmd[2]);
    					robot.mouseMove(x,y);
    				}
     
    			}
    			if (arrcmd[0].equals("Dragged")) {
    				int x = 0;
    				int y = 0;
    				if (COMMDATA.GetGraphicCnt() == 1) {
    					x = Integer.parseInt(arrcmd[1]);
    					y = Integer.parseInt(arrcmd[2]);
    				}
    				if (COMMDATA.GetGraphicCnt() == 2) {
    					x = Integer.parseInt(arrcmd[1]) - COMMDATA.GetGraphicDiff();
    					y = Integer.parseInt(arrcmd[2]);
    				}
    				robot.mouseMove(x,y);
    			}
    			if (arrcmd[0].equals("Keyboard")) {
     
    				if (Integer.parseInt(arrcmd[1]) == KeyEvent.VK_V && Integer.parseInt(arrcmd[2]) == 2){
    					//CTRL+V
    					String datastr = "";
    					int sidx = recvdata.indexOf("@TEXTSTART@");
    					int eidx = recvdata.indexOf("@TEXTEND@");
    					if (sidx > 0 && eidx > 0){
    						datastr = recvdata.substring(sidx + 11, eidx);
    						
    						StringSelection stringSelection = new StringSelection(datastr);
    					    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    					    clipboard.setContents(stringSelection, null);
    					}
    				}
      
    				char ch1 = (char)Integer.parseInt(arrcmd[1]);
    				char ch2 = (char)Integer.parseInt(arrcmd[2]);
    				if (ch2 > 0){
    					if ((ch2 & 0x01) > 0) {
    						robot.keyPress(KeyEvent.VK_SHIFT);
    						robot.delay(10);
    					}
    					if ((ch2 & 0x02) > 0) {
    						robot.keyPress(KeyEvent.VK_CONTROL);
    						robot.delay(10);
    					}
    					if ((ch2 & 0x08) > 0) {
    						robot.keyPress(KeyEvent.VK_ALT);
    					}
    					
    					robot.keyPress(ch1);
    					robot.delay(10);
    					robot.keyRelease(ch1);
    					
    					if ((ch2 & 0x01) > 0) {
    						robot.keyRelease(KeyEvent.VK_SHIFT);
    						robot.delay(10);
    					}
    					if ((ch2 & 0x02) > 0) {
    						robot.keyRelease(KeyEvent.VK_CONTROL);
    						robot.delay(10);
    					}
    					if ((ch2 & 0x08) > 0) {
    						robot.keyRelease(KeyEvent.VK_ALT);
    						robot.delay(10);
    					}
    					
    				}
    				else {
    					robot.keyPress(ch1);
    					robot.keyRelease(ch1);
    				}
    				
    				if (Integer.parseInt(arrcmd[1]) == KeyEvent.VK_C && Integer.parseInt(arrcmd[2]) == 2){
    					//CTRL+C
    					String datastr = getClipboardContents();
    					COMMDATA.SetCtrlCStr(datastr);
    					COMMDATA.SetCtrlCStrStatus(true);
    				}
    			}
     
    		}catch(Exception e) {}

    	}
        public String getClipboardContents() {
            String result = "";
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
     
            Transferable contents = clipboard.getContents(null);
            boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
            if (hasTransferableText) {
    	          try {
    	        	  result = (String)contents.getTransferData(DataFlavor.stringFlavor);
    	          }
    	          catch (Exception ex){
    		            System.out.println(ex);
    		            ex.printStackTrace();
    	          }
            }
            return result;
       }
    	private void Proc_FileSend(byte[] recvdata)
    	{
    		
    		if (recvdata.length <= 100) return;
    		byte[] cmdbytes = new byte[100];
    		System.arraycopy(recvdata, 0, cmdbytes, 0, cmdbytes.length);
    		
    		String cmd = new String(recvdata);
    		String[] arrcmd = cmd.split(":");
    	 
    		try {
    			if (arrcmd[0].equals("FileSend")) {
    				String WriteFileName = System.getProperty("user.home").replace("\\", "/") + "/Desktop/" + arrcmd[1].trim() + ".backup";
    				DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(WriteFileName)));
    				out.write(recvdata, 100, recvdata.length);
    				out.close();
    			}
    		}catch(Exception e) {}

    	}
        private void setThreadSleep(int time)
        {
       	    try{
                Thread.sleep(time);
            } catch(InterruptedException e) {}
        } 
     
    }

    public class ThreadManagerSplit extends Thread{ 
    	
    	private CommData  COMMDATA = null;
        private boolean   isStart = false;
        private Rectangle all_rect = new Rectangle(0,0,0,0);
    	private Socket           one_client = null;
    	private DataInputStream  dis        = null;
    	private DataOutputStream dos        = null;
    	
    	private Robot          robot    = null;
    	private byte[]         NewRGB   = null;
    	private byte[]         OldRGB   = null;
    	private BufferedImage  NewImage = null;
    	private String         fname    = "";
    	private int            diffgap  = 240;
    	private int            xgap     = diffgap;
    	private int            ygap     = diffgap;
    	private String         SendList = "";
    	private boolean        isDiff   = false;
    	private byte[]         DiffData = null;
    	private byte[]         bitmapbuf= null;
    	private int            bitidx   = 0;
        private int            bitnam   = 0;
        private int            DiffIdx  = 0;
        private int            debug    = 0;
        
        public ThreadManagerSplit(CommData commdata, Rectangle rect)
        { 
        	COMMDATA = commdata;
        	all_rect = rect;
    		isStart = false;
        }
        public void ThreadStop()
        {
    		try {
    			System.out.println("Thread Interrupt Exit.");
	    		if (one_client != null) one_client.close();
	    		if (dis != null) dis.close();
	    		if (dos != null) dos.close();
    		}catch(Exception e){}
        }
    	public void run()
    	{  

            
            //ĸ���� �̹��� ������ ���� Socket Channel Connect
            try{
            	System.out.println(COMMDATA.GetRemoteIP() + ":" + COMMDATA.GetRemoteSendPort());
            		
    			one_client = new Socket(COMMDATA.GetRemoteIP(), Integer.parseInt(COMMDATA.GetRemoteSendPort()));
    	        one_client.setSoTimeout(5000);
                dos = new DataOutputStream(one_client.getOutputStream());
                dis = new DataInputStream(one_client.getInputStream());
     
            }catch(IOException e1){
    			JOptionPane.showMessageDialog(null, "���Ἴ���� ���� �ǿ����ϴ�.");
                return;              
            } 
     
     		while(!Thread.currentThread().isInterrupted()) {

        		try {
        			//��ü �̹����� ĸ���Ͽ�, ȭ��(bmp)�� Write�Ѵ�. 
        			//ByteArray�� ���� ���, �ӵ��� ������ �ε����ϰ� ȭ�� Read/Write ����� �̿���.
        			SendList = "";
        			if (robot == null) robot = new Robot();
        			NewImage = robot.createScreenCapture(all_rect);
        			
        			
        			isDiff = false;
        			if (isStart != true) {
        				isStart = true;
        				fname = "./Send/image_" + all_rect.x + "_" + all_rect.y + "_" + all_rect.width + "_" + all_rect.height + "_Y.snd";
        				ImageIO.write(NewImage, "bmp", new File(fname)); //File Write
        				
                    	File f = new File(fname);
                        dis = new DataInputStream(new FileInputStream(f));
                        int len = (int) f.length();
                        OldRGB = new byte[len];
                        NewRGB = new byte[len];
                        DiffData = new byte[len];
                        dis.readFully(OldRGB);
                        dis.close();
                        
                        SendList = fname;
                        isDiff = true;
        			}
        			else {
        				//�ű� �̹��� ���� Load
        				ImageIO.write(NewImage, "bmp", new File("./Send/NewImage.bmp")); 
                    	File f = new File("./Send/NewImage.bmp");
                        dis = new DataInputStream(new FileInputStream(f));
                        int len = (int) f.length();
                        dis.readFully(NewRGB);
                        dis.close();
                        
                        //������ ����
                        bitmapbuf = new byte[NewRGB.length/8];
                        if ((NewRGB.length % 8) > 0) bitmapbuf = new byte[NewRGB.length/8 + 1];
                        for(int i=0;i < bitmapbuf.length ;i++) bitmapbuf[i] = 0x00;
            
                        DiffIdx  = 0;
            			for(int i=0;i< NewRGB.length;i++) 
            			{
            				try {
            					
                                if (NewRGB[i] != OldRGB[i] ) {
                                	isDiff = true;
                                	
                        			bitidx = i / 8;
                        			bitnam = i % 8;
                        			 
                    				if (bitnam == 0 ) {bitmapbuf[bitidx] = (byte)(bitmapbuf[bitidx] | 0x80);DiffData[DiffIdx++] = NewRGB[i];}
                    				if (bitnam == 1 ) {bitmapbuf[bitidx] = (byte)(bitmapbuf[bitidx] | 0x40);DiffData[DiffIdx++] = NewRGB[i];}
                    				if (bitnam == 2 ) {bitmapbuf[bitidx] = (byte)(bitmapbuf[bitidx] | 0x20);DiffData[DiffIdx++] = NewRGB[i];}
                    				if (bitnam == 3 ) {bitmapbuf[bitidx] = (byte)(bitmapbuf[bitidx] | 0x10);DiffData[DiffIdx++] = NewRGB[i];}
                    				if (bitnam == 4 ) {bitmapbuf[bitidx] = (byte)(bitmapbuf[bitidx] | 0x08);DiffData[DiffIdx++] = NewRGB[i];}
                    				if (bitnam == 5 ) {bitmapbuf[bitidx] = (byte)(bitmapbuf[bitidx] | 0x04);DiffData[DiffIdx++] = NewRGB[i];}
                    				if (bitnam == 6 ) {bitmapbuf[bitidx] = (byte)(bitmapbuf[bitidx] | 0x02);DiffData[DiffIdx++] = NewRGB[i];}
                    				if (bitnam == 7 ) {bitmapbuf[bitidx] = (byte)(bitmapbuf[bitidx] | 0x01);DiffData[DiffIdx++] = NewRGB[i];}
                                }
            				}catch(Exception e1){break;}
            			}
            			if (isDiff){
            				fname = "./Send/image_" + all_rect.x + "_" + all_rect.y + "_" + all_rect.width + "_" + all_rect.height + "_N.snd";
            				DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(fname)));
            				out.write(bitmapbuf, 0, bitmapbuf.length);
            				out.write(DiffData, 0, DiffIdx);
            				out.close();
            				
            				SendList = fname;
            				System.arraycopy(NewRGB, 0, OldRGB, 0, NewRGB.length);
            			}
     
        			}
     
                    //������ �̹����� ������, �̹����� �����ϰ� NewImage�� OldImage�� �����Ѵ�.
        			if (isDiff  ) {
        				 
        				ZipImage(SendList.split("\n"));

        			    //������ �̹����� �����Ͽ�, Relay Server �� ������.
                    	File f = new File("./Send/send.zip");
                        dis = new DataInputStream(new FileInputStream(f));
                        int len = (int) f.length();
                        byte[] imgbuf = new byte[len];
                        dis.readFully(imgbuf);
                        dis.close();

                        String lencmd = String.format("%08dIMGSENDCLI", imgbuf.length) ;
                    	dos = new DataOutputStream( one_client.getOutputStream());
            	        dos.write(lencmd.getBytes());
            	        dos.write(imgbuf);
                        dos.flush();
                   
                        //Client ȭ�鿡�� Ctrl+C �� ����������,Manager ������ Clipboard �� �Ȱ��� �ٿ��ֱ⸦ �� ��
                        //�ֳ��ϸ�, Manager PC�� Notepad ��� ���� Editor�� Ctrl+V�� ���� ��� �����ϱ� ����.
                        if (COMMDATA.GetCtrlCStrStatus()) {
                        	lencmd = String.format("%08d", COMMDATA.GetCtrlCStr().getBytes().length ) + "CLIPBRDCLI";
    	                	dos.write(lencmd.getBytes());
    	        	        dos.write(COMMDATA.GetCtrlCStr().getBytes());
    	                    dos.flush();
    	                    COMMDATA.SetCtrlCStrStatus(false);
                        }
                         
                       // System.out.println("SendSize:" + len);
 
        			}
        			
    			}catch(Exception e){
    				JOptionPane.showMessageDialog(null, "���Ἴ���� ���� �ǿ����ϴ�.");
    				e.printStackTrace();
    	            return;              
    	        } 
    			isStart = true;
     		}//while - end
     		

    	}
     
    	public int getByteToInt(byte[] byteparm)
    	{
    	       int newValue = 0;
    	        switch ( byteparm.length )
    	        {
    	            case 1 :
    	                newValue |= ( byteparm[ 0 ] ) & 0xFF;
    	                break;
    	            case 2 :
    	                newValue |= ( ( byteparm[ 0 ] ) << 8 ) & 0xFF00;
    	                newValue |= ( byteparm[ 1 ] ) & 0xFF;
    	                break;
    	            case 3 :
    	                newValue |= ( ( byteparm[ 0 ] ) << 16 ) & 0xFF0000;
    	                newValue |= ( ( byteparm[ 1 ] ) << 8 ) & 0xFF00;
    	                newValue |= ( byteparm[ 2 ] ) & 0xFF;
    	                break;
    	            case 4 :
    	                newValue |= ( ( byteparm[ 0 ] ) << 24 ) & 0xFF000000;
    	                newValue |= ( ( byteparm[ 1 ] ) << 16 ) & 0xFF0000;
    	                newValue |= ( ( byteparm[ 2 ] ) << 8 ) & 0xFF00;
    	                newValue |= ( byteparm[ 3 ] ) & 0xFF;
    	            default :
    	                break;
    	        }
    	        return newValue;
     
    	}
    	
    	public int[] getByteToInt(byte[] byteparm, int size)
    	{
    		int[] result = new int[size / 4];
    		byte[] byte4 = new byte[4];
    		for(int i=0;i < result.length ;i++){
    			System.arraycopy(byteparm, i * 4, byte4, 0, 4);
    			result[i] = getByteToInt(byte4);
    		}
            return result;
    	}
    	public byte[] getIntToBytes(int intparam )
    	{
            ByteBuffer buff = ByteBuffer.allocate( 4 );
            buff.putInt( intparam );
            buff.order( ByteOrder.BIG_ENDIAN );
            return buff.array( );
     
    	}
    	public byte[] getIntToBytes(int[] intparam, int sidx, int size)
    	{
    		byte[] intToByte = new byte[size * 4]; 
    		for(int i=0;i < size;i++){
    			System.arraycopy(getIntToBytes(intparam[sidx + i]),0, intToByte, i * 4, 4);
    		}
    		return intToByte;
    	}
    	public int[] getByteImage(BufferedImage im, int sx, int sy, int w, int h)
    	{
    	  try {
    		  int[] rgbs = new int[w*h]; 
    		  im.getRGB(sx, sy, w, h, rgbs, 0, w);
    		  return rgbs;
    	  }
    	  catch (Exception ex) {
    	      ex.printStackTrace();
    	      System.out.println("sx:" + sx + ", sy:" + sy);
    	      return null;
    	  }
    	}
    	public void setByteImage(BufferedImage im, int sx, int sy, int w, int h, int[] rgbs )
    	{
    		  try {
    			  im.setRGB(sx, sy, w, h, rgbs, 0, w);
    		  }
    		  catch (Exception ex) {
    		      ex.printStackTrace();
    		  }
    	}
        private void setThreadSleep(int time)
        {
       	    try{
                Thread.sleep(time);
            } catch(InterruptedException e) {}
        } 
        public void ZipImage(String[] arrSendList)
        {
    		int size;
        	byte[] buf = new byte[1024];
        	String fname = "./Send/send.zip";

        	try {
        		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(fname));

        		for(int i=0;i < arrSendList.length ;i++) {
        			if (arrSendList[i].equals("")) break;
     
        	        FileInputStream fis = new FileInputStream(arrSendList[i]);
        	        ZipEntry ze = new ZipEntry(arrSendList[i]); 
         
                	zos.putNextEntry(ze); 
                	while(true)
                	{
                	    size = fis.read(buf, 0, buf.length);
                	    if (size < 0) break;
                	    zos.write(buf, 0, size);
                	}
                	zos.closeEntry(); 
                	fis.close();
                	
            		//���� ������ �̹��� ȭ�� ����
            		File delfile = new File(arrSendList[i]);
                    if (delfile.isFile()) {
                    	delfile.delete();
                    }
        	 
        		}
        		zos.close();
      
        	}catch(Exception e){
                //skip 
        	}
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
}
