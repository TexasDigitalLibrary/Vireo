package org.tdl.vireo.model;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public class RoleTest extends AbstractEnumTest<Role> {

    protected static Stream<Arguments> provideEnumParameters() {
        return Stream.of(
            Arguments.of(Role.ROLE_ADMIN, "ROLE_ADMIN", 0),
            Arguments.of(Role.ROLE_MANAGER, "ROLE_MANAGER", 1),
            Arguments.of(Role.ROLE_REVIEWER, "ROLE_REVIEWER", 2),
            Arguments.of(Role.ROLE_STUDENT, "ROLE_STUDENT", 3),
            Arguments.of(Role.ROLE_ANONYMOUS, "ROLE_ANONYMOUS", 4) 
        );
    }

}
