package com.demo.proxy;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class Proxy {
	public static Object newProxyInstance(Class infce, InvocationHandler h) throws Exception{ //JDK6 Complier API, CGLib, ASM
		String methodStr = "";
		String rt = "\r\n";
		
		Method[] methods = infce.getMethods();
		/*for(Method m:methods){
			methodStr += 
					"@Override" + rt + 
					"public void " + m.getName() + "() {" + rt +
					"		long start = System.currentTimeMillis();"+ rt +
					"		System.out.println(\"start-time：\" + start);"+ rt +
					"		t."+ m.getName() +"();" + rt+	
					"		long end = System.currentTimeMillis();"+ rt +
					"		System.out.println(\"end-time：\" + end);"+ rt +
					"		System.out.println(\"time：\" + (end - start));"+ rt +
					"	}";
		}*/
		
		for(Method m:methods){
			methodStr += 
					"@Override" + rt + 
					"public void " + m.getName() + "() {" + rt +
					"	try{" + rt +
					"		Method md = " + infce.getName() + ".class.getMethod(\"" + m.getName() + "\");" + rt +
					"		h.invoke(this,md);"+ rt +
					"	}catch(Exception e) {e.printStackTrace();}"+rt+
					"	}";
		}
		
		//System.out.println(methodStr);
		
		String src = 
				"package com.demo.proxy;"+ rt +
				"import java.lang.reflect.Method;" + rt +
				"public class TankTimeProxy implements "+ infce.getName() +" {"+ rt +
				"	public TankTimeProxy(InvocationHandler h) {"+ rt +
				"		this.h = h;"+ rt +
				"	}"+ rt +

				"	com.demo.proxy.InvocationHandler h;"+ rt +

				methodStr+ rt +
				"}";
		String fileName = "d:/src/com/demo/proxy/TankTimeProxy.java";
		
		File f = new File(fileName);
		FileWriter fw = new FileWriter(f);
		fw.write(src);
		fw.flush();
		fw.close();
		
		//编译
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileMgr = compiler.getStandardFileManager(null, null, null);
		Iterable units = fileMgr.getJavaFileObjects(fileName);
		CompilationTask t = compiler.getTask(null, fileMgr, null, null, null, units);
		t.call();
		fileMgr.close();
		
		//加载到内存并生成新对象
		URL[] urls = new URL[]{new URL("file:/d:/src")};
		URLClassLoader ul = new URLClassLoader(urls);
		Class c =ul.loadClass("com.demo.proxy.TankTimeProxy");
		System.out.println(c);
		
		Constructor ctr = c.getConstructor(InvocationHandler.class);
		Object m = ctr.newInstance(h);
		//m.move();
		return m;
	}
}
