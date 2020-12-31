package rocks.zipcode.zas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rocks.zipcode.Panic;
import rocks.zipcode.Word;

/*
 * the ZipRISC1 assembler.
 */

public class ZAS {
    ArrayList<WordAt> instructions = new ArrayList<>();
    SymbolTable symbols = new SymbolTable();
    
    HashMap<String,Integer> registers = new HashMap<>();
    HashMap<String,Integer> opcodes = new HashMap<>();

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
                zas.loadTables();
                zas.parseFile(args[0]);
                zas.dumpSymbols();
                zas.outputResults();
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
        String lineNoComments = line;
        int index = line.indexOf("//");
        if (index > 0) {
            lineNoComments= line.substring(0, index);
        }
        System.err.print(lineNoComments);
        Matcher lMatch = label_pattern.matcher(lineNoComments);
        Matcher dMatch = directive_pattern.matcher(lineNoComments);
        Matcher codeMatch = code_pattern.matcher(lineNoComments);
        if (lMatch.find()) {
            this.handleLabel(lineNoComments, lMatch.group(1));
        } else if (dMatch.find()) {
            this.handleDirective(lineNoComments, dMatch.group(1));
        } else if (codeMatch.find()) {
            this.handleCode(lineNoComments, codeMatch.group(1));
        }
        System.err.println();

    }

    private void handleDirective(String line, String dir) {
        System.err.print(" /"+dir+"/ ");
        System.err.print("["+line+"]");
        String[] tokens = line.split("\\s");
        for (String token : tokens) {
            System.err.print(" >> "+token);
        }
        if (dir.equals("WD")) {
            int wordSaved = Integer.parseInt(tokens[1]);
            this.instructions.add(new WordAt(currentAddressString(), 
                new Word(wordSaved)));
            this.address++;
        }
        if (dir.equals("OR")) {
            int newStart = Integer.decode(tokens[1]);
            this.address = newStart;
        }
    }

    private void handleLabel(String line, String label) {
        System.err.print(" // "+label+" ");
        symbols.put(label, currentAddressString());
    }

    private String currentAddressString() {
        return String.format("0x%04X", this.address);
    }

    private void handleCode(String line, String opcode) {
        System.err.print(" // "+opcode);
        line = line.trim().replace(",", "");
        System.err.print("["+line+"]");
        String[] tokens = line.split("\\W");
        for (String token : tokens) {
            System.err.print(" >> "+token);
        }
        if (opcode.equals("ADD")) {
            WordAt newWord = new WordAt(currentAddressString(),
                resolve(tokens[0]), 
                resolve(tokens[1]), 
                resolve(tokens[2]), 
                resolve(tokens[3]));
            this.instructions.add(newWord);
            this.address++;
            return;
        }
        if (opcode.equals("HLT")) {
            WordAt newWord = new WordAt(currentAddressString(),
            0, 0, 0, 0);
        this.instructions.add(newWord);
        this.address++;
        return;
        }
        if (opcode.equals("DUMP")) {
            WordAt newWord = new WordAt(currentAddressString(),
            0x0F, 0, 0, 0);
        this.instructions.add(newWord);
        this.address++;
        return;
        }
        if (opcode.equals("LD")) {
            WordAt newWord = new WordAt(currentAddressString(),
            resolve(tokens[0]),
            resolve(tokens[1]),
            upperHalf(resolve(tokens[2])),
            lowerHalf(resolve(tokens[2]))
            );
        this.instructions.add(newWord);
        this.address++;
        return;
        }
        if (opcode.equals("BRA")) {
            WordAt newWord = new WordAt(currentAddressString(),
            resolve(tokens[0]),
            0,
            upperHalf(resolve(tokens[1])),
            lowerHalf(resolve(tokens[1]))
            );
        this.instructions.add(newWord);
        this.address++;
        return;
        }
        


    }

    private int lowerHalf(int addr) {
        int i = (addr & 0xFF);
        return i;
    }

    private int upperHalf(int addr) {
        int i = (addr & 0xFF00) >> 16;
        return i;
    }

    private int resolve(String token) {
        // try to resolve as a symbol
        if (this.symbols.containsKey(token)) {
            return Integer.decode(this.symbols.get(token));
        }
        // or try as a register name
        if (this.registers.containsKey(token)) {
            return this.registers.get(token);
        }
        // otherwise try to interpret the token as address
        int newAddress = Integer.decode(token);
        return newAddress;
    }

    private void dumpSymbols() {
        System.err.print(" // SYMBOLS\n");
        for (Map.Entry<String, String> entry : this.symbols.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue().toString());
       }
       System.err.print(" // END SYMBOLS\n");
    }

    private void outputResults() {
        for (WordAt w : this.instructions) {
            System.out.println(w.toString());
        }
    }

    private void loadTables() {
        // load up the registers table
        registers.put("x0", 0); registers.put("x1", 1); 
        registers.put("x2", 2); registers.put("x3", 3); 
        registers.put("x4", 4); registers.put("x5", 5); 
        registers.put("x6", 6); registers.put("x7", 7); 
        registers.put("x8", 8); registers.put("x9", 9); 
        registers.put("xA", 10); registers.put("xB", 11); 
        registers.put("xC", 12); registers.put("xD", 13); 
        registers.put("xE", 14); registers.put("xF", 15); 

        // load up opcodes
        registers.put("ADD", 1);
        registers.put("HLT", 0);
        registers.put("DUMP", 0x0F);
        registers.put("BRA", 6);
        registers.put("BRZ", 6);
        registers.put("LD", 8);

        
    }

}
