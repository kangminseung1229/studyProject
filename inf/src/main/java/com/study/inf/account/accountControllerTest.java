
package com.study.inf.account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

//import error
//import static org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder.*;

@SpringBootTest
@AutoConfigureMockMvc
public class accountControllerTest {

    
    @Autowired
    private MockMvc mockMvc;

    @DisplayName("회원가입 테스트")
    @Test
    void signUpForm() throws Exception{
    }
}
