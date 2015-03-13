package Manager.DataModel;

import javax.swing.JComboBox;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
  
public class myLineInfoModel extends AbstractTreeTableModel {   
    private JXTreeTable myPaneLineTable;
    private String[] titles = {"업무코드","업무명","포트번호","연결방식", "헤더Type","헤더크기","길이설정방법","길이시작","길이Size","사용" };
 
    public myLineInfoModel(DefaultMutableTreeNode root ) {  
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
    	if (column == 6) return JComboBox.class;
        return String.class;  
    }  
  
    public Object getValueAt(Object arg0, int arg1) {  
        if (arg0 instanceof myLineInfoComm) {  
        	myLineInfoComm data = (myLineInfoComm) arg0;  
            if (data != null) {  
            	System.out.println("getValueAt-1 : " + data.getUserYN());
                switch (arg1) {  
	                case 0:  
	                    return data.getApplCode();   
	                case 1:                       
	                    return data.getApplName();   
	                case 2:                       
	                    return data.getPortNo();     
	                case 3:                       
	                    return data.getConnMethod(); 
	                case 4:                       
	                    return data.getHeaderType(); 
	                case 5:                       
	                    return data.getHeaderSize(); 
	                case 6:                       
	                    return data.getLenMethod();  
	                case 7:                       
	                    return data.getLenOffset();  
	                case 8:                       
	                    return data.getLenSize();    
	                case 9:                       
	                    return data.getUserYN();     
                }  
            }  
  
        }  
   
        if (arg0 instanceof DefaultMutableTreeNode) {  
            DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) arg0;  
            myLineInfoComm data = (myLineInfoComm) dataNode.getUserObject();  
            if (data != null) {  
                switch (arg1) {  
	                case 0:  
	                    return data.getApplCode();   
	                case 1:                       
	                    return data.getApplName();   
	                case 2:                       
	                    return data.getPortNo();     
	                case 3:                       
	                    return data.getConnMethod(); 
	                case 4:                       
	                    return data.getHeaderType(); 
	                case 5:                       
	                    return data.getHeaderSize(); 
	                case 6:                       
	                    return data.getLenMethod();  
	                case 7:                       
	                    return data.getLenOffset();  
	                case 8:                       
	                    return data.getLenSize();    
	                case 9:                      
	                	
	                    return data.getUserYN();     
                } 
                
            }  
  
        }  
        
        return null;  
    }  
    
    @Override
	public void setValueAt(Object value, Object node, int col) {
    	if (node instanceof DefaultMutableTreeNode) {
  	         DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) node;  
	         myLineInfoComm data = (myLineInfoComm) dataNode.getUserObject(); 
 
	         if (data != null && value != null) {
	        	 
	             switch (col) {  
	                case 0:  
	                    data.setApplCode((String)value);  
	                    break;
	                case 1:                       
	                    data.setApplName((String)value); 
	                    break;
	                case 2:                       
	                    data.setPortNo((String)value);
	                    break;
	                case 3:                       
	                    data.setConnMethod((String)value);
	                    break;
	                case 4:                       
	                    data.setHeaderType((String)value);
	                    break;
	                case 5:                       
	                    data.setHeaderSize((String)value);
	                    break;
	                case 6:                       
	                    data.setLenMethod((String)value);
	                    break;
	                case 7:                       
	                    data.setLenOffset((String)value);
	                    break;
	                case 8:                       
	                    data.setLenSize((String)value);
	                    break;
	                case 9:       
	                	data.setUserYN((String)value);
	                    break;

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
    	if (column >= 6) return true;
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

