/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thinkscience.demo.structurecleaner.servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import org.apache.catalina.core.ApplicationContext;

/**
 *
 * @author Mitch
 */
public class StructureFixerServletTest {

    public StructureFixerServletTest() {
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

	/**
	 * Test of processRequest method, of class StructureFixerServlet.
	 */
	//@Test
	public void testProcessRequest() throws Exception
	{
		System.out.println("processRequest");
		HttpServletRequest request = null;
		HttpServletResponse response = null;
		StructureFixerServlet instance = new StructureFixerServlet();
		instance.processRequest(request, response);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of doGet method, of class StructureFixerServlet.
	 */
	//@Test
	public void testDoGet() throws Exception
	{
		System.out.println("doGet");
		HttpServletRequest request = null;
		HttpServletResponse response = null;
		StructureFixerServlet instance = new StructureFixerServlet();
		instance.doGet(request, response);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of doPost method, of class StructureFixerServlet.
	 */
	//@Test
	public void testDoPost() throws Exception
	{
		System.out.println("doPost");
		HttpServletRequest request = null;
		HttpServletResponse response = null;
		StructureFixerServlet instance = new StructureFixerServlet();
		instance.doPost(request, response);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getServletInfo method, of class StructureFixerServlet.
	 */
	@Test
	public void testSetUpBasicQuery()
	{
		System.out.println("setUpBasicQuery");

		StructureFixerServlet instance = new StructureFixerServlet();

		ArrayList<String> tables = new ArrayList<String>();
		ArrayList<String> columns = new ArrayList<String>();
//		ServletContext con = new ServletContext();
//		instance.setUpBasicQuery( tables, columns);
		assertTrue("Must have table names and column names",
				(tables.size()>0 && columns.size()>0));
	}

}