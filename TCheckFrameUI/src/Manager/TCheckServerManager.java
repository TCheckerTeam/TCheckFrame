 package Manager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

  
class TCheckServerManager extends JFrame
{
	private String myTitle = "";	
	private Container myContainer = null;
	private JPanel myPaneLine, myPaneUser, myPaneResMap, myPaneTxDetail ;
 
	private TCheckPanelLine myclassLine = null;
	private TCheckPanelUser myclassUser = null;
	private TCheckPanelResMap myclassResMap = null;
	private TCheckPanelTxDetail myclassTxDetail = null;
 
	private JLabel myWorkDisply ;
	private int    gCurrentIdx = 0;
 
	//MyEditor Ŭ������ �����ڸ� ���� 
	public TCheckServerManager(String title) {
 
		GraphicsEnvironment ee = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gd = ee.getScreenDevices();
		Rectangle rect = ee.getMaximumWindowBounds();
        
		this.myTitle = title;
		this.setTitle(title);
		this.setExtendedState(MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        
		//�������� ����Ʈ������ �� �����̳� ��ü�� ����
		myContainer = this.getContentPane();
		myContainer.setLayout(new BorderLayout());
		myContainer.setBackground(Color.WHITE);
		 
		
		//�޴��� ���� �ǳڸ� ������.
		myPaneLine  = new JPanel(); 
		myPaneUser  = new JPanel();
 
		myPaneResMap = new JPanel();
		myPaneTxDetail = new JPanel();
	 
 
		myPaneLine.setBounds(0, 0, rect.width, rect.height - 50 );myPaneLine.setBackground(Color.WHITE);
		myPaneUser.setBounds(0, 0, rect.width, rect.height);myPaneUser.setBackground(Color.WHITE);
		 
		myPaneResMap.setBounds(0, 0, rect.width, rect.height);myPaneResMap.setBackground(Color.WHITE);
		myPaneTxDetail.setBounds(0, 0, rect.width, rect.height - 50 );myPaneTxDetail.setBackground(Color.WHITE);
	 
		
		myContainer.add(myPaneLine, BorderLayout.CENTER,0);
		myContainer.add(myPaneTxDetail, BorderLayout.CENTER,1);
		myContainer.add(myPaneResMap, BorderLayout.CENTER,2);
		myContainer.add(myPaneUser, BorderLayout.CENTER,3);
	 
		//�޴��� Class ����
		myclassLine = new TCheckPanelLine(myPaneLine);
		myclassUser = new TCheckPanelUser(myPaneUser);
		myclassResMap = new TCheckPanelResMap(myPaneResMap);
		myclassTxDetail = new TCheckPanelTxDetail(myPaneTxDetail);
		 
		
        //Menu Item ����
        InitMenu();
        
		//Panel�� ZOrder ����
		SetScreen(0);
		
		//Look & Feel ����
		try{
			//Java��Ÿ���� Look&Feel �� ����
			UIManager.setLookAndFeel( "javax.swing.plaf.metal.MetalLookAndFeel");
			
			//������ �ݿ�
			SwingUtilities.updateComponentTreeUI(this);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//�������� ũ�⸦ ���� �Ͽ� ǥ��
		this.setSize(500,500);
 
		this.setVisible(true);
		this.setExtendedState(MAXIMIZED_BOTH);
 	
	}//END ������ ====================
	private void SetScreen(int idx)
	{
  
		myPaneLine.setVisible(false);
		myPaneTxDetail.setVisible(false);			
		myPaneResMap.setVisible(false);			
		myPaneUser.setVisible(false);
    
		if (idx == 0) {
 
			myPaneLine.setVisible(true);
 
		}
		if (idx == 1) {
 
		    myPaneTxDetail.setVisible(true);
		 
		}
		if (idx == 2) {
 
		    myPaneResMap.setVisible(true);
	 
		}
		if (idx == 3) {
 
		    myPaneUser.setVisible(true);
		}
 
		myContainer.update(myContainer.getGraphics());
	}
	
	private void InitMenu()
	{
		//�޴��� �ۼ� ----------------------------------------
		//MenuBar/Menu/MenuItem ���� ����
		JMenuBar myMenuBar ;
		JMenu myMgrMenu;
		//JMenu myRemoteMenu;
		JMenu myHelpMenu;
		JMenu myExitMenu;
		
		JMenuItem myMgrLine  ; 
		JMenuItem myMgrUser ; 
		JMenuItem myMgrAsync ; 
		//JMenuItem myRemoteStart ;
		//JMenuItem myRemoteStop ;
		JMenuItem myExitMItem ; 
		JMenuItem myHelpMItem ; 
 
		//MenuBar�� �ۼ��Ͽ� �����ӿ� ����
		myMenuBar = new JMenuBar();
		myMenuBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		setJMenuBar(myMenuBar);  //�������̸�.setJMenubar(�޴����̸�) �����ӿ� �޴��� ����

		//File Menu�� �ۼ��� ����------------------------------
		myMgrMenu = new JMenu("ȯ�漳��(&F)");
		//myRemoteMenu = new JMenu("��������");
		myHelpMenu = new JMenu("����");
		myExitMenu = new JMenu("���α׷�����");

		//�ϸ�� Ű�� ����
		myMgrMenu.setMnemonic('F');
 
 	
		//ȸ������ �޴�������
		myMgrLine = new JMenuItem("ȸ������", new ImageIcon("./Image/line.png"));
		myMgrMenu.add(myMgrLine);
		myMgrLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				SetScreen(0);
				TCheckServerManager.this.setTitle(myTitle + " - ȸ������") ;
				myWorkDisply.setText(">>>>>>>>>>>> ȸ������ <<<<<<<<<<<<<");
			}
		});

