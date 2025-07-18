package io.github.tobyrue.xml;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface XML {

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    @interface Parent {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    @interface Children {
        Class<? extends XMLNode>[] allow() default {};
        boolean omitBlankText() default true;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @interface Root {
        boolean strict() default true;
        boolean ignoreCase() default true;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.PARAMETER})
    @interface Name {
        String value();
        String TEXT = "[[text]]";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    @interface Attribute {
        String fallBack() default NO_FALLBACK_VALUE;
        String NO_FALLBACK_VALUE = "\0";
    }
}
