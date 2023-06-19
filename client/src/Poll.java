import javax.swing.*;
import java.awt.event.*;
import java.rmi.RemoteException;

public class Poll extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextArea description;
    private JTextField endDate;
    private JTextField options;
    private JTextField title;
    private Server server;
    private Client user;

    public Poll(Server server,Client user) {
        this.server = server;
        this.user = user;
        setTitle("New Poll");
        setBounds(100, 100, 750, 500);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        setVisible(true);
        this.pack();
    }

    private void onOK() {
        String title = this.title.getText();
        String description = this.description.getText();
        String endDate = this.endDate.getText();
        String options = this.options.getText();
        if (title.isEmpty() || description.isEmpty() || endDate.isEmpty() || options.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter all text fields.",
                    "ERROR",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if(server.addPoll(title,description,endDate,options,user.getId())){
            dispose();
            new HomeAdmin(server,user);
            }else {
                JOptionPane.showMessageDialog(this,
                        "Fail to add new Poll.",
                        "ERROR",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Fail to add new Poll.",
                    "ERROR",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        try {
            dispose();
            new HomeAdmin(server,user);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
