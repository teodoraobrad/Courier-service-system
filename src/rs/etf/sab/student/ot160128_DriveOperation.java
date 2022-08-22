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
import rs.etf.sab.operations.DriveOperation;
import java.util.List;

/**
 *
 * @author Teodora
 */
public class ot160128_DriveOperation implements DriveOperation {

    Connection connection = ot160128_DB.getInstance().getConnection();

    @Override
    public List<Integer> getPackagesInVehicle(String courierUsername) {
        String sql = "select p.IdP\n"
                + "from Paket p join Voznja v on p.IdVoz=v.IdVoz\n"
                + "where v.KorisnickoIme=? and v.Gotova=0 and p.Status=2";

        List<Integer> list = new LinkedList<Integer>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, courierUsername);

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
    public int nextStop(String courierUsername) {
        int ret = -1;
        //String kurir = "";
        String sql = "select IdVoz,BrojIsporucenihPaketa,Profit,Distanca from Voznja where KorisnickoIme=? and Gotova=0";
        int idvoz = 0;
        int brpak = 0;
        BigDecimal profit = BigDecimal.ZERO;
        BigDecimal dist = BigDecimal.ZERO;

        try (PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setString(1, courierUsername);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                idvoz = rs.getInt(1);
                brpak = rs.getInt(2);
                // nosi = rs.getBigDecimal(3);
                profit = rs.getBigDecimal(3);
                dist = rs.getBigDecimal(4);
            } else {

                rs.close();
                return ret;
            }
            rs.close();

        } catch (SQLException s) {
        }

        int rbrStari = 0;
        rbrStari = sledeca(idvoz);

        if (rbrStari == -1) {

            //nema sl stanice
            return -1;
        }

        sql = "select IdA,Preuzima from Ruta where IdVoz=? and RBR=?";
        int proslaStanicaIdA = 0, preuz = -1;
        try (PreparedStatement ps3 = connection.prepareStatement(sql);) {
            ps3.setInt(1, idvoz);
            ps3.setInt(2, rbrStari);

            ResultSet rs = ps3.executeQuery();
            if (rs.next()) {
                proslaStanicaIdA = rs.getInt(1);
                preuz = rs.getInt(2);
            } else {
                ret = -3;
                rs.close();

                return ret;//gresa,
            }
            rs.close();

        } catch (SQLException s) {
        }

        brisiRutu(rbrStari, idvoz);

        int rbrNovi = sledeca(idvoz);

        sql = "select IdA,Preuzima from Ruta where IdVoz=? and RBR=?";
        int novaStanicaIdA = 0, preuzNovo = -1;
        try (PreparedStatement ps3 = connection.prepareStatement(sql);) {
            ps3.setInt(1, idvoz);
            ps3.setInt(2, rbrNovi);

            ResultSet rs = ps3.executeQuery();
            if (rs.next()) {
                novaStanicaIdA = rs.getInt(1);
                preuzNovo = rs.getInt(2);
            } else {
                ret = -3;
                rs.close();

                return ret;//gresa,
            }
            rs.close();

        } catch (SQLException s) {
        }

        ot160128_StockroomOperations mag = new ot160128_StockroomOperations();
        if (mag.proveriPostojanje(novaStanicaIdA)) {

            sql = "update Paket set IdATre=? where IdVoz=? and Status=2 and Mag=1";

            try (PreparedStatement ps10 = connection.prepareStatement(sql);) {
                ps10.setInt(1, novaStanicaIdA);
                ps10.setInt(2, idvoz);

                ps10.executeUpdate();

            } catch (SQLException s) {
            }

            int din = dohvatiGorivo(idvoz);
            if (din == 0) {
                din = 15;
            } else if (din == 1) {
                din = 32;
            } else if (din == 2) {
                din = 36;
            }
            BigDecimal nova = dist.add(BigDecimal.valueOf(dohvatiKM(proslaStanicaIdA, novaStanicaIdA)));
            BigDecimal isporuka = profit.subtract(nova.multiply(BigDecimal.valueOf(din)));

            sql = "update Voznja set Distanca=?,Profit=?,Gotova=1,BrojIsporucenihPaketa=? where IdVoz=? ";

            try (PreparedStatement ps11 = connection.prepareStatement(sql);) {
                ps11.setBigDecimal(1, nova);
                ps11.setBigDecimal(2, isporuka);
                ps11.setInt(3, brpak);
                ps11.setInt(4, idvoz);

                ps11.executeUpdate();

            } catch (SQLException s) {
            }

            sql = "update Kurir set Status=0,Profit=Profit+?,BrojPaketa=BrojPaketa+? where KorisnickoIme=? ";

            try (PreparedStatement ps11 = connection.prepareStatement(sql);) {
                ps11.setBigDecimal(1, isporuka);
                ps11.setInt(2, brpak);
                ps11.setString(3, courierUsername);

                ps11.executeUpdate();

            } catch (SQLException s) {
            }

            return -1;

            //dosli smo u mag na kraju, moze biti err
        }
       
