package com.yzs.wx.manage;

import com.yzs.wx.utils.ZKCurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages="com.yzs.wx.mapper")
@ComponentScan(basePackages = {"com.yzs.wx","org.n3r.idworker"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Bean(initMethod = "init")
    public ZKCurator zkCurator(){
        return new ZKCurator();
    }

}