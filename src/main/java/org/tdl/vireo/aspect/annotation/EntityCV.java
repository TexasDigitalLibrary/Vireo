package org.tdl.vireo.aspect.annotation;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityCV {

    String name();

    Subset[] subsets() default {};

    @Target({ TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface Subset {

        String name();

        Filter[] filters();
    }

    @Target({ TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface Filter {

        String path();

        String value();
    }

}
