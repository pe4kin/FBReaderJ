package org.geometerplus.android.fbreader.library;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.geometerplus.fbreader.formats.PluginCollection;
import org.geometerplus.fbreader.library.Book;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.resources.ZLResource;

public class FileUtil {
	
	public static void copyFile(String src, String targetDirectory) throws IOException {
		final ZLResource myResource = ZLResource.resource("libraryView");

		if (ZLFile.createFileByPath(targetDirectory).isDirectory()){
			ZLFile srcFile = ZLFile.createFileByPath(src);
			String srcName = srcFile.getShortName();
			
			for (ZLFile f : ZLFile.createFileByPath(targetDirectory).children()){
				if (f.getShortName().equals(srcName))
					throw new IOException (myResource.getResource("messFileExists").getValue());
			}
			String target = targetDirectory + "/" + srcFile.getShortName(); 
			FileChannel ic = new FileInputStream(src).getChannel();
			FileChannel oc = new FileOutputStream(target).getChannel();
			ic.transferTo(0, ic.size(), oc);
			ic.close();
			oc.close();
		}else{
			throw new IOException(myResource.getResource("messInsertIntoArchive").getValue());
		}
	}
	
	public static void moveFile(String src, String targetDirectory) throws IOException {
		copyFile(src, targetDirectory);
		ZLFile.createFileByPath(src).getPhysicalFile().delete();
	}
	
	public static boolean contain(String fileName, ZLFile parent){
		for(ZLFile f : parent.children()){
			if (f.getShortName().equals(fileName))
				return true;
		}
		return false;
	}
	
	public static List<Book> getBooksList(ZLFile file){
		List<Book> books = null;
		if (file.isDirectory() || file.isArchive()){
			books = new ArrayList<Book>();
			getBooks(file, books);
		}
		return books;
	}
	
	private static void getBooks(ZLFile file, List<Book> books){
		for(ZLFile f : file.children()){
			if (PluginCollection.Instance().getPlugin(f) != null){
				books.add(Book.getByFile(f));
			} else if (f.isDirectory() || f.isArchive()) {
				getBooks(f, books);
			}
		}
	}
}
