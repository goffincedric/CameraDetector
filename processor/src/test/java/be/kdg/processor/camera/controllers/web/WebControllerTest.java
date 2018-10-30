package be.kdg.processor.camera.controllers.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author C&eacute;dric Goffin
 * 20/10/2018 18:06
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class WebControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testSuccessLogin() throws Exception {
        RequestBuilder requestBuilder = post("/login")
                .param("username", "admin")
                .param("password", "admin");
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }

    @Test
    public void testBadLogin() throws Exception {
        RequestBuilder requestBuilder = post("/login")
                .param("username", "admin")
                .param("password", "notAnAdmin");
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }
}