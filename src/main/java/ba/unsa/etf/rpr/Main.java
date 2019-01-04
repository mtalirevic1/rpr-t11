package ba.unsa.etf.rpr;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {



    public static void glavniGrad(){
        System.out.println("Unesite naziv drzave: ");
        Scanner scanner = new Scanner(System.in);
        String drzava = scanner.nextLine();
        Grad grad = GeografijaDAO.getInstance().glavniGrad(drzava);
        if (grad == null) {
            System.out.println("Nepostojeca drzava");

        }
        else {
            System.out.println("Glavni grad drzave " + drzava + " je " + grad.getNaziv());
        }

    }

    public static String ispisiGradove(){
        ArrayList<Grad> gradovi = GeografijaDAO.getInstance().gradovi();
        String str = "";
        for (Grad grad: gradovi) {
            str += grad.getNaziv() + " (" + grad.getDrzava().getNaziv() + ")" + " - " +
                    grad.getBroj_Stanovnika() + "\n";
        }
        return str;
    }

    public static void main(String[] args) {
        System.out.println(ispisiGradove());
        glavniGrad();
    }
}