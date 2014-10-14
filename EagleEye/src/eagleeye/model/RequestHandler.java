package eagleeye.model;
import java.util.ArrayList;
import java.util.List;

public interface RequestHandler {
	
	public ArrayList<eagleeye.entities.File> getFolderStructure();
	public String getFilePath(int id);
	public String getFileSize(int id);
	
}
