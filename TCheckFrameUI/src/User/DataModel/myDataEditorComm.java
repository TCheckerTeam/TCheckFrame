package User.DataModel;

public class myDataEditorComm {
    String No;	
    String Kor;
    String Eng;
    String Type;
    String Len;
 
    boolean isRoot;
    String ArrayCntName;
 
    
    public myDataEditorComm( 
    	    String No,	
    	    String Kor,
    	    String Eng,
    	    String Type,
    	    String Len,
    	    boolean isLeaf) 
    {  
    	this.No     = No  ;
    	this.Kor    = Kor  ;
    	this.Eng    = Eng   ;
    	this.Type   = Type ;
    	this.Len    = Len    ;
    	this.isRoot = isLeaf;  
    }

    public String getNo()    { return No; }
    public String getKor()   { return Kor; }
    public String getEng()  { return Eng; }
    public String getType()  { return Type; }
    public String getLen()    { return Len; }
 
    public void setNo     (String param0) { this.No   = param0; }
    public void setKor    (String param0) { this.Kor   = param0; }
    public void setEng    (String param0) { this.Eng  = param0; }
    public void setType   (String param0) { this.Type = param0; }
    public void setLen    (String param0) { this.Len   = param0; }
}
