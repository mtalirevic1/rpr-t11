package ba.unsa.etf.rpr;

import java.sql.*;
import java.util.ArrayList;

public class GeografijaDAO {

    private static GeografijaDAO geografijaDAO = null;

    private static Connection connection;

    public GeografijaDAO() {
        connection = null;

        try {
            String url = "jdbc:sqlite:resources/baza.db";
            connection = DriverManager.getConnection(url);
            kreirajTabele();
            popuni();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    public void kreirajTabele() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS gradovi (\n" + "	id integer PRIMARY KEY,\n" + "	naziv text NOT NULL UNIQUE,\n" + " brojStanovnika integer,\n" + " drzava integer,\n" + "	FOREIGN KEY(drzava) REFERENCES drzave(id) ON DELETE CASCADE\n" + ");";

        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.execute();
        sql = "CREATE TABLE IF NOT EXISTS drzave (\n" + "	id integer PRIMARY KEY,\n" + "	naziv text NOT NULL UNIQUE,\n" + " glavniGrad integer,\n" + "	FOREIGN KEY(glavniGrad) REFERENCES gradovi(id) ON DELETE CASCADE\n" + ");";
        stmt = connection.prepareStatement(sql);
        stmt.execute();
    }

    private static void initialize() {
        geografijaDAO = new GeografijaDAO();
    }

    public void popuni() {
        Grad pariz = new Grad("Pariz", 2206488);
        Drzava francuska = new Drzava("Francuska", pariz);
        pariz.setDrzava(francuska);
        dodajDrzavu(francuska);
        dodajGrad(pariz);

        Grad london = new Grad("London", 8825000 );
        Drzava vb = new Drzava("Velika Britanija", london);
        london.setDrzava(vb);
        dodajDrzavu(vb);
        dodajGrad(london);

        Grad manchester = new Grad("Manchester", 545500);
        manchester.setDrzava(vb);
        dodajGrad(manchester);

        Grad bec = new Grad("Beč", 1899055);
        Drzava austrija = new Drzava("Austrija", bec);
        bec.setDrzava(austrija);
        dodajDrzavu(austrija);
        dodajGrad(bec);

        Grad graz = new Grad("Graz",280200);
        graz.setDrzava(austrija);
        dodajGrad(graz);

    }

    public void dodajGrad(Grad grad) {
        try {
            if (nadjiGrad(grad.getNaziv()) != null)
                return;
            PreparedStatement stmt = connection.prepareStatement("INSERT OR REPLACE INTO gradovi(naziv, brojStanovnika, drzava) VALUES(?,?,?)");
            stmt.setString(1, grad.getNaziv());
            stmt.setInt(2, grad.getBroj_Stanovnika());
            stmt.setInt(3, grad.getDrzava().getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void dodajDrzavu(Drzava drzava) {
        try {
            if (nadjiDrzavu(drzava.getNaziv()) != null)
                return;
            PreparedStatement stmt = connection.prepareStatement("INSERT OR REPLACE INTO drzave(naziv, glavniGrad) VALUES(?,null)");
            stmt.setString(1, drzava.getNaziv());
            stmt.executeUpdate(); //dodali drzavu pod nazivom

            Drzava d = nadjiDrzavu(drzava.getNaziv());
            drzava.getGlavniGrad().setDrzava(d);

            dodajGrad(drzava.getGlavniGrad()); //ako vec nije u tabeli
            Grad g = nadjiGrad(drzava.getGlavniGrad().getNaziv());
            g.setDrzava(d);
            drzava.getGlavniGrad().setId(g.getId());
            drzava.setId(d.getId());

            g.setDrzava(drzava);
            izmijeniGrad(g);
            izmijeniDrzavu(drzava);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void izmijeniDrzavu(Drzava drzava) {
        try {
            PreparedStatement stmt = connection.prepareStatement("UPDATE drzave SET naziv=?, glavniGrad=? WHERE id=?");
            stmt.setString(1, drzava.getNaziv());
            stmt.setInt(2, drzava.getGlavniGrad().getId());
            stmt.setInt(3, drzava.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void izmijeniGrad(Grad grad) {
        try {
            PreparedStatement stmt = connection.prepareStatement("UPDATE gradovi SET naziv=?, brojStanovnika=?, drzava=? WHERE id=?");
            stmt.setString(1, grad.getNaziv());
            stmt.setInt(2, grad.getBroj_Stanovnika());
            stmt.setInt(3, grad.getDrzava().getId());
            stmt.setInt(4, grad.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    public Drzava nadjiDrzavu(String drzava) {
        Drzava d = new Drzava();
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT id, naziv, glavniGrad FROM drzave WHERE naziv=?");
            stmt.setString(1, drzava);
            ResultSet rs = stmt.executeQuery();
            if (rs.isClosed())
                return null;
            d.setId(rs.getInt(1));
            d.setNaziv(rs.getString(2));
            return d;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    public Grad nadjiGrad(String grad) {

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT id, naziv, brojStanovnika, drzava FROM gradovi WHERE naziv=?");
            stmt.setString(1, grad);
            ResultSet rs = stmt.executeQuery();
            if (rs.isClosed())
                return null;
            while (rs.next()) {
                return new Grad(rs.getInt(1), rs.getString(2), rs.getInt(3));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }



    public Grad glavniGrad(String nazivDrzave) {

        //ako nema drzave vratit ce null
        if (nadjiDrzavu(nazivDrzave) == null)
            return null;
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT gradovi.id, gradovi.naziv, brojStanovnika, drzava, " + "drzave.id, drzave.naziv, drzave.glavniGrad FROM gradovi INNER JOIN drzave ON " + "gradovi.drzava = drzave.id WHERE drzave.naziv = ?");

            stmt.setString(1, nazivDrzave);
            ResultSet rs = stmt.executeQuery();

            Grad grad = new Grad(rs.getInt(1),rs.getString(2),rs.getInt(3));
            Drzava drzava = new Drzava(rs.getInt(5), rs.getString(6), grad);
            grad.setDrzava(drzava);
            return grad;

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void obrisiDrzavu(String nazivDrzave) {
        Drzava drzava=nadjiDrzavu(nazivDrzave);
        try {
            //provjera ukoliko nema drzave
            if (drzava == null)
                return;
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM drzave WHERE naziv=?");
            //prvo obrišem sve gradove unutar drzave
            try {
                PreparedStatement stmt1 = connection.prepareStatement("DELETE FROM gradovi WHERE drzava=?");
                stmt1.setInt(1, drzava.getId());
                stmt1.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            stmt.setString(1, nazivDrzave);
            stmt.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    public ArrayList<Grad> gradovi() {
        ArrayList<Grad> lista = new ArrayList<Grad>();
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT gradovi.id, gradovi.naziv, brojStanovnika, drzava, " + "drzave.id, drzave.naziv, drzave.glavniGrad FROM gradovi INNER JOIN drzave ON " + "gradovi.drzava = drzave.id ORDER BY brojStanovnika DESC");
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {

                Grad grad = new Grad(resultSet.getInt(1),resultSet.getString(2),resultSet.getInt(3));
                Grad glavni=nadjiGlavniGrad(resultSet.getInt(7));
                Drzava drzava = new Drzava(resultSet.getInt(5),resultSet.getString(6),glavni);
                drzava.getGlavniGrad().setDrzava(drzava);
                grad.setDrzava(drzava);
                lista.add(grad);
            }
            return lista;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Grad nadjiGlavniGrad(Integer id) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT id, naziv, brojStanovnika, drzava FROM gradovi WHERE id=?");
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                return new Grad(resultSet.getInt(1),resultSet.getString(2),resultSet.getInt(3));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static GeografijaDAO getInstance() {
        if (geografijaDAO == null) initialize();
        return geografijaDAO;
    }

    public static void removeInstance() {
        try {
            connection.close();
            connection = null;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        geografijaDAO = null;
    }


}
