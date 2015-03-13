package User.DataModel;

public class myTcpMapComm {
    String Item;
    String Type;
    String Len;
    String Conts;
    String No;
    String Eng;
    boolean isRoot;
    String ArrayCntName;
 
    
    public myTcpMapComm( 
    		String Item,
    	    String Type,
    	    String Len,
    	    String Conts,
    	    String No,
    	    String Eng,
    	    boolean isLeaf) 
    {  
    	this.Item   = Item  ;
    	this.Type   = Type  ;
    	this.Len    = Len   ;
    	this.Conts  = Conts ;
    	this.No     = No    ;
    	this.Eng    = Eng   ;
    	this.isRoot = isLeaf;  
    }

    public String getItem()  { return Item; }
    public String getType()  { return Type; }
    public String getLen()   { return Len; }
    public String getConts() { return Conts.replace("<NODATA>", ""); }
    public String getNo()  { return No; }
    public String getArrayCntName() { return ArrayCntName; }
    public String getEng()  { return Eng; }
    
    public void setItem   (String param0) { this.Item   = param0; }
    public void setType   (String param0) { this.Type   = param0; }
    public void setLen    (String param0) { this.Len    = param0; }
    public void setConts  (String param0) { this.Conts  = param0; }
    public void setNo     (String param0) { this.No     = param0; }
    public void setArrayCntName  (String param0) { this.ArrayCntName  = param0; }
    public void setEng     (String param0) { this.Eng     = param0; }
}
