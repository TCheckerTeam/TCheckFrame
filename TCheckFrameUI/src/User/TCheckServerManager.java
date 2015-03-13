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
	
	//MyEditor 클래스의 생성자를 정의 
	public TCheckServerManager(String title) {

		GraphicsEnvironment ee = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gd = ee.getScreenDevices();
		Rectangle rect = ee.getMaximumWindowBounds();
        
		this.myTitle = title;
		this.setTitle(title);
		this.setExtendedState(MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 

		//프레임의 컨텐트페인을 얻어서 컨테이너 객체에 대입
		myContainer = this.getContentPane();
		myContainer.setLayout(new BorderLayout());
		myContainer.setBackground(Color.WHITE);
		
		
		//메뉴별 메인 판넬를 생성함.
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
		 
 
		//메뉴별 Class 설정
		myclassTcp = new TCheckPanelTCP(myPaneTCP, gUserID);
		myclassUrl = new TCheckPanelSOAP(myPaneSOAP, gUserID);
		myclassRemote = new TCheckPanelRemote(myPaneRemote);
		myclassTcpAll = new TCheckPanelTCPALL(myPaneTCPALL, gUserID);
		
		//Panel의 ZOrder 설정
		SetScreen(0); 
 
        //Menu Item 설정
        InitMenu();
 

		//Look & Feel 설정
		try{
			//Java스타일의 Look&Feel 로 설정
			UIManager.setLookAndFeel( "javax.swing.plaf.metal.MetalLookAndFeel");
			
			//설정을 반영
			SwingUtilities.updateComponentTreeUI(this);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//프레임의 크기를 정의 하여 표시
		this.setSize(1024,730);
		this.setVisible(true);
		this.setExtendedState(MAXIMIZED_BOTH);
  
	}//END 생성자 ====================
	
   
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
		//메뉴바 작성 ----------------------------------------
		//MenuBar/Menu/MenuItem 변수 선언
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
		
		//MenuBar를 작성하여 프레임에 설정
		myMenuBar = new JMenuBar();
		myMenuBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		setJMenuBar(myMenuBar);  //프레임이름.setJMenubar(메뉴바이름) 프레임에 메뉴바 삽입

		//File Menu의 작성과 구성------------------------------
		myMgrMenu = new JMenu("전문테스트");
		myRemoteMenu = new JMenu("원격지원");
		myHelpMenu = new JMenu("도움말");
		myExitMenu = new JMenu("프로그램종료");

		//니모닉 키를 설정
		//myMgrMenu.setMnemonic('F');
 
		//TCP 전문 메뉴아이템
		myMgrTCP = new JMenuItem("TCP 전문(거래별)", new ImageIcon("./Image/tcp.png"));
		myMgrMenu.add(myMgrTCP);
		myMgrTCP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				SetScreen(0);
				TCheckServerManager.this.setTitle(myTitle + " - TCP 전문(거래별)") ;
				myWorkDisply.setText(">>>>>>>>>>>> TCP 전문(거래별)  <<<<<<<<<<<<<");
			}
		});
  
		//TCPALL 전문 메뉴아이템
		myMgrTCPALL = new JMenuItem("TCP 전문(업무별)", new ImageIcon("./Image/tcp.png"));
		myMgrMenu.add(myMgrTCPALL);
		myMgrTCPALL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				SetScreen(3);
				TCheckServerManager.this.setTitle(myTitle + " - TCP 전문(업무별)") ;
				myWorkDisply.setText(">>>>>>>>>>>> TCP 전문(업무별)  <<<<<<<<<<<<<");
			}
		});
		
 
		//SOAP 전문 메뉴아이템
		myMgrSOAP = new JMenuItem("SOAP 전문", new ImageIcon("./Image/soap.png"));
		myMgrMenu.add(myMgrSOAP);
		myMgrSOAP.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				SetScreen(1);
				TCheckServerManager.this.setTitle(myTitle + " - SOAP 전문") ;
				myWorkDisply.setText(">>>>>>>>>>>> SOAP 전문  <<<<<<<<<<<<<");
			}
		});
 
		//원격지원 메뉴아이템 
		myRemoteStart = new JMenuItem("원격지원 시작",new ImageIcon("./Image/remotestart.png"));
		myRemoteMenu.add(myRemoteStart);
		myRemoteStart.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				SetScreen(2);
				myclassRemote.myPaneRemoteInit();
				TCheckServerManager.this.setTitle(myTitle + " - 원격지원") ;
				myWorkDisply.setText(">>>>>>>>>>>> 원격지원 <<<<<<<<<<<<<");
			}
		});
		
		myRemoteStop = new JMenuItem("원격지원 중지",new ImageIcon("./Image/remotestop.png"));
		myRemoteMenu.add(myRemoteStop);
		myRemoteStop.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				SetScreen(2);
				myclassRemote.myPaneRemoteClose();
				TCheckServerManager.this.setTitle(myTitle + " - 원격지원") ;
				myWorkDisply.setText(">>>>>>>>>>>> 원격지원 <<<<<<<<<<<<<");
			}
		});
		
		
		
		//도움말 메뉴아이템 
		myHelpMItem = new JMenuItem("도움말",new ImageIcon("./Image/help.png"));
		myHelpMenu.add(myHelpMItem);
		myHelpMItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				String pdfexename = GetRegister("TCHECKER_PDF");
				if (pdfexename == null || pdfexename.trim().equals("")) {
					JFileChooser file = new JFileChooser();
					file.setDialogTitle("Acrobat 실행화일을 선택하세요");
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
 
				arrcmd[1] = "./Manual/TCheckerFrame_사용자매뉴얼_V1.0.0.pdf";
 
				try {
				    ps = rt.exec(arrcmd);
				} catch (Exception e) {
				    e.printStackTrace();
				}
			}
		});
 
		
		//Exit 메뉴아이템 
		myExitMItem = new JMenuItem("Exit",new ImageIcon("./Image/close.gif"));
		myExitMenu.add(myExitMItem);
		myExitMItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				System.exit(0);
			}
		});

		//FileMenu를 MenuBar에 편성
		myMenuBar.add(myMgrMenu);
		myMenuBar.add(myRemoteMenu);
		myMenuBar.add(myHelpMenu);
		myMenuBar.add(myExitMenu);
 
		//선택 메뉴명 보여주기
		Font font = new Font("바탕체",Font.BOLD,14);
		myMenuBar.add(new JLabel("                                   "));
		myWorkDisply = new JLabel(">>>>>>>>>>>> TCP 전문 <<<<<<<<<<<<<");
		myWorkDisply.setFont(font);
		myWorkDisply.setForeground(Color.BLUE);
		myMenuBar.add(myWorkDisply);
 
		//메뉴바 작성 끝 --------------------------------------
		SetScreen(0);
		TCheckServerManager.this.setTitle(myTitle + " - TCP 전문") ;
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
