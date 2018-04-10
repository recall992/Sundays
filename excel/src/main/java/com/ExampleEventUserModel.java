package com;

import com.alibaba.fastjson.JSON;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class ExampleEventUserModel {


  public void processOneSheet(String filename) throws Exception {
    OPCPackage pkg = OPCPackage.open(filename);
    XSSFReader r = new XSSFReader(pkg);
    SharedStringsTable sst = r.getSharedStringsTable();

    XMLReader parser = fetchSheetParser(sst);

    // To look up the Sheet Name / Sheet Order / rID,
    //  you need to process the core Workbook stream.
    // Normally it's of the form rId# or rSheet#
    InputStream sheet2 = r.getSheet("rId2");
    InputSource sheetSource = new InputSource(sheet2);
    parser.parse(sheetSource);
    sheet2.close();
  }

  public void processAllSheets(String filename) throws Exception {
    OPCPackage pkg = OPCPackage.open(filename);
    XSSFReader r = new XSSFReader(pkg);
    SharedStringsTable sst = r.getSharedStringsTable();

    XMLReader parser = fetchSheetParser(sst);

    //Iterator<InputStream> sheets = r.getSheetsData();
    XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) r.getSheetsData();
    while (iter.hasNext()) {
      InputStream sheet = iter.next();
      System.out.println(String.format("Processing new sheet:%s",iter.getSheetName()));
      InputSource sheetSource = new InputSource(sheet);
      parser.parse(sheetSource);
      sheet.close();
      System.out.println("");
    }
  }

  public XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException {
    XMLReader parser =
        XMLReaderFactory.createXMLReader(
            "com.sun.org.apache.xerces.internal.parsers.SAXParser"
        );
    ContentHandler handler = new SheetHandler(sst);
    parser.setContentHandler(handler);
    return parser;
  }

  /**
   * See org.xml.sax.helpers.DefaultHandler javadocs
   */
  private static class SheetHandler extends DefaultHandler {
    private SharedStringsTable sst;
    private String lastContents;
    private boolean nextIsString;

    private SheetHandler(SharedStringsTable sst) {
      this.sst = sst;
    }

    public void startElement(String uri, String localName, String name,
                             Attributes attributes) throws SAXException {
      // c => cell
      if (name.equals("c")) {
        // Print the cell reference
        System.out.println(JSON.toJSONString(attributes));
        System.out.print(attributes.getValue("r") + " - ");
        // Figure out if the value is an index in the SST
        String cellType = attributes.getValue("t");
        if (cellType != null && cellType.equals("s")) {
          nextIsString = true;
        } else {
          nextIsString = false;
        }
      }
      // Clear contents cache
      lastContents = "";
    }

    public void endElement(String uri, String localName, String name)
        throws SAXException {
      // Process the last contents as required.
      // Do now, as characters() may be called more than once
      if (nextIsString) {
        int idx = Integer.parseInt(lastContents);
        lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
        nextIsString = false;
      }

      // v => contents of a cell
      // Output after we've seen the string contents
      if (name.equals("v")) {
        System.out.print(lastContents);
      }
    }

    public void characters(char[] ch, int start, int length)
        throws SAXException {
      lastContents += new String(ch, start, length);
    }
  }

  public static void main(String[] args) throws Exception {
    ExampleEventUserModel example = new ExampleEventUserModel();
    String path = "/home/sundays/Desktop/excel/合并单元格2.xlsx";
    //example.processOneSheet(path);
    example.processAllSheets(path);
  }
}