package com;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLProperties;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.POIXMLProperties.CoreProperties;
import org.apache.poi.POIXMLProperties.CustomProperties;
import org.apache.poi.POIXMLProperties.ExtendedProperties;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.extractor.ExcelExtractor;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.eventusermodel.XSSFReader.SheetIterator;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.model.CommentsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFSimpleShape;
import org.apache.xmlbeans.XmlException;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class XSSFEventExcelExtractor extends POIXMLTextExtractor implements ExcelExtractor {
  private static final POILogger LOGGER = POILogFactory.getLogger(org.apache.poi.xssf.extractor.XSSFEventBasedExcelExtractor.class);
  private OPCPackage container;
  private POIXMLProperties properties;
  private Locale locale;
  private boolean includeTextBoxes;
  private boolean includeSheetNames;
  private boolean includeCellComments;
  private boolean includeHeadersFooters;
  private boolean formulasNotResults;
  private boolean concatenatePhoneticRuns;

  public XSSFEventExcelExtractor(String path) throws XmlException, OpenXML4JException, IOException {
    this(OPCPackage.open(path));
  }

  public XSSFEventExcelExtractor(OPCPackage container) throws XmlException, OpenXML4JException, IOException {
    super((POIXMLDocument)null);
    this.includeTextBoxes = true;
    this.includeSheetNames = true;
    this.includeCellComments = false;
    this.includeHeadersFooters = true;
    this.formulasNotResults = false;
    this.concatenatePhoneticRuns = true;
    this.container = container;
    this.properties = new POIXMLProperties(container);
  }

  public static void main(String[] args) throws Exception {

    POIXMLTextExtractor extractor = new org.apache.poi.xssf.extractor.XSSFEventBasedExcelExtractor("/home/sundays/Desktop/excel/合并单元格2.xlsx");
    System.out.println(extractor.getText());
    extractor.close();
  }

  public void setIncludeSheetNames(boolean includeSheetNames) {
    this.includeSheetNames = includeSheetNames;
  }

  public boolean getIncludeSheetNames() {
    return this.includeSheetNames;
  }

  public void setFormulasNotResults(boolean formulasNotResults) {
    this.formulasNotResults = formulasNotResults;
  }

  public boolean getFormulasNotResults() {
    return this.formulasNotResults;
  }

  public void setIncludeHeadersFooters(boolean includeHeadersFooters) {
    this.includeHeadersFooters = includeHeadersFooters;
  }

  public boolean getIncludeHeadersFooters() {
    return this.includeHeadersFooters;
  }

  public void setIncludeTextBoxes(boolean includeTextBoxes) {
    this.includeTextBoxes = includeTextBoxes;
  }

  public boolean getIncludeTextBoxes() {
    return this.includeTextBoxes;
  }

  public void setIncludeCellComments(boolean includeCellComments) {
    this.includeCellComments = includeCellComments;
  }

  public boolean getIncludeCellComments() {
    return this.includeCellComments;
  }

  public void setConcatenatePhoneticRuns(boolean concatenatePhoneticRuns) {
    this.concatenatePhoneticRuns = concatenatePhoneticRuns;
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  public Locale getLocale() {
    return this.locale;
  }

  public OPCPackage getPackage() {
    return this.container;
  }

  public CoreProperties getCoreProperties() {
    return this.properties.getCoreProperties();
  }

  public ExtendedProperties getExtendedProperties() {
    return this.properties.getExtendedProperties();
  }

  public CustomProperties getCustomProperties() {
    return this.properties.getCustomProperties();
  }

  public void processSheet(SheetContentsHandler sheetContentsExtractor, StylesTable styles, CommentsTable comments, ReadOnlySharedStringsTable strings, InputStream sheetInputStream) throws IOException, SAXException {
    DataFormatter formatter;
    if (this.locale == null) {
      formatter = new DataFormatter();
    } else {
      formatter = new DataFormatter(this.locale);
    }

    InputSource sheetSource = new InputSource(sheetInputStream);

    try {
      XMLReader sheetParser = SAXHelper.newXMLReader();
      ContentHandler handler = new XSSFSheetXMLHandler(styles, comments, strings, sheetContentsExtractor, formatter, this.formulasNotResults);
      sheetParser.setContentHandler(handler);
      sheetParser.parse(sheetSource);
    } catch (ParserConfigurationException var10) {
      throw new RuntimeException("SAX parser appears to be broken - " + var10.getMessage());
    }
  }

  public String getText() {
    try {
      ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(this.container, this.concatenatePhoneticRuns);
      XSSFReader xssfReader = new XSSFReader(this.container);
      StylesTable styles = xssfReader.getStylesTable();
      SheetIterator iter = (SheetIterator)xssfReader.getSheetsData();
      StringBuffer text = new StringBuffer();
      XSSFEventExcelExtractor.SheetTextExtractor sheetExtractor = new XSSFEventExcelExtractor.SheetTextExtractor();

      while(iter.hasNext()) {
        InputStream stream = iter.next();
        if (this.includeSheetNames) {
          text.append(iter.getSheetName());
          text.append('\n');
        }

        CommentsTable comments = this.includeCellComments ? iter.getSheetComments() : null;
        this.processSheet(sheetExtractor, styles, comments, strings, stream);
        if (this.includeHeadersFooters) {
          sheetExtractor.appendHeaderText(text);
        }

        sheetExtractor.appendCellText(text);
        if (this.includeTextBoxes) {
          this.processShapes(iter.getShapes(), text);
        }

        if (this.includeHeadersFooters) {
          sheetExtractor.appendFooterText(text);
        }

        sheetExtractor.reset();
        stream.close();
      }

      return text.toString();
    } catch (IOException var9) {
      LOGGER.log(5, new Object[]{var9});
      return null;
    } catch (SAXException var10) {
      LOGGER.log(5, new Object[]{var10});
      return null;
    } catch (OpenXML4JException var11) {
      LOGGER.log(5, new Object[]{var11});
      return null;
    }
  }

  void processShapes(List<XSSFShape> shapes, StringBuffer text) {
    if (shapes != null) {
      Iterator i$ = shapes.iterator();

      while(i$.hasNext()) {
        XSSFShape shape = (XSSFShape)i$.next();
        if (shape instanceof XSSFSimpleShape) {
          String sText = ((XSSFSimpleShape)shape).getText();
          if (sText != null && sText.length() > 0) {
            text.append(sText).append('\n');
          }
        }
      }

    }
  }

  public void close() throws IOException {
    if (this.container != null) {
      this.container.close();
      this.container = null;
    }

    super.close();
  }

  protected class SheetTextExtractor implements SheetContentsHandler {
    private final StringBuffer output = new StringBuffer();
    private boolean firstCellOfRow = true;
    private final Map<String, String> headerFooterMap;

    protected SheetTextExtractor() {
      this.headerFooterMap = XSSFEventExcelExtractor.this.includeHeadersFooters ? new HashMap() : null;
    }

    public void startRow(int rowNum) {
      this.firstCellOfRow = true;
    }

    public void endRow(int rowNum) {
      this.output.append('\n');
    }

    public void cell(String cellRef, String formattedValue, XSSFComment comment) {
      if (this.firstCellOfRow) {
        this.firstCellOfRow = false;
      } else {
        this.output.append('\t');
      }

      if (formattedValue != null) {
       XSSFEventExcelExtractor.this.checkMaxTextSize(this.output, formattedValue);
        this.output.append(formattedValue);
      }

      if (XSSFEventExcelExtractor.this.includeCellComments && comment != null) {
        String commentText = comment.getString().getString().replace('\n', ' ');
        this.output.append(formattedValue != null ? " Comment by " : "Comment by ");
        XSSFEventExcelExtractor.this.checkMaxTextSize(this.output, commentText);
        if (commentText.startsWith(comment.getAuthor() + ": ")) {
          this.output.append(commentText);
        } else {
          this.output.append(comment.getAuthor()).append(": ").append(commentText);
        }
      }

    }

    public void headerFooter(String text, boolean isHeader, String tagName) {
      if (this.headerFooterMap != null) {
        this.headerFooterMap.put(tagName, text);
      }

    }

    private void appendHeaderFooterText(StringBuffer buffer, String name) {
      String text = (String)this.headerFooterMap.get(name);
      if (text != null && text.length() > 0) {
        text = this.handleHeaderFooterDelimiter(text, "&L");
        text = this.handleHeaderFooterDelimiter(text, "&C");
        text = this.handleHeaderFooterDelimiter(text, "&R");
        buffer.append(text).append('\n');
      }

    }

    private String handleHeaderFooterDelimiter(String text, String delimiter) {
      int index = text.indexOf(delimiter);
      if (index == 0) {
        text = text.substring(2);
      } else if (index > 0) {
        text = text.substring(0, index) + "\t" + text.substring(index + 2);
      }

      return text;
    }

    void appendHeaderText(StringBuffer buffer) {
      this.appendHeaderFooterText(buffer, "firstHeader");
      this.appendHeaderFooterText(buffer, "oddHeader");
      this.appendHeaderFooterText(buffer, "evenHeader");
    }

    void appendFooterText(StringBuffer buffer) {
      this.appendHeaderFooterText(buffer, "firstFooter");
      this.appendHeaderFooterText(buffer, "oddFooter");
      this.appendHeaderFooterText(buffer, "evenFooter");
    }

    void appendCellText(StringBuffer buffer) {
      XSSFEventExcelExtractor.this.checkMaxTextSize(buffer, this.output.toString());
      buffer.append(this.output);
    }

    void reset() {
      this.output.setLength(0);
      this.firstCellOfRow = true;
      if (this.headerFooterMap != null) {
        this.headerFooterMap.clear();
      }

    }
  }
}
