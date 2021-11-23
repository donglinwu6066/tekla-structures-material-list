/**
 * @author donglin
 *
 */
import bnotai.tekla.material.data.*;
import bnotai.tekla.material.fileio.*;
import java.lang.*;
import java.util.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;

//import org.apache.commons.math3.util.Pair;
// 2003 = HSSFWorkbook, 2007 = XSSFWorkbook;
import org.apache.poi.hssf.usermodel.*;
// import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;

public class sum {
	static Txtio txtio = new Txtio();
	static Excelio excelio = new Excelio();
	static Crypto crypto = null;
	
    public static void main(String argv[]) throws Exception{
    	// lock
    	Pair<String, String> access = txtio.readDES("data.dat");
    	
		Crypto crypto = new Crypto();
		String hostname = crypto.gethostname();
		String MachineID = crypto.getMachineID();
    	if((boolean)(!hostname.equals(crypto.decrypt(access.getFirst()))) && (boolean)(!MachineID.equals(crypto.decrypt(access.getSecond())))){
    		System.exit(0);
    	}
    	
    	// program description
    	System.out.println("This program accmulate all material types used from tekla\n");
    	
    	// read input file name
    	String prjname = txtio.getprjname("input.txt");
    	String loadtag = "Auto";
    	String tag = "Sum";
    	System.out.println("Reading project: " + prjname);
    	File[] listOfFiles = txtio.getfilesname(".\\prj\\"+prjname+"\\ncl\\", "*.ncl");
    	
    	// set read data folder
    	String prjdir = ".\\prj\\"+prjname+"\\";
    	
    	// read ncl
    	Hashtable<String, Triple<List<Triple<Double, Double, Double>>, List<Triple<Double, Double, Double>>, List<Triple<Double, Double, Double>>>> 
		rawncl = txtio.readncl(prjdir+"\\ncl\\", listOfFiles);
    	Interpreter interpreter = new Interpreter(rawncl);
    	interpreter.genConnCode();
    	
    	// read input excel template
    	String extStr = excelio.checkWbVer(prjdir);
    	System.out.println("\nExcel template is " + prjdir + prjname + loadtag + extStr);
    	Workbook tplwb = excelio.getWb(prjdir + prjname + loadtag + extStr);
    	
    	// get the number of rows in data sheet
    	int maxrow = excelio.cntmaxrow(tplwb, 1, 1);
    	Pair<int[][], Integer> content = excelio.cntsum(tplwb, 1, maxrow);
    	excelio.cr8sumsheet(tplwb, "總計", content);
    	
    	// export to new excel
    	excelio.writeWb(tplwb, prjdir + prjname + tag + extStr);
    	System.out.println("done\n");
    	
    }
}
