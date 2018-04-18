package com.excel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.eventusermodel.EventWorkbookBuilder;
import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.eventusermodel.MissingRecordAwareHSSFListener;
import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BlankRecord;
import org.apache.poi.hssf.record.BoolErrRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.EOFRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.LabelRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NoteRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.RKRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.record.StringRecord;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class ReadXls implements HSSFListener {

  protected String filename;
  protected int minColumns = -1;
  protected POIFSFileSystem fs;
  protected int skipRows = 0;
  private int readRows = Integer.MAX_VALUE;
  private List<SheetInfo> sheets = new ArrayList<SheetInfo>();
  private SheetInfo currentSheet;
  protected List<JSONArray> rows;
  protected JSONArray row;
  protected String currentSheetName;
  protected long rowNum = 0;
  protected int currentSheetMaxColumns;
  protected int lastRowNumber;
  protected int lastColumnNumber;

  protected boolean outputFormulaValues = true;
  protected boolean ignoreEmptyRow = false;

  protected EventWorkbookBuilder.SheetRecordCollectingListener workbookBuildingListener;
  protected HSSFWorkbook stubWorkbook;

  // Records we pick up as we process
  protected SSTRecord sstRecord;
  protected FormatTrackingHSSFListener formatListener;

  protected int sheetIndex = -1;
  protected BoundSheetRecord[] orderedBsrs;
  protected List<BoundSheetRecord> boundSheetRecords = new ArrayList<BoundSheetRecord>();

  // For handling formulas with string results
  protected int nextRow;
  protected int nextColumn;
  protected boolean outputNextStringRecord;

  public List<SheetInfo> getSheets() {
    return sheets;
  }

  /**
   * Initiates the processing of the XLS file to CSV.
   */
  public void process() throws IOException {
    this.fs = new POIFSFileSystem(new FileInputStream(filename));
    MissingRecordAwareHSSFListener listener = new MissingRecordAwareHSSFListener(this);
    formatListener = new FormatTrackingHSSFListener(listener);
    HSSFEventFactory factory = new HSSFEventFactory();
    HSSFRequest request = new HSSFRequest();
    if (outputFormulaValues) {
      request.addListenerForAllRecords(formatListener);
    } else {
      workbookBuildingListener = new EventWorkbookBuilder
          .SheetRecordCollectingListener(formatListener);
      request.addListenerForAllRecords(workbookBuildingListener);
    }
    factory.processWorkbookEvents(request, fs);
  }


  @Override
  public void processRecord(Record record) {
    int thisRow = -1;
    int thisColumn = -1;
    String thisStr = null;
    switch (record.getSid()) {
      case BoundSheetRecord.sid:
        boundSheetRecords.add((BoundSheetRecord) record);
        break;
      case BOFRecord.sid:
        BOFRecord br = (BOFRecord) record;
        if (br.getType() == BOFRecord.TYPE_WORKSHEET) {
          // Create sub workbook if required
          if (workbookBuildingListener != null && stubWorkbook == null) {
            stubWorkbook = workbookBuildingListener.getStubHSSFWorkbook();
          }
          sheetIndex++;
          if (orderedBsrs == null) {
            orderedBsrs = BoundSheetRecord.orderByBofPosition(boundSheetRecords);
          }
          currentSheetName = orderedBsrs[sheetIndex].getSheetname();
          updateCurrentSheetInfo();
          row = new JSONArray();
          rowNum = 0;
        }
        break;
      case SSTRecord.sid:
        sstRecord = (SSTRecord) record;
        break;
      case BlankRecord.sid:
        BlankRecord brec = (BlankRecord) record;

        thisRow = brec.getRow();
        thisColumn = brec.getColumn();
        thisStr = "";
        break;
      case BoolErrRecord.sid:
        BoolErrRecord berec = (BoolErrRecord) record;
        thisRow = berec.getRow();
        thisColumn = berec.getColumn();
        thisStr = "";
        break;
      case FormulaRecord.sid:
        FormulaRecord frec = (FormulaRecord) record;
        thisRow = frec.getRow();
        thisColumn = frec.getColumn();
        if (outputFormulaValues) {
          // catch string formula record
          if (Double.isNaN(frec.getValue()) || frec.hasCachedResultString()) {
            // Formula result is a string
            // This is stored in the next record
            outputNextStringRecord = true;
            nextRow = frec.getRow();
            nextColumn = frec.getColumn();
          } else {
            thisStr = formatListener.formatNumberDateCell(frec);
          }
        } else {
          thisStr = HSSFFormulaParser.toFormulaString(stubWorkbook, frec.getParsedExpression());
        }
        break;
      case StringRecord.sid:
        if (outputNextStringRecord) {
          // String for formula
          StringRecord srec = (StringRecord) record;
          thisStr = srec.getString();
          thisRow = nextRow;
          thisColumn = nextColumn;
          outputNextStringRecord = false;
        }
        break;
      case LabelRecord.sid:
        LabelRecord lrec = (LabelRecord) record;
        thisRow = lrec.getRow();
        thisColumn = lrec.getColumn();
        thisStr = lrec.getValue();
        break;
      case LabelSSTRecord.sid:
        LabelSSTRecord lsrec = (LabelSSTRecord) record;
        thisRow = lsrec.getRow();
        thisColumn = lsrec.getColumn();
        if (sstRecord == null) {
          thisStr = "(No SST Record, can't identify string)";
        } else {
          thisStr = sstRecord.getString(lsrec.getSSTIndex()).toString();
        }
        break;
      case NoteRecord.sid:
        NoteRecord nrec = (NoteRecord) record;

        thisRow = nrec.getRow();
        thisColumn = nrec.getColumn();
        // TODO: Find object to match nrec.getShapeId()
        thisStr = "(TODO)";
        break;
      case NumberRecord.sid:
        NumberRecord numrec = (NumberRecord) record;
        thisRow = numrec.getRow();
        thisColumn = numrec.getColumn();
        final int formatIndex = formatListener.getFormatIndex(numrec);
        final String formatString = formatListener.getFormatString(numrec);
        // format date
        if (176 <= formatIndex && formatIndex <= 207) {
          //if (HSSFDateUtil.isADateFormat(formatIndex, formatString)) {
          thisStr = HSSFDateUtil.getJavaCalendar(numrec.getValue()).toString();
        } else {
          // Format
          thisStr = formatListener.formatNumberDateCell(numrec);
        }
        break;
      case RKRecord.sid:
        RKRecord rkrec = (RKRecord) record;

        thisRow = rkrec.getRow();
        thisColumn = rkrec.getColumn();
        thisStr = "(TODO)";
        break;
      default:
        break;
    }

    // Handle new row
    if (thisRow != -1 && thisRow != lastRowNumber) {
      lastColumnNumber = -1;
    }
    // Handle missing column
    if (record instanceof MissingCellDummyRecord) {
      MissingCellDummyRecord mc = (MissingCellDummyRecord) record;
      thisRow = mc.getRow();
      thisColumn = mc.getColumn();
      //thisStr = "";
    }

    // If we got something to print out, do so
    if (thisStr != null) {
      //clean double type zero
      row.add(thisStr);
    }
    // Update column and row count
    if (thisRow > -1) {
      lastRowNumber = thisRow;
    }

    if (thisColumn > -1) {
      lastColumnNumber = thisColumn;
    }

    // Handle end of row
    if (record instanceof LastCellOfRowDummyRecord) {
      // Print out any missing commas if needed
      if (minColumns > 0) {
        // Columns are 0 based
        if (lastColumnNumber == -1) {
          lastColumnNumber = 0;
        }
      }

      // We're onto a new row
      lastColumnNumber = -1;

      // End the row
      rowNum++;
      rowEnd(row, rowNum);
      row = new JSONArray();
    }
    if (EOFRecord.sid == record.getSid()) {
      sheetEnd();
    }
  }

  /**
   * .
   *
   * @param filename .
   */
  public ReadXls(String filename) {
    this.filename = filename;
  }

  /**
   * .
   *
   * @param filename .
   * @param skipRows .
   * @param readRows .
   */
  public ReadXls(String filename, int skipRows, int readRows) {
    this.filename = filename;
    this.skipRows = skipRows;
    this.readRows = readRows;
  }

  protected void updateCurrentSheetInfo() {
    currentSheet = new SheetInfo();
    sheets.add(currentSheet);
    currentSheet.setIndex(sheetIndex);
    currentSheet.setSheetName(currentSheetName);
    rows = new ArrayList<JSONArray>();
    currentSheet.setContent(rows);
  }

  protected void rowEnd(JSONArray row, long rowNum) {
    if (skipRows < rowNum && rowNum < readRows + skipRows + 1) {
      if (ignoreEmptyRow) {
        if (row != null && !row.isEmpty()) {
          rows.add(row);
        }
      } else {
        rows.add(row);
      }
    }
  }

  protected void sheetEnd() {

  }

  public static void main(String[] args) throws IOException {
    String path = "/home/sundays/Desktop/excel/合并单元格2.xls";
    ReadXls xls = new ReadXls(path, 0, 100);
    xls.process();
    System.out.println(JSON.toJSONString(xls.getSheets(), true));
  }
}


