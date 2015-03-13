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
		txtSubTitle.setLineWrap(true); //������ �ʹ� ��� �ڵ����� �������� ����
		txtSubTitle.setColumns(120); //���� ũ��(����ũ��)
		txtSubTitle.setRows(arrSubTitle.length); //���� ũ��(����ũ��)
		txtSubTitle.setText(subtitle);
		txtSubTitle.setEditable(false);
		txtSubTitle.setBackground(new Color(255,245,162));
 
		JLabel lblLabel = new JLabel("  �Է°� ");
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
 
        p2.add(btnOK    = new JButton ("Ȯ��")); 
        p2.add(btnClose = new JButton ("�ݱ�")); 
      
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
    	
    	if (cmd.equals("Ȯ��")){
    		InData = txtInData.getText();
    		dispose();
    	}
    	if (cmd.equals("�ݱ�")){
    		InData = "";
    		dispose();
    	}
	}
	public String getInData()
	{
		return InData;
	}

}