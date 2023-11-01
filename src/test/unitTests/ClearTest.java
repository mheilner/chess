package unitTests;

import dataAccess.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import services.ClearService;

import static org.junit.jupiter.api.Assertions.*;

public class ClearTest {

    private ClearService clearService;

    @BeforeEach
    public void setUp() {
        clearService = new ClearService();
    }

    @Test
    @DisplayName("Test successful clearing of database")
    public void testClearDatabaseSuccess() throws DataAccessException {
        assertTrue(clearService.clear(), "Failed to clear the database.");
    }
}
