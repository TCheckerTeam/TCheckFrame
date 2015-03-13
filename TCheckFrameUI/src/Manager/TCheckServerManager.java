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
	 
		//메뉴별 Class 설정
		myclassLine = new TCheckPanelLine(myPaneLine);
		myclassUser = new TCheckPanelUser(myPaneUser);
		myclassResMap = new TCheckPanelResMap(myPaneResMap);
		myclassTxDetail = new TCheckPanelTxDetail(myPaneTxDetail);
		 
		
        //Menu Item 설정
        InitMenu();
        
		//Panel의 ZOrder 설정
		SetScreen(0);
		
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
		this.setSize(500,500);
 
		this.setVisible(true);
		this.setExtendedState(MAXIMIZED_BOTH);
 	
	}//END 생성자 ====================
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
		//메뉴바 작성 ----------------------------------------
		//MenuBar/Menu/MenuItem 변수 선언
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
 
		//MenuBar를 작성하여 프레임에 설정
		myMenuBar = new JMenuBar();
		myMenuBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		setJMenuBar(myMenuBar);  //프레임이름.setJMenubar(메뉴바이름) 프레임에 메뉴바 삽입

		//File Menu의 작성과 구성------------------------------
		myMgrMenu = new JMenu("환경설정(&F)");
		//myRemoteMenu = new JMenu("원격지원");
		myHelpMenu = new JMenu("도움말");
		myExitMenu = new JMenu("프로그램종료");

		//니모닉 키를 설정
		myMgrMenu.setMnemonic('F');
 
 	
		//회선정보 메뉴아이템
		myMgrLine = new JMenuItem("회선정보", new ImageIcon("./Image/line.png"));
		myMgrMenu.add(myMgrLine);
		myMgrLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				SetScreen(0);
				TCheckServerManager.this.setTitle(myTitle + " - 회선정보") ;
				myWorkDisply.setText(">>>>>>>>>>>> 회선정보 <<<<<<<<<<<<<");
			}
		});

		// In/Out업무 거래설정 메뉴아이템
		myMgrAsync = new JMenuItem("거래정보", new ImageIcon("./Image/txdetail.png"));
		myMgrMenu.add(myMgrAsync);
		myMgrAsync.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				SetScreen(1);
				TCheckServerManager.this.setTitle(myTitle + " - 거래정보") ;
				myWorkDisply.setText(">>>>>>>>>>>> 거래정보 <<<<<<<<<<<<<");
			}
		});
 
		//세퍼레이터 추가 ; 구분짓는선
		myMgrMenu.addSeparator();
	 
		//응답매핑 거래설정 메뉴아이템
		myMgrAsync = new JMenuItem("응답매핑", new ImageIcon("./Image/resmap.gif"));
		myMgrMenu.add(myMgrAsync);
		myMgrAsync.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				SetScreen(2);
				TCheckServerManager.this.setTitle(myTitle + " - 응답매핑") ;
				myWorkDisply.setText(">>>>>>>>>>>> 응답매핑 <<<<<<<<<<<<<");
			}
		});
 
		//세퍼레이터 추가 ; 구분짓는선
		myMgrMenu.addSeparator();
		
		//사용자정보 메뉴아이템
		myMgrUser = new JMenuItem("사용자", new ImageIcon("./Image/user.gif"));
		myMgrMenu.add(myMgrUser);
		myMgrUser.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				SetScreen(3);
				TCheckServerManager.this.setTitle(myTitle + " - 사용자") ;
				myWorkDisply.setText(">>>>>>>>>>>> 사용자정보 <<<<<<<<<<<<<");
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
 
				arrcmd[1] = "./Manual/TCheckerFrame_운영자매뉴얼_V1.0.0.pdf";
 
				try {
				    ps = rt.exec(arrcmd);
				} catch (Exception e) {
				    e.printStackTrace();
				}

			}
		});
 
		//세퍼레이터 추가 ; 구분짓는선
		myMgrMenu.addSeparator();
		
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
		//myMenuBar.add(myRemoteMenu);
		myMenuBar.add(myHelpMenu);
		myMenuBar.add(myExitMenu);
		
		//선택 메뉴명 보여주기
		Font font = new Font("바탕체",Font.BOLD,14);
		myMenuBar.add(new JLabel("                                  "));
		myWorkDisply = new JLabel(">>>>>>>>>>>> 회선정보 <<<<<<<<<<<<<");
		myWorkDisply.setFont(font);
		myWorkDisply.setForeground(Color.BLUE);
		myMenuBar.add(myWorkDisply);
 
		//메뉴바 작성 끝 --------------------------------------
		SetScreen(0);
		TCheckServerManager.this.setTitle(myTitle + " - 회선정보") ;
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


 