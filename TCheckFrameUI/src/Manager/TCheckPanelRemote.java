package Manager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.ScrollPane;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Enumeration;
import java.util.List;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;
 
public class TCheckPanelRemote extends JFrame {
    public TCheckPanelRemote mythis = null;
    public JTextPane    pane;
    public JMenuBar     menuBar;
	private CommData    commdata = null;
    private MyPanel     ImagePanel = null;
    private String      CopyFileList = "";
 
    private ThreadListenerSend threadlistenersend = null;
    private ThreadListenerRecv threadlistenerrecv = null;
 
	public TCheckPanelRemote()
	{
		commdata = new CommData();
		mythis = this;
		this.setTitle("원격지원 : 대기중");
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GraphicsEnvironment ee = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gd = ee.getScreenDevices();
		Rectangle screen_rect = ee.getMaximumWindowBounds();
		
        ScrollPane srp = new ScrollPane();
        srp.setSize(1000, 700);
        
        ImagePanel = new MyPanel();
        ImagePanel.setLayout(null);
        ImagePanel.setBackground(Color.WHITE);
        ImagePanel.setSize(4000, 2000);
        
        Dimension dim = new Dimension();
        dim.width = 4000;
        dim.height = 2000;
        ImagePanel.setPreferredSize(dim);
        
        mySetEventInfo();
        
        srp.add(ImagePanel);
        this.add(srp);

		//프레임의 크기를 정의 하여 표시
		this.setVisible(true);
		this.setSize(1000, 700);
		this.setExtendedState(MAXIMIZED_BOTH);
		
     
		//Look & Feel 설정
		try{
			//Java스타일의 Look&Feel 로 설정
			UIManager.setLookAndFeel( "javax.swing.plaf.metal.MetalLookAndFeel");
			
			//설정을 반영
			SwingUtilities.updateComponentTreeUI(this);
		}catch(Exception e){
			e.printStackTrace();
		}
		

	}
	public void myPaneRemoteClose()
	{
		threadlistenersend.myPaneRemoteClose();
		threadlistenerrecv.myPaneRemoteClose();
 
	}
	
