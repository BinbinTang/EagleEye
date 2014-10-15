package eagleeye.model;

import java.util.ArrayList;

import eagleeye.entities.*;

public class UIRequestHandler implements RequestHandler {
	private ArrayList<eagleeye.entities.File> folderStructure;
	
	@Override
	/*
	/*Return a list of File Object, as defined by entities package
	*/
	public ArrayList<eagleeye.entities.File> getFolderStructure() {
		if(folderStructure==null){
			folderStructure=new ArrayList<eagleeye.entities.File>();
			// TODO Query database and populate folderStructure
		/*
			// for now:
			folderStructure.add(new File(0,-1,	0,false,false,true,"root","C:/","ext","modext","delDate","createDate","accDate","modDate"));
			folderStructure.add(new File(0,	0,	1,false,false,true,"pic","C:/","ext","modext","delDate","createDate","accDate","modDate"));
			folderStructure.add(new File(0,	0,	2,false,false,true,"audio","C:/","ext","modext","delDate","createDate","accDate","modDate"));
			folderStructure.add(new File(0,	0,	3,false,false,true,"txt","C:/","ext","modext","delDate","createDate","accDate","modDate"));
			folderStructure.add(new File(0,	1,	4,false,false,true,"pic1","C:/","txt","modext","delDate","createDate","accDate","modDate"));
			folderStructure.add(new File(0,	1,	5,false,false,true,"pic2","C:/","txt","modext","delDate","createDate","accDate","modDate"));
			folderStructure.add(new File(0,	2,	6,false,false,true,"audio1","C:/","mp3","modext","delDate","createDate","accDate","modDate"));
			folderStructure.add(new File(0,	2,	7,false,false,true,"audio2","C:/","wav","modext","delDate","createDate","accDate","modDate"));
			folderStructure.add(new File(0,	3,	8,false,false,true,"text1","C:/","txt","modext","delDate","createDate","accDate","modDate"));
			folderStructure.add(new File(0,	3,	9,false,false,true,"text2","C:/","txt","modext","delDate","createDate","accDate","modDate"));
		*/
		}
		return folderStructure;
	}

	@Override
	public String getFilePath(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFileSize(int id) {
		// TODO Auto-generated method stub
		return null;
	}

}

