package com;

import java.util.TreeMap;

public class MergeCell {
  private String start;
  private String end;
  private String value;

  public static void main(String[] args) {
    TreeMap<Object, String> treeMap = new TreeMap<>();
    treeMap.put("A2", "123");
    treeMap.put("AA2", "123");
    treeMap.put("B2", "123");
    treeMap.forEach((k, v) -> {
      System.out.println(k + "\t" + v);
    });
  }
}
