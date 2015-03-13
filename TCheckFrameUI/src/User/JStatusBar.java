package User;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

 
public class JStatusBar extends JPanel implements  PropertyChangeListener {
 
    private JProgressBar progressBar;
 
    private JTextArea taskOutput;
    private Task task;
    private int currval = 0;
    class Task extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
            Random random = new Random();
     
            //Initialize progress property.
            setProgress(currval);
            while (currval < 100) {
                //Sleep for up to one second.
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignore) {}
 
                setProgress(currval);
                progressBar.updateUI();
            }
            return null;
        }
 
        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
        
            setCursor(null); //turn off the wait cursor
            taskOutput.append("Done!\n");
        }
    }
 
    public JStatusBar() {
        super(new BorderLayout());

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
 
        taskOutput = new JTextArea(5, 20);
        taskOutput.setMargin(new Insets(5,5,5,5));
        taskOutput.setEditable(false);
 
        JPanel panel = new JPanel();
       
        panel.add(progressBar);
 
        add(panel, BorderLayout.PAGE_START);
        add(new JScrollPane(taskOutput), BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
 

    }
 
 
    /**
     * Invoked when task's progress property changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
            taskOutput.append(String.format(
                    "Completed %d%% of task.\n", task.getProgress()));
        } 
    }
    
    public void TaskStart() {
    	currval = 0;
        task = new Task();
        task.addPropertyChangeListener(this);
        task.execute();
    }
    public void TaskCurrent(int val) {
    	currval = val;
    }
}