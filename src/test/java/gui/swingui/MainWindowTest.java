package gui.swingui;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.testng.testcase.AssertJSwingTestngTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

public class MainWindowTest extends AssertJSwingTestngTestCase {

    private FrameFixture window;

    @Override
    protected void onSetUp() {
        MainWindow frame = GuiActionRunner.execute(MainWindow::new);
        // IMPORTANT: note the call to 'robot()'
        // we must use the Robot from AssertJSwingJUnitTestCase
        window = new FrameFixture(robot(), frame);
        window.show(); // shows the frame to test
    }

    @Test
    public void simpleSearchTest() {
        window.comboBox("searchCombo").clearSelection().enterText("1");
        window.button("searchButton").click();
        window.table("mainTable").requireRowCount(1);
    }

    @AfterClass
    public void onTearDown() {
        window.cleanUp();
    }
}
