import java.io.*;


public class CollectionsConverter {

    CollectionsConverter(String sourceDir, String destDir) {
	convertDirectory(sourceDir, destDir);
    }

    private void convertDirectory(String sourcePath, String destPath) {
	File f = new File(sourcePath);
	if (f.isDirectory()) {
	    File f2 = new File(destPath);
	    if (!f2.exists()) {
		f2.mkdir();
	    }

	    String list[] = f.list();
	    for (int i = 0; i < list.length; i++) {
		convertDirectory(sourcePath + File.separator + list[i],
				 destPath + File.separator + list[i]);
	    }
	} else {
	    if (sourcePath.endsWith(".java")) {
		convertFile(sourcePath, destPath);
	    } else {
		copyFile(sourcePath, destPath);
	    }
	}
    }

    private void copyFile(String source, String dest) {
	System.out.println(source + " -> " + dest);
	try {
	    InputStream is = new FileInputStream(source);
	    OutputStream os = new FileOutputStream(dest);

	    int b = is.read();
	    while (b >= 0) {
		os.write(b);
		b = is.read();
	    }

	    os.close();
	    is.close();
	} catch (FileNotFoundException fnfe) {
	    System.out.println(fnfe.toString());
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	}
    }

    private void convertFile(String source, String dest) {
	System.out.println("converting: " + source + " -> " + dest);
	try {
	    BufferedReader r = new BufferedReader(new FileReader(source));
	    BufferedWriter w = new BufferedWriter(new FileWriter(dest));

	    String line = r.readLine();
	    while (line != null) {
		line = convertUtil(line, "AbstractCollection");
		line = convertUtil(line, "AbstractList");
		line = convertUtil(line, "AbstractMap");
		line = convertUtil(line, "AbstractSequentialList");
		line = convertUtil(line, "AbstractSet");
		line = convertUtil(line, "ArrayList");
		line = convertUtil(line, "Arrays");
		line = convertUtil(line, "Collection");
		line = convertUtil(line, "Collections");
		line = convertUtil(line, "Comparator");
		line = convertUtil(line, "ConcurrentModificationException");
		line = convertUtil(line, "HashMap");
		line = convertUtil(line, "HashSet");
		line = convertUtil(line, "Hashtable");
		line = convertUtil(line, "Iterator");
		line = convertUtil(line, "LinkedList");
		line = convertUtil(line, "List");
		line = convertUtil(line, "ListIterator");
		line = convertUtil(line, "Map");
		line = convertUtil(line, "NoSuchElementException");
		line = convertUtil(line, "Random");
		line = convertUtil(line, "Set");
		line = convertUtil(line, "SortedMap");
		line = convertUtil(line, "SortedSet");
		line = convertUtil(line, "TreeMap");
		line = convertUtil(line, "TreeSet");
		line = convertUtil(line, "Vector");
		line = convertLang(line, "Comparable");
		line = convertLang(line, "UnsupportedOperationException");
	    
		w.write(line);
		w.newLine();
		line = r.readLine();
	    }
	    
	    w.flush();
	    w.close();
	    
	    r.close();
	} catch (FileNotFoundException fnfe) {
	    System.out.println(fnfe.toString());
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	}
    }

    private String convertUtil(String line, String className) {
	return convert(line, "java.util." + className,
		       "com.sun.java.util.collections." + className);
    }

    private String convertLang(String line, String className) {
	return convert(line, "java.lang." + className,
		       "com.sun.java.util.collections." + className);
    }

    private String convert(String line, String thisStr, String thatStr) {
	String str = new String(line);

	int index = line.indexOf(thisStr);
	if (index >= 0) {
	    str = line.substring(0, index);
	    str += thatStr;
	    str += line.substring(index + thisStr.length(), line.length());
	}

	return str;
    }

    public static void main(String args[]) {
	if (args.length == 2) {
	    new CollectionsConverter(args[0], args[1]);
	} else {
	    System.out.println("usage: CollectionsConverter [source] [dest]");
	}
    }
}
