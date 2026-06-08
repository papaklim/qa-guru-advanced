package guru.qa.niffler.jupiter.annotation;

import guru.qa.niffler.jupiter.extension.CreateCategoryExtension;
import guru.qa.niffler.jupiter.extension.CategoryResolverExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith({CreateCategoryExtension.class, CategoryResolverExtension.class})
public @interface Category {
    String name() default "test_category";

    String username();

    boolean archived();
}
