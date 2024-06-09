package com.example.huang.myapplication;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;


public class categoryActivityTest {

    @Test
    public void onCreate() throws IOException {
        assertEquals("aaa", getJsonString.convertInputStreamToString2("aaa"));
    }
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
}