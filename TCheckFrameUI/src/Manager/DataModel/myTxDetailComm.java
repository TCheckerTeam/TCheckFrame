package Manager.DataModel;

public class myTxDetailComm {
    String KindCode;
    String KindName;
    String TxCode;
    String TxName;
    String TxGubun;
    String MapGubun;
    String KeyOffset1;
    String KeyLen1;
    String KeyVal1;
    String KeyOffset2;
    String KeyLen2;
    String KeyVal2;
    String KeyOffset3;
    String KeyLen3;
    String KeyVal3;
    String ResKindCode;
    boolean isRoot;
    
    public myTxDetailComm(    
    		String KindCode,
    	    String KindName,
    	    String TxCode,
    	    String TxName,
    	    String TxGubun,
    	    String MapGubun,
    	    String KeyOffset1,
    	    String KeyLen1,
    	    String KeyVal1,
    	    String KeyOffset2,
    	    String KeyLen2,
    	    String KeyVal2,
    	    String KeyOffset3,
    	    String KeyLen3,
    	    String KeyVal3,
    	    String ResKindCode,
    	    boolean isLeaf) 
    {  
    	this.KindCode  = KindCode ;
    	this.KindName  = KindName ;
    	this.TxCode    = TxCode   ;
    	this.TxName    = TxName   ;
    	this.TxGubun   = TxGubun  ;
    	this.MapGubun  = MapGubun ;
    	
    	this.KeyOffset1 = KeyOffset1 ;
    	this.KeyLen1    = KeyLen1 ;
    	this.KeyVal1    = KeyVal1 ;
    	this.KeyOffset2 = KeyOffset2 ;
    	this.KeyLen2    = KeyLen2 ;
    	this.KeyVal2    = KeyVal2 ;
    	this.KeyOffset3 = KeyOffset3 ;
    	this.KeyLen3    = KeyLen3 ;
    	this.KeyVal3    = KeyVal3 ;
    	this.ResKindCode    = ResKindCode ;
    	this.isRoot = isLeaf;  
    }

    public String getKindCode() { return KindCode; }
    public String getKindName() { return KindName; }
    public String getTxCode  () { return TxCode  ; }
    public String getTxName  () { return TxName  ; }
    public String getTxGubun () { return TxGubun ; }
    public String getMapGubun() { return MapGubun; }
    public String getKeyOffset1() { return KeyOffset1; }
    public String getKeyLen1()    { return KeyLen1; }
    public String getKeyVal1()    { return KeyVal1; }
    public String getKeyOffset2() { return KeyOffset2; }
    public String getKeyLen2()    { return KeyLen2; }
    public String getKeyVal2()    { return KeyVal2; }
    public String getKeyOffset3() { return KeyOffset3; }
    public String getKeyLen3()    { return KeyLen3; }
    public String getKeyVal3()    { return KeyVal3; }
    public String getResKindCode()    { return ResKindCode; }

    public void setKindCode  (String param0) { this.KindCode   = param0; }
    public void setKindName  (String param0) { this.KindName   = param0; }
    public void setTxCode    (String param0) { this.TxCode     = param0; }
    public void setTxName    (String param0) { this.TxName     = param0; }
    public void setTxGubun   (String param0) { this.TxGubun    = param0; }
    public void setMapGubun  (String param0) { this.MapGubun   = param0; }
    public void setKeyOffset1(String param0) { this.KeyOffset1 = param0; }
    public void setKeyLen1   (String param0) { this.KeyLen1    = param0; }
    public void setKeyVal1   (String param0) { this.KeyVal1    = param0; }
    public void setKeyOffset2(String param0) { this.KeyOffset2 = param0; }
    public void setKeyLen2   (String param0) { this.KeyLen2    = param0; }
    public void setKeyVal2   (String param0) { this.KeyVal2    = param0; }
    public void setKeyOffset3(String param0) { this.KeyOffset3 = param0; }
    public void setKeyLen3   (String param0) { this.KeyLen3    = param0; }
    public void setKeyVal3   (String param0) { this.KeyVal3    = param0; }
    public void setResKindCode   (String param0) { this.ResKindCode    = param0; }
}
