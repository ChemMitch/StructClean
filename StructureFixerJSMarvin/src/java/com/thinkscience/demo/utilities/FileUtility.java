/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thinkscience.demo.utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.Base64;
/**
 *
 * @author Mitch
 */
public class FileUtility
{
	public static String saveTemporaryMolfile(String molfile)
	{
		try
		{
			File temp = File.createTempFile("temp-file-name", ".mol"); 
			BufferedWriter writer = new BufferedWriter(new FileWriter(temp.getAbsoluteFile()));
			writer.write(molfile);
			writer.close();
			return temp.getAbsolutePath();
		}
		catch(IOException iox)
		{
			iox.printStackTrace();
		}
		return "";
	}
	
	 public static byte[] a(String str) throws Exception {
        if (str == null || str.length() == 0) {
            return null;
        }
        System.out.println("String length : " + str.length());
        ByteArrayOutputStream obj=new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(obj);
        gzip.write(str.getBytes("UTF-8"));
        gzip.close();
        return obj.toByteArray();
        //String outStr = obj.toString("UTF-8");
        //System.out.println("Output String length : " + outStr.length());
        //return outStr;
     }

public static String base64Encode(byte[] data)
{
	return Base64.getEncoder().encodeToString(data);
}

}
