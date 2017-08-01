package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.springframework.dao.DataIntegrityViolationException;
import org.tdl.vireo.model.formatter.DSpaceMetsFormatter;

public class DepositLocationTest extends AbstractEntityTest {

    @Before
    public void setup() {
        packager = abstractPackagerRepo.createDSpaceMetsPackager(new DSpaceMetsFormatter());
    }

    @Override
    public void testCreate() {
        DepositLocation depositLocation = depositLocationRepo.create(TEST_DEPOSIT_LOCATION_NAME, TEST_DEPOSIT_REPOSITORY, TEST_DEPOSIT_COLLECTION, TEST_DEPOSIT_USERNAME, TEST_DEPOSIT_PASSWORD, TEST_DEPOSIT_ONBEHALFOF, packager, TEST_DEPOSIT_DEPOSITOR, DepositLocation.DEFAULT_TIMEOUT);
        assertEquals("The deposit location name was wrong!", depositLocation.getName(), TEST_DEPOSIT_LOCATION_NAME);
        assertEquals("The default deposit location timeout was wrong!", depositLocation.getTimeout(), DepositLocation.DEFAULT_TIMEOUT);
        assertEquals("The desposit location was not saved!", 1, depositLocationRepo.count());
    }

    @Override
    public void testDuplication() {
        depositLocationRepo.create(TEST_DEPOSIT_LOCATION_NAME, TEST_DEPOSIT_REPOSITORY, TEST_DEPOSIT_COLLECTION, TEST_DEPOSIT_USERNAME, TEST_DEPOSIT_PASSWORD, TEST_DEPOSIT_ONBEHALFOF, packager, TEST_DEPOSIT_DEPOSITOR, DepositLocation.DEFAULT_TIMEOUT);
        try {
            depositLocationRepo.create(TEST_DEPOSIT_LOCATION_NAME, TEST_DEPOSIT_REPOSITORY, TEST_DEPOSIT_COLLECTION, TEST_DEPOSIT_USERNAME, TEST_DEPOSIT_PASSWORD, TEST_DEPOSIT_ONBEHALFOF, packager, TEST_DEPOSIT_DEPOSITOR, DepositLocation.DEFAULT_TIMEOUT);
        } catch (DataIntegrityViolationException e) {
        }
        assertEquals("The desposit location was duplicated!", 1, depositLocationRepo.count());
    }

    @Override
    public void testDelete() {
        DepositLocation depositLocation = depositLocationRepo.create(TEST_DEPOSIT_LOCATION_NAME, TEST_DEPOSIT_REPOSITORY, TEST_DEPOSIT_COLLECTION, TEST_DEPOSIT_USERNAME, TEST_DEPOSIT_PASSWORD, TEST_DEPOSIT_ONBEHALFOF, packager, TEST_DEPOSIT_DEPOSITOR, DepositLocation.DEFAULT_TIMEOUT);
        depositLocationRepo.delete(depositLocation);
        assertEquals("The desposit location was not deleted!", 0, depositLocationRepo.count());
    }

    @Override
    public void testCascade() {

    }

    @After
    public void cleanUp() {
        depositLocationRepo.deleteAll();
        abstractPackagerRepo.deleteAll();
    }

}
