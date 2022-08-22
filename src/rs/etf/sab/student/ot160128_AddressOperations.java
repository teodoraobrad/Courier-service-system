/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import rs.etf.sab.operations.AddressOperations;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CityOperations;

/**
 *
 * @author Teodora
 */
public class ot160128_AddressOperations implements AddressOperations {

    Connection connection = ot160128_DB.getInstance().getConnection();

    private int dohvatiIdA(String naziv, int broj) {

        int ret = -1;
        try (
                PreparedStatement stmt = connection.prepareStatement("select IdA from Adresa where Ulica=? and Broj=?");) {
            stmt.setString(1, naziv);
            stmt.setInt(2, broj);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ret = rs.getInt(1);
            }
            rs.close();

        } catch (SQLException ex) {
        }

        return ret;

    }

    public boolean proveriPostojanjeA(int id) {

        boolean ret = false;
        try (
                PreparedStatement stmt = connection.prepareStatement("select IdG from Adresa where IdA=?");) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {

                ret = true;
            }
            rs.close();

        } catch (SQLException ex) {
        }

        return ret;
    }

    public int getIdCity(int ida) {
        int ret = -1;
        try (
                PreparedStatement stmt = connection.prepareStatement("select IdG from Adresa where IdA=?");) {
            stmt.setInt(1, ida);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ret = rs.getInt(1);
            }
            rs.close();

        } catch (SQLException ex) {
        }

        return ret;
    }

    public List<Integer> dohvatiSveAdreseIzGrada(int IdG) {

        String sql = "select IdA\n"
                + "from Adresa where IdG=?";
        System.out.println("ADRESA Dohvati  Adrese svw iz grada "); //

        List<Integer> list = new LinkedList<Integer>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);) {
            stmt.setInt(1, IdG);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(rs.getInt(1));
                System.out.println(" " + rs.getInt(1));//
            }
            rs.close();

            return list;

        } catch (SQLException ex) {
        }
        System.out.println("ADRESA Greska dohvati Adrese ");
        return null;
    }

    @Override
    public int deleteAddresses(String name, int number) {

        System.out.println("ADRESA Obrisi Adresu " + name + number);
        String sqlhelp = "select IdA from Adresa where Ulica=? and Broj=?";
        String sql = "delete from Adresa where Ulica=? and Broj=?";
        int ret = 0;

        List<Integer> ida = dohvatiIdAList(name, number);

        boolean postojipak = false;
        for (int i = 0; i < ida.size(); i++) {

            //ruta
            try (PreparedStatement ps = connection.prepareStatement("delete from Ruta where IdA=?");) {
                ps.setInt(1, ida.get(i));

                ps.executeUpdate();

            } catch (SQLException ex) {
                Logger.getLogger(ot160128_AddressOperations.class.getName()).log(Level.SEVERE, null, ex);
            }

            //PAKET, 
            ot160128_PackageOperations p = new ot160128_PackageOperations();
            try (PreparedStatement psh = connection.prepareStatement("select IdP from Paket where IdAPre=? or IdADos=? or IdATre=?");) {
                psh.setInt(1, ida.get(i));
                psh.setInt(2, ida.get(i));

                psh.setInt(3, ida.get(i));

                ResultSet rs1 = psh.executeQuery();
                while (rs1.next()) {
                    p.deletePackage(rs1.getInt(1));
                }
                rs1.close();

            } catch (SQLException ex) {
                Logger.getLogger(ot160128_AddressOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            ot160128_UserOperations user = new ot160128_UserOperations();
            user.brisiKorisnikeSaAdresom(ida.get(i));

            ot160128_StockroomOperations magacin = new ot160128_StockroomOperations();

            if (magacin.proveriPostojanje(ida.get(i))) {
                postojipak = magacin.deleteStockroom(ida.get(i));
                if (!postojipak) {
                    System.out.println("rs.etf.sab.student.ot160128_AddressOperations.deleteAddresses() greska kod brisanja magacina, nesto postoji ");
                }
            }

        }

        try (PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setString(1, name);
            ps.setInt(2, number);

            ps.executeUpdate();
            //ResultSet rs=
            //  while(rs.next()){
            //      deleteAdress(rs.getInt(1));
            //      ret++;
            // }
            // rs.close();
            System.out.println("ADRESA Obrisano adresa " + ps.getUpdateCount());
            //return ret;
            return ps.getUpdateCount();

        } catch (SQLException ex) {
        }
        System.out.println("ADRESA Obrisi Adresu greska ");
        return -1;

    }

    @Override
    public boolean deleteAdress(int idAddress) {
        String sql = "delete from Adresa where IdA=?";
        System.out.println("Obrisi Adresu " + idAddress);
        if (!proveriPostojanjeA(idAddress)) {
            return false;
        }
        System.out.println("Obrisi Adresu koja postoji");

        //ruta
        try (PreparedStatement ps = connection.prepareStatement("delete from Ruta where IdA=?");) {
            ps.setInt(1, idAddress);

            ps.executeUpdate();

        } catch (SQLException ex) {
        }

        //PAKET, 
        ot160128_PackageOperations p = new ot160128_PackageOperations();
        try (PreparedStatement psh = connection.prepareStatement("select IdP from Paket where IdAPre=? or IdADos=?");) {
            psh.setInt(1, idAddress);
            psh.setInt(2, idAddress);

            ResultSet rs1 = psh.executeQuery();
            while (rs1.next()) {
                p.deletePackage(rs1.getInt(1));
            }
            rs1.close();

        } catch (SQLException ex) {
        }

        ot160128_UserOperations user = new ot160128_UserOperations();
        user.brisiKorisnikeSaAdresom(idAddress);
        ot160128_StockroomOperations magacin = new ot160128_StockroomOperations();
        if (magacin.proveriPostojanje(idAddress)) {
            magacin.deleteStockroom(idAddress);
        }

        try (PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setInt(1, idAddress);

            ps.executeUpdate();

            System.out.println("ADRESA Obrisi Adresu " + ps.getUpdateCount());
            //return (ps.executeUpdate() == 0) ? false : true;
            return (ps.getUpdateCount() == 0) ? false : true;

        } catch (SQLException ex) {
        }
        System.out.println("ADRESA Obrisi Adresu GRESKA");
        return false;

    }

    @Override
    public int deleteAllAddressesFromCity(int idCity) {

        String sql = "delete from Adresa where IdG=?";
        System.out.println("ADRESA Obrisi Adresu iz grada " + idCity);

        List<Integer> lista = dohvatiSveAdreseIzGrada(idCity);
        for (int i = 0; i < lista.size(); i++) {

            //ruta
            try (PreparedStatement ps = connection.prepareStatement("delete from Ruta where IdA=?");) {
                ps.setInt(1, lista.get(i));

                ps.executeUpdate();

            } catch (SQLException ex) {
            }

            //PAKET, 
            ot160128_PackageOperations p = new ot160128_PackageOperations();
            try (PreparedStatement psh = connection.prepareStatement("select IdP from Paket where IdAPre=? or IdADos=?");) {
                psh.setInt(1, lista.get(i));
                psh.setInt(2, lista.get(i));

                ResultSet rs1 = psh.executeQuery();
                while (rs1.next()) {
                    p.deletePackage(rs1.getInt(1));
                }
                rs1.close();

            } catch (SQLException ex) {
            }

            ot160128_UserOperations user = new ot160128_UserOperations();
            user.brisiKorisnikeSaAdresom(lista.get(i));
            ot160128_StockroomOperations magacin = new ot160128_StockroomOperations();
            if (magacin.proveriPostojanje(lista.get(i))) {
                magacin.deleteStockroom(lista.get(i));
            }

        }

        try (PreparedStatement ps = connection.prepareStatement(sql);) {

            ps.setInt(1, idCity);
            ps.executeUpdate();
            System.out.println("ADRESA Obrisane  " + ps.getUpdateCount());

            return ps.getUpdateCount();

        } catch (SQLException ex) {
        }
        System.out.println("ADRESA Obrisi Adresu GRESKA");
        return -1;
    }

    @Override
    public List<Integer> getAllAddresses() {

        String sql = "select IdA\n"
                + "from Adresa";
        System.out.println("ADRESA Dohvati  Adrese sve ");

        List<Integer> list = new LinkedList<Integer>();

        try (Statement stmt = connection.createStatement()) {

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                list.add(rs.getInt(1));
                System.out.println(" " + rs.getInt(1));
            }
            rs.close();

            return list;

        } catch (SQLException ex) {
        }
        System.out.println("ADRESA Greska dohvati Adrese ");
        return null;
    }

    @Override
    public List<Integer> getAllAddressesFromCity(int idCity) {

        String sql = "select IdA\n"
                + "from Adresa where IdG=?";
        System.out.println("ADRESA Dohvati Adresu iz " + idCity);
        ot160128_CityOperations city = new ot160128_CityOperations();
        if (!city.proveriPostojanje(idCity)) {
            return null;
        }
        System.out.println("ADRESA postoji city ");

        List<Integer> list = new LinkedList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, idCity);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(rs.getInt(1));
                System.out.println("adr  " + rs.getInt(1));
            }

            rs.close();

            return list;

        } catch (SQLException ex) {
        }
        System.out.println("ADRESA Greska dohv Adresue ");
        return null;

    }

    @Override
    public int insertAddress(String street, int number, int cityId, int xCord, int yCord) {

        String sql = "insert into Adresa(Ulica,Broj,IdG,X,Y) values(?,?,?,?,?)";
        System.out.println("ADRESA insert  Adresu u " + cityId);

        ot160128_CityOperations city = new ot160128_CityOperations();
        if (!city.proveriPostojanje(cityId)) {
            System.out.println("greska id grada");
            return -1;
        }
        System.out.println("ADRESA postoji taj grad ");

        try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);) {
            ps.setString(1, street);
            ps.setInt(2, number);
            ps.setInt(3, cityId);
            ps.setInt(4, xCord);
            ps.setInt(5, yCord);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys();) {
                if (rs.next()) {
                    int ret = rs.getInt(1);
                    System.out.println("ADRESA Novi id " + ret);
                    rs.close();
                    return ret;
                }

            } catch (SQLException ex) {
            }

        } catch (SQLException ex) {
        }
        System.out.println("ADRESA greska insert");
        return -1;

    }

    private List<Integer> dohvatiIdAList(String name, int number) {

        System.out.println("ADRESA Dohvati  Adrese sve sa name,number ");
        List<Integer> list = new LinkedList<Integer>();

        try (PreparedStatement stmt = connection.prepareStatement("select IdA from Adresa where Ulica=? and Broj=?")) {

            stmt.setString(1, name);
            stmt.setInt(2, number);

            try (ResultSet rs = stmt.executeQuery();) {

                while (rs.next()) {
                    list.add(rs.getInt(1));
                    System.out.println(" " + rs.getInt(1));
                }
                rs.close();
            } catch (SQLException ex) {
            }

            return list;

        } catch (SQLException ex) {
        }
        System.out.println("ADRESA Greska dohvati Adrese ");
        return null;
    }

}
