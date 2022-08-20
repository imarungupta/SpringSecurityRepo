package com.example.service.security.student;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(SpringJUnit4ClassRunner.class) // Instead of mockito we are using Spring JUnit 
public class TestHelloStudent {

	private MockMvc mockMvc;  // Came from spring frame work to mock any servlet 
	
	@InjectMocks
	private HelloStudentResource helloResource;
	
	@Before
	public void setUp() {
		mockMvc= MockMvcBuilders.standaloneSetup(helloResource).build();
		
	}
	// Test the controller and get method returning string 
	@Test
	public void TestStudentHello() throws Exception{
		mockMvc.perform(MockMvcRequestBuilders.get("/helloStudent"))
		       .andExpect(MockMvcResultMatchers.status().isOk())
		       .andExpect(MockMvcResultMatchers.content().string("Hello Student!!!"));		
	}
	// Now let's test the controller and get method is returning Json 
	
	@Test
	public void TesHelloStudentJson() throws Exception{
		mockMvc.perform(MockMvcRequestBuilders.get("/json").accept(MediaType.APPLICATION_JSON))
		       .andExpect(MockMvcResultMatchers.status().isOk())
		       .andExpect(MockMvcResultMatchers.jsonPath("$.studentId", Matchers.is(101), Student.class))
		       .andExpect(MockMvcResultMatchers.jsonPath("$.studentName", Matchers.is("Arun"), Student.class));
		      // .andExpect(MockMvcResultMatchers.jsonPath("$.studentId", Matchers.is(101)))
		      // .andExpect(MockMvcResultMatchers.jsonPath("$.studentName", Matchers.is("Arun")));
	}
}
