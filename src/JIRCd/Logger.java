package JIRCd;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	private String Name;
	private String OutputFilename;
	private boolean LogToConsole = true;
	public Logger(String Name, String Filename) {
		this.Name = Name;
		this.OutputFilename = Filename;
	}
	
	public Logger(String Name, String Filename, boolean ConsoleOutput) {
		this.Name = Name;
		this.OutputFilename = Filename;
		this.LogToConsole = ConsoleOutput;
	}
	
	public String GetName() {
		return Name;
	}
	
	private void log(String Output) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(OutputFilename, true));
			out.write(Output + "\r\n");
			out.close();
			if(LogToConsole){
				System.out.println(Output);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void info(String Message) {
		Date CurrentDateTime = new Date();
		new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(CurrentDateTime);
		String Output = "[" + CurrentDateTime.toString() + "] " + Message;
		log(Output);
	}
	
	public void info(String Message, boolean Console) {
		Date CurrentDateTime = new Date();
		new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(CurrentDateTime);
		String Output = "[" + CurrentDateTime.toString() + "] " + Message;
		boolean OriginalConsoleStatus = LogToConsole;
		LogToConsole = Console;
		log(Output);
		LogToConsole = OriginalConsoleStatus;
	}
	
	public void warning(String Message) {
		Date CurrentDateTime = new Date();
		new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(CurrentDateTime);
		String Output = "[" + CurrentDateTime.toString() + "] [WARNING] " + Message;
		log(Output);
	}
	
	public void severe(String Message) {
		Date CurrentDateTime = new Date();
		new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(CurrentDateTime);
		String Output = "[" + CurrentDateTime.toString() + "] [SEVERE] " + Message;
		log(Output);
	}
}
