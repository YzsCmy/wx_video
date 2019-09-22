const app = getApp()

Page({
  data: {
    // 用于分页的属性
    totalPage: 1,
    page:1,
    videoList:[],
    follow:'',

    screenWidth: 350,
    serverUrl: "",

    searchContent: ""
  },

  onLoad: function (params) {
    var me = this;
    
    var screenWidth = wx.getSystemInfoSync().screenWidth;
    me.setData({
      screenWidth: screenWidth,
    });

    if (params.follow != undefined) {
      var thispage = params.page
      me.setData({
        page:thispage,
        follow: params.follow
      })
      me.getMyFollowList(Number(params.page))
      return;
    }

    var searchContent = params.search;
    var isSaveRecord = params.isSaveRecord;
    // var isSaveRecord = 0;
    if (isSaveRecord == null || isSaveRecord == '' || isSaveRecord == undefined) {
      isSaveRecord = 0;
    }

    me.setData({
      searchContent: searchContent
    });

    // 获取当前的分页数
    var page = me.data.page;
    me.getAllVideoList(page, isSaveRecord);
  },

  getAllVideoList: function (page, isSaveRecord) {
    var me = this;
    var serverUrl = app.serverUrl;
    wx.showLoading({
      title: '请等待，加载中...',
    });

    var searchContent = me.data.searchContent;

    wx.request({
      url: serverUrl + '/video/showAll?page=' + page + "&isSaveRecord=" + isSaveRecord,
      method: "POST",
      data: {
        videoDesc: searchContent
        // videoDesc: ""
      },
      success: function (res) {
        wx.hideLoading();
        wx.hideNavigationBarLoading();
        wx.stopPullDownRefresh();

        console.log(res.data);

        // 判断当前页page是否是第一页，如果是第一页，那么设置videoList为空
        if (page === 1) {
          me.setData({
            videoList: []
          });
        }

        var videoList = res.data.data.rows;
        var newVideoList = me.data.videoList;

        me.setData({
          videoList: newVideoList.concat(videoList),
          page: page,
          totalPage: res.data.data.total,
          serverUrl: serverUrl
        });

      }
    })
  },

  getMyFollowList:function (page) {
    var me = this;
    var userId = app.getGlobalUserInfo().id
    console.log("getMyFollowList  userid ", userId)
    console.log("getMyFollowList  page " ,page)
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
        wx.hideLoading();
        wx.hideNavigationBarLoading();
        wx.stopPullDownRefresh();
        console.log(userId + "<===getMyVideoList===>" + res.data);
        var followVideoList = res.data.data.rows;

        var newVideoList = me.data.videoList;
        me.setData({
          page: page,
          videoList: newVideoList.concat(followVideoList),
          totalPage: res.data.data.total,
          serverUrl: app.serverUrl
        });
      }
    })
  },

  onPullDownRefresh: function() {
    if(me.data.follow!=''){
      wx.showNavigationBarLoading();
      this.getMyFollowList(1)
      return;
    }
    wx.showNavigationBarLoading();
    this.getAllVideoList(1, 0);
  },

  onReachBottom:function() {
    var me = this;
    var currentPage = me.data.page;
    var totalPage = me.data.totalPage;

    // 判断当前页数和总页数是否相等，如果想的则无需查询
    if (currentPage === totalPage) {
      wx.showToast({
        title: '当前页没有视频啦~~',
        icon: "none"
      })
      return;
    }
    if (me.data.follow != '') {
      this.getMyFollowList(me.data.page+1)
      return;
    }

    var page = currentPage + 1;

    me.getAllVideoList(page, 0);
  },

  showVideoInfo: function(e) {
    var me = this;
    // console.log(e)
    var videoList = me.data.videoList;
    var arrindex = e.target.dataset.arrindex;
    // var videoInfo = JSON.stringify(videoList[arrindex]);
    var videosData = JSON.stringify(videoList);

    var thisurl = me.data.serverUrl + '/video/showAll?page=' + me.data.page + "&isSaveRecord=" + 0
    thisurl = me.formatUrl(thisurl)
    var requestUrl = "&requestUrl="+thisurl
    wx.redirectTo({
      url: '../videoInfo/videoInfo?videoInfo=' + videosData + '&index=' + arrindex + "&ispost=1" 
      + "&videoDesc=" + me.data.searchContent+requestUrl
      // url: '../videoinfo/videoinfo?videoInfo=' + videoInfo
    })
  },

  formatUrl: function (redirectUrl) {

    if (redirectUrl != undefined && redirectUrl != null && redirectUrl != '') {
      redirectUrl = redirectUrl.replace(/\?/g, "#");
      redirectUrl = redirectUrl.replace(/=/g, "@");
      redirectUrl = redirectUrl.replace(/&/g, "*");

      return redirectUrl;
    }
  },

})
