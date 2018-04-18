package com.excel;

import com.alibaba.fastjson.JSON;

public class Main {
  public static void main(String[] args) throws Exception {
    String path = "/home/sundays/Desktop/excel/合并单元格2.xlsx";
    XlsxMergeHandler mergeHandler = new XlsxMergeHandler(path);
    System.out.println(JSON.toJSONString(mergeHandler.getMergeCells(), true));
    ReadXlsx xlsx = new ReadXlsx("/home/sundays/Desktop/excel/合并单元格2.xlsx", 0, 100);
    xlsx.process();
    System.out.println(JSON.toJSONString(xlsx, true));
  }
}
