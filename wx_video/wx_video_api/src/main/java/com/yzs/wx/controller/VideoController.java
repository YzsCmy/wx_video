package com.yzs.wx.controller;

import com.yzs.wx.pojo.Comments;
import com.yzs.wx.pojo.Videos;
import com.yzs.wx.service.BgmService;
import com.yzs.wx.service.VideoService;
import com.yzs.wx.utils.FetchVideoCover;
import com.yzs.wx.utils.MergeVideoMp3;
import com.yzs.wx.utils.PagedResult;
import com.yzs.wx.utils.WXJSONResult;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;

@RestController
@RequestMapping("/video")
public class VideoController {

    //文件路径前缀
    @Value("${image.filepath.prefix}")
    private String prefix;
    @Value("${ffmpeg.path}")
    private String ffmpegexe;
    @Value("${video.pagesize}")
    private Integer PAGE_SIZE;


    @Autowired
    private BgmService bgmService;
    @Autowired
    private VideoService videoService;

    //上传视频
    @PostMapping("/upload")
    public WXJSONResult uploadVideo(Videos video, @RequestParam("file") MultipartFile file)throws Exception{
        if(StringUtils.isBlank(video.getUserId())){
            return WXJSONResult.errorMsg("userid不能为空！");
        }
        if(file==null||StringUtils.isBlank(file.getOriginalFilename())){
            return WXJSONResult.errorMsg("视频不能为空！");
        }
        String path = video.getUserId()+"/video/";
        String coverPathDB = "/" + video.getUserId() + "/video/";

        FileOutputStream outputStream=null;
        InputStream inputStream = null;

        String sqlpath="";
        String finalVideoPath="";
        try {

            //保存视频
            sqlpath = saveVideo(path,outputStream,inputStream,file);

            finalVideoPath = prefix+sqlpath;

            String arrayFilenameItem[] =  file.getOriginalFilename().split("\\.");
            String fileNamePrefix = "";
            for (int i = 0 ; i < arrayFilenameItem.length-1 ; i ++) {
                fileNamePrefix += arrayFilenameItem[i];
            }
            coverPathDB = coverPathDB + fileNamePrefix + ".jpg";


        }catch (Exception e){

            e.printStackTrace();
            return WXJSONResult.errorMsg("上传出错...");
        }finally {
            if(outputStream!=null){
                outputStream.flush();
                outputStream.close();
            }
        }
        //用户选择了背景音乐
        if(StringUtils.isNotBlank(video.getAudioId())){

            String bgmpath = bgmService.queryBgmPathById(video.getAudioId());

            MergeVideoMp3 tool = new MergeVideoMp3(ffmpegexe);
            String finalBgmpath = prefix+bgmpath;

            String videoInputPath = prefix+sqlpath;
            String videoOutputName = UUID.randomUUID().toString() + ".mp4";
            sqlpath = path+videoOutputName;
            finalVideoPath = prefix + path +videoOutputName;
            //合并音视频
            System.out.println("videoInputPath===="+videoInputPath);
            System.out.println("video.getVideoSeconds()===="+video.getVideoSeconds());
            System.out.println("bgm路径===="+finalBgmpath);
            tool.convertor(videoInputPath,finalBgmpath,video.getVideoSeconds(),finalVideoPath);

        }
        System.out.println("uploadPathDB=" + sqlpath);
        System.out.println("finalVideoPath=" + finalVideoPath);

        // 对视频进行截图
        FetchVideoCover videoInfo = new FetchVideoCover(ffmpegexe);
        videoInfo.getCover(finalVideoPath, prefix + coverPathDB);

        video.setCoverPath(coverPathDB);

        //添加video数据
        String videoId = videoService.uploadVideo(video,sqlpath);

        return WXJSONResult.ok(videoId);

    }
    
    //上传视频封面
    @PostMapping(value="/uploadCover")
    public WXJSONResult uploadCover(String userId,String videoId, @RequestParam("file") MultipartFile file) throws Exception {

        if (StringUtils.isBlank(videoId) || StringUtils.isBlank(userId)) {
            return WXJSONResult.errorMsg("视频主键id和用户id不能为空...");
        }


        // 保存到数据库中的相对路径
        String uploadPathDB = userId + "/video/";

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        // 文件上传的最终保存路径
        String finalCoverPath = "";
        try {
            if (file != null) {

                String fileName = file.getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)) {

                    finalCoverPath = prefix + uploadPathDB + fileName;
                    // 设置数据库保存的路径
                    uploadPathDB = "/" +uploadPathDB+ fileName;

                    File outFile = new File(finalCoverPath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        // 创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }

            } else {
                return WXJSONResult.errorMsg("上传出错...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return WXJSONResult.errorMsg("上传出错...");
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        videoService.updateVideo(videoId, uploadPathDB);

        return WXJSONResult.ok();
    }

    //isSaveRecord( 1: 保存搜索记录，0：不保存搜索记录)
    @PostMapping(value="/showAll")
    public WXJSONResult showAll(@RequestBody Videos video, Integer isSaveRecord,
                                   Integer page, Integer pageSize) throws Exception {

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = PAGE_SIZE;
        }

        PagedResult result = videoService.getAllVideos(video, isSaveRecord, page, pageSize);
        return WXJSONResult.ok(result);
    }

    /**
     * @Description: 我关注的人发的视频
     */
    @PostMapping("/showMy")
    public WXJSONResult showMyList(String userId, Integer page) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return WXJSONResult.ok();
        }

        if (page == null) {
            page = 1;
        }

        int pageSize = 6;

        PagedResult videosList = videoService.queryMy(userId, page, pageSize);

        return WXJSONResult.ok(videosList);
    }


