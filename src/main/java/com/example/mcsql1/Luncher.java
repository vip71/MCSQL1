package com.example.mcsql1;

import javax.swing.*;
import java.sql.SQLException;

public class Luncher extends JFrame{

    public static void main(String[] args) {
        Database.initConnection();
        new Luncher();
    }

    Luncher(){
        setContentPane(mainPanel);
        setSize(600, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        addActionListeners();
    }

    private int playerId=-1;
    private JButton lunch;
    private JPanel mainPanel;
    private JTextField nameTextField;
    private JPasswordField surnamePasswordField;
    private JTextField blockTextField;
    private JTextArea Output;
    private JButton login;
    private JButton blockData;
    private JButton register;
    private JTextField worldTextField;
    private JButton createWorld;
    private JButton resetWorld;

private void addActionListeners() {
        lunch.addActionListener(
            e -> {
                String result =Database.selectWorld(worldTextField.getText());
                if(result!=null && playerId!=-1){
                    setVisible(false);
                    ClientApplication.main(Integer.parseInt(result),playerId);
                    System.exit(0);
                }
            });
        login.addActionListener(
            e -> {
                try {
                    String status = Database.selectOneRow("Select TryToLogin('"+nameTextField.getText().hashCode()+"','"+surnamePasswordField.getText().hashCode()+"')");
                    playerId = Integer.parseInt(Database.selectOneRow("Select TryToGetPlayerId('"+nameTextField.getText().hashCode()+"','"+surnamePasswordField.getText().hashCode()+"')"));
                    System.out.println(playerId);
                    if(status==null)
                    {
                        Output.setText("Invalid Login");
                    }
                    else
                    {
                        Output.setText("Valid Login " +(status.matches("0") ? "User" : "Admin"));
                        if(status.matches("1"))
                            Database.initConnection("admin","0");
                        else
                            Database.initConnection("logged","0");
                        login.setEnabled(false);
                        register.setEnabled(false);
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
        register.addActionListener(
            e -> {
                try {
                    String newUser = Database.selectOneRow("Select Register('"+nameTextField.getText().hashCode()+"','"+surnamePasswordField.getText().hashCode()+"')");
                    if(newUser.matches("0"))
                    {
                        Output.setText("Invalid Registration");
                    }
                    else
                    {
                        Output.setText("Valid Registration");
                        register.setEnabled(false);
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
        blockData.addActionListener(
            e -> {
                try {
                    Output.setText(Database.selectBlockData(blockTextField.getText()));
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
        createWorld.addActionListener(
            e -> {
                if(worldTextField.getText()!=null){
                    if(Database.selectWorld(worldTextField.getText())==null){
                        System.out.println("Created World");
                        Database.createWorld(worldTextField.getText());
                    }
                    else
                        System.out.println("World Already Exits");
                }
            });
        resetWorld.addActionListener(
            e -> Database.callStatement("CALL ResetWorld("+Database.selectWorld(worldTextField.getText())+");"));
    }
}
