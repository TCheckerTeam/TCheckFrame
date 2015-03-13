package Manager.DataModel;

public class myLineInfoComm { 
	private String ApplCode;
	private String ApplName;
	private String PortNo;
	private String ConnMethod;
	private String HeaderType;
	private String HeaderSize;
	private String LenMethod;
	private String LenOffset;
	private String LenSize;
	private String UserYN;
	private boolean isRoot;
    
    public myLineInfoComm(
    		String ApplCode,
    		String ApplName,
    		String PortNo,
    		String ConnMethod,
    		String HeaderType,
    		String HeaderSize,
    		String LenMethod,
    		String LenOffset,
    		String LenSize,
    		String UserYN,
    		boolean isLeaf) 
    {  
    	this.ApplCode     = ApplCode    ;
    	this.ApplName     = ApplName    ;
    	this.PortNo       = PortNo      ;
    	this.ConnMethod   = ConnMethod  ;
    	this.HeaderType   = HeaderType  ;
    	this.HeaderSize   = HeaderSize  ;
    	this.LenMethod    = LenMethod   ;
    	this.LenOffset    = LenOffset   ;
    	this.LenSize      = LenSize     ;
    	this.UserYN       = UserYN      ;
        this.isRoot = isLeaf;  
    }

    public String getApplCode   () { return ApplCode    ; }
    public String getApplName   () { return ApplName    ; }
    public String getPortNo     () { return PortNo      ; }
    public String getConnMethod () { return ConnMethod  ; }
    public String getHeaderType () { return HeaderType  ; }
    public String getHeaderSize () { return HeaderSize  ; }
    public String getLenMethod  () { return LenMethod   ; }
    public String getLenOffset  () { return LenOffset   ; }
    public String getLenSize    () { return LenSize     ; }
    public String getUserYN     () { return UserYN      ; }
 
 
    public void setApplCode   (String param0) { this.ApplCode    = param0; }
    public void setApplName   (String param0) { this.ApplName    = param0; }
    public void setPortNo     (String param0) { this.PortNo      = param0; }
    public void setConnMethod (String param0) { this.ConnMethod  = param0; }
    public void setHeaderType (String param0) { this.HeaderType  = param0; }
    public void setHeaderSize (String param0) { this.HeaderSize  = param0; }
    public void setLenMethod  (String param0) { this.LenMethod   = param0; }
    public void setLenOffset  (String param0) { this.LenOffset   = param0; }
    public void setLenSize    (String param0) { this.LenSize     = param0; }
    public void setUserYN     (String param0) { this.UserYN      = param0; }

    
    public String toString()
    {
        return ApplName;
    }
}
