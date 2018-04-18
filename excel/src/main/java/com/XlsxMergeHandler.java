package com;

import com.alibaba.fastjson.JSON;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import java.util.Map;
import org.apache.poi.ss.util.CellReference;
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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class XlsxMergeHandler {

  private Map<String, Map<String, Short>> mergeCells = new HashMap<>();
  private Map<String, Short> currentSheetMerge = null;

  private XlsxMergeHandler() {

  }

  public XlsxMergeHandler(String path) throws Exception {
    OPCPackage pkg = OPCPackage.open(path);
    processAllSheets(pkg);
  }

  public XlsxMergeHandler(OPCPackage pkg) throws Exception {
    processAllSheets(pkg);
  }

  public void processAllSheets(OPCPackage pkg) throws Exception {
    XSSFReader r = new XSSFReader(pkg);
    SharedStringsTable sst = r.getSharedStringsTable();
    XMLReader parser = fetchSheetParser(sst);

    XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) r.getSheetsData();
    while (iter.hasNext()) {
      InputStream sheet = iter.next();
      InputSource sheetSource = new InputSource(sheet);
      parser.parse(sheetSource);
      sheet.close();
      currentSheetMerge = new HashMap<>();
      mergeCells.put(iter.getSheetName(), currentSheetMerge);
    }
    pkg.close();
  }

  /**
   * .
   *
   * @param sst .
   * @return .
   * @throws SAXException .
   */
  public XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException {
    XMLReader parser =
        XMLReaderFactory.createXMLReader(
            "com.sun.org.apache.xerces.internal.parsers.SAXParser"
        );
    ContentHandler handler = new SheetHandler(sst);
    parser.setContentHandler(handler);
    return parser;
  }

  private class SheetHandler extends DefaultHandler {
    private SharedStringsTable sst;

    private SheetHandler(SharedStringsTable sst) {
      this.sst = sst;
    }

    public void startElement(String uri, String localName, String name,
                             Attributes attributes) throws SAXException {
      if (name.equals("mergeCell")) {
        setMergeCells(attributes.getValue("ref"));
      }
    }
  }

  private void setMergeCells(String merge) {
    final String[] split = merge.split(":");
    CellReference startCell = new CellReference(split[0]);
    CellReference endCell = new CellReference(split[1]);
    int mergeCell = endCell.getCol() - startCell.getCol();
    for (int i = startCell.getRow(); i <= endCell.getRow(); i++) {
      currentSheetMerge.put(new CellReference(startCell.getCol(), startCell.getRow())
              .formatAsString(),
          (short) mergeCell);
    }
  }

  public Map<String, Map<String, Short>> getMergeCells() {
    return this.mergeCells;
  }

}
