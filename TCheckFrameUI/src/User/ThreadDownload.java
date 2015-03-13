package User;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.prefs.Preferences;

import javax.swing.JTextArea;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.JXTreeTable;
  
public class ThreadDownload extends Thread{ 
	private Socket one_client = null;
	private DataOutputStream dos = null;
	private DataInputStream dis = null;
	private String recvdata = "";
	private String VerAnylinkIP      = "";
	private String VerAnylinkPort    = "";
	private String VerVersion        = "";
	public  String NewVerVersion     = "";
	public  boolean isDownload       = false;
	public  boolean isSkip           = false;
	public  int    CurrentTaskSize = 0;
	public  int    DownLoadFileSize = 0;
	public ThreadDownload(String pVerAnylinkIP, String pVerAnylinkPort, String pVerVersion)
    { 
		VerAnylinkIP      = pVerAnylinkIP;
		VerAnylinkPort    = pVerAnylinkPort;
		VerVersion        = pVerVersion;
		isDownload        = false;
		CurrentTaskSize   = 0;
		DownLoadFileSize  = 0;
    }
  
	public void run()
	{
		try{
    		one_client = new Socket();
	        one_client.connect(new InetSocketAddress(VerAnylinkIP, Integer.parseInt(VerAnylinkPort)), 3000);  //3초 기다림
            one_client.setSoTimeout(5000);
            
            dos = new DataOutputStream(one_client.getOutputStream());
            dis = new DataInputStream(one_client.getInputStream());
            
    		//Send
            String cmd = "UIUPGRADE                                        ";
            String SendStr = String.format("%08d", VerVersion.getBytes().length) + cmd.substring(0,32) + VerVersion;
            dos.write(SendStr.getBytes(), 0, SendStr.getBytes().length);
            dos.flush();
 
      	    //데이타부 길이정보 읽기
            int    tmplen = 0;
            byte[] tmpbyte1    = new byte[8];
            byte[] versionbyte = new byte[8];
            try{
               for(int i = 0 ; i < 8 ;i++) {
            	   tmpbyte1[i] = dis.readByte();  
               }
               tmplen = Integer.parseInt(new String(tmpbyte1));
               if (tmplen <= 8) {
            	   isSkip = true;
            	   CloseGlobalSocket(one_client, dos, dis);
		           return ;
               }
               DownLoadFileSize = tmplen - 8;
               
               for(int i = 0 ; i < 8 ;i++) {
            	   versionbyte[i] = dis.readByte();  
               }
            }catch(Exception e2){
            	isSkip = true;
            	CloseGlobalSocket(one_client, dos, dis);
                return ;
            }
            
            //Version 비교
            if (VerVersion.trim().equals(new String(versionbyte).trim())){
            	isSkip = true;
            	CloseGlobalSocket(one_client, dos, dis);
                return ;
            }
            
            //데이타부 읽기
    		FileOutputStream ffos  = null; 
            DataOutputStream fdos = null;
            try{
            	File dir = new File("./Download");
            	if (!dir.exists()) dir.mkdir();
            	
            	ffos = new FileOutputStream("./download/TCheckerUI.jar." + new String(versionbyte).trim()); 
            	fdos = new DataOutputStream(ffos);
            	byte ch = 0;
            	try{
                    for(int i = 0 ; i < DownLoadFileSize;i++) {
                    	ch = dis.readByte();
	 	            	fdos.write(ch); 
	 	            	CurrentTaskSize++;
	 	            }
 	            }catch(Exception e2){
 	            	isSkip = true;
 	            	fdos.close();
	                ffos.close();
	                CloseGlobalSocket(one_client, dos, dis);
	                return ;
 	            }
 	            
                fdos.close();
                ffos.close();
                CloseGlobalSocket(one_client, dos, dis);

                //신규버젼 셋팅
                NewVerVersion = new String(versionbyte).trim();
                FileCopy("./download/TCheckerUI.jar." + new String(versionbyte).trim(), "./download/TCheckerUI.jar");
                isDownload = true;
                
	        }catch(Exception e2){
	        	isSkip = true;
            	CloseGlobalSocket(one_client, dos, dis);
                return ;
	        }
	        
		}catch(Exception MainE){
			isSkip = true;
			CloseGlobalSocket(one_client, dos, dis);
            return ;
		}
		CloseGlobalSocket(one_client, dos, dis);
	}
 
    public void CloseGlobalSocket(Socket pone_client, DataOutputStream pdos, DataInputStream pdis)
    {
        try{ if(pone_client != null) pone_client.close();}catch(Exception e){};
        try{ if(pdos != null) pdos.close();}catch(Exception e){};
        try{ if(pdis != null) pdis.close();}catch(Exception e){};
    }
    public void FileCopy(String srcFilename, String dstFilename)
    {
         FileInputStream fis = null;
         FileOutputStream fos = null;
         BufferedInputStream bis;
         BufferedOutputStream bos;
         File source;
         File target;

         byte[] data;

         try {

             source = new File(srcFilename);
             fis = new FileInputStream(source);
             bis = new BufferedInputStream(fis);

             target = new File(dstFilename);
             fos = new FileOutputStream(target);
             bos = new BufferedOutputStream(fos);

             data = new byte[1024];

             while ((bis.read(data) > -1)) {
                 bos.write(data);
             }

             bos.flush();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } 
    }
}
