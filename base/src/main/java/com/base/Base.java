package com.base;

import java.lang.reflect.Field;

public class Base {
  /**
   * 获取字段.
   *
   * @param name field name.
   * @param obj  Object
   * @return 反射字段.
   */
  private Field getField(String name, Object obj) {
    Field field = null;
    Class<?> clazz = obj.getClass();

    for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
      try {
        field = clazz.getDeclaredField(name);
      } catch (Exception e) {
        // 这里甚么都不能抛出去。
        // 如果这里的异常打印或者往外抛，则就不会进入
      }
    }
    field.setAccessible(true);
    return field;
  }

}
