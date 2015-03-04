package eagleeye.api.plugin;

import java.util.ArrayList;
import java.util.List;

public class MarkableItemManager{
	
	private List<MarkableItem> items;
	public MarkableItemManager(){
		setItems(new ArrayList<MarkableItem>());
		
	}
	public void add(MarkableItem m){
		items.add(m);
	}
	public List<MarkableItem> getMarkedItems(){
		List<MarkableItem> markedItems = new ArrayList<MarkableItem>();
		for(MarkableItem i: items){
			if(i.isMarked())
				markedItems.add(i);
		}
		return markedItems;
	}
	public List<MarkableItem> getItems() {
		return items;
	}
	public void setItems(List<MarkableItem> items) {
		this.items = items;
	}

}
