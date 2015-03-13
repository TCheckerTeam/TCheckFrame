package Manager.DataModel;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
  
public class myResMapModel extends AbstractTreeTableModel {  
  
    private String[] titles = {"요청업무","요청종별","요청거래","응답업무","응답종별","응답거래","응답포트" };  
    public myResMapModel(DefaultMutableTreeNode root) {  
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
        if (arg0 instanceof myLineInfoComm) {  
        	myResMapComm data = (myResMapComm) arg0;  
            if (data != null) {  
                switch (arg1) {  
	                case 0:  
	                    return data.getReqApplName ();
	                case 1:                           
	                    return data.getReqKindName ();
	                case 2:                           
	                    return data.getReqTxName   ();
	                case 3:                           
	                    return data.getResApplName ();
	                case 4:                           
	                    return data.getResKindName ();
	                case 5:                           
	                    return data.getResTxName   ();
	                case 6:                           
	                    return data.getPontNo      ();
                }  
            }  
  
        }  
  
        if (arg0 instanceof DefaultMutableTreeNode) {  
            DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) arg0;  
            myResMapComm data = (myResMapComm) dataNode.getUserObject();  
            if (data != null) {  
                switch (arg1) {  
	                case 0:  
	                    return data.getReqApplName ();
	                case 1:                           
	                    return data.getReqKindName ();
	                case 2:                           
	                    return data.getReqTxName   ();
	                case 3:                           
	                    return data.getResApplName ();
	                case 4:                           
	                    return data.getResKindName ();
	                case 5:                           
	                    return data.getResTxName   ();
	                case 6:                           
	                    return data.getPontNo      ();
                }  
            }  
  
        }  
        return null;  
    }  
    @Override
	public void setValueAt(Object value, Object node, int col) {
    	if (node instanceof DefaultMutableTreeNode) {
  	         DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) node;  
  	         myResMapComm data = (myResMapComm) dataNode.getUserObject(); 
 
	         if (data != null && value != null) {
	        	 
	             switch (col) {  
	                case 0: data.setReqApplName((String)value); break;
	                case 1: data.setReqKindName((String)value); break;
	                case 2: data.setReqTxName  ((String)value); break;
	                case 3: data.setResApplName((String)value); break;
	                case 4: data.setResKindName((String)value); break;
	                case 5: data.setResTxName  ((String)value); break;
	                case 6: data.setPontNo     ((String)value); break;
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
    	if (column >= 2) return true;
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