		// In/Out���� �ŷ����� �޴�������
		myMgrAsync = new JMenuItem("�ŷ�����", new ImageIcon("./Image/txdetail.png"));
		myMgrMenu.add(myMgrAsync);
		myMgrAsync.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				SetScreen(1);
				TCheckServerManager.this.setTitle(myTitle + " - �ŷ�����") ;
				myWorkDisply.setText(">>>>>>>>>>>> �ŷ����� <<<<<<<<<<<<<");
			}
		});
 
		//���۷����� �߰� ; �������¼�
		myMgrMenu.addSeparator();
	 
		//������� �ŷ����� �޴�������
		myMgrAsync = new JMenuItem("�������", new ImageIcon("./Image/resmap.gif"));
		myMgrMenu.add(myMgrAsync);
		myMgrAsync.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				SetScreen(2);
				TCheckServerManager.this.setTitle(myTitle + " - �������") ;
				myWorkDisply.setText(">>>>>>>>>>>> ������� <<<<<<<<<<<<<");
			}
		});
 
		//���۷����� �߰� ; �������¼�
		myMgrMenu.addSeparator();
		
		//��������� �޴�������
		myMgrUser = new JMenuItem("�����", new ImageIcon("./Image/user.gif"));
		myMgrMenu.add(myMgrUser);
		myMgrUser.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				SetScreen(3);
				TCheckServerManager.this.setTitle(myTitle + " - �����") ;
				myWorkDisply.setText(">>>>>>>>>>>> ��������� <<<<<<<<<<<<<");
			}
		});

 
		
		//���� �޴������� 
		myHelpMItem = new JMenuItem("����",new ImageIcon("./Image/help.png"));
		myHelpMenu.add(myHelpMItem);
		myHelpMItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				String pdfexename = GetRegister("TCHECKER_PDF");
				if (pdfexename == null || pdfexename.trim().equals("")) {
					JFileChooser file = new JFileChooser();
					file.setDialogTitle("Acrobat ����ȭ���� �����ϼ���");
					file.showSaveDialog(null);
			        File myFile = file.getSelectedFile();
 	                if (myFile != null){
 	       	    	    Preferences userRootPrefs = Preferences.userRoot();
 	   	    	        userRootPrefs.put("TCHECKER_PDF", myFile.getPath());
 	   	    	        pdfexename = myFile.getPath();
 	                }
			        
				}
				
				Runtime rt = Runtime.getRuntime();
				Process ps = null;
				String[] arrcmd = new String[2];
				arrcmd[0] = pdfexename;
 
				arrcmd[1] = "./Manual/TCheckerFrame_��ڸŴ���_V1.0.0.pdf";
 
				try {
				    ps = rt.exec(arrcmd);
				} catch (Exception e) {
				    e.printStackTrace();
				}

			}
		});
 
		//���۷����� �߰� ; �������¼�
		myMgrMenu.addSeparator();
		
		//Exit �޴������� 
		myExitMItem = new JMenuItem("Exit",new ImageIcon("./Image/close.gif"));
		myExitMenu.add(myExitMItem);
		myExitMItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				System.exit(0);
			}
		});

		//FileMenu�� MenuBar�� ��
		myMenuBar.add(myMgrMenu);
		//myMenuBar.add(myRemoteMenu);
		myMenuBar.add(myHelpMenu);
		myMenuBar.add(myExitMenu);
		
		//���� �޴��� �����ֱ�
		Font font = new Font("����ü",Font.BOLD,14);
		myMenuBar.add(new JLabel("                                  "));
		myWorkDisply = new JLabel(">>>>>>>>>>>> ȸ������ <<<<<<<<<<<<<");
		myWorkDisply.setFont(font);
		myWorkDisply.setForeground(Color.BLUE);
		myMenuBar.add(myWorkDisply);
 
		//�޴��� �ۼ� �� --------------------------------------
		SetScreen(0);
		TCheckServerManager.this.setTitle(myTitle + " - ȸ������") ;
	}
	private String GetRegister(String pKey)
    {
    	Preferences userRootPrefs = Preferences.userRoot();
    	if (userRootPrefs.get(pKey, null) != null) { 
    		return userRootPrefs.get(pKey, "");
    	}
    	return "";
    }

}


 