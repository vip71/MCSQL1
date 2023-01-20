package com.example.mcsql1;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {

private static Connection connection;
private static final Logger logger = Logger.getLogger(Database.class.getName());
private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
private static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/minecraft";
private static final String DEFAULT_DB_USER = "not_logged";// "root";
private static final String DEFAULT_DB_PASSWORD = "0";// "password";

public static void initConnection(String DB_USER,String DB_PASSWORD)
{
    try {
        Class.forName(DB_DRIVER);
    } catch (ClassNotFoundException exception) {
        logger.log(Level.SEVERE, exception.getMessage());
    }

    try {
        Database.connection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
    } catch (SQLException exception) {
        logger.log(Level.SEVERE, exception.getMessage());
    }
}

    public static void initConnection(){
        initConnection(DEFAULT_DB_USER, DEFAULT_DB_PASSWORD);
    }

    public static void callStatement(String statement){
        CallableStatement callStatement;
        try {
            callStatement = connection.prepareCall(statement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            callStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String selectOneRow(String query) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(query);
        result.next();
        return result.getString(1);
    }

    public static String selectBlockData(String argument) throws SQLException {
    String query = "SELECT needs_solid, is_solid FROM block_type WHERE name = ?";
    PreparedStatement statement = connection.prepareStatement(query);
    statement.setString(1,argument);
    ResultSet result = statement.executeQuery();
    String resultStr="";
    result.next();
    resultStr+=result.getString(1).matches("1") ?  "needs solid block, " : "does not need solid block,";
    resultStr+=result.getString(2).matches("1") ?  " is solid block" : " is not solid block";
    return resultStr;
    }

    public static String selectWorld(String argument) {
        String query = "SELECT id FROM world WHERE name = ?";
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(query);
            statement.setString(1,argument);
            ResultSet result = statement.executeQuery();
            result.next();
            return result.getString(1);
        } catch (SQLException e) {
            return null;
        }
    }

    public static void createWorld(String argument) {
        System.out.println(argument);
    String query = "INSERT INTO world (name) VALUES (?)";
    PreparedStatement statement;
    try {
        statement = connection.prepareStatement(query);
        statement.setString(1,argument);
        statement.executeUpdate();
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
}

}