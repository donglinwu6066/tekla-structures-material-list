import java.lang.*;
import java.util.*;
import java.text.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;

// 2003 = HSSFWorkbook, 2007 = XSSFWorkbook;
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


public class cut {
    static Hashtable<String, Element> database;
    static Hashtable<String, Pair<String, String>> elem2texture;
    static Vector<Pair<Material, Integer>> prediction;
    static Hashtable<String, W> wtable = new Hashtable<String, W>();
    static Hashtable<String, WR> wrtable = new Hashtable<String, WR>();
    static Hashtable<String, FR> frtable = new Hashtable<String, FR>();
    // static HSSFCellStyle cellStyle;
    // static HSSFCellStyle cellStyle_red;
    // static HSSFCellStyle cellStyle_blue_border;
    // static HSSFCellStyle cellStyle_border;
    // static HSSFCellStyle cellStyle_red_border;
    static CellStyle cellStyle;
    static CellStyle cellStyle_red;
    static CellStyle cellStyle_blue_border;
    static CellStyle cellStyle_border;
    static CellStyle cellStyle_red_border;
    public static void main(String argv[]) {
        String projectName;
        if(argv.length>0){
            projectName = argv[0];
        }
        else{
            System.out.println("請輸入檔名，格式 java cut 檔名");
            return ;
        }
        // get first input.txt name
        String file = "";
        try (BufferedReader br = Files.newBufferedReader(Paths.get("input.txt"))) {
            if ((file = br.readLine()) == null) {
                System.out.println("Cannot find data in input.txt(reading the first row of .xls)");
                return;
            }
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
        // folder
        File theDir = new File("out-cut");
        theDir.mkdirs();

        // 2003 = xls; 2007 = xlsx;
        List<String> dataList = new ArrayList<>();
        // new file
        String path;
        Workbook wb = null;
        InputStream is;
        try{
            path = ".\\out-cut\\" + file + "Cut.xlsx";
            is = new FileInputStream(".\\out-sum\\" + file + "Sum.xlsx");
            System.out.println("Reading template \n.\\out-sum\\" + file + "Sum.xlsx\n");
            wb = new XSSFWorkbook(is);
        }
        catch(IOException e){
            try{
                path = ".\\out-cut\\" + file + "Cut.xls";
                is = new FileInputStream(".\\out-sum\\" + file + "Sum.xls");
                System.out.println("Reading template \n.\\out-sum\\" + file + "Sum.xls\n");
                wb = new HSSFWorkbook(is);
            }catch(IOException e2){
                System.out.println("Fail to read files");
                e2.printStackTrace();
                System.out.println("Cannot find .xls or .xlsx");
                return;
            }
        }
        // try {
        //     is = new FileInputStream(".\\out-sum\\" + file + "Sum.xls");
        //     System.out.println("Reading template \n.\\out-sum\\" + file + "Sum.xls\n");
        //     if (".xls".equals(extString)) {
        //         wb = new HSSFWorkbook(is);
        //     } else if (".xlsx".equals(extString)) {
        //         wb = new XSSFWorkbook(is);
        //     }
        // } catch (IOException e) {
        //     System.out.println("\nCannot find .\\out-sum\\" + file + "Sum.xls\n");
        //     // print the work file
        //     e.printStackTrace();
        //     if (".xls".equals(extString)) {
        //         wb = new HSSFWorkbook();
        //     } else if (".xlsx".equals(extString)) {
        //         wb = new XSSFWorkbook();
        //     } else {
        //         System.out.println("Cannot find .xls or .xlsx");
        //         return;
        //     }
        // }

        // Excel
        elem2texture = buildElem2Texture(wb.getSheetAt(0));
        prediction = buildPrediction(wb.getSheetAt(1));
        database = buildDatabase(wb.getSheetAt(3));

        // create new excel--------------------------------------------------------------------------------------------------
        String[] split = prediction.get(0).x.elem.get(0).x.split("M");
        String[] split2;
        
        Workbook wb_cut = null;
        String extString = path.substring(path.lastIndexOf("."));
        if(extString.equals(".xls")){
            wb_cut = new HSSFWorkbook();
        }
        else{
            wb_cut = new XSSFWorkbook();
        }
        
        setCellStyle(wb_cut);
        Sheet sheet = wb_cut.createSheet(split[0]+"M");
        int label = 1;
        int start = 0;
        int page =0;
        plotBlock(wb_cut, projectName, start, page);
        fillBlock(wb_cut, prediction.get(0).x, prediction.get(0).y, start, label, page);
        label++;
        start +=50;

        int i = 1;
        while(i< prediction.size()){
            split = prediction.get(i-1).x.elem.get(0).x.split("M");
            split2 = prediction.get(i).x.elem.get(0).x.split("M");
            if(!split[0].equals(split2[0])){
                sheet = wb_cut.createSheet(split2[0]+"M");
                start = 0;
                page ++;
                label = 1;
            }
            plotBlock(wb_cut, projectName, start, page);
            fillBlock(wb_cut, prediction.get(i).x, prediction.get(i).y, start, label, page);
            start +=50;
            i++;
            label++;
        }
        

        // write out Excel
        try {
            FileOutputStream fos = new FileOutputStream(new File(path));
            wb_cut.write(fos);
            fos.flush();
            fos.close();
            System.out.println("Write to\n" + path + "\n");
            System.out.println("Finish");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("All done~");
    }
    
    static Vector<Pair<Material, Integer>> buildPrediction(Sheet sheet){
        Vector<Pair<Material, Integer>> vec = new Vector<Pair<Material, Integer>>();
        Material mb = new Material();
        int count = 0;
        Row row = null;
        Cell cell = null;
        // input first data;
        row = sheet.getRow(1);
        
        vec.add(new Pair<Material, Integer>(read_mb(row), 1));

        int r = 2;
        int i = 0;
        while(true){
            row = sheet.getRow(r);
            mb = read_mb(row);
            
            if(mb!=null){
                if(compMate(vec.get(i).x, mb)){
                    (vec.get(i).y)++;
                }
                else{
                    vec.add(new Pair<Material, Integer>(mb, 1));
                    i++;
                }
                r++;
            }
            else{
                break;
            }

        }
        return vec;
    }
    static Hashtable<String, Element> buildDatabase(Sheet sheet){
        Hashtable<String, Element> database = new Hashtable<String, Element>();
        Element eb = null;
        Row row = null;
        Cell cell = null;
        int r = 1;
        int empty = 0;
        String elemStr;
        while (true) {
            row = sheet.getRow(r);
            if (row != null) {
                cell = row.getCell(0);
                try {
                    elemStr = cell.toString();
                } catch (NullPointerException NPE) {
                    elemStr = null;
                }
                if (elemStr != "") {
                    empty = 0;
                    // first time show up
                    if(database.get(elemStr) == null){
                        String[] split = row.getCell(1).toString().split("-");
                        eb = new Element();

                        if(split[0].equals("W")){
                            eb.w.add(read_w(row));
                            wtable.put(row.getCell(1).toString(), read_w(row));
                        }
                        else if(split[0].equals("WR")){
                            Row rowXZ[] = {row, sheet.getRow(++r)};
                            eb.wr.add(read_wr(rowXZ));
                            wrtable.put(row.getCell(1).toString(), read_wr(rowXZ));
                        }
                        else if(split[0].equals("FR")){
                            Row rowXY[] = {row, sheet.getRow(++r)};
                            eb.fr.add(read_fr(rowXY));
                            frtable.put(row.getCell(1).toString(), read_fr(rowXY));
                        }
                        else{
                            System.out.println("\ndatabase error!!!!\n");
                            System.exit(0);
                        }
                        database.put(elemStr, eb);
                    }
                    // second or more time show up
                    else{
                        eb = database.get(elemStr);
                        String[] split = row.getCell(1).toString().split("-");
                        if(split[0].equals("W")){
                            eb.w.add(read_w(row));
                            wtable.put(row.getCell(1).toString(), read_w(row));
                        }
                        else if(split[0].equals("WR")){
                            Row rowXZ[] = {row, sheet.getRow(++r)};
                            eb.wr.add(read_wr(rowXZ));
                            wrtable.put(row.getCell(1).toString(), read_wr(rowXZ));
                        }
                        else if(split[0].equals("FR")){
                            Row rowXY[] = {row, sheet.getRow(++r)};
                            eb.fr.add(read_fr(rowXY));
                            frtable.put(row.getCell(1).toString(), read_fr(rowXY));
                        }
                        else{
                            System.out.println("\ndatabase error!!!!\n");
                            System.exit(0);
                        }
                        database.put(elemStr, eb);

                    }
                    r++;
                    
                }
                else {
                    empty ++;
                    r++;
                    if(empty >=3){
                        break;
                    }
                }
                
            } 
            else {
                
                break;
            }
        }

        return database;
    }
    static Hashtable<String, Pair<String, String>> buildElem2Texture(Sheet sheet){
        Hashtable<String, Pair<String, String>> hashtable = new Hashtable<String, Pair<String, String>>();
        Row row = null;
        Cell cellElem = null;
        Cell cellSpec = null;
        Cell cellTexture = null;
        
        int i=1;
        while(true){
            row = sheet.getRow(i);
            if (row != null) {
                cellElem = row.getCell(0);
                cellSpec = row.getCell(4);
                cellTexture = row.getCell(5);
                if(!cellElem.toString().equals("")){

                    hashtable.put(cellElem.toString(), new Pair<String, String>(cellSpec.toString(), cellTexture.toString()));
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
    static Material read_mb(Row row){
        Material mb = null;
        if(!row.getCell(1).toString().equals("")){
            
            try{
                mb = new Material();
                mb.spec = row.getCell(0).getRichStringCellValue().getString();
                mb.len = row.getCell(1).getNumericCellValue();
            }catch(Exception e){
                System.out.println("請把***Sum.xls的估算資料讀進去(去素材規格按enter)");
                System.exit(0);;
            }
            
            int i = 2;
            while((row.getCell(i)!=null) && !(row.getCell(i).toString().equals(""))){
                mb.elem.add(new Pair<String, Double>(row.getCell(i).toString(), row.getCell(i+1).getNumericCellValue()));
                i+=2;
            }
        }
        else{
            return null;
        }
        return mb;
    }
    static boolean compMate(Material x, Material y){
        if(!x.spec.equals(y.spec) || (x.len != y.len) || x.elem.size() != y.elem.size()){
            return false;
        }

        for(int i= 0 ; i<x.elem.size() ;i++){
            if(!x.elem.get(i).x.equals(y.elem.get(i).x) || !x.elem.get(i).y.equals(y.elem.get(i).y)){
                return false;
            }
        }
        return true;
    }
    static W read_w(Row row){
        W w = new W();
        ArrayList<Double> vec = new ArrayList<Double>();
        for(int i= 1 ;i<=5 ;i++){
            vec.add(row.getCell(1+i).getNumericCellValue());
        }
        w.code = row.getCell(1).toString();
        w.add(vec);
        return w;
    }
    static WR read_wr(Row rowXZ[]){
        ArrayList<Double> vec = new ArrayList<Double>();
        WR wr = new WR();
        int i = 2;
        if(rowXZ[0].getCell(2).toString().equals("X") && rowXZ[1].getCell(2).toString().equals("Z")){
            wr.code = rowXZ[0].getCell(1).toString();
            while(rowXZ[0].getCell(1+i) != null && rowXZ[1].getCell(1+i) != null && !rowXZ[0].getCell(1+i).toString().equals("")){
                wr.addx(rowXZ[0].getCell(1+i).getNumericCellValue());
                wr.addz(rowXZ[1].getCell(1+i).getNumericCellValue());
                i++;
            }
        }
        else{
            System.out.println("Wrong WR database");
            System.exit(0);
        }

        return wr;
    }
    static FR read_fr(Row rowXY[]){
        ArrayList<Double> vec = new ArrayList<Double>();
        int i = 2;
        FR fr = new FR();
        if(rowXY[0].getCell(2).toString().equals("X") && rowXY[1].getCell(2).toString().equals("Y")){
            fr.code = rowXY[0].getCell(1).toString();
            while(rowXY[0].getCell(1+i) != null && rowXY[1].getCell(1+i) != null&& !rowXY[0].getCell(1+i).toString().equals("")){
                fr.addx(rowXY[0].getCell(1+i).getNumericCellValue());
                fr.addy(rowXY[1].getCell(1+i).getNumericCellValue());
                i++;
            }
        }
        else{
            System.out.println("Wrong FR database");
            System.exit(0);
        }
        return fr;
    }

    static int fillCode(Workbook wb, String code, int hor, int ver, int page){
        int height = 6;

        Sheet sheet = wb.getSheetAt(page);
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
            Double[] strCode = {wtable.get(code).xp, wtable.get(code).xn, wtable.get(code).zp, wtable.get(code).zn, wtable.get(code).os};
            for(int i=0; i<5 ;i++){
                putText(new Text(str[i], sheet, cellStyle_border, new Pair<Integer, Integer>(ver, hor+1+i)));
                putText(new Text(format(strCode[i]+""), sheet, cellStyle_border, new Pair<Integer, Integer>(ver+1, hor+1+i)));
            }
            height =6;
        }else if(split[0].equals("WR")){
            cell.setCellStyle(cellStyle_red_border);
            String[] str = {"X", "Z"};
            for(int i=0; i<2 ;i++){
                putText(new Text(str[i], sheet, cellStyle_border, new Pair<Integer, Integer>(ver+i, hor+1)));
            }

            for(int i=0;i<wrtable.get(code).x.size();i++){
                putText(new Text(format(wrtable.get(code).x.get(i)+""), sheet, cellStyle_border, new Pair<Integer, Integer>(ver, hor+2+i)));
                putText(new Text(format(wrtable.get(code).z.get(i)+""), sheet, cellStyle_border, new Pair<Integer, Integer>(ver+1, hor+2+i)));
            }

            height = wrtable.get(code).x.size()+2;
        }else if(split[0].equals("FR")){
            cell.setCellStyle(cellStyle_blue_border);
            String[] str = {"X", "Y"};
            for(int i=0; i<2 ;i++){
                putText(new Text(str[i], sheet, cellStyle_border, new Pair<Integer, Integer>(ver+i, hor+1)));
            }
            for(int i=0;i<frtable.get(code).x.size();i++){
                putText(new Text(format(frtable.get(code).x.get(i)+""), sheet, cellStyle_border, new Pair<Integer, Integer>(ver, hor+2+i)));
                putText(new Text(format(frtable.get(code).y.get(i)+""), sheet, cellStyle_border, new Pair<Integer, Integer>(ver+1, hor+2+i)));
            }

            height = frtable.get(code).x.size()+2;
        }

        return height;

    }
    static void putText(Text text){
        Row row = text.sheet.getRow(text.point.y);
        Cell cell = row.getCell(text.point.x);
        cell.setCellValue(text.text);
        cell.setCellStyle(text.cellStyle);
    }
    static void plotBlock(Workbook wb, String projectName, int start, int page){

        Sheet sheet = wb.getSheetAt(page);
        Row row = null;
        Cell cell = null;
        int sheetlen = 50;

        for(int i=start; i<start+sheetlen ; i++){
            row = sheet.createRow(i);    
            for(int j=0 ; j<12;j++){
                cell = row.createCell(j);
            }
            
        }

        // blocks
        CellRangeAddress callRangeAddress_header = new CellRangeAddress(start,start,0,11);
        CellRangeAddress callRangeAddress_project = new CellRangeAddress(start+1,start+1,0,4);
        CellRangeAddress callRangeAddress_count = new CellRangeAddress(start+11,start+11,7,8);
        CellRangeAddress callRangeAddress_lister = new CellRangeAddress(start+14,start+14,7,8);
        CellRangeAddress callRangeAddress_lister2 = new CellRangeAddress(start+15,start+16,7,8);
        CellRangeAddress callRangeAddress_auditor = new CellRangeAddress(start+14,start+14,9,10);
        CellRangeAddress callRangeAddress_auditor2 = new CellRangeAddress(start+15,start+16,9,10);

        sheet.addMergedRegion(callRangeAddress_header);
        sheet.addMergedRegion(callRangeAddress_project);
        sheet.addMergedRegion(callRangeAddress_count);
        sheet.addMergedRegion(callRangeAddress_lister);
        sheet.addMergedRegion(callRangeAddress_lister2);
        sheet.addMergedRegion(callRangeAddress_auditor);
        sheet.addMergedRegion(callRangeAddress_auditor2);

        // fixed words
        row = sheet.getRow(start+14);
        cell = row.getCell(7);
        cell.setCellValue("製表");
        cell.setCellStyle(cellStyle_border);
        RegionUtil.setBorderBottom(BorderStyle.THIN, callRangeAddress_lister, sheet); // 下邊框
        RegionUtil.setBorderLeft(BorderStyle.THIN, callRangeAddress_lister, sheet); // 左邊框
        RegionUtil.setBorderRight(BorderStyle.THIN, callRangeAddress_lister, sheet); // 有邊框
        RegionUtil.setBorderTop(BorderStyle.THIN, callRangeAddress_lister, sheet); // 上邊框


        row = sheet.getRow(start+15);
        cell = row.getCell(7);
        cell.setCellStyle(cellStyle_border);
        RegionUtil.setBorderBottom(BorderStyle.THIN, callRangeAddress_lister2, sheet); // 下邊框
        RegionUtil.setBorderLeft(BorderStyle.THIN, callRangeAddress_lister2, sheet); // 左邊框
        RegionUtil.setBorderRight(BorderStyle.THIN, callRangeAddress_lister2, sheet); // 有邊框
        RegionUtil.setBorderTop(BorderStyle.THIN, callRangeAddress_lister2, sheet); // 上邊框


        row = sheet.getRow(start+14);
        cell = row.getCell(9);
        cell.setCellValue("核對");
        cell.setCellStyle(cellStyle_border);
        RegionUtil.setBorderBottom(BorderStyle.THIN, callRangeAddress_auditor, sheet); // 下邊框
        RegionUtil.setBorderLeft(BorderStyle.THIN, callRangeAddress_auditor, sheet); // 左邊框
        RegionUtil.setBorderRight(BorderStyle.THIN, callRangeAddress_auditor, sheet); // 有邊框
        RegionUtil.setBorderTop(BorderStyle.THIN, callRangeAddress_auditor, sheet); // 上邊框


        row = sheet.getRow(start+15);
        cell = row.getCell(9);
        cell.setCellStyle(cellStyle_border);
        RegionUtil.setBorderBottom(BorderStyle.THIN, callRangeAddress_auditor2, sheet); // 下邊框
        RegionUtil.setBorderLeft(BorderStyle.THIN, callRangeAddress_auditor2, sheet); // 左邊框
        RegionUtil.setBorderRight(BorderStyle.THIN, callRangeAddress_auditor2, sheet); // 有邊框
        RegionUtil.setBorderTop(BorderStyle.THIN, callRangeAddress_auditor2, sheet); // 上邊框

        Date dNow = new Date( );
        SimpleDateFormat date = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        String[] str = {"勃 泰 有 限 公 司", "編號:", "FILE:", "PRO:", "NO:",
                        "素材長度", "寬度", "翼板高度", "翼板厚度", "材質", 
                        "CNC 鑽孔程式表", "規格：", "CHECK:", "使用長度:", "製表日期:",
                        date.format(dNow), "出表日期:", projectName, "素材總數:"};
        int [][]points = {{start,0}, {start+2,0}, {start+5,0}, {start+6,0}, {start+7,0}, 
                            {start+3,2}, {start+4,2}, {start+5,2}, {start+6,2}, {start+7,2}, 
                            {start+1,7}, {start+2,7}, {start+12,7}, {start+13,7}, {start+17,7}, 
                            {start+17,9}, {start+18,7}, {start+1,0}, {start+11,7}};
        CellStyle []setCellStyles ={cellStyle, cellStyle, cellStyle, cellStyle, cellStyle,
                        cellStyle, cellStyle, cellStyle, cellStyle, cellStyle,
                        cellStyle, cellStyle, cellStyle, cellStyle ,cellStyle,
                        cellStyle, cellStyle, cellStyle_red, cellStyle_red};

        for(int i=0 ;i<str.length; i++){
            putText(new Text(str[i], sheet, setCellStyles[i], new Pair<Integer, Integer>(points[i][1], points[i][0])));
        }


        String steps[] = {"步 驟", "位 置", "左翼板", "腹 板", "右翼板", " "};
        for (int i = start+8; i < start+34; i++) {
            row = sheet.getRow(i);
            for(int j =0 ;j<6 ;j++){
                cell = row.getCell(j);
                cell.setCellStyle(cellStyle_border);
                if(i == start+8){
                    cell.setCellValue(steps[j]);
                }
                else{
                    if(j==0){
                        cell.setCellValue(i-start-8);
                    }    
                }
            }
        }

        String number[] = {"編 號", "構 件", "長 度", "零 件", "支 數"};
        for (int i = start+3; i < start+10; i++) {
            row = sheet.getRow(i);
            for(int j =7 ;j<12 ;j++){
                cell = row.getCell(j);
                cell.setCellStyle(cellStyle_border);
                if(i == start+3){
                    cell.setCellValue(number[j-7]);
                }
                else{
                    if(j==7){
                        cell.setCellValue(i-start-3);
                    }    
                }
            }
        }

    }
    static void fillBlock(Workbook wb, Material material, int count, int start, int label, int page){
        String []split = material.spec.split("[*]");
        String []split2 = split[0].split("(?<=\\D)(?=\\d)");
        // 編號, 規格, 素材長度, 寬度, 翼板高度, 
        //  模板厚度, 材質, 素材總數
        String[] str = {String.valueOf(label), material.spec, format(material.len+""), split2[1], split[1], 
                    split[2], elem2texture.get(material.elem.get(0).x).y, String.valueOf(count)};
        int [][]points ={{start+2, 1}, {start+2, 7}, {start+3, 3}, {start+4, 3}, {start+5, 3}, 
        {start+6, 3}, {start+7, 3}, {start+11, 9}};
        CellStyle []setCellStyles ={cellStyle, cellStyle, cellStyle_red, cellStyle, cellStyle,
            cellStyle, cellStyle_red, cellStyle_red};
        for(int i=0; i<str.length ;i++){
            putText(new Text(str[i], wb.getSheetAt(page), setCellStyles[i], new Pair<Integer, Integer>(points[i][1], points[i][0])));
        }
        
        //右上表格
        String spec;
        double len =0;
        int translation = 0;
        Vector<String> types = new Vector<String>();
        count = 0;
        // 10mm = redundant, 3mm = blade width;
        double checkcnt = 10 + 3;
        for(int i=0 ;i<material.elem.size() ;i++){
            len = material.elem.get(i).y;
            spec = material.elem.get(i).x;
            count++;
            if((i+1 == material.elem.size()) || !spec.equals(material.elem.get(i+1).x)){
                putText(new Text(format(len + ""), wb.getSheetAt(page), cellStyle_border, new Pair<Integer, Integer>(9, 4+translation+start)));
                putText(new Text(spec, wb.getSheetAt(page), cellStyle_border, new Pair<Integer, Integer>(10, 4+translation+start)));
                putText(new Text(String.valueOf(count), wb.getSheetAt(page), cellStyle_red_border, new Pair<Integer, Integer>(11, 4+translation+start)));
                types.add(spec);
                translation++;
                checkcnt += count*len + count*3 ;
                count = 0;
            }
        }

        putText(new Text(format(checkcnt+""), wb.getSheetAt(page), cellStyle, new Pair<Integer, Integer>(9, 12+start)));

        //右下
        Set<String> wSet = new HashSet<String>();
        Set<String> wrSet = new HashSet<String>();
        Set<String> frSet = new HashSet<String>();

        for(int i=0 ; i<types.size();i++){
            if(database.get(types.get(i)) != null && !database.get(types.get(i)).w.isEmpty()){
                ArrayList<W> wArr = database.get(types.get(i)).w;
                for(int j=0;j<wArr.size();j++){
                    String wStr = wArr.get(j).code;
                    wSet.add(wStr);
                }
            }

            if(database.get(types.get(i)) != null && database.get(types.get(i)).wr.size()>0){
                ArrayList<WR> wrArr = database.get(types.get(i)).wr;
                for(int j=0;j<wrArr.size();j++){
                    String wrStr = wrArr.get(j).code;
                    wrSet.add(wrStr);
                }
            }
            if(database.get(types.get(i)) != null && database.get(types.get(i)).fr.size()>0){
                ArrayList<FR> frArr = database.get(types.get(i)).fr;
                for(int j=0;j<frArr.size();j++){
                    String frStr = frArr.get(j).code;
                    frSet.add(frStr);
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
                    height = fillCode(wb, code, row_l, col_l, page);
                    row_l += height;
                }
                else{
                    height = fillCode(wb, code, row_r, col_r, page);
                    row_r += height;
                }
            } 
        }
    }
    static void setCellStyle(Workbook wb){

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
    static String format(String val){
        String stringVal = String.valueOf(val);
        String[] number = stringVal.split( "[.]" );
        if(number.length>1 && number[1].equalsIgnoreCase("0")){
            return number[0];                               
        } else {
            return val;
        }
    }
}
class Text{
    String text;
    Sheet sheet;
    CellStyle cellStyle;
    Pair<Integer, Integer> point;
    public Text(String text, Sheet sheet, CellStyle cellStyle, Pair<Integer, Integer> point){
        this.text = text;
        this.sheet = sheet;
        this.cellStyle = cellStyle;
        this.point = point;
    }
    public void set(String text, Sheet sheet, CellStyle cellStyle, Pair<Integer, Integer> point){
        this.text = text;
        this.sheet = sheet;
        this.cellStyle = cellStyle;
        this.point = point;
    }

}
class Material{
    String spec;
    double len;
    Vector<Pair<String, Double>> elem;
    public Material(){
        elem = new Vector<Pair<String, Double>>();
    }
}
class Element{
    ArrayList<W> w;
    ArrayList<WR> wr;
    ArrayList<FR> fr;
    public Element(){

        w = new ArrayList<W>();
        wr = new ArrayList<WR>();
        fr = new ArrayList<FR>();
    }

}
class W{
    String code;
    double xp;
    double xn; 
    double zp;
    double zn;
    double os;
    public W(){
        code = "";
        this.xp = 0;
        this.xn = 0; 
        this.zp = 0;
        this.zn = 0;
        this.os = 0;
    }
    public void add(ArrayList<Double> vec){
        this.xp = vec.get(0);
        this.xn = vec.get(1); 
        this.zp = vec.get(2);
        this.zn = vec.get(3);
        this.os = vec.get(4);
    }
}
class WR{
    String code;
    ArrayList<Double> x = null;
    ArrayList<Double> z = null;
    public WR(){
        code = "";
        this.x = new ArrayList<Double>();
        this.z = new ArrayList<Double>();
    }
    public void addx(double x){
        this.x.add(x);
    }
    public void addz(double z){
        this.z.add(z);
    }
}

class FR{
    String code;
    ArrayList<Double> x;
    ArrayList<Double> y;
    public FR(){
        code = "";
        this.x = new ArrayList<Double>();
        this.y = new ArrayList<Double>();
    }
    public void addx(double x){
        this.x.add(x);
    }
    public void addy(double y){
        this.y.add(y);
    }
    
}

class Pair<X, Y> { 
    public X x; 
    public Y y; 
    public Pair(X x, Y y) { 
      this.x = x; 
      this.y = y; 
    } 

  } 