import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;
import java.rmi.RemoteException;
import java.util.List;

public class HomeAdmin extends JDialog {
    private JPanel contentPane;
    private JButton newPollButton;
    private JButton buttonCancel;
    private JList polls;
    private JLabel username;
    private String userName;
    private Server server;
    private Client user;

    public HomeAdmin(Server server, Client user) throws RemoteException {
        this.server = server;
        this.user = user;
        this.userName = user.getUsername();
        this.username.setText(userName);
        setTitle("Home - Admin");
        setBounds(100, 100, 750, 500);
        setContentPane(contentPane);
        setModal(true);
//        addUsers();
        addPolls();

        polls.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                dispose();
                String title = ((String) polls.getSelectedValue()).split(":")[0];
                new UDPoll(server, user, title);
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        newPollButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Poll(server, user);
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

    private void onCancel() {
        dispose();
        new Login(server);
    }
    private void addPolls() throws RemoteException {
        List<String> polls = server.listPolls();
        if (polls != null) {
            DefaultListModel model = new DefaultListModel();
            for (String item : polls) {
                String[] poll = item.split(",");
                model.addElement(poll[1] + ":  End Date: " + poll[2]);
            }
            this.polls.setModel(model);
        }
    }
}