        if (preuzNovo == 1) {

//preuzimanje
            sql = "select IdP from Paket where IdVoz=? and Status=1 and IdAPre=?";
            int idp = 0;
            try (PreparedStatement ps6 = connection.prepareStatement(sql);) {
                ps6.setInt(1, idvoz);
                ps6.setInt(2, novaStanicaIdA);

                ResultSet rs = ps6.executeQuery();
                while (rs.next()) {
                    idp = rs.getInt(1);
                    staviStatusuPaket(2, idp);
                }
                rs.close();

            } catch (SQLException s) {
            }

            ret = -2;

        }

        int isp = 0;
        sql = "select IdP,Cena from Paket where IdVoz=? and Status=2 and IdADos=?";
        int idp = 0;
        BigDecimal cena = BigDecimal.ZERO;

        try (PreparedStatement ps4 = connection.prepareStatement(sql);) {
            ps4.setInt(1, idvoz);
            ps4.setInt(2, novaStanicaIdA);

            ResultSet rs = ps4.executeQuery();
            while (rs.next()) {

                idp = rs.getInt(1);
                cena.add(rs.getBigDecimal(2));

                ret = idp;
                isp++;

                staviStatusuPaket(3, idp);

            }

            float predjeno = dohvatiKM(proslaStanicaIdA, novaStanicaIdA);

            sql = "update Voznja set Profit=?, BrojIsporucenihPaketa=?, Distanca=?"
                    + "where IdVoz=?";

            try (PreparedStatement ps8 = connection.prepareStatement(sql);) {
                ps8.setInt(4, idvoz);
                ps8.setInt(2, isp + brpak);
                ps8.setBigDecimal(1, profit.add(cena));
                ps8.setBigDecimal(3, dist.add(BigDecimal.valueOf(predjeno)));

                ps8.executeUpdate();

            } catch (SQLException s) {
            }

        } catch (SQLException s) {
        }

