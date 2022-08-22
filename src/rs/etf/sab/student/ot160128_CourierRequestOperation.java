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
import rs.etf.sab.operations.CourierRequestOperation;
import java.util.List;

/**
 *
 * @author Teodora
 */
public class ot160128_CourierRequestOperation implements CourierRequestOperation {

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

    public boolean postojiZahtev(String userName) {

        try (
                PreparedStatement stmt = connection.prepareStatement("select count(*) from Zahtev where KorisnickoIme=?");) {
            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                if (rs.getInt(1) == 0) {
                    rs.close();
                    return false;
                }
                rs.close();
                return true;
            }
            rs.close();

        } catch (SQLException ex) {

        }

        return false;
    }

    @Override
    public boolean changeDriverLicenceNumberInCourierRequest(String userName, String licencePlateNumber) {
        System.out.println("REQ menjaj" + userName);
        boolean valid = postojiZahtev(userName);

        if (valid) {
            System.out.println("zah psotoji");
            try (PreparedStatement stmt = connection.prepareStatement("update Zahtev set BrojDozvole=? where KorisnickoIme=?");) {
                stmt.setString(1, licencePlateNumber);
                stmt.setString(2, userName);
                stmt.executeUpdate();

                //return stmt.getUpdateCount() == 1;
                 int num = stmt.getUpdateCount();
                if (num == 0) {
                    System.out.println("nista nije menjano TRYE?FALSE");
                    return false;   //ILI TRUEE?
                } else {
                    System.out.println("promenjeno");
                    return true;
                }

            } catch (SQLException ex) {

            }

        }
        return false;

    }

    @Override
    public boolean deleteCourierRequest(String userName) {
        System.out.println("REQ Obrisi zah " + userName);
        String sql = "delete from Zahtev where KorisnickoIme=?";

        if (!postojiZahtev(userName)) {
            System.out.println("zah ne psotoji");
            return false;
        }
        /* if (!postojiKorisnik(userName)) {
            System.out.println("user ne psotoji");
            return false;
        }/*///4.477564102564102

        try (PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setString(1, userName);
            ps.executeUpdate();
            System.out.println("REQ Obrisano zah " + ps.getUpdateCount());
            return ps.getUpdateCount() == 1;

        } catch (SQLException ex) {
        }
        System.out.println("REQ Obrisi zah greska ");
        return false;

    }

    @Override
    public List<String> getAllCourierRequests() {

        String sql = "select KorisnickoIme\n"
                + "from Zahtev";
        System.out.println("REQ Dohvati  zah svw ");

        List<String> list = new LinkedList<>();

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
        System.out.println("REQ Greska dohvati zah ");
        return null;

    }

    @Override
    public boolean grantRequest(String username) {

        String licence = "";
        System.out.println("REQ odobri " + username);
        /*if (!postojiKorisnik(username)) {
            System.out.println("REQ user ne psotoji");
            return false;
        }*/
        if (!postojiZahtev(username)) {
            System.out.println("REQ zah ne psotoji");
            return false;
        }

        try (PreparedStatement stmt = connection.prepareStatement("select BrojDozvole from Zahtev where KorisnickoIme=?");) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery();) {

                if (rs.next()) {
                    licence = rs.getString(1);
                    ot160128_CourierOperations kurir = new ot160128_CourierOperations();
                    boolean valid = kurir.insertCourier(username, licence);
                    if (!valid) {
                        System.out.println("REQ kurir insert los ");
                        return false;
                    }
                    valid = deleteCourierRequest(username);
                    if (!valid) {
                        System.out.println("REQ delete postojeci los");
                        return false;
                    }
                    return true;                    

                } else {
                    return false;
                }

            } catch (SQLException ex) {

            }

            return false;

        } catch (SQLException ex) {

        }
        System.out.println("REQ greska grant");
        return false;

    }

    @Override
    public boolean insertCourierRequest(String userName, String driverLicenceNumber) {

        //getAllCourierRequests();
        boolean ret = false;
        String sql = "insert into Zahtev(KorisnickoIme,BrojDozvole) values(?,?)";
        System.out.println("REQ insert  zah " + userName);
        if (!postojiKorisnik(userName)) {
            System.out.println("REQ user ne psotoji");
            return false;
        }

        if (postojiZahtev(userName)) {
            System.out.println("REQ zahtev vez  psotoji");

            try (
                    PreparedStatement stmt = connection.prepareStatement("select BrojDozvole from Zahtev where KorisnickoIme=?");) {
                stmt.setString(1, userName);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {

                    if (rs.getString(1).equals(driverLicenceNumber)) {
                        System.out.println("REQ rpostoji vec isit TRYE?FALSE");
                        ret = false;
                    } else {
                        ret = false;
                        System.out.println("REQ postoji vec al drugi driverlic TRYE?FALSE");
                        // ret=changeDriverLicenceNumberInCourierRequest(userName, driverLicenceNumber);

                    }
                    //mozda treba folse a ne change
                }
                rs.close();
                return ret;

            } catch (SQLException ex) {

            }

            return false;

        }
        System.out.println("REQ ne postoji taj zah ");
        if (postojiBrVozacke(driverLicenceNumber)) {
            System.out.println("REQ postoji ta vozacksa ");
            return false;
        }

        try (PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setString(1, userName);
            ps.setString(2, driverLicenceNumber);

            ps.executeUpdate();

            // try (ResultSet rs = ps.getGeneratedKeys();) {
            if (ps.getUpdateCount() > 0) {
                System.out.println("REQ Novi ubac " );
                return true;
            } else {
                System.out.println("REQ nije ubac " );
               
                return false;
            }

            //} catch (SQLException ex) {
            //}
        } catch (SQLException ex) {
        }
        System.out.println("req greska ");
        return false;

    }

    private boolean postojiBrVozacke(String driverLicenceNumber) {
        try (
                PreparedStatement stmt = connection.prepareStatement("select KorisnickoIme from Zahtev where BrojDozvole=?");) {
            stmt.setString(1, driverLicenceNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                rs.close();
                return true;
            }
            rs.close();

        } catch (SQLException ex) {

        }
        try (
                PreparedStatement stmt1 = connection.prepareStatement("select KorisnickoIme from Kurir where BrojDozvole=?");) {
            stmt1.setString(1, driverLicenceNumber);
            ResultSet rs = stmt1.executeQuery();
            if (rs.next()) {
                rs.close();
                return true;
            }
            rs.close();

        } catch (SQLException ex) {

        }

        return false;
    }

}
