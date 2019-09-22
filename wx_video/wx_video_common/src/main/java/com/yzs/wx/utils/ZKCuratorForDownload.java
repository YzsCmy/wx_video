package com.yzs.wx.utils;

import com.yzs.wx.enums.BGMOperatorTypeEnum;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

@Component
public class ZKCuratorForDownload {


    private CuratorFramework client = null;

    final static Logger log = LoggerFactory.getLogger(ZKCuratorForDownload.class);
    @Value("${zookeeper.server.url}")
    public  String ZOOKEEPER_SERVER;
    @Value("${bgm.filepath.prefix}")
    public  String bgmPrefix;
    @Value("${bgm.server.prefix}")
    public  String bgmserverPrefix;

    public void init() {
        if (client != null) {
            return;
        }

        //创建重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 5);

        //创建zookeeper客户端
        client = CuratorFrameworkFactory.builder().connectString(ZOOKEEPER_SERVER)
                .sessionTimeoutMs(10000)
                .retryPolicy(retryPolicy)
                .namespace("admin")
                .build();

        client.start();

        try {
           addChildWatch("/bgm");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void addChildWatch(String nodePath) throws Exception {

        final PathChildrenCache cache = new PathChildrenCache(client, nodePath, true);
        cache.start();
        cache.getListenable().addListener(new PathChildrenCacheListener() {

            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event)
                    throws Exception {

                if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
                    log.info("监听到事件 CHILD_ADDED");

                    // 1. 从zookeeper查询bgm对象，获取路径path
                    String path = event.getData().getPath();
                    String operatorObjStr = new String(event.getData().getData());
                    Map<String, String> map = JsonUtils.jsonToPojo(operatorObjStr, Map.class);
                    String operatorType = map.get("operType");
                    String songPath = map.get("path");

                    // 2. 定义保存到本地的bgm路径
                    String filePath = bgmPrefix + songPath;

                    // 3. 定义下载的路径（播放url）
                    String arrPath[] = songPath.split("\\\\");
                    String finalPath = "";
                    // 3.1 处理url的斜杠以及编码
                    for(int i = 0; i < arrPath.length ; i ++) {
                        if (StringUtils.isNotBlank(arrPath[i])) {
                            finalPath += "/";
                            finalPath += URLEncoder.encode(arrPath[i], "UTF-8") ;
                        }
                    }
//					String bgmUrl = "http://192.168.1.2:8080/mvc" + finalPath;
                    String bgmUrl = bgmserverPrefix + finalPath;

                    if (operatorType.equals(BGMOperatorTypeEnum.ADD.type)) {
                        // 下载bgm到spingboot服务器
                        URL url = new URL(bgmUrl);
                        File file = new File(filePath);
                        FileUtils.copyURLToFile(url, file);
                        client.delete().forPath(path);
                    } else if (operatorType.equals(BGMOperatorTypeEnum.DELETE.type)) {
                        File file = new File(filePath);
                        FileUtils.forceDelete(file);
                        client.delete().forPath(path);
                    }
                }
            }
        });
    }


}