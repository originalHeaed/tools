package myDeal.readFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MyTestGenBigFile {
    public static void main(String[] args) throws IOException {
        File file = new File("D:\\test.txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        /* 生成一个大文件 */
        for (int i = 0; i < 500000000; i++) {
            bufferedWriter.write("121212112");
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
        System.out.println("累计写入：" + 1000000000 + "行记录");
    }
}
