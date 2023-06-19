import javax.swing.*;
import java.awt.event.*;
import java.rmi.RemoteException;

public class UDPoll extends JDialog {
    private JPanel contentPane;
    private JButton buttonEdite;
    private JButton buttonCancel;
    private JButton buttonDelete;
    private Server server;
    private Client user;

    public UDPoll(Server server, Client user, String title) {
        this.server = server;
        this.user = user;
        setTitle(title);
        setBounds(100, 100, 500, 200);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonEdite);

        buttonEdite.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onEdite(title);
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
        buttonDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDelete(title);
            }
        });
        setVisible(true);
    }

    private void onDelete(String title) {
        try {
            dispose();
            if(!server.deletePoll(title)){
                JOptionPane.showMessageDialog(this,
                        "Fail to Delete!",
                        "ERROR",
                        JOptionPane.ERROR_MESSAGE);
            }
            new HomeAdmin(server, user);
        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void onEdite(String title) {
        try {
            dispose();
            String[] poll = server.getPoll(title);
            new EditePoll(server,user,poll);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
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
