package User.DataModel;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
  
public class myDataEditorModel extends AbstractTreeTableModel {  
  
    private String[] titles = {"No", "한글명", "영문명","타입","길이"};  
    private boolean[] editablelist = new boolean[titles.length];
    public myDataEditorModel(DefaultMutableTreeNode root) {  
        super(root);  
        for(int i=0;i < editablelist.length ;i++) editablelist[i] = true;
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
        if (arg0 instanceof myDataEditorComm) {  
        	myDataEditorComm data = (myDataEditorComm) arg0;  
            if (data != null) {  
                switch (arg1) {  
                    case 0: return data.getNo();
	                case 1: return data.getKor();
	                case 2: return data.getEng();
	                case 3: return data.getType();
	                case 4: return data.getLen();
                }  
            }  
        }  
  
        if (arg0 instanceof DefaultMutableTreeNode) {  
            DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) arg0;  
            myDataEditorComm data = (myDataEditorComm) dataNode.getUserObject();  
            if (data != null) {  
                switch (arg1) {  
	                case 0: return data.getNo();
	                case 1: return data.getKor();
	                case 2: return data.getEng();
	                case 3: return data.getType();
	                case 4: return data.getLen();
                }  
            }  
  
        }  
        return null;  
    }  
    @Override
	public void setValueAt(Object value, Object node, int col) {
    	if (node instanceof DefaultMutableTreeNode) {
  	         DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) node;  
	         myDataEditorComm data = (myDataEditorComm) dataNode.getUserObject(); 
 
	         if (data != null && value != null) {
	        	 
	             switch (col) {  
	                case 0: data.setNo   ((String)value); break;
	                case 1: data.setKor   ((String)value); break;
	                case 2: data.setEng  ((String)value); break;
	                case 3: data.setType ((String)value); break;
	                case 4: data.setLen  ((String)value); break;
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
    	if (column == 3) return true;
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
