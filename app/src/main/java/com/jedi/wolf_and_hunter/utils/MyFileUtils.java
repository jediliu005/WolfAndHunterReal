package com.jedi.wolf_and_hunter.utils;

//import net.lingala.zip4j.core.ZipFile;
//import net.lingala.zip4j.exception.ZipException;
//import net.lingala.zip4j.io.ZipInputStream;
//import net.lingala.zip4j.model.FileHeader;
//import net.lingala.zip4j.unzip.UnzipUtil;


public class MyFileUtils {

//	private static String dataBasePath;
//	private static String photoPath;
//	private static String videoPath;
//	private static String sqliteFilePath;
//	private static String otherFilePath;
//	private static String zipFileName;
//	private static String packageBase;
//	private static String sqliteFileName;
//	private static String otherZipFileName;
//
//
//	public static String getOtherFilePath() {
//		return otherFilePath;
//	}
//	public static String getOtherZipFileName() {
//		return otherZipFileName;
//	}
//
//	public static String getSqliteFileName() {
//		return sqliteFileName;
//	}
//
//	public static String getZipFileName() {
//		return zipFileName;
//	}
//
//	public static String getDataBasePath() {
//		return dataBasePath;
//	}
//
//	public static String getPhotoPath() {
//		return photoPath;
//	}
//
//	public static String getVideoPath() {
//		return videoPath;
//	}
//
//	public static String getSqliteFilePath() {
//		return sqliteFilePath;
//	}
//
//	public static String getPackageBase() {
//		return packageBase;
//	}
//
//	public static void initStaticFilePath(Context context) throws IOException {
//		Properties p = new Properties();
//		InputStream is = context.getAssets().open("path.properties");
//		p.load(is);
//		dataBasePath = (String) p.get("dataBasePath");
//		photoPath = (String) p.get("photoPath");
//		videoPath = (String) p.get("videoPath");
//		sqliteFilePath = (String) p.get("sqliteFilePath");
//		otherFilePath = (String) p.get("otherFilePath");
//		zipFileName = (String) p.get("zipFileName");
//		packageBase = (String) p.get("packageBase");
//		otherZipFileName = (String) p.get("otherZipFileName");
//		sqliteFileName = sqliteFilePath.substring(sqliteFilePath
//				.lastIndexOf("/") + 1);
//	}
//
//	public static boolean copyFileFromAsset(Context context, String fileName) {
//		InputStream is = null;
//		FileOutputStream fos = null;
//		try {
//			is = context.getAssets().open(fileName);
//			File toFile = new File(context.getFilesDir() + "/" + fileName);
//			if (toFile.exists() == false)
//				toFile.createNewFile();
//			fos = new FileOutputStream(toFile);
//			int len;
//			byte[] buff = new byte[1024];
//			while ((len = is.read(buff)) > 0) {
//				fos.write(buff, 0, len);
//			}
//			fos.flush();
//
//		} catch (IOException e) {
//			e.printStackTrace();
//			return false;
//		} finally {
//			if (fos != null)
//				try {
//					fos.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			if (is != null)
//				try {
//					is.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//		}
//
//		return true;
//	}
//
//	public static boolean dealNewDataZip(Context context, boolean deleteAtFirst)
//			throws Exception {
//		initStaticFilePath(context);
//		File zipFile = new File(
//				Environment
//						.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//						+ File.separator + zipFileName);
//		// File dataBasePath = context.getDatabasePath("zzb.sqlite");
//		File releasePath = context.getFilesDir();
//		File dataDir = new File(releasePath + File.separator + packageBase);
//		if (zipFile.exists() == false) {
//			return false;
//		} else if (zipFile.exists() || deleteAtFirst) {
//			File sqliteFile = new File(sqliteFilePath);
//			if (sqliteFile.exists())
//				sqliteFile.delete();
//
//			File photoDir = new File(releasePath + File.separator + photoPath);
//			File[] photoes = photoDir.listFiles();
//			if (photoes != null) {
//				for (File photo : photoes) {
//					if (photo.isFile())
//						photo.delete();
//				}
//			}
//
//			File videoDir = new File(releasePath + File.separator + videoPath);
//			File[] videoes = videoDir.listFiles();
//			if (videoes != null) {
//				for (File video : videoes) {
//					if (video.isFile())
//						video.delete();
//				}
//			}
//		}
//		dealZIP(zipFile, releasePath);
//		copyFileFromAsset(context, "dataDownload.properties");
//		return true;
//	}
//
//	public static void clearDir(File dir){
//		if(dir==null||dir.exists()==false||dir.isDirectory()==false)
//			return;
//		File[] files=dir.listFiles();
//		for(File f:files){
//			if(f.isFile())
//				f.delete();
//			else
//				clearDir(f);
//		}
//	}
//
//	public static boolean dealOtherFileZip(Context context)
//			throws Exception {
//		initStaticFilePath(context);
//		File zipFile = new File(
//			 	Environment
//						.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//						+ File.separator + otherZipFileName);
//		// File dataBasePath = context.getDatabasePath("zzb.sqlite");
//		File otherFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ File.separator +otherFilePath);
//		if (otherFileDir.exists()==false)
//			otherFileDir.mkdirs();
//		if (zipFile.exists() == false) {
//			return false;
//		} else {
//			clearDir(otherFileDir);
//			dealZIPForOther(zipFile, otherFileDir);
//		}
//		return true;
//	}
//
//
//	private static boolean dealZIP(File fromFile, File toFile) {
//		ZipFile zf = null;
//		InputStream is = null;
//		OutputStream os = null;
//		//
//		try {
//			zf = new ZipFile(fromFile);
//			if (zf.isEncrypted())
//				zf.setPassword("30wish");
//			// zf.setPassword("GZZZB_30SAN");
//			List fileHeaderList = zf.getFileHeaders();
//
//			// Loop through the file headers
//			for (int i = 0; i < fileHeaderList.size(); i++) {
//				FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
//				if (fileHeader.getFileName().indexOf(".sqlite") < 0)
//					zf.extractFile(fileHeader, toFile.getPath());
//			}
//			exDataBase(zf);
//			decryptPhotoes(toFile.getPath() + File.separator
//					+ MyFileUtils.getPhotoPath());
//		} catch (Exception e) {
//			Log.i("ASYNC_TASK", e.getMessage());
//		}
//		fromFile.delete();// ɾ�����ݰ�
//
//		return true;
//	}
//
//	private static boolean dealZIPForOther(File fromFile, File toFile) {
//		boolean isSuccess=true;
//		ZipFile zf = null;
//		InputStream is = null;
//		OutputStream os = null;
//		if(toFile.exists()==false){
//			toFile.mkdirs();
//		}
//		//
//		try {
//			zf = new ZipFile(fromFile);
//			zf.setFileNameCharset("GBK");
//			if (zf.isEncrypted())
//				zf.setPassword("30wish");
//			// zf.setPassword("GZZZB_30SAN");
//			List fileHeaderList = zf.getFileHeaders();
//
//			// Loop through the file headers
//			for (int i = 0; i < fileHeaderList.size(); i++) {
//				FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
//				String fileName=fileHeader.getFileName();
//				if (fileName.endsWith("jpg")||fileName.endsWith("gif")||fileName.endsWith("png"))
//					zf.extractFile(fileHeader, toFile.getPath());
//			}
//		} catch (Exception e) {
//			Log.i("ASYNC_TASK", e.getMessage());
//			isSuccess = false;
//		}
//		fromFile.delete();// ɾ�����ݰ�
//
//		return isSuccess;
//	}
//	private static void decryptPhotoes(String photoDir) {
//		File dir = new File(photoDir);
//		if (dir.exists() && dir.isDirectory()) {
//			File[] files = dir.listFiles();
//			for (File photo : files) {
//				if (photo.isFile() == false)
//					continue;
//				DES des = new DES();
//				des.viewImage(photoDir, photo.getName(), photoDir);
//			}
//		}
//	}
//
//	private static void exDataBase(ZipFile zf) {
//		ZipInputStream is = null;
//		OutputStream os = null;
//
//		try {
//
//			FileHeader fileHeader = zf.getFileHeader(sqliteFilePath);
//
//			if (fileHeader != null) {
//				File dataBaseFolder = new File(dataBasePath);
//				if (dataBaseFolder.exists() == false)
//					dataBaseFolder.mkdirs();
//
//				String outFilePath = dataBasePath
//						+ fileHeader.getFileName().substring(
//								fileHeader.getFileName().lastIndexOf(
//										File.separator));
//				File outFile = new File(outFilePath);
//
//				is = zf.getInputStream(fileHeader);
//				os = new FileOutputStream(outFile);
//
//				int readLen = -1;
//				byte[] buff = new byte[4096];
//
//				while ((readLen = is.read(buff)) != -1) {
//					os.write(buff, 0, readLen);
//				}
//				os.flush();
//				UnzipUtil.applyFileAttributes(fileHeader, outFile);
//
//				System.out.println("Done extracting: "
//						+ fileHeader.getFileName());
//			} else {
//				System.err.println("FileHeader does not exist");
//			}
//		} catch (ZipException e) {
//			e.printStackTrace();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (os != null) {
//					os.close();
//					os = null;
//				}
//				if (is != null) {
//					is.close();
//					is = null;
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
}
