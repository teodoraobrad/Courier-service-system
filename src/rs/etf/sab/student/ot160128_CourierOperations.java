/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import rs.etf.sab.operations.CourierOperations;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Teodora
 */
public class ot160128_CourierOperations implements CourierOperations {

    Connection connection = ot160128_DB.getInstance().getConnection();

    private boolean postojiKorisnik(String username) {
        try (
                PreparedStatement stmt = connection.prepareStatement("select * from Korisnik where KorisnickoIme=?");) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                rs.close();
                return true;
            }
            rs.close();

        } catch (SQLException ex) {

        }

        return false;
    }

    public boolean postojiKurir(String username) {
        try (
                PreparedStatement stmt = connection.prepareStatement("select KorisnickoIme from Kurir where KorisnickoIme=?");) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                rs.close();
                return true;
            }
            rs.close();

        } catch (SQLException ex) {

        }

        return false;
    }

    @Override
    public boolean deleteCourier(String courierUserName) { //MOZDA SE I KOORISNIK BRISE??

        String sql = "delete from Kurir where KorisnickoIme=?";
        if (!postojiKurir(sql)) {
            return false;
        }
        ot160128_DriveOperation voznja = new ot160128_DriveOperation();

        voznja.delete(courierUserName);

        System.out.println("KURIR Obrisi kurira " + courierUserName);
        try (PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setString(1, courierUserName);
            ps.executeUpdate();
            System.out.println("KURIR Obrisano kur " + ps.getUpdateCount());
            return ps.getUpdateCount() > 0;

        } catch (SQLException ex) {
        }
        System.out.println("KURIR Obrisi greska ");
        return false;

    }

    @Override
    public List<String> getAllCouriers() {
        String sql = "select KorisnickoIme\n"
                + "from Kurir order by Profit desc";
        System.out.println("KURIR Dohvati  svw ");

        List<String> list = new LinkedList<String>();

        try (Statement stmt = connection.createStatement()) {

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                list.add(rs.getString(1));
                System.out.println(" " + rs.getString(1));
            }
            rs.close();

            return list;

        } catch (SQLException ex) {
        }
        System.out.println("KURIR Greska dohvati ");
        return null;
    }

    @Override
    public BigDecimal getAverageCourierProfit(int numberOfDeliveries) {

        System.out.println("KURUR dohvati provit avg " + numberOfDeliveries);
        String sql;
        BigDecimal ret = BigDecimal.ZERO;
        if (numberOfDeliveries == -1) { //coalesce
            sql = "select avg(Profit)\n"
                    + "from Kurir";
            try (PreparedStatement ps = connection.prepareStatement(sql);) {

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    ret = rs.getBigDecimal(1);
                } else {
                    rs.close();
                    return ret;
                }
                rs.close();

            } catch (SQLException ex) {
            }

        } else {///coalesce
            sql = "select avg(Profit)\n"
                    + "from Kurir\n"
                    + "where BrojPaketa=?";
            try (PreparedStatement ps1 = connection.prepareStatement(sql);) {
                ps1.setInt(1, numberOfDeliveries);
                ResultSet rs1 = ps1.executeQuery();
                if (rs1.next()) {

                    ret = rs1.getBigDecimal(1);
                } else {
                    rs1.close();
                    return ret;
                }
                rs1.close();

            } catch (SQLException ex) {
                Logger.getLogger(ot160128_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        System.out.println("KURIR profit ret " + ret);

        return ret;

    }

    @Override
    public List<String> getCouriersWithStatus(int statusOfCourier) {
        System.out.println("KURIR sa statusom" + statusOfCourier);

        String sql = "select KorisnickoIme\n"
                + "from Kurir where Status=?";
        System.out.println("KURIR Dohvati  kurirre sa statusom svw ");

        String sql1 = "select m.KorisnickoIme \n"
                + "from Korisnik m\n"
                + "where ( \n"
                + "select count(*) \n"
                + "from Voznja v join Kurir k on v.KorisnickoIme=k.KorisnickoIme\n"
                + "where v.Gotova=0 and k.KorisnickoIme=m.KorisnickoIme)>0";
        String sql2 = "select m.KorisnickoIme \n"
                + "from Korisnik m\n"
                + "where ( \n"
                + "select count(*) \n"
                + "from Voznja v join Kurir k on v.KorisnickoIme=k.KorisnickoIme\n"
                + "where v.Gotova=0 and k.KorisnickoIme=m.KorisnickoIme)=0";

        List<String> list = new LinkedList<String>();

        /*      try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, statusOfCourier);
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                list.add(rs.getString(1));
                //System.out.println(" " + rs.getInt(1));
            }
            rs.close();
            // for (int i = 0; i < list.size(); i++) {
             //   System.out.println("clan  " + list.get(i));
           // }

            return list;

        } catch (SQLException ex) {
        }

         */
        try (Statement stmt = connection.createStatement()) {

            ResultSet rs = stmt.executeQuery(sql1);
            ResultSet rs1 = stmt.executeQuery(sql2);
            if (statusOfCourier == 1) {
                while (rs.next()) {
                    list.add(rs.getString(1));
                    System.out.println(" " + rs.getInt(1));
                }
            } else {

                while (rs1.next()) {
                    list.add(rs1.getString(1));

                    System.out.println(" " + rs.getInt(1));
                }

            }

            rs.close();
            rs1.close();

            return list;

        } catch (SQLException ex) {
        }
        System.out.println("KURIR Greska ");
        return null;

    }

    @Override
    public boolean insertCourier(String courierUserName, String driverLicenceNumber) {
        System.out.println("KURIR insert " + courierUserName + " " + driverLicenceNumber);
        if (!postojiKorisnik(courierUserName)) {
            System.out.println("KURIR ne postoji korisnik");
            return false;
        }
        if (postojiKurir(courierUserName)) {
            System.out.println("KURIR vec postoji kurir");
            return false;
        }

        try (
                PreparedStatement stmt = connection.prepareStatement("select KorisnickoIme from Kurir where BrojDozvole=?");) {

            stmt.setString(1, driverLicenceNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                rs.close();
                System.out.println("KURIR vec se koristi voxacka");
                return false;

            }

            rs.close();

            try (
                    PreparedStatement ps = connection.prepareStatement("insert into Kurir(KorisnickoIme,BrojDozvole) values(?,?)");) {
                //>>> mozda treba status
                ps.setString(1, courierUserName);
                ps.setString(2, driverLicenceNumber);

                ps.executeUpdate();
                if (ps.getUpdateCount() == 0) {
                    System.out.println("KURIR nije insertovan");

                    return false;

                }
                System.out.println("KURIR je insertovan");

                return true;

            } catch (SQLException ex) {
            }

        } catch (SQLException ex) {
        }
        System.out.println("KURIR err");

        return false;
    }

}
