package Manager.DataModel;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
  
public class myTxDetailModel extends AbstractTreeTableModel {  
  
    private String[] titles = {"종별코드","종별명칭","거래코드","거래코드명","거래구분","매핑구분","키옵셋1","키길이1","비교값1","키옵셋2","키길이2","비교값2","키옵셋3","키길이3","비교값3","응답종별코드" };  
    public myTxDetailModel(DefaultMutableTreeNode root) {  
        super(root);  
    }  
  
    /** 
     * Table Columns 
     */  
    @Override  
    public String getColumnName(int column) {  
        if (column < titles.length) {  
            return (String) titles[column];  
        } else {  
            return "";  
        }  
    }  
  
    public int getColumnCount() {  
        return titles.length;  
    }  
  
    @Override  
    public Class getColumnClass(int column) {  
        return String.class;  
    }  
  
    public Object getValueAt(Object arg0, int arg1) {  
        if (arg0 instanceof myTxDetailComm) {  
        	myTxDetailComm data = (myTxDetailComm) arg0;  
            if (data != null) {  
                switch (arg1) {  
	                case 0:                       
	                    return data.getKindCode();
	                case 1:                       
	                    return data.getKindName();
	                case 2:                       
	                    return data.getTxCode  ();
	                case 3:                       
	                    return data.getTxName  ();
	                case 4:                       
	                    return data.getTxGubun ();
	                case 5:                       
	                    return data.getMapGubun();
	  
	                case 6:                       
	                    return data.getKeyOffset1();
	                case 7:                       
	                    return data.getKeyLen1();
	                case 8:                       
	                    return data.getKeyVal1();
	                case 9:                       
	                    return data.getKeyOffset2();
	                case 10:                       
	                    return data.getKeyLen2();
	                case 11:                       
	                    return data.getKeyVal2();
	                case 12:                       
	                    return data.getKeyOffset3();
	                case 13:                       
	                    return data.getKeyLen3();
	                case 14:                       
	                    return data.getKeyVal3();
	                case 15:                       
	                    return data.getResKindCode();
	                    
                }  
            }  
  
        }  
  
        if (arg0 instanceof DefaultMutableTreeNode) {  
            DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) arg0;  
            myTxDetailComm data = (myTxDetailComm) dataNode.getUserObject();  
            if (data != null) {  
                switch (arg1) {  
	                case 0:                       
	                    return data.getKindCode();
	                case 1:                       
	                    return data.getKindName();
	                case 2:                       
	                    return data.getTxCode  ();
	                case 3:                       
	                    return data.getTxName  ();
	                case 4:                       
	                    return data.getTxGubun ();
	                case 5:                       
	                    return data.getMapGubun();
	                case 6:                       
	                    return data.getKeyOffset1();
	                case 7:                       
	                    return data.getKeyLen1();
	                case 8:                       
	                    return data.getKeyVal1();
	                case 9:                       
	                    return data.getKeyOffset2();
	                case 10:                       
	                    return data.getKeyLen2();
	                case 11:                       
	                    return data.getKeyVal2();
	                case 12:                       
	                    return data.getKeyOffset3();
	                case 13:                       
	                    return data.getKeyLen3();
	                case 14:                       
	                    return data.getKeyVal3();
	                case 15:                       
	                    return data.getResKindCode();
                }  
            }  
  
        }  
        return null;  
    }  
    @Override
	public void setValueAt(Object value, Object node, int col) {
    	if (node instanceof DefaultMutableTreeNode) {
  	         DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) node;  
	         myTxDetailComm data = (myTxDetailComm) dataNode.getUserObject(); 
 
	         if (data != null && value != null) {
	        	 
	             switch (col) {  
	                case 0: data.setKindCode((String)value); break;
	                case 1: data.setKindName((String)value); break;
	                case 2: data.setTxCode  ((String)value); break;
	                case 3: data.setTxName  ((String)value); break;
	                case 4: data.setTxGubun ((String)value); break;
	                case 5: data.setMapGubun((String)value); break;
	                case 6: data.setKeyOffset1 ((String)value); break;
	                case 7: data.setKeyLen1    ((String)value); break;
	                case 8: data.setKeyVal1    ((String)value); break;
	                case 9: data.setKeyOffset2 ((String)value); break;
	                case 10: data.setKeyLen2   ((String)value); break;
	                case 11: data.setKeyVal2   ((String)value); break;
	                case 12: data.setKeyOffset3((String)value); break;
	                case 13: data.setKeyLen3   ((String)value); break;
	                case 14: data.setKeyVal3   ((String)value); break;
	                case 15: data.setResKindCode   ((String)value); break;
                }                 
	         }                      
                                  
    	}
	 
	}
    public Object getChild(Object arg0, int arg1) {  
  
        if (arg0 instanceof DefaultMutableTreeNode) {  
            DefaultMutableTreeNode nodes = (DefaultMutableTreeNode) arg0;  
            return nodes.getChildAt(arg1);  
        }  
        return null;  
    }  
    @Override
    public boolean isCellEditable(Object node, int column) {
    	if (column >= 4) return true;
    	return false;
    }
  
    public int getChildCount(Object arg0) {  
  
        if (arg0 instanceof DefaultMutableTreeNode) {  
            DefaultMutableTreeNode nodes = (DefaultMutableTreeNode) arg0;  
            return nodes.getChildCount();  
        }  
        return 0;  
    }  
  
    public int getIndexOfChild(Object arg0, Object arg1) {  
        // TODO Auto-generated method stub  
        return 0;  
    }  
  
    @Override  
    public boolean isLeaf(Object node) {  
        return getChildCount(node) == 0;  
    }  
}  
