package Manager.DataModel;

import javax.swing.tree.DefaultMutableTreeNode;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
  
public class myUserPermitModel extends AbstractTreeTableModel {  
  
    private String[] titles = {"업무코드","업무명", "종별코드","종별코드명", "거래코드","거래코드명", "권한(Y/N)"};  
  
    public myUserPermitModel(DefaultMutableTreeNode root) {  
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
        if (arg0 instanceof myUserPermitComm) {  
        	myUserPermitComm data = (myUserPermitComm) arg0;  
            if (data != null) {  
                switch (arg1) {  
	                case 0:  
	                    return data.getApplCode();  
	                case 1:  
	                    return data.getApplName();  
	                case 2:  
	                    return data.getKindCode();  
                    case 3:  
                        return data.getKindName();  
                    case 4:  
                        return data.getTxCode();  
                    case 5:  
                        return data.getTxName();  
                    case 6:  
                        return data.getPermitFlag();  
                }  
            }  
  
        }  
  
        if (arg0 instanceof DefaultMutableTreeNode) {  
            DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) arg0;  
            myUserPermitComm data = (myUserPermitComm) dataNode.getUserObject();  
            if (data != null) {  
                switch (arg1) {  
	                case 0:  
	                    return data.getApplCode();  
	                case 1:  
	                    return data.getApplName();  
	                case 2:  
	                    return data.getKindCode();  
	                case 3:  
	                    return data.getKindName();  
	                case 4:  
	                    return data.getTxCode();  
	                case 5:  
	                    return data.getTxName();  
	                case 6:  
	                    return data.getPermitFlag();  
                }  
            }  
  
        }  
        return null;  
    }  
  
    @Override
	public void setValueAt(Object value, Object node, int col) {
    	if (node instanceof DefaultMutableTreeNode) {
  	         DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) node;  
	         myUserPermitComm data = (myUserPermitComm) dataNode.getUserObject(); 
 
	         if (data != null && value != null) {
	        	 
	             switch (col) {  
	                case 0: data.setApplCode  ((String)value); break;
	                case 1: data.setApplName  ((String)value); break;
	                case 2: data.setKindCode  ((String)value); break;
	                case 3: data.setKindName  ((String)value); break;
	                case 4: data.setTxCode    ((String)value); break;
	                case 5: data.setTxName    ((String)value); break;
	                case 6: data.setPermitFlag((String)value); break;
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
        return column == 6;
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
