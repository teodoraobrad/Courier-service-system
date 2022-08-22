/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import rs.etf.sab.operations.CityOperations;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.AddressOperations;

/**
 *
 * @author Teodora
 */
public class ot160128_CityOperations implements CityOperations {

    Connection connection = ot160128_DB.getInstance().getConnection();

    private int dohvatiIdG(String naziv) {
        System.out.println("GRAD Dohvati idg " + naziv);

        int ret = -1;
        try (
                PreparedStatement stmt = connection.prepareStatement("select IdG from Grad where Naziv=?");) {
            stmt.setString(1, naziv);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ret = rs.getInt(1);
            }
            rs.close();
            System.out.println("Grad idg=" + ret);

        } catch (SQLException ex) {
        }
        System.out.println("Grad idg=" + ret);
        return ret;

    }

    public boolean proveriPostojanje(int id) {
        System.out.println("GRAD Proveri idg " + id);

        boolean ret = false;
        try (
                PreparedStatement stmt = connection.prepareStatement("select IdG from Grad where IdG=?");) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("Grad postoji " + id);
                return true;
            }
            rs.close();
            System.out.println("Grad ne postoji " + id);

        } catch (SQLException ex) {
        }
        System.out.println("Grad ne postoji=");
        return ret;
    }

    @Override
    public boolean deleteCity(int idCity) {
        String sql = "delete from Grad where IdG=?";
        System.out.println("GRAD brisi grad idg=" + idCity);
        if (!proveriPostojanje(idCity)) {
            System.out.println("Grad ne postoji ");
            return false;
        }

        try (PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setInt(1, idCity);

            AddressOperations ao = new ot160128_AddressOperations();
            ao.deleteAllAddressesFromCity(idCity);

            ps.executeUpdate();

            return (ps.getUpdateCount() != 0) ? true : false;

        } catch (SQLException ex) {
        }
        System.out.println("Grad nije obrisan=");
        return false;
    }

    @Override
    public int deleteCity(String... names) {

        System.out.println("GRAD Brisi gradove ");
        //int n=names.length;
        int num = 0;

        String sql = "delete from Grad where Naziv=? and IdG=?";//
        AddressOperations ao = new ot160128_AddressOperations();

        try (PreparedStatement ps = connection.prepareStatement(sql);) {

            int idCity;

            for (String name : names) {

                idCity = dohvatiIdG(name);
                while (idCity != -1) {

                    ao.deleteAllAddressesFromCity(idCity);

                    ps.setString(1, name);
                    ps.setInt(2, idCity);
                    //  num+=  
                    ps.executeUpdate();

                    num += ps.getUpdateCount();

                    System.out.println("Grad obrisan " + name);
                    idCity = dohvatiIdG(name);//
                }

            }
            System.out.println("Gradovaobrisano  " + num);
            return num;

        } catch (SQLException ex) {
        }
        System.out.println("greska Brisi gradove ");
        return -1;
    }

    @Override
    public List<Integer> getAllCities() {
        System.out.println("GRAD dohvati sve ");
        String sql = "select IdG\n"
                + "from Grad";

        List<Integer> list = new LinkedList<>();

        try (Statement stmt = connection.createStatement()) {

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                list.add(rs.getInt(1));
                System.out.println("Grad dodat u listu=");
            }
            rs.close();

            return list;

        } catch (SQLException ex) {
        }
        System.out.println("losa list");
        return null;

    }

    @Override
    public int insertCity(String name, String postalCode) {

        String sql = "insert into Grad(Naziv,PostanskiBroj) values(?,?)";
        String help_sql = "select count(*) from Grad where PostanskiBroj=?";
        System.out.println("GRAD ubaci grad " + name + postalCode);
        try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);) {
            ps.setString(1, name);
            ps.setString(2, postalCode);

            try (PreparedStatement stmt = connection.prepareStatement(help_sql);) {
                stmt.setString(1, postalCode);
                int postoji = 0;
                ResultSet rshelp = stmt.executeQuery();
                if (rshelp.next()) {
                    postoji = rshelp.getInt(1);
                }
                if (postoji != 0) {
                    System.out.println("Grad sa tim postanskim brojem vec postoji");
                    rshelp.close();
                    return -1;
                }
                rshelp.close();
                System.out.println("Grad sa tim postanskim brojem ne postoji, moguce ubacivanje");

            } catch (SQLException ex) {
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys();) {
                if (rs.next()) {
                    System.out.println("Grad ubacen pod id " + rs.getInt(1));
                    return rs.getInt(1);
                }

            } catch (SQLException ex) {
            }

        } catch (SQLException ex) {
        }
        System.out.println("Grad nije ubacen");
        return -1;
    }

}
