package com.manorrock.aegean;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IndexPageIT {

    @Test
    public void testIndexPageTitle() {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();
            page.navigate("http://localhost:8080/aegean/index.html");
            assertEquals("Manorrock Aegean", page.title());
        }
    }
}
