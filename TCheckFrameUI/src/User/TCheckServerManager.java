package User;
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
	private JPanel myPaneTCP, myPaneTCPALL, myPaneSOAP, myPaneRemote;
	private TCheckPanelTCP myclassTcp = null;
	private TCheckPanelTCPALL myclassTcpAll = null;
	private TCheckPanelSOAP myclassUrl = null;
	private TCheckPanelRemote myclassRemote = null;
	private JLabel myWorkDisply ;
	private String gUserID = "";
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
		myPaneTCP    = new JPanel(); 
		myPaneSOAP   = new JPanel();
		myPaneRemote = new JPanel();
		myPaneTCPALL = new JPanel(); 
 
		myPaneTCP.setBounds(0, 0, rect.width, rect.height - 50 );myPaneTCP.setBackground(Color.WHITE);
		myPaneSOAP.setBounds(0, 0, rect.width, rect.height);myPaneSOAP.setBackground(Color.WHITE);
		myPaneRemote.setBounds(0, 0, rect.width, rect.height);myPaneRemote.setBackground(Color.WHITE);
		myPaneTCPALL.setBounds(0, 0, rect.width, rect.height);myPaneTCPALL.setBackground(Color.WHITE);
		
		myContainer.add(myPaneTCP, BorderLayout.CENTER,0);
		myContainer.add(myPaneSOAP, BorderLayout.CENTER,1);
		myContainer.add(myPaneRemote, BorderLayout.CENTER,2);
		myContainer.add(myPaneTCPALL, BorderLayout.CENTER,3);
		 
 
		//�޴��� Class ����
		myclassTcp = new TCheckPanelTCP(myPaneTCP, gUserID);
		myclassUrl = new TCheckPanelSOAP(myPaneSOAP, gUserID);
		myclassRemote = new TCheckPanelRemote(myPaneRemote);
		myclassTcpAll = new TCheckPanelTCPALL(myPaneTCPALL, gUserID);
		
		//Panel�� ZOrder ����
		SetScreen(0); 
 
        //Menu Item ����
        InitMenu();
 

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
		this.setSize(1024,730);
		this.setVisible(true);
		this.setExtendedState(MAXIMIZED_BOTH);
  
	}//END ������ ====================
	
   
	private void SetScreen(int idx)
	{
		gCurrentIdx = idx;
		
		myPaneTCP.setVisible(false);
		myPaneSOAP.setVisible(false);
		myPaneRemote.setVisible(false);
		myPaneTCPALL.setVisible(false);
		
		if (idx == 0) {
			myContainer.setComponentZOrder(myPaneTCP, 0);
			myContainer.setComponentZOrder(myPaneSOAP, 1);
			myContainer.setComponentZOrder(myPaneRemote, 2);
			myContainer.setComponentZOrder(myPaneTCPALL, 3);
			myPaneTCP.setVisible(true);
		}
		if (idx == 1) {
			myContainer.setComponentZOrder(myPaneTCP, 1);
			myContainer.setComponentZOrder(myPaneSOAP, 0);
			myContainer.setComponentZOrder(myPaneRemote, 2);
			myContainer.setComponentZOrder(myPaneTCPALL, 3);
			myPaneSOAP.setVisible(true);
		}
		if (idx == 2) {
			myContainer.setComponentZOrder(myPaneTCP, 2);
			myContainer.setComponentZOrder(myPaneSOAP, 1);
			myContainer.setComponentZOrder(myPaneRemote, 0);
			myContainer.setComponentZOrder(myPaneTCPALL, 3);
			myPaneRemote.setVisible(true);
 
		}
		if (idx == 3) {
			myContainer.setComponentZOrder(myPaneTCP, 2);
			myContainer.setComponentZOrder(myPaneSOAP, 1);
			myContainer.setComponentZOrder(myPaneRemote, 3);
			myContainer.setComponentZOrder(myPaneTCPALL, 0);
			myPaneTCPALL.setVisible(true);
 
		}
	}
	private void InitMenu()
	{
		//�޴��� �ۼ� ----------------------------------------
		//MenuBar/Menu/MenuItem ���� ����
		JMenuBar myMenuBar ;
		JMenu myMgrMenu;
		JMenu myRemoteMenu;
		JMenu myHelpMenu;
		JMenu myExitMenu;
		
		
		JMenuItem myMgrTCP  ; 
		JMenuItem myMgrSOAP ; 
		JMenuItem myRemoteStart ;
		JMenuItem myMgrTCPALL ; 
		JMenuItem myRemoteStop ;
		JMenuItem myExitMItem ; 
		JMenuItem myHelpMItem ; 
		
		//MenuBar�� �ۼ��Ͽ� �����ӿ� ����
		myMenuBar = new JMenuBar();
		myMenuBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		setJMenuBar(myMenuBar);  //�������̸�.setJMenubar(�޴����̸�) �����ӿ� �޴��� ����

		//File Menu�� �ۼ��� ����------------------------------
		myMgrMenu = new JMenu("�����׽�Ʈ");
		myRemoteMenu = new JMenu("��������");
		myHelpMenu = new JMenu("����");
		myExitMenu = new JMenu("���α׷�����");

		//�ϸ�� Ű�� ����
		//myMgrMenu.setMnemonic('F');
 
		//TCP ���� �޴�������
		myMgrTCP = new JMenuItem("TCP ����(�ŷ���)", new ImageIcon("./Image/tcp.png"));
		myMgrMenu.add(myMgrTCP);
		myMgrTCP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				SetScreen(0);
				TCheckServerManager.this.setTitle(myTitle + " - TCP ����(�ŷ���)") ;
				myWorkDisply.setText(">>>>>>>>>>>> TCP ����(�ŷ���)  <<<<<<<<<<<<<");
			}
		});
  
		//TCPALL ���� �޴�������
		myMgrTCPALL = new JMenuItem("TCP ����(������)", new ImageIcon("./Image/tcp.png"));
		myMgrMenu.add(myMgrTCPALL);
		myMgrTCPALL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				SetScreen(3);
				TCheckServerManager.this.setTitle(myTitle + " - TCP ����(������)") ;
				myWorkDisply.setText(">>>>>>>>>>>> TCP ����(������)  <<<<<<<<<<<<<");
			}
		});
		
 
		//SOAP ���� �޴�������
		myMgrSOAP = new JMenuItem("SOAP ����", new ImageIcon("./Image/soap.png"));
		myMgrMenu.add(myMgrSOAP);
		myMgrSOAP.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				SetScreen(1);
				TCheckServerManager.this.setTitle(myTitle + " - SOAP ����") ;
				myWorkDisply.setText(">>>>>>>>>>>> SOAP ����  <<<<<<<<<<<<<");
			}
		});
 
		//�������� �޴������� 
		myRemoteStart = new JMenuItem("�������� ����",new ImageIcon("./Image/remotestart.png"));
		myRemoteMenu.add(myRemoteStart);
		myRemoteStart.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				SetScreen(2);
				myclassRemote.myPaneRemoteInit();
				TCheckServerManager.this.setTitle(myTitle + " - ��������") ;
				myWorkDisply.setText(">>>>>>>>>>>> �������� <<<<<<<<<<<<<");
			}
		});
		
		myRemoteStop = new JMenuItem("�������� ����",new ImageIcon("./Image/remotestop.png"));
		myRemoteMenu.add(myRemoteStop);
		myRemoteStop.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				SetScreen(2);
				myclassRemote.myPaneRemoteClose();
				TCheckServerManager.this.setTitle(myTitle + " - ��������") ;
				myWorkDisply.setText(">>>>>>>>>>>> �������� <<<<<<<<<<<<<");
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
 
				arrcmd[1] = "./Manual/TCheckerFrame_����ڸŴ���_V1.0.0.pdf";
 
				try {
				    ps = rt.exec(arrcmd);
				} catch (Exception e) {
				    e.printStackTrace();
				}
			}
		});
 
		
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
		myMenuBar.add(myRemoteMenu);
		myMenuBar.add(myHelpMenu);
		myMenuBar.add(myExitMenu);
 
		//���� �޴��� �����ֱ�
		Font font = new Font("����ü",Font.BOLD,14);
		myMenuBar.add(new JLabel("                                   "));
		myWorkDisply = new JLabel(">>>>>>>>>>>> TCP ���� <<<<<<<<<<<<<");
		myWorkDisply.setFont(font);
		myWorkDisply.setForeground(Color.BLUE);
		myMenuBar.add(myWorkDisply);
 
		//�޴��� �ۼ� �� --------------------------------------
		SetScreen(0);
		TCheckServerManager.this.setTitle(myTitle + " - TCP ����") ;
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
