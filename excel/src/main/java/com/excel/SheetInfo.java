package com.excel;

import com.alibaba.fastjson.JSONArray;
import java.util.ArrayList;
import java.util.List;

public class SheetInfo {
  protected int index;
  protected String sheetName;
  private int maxColumns;

  public int getMaxColumns() {
    return maxColumns;
  }

  public void setMaxColumns(int maxColumns) {
    this.maxColumns = maxColumns;
  }

  protected List<JSONArray> content = new ArrayList<>();

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public String getSheetName() {
    return sheetName;
  }

  public void setSheetName(String sheetName) {
    this.sheetName = sheetName;
  }

  public List<JSONArray> getContent() {
    return content;
  }

  public void setContent(List<JSONArray> content) {
    this.content = content;
  }

  /**
   * 构造函数.
   *
   * @param index     index
   * @param sheetName sheet名称
   * @param content   内容.
   */
  public SheetInfo(int index, String sheetName, List<JSONArray> content) {
    super();
    this.index = index;
    this.sheetName = sheetName;
    this.content = content;
  }

  public SheetInfo() {
    super();
  }

  /**
   * 构造函数.
   *
   * @param index     index
   * @param sheetName sheet名称 .
   */
  public SheetInfo(int index, String sheetName) {
    super();
    this.index = index;
    this.sheetName = sheetName;
  }


}
