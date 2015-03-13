package anylinklicense.views;


import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import anylinklicense.Activator;
 
  
/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class LicenseView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "anylinkexceptionappl.views.LicenseView";

	private TableViewer viewer;
	private Action action_load;
	private Action action_save;
 
	
	String[] EnvItems = new String[12];

	/*
	 * The content provider class is responsible for
	 * providing objects to the view. It can wrap
	 * existing objects in adapters or simply return
	 * objects as-is. These objects may be sensitive
	 * to the current input of the view, or ignore
	 * it and always show the same content 
	 * (like Task List, for example).
	 */
	 
	@Override
	public String getTitle() {
	 
		return "";
	}
 
	public LicenseView() {
	}
  
	public void createPartControl(Composite parent) {
 
	    GridLayout layout = new GridLayout(3, false);
	    parent.setLayout(layout);
		
	    makeToolBarsAction();	
	    makeActions();
	 
		// TableViewer�� �ۼ��Ͽ�,  Composite parent �� �����Ѵ�.
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		
		// TableViewer�� �÷��� �ۼ��ϰ�, Composite parent �� �����Ѵ�.
		MakeCreateColumns(parent, viewer);
		
		// viewer�� Table�� ����� ������ ���̰� �Ѵ�.
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// table�� ������ ä�ﶧ, Array�� ä��� �ֵ��� ArrayContentProvider�� �����Ͽ� �����Ѵ�.
		viewer.setContentProvider(new ArrayContentProvider());
 
		// Make the selection available to other views
		getSite().setSelectionProvider(viewer);
		
		// Layout the viewer : Table Layout�� �� ���� ���̰� �Ѵ�.
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
 
		viewer.getControl().setLayoutData(gridData);
		
		// Table Column�� Click�Ǹ�, Editable�� �� �ֵ��� Listner�� �����Ѵ�.
		/* Add Listener - Start */
	 
		table.addListener(SWT.MouseDown , new Listener() {
			   public void handleEvent(Event event) {
				    Table table = viewer.getTable();
				    Rectangle clientArea = table.getClientArea();
				    Point pt = new Point(event.x, event.y);
				    int index = table.getTopIndex();
	
				    final TableEditor editor = new TableEditor(table);
				    editor.horizontalAlignment = SWT.LEFT;
				    editor.grabHorizontal = true;
				    
				    while (index < table.getItemCount()) {
				          boolean visible = false;
				          final TableItem item = table.getItem(index);
				          for (int i = 0; i < table.getColumnCount(); i++) {
				        	   
				               Rectangle rect = item.getBounds(i);
				               
				               // 0 ~ 8 �÷��� 8�� �÷��� Edit�� �� �ֵ��� ������.
				               if (rect.contains(pt) && (i == 1)) {
				                   bindTextCell(editor,item,i);
				                   return;
				               }
					           if (!visible && rect.intersects(clientArea)) {
					               visible = true;
					           }		
				          }
  
				          if (!visible) return;
				          index++;
				    }
			   }
			  });

			 /* Add Listener - End */	
		contributeToActionBars();
		hookContextMenu();
		InitViewer();
	 
	}
	 
	private void makeToolBarsAction() {
		   IViewSite viewSite = getViewSite();
		   IActionBars actionBars = viewSite.getActionBars();
		   IToolBarManager toolBarManager = actionBars.getToolBarManager();
 	 
	} 
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				LicenseView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}
	
	private void fillContextMenu(IMenuManager manager) {

	}
	 public void bindTextCell(final TableEditor editor,final TableItem item,final int columnIndex)
	 {
		  final Text text = new Text(viewer.getTable(), SWT.NONE);
	 
		  Listener textListener = new Listener() {
				   public void handleEvent(final Event e) {
					    switch (e.type) {
					    case SWT.FocusOut:
						     item.setText(columnIndex, text.getText());
						     for(int i = 0 ; i < viewer.getTable().getItemCount();i++){
						    	 TableItem tmpitem = viewer.getTable().getItem(i);
						    	 if (tmpitem.getText(0).equals(item.getText(0))) {
						    		 if (columnIndex == 1) ((LicenseInfo)viewer.getTable().getItem(i).getData()).setItemValue(text.getText());
						    		 break;
						    	 }
						     }

							 text.dispose();

						     break;
					    case SWT.Traverse:
						     switch (e.detail) {
						     case SWT.TRAVERSE_RETURN:

							     item.setText(columnIndex, text.getText());
							     for(int i = 0 ; i < viewer.getTable().getItemCount();i++){
							    	 TableItem tmpitem = viewer.getTable().getItem(i);
							    	 if (tmpitem.getText(0).equals(item.getText(0))) {
							    		 if (columnIndex == 1) ((LicenseInfo)viewer.getTable().getItem(i).getData()).setItemValue(text.getText());
							    		 break;
							    	 }
							     }
									 
						      // FALL THROUGH
						     case SWT.TRAVERSE_ESCAPE:
							      System.out.print("bindTextCell SWT.Traverse   TRAVERSE_ESCAPE : " + text.getText() + "\n");
							      text.dispose();
							      e.doit = false;
					     }
					     break;
					    }
		          }
		  };
  
		  text.addListener(SWT.FocusOut, textListener);
		  text.addListener(SWT.Traverse, textListener);
		  editor.setEditor(text, item, columnIndex);
		  text.setText(item.getText(columnIndex));
		  text.selectAll();
		  text.setFocus();
	 }
  
	// This will create the columns for the table
	private void MakeCreateColumns(final Composite parent, final TableViewer viewer) {
		String[] titles = { "�׸��", "Value"};
		int[] bounds = { 200, 500};
 

		// ������
		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				LicenseInfo p = (LicenseInfo) element;
				return p.getItemName();
			}
		});

		// ��������
		col = createTableViewerColumn(titles[1], bounds[1], 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				LicenseInfo p = (LicenseInfo) element;
				return p.getItemValue();
			}
		});
	 
		 
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
	 
		column.setAlignment(SWT.LEFT);
		return viewerColumn;
	}
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		
		bars.getToolBarManager().add(action_load);
		bars.getToolBarManager().add(action_save);
 
	}
	private void InitViewer()
	{
		List<LicenseInfo> MsgList = new ArrayList<LicenseInfo>();
		MsgList.add(new LicenseInfo("��ǰ��", ""));
		MsgList.add(new LicenseInfo("Version", ""));
	    MsgList.add(new LicenseInfo("(D)emo/(R)ealese ����", ""));
	    MsgList.add(new LicenseInfo("�ùķ����� ����IP", ""));
	    MsgList.add(new LicenseInfo("��ܰ� ����IP", ""));
	    MsgList.add(new LicenseInfo("Demo ������", ""));
		MsgList.add(new LicenseInfo("�����", ""));
		MsgList.add(new LicenseInfo("������Ʈ��", ""));
	    MsgList.add(new LicenseInfo("�����", ""));
	    MsgList.add(new LicenseInfo("����ó", ""));
 
		viewer.getTable().clearAll();
		viewer.setInput(MsgList);	
	}
	private void makeActions() {
 
		//Load
		action_load = new Action() {
			public void run() {
				try {
					List<LicenseInfo> MsgList = new ArrayList<LicenseInfo>();
					
					File dataDir = new File("./LicenseCreate/License.dat" );
	    			DataInputStream inn = new DataInputStream(new FileInputStream(dataDir));
	 	  		    int len = (int) dataDir.length();
		  		    byte buf[] = new byte[len];
		  		    inn.readFully(buf);
		  		    inn.close();
		  		    
		  		    byte[] Data1 = hexToByteArray(new String(buf));
		  		    byte[] Data2 = hexToByteArray(new String(Data1));
		  	        String Data = new String(Data2);
		  	        String[] arrData = Data.split("\n");
		  	        
		  	    
		  	      
		  	        for(int i=0;i < arrData.length ;i++){
		  	        	String[] arrTmp = arrData[i].split("\t");
		  	         
		  	        	if (arrTmp.length == 1) {
		  	        		MsgList.add(new LicenseInfo(arrTmp[0],""));
		  	        	}
		  	        	else {
		  	        	    MsgList.add(new LicenseInfo(arrTmp[0],arrTmp[1]));
		  	        	}
		  	        }
		 
					viewer.getTable().clearAll();
					viewer.setInput(MsgList);	
				}catch(IOException e){}
				 
			}
		};
 
		action_load.setToolTipText("ȯ�� ���� Load");
		action_load.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/refresh.gif"));
 
		//����
		action_save = new Action() {
			public void run() {
 
				 try{
					   String saveData = "";
					   String Encrypt = "";
 
				       for(int i=0;i < viewer.getTable().getItemCount();i++) {
						   TableItem tmp = viewer.getTable().getItem(i);
 
						   String ItemName  = ((LicenseInfo)tmp.getData()).getItemName(); 
						   String ItemValue  = ((LicenseInfo)tmp.getData()).getItemValue(); 
						   saveData = saveData + ItemName + "\t" + ItemValue + "\n";
				       }
				       for(int i=0;i < saveData.getBytes().length ;i++){
				    	   Encrypt = Encrypt + String.format("%02X", saveData.getBytes()[i]);
				       }
 
				       BufferedWriter bw = new BufferedWriter(new FileWriter("./LicenseCreate/License.dat"));
				       for(int i=0;i < Encrypt.getBytes().length;i++) {
				    	   bw.write(String.format("%02X", Encrypt.getBytes()[i]));
				       }
				       
				       bw.close();
				 }catch(IOException e){}
 				   				 
			}
		};
 
		action_save.setToolTipText("���̼��� ����");
		action_save.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_SAVE_EDIT));
	 
		 
	}
   public  byte[] hexToByteArray(String hex)
    {

        int i = 0;

        hex = hex.replace(" ","");
        hex = hex.replace("-","");
        hex = hex.replace("\n","");
        
        if (hex == null || hex.length() == 0 ) return null;
        byte[] chk = hex.getBytes();
        for(i = 0;i < chk.length; i++) {
            if (  !((chk[i] >= '0' && chk[i] <= '9') || (chk[i] >= 'a' && chk[i] <= 'f') || (chk[i] >= 'A' && chk[i] <= 'F')) )
            {
                  System.out.println("HexData:" + i + ":" + hex);
                  return null;
            }
        }

        byte[] ba = new byte[hex.length()/2];

        for(i = 0;i < ba.length; i++) {
            ba[i] = (byte)Integer.parseInt(hex.substring(i*2,i*2+2),16);
        }
        return ba;
    }
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	private void showMessage(String message) 
	{
		MessageDialog.openInformation( viewer.getControl().getShell(), "���̼���", message);
		
	}
	 
}