package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.springframework.dao.DataIntegrityViolationException;

public class DepositLocationTest extends AbstractEntityTest {

    @Override
    public void testCreate() {
        DepositLocation depositLocation = depositLocationRepo.create(TEST_DEPOSIT_LOCATION_NAME);
        assertEquals("The deposit location name was wrong!", depositLocation.getName(), TEST_DEPOSIT_LOCATION_NAME);
        assertEquals("The default deposit location timeout was wrong!", depositLocation.getTimeout(), DepositLocation.DEFAULT_TIMEOUT);
        assertEquals("The desposit location was not saved!", 1, depositLocationRepo.count());
    }

    @Override
    public void testDuplication() {
        depositLocationRepo.create(TEST_DEPOSIT_LOCATION_NAME);
        try {
            depositLocationRepo.create(TEST_DEPOSIT_LOCATION_NAME);
        } 
        catch (DataIntegrityViolationException e) { /* SUCCESS */ }
        assertEquals("The desposit location was duplicated!", 1, depositLocationRepo.count());
    }

    @Override
    public void testDelete() {
        DepositLocation depositLocation = depositLocationRepo.create(TEST_DEPOSIT_LOCATION_NAME);
        depositLocationRepo.delete(depositLocation);
        assertEquals("The desposit location was duplicated!", 0, depositLocationRepo.count());
    }

    @Override
    public void testCascade() {
    }

    @After
    public void cleanUp() {
        depositLocationRepo.deleteAll();
    }

}
