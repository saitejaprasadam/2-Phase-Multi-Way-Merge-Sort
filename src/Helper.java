import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.util.Pair;

public class Helper {

	static HashMap<Integer, Double> fileToHashMap(File file, boolean limitToTen) {

		HashMap<Integer, Double> entries = new HashMap<>();
		
		try {
			
			BufferedReader bufferReader = new BufferedReader(new FileReader(file));
			String line = null;
			
			while((line = bufferReader.readLine()) != null) {			
				String[] array = line.split(" ");		
				entries.put(Integer.parseInt(array[0]), Double.parseDouble(array[1]));
				
				if(limitToTen && entries.size() > 10) {
					bufferReader.close();
					return entries;
				}
				
			}
			
			bufferReader.close();
			
		} catch (NumberFormatException | IOException  e) {
			e.printStackTrace();
			return null;
		}
		
		return entries;
		
	}

	/**
	 * Sorts the HashMap by value in descending order
	 * @param entries => HashMap Entries
	 * @return sorted HashMap
	 */
	static HashMap<Integer, Double> sortHashMap(HashMap<Integer, Double> entries) {
		
		return entries.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(
				Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
		
	}
	
	/**
	 * Storing sorted batch for later merge sort
	 * @param sortedEntries
	 * @param batchIndex
	 */
	static void temporaryStore(String fileNamePrefix, HashMap<Integer, Double> sortedEntries, int batchIndex) {
		
		try {
			
			File file = new File(fileNamePrefix + batchIndex + ".txt");
			file.getParentFile().mkdirs();
			file.createNewFile();
			
			FileWriter fileWriter = new FileWriter(file.getAbsolutePath());

			for (Map.Entry<Integer, Double> element : sortedEntries.entrySet()) 
				fileWriter.write(element.getKey() + " " + element.getValue() + "\n");	

		    fileWriter.close();
					
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Read a batch from the source file and return grouped entries
	 * @param bufferedReader Buffered Reader of the source file.
	 * @param entriesSize 
	 * @return Pair of hashmap entries and the 
	 */
	static Pair<HashMap<Integer, Double>, Boolean> readBatch(BufferedReader bufferedReader, int entriesSize) {
		
		HashMap<Integer, Double> entries = new HashMap<>();
		String line = null;

	    try {			
	    	
	    	//Random rand = new Random();

	    	while(entries.size() < entriesSize && (line = bufferedReader.readLine()) != null) {
				
	    		//TODO remove entriesRead
				int id = Integer.parseInt(line.substring(18, 27));// + rand.nextInt(299);
				double value = Double.parseDouble(line.substring(line.length() - 11));				
				
				if(entries.containsKey(id))
					entries.put(id, entries.get(id) + value);
				else 
					entries.put(id, value);

			}
			
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
	    
	    return new Pair<HashMap<Integer, Double>, Boolean>(entries, line == null);
	}
	
	/**
	 * Copy file content to another file
	 * @param sourceFile source content file
	 * @param destinationFile destination content file
	 */
	static void copyFileContentToAnotherFile(File sourceFile, String destinationFile) {
		try {
			Files.copy(sourceFile.toPath(), new File(destinationFile).toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static void persistentStorageMergeSort(File file1, File file2, String outputFilePath) {
		
		try {
		
			BufferedReader file1BufferedReader = new BufferedReader(new FileReader(file1.getAbsolutePath()));
			BufferedReader file2BufferedReader = new BufferedReader(new FileReader(file2.getAbsolutePath()));
			
			File outputFile = new File(outputFilePath);
			outputFile.getParentFile().mkdirs();
			outputFile.createNewFile();
			
			//TODO optimize
			PrintWriter pw = new PrintWriter(outputFile.getAbsolutePath());
			pw.close();
			
			FileWriter outputFileWriter = new FileWriter(outputFile.getAbsolutePath(), true);
						
			String file1Line = file1BufferedReader.readLine();
			String file2Line = file2BufferedReader.readLine();
						
			while(file1Line != null || file2Line != null){											
				
				if(file1Line == null) {
					outputFileWriter.write(file2Line + "\n");
					file2Line = file2BufferedReader.readLine();
				} 
				
				else if(file2Line == null) {
					outputFileWriter.write(file1Line + "\n");
					file1Line = file1BufferedReader.readLine();
				}
				
				else if(Integer.parseInt(file1Line.split(" ")[0]) > Integer.parseInt(file2Line.split(" ")[0])) {
					outputFileWriter.write(file2Line + "\n");
					file2Line = file2BufferedReader.readLine();
				}
				
				else if(Integer.parseInt(file1Line.split(" ")[0]) < Integer.parseInt(file2Line.split(" ")[0])) {
					outputFileWriter.write(file1Line + "\n");
					file1Line = file1BufferedReader.readLine();
				}
				
				else if(Integer.parseInt(file1Line.split(" ")[0]) == Integer.parseInt(file2Line.split(" ")[0])) {
					String line = file1Line.split(" ")[0] + " " + (Double.parseDouble(file1Line.split(" ")[1]) + Double.parseDouble(file2Line.split(" ")[1]));
					outputFileWriter.write(line + "\n");
					
					file1Line = file1BufferedReader.readLine();
					file2Line = file2BufferedReader.readLine();
				}

			}
			
			file1BufferedReader.close();
			file2BufferedReader.close();
			outputFileWriter.close();
			
		} catch(IOException e) {
			e.printStackTrace();			
		}
		
	}
	
	static LinkedList<Pair<Integer, Double>> getTopTen(String file) {
		
		SortedLinkedList sortedLinkedList = new SortedLinkedList();
		
		try {
			BufferedReader file1BufferedReader = new BufferedReader(new FileReader(file));
			String line = null;
			
			while((line = file1BufferedReader.readLine()) != null) {
				sortedLinkedList.addToSortedList(line);	
			}
			
			file1BufferedReader.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sortedLinkedList;	
	}
	
	static boolean deleteDirectory(File directoryFile) {
		
		File[] files = directoryFile.listFiles();
		
		if (files != null) 
			for (File file : files) 
				deleteDirectory(file);
					
		return directoryFile.delete();

	}
	
}