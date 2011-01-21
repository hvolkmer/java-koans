package com.sandwich.koans;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlTest;

public class KoansOrderer implements IMethodInterceptor {

	 public static final Comparator<IMethodInstance> KOANS_SORTER 
	    = new Comparator<IMethodInstance>() {
	    public int compare(IMethodInstance o1, IMethodInstance o2) {
	      // If the two methods are in different <test>
	      XmlTest test1 = o1.getMethod().getTestClass().getXmlTest();
	      XmlTest test2 = o2.getMethod().getTestClass().getXmlTest();

	      // If the two methods are not in the same <test>, we can't compare them
	      if (! test1.getName().equals(test2.getName())) {
	        return 0;
	      }

	      int result = 0;

	      // If the two methods are in the same <class>, compare them by their Order
	      // index, otherwise compare them with their class index.
	      XmlClass class1 = o1.getMethod().getTestClass().getXmlClass();
	      XmlClass class2 = o2.getMethod().getTestClass().getXmlClass();

	      if (! class1.getName().equals(class2.getName())) {
	        int index1 = class1.getIndex();
	        int index2 = class2.getIndex();
	        result = index1 - index2;
	      }
	      else {
	    	  result = getMethodOrder(o1) - getMethodOrder(o2);
	      }

	      return result;
	    }

		private int getMethodOrder(IMethodInstance mi) {
			int result = 10000;
			
			Method method = mi.getMethod().getMethod();
			Order a1 = method.getAnnotation(Order.class);
			if (a1 != null) {
				result = a1.value();
			} 
			return result;
		}

	    
	  };
	
	public List<IMethodInstance> intercept(List<IMethodInstance> methods,
			ITestContext context) {
		
		Collections.sort(methods, KOANS_SORTER);
		return methods;

	}
}