import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;
import java.rmi.RemoteException;
import java.util.List;


public class Vote extends JDialog {
    private JPanel contentPane;
    private JButton buttonLogout;
    private JList options;
    private JList count;
    private JLabel title;
    private JButton refreshButton;
    private JPanel Title;
    private JTextPane description;
    private JButton buttonHome;
    private Server server;
    private Client user;

    public Vote(Server server, Client user, String title) throws RemoteException {
        this.server = server;
        this.user = user;
        this.title.setText(title);
        this.description.setEditable(false);
        setTitle("Vote");
        setBounds(100, 100, 500, 500);
        setContentPane(contentPane);
        setModal(true);
        addOptions();
        this.options.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                addVote();
            }
        });

        buttonLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    addOptions();
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
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

        options.addComponentListener(new ComponentAdapter() {
        });

        buttonHome.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    dispose();
                    new Home(server, user);
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private void addVote() {
        String option = (String) options.getSelectedValue();
        try {
            if(server.addVote(user.getId(),title.getText(),option)){
                addOptions();
            }else {
                JOptionPane.showMessageDialog(this,
                        "You are already voted",
                        "ERROR",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void addOptions() throws RemoteException {
        List<String> options = server.addOptions(title.getText());
        if(options!=null){
            this.description.setText(options.get(0).split(";")[2]);
            DefaultListModel modelOptions = new DefaultListModel();
            DefaultListModel modelCount = new DefaultListModel();
            for (String item : options) {
                String[] votes = item.split(";");
                modelOptions.addElement(votes[0]);
                modelCount.addElement(votes[1]);
            }
            this.options.setModel(modelOptions);
            this.count.setModel(modelCount);
        }
    }
    private void onCancel() {
        dispose();
        new Login(server);
    }
}
