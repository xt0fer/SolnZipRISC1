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
    public static final boolean DEBUG = true;

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
        WordAt newWord;
        if (DEBUG) System.err.print("> ");
        String lineNoComments = line;
        int index = line.indexOf("//");
        if (index > 0) {
            lineNoComments= line.substring(0, index);
        }
        if (DEBUG) System.err.print(lineNoComments);
        Matcher lMatch = label_pattern.matcher(lineNoComments);
        Matcher dMatch = directive_pattern.matcher(lineNoComments);
        Matcher codeMatch = code_pattern.matcher(lineNoComments);
        if (lMatch.find()) {
            this.handleLabel(lineNoComments, lMatch.group(1));
        } else if (dMatch.find()) {
            this.handleDirective(lineNoComments, dMatch.group(1));
        } else if (codeMatch.find()) {
            newWord = this.handleCode(lineNoComments, codeMatch.group(1));
            this.instructions.add(newWord);
            this.address++;
        }

        if (DEBUG) System.err.println();

    }

    private void handleDirective(String line, String dir) {
        String[] tokens = line.split("\\s");
        if (DEBUG) {
        System.err.print(" /"+dir+"/ ");
        System.err.print("["+line+"]");
        for (String token : tokens) {
            System.err.print(" >> "+token);
        }
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
        if (DEBUG) System.err.print(" // "+label+" ");
        symbols.put(label, currentAddressString());
    }

    private String currentAddressString() {
        return String.format("0x%04X", this.address);
    }

    private WordAt handleCode(String line, String opcode) {
        // change to standard interface, and call create opcode
        line = line.trim().replace(",", "");
        String[] tokens = line.split("\\W");
        if (DEBUG) { 
            System.err.print(" // "+opcode);
            System.err.print("["+line+"]");
            for (String token : tokens) {
                System.err.print(" >> "+token);
            }
        }
        if (opcode.equals("ADD")) {
            return new3argWord(opcode, tokens[1], tokens[2], tokens[3]);
        }
        if (opcode.equals("MOV")) {
            return new3argWord(opcode, tokens[1], tokens[2], "x0");
        }
        if (opcode.equals("HLT")) {
            return newHaltWord();
        }
        if (opcode.equals("DUMP")) {
            return newDumpWord();
        }
        if (opcode.equals("LD")) {
            return newMemoryWord(opcode, tokens[1], tokens[2]);
        }
        if (opcode.equals("ST")) {
            return newMemoryWord(opcode, tokens[1], tokens[2]);
        }
        if (opcode.equals("BRA")) {
            return newBRZWord("BRZ", "x0", tokens[1]);
        }
        if (opcode.equals("BRZ")) {
            return newBRZWord("BRZ", tokens[1], tokens[2]);
        }
        if (opcode.equals("SUB")) {
            return new3argWord(opcode, tokens[1], tokens[2], tokens[3]);
        }
        if (opcode.equals("SUBI")) {
            return newShiftWord(opcode, tokens[1], tokens[2], tokens[3]);
        }
        if (opcode.equals("LSH")) {
            return newShiftWord(opcode, tokens[1], tokens[2], tokens[3]);
        }
        if (opcode.equals("RSH")) {
            return newShiftWord(opcode, tokens[1], tokens[2], tokens[3]);
        }
        return newHCFWord();

        /*
        - ADD rd, rs, rt | 1dst | rd <- rs + rt
        - SUB rd, rs, rt | 2dst | rd <- rs - rt
        - SUBI rd, rs, k | 3dsk | rd ← rs - k
        - LSH rd, rs, k | 4dsk | rd <- rs / k ??
        - RSH rd, rs, k | 5dsk | rd <- rs * k ??
        - BRZ rd, aa | 6daa | branch to aa on rd == 0
        - BGT rd, aa | 7daa | branch to aa on rd > 0
        - LD rd, aa | 8daa | load rd with value of memory loc aa
        - ST rs, aa | 9saa | store rd value to memory loc aa
        - HLT | 0000 | halt.
        - HCF | 0FFF | halt and catch fire.
        PSEUDOs
        MOV rd, rs │ ADD rd, rs, x0 │ rd ← rs
        CLR rd │ ADD rd, x0, x0 │ rd ← 0
        DEC rd │ SUBI rd, rd, 1 │ rd ← rd - 1
        INCR rd |ADD rd, rd, 1  | rd <- rd + 1
        BRA aa │ BRZ x0, aa │ next instruction to read is at aa
        IN rd | Ad00 | read in a number to rd
        OUT rd | Bd00 | output a number from rd
        DUMP | F000 | print out registers, machine state and memory
        */
    }

    private WordAt newHCFWord() {
        return new WordAt(currentAddressString(), 0, 0xFF, 0xFF, 0xFF);
    }

    // LSH, RSH, and SUBI
    private WordAt newShiftWord(String opcode,
        String a1,
        String a2,
        String a3) {
        return new WordAt(currentAddressString(),
        resolve(opcode), resolve(a1), 
        resolve(a2), Integer.parseInt(a3));
    }

    private WordAt newBRZWord(String opcode, String rd, String aa) {
        int addr = resolve(aa);
        WordAt newWord = new WordAt(currentAddressString(),
        resolve(opcode),
        resolve(rd),
        upperHalf(addr),
        lowerHalf(addr)
        );
        if (addr == -1) {
            // it's a forward ref.
            newWord.undefineForwardRef(aa);
        }
        this.instructions.add(newWord);
        this.address++;
        return newWord;
    }

    // both LD and ST
    private WordAt newMemoryWord(String opcode, String a1, String a2) {
        int addr = resolve(a2);
        WordAt newWord = new WordAt(currentAddressString(),
        resolve(opcode),
        resolve(a1),
        upperHalf(resolve(a2)),
        lowerHalf(resolve(a2))
        );
        if (addr == -1) {
            // it's a forward ref.
            newWord.undefineForwardRef(a2);
        }
        return newWord;
}

    private WordAt newDumpWord() {
        return new WordAt(currentAddressString(), 0x0F, 0, 0, 0);
    }

    private WordAt newHaltWord() {
        return new WordAt(currentAddressString(), 0, 0, 0, 0);
    }

    // both ADD and SUB
    private WordAt new3argWord(String op,
            String a1,
            String a2,
            String a3) {
        return new WordAt(currentAddressString(),
        resolve(op), resolve(a1), 
        resolve(a2), resolve(a3));
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
        if (token.startsWith("0x")){
            int newAddress = Integer.decode(token);
            return newAddress;            
        }
        // assume at this point the token is a forward symbol reference.
        return -1;
    }

    private void dumpSymbols() {
        System.err.print(" // SYMBOLS\n");
        for (Map.Entry<String, String> entry : this.symbols.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue().toString());
       }
       System.err.print(" // END SYMBOLS\n");
    }

    private void outputResults() {
        // resolve any unknown forward refs.
        // and output
        for (WordAt w : this.instructions) {
            if (w.isForwardReference()) {
                if (symbols.containsKey(w.getForwardref())) {
                    String a = symbols.get(w.getForwardref());
                    w.defineForwardReference(a);
                } else {
                    throw new Panic("unable to resolve symbol "+w.getForwardref());
                }
            }
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
        registers.put("SUB", 2);
        registers.put("SUBI", 3);
        registers.put("LSH", 4);
        registers.put("RSH", 5);
        registers.put("HLT", 0);
        registers.put("DUMP", 0x0F);
        registers.put("BRA", 6);
        registers.put("BRZ", 6);
        registers.put("BGT", 7);
        registers.put("LD", 8);
        registers.put("ST", 9);

        
    }

}
