package bnotai.tekla.material.fileio;
import bnotai.tekla.material.data.*;

import java.lang.*;
import java.util.*;
import java.util.Map.Entry;
import java.text.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
//import org.apache.commons.math3.util.Pair;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.BorderStyle;  
import org.apache.poi.ss.usermodel.HorizontalAlignment;  
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
//零件編號(18M2) : comp
//接頭代碼(W-01) : connCode
//接頭型號(XP,	XN,	ZP,	ZN, OS): connMdl
//材質(SN400YB) : mater
//規格(RH300*300*10*15) : spec
//鑽頭尺寸 : 500mm

public class Excelio {
    static CellStyle cellStyle;
    static CellStyle cellStyle_red;
    static CellStyle cellStyle_blue_border;
    static CellStyle cellStyle_border;
    static CellStyle cellStyle_red_border;
    static int sheetlen = 50;
//    static int compTypes = 0;
//    static int compIdx = 0;
    String corpname = "Your company name";
    String prjname = "Project Name";
    static Hashtable<String, UBeam> comp2mater;
    static LinkedHashMap<BCOrd, Integer> UBCrec;
    static Hashtable<String, String> comp2connCode;
    static Hashtable<String, W> wtable = new Hashtable<String, W>();
    static Hashtable<String, WR> wrtable = new Hashtable<String, WR>();
    static Hashtable<String, FR> frtable = new Hashtable<String, FR>();
    
//    static Hashtable<comp, UBeam> connCode2connMdl;
//	static Vector<Pair<Material, Integer>> prediction;
	public Excelio() {
		
	}
	/**
	 * 
	 * @param {String} path
	 * @return {Workbook}
	 */
	public void setCorp(String corpname, String prjname) {
		this.corpname = corpname;
		this.prjname = prjname;
	}
	public void setTriaxial(Workbook wb) {
		comp2mater = getComp2mater(wb, 0);
		UBCrec = getUBCrecoder(wb, 1);
		buildconnCode2drill(wb, 2);
		comp2connCode = getComp2connCode(wb, 3);
		Sheet sheet = null;
		
		
		Iterator<BCOrd> itr = UBCrec.keySet().iterator();
		String lastTag = "";
		int page = 0;
		int idx = 0;
//		System.out.println(UBCrec);
		while(itr.hasNext()) {
			BCOrd bcord = itr.next();
			int cnt = UBCrec.get(bcord);
			String []compTag = bcord.comps.get(0).getFirst().split("M");
			if(!lastTag.equals(compTag[0])) {
				sheet = wb.createSheet(compTag[0]+"M");
				lastTag = compTag[0];
				idx = 0;
			}
			
			plotTable(sheet, idx);
			fillTable(sheet, idx, bcord, cnt);
			idx ++;
		}
		
		
	}
	public Hashtable<String, UBeam> getComp2mater(Workbook wb, int page){
		Sheet sheet = wb.getSheetAt(page);
        Hashtable<String, UBeam> hashtable = new Hashtable<String, UBeam>();
        Row row = null;
        Cell cellComp = null;
        Cell cellLen = null;
        Cell cellSpec = null;
        Cell cellMater = null;
        
        int i=1;
        while(true){
            row = sheet.getRow(i);
            if (row != null) {
                cellComp = row.getCell(0);
                cellLen = row.getCell(2);
                cellSpec = row.getCell(4);
                cellMater = row.getCell(5);
                if(!cellComp.toString().equals("")){
                    hashtable.put(cellComp.toString(), new UBeam(cellSpec.toString(), cellMater.toString(), cellLen.getNumericCellValue()));
                }
                else{
                    break;
                }
            }
            else{
                break;
            }
            i++;
        }
        return hashtable;
    }
	public void buildconnCode2drill(Workbook wb, int page) {
		Sheet sheet = wb.getSheetAt(page);
        Row row = null;
        Cell cell = null;
        String connCodeStr = "";
        int r = 1;
        while(true) {
        	row = sheet.getRow(r);
        	if (row == null) {break;}
        	cell = row.getCell(0);
        	
        	try {
        		connCodeStr = cell.toString();
            } catch (NullPointerException NPE) {
            	
            	break;
//            	connCodeStr = null;
            }

        	String[] split = row.getCell(0).toString().split("-");
        	System.out.println(split);
        	if(split[0].equals("W")){
                wtable.put(row.getCell(0).toString(), read_w(row));
                System.out.println("put " + row.getCell(1).toString());
            }
            else if(split[0].equals("WR")){
                Row rowXZ[] = {row, sheet.getRow(++r)};
                wrtable.put(row.getCell(0).toString(), read_wr(rowXZ));
            }
            else if(split[0].equals("FR")){
                Row rowXY[] = {row, sheet.getRow(++r)};
                frtable.put(row.getCell(0).toString(), read_fr(rowXY));
            }
            else{
                System.out.println("\ndatabase error!!!!\n");
                System.exit(0);
            }
        	
        	r++;
        	
        }
	}
    W read_w(Row row){
        W w = new W();
        ArrayList<Double> vec = new ArrayList<Double>();
        for(int i= 1 ;i<=5 ;i++){
            vec.add(row.getCell(i).getNumericCellValue());
        }
        w.code = row.getCell(0).toString();
        w.set(vec);
        return w;
    }
    WR read_wr(Row rowXZ[]){
        ArrayList<Double> vec = new ArrayList<Double>();
        WR wr = new WR();
        int i = 2;
        if(rowXZ[0].getCell(1).toString().equals("X") && rowXZ[1].getCell(1).toString().equals("Z")){
            wr.code = rowXZ[0].getCell(0).toString();
            while(rowXZ[0].getCell(i) != null && rowXZ[1].getCell(i) != null && !rowXZ[0].getCell(i).toString().equals("")){
                wr.addx(rowXZ[0].getCell(i).getNumericCellValue());
                wr.addz(rowXZ[1].getCell(i).getNumericCellValue());
                i++;
            }
        }
        else{
            System.out.println("Wrong WR database");
            System.exit(0);
        }

        return wr;
    }
    FR read_fr(Row rowXY[]){
        ArrayList<Double> vec = new ArrayList<Double>();
        int i = 2;
        FR fr = new FR();
        if(rowXY[0].getCell(1).toString().equals("X") && rowXY[1].getCell(1).toString().equals("Y")){
            fr.code = rowXY[0].getCell(0).toString();
            while(rowXY[0].getCell(i) != null && rowXY[1].getCell(i) != null&& !rowXY[0].getCell(i).toString().equals("")){
                fr.addx(rowXY[0].getCell(i).getNumericCellValue());
                fr.addy(rowXY[1].getCell(i).getNumericCellValue());
                i++;
            }
        }
        else{
            System.out.println("Wrong FR database");
            System.exit(0);
        }
        return fr;
    }
	public LinkedHashMap<BCOrd, Integer> getUBCrecoder(Workbook wb, int page){
		Sheet sheet = wb.getSheetAt(page);
		LinkedHashMap<BCOrd, Integer> hashmap = new LinkedHashMap<BCOrd, Integer>();
		Row row = null;
		Cell cell = null;
//		compTypes = 0;
//		String compTypeStr = "";
		int maxrow = cntmaxrow(wb, 1, 1);
//		System.out.println("maxrow for getUBCrecoder " + maxrow);
		
		
		for(int i = 1 ; i<maxrow ; i++) {
			BCOrd bcord = getBCOrd(sheet.getRow(i));
			Integer cnt = hashmap.get(bcord);
			if(cnt == null) {
				hashmap.put(bcord, 1);
//				System.out.println("bcord " + bcord);
//				String []split = sheet.getRow(i).getCell(2).toString().split("M");
//				if(!split[0].equals(compTypeStr)) {
//					compTypeStr = split[0];
//					compTypes++;
//				}
			}
			else {
				
				hashmap.put(bcord, ++cnt);
//				System.out.println("else bcord " + bcord + " and " + cnt);
			}
		}
		
		return hashmap;
	}
	public Hashtable<String, String> getComp2connCode(Workbook wb, int page){
		Sheet sheet = wb.getSheetAt(page);
		Hashtable<String, String> hashtable = new Hashtable<String, String>();
		int maxrow = cntmaxrow(wb, page, 0);
//		System.out.println("maxrow for getComp2connCode " + maxrow);
		Row row = null;
		Cell cell =null;
		String str = "";
		String compStr = "";
		String connCodeStr = "";
		int c = 0;
		for(int r = 1; r< maxrow ;r++) {
			row = sheet.getRow(r);
			c = 0;
			str = "";
			connCodeStr = "";
			while((cell = row.getCell(c)) != null) {
				cell = row.getCell(c);
				str = cell.toString();
				if(!str.equals("") && str != null) {
					if(c>0) {
						connCodeStr += str + " ";
					}
					else {
						compStr = str;
					}
				}
				c++;
			}
			hashtable.put(compStr, connCodeStr);
//			System.out.println(compStr + ": " + connCodeStr);
		}
		return hashtable;
	}
//	public Hashtable<String, String> getConnCode2connMdl(Sheet sheet)
	public BCOrd getBCOrd(Row row) {
		BCOrd bcord = new BCOrd();
		Cell cell = null;
		String comp = "";
		if((cell = row.getCell(1))!=null) {
			bcord.len = (int)cell.getNumericCellValue();
		}
		int i = 2;
		while((cell = row.getCell(i))!=null) {
			comp = cell.toString();
			if(!comp.equals("")) {
				bcord.add(comp);
				i += 2;
			}
			else {
				break;
			}
			
		}
		return bcord;
	}
	
