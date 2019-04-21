import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import javafx.util.Pair;

public class Runner {	
	
	public static void main(String[] args) {
		
		long start = System.currentTimeMillis();
		
		try {

			int memory = 5;
			
			if(args.length == 1)
				memory = Integer.parseInt(args[0]);
			
			int batchIndex = 0;
			boolean reachedEndOfFile = false;
			BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("input.txt")));
			Helper.deleteDirectory(new File("./tmp/"));
			
			do {				
				reachedEndOfFile = processBatch(bufferedReader, batchIndex, memory * 2800);
				batchIndex++;
			} while(!reachedEndOfFile);
			
			//	if(reachedEndOfFile)
			//		bufferedReader = new BufferedReader(new FileReader(new File("input.txt")));
				
			//} while(batchIndex < 7);	

			
			bufferedReader.close();
			
			LinkedList<Pair<Integer, Double>> result = merge("./tmp/sorted-batch", batchIndex);
			System.out.println("");
			
			for (Pair<Integer, Double> entry : result) {
				System.out.println(entry.getKey() + " => " + entry.getValue());
			}
			
			long time = System.currentTimeMillis() - start;			
			String timeString = String.format("%d min, %d sec or %d milliseconds", 
					(int) ((time / (1000*60)) % 60),
					(int) (time / 1000) % 60,
					(int) time);
			
			System.out.println("\nDone in " + timeString);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Read N entries from the file and process them
	 * @param entriesSize 
	 * @param bufferReader BufferReader Object of the input file
	 * @return boolean => Reached end of the file or not
	 */
	public static boolean processBatch(BufferedReader bufferedReader, int batchIndex, int entriesSize) {
		
		Pair<HashMap<Integer, Double>, Boolean> result = Helper.readBatch(bufferedReader, entriesSize);		
		HashMap<Integer, Double> entries = result.getKey();
		
		entries = Helper.sortHashMap(entries);
	    Helper.temporaryStore("./tmp/sorted-batch", entries, batchIndex);
	    
		return result.getValue();
		
	}
	
	private static LinkedList<Pair<Integer, Double>> merge(String fileNamePrefix, int numberOfBatches) {
		
		System.out.println(fileNamePrefix);
		
		if(numberOfBatches == 1)
			return Helper.getTopTen(fileNamePrefix + "0.txt");
		
		int innerBatchesIndex = 0;
		for(int index=0; index < numberOfBatches; index = index + 2, innerBatchesIndex++) {
			
			File file1 = new File(fileNamePrefix + index + ".txt");
			File file2 = new File(fileNamePrefix + (index + 1) + ".txt");			

			if(!file2.exists())
				Helper.copyFileContentToAnotherFile(file1, fileNamePrefix + "0" + innerBatchesIndex + ".txt");	
									
			else			
				Helper.persistentStorageMergeSort(file1, file2, fileNamePrefix + "0" + innerBatchesIndex + ".txt");
			
		}
		
		return merge(fileNamePrefix + "0", innerBatchesIndex);
	}
	
}