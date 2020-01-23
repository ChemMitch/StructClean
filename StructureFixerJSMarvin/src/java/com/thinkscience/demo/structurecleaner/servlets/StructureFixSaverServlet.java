/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thinkscience.demo.structurecleaner.servlets;

import com.thinkscience.demo.structurecleaner.objects.ifc.IStructureProcessor;
import com.thinkscience.demo.structurecleaner.objects.impl.StructureProcessor;
import com.thinkscience.demo.utilities.ArrayListUtilities;
import com.thinkscience.demo.utilities.DatabaseUtilities;
import com.thinkscience.demo.utilities.HttpUtilities;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Mitch
 */
public class StructureFixSaverServlet extends HttpServlet
{

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		response.setContentType("text/html;charset=UTF-8");
		HttpUtilities httpUtil = new HttpUtilities();
		HttpSession currSession = request.getSession();

		String tempVal = getServletContext().getInitParameter("StructureIDRequiresQuotes");
		Boolean structureIDRequiresQuotes = false;
		if (tempVal != null && tempVal.toUpperCase().equals("TRUE"))
		{
			structureIDRequiresQuotes = true;
		}

		ArrayList<String> IDsToIgnore = new ArrayList<String>();

		ArrayList<String> IDsToProcess = httpUtil.getSelectedIDsFromRequest(request,
				"includeID_", structureIDRequiresQuotes, IDsToIgnore);

		ArrayList<String> knownIDsToProcess = (ArrayList<String>)
				currSession.getAttribute("IDsToProcess");
		ArrayListUtilities.DumpArrayList(IDsToIgnore, 
				"[StructureFixSaverServlet] IDs to ignore:");
		ArrayListUtilities.DumpArrayList(IDsToProcess, 
				"[StructureFixSaverServlet] IDs from Request object:");

		ArrayListUtilities.DumpArrayList(knownIDsToProcess, 
				"[StructureFixSaverServlet] IDs from Session object:");

		ArrayListUtilities.ConsolidateArrayLists(knownIDsToProcess, IDsToProcess,
				IDsToIgnore);

		ArrayListUtilities.DumpArrayList(IDsToProcess,
				"[StructureFixSaverServlet] Consolidated IDs to process:");

		//Now run Cheshire
		IStructureProcessor structureProcessor = new StructureProcessor();
		String chemicalTable = getServletContext().getInitParameter("ChemTable");

		String cheshireInitString = getServletContext().getInitParameter("CheshireInit");
		String cheshireTransformQueryString = getServletContext().getInitParameter("CheshireUpdateSQL");
		
		Connection conn = (Connection) currSession.getAttribute("OracleConnection");
		try
		{
			if (conn == null || conn.isClosed())
			{
				httpUtil.processError("Error in StructureFixSaverServlet; Connection is closed ",
						request, response, getServletContext());
			}
		} catch (SQLException ex)
		{
		}
		StringBuilder errorMessage = null;
		//initialize the Cheshire environment
		if (!structureProcessor.initCheshire(cheshireInitString, conn, errorMessage))
		{
			httpUtil.processError(errorMessage.toString(), request, response,
					getServletContext());
		}
		getServletContext().log("called initCheshire with SQL " + cheshireInitString);

		//Load the template molecule
		String cheshireLoadMolString = getServletContext().getInitParameter("CheshireLoadMol");
		String structureAsChimestring = (String) currSession.getAttribute("OrientationStructure");
		String molfileName = java.util.UUID.randomUUID() + ".mol";
		DatabaseUtilities dbUtilities = new DatabaseUtilities();
		String tempMolfilePath = dbUtilities.saveMolfile(structureAsChimestring, molfileName, conn);

		if (!structureProcessor.runCheshireMoleculeLoadingSQL(tempMolfilePath,
				cheshireLoadMolString, conn, errorMessage, " loading template molecule"))
		{
			httpUtil.processError(errorMessage.toString(), request, response,
					getServletContext());
		}
		getServletContext().log(String.format("loading structure '%s' using SQL %s",
				structureAsChimestring, cheshireLoadMolString));

		String IDFieldName = getServletContext().getInitParameter("StructureIdField");
		String cheshireScriptSQL = "";
		//look for an indication that the user intends to run clean before orient
		String cleanBeforeOrienting = (String) currSession.getAttribute("CleanBeforeOrienting");
		if (cleanBeforeOrienting != null && cleanBeforeOrienting.length() > 0
				&& cleanBeforeOrienting.toUpperCase().equals("TRUE"))
		{
			//Load the script function
			String cheshireLoadScriptSQL = getServletContext().getInitParameter("ScriptLoadingSQL");

			if(!structureProcessor.runGeneralCheshireSQL(cheshireLoadScriptSQL, conn,
				errorMessage, " loading Cheshire function"))
			{
				httpUtil.processError(errorMessage.toString(), request, response,
					getServletContext());
			}
			getServletContext().log("Loading script using SQL: " + cheshireLoadMolString);
			cheshireScriptSQL = getServletContext().getInitParameter("ScriptRunningSQL");
		}
		else
		{
			getServletContext().log("Skipped loading script!  Will orient but not clean" );
			cheshireScriptSQL = getServletContext().getInitParameter("ScriptRunningSQLOrientOnly");
		}


		StringBuilder errorMessages = new StringBuilder();
		boolean result = structureProcessor.processUsingCheshire(chemicalTable,
				structureAsChimestring, IDsToProcess, cheshireScriptSQL,
				cheshireTransformQueryString, IDFieldName,
				conn, errorMessages);

		if (!result)
		{
			httpUtil.processError(errorMessages.toString(), request, response,
					getServletContext());
		} else
		{

			request.getSession().setAttribute("IDsToProcess", IDsToProcess);
			request.setAttribute("IDsToProcess", IDsToProcess);
			request.setAttribute("ProcessingResult", result);

			httpUtil.forwardEasy("/processingresult.jsp", request, response,
					getServletContext());
		}
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo()
	{
		return "Short description";
	}// </editor-fold>
}
