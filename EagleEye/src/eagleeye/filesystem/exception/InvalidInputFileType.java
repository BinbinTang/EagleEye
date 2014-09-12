package eagleeye.filesystem.exception;


public class InvalidInputFileType extends Exception{

	private static final long serialVersionUID = 1L;
	public static final String DEFAULT_ERROR_MESSAGE = "Invalid Input File Type!";
	public InvalidInputFileType(){
		super(DEFAULT_ERROR_MESSAGE);
	}
	public InvalidInputFileType(String message){
		super(message);
	}
	
}