    /**
     * @Description: 我关注的人发的视频
     */
    @PostMapping("/showMyFollow")
    public WXJSONResult showMyFollow(String userId, Integer page) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return WXJSONResult.ok();
        }

        if (page == null) {
            page = 1;
        }

        int pageSize = 6;

        PagedResult videosList = videoService.queryMyFollowVideos(userId, page, pageSize);

        return WXJSONResult.ok(videosList);
    }

    /**
     * @Description: 我收藏(点赞)过的视频列表
     */
    @PostMapping("/showMyLike")
    public WXJSONResult showMyLike(String userId, Integer page, Integer pageSize) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return WXJSONResult.ok();
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = 6;
        }

        PagedResult videosList = videoService.queryMyLikeVideos(userId, page, pageSize);

        return WXJSONResult.ok(videosList);
    }

    @PostMapping(value="/hot")
    public WXJSONResult hot() throws Exception {
        return WXJSONResult.ok(videoService.getHotwords());
    }

    @PostMapping(value="/userLike")
    public WXJSONResult userLike(String userId, String videoId, String videoCreaterId)
            throws Exception {
        videoService.userLikeVideo(userId, videoId, videoCreaterId);
        return WXJSONResult.ok();
    }

    @PostMapping(value="/userUnLike")
    public WXJSONResult userUnLike(String userId, String videoId, String videoCreaterId) throws Exception {
        videoService.userUnLikeVideo(userId, videoId, videoCreaterId);
        return WXJSONResult.ok();
    }

    @PostMapping(value="/query")
    public WXJSONResult query(String videoId) throws Exception {

        Videos video = videoService.query(videoId);
        return WXJSONResult.ok(video);
    }

    @PostMapping("/saveComment")
    public WXJSONResult saveComment(@RequestBody Comments comment,
                                       String fatherCommentId, String toUserId) throws Exception {

        comment.setFatherCommentId(fatherCommentId);
        comment.setToUserId(toUserId);

        videoService.saveComment(comment);
        return WXJSONResult.ok();
    }

    @PostMapping("/getVideoComments")
    public WXJSONResult getVideoComments(String videoId, Integer page, Integer pageSize) throws Exception {

        if (StringUtils.isBlank(videoId)) {
            return WXJSONResult.ok();
        }

        // 分页查询视频列表，时间顺序倒序排序
        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = 10;
        }

        PagedResult list = videoService.getAllComments(videoId, page, pageSize);

        return WXJSONResult.ok(list);
    }


    @PostMapping("/getVideoFirstComments")
    public WXJSONResult getVideoFirstComments(String videoId, Integer page, Integer pageSize) throws Exception {

        if (StringUtils.isBlank(videoId)) {
            return WXJSONResult.ok();
        }

        // 分页查询视频列表，时间顺序倒序排序
        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = 10;
        }

        PagedResult list = videoService.getFirstComments(videoId, page, pageSize);

        return WXJSONResult.ok(list);
    }

    @PostMapping("/getVideoSecondComments")
    public WXJSONResult getVideoSecondComments(String videoId, String fatherCommentId) throws Exception {

        if (StringUtils.isBlank(videoId)) {
            return WXJSONResult.ok();
        }

        //// 分页查询视频列表，时间顺序倒序排序
        //if (page == null) {
        //    page = 1;
        //}
        //
        //if (pageSize == null) {
        //    pageSize = 10;
        //}

        PagedResult list = videoService.getSecondComments(videoId, fatherCommentId);

        return WXJSONResult.ok(list);
    }


    //保存视频
    private String saveVideo(String path, FileOutputStream outputStream,InputStream inputStream,MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename();

        String sqlpath = path+filename;
        String finalpath = prefix+path+filename;
        File savefile = new File(finalpath);
        if(savefile.getParentFile()!=null||!savefile.getParentFile().isDirectory()){
            savefile.getParentFile().mkdirs();
        }
        outputStream = new FileOutputStream(finalpath);

        inputStream = file.getInputStream();
        IOUtils.copy(inputStream,outputStream);

        return sqlpath;
    }

}
