package org.changgou.util;

import org.apache.commons.io.FilenameUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Arrays;

/**
 * Author:  HZ
 * <p> FastDFS客户端
 * Create:  2019/8/10  21:33
 */
public class FastDFSClient {

    private static Logger logger = LoggerFactory.getLogger(FastDFSClient.class);

    /*
     * 加载配置文件初始化
     */
    static {
        String path = new ClassPathResource("fdfs_client.conf").getPath();
        try {
            ClientGlobal.init(path);
        } catch (IOException | MyException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取代表了trackerServer的对象
     *
     * @return trackerServer对象
     */
    public static TrackerServer getTrackerServer() {
        // 通过加载过的配置文件,创建tracker客户端
        TrackerClient trackerClient = new TrackerClient();
        // 通过客户端和加载或的配置文件信息,获取trackerServer
        try {
            return trackerClient.getConnection();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("FastDFS Client Init Fail!", e);
        }
        return null;
    }

    /**
     * 获取StorageClient客户端
     *
     * @return StorageClient客户端
     */
    public static StorageClient getStorageClient() {
        TrackerServer trackerServer = getTrackerServer();
        // 根据trackerServer获取StorageClient客户端
        return new StorageClient(trackerServer, null);
    }

    /**
     * 获取trackerServer的地址,包括端口号
     *
     * @return trackerServer的ip加端口地址
     */
    public static String getTrackerUrl() {
        TrackerServer trackerServer = getTrackerServer();
        String hostAddress = trackerServer.getInetSocketAddress().getAddress().getHostAddress();
        int tracker_http_port = ClientGlobal.getG_tracker_http_port();
        return "http://" + hostAddress + ":" + tracker_http_port+"/";
    }

    /**
     * 上传文件
     *
     * @param file 文件对象
     * @return 上传后服务器返回的信息, 包含了组名和文件完整名
     */
    public static String[] upload(@NotNull MultipartFile file) {
        String[] uploadInfo = null;
        try {
            // 获取文件原名
            String filename = file.getOriginalFilename();
            // 根据文件原名,调用file-upload工具包提供的工具类获取文件扩展名
            String extension = FilenameUtils.getExtension(filename);
            // 获取文件数据
            byte[] bytes = file.getBytes();
            StorageClient storageClient = getStorageClient();
            // 上传文件
            uploadInfo = storageClient.upload_file(bytes, extension, null);
        } catch (IOException | MyException e) {
            e.printStackTrace();
            logger.error("Exception when uploading the file:" + file.getOriginalFilename(), e);
        }
        System.out.println(Arrays.toString(uploadInfo));
        return uploadInfo;
    }

    /**
     * 根据组名和完整文件名下载文件
     * @param groupName 组名
     * @param filename 完整文件名
     * @return 返回该文件的字节数组
     */
    public static byte[] downFile(String groupName, String filename) {
        StorageClient storageClient = getStorageClient();
        byte[] bytes = null;
        try {
            // 下载文件,返回字节数组
            bytes = storageClient.download_file(groupName, filename);
        } catch (IOException | MyException e) {
            e.printStackTrace();
            logger.error("Exception: Get File from Fast DFS failed", e);
        }
        return bytes;
    }

    /**
     * 删除文件
     * @param groupName 组名
     * @param filename 完整文件名
     */
    public static void deleteFile(String groupName, String filename) {
        StorageClient storageClient = getStorageClient();
        try {
            storageClient.delete_file(groupName, filename);
        } catch (IOException | MyException e) {
            e.printStackTrace();
            logger.error("Exception: Delete File from Fast DFS failed", e);
        }
    }

    /**
     * 根据组名获取 该组的存储服务器数组 (该方法主要是获取存储服务器信息,用于上传)
     *
     * @param groupName 组名
     * @return 该组存储服务器数组
     * @throws IOException 连接异常
     */
    public static StorageServer[] getStorageServer(String groupName) throws IOException {
        //创建TrackerClient
        TrackerServer trackerServer = getTrackerServer();
        //获取TrackerServer
        TrackerClient trackerClient = new TrackerClient();
        //获取Storage数组
        return trackerClient.getStoreStorages(trackerServer, groupName);
    }

    /**
     * 根据组名,文件名 获取包含了存储服务器信息的ServerInfo数组(主要是获取存储服务器信息,用于下载)
     *
     * @param groupName 组名
     * @param filename  完整文件名
     * @return 包含了存储服务器信息的ServerInfo数组
     * @throws IOException 连接异常
     */
    public static ServerInfo[] getServerInfo(String groupName, String filename) throws IOException {
        //创建TrackerClient
        TrackerServer trackerServer = getTrackerServer();
        //获取TrackerServer
        TrackerClient trackerClient = new TrackerClient();
        // 获取ServerInfo数组
        return trackerClient.getFetchStorages(trackerServer, groupName, filename);
    }

    /**
     * 根据组名和文件完成名获取文件相关信息
     *
     * @param groupName 组名
     * @param filename  文件完整名
     * @return 包含文件信息的FileInfo对象
     */
    public static FileInfo getFileInfo(String groupName, String filename) throws IOException, MyException {
        StorageClient storageClient = getStorageClient();
        return storageClient.get_file_info(groupName, filename);
    }
}
