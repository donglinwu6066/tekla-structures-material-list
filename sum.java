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
    // two types: aym, sum
    public static void main(String argv[]) {
        
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
        File theDir = new File("out-sum");
        theDir.mkdirs();

        // 2003 = xls; 2007 = xlsx;
        List<String> dataList = new ArrayList<>();
        // new file
        String path = ".\\out-sum\\" + file + "Sum.xls";
        Workbook wb = null;
        String extString = path.substring(path.lastIndexOf("."));
        InputStream is;
        try {
            is = new FileInputStream(".\\out-auto\\" + file + ".xls");
            System.out.println("Reading template \n.\\out-auto\\" + file + ".xls\n");
            if (".xls".equals(extString)) {
                wb = new HSSFWorkbook(is);
            } else if (".xlsx".equals(extString)) {
                wb = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            System.out.println("\nCannot find .\\out-auto\\" + file + ".xls\n");
            // print the work file
            e.printStackTrace();
            if (".xls".equals(extString)) {
                wb = new HSSFWorkbook();
            } else if (".xlsx".equals(extString)) {
                wb = new XSSFWorkbook();
            } else {
                System.out.println("Cannot find .xls or .xlsx");
                return;
            }
        }

        // Excel
        // Sheet sheet = wb.createSheet();
        Sheet sheet = wb.getSheetAt(1);
        Row row = null;
        Cell cell = null;
        // count
        // row--------------------------------------------------------------------------------------------------
        int r = 1;
        int exist;
        String existStr;
        while (true) {
            row = sheet.getRow(r);
            if (row != null) {
                cell = row.getCell(1);
                try {
                    existStr = cell.toString();
                } catch (NullPointerException NPE) {
                    existStr = null;
                }
                // System.out.println(cell);
                if (existStr != "") {
                    r++;
                } else {
                    break;
                }
            } else {
                break;
            }
        }

        // read sheet 1
        int[][] content = new int[r][3];
        int tail = 0, cursor = 0;
        int len, spec;
        String lenStr, specStr;
        boolean newrow = true;
        row = null;
        cell = null;

        for (int i = 1; i < r; i++) {

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

        // write to the second page
        Sheet sheet2 = wb.getSheetAt(2);
        row = null;
        cell = null;
        // create first row

        String[] title = { "素材", "長度", "數量" };
        row = sheet2.createRow(0);
        for (int i = 0; i < 3; i++) {
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
        }

        for (int i = 1; i <= tail; i++) {
            row = sheet2.createRow(i);
            for (int j = 0; j < 3; j++) {
                cell = row.createCell(j);
                cell.setCellValue(content[i - 1][j]);
            }
        }

        // write out Excel
        try {
            FileOutputStream fos = new FileOutputStream(new File(path));
            wb.write(fos);
            fos.flush();
            fos.close();
            System.out.println("Write to\n" + path + "\n");
            System.out.println("Finish");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