    public void mySetEventInfo()
    {

        ImagePanel.addMouseListener(new MouseAdapter()
        {
        	String cmd = "";
            public void mousePressed(MouseEvent e)
            {
            	cmd = "Pressed:" + (e.getX()  ) + ":" + (e.getY()  ) + ":" + e.getButton() + ":" + e.getClickCount();
            	if (threadlistenersend != null) threadlistenersend.SendCommand(cmd);
            }
            public void mouseClicked(MouseEvent e) {
            	cmd = "Clicked:" + (e.getX()  ) + ":" + (e.getY()  ) + ":" + e.getButton() + ":" + e.getClickCount();
            	if (threadlistenersend != null) threadlistenersend.SendCommand(cmd);
            }
            public void mouseReleased(MouseEvent e) {
            	cmd = "Released:" + (e.getX() ) + ":" + (e.getY()  ) + ":" + e.getButton() + ":" + e.getClickCount();
            	if (threadlistenersend != null) threadlistenersend.SendCommand(cmd);
            }
            public void mouseEntered(MouseEvent e) {

            }
            public void mouseExited(MouseEvent e) {

            }

        });
        ImagePanel.addMouseMotionListener(new MouseMotionAdapter()
        {
        	String cmd = "";
 
            public void mouseDragged(MouseEvent e) {
 
            		cmd = "Dragged:" + (e.getX()  ) + ":" + (e.getY() ) + ":" + e.getButton() + ":" + 0;
                	if (threadlistenersend != null) threadlistenersend.SendCommand(cmd);
                    
            }
            public void mouseMoved(MouseEvent e) {

            		cmd = "Moved:" + (e.getX()  ) + ":" + (e.getY()  ) + ":" + 0 + ":" + 0;
                	if (threadlistenersend != null) threadlistenersend.SendCommand(cmd);
                	System.out.println(cmd);
                    
            }

        });
 
        
        //KeyEvent 관련 항목 생성
        ImagePanel.setFocusable(true);
        ImagePanel.addKeyListener(new KeyListener()
        {
        	public void keyReleased(KeyEvent e) {
        		System.out.println("keyReleased:" + e.getKeyCode() + ":" + e.getModifiers());
        		if (e.getKeyCode() == KeyEvent.VK_V && e.getModifiers() == 2){
        			//CTRL+V
        			
        			String cmd = "Keyboard:" + e.getKeyCode() + ":" + e.getModifiers() + ":@TEXTSTART@" + getClipboardContents() + "@TEXTEND@";
                	if (threadlistenersend != null) threadlistenersend.SendCommand(cmd);
                	 
        		}
        		else if (e.getKeyCode() != 154) {
        			
        			String cmd = "Keyboard:" + e.getKeyCode() + ":" + e.getModifiers() + ":" + 0 + ":" + 0;
                	if (threadlistenersend != null) threadlistenersend.SendCommand(cmd);
 
        		}
        	}
            public void keyTyped(KeyEvent e) {
    
        	}
        	public void keyPressed(KeyEvent e) {
   
            }
 
        });
 
  
        //중계서버의 IP 및 포트번호 설정
        commdata.SetImagePanel(ImagePanel);
  
        File file = new File("./Recv");
        if (!file.exists()) file.mkdir();
 
        StartRecvThread();
    }
    public void StartRecvThread()
    {
 
		String RetData = Communication("READ_ADMINIP", "NODATA");
    	if (RetData.trim().equals("") || RetData.trim().equals("NOT-FOUND")) {
    		JOptionPane.showMessageDialog(null,"Administrator에 대한 접속정보를 가져오지 못했습니다.");
    		return ;
    	}
    	String[] arrRetData = RetData.split("\r")[0].split("\n")[0].split(":");
        commdata.SetRemoteIP(arrRetData[0]);
        commdata.SetRemoteSendPort(GetRegister("TCHECKER_SERVERREMOTESENDPORT"));
        commdata.SetRemoteRecvPort(GetRegister("TCHECKER_SERVERREMOTERECVPORT"));
  
		threadlistenersend = new ThreadListenerSend(commdata);
		threadlistenersend.start();
 
		threadlistenerrecv = new ThreadListenerRecv(commdata);
		threadlistenerrecv.start();
 		
    }
    public void LoadImage(String imgfname)
    {
   
		try {
    		File f = new File(imgfname);
			BufferedImage image = ImageIO.read(f);
			ImagePanel.repaint();
		}catch(Exception e1) {
		    e1.printStackTrace();
	    }
    }
    public void LoadImage(MyPanel mypanel, String imgfname)
    {
		try {
    		File f = new File(imgfname);
			BufferedImage image = ImageIO.read(f);
		 
			mypanel.repaint();
		}catch(Exception e1) {
			e1.printStackTrace();
		}
    }
    
