package bnotai.tekla.material.data;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

public class Text {
	String text;
    Sheet sheet;
    CellStyle cellStyle;
    Row row;
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
    public void put() {
        Row row = sheet.getRow(point.getFirst());
        Cell cell = row.getCell(point.getSecond());
        cell.setCellValue(text);
        cell.setCellStyle(cellStyle);
    }
}
