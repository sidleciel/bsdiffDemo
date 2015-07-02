package com.rhodes.bsdiffdemo.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by xiet on 2015/7/1.
 */
public class DigestTool {

    /**
     * ��������G����ļ�
     */
    public static String getFileSha1(String path) throws OutOfMemoryError, IOException {
        File file = new File(path);
        FileInputStream in = new FileInputStream(file);
        MessageDigest messagedigest;
        try {
            messagedigest = MessageDigest.getInstance("SHA-1");

            byte[] buffer = new byte[1024 * 1024 * 10];
            int len = 0;

            while ((len = in.read(buffer)) > 0) {
                //�ö���ͨ��ʹ�� update����������������
                messagedigest.update(buffer, 0, len);
            }

            //���ڸ��������ĸ������ݣ�digest ����ֻ�ܱ�����һ�Ρ��ڵ��� digest ֮��MessageDigest �����������ó����ʼ״̬��
            return byteArrayToHex(messagedigest.digest());
        } catch (NoSuchAlgorithmException e) {
            Logger.log("getFileSha1->NoSuchAlgorithmException###" + e.toString());
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            Logger.log("getFileSha1->OutOfMemoryError###" + e.toString());
            e.printStackTrace();
            throw e;
        } finally {
            in.close();
        }
        return null;
    }

    public static String byteArrayToHex(byte[] byteArray) {

        // ���ȳ�ʼ��һ���ַ����飬�������ÿ��16�����ַ�
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        // newһ���ַ����飬�������������ɽ���ַ����ģ�����һ�£�һ��byte�ǰ�λ�����ƣ�Ҳ����2λʮ�������ַ���2��8�η�����16��2�η�����
        char[] resultCharArray = new char[byteArray.length * 2];

        // �����ֽ����飬ͨ��λ���㣨λ����Ч�ʸߣ���ת�����ַ��ŵ��ַ�������ȥ
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        // �ַ�������ϳ��ַ�������

        return new String(resultCharArray);
    }

    public static String stringMD5(String input) {
        try {
            // �õ�һ��MD5ת�����������ҪSHA1�������ɡ�SHA1����
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");

            // ������ַ���ת�����ֽ�����
            byte[] inputByteArray = input.getBytes();

            // inputByteArray�������ַ���ת���õ����ֽ�����
            messageDigest.update(inputByteArray);

            // ת�������ؽ����Ҳ���ֽ����飬����16��Ԫ��
            byte[] resultByteArray = messageDigest.digest();

            // �ַ�����ת�����ַ�������
            return byteArrayToHex(resultByteArray);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
