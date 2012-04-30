package com.gonevertical.client.views.peopleedit.editor;

import java.util.ArrayList;

public enum Gender {

  MALE(1),

  FEMALE(2);

  int gender;

  Gender(int gender) {
    this.gender = gender;
  }

  public int getGender() {
    return gender;
  }

  public String toString() {
    return name();
  }
  
  public static ArrayList<Integer> getValues() {
    Gender[] values = Gender.values();
    ArrayList<Integer> list = new ArrayList<Integer>();
    for (int i=0; i < values.length; i++) {
      list.add(values[i].getGender());
    }
    return list;
  }
  
  public static String getGenderName(Integer gender) {
    if (gender == null) {
      return "";
    }
    String s = "";
    switch (gender) {
    case 1:
      s = "male";
      break;
    case 2:
      s = "female";
      break;
    }
    return s;
  }

}