        return ret;

    }

    @Override
    public boolean planingDrive(String courierUsername) {

        if (daLiVecVozi(courierUsername)) {
            return false;
        }

        int idv = preuzmiVozilo(courierUsername);
        if (idv == -1) {
            return false;
        }

        int idvoz = kreirajVoznju(courierUsername, idv);
        if (idvoz == -1) {
            return false;
        }

        List<Integer> list = listaMozeUzeti(courierUsername);
        List<Integer> preuzeti = new LinkedList<>();
        // List<Integer> preuzetiMag = new LinkedList<>();

        BigDecimal nosivost = dohvatiNosivostVozila(idv);
        BigDecimal trenutnoNosi = BigDecimal.ZERO;
        //boolean pun = false;

        if (nosivost == BigDecimal.ZERO) {
            return false;
        }

        int trenutnaadr = dohvatiStart(idv);
        int ida;
        // int rbr = 1;
        ubaciRutu(idvoz, trenutnaadr, 0);

        for (int i = 0; i < list.size() && trenutnoNosi.doubleValue() < nosivost.doubleValue(); i++) {

            BigDecimal t = dohvatiTezinuPaketa(list.get(i));
            if (t.doubleValue() >= 0) {
                BigDecimal saber = trenutnoNosi.add(t);
                if ((saber.doubleValue()) < nosivost.doubleValue()) {
                    trenutnoNosi = saber;

                    // boolean preuzeto = preuzmiPaket(idvoz, list.get(i));
                    //  if (preuzeto) {
                    preuzeti.add(list.get(i));
                    ida = dohvatiAdresuPaketa(list.get(i));
                    staviIdVozuPaket(idvoz, list.get(i));
                    ubaciRutu(idvoz, ida, 1);
                    trenutnaadr = ida;
                    //   rbr++;
                    //}

                }

            }
        }

        /*  for (int i = 0; i < preuzeti.size(); i++) {
            int ida = sledeciMinPoEuklidu(trenutnaadr, preuzeti);
            if (ubaciRutu(rbr, idvoz, ida,1)) {
                trenutnaadr = ida;
            }
            rbr++;

        }*/
        int mag = dohvatiStart(idv);
        boolean preuzetoMag = false;
        if (nosivost.doubleValue() > trenutnoNosi.doubleValue()) { //ide do magacina

            list = listaMozeUzetiMagacin(courierUsername);

            for (int i = 0; i < list.size() && trenutnoNosi.doubleValue() < nosivost.doubleValue(); i++) {

                BigDecimal t = dohvatiTezinuPaketa(list.get(i));
                if (t.doubleValue() >= 0) {

                    if ((trenutnoNosi.add(t)).doubleValue() < nosivost.doubleValue()) {
                        trenutnoNosi = trenutnoNosi.add(t);

                        // preuzetoMag = preuzmiPaketIzMag(idvoz, list.get(i), trenutnoNosi);
                        // if (preuzetoMag) {
                        staviIdVozuPaket(idvoz, list.get(i));
                        skloniIdATreMag(list.get(i));
                        preuzeti.add(list.get(i));
                        // }

                    }

                }
            }
            if (!ubaciRutu(idvoz, mag, 1)) {

                return false;
            }

            // rbr++;
            trenutnaadr = mag;

        }

        //preuzti, pravi rutu isporuke
        List<Integer> izGrada = new LinkedList<>();
        ot160128_AddressOperations adr = new ot160128_AddressOperations();

        int gradStari = adr.getIdCity(trenutnaadr);
        int gradNovi;//= adr.getIdCity(trenutnaadr);
        int minind;
        int iddos;
        for (; preuzeti.size() > 0;) {

            minind = nadjiMinRazdaljinu(trenutnaadr, preuzeti);
            int minIdP = preuzeti.get(minind);
            iddos = dohvatiAdresuPaketaDos(minIdP);
            gradNovi = adr.getIdCity(iddos);

            if (gradNovi != gradStari && gradStari != adr.getIdCity(dohvatiStart(idv))) {

                izGrada = dohvatiPaketeZaPreuzimanjeIzGrada(gradStari);
                for (int i = 0; i < izGrada.size() && trenutnoNosi.doubleValue() < nosivost.doubleValue(); i++) {

                    BigDecimal t = dohvatiTezinuPaketa(izGrada.get(i));
                    if (t.doubleValue() >= 0) {

                        if ((trenutnoNosi.add(t)).doubleValue() < nosivost.doubleValue()) {
                            trenutnoNosi = t.add(trenutnoNosi);

                            // boolean preuzeto = preuzmiPaket(idvoz, izGrada.get(i), trenutnoNosi);
                            //if (preuzeto) {
                            staviZaUMag(izGrada.get(i));
                            ubaciRutu(idvoz, dohvatiAdresuPaketa(izGrada.get(i)), 1);
                            trenutnaadr = dohvatiAdresuPaketa(izGrada.get(i));

                            //}
                        }

                    }

                }
                gradStari = adr.getIdCity(trenutnaadr);
                minind = nadjiMinRazdaljinu(trenutnaadr, preuzeti);
                minIdP = preuzeti.get(minind);
                iddos = dohvatiAdresuPaketaDos(minIdP);
                gradNovi = adr.getIdCity(iddos);
            }

            ubaciRutu(idvoz, iddos, 0);
            trenutnaadr = iddos;
            trenutnoNosi.subtract(dohvatiTezinuPaketa(minIdP));

            /* 

            }*/
            list.remove(minind);
        }

        ///planiraj povratak u magacin i ostavljanje paketa
        ubaciRutu(idvoz, mag, 0);

        return true;

        //if(!trenutnoNosiStavi(idvoz,trenutnoNosi))
        //  return false;
    }

    private List<Integer> dohvatiPaketeZaPreuzimanjeIzGrada(int idgrada) {
        String sql = "select p.IdP\n"
                + "from Paket p join Adresa a on p.IdAPre=a.IdA\n"
                + "where p.Status=1 and a.IdG=?\n"
                + "order by p.VremePrihvatanja";
        //System.out.println("Dohvati  Adrese svw ");

        List<Integer> list = new LinkedList<Integer>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);) {
            stmt.setInt(1, idgrada);
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

    private int preuzmiVozilo(String courierUsername) {
        String sqlPreuzimaVozilo = "select top 1 v.IdV \n"
                + "from Vozilo v join Adresa a on v.IdAmag=a.IdA\n"
                + "where a.IdG=(select adr.IdG from Korisnik k join Adresa adr on adr.IdA=k.IdA where k.KorisnickoIme=?)";

        try (PreparedStatement stmt = connection.prepareStatement(sqlPreuzimaVozilo);) {

            stmt.setString(1, courierUsername);
            ResultSet rs = stmt.executeQuery(sqlPreuzimaVozilo);
            if (rs.next()) {

                rs.close();
                return rs.getInt(1);

            }
            rs.close();

        } catch (SQLException ex) {
        }
        return -1;
    }

    private int kreirajVoznju(String courierUsername, int idv) {

        String sqlKreiraj = "insert into Voznja (KorisnickoIme,IdV,Gotova) values (?,?,0)";

        try (PreparedStatement stmt = connection.prepareStatement(sqlKreiraj, PreparedStatement.RETURN_GENERATED_KEYS);) {
            stmt.setString(1, courierUsername);
            stmt.setInt(2, idv);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int ret = rs.getInt(1);
                rs.close();

                sqlKreiraj = "update Kurir set Status=1 where KorisnickoIme=?";

                try (PreparedStatement stmt1 = connection.prepareStatement(sqlKreiraj);) {
                    stmt1.setString(1, courierUsername);

                    stmt1.executeUpdate();

                } catch (SQLException ex) {
                }

                return ret;

            }
            rs.close();

        } catch (SQLException ex) {
        }

        return -1;

    }

    private List<Integer> listaMozeUzeti(String courierUsername) {
        String sql = "select p.IdP\n"
                + "from Paket p join Adresa a on p.IdAPre=a.IdA\n"
                + "where p.Status=1 and a.IdG=(select adr.IdG from Korisnik k join Adresa adr on k.IdA=a.IdA where k.KorisnickoIme=?)\n"
                + "order by p.VremePrihvatanja";
        //System.out.println("Dohvati  Adrese svw ");

        List<Integer> list = new LinkedList<Integer>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);) {
            stmt.setString(1, courierUsername);
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

    private BigDecimal dohvatiNosivostVozila(int idv) {
        String sql = "select Nosivost from Vozilo where IdV=?";

        try (PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setInt(1, idv);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                BigDecimal ret = rs.getBigDecimal(1);
                rs.close();
                return ret;

            }
            rs.close();

        } catch (SQLException ex) {
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal dohvatiTezinuPaketa(int get) {
        String sql = "select Tezina from Paket where IdP=?";

        try (PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setInt(1, get);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                BigDecimal rey = rs.getBigDecimal(1);
                rs.close();
                return rey;

            }
            rs.close();

        } catch (SQLException ex) {
        }
        return BigDecimal.valueOf(-1);
    }

    /* private boolean preuzmiPaket(int idvoz, int get, BigDecimal t) {

        String sqlKreiraj = "update Voznja set Nosi=? where IdVoz=?";

        try (PreparedStatement stmt = connection.prepareStatement(sqlKreiraj);) {
            stmt.setBigDecimal(1, t);
            stmt.setInt(2, idvoz);

            int rs = stmt.executeUpdate();
            if (rs == 1) {
                if (staviIdVozuPaket(idvoz, get) && staviStatusuPaket(2, get)) {
                    return true;
                }

            }

        } catch (SQLException ex) {
        }
        return false;

    }
     */
    private boolean staviIdVozuPaket(int idvoz, int get) {
        String sqlKreiraj = "update Paket set IdVoz=? where IdP=?";

        try (PreparedStatement stmt = connection.prepareStatement(sqlKreiraj);) {
            stmt.setInt(1, idvoz);
            stmt.setInt(2, get);

            int rs = stmt.executeUpdate();
            if (rs == 1) {
                return true;
            }

        } catch (SQLException ex) {
        }
        return false;
    }

    private boolean staviStatusuPaket(int st, int id) {
        String sqlKreiraj = "update Paket set Status=? where IdP=?";

        try (PreparedStatement stmt = connection.prepareStatement(sqlKreiraj);) {
            stmt.setInt(1, st);
            stmt.setInt(2, id);

            int rs = stmt.executeUpdate();
            if (rs == 1) {
                return true;

            }

        } catch (SQLException ex) {
        }
        return false;
    }

    /* private boolean trenutnoNosiStavi(int idvoz, BigDecimal trenutnoNosi) {
        String sqlKreiraj = "update Voznja set Nosi=? where IdVoz=?";

        try (PreparedStatement stmt = connection.prepareStatement(sqlKreiraj);) {
            stmt.setBigDecimal(1, trenutnoNosi);
            stmt.setInt(2, idvoz);

            int rs = stmt.executeUpdate();
            if (rs == 1) {
                return true;

            }

        } catch (SQLException ex) {
        }
        return false;
    }*/
    private List<Integer> listaMozeUzetiMagacin(String courierUsername) {
        String sql = "select p.IdP\n"
                + "from Paket p join Adresa a on p.IdATre=a.IdA\n"
                + "where p.Status=2 and a.IdG=(select adr.IdG from Korisnik k join Adresa adr on k.IdA=adr.IdA where k.KorisnickoIme=?)\n"
                + "order by p.VremePrihvatanja";
        //System.out.println("Dohvati  Adrese svw ");
//ILI ADR OD VOZILA?
        List<Integer> list = new LinkedList<Integer>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);) {
            stmt.setString(1, courierUsername);
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

    private boolean preuzmiPaketIzMag(int idvoz, int idp, BigDecimal trenutnoNosi) {
        String sqlKreiraj = "update Voznja set Nosi=? where IdVoz=?";

        try (PreparedStatement stmt = connection.prepareStatement(sqlKreiraj);) {
            stmt.setBigDecimal(1, trenutnoNosi);
            stmt.setInt(2, idvoz);

            int rs = stmt.executeUpdate();
            if (rs == 1) {
                if (skloniIdATreMag(idp)) {
                    return true;
                }

            }

        } catch (SQLException ex) {
        }
        return false;
    }

    private boolean skloniIdATreMag(int get) {
        String sqlKreiraj = "update Paket set Mag=0 and IdATre=NULL where IdP=?";

        try (PreparedStatement stmt = connection.prepareStatement(sqlKreiraj);) {

            stmt.setInt(1, get);

            int rs = stmt.executeUpdate();
            if (rs == 1) {
                return true;

            }

        } catch (SQLException ex) {
        }
        return false;
    }

    private int dohvatiStart(int idv) {
        String sql = "select IdAmag from Vozilo where IdV=?";

        try (PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setInt(1, idv);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int ret = rs.getInt(1);
                rs.close();
                return ret;

            }
            rs.close();

        } catch (SQLException ex) {
        }
        return -1;
    }

    private int dohvatiAdresuPaketa(int get) {
        String sql = "select IdAPre from Paket where IdP=?";

        try (PreparedStatement stmt = connection.prepareStatement(sql);) {

            stmt.setInt(1, get);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int ret = rs.getInt(1);
                rs.close();
                return ret;

            }
            rs.close();

        } catch (SQLException ex) {
        }
        return -1;
    }

    private boolean ubaciRutu(int idvoz, int ida, int i) {

        String sql = "select MIN(RBR) from Ruta where IdVoz=?";
        String sql1 = "insert into Ruta (RBR,IdVoz,IdA,Preuzima) values(?,?,?,?)";

        int rbr;

        try (PreparedStatement stmt = connection.prepareStatement(sql);) {

            stmt.setInt(1, idvoz);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                rbr = rs.getInt(1) + 1;

            } else {
                rbr = 1;
            }
            rs.close();

            try (PreparedStatement stmt1 = connection.prepareStatement(sql1);) {

                stmt1.setInt(1, rbr);
                stmt1.setInt(2, idvoz);
                stmt1.setInt(3, ida);
                stmt1.setInt(4, i);

                return (stmt.executeUpdate() > 0) ? true : false;

            } catch (SQLException ex) {
            }

        } catch (SQLException ex) {
        }
        return false;

    }

    private int nadjiMinRazdaljinu(int trenutnaadr, List<Integer> list) {

        int len = list.size();
        int minNiz[];
        minNiz = new int[len];

        for (int i = 0; i < len; i++) {

            minNiz[i] = zoviFunk(trenutnaadr, dohvatiAdresuPaketaDos(list.get(i)));

        }
        int min = minNiz[0];
        int minind = 0;
        for (int i = 1; i < len; i++) {
            if (min > minNiz[i] && minNiz[i] >= 0) {
                min = minNiz[i];
                minind = i;
            }
        }
        return minind;

    }

    private int zoviFunk(int trenutnaadr, int dohvatiAdresuPaketa) {

        String sql = "SELECT dbo.euklidskaDistanca (?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, trenutnaadr);
            ps.setInt(2, dohvatiAdresuPaketa);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int r = rs.getInt(1);
                rs.close();
                return r;
            } else {
                rs.close();
                return -1;
            }
        } catch (Exception e) {
        }
        return -1;

    }

    private int dohvatiAdresuPaketaDos(int get) {
        String sql = "select IdADos from Paket where IdP=?";

        try (PreparedStatement stmt = connection.prepareStatement(sql);) {

            stmt.setInt(1, get);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int ret = rs.getInt(1);
                rs.close();

                return ret;

            }
            rs.close();

        } catch (SQLException ex) {
        }
        return -1;
    }

    private boolean staviZaUMag(int get) {
        String sqlKreiraj = "update Paket set Mag=1  where IdP=?";

        try (PreparedStatement stmt = connection.prepareStatement(sqlKreiraj);) {

            stmt.setInt(1, get);

            int rs = stmt.executeUpdate();
            if (rs == 1) {

                return true;

            }

        } catch (SQLException ex) {
        }
        return false;
    }

    private int sledeca(int idv) {
        String sql = "select min(RBR) from Ruta where IdVoz=? ";
        int rbr = 0;
        try (PreparedStatement ps1 = connection.prepareStatement(sql);) {
            ps1.setInt(1, idv);

            ResultSet rs = ps1.executeQuery();
            if (rs.next()) {
                rbr = rs.getInt(1);
            } else {
                rs.close();
                return -1;
            }
            rs.close();
            return rbr;

        } catch (SQLException s) {
        }
        return -1;
    }

    private void brisiRutu(int rbr, int idv) {
        String sql = "delete from Ruta where RBR=? and IdVoz=?";

        try (PreparedStatement ps1 = connection.prepareStatement(sql);) {
            ps1.setInt(1, rbr);
            ps1.setInt(2, idv);

            ps1.executeUpdate();

        } catch (SQLException s) {
        }

    }

    private boolean daLiVecVozi(String courierUsername) {
        boolean ret;
        String sqlPreuzimaVozilo = "select count(*) from Voznja where Gotovo=0 and KorisnickoIme=?";

        try (PreparedStatement stmt = connection.prepareStatement(sqlPreuzimaVozilo);) {

            stmt.setString(1, courierUsername);
            ResultSet rs = stmt.executeQuery(sqlPreuzimaVozilo);
            if (rs.next()) {

                if (rs.getInt(1) == 0) {
                    ret = false;
                } else {
                    ret = true;
                }
                rs.close();
                return ret;

            }
            rs.close();

        } catch (SQLException ex) {
        }
        return false;
    }

    private float dohvatiKM(int proslaStanicaIdA, int novaStanicaIdA) {

        String sql = "SELECT dbo.euklidskaDistanca (?,?)";

        try (PreparedStatement ps1 = connection.prepareStatement(sql);) {
            ps1.setInt(1, proslaStanicaIdA);
            ps1.setInt(2, novaStanicaIdA);

            ResultSet ex = ps1.executeQuery();
            if (ex.next()) {
                float re = ex.getFloat(1);
                ex.close();
                return re;
            }
            ex.close();

        } catch (SQLException s) {
        }
        return 0;

    }

    private int dohvatiGorivo(int idvoz) {
        try (PreparedStatement ps = connection.prepareStatement("select v.Gorivo from Voznja voz join Vozilo v on v.IdV=voz.IdV where voz.IdVoz=?");) {
            ps.setInt(1, idvoz);

            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    int ret = rs.getInt(1);
                    rs.close();
                    return ret;

                }
                rs.close();

            } catch (SQLException ex) {
            }

        } catch (SQLException ex) {
        }
        return -1;
    }

    void delete(String courierUserName) {//brisi kad brises kurura

        ot160128_PackageOperations p = new ot160128_PackageOperations();
        String sql = "SELECT IdVoz from Voznja where KorisnickoIme=?";

        int idvoz = 0;
        try (PreparedStatement ps1 = connection.prepareStatement(sql);) {
            ps1.setString(1, courierUserName);
            ResultSet ex = ps1.executeQuery();
            while (ex.next()) {

                idvoz = ex.getInt(1);
                sql = "delete from Ruta where IdVoz=?";

                try (PreparedStatement ps2 = connection.prepareStatement(sql);) {
                    ps2.setInt(1, idvoz);

                    ps2.executeQuery();

                } catch (SQLException s) {
                }

                sql = "SELECT IdP from Paket where IdVoz=?";

                try (PreparedStatement ps3 = connection.prepareStatement(sql);) {
                    ps3.setInt(1, idvoz);

                    ResultSet ex1 = ps3.executeQuery();
                    while (ex1.next()) {

                        p.deletePackage(ex1.getInt(1));
                    }
                    ex1.close();

                } catch (SQLException s) {
                }
                 sql = "delete from Voznja where IdVoz=?";

                try (PreparedStatement ps22 = connection.prepareStatement(sql);) {
                    ps22.setInt(1, idvoz);

                    ps22.executeQuery();

                } catch (SQLException s) {
                }

            }
            ex.close();

        } catch (SQLException s) {
        }

    }
    void deleteV(String regbr) {//brisi kad brises vozilo

        ot160128_PackageOperations p = new ot160128_PackageOperations();
        String sql = "SELECT v.IdVoz from Voznja v join Vozilo v1 on v.IdV=v1.IdV where RegBr=?";

        int idvoz = 0;
        try (PreparedStatement ps1 = connection.prepareStatement(sql);) {
            ps1.setString(1, regbr);
            ResultSet ex = ps1.executeQuery();
            while (ex.next()) {

                idvoz = ex.getInt(1);
                sql = "delete from Ruta where IdVoz=?";

                try (PreparedStatement ps2 = connection.prepareStatement(sql);) {
                    ps2.setInt(1, idvoz);

                    ps2.executeQuery();

                } catch (SQLException s) {
                }

                sql = "SELECT IdP from Paket where IdVoz=?";

                try (PreparedStatement ps3 = connection.prepareStatement(sql);) {
                    ps3.setInt(1, idvoz);

                    ResultSet ex1 = ps3.executeQuery();
                    while (ex1.next()) {

                        p.deletePackage(ex1.getInt(1));
                    }
                    ex1.close();

                } catch (SQLException s) {
                }
                sql = "delete from Voznja where IdVoz=?";

                try (PreparedStatement ps22 = connection.prepareStatement(sql);) {
                    ps22.setInt(1, idvoz);

                    ps22.executeQuery();

                } catch (SQLException s) {
                }

            }
            ex.close();

        } catch (SQLException s) {
        }

    }

}
