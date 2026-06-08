package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.service.SpendApiClient;
import guru.qa.niffler.service.SpendClient;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.UUID;

public class CreateCategoryExtension implements BeforeEachCallback {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CreateCategoryExtension.class);
    private final SpendClient spendClient = new SpendApiClient();

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
}
