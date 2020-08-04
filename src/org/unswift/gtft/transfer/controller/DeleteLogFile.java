package org.unswift.gtft.transfer.controller;

import java.io.File;

public class DeleteLogFile {

	public static void main(String[] args) {
		File file=new File("D:\\develop\\web-home\\gtft-transfer2\\task-logger\\");
		readChildFile(file);
	}
	
	public static void readChildFile(File file){
		File[] files=file.listFiles();
		for (File file2 : files) {
			if(file2.isFile()){
				file2.delete();
				System.out.println("删除文件："+file2.getAbsolutePath());
			}else if(file2.isDirectory()){
				readChildFile(file2);
				//file2.delete();
				//System.out.println("删除文件夹："+file2.getAbsolutePath());
			}
		}
	}
}
