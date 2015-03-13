package User;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
 
class DataInsertDialog extends JDialog implements ActionListener{
	private JTextArea txtInData;
	private JButton btnOK, btnClose;
	private String  InData = "";
	public DataInsertDialog(int x, int y, int width, int height, String title) {
		this.setTitle(title);
		this.setModal(true);
		this.setLayout(new BorderLayout());

        txtInData = new JTextArea();
        txtInData.setLineWrap(true); //������ �ʹ� ��� �ڵ����� �������� ����
        txtInData.setColumns(120); //���� ũ��(����ũ��)
        txtInData.setRows(1000); //���� ũ��(����ũ��)
        JScrollPane myPanetxtSend = new JScrollPane(txtInData);
        myPanetxtSend.setAutoscrolls(true);

        myPanetxtSend.setPreferredSize(new Dimension(270,200));
        this.add(myPanetxtSend, BorderLayout.CENTER );
        
        JPanel p1 = new JPanel();
 
        p1.add(btnOK    = new JButton ("Ȯ��")); 
        p1.add(btnClose = new JButton ("�ݱ�")); 
      
        this.add(p1, BorderLayout.SOUTH );

        
        btnOK.addActionListener(this);
        btnClose.addActionListener(this);
        this.setBounds(x, y, width, height);

        this.setVisible(true);
  
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