package org.tdl.vireo.model;

import org.junit.After;

public class DepositLocationTest extends AbstractEntityTest {

    @Override
    public void testCreate() {
    	/*
        DepositLocation depositLocation = depositLocationRepo.create(TEST_DEPOSIT_LOCATION_NAME, TEST_DEPOSIT_REPOSITORY, TEST_DEPOSIT_COLLECTION, TEST_DEPOSIT_USERNAME, TEST_DEPOSIT_PASSWORD, TEST_DEPOSIT_ONBEHALFOF, TEST_DEPOSIT_PACKAGER, TEST_DEPOSIT_DEPOSITOR);
        assertEquals("The deposit location name was wrong!", depositLocation.getName(), TEST_DEPOSIT_LOCATION_NAME);
        assertEquals("The default deposit location timeout was wrong!", depositLocation.getTimeout(), DepositLocation.DEFAULT_TIMEOUT);
        assertEquals("The desposit location was not saved!", 1, depositLocationRepo.count());
        */
    }

    @Override
    public void testDuplication() {
    	/*
        depositLocationRepo.create(TEST_DEPOSIT_LOCATION_NAME, TEST_DEPOSIT_REPOSITORY, TEST_DEPOSIT_COLLECTION, TEST_DEPOSIT_USERNAME, TEST_DEPOSIT_PASSWORD, TEST_DEPOSIT_ONBEHALFOF, TEST_DEPOSIT_PACKAGER, TEST_DEPOSIT_DEPOSITOR);
        try {
            depositLocationRepo.create(TEST_DEPOSIT_LOCATION_NAME, TEST_DEPOSIT_REPOSITORY, TEST_DEPOSIT_COLLECTION, TEST_DEPOSIT_USERNAME, TEST_DEPOSIT_PASSWORD, TEST_DEPOSIT_ONBEHALFOF, TEST_DEPOSIT_PACKAGER, TEST_DEPOSIT_DEPOSITOR);
        } catch (DataIntegrityViolationException e) {}
        assertEquals("The desposit location was duplicated!", 1, depositLocationRepo.count());
        */
    }

    @Override
    public void testDelete() {
    	/*
        DepositLocation depositLocation = depositLocationRepo.create(TEST_DEPOSIT_LOCATION_NAME, TEST_DEPOSIT_REPOSITORY, TEST_DEPOSIT_COLLECTION, TEST_DEPOSIT_USERNAME, TEST_DEPOSIT_PASSWORD, TEST_DEPOSIT_ONBEHALFOF, TEST_DEPOSIT_PACKAGER, TEST_DEPOSIT_DEPOSITOR);
        depositLocationRepo.delete(depositLocation);
        assertEquals("The desposit location was duplicated!", 0, depositLocationRepo.count());
        */
    }

    @Override
    public void testCascade() {

    }

    @After
    public void cleanUp() {
        depositLocationRepo.deleteAll();
    }

}
