import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;
import java.rmi.RemoteException;
import java.util.List;


public class Home extends JDialog {
    private JPanel contentPane;
    private JButton buttonLogout;
    private JLabel username;
    private JButton refreshButton;
    private JList polls;
    private Server server;
    private Client user;

    public Home(Server server, Client user) throws RemoteException {
        this.server = server;
        this.user = user;
        this.username.setText(this.user.getUsername());
        setTitle("Home");
        setBounds(100, 100, 500, 500);
        setContentPane(contentPane);
        setModal(true);
        addPolls();

        buttonLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
        polls.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                dispose();
                String title = ((String) polls.getSelectedValue()).split(":")[0];
                try {
                    new Vote(server, user, title);
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    addPolls();
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

        polls.addComponentListener(new ComponentAdapter() {
        });
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

    private void onCancel() {
        dispose();
        new Login(server);
    }
}
