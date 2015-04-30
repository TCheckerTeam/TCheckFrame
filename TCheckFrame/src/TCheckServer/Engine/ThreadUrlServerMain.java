package TCheckServer.Engine;

import java.net.ServerSocket;
import java.net.Socket;
 
public class ThreadUrlServerMain extends Thread{ 
	private CommData         COMMDATA  = null;
    private ServerSocket     server    = null;
	public ThreadUrlServerMain(CommData commdata)
    { 
    	COMMDATA = commdata;
     }
    public void run()
    {
		/*----------------- 회선 Listener --------------------------------*/
		while(!Thread.currentThread().isInterrupted()) {
			/*--------------------- Port Listener 실행 ---------------------------*/
	    	try {
                if (server == null) server = new ServerSocket(Integer.parseInt(COMMDATA.GetLU_NAME()));
                Socket client = server.accept();
                ThreadUrlServer thread = new ThreadUrlServer(COMMDATA, client);
                thread.start();   
 
	    	}catch(Exception e) {}
		}
	}
  
}
