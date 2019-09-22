const app = getApp()

Page({
  data: {
  },

  onLoad:function(params){
    var me = this;
    var redirectUrl = params.redirectUrl;
    // debugger;
    if (redirectUrl != null && redirectUrl != undefined && redirectUrl != '') {
      redirectUrl = redirectUrl.replace(/#/g, "?");
      redirectUrl = redirectUrl.replace(/@/g, "=");
      redirectUrl = redirectUrl.replace(/\*/g, "&");

      me.redirectUrl = redirectUrl;
    }
  },

  // 登录  
  doLogin: function (e) {
    var me = this;
    var formObject = e.detail.value;
    var username = formObject.username;
    var password = formObject.password;
    // 简单验证
    if (username.length == 0 || password.length == 0) {
      wx.showToast({
        title: '用户名或密码不能为空',
        icon: 'none',
      })
    } else {
      var serverUrl = app.serverUrl;
      wx.showLoading({
        title: '请等待...',
      });
      // 调用后端
      wx.request({
        url: serverUrl + '/login',
        method: "POST",
        data: {
          username: username,
          password: password
        },
        header: {
          'content-type': 'application/json' // 默认值
        },
        success: function (res) {
          console.log(res.data);
          wx.hideLoading();
          if (res.data.status == 200) {
            
            wx.showToast({
              title: '登录成功',
              icon: 'success',
              duration: 2500
            });
            app.setGlobalUserInfo(res.data.data)
            
            // 登录成功跳转 
            // wx.redirectTo({
            //   url: '../mine/mine',
            // })
            
            //页面跳转

            // var redirectUrl = me.redirectUrl;
            // if (redirectUrl != null && redirectUrl != undefined && redirectUrl != '') {
            //   wx.redirectTo({
            //     url: redirectUrl,
            //   })
            // } else {
              wx.redirectTo({
                // url: '../index/index',
                url: '../videoInfo/videoInfo',//#####
              })
            // }
            //登陆失败
          } else {
            // 失败弹出框
            wx.showToast({
              title: res.data.msg,
              icon: 'none',
              duration: 3000
            })
          }
        }
      })
    }
  },

  goRegistPage:function() {
    wx.redirectTo({
      url: '../userRegist/regist',
    })
  }
})