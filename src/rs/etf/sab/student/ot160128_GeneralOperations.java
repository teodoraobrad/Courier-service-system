/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import rs.etf.sab.operations.GeneralOperations;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Teodora
 */
public class ot160128_GeneralOperations implements GeneralOperations {

    Connection connection = ot160128_DB.getInstance().getConnection();

    @Override
    public void eraseAll() {

        /*String sql =  "DELETE FROM [dbo].[Admin]\n"
                + "DELETE FROM [dbo].[Magacin]\n"
                + "DELETE FROM [dbo].[Ponuda]\n"
                + "DELETE FROM [dbo].[Zahtev]\n"
                + "DELETE FROM [dbo].[Paket]\n"
                + "DELETE FROM [dbo].[Adresa]\n"
                + "DELETE FROM [dbo].[Grad]\n"
               + "DELETE FROM [dbo].[Ruta]\n"

                + "DELETE FROM [dbo].[Voznja]\n"
                + "DELETE FROM [dbo].[Kurir]\n"
                + "DELETE FROM [dbo].[Vozilo]\n"
                + "DELETE FROM [dbo].[Korisnik]";*/
String sql =  "DELETE FROM [dbo].[Admin]\n"
                + "DELETE FROM [dbo].[Ruta]\n"
                + "DELETE FROM [dbo].[Ponuda]\n"
                + "DELETE FROM [dbo].[Zahtev]\n"
        
                + "DELETE FROM [dbo].[Paket]\n"
                + "DELETE FROM [dbo].[Voznja]\n"
                + "DELETE FROM [dbo].[Vozilo]\n"
                + "DELETE FROM [dbo].[Magacin]\n"
                + "DELETE FROM [dbo].[Kurir]\n"
                + "DELETE FROM [dbo].[Korisnik]"
                + "DELETE FROM [dbo].[Adresa]\n"
                + "DELETE FROM [dbo].[Grad]\n";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);

        } catch (SQLException ex) {
        }

    }
    
    
   

}
