package analyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import reader.SQLiteReaderPlugin;
import eagleeye.pluginmanager.Plugin;

public class AndroidLocationAnalyzerPlugin implements Plugin{
	private class LocationInformation implements Comparable<LocationInformation> {
		private String key;
		private int    accuracy;
		private int    confidence;
		//public int    latIE6;
		//public int    lonIE6;
		private double latIE6;
		private double lonIE6;
		private long   timestamp;
		private String type;
		
		private String timestampToDate()
		{
			SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
			return format.format(timestamp);
		}
		@Override
		public String toString()
		{
			return "LocationInformation{ key: " + key + " accuracy: " + accuracy + " confidence: " + confidence + " latitude: " + latIE6 + " longitude: " + lonIE6 + " time: " + timestampToDate() + "}";		
		}
		public List<String> toList(){
			List<String> ls = new ArrayList<String>();
			
			ls.add(type);
			ls.add(""+latIE6);
			ls.add(""+lonIE6);
			ls.add(timestampToDate());
			ls.add(""+accuracy);
			ls.add(""+confidence);
			
			return ls;
		}

		@Override
		public int compareTo(LocationInformation another) {
			if ( this.timestamp < another.timestamp ) {
				return -1;
			} else if ( this.timestamp > another.timestamp ) {
				return 1;
			} else {
				return 0;
			}
		}
	}
	private String deviceRoot;
	private List<String> searchPaths;
	private Plugin sqlreader;
	public static String decodeString(final byte[] arr, final int idx, final int length)
	{
		final StringBuilder sb = new StringBuilder(length);
		for ( int i = idx; i < (idx+length); i++ ) {
			sb.append((char)arr[i]);
		}
		return sb.toString();
	}
	
	public static short decodeShort(final byte[] arr, final int idx)
	{
		final short short1 = arr[idx  ];
		final short short2 = arr[idx+1];

		return (short) (((short)(short1 & 255)<<8) + ((short)(short2 & 255)));
	}

	public static int decodeInt(final byte[] arr, final int idx)
	{
		final int int1 = arr[idx  ];
		final int int2 = arr[idx+1];
		final int int3 = arr[idx+2];
		final int int4 = arr[idx+3];

		return ((int)(int1 & 255)<<24) + ((int)(int2 & 255)<<16) + ((int)(int3 & 255)<<8) + ((int)(int4 & 255));
	}	

	public static long decodeLong(final byte[] arr, final int idx)
	{
		final int int1 = arr[idx  ];
		final int int2 = arr[idx+1];
		final int int3 = arr[idx+2];
		final int int4 = arr[idx+3];
		final int int5 = arr[idx+4];
		final int int6 = arr[idx+5];
		final int int7 = arr[idx+6];
		final int int8 = arr[idx+7];

		return ((long)(int1 & 255)<<56)
		+ ((long)(int2 & 255)<<48)
		+ ((long)(int3 & 255)<<40)
		+ ((long)(int4 & 255)<<32)
		+ ((long)(int5 & 255)<<24)
		+ ((long)(int6 & 255)<<16) 
		+ ((long)(int7 & 255)<<8) 
		+ ((long)(int8 & 255));
	}

	public static double decodeFloat(final byte[] arr, final int idx)
	{
		final long value = decodeLong(arr, idx);

		return Double.longBitsToDouble(value);
	}	
	

	public List<LocationInformation> parseLocationCacheFile(byte[] data, String type)
	{
		// unpack ">hh" - version, count
		// big-endian short (2), short(2)
		short version = decodeShort(data, 0);
		short count   = decodeShort(data, 2);

		System.out.println("version: " + version);
		System.out.println("location count:   " + count  );

		List<LocationInformation> locations = new ArrayList<LocationInformation>(count);
		
		// unpack ">hSiiddQ" - keyLength, key, accuracy, confidence, latitude, longitude, time
		// big-endian short (2), string(keyLength), int (4), int (4), double (8), double(8), unsigned long long (8)
		// total - 34 bytes
		for ( int i = 4; i < data.length; i += 34 ){
			if ( (i + 34) > data.length ) {
				System.out.println("malformed last record? at i: " + i + " but only length: " + data.length);
				break;
			}

			LocationInformation location = new LocationInformation();

			int keyLength = decodeShort(data, i+ 0);

			location.key = decodeString(data, i+2, keyLength);

			i += keyLength;
			
			location.accuracy   = decodeInt(data, i+ 2);
			location.confidence = decodeInt(data, i+ 6);
			//location.latIE6     = (int)(decodeFloat(data, i+10) * 1E6);
			//location.lonIE6     = (int)(decodeFloat(data, i+18) * 1E6);
			location.latIE6 = decodeFloat(data, i+10);
			location.lonIE6 = decodeFloat(data, i+18);
			location.timestamp  = decodeLong(data, i+26);
			location.type       = type;
			locations.add(location);
		}						
		return locations;
	}
	

	@Override
	public String getName() {
		return "Android Location Analyzer";
	}

	@Override
	public Object getResult() {
		List<List<String>> result = new ArrayList<List<String>>();
		
		for(String f: searchPaths){
			System.out.println(f);
			try {
		         FileInputStream fis=new FileInputStream(new File(f));
		         try {
		             byte[] b=new byte[fis.available()];
		             fis.read(b);
		             fis.close();

		             List<LocationInformation> result2 = parseLocationCacheFile(b, (new File(f)).getName());
		             for(LocationInformation li : result2){
		            	 //System.out.println(li.toString());
		            	 result.add(li.toList());
		             }
		             
		         } catch (IOException e) {
		             e.printStackTrace();
		         }
		         
		     } catch (FileNotFoundException e) {
		         e.printStackTrace();
		     }
			
		}
		return result;
		
	}

	@Override
	public Type getType() {
		return Type.ANALYZER;
	}

	@Override
	public boolean hasError() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int setParameter(List argList) {
		deviceRoot = (String) argList.get(0);
		List<String> paths = new ArrayList<String>();
		searchPaths = new ArrayList<String>();
		
		//List of files to search
		paths.add(deviceRoot+File.separator+"data"+File.separator+"com.google.android.location"+File.separator+"files"+File.separator+"cache.cell");	//google cellular location cache
		paths.add(deviceRoot+File.separator+"data"+File.separator+"com.google.android.location"+File.separator+"files"+File.separator+"cache.wifi");	//google wifi location cache
		
		//check if files exist
		for(String s :paths){
			File f = new File(s);
			if(f.exists()){
				searchPaths.add(s);
			}
		}
		
		return 0;
	}
	
	@Override
	public int setAvailablePlugins(List<Plugin> pls) {
		for(Plugin pl: pls){
			if(pl.getName().equals("SQLite Reader")){
				sqlreader= pl;
			}
		}
		return 0;
	} 
	
	public static void main(String[] args) { 
		
		AndroidLocationAnalyzerPlugin cp = new AndroidLocationAnalyzerPlugin();
		List<Plugin> pls = new ArrayList<Plugin>();
		pls.add(new SQLiteReaderPlugin());
		cp.setAvailablePlugins(pls);
		List paths = new ArrayList();
		String root = ".."+File.separator+"EagleEye"+File.separator+"output"+File.separator+"mtd8.dd"+File.separator+"mtd8.dd";
		paths.add(root);
		cp.setParameter(paths);
		List<List<String>> result = (List<List<String>>) cp.getResult();
		for(List<String> i : result){
			System.out.println(i);
		}
	}
}
