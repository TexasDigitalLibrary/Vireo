package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.dao.DataIntegrityViolationException;
import org.tdl.vireo.model.formatter.DSpaceMetsFormatter;

public class DepositLocationTest extends AbstractEntityTest {

    @BeforeEach
    public void setup() {
        packager = abstractPackagerRepo.createDSpaceMetsPackager("DSpaceMETS", new DSpaceMetsFormatter());
    }

    @Override
    public void testCreate() {
        DepositLocation depositLocation = depositLocationRepo.create(TEST_DEPOSIT_LOCATION_NAME, TEST_DEPOSIT_REPOSITORY, TEST_DEPOSIT_COLLECTION, TEST_DEPOSIT_USERNAME, TEST_DEPOSIT_PASSWORD, TEST_DEPOSIT_ONBEHALFOF, packager, TEST_DEPOSIT_DEPOSITOR, DepositLocation.DEFAULT_TIMEOUT);
        assertEquals(depositLocation.getName(), TEST_DEPOSIT_LOCATION_NAME, "The deposit location name was wrong!");
        assertEquals(depositLocation.getTimeout(), DepositLocation.DEFAULT_TIMEOUT, "The default deposit location timeout was wrong!");
        assertEquals(1, depositLocationRepo.count(), "The desposit location was not saved!");
    }

    @Override
    public void testDuplication() {
        depositLocationRepo.create(TEST_DEPOSIT_LOCATION_NAME, TEST_DEPOSIT_REPOSITORY, TEST_DEPOSIT_COLLECTION, TEST_DEPOSIT_USERNAME, TEST_DEPOSIT_PASSWORD, TEST_DEPOSIT_ONBEHALFOF, packager, TEST_DEPOSIT_DEPOSITOR, DepositLocation.DEFAULT_TIMEOUT);
        try {
            depositLocationRepo.create(TEST_DEPOSIT_LOCATION_NAME, TEST_DEPOSIT_REPOSITORY, TEST_DEPOSIT_COLLECTION, TEST_DEPOSIT_USERNAME, TEST_DEPOSIT_PASSWORD, TEST_DEPOSIT_ONBEHALFOF, packager, TEST_DEPOSIT_DEPOSITOR, DepositLocation.DEFAULT_TIMEOUT);
        } catch (DataIntegrityViolationException e) {
        }
        assertEquals(1, depositLocationRepo.count(), "The desposit location was duplicated!");
    }

    @Override
    public void testDelete() {
        DepositLocation depositLocation = depositLocationRepo.create(TEST_DEPOSIT_LOCATION_NAME, TEST_DEPOSIT_REPOSITORY, TEST_DEPOSIT_COLLECTION, TEST_DEPOSIT_USERNAME, TEST_DEPOSIT_PASSWORD, TEST_DEPOSIT_ONBEHALFOF, packager, TEST_DEPOSIT_DEPOSITOR, DepositLocation.DEFAULT_TIMEOUT);
        depositLocationRepo.delete(depositLocation);
        assertEquals(0, depositLocationRepo.count(), "The desposit location was not deleted!");
    }

    @Override
    public void testCascade() {

    }

    @AfterEach
    public void cleanUp() {
        depositLocationRepo.deleteAll();
        abstractPackagerRepo.deleteAll();
    }

}
