package controller.integration.excel;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TranslitTest {
    @Test
    public void getTranslitedRusToEngWordTest() {
        String russian = "Тест";
        String expected = "test";
        String actual = Translit.getTranslitedRusToEngWord(russian);
        Assert.assertEquals(actual, expected);
    }
}
