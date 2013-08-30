package com.openceres.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class DynamicLoader {
	
	@SuppressWarnings({ "resource", "rawtypes" })
	public static void loadJar(List<String> jarNameList) throws IOException, ClassNotFoundException {
		File file = null;
		URLClassLoader clazzLoader = null;
		for(String jarName : jarNameList) {
			 file = new File(jarName);
			 clazzLoader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()});
			 
			 JarFile jarFile = new JarFile(file);
			 Enumeration<JarEntry> entries = jarFile.entries();
			 
			 while (entries.hasMoreElements()) {
				 JarEntry element = entries.nextElement();
				 if(element.getName().endsWith(".class")) {
					 Class c = clazzLoader.loadClass(element.getName().replaceAll(".class", "").replaceAll("/", "."));
				 }
			 }
		}
	}
	
}
