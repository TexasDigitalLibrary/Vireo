package org.tdl.vireo.model.exception;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.tdl.vireo.controller.advice.CustomResponseEntityExceptionHandler;
import org.tdl.vireo.exception.SwordDepositInternalServerErrorException;

public class SwordDepositInternalServerErrorExceptionTest extends SwordDepositExceptionBase {

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        mvc = MockMvcBuilders.standaloneSetup(depositLocationController)
            .setControllerAdvice(new CustomResponseEntityExceptionHandler())
            .build();
    }

    @Test
    public void testNoArguments() throws Exception {
        when(depositLocationController.allDepositLocations()).thenThrow(new SwordDepositInternalServerErrorException());

        mvc.perform(get(DEPOSIT_ALL)).andExpect(status().isInternalServerError());
    }

    @Test
    public void testWithMessage() throws Exception {
        when(depositLocationController.allDepositLocations()).thenThrow(new SwordDepositInternalServerErrorException("message"));

        mvc.perform(get(DEPOSIT_ALL)).andExpect(status().isInternalServerError());
    }

    @Test
    public void testWithMessageAndException() throws Exception {
        when(depositLocationController.allDepositLocations()).thenThrow(new SwordDepositInternalServerErrorException("message", new RuntimeException("Stub")));

        mvc.perform(get(DEPOSIT_ALL)).andExpect(status().isInternalServerError());
    }

}
