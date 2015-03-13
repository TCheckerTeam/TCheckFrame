package Manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
 
class DataInsertDialog extends JDialog implements ActionListener{
	private JTextArea   txtSubTitle ;
	private JTextField  txtInData;
	private JButton btnOK, btnClose;
	private String  InData = "";
	public DataInsertDialog(int x, int y, int width, int height, String title, String subtitle) {
		this.setTitle(title);
		this.setModal(true);
		this.setLayout(new BorderLayout());
		
		//subtitle
		String[] arrSubTitle = subtitle.split("\n");
		txtSubTitle = new JTextArea();
		txtSubTitle.setLineWrap(true); //한줄이 너무 길면 자동으로 개행할지 설정
		txtSubTitle.setColumns(120); //열의 크기(가로크기)
		txtSubTitle.setRows(arrSubTitle.length); //행의 크기(세로크기)
		txtSubTitle.setText(subtitle);
		txtSubTitle.setEditable(false);
		txtSubTitle.setBackground(new Color(255,245,162));
 
		JLabel lblLabel = new JLabel("  입력값 ");
        txtInData = new JTextField();
    
 
        this.add(txtSubTitle, BorderLayout.NORTH);
        
        JPanel p1 = new JPanel();
        p1.setLayout(null);
        
        p1.add(lblLabel);
        p1.add(txtInData);
        this.add(p1, BorderLayout.CENTER );
        
        lblLabel.setBounds(0, 0, 50, 20);

        txtInData.setBounds(50, 0, width - 70, 20);
 
        JPanel p2 = new JPanel();
 
        p2.add(btnOK    = new JButton ("확인")); 
        p2.add(btnClose = new JButton ("닫기")); 
      
        this.add(p2, BorderLayout.SOUTH );
 
        btnOK.addActionListener(this);
        btnClose.addActionListener(this);
        this.setBounds(x, y, width, height);

        this.setVisible(true);
        
        txtInData.setFocusable(true);
  
	}
	@Override
	public void actionPerformed(ActionEvent e){
		String cmd = e.getActionCommand();
    	
    	if (cmd.equals("확인")){
    		InData = txtInData.getText();
    		dispose();
    	}
    	if (cmd.equals("닫기")){
    		InData = "";
    		dispose();
    	}
	}
	public String getInData()
	{
		return InData;
	}

}