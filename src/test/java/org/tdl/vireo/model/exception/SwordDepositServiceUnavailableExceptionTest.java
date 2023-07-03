package org.tdl.vireo.model.exception;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.tdl.vireo.exception.SwordDepositServiceUnavailableException;

public class SwordDepositServiceUnavailableExceptionTest extends SwordDepositExceptionBase {

    @Test
    public void testNoArguments() throws Exception {
        when(depositLocationController.allDepositLocations()).thenThrow(new SwordDepositServiceUnavailableException());

        mvc.perform(get(DEPOSIT_ALL)).andExpect(status().isServiceUnavailable());
    }

    @Test
    public void testWithMessage() throws Exception {
        when(depositLocationController.allDepositLocations()).thenThrow(new SwordDepositServiceUnavailableException("message"));

        mvc.perform(get(DEPOSIT_ALL)).andExpect(status().isServiceUnavailable());
    }

    @Test
    public void testWithMessageAndException() throws Exception {
        when(depositLocationController.allDepositLocations()).thenThrow(new SwordDepositServiceUnavailableException("message", new RuntimeException("Stub")));

        mvc.perform(get(DEPOSIT_ALL)).andExpect(status().isServiceUnavailable());
    }

}
