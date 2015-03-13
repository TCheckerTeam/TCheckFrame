package Manager.DataModel;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
  
public class myGongUModel extends AbstractTreeTableModel {   
  
    private String[] titles = {"포트번호","업무코드","시작위치","길이", "비교1","비교2","비교3","비교4","비교5","기본업무(Y/N)" };  
    public myGongUModel(DefaultMutableTreeNode root) {  
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
        	myGongUComm data = (myGongUComm) arg0;  
            if (data != null) {  
                switch (arg1) {  
	                case 0:  
	                    return data.getPortNo     ();
	                case 1:                          
	                    return data.getApplCode   ();
	                case 2:                          
	                    return data.getStartOffset();
	                case 3:                          
	                    return data.getStartSize  ();
	                case 4:                          
	                    return data.getBigo1      ();
	                case 5:                          
	                    return data.getBigo2      ();
	                case 6:                          
	                    return data.getBigo3      ();
	                case 7:                          
	                    return data.getBigo4      ();
	                case 8:                          
	                    return data.getBigo5      ();
	                case 9:                          
	                    return data.getBaseYN     (); 
                }  
            }  
  
        }  
  
        if (arg0 instanceof DefaultMutableTreeNode) {  
            DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) arg0;  
            myGongUComm data = (myGongUComm) dataNode.getUserObject();  
            if (data != null) {  
                switch (arg1) {  
	                case 0:  
	                    return data.getPortNo     ();
	                case 1:                          
	                    return data.getApplCode   ();
	                case 2:                          
	                    return data.getStartOffset();
	                case 3:                          
	                    return data.getStartSize  ();
	                case 4:                          
	                    return data.getBigo1      ();
	                case 5:                          
	                    return data.getBigo2      ();
	                case 6:                          
	                    return data.getBigo3      ();
	                case 7:                          
	                    return data.getBigo4      ();
	                case 8:                          
	                    return data.getBigo5      ();
	                case 9:                          
	                    return data.getBaseYN     (); 
                }  
            }  
  
        }  
        return null;  
    }  
    @Override
	public void setValueAt(Object value, Object node, int col) {
    	if (node instanceof DefaultMutableTreeNode) {
  	         DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) node;  
  	         myGongUComm data = (myGongUComm) dataNode.getUserObject(); 
 
	         if (data != null && value != null) {
	        	 
	             switch (col) {  
	                case 0: data.setPortNo     ((String)value); break;
	                case 1: data.setApplCode   ((String)value); break;
	                case 2: data.setStartOffset((String)value); break;
	                case 3: data.setStartSize  ((String)value); break;
	                case 4: data.setBigo1      ((String)value); break;
	                case 5: data.setBigo2      ((String)value); break;
	                case 6: data.setBigo3      ((String)value); break;
	                case 7: data.setBigo4      ((String)value); break;
	                case 8: data.setBigo5      ((String)value); break;
	                case 9: data.setBaseYN     ((String)value); break;
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
