package rocks.zipcode.zas;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rocks.zipcode.Panic;
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
                zas.dumpSymbols();
            } catch (Panic e) {
                e.printStackTrace();
                java.lang.System.exit(-1);
            }    
        } else {
            System.err.println("no input file.");
            java.lang.System.exit(-1);
        }
        java.lang.System.exit(0);
    }

    String directiveLine = "^\\.([A-Z][A-Z])"; // dot, TWO UPPERCASE chars, space, and rest
    String labelLine = "^([a-z]+):"; // start of line, all lowercase alpha, colon.
    String codeLine = "[\\s+]([A-Z]+)[\\s]*([\\w]*)[,]*[\\s]*([\\w]*)[,]*[\\s]*([\\w]*)";
    Pattern directive_pattern;
    Pattern label_pattern;
    Pattern code_pattern;
    Integer address = 0;
    
    public ZAS() {
        this.directive_pattern = Pattern.compile(directiveLine, Pattern.CASE_INSENSITIVE);
        this.label_pattern = Pattern.compile(labelLine, Pattern.CASE_INSENSITIVE);
        this.code_pattern = Pattern.compile(codeLine, Pattern.CASE_INSENSITIVE);
    }

    public void parseFile(String filename) {
        java.io.File tempFile = new java.io.File(filename);
        if (tempFile.exists() != true)
            throw new Panic("Panic: input assembly file not found");
        
        // open and load each line.
        java.io.BufferedReader reader;
		try {
			reader = new java.io.BufferedReader(new java.io.FileReader(
                filename));
			String line = reader.readLine();
			while (line != null) {
                this.parseLine(line);
                // read next line
				line = reader.readLine();
			}
			reader.close();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
    }


    private void parseLine(String line) {
        System.err.print("> ");
        System.err.print(line);
        Matcher lMatch = label_pattern.matcher(line);
        Matcher dMatch = directive_pattern.matcher(line);
        Matcher codeMatch = code_pattern.matcher(line);
        if (lMatch.find()) {
            this.handleLabel(line, lMatch.group(1));
        } else if (dMatch.find()) {
            this.handleDirective(line, dMatch.group(1));
        } else if (codeMatch.find()) {
            this.handleCode(line, codeMatch.group(1));
        }
        System.err.println();

    }

    private void handleDirective(String line, String dir) {
        System.err.print(" /"+dir+"/ ");
        if (dir.equals("WD")) {
            System.err.print("["+line+"]");
            String[] tokens = line.split("\\s");
            for (String token : tokens) {
                System.err.print(" >> "+token);
            }
            int wordsSaved = Integer.parseInt(tokens[1]);
            this.address += wordsSaved;
        }
    }

    private void handleLabel(String line, String label) {
        System.err.print(" // "+label+" ");
        symbols.put(label, String.format("0x%04X", this.address));
    }

    private void handleCode(String line, String opcode) {
        System.err.print(" // "+opcode);
        this.address++;
    }

    private void dumpSymbols() {
        System.err.print(" // SYMBOLS\n\n");
        for (Map.Entry<String, String> entry : this.symbols.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue().toString());
       }
    }


}
