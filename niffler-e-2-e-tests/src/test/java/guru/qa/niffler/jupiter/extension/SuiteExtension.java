package guru.qa.niffler.jupiter.extension;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public interface SuiteExtension extends BeforeAllCallback {

    /*
     * 1. Нужно быть уверенным, что SuiteExtension будет выполняться перед каждым
     * тестовым классом
     * 2. Если выполним какую-либо логику перед загрузкой тестового класса - это и
     * будет beforeSuite()
     * 3. Для остальных тестовых классов больше не должен вызываться beforeSuite()
     * 4. После завершения всех тестов вызывается afterSuite()
     */
    @Override
    default void beforeAll(ExtensionContext context) throws Exception {
        final ExtensionContext rooContext = context.getRoot();
        rooContext.getStore(ExtensionContext.Namespace.GLOBAL)
            .getOrComputeIfAbsent(
                this.getClass(),
                key -> {
                    beforeSuite(rooContext);
                    return new ExtensionContext.Store.CloseableResource() {
                        @Override
                        public void close() throws Throwable {
                            afterSuite();
                        }
                    };
                });
    }

    default void beforeSuite(ExtensionContext context) {
    }

    default void afterSuite() {
    }

}
