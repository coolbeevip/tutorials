package com.coolbeevip.java.comparator;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudentMultiFieldsTest {

  @Test
  public void test(){
    List<StudentMultiFields> ss = new ArrayList<>();

    StudentMultiFields s1 = new StudentMultiFields();
    s1.setAge(10);
    s1.setHigh(170);
    ss.add(s1);

    StudentMultiFields s2 = new StudentMultiFields();
    s2.setAge(11);
    s2.setHigh(130);
    ss.add(s2);

    StudentMultiFields s3 = new StudentMultiFields();
    s3.setAge(11);
    s3.setHigh(120);
    ss.add(s3);

    Collections.sort(ss);
    ss.forEach(System.out::println);
  }
}
