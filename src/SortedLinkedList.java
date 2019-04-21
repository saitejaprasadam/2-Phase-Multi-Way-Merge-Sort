import java.util.LinkedList;

import javafx.util.Pair;

public class SortedLinkedList extends LinkedList<Pair<Integer, Double>> {

	private static final long serialVersionUID = 2759245629789539649L;

	public void addToSortedList(String line) {

		String[] temp = line.split(" ");
		Pair<Integer, Double> entry = new Pair<Integer, Double>(Integer.parseInt(temp[0]), Double.parseDouble(temp[1]));
		
		if(this.size() == 0)
			add(entry);
		
		else{
			int insertedIndex = -1;
			
			int size = size();
			for (int i = 0; i < size; i++) {
				
				if(entry.getValue() >= get(i).getValue()) {
					insertedIndex = i;
					break;
				}				
			}				
			
			if(insertedIndex == -1)
				add(entry);
			else
				add(insertedIndex, entry);
			
		}
	
		if(size() > 10)
			removeRange(10, size());

	}
	
}