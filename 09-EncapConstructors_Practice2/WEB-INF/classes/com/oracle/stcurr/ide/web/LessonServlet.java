/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.stcurr.ide.web;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 *
 * @author mheimer
 */
@WebServlet(name = "LessonServlet", urlPatterns = {"/lessons/*","/lesson-solutions/*"})
public class LessonServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String[] uriParts = request.getRequestURI().split("/");
        String exerciseName = uriParts[uriParts.length - 1];
        String chapterName = uriParts[uriParts.length - 2];
        request.setAttribute("exerciseName", exerciseName);
        request.setAttribute("chapterName", chapterName);

        LessonReader lessonReader = new LessonReader();
        Map<Path,String> files;
        if (uriParts[uriParts.length - 3].equals("lessons")) {
            request.setAttribute("isSolution", false);
            files = lessonReader.getExerciseFiles(chapterName, exerciseName);
        } else {
            request.setAttribute("isSolution", true);
            files = lessonReader.getSolutionFiles(chapterName, exerciseName);
        }
        StringBuilder tabsArray = new StringBuilder();
        tabsArray.append("[");
        for(Path path : files.keySet()) {
            tabsArray.append("{ name : ");
            tabsArray.append("'");
            tabsArray.append(path.getFileName());
            tabsArray.append("', text : ");
            tabsArray.append("'");
            tabsArray.append(StringEscapeUtils.escapeEcmaScript(files.get(path)));
            tabsArray.append("'");
            tabsArray.append("},");
        }
        tabsArray.append("]");
        request.setAttribute("tabs", tabsArray.toString());
        String readme = lessonReader.getReadme(chapterName, exerciseName);
        if(readme != null) {
            request.setAttribute("readme", readme);
        }

        RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/lesson.jsp");
        rd.forward(request, response);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
