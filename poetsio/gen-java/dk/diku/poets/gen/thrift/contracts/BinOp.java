/**
 * Autogenerated by Thrift Compiler (0.8.0-dev)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package dk.diku.poets.gen.thrift.contracts;


import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.TEnum;

public enum BinOp implements org.apache.thrift.TEnum {
  EQ(1),
  LEQ(2),
  PLUS(3),
  TIMES(4),
  DIV(5),
  AND(6),
  CONS(7),
  DPLUS(8),
  DTIMES(9);

  private final int value;

  private BinOp(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  public static BinOp findByValue(int value) { 
    switch (value) {
      case 1:
        return EQ;
      case 2:
        return LEQ;
      case 3:
        return PLUS;
      case 4:
        return TIMES;
      case 5:
        return DIV;
      case 6:
        return AND;
      case 7:
        return CONS;
      case 8:
        return DPLUS;
      case 9:
        return DTIMES;
      default:
        return null;
    }
  }
}