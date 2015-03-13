package User;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import javax.swing.JTextArea;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.JXTreeTable;
   
public class ThreadListenerRecv extends Thread{ 
	private ServerSocket server = null;
	private String PortNo = "";
	private JTextArea      txtRecv;
	private DefaultMutableTreeNode mytcptree ;
	private JXTreeTable    myPaneTcpResTable;
	private JTextArea      txtMsgBody;
	private JTextArea      txtMsg;
	public ThreadListenerRecv(String                 pPortNo, 
			                  JTextArea              txtRecv, 
			                  DefaultMutableTreeNode mytcptree, 
			                  JXTreeTable            myPaneTcpResTable , 
			                  JTextArea              txtMsgBody,
 		                      JTextArea              txtMsg)
    { 
    	this.PortNo = pPortNo;
    	this.txtRecv = txtRecv;
    	this.mytcptree = mytcptree;
    	this.myPaneTcpResTable = myPaneTcpResTable;
    	this.txtMsgBody = txtMsgBody;
    	this.txtMsg     = txtMsg;
    }
  
	public void run()
	{
 
		while(!Thread.currentThread().isInterrupted()) {
	        try{
	        	if (server == null) server = new ServerSocket(Integer.parseInt(PortNo));
	        	Socket client = server.accept();
	        	ThreadListenerRecvSub listenrecv = new ThreadListenerRecvSub(client, txtRecv, mytcptree, myPaneTcpResTable, txtMsgBody, txtMsg);
	        	listenrecv.start();		
	        }catch(IOException e){
	        	e.printStackTrace();
	                           
	        } 
		}

	}
 
}
