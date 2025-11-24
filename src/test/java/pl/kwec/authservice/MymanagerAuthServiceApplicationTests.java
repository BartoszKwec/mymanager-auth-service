package pl.kwec.authservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MymanagerAuthServiceApplication Tests")
class MymanagerAuthServiceApplicationTests {

    @Test
    @DisplayName("should have SpringBootApplication annotation")
    void shouldHaveSpringBootApplicationAnnotation() {
        assertTrue(MymanagerAuthServiceApplication.class.isAnnotationPresent(SpringBootApplication.class),
                "MymanagerAuthServiceApplication should be annotated with @SpringBootApplication");
    }

    @Test
    @DisplayName("should have main method")
    void shouldHaveMainMethod() {
        assertDoesNotThrow(() -> {
            var method = MymanagerAuthServiceApplication.class.getMethod("main", String[].class);
            assertNotNull(method, "main method should not be null");
            assertTrue(java.lang.reflect.Modifier.isStatic(method.getModifiers()),
                    "main method should be static");
            assertTrue(java.lang.reflect.Modifier.isPublic(method.getModifiers()),
                    "main method should be public");
        });
    }

    @Test
    @DisplayName("main method should have correct parameter type")
    void shouldHaveCorrectMainMethodParameterType() {
        assertDoesNotThrow(() -> {
            var method = MymanagerAuthServiceApplication.class.getMethod("main", String[].class);
            var parameters = method.getParameterTypes();
            assertEquals(1, parameters.length, "main method should have exactly one parameter");
            assertEquals(String[].class, parameters[0], "main method parameter should be String[]");
        });
    }

}
