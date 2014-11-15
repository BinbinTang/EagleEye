package eagleeye.fileReader;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedReader;
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
	/*   private ActionListener timerAction = new ActionListener()
    {
        public void actionPerformed(ActionEvent ae)
        {
            x++;
            y++;
            paintingPanel.setPosition(x, y);
        }
    };*/

	 //return true if format is txt or any of the three photo formats
	public static boolean checkFormat (String filePath){  
		String extension=null;
		int position = filePath.lastIndexOf(".");
		extension=filePath.substring(position+1);
		System.out.println("format is "+extension);
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

	private void createAndDisplayGUI(String filePath)
	{
		JFrame frame = new JFrame("file displayed");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		paintingPanel = new CustomPanel();
		if(isPhoto){
		System.out.println("now displaying photo");
		}
		if(isText){
			System.out.println("now displaying text");
		}
		paintingPanel.setShowPhoto(isPhoto);
		paintingPanel.setShowText(isText);
		paintingPanel.setPath(filePath);

		/*      final JButton startStopButton = new JButton("STOP");
        startStopButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                if (timer.isRunning())
                {
                    startStopButton.setText("START");
                    timer.stop();
                }
                else if (!timer.isRunning())
                {
                    startStopButton.setText("STOP");
                    timer.start();
                }
            }
        }); */

		frame.add(paintingPanel, BorderLayout.CENTER);
		//        frame.add(startStopButton, BorderLayout.PAGE_END);
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
		//      timer = new Timer(100, timerAction);
		//      timer.start();
	}

	public void start(String filePath)
	//public static void main(String... args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
		  //     String filePath=args[0];
		  //	String filePath="c:\\sp.png";
		        listOfFormat.add("jpg");
				listOfFormat.add("jpeg");
				listOfFormat.add("png");
		       if(checkFormat(filePath)==false){
		   		JOptionPane.showMessageDialog(null,"Current doc format is not supported","Error",JOptionPane.INFORMATION_MESSAGE);   
		       }
		       else{
		    	new fileLoader().createAndDisplayGUI(filePath);
		       }
			}
		});
	}
	
/*	  public static void main(String... args)
	    {
	        SwingUtilities.invokeLater(new Runnable()
	        {
	            public void run()
	            {
	                new PaintingExample().createAndDisplayGUI();
	            }
	        });
	    }
	*/
	public void printPath(){
		System.out.println("path is "+path);
	}

	
}

class CustomPanel extends JPanel
{
	private int x = 0;
	private int y = 0;
	public boolean displayPhoto=false;
	public boolean displayText=false;
	private String displayedPath=null;
	private boolean showPhoto=false;
	private boolean showText=false;
	private Image picture = null;
	private String content=null;
	
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
		System.out.println(" path is "+displayedPath);	
		
		if(showPhoto==true){
		picture = getImage(displayedPath);
		Graphics2D g2 = (Graphics2D)g;	    
		g2.drawImage(picture, 0, 0, 800,600, this);
		
		}
		if(showText==true){
		/*	super.paintComponent(g);
			g.clearRect(0, 0, getWidth(), getHeight());
			g.setColor(Color.MAGENTA);
			g.fillOval(x, y, 10, 10);
		*/	
			Graphics2D g2 = (Graphics2D)g;	
	 		try {
	 			content=readFile(displayedPath);
	 		} catch (IOException e) {
	 			// TODO Auto-generated catch block
	 			e.printStackTrace();
	 		}
	 		LinkedList<String> list=convertStringToLineString(content);
	 		for(int i=0;i<list.size();i++){
	 			g2.drawString(list.get(i),10,10*(i+1));
	 		}
		}
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

		System.out.println("num of rows "+parts.length);
		for(int i=0;i<parts.length;i++){
			list.add(parts[i]);
		}
		return list;
	}
}