/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thinkscience.demo.utilities;

import com.thinkscience.demo.structurecleaner.objects.ifc.IQueryClause;
import com.thinkscience.demo.structurecleaner.objects.ifc.IQueryJoinItem;
import com.thinkscience.demo.structurecleaner.objects.ifc.IReturnedField;
import com.thinkscience.demo.structurecleaner.objects.impl.ReturnedField;
import com.thinkscience.demo.structurecleaner.objects.impl.StructureProcessor;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Mitch
 */
public class DatabaseUtilitiesTest {

    public DatabaseUtilitiesTest() {
    }

	@BeforeClass
	public static void setUpClass() throws Exception
	{
	}

	@AfterClass
	public static void tearDownClass() throws Exception
	{
	}

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

	private Connection getConnection()
	{
		String server = "localhost";
		String dbid = "THINK";
		String port = "1521";
		String user = "CHEM";
		String pw = "CHEM";
		DatabaseUtilities instance = new DatabaseUtilities();
		Connection result = instance.getOracleConnection(server, dbid, port, user, pw);
		return result;
	}
	/**
	 * Test of getOracleConnection method, of class DatabaseUtilities.
	 */
	@Test
	public void testGetOracleConnection()
	{
		System.out.println("getOracleConnection");
		String server = "localhost";
		String dbid = "ORCL";
		String port = "1521";
		String user = "CHEM";
		String pw = "CHEM";
		DatabaseUtilities instance = new DatabaseUtilities();
		Connection result = instance.getOracleConnection(server, dbid, port, user, pw);
		try
		{
			assertTrue(result != null && result.isValid(100) );
			result.close();
			result=null;
		}
		catch(SQLException ex)
		{
			fail("Error during testGetOracleConnection: " + ex.getMessage());
		}
	}

