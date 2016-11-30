package com.demo.proxy.compiler.test;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import com.demo.proxy.Moveable;
import com.demo.proxy.Tank;

import javax.tools.JavaCompiler.CompilationTask;

public class Test {

	public static void main(String[] args) throws Exception {
		String rt = "\r\n";
		String src = 
				"package com.demo.proxy;"+ rt +

				"public class TankTimeProxy implements Moveable {"+ rt +
				"	public TankTimeProxy(Moveable t) {"+ rt +
				"		super();"+ rt +
				"		this.t = t;"+ rt +
				"	}"+ rt +

				"	Moveable t;"+ rt +

				"	@Override"+ rt +
				"	public void move() {"+ rt +
				"		long start = System.currentTimeMillis();"+ rt +
				"		System.out.println(\"start-time：\" + start);"+ rt +
				"		t.move();" + rt+	
				"		long end = System.currentTimeMillis();"+ rt +
				"		System.out.println(\"end-time：\" + end);"+ rt +
				"		System.out.println(\"time：\" + (end - start));"+ rt +
				"	}"+ rt +
				"}";
		String fileName = System.getProperty("user.dir") + "/src/com/demo/proxy/TankTimeProxy.java";
		
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
		//t.call();
		fileMgr.close();
		
		//加载到内存并生成新对象
		URL[] urls = new URL[]{new URL("file:/" + System.getProperty("user.dir") + "/src")};
		URLClassLoader ul = new URLClassLoader(urls);
		Class c =ul.loadClass("com.demo.proxy.TankTimeProxy");
		System.out.println(c);
		
		Constructor ctr = c.getConstructor(Moveable.class);
		Moveable m = (Moveable)ctr.newInstance(new Tank());
		m.move();
	}
}
