package com.sergepogosyan.shishnashki.solver;

public class Solutions {

  public static int getCase(int[] tiles) {
    StringBuilder str = new StringBuilder();
    for (int i : tiles) {
      str.append(i);
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
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 23:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 24:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 25:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 26:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 27:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 28:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 31:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 32:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 34:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 35:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 36:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 37:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 38:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 41:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 42:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 43:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 45:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 46:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 47:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 48:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 51:
        solution = Storage5.checkCase51(fullCaseNum);
        break;
      case 52:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 53:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 54:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 56:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 57:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 58:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 61:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 62:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 63:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 64:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 65:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 67:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 68:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 71:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 72:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 73:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 74:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 75:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 76:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 78:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 81:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 82:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 83:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 84:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 85:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 86:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
      case 87:
        solution = Storage1.checkCase12(fullCaseNum);
        break;
    }

    int[] turns = null;
    if (solution != 0) {
      String temp = String.valueOf(solution);
      int cap = temp.length();
      turns = new int[cap];
      for (int i = 0; i < cap; i++) {
        turns[i] = Integer.valueOf(temp.substring(i, i+1));
      }
    }
    return turns;
  }
}
