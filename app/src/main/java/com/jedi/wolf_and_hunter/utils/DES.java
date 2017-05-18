package com.jedi.wolf_and_hunter.utils;

import com.sun.crypto.provider.SunJCE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * DES?1?7?1?7?1?7?1?7
 * 
 * @author Lion
 * 
 */
public class DES {
	private String Algorithm = "DES";

	private KeyGenerator keygen;

	private SecretKey deskey;

	private Cipher c;

	private byte[] cipherByte;

	/**
	 * ?1?7?1?7?0?3?1?7?1?7 DES ?0?6?1?7?1?7
	 */
	public DES() {
		init();
	}

	public void init() {
		Security.addProvider(new SunJCE());
		try {
			// keygen = KeyGenerator.getInstance(Algorithm);
			// deskey = keygen.generateKey();
			// ?1?7?1?7?1?7?1?7?1?7?1?7?1?7?0?3?0?1?1?7?1?7?1?7?1?7key?1?7?1?7?1?7?1?7?1?7?1?7?1?7?0?4?1?7?0?8?1?7?1?7key?0?5?1?7?0?3?1?7?1?7?1?7?1?7?1?7key="zhangsan"
			/**
			 * String key = "zhangsan"; DESKeySpec desKeySpec = new
			 * DESKeySpec(key.getBytes("UTF-8")); SecretKeyFactory keyFactory =
			 * SecretKeyFactory.getInstance("DES"); SecretKey secretKey =
			 * keyFactory.generateSecret(desKeySpec);
			 */
			// ?1?7?0?4?1?7?0?8?1?7?1?7key
			DESKeySpec desKeySpec;
			try {
				String key = "gzzzbSystemzhangjiexiangyongsong";
				desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
				SecretKeyFactory keyFactory = SecretKeyFactory
						.getInstance("DES");
				deskey = keyFactory.generateSecret(desKeySpec);
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			c = Cipher.getInstance(Algorithm);
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		} catch (NoSuchPaddingException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * ?1?7?1?7?1?7?1?8?1?7?1?7?1?7?1?7?1?7?0?4?1?7
	 * 
	 * @param filePath
	 *            ?1?7?0?4?1?7?1?7?1?7?1?7?0?4?1?7?1?7?1?7???1?7?1?7
	 * @param fileName
	 *            ?1?7?0?4?1?7?1?7?1?7
	 */
	public void createFile(String filePath, String fileName) {
		try {
			c.init(Cipher.ENCRYPT_MODE, deskey);
			// ?1?7?1?7?1?7?0?4?1?7?1?7?1?7???1?7?1?7?1?7?1?7?1?7?0?4?1?7?1?7?1?7?1?7?1?7?1?7?0?0?1?7?0?3?1?7?1?7?0?4?1?7
			File file = new File(filePath + "/" + fileName);
			FileInputStream fileinputstream = null;
			FileOutputStream fileoutputStream = null;
			// ?1?7?1?7?1?7
			if (file.exists()) {
				String newFileName = fileName.substring(0,
						fileName.lastIndexOf("."));
				fileinputstream = new FileInputStream(file);
				long len = file.length();

				byte abyte0[] = new byte[Integer.parseInt(Long.toString(len))];
				File newFile = new File(filePath + "/" + newFileName);
				if (!newFile.exists()) {
					newFile.createNewFile();
				}

				fileoutputStream = new FileOutputStream(newFile);

				fileinputstream.read(abyte0, 0,
						Integer.parseInt(Long.toString(len)));

				fileoutputStream.write(createEncryptor(abyte0));
			}
			if (fileinputstream != null) {
				fileinputstream.close();
			}
			if (fileoutputStream != null) {
				fileoutputStream.close();
			}
			file.delete();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * ?1?7?1?7?1?7?1?8?1?7?1?7?1?7?1?7?1?7?0?4?1?7
	 * 
	 * @param filePath
	 *            ?1?7?0?4?1?7?1?7?1?7?1?7?0?4?1?7?1?7?1?7???1?7?1?7
	 * @param fileName
	 *            ?1?7?0?4?1?7?1?7?1?7
	 * @param savaPath
	 *            ?1?7?1?7?1?7?1?7?1?7?1?7?0?4?1?7?1?7?1?7???1?7?1?7
	 */
	public void createFile(String filePath, String fileName, String savaPath) {
		try {
			c.init(Cipher.ENCRYPT_MODE, deskey);
			// ?1?7?1?7?1?7?0?4?1?7?1?7?1?7???1?7?1?7?1?7?1?7?1?7?0?4?1?7?1?7?1?7?1?7?1?7?1?7?0?0?1?7?0?3?1?7?1?7?0?4?1?7
			File file = new File(filePath + "/" + fileName);
			FileInputStream fileinputstream = null;
			FileOutputStream fileoutputStream = null;
			// ?1?7?1?7?1?7
			if (file.exists()) {
				String newFileName = fileName.substring(0,
						fileName.lastIndexOf("."));
				fileinputstream = new FileInputStream(file);
				long len = file.length();

				byte abyte0[] = new byte[Integer.parseInt(Long.toString(len))];
				File newFile = new File(savaPath + "/" + newFileName);
				if (!newFile.exists()) {
					newFile.createNewFile();
				}

				fileoutputStream = new FileOutputStream(newFile);

				fileinputstream.read(abyte0, 0,
						Integer.parseInt(Long.toString(len)));

				fileoutputStream.write(createEncryptor(abyte0));
			}
			if (fileinputstream != null) {
				fileinputstream.close();
			}
			if (fileoutputStream != null) {
				fileoutputStream.close();
			}
			file.delete();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * ?1?7?1?7?1?7?1?7?1?7?1?7?0?3?0?0?0?2
	 * 
	 * @param filePath
	 *            ?1?7?0?4?1?7?1?7?1?7?1?7?0?4?1?7?1?7?1?7???1?7?1?7
	 * @param fileName
	 *            ?1?7?0?4?1?7?1?7?1?7
	 * @param savaPath
	 *            ?1?7?1?7?1?7?1?7?1?7?1?7?0?4?1?7?1?7?1?7???1?7?1?7
	 */
	public void encryptorPersonPhoto(String filePath, String fileName,
			String savaPath) {
		try {
			c.init(Cipher.ENCRYPT_MODE, deskey);
			// ?1?7?1?7?1?7?0?4?1?7?1?7?1?7???1?7?1?7?1?7?1?7?1?7?0?4?1?7?1?7?1?7?1?7?1?7?1?7?0?0?1?7?0?3?1?7?1?7?0?4?1?7
			File file = new File(filePath + "/" + fileName);
			FileInputStream fileinputstream = null;
			FileOutputStream fileoutputStream = null;
			// ?1?7?1?7?1?7
			if (file.exists()) {
				String newFileName = fileName.substring(0,
						fileName.lastIndexOf("."));
				fileinputstream = new FileInputStream(file);
				long len = file.length();

				byte abyte0[] = new byte[Integer.parseInt(Long.toString(len))];
				File newFile = new File(savaPath + "/" + newFileName);
				if (!newFile.exists()) {
					newFile.createNewFile();
				}

				fileoutputStream = new FileOutputStream(newFile);

				fileinputstream.read(abyte0, 0,
						Integer.parseInt(Long.toString(len)));

				fileoutputStream.write(createEncryptor(abyte0));
			}
			if (fileinputstream != null) {
				fileinputstream.close();
			}
			if (fileoutputStream != null) {
				fileoutputStream.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * ?1?7?1?7?1?7?1?8?1?7?1?7?1?7?1?7?1?7?0?4?1?7
	 * 
	 * @param filePath
	 *            ?1?7?0?4?1?7?1?7?1?7?1?7?0?4?1?7?1?7?1?7???1?7?1?7
	 * @param fileName
	 *            ?1?7?0?4?1?7?1?7?1?7
	 */
	public void viewImage(String filePath, String fileName, String savaPath) {
		try {
			c.init(Cipher.ENCRYPT_MODE, deskey);
			File file = new File(filePath + "/" + fileName);
			FileInputStream fileinputstream = null;
			FileOutputStream fileoutputStream = null;
			if (file.exists()) {
				String newFileName = fileName + ".jpg";
				fileinputstream = new FileInputStream(file);
				long len = file.length();

				byte abyte0[] = new byte[Integer.parseInt(Long.toString(len))];
				File newFile = new File(savaPath + "/" + newFileName);
				if (!newFile.exists()) {
					newFile.createNewFile();
				}

				fileoutputStream = new FileOutputStream(newFile);

				fileinputstream.read(abyte0, 0,
						Integer.parseInt(Long.toString(len)));

				fileoutputStream.write(createDecryptor(abyte0));
			}
			if (fileinputstream != null) {
				fileinputstream.close();
			}
			if (fileoutputStream != null) {
				fileoutputStream.close();
			}
			file.delete();
			// file.delete();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * ?1?7?1?7 String ?1?7?1?7?1?7??1?7?1?7?1?7
	 * 
	 * @param str
	 *            ?0?8?1?7?1?7?1?7?1?9?1?7?1?7?1?7?1?7?1?7
	 * @return ?1?7?1?7?1?7?1?0?1?7?1?7?1?4?1?7?1?7 byte ?1?7?1?7?1?7?1?7
	 */
	public byte[] createEncryptor(byte[] str) {
		try {
			c.init(Cipher.ENCRYPT_MODE, deskey);
			cipherByte = c.doFinal(str);
		} catch (InvalidKeyException ex) {
			ex.printStackTrace();
		} catch (javax.crypto.BadPaddingException ex) {
			ex.printStackTrace();
		} catch (javax.crypto.IllegalBlockSizeException ex) {
			ex.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cipherByte;
	}

	/**
	 * ?1?7?1?7 Byte ?1?7?1?7?1?7?1?7?1?7?1?7??1?7?1?7?1?7
	 * 
	 * @param buff
	 *            ?0?8?1?7?1?7?1?7?1?9?1?7?1?7?1?7?1?7?1?7
	 * @return ?1?7?1?7?1?7?1?0?1?7?1?7?1?4?1?7?1?7 String
	 */
	public byte[] createDecryptor(byte[] buff) {
		try {
			c.init(Cipher.DECRYPT_MODE, deskey);
			cipherByte = c.doFinal(buff);
		} catch (InvalidKeyException ex) {
			ex.printStackTrace();
		} catch (javax.crypto.BadPaddingException ex) {
			ex.printStackTrace();
		} catch (javax.crypto.IllegalBlockSizeException ex) {
			ex.printStackTrace();
		}
		return (cipherByte);
	}

}
