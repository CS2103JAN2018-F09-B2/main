package seedu.address.storage;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static seedu.address.testutil.TypicalPersons.getTypicalRecipeBook;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import seedu.address.commons.events.model.RecipeBookChangedEvent;
import seedu.address.commons.events.storage.DataSavingExceptionEvent;
import seedu.address.model.ReadOnlyRecipeBook;
import seedu.address.model.RecipeBook;
import seedu.address.model.UserPrefs;
import seedu.address.ui.testutil.EventsCollectorRule;

public class StorageManagerTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    @Rule
    public final EventsCollectorRule eventsCollectorRule = new EventsCollectorRule();

    private StorageManager storageManager;

    @Before
    public void setUp() {
        XmlRecipeBookStorage addressBookStorage = new XmlRecipeBookStorage(getTempFilePath("ab"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(getTempFilePath("prefs"));
        storageManager = new StorageManager(addressBookStorage, userPrefsStorage);
    }

    private String getTempFilePath(String fileName) {
        return testFolder.getRoot().getPath() + fileName;
    }


    @Test
    public void prefsReadSave() throws Exception {
        /*
         * Note: This is an integration test that verifies the StorageManager is properly wired to the
         * {@link JsonUserPrefsStorage} class.
         * More extensive testing of UserPref saving/reading is done in {@link JsonUserPrefsStorageTest} class.
         */
        UserPrefs original = new UserPrefs();
        original.setGuiSettings(300, 600, 4, 6);
        storageManager.saveUserPrefs(original);
        UserPrefs retrieved = storageManager.readUserPrefs().get();
        assertEquals(original, retrieved);
    }

    @Test
    public void addressBookReadSave() throws Exception {
        /*
         * Note: This is an integration test that verifies the StorageManager is properly wired to the
         * {@link XmlRecipeBookStorage} class.
         * More extensive testing of UserPref saving/reading is done in {@link XmlRecipeBookStorageTest} class.
         */
        RecipeBook original = getTypicalRecipeBook();
        storageManager.saveRecipeBook(original);
        ReadOnlyRecipeBook retrieved = storageManager.readRecipeBook().get();
        assertEquals(original, new RecipeBook(retrieved));
    }

    @Test
    public void getRecipeBookFilePath() {
        assertNotNull(storageManager.getRecipeBookFilePath());
    }

    @Test
    public void handleRecipeBookChangedEvent_exceptionThrown_eventRaised() {
        // Create a StorageManager while injecting a stub that  throws an exception when the save method is called
        Storage storage = new StorageManager(new XmlRecipeBookStorageExceptionThrowingStub("dummy"),
                                             new JsonUserPrefsStorage("dummy"));
        storage.handleRecipeBookChangedEvent(new RecipeBookChangedEvent(new RecipeBook()));
        assertTrue(eventsCollectorRule.eventsCollector.getMostRecent() instanceof DataSavingExceptionEvent);
    }


    /**
     * A Stub class to throw an exception when the save method is called
     */
    class XmlRecipeBookStorageExceptionThrowingStub extends XmlRecipeBookStorage {

        public XmlRecipeBookStorageExceptionThrowingStub(String filePath) {
            super(filePath);
        }

        @Override
        public void saveRecipeBook(ReadOnlyRecipeBook addressBook, String filePath) throws IOException {
            throw new IOException("dummy exception");
        }
    }


}
