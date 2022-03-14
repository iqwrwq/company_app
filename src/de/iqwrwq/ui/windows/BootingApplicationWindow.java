package de.iqwrwq.ui.windows;

import javax.swing.*;
import java.awt.*;

public class BootingApplicationWindow extends JPanel {
    private JLabel logo;
    private JTextField ipInputField;
    private JLabel ipLabelField;
    private JLabel portLabel;
    private JTextField portInputField;
    private JButton startApplicationButton;
    private JButton testConnectionButton;
    private JTextArea log;

    public BootingApplicationWindow() {
        logo = new JLabel(new ImageIcon("src/de/iqwrwq/resources/logo.png"));
        ipInputField = new JTextField (5);
        ipLabelField = new JLabel ("IP");
        portLabel = new JLabel ("Port");
        portInputField = new JTextField (5);
        startApplicationButton = new JButton ("Start");
        testConnectionButton = new JButton ("Test Connection");
        log = new JTextArea (5, 5);

        ipInputField.setToolTipText ("Enter the IP where the main seatrade application is running");
        ipLabelField.setToolTipText ("Enter the IP where the main seatrade application is running");
        portLabel.setToolTipText ("Enter the port where the main seatrade application is running");
        portInputField.setToolTipText ("Enter the port where the main seatrade application is running");
        startApplicationButton.setToolTipText ("start company app");
        testConnectionButton.setToolTipText ("Test the connection to the main seatrade application");

        setPreferredSize (new Dimension (272, 246));
        setLayout (null);

        add(logo);
        add (ipInputField);
        add (ipLabelField);
        add (portLabel);
        add (portInputField);
        add (startApplicationButton);
        add (testConnectionButton);
        add (log);

        logo.setBounds (110, 10, 60, 60);
        ipInputField.setBounds (15, 175, 145, 30);
        ipLabelField.setBounds (25, 155, 100, 25);
        portLabel.setBounds (175, 155, 80, 25);
        portInputField.setBounds (165, 175, 100, 30);
        startApplicationButton.setBounds (165, 210, 100, 25);
        testConnectionButton.setBounds (10, 210, 145, 25);
        log.setBounds (20, 80, 235, 65);
    }


    public static void main (String[] args) {
        JFrame frame = new JFrame ("Company App");
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add (new BootingApplicationWindow());
        frame.pack();
        frame.setVisible (true);
    }
}
