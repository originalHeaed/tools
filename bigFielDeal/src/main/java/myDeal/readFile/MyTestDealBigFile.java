package myDeal.readFile;

public class MyTestDealBigFile {
    public static void main(String[] args) {
        FileDealUtil instance = FileDealUtil.getInstance("D:\\test.txt");
        instance.dealFile(val -> {
            int size = val.size();
            System.out.println("本批次处理了 " + size + "笔记录，将记录进行落表");
        });
    }
}
