package com.demo.proxy.compiler.test;

import java.lang.reflect.Method;

import sun.applet.Main;

public class Test2 {
	public static void main(String[] args) {
		Method[] methods = com.demo.proxy.Moveable.class.getMethods();
		for(Method m:methods){
			System.out.println(m.getName());
		}
	}
}
