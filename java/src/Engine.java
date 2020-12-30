public class Engine {

    private CPU cpu = null;

	public Engine(CPU cpu) {
        this.cpu = cpu;
	}

	public void startAt(int i) throws PanicException {
        if (i > this.cpu.memoryLimit) {
            throw new PanicException("memory overflow");
        }
	}
    
}
