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


public class auto {
	static Txtio txtio = new Txtio();
	static Excelio excelio = new Excelio();
	
    public static void main(String argv[]) throws Exception{
    	// program description
    	System.out.println("This program read .sym from tekla to excel\n");
    	
    	// read input file name
    	String prjname = txtio.getprjname("input.txt");
    	String tag = "Auto";
    	System.out.println("Reading project :" + prjname);
    	File[] listOfFiles = txtio.getfilesname(".\\prj\\"+prjname+"\\sym\\", "*.sym");
    	
    	// set read data folder
    	String prjdir = ".\\prj\\"+prjname+"\\";
    	
    	// read input excel template
    	String extStr = excelio.checkWbVer(prjdir + prjname);
    	System.out.println("\nExcel template is " + prjdir + prjname + extStr);
    	Workbook tplwb = excelio.getWb(prjdir + prjname + extStr);
    	excelio.setCellStyle(tplwb);
    	
    	// get .sym data
    	Pair<List<String>, List<Integer>> symdata = txtio.readsym(prjdir+"\\sym\\", listOfFiles);

    	// write to new workbook
    	excelio.cr8symsheet(tplwb, 1, symdata);
        
    	// export to new excel
    	excelio.writeWb(tplwb, prjdir+prjname+tag+extStr);
    	System.out.println("done\nPress Enter to exit");
    	System.in.read();
    }

}
