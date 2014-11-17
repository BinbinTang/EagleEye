package eagleeye.fileReader;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;

import javax.swing.JOptionPane;

public class fileLoader
{
	private CustomPanel paintingPanel;



	public String path=null;
	public String content=null;

	static int ListOfFormatSize=3;
	private static LinkedList<String> listOfFormat=new LinkedList();

	public static  boolean isPhoto=false;
	public static  boolean isText=false;

	public static boolean checkFormat (String filePath){  
		String extension=null;
		int position = filePath.lastIndexOf(".");
		extension=filePath.substring(position+1);
	//	System.out.println("format is "+extension);
		if(extension.equals("txt")){
			isText=true;
			return true;
		}
		for(int i=0;i<ListOfFormatSize;i++){
			if(listOfFormat.get(i).equals(extension)){
				isPhoto=true;
				return true;
			}
		}
		return false;
	}

	void createAndDisplayGUI(String filePath) throws IOException
	{
		String fileName="file displayed";
		fileName=filePath.substring(filePath.lastIndexOf("\\")+1);
		JFrame frame = new JFrame(fileName);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		paintingPanel = new CustomPanel();

		paintingPanel.setShowPhoto(isPhoto);
		paintingPanel.setShowText(isText);
		paintingPanel.setPath(filePath);

		if(isPhoto){
	//		System.out.println("now displaying photo");
			BufferedImage bimg = ImageIO.read(new File(filePath));
			int width = bimg.getWidth();
			int height = bimg.getHeight();
	//		paintingPanel.setWidth(width);
			
			if(width<=150){
			 paintingPanel.setWidth(width);  
			 paintingPanel.setHeight(height*(150/width));
			}
			
			else{
			paintingPanel.setWidth(width);
			paintingPanel.setHeight(height);
			}
			
			System.out.println("width is "+width);
			System.out.println("height is "+height);
			frame.add(paintingPanel, BorderLayout.CENTER);
			frame.pack();
			frame.setLocationByPlatform(true);
			frame.setVisible(true);

		}
		if(isText){
	//		System.out.println("now displaying text");
			try {
				content=readFile(filePath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			JTextArea textArea = new JTextArea(content, 30, 40);
			textArea.setPreferredSize(new Dimension(800, 600));
			JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			textArea.setLineWrap(true);
			textArea.setEditable(false);
			frame.add(scrollPane);
			frame.pack();
			frame.setLocationByPlatform(true);
			frame.setVisible(true);
		}

	}

	public void start(String filePath)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{

				listOfFormat.add("jpg");
				listOfFormat.add("jpeg");
				listOfFormat.add("png");
				if(checkFormat(filePath)==false){
					JOptionPane.showMessageDialog(null,"Current file format is not supported by viewer","Format not suppported",JOptionPane.INFORMATION_MESSAGE);   
				}
				else{
					try {
						new fileLoader().createAndDisplayGUI(filePath);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}

	public void printPath(){
		System.out.println("path is "+path);
	}

	public String readFile(String filePath) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			return sb.toString();
		} finally {
			br.close();
		}
	}
}

class CustomPanel extends JPanel
{
	private int x = 0;
	private int y = 0;
	private String displayedPath=null;
	private boolean showPhoto=false;
	private boolean showText=false;
	private Image picture = null;
	private String content=null;
	private int width=0;
	private int height=0;

	public void setWidth(int x){
		width=x;
	}
	public void setHeight(int x){
		height=x;
	}
	public void setShowPhoto(boolean x){
		showPhoto=x;
	}

	public void setShowText(boolean x){
		showText=x;
	}
	public void setPath(String p){
		displayedPath=p;
	}
	@Override
	public Dimension getPreferredSize()
	{		
		if(showPhoto){
			System.out.println("showing photo dimension "+width+" "+height);
			return (new Dimension(width, height));
		}
		if(showText){
			return (new Dimension(800, 600));
		}
		return (new Dimension(800, 600));
	}

	public void setPosition(int a, int b)
	{
		x = a;
		y = b;
		if (x <(getWidth() - 10) && y < (getHeight() - 10))
			repaint();
		else
	       System.out.println("Nothing is happening...");
	}

	@Override
	public void paintComponent(Graphics g)
	{
	//	System.out.println(" path is "+displayedPath);	

		//	if(showPhoto==true){
		picture = getImage(displayedPath);
		Graphics2D g2 = (Graphics2D)g;	    
		g2.drawImage(picture, 0,0,getWidth(),getHeight(),this);

	}


	public Image getImage(String path){
		Image tempImage=null;
		try{
			URL imageURL = (new java.io.File(path)).toURI().toURL();
			tempImage = Toolkit.getDefaultToolkit().getImage(imageURL);
		}
		catch(Exception e){
			System.out.println("error loading occur--"+e.getMessage());
		}
		return tempImage;
	}
	public static LinkedList<String>  convertStringToLineString(String str) {
		if (str==null)
			return null;

		String[] parts = str.split("\n");
		LinkedList<String> list=new LinkedList();

	//	System.out.println("num of rows "+parts.length);
		for(int i=0;i<parts.length;i++){
			list.add(parts[i]);
		}
		return list;
	}
}