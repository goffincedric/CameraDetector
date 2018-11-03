package be.kdg.processor.user.controllers.rest;

import be.kdg.processor.user.dom.Role;
import be.kdg.processor.user.dom.User;
import be.kdg.processor.user.dto.SafeUserDTO;
import be.kdg.processor.user.dto.UserDTO;
import be.kdg.processor.user.exceptions.UserException;
import be.kdg.processor.user.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

/**
 * @author C&eacute;dric Goffin
 * 23/10/2018 20:19
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserRestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;

    @Test
    public void getUser() throws Exception, UserException {
        userService.save(new User("testUser", "testUser", List.of(new Role("test"))));

        mockMvc.perform(get("/api/user?username=testUser"))
                .andExpect(result -> {
                    SafeUserDTO safeUserDTO = objectMapper.readValue(result.getResponse().getContentAsString(), SafeUserDTO.class);
                    Assert.assertEquals("testUser", safeUserDTO.getUsername());
                })
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    public void addUser() throws Exception {
        UserDTO userDTO = new UserDTO("testUser", "testUser", List.of("test"));

        mockMvc.perform(post("/api/user")
                .contentType(APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andDo(print())
                .andExpect(result -> {
                    SafeUserDTO safeUserDTO = objectMapper.readValue(result.getResponse().getContentAsString(), SafeUserDTO.class);
                    Assert.assertEquals("testUser", safeUserDTO.getUsername());
                    Assert.assertTrue(safeUserDTO.getRoles().contains("test"));
                })
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void updateUser() throws Exception, UserException {
        userService.save(new User("testUser", "testUser", List.of(new Role("test"))));
        UserDTO userDTO = new UserDTO("testUser", "testUser", List.of("test", "test2"));

        mockMvc.perform(put("/api/user", userDTO)
                .contentType(APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andDo(print())
                .andExpect(result -> {
                    SafeUserDTO safeUserDTO = objectMapper.readValue(result.getResponse().getContentAsString(), SafeUserDTO.class);
                    Assert.assertEquals("testUser", safeUserDTO.getUsername());
                    Assert.assertTrue(safeUserDTO.getRoles().contains("test") && userDTO.getRoles().contains("test2"));
                })
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void deleteUser() throws Exception, UserException {
        String username = "testUser";
        userService.save(new User(username, "testUser", List.of(new Role("test"))));

        mockMvc.perform(delete("/api/user?username=" + username))
                .andDo(print())
                .andExpect(content().string("deleted " + username))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}