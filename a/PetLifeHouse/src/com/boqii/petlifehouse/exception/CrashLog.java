package com.boqii.petlifehouse.exception;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;

/**
 * Default exception handler for all activities.
 * 
 * @author DuJun
 * 
 */
public class CrashLog implements UncaughtExceptionHandler {
	 
		private Context context = null;

		private Object FILE_LOCK = new Object();
		private UncaughtExceptionHandler exceptionHandler;
		private static String LOG_PATH = Environment.getExternalStorageState() + "/boqii/petlifehouse";
		private static final String LOG_CRASH_FILENAME = "crash";
		private static final String LOG_FILE_EXTNAME = ".log";
		private static final int CRASH_LOG_MAX_FILE_LENGTH = 2048;
		public static final String fullName = LOG_PATH + File.separator + LOG_CRASH_FILENAME + LOG_FILE_EXTNAME;

		public CrashLog(Context ctx) {
			this.context = ctx;
			this.exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
			File dir = new File(LOG_PATH);
			if (!dir.exists() || !dir.isDirectory()) {
				dir.mkdirs();
			}
		}

		public void uncaughtException(Thread thread, Throwable ex) {
			saveCrashLog(ex);
			if (exceptionHandler != null) {
				exceptionHandler.uncaughtException(thread, ex);
			}
		}

		private void saveCrashLog(Throwable ex) {
			String crashLog = getDebugReport(ex);
			saveLogToFile(crashLog);
		}

		private String getDebugReport(Throwable exception) {
			NumberFormat theFormatter = new DecimalFormat("#0.");
			StringBuilder theErrReport = new StringBuilder();
			theErrReport.append(context.getPackageName()
					+ " generated the following exception:\n");
			if (exception != null) {
				theErrReport.append(exception.toString() + "\n\n");
				// stack trace
				StackTraceElement[] theStackTrace = exception.getStackTrace();
				if (theStackTrace.length > 0) {
					theErrReport.append("======== Stack trace =======\n");
					int length = theStackTrace.length;
					for (int i = 0; i < length; i++) {
						theErrReport.append(theFormatter.format(i + 1) + "\t"
								+ theStackTrace[i].toString() + "\n");
					}
					theErrReport.append("=====================\n\n");
				}
				Throwable theCause = exception.getCause();
				if (theCause != null) {
					theErrReport.append("======== Cause ========\n");
					theErrReport.append(theCause.toString() + "\n\n");
					theStackTrace = theCause.getStackTrace();
					int length = theStackTrace.length;
					for (int i = 0; i < length; i++) {
						theErrReport.append(theFormatter.format(i + 1) + "\t"
								+ theStackTrace[i].toString() + "\n");
					}
					theErrReport.append("================\n\n");
				}
				PackageManager pm = context.getPackageManager();
				PackageInfo pi;
				try {
					pi = pm.getPackageInfo(context.getPackageName(), 0);
				} catch (NameNotFoundException e) {
					pi = new PackageInfo();
					pi.versionName = "unknown";
					pi.versionCode = 0;
				}
				Date now = new Date();
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				theErrReport.append("======== Environment =======\n");
				theErrReport.append("Time=" + format.format(now) + "\n");
				theErrReport.append("Device=" + Build.FINGERPRINT + "\n");
				try {
					Field mfField = Build.class.getField("MANUFACTURER");
					theErrReport.append("Manufacturer=" + mfField.get(null) + "\n");
				} catch (SecurityException e) {
				} catch (NoSuchFieldException e) {
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				}
				theErrReport.append("Model=" + Build.MODEL + "\n");
				theErrReport.append("Product=" + Build.PRODUCT + "\n");
				theErrReport.append("App=" + context.getPackageName()
						+ ", version " + pi.versionName + " (build "
						+ pi.versionCode + ")\n");
				theErrReport.append("=========================\nEnd Report");
			} else {
				theErrReport.append("the exception object is null\n");
			}

			return theErrReport.toString();
		}

		/**
		 * 将日志写入文件�? crash日志每个文件�?���?K，超过大小的进行拆分�?对于拆分的文件命名为文件名加时间
		 * exception日志，每个文件最大为500k，超过大小的进行拆分，最多存在两个，旧的在后面加上_B的后�?
		 * debug日志，只有在debug模式下才会写入文件，且无文件大小限制 info日志，同�?
		 * 
		 * @param message
		 */
		public void saveLogToFile(String message) {
			if (isSDCardAvailable()) {
				synchronized (FILE_LOCK) {
					String fullName = LOG_PATH + File.separator
							+ LOG_CRASH_FILENAME + LOG_FILE_EXTNAME;
					File file = new File(fullName);
					if (file.exists() && file.isFile()) {
						if (file.length() >= CRASH_LOG_MAX_FILE_LENGTH) {
							String newFileName = LOG_PATH
									+ File.separator
									+ LOG_CRASH_FILENAME
									+ new SimpleDateFormat("yyyyMMddHHmmss")
											.format(new Date()) + LOG_FILE_EXTNAME;
							file.renameTo(new File(newFileName));
						}
					}
					writeFile(fullName, message);
				}
			}
		}

		private static boolean isSDCardAvailable() {
			String status = Environment.getExternalStorageState();
			if (status.equals(Environment.MEDIA_MOUNTED)) {
				return true;
			}

			return false;
		}

		private void writeFile(String fileName, String data) {
			BufferedWriter buf = null;
			try {
				buf = new BufferedWriter(new FileWriter(fileName, true));
				buf.write(data, 0, data.length());
				buf.newLine();
			} catch (OutOfMemoryError oom) {

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (buf != null)
						buf.close();
				} catch (IOException e) {
				}
			}

		}
		
		public String getFilePath(){
			String fullName = LOG_PATH + File.separator
					+ LOG_CRASH_FILENAME + LOG_FILE_EXTNAME;
			return fullName;
		}

}
