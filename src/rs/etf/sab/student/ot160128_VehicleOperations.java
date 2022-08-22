/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

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
import rs.etf.sab.operations.VehicleOperations;

/**
 *
 * @author Teodora
 */
public class ot160128_VehicleOperations implements VehicleOperations {

    Connection connection = ot160128_DB.getInstance().getConnection();

    public Connection getConnection() {
        return connection;
    }

    @Override
    public boolean changeCapacity(String licensePlateNumber, BigDecimal capacity) {

        System.out.println("VOZILO kap");
        getAllVehichles();
        if (!proveriRegistraciju(licensePlateNumber)) {
            System.out.println("lose reg");
            return false;
        }

        if (proveriNosivost(licensePlateNumber, capacity)) {
            System.out.println("ista nosivost");
            return false;
        }

        if (!voziloUMagacinu(licensePlateNumber)) {
            System.out.println("nije u mag");
            return false;
        }

        try (PreparedStatement ps = connection.prepareStatement("update Vozilo set Nosivost=? where RegBr=?");) {
            ps.setBigDecimal(1, capacity);
            ps.setString(2, licensePlateNumber);

            ps.executeUpdate();
            System.out.println("kap promenjen");

            return ps.getUpdateCount() > 0;

        } catch (SQLException ex) {
            Logger.getLogger(ot160128_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    @Override
    public boolean changeConsumption(String licensePlateNumber, BigDecimal fuelConsumption) {

        System.out.println("VOZILO promeni pootr");
        getAllVehichles();
        if (!proveriRegistraciju(licensePlateNumber)) {
            System.out.println("lose reg");
            return false;
        }

        if (proveriPotrosnju(licensePlateNumber, fuelConsumption)) {
            System.out.println("ista pot");
            return false;
        }

        if (!voziloUMagacinu(licensePlateNumber)) {
            System.out.println("nije u mag");
            return false;
        }

        try (PreparedStatement ps = connection.prepareStatement("update Vozilo set Potrosnja=? where RegBr=?");) {
            ps.setBigDecimal(1, fuelConsumption);
            ps.setString(2, licensePlateNumber);

            ps.executeUpdate();
            System.out.println("promenjeo potr ");

            return ps.getUpdateCount() > 0;

        } catch (SQLException ex) {
            Logger.getLogger(ot160128_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("VOZILO fail");
        return false;
    }

    @Override
    public boolean changeFuelType(String licensePlateNumber, int fuelType) {
        System.out.println("VOZILO gorivo promeni");

        if (!proveriRegistraciju(licensePlateNumber)) {
            System.out.println("lose reg");
            return false;
        }

        if (proveriGorivo(licensePlateNumber, fuelType)) {
            System.out.println("lose gor");
            return false;
        }

        if (!voziloUMagacinu(licensePlateNumber)) {
            System.out.println("nije u mag");
            return false;
        }

        try (PreparedStatement ps = connection.prepareStatement("update Vozilo set Gorivo=? where RegBr=?");) {
            ps.setInt(1, fuelType);
            ps.setString(2, licensePlateNumber);

            ps.executeUpdate();
            System.out.println("gor promenjeno");
            return ps.getUpdateCount() > 0;

        } catch (SQLException ex) {
        }
        System.out.println("VOZILO fail");
        return false;
    }

    @Override
    public int deleteVehicles(String... licencePlateNumbers) {

        System.out.println("VOZILO Prosledjenih za brisanje " + licencePlateNumbers.length);
        getAllVehichles();

        int num = 0;

        String sql = "delete from Vozilo where RegBr=?";

        try (PreparedStatement ps = connection.prepareStatement(sql);) {

            for (String plate : licencePlateNumbers) {

                boolean valid = proveriRegistraciju(plate);
                
                ot160128_DriveOperation d=new ot160128_DriveOperation();
                d.deleteV(plate);

                if (valid) {

                    System.out.println("postoji pa ga brise");
                    ps.setString(1, plate);
                    ps.executeUpdate();

                    num++;

                    System.out.println("vozilo obrisano ");
                } else {
                    System.out.println("Ne postoji pa ga ne brise");
                }

            }

            System.out.println("Broj obrisanih " + num);
            return num;

        } catch (SQLException ex) {
        }
        System.out.println("VOZILO fsil");
        return 0;

    }

    @Override
    public List<String> getAllVehichles() {

        String sql = "select RegBr\n"
                + "from Vozilo";

        List<String> list = new LinkedList<>();
        System.out.println("VOZILO dohvati sve");
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
        System.out.println("VOZILO losa list");
        return null;
    }

    @Override
    public boolean insertVehicle(String licencePlateNumber, int fuelType, BigDecimal fuelConsumtion, BigDecimal capacity) {
        System.out.println("insert v " + licencePlateNumber + " " + fuelType + " " + fuelConsumtion.toString() + " " + capacity.toString());
        getAllVehichles();
        String sql = "insert into Vozilo(RegBr,Gorivo,Potrosnja,Nosivost) values(?,?,?,?)";

        if (proveriRegistraciju(licencePlateNumber)) {
            System.out.println("VOZILO lose reg");
            return false;
        }

        if (!(fuelType == 1 || fuelType == 0 || fuelType == 2)) {
            System.out.println("VOZILO lose gorivo");
            return false;
        }

        try (PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setString(1, licencePlateNumber);
            ps.setInt(2, fuelType);
            ps.setBigDecimal(3, fuelConsumtion);
            ps.setBigDecimal(4, capacity);

            ps.executeUpdate();
            System.out.println("VOZILO dal ubacen >" + ps.getUpdateCount());
            return (ps.getUpdateCount() == 0) ? false : true;

        } catch (SQLException ex) {
        }

        System.out.println("VOZILO fail");
        return false;

    }

    @Override
    public boolean parkVehicle(String licencePlateNumbers, int idStockroom) {
        System.out.println("park it" + licencePlateNumbers+" "+idStockroom);

        String sql = "select count(*) from Voznja v join Vozilo z on v.IdV=z.IdV where Gotova=0 and z.RegBr=?";

        if(!proveriRegistraciju(licencePlateNumbers)) {
            System.out.println("VOZILO ne postoji vozilo");
            return false;
        }
        ot160128_StockroomOperations mag=new ot160128_StockroomOperations();
        
        if(!mag.proveriPostojanje(idStockroom)) {
            System.out.println("VOZILO mag ne postoji");
            return false;
        }
        boolean ret = false;

        try (PreparedStatement stmt = connection.prepareStatement(sql);) {
            stmt.setString(1, licencePlateNumbers);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("odbar upit");
                ret = (rs.getInt(1) == 0) ? true : false;

                if (!ret) {
                    rs.close();
                    System.out.println("vozi se");
                    return ret;
                } else {
                    System.out.println("ne vozi se pa ok");
                }
            }
            rs.close();

        } catch (SQLException ex) {
        }
        sql = "update Vozilo set IdAmag=? where RegBr=?";
        if (ret) {

            try (PreparedStatement stmt = connection.prepareStatement(sql);) {
                stmt.setString(2, licencePlateNumbers);
                stmt.setInt(1, idStockroom);
                stmt.executeUpdate();
                System.out.println("parkirano u " + idStockroom);
                return stmt.getUpdateCount() > 0;

            } catch (SQLException ex) {
            }
        }

        return false;

    }

    private boolean proveriRegistraciju(String licencePlateNumber) {

        String sql = "select count(*) from Vozilo where RegBr=?";

        boolean ret = false;
        try (PreparedStatement stmt = connection.prepareStatement(sql);) {
            stmt.setString(1, licencePlateNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {

                ret = (rs.getInt(1) == 0) ? false : true;

            }
            rs.close();
            if (ret) {
                //System.out.println("User postoji ");
            }
            return ret;

        } catch (SQLException ex) {
        }
        //System.out.println("User ne postoji=");
        return ret;
    }

    private boolean proveriGorivo(String licensePlateNumber, int fuelType) {
        try (PreparedStatement ps = connection.prepareStatement("select Gorivo from Vozilo where RegBr=?");) {
            ps.setString(1, licensePlateNumber);

            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    if (rs.getInt(1) == fuelType) {
                        return true;//bilo je to
                    } else {
                        return false;
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(ot160128_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ot160128_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private boolean voziloUMagacinu(String licensePlateNumber) {

        try (PreparedStatement ps = connection.prepareStatement("select count(*) from Voznja voz join Vozilo v on v.IdV=voz.IdV where voz.Gotova=0 and v.RegBr=?");) {
            ps.setString(1, licensePlateNumber);

            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    if (rs.getInt(1) > 0) {
                        return false;//vozi se
                    } else {
                        return true;
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(ot160128_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ot160128_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private boolean proveriNosivost(String licensePlateNumber, BigDecimal nos) {
        try (PreparedStatement ps = connection.prepareStatement("select Nosivost from Vozilo where RegBr=?");) {
            ps.setString(1, licensePlateNumber);

            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    if (rs.getBigDecimal(1) == nos) {
                        return true;//bilo je to
                    } else {
                        return false;
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(ot160128_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ot160128_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private boolean proveriPotrosnju(String licensePlateNumber, BigDecimal fuelConsumption) {
        try (PreparedStatement ps = connection.prepareStatement("select Potrosnja from Vozilo where RegBr=?");) {
            ps.setString(1, licensePlateNumber);

            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    if (rs.getBigDecimal(1) == fuelConsumption) {
                        return true;//bilo je to
                    } else {
                        return false;
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(ot160128_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ot160128_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

}
