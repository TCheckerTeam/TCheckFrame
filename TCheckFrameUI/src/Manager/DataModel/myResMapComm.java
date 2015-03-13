package Manager.DataModel;

public class myResMapComm {
    String ReqApplName;
    String ReqKindName;
    String ReqTxName;
    String ResApplName;
    String ResKindName;
    String ResTxName;
    String PontNo;
    boolean isRoot;
    
    public myResMapComm(
    		String ReqApplName, 
    		String ReqKindName, 
    		String ReqTxName,   
    		String ResApplName, 
    		String ResKindName, 
    		String ResTxName,   
    		String PontNo, 
    	    boolean isLeaf) 
    {  
    	this.ReqApplName = ReqApplName ;
    	this.ReqKindName = ReqKindName ;
    	this.ReqTxName   = ReqTxName   ;
    	this.ResApplName = ResApplName ;
    	this.ResKindName = ResKindName ;
    	this.ResTxName   = ResTxName   ;
    	this.PontNo      = PontNo      ;
    	this.isRoot = isLeaf;  
    }

    public String getReqApplName() { return ReqApplName; }
    public String getReqKindName() { return ReqKindName; }
    public String getReqTxName  () { return ReqTxName  ; }
    public String getResApplName() { return ResApplName; }
    public String getResKindName() { return ResKindName; }
    public String getResTxName  () { return ResTxName  ; }
    public String getPontNo     () { return PontNo     ; }
 

    public void setReqApplName (String param0) { this.ReqApplName = param0; }
    public void setReqKindName (String param0) { this.ReqKindName = param0; }
    public void setReqTxName   (String param0) { this.ReqTxName   = param0; }
    public void setResApplName (String param0) { this.ResApplName = param0; }
    public void setResKindName (String param0) { this.ResKindName = param0; }
    public void setResTxName   (String param0) { this.ResTxName   = param0; }
    public void setPontNo      (String param0) { this.PontNo      = param0; }
 
}