	public void plotTable(Sheet sheet, int idx) {
		int start = idx*sheetlen;
        Row row = null;
        Cell cell = null;
        
        for(int i=start; i<start+sheetlen ; i++){
            row = sheet.createRow(i);    
            for(int j=0 ; j<12;j++){
                cell = row.createCell(j);
            }
            
        }
        Date dNow = new Date( );
        int [][]region = {{start,start,0,11}, {start+1,start+1,0,4}, {start+11,start+11,7,8}, {start+14,start+14,7,8}, {start+15,start+16,7,8},
        				{start+14,start+14,9,10}, {start+15,start+16,9,10}};
        // corpname, project, count, lister, lister2, auditor, auditor2
        CellRangeAddress []callRangeAddress = {new CellRangeAddress(region[0][0],region[0][1],region[0][2],region[0][3]), 
        										new CellRangeAddress(region[1][0],region[1][1],region[1][2],region[1][3]),
        										new CellRangeAddress(region[2][0],region[2][1],region[2][2],region[2][3]), 
        										new CellRangeAddress(region[3][0],region[3][1],region[3][2],region[3][3]), 
        										new CellRangeAddress(region[4][0],region[4][1],region[4][2],region[4][3]), 
        										new CellRangeAddress(region[5][0],region[5][1],region[5][2],region[5][3]), 
        										new CellRangeAddress(region[6][0],region[6][1],region[6][2],region[6][3])};
        for(CellRangeAddress cra : callRangeAddress) {
        	sheet.addMergedRegion(cra);
        }
        
        
        SimpleDateFormat date = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        
        String[] str = {corpname, prjname, "素材總數:", "製表", "", 
        		"核對", "", "編號:", "FILE:", "PRO:", 
        		"NO:", "素材長度", "寬度", "翼板高度", "翼板厚度",
        		"材質", "CNC 鑽孔程式表", "CHECK:", "使用長度:", "製表日期:", 
        		date.format(dNow), "出表日期:"};
        
        int [][]points = {{region[0][1],region[0][2]}, {region[1][0], region[1][2]}, {region[2][0], region[2][2]}, {region[3][0], region[3][2]},{region[4][0], region[4][2]}, 
        				{region[5][0], region[5][2]}, {region[6][0], region[6][2]}, {start+2,0}, {start+5,0}, {start+6,0}, 
        				{start+7,0},{start+3,2}, {start+4,2}, {start+5,2}, {start+6,2}, 
        				{start+7,2}, {start+1,7}, {start+12,7}, {start+13,7}, {start+17,7}, 
        				{start+17,9},{start+18,7}};
        
        CellStyle []setCellStyles ={cellStyle, cellStyle_red, cellStyle, cellStyle, cellStyle, 
        		cellStyle_red, cellStyle, cellStyle, cellStyle, cellStyle, 
        		cellStyle, cellStyle, cellStyle, cellStyle, cellStyle, 
        		cellStyle, cellStyle, cellStyle, cellStyle, cellStyle, 
        		cellStyle, cellStyle};
        
        
        for(int i=0 ; i<str.length ; i++) {
        	(new Text(str[i], sheet, setCellStyles[i], new Pair<Integer, Integer>(points[i][0], points[i][1]))).put();
        }
        for(int i=3 ; i<callRangeAddress.length ; i++) {
            RegionUtil.setBorderBottom(BorderStyle.THIN, callRangeAddress[i], sheet); // 下邊框
            RegionUtil.setBorderLeft(BorderStyle.THIN, callRangeAddress[i], sheet); // 左邊框
            RegionUtil.setBorderRight(BorderStyle.THIN, callRangeAddress[i], sheet); // 有邊框
            RegionUtil.setBorderTop(BorderStyle.THIN, callRangeAddress[i], sheet); // 上邊框
        }
        
        // ******************** //
        String steps[] = {"步 驟", "位 置", "左翼板", "腹 板", "右翼板", " "};
        String number[] = {"編 號", "構 件", "長 度", "零 件", "支 數"};
        tabulate(sheet, steps, start+8, 26, 0, 6);
        tabulate(sheet, number, start+3, 6, 7, 5);
        
	}
	public void fillTable(Sheet sheet, int idx, BCOrd bcord, int cnt) {
		int start = idx*sheetlen;
		//String spec = bcord.comps.get(0).getFirst();
		String comp = bcord.comps.get(0).getFirst();
		String spec = comp2mater.get(comp).spec;
		System.out.println("spec is = " +spec);
		String []split = comp2mater.get(comp).spec.split("[*]");
		String []split2 = split[0].split("(?<=\\D)(?=\\d)");

        // 編號, 規格, 素材長度, 寬度, 翼板高度, 
        //  模板厚度, 材質, 素材總數
        String[] str = {String.valueOf(idx+1), spec, String.valueOf(bcord.len), split2[1], split[1], 
                    split[2], comp2mater.get(comp).mater, Integer.toString(cnt)};
        int [][]points ={{start+2, 1}, {start+2, 7}, {start+3, 3}, {start+4, 3}, {start+5, 3}, 
        {start+6, 3}, {start+7, 3}, {start+11, 9}};
        CellStyle []setCellStyles ={cellStyle, cellStyle, cellStyle_red, cellStyle, cellStyle,
            cellStyle, cellStyle_red, cellStyle_red};
		
        for(int i=0; i<str.length ;i++){
            (new Text(str[i], sheet, setCellStyles[i], new Pair<Integer, Integer>(points[i][0], points[i][1]))).put();;
        }
		
        String len = "";
        String compCnt = "";
        double checkcnt = 10 + 3;
		//upper right
        for(int i=0 ; i<bcord.comps.size(); i++) {
        	comp = bcord.comps.get(i).getFirst();
        	if(comp == null || comp2mater.get(comp) == null) {
        		continue;
        	}

	        len = format(String.valueOf(comp2mater.get(comp).len));
	       	compCnt = String.valueOf(bcord.comps.get(i).getSecond());
	       	
	       	(new Text(len, sheet, cellStyle_border, new Pair<Integer, Integer>(start+4+i, 9))).put();
	       	(new Text(comp, sheet, cellStyle_border, new Pair<Integer, Integer>(start+4+i, 10))).put();
	       	(new Text(compCnt, sheet, cellStyle_border, new Pair<Integer, Integer>(start+4+i, 11))).put();

        	// checkcnt += count*len + count*3 ;
        	checkcnt += bcord.comps.get(i).getSecond()*(comp2mater.get(comp).len + 3);
        }
		
        (new Text(format(checkcnt+""), sheet, cellStyle, new Pair<Integer, Integer>(start+12, 9))).put();
        //lower right
        Set<String> wSet = new HashSet<String>();
        Set<String> wrSet = new HashSet<String>();
        Set<String> frSet = new HashSet<String>();
        
        
        for(int i=0 ; i<bcord.comps.size();i++){
        	comp = bcord.comps.get(i).getFirst();
        	String code = comp2connCode.get(comp);
        	if(code == null) {
        		continue;
        	}
        	String[] split3 = code.split(" ");
        	for(int j = 0 ; j<split3.length ; j++) {
        		String[] split4 = split3[j].split("-");
            	
        		if(split3[j] == null || split3[j].equals(""))
        			continue;
//        		System.out.println(comp +" split3 is "+split3[j]);
	        	if(split4[0].equals("W")) {
	        		wSet.add(split3[j]);
	        	}
	        	else if(split4[0].equals("WR")) {
	        		wrSet.add(split3[j]);
	        	}
	        	else if(split4[0].equals("FR")) {
	        		frSet.add(split3[j]);
	        	}
        	}
        }
        
        // Creating an iterator 
        Iterator []value = {wSet.iterator(), wrSet.iterator(), frSet.iterator()}; 
  
        // Displaying the values after iterating through the iterator 
        int row_r = start+19;
        int col_r = 9;
        int row_l = start+19;
        int col_l = 7;
        int height = 0;
        for(int i=0;i<3;i++){
            while (value[i].hasNext() ) { 
                String code = value[i].next().toString();
                if(row_l <= row_r){
                    height = fillCode(sheet, code, row_l, col_l);
                    row_l += height;
                }
                else{
                    height = fillCode(sheet, code, row_r, col_r);
                    row_r += height;
                }
            } 
        }
	}
	public void tabulate(Sheet sheet, String[] items, int startrow, int height, int startcol, int width) {
		Row row = null;
		Cell cell = null;
		for (int i = startrow; i < startrow+height; i++) {
            row = sheet.getRow(i);
            for(int j =startcol ; j<startcol+width ;j++){
                cell = row.getCell(j);
                cell.setCellStyle(cellStyle_border);
                if(i == startrow){
                    cell.setCellValue(items[j-startcol]);
                }
                else{
                    if(j==startcol){
                        cell.setCellValue(i-startrow);
                    }    
                }
            }
        }
	}
    static int fillCode(Sheet sheet, String code, int hor, int ver){
        int height = 6;

        CellRangeAddress callRangeAddress_header = new CellRangeAddress(hor, hor, ver, ver+1);
        sheet.addMergedRegion(callRangeAddress_header);
        Row row = sheet.getRow(hor);
        Cell cell = row.getCell(ver);
        cell.setCellValue(code);
        
        RegionUtil.setBorderBottom(BorderStyle.THIN, callRangeAddress_header, sheet); // 下邊框
        RegionUtil.setBorderLeft(BorderStyle.THIN, callRangeAddress_header, sheet); // 左邊框
        RegionUtil.setBorderRight(BorderStyle.THIN, callRangeAddress_header, sheet); // 有邊框
        RegionUtil.setBorderTop(BorderStyle.THIN, callRangeAddress_header, sheet); // 上邊框

        String[] split = code.split("-");
        if(split[0].equals("W")){
            cell.setCellStyle(cellStyle_red_border);
            String[] str = {"XP", "XN", "ZP", "ZN", "OS"};
//            System.out.println(code);
            Double[] strCode = {wtable.get(code).drill.get(0), wtable.get(code).drill.get(1), wtable.get(code).drill.get(2), wtable.get(code).drill.get(3), wtable.get(code).drill.get(4)};
            for(int i=0; i<5 ;i++){
                (new Text(str[i], sheet, cellStyle_border, new Pair<Integer, Integer>(hor+1+i, ver))).put();
                (new Text(format(strCode[i]+""), sheet, cellStyle_border, new Pair<Integer, Integer>(hor+1+i, ver+1))).put();
                
            }
            height =6;
        }else if(split[0].equals("WR")){
            cell.setCellStyle(cellStyle_red_border);
            String[] str = {"X", "Z"};
            for(int i=0; i<2 ;i++){
                (new Text(str[i], sheet, cellStyle_border, new Pair<Integer, Integer>(hor+1, ver+i))).put();
            }

            for(int i=0;i<wrtable.get(code).x.size();i++){
                (new Text(format(wrtable.get(code).x.get(i)+""), sheet, cellStyle_border, new Pair<Integer, Integer>(hor+2+i, ver))).put();
                (new Text(format(wrtable.get(code).z.get(i)+""), sheet, cellStyle_border, new Pair<Integer, Integer>(hor+2+i, ver+1))).put();
            }

            height = wrtable.get(code).x.size()+2;
        }else if(split[0].equals("FR")){
            cell.setCellStyle(cellStyle_blue_border);
            String[] str = {"X", "Y"};
            for(int i=0; i<2 ;i++){
                (new Text(str[i], sheet, cellStyle_border, new Pair<Integer, Integer>(hor+1, ver+i))).put();
            }
            for(int i=0;i<frtable.get(code).x.size();i++){
                (new Text(format(frtable.get(code).x.get(i)+""), sheet, cellStyle_border, new Pair<Integer, Integer>(hor+2+i, ver))).put();
                (new Text(format(frtable.get(code).y.get(i)+""), sheet, cellStyle_border, new Pair<Integer, Integer>(hor+2+i, ver+1))).put();
            }

            height = frtable.get(code).x.size()+2;
        }

        return height;

    }
	public void plotTriaxial() {
		
	}
 	public Workbook getWb(String path) {

		Workbook wb = null;
		InputStream is;
		String ext = FilenameUtils.getExtension(path);
		try{
			is = new FileInputStream(path);
			if(ext.equals("xlsx")) {
				wb = new XSSFWorkbook(is);
			}
			else {
				wb = new HSSFWorkbook(is);
			}
		}catch(IOException e){			
            System.out.println("Fail to read .xlsx or .xls from " + path);
            System.exit(0);
		}
		return wb;
	}
    public void setCellStyle(Workbook wb){

        cellStyle = wb.createCellStyle();
        cellStyle_red = wb.createCellStyle();
        cellStyle_blue_border = wb.createCellStyle();
        cellStyle_border = wb.createCellStyle();
        cellStyle_red_border = wb.createCellStyle();
        
        // font size
        Font font = wb.createFont();
        Font font_red = wb.createFont();
        Font font_blue = wb.createFont();
        font.setFontHeightInPoints((short)12);
        font_red.setFontHeightInPoints((short)12);
        font_blue.setFontHeightInPoints((short)12);
        cellStyle.setFont(font);
        cellStyle_border.setFont(font);
        // red
        font_red.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
        cellStyle_red.setFont(font_red);
        cellStyle_red_border.setFont(font_red);
        // blue
        font_blue.setColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
        cellStyle_blue_border.setFont(font_blue);
        // border
        cellStyle_border.setBorderBottom(BorderStyle.THIN); 
        cellStyle_border.setBorderLeft(BorderStyle.THIN);
        cellStyle_border.setBorderTop(BorderStyle.THIN);
        cellStyle_border.setBorderRight(BorderStyle.THIN);
        cellStyle_red_border.setBorderBottom(BorderStyle.THIN); 
        cellStyle_red_border.setBorderLeft(BorderStyle.THIN);
        cellStyle_red_border.setBorderTop(BorderStyle.THIN);
        cellStyle_red_border.setBorderRight(BorderStyle.THIN);
        cellStyle_blue_border.setBorderBottom(BorderStyle.THIN); 
        cellStyle_blue_border.setBorderLeft(BorderStyle.THIN);
        cellStyle_blue_border.setBorderTop(BorderStyle.THIN);
        cellStyle_blue_border.setBorderRight(BorderStyle.THIN);
        // center
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle_red.setAlignment(HorizontalAlignment.CENTER);
        cellStyle_blue_border.setAlignment(HorizontalAlignment.CENTER);
        cellStyle_border.setAlignment(HorizontalAlignment.CENTER);
        cellStyle_red_border.setAlignment(HorizontalAlignment.CENTER);

    }
	/**
	 * return the excel version of template, xlsx(2007) is prior
	 * @param {String} path file path without extension
	 * @returns {String} xlsx or xls extension
	 */
	public String checkWbVer(String path) {
    	if(!(new File(path+"xlsx").isFile())) {
    		return ".xlsx";
    	}
    	else if(!(new File(path+"xls").isFile())){
    		return ".xls";
    	}
    	else {
    		System.exit(1);
    	}
		return null;
	}
	/** Read .sym data and create new sheet in old excel 
	 * @param {Workbook} wb template 
	 * @param {int} page prefilled formula sheet  
	 * @param {Pair<List<String>, List<Integer>>} symdata first value is
	 */
	public void cr8symsheet(Workbook wb, int page, Pair<List<String>, List<Integer>> symdata) {
		System.out.println("creating sheet");
		Sheet sheet = wb.getSheetAt(page);
		int cnt = 0;
		int r = 1, c = 1;
		int dupecnt, dupeidx = 0;
		int linemax = symdata.getFirst().size();
		Row row = null;
		Cell cell = null;

		while (cnt < linemax) {
            row = sheet.getRow(r);
            while (true) {
                cell = row.getCell(c);
                if (cell == null) {
                    cell = row.createCell(c);
                }
                
                dupecnt = symdata.getSecond().get(dupeidx);
                if ("Stock".equals(symdata.getFirst().get(cnt).substring(0, 5))) {
                    String[] tokens = symdata.getFirst().get(cnt).split(" ");
                    // ------------
                    
                    for (int i = 0; i < dupecnt; i++) {
                        row = sheet.getRow(r + i);
                        cell = row.getCell(c);
                        if (cell == null) {
                            cell = row.createCell(c);
                        }
                        cell.setCellValue(Integer.valueOf(tokens[4].substring(0, tokens[4].lastIndexOf("."))));
                        
                    }
                    // ------------
                } else if ("(".equals(symdata.getFirst().get(cnt).substring(0, 1))) {
                    String[] tokens = symdata.getFirst().get(cnt).split("-");
                    
                    for (int i = 0; i < dupecnt; i++) {
                        row = sheet.getRow(r + i);
                        cell = row.getCell(c);
                        if (cell == null) {
                            cell = row.createCell(c);
                        }
                        cell.setCellValue(tokens[0].substring(1));
                        cell.setCellStyle(cellStyle_red_border);
                        wb.getCreationHelper().createFormulaEvaluator().evaluateFormulaCell(sheet.getRow(r + i).getCell(c+1));
                        wb.getCreationHelper().createFormulaEvaluator().evaluateFormulaCell(sheet.getRow(r + i).getCell(0));
                    }
                    
                    if (((cnt + 1) == linemax) || "Stock".equals(symdata.getFirst().get(cnt + 1).substring(0, 5))) {
                        break;
                    }
                }

                if(c==1){c++;}
                else{c+=2;}
                	
                cnt++;
            }
            r += symdata.getSecond().get(dupeidx);
            c = 1;
            dupeidx++;
            cnt++;
        }
	}
	
	/**
	 * @param {Workbook} wb 
	 * @param {String} path destination
	 */
	public void writeWb(Workbook wb, String path) {
		System.out.println("writing to sheet");
        try {
            FileOutputStream fos = new FileOutputStream(new File(path));
            wb.write(fos);
            fos.flush();
            fos.close();
            System.out.println("Write to " + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	/**
	 * @param {Workbook} wb 
	 * @param {int} page page of workbook
	 * @param {int} inxc reference column
	 * @return return max row by reference column
	 */
	public int cntmaxrow(Workbook wb, int page, int inxc) {
		Sheet sheet = wb.getSheetAt(page);
		String existStr = "";
        Row row = null;
        Cell cell = null;
        int rcnt = 1;
        
        while (true) {
            row = sheet.getRow(rcnt);
            if (row != null) {
                cell = row.getCell(inxc);
                try {
                    existStr = cell.toString();
                    System.out.println(existStr);
                } catch (NullPointerException NPE) {
                    existStr = null;
                }
                // System.out.println(cell);
                if (existStr != "") {
                	rcnt++;
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        return rcnt;
	}
	/**
	 * @param {Workbook} wb
	 * @param {int} page page of workbook
	 * @param rmax get from (Workbook wb, int page, int inxc)
	 */
	public Pair<int[][], Integer> cntsum(Workbook wb, int page, int rmax) {
		Sheet sheet = wb.getSheetAt(page);
		int[][] content = new int[rmax][3];
        int tail = 0, cursor = 0;
        int len, spec;
        String lenStr, specStr;
        boolean newrow = true;
        Row row = null;
        Cell cell = null;

        for (int i = 1; i < rmax; i++) {
            newrow = true;
            cursor = 0;
            row = sheet.getRow(i);
            // len
            lenStr = row.getCell(1).toString();
            len = Integer.valueOf(lenStr.substring(0, lenStr.indexOf(".")));
            // spec
            specStr = row.getCell(2).toString();
            spec = Integer.valueOf(specStr.substring(0, specStr.indexOf("M")));
            // record
            while (cursor < tail) {
                if (spec == content[cursor][0] && len == content[cursor][1]) {
                    newrow = false;
                    content[cursor][2]++;
                    break;
                }
                cursor++;
            }
            if (newrow) {
                content[tail][0] = spec;
                content[tail][1] = len;
                content[tail][2] = 1;
                tail++;
            }
        }
        return new Pair<int[][], Integer>(content, tail);
	}
	public void cr8sumsheet(Workbook wb, String sheetname, Pair<int[][], Integer> content) {
		Sheet sheet = wb.createSheet(sheetname);
        Row row = null;
        Cell cell = null;
        int lastidx = content.getSecond();
        // create first row

        String[] title = { "素材", "長度", "數量" };
        row = sheet.createRow(0);
        for (int i = 0; i < 3; i++) {
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
        }

        for (int i = 1; i <= lastidx; i++) {
            row = sheet.createRow(i);
            for (int j = 0; j < 3; j++) {
                cell = row.createCell(j);
                cell.setCellValue(content.getFirst()[i - 1][j]);
            }
        }
	}
    static String format(String val){
        String stringVal = String.valueOf(val);
        String[] number = stringVal.split( "[.]" );
        if(number.length>1 && number[1].equalsIgnoreCase("0")){
            return number[0];                               
        } else {

            return String.format("%.1f", Double.parseDouble(val));
        }
    }
}
