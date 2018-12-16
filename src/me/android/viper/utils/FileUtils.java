package me.android.viper.utils;

import java.io.*;

/**
 * Created by luguanquan on 2018/12/13.
 */
public class FileUtils {

    public static void writeJavaFile(String content, String classPath, String className){
        writeToFile(content, classPath, className, ".java");
    }

    public static void writeXmlFile(String content, String classPath, String className){
        writeToFile(content, classPath, className, ".xml");
    }

    private static void writeToFile(String content, String classPath, String className, String fileType) {
        try {
            File dir = new File(classPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(classPath + "/" + className + fileType);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String streamToString(InputStream in) {
        String content = "";
        try {
            content = new String(readStream(in));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }


    public static byte[] readStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        try {
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            outputStream.close();
            inputStream.close();
        }

        return outputStream.toByteArray();
    }

}
