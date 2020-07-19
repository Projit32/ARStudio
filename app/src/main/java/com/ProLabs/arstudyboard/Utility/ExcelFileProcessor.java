package com.ProLabs.arstudyboard.Utility;

import android.content.Context;

import android.net.Uri;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;


import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class ExcelFileProcessor {
    private Uri uri;
    private android.os.Handler handler=new Handler(Looper.getMainLooper());
    private Context context;
    private ArrayList<ArrayList<String>> ExelData= new ArrayList<>();
    private ArrayList<String> columnData= new ArrayList<>();

    public ExcelFileProcessor(Uri fileUri, Context context) {
        uri = fileUri;
        this.context = context;
    }


    public void readExcelFileFromAssets() {
        try {
            InputStream myInput;
            String path=RealPathUtil.getRealPath(context,uri);
            File file = new File(path);
            myInput = new FileInputStream(file);
            Workbook workbook = null;
            if(path.toLowerCase().endsWith("xlsx")){
                workbook = new XSSFWorkbook(myInput);
            }else if(path.toLowerCase().endsWith("xls")){
                workbook = new HSSFWorkbook(myInput);
            }
            Sheet sheet = workbook.getSheetAt(0);

            int rowsCount = sheet.getPhysicalNumberOfRows();
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

            for (int r = 0; r < rowsCount; r++) {
                Row row = sheet.getRow(r);
                int cellsCount = row.getPhysicalNumberOfCells();
                //inner loop, loops through columns
                for (int c = 0; c < cellsCount; c++) {

                    columnData.add(getCellAsString(row, c, formulaEvaluator));
                    }
                ExelData.add(new ArrayList<>(columnData));
                columnData.clear();
                }

            //Toast.makeText(context,"Data Fetched", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            handler.post(()->Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show());
        }
    }

    public ArrayList<ArrayList<String>> getExelData() {
        return ExelData;
    }

    private String getCellAsString(Row row, int c, FormulaEvaluator formulaEvaluator) {
        String value = "";
        try {
            Cell cell = row.getCell(c);
            CellValue cellValue = formulaEvaluator.evaluate(cell);
            switch (cellValue.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    value = ""+cellValue.getBooleanValue();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    double numericValue = cellValue.getNumberValue();
                    if(HSSFDateUtil.isCellDateFormatted(cell)) {
                        double date = cellValue.getNumberValue();
                        SimpleDateFormat formatter =
                                new SimpleDateFormat("MM/dd/yy");
                        value = formatter.format(HSSFDateUtil.getJavaDate(date));
                    } else {
                        value = ""+numericValue;
                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    value = ""+cellValue.getStringValue();
                    break;
                default:
            }
        } catch (NullPointerException e) {
            handler.post(()->Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show());
        }
        return value;
    }



}
