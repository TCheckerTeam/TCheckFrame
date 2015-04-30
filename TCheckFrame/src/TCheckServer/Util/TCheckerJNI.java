package TCheckServer.Util;
public class TCheckerJNI {
    static 
    {
        System.loadLibrary("TCheckerJNI");
    }
 
    public static native String SendMapper(String pInstCode,String pApplCode, String pKindCode, String pTxCode, 
                                           String pMapperName, String arrpRecvData);
 
    public String FuncStringToHex(String data)
    {
       String retstr = "";
       byte[] workdata = data.getBytes();
       for(int i=0;i < workdata.length;i++)
       {
           retstr = retstr + String.format("%02X",workdata[i]);
       }
       return retstr;
    }
    
    public String FuncHexToString(String data)
    {
    
        int i = 0;
        if (data == null || data.length() == 0 ) return null;
    
        byte[] ba = new byte[data.length()/2];
    
        for(i = 0;i < ba.length; i++) {
            ba[i] = (byte)Integer.parseInt(data.substring(i*2,i*2+2),16);
        }
        return new String(ba);
    }
}
