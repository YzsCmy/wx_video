const app = getApp()

Page({
  data: {

  },
  doRegist:function(e){
    var obj = e.detail.value
    var username = obj.username
    var passw = obj.password
    if(username.length==0||passw.length==0){
      wx.showToast({
        title: '用户名和密码不能为空',
        icon:"none"
      })
    }else{
      wx.showLoading({
        title: '请等待...',
      });
      wx.request({
        url: app.serverUrl+'/regist',
        data:{
          username:username,
          password:passw
        },
        method:"post",
        success:function(res){
          wx.hideLoading();
          var status = res.data.status;
          if(status==200){
            wx.showToast({
              title: "注册成功"
            })
            console.log(res.data)
            app.setGlobalUserInfo(res.data.data)
            wx.redirectTo({
              url: '../mine/mine',
            })
          }else{
            wx.showToast({
              title: res.data.msg,
              icon: "none"
            })
          }
        }
      })
    }
  },
  goLoginPage: function () {
    wx.redirectTo({
      url: '../userLogin/login',
    })
  }
})