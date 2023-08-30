package myDeal.writeFile;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class BufferWriteUtil {
    /**
     * 写缓冲对象
     */
    private BufferedWriter bufferedWriter;

    /**
     * 文件内容的编码
     */
    private Charset charset;

    public static BufferWriteUtil getBufferWriteUtil(String path) throws FileNotFoundException {
        return new BufferWriteUtil(path, StandardCharsets.UTF_8);
    }

    public static BufferWriteUtil getBufferWriteUtil(String path, String charSet) throws FileNotFoundException {
        return new BufferWriteUtil(path, Charset.forName(charSet));
    }

    private BufferWriteUtil(String path, Charset charset) throws FileNotFoundException {
        this.charset = charset;
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), this.charset), 819200);
    }

    /**
     * 将数据写入文件中
     * @param content
     */
    public void write(List<String> content) {
        if (content == null || content.size() == 0) return;
        content.forEach(ele -> {
            try {
                bufferedWriter.write(ele);
            } catch (IOException e) {
                System.out.println("将数据写入文件失败");
            }
        });
    }

    /**
     * 释放资源
     */
    public void close() {
        try {
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        } catch (Exception e) {}
    }

}
