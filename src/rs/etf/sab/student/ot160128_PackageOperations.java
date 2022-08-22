/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import rs.etf.sab.operations.PackageOperations;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Teodora
 */
public class ot160128_PackageOperations implements PackageOperations {
    
    Connection connection = ot160128_DB.getInstance().getConnection();
    
    @Override
    public boolean acceptAnOffer(int packageId) {
        
        if (!(getDeliveryStatus(packageId) == 0)) {
            return false;
        }

        /* try (PreparedStatement ps1 = connection.prepareStatement("update Ponuda set Status=1 where IdP=?");) {
            ps1.setInt(1, packageId);

            ps1.executeUpdate();

            if (ps1.getUpdateCount() == 0) {
                return false;
            }

        } catch (SQLException ex) {
        }*/
        try (PreparedStatement ps2 = connection.prepareStatement("select Cena from Ponuda where IdP=?");) {
            
            ps2.setInt(1, packageId);
            
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) {
                
                try (PreparedStatement ps3 = connection.prepareStatement("update Paket set Cena=?, VremePrihvatanja=? where IdP=?");) {
                    ps3.setBigDecimal(1, rs2.getBigDecimal(1));
                    LocalDate currentDate = LocalDate.now();
                    
                    ps3.setDate(2, new Date(System.currentTimeMillis()));
                    ps3.setInt(3, packageId);
                    
                    ps3.executeUpdate();
                    rs2.close();
                    return ps3.getUpdateCount() > 0;
                    
                } catch (SQLException ex) {
                }
                
            } else {
                rs2.close();
                
                return false;
            }
            
        } catch (SQLException ex) {
        }
        
