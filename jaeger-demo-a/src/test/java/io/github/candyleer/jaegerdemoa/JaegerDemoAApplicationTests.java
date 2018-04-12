package io.github.candyleer.jaegerdemoa;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JaegerDemoAApplicationTests {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void testHello() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/a/hello"))
                .andDo(print())
                .andReturn();
        Assert.assertEquals("{\"hello\":\"a\"}", result.getResponse().getContentAsString());
    }

    @Test
    public void testHello1() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/a/hello").header("uber-trace-id", "1:1:1:1"))
                .andDo(print())
                .andReturn();
        Assert.assertEquals("{\"hello\":\"a\"}", result.getResponse().getContentAsString());
    }

    @Test
    public void contextLoads() {
    }

}
