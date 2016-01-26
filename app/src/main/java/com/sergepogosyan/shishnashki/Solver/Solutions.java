package com.sergepogosyan.shishnashki.solver;

public class Solutions {

  public static int getCaseL(int[] tiles) {
    StringBuilder str = new StringBuilder();
    for (int i : tiles) {
      str.append(i);
    }
    return Integer.parseInt(str.toString());
  }

  public static int getCaseH(int[] tiles) {
    StringBuilder str = new StringBuilder();
    for (int i : tiles) {
      str.append(i - 8);
    }
    return Integer.parseInt(str.toString());
  }

  // Better than static final field -> allows VM to unload useless String
  // Because you need this string only once per application life on the device
  public static int[] getSolutions(int fullCaseNum) {
    // TODO: 1/15/2016 return map of solutions for specified range
    // TODO: 1/22/2016 check memory consumpution, probably keeping two arrays of int and long is better than map 
    long solution = 0;
    int caseNum = Integer.valueOf(String.valueOf(fullCaseNum).substring(0, 2));
    switch (caseNum) {
      case 12:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 13:
        solution = Storage1.checkCase13(fullCaseNum);
        break;
      case 14:
        solution = Storage1.checkCase14(fullCaseNum);
        break;
      case 15:
        solution = Storage1.checkCase15(fullCaseNum);
        break;
      case 16:
        solution = Storage1.checkCase16(fullCaseNum);
        break;
      case 17:
        solution = Storage1.checkCase17(fullCaseNum);
        break;
      case 18:
        solution = Storage1.checkCase18(fullCaseNum);
        break;
      case 21:
        solution = Storage2.checkCase21(fullCaseNum);
        break;
      case 23:
        solution = Storage2.checkCase23(fullCaseNum);
        break;
      case 24:
        solution = Storage2.checkCase24(fullCaseNum);
        break;
      case 25:
        solution = Storage2.checkCase25(fullCaseNum);
        break;
      case 26:
        solution = Storage2.checkCase26(fullCaseNum);
        break;
      case 27:
        solution = Storage2.checkCase27(fullCaseNum);
        break;
      case 28:
        solution = Storage2.checkCase28(fullCaseNum);
        break;
      case 31:
        solution = Storage3.checkCase31(fullCaseNum);
        break;
      case 32:
        solution = Storage3.checkCase32(fullCaseNum);
        break;
      case 34:
        solution = Storage3.checkCase34(fullCaseNum);
        break;
      case 35:
        solution = Storage3.checkCase35(fullCaseNum);
        break;
      case 36:
        solution = Storage3.checkCase36(fullCaseNum);
        break;
      case 37:
        solution = Storage3.checkCase37(fullCaseNum);
        break;
      case 38:
        solution = Storage3.checkCase38(fullCaseNum);
        break;
      case 41:
        solution = Storage4.checkCase41(fullCaseNum);
        break;
      case 42:
        solution = Storage4.checkCase42(fullCaseNum);
        break;
      case 43:
        solution = Storage4.checkCase43(fullCaseNum);
        break;
      case 45:
        solution = Storage4.checkCase45(fullCaseNum);
        break;
      case 46:
        solution = Storage4.checkCase46(fullCaseNum);
        break;
      case 47:
        solution = Storage4.checkCase47(fullCaseNum);
        break;
      case 48:
        solution = Storage4.checkCase48(fullCaseNum);
        break;
      case 51:
        solution = Storage5.checkCase51(fullCaseNum);
        break;
      case 52:
        solution = Storage5.checkCase52(fullCaseNum);
        break;
      case 53:
        solution = Storage5.checkCase53(fullCaseNum);
        break;
      case 54:
        solution = Storage5.checkCase54(fullCaseNum);
        break;
      case 56:
        solution = Storage5.checkCase56(fullCaseNum);
        break;
      case 57:
        solution = Storage5.checkCase57(fullCaseNum);
        break;
      case 58:
        solution = Storage5.checkCase58(fullCaseNum);
        break;
      case 61:
        solution = Storage6.checkCase61(fullCaseNum);
        break;
      case 62:
        solution = Storage6.checkCase62(fullCaseNum);
        break;
      case 63:
        solution = Storage6.checkCase63(fullCaseNum);
        break;
      case 64:
        solution = Storage6.checkCase64(fullCaseNum);
        break;
      case 65:
        solution = Storage6.checkCase65(fullCaseNum);
        break;
      case 67:
        solution = Storage6.checkCase67(fullCaseNum);
        break;
      case 68:
        solution = Storage6.checkCase68(fullCaseNum);
        break;
      case 71:
        solution = Storage7.checkCase71(fullCaseNum);
        break;
      case 72:
        solution = Storage7.checkCase72(fullCaseNum);
        break;
      case 73:
        solution = Storage7.checkCase73(fullCaseNum);
        break;
      case 74:
        solution = Storage7.checkCase74(fullCaseNum);
        break;
      case 75:
        solution = Storage7.checkCase75(fullCaseNum);
        break;
      case 76:
        solution = Storage7.checkCase76(fullCaseNum);
        break;
      case 78:
        solution = Storage7.checkCase78(fullCaseNum);
        break;
      case 81:
        solution = Storage8.checkCase81(fullCaseNum);
        break;
      case 82:
        solution = Storage8.checkCase82(fullCaseNum);
        break;
      case 83:
        solution = Storage8.checkCase83(fullCaseNum);
        break;
      case 84:
        solution = Storage8.checkCase84(fullCaseNum);
        break;
      case 85:
        solution = Storage8.checkCase85(fullCaseNum);
        break;
      case 86:
        solution = Storage8.checkCase86(fullCaseNum);
        break;
      case 87:
        solution = Storage8.checkCase87(fullCaseNum);
        break;
    }

    int[] turns = null;
    if (solution != 0) {
      String temp = String.valueOf(solution);
      int cap = temp.length();
      turns = new int[cap];
      for (int i = cap; i > 0; i--) {
        turns[cap - i] = Integer.valueOf(temp.substring(i-1, i));
      }
    }
    return turns;
  }
}
