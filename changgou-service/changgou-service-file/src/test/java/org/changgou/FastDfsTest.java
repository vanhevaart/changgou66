package org.changgou;

import org.apache.commons.io.IOUtils;
import org.changgou.util.FastDFSClient;
import org.csource.fastdfs.StorageServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/11  21:13
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class FastDfsTest {

    @Test
    public void trackerUrlTest() throws IOException {
        String trackerUrl = FastDFSClient.getTrackerUrl();
        System.out.println(trackerUrl);
    }

    @Test
    public void storeStoragesTest() throws IOException {
        StorageServer[] storeStorages = FastDFSClient.getStorageServer("group1");
        System.out.println("存储组" + Arrays.toString(storeStorages));
    }

    @Test
    public void downloadTest() throws IOException {
        byte[] bytes = FastDFSClient.downFile("group1", "M00/00/00/wKi6hF1RKP-AEOt-AAXpnaejyeI908.png");
        File file = new File("D:\\Java\\fdfs.png");
        IOUtils.write(bytes, new FileWriter(file));
    }

    @Test
    public void deleteTest() throws IOException {
        FastDFSClient.deleteFile("group1","M00/00/00/wKi6hF1RTS2AHP-aAACqz0UiLnk482.jpg");
    }


}
