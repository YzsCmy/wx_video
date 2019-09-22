// var videoUtil = require('../../utils/videoUtil.js')

const app = getApp()

Page({
  data: {
    faceUrl: "../resource/images/noneface.png",
    isMe: true,
    isFollow: false,
    publisherId:'',


    videoSelClass: "video-info",
    isSelectedWork: "video-info-selected",
    isSelectedLike: "",
    isSelectedFollow: "",

    myVideoList: [],
    myVideoPage: 1,
    myVideoTotal: 1,

    likeVideoList: [],
    likeVideoPage: 1,
    likeVideoTotal: 1,

    followVideoList: [],
    followVideoPage: 1,
    followVideoTotal: 1,

    myWorkFalg: false,
    myLikesFalg: true,
    myFollowFalg: true

  },

  onLoad:function(params){
    var me = this;
    var serverUrl = app.serverUrl;
    var publisherId = params.publisherId
    var user = app.getGlobalUserInfo()
    if(user!=null){
      wx.showLoading({
        title: '请等待...',
      });
      var userid = user.id
      
      if (publisherId != null && publisherId != "" && publisherId != undefined && publisherId!=userid){
        userid = publisherId
        me.setData({
          isMe:false,
          publisherId:userid
        })
      }
      me.setData({
        userId: userid
      })
      wx.request({
        url: serverUrl + '/user/query?userid=' + userid+'&fanId='+user.id,
        method: "post",
        header: {
          'content-type': 'application/json', // 默认值
          'headerUserId': user.id,
          'headerUserToken': user.userToken
        },
        success: function (res) {
          wx.hideLoading();
          var status = res.data.status;
          var data = res.data.data
          var faceurl=''
          console.log(res.data)
          console.log(status)
          if(status==502){
            wx.showToast({
              title: res.data.msg,
              duration: 3000,
              icon: "none",
              success: function () {
                wx.redirectTo({
                  url: '../userLogin/login',
                })
              }
            })
            return ;
          }
          if (data.faceImage == undefined || data.faceImage == '' || data.faceImage == null){
            faceurl = me.data.faceUrl

          }else{
            faceurl = serverUrl + '/' + data.faceImage
          }
          if (status == 200) {
            me.setData({
              nickname: data.nickname,
              faceUrl: faceurl,
              fansCounts: data.fansCounts,
              followCounts: data.followCounts,
              receiveLikeCounts: data.receiveLikeCounts,
              isFollow: data.follow
            })
            me.doSelectWork()

          } else if (status === 502) {
            
          }
        }
      })
    }
    
    
  },

  followMe: function (e) {
    var me = this;
    var user = app.getGlobalUserInfo();
    var userId = user.id;
    var publisherId = me.data.publisherId;

    var followType = e.currentTarget.dataset.followtype;

    // 1：关注 0：取消关注
    var url = '';
    if (followType == '1') {
      url = '/user/beyourfans?userId=' + publisherId + '&fanId=' + userId;
    } else {
      url = '/user/dontbeyourfans?userId=' + publisherId + '&fanId=' + userId;
    }

    wx.showLoading();
    wx.request({
      url: app.serverUrl + url,
      method: 'POST',
      header: {
        'content-type': 'application/json', // 默认值
        'headerUserId': user.id,
        'headerUserToken': user.userToken
      },
      success: function () {
        wx.hideLoading();
        if (followType == '1') {
          me.setData({
            isFollow: true,
            fansCounts: ++me.data.fansCounts
          })
        } else {
          me.setData({
            isFollow: false,
            fansCounts: --me.data.fansCounts
          })
        }
      }
    })
  },

  logout:function(){
    var userid = app.getGlobalUserInfo().id;
    wx.showLoading({
      title: '请等待...',
    });
    wx.request({
      url: app.serverUrl + '/logout?userid='+userid,
      method: "get",
      success: function (res) {
        wx.hideLoading();
        var status = res.data.status;
        console.log(res.data)
        if (status == 200) {
          wx.showToast({
            title: "注销成功"
          })
          wx.removeStorageSync("userInfo")
          // app.userInfo = null
          //注销成功跳转
          wx.redirectTo({
            url: '../userLogin/login',
          })
        } else {
          wx.showToast({
            title: res.data.msg,
            icon: "none"
          })
        }
      }
    })
  },
  changeFace:function(){

    var me = this;
    wx.chooseImage({
      count: 1,
      sizeType: [ 'compressed'],
      sourceType: ['album'],
      success(res) {
        // tempFilePath可以作为img标签的src属性显示图片
        const tempFilePaths = res.tempFilePaths

        var serverUrl = app.serverUrl;
        wx.showLoading({
          title: '请等待...',
        });

        var userInfo = app.getGlobalUserInfo();
        wx.uploadFile({
          url: serverUrl + '/user/uploadFace?userid=' + userInfo.id, 
          filePath: tempFilePaths[0],
          name: 'file',
          header: {
            'content-type': 'application/json', // 默认值
            'headerUserId': userInfo.id,
            'headerUserToken': userInfo.userToken
          },
          success(res) {
            wx.hideLoading();
            var data = JSON.parse(res.data)
            console.log(data)
            if(data.status==200){
              wx.showToast({
                title: '上传头像成功'
              })
              me.setData({
                faceUrl:serverUrl+'/'+data.data
              })
            }else if(data.status==500){
              wx.showToast({
                title: res.data.msg,
                icon:"none"
              })
            } else if (data.status == 502) {
              wx.showToast({
                title: res.data.msg,
                duration: 3000,
                icon: "none",
                success: function () {
                  wx.redirectTo({
                    url: '../userLogin/login',
                  })
                }
              })
            }
           
          }
        })
      }
    })
  },
  uploadVideo:function(){
    var me = this;
    wx.chooseVideo({
      sourceType: ['album'],
      maxDuration: 60,
      compressed:false,
      success(res) {
        console.log(res)
        var duration = res.duration;
        var tmpHeight = res.height;
        var tmpWidth = res.width;
        var tmpVideoUrl = res.tempFilePath;
        var tmpCoverUrl = res.thumbTempFilePath;

        if (duration > 60) {
          wx.showToast({
            title: '视频长度不能超过60秒...',
            icon: "none",
            duration: 3000
          })
        } else if (duration < 1) {
          wx.showToast({
            title: '视频长度太短，请上传超过1秒的视频...',
            icon: "none",
            duration: 3000
          })
        } else {
          //打开选择bgm的页面
          wx.navigateTo({
            url: '../chooseBgm/chooseBgm?duration=' + duration
              + "&tmpHeight=" + tmpHeight
              + "&tmpWidth=" + tmpWidth
              + "&tmpVideoUrl=" + tmpVideoUrl
              + "&tmpCoverUrl=" + tmpCoverUrl
            ,
          })
        }

      }
    })
  },

  doSelectWork: function () {
    this.setData({
      isSelectedWork: "video-info-selected",
      isSelectedLike: "",
      isSelectedFollow: "",

      myWorkFalg: false,
      myLikesFalg: true,
      myFollowFalg: true,

      myVideoList: [],
      myVideoPage: 1,
      myVideoTotal: 1,

      likeVideoList: [],
      likeVideoPage: 1,
      likeVideoTotal: 1,

      followVideoList: [],
      followVideoPage: 1,
      followVideoTotal: 1
    });

    this.getMyVideoList(1);
  },

  doSelectLike: function () {
    this.setData({
      isSelectedWork: "",
      isSelectedLike: "video-info-selected",
      isSelectedFollow: "",

      myWorkFalg: true,
      myLikesFalg: false,
      myFollowFalg: true,

      myVideoList: [],
      myVideoPage: 1,
      myVideoTotal: 1,

      likeVideoList: [],
      likeVideoPage: 1,
      likeVideoTotal: 1,

      followVideoList: [],
      followVideoPage: 1,
      followVideoTotal: 1
    });

    this.getMyLikesList(1);
  },

  doSelectFollow: function () {
    this.setData({
      isSelectedWork: "",
      isSelectedLike: "",
      isSelectedFollow: "video-info-selected",

      myWorkFalg: true,
      myLikesFalg: true,
      myFollowFalg: false,

      myVideoList: [],
      myVideoPage: 1,
      myVideoTotal: 1,

      likeVideoList: [],
      likeVideoPage: 1,
      likeVideoTotal: 1,

      followVideoList: [],
      followVideoPage: 1,
      followVideoTotal: 1
    });

    this.getMyFollowList(1)
  },
  getMyVideoList: function (page) {
    var me = this;

    // 查询视频信息
    wx.showLoading();
    // 调用后端
    var serverUrl = app.serverUrl;
    wx.request({
      url: serverUrl + '/video/showAll/?page=' + page + '&pageSize=6',
      method: "POST",
      data: {
        userId: me.data.userId
      },
      header: {
        'content-type': 'application/json' // 默认值
      },
      success: function (res) {
        console.log(me.data.userId+"<===getMyVideoList===>"+res.data);
        var myVideoList = res.data.data.rows;
        wx.hideLoading();

        var newVideoList = me.data.myVideoList;
        me.setData({
          myVideoPage: page,
          myVideoList: newVideoList.concat(myVideoList),
          myVideoTotal: res.data.data.total,
          serverUrl: app.serverUrl
        });
      }
    })
  },

  getMyLikesList: function (page) {
    var me = this;
    var userId = me.data.userId;

    // 查询视频信息
    wx.showLoading();
    // 调用后端
    var serverUrl = app.serverUrl;
    wx.request({
      url: serverUrl + '/video/showMyLike/?userId=' + userId + '&page=' + page + '&pageSize=6',
      method: "POST",
      header: {
        'content-type': 'application/json' // 默认值
      },
      success: function (res) {
        console.log(userId + "<===getMyVideoList===>" + res.data);
        var likeVideoList = res.data.data.rows;
        wx.hideLoading();

        var newVideoList = me.data.likeVideoList;
        me.setData({
          likeVideoPage: page,
          likeVideoList: newVideoList.concat(likeVideoList),
          likeVideoTotal: res.data.data.total,
          serverUrl: app.serverUrl
        });
      }
    })
  },

  getMyFollowList: function (page) {
    var me = this;
    var userId = me.data.userId;

    // 查询视频信息
    wx.showLoading();
    // 调用后端
    var serverUrl = app.serverUrl;
    wx.request({
      url: serverUrl + '/video/showMyFollow/?userId=' + userId + '&page=' + page + '&pageSize=6',
      method: "POST",
      header: {
        'content-type': 'application/json' // 默认值
      },
      success: function (res) {
        console.log(userId + "<===getMyVideoList===>" + res.data);
        var followVideoList = res.data.data.rows;
        wx.hideLoading();

        var newVideoList = me.data.followVideoList;
        me.setData({
          followVideoPage: page,
          followVideoList: newVideoList.concat(followVideoList),
          followVideoTotal: res.data.data.total,
          serverUrl: app.serverUrl
        });
      }
    })
  },

  // 点击跳转到视频详情页面
  showVideo: function (e) {
    var me = this;
    console.log(e);

    var myWorkFalg = this.data.myWorkFalg;
    var myLikesFalg = this.data.myLikesFalg;
    var myFollowFalg = this.data.myFollowFalg;
    var videoList=[]
    var requestUrl = ''
    if (!myWorkFalg) {
      videoList = this.data.myVideoList;
    } else if (!myLikesFalg) {
      videoList = this.data.likeVideoList;
    } else if (!myFollowFalg) {
      videoList = this.data.followVideoList;
    }

    var serverUrl = app.serverUrl
    var userId = this.data.userId
    if (!myWorkFalg) {

      var thisurl = serverUrl + '/video/showMy/?userId=' + userId + '&page=' + me.data.myVideoPage + '&pageSize=6'
      thisurl = this.formatUrl(thisurl)
      requestUrl = '&requestUrl='+ thisurl
    } else if (!myLikesFalg) {
      var lthisurl = serverUrl + '/video/showMyLike/?userId=' + userId + '&page=' + me.data.likeVideoPage + '&pageSize=6'
      lthisurl = this.formatUrl(lthisurl)
      requestUrl = '&requestUrl=' + lthisurl
    } else if (!myFollowFalg) {
      var fthisurl = serverUrl + '/video/showMyFollow/?userId=' + userId + '&page=' + me.data.followVideoPage + '&pageSize=6'
      fthisurl = this.formatUrl(fthisurl)
      requestUrl = '&requestUrl=' + fthisurl
      
    }

    //停止上个页面的视频
    // var pages = getCurrentPages()
    // if (pages.length>1){
    //   console.log('stopVideo stopVideo stopVideo stopVideo')
    //   var prePage = pages[pages.length-2]
    //   prePage.stopVideo()
    // }

    var arrindex = e.target.dataset.arrindex;
    // var videoInfo = JSON.stringify(videoList[arrindex]);
    var videosData = JSON.stringify(videoList);

    wx.redirectTo({
      url: '../videoInfo/videoInfo?videoInfo=' + videosData + '&index=' + arrindex + requestUrl
      // url: '../videoinfo/videoinfo?videoInfo=' + videoInfo
    })

  },
  formatUrl: function (redirectUrl){

    if (redirectUrl != undefined && redirectUrl != null && redirectUrl != '') {
      redirectUrl = redirectUrl.replace(/\?/g, "#");
      redirectUrl = redirectUrl.replace(/=/g, "@");
      redirectUrl = redirectUrl.replace(/&/g, "*");

      return redirectUrl;
    }
  },

  // 到底部后触发加载
  onReachBottom: function () {
    var myWorkFalg = this.data.myWorkFalg;
    var myLikesFalg = this.data.myLikesFalg;
    var myFollowFalg = this.data.myFollowFalg;

    if (!myWorkFalg) {
      var currentPage = this.data.myVideoPage;
      var totalPage = this.data.myVideoTotal;
      // 获取总页数进行判断，如果当前页数和总页数相等，则不分页
      if (currentPage === totalPage) {
        wx.showToast({
          title: '已经没有视频啦...',
          icon: "none"
        });
        return;
      }
      var page = currentPage + 1;
      this.getMyVideoList(page);
    } else if (!myLikesFalg) {
      var currentPage = this.data.likeVideoPage;
      var totalPage = this.data.myLikesTotal;
      // 获取总页数进行判断，如果当前页数和总页数相等，则不分页
      if (currentPage === totalPage) {
        wx.showToast({
          title: '已经没有视频啦...',
          icon: "none"
        });
        return;
      }
      var page = currentPage + 1;
      this.getMyLikesList(page);
    } else if (!myFollowFalg) {
      var currentPage = this.data.followVideoPage;
      var totalPage = this.data.followVideoTotal;
      // 获取总页数进行判断，如果当前页数和总页数相等，则不分页
      if (currentPage === totalPage) {
        wx.showToast({
          title: '已经没有视频啦...',
          icon: "none"
        });
        return;
      }
      var page = currentPage + 1;
      this.getMyFollowList(page);
    }

  }

})
