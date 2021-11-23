package bnotai.tekla.material.fileio;


//import org.apache.commons.math3.util.Pair;
import bnotai.tekla.material.data.*;

import java.lang.*;
import java.util.*;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import java.io.*;

public class Txtio{
	public Txtio() {
		
	}
	/**
	 * @param {String} path read input file name from txt, .sym(record in path file) should be replace 
	 * line by line without extension
	 * @return {List<String>} a list of .sym file name without extension
	 */
//	public List<String> readtxt(String path) {
//		List<String> fileList = new ArrayList<>();
//		try (BufferedReader br = Files.newBufferedReader(Paths.get(path))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                fileList.add(line);
//            }
//        } catch (IOException e) {
//            System.err.format("IOException: %s%n", e);
//        }
//        return fileList;
//	}
	
	public String getprjname(String path) {
		String filename = "";
		
		try (BufferedReader br = Files.newBufferedReader(Paths.get(path))) {
			if ((filename = br.readLine()) == null) {
                System.out.println("Cannot find data in input.txt(reading the first row of .xls)");
                System.exit(1);
            }
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }

        return filename;
	}
	public Hashtable<String, Triple<List<Triple<Double, Double, Double>>, List<Triple<Double, Double, Double>>, List<Triple<Double, Double, Double>>>> 
		readncl(String path, File[] fileList) {
		Hashtable<String, Triple<List<Triple<Double, Double, Double>>, List<Triple<Double, Double, Double>>, List<Triple<Double, Double, Double>>>> rawncl = 
				new Hashtable<String, Triple<List<Triple<Double, Double, Double>>, List<Triple<Double, Double, Double>>, List<Triple<Double, Double, Double>>>>();
		
        for(File f : fileList) {
//            System.out.println(f.getName());
        	System.out.println(f.getName());
            Triple<List<Triple<Double, Double, Double>>, List<Triple<Double, Double, Double>>, List<Triple<Double, Double, Double>>> compdata
            	= readcompdata(path + f.getName());
            rawncl.put(FilenameUtils.removeExtension(f.getName()), compdata);
            System.out.println("");
        }
		
		return rawncl;
	}
	
	public Triple<List<Triple<Double, Double, Double>>, List<Triple<Double, Double, Double>>, List<Triple<Double, Double, Double>>> readcompdata(String filename){
		List<Triple<Double, Double, Double>> lw = new ArrayList<Triple<Double, Double, Double>>();
		List<Triple<Double, Double, Double>> abs = new ArrayList<Triple<Double, Double, Double>>();
		List<Triple<Double, Double, Double>> rw = new ArrayList<Triple<Double, Double, Double>>();
		boolean flag = false;
		try (BufferedReader br = Files.newBufferedReader(Paths.get(filename))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		       // process the line.
		    	if(line.equals("BO")) {
		    		flag = !flag;
		    	}
		    	else if(line.equals("EN")) {
		    		break;
		    	}
		    	else if(flag) {
		    		
		    		String []split = line.trim().split("\\s+");
		    		if((int)(Double.parseDouble(split[3])) == 0) {
		    			flag = !flag;
		    			break;
		    		}
//		    		System.out.println(line.trim());
//		    		System.out.println("split.length = " + split.length);
//		    		for(String s : split) {
//		    			System.out.println(s);
//		    		}
		    		
		    		split[1] = split[1].substring(0, split[1].length() - 1);
		    		Triple<Double, Double, Double> data = new Triple<Double, Double, Double>(Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
		    		System.out.println("tag is " +split[0] + ", data is: " + data.getFirst()+ " and "+ data.getSecond()+ " and "+data.getThird());
		    		// lw
		    		if(split[0].equals("o")) {
		    			lw.add(data);
		    		}
		    		// abs
		    		else if(split[0].equals("v")) {
		    			abs.add(data);
		    		}
		    		// rw
		    		else if(split[0].equals("u")) {
		    			rw.add(data);
		    		}

		    	}
		    	
		    	
		    }
		} catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
		return new Triple<List<Triple<Double, Double, Double>>, List<Triple<Double, Double, Double>>, List<Triple<Double, Double, Double>>>(lw, abs, rw);
	}
	/**
	 * @param {String} fileList a List<String> get from readtxt(String path)
	 * @param path directory of .sym
	 * @return {Pair<List<String>, List<Integer>>} first List<String> is information of "Stock Sheet" 
	 * and "XXMXX", List<Integer> record duplicate number
	 */
//	public Pair<List<String>, List<Integer>> readsym(List<String> fileList, String path){
	public Pair<List<String>, List<Integer>> readsym(String path, File[] fileList){
		int filecnt = 0;
		List<String> dataList = new ArrayList<>();
		List<Integer> dupeList = new ArrayList<>();
		
		while (filecnt < fileList.length) {

            try (BufferedReader br = Files
                    .newBufferedReader(Paths.get(path + fileList[filecnt].getName()))) {
                // read line by line
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.length() >= 5 && ("Stock".equals(line.substring(0, 5)))) {
                        String[] tokens = line.split(" ");
                        dataList.add(line);
                        dupeList.add(Integer.valueOf(tokens[6]));
                    }
                    if (line.length() >= 5 && "(".equals(line.substring(0, 1))) {
                        dataList.add(line);
                    }
                }
            } catch (IOException e) {
                System.err.format("IOException: %s%n", e);
            }
            filecnt++;
        }
		
		return (new Pair<List<String>, List<Integer>>(dataList, dupeList));
	}

	public File[] getfilesname(String foldername, String ext) {
		
		File folder = new File(foldername); 
		File[] listOfFiles = folder.listFiles();
		FileFilter fileFilter = new WildcardFileFilter(ext, IOCase.INSENSITIVE);  
		// For taking both .SYM and .sym files    
        
		if (listOfFiles.length > 0) {             
			sortByNumber(listOfFiles);   
		}    
		
//		System.out.println(listOfFiles.length);
//		for (int i = 0; i < listOfFiles.length; i++) {
//		   System.out.println(listOfFiles[i].getName());
//
//		}
		return listOfFiles;
	}

    public void sortByNumber(File[] files) {
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                int n1 = extractNumber(o1.getName());
                int n2 = extractNumber(o2.getName());
                return n1 - n2;
            }

            private int extractNumber(String name) {
                int i = 0;
                try {
                    int s = name.indexOf('M')+1;
                    int e = name.lastIndexOf('.');
                    String number = name.substring(s, e);
                    i = Integer.parseInt(number);
                } catch(Exception e) {
                    i = 0; // if filename does not match the format
                           // then default to 0
                }
                return i;
            }
        });

        for(File f : files) {
            System.out.println(f.getName());
        }
    }
    
    public Pair<String, String>  readDES(String filename) throws Exception {
    	InputStream is = new FileInputStream(filename);
        BufferedReader din = new BufferedReader(new InputStreamReader(is));
        // count the available bytes form the input stream
        int count = is.available();
        // create buffer
        byte[] bs = new byte[count];
        
    	String hostname = "";
    	String uuid = "";
        
        try {
//            File myObj = new File("data.bin");
//            Scanner myReader = new Scanner(myObj);
            hostname = din.readLine().trim();
            uuid = din.readLine().trim();
          } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
        System.out.println(hostname);
        System.out.println(uuid);
        return new Pair<String, String>(hostname, uuid);
    	
    }
}