        return false;
    }
    
    @Override
    public boolean changeType(int packageId, int newType) {
        if (!proveriPaket(packageId)) {
            return false;
        }
        
        if (!(newType == 0 || newType == 1 || newType == 2 || newType == 3)) {
            return false;
        }
        if (!proveriKreiran(packageId)) {
            return false;
        }
        
        try (PreparedStatement ps = connection.prepareStatement("update Paket set Tip=? where IdP=?");) {
            ps.setInt(1, newType);
            ps.setInt(2, packageId);
            
            ps.executeUpdate();
            
            return (ps.getUpdateCount() > 0);
            
        } catch (SQLException ex) {
        }
        
        return false;
    }
    
    @Override
    public boolean changeWeight(int packageId, BigDecimal newWeight) {
        if (!proveriPaket(packageId)) {
            return false;
        }
        if (!proveriKreiran(packageId)) {
            return false;
        }
        if((newWeight.compareTo(BigDecimal.ZERO))==-1) return false;
        
        try (PreparedStatement ps = connection.prepareStatement("update Paket set Tezina=? where IdP=?");) {
            ps.setBigDecimal(1, newWeight);
            ps.setInt(2, packageId);
            
            ps.executeUpdate();
            
            return ps.getUpdateCount() > 0;
            
        } catch (SQLException ex) {
        }
        
        return false;
    }
    
    @Override
    public boolean deletePackage(int packageId) {
        
         String sqlq = "select IdP from Paket where IdP=? and (Status=0 or Status=4)";
        try (PreparedStatement stmt1 = connection.prepareStatement(sqlq);) {
            stmt1.setInt(1, packageId);
             ResultSet e = stmt1.executeQuery();
            if(e.next()){
                if(e.getInt(1)==packageId){
                    
                }
                else return false;
                            }else return false;

        } catch (SQLException ex) {
        }
        
        
        
        String sqlhelp = "delete from Ponuda where IdP=?";
        try (PreparedStatement stmt = connection.prepareStatement(sqlhelp);) {
            stmt.setInt(1, packageId);
            stmt.executeUpdate();
            
        } catch (SQLException ex) {
        }
        String sql = "delete from Paket where IdP=? and (Status=0 or Status=4)";
        try (PreparedStatement stmt = connection.prepareStatement(sql);) {
            stmt.setInt(1, packageId);
            stmt.executeUpdate();
            return stmt.getUpdateCount() > 0;
            
        } catch (SQLException ex) {
        }
        
        return false;
        
    }
    
    @Override
    public Date getAcceptanceTime(int packageId) {
        
        Date ret = null;
        int status = getDeliveryStatus(packageId);
        
        if (status == 0 || status == 4) {
            
            return ret;
        }

        //System.out.println("User ne postoji=");
        String sql = "select VremePrihvatanja from Paket where IdP=?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);) {
            stmt.setInt(1, packageId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                
                ret = rs.getDate(1);
                
            }
            rs.close();
            return ret;
            
        } catch (SQLException ex) {
        }
        //System.out.println("User ne postoji=");

        return ret;
        
    }
    
    @Override
    public List<Integer> getAllPackages() {
        
        String sql = "select IdP\n"
                + "from Paket";
        //System.out.println("Dohvati  Adrese svw ");

        List<Integer> list = new LinkedList<Integer>();
        
        try (Statement stmt = connection.createStatement()) {
            
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                list.add(rs.getInt(1));
                //System.out.println(" " + rs.getInt(1));
            }
            rs.close();
            
            return list;
            
        } catch (SQLException ex) {
        }
        //System.out.println("Greska dohvati Adrese ");
        return null;
    }
    
    @Override
    public List<Integer> getAllPackagesCurrentlyAtCity(int cityId) {
        
        String sql = "select fin.IdP from Paket fin where fin.IdP in ( \n"
                + "select p.IdP from Paket p join Adresa a on a.IdA=p.IdAPre where a.IdG=? and (p.Status=0 or p.Status=1 or p.Status=4)\n"
                + "union\n"
                + "select p1.IdP from Paket p1 join Adresa a1 on a1.IdA=p1.IdADos where a1.IdG=? and (p1.Status=3 )\n"
                + "union \n"
                + "select p2.IdP from Paket p2 join Adresa a2 on a2.IdA=p2.IdATre where a2.IdG=? and (p2.Status=2 and p2.Mag=1)\n"
                + ")";
        //System.out.println("Dohvati  Adrese svw ");

        List<Integer> list = new LinkedList<Integer>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);) {
            stmt.setInt(1, cityId);
            stmt.setInt(2, cityId);
            stmt.setInt(3, cityId);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                list.add(rs.getInt(1));
                //System.out.println(" " + rs.getInt(1));
            }
            rs.close();
            
            return list;
            
        } catch (SQLException ex) {
        }
        //System.out.println("Greska dohvati Adrese ");
        return null;
    }
    
    @Override
    public List<Integer> getAllPackagesWithSpecificType(int type) {
        
        String sql = "select IdP\n"
                + "from Paket where Tip=?";
        //System.out.println("Dohvati  Adrese svw ");

        List<Integer> list = new LinkedList<Integer>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, type);
            
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                list.add(rs.getInt(1));
                //System.out.println(" " + rs.getInt(1));
            }
            rs.close();
            
            return list;
            
        } catch (SQLException ex) {
        }
        //System.out.println("Greska dohvati Adrese ");
        return null;
    }
    
    @Override
    public List<Integer> getAllUndeliveredPackages() {
        
        String sql = "select IdP\n"
                + "from Paket where Status=1 or Status=2";
        //System.out.println("Dohvati  Adrese svw ");

        List<Integer> list = new LinkedList<Integer>();
        
        try (Statement stmt = connection.createStatement()) {
            
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                list.add(rs.getInt(1));
                //System.out.println(" " + rs.getInt(1));
            }
            rs.close();
            
            return list;
            
        } catch (SQLException ex) {
        }
        //System.out.println("Greska dohvati Adrese ");
        return null;
    }
    
    @Override
    public List<Integer> getAllUndeliveredPackagesFromCity(int cityId) {
        String sql = "select p.IdP\n"
                + "from Paket p join Adresa a on p.IdAPre=a.IdA where a.IdG=? and (p.Status=1 or p.Status=2)";
        //System.out.println("Dohvati  Adrese svw ");

        List<Integer> list = new LinkedList<Integer>();
        
        try (PreparedStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, cityId);
            stmt.setInt(2, cityId);
            
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                list.add(rs.getInt(1));
                //System.out.println(" " + rs.getInt(1));
            }
            rs.close();
            
            return list;
            
        } catch (SQLException ex) {
        }
        //System.out.println("Greska dohvati Adrese ");
        return null;
    }
    
    @Override
    public int getCurrentLocationOfPackage(int packageId) {
        String sql = "select count(*)\n"
                + "from Paket p join Voznja v on p.IdVoz=v.IdVoz \n"
                + "where p.IdP=? and v.Gotova=0";
        try (PreparedStatement stmt = connection.prepareStatement(sql);) {
            stmt.setInt(1, packageId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                
                if (rs.getInt(1) == 1) {
                    
                    rs.close();
                    return -1;
                }
                
            }
            rs.close();
            
        } catch (SQLException ex) {
        }
        
        sql = "select a.IdG\n"
                + "from Paket p join Adresa a on a.IdA=p.IdAPre\n"
                + "where p.IdP=? and (p.Status=1 or p.Status=0 or p.Status=4)\n"
                + "union\n"
                + "select a1.IdG\n"
                + "from Paket p1 join Adresa a1 on a1.IdA=p1.IdADos\n"
                + "where p1.IdP=? and (p.1Status=3)\n"
                + "union\n"
                + "select a2.IdG\n"
                + "from Paket p2 join Adresa a2 on a2.IdA=p2.IdATre\n"
                + "where p2.IdP=? and (p2.Status=2 and p2.Mag=1)";
        try (PreparedStatement stmt = connection.prepareStatement(sql);) {
            stmt.setInt(1, packageId);
            stmt.setInt(1, packageId);
            stmt.setInt(1, packageId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                
                int i = rs.getInt(1);
                
                rs.close();
                return i;
                
            }
            rs.close();
            
        } catch (SQLException ex) {
        }
        return -1;
        
    }
    
    @Override
    public int getDeliveryStatus(int packageId) {
        String sql = "select Status from Paket where IdP=?";
        
        int ret = 0;
        try (PreparedStatement stmt = connection.prepareStatement(sql);) {
            stmt.setInt(1, packageId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                
                ret = rs.getInt(1);
                
            }
            rs.close();
            
            return ret;
            
        } catch (SQLException ex) {
        }
        //System.out.println("User ne postoji=");
        return ret;
    }
    
    @Override
    public BigDecimal getPriceOfDelivery(int packageId) {
        String sql = "select Cena from Paket where IdP=?";
        
        BigDecimal ret = BigDecimal.ZERO;
        try (PreparedStatement stmt = connection.prepareStatement(sql);) {
            stmt.setInt(1, packageId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                
                ret = rs.getBigDecimal(1);
                rs.close();
            
            return ret;
                
            }
            
            
        } catch (SQLException ex) {
        }
        //System.out.println("User ne postoji=");
        return ret;
    }
    
    @Override
    public int insertPackage(int addressFrom, int addressTo, String userName, int packageType, BigDecimal weight) {
        
        BigDecimal w;
        ot160128_AddressOperations addr = new ot160128_AddressOperations();
        ot160128_UserOperations user = new ot160128_UserOperations();
        
        if (!addr.proveriPostojanjeA(addressFrom)) {
            return -1;
        }
        if (!addr.proveriPostojanjeA(addressTo)) {
            return -1;
        }
        if (!user.proveriKorisnickoIme(userName)) {
            return -1;
        }
        
        if (!(packageType == 0 || packageType == 1 || packageType == 2 || packageType == 3)) {
            return -1;
        }
        /*  if (weight == null ) {//|| weight == BigDecimal.ZERO
            w = BigDecimal.valueOf(10.0);
        } else {
            w = weight;
        }*/
        
        try (PreparedStatement ps = connection.prepareStatement("insert into Paket(IdAPre,IdADos,KorisnickoImePos,Tip,Tezina) values(?,?,?,?,COALESCE(?,?))", PreparedStatement.RETURN_GENERATED_KEYS);) {
            ps.setInt(1, addressFrom);
            ps.setInt(2, addressTo);
            ps.setString(3, userName);
            ps.setInt(4, packageType);
            ps.setBigDecimal(5, weight);
            ps.setBigDecimal(6, BigDecimal.valueOf(10.0));
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int ret =rs.getInt(1);
                rs.close();
                        
                return ret ;
            }
            rs.close();
            return -1;
            
        } catch (SQLException ex) {
        }
        
        return -1;
        
    }
    
    @Override
    public boolean rejectAnOffer(int packageId) {
        try (PreparedStatement ps = connection.prepareStatement("select Status from Paket where IdP=?");) {
            ps.setInt(1, packageId);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getInt(1) == 0) {
                    rs.close();
                } else {
                    rs.close();
                    return false;
                }
            } else {
                return false;
            }
            
        } catch (SQLException ex) {
        }
        
        try (PreparedStatement ps3 = connection.prepareStatement("update Paket set Status=4 where IdP=?");) {
            
            ps3.setInt(1, packageId);
            
            ps3.executeUpdate();
            
            return ps3.getUpdateCount() > 0;
            
        } catch (SQLException ex) {
        }
        
        return false;
    }
    
    boolean postojiPaketOd(String name) {
        String sql = "select count(*) from Paket where KorisnickoImePos=?";
        
        boolean ret = false;
        try (PreparedStatement stmt = connection.prepareStatement(sql);) {
            stmt.setString(1, name);
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
    
    void obrisiPaketOd(String name) {
        String sql = "select IdP from Paket where KorisnickoImePos=?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                
                String sqlhelp = "delete from Ponuda where IdP=?";
                try (PreparedStatement stmt1 = connection.prepareStatement(sqlhelp);) {
                    stmt.setInt(1, rs.getInt(1));
                    stmt.executeUpdate();
                    
                } catch (SQLException ex) {
                }
                String sql1 = "delete from Paket where IdP=?";
                try (PreparedStatement stmt2 = connection.prepareStatement(sql1);) {
                    stmt.setInt(1, rs.getInt(1));
                    stmt.executeUpdate();
                    
                } catch (SQLException ex) {
                }
                
            }
            rs.close();
            
        } catch (SQLException ex) {
        }
        //System.out.println("User ne postoji=");
    }
    
    private boolean proveriPaket(int packageId) {
        String sql = "select count(*) from Paket where IdP=?";
        
        boolean ret = false;
        try (PreparedStatement stmt = connection.prepareStatement(sql);) {
            stmt.setInt(1, packageId);
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
    
    private boolean proveriKreiran(int packageId) {
        if (getDeliveryStatus(packageId) == 0) {
            return true;
        } else {
            return false;
        }
    }
    
}
