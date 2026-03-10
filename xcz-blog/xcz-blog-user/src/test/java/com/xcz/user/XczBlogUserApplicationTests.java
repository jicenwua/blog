package com.xcz.user;

import com.xcz.common.database.extend.GeneraCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class XczBlogUserApplicationTests {

    @Value("${spring.datasource.dynamic.datasource.master.url}")
    private String url;
    @Value("${spring.datasource.dynamic.datasource.master.username}")
    private String username;
    @Value("${spring.datasource.dynamic.datasource.master.password}")
    private String password;

    @Test
    void contextLoads() {
        GeneraCode generaCode = new GeneraCode(url, username, password);
        generaCode.generate("user", "com.xcz");
    }

}
