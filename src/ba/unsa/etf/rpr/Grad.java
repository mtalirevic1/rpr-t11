package ba.unsa.etf.rpr;

public class Grad {
    private int id;
    private  String naziv;
    private int broj_Stanovnika;
    private Drzava drzava;

    public Grad(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public int getBroj_Stanovnika() {
        return broj_Stanovnika;
    }

    public void setBroj_Stanovnika(int broj_Stanovnika) {
        this.broj_Stanovnika = broj_Stanovnika;
    }

    public Drzava getDrzava() {
        return drzava;
    }

    public void setDrzava(Drzava drzava) {
        this.drzava = drzava;
    }
}
