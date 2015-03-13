package Manager.DataModel;

public class myUserPermitComm {
    String ApplName;
    String KindName;
    String TxName;
    String ApplCode;
    String KindCode;
    String TxCode;
    String PermitFlag;
    boolean isRoot;
    
    public myUserPermitComm(String ApplCode, String ApplName, 
    		                String KindCode, String KindName, 
    		                String TxCode, String TxName, 
    		                String PermitFlag, 
    		                boolean isLeaf) {  
    	         this.ApplName = ApplName;  
    	         this.KindName = KindName;  
    	         this.TxName = TxName;  
    	         this.ApplCode = ApplCode;  
    	         this.KindCode = KindCode;  
    	         this.TxCode = TxCode;  
    	         this.PermitFlag = PermitFlag;  
    	         this.isRoot = isLeaf;  
    }

    
    public String getApplName  () { return ApplName  ; }
    public String getKindName  () { return KindName  ; }
    public String getTxName    () { return TxName    ; }
    public String getApplCode  () { return ApplCode  ; }
    public String getKindCode  () { return KindCode  ; }
    public String getTxCode    () { return TxCode    ; }
    public String getPermitFlag() { return PermitFlag; }

    public void setApplName  (String param0) { this.ApplName   = param0; }
    public void setKindName  (String param0) { this.KindName   = param0; }
    public void setTxName    (String param0) { this.TxName     = param0; }
    public void setApplCode  (String param0) { this.ApplCode   = param0; }
    public void setKindCode  (String param0) { this.KindCode   = param0; }
    public void setTxCode    (String param0) { this.TxCode     = param0; }
    
    public void setPermitFlag(String param0) { this.PermitFlag = param0; }

    public String toString()
    {
        return ApplName;
    }
}
