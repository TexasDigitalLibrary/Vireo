package org.tdl.vireo.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.enums.Role;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.framework.enums.ApiResponseType;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;

public class DepositLocationControllerTest extends AbstractControllerTest {

	@Before
	public void setup() {

	}

	@Test
	@Order(value = 1)
	public void testAllDepositLocations() {
		
	}

	@Test
	@Order(value = 2)
	public void testCreateDepositLocation() {

	}

	@Test
	@Order(value = 3)
	public void testReorderDepositLocations() {

	}
	
	@After
	public void cleanup() {

	}

}
