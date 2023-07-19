package org.tdl.vireo.model.exception;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.tdl.vireo.controller.DepositLocationController;
import org.tdl.vireo.controller.advice.CustomResponseEntityExceptionHandler;

public class SwordDepositExceptionBase {

    protected static String DEPOSIT_ALL = "/settings/deposit-location/all";

    protected MockMvc mvc;

    @Mock
    protected DepositLocationController depositLocationController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        mvc = MockMvcBuilders.standaloneSetup(depositLocationController)
            .setControllerAdvice(new CustomResponseEntityExceptionHandler())
            .build();
    }

}
