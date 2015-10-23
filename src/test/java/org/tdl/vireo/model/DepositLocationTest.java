package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.tdl.vireo.Application;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class DepositLocationTest extends AbstractEntityTest {

	@Test
	@Order(value = 1)
	public void testCreate() {
		DepositLocation depositLocation = depositLocationRepo.create(TEST_DEPOSIT_LOCATION_NAME);
		assertEquals("The deposit location name was wrong!", depositLocation.getName(), TEST_DEPOSIT_LOCATION_NAME);
		assertEquals("The default deposit location timeout was wrong!", depositLocation.getTimeout(),
				DepositLocation.DEFAULT_TIMEOUT);
		assertEquals("The desposit location was not saved!", 1, depositLocationRepo.count());
	}

	@Test
	@Order(value = 2)
	public void testDuplicate() {
		depositLocationRepo.create(TEST_DEPOSIT_LOCATION_NAME);
		try {
			depositLocationRepo.create(TEST_DEPOSIT_LOCATION_NAME);
		} catch (DataIntegrityViolationException e) {
			/* SUCCESS */
		}
		assertEquals("The desposit location was duplicated!", 1, depositLocationRepo.count());
	}

	@Test
	@Order(value = 3)
	public void testDelete() {
		DepositLocation depositLocation = depositLocationRepo.create(TEST_DEPOSIT_LOCATION_NAME);
		depositLocationRepo.delete(depositLocation);
		assertEquals("The desposit location was duplicated!", 0, depositLocationRepo.count());
	}

	@Override
	public void testDuplication() {
	}

	@Override
	public void testFind() {
	}

	@Override
	public void testCascade() {
	}

	@After
	public void cleanUp() {
		depositLocationRepo.deleteAll();
	}

}
