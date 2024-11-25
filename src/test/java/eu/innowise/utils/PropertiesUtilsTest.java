package eu.innowise.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PropertiesUtilsTest {

    @Test
    void getProperty_shouldLoadProperty_whenFileExists() {
        // when & then
        Assertions.assertEquals("db/migrations", PropertiesUtils.getProperty("migration.folder"));
    }

    @Test
    void getProperty_shouldReturnNull_whenKeyDoesNotExist() {
        // when
        String result = PropertiesUtils.getProperty("non.existent.key");

        // then
        Assertions.assertNull(result);
    }
}