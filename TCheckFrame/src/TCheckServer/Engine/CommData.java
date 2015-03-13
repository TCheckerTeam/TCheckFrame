package TCheckServer.Engine;

import TCheckServer.DBMS.DBManager;
import TCheckServer.Util.TCheckerLog;

public class CommData {
	private TCheckerLog   tcheckerlog = null;
	private DBManager     dbmanager   = null;
 
	private String APPL_CODE      = "";     //업무코드
	private String LU_NAME        = "";     //PortNo
	private String CONNECT_TYPE   = "";     //Connect Type
	private String STA_TYPE       = "";     //1:사용, 2:미사용
	private String COMM_HEAD_TYPE = "";     //Header Type
	private String COMM_HEAD_SIZE = "";     //Header Size
	private String LEN_TYPE       = "";     //통신길이 Type
	private String LEN_OFFST      = "";     //통신길이 Offset
	private String LEN_SIZE       = "";     //통신길이 Size
	private String KIND_OFFSET    = "";     //통신종별 Offset
	private String KIND_LEN       = "";     //통신종별 Size
	private String TX_OFFSET      = "";     //통신거래 Offset
	private String TX_LEN         = "";     //통신거래 Size  
	private String UID_OFFSET     = "";     //통신UID Offset
	private String UID_LEN        = "";     //통신UID Size  
	private String KindTxGagam    = "";
	private String RESP_LU_NAME   = "";     //응답수신 포트
	private String RESP_CONNECT_TYPE = "";  //응답수신 포트의 Connect Type
	private String RESP_KINDCODE = "";     //응답종별코드
	private String[] URL_LIST        = null;//InBound URLInfo List
	private boolean EBXML_GUBUN   = false;
	
	private String   CPAID        = "";
	private String   CPAFrom      = "";
	private String   CPATo        = "";
	private String   CPASERVCE    = "";
    private String   GongUInfo    = "";
    
	public void SetTCheckerLog(TCheckerLog parm0){ tcheckerlog = parm0; }
	public TCheckerLog GetTCheckerLog() {return tcheckerlog;}
	
	public void SetDBManager(DBManager parm0){ dbmanager = parm0; }
	public DBManager GetDBManager() {return dbmanager;}
 
    //lineinfo
	public void SetAPPL_CODE     (String parm0){ APPL_CODE      = parm0; }
	public void SetLU_NAME       (String parm0){ LU_NAME        = parm0; }
	public void SetCONNECT_TYPE  (String parm0){ CONNECT_TYPE   = parm0; }
	public void SetSTA_TYPE      (String parm0){ STA_TYPE       = parm0; }
    public void SetCOMM_HEAD_TYPE(String parm0){ COMM_HEAD_TYPE = parm0; }
	public void SetCOMM_HEAD_SIZE(String parm0){ COMM_HEAD_SIZE = parm0; }
	public void SetLEN_TYPE      (String parm0){ LEN_TYPE       = parm0; }
	public void SetLEN_OFFST     (String parm0){ LEN_OFFST      = parm0; }
	public void SetLEN_SIZE      (String parm0){ LEN_SIZE       = parm0; }
    public void SetKIND_OFFSET   (String parm0){ KIND_OFFSET    = parm0; }
	public void SetKIND_LEN      (String parm0){ KIND_LEN       = parm0; }
	public void SetTX_OFFSET     (String parm0){ TX_OFFSET      = parm0; }
	public void SetTX_LEN        (String parm0){ TX_LEN         = parm0; }
	public void SetUID_OFFSET    (String parm0){ UID_OFFSET     = parm0; }
    public void SetUID_LEN       (String parm0){ UID_LEN        = parm0; }
    public void SetRESP_LU_NAME  (String parm0){ RESP_LU_NAME   = parm0; }
    public void SetRESP_CONNECT_TYPE(String parm0){ RESP_CONNECT_TYPE = parm0; }
    public void SetURL_LIST      (String[] parm0){ URL_LIST   = parm0; }
    public void SetEBXML_GUBUN   (boolean parm0){ EBXML_GUBUN   = parm0; }
	public void SetCPAID         (String parm0){ CPAID       = parm0; }
	public void SetCPAFrom       (String parm0){ CPAFrom     = parm0; }
    public void SetCPATo         (String parm0){ CPATo       = parm0; }
    public void SetCPASERVCE     (String parm0){ CPASERVCE   = parm0; }
    public void SetGongUInfo     (String parm0){ GongUInfo   = parm0; }
    public void SetRESP_KINDCODE(String parm0){ RESP_KINDCODE = parm0; }
    public void setKindTxGagam(String param0) { this.KindTxGagam = param0; }
    
	public String GetAPPL_CODE     () {return APPL_CODE     ;}
	public String GetLU_NAME       () {return LU_NAME       ;}
	public String GetCONNECT_TYPE  () {return CONNECT_TYPE  ;}
	public String GetSTA_TYPE      () {return STA_TYPE      ;}
	public String GetCOMM_HEAD_TYPE() {return COMM_HEAD_TYPE;}
	public String GetCOMM_HEAD_SIZE() {return COMM_HEAD_SIZE;}
	public String GetLEN_TYPE      () {return LEN_TYPE      ;}
	public String GetLEN_OFFST     () {return LEN_OFFST     ;}
	public String GetLEN_SIZE      () {return LEN_SIZE      ;}
	public String GetKIND_OFFSET   () {return KIND_OFFSET   ;}
	public String GetKIND_LEN      () {return KIND_LEN      ;}
	public String GetTX_OFFSET     () {return TX_OFFSET     ;}
	public String GetTX_LEN        () {return TX_LEN        ;}
	public String GetUID_OFFSET    () {return UID_OFFSET    ;}
	public String GetUID_LEN       () {return UID_LEN       ;}
	public String GetRESP_LU_NAME  () {return RESP_LU_NAME  ;}
	public String GetRESP_CONNECT_TYPE() {return RESP_CONNECT_TYPE    ;}
	public String[] GetURL_LIST    () {return URL_LIST      ;}
	public boolean GetEBXML_GUBUN  () {return EBXML_GUBUN   ;}
	public String GetCPAID      () {return CPAID      ;}
	public String GetCPAFrom    () {return CPAFrom    ;}
	public String GetCPATo      () {return CPATo      ;}
	public String GetCPASERVCE  () {return CPASERVCE  ;}
	public String GetGongUInfo  () {return GongUInfo  ;}
	public String GetRESP_KINDCODE() {return RESP_KINDCODE  ;}
	public String getKindTxGagam() { return KindTxGagam ; }
}
