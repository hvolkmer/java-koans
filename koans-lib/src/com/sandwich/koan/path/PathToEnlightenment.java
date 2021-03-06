package com.sandwich.koan.path;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.sandwich.koan.KoanMethod;
import com.sandwich.koan.constant.KoanConstants;
import com.sandwich.koan.path.xmltransformation.XmlToPathTransformer;
import com.sandwich.koan.path.xmltransformation.XmlToPathTransformer.KoanElementAttributes;

public abstract class PathToEnlightenment {

	static Path theWay;

	static Path createPath(){
		try{
			return new XmlToPathTransformer(KoanConstants.PATH_XML_LOCATION).transform();
		}catch(Throwable t){
			throw new RuntimeException(t);
		}
	}
	
	public static Path getPathToEnlightment(){
		if(theWay == null){
			theWay = createPath();
		}
		return theWay;
	}
	
	public static void removeAllKoanMethodsExcept(String koanName) {
		Path koans = getPathToEnlightment();
		// if more than 1 pkg, or more than 1 suite - something is likely broken before this point
		Map<Object, List<KoanMethod>> lessonsBySuiteMap = koans.iterator().next().getValue();
		koanName = koanName.trim();
		for(Entry<Object, List<KoanMethod>> methodsBySuite : lessonsBySuiteMap.entrySet()){
			KoanMethod keeper = null;
			for(KoanMethod method : methodsBySuite.getValue()){
				if(koanName.equals(method.getMethod().getName())){
					keeper = method;
					break;
				}
			}
			if(keeper == null){
				// default is to assume method if not a class or recognized arg
				// no sense throwing exception, might be user error
				System.err.println(koanName+" was not a recognized parameter.");
				System.exit(-3);
			}
			methodsBySuite.setValue(Arrays.asList(keeper));
		}
		// defer the warning until after potential user error is in console and app is exited
		if(koans.size() != 1 || lessonsBySuiteMap.size() != 1){
			Logger.getAnonymousLogger().warning("not just one koansuite remains, " +
				"check koan suite name argument - not likely that filtering by method will work.");
		}
	}

	static void stagePathToEnlightenment(String pkg, String koanSuite){
		try{
			Class<?> koanClass;
			if(koanSuite.contains(KoanConstants.PERIOD)){
				koanClass = Class.forName(koanSuite);
			}else{
				koanClass = Class.forName(
						new StringBuilder(pkg).append(KoanConstants.PERIOD).append(koanSuite).toString());
			}
			stagePathToEnlightenment(koanClass);
		}catch(Throwable t){
			throw new RuntimeException(t);
		}
	}

	public static void stagePathToEnlightenment(String koanSuite){
		try{
			Class<?> koanClass;
			if(koanSuite.contains(KoanConstants.PERIOD)){
				koanClass = Class.forName(koanSuite);
			}else{
				throw new RuntimeException("need package to instantiate the class");
			}
			stagePathToEnlightenment(koanClass);
		}catch(Throwable t){
			throw new RuntimeException(t);
		}
	}
	
