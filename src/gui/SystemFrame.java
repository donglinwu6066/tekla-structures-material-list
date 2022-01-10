package gui;
//import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
//import java.util.Arrays;

import bnotai.tekla.material.fileio.Crypto;
import gui.SystemFrame;

//import java.util.List;
//import java.net.InetAddress;
//import java.net.NetworkInterface;
//import java.util.UUID;

import javax.swing.*;

//import java.awt.EventQueue;
//import java.awt.event.ActionEvent;

import java.nio.file.Files;
import java.nio.file.Paths;
//import java.nio.ByteBuffer;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class SystemFrame {
	JFrame frame;
	JPanel panel;
	// modify password here
	static String password = "password";
	private JPasswordField passwordField;

	JButton OK;
	private JLabel doneLabel;
	public SystemFrame() {
		initComponent();
	}
	public void initComponent() {
		frame = new JFrame("System Input");
		frame.setSize(600, 100);
		
		panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.NORTH);
		
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{125, 96, 132, 49, 0};
		gbl_panel.rowHeights = new int[]{0, 23, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		doneLabel = new JLabel("");
		GridBagConstraints gbc_doneLabel = new GridBagConstraints();
		gbc_doneLabel.insets = new Insets(0, 0, 5, 5);
		gbc_doneLabel.gridx = 1;
		gbc_doneLabel.gridy = 0;
		panel.add(doneLabel, gbc_doneLabel);
		
		passwordField = new JPasswordField(30);
		GridBagConstraints gbc_passwordField = new GridBagConstraints();
		gbc_passwordField.gridwidth = 2;
		gbc_passwordField.anchor = GridBagConstraints.WEST;
		gbc_passwordField.insets = new Insets(0, 0, 0, 5);
		gbc_passwordField.gridx = 1;
		gbc_passwordField.gridy = 1;
		panel.add(passwordField, gbc_passwordField);
		
		OK = new JButton("OK");
		OK.setActionCommand("OK");
		OK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    String input = new String(passwordField.getPassword());
			    if (isPasswordCorrect(input)) {
			        try {
			        	createLicence();
						doneLabel.setText("done");
			        } catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			    } 
			    else {
			        doneLabel.setText("no");
			    }
			    //Zero out the possible password, for security.
			    input = "";
			    passwordField.selectAll();
			}
		});
		GridBagConstraints gbc_OK = new GridBagConstraints();
		gbc_OK.anchor = GridBagConstraints.NORTHWEST;
		gbc_OK.gridx = 3;
		gbc_OK.gridy = 1;
		panel.add(OK, gbc_OK);
		frame.setVisible(true);
	}
	public static void main(String[] srgs) {
		new SystemFrame();
	}

	private static boolean isPasswordCorrect(String input) {	    
	    return input.equals(password);
	}
	private static void createLicence() throws Exception {
		Crypto crypto = new Crypto();
		String hostname = crypto.gethostname();
		String MachineID = crypto.getMachineID();


		String fileName = "data.dat";
		// remove old file
		Files.deleteIfExists(Paths.get(fileName));
		
		File outputFile = new File(fileName);
//		outputFile.getParentFile().mkdirs();
		outputFile.createNewFile();
		
		// Security.addProvider(new com.sun.crypto.provider.SunJCE());
		
        // Creating binary file
        FileOutputStream fout=new FileOutputStream(outputFile);
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(crypto.encrypt(hostname)+'\n');
        writer.write(crypto.encrypt(MachineID)+'\n');
        writer.close();
        
        fout.close();
//        dout.close();
        System.out.println("done");
	}
	
}
