package Manager.DataModel;

import javax.swing.table.DefaultTableModel;
 
public class myTableModel extends DefaultTableModel
{
	   private boolean[] editcols = new boolean[100];
       myTableModel(String[] columnNames, String[][] dataValues)
       {
           super(dataValues,columnNames);
       }
       public boolean isCellEditable(int row,int cols)
       {
    	   if(editcols[cols] == true) return true;
           return false;                                                                                    
       }
       public void setEditableCol(int colidx, boolean flag)
       {
    	   editcols[colidx] = flag;
       }
}  
