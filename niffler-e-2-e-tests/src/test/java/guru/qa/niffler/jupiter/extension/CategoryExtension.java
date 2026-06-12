package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.SpendClient;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.service.SpendDbClient;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.UUID;

public class CategoryExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
//    private final SpendClient spendClient = new SpendApiClient();
    private final SpendClient spendClient = new SpendDbClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(
            context.getRequiredTestMethod(),
            Category.class
        ).ifPresent(
            anno -> {
                final CategoryJson created = spendClient.createCategory(
                    new CategoryJson(
                        null,
                        anno.name() + "_" + UUID.randomUUID().toString().substring(0, 8),
                        anno.username(),
                        anno.archived()
                    )
                );
                context.getStore(NAMESPACE).put(
                    context.getUniqueId(),
                    created
                );
            }
        );
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
    }

    @Override
    public CategoryJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE)
            .get(extensionContext.getUniqueId(), CategoryJson.class);
    }
}