    private  void setThreadSleep(int time)
    {
   	    try{
            Thread.sleep(time);
        } catch(InterruptedException e) {}
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
		            
		            ex.printStackTrace();
	          }
        }
        return result;
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
    //-------------------------- Class -----------------------------
    class MyPanel extends JPanel implements DropTargetListener   {
    	 
    	public MyPanel() {
    		DropTarget dt = new DropTarget(this,DnDConstants.ACTION_COPY_OR_MOVE,this,true);
    	}
 
        public void paintComponent(Graphics g){
    		super.paintComponent(g);
            g.drawImage(commdata.GetIMG(), 0, 0, null);
     
       }
        @Override
        public void dragEnter(DropTargetDragEvent dtde) {
        	CopyFileList = "";
        	Transferable t = dtde.getTransferable();
        	if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
        		try {
        			Object td = t.getTransferData(DataFlavor.javaFileListFlavor);
        			if(td instanceof List) {
        				for(Object value:((List)td)) {
        					if(value instanceof File){
        						File file = (File)value;
        						String name = file.getName();
        						CopyFileList = CopyFileList + file.getPath() + "\n";
	
        					}
        				}
        			}
        			 
        		}catch(Exception ex) {
        			ex.printStackTrace();
        		}
        	}
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
        	   String[] arrList = CopyFileList.split("\n");
        	   for(int i=0;i < arrList.length ;i++){
        		   if (arrList[i].trim().equals("")) break;
        		   
        		    
        		   try {
	        		   File f1 = new File(arrList[i]);
	        		   DataInputStream in = new DataInputStream(new FileInputStream(f1));
	
	                   int len1 = (int) f1.length();
	                   byte[] RData = new byte[len1];
	                   in.readFully(RData);
	                   in.close();
	     
	                   if (threadlistenersend != null) {
	                	   threadlistenersend.SendFile(arrList[i]);
	                   }
	                   
        		   }catch(Exception e) {
        			   e.printStackTrace();
        		   }
        	   }
        }
    }
 
    public class CommData {
    	private String UserID = "";
    	private String ChannelID = "";
    	private BufferedImage IMG = null;
        private MyPanel ImagePanel = null;
    	private String RemoteIP = "";
    	private String RemoteSendPort = "";
    	private String RemoteRecvPort = "";
    	
    	public void SetUserID(String parm0){ UserID = parm0; }
    	public String GetUserID() {return UserID;}
    	
    	public void SetChannelID(String parm0){ ChannelID = parm0; }
    	public String GetChannelID() {return ChannelID;}
     
    	public void SetIMG(int yCnt, int xCnt, int xWidth, int yHeight, BufferedImage image)
    	{
    	    IMG = image;
    	    
    	    ImagePanel.repaint();
   
    	}
    	public BufferedImage GetIMG() {return IMG;}
    	public void SetImagePanel(MyPanel parm0){ ImagePanel = parm0; }
     
    	public void SetRemoteIP(String parm0){ RemoteIP = parm0; }
    	public String GetRemoteIP() {return RemoteIP;}
    	public void SetRemoteSendPort(String parm0){ RemoteSendPort = parm0; }
    	public String GetRemoteSendPort() {return RemoteSendPort;}
    	public void SetRemoteRecvPort(String parm0){ RemoteRecvPort = parm0; }
    	public String GetRemoteRecvPort() {return RemoteRecvPort;}
     
    }
    public class ThreadListenerRecv extends Thread{ 
    	private CommData COMMDATA = null;
    	private ServerSocket server = null;
    	private ThreadListenerRecvSub listenrecv = null;
    	public ThreadListenerRecv(CommData commdata)
        { 
        	COMMDATA = commdata;
        }
    	public void myPaneRemoteClose()
    	{
    		listenrecv.ThreadStop();
    
    	}
    	public void run()
    	{
    		String RetData = Communication("READ_ADMINIP", "NODATA");
        	if (RetData.trim().equals("") || RetData.trim().equals("NOT-FOUND")) {
        		JOptionPane.showMessageDialog(null,"Administrator에 대한 접속정보를 가져오지 못했습니다.");
        		return ;
        	}
        	String[] arrRetData = RetData.split("\r")[0].split("\n")[0].split(":");
	        commdata.SetRemoteIP(arrRetData[0]);
	        commdata.SetRemoteSendPort(arrRetData[1]);
	        commdata.SetRemoteRecvPort(arrRetData[2]);
     
    		while(!Thread.currentThread().isInterrupted()) {
    	        try{
    	        	if (server == null) server = new ServerSocket(Integer.parseInt(commdata.GetRemoteRecvPort()));
    	        	Socket client = server.accept();
    	        	
    	        	String userpcip = client.getInetAddress().toString().replace("/", "");
    	        	mythis.setTitle("원격지원 : " + userpcip);
    	        	
    	        	listenrecv = new ThreadListenerRecvSub(COMMDATA,client);
    	        	listenrecv.start();		
    	        }catch(IOException e){
    	        	
    	        	e.printStackTrace();
    	        	System.out.println("중복실행으로 종료처리합니다.");
    	        	mythis.dispose();
    	        	break;
    	        } 
    		}

    	}
     
    }

    public class ThreadListenerRecvSub extends Thread{ 
    	private CommData       COMMDATA = null;
    	private Socket         one_client = null;
    	private int            all_width = 0;
    	private int            all_height = 0;
    	private byte[]         NewRGB   = null;
    	private byte[]         OldRGB   = null;
    	BufferedImage          NewImage = null;
    	private byte[]         DiffData = null;
    	private byte[]         bitmapbuf= null;
    	DataOutputStream       dos = null;
    	DataInputStream        dis = null;
        
        public ThreadListenerRecvSub(CommData commdata, Socket client)
        { 
        	COMMDATA = commdata;
        	one_client = client;
     
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

        	byte[] lenbyte  = new byte[8];
            byte[] cmdbyte  = new byte[10];
     
     		while(!Thread.currentThread().isInterrupted()) {
                try{
                	one_client.setSoTimeout(10000);
                    dos = new DataOutputStream(one_client.getOutputStream());
                    dis = new DataInputStream(one_client.getInputStream());
                	
                	int rc = dis.read(lenbyte);
                	if (rc != 8) {
                		setThreadSleep(10);
                		continue;
                	}
                    
                    //cmd 을 읽어오고, ID 및 PWD를 체크를 한다.
                    dis.read(cmdbyte);
                    String cmd = new String(cmdbyte);
     
                    //Image Data 를 읽어온다.
                    byte[] recvdata = new byte[Integer.parseInt(new String(lenbyte).trim())];
     
                    for(int i = 0 ; i < recvdata.length ;i++){
                   	    recvdata[i] = dis.readByte();
        	        }

        			if (cmd.indexOf("IMGSENDCLI") >= 0) Proc_Image(cmd, recvdata);
        			if (cmd.indexOf("CLIPBRDCLI") >= 0) Proc_ClipboardPaste(recvdata);
        	 
                }catch(EOFException e1){
                	JOptionPane.showMessageDialog(null, "연결세션이 종료 되였습니다.");
                	break;
                }catch(IOException e2){
                	setThreadSleep(1000);
                } 
     		}
     		COMMDATA.SetChannelID("00");
    	}
    	
    	private void Proc_Image(String cmd, byte[] imagedata)
    	{

    		try {
    			 
    				//File Write
    				DataOutputStream out = new DataOutputStream(new FileOutputStream(new File("./Recv/send.zip")));
    				out.write(imagedata);
    				out.close();
    				
    				//압축풀기
    				String zipentryname = ZipFileExtract("./Recv/send.zip", "./Recv/");
                    if (zipentryname.equals("")) return;
                    
                    String[] EntryList = zipentryname.split("\n");
                    String fname = "./Recv/" + EntryList[0];
                    String[] arrtmp = EntryList[0].replace(".\\Send\\","").replace(".","_").split("_");
                    int xGap   = Integer.parseInt(arrtmp[2]);
                    int yGap   = Integer.parseInt(arrtmp[3]);
                    all_width  = Integer.parseInt(arrtmp[4]);
                    all_height = Integer.parseInt(arrtmp[5]);
                    
                    if (arrtmp[6].equals("Y")) {
                    	NewImage = ImageIO.read(new File(fname));
                    	COMMDATA.SetIMG(0, 0, 0, 0, NewImage);
                    	
                    	File f = new File(fname);
                    	DataInputStream dis = new DataInputStream(new FileInputStream(f));
                        int len  = (int) f.length();
                        OldRGB   = new byte[len];
                        NewRGB   = new byte[len];
                        DiffData = new byte[len];
                        dis.readFully(OldRGB);
                        dis.close();

              		    bitmapbuf= new byte[len / 8];
              		    if ((NewRGB.length % 8) > 0) bitmapbuf = new byte[NewRGB.length/8 + 1];
                    }
                    else {

        				File f0 = new File(fname);
        				DataInputStream in = new DataInputStream(new FileInputStream(f0));
                        int len2 = (int) f0.length();
                        in.readFully(bitmapbuf,0, bitmapbuf.length);
                        if ( (len2 - bitmapbuf.length) > 0 ) {
                        	in.readFully(DiffData, 0, len2 - bitmapbuf.length);	
                        }
                        else {
                        	System.out.println("Bug");
                        }
          
                        
                        in.close();
                        f0.delete();
       
                        int DiffDataIdx = 0;
                        int ORGDataIdx = 0;
                    	for(int i=0;i < bitmapbuf.length;i++){
     	        			if ((bitmapbuf[i] & 0x80 ) > 0) OldRGB[ORGDataIdx + 0] = DiffData[DiffDataIdx++];
     	        			if ((bitmapbuf[i] & 0x40 ) > 0) OldRGB[ORGDataIdx + 1] = DiffData[DiffDataIdx++];
     	        			if ((bitmapbuf[i] & 0x20 ) > 0) OldRGB[ORGDataIdx + 2] = DiffData[DiffDataIdx++];
     	        			if ((bitmapbuf[i] & 0x10 ) > 0) OldRGB[ORGDataIdx + 3] = DiffData[DiffDataIdx++];
     	        			if ((bitmapbuf[i] & 0x08 ) > 0) OldRGB[ORGDataIdx + 4] = DiffData[DiffDataIdx++];
     	        			if ((bitmapbuf[i] & 0x04 ) > 0) OldRGB[ORGDataIdx + 5] = DiffData[DiffDataIdx++];
     	        			if ((bitmapbuf[i] & 0x02 ) > 0) OldRGB[ORGDataIdx + 6] = DiffData[DiffDataIdx++];
     	        			if ((bitmapbuf[i] & 0x01 ) > 0) OldRGB[ORGDataIdx + 7] = DiffData[DiffDataIdx++];
     	        			ORGDataIdx += 8;
                    	}
                     
        				DataOutputStream out1 = new DataOutputStream(new FileOutputStream(new File("./Recv/NewImage.bmp")));
        				out1.write(OldRGB, 0, OldRGB.length);
        				out1.close();
        				
                    	NewImage = ImageIO.read(new File("./Recv/NewImage.bmp"));
                    	COMMDATA.SetIMG(0, 0, 0, 0, NewImage);
                    	
                    	File delfile = new File("./Recv/NewImage.bmp");
                    	delfile.delete();
                    }
             
    	   }catch(Exception e) {
    		    e.printStackTrace();
    	   }
    	}
    	 
    	private void Proc_ClipboardPaste(byte[] recvdata)
    	{
    		try {
    			StringSelection stringSelection = new StringSelection(new String(recvdata));
    		    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    		    clipboard.setContents(stringSelection, null);
    			
    	   }catch(Exception e) {
    		    e.printStackTrace();
    	   }
    	}
        private  void setThreadSleep(int time)
        {
       	    try{
                Thread.sleep(time);
            } catch(InterruptedException e) {}
        } 

        public  void ZipFile(String[] source, String target)
        {
        	byte[] buff = new byte[1024];
        	try {
        	    ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(target));
        	    for(int i=0;i < source.length;i++)
        	    {
        	    	FileInputStream in = new FileInputStream(source[i]);
        	    	zipOut.putNextEntry(new ZipEntry(source[i]));
        	    	int len;
        	    	while((len=in.read(buff)) > 0) {
        	    		zipOut.write(buff,0,len);
        	    	}
        	    	zipOut.closeEntry();
        	    	in.close();
        	    }
        	}catch(Exception e){}
        }
        
        public  String ZipFileExtract(String zipFileName, String extractPath)
        {
        	String osName = System.getProperty( "os.name" );
        	InputStream inputStream = null;
        	FileOutputStream outputStream = null;
        	File file;
        	File directories;
        	String entryName = "";
        	String ReturnEntryName = "";
            try {
        		ZipFile zipFile = new ZipFile(zipFileName, "EUC-KR");
        		Enumeration en = zipFile.getEntries(); // nextElement() is called.
        		
        		while (en.hasMoreElements()) {
            		ZipEntry entry = (ZipEntry) en.nextElement();
            		entryName = entry.getName();
            		ReturnEntryName = ReturnEntryName + entryName + "\n";
     
            		file = new File( extractPath + entryName.replace("\\", "/") );
     
            		// 1. empty directory
            		if ( entry.isDirectory()
            		|| (!osName.equals("unix") && entryName.endsWith("\\")) ) {
    	        		file.mkdir();
    	        		continue;
            		}

            		// 2. regular file, if directory exist first mkdir, and write files.
            		String path = extractPath + "/Send";
            		if ( path != null ) {
    	        		directories = new File( path );
    	        		directories.mkdirs();
            		}
     
            		file.createNewFile();

            		inputStream = zipFile.getInputStream( entry );
            		outputStream = new FileOutputStream( file );
            		copyStream( inputStream, outputStream );

            		inputStream.close();
            		outputStream.close();
            	}
            	zipFile.close();
    		} catch ( Exception e ) {
        		return "";
        	} finally {
        		if ( inputStream != null ) try { inputStream.close(); } catch ( IOException ioe ) {}
        		if ( outputStream != null ) try { outputStream.close(); } catch ( IOException ioe ) {}
        	}
     
        	return ReturnEntryName;
        }
        private  void copyStream(InputStream in, OutputStream out) throws Exception {
        	byte buffer[] = new byte[1024];
        	int len = 0;
        	while ( (len = in.read(buffer) ) != -1) {
        	    out.write(buffer, 0, len);
        	}
        }
    	public  int[] getByteImage(BufferedImage im, int sx, int sy, int w, int h)
    	{
    	  try {
     
    		  int[] rgbs = new int[w*h]; 
    		  im.getRGB(sx, sy, w, h, rgbs, 0, w);
    		  return rgbs;
    	  }
    	  catch (Exception ex) {
    		  System.out.println("sx:" + sx + ", sy:" + sy);
    	      ex.printStackTrace();
    	      
    	      return null;
    	  }
    	}
    	public void setByteImage(BufferedImage im, int sx, int sy, int w, int h, int[] rgbs )
    	{
    		  try {
    			  im.setRGB(sx, sy, w, h, rgbs, 0, w);
     
    		  }
    		  catch (Exception ex) {
    			  System.out.println(sx + "," + sy + "," + w + "," + h + "," +  rgbs.length  );
    		      ex.printStackTrace();
    		      
    		    
    		  }
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
    }
    public class ThreadListenerSend extends Thread{ 
    	private CommData COMMDATA = null;
    	private ServerSocket server = null;
    	private ThreadListenerSendSub listensend = null;
    	
    	public ThreadListenerSend(CommData commdata)
        { 
        	COMMDATA = commdata;
        }
    	public void myPaneRemoteClose()
    	{
    		listensend.ThreadStop();
     
    	}
    	public void run()
    	{
 
    		String RetData = Communication("READ_ADMINIP", "NODATA");
        	if (RetData.trim().equals("") || RetData.trim().equals("NOT-FOUND")) {
        		JOptionPane.showMessageDialog(null,"Administrator에 대한 접속정보를 가져오지 못했습니다.");
        		return ;
        	}
        	String[] arrRetData = RetData.split("\r")[0].split("\n")[0].split(":");
	        commdata.SetRemoteIP(arrRetData[0]);
	        commdata.SetRemoteSendPort(arrRetData[1]);
	        commdata.SetRemoteRecvPort(arrRetData[2]);
     
    		while(!Thread.currentThread().isInterrupted()) {
    	        try{
    	        	if (server == null) server = new ServerSocket(Integer.parseInt(commdata.GetRemoteSendPort()));
    	        	Socket client = server.accept();
 
    	        	listensend = new ThreadListenerSendSub(COMMDATA,client);
    	        	listensend.start();		
    	        }catch(IOException e){
    	        	e.printStackTrace();
    	        	System.out.println("중복실행으로 종료처리합니다.");
    	        	mythis.dispose();
    	        	break;
    	        } 
    		}

    	}
        public void SendCommand(String cmd)
        {
        	if (listensend != null)
        	    listensend.SendCommand(cmd);
        }
        public void SendFile(String fullname)
        {
        	if (listensend != null)
        	    listensend.SendFile(fullname);
        }
    }
    
    public class ThreadListenerSendSub extends Thread{ 
    	private CommData COMMDATA = null;
    	private Socket one_client = null;
    	private DataInputStream  dis = null;
     
        public ThreadListenerSendSub(CommData commdata, Socket client)
        { 
        	COMMDATA = commdata;
        	one_client = client;
        }
     
        public void ThreadStop()
        {
    		try {
    			System.out.println("Thread Interrupt Exit.");
	    		if (one_client != null) one_client.close();
	    		if (dis != null) dis.close();
    		}catch(Exception e){}
        }
        
    	public void run()
    	{  
    		
    		byte tmpbyte = (byte)0;
     		while(!Thread.currentThread().isInterrupted()) {
                try{
                	//수신하는 데이타는 없으나, 채널 유지 및 Remote에 의한 채널 close를 감지하기 위해
                	//1-Byte에 대한 Read를 한다.
                	one_client.setSoTimeout(10000);
                	dis = new DataInputStream(one_client.getInputStream());
                	tmpbyte = dis.readByte();
                }catch(EOFException e1){
                	System.out.println("Send Channel Close : " + e1.getMessage());
                	return;
                }catch(Exception e2){
                    //skip
                }
     		}
     		COMMDATA.SetChannelID("00");
    	}
    	
        public void SendCommand(String cmd)
        {
        	DataOutputStream dos = null;
        	
        	try {
        		one_client.setSoTimeout(1000);
                dos = new DataOutputStream(one_client.getOutputStream());
         
        		String lencmd = String.format("%08dCOMMANDSVR", cmd.getBytes().length);
                dos.write(lencmd.getBytes());
                dos.write(cmd.getBytes());
                dos.flush();
     
        	}catch(Exception e){}
        }
        public void SendFile(String fullname)
        {
        	DataOutputStream dos = null;
        	try {
        		
        		one_client.setSoTimeout(1000);
                dos = new DataOutputStream(one_client.getOutputStream());
          
    		    File f1 = new File(fullname);
    		    DataInputStream in = new DataInputStream(new FileInputStream(f1));
                int len1 = (int) f1.length();
                byte[] RData = new byte[len1];
                in.readFully(RData);
                in.close();
     
                String[] arrfname = fullname.replace("\\","\t").split("\t");
                String SendFileName = "FileSend:" + arrfname[arrfname.length - 1] + "                                                                                                     ";
                SendFileName = SendFileName.substring(0,100);
        		String lencmd = String.format("%08dFILESNDSVR", RData.length + 100);
                dos.write(lencmd.getBytes());
                dos.write(SendFileName.getBytes());
                dos.write(RData);
                dos.flush();
     
        	}catch(Exception e){ }
        }
        
        private void setThreadSleep(int time)
        {
       	    try{
                Thread.sleep(time);
            } catch(InterruptedException e) {}
        } 

    }
 
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        new TCheckPanelRemote();
	}
 
}

 