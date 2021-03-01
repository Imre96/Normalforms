package com.company;

import com.company.Formula.Formula;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner be = new Scanner(System.in);

        System.out.println("Add meg a formulát");

        String bemenet;
        do {
             System.out.println("Válaszd ki a formula beviteli módját: F, ha formula, M ha művelettábla, q a kilépéshez");

             bemenet = be.nextLine();
             Formula normálra;

             if (bemenet.charAt(0)=='M'){
                 System.out.println("Add meg a művelettáblát: (2^n {0,1} karakter, whitespace megengedett)");
                 bemenet = be.nextLine();
                 try {
                     System.out.println("Teljes diszjunktív normál forma");
                     normálra= Formula.fromOperationTable(bemenet,true,false);
                     System.out.println(normálra.toDNFString());
                     System.out.println("Egyszerűsített diszjunktív normál forma(buggos)");
                     normálra= Formula.fromOperationTable(bemenet,true,true);
                     System.out.println(normálra.toDNFString());

                     System.out.println("Teljes konjunktív normálforma");
                     normálra= Formula.fromOperationTable(bemenet,false,false);
                     System.out.println(normálra.toKNFString());
                     System.out.println("Egyszerűsített konjunktív normál forma(nem implementált)");


                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }else if (bemenet.charAt(0)=='F'){
                 System.out.println("Add meg a formulát:");
                 System.out.println("Egyszerűsített jelölés: != \u00AC , && = \u2227, || = \u2228, > = \u2283");
                 bemenet = be.nextLine();
                 normálra=Formula.fromExpression(bemenet);
                 System.out.println("diszjunktív normál forma");
                 Formula disz= normálra.toDisjunctiveNormalForm();
                 System.out.println(disz.toDNFString());
                 System.out.println("konjunktív normál forma");
                 Formula kon= normálra.toKonjunctiveNormalForm();
                 System.out.println(disz.toKNFString());

             }else if (bemenet.charAt(0)=='q'){
              break;
             }

        }while (bemenet!="q");


    }
}
