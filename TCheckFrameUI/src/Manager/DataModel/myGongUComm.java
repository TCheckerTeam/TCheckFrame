package Manager.DataModel;

public class myGongUComm { 
	String PortNo; 
	String ApplCode;
	String StartOffset;
	String StartSize;
	String Bigo1;
	String Bigo2;
	String Bigo3;
	String Bigo4;
	String Bigo5;
	String BaseYN;
	 
    boolean isRoot;
    
    public myGongUComm(	
    		String PortNo, 
    		String ApplCode,
    		String StartOffset,
    		String StartSize,
    		String Bigo1,
    		String Bigo2,
    		String Bigo3,
    		String Bigo4,
    		String Bigo5,
    		String BaseYN,
    		boolean isLeaf) 
    {  
    	this.PortNo      = PortNo      ;
    	this.ApplCode    = ApplCode    ;
    	this.StartOffset = StartOffset ;
    	this.StartSize   = StartSize   ;
    	this.Bigo1       = Bigo1       ;
    	this.Bigo2       = Bigo2       ;
    	this.Bigo3       = Bigo3       ;
    	this.Bigo4       = Bigo4       ;
    	this.Bigo5       = Bigo5       ;
    	this.BaseYN      = BaseYN      ;

    	this.isRoot = isLeaf;  
    }

    public String getPortNo      () { return PortNo       ; }
    public String getApplCode    () { return ApplCode     ; }
    public String getStartOffset () { return StartOffset  ; }
    public String getStartSize   () { return StartSize    ; }
    public String getBigo1       () { return Bigo1        ; }
    public String getBigo2       () { return Bigo2        ; }
    public String getBigo3       () { return Bigo3        ; }
    public String getBigo4       () { return Bigo4        ; }
    public String getBigo5       () { return Bigo5        ; }
    public String getBaseYN      () { return BaseYN       ; }
   
 
    public void setPortNo      (String param0) { this.PortNo      = param0; }
    public void setApplCode    (String param0) { this.ApplCode    = param0; }
    public void setStartOffset (String param0) { this.StartOffset = param0; }
    public void setStartSize   (String param0) { this.StartSize   = param0; }
    public void setBigo1       (String param0) { this.Bigo1       = param0; }
    public void setBigo2       (String param0) { this.Bigo2       = param0; }
    public void setBigo3       (String param0) { this.Bigo3       = param0; }
    public void setBigo4       (String param0) { this.Bigo4       = param0; }
    public void setBigo5       (String param0) { this.Bigo5       = param0; }
    public void setBaseYN      (String param0) { this.BaseYN      = param0; }
 
    public String toString()
    {
        return PortNo;
    }
}
