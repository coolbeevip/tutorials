package com.coolbeevip.groovy;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.Script;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class GroovyScriptEvaluator<T> implements Evaluator<T> {
  GroovyClassLoader groovyLoader = new GroovyClassLoader();
  private Map<String, GroovyObject> groovyObjectCache = new HashMap<>();
  private Map<String, String> groovyScriptCache = new HashMap();


  @Override
  public T evaluate(String groovyScriptId, String methodName, Map<String, Object> arguments) {
    GroovyObject groovyObject = groovyObjectCache.get(groovyScriptId);
    return (T) groovyObject.invokeMethod(methodName, new Object[]{arguments});
  }

  @Override
  public void loadGroovyScript(String id, String groovyScript) throws InstantiationException, IllegalAccessException {
    String hashKey = hash(groovyScript);
    if (!groovyScriptCache.containsKey(id) || !groovyScriptCache.get(id).equals(hashKey)) {
      groovyScriptCache.put(id, hashKey);
      Class<Script> groovyClass = (Class<Script>) groovyLoader.parseClass(groovyScript);
      groovyObjectCache.put(id, groovyClass.newInstance());
    }
  }

  private String hash(String origin) {
    MessageDigest md = null;
    try {
      md = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
    md.update(origin.getBytes());
    return new String(md.digest());
  }
}
