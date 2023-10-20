package ca.pfv.spmf.tools;

public class MemoryLoggerTest {

	public static void main(String[] args) {
		// Reset the recorded memory usage
		MemoryLogger.getInstance().reset();
		
		// Check the memory usage
		MemoryLogger.getInstance().checkMemory();
		
		// Print the maximum memory usage until now.
		System.out.println("Max memory : " + MemoryLogger.getInstance().getMaxMemory());
		
		int[][] array = new int[99999][9999];
		
		// Check the memory usage
		MemoryLogger.getInstance().checkMemory();
		
		// Print the maximum memory usage until now.
		System.out.println("Max memory : " + MemoryLogger.getInstance().getMaxMemory());
	}

}
