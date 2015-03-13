package TCheckServer.Engine;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
 
public class ThreadManagerMain extends Thread{ 
	private CommData COMMDATA = null;

    public ThreadManagerMain(CommData commdata)
    { 
    	COMMDATA = commdata;
    }
    
    public void run()
	{
        ServerSocket server = null;
 
 		while(true) {
            try{
 
     
                try {
                    if (server == null) server = new ServerSocket(Integer.parseInt(COMMDATA.GetLU_NAME()));
                }catch(Exception ee){}
    
                Socket client = server.accept();
 
                ThreadManagerMainSub tmpthread = new ThreadManagerMainSub(client, COMMDATA);
                tmpthread.start();		
               
                
            }catch(IOException ioe){
                ioe.printStackTrace();
                try{Thread.sleep(1000);}catch(Exception ee){}                
            } 
 		}
	}
}