	@Test
	public void testrunGeneralCheshireSQL()
	{

		Connection con = getConnection();
		try
		{
		if( con == null || con.isClosed())
		{
			System.err.println("Error establishing Oracle connection");
			fail("No Oracle Connection");
		}
		StructureProcessor sp = new StructureProcessor();
		String erroroneousCommand = "Select oink from dual";

		boolean test = sp.runGeneralCheshireSQL(erroroneousCommand, con, null, null);

		assertFalse(test);

			}
		catch(Exception ex)
		{
			fail("Error during unit test: " + ex.getMessage());
		}
	}
	/**
	 * Test of getDataFromResultSet method, of class DatabaseUtilities.
	 */
	@Test
	public void testGetDataFromResultSet()
	{
		System.out.println("getDataFromResultSet");
		ResultSet rs = null;
		DatabaseUtilities instance = new DatabaseUtilities();
		ArrayList expResult = null;
		ArrayList result = instance.getDataFromResultSet(rs);
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of parseQualifiedColumnName method, of class DatabaseUtilities.
	 */
	@Test
	public void testParseQualifiedColumnName()
	{
		System.out.println("parseQualifiedColumnName");
		String qualifiedColumnName = "[tab1].field2";
		IReturnedField expResult = new ReturnedField("tab1","field2");
		IReturnedField result = DatabaseUtilities.parseQualifiedColumnName(qualifiedColumnName);
		//assertEquals(expResult, result);
		assertTrue( result.getFieldName().equals(expResult.getFieldName()) &&
				result.getTableName().equals(expResult.getTableName()));
	}

	/**
	 * Test of createQueryFromComponents method, of class DatabaseUtilities.
	 */
	@Test
	public void testCreateQueryFromComponents()
	{
		System.out.println("createQueryFromComponents");
		ArrayList<String> columns = null;
		ArrayList<String> tables = null;
		ArrayList<IQueryClause> clauseItems = null;
		ArrayList<IQueryJoinItem> joinItems = null;
		ArrayList<String> presetQueryWhereClauses = null;
		String expResult = "";
		String result = DatabaseUtilities.createQueryFromComponents(columns, tables, clauseItems, joinItems, presetQueryWhereClauses);
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	@Test
	public void testgetSQLOperator()
	{
		String inputOperator = "=";
		String inputValue = "IARC";

		String[] expResult = new String[2];
		expResult[0] = "=";
		expResult[1] = "IARC";

		String[] result = HttpUtilities.getSQLOperator(inputOperator, inputValue);

		assertArrayEquals(expResult, result);

		inputOperator = "starts";
		inputValue = "IAR";

		expResult[0] = "like";
		expResult[1] = "IAR%";

		result = HttpUtilities.getSQLOperator(inputOperator, inputValue);
		assertArrayEquals(expResult, result);

		inputOperator = "contains";
		inputValue = "IAR";

		expResult[0] = "like";
		expResult[1] = "%IAR%";

		result = HttpUtilities.getSQLOperator(inputOperator, inputValue);

		assertArrayEquals(expResult, result);

	}

	@Test
	public void testStringReplace()
	{
		String before ="[testtable1].field";
		String after = before.replace("[testtable1]", "T1");
		String afterExpected = "T1.field";
		assertEquals(afterExpected, after);
	}
	
	@Test
	public void saveMolfileTest()
	{
			String molfileText = "\n  MJ180200                      \n"
				+ "\n"
				+ " 45 49  0  0  0  0  0  0  0  0999 V2000\n"
				+ "    3.8110   -1.2019    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    3.9347   -0.3862    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    4.5975   -1.5879    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    3.0717   -1.6607    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    4.8232   -0.2551    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    3.3157    0.2109    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    5.2347   -0.9834    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    4.6558   -2.4290    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    3.1118   -2.4946    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    5.2347    0.4913    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    2.5400   -0.2296    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    3.8910   -2.8805    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    1.7535    0.1964    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    2.5400   -1.0562    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    1.7535    1.0558    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    0.9852   -0.2442    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    2.5400    1.4782    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    0.0639    0.1891    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    3.3157    1.0558    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    2.5219    2.3411    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "   -0.7517   -0.2442    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    0.0239    1.0193    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    4.0876    1.4782    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    3.3157    2.7598    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "   -1.5018    0.1964    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "    4.0876    2.3411    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "   -2.2884   -0.2296    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "   -1.5018    1.0558    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "   -3.0676    0.1964    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "   -2.3065   -1.0635    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "   -2.2884    1.4782    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "   -3.0676    1.0558    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "   -3.0859   -1.4786    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "   -1.5237   -1.4968    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "   -3.8469    1.4890    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "   -3.8469   -1.0562    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "   -4.6370    1.0593    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "   -4.6370   -0.6229    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "   -3.4027   -0.3388    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "   -4.3166   -1.7918    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "   -4.6443    0.1964    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "   -5.4164    1.4782    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "   -5.4164   -0.2078    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "   -6.1993    1.0593    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "   -6.2175    0.2109    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
				+ "  1  2  1  0  0  0  0\n"
				+ "  1  3  2  0  0  0  0\n"
				+ "  1  4  1  0  0  0  0\n"
				+ "  2  5  1  0  0  0  0\n"
				+ "  2  6  1  6  0  0  0\n"
				+ "  3  7  1  0  0  0  0\n"
				+ "  3  8  1  0  0  0  0\n"
				+ "  4  9  2  0  0  0  0\n"
				+ "  5 10  1  6  0  0  0\n"
				+ "  6 11  1  0  0  0  0\n"
				+ "  8 12  2  0  0  0  0\n"
				+ " 11 13  1  0  0  0  0\n"
				+ " 11 14  2  0  0  0  0\n"
				+ " 13 15  1  1  0  0  0\n"
				+ " 13 16  1  0  0  0  0\n"
				+ " 15 17  1  0  0  0  0\n"
				+ " 16 18  1  0  0  0  0\n"
				+ " 17 19  2  0  0  0  0\n"
				+ " 17 20  1  0  0  0  0\n"
				+ " 18 21  1  0  0  0  0\n"
				+ " 18 22  1  1  0  0  0\n"
				+ " 19 23  1  0  0  0  0\n"
				+ " 20 24  2  0  0  0  0\n"
				+ " 21 25  1  0  0  0  0\n"
				+ " 23 26  2  0  0  0  0\n"
				+ " 25 27  1  0  0  0  0\n"
				+ " 25 28  1  0  0  0  0\n"
				+ " 27 29  1  0  0  0  0\n"
				+ " 27 30  1  6  0  0  0\n"
				+ " 28 31  1  0  0  0  0\n"
				+ " 29 32  1  0  0  0  0\n"
				+ " 30 33  1  0  0  0  0\n"
				+ " 30 34  2  0  0  0  0\n"
				+ " 32 35  1  0  0  0  0\n"
				+ " 33 36  1  0  0  0  0\n"
				+ " 35 37  1  0  0  0  0\n"
				+ " 36 38  1  0  0  0  0\n"
				+ " 36 39  1  0  0  0  0\n"
				+ " 36 40  1  0  0  0  0\n"
				+ " 37 41  1  0  0  0  0\n"
				+ " 37 42  2  0  0  0  0\n"
				+ " 41 43  2  0  0  0  0\n"
				+ " 42 44  1  0  0  0  0\n"
				+ " 43 45  1  0  0  0  0\n"
				+ "  5  7  1  0  0  0  0\n"
				+ "  9 12  1  0  0  0  0\n"
				+ " 24 26  1  0  0  0  0\n"
				+ " 31 32  1  0  0  0  0\n"
				+ " 44 45  2  0  0  0  0\n"
				+ "M  END";

			Connection connection = getConnection();
			DatabaseUtilities dbUtilities = new DatabaseUtilities();
			String fileNameNoPath = java.util.UUID.randomUUID() + ".mol";
			String fullPath = dbUtilities.saveMolfile(molfileText, fileNameNoPath, connection);
			java.io.File testFile = new File(fullPath);
			
			assertTrue(testFile.exists());
	}
}