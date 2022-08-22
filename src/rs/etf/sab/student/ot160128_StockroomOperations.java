/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import rs.etf.sab.operations.StockroomOperations;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Teodora
 */
public class ot160128_StockroomOperations implements StockroomOperations {

    Connection connection = ot160128_DB.getInstance().getConnection();

    public boolean proveriPostojanje(int id) {
        boolean ret = false;
        try (
                PreparedStatement stmt = connection.prepareStatement("select IdA from Magacin where IdA=?");) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                //System.out.println("Grad postoji " + id);
                return true;
            }
            rs.close();
            //System.out.println("Grad ne postoji " + id);

        } catch (SQLException ex) {
        }
        //System.out.println("Grad ne postoji=");
        return ret;
    }

    @Override
    public boolean deleteStockroom(int idStockroom) {
        String sql = "delete from Magacin where IdA=?";
        System.out.println("MAG brisi magacin ida=" + idStockroom);
        if (!proveriPostojanje(idStockroom)) {
            System.out.println("MAG Magacin ne postoji ");
            return false;
        }
        String sql_empty="select count(*) from Paket p where p.Status=2 and p.IdATre=?";
        try (PreparedStatement ps = connection.prepareStatement(sql_empty);) {
            ps.setInt(1, idStockroom);

            
            ResultSet rs=ps.executeQuery();
            if(rs.next()) {
                if(rs.getInt(1)>0){
                rs.close();
                
        System.out.println("MAG  nije obrisan=");
                return false;
                } else{
                    rs.close();
                }
            }
           /* else{
                rs.close();
                
        System.out.println("MAG  nije obrisan=");
                return false;
                
            }*/

        } catch (SQLException ex) {
            Logger.getLogger(ot160128_AddressOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        
        
        try (PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setInt(1, idStockroom);

            ps.executeUpdate();
            return (ps.getUpdateCount() != 0) ? true : false;

            //ili radi sa get row count
        } catch (SQLException ex) {
            Logger.getLogger(ot160128_AddressOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("MAG  nije obrisan=");
        return false;
    }

    @Override
    public int deleteStockroomFromCity(int idCity) {
        
        System.out.println("MAG  brisi iz grada ");
        String sql="select Magacin.IdA from Magacin join Adresa on Magacin.IdA=Adresa.IdA where Adresa.IdG=?";
        try (PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setInt(1, idCity);

            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                int idMag=rs.getInt(1);
                
                deleteStockroom(idMag);
                rs.close();
                return idMag;
            }else {
                rs.close();
                return -1;
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ot160128_AddressOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("MAG  greska brisi iz grada");
        return -1;
        
    }

    @Override
    public List<Integer> getAllStockrooms() {
        System.out.println("MAG dohvati sve ");
        String sql = "select IdA\n"
                + "from Magacin";

        List<Integer> list = new LinkedList<Integer>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

        System.out.println("MAG dobar upit");
            while (rs.next()) {
                list.add(rs.getInt(1));
                System.out.println(rs.getInt(1));
            }
            rs.close();

            return list;

        } catch (SQLException ex) {
        }
        System.out.println("MAG losa list");
        return null;
    }

    @Override
    public int insertStockroom(int address) {

        System.out.println("MAG ins");
        ot160128_AddressOperations addr = new ot160128_AddressOperations();
        if (!addr.proveriPostojanjeA(address)) {
            
        System.out.println("MAG nije insert ne postoji grad");
            return -1;
        }
        int idg = addr.getIdCity(address);
        if (idg == -1) {
            System.out.println("MAG nije insert ne postoji grad");
        
            return -1;
        }

        String sql = "select count(*) from Magacin join Adresa on Adresa.IdA=Magacin.IdA join Grad on Grad.IdG=Adresa.IdG where Grad.IdG=?";
        String sqlInsert = "insert into Magacin(IdA) values(?)";
        try (PreparedStatement ps = connection.prepareStatement(sql);) {

            ps.setInt(1, idg);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                if (rs.getInt(1) == 0) {

                    try (PreparedStatement ps1 = connection.prepareStatement(sqlInsert);) {

                        ps1.setInt(1, address);
                        ps1.executeUpdate();
                        return address;

                    } catch (SQLException ex) {
                        Logger.getLogger(ot160128_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else { System.out.println("MAG nije insert postoji mag u gr");
        
                    return -1;
                }

            } else { System.out.println("MAG nije insert");
        
                return -1;
            }

        } catch (SQLException ex) {
            Logger.getLogger(ot160128_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("MAG nerr");
        
        return -1;

    }

}
