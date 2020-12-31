package rocks.zipcode.zas;

import java.util.ArrayList;
import rocks.zipcode.Word;

/*
 * the ZipRISC1 assembler.
 */

public class ZAS {
    ArrayList<Word> instructions = new ArrayList<>();
    SymbolTable symbols = new SymbolTable();
    
    public static void main(String[] args) {
        ZAS zas = new ZAS();
        System.err.println("**** ZipRISC1 Assembler v1 ****\n");
        // System.err.print("args ");
        // for (String arg : args) {
        //     System.out.print(" ");
        //     System.out.print(arg);
        // }
        System.err.println();
        if (args.length == 1) {
            try {
                zas.parseFile(args[0]); 
            } catch (Panic e) {
                e.printStackTrace();
                System.err.println(e);
                java.lang.System.exit(-1);
            }    
        } else {
            System.err.println("no input file.");
            java.lang.System.exit(-1);
        }
        java.lang.System.exit(0);
    }

    public void parseFile(String filename) {

    }
}
