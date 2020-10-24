package el.util;

import java.io.*;

public class FileUtils {
    /**
     * make sure file exist
     *
     * @param addr file addr
     * @throws IOException
     */
    public static void createFileIfNotExist(String addr) throws IOException {
        File file = new File(addr);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    /**
     * read the content from the file
     *
     * @param filename file name
     * @return
     */
    public static String readFromFile(String filename) {
        String ret = "";
        File file = new File(filename);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            StringBuffer buffer = new StringBuffer();
            while ((tempString = reader.readLine()) != null) {
                buffer.append(tempString);
            }
            ret = buffer.toString();
            reader.close();
        } catch (IOException e) {
            //e.printStackTrace();  
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return ret;
    }

    /**
     * write data to file
     *
     * @param addr file addr
     * @param data data content
     * @throws IOException
     */
    public static void writeToFile(String addr, String data) throws IOException {
        FileWriter fileWriter = new FileWriter(addr);
        fileWriter.write(data);
        fileWriter.flush();
        fileWriter.close();
    }

}