	static void stagePathToEnlightenment(Class<?> koanSuite) {
		try {
			stagePathToEnlightenment(koanSuite.newInstance());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	static void stagePathToEnlightenment(Object koanSuite) {
		if(koanSuite instanceof Class<?>){
			stagePathToEnlightenment((Class<?>)koanSuite);
		}
		Map<String, Map<Object, List<KoanMethod>>> koans = new HashMap<String, Map<Object,List<KoanMethod>>>();
		Map<Object, List<KoanMethod>> suiteAndMethods = new HashMap<Object, List<KoanMethod>>();
		List<KoanMethod> methods = XmlToPathTransformer.getKoanMethods(koanSuite.getClass(), 
				createStubbedLessonMapForSuite(koanSuite.getClass()));
		suiteAndMethods.put(koanSuite, methods);
		koans.put(koanSuite.toString(), suiteAndMethods);
		PathToEnlightenment.theWay = new Path(koans);
	}
	
	private static Map<String, KoanElementAttributes> createStubbedLessonMapForSuite(
			Class<? extends Object> clazz) {
		Map<String, KoanElementAttributes> partialLessonByKoanMap = new HashMap<String, KoanElementAttributes>();
		Map<String, String> lessonsByMethodName = Collections.emptyMap();
		try{
			Path path = PathToEnlightenment.createPath();
			Map<String, Map<Object, List<KoanMethod>>> koanMethodsBySuiteByPackage = path.koanMethodsBySuiteByPackage;
			Map<Object, List<KoanMethod>> methodsBySuite = Collections.emptyMap();
			for(Entry<String, Map<Object, List<KoanMethod>>> e : koanMethodsBySuiteByPackage.entrySet()){
				for(Entry<Object, List<KoanMethod>> e1 : e.getValue().entrySet()){
					if(clazz.isInstance(e1.getKey())){
						methodsBySuite = e.getValue();
						break;
					}
				}
			}
			for(Entry<Object, List<KoanMethod>> e : methodsBySuite.entrySet()){
				if(clazz.isInstance(e.getKey())){
					lessonsByMethodName = new HashMap<String, String>();
					for(KoanMethod method : e.getValue()){
						lessonsByMethodName.put(method.getMethod().getName(), method.getLesson());
					}
					break;
				}
			}
		}catch(Throwable x){
			Logger.getAnonymousLogger().severe(x.getMessage());
		}
		for(Method m : clazz.getMethods()){
			String methodName = m.getName();
			partialLessonByKoanMap.put(methodName, new KoanElementAttributes(
				lessonsByMethodName.get(methodName), methodName, "true"));
		}
		return partialLessonByKoanMap;
	}

	private PathToEnlightenment(){} // non instantiable
	
	public static class FileFormatException extends RuntimeException{
		private static final long serialVersionUID = -1343169944770684376L;
		public FileFormatException(String message){
			super(message);
		}
	}
	
	public static class Path implements Iterable<Entry<String, Map<Object, List<KoanMethod>>>>{
		final Map<String, Map<Object, List<KoanMethod>>> koanMethodsBySuiteByPackage;
		public Path(Map<String, Map<Object, List<KoanMethod>>> koanMethodsBySuiteByPackage){
			this.koanMethodsBySuiteByPackage = Collections.unmodifiableMap(koanMethodsBySuiteByPackage);
		}
		public int getTotalNumberOfKoans() {
			int total = 0;
			for(Entry<String, Map<Object, List<KoanMethod>>> e0 : koanMethodsBySuiteByPackage.entrySet()){
				for(Entry<Object, List<KoanMethod>> e1 : e0.getValue().entrySet()){
					total += e1.getValue().size();
				}
			}
			return total;
		}
		public Iterator<Entry<String, Map<Object, List<KoanMethod>>>> iterator() {
			return koanMethodsBySuiteByPackage.entrySet().iterator();
		}
		public int size() {
			return koanMethodsBySuiteByPackage.size();
		}
		public int size(String pkg){
			Map<?,?> suiteAndKoans = koanMethodsBySuiteByPackage.get(pkg);
			if(suiteAndKoans == null){
				return -1;
			}
			return suiteAndKoans.size();
		}
		@Override public boolean equals(Object o){
			if(o == this){
				return true;
			}
			if(o instanceof Path){
				if(koanMethodsBySuiteByPackage == ((Path)o).koanMethodsBySuiteByPackage){
					return true;
				}
				if(koanMethodsBySuiteByPackage == null || ((Path)o).koanMethodsBySuiteByPackage == null
						|| koanMethodsBySuiteByPackage.size() != ((Path)o).koanMethodsBySuiteByPackage.size()
						|| koanMethodsBySuiteByPackage.getClass() != ((Path)o).koanMethodsBySuiteByPackage.getClass()){
					return false;
				}
				Iterator<Entry<String,Map<Object, List<KoanMethod>>>> i1 = 
					koanMethodsBySuiteByPackage.entrySet().iterator();
				Iterator<Entry<String,Map<Object, List<KoanMethod>>>> i2 = 
					((Path)o).koanMethodsBySuiteByPackage.entrySet().iterator();
				while(i1.hasNext()){
					Map<Object, List<KoanMethod>> m1 = i1.next().getValue();
					Map<Object, List<KoanMethod>> m2 = i2.next().getValue();
					if(m1 == m2){
						continue;
					}
					if(			m1 == null 
							||  m2 == null
							||  m1.size() != m2.size()
							||  m1.getClass() != m2.getClass()){
						return false;
					}
					Iterator<Entry<Object, List<KoanMethod>>> ii1 = m1.entrySet().iterator();
					Iterator<Entry<Object, List<KoanMethod>>> ii2 = m2.entrySet().iterator();
					while(ii1.hasNext()){
						Entry<Object, List<KoanMethod>> e1 = ii1.next();
						Entry<Object, List<KoanMethod>> e2 = ii2.next();
						if(!e1.getKey().getClass().equals(e2.getKey().getClass())){
							return false;
						}
						if(!e1.getValue().equals(e2.getValue())){
							return false;
						}
					}
				}
			}
			return true;
		}
		@Override public int hashCode(){
			return koanMethodsBySuiteByPackage.hashCode();
		}
		@Override public String toString(){
			return koanMethodsBySuiteByPackage.toString();
		}
	}
}
