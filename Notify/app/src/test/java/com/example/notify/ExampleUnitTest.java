package com.example.notify;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void testTitleSetterGetter() {
        Notes notes = new Notes();
        notes.setTitle("Sample Title");
        assertEquals("Sample Title", notes.getTitle());
    }
}
