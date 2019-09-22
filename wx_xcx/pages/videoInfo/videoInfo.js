var videoUtil = require('../../utils/videoUtil.js')

const app = getApp()

Page({
  data: {
    btnShow:true,
    searchContent:null,
    requestUrl:'',//发起获取视频列表请求的url
    windowHeight:0,
    animationShow: false,//动画显示状态
    isonload: false,
    isMe: false,
    isfans: false,
    userLikeVideo: false,
    likeNum: 0,
    cover: "cover",
    percent: 1,
    isPause: false,
    serverUrl: '',
    publisher: {},

    videosData: [],
    page: 1,//当前页、
    totalPage:1,
    currentIndex: 0, //当前视频在videosData的index
    videoId: '',
    src: '',
    videoInfo: {},

    animation:{},
    touchStartingY: 0,
    touchStartTime: 0,
    currentTranslateY:0,

    //评论
    commentsPage: 1,//当前页
    commentsTotalPage: 1,//总页数
    commentsList: [],//评论数据数组
    secondCommentsList:[],//二级评论数据
    commentTotalRecords:0,//总记录数

    placeholder: "说点什么...",

    hasmoreData:true,
    hiddenloading:true
    
  },
  videoCtx: {},
  onLoad: function(params) {
    this.setData({
      isonload: true
    })

    this.videoChange = throttle(this.touchEndHandler, 200)
    console.log("params=====>" + JSON.stringify(params))
    var me = this
   

    // me.videoCtx = wx.createVideoContext("myVideo", this)
    //作为首页 TODO
    if ((JSON.stringify(params) == '{}')) {

      // var searchContent = params.search;
      // var isSaveRecord = params.isSaveRecord;
      var isSaveRecord = 0;
      if (isSaveRecord == undefined||isSaveRecord == null || isSaveRecord == '') {
        isSaveRecord = 0;
      }

      // me.setData({
      //   searchContent: searchContent
      // });

      // 获取当前的分页数
      var page = me.data.page;
      me.getAllVideoList(page, isSaveRecord,'');
      
    
    } else {
      //获取上一个页面传过来的数据

      console.log('<=========>params<=========>',params)
      if(params.ispost!=undefined&&params.ispost==1){
        me.setData({
          searchContent: params.videoDesc
        })
      }
      var requestUrl = me.parseUrl(params.requestUrl)
      console.log(requestUrl)
      var head = requestUrl.indexOf("&page=")
      var tail = requestUrl.indexOf("&pageSize")
      var currentPage = parseInt(requestUrl.substring(head+6,tail))
      currentPage = Number(currentPage)
      console.log(currentPage)
      me.setData({
        requestUrl: requestUrl,
        page:currentPage
      })

      var datas = JSON.parse(params.videoInfo)
      var serverUrl = app.serverUrl
      me.setData({
        serverUrl: serverUrl,
        videosData: datas,
        currentIndex: params.index,
        videoInfo: datas[params.index],
        src: serverUrl + '/' + datas[params.index].videoPath,
        videoId: datas[params.index].id,
        likeNum: datas[params.index].likeCounts

      })
      //TODO初始化评论
      me.initComment()
      me.fixedScreen()
      me.queryPublisher()
      
    }
    
    
  },

  initComment(){

    console.log('=================initComment================')
    var me = this
    // debugger
    me.getCommentsList(1);
    // me.setData({
    //   commnetNum: me.data.commentTotalRecords
    // })

  },

  parseUrl: function (redirectUrl) {

    if (redirectUrl != undefined && redirectUrl != null && redirectUrl != '') {
      redirectUrl = redirectUrl.replace(/#/g, "?");
      redirectUrl = redirectUrl.replace(/@/g, "=");
      redirectUrl = redirectUrl.replace(/\*/g, "&");

      return redirectUrl;
    }
  },

  //视频页面自适应处理
  fixedScreen:function(){
    var me = this;
    if (me.data.videoInfo.videoWidth > me.data.videoInfo.videoHeight) {
      me.setData({
        cover: ""
      })
    } else {
      me.setData({
        cover: "cover"
      })
    }
  },
  //isSaveRecord( 1: 保存搜索记录，0：不保存搜索记录)
  getAllVideoList: function (page, isSaveRecord,searchContent,requestUrl) {
    var me = this;
    var serverUrl = app.serverUrl;
    wx.showLoading({
      title: '请等待，加载中...',
    });
    var url = serverUrl + '/video/showAll?page=' + page + "&isSaveRecord=" + isSaveRecord
    // if(searchContent==undefined||searchContent==null||searchContent.trim()==''){
    //   searchContent = '';
    // }
    if (requestUrl != undefined && requestUrl != null && requestUrl != ''){
      var num = requestUrl.indexOf('&page')
      url = requestUrl.substring(0, num) + "&page=" + page + '&pageSize=6'
    }
    // var searchContent = me.data.searchContent;

    if(searchContent!=null){
      console.log('3333333333333')

      wx.request({
        url: url,
        method: "POST",
        data: {
          videoDesc: searchContent
        },
        success: function (res) {
          wx.hideLoading();
          // wx.hideNavigationBarLoading();
          // wx.stopPullDownRefresh();

          console.log(res.data);
          var videoList = res.data.data.rows;
          var newVideoList = me.data.videosData;
          newVideoList = newVideoList.concat(videoList)
          // 判断当前页page是否是第一页，如果是第一页，那么设置videoList为空
          if (page === 1) {
            me.setData({
              videosData: [],
              videoInfo: newVideoList[me.data.currentIndex],
            });
            // debugger
            console.log("===============me.data.videoInfo===========", me.data.videoInfo)
          }

          me.setData({
            videosData: newVideoList,
            page: page,
            totalPage: res.data.data.total,
            serverUrl: serverUrl,
            src: serverUrl + '/' + newVideoList[me.data.currentIndex].videoPath,
            videoId: newVideoList[me.data.currentIndex].id,
            likeNum: newVideoList[me.data.currentIndex].likeCounts
            //TODO获取评论数量
          });
          // debugger
          //TODO初始化评论
          me.initComment()
          me.fixedScreen()
          me.queryPublisher();

        }
      })
    }else{
      console.log('44444444444444')
      console.log(page)
      console.log('url',url)
      console.log('requestUrl',requestUrl)

      wx.request({
        url: url,
        method: "POST",
        success: function (res) {
          wx.hideLoading();
          // wx.hideNavigationBarLoading();
          // wx.stopPullDownRefresh();

          console.log(res.data);
          var videoList = res.data.data.rows;
          var newVideoList = me.data.videosData;
          
          // 判断当前页page是否是第一页，如果是第一页，那么设置videoList为空
          if (page === 1) {
            newVideoList=[]
            me.setData({
              videosData: [],
              videoInfo: newVideoList[me.data.currentIndex],
            });
          }
          newVideoList = newVideoList.concat(videoList)

          me.setData({
            videosData: newVideoList,
            page: page,
            totalPage: res.data.data.total,
            serverUrl: serverUrl,
            src: serverUrl + '/' + newVideoList[me.data.currentIndex].videoPath,
            videoId: newVideoList[me.data.currentIndex].id,
            likeNum: newVideoList[me.data.currentIndex].likeCounts
            //TODO获取评论数量
          });
          //TODO初始化评论
          me.initComment()

          me.fixedScreen()
          me.queryPublisher();

        }
      })

    }
    
  },

  queryPublisher: function() {
    var me = this;
    var videoInfo = me.data.videoInfo
    var serverUrl = app.serverUrl;
    var user = app.getGlobalUserInfo();
    var loginUserId = "";
    if (user != null && user != undefined && user != '') {
      loginUserId = user.id;
    }
    wx.request({
      url: serverUrl + '/user/queryPublisher?loginUserId=' + loginUserId + "&videoId=" + videoInfo.id + "&publishUserId=" + videoInfo.userId,
      method: 'POST',
      success: function(res) {
        // console.log('url===>'+this.url)
        console.log(res.data);
        var publisher = res.data.data.publisher;
        var userLikeVideo = res.data.data.userLikeVideo;
        if (publisher.id == user.id) {
          me.setData({
            isMe: true
          })
        } else {
          me.setData({
            isMe: false
          })
        }
        if (publisher.follow) {
          me.setData({
            isfans: true
          })
        }else{
          me.setData({
            isfans: false
          })
        }
        me.setData({
          serverUrl: serverUrl,
          publisher: publisher,
          userLikeVideo: userLikeVideo
        });
      }
    })
  },

  onShow: function() {
    var me = this
    console.log("isonload........" + me.data.isonload)
    me.setData({
      windowHeight: wx.getSystemInfoSync().windowHeight
    })
    if (!me.data.isonload) {
      this.setData({
        isonload: false
      })
      me.setData({
        commentsList: [],//评论数据数组
        secondCommentsList: [],//二级评论数据
        commentTotalRecords: 0//总记录数
      })
      me.updateVideoInfo();
      
      if (!me.data.isPause) {
        setTimeout(() => {
          me.videoCtx.play()
        }, 500)
          console.log('isPause............' + me.data.isPause)
      }
    }

    
  },

  updateVideoInfo:function(){

    console.log('updateVideoInfo <======> updateVideoInfo')
    var me = this
    var serverUrl = app.serverUrl
    var user = app.getGlobalUserInfo()
    wx.request({
      url: serverUrl + '/video/query?videoId=' + me.data.videoId,
      method: 'post',
      header: {
        'content-type': 'application/json', // 默认值
        'headerUserId': user.id,
        'headerUserToken': user.userToken
      },
      success: function (res) {

        console.log('onshow   userInfo==========>' + res.data)
        var videoInfo = res.data.data;
        me.setData({
          videoInfo: videoInfo,
          likeNum: videoInfo.likeCounts
          //TODO获取评论数量
        })
        //TODO初始化评论
        me.initComment()
        // debugger
       
      }
    })
    me.queryPublisher();
    // //TODO初始化评论
    // me.initComment()
    console.log('updateVideoInfo <======> updateVideoInfo')
  },


  stopVideo:function(){
    console.log('stopVideo  stopVideo  stopVideo')
    var me = this;
    me.setData({
      searchContent: null
    })
    me.videoCtx.stop()
  },

  onHide: function() {
    var me = this
    me.videoCtx.pause()
  },
  showSearch: function() {
    this.setData({
      isonload:false
    })
    wx.navigateTo({
      url: '../searchVideo/searchVideo',
    })

  },
  pause: function() {
    this.videoCtx.pause()
    this.setData({
      isPause: true
    })
  },
  play: function() {
    setTimeout(()=>{
      this.videoCtx.play()
    },500)
    this.setData({
      isPause: false
    })
  },

  goUserHome: function() {
    var me = this;

    var publisherId = me.data.publisher.id
    var user = app.getGlobalUserInfo();
    // '../videoInfo/videoInfo?videoInfo=' + videosData + '&index=' + arrindex
    // var videoInfo = JSON.stringify(me.data.videosData);

    // var realUrl = '../videoInfo/videoInfo#videoInfo@' + videoInfo + '*index@' + me.data.currentIndex;

    this.setData({
      isonload: false
    })
    // if (user == null || user == undefined || user == '') {
    //   wx.navigateTo({
    //     url: '../userLogin/login?redirectUrl=' + realUrl,
    //   })
    // } else {
      wx.navigateTo({
        url: '../mine/mine?publisherId=' + publisherId,
      })
    // }
  },

  goAddVideo: function() {

    var me = this;

    var user = app.getGlobalUserInfo();
    // '../videoInfo/videoInfo?videoInfo=' + videosData + '&index=' + arrindex
    // var videoInfo = JSON.stringify(me.data.videosData);

    // var realUrl = '../videoInfo/videoInfo#videoInfo@' + videoInfo + '*index@' + me.data.currentIndex;

    if (user == null || user == undefined || user == '') {
      this.setData({
        isonload: false
      })
      wx.redirectTo({
        // url: '../userLogin/login?redirectUrl=' + realUrl,
        url: '../userLogin/login',
      })
    } else {
      videoUtil.uploadVideo();
    }

  },

  showMine: function() {
    var user = app.getGlobalUserInfo();
    this.setData({
      isonload: false
    })
    if (user == null || user == undefined || user == '') {
      wx.redirectTo({
        url: '../userLogin/login',
      })
    } else {
      wx.navigateTo({
        url: '../mine/mine',
      })
    }
  },

  likeVideoOrNot: function() {
    var me = this;
    var user = app.getGlobalUserInfo();
    var videoInfo = me.data.videoInfo

    if (user == null || user == undefined || user == '') {
      this.setData({
        isonload: false
      })
      wx.redirectTo({
        url: '../userLogin/login',
      })
    } else {
      var userLikeVideo = me.data.userLikeVideo;
      var url = '/video/userLike?userId=' + user.id + '&videoId=' + videoInfo.id + '&videoCreaterId=' + videoInfo.userId;
      if (userLikeVideo) {
        url = '/video/userUnLike?userId=' + user.id + '&videoId=' + videoInfo.id + '&videoCreaterId=' + videoInfo.userId;
      }

      var serverUrl = app.serverUrl;
      // wx.showLoading({
      //   title: '...',
      // })
      wx.request({
        url: serverUrl + url,
        method: 'POST',
        header: {
          'content-type': 'application/json', // 默认值
          'headerUserId': user.id,
          'headerUserToken': user.userToken
        },

        success: function(res) {

          var num = me.data.likeNum
          userLikeVideo ? num = num - 1 : num = num + 1
          // wx.hideLoading();
          me.setData({
            userLikeVideo: !userLikeVideo,
            likeNum: num
          });
        }
      })


    }
  },
  addFans: function() {

    var me = this;
    var user = app.getGlobalUserInfo();
    var userId = user.id;
    var publisherId = me.data.publisher.id;
    var url = '/user/beyourfans?userId=' + publisherId + '&fanId=' + userId;
    wx.request({
      url: app.serverUrl + url,
      method: 'POST',
      header: {
        'content-type': 'application/json', // 默认值
        'headerUserId': user.id,
        'headerUserToken': user.userToken
      },
      success: function() {
        console.log("已关注")
        me.setData({
          isfans: true
        })
      }
    })
  },
  deleteFans: function() {
    var me = this;
    var user = app.getGlobalUserInfo();
    var userId = user.id;
    var publisherId = me.data.publisher.id;
    var url = '/user/dontbeyourfans?userId=' + publisherId + '&fanId=' + userId;
    wx.request({
      url: app.serverUrl + url,
      method: 'POST',
      header: {
        'content-type': 'application/json', // 默认值
        'headerUserId': user.id,
        'headerUserToken': user.userToken
      },
      success: function() {
        console.log("取消关注")
        me.setData({
          isfans: false
        })
      }
    })
  },

  bindtimeupdate(e) {
    let percent = (e.detail.currentTime / e.detail.duration) * 100
    this.setData({
      percent: percent.toFixed(2)
    })
  },


  shareMe: function () {
    var me = this;
    var user = app.getGlobalUserInfo();

    wx.showActionSheet({
      itemList: ['下载到本地', '举报用户'],
      success: function (res) {
        console.log(res.tapIndex);
        if (res.tapIndex == 0) {
          // 下载
          wx.showLoading({
            title: '下载中...',
          })
          wx.downloadFile({
            url: app.serverUrl +'/'+me.data.videoInfo.videoPath,
            success: function (res) {
              // 只要服务器有响应数据，就会把响应内容写入文件并进入 success 回调，业务需要自行判断是否下载到了想要的内容
              if (res.statusCode === 200) {
                console.log(res.tempFilePath);

                wx.saveVideoToPhotosAlbum({
                  filePath: res.tempFilePath,
                  success: function (res) {
                    console.log(res.errMsg)
                    wx.hideLoading();
                  }
                })
              }
            }
          })
        } else if (res.tapIndex == 1) {
          // 举报

          // var videoInfo = JSON.stringify(me.data.videosData);

          // var realUrl = '../videoInfo/videoInfo#videoInfo@' + videoInfo + '*index@' + me.data.currentIndex;

          if (user == null || user == undefined || user == '') {
            // wx.navigateTo({
            //   url: '../userLogin/login?redirectUrl=' + realUrl,
            // })
            wx.redirectTo({
              url: '../userLogin/login',
            })
          } else {
            var publishUserId = me.data.videoInfo.userId;
            var videoId = me.data.videoInfo.id;
            var currentUserId = user.id;
            wx.navigateTo({
              url: '../report/report?videoId=' + videoId + "&publishUserId=" + publishUserId
            })
          }
        } else {

          // console.log('share........')
          // wx.showShareMenu({
          //   withShareTicket: true
          // })
          // this.onShareAppMessage();
          
          // wx.showToast({
          //   title: '官方暂未开放...',
          // })
        }
      }
    })
  },

  onShareAppMessage: function (res) {

    var me = this;
    var videoInfo = me.data.videoInfo;

    return {
      title: '短视频内容分享',
      path: "pages/videoinfo/videoinfo?videoInfo=" + JSON.stringify(videoInfo)
    }
  },
//关注
  goFollow: function () {
    var me = this;
    var user = app.getGlobalUserInfo();
    this.setData({
      isonload: false
    })
    if (user == null || user == undefined || user == '') {
      
      wx.redirectTo({
        url: '../userLogin/login',
      })
    } else {
      wx.navigateTo({
        url: '../index/index?follow=yes&page=1',
      })
    }

  },
//发现
  goSearch: function () {
    this.setData({
      isonload: false
    })
    wx.navigateTo({
      url: '../searchVideo/searchVideo',
    })
  },


  ///////////////////  动画部分////////////////////////
  onReady: function () {
    var me = this
    me.videoCtx = wx.createVideoContext("myVideo", this)
    //视频切换动画
    this.animation = wx.createAnimation({
      duration: 500,// 整个动画过程花费的时间，单位为毫秒
      transformOrigin: '0 0 0'// 动画开始的坐标
    })
    //评论组件动画
    this.animationTwo = wx.createAnimation({ //评论组件弹出动画
      duration: 400, // 整个动画过程花费的时间，单位为毫秒
      timingFunction: "ease", // 动画的类型
      delay: 0 // 动画延迟参数
    })
    var myheight = parseInt(me.data.currentIndex) * wx.getSystemInfoSync().windowHeight
    if(me.data.currentIndex!=0){
      console.log('myheight   ',myheight)
      console.log('currentIndex   ', me.data.currentIndex)
      this.animation.translateY(-myheight).step()
      me.setData({
        animation:this.animation.export(),
        currentTranslateY:-myheight
      })
    }

  },
  touchStart(e) {
    let touchStartingY = this.data.touchStartingY
    this.setData({
      touchStartTime:e.timeStamp
    })
    touchStartingY = e.touches[0].clientY
    console.log(touchStartingY)
    this.setData({
      touchStartingY: touchStartingY
    })
  },
  touchEndHandler(e) {
    var me = this;
    let touchStartingY = this.data.touchStartingY
    console.log(touchStartingY)
    console.log(e.changedTouches[0].clientY)
    let deltaY = e.changedTouches[0].clientY - touchStartingY
    console.log('deltaY ', deltaY)

    let index = this.data.currentIndex
    console.log(index, 'indexindexindexindex')

    if (deltaY > 100 && index != 0) {

      me.setData({
        isPause:false
      })
      // this.videoCtx.pause()
      this.videoCtx.stop()

      // 更早地设置 animationShow
      this.setData({
        animationShow: true
      }, () => {
        console.log('-1 切换')
        this.setData({
          commentsList: []//TODO滑动上一个视频清除评论列表
        }) 
        this.createAnimation(-1, index).then((res) => {
          console.log(res)
          this.setData({
            isPause:false,
            animation: this.animation.export(),
            currentIndex: res.index,
            videoInfo: me.data.videosData[res.index],
            videoId:me.data.videosData[res.index].id,
            currentTranslateY: res.currentTranslateY,
            percent: 1
          }, () => {
            console.log('<===============>res.index<=============>', res.index)
            me.updateVideoInfo()
            console.log(me.data.publisher)
            me.updateVideoIndex(res.index)//change
          })
        })
      })
    } else if (deltaY < -100 && index != (this.data.videosData.length - 1)) {

      me.setData({
        isPause: false
      })
      // this.videoCtx.pause()
      this.videoCtx.stop()

      this.setData({
        animationShow: true
      }, () => {
        console.log('+1 切换')
        console.log(index)
        this.setData({
          commentsList: []//TODO滑动上一个视频清除评论列表
        }) 
        this.createAnimation(1, index).then((res) => {
          console.log('res.index',res.index)
          console.log('me.data.videosData',me.data.videosData)
          this.setData({
            isPause:false,
            animation: this.animation.export(),
            currentIndex: res.index,
            videoInfo: me.data.videosData[res.index],
            videoId: me.data.videosData[res.index].id,
            currentTranslateY: res.currentTranslateY,
            percent: 1
          }, () => {

            console.log('<===============>res.index<=============>',res.index)
            me.updateVideoInfo()
            console.log(me.data.publisher)
            me.updateVideoIndex(res.index)//change
          })
        })
      })
    } else if ((deltaY > 100 ||deltaY < -100) &&index == (this.data.videosData.length - 1)){
      console.log('已经没有视频啦...')
      wx.showToast({
        title: '已经没有视频啦...',
        icon: "none"
      });
    }
  },

  updateVideoIndex:function(index) {

    console.log('updateVideoIndex <======> updateVideoIndex')
    var me = this
    console.log('event updateVideoIndex:', index)
    if (index == me.data.videosData.length - 1) { //当滑动到最后一条加载下一条数据
      // this.setData({
      //   page: parseInt(this.data.page + 1)
      // })
      console.log('<=========me.data.requestUrl=======>', me.data.requestUrl)
      console.log('<=========me.data.searchContent=======>', me.data.searchContent)
      var thispage = Number(me.data.page)+1
      if(me.data.searchContent==null&&me.data.requestUrl==''){
        console.log('111111111111')
        me.getAllVideoList(thispage,0,'',me.data.requestUrl)//change
      }else{
        console.log(thispage)
        console.log('2222222222222222')
        console.log(me.data.page + 1)
        me.getAllVideoList(thispage,0,me.data.searchContent,me.data.requestUrl)//change
      }
      // this.getVideos()
    }
    me.fixedScreen()
    setTimeout(() => {
      me.setData({
        animationShow: false,
        isPause:false,
      }, () => {
        // me.videoCtx.play()
        // 切换src后，video不能立即播放，settimeout一下
        setTimeout(() => {
          me.videoCtx.play()
        }, 500)
      })
    }, 600)
  },
  touchEnd(e) {
    console.log('------touchEnd------')
    console.log(e)
    this.setData({
      touchEndTime :e.timeStamp
    })
    this.videoChange(e)
  },

  createAnimation(direction, index) {
    // direction为-1，向上滑动，animationImage1为(index)的poster，animationImage2为(index+1)的poster
    // direction为1，向下滑动，animationImage1为(index-1)的poster，animationImage2为(index)的poster
    let videos = this.data.videosData
    let currentTranslateY = this.data.currentTranslateY
    console.log('direction ', direction)
    console.log('index ', index)
    // 更新 videoIndex
    index = Number(index)
    direction = Number(direction)
    index += direction
    currentTranslateY += -direction * this.data.windowHeight
    console.log('currentTranslateY: ', currentTranslateY)
    this.animation.translateY(currentTranslateY).step()

    return Promise.resolve({
      index: index,
      currentTranslateY: currentTranslateY
    })
  },

  ///////////////评论/////////////////////////
  showComments:function(){
    this.showTalks()
  },

  showTalks: function (e) {
    this.videoCtx.pause()
    // debugger
    this.setData({
      btnShow: false
    })
    

    // 设置动画内容为：使用绝对定位显示区域，高度变为100%
    this.animationTwo.bottom("0rpx").height("100%").step()
    this.setData({
      talksAnimationData: this.animationTwo.export(),
      animationShow: true
    })
  },

  hideTalks: function () {

    this.setData({
      btnShow: true
    })
    // 设置动画内容为：使用绝对定位隐藏整个区域，高度变为0
    this.animationTwo.bottom("-100%").height("0rpx").step()
    this.setData({
      // commentList: [],
      talksAnimationData: this.animationTwo.export(),
      animationShow: false,
    })
    // console.log(this.data.playState)
    // if (!this.data.isPause) {

    //   this.videoCtx.play()
    // }
    this.setData({
      placeholder: "说点什么...",
      secondCommentsList:[],
      replyFatherCommentId: '',
      replyToUserId: '',
    });
    if(!this.data.isPause){

      setTimeout(() => {
        this.videoCtx.play()
      }, 500)
    }
  },
  onScrollLoad: function () {

    var me = this;
    var currentPage = me.data.commentsPage;
    var totalPage = me.data.commentsTotalPage;
    if (currentPage === totalPage) {
      me.setData({
        hasmoreData: false,
        hiddenloading: true,
      })
      return;
    }
    var page = currentPage + 1;
    me.getCommentsList(page);
    me.setData({
      hasmoreData: true,
      hiddenloading: false,
    })
  
    
  },

  leaveComment: function () {
    // this.setData({
    //   commentFocus: true
    // });
  },

  replyFocus: function (e) {
    var fatherCommentId = e.currentTarget.dataset.fathercommentid;
    var toUserId = e.currentTarget.dataset.touserid;
    var toNickname = e.currentTarget.dataset.tonickname;

    this.setData({
      placeholder: "回复  " + toNickname,
      replyFatherCommentId: fatherCommentId,
      replyToUserId: toUserId,
      commentFocus: true
    });
  },

  saveComment: function (e) {
    var me = this;
    var content = e.detail.value;

    // 获取评论回复的fatherCommentId和toUserId
    var fatherCommentId = e.currentTarget.dataset.replyfathercommentid;
    var toUserId = e.currentTarget.dataset.replytouserid;
    if(fatherCommentId==undefined||fatherCommentId==null){
      fatherCommentId = ''
    }
    if (toUserId == undefined || toUserId==null){
      toUserId = me.data.publisher.id
    }

    var user = app.getGlobalUserInfo();
    // var videoInfo = JSON.stringify(me.data.videoInfo);
    // var realUrl = '../videoinfo/videoinfo#videoInfo@' + videoInfo;

    if (user == null || user == undefined || user == '') {
      wx.redirectTo({
        url: '../userLogin/login',
      })
    } else {
      wx.showLoading({
        title: '请稍后...',
      })
      wx.request({
        url: app.serverUrl + '/video/saveComment?fatherCommentId=' + fatherCommentId + "&toUserId=" + toUserId,
        method: 'POST',
        header: {
          'content-type': 'application/json', // 默认值
          'headerUserId': user.id,
          'headerUserToken': user.userToken
        },
        data: {
          fromUserId: user.id,
          videoId: me.data.videoInfo.id,
          comment: content
        },
        success: function (res) {
          // debugger
          console.log(res.data)
          wx.hideLoading();

          me.setData({
            contentValue: "",
            commentsList: [],
            placeholder: "说点什么..."
          });

          me.getCommentsList(1);
          if(fatherCommentId!=''){

            me.getVideoSecondComments(fatherCommentId)
          }
        }
      })
    }
  },


  getCommentsList: function (page) {
    var me = this;

    var videoId = me.data.videoInfo.id;
    // debugger
    wx.request({
      url: app.serverUrl + '/video/getVideoFirstComments?videoId=' + videoId + "&page=" + page + "&pageSize=5",
      method: "POST",
      success: function (res) {
        console.log(res.data);

        var commentsList = res.data.data.rows;
        var newCommentsList = me.data.commentsList;
        if(page==1){
          newCommentsList=[]
        }

        me.setData({
          commentsList: newCommentsList.concat(commentsList),
          commentsPage: page,
          commentsTotalPage: res.data.data.total,
          commentTotalRecords: res.data.data.records
        },()=>{
          console.log('=================initComment================',videoId,page, me.data.commentTotalRecords)
        });
      }
    })
  },

  getVideoSecondComments: function (fid,e) {
    var me = this;
    console.log("fid",fid)
    var videoId = me.data.videoInfo.id;
    // debugger
    // debugger
    wx.request({
      url: app.serverUrl + '/video/getVideoSecondComments?videoId=' + videoId + "&fatherCommentId=" + fid,
      method: "POST",
      success: function (res) {
        console.log(res.data);

        var commentsList = res.data.data.rows;
        // var newCommentsList = me.data.commentsList;

        me.setData({
          secondCommentsList: commentsList,
        },()=>{
          if(e!=undefined){
            var ids = e.currentTarget.dataset.idx

            console.log('ids', ids)
            var num = Number(ids)
            me.data.commentsList[num].second = true
            me.data.commentsList[num].secondList = me.data.secondCommentsList
            // debugger
            me.setData({
              commentsList: me.data.commentsList
            })
          }
        });
      }
    })
  },

  showSecondComments:function(e){
    this.getVideoSecondComments(e.currentTarget.dataset.ids,e)    
  },

  loseSecondComment:function(e){
    var me = this
    var ids = e.currentTarget.dataset.idx
    // debugger
    console.log('ids', ids)
    var num = Number(ids)
    me.data.commentsList[num].second = false
    me.data.commentsList[num].secondList = []
    // debugger
    me.setData({
      commentsList: me.data.commentsList
    })

  }


})
function throttle(fn, delay) {
  var timer = null;
  return function () {
    var context = this, args = arguments;
    clearTimeout(timer);
    timer = setTimeout(function () {
      fn.apply(context, args);
    }, delay);
  }
}