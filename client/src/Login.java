import javax.swing.*;
import java.awt.event.*;
import java.rmi.RemoteException;

public class Login extends JDialog implements ActionListener{
    private JPanel contentPane;
    private JButton loginBtn;
    private JButton buttonCancel;
    private JTextField username;
    private JPasswordField password;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JButton registerBtn;
    private Server server;
    private User user;
    public Login(Server server) {
        this.server = server;
        setBounds(100, 100, 800, 500);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(loginBtn);

        loginBtn.addActionListener(this);
        registerBtn.addActionListener(this);

        loginBtn.setActionCommand("login");
        registerBtn.setActionCommand("register");

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
        this.setVisible(true);
    }

    private void onCancel() {
        dispose();
        System.exit(0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String username = this.username.getText();
        String password = String.valueOf(this.password.getPassword());
        String actionCommand = e.getActionCommand();
        String message = "";
        String[] response = null;

        //Check if all text fields are enter
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter all text fields.",
                    "ERROR",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            if (actionCommand.equals("login")) {
                message = "login";
                response = server.login(username, password);
            } else if (actionCommand.equals("register")) {
                message = "register";
                response = server.register(username, password);
            }
            if(response==null){
                JOptionPane.showMessageDialog(this,
                        "Username or password incorrect.",
                        "ERROR",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            user = new User(Long.parseLong(response[0]), response[1], response[2], Boolean.parseBoolean(response[3]));
            dispose();
            if(user.isAdmin()){
                new HomeAdmin(server,user);
            }else{
                new Home(server,user);
            }
        } catch (RemoteException exception) {
            exception.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Fail to "+message+".",
                    "ERROR",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
    dispose();
    }
}
