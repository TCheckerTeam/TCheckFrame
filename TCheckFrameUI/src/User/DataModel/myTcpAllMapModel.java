package User.DataModel;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
  
public class myTcpAllMapModel extends AbstractTreeTableModel {  
  
    private String[] titles = {"No","종별코드","거래코드","수행시간", "전송전문","응답전문"};  
    private boolean[] editablelist = new boolean[titles.length];
    public myTcpAllMapModel(DefaultMutableTreeNode root) {  
        super(root);  
        for(int i=0;i < editablelist.length ;i++) editablelist[i] = false;
        editablelist[3] = true;  //입력데이타 컬럼은 무조건 true
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
        if (arg0 instanceof myTcpAllMapComm) {  
        	myTcpAllMapComm data = (myTcpAllMapComm) arg0;  
            if (data != null) {  
                switch (arg1) {  
	                case 0: return data.getNo();
	                case 1: return data.getKind();
	                case 2: return data.getTx();
	                case 3: return data.getTime();
	                case 4: return data.getSend();
	                case 5: return data.getRecv();
                }  
            }  
  
        }  
  
        if (arg0 instanceof DefaultMutableTreeNode) {  
            DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) arg0;  
            myTcpAllMapComm data = (myTcpAllMapComm) dataNode.getUserObject();  
            if (data != null) {  
                switch (arg1) {  
	                case 0: return data.getNo();
	                case 1: return data.getKind();
	                case 2: return data.getTx();
	                case 3: return data.getTime();
	                case 4: return data.getSend();
	                case 5: return data.getRecv();
                }  
            }  
  
        }  
        return null;  
    }  
    @Override
	public void setValueAt(Object value, Object node, int col) {
    	if (node instanceof DefaultMutableTreeNode) {
  	         DefaultMutableTreeNode dataNode = (DefaultMutableTreeNode) node;  
	         myTcpAllMapComm data = (myTcpAllMapComm) dataNode.getUserObject(); 
 
	         if (data != null && value != null) {
	        	 
	             switch (col) {  
	                case 0: data.setNo     ((String)value); break;
	                case 1: data.setKind   ((String)value); break;
	                case 2: data.setTx     ((String)value); break;
	                case 3: data.setTime   ((String)value); break;
	                case 4: data.setSend   ((String)value); break;
	                case 5: data.setRecv   ((String)value); break;
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
    	if (column > 0) return true;
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
