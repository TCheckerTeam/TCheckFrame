package User.DataModel;

public class myTcpAllMapComm {
	String No;
    String Kind;
    String Tx;
    String Send;
    String Recv;
    String Time;
    
    String Eng;
    boolean isRoot;
    String ArrayCntName;
 
    
    public myTcpAllMapComm( 
    		String No,
    	    String Kind,
    	    String Tx,
    	    String Time,
    	    String Send,
    	    String Recv,

    	    boolean isLeaf) 
    {  
    	this.No     = No  ;
    	this.Kind   = Kind ;
    	this.Tx     = Tx   ;
    	this.Send   = Send ;
    	this.Recv   = Recv ;
    	this.Time   = Time ;
    	this.isRoot = isLeaf;  
    }

    public String getNo  ()  { return No  ; }
    public String getKind()  { return Kind; }
    public String getTx  ()  { return Tx  ; }
    public String getSend()  { return Send; }
    public String getRecv()  { return Recv; }
    public String getTime()  { return Time; }
    
    public void setNo    (String param0) { this.No     = param0; }
    public void setKind  (String param0) { this.Kind   = param0; }
    public void setTx    (String param0) { this.Tx     = param0; }
    public void setSend  (String param0) { this.Send   = param0; }
    public void setRecv  (String param0) { this.Recv   = param0; }
    public void setTime  (String param0) { this.Time   = param0; }
}
