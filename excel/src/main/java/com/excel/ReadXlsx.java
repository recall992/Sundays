package com.excel;

import com.alibaba.fastjson.JSONArray;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * .
 *
 * @author Sundays
 * @date 17-11-9 15:02
 */
public class ReadXlsx {

  private List<SheetInfo> sheets = new ArrayList<>();
  protected String filename;
  private int readRows;
  protected int skipRows;
  protected InputStream currentStream;
  private List<JSONArray> currentSheetContent;
  protected int currentSheetMaxColumns = 0;

  private boolean ignoreEmptyRow = false;

  private final int minColumns = 0;


  public List<SheetInfo> getSheets() {
    return sheets;
  }

  private class SheetHandler implements SheetContentsHandler {
    private boolean firstCellOfRow;
    private int currentRow = -1;
    private int currentCol = -1;
    private JSONArray rowData;

    private void outputMissingRows(int number) {
      if (!ignoreEmptyRow) {
        for (int i = 0; i < number; i++) {
          for (int j = 0; j < currentSheetMaxColumns; j++) {
            rowData.add("");
          }
          currentSheetContent.add(rowData);
          rowData = new JSONArray();
        }
      }
    }

    @Override
    public void startRow(int rowNum) {
      rowData = new JSONArray();
      // If there were gaps, output the missing rows
      outputMissingRows(rowNum - currentRow - 1);
      // Prepare for this row
      firstCellOfRow = true;
      currentRow = rowNum;
      currentCol = -1;
    }

    @Override
    public void endRow(int rowNum) {
      if (rowNum > skipRows - 1 && rowNum < skipRows + readRows) {
        currentSheetContent.add(rowData);
      } else if (rowNum >= skipRows + readRows) {
        try {
          currentStream.close();
        } catch (IOException e) {
          System.out.println("end read this sheet");
        }
      }

    }

    @Override
    public void cell(String cellReference, String formattedValue,
                     XSSFComment comment) {
      if (firstCellOfRow) {
        firstCellOfRow = false;
      }

      // gracefully handle missing CellRef here in a similar way as XSSFCell does
      if (cellReference == null) {
        cellReference = new CellAddress(currentRow, currentCol).formatAsString();
      }

      // Did we miss any cells?
      int thisCol = (new CellReference(cellReference)).getCol();
      int missedCols = thisCol - currentCol - 1;
      for (int i = 0; i < missedCols; i++) {
        rowData.add("");
      }
      currentCol = thisCol;
      rowData.add(formattedValue);
    }

    @Override
    public void headerFooter(String s, boolean b, String s1) {

    }
  }


  /**
   * .
   *
   * @throws OpenXML4JException .
   * @throws IOException        .
   * @throws SAXException       .
   */
  public void process() throws OpenXML4JException, IOException, SAXException {
    OPCPackage p = OPCPackage.open(filename, PackageAccess.READ);
    ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(p);
    XSSFReader xssfReader = new XSSFReader(p);
    StylesTable styles = xssfReader.getStylesTable();
    XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
    int index = 0;
    while (iter.hasNext()) {
      currentStream = iter.next();
      String sheetName = iter.getSheetName();
      final SheetInfo sheet = new SheetInfo();
      sheet.setSheetName(sheetName);
      sheet.setIndex(index);
      currentSheetContent = new ArrayList<>();
      sheet.setContent(currentSheetContent);
      sheets.add(sheet);
      processSheet(styles, strings, new SheetHandler(), currentStream);
      try {
        currentStream.close();
      } catch (Exception e) {
        System.out.println("end read this sheet:" + sheetName);
      }
      ++index;
    }
  }


  /**
   * .
   *
   * @param styles           .
   * @param strings          .
   * @param sheetHandler     .
   * @param sheetInputStream .
   * @throws IOException  .
   * @throws SAXException .
   */
  public void processSheet(
      StylesTable styles,
      ReadOnlySharedStringsTable strings,
      SheetContentsHandler sheetHandler,
      InputStream sheetInputStream) throws IOException, SAXException {
    DataFormatter formatter = new DataFormatter();
    //PoiDataFormatter formatter = new PoiDataFormatter();
    InputSource sheetSource = new InputSource(sheetInputStream);
    try {
      XMLReader sheetParser = SAXHelper.newXMLReader();
      ContentHandler handler = new XSSFSheetXMLHandler(
          styles, null, strings, sheetHandler, formatter, false);
      sheetParser.setContentHandler(handler);
      try {
        sheetParser.parse(sheetSource);
      } catch (Exception e) {
        System.out.println("end this sheet read");
      }

    } catch (ParserConfigurationException e) {
      throw new RuntimeException("SAX parser appears to be broken - " + e.getMessage());
    }
  }


  /**
   * .
   *
   * @param filename .
   * @param skipRows .
   * @param readRows .
   */
  public ReadXlsx(String filename, int skipRows, int readRows) {
    this.filename = filename;
    this.skipRows = skipRows;
    this.readRows = readRows;
  }

  protected void rowEnd(JSONArray row, int rowNum) {

  }
}
