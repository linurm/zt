package zj.zfenlly.coloradjust;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.util.Log;

class FileOpration {
	static private File OpFile = null;

	// static final private String sys_file1 = "/sys/class/graphics/fb4/bcsh";

	private static final String TAG = FileOpration.class.getSimpleName();

	private static void print(String msg) {
		Log.i(TAG, msg);
	}

	static public int reset_bcsh(String sys_file) {
		OpFile = new File(sys_file);
		if (OpFile.exists()) {
			RandomAccessFile rdf = null;
			try {
				rdf = new RandomAccessFile(OpFile, "rw");
				try {
					rdf.writeBytes("close");
					return 1;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return 0;
	}

	static public int open_bcsh(String sys_file) {
		OpFile = new File(sys_file);
		if (OpFile.exists()) {
			RandomAccessFile rdf = null;
			try {
				rdf = new RandomAccessFile(OpFile, "rw");
				try {
					rdf.writeBytes("open");
					return 1;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return 0;
	}

	static public int write_bcsh(String sys_file, String st) {
		OpFile = new File(sys_file);
		if (OpFile.exists()) {
			RandomAccessFile rdf = null;
			try {
				rdf = new RandomAccessFile(OpFile, "rw");
				try {
					rdf.writeBytes(st);
					print(st);
					return 1;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return 0;
	}

	static public String read_bcsh(String sys_file) {
		FileReader fread = null;
		try {
			fread = new FileReader(sys_file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		BufferedReader buffer = new BufferedReader(fread);
		String str = null;
		try {
			while ((str = buffer.readLine()) != null) {
				// Log.w("fileop", "str " + str);
				return str;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
