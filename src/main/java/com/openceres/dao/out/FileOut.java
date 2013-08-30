package com.openceres.dao.out;


import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import com.openceres.model.ActorInfo;

public class FileOut implements Out{
	static FileOut fileOut = new FileOut();
	
	final static int PAGE_MAX = 50;
	
	String path = "/Users/changbaechoi";
	String logPattern = "testing_%d-%d-%d.log";
	FileWriter logfile = null;
	Calendar calendar = null;

	private FileOut() {
	}

	public static FileOut getInstance() {
		return fileOut;
	}
	
	private void openFile(Calendar calendar) throws IOException {
		String fileName = String.format(logPattern, 
				calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DATE));
		String fullName = path + "/" + fileName;
		
		logfile = new FileWriter(fullName);
		this.calendar = calendar;
	}
	
	private void closeFile() throws IOException {
		logfile.close();
		logfile = null;
	}
	
	@Override
	public void writeLog(ActorInfo actorInfo) {
		Calendar logCal = Calendar.getInstance();
		logCal.setTime(actorInfo.getStart());
		try {
			//날짜가 같으면 현재 파일에 쓴다. 
			if(calendar != null || calendar.get(Calendar.DATE) != logCal.get(Calendar.DATE)) {
				if(logfile != null) {
					closeFile();
				}
				openFile(logCal);
			}
			
			logfile.write(actorInfo.toJson());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
