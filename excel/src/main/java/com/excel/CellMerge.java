package com.excel;

public class CellMerge {
  private String cellReference;
  private Short merge;

  public CellMerge(String cellReference, Short merge) {
    this.cellReference = cellReference;
    this.merge = merge;
  }
}
