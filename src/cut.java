/**
 * @author donglin
 *
 */
import bnotai.tekla.material.data.*;
import bnotai.tekla.material.fileio.*;
import java.lang.*;
import java.net.URISyntaxException;
import java.util.*;
import java.text.*;
import java.text.NumberFormat.Style;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;

// 2003 = HSSFWorkbook, 2007 = XSSFWorkbook;
//import org.apache.commons.math3.util.Pair;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;  
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;  
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.commons.io.FilenameUtils;

//零件編號(18M2) : comp
//接頭代碼(W-01) : connCode
//接頭型號(XP,	XN,	ZP,	ZN, OS): connMdl
//材質(SN400YB) : mater
//規格(RH300*300*10*15) : spec
//鑽頭尺寸 : 500mm

public class cut {
	static Txtio txtio = new Txtio();
	static Excelio excelio = new Excelio();
	static Crypto crypto = null;
	
    static Hashtable<String, W> wtable = new Hashtable<String, W>();
    static Hashtable<String, WR> wrtable = new Hashtable<String, WR>();
    static Hashtable<String, FR> frtable = new Hashtable<String, FR>();

    public static void main(String argv[]) throws Exception, IOException, InterruptedException, URISyntaxException{
    	// create console for user
    	Console console = System.console();
        if(console == null && !GraphicsEnvironment.isHeadless()){
            String filename = auto.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(6);
            Runtime.getRuntime().exec(new String[]{"cmd","/c","start","cmd","/k","java -jar \"" + filename + "\""});
        }else{
            System.out.println("Program has ended, please type 'exit' to close the console");
        }
        
    	// lock
    	Pair<String, String> access = txtio.readDES("data.dat");
    	
		Crypto crypto = new Crypto();
		String hostname = crypto.gethostname();
		String MachineID = crypto.getMachineID();
    	if((boolean)(!hostname.equals(crypto.decrypt(access.getFirst()))) && (boolean)(!MachineID.equals(crypto.decrypt(access.getSecond())))){
    		System.exit(0);
    	}
    	
    	// program description
    	System.out.println("This program read material list and generate report\n");
    	
    	// read input file name
    	String prjname = txtio.getprjname("input.txt");
    	String loadtag = "Sum";
    	String tag = "Cut";
    	System.out.println("Reading project :" + prjname);
    	File[] listOfFiles = txtio.getfilesname(".\\prj\\"+prjname+"\\ncl\\", "*.ncl");
    	
    	// set read data folder
    	String prjdir = ".\\prj\\"+prjname+"\\";
    	
    	// read input excel template
    	String extStr = excelio.checkWbVer(prjdir + prjname);
    	System.out.println("\nExcel template is " + prjdir + prjname + extStr);
    	Workbook tplwb = excelio.getWb(prjdir + prjname + loadtag  + extStr);
    	excelio.setCellStyle(tplwb);
    	// modify company name
    	excelio.setCorp("X X 有 限 公 司", prjname);
    	excelio.plotTriaxial();
    	
    	Hashtable<String, Triple<List<Triple<Double, Double, Double>>, List<Triple<Double, Double, Double>>, List<Triple<Double, Double, Double>>>> 
    		rawncl = txtio.readncl(prjdir+"\\ncl\\", listOfFiles);
    	Interpreter interpreter = new Interpreter(rawncl);
    	
//    	for(File f : listOfFiles) {
//    		try {
//    			System.out.println(f.getName() +" lw first element: "+rawncl.get(FilenameUtils.removeExtension(f.getName())));
//    		}catch(Exception e){
//    			
//    		}
//    	}
//    			System.out.println(f.getName() +" lw first element: "+rawncl.get(FilenameUtils.removeExtension(f.getName())));
//    		}
//    	Triple<List<String>, List<String>, List<String>> ncl = txtio.readncls(prjdir+"\\ncl\\"+listOfFiles[i].getName());
    	excelio.setTriaxial(tplwb);
    	
    	// export to new excel
    	excelio.writeWb(tplwb, prjdir + prjname + tag + extStr);
    	System.out.println("done");
    	System.in.read();
    }
}