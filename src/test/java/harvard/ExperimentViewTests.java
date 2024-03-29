package harvard;

import harvard.pages.ExperimentBlock;
import harvard.pages.ExperimentPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.testng.Assert.*;

@Test
public class ExperimentViewTests extends AbstractViewTests {

    @Autowired
    private ExperimentPage experimentPage;

    private String experimentName;
    private String experimentInstructions = "some instructions!";

    @BeforeMethod
    void setup() {
        experimentPage.go();
        assertEquals(experimentPage.getTitle(), "Experiments");

        experimentName = "test " + UUID.randomUUID().toString();
        experimentPage.createExperiment(experimentName, experimentInstructions);
        experimentPage.selectExperiment(experimentName);
    }

    @AfterMethod
    void cleanupMethod() {
        experimentPage.deleteExperiment(experimentName);
        assertFalse(experimentPage.containsExperiment(experimentName), "Experiment in list after delete.");
    }

    @Test
    void canModifyExperimentInstructions() {
        assertEquals(experimentPage.getInstructions(), "");
        experimentPage.setInstructions("Some stuff in the instructions.");
        assertEquals(experimentPage.getInstructions(), "Some stuff in the instructions.");
        experimentPage.save();
    }

    @Test
    void canAddBlockToExperiment() {
        assertEquals(experimentPage.getBlocks().size(), 0, "Before block is created, experiment should have one block.");
        experimentPage.addBlock();
        List<ExperimentBlock> blocks = experimentPage.getBlocks();
        assertEquals(blocks.size(), 1, "Experiment should have exactly one block.");
        ExperimentBlock block = blocks.get(0);
        assertBlockLabelsAreEqual(block, "", "", "", "");
        block.setUpperLeftLabel("upper left");
        block.setLowerLeftLabel("lower left");
        block.setUpperRightLabel("upper right");
        block.setLowerRightLabel("lower right");
        assertBlockLabelsAreEqual(block, "upper left", "lower left", "upper right", "lower right");

        assertEquals(block.getInstructions(), "");
        block.setInstructions("some instructions");
        assertEquals(block.getInstructions(), "some instructions");
    }

    @Test
    void experimentIsPersistedAfterSave() throws InterruptedException {
        String testName = "test " + UUID.randomUUID().toString();
        experimentPage.createExperiment(testName, experimentInstructions);
        experimentPage.go();
        Thread.sleep(1000);
        assertTrue(experimentPage.containsExperiment(testName), "Experiment was not persisted after refresh.");
        experimentPage.deleteExperiment(testName);
        experimentPage.go();
        Thread.sleep(1000);
        assertFalse(experimentPage.containsExperiment(testName), "Experiment was still in list after delete and refresh.");
    }

    private void assertBlockLabelsAreEqual(ExperimentBlock block, String upperLeft, String lowerLeft, String upperRight, String lowerRight) {
        assertEquals(block.getUpperLeftLabel(), upperLeft);
        assertEquals(block.getLowerLeftLabel(), lowerLeft);
        assertEquals(block.getUpperRightLabel(), upperRight);
        assertEquals(block.getLowerRightLabel(), lowerRight);
    }
}
