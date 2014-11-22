/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fetcher.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Ryan Gilera
 */
public class UtilsTest {
    
    public UtilsTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of isValidURL method, of class Utils.
     */
    @Test
    public void testIsValidURLForValidURL() {
        // given:
        String URL_String = "http://google.com";
        
        // when:
        boolean result = Utils.isValidURL(URL_String);
        
        // then:
        assertTrue(result);
    }
    
    /**
     * Test of isValidURL method, of class Utils.
     */
    @Test
    public void testIsValidURLForInvalidURL() {
        // given:
        String URL_String = "dummy text";
        
        // when:
        boolean result = Utils.isValidURL(URL_String);
        
        // then:
        assertFalse(result);
    }

    /**
     * Test of exists method, of class Utils.
     */
    @Test
    public void testExistsValid() {
        // given:
        boolean expResult = true;
        Integer inputArgument = 1;
        List<Integer> testList = new ArrayList<Integer>();
        testList.add(1);
        
        // when:
        boolean result = Utils.exists(testList, inputArgument);
        
        // then:
        assertEquals(expResult, result);
    }
    
    /**
     * Test of exists method, of class Utils.
     */
    @Test
    public void testExistsInvalid() {
        // given:
        boolean expResult = false;
        Integer inputArgument = 2;
        List<Integer> testList = new ArrayList<Integer>();
        testList.add(1);
        
        // when:
        boolean result = Utils.exists(testList, inputArgument);
        
        // then:
        assertEquals(expResult, result);
    }

    /**
     * Test of convertToObservableList method, of class Utils.
     */
    @Test
    public void testConvertToObservableListValid() {
        // given:
        HashSet<Integer> aHashSetObj = new HashSet<Integer>();
        aHashSetObj.add(1);
        aHashSetObj.add(2);
        
        ObservableList expResult = FXCollections.observableArrayList();
        expResult.add(1);
        expResult.add(2);
        
        // when:
        ObservableList result = Utils.convertToObservableList(aHashSetObj);
        
        // then:
        assertEquals(expResult, result);
        assertEquals(expResult.size(), result.size());
        assertEquals(expResult.get(0), result.get(0));
        assertEquals(expResult.get(1), result.get(1));
    }
    
    /**
     * Test of convertToObservableList method, of class Utils.
     */
    @Test
    public void testConvertToObservableListEmpty() {
        // given:
        HashSet<Integer> aHashSetObj = new HashSet<Integer>();
        ObservableList expResult = FXCollections.observableArrayList();
        
        // when:
        ObservableList result = Utils.convertToObservableList(aHashSetObj);
        
        // then:
        assertEquals(expResult, result);
        assertEquals(expResult.size(), result.size());
    }

    /**
     * Test of FileExists method, of class Utils.
     */
    @Test
    public void testFileExistsInvalid() {
        // given:
        String file = "dummy";
        boolean expResult = false;
        
        // when:
        boolean result = Utils.FileExists(file);
        
        // then:
        assertEquals(expResult, result);
    }
    
    // TODO: testFileExistsValid()
    
}
