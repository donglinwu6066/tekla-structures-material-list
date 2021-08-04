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

public class auto {
    // two types: aym, sum
    public static void main(String argv[]) {
        // get all file number
        System.out.println("Working process \"auto -> sum -> cut\"");
        System.out.println("input .sym files");
        List<String> fileList = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get("input.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                fileList.add(line);
            }
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
        for (String data : fileList) {
            System.out.println(".\\in\\" + data);
        }
        System.out.println(" ");
        // folder
        File theDir = new File("out-auto");
        theDir.mkdirs();

        // processing each data
        int fileNumber = 0;

        // 2003 = xls; 2007 = xlsx;
        List<String> dataList = new ArrayList<>();
        // new file
        String path = ".\\out-auto\\" + fileList.get(0) + ".xls";
        Workbook wb = null;
        String extString = path.substring(path.lastIndexOf("."));
        InputStream is;
        try {
            is = new FileInputStream(".\\in\\" + fileList.get(0) + ".xls");
            System.out.println("Reading template \n.\\in\\" + fileList.get(0) + ".xls\n");
            if (".xls".equals(extString)) {
                wb = new HSSFWorkbook(is);
            } else if (".xlsx".equals(extString)) {
                wb = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            System.out.println("Fail to read files");
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

        List<Integer> repeatList = new ArrayList<>();
        while (fileNumber < fileList.size()) {

            try (BufferedReader br = Files
                    .newBufferedReader(Paths.get(".\\in\\" + fileList.get(fileNumber) + ".sym"))) {
                // read line by line
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.length() >= 5 && ("Stock".equals(line.substring(0, 5)))) {
                        String[] tokens = line.split(" ");
                        dataList.add(line);
                        repeatList.add(Integer.valueOf(tokens[6]));
                    }
                    if (line.length() >= 5 && "(".equals(line.substring(0, 1))) {
                        dataList.add(line);
                    }
                }
            } catch (IOException e) {
                System.err.format("IOException: %s%n", e);
            }
            fileNumber++;
        }
        // prediction--------------------------------------------------------------------------------------------------
        int count = 0;
        boolean flagR = true, flagC = true;
        int r = 1, c = 1, reIndex = 0;
        while (count < dataList.size()) {
            row = sheet.getRow(r);
            while (flagC) {
                cell = row.getCell(c);
                if (cell == null) {
                    cell = row.createCell(c);
                }
                if ("Stock".equals(dataList.get(count).substring(0, 5))) {
                    String[] tokens = dataList.get(count).split(" ");
                    // ------------
                    for (int i = 0; i < repeatList.get(reIndex); i++) {
                        row = sheet.getRow(r + i);
                        cell = row.getCell(c);
                        if (cell == null) {
                            cell = row.createCell(c);
                        }
                        cell.setCellValue(Integer.valueOf(tokens[4].substring(0, tokens[4].lastIndexOf("."))));
                    }
                    // ------------
                } else if ("(".equals(dataList.get(count).substring(0, 1))) {
                    String[] tokens = dataList.get(count).split("-");
                    for (int i = 0; i < repeatList.get(reIndex); i++) {
                        row = sheet.getRow(r + i);
                        cell = row.getCell(c);
                        if (cell == null) {
                            cell = row.createCell(c);
                        }
                        cell.setCellValue(tokens[0].substring(1));
                    }
                    if (((count + 1) == dataList.size()) || "Stock".equals(dataList.get(count + 1).substring(0, 5))) {
                        break;
                    }
                }
                if (c == 1) {
                    c++;
                } else {
                    c += 2;
                }
                count++;
            }
            r += repeatList.get(reIndex);
            c = 1;
            reIndex++;
            count++;
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
        System.out.println("Next process \"sum\"");

    }

}
