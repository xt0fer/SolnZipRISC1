public class Simulator {
    private CPU cpu;

    public Simulator(CPU cpu) {
        this.cpu = cpu;
    }

    public static void main(String[] args) {
        Simulator s = new Simulator(new CPU());
        if (args.length > 1) {
            try {
                s.load(args[1]);
                s.run();
            } catch (PanicException e) {
                e.printStackTrace();
                java.lang.System.exit(-1);
            }    
        }
        java.lang.System.exit(0);
    }

    private void run() throws PanicException{
        Engine engine = new Engine(this.cpu);
        engine.startAt(0x0000);
    }

    private void load(String executable_filename) throws PanicException {
        // see if executable_filename exists.
        // open and load each line.
        
    }
}
