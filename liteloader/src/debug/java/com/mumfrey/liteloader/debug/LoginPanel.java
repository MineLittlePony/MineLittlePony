package com.mumfrey.liteloader.debug;

import static javax.swing.WindowConstants.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

/**
 * JPanel displayed in a JDialog to prompt the user for login credentials for
 * minecraft.
 * 
 * @author Adam Mummery-Smith
 */
public class LoginPanel extends JPanel
{
    private static final long serialVersionUID = 1L;

    private GridBagLayout panelLoginLayout;

    private JPanel panelTitle;
    private JPanel panelCentre;
    private JPanel panelPadding;
    private JPanel panelBottom;
    private JLabel lblTitle;
    private JLabel lblSubTitle;
    private JLabel lblMessage;
    private JLabel lblUserName;
    private JLabel lblPassword;
    private TextField txtUsername;
    private TextField txtPassword;
    private JButton btnLogin;
    private JButton btnCancel;
    private JCheckBox chkOffline;

    private JDialog dialog;

    private ListFocusTraversal tabOrder = new ListFocusTraversal();

    private boolean dialogResult = false;

    public LoginPanel(String username, String password, String error)
    {
        Color backColour = new Color(102, 118, 144);

        this.setFocusable(false);
        this.setPreferredSize(new Dimension(400, 260));
        this.setBackground(new Color(105, 105, 105));
        this.setLayout(new BorderLayout(0, 0));

        this.panelTitle = new JPanel();
        this.panelTitle.setBackground(backColour);
        this.panelTitle.setPreferredSize(new Dimension(400, 64));
        this.panelTitle.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        this.panelBottom = new JPanel();
        this.panelBottom.setBackground(backColour);
        this.panelBottom.setPreferredSize(new Dimension(400, 32));
        this.panelBottom.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        this.panelPadding = new JPanel();
        this.panelPadding.setBorder(new EmptyBorder(4, 8, 8, 8));
        this.panelPadding.setOpaque(false);
        this.panelPadding.setLayout(new BorderLayout(0, 0));

        this.panelCentre = new JPanel();
        this.panelCentre.setOpaque(false);
        this.panelCentre.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Yggdrasil Login", 
                TitledBorder.LEADING, TitledBorder.TOP, null, Color.WHITE));
        this.panelLoginLayout = new GridBagLayout();
        this.panelLoginLayout.columnWidths = new int[] {30, 80, 120, 120, 30};
        this.panelLoginLayout.rowHeights = new int[] {24, 32, 32, 32};
        this.panelLoginLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        this.panelLoginLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0};
        this.panelCentre.setLayout(this.panelLoginLayout);

        this.lblTitle = new JLabel("Log in to minecraft.net");
        this.lblTitle.setBorder(new EmptyBorder(4, 16, 0, 16));
        this.lblTitle.setFont(new Font("Tahoma", Font.BOLD, 18));
        this.lblTitle.setForeground(Color.WHITE);
        this.lblTitle.setPreferredSize(new Dimension(400, 26));

        this.lblSubTitle = new JLabel("Your password will not be stored, logging in with Yggdrasil");
        this.lblSubTitle.setBorder(new EmptyBorder(0, 16, 0, 16));
        this.lblSubTitle.setForeground(Color.WHITE);
        this.lblSubTitle.setPreferredSize(new Dimension(400, 16));

        this.lblMessage = new JLabel("Enter your login details for minecraft.net");
        this.lblMessage.setForeground(Color.WHITE);

        this.lblUserName = new JLabel("User name");
        this.lblUserName.setForeground(Color.WHITE);

        this.lblPassword = new JLabel("Password");
        this.lblPassword.setForeground(Color.WHITE);

        this.txtUsername = new TextField();
        this.txtUsername.setPreferredSize(new Dimension(200, 22));
        this.txtUsername.setText(username);

        this.txtPassword = new TextField();
        this.txtPassword.setEchoChar('*');
        this.txtPassword.setPreferredSize(new Dimension(200, 22));
        this.txtPassword.setText(password);

        this.btnLogin = new JButton("Log in");
        this.btnLogin.addActionListener(new ActionListener()
        {
            @Override public void actionPerformed(ActionEvent e)
            {
                LoginPanel.this.onLoginClick();
            }
        });

        this.btnCancel = new JButton("Cancel");
        this.btnCancel.addActionListener(new ActionListener()
        {
            @Override public void actionPerformed(ActionEvent e)
            {
                LoginPanel.this.onCancelClick();
            }
        });

        this.chkOffline = new JCheckBox("Never ask me to log in (always run offline)");
        this.chkOffline.setPreferredSize(new Dimension(380, 23));
        this.chkOffline.setForeground(Color.WHITE);
        this.chkOffline.setOpaque(false);
        this.chkOffline.addActionListener(new ActionListener()
        {
            @Override public void actionPerformed(ActionEvent e)
            {
                LoginPanel.this.onOfflineCheckedChanged();
            }
        });

        GridBagConstraints lblMessageConstraints = new GridBagConstraints();
        lblMessageConstraints.anchor = GridBagConstraints.WEST;
        lblMessageConstraints.gridwidth = 2;
        lblMessageConstraints.insets = new Insets(0, 0, 5, 5);
        lblMessageConstraints.gridx = 2;
        lblMessageConstraints.gridy = 0;

        GridBagConstraints lblUserNameConstraints = new GridBagConstraints();
        lblUserNameConstraints.anchor = GridBagConstraints.WEST;
        lblUserNameConstraints.fill = GridBagConstraints.VERTICAL;
        lblUserNameConstraints.insets = new Insets(0, 0, 5, 5);
        lblUserNameConstraints.gridx = 1;
        lblUserNameConstraints.gridy = 1;

        GridBagConstraints lblPasswordConstraints = new GridBagConstraints();
        lblPasswordConstraints.anchor = GridBagConstraints.WEST;
        lblPasswordConstraints.fill = GridBagConstraints.VERTICAL;
        lblPasswordConstraints.insets = new Insets(0, 0, 5, 5);
        lblPasswordConstraints.gridx = 1;
        lblPasswordConstraints.gridy = 2;

        GridBagConstraints txtUsernameConstraints = new GridBagConstraints();
        txtUsernameConstraints.gridwidth = 2;
        txtUsernameConstraints.fill = GridBagConstraints.HORIZONTAL;
        txtUsernameConstraints.insets = new Insets(0, 0, 5, 0);
        txtUsernameConstraints.gridx = 2;
        txtUsernameConstraints.gridy = 1;

        GridBagConstraints txtPasswordConstraints = new GridBagConstraints();
        txtPasswordConstraints.gridwidth = 2;
        txtPasswordConstraints.insets = new Insets(0, 0, 5, 0);
        txtPasswordConstraints.fill = GridBagConstraints.HORIZONTAL;
        txtPasswordConstraints.gridx = 2;
        txtPasswordConstraints.gridy = 2;

        GridBagConstraints btnLoginConstraints = new GridBagConstraints();
        btnLoginConstraints.fill = GridBagConstraints.HORIZONTAL;
        btnLoginConstraints.gridx = 3;
        btnLoginConstraints.gridy = 3;

        GridBagConstraints btnCancelConstraints = new GridBagConstraints();
        btnCancelConstraints.anchor = GridBagConstraints.EAST;
        btnCancelConstraints.insets = new Insets(0, 0, 0, 5);
        btnCancelConstraints.gridx = 2;
        btnCancelConstraints.gridy = 3;

        this.add(this.panelTitle, BorderLayout.NORTH);
        this.add(this.panelPadding, BorderLayout.CENTER);
        this.add(this.panelBottom, BorderLayout.SOUTH);

        this.panelPadding.add(this.panelCentre);

        this.panelTitle.add(this.lblTitle);
        this.panelTitle.add(this.lblSubTitle);

        this.panelCentre.add(this.lblMessage, lblMessageConstraints);
        this.panelCentre.add(this.lblUserName, lblUserNameConstraints);
        this.panelCentre.add(this.lblPassword, lblPasswordConstraints);
        this.panelCentre.add(this.txtUsername, txtUsernameConstraints);
        this.panelCentre.add(this.txtPassword, txtPasswordConstraints);
        this.panelCentre.add(this.btnLogin, btnLoginConstraints);
        this.panelCentre.add(this.btnCancel, btnCancelConstraints);

        this.panelBottom.add(this.chkOffline);

        this.tabOrder.add(this.txtUsername);
        this.tabOrder.add(this.txtPassword);
        this.tabOrder.add(this.btnLogin);
        this.tabOrder.add(this.btnCancel);
        this.tabOrder.add(this.chkOffline);

        if (error != null)
        {
            this.lblMessage.setText(error);
            this.lblMessage.setForeground(new Color(255, 180, 180));
        }
    }

    protected void onShowDialog()
    {
        if (this.txtUsername.getText().length() > 0)
        {
            if (this.txtPassword.getText().length() > 0)
            {
                this.txtUsername.select(0, this.txtUsername.getText().length());
            }
            else
            {
                this.txtPassword.requestFocusInWindow();
            }
        }
    }

    protected void onLoginClick()
    {
        this.dialogResult = true;
        this.dialog.setVisible(false);
    }

    protected void onCancelClick()
    {
        this.dialog.setVisible(false);
    }

    protected void onOfflineCheckedChanged()
    {
        boolean selected = this.chkOffline.isSelected();
        this.btnLogin.setText(selected ? "Work Offline" : "Log In");
        this.txtUsername.setEnabled(!selected);
        this.txtPassword.setEnabled(!selected);
    }

    /**
     * @param dialog
     */
    public void setDialog(JDialog dialog)
    {
        this.dialog = dialog;

        this.dialog.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowOpened(WindowEvent e)
            {
                LoginPanel.this.onShowDialog();
            }
        });

        this.dialog.getRootPane().setDefaultButton(this.btnLogin);
        this.dialog.setFocusTraversalPolicy(this.tabOrder);
    }

    public boolean showModalDialog()
    {
        this.dialog.setVisible(true);
        this.dialog.dispose();
        return this.dialogResult;
    }

    public String getUsername()
    {
        return this.txtUsername.getText();
    }

    public String getPassword()
    {
        return this.txtPassword.getText();
    }

    public boolean workOffline()
    {
        return this.chkOffline.isSelected();
    }

    public static LoginPanel getLoginPanel(String username, String password, String error)
    {
        if (username == null) username = "";
        if (password == null) password = "";

        final JDialog dialog = new JDialog();
        final LoginPanel panel = new LoginPanel(username, password, error);
        panel.setDialog(dialog);

        dialog.setContentPane(panel);
        dialog.setTitle("Yggdrasil Login");
        dialog.setResizable(false);
        dialog.pack();
        dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);

        return panel;
    }

    class ListFocusTraversal extends FocusTraversalPolicy
    {
        private final List<Component> components = new ArrayList<Component>();

        void add(Component component)
        {
            this.components.add(component);
        }

        @Override
        public Component getComponentAfter(Container container, Component component)
        {
            int index = this.components.indexOf(component) + 1;
            if (index >= this.components.size()) return this.components.get(0);
            return this.components.get(index);
        }

        @Override
        public Component getComponentBefore(Container container, Component component)
        {
            int index = this.components.indexOf(component) - 1;
            if (index < 0) return this.getLastComponent(container);
            return this.components.get(index);
        }

        @Override
        public Component getFirstComponent(Container container)
        {
            return this.components.get(0);
        }

        @Override
        public Component getLastComponent(Container container)
        {
            return this.components.get(this.components.size() - 1);
        }

        @Override
        public Component getDefaultComponent(Container container)
        {
            return this.getFirstComponent(container);
        }
    }
}
