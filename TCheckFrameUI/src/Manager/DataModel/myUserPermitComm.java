package Manager.DataModel;

public class myUserPermitComm {
    String ApplName;
    String ApplCode;
    String PermitFlag;
    boolean isRoot;
    
    public myUserPermitComm(String ApplCode, String ApplName,  String PermitFlag, boolean isLeaf) {  
    	         this.ApplName = ApplName;  
    	         this.ApplCode = ApplCode;  
    	         this.PermitFlag = PermitFlag;  
    	         this.isRoot = isLeaf;  
    }

    
    public String getApplName  () { return ApplName  ; }
    public String getApplCode  () { return ApplCode  ; }
    public String getPermitFlag() { return PermitFlag; }

    public void setApplName  (String param0) { this.ApplName   = param0; }
    public void setApplCode  (String param0) { this.ApplCode   = param0; }
    public void setPermitFlag(String param0) { this.PermitFlag = param0; }

    public String toString()
    {
        return ApplName;
    }
}
