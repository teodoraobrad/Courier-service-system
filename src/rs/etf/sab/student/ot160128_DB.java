/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Teodora
 */

public class ot160128_DB {
    private static final String username = "sa";
    private static final String password = "123";
    private static final String database = "ot160128";
    private static final int port = 1433;
    private static final String serverName = "localhost";
    
    //jdbc:sqlserver://[serverName[\instanceName][:portNumber]][;property=value[;property=value]]
 //   private static final String connectionString="jdbc:sqlserver://"
 //           + serverName + ":" + port + ";"
 //          + "database=" + database + ";"
 //           +"user=" + username 
 //           + ";password=" + password;
    private static final String connectionString="jdbc:sqlserver://"+serverName+":"+port+";databaseName="+database+";integratedSecurity=true;";
    
    
    private Connection connection;  
    
    private ot160128_DB(){
        try {
            connection=DriverManager.getConnection(connectionString);
        } catch (SQLException ex) {
            connection = null;
            Logger.getLogger(ot160128_DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Connection getConnection() {
        return connection;
    }
     
    private static ot160128_DB db=null;
    
    public static ot160128_DB getInstance()
    {
        if(db == null)
            db = new ot160128_DB();
        return db;
    } 
}

