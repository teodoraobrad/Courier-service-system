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
import rs.etf.sab.operations.UserOperations;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Teodora
 */
public class ot160128_UserOperations implements UserOperations {

    Connection connection = ot160128_DB.getInstance().getConnection();

    public boolean proveriKorisnickoIme(String s) {

        String sql = "select count(*) from Korisnik where KorisnickoIme=?";
        System.out.println("USER proveri kosisnicko " + s);
        boolean ret = false;
        try (PreparedStatement stmt = connection.prepareStatement(sql);) {
            stmt.setString(1, s);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {

                ret = (rs.getInt(1) == 0) ? false : true;

            }
            rs.close();
            if (ret) {
                System.out.println("User postoji ");
            } else {
                System.out.println("User ne postoji=");
            }
            return ret;

        } catch (SQLException ex) {
        }
        System.out.println("User ne postoji ");
        return ret;

    }

    private boolean proveraStringa(String original, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(original);
        return matcher.matches();
    }

    @Override
    public boolean declareAdmin(String userName) {
        System.out.println("USER Postani admin " + userName);
        //getAllUsers();

        boolean ret = false;
        try (PreparedStatement stmt = connection.prepareStatement("select count(*) from Admin where KorisnickoIme=?");) {
            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {

                ret = (rs.getInt(1) == 0) ? false : true;
            }
            rs.close();
            System.out.println("USER Postoji redova  " + ret);

        } catch (SQLException ex) {
        }

        if (ret) {
            System.out.println("USER Vec je admin " + ret);
            return false;
        }
        boolean valid = proveriKorisnickoIme(userName);
        if (!valid) {
            System.out.println("USER Korisnik ne postoji");
            return false;
        }
        System.out.println("USER Nie  vecadmin ");
        try (PreparedStatement ps = connection.prepareStatement("insert into Admin(KorisnickoIme)values(?)");) {
            ps.setString(1, userName);
            ps.executeUpdate();
            int r = ps.getUpdateCount();

            if (r > 0) {
                System.out.println("USER Novi id " + userName);
                return true;
            } else {
                System.out.println("USER fsil");
                return false;
            }

        } catch (SQLException ex) {
        }
        System.out.println("USER fsil");
        return false;

    }

    @Override
    public int deleteUsers(String... userNames) {
        System.out.println("USER Prosledjenih za brisanje " + userNames.length);

        //getAllUsers();
        int num = 0;

        String sql = "delete from Korisnik where KorisnickoIme=?";

        ot160128_CourierOperations kurir = new ot160128_CourierOperations();
        ot160128_CourierRequestOperation zah = new ot160128_CourierRequestOperation();
        ot160128_PackageOperations pak = new ot160128_PackageOperations();

        try (PreparedStatement ps = connection.prepareStatement(sql);) {

            for (String name : userNames) {

                boolean valid = proveriKorisnickoIme(name);

                if (valid) {

                    System.out.println("USER postoji pa ga brise");

                    /*  try(PreparedStatement ba=connection.prepareStatement("delete from Admin where KorisnickoIme=?");){
                        ba.setString(1, name);
                    }catch(SQLException a){
                      
                    } */
                    if (kurir.postojiKurir(name)) {
                        kurir.deleteCourier(name);
                    }
                    if (zah.postojiZahtev(name)) {
                        zah.deleteCourierRequest(name);
                    }
                    if (pak.postojiPaketOd(name)) {
                        pak.obrisiPaketOd(name);
                    }

                    ps.setString(1, name);
                   // ps.executeUpdate();

                    num+=ps.executeUpdate();

                    System.out.println("USER obrisan " + name);
                } else {
                    System.out.println("USER Ne postoji pa ga ne brise");
                }

            }

            System.out.println("USER Broj obrisanih " + num);
            return num;

        } catch (SQLException ex) {
        }
        System.out.println("USER fsil");
        return 0;
    }

    @Override
    public List<String> getAllUsers() {

        String sql = "select KorisnickoIme\n"
                + "from Korisnik";

        List<String> list = new LinkedList<>();
        System.out.println("USER dohvati sve");
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
        System.out.println("USER losa list");
        return null;

    }

    @Override
    public int getSentPackages(String... userNames) {
        System.out.println("USER Broj paketa");
        //getAllUsers();
        int num = 0;
        boolean valid = false;
        int nonvalid = 0;
        String sql = "select count(*) from Paket where KorisnickoImePos=?";

        try (PreparedStatement ps = connection.prepareStatement(sql);) {

            for (String name : userNames) {

                valid = proveriKorisnickoIme(name);

                if (valid) {
                    System.out.println("USER Ok korisnicko");

                    ps.setString(1, name);
                    System.out.println("USER poslao "+name);
                    try (ResultSet rs = ps.executeQuery();) {
                        num += rs.getInt(1);
                        System.out.println("paketa " + rs.getInt(1));
                    } catch (SQLException ex) {
                    }

                } else {
                    System.out.println("USER lose korisnicko");
                    nonvalid++;
                }

            }
            if (userNames.length == nonvalid) {
                System.out.println("USER lose korisnicka sva");
                return -1;
            }
            System.out.println("USER ukupan br paketa " + num);

            return num;

        } catch (SQLException ex) {
        }
        System.out.println("USER lfail");
        return -1;

    }

    @Override
    public boolean insertUser(String userName, String firstName, String lastName, String password, int idAddress) {
        System.out.println("USER insert  " + userName + " " + firstName + " " + lastName + " " + password + " " + idAddress);

        //getAllUsers();
        String sql = "insert into Korisnik(KorisnickoIme,Ime,Prezime,Sifra,IdA) values(?,?,?,?,?)";
        ot160128_AddressOperations addr = new ot160128_AddressOperations();
        if (!addr.proveriPostojanjeA(idAddress)) {
            System.out.println("USER losa adr");
            return false;
        }
        System.out.println("USER dobra adresa");

        if (proveriKorisnickoIme(userName)) {
            System.out.println("USER lose korisnicko");
            return false;
        }

        boolean prosloIme = proveraVeliko(firstName);
        boolean prosloPrezime = proveraVeliko(lastName);
        boolean proslaSifra = proveraStringa(password, "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[`~!@#$%^&*()_+[\\\\]\\\\\\\\;\\',./{}|:\\\"<>?]).{8,100}$");

        if (!(proslaSifra && prosloIme && prosloPrezime)) {
            System.out.println("USER losa sifra");
            return false;
        }

        try (PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setString(1, userName);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setString(4, password);
            ps.setInt(5, idAddress);

            ps.executeUpdate();
            System.out.println("USER dal je ubacen >" + ps.getUpdateCount());
            return (ps.getUpdateCount() == 0) ? false : true;

        } catch (SQLException ex) {
        }

        //System.out.println("fail");
        return false;

    }

    private boolean proveraVeliko(String firstName) {
        if (firstName.charAt(0) >= 'A' && firstName.charAt(0) <= 'Z') {
            return true;
        } else {
            return false;
        }
    }

    public void brisiKorisnikeSaAdresom(int ida) {

        String sql = "select KorisnickoIme from Korisnik where IdA=?";

        List<String> list = new LinkedList<>();
        System.out.println("USER pom dohvati sve za brisanje");
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, ida);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(rs.getString(1));
                System.out.println("USER dodat u listu=" + rs.getString(1));
            }
            rs.close();

        } catch (SQLException ex) {
        }

        if (list.size() == 0) {
            return;
        }

        for (int i = 0; i < list.size(); i++) {

            deleteUsers(list.get(i));
        }

    }

}
