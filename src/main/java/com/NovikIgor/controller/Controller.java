package com.NovikIgor.controller;

import com.NovikIgor.dao.entity.ClientType;
import com.NovikIgor.dao.mock.UserMock;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Locale;

/**
 * This is controllerServlet class for MVC model of InternetBanking.
 * <p>
 * Created by nolik on 15.09.16.
 */

@WebServlet("/Controller")
public class Controller extends HttpServlet {
    private static final long serialVersionUID = -4051736549894026861L;

    private static Logger logger = Logger.getLogger(Controller.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("loc", Locale.getDefault());

        /**
         * This is testing Moc for Login Form -> check index.jsp
         */
        UserMock userMock = new UserMock("nolik", "123");

        String login = String.valueOf(req.getParameter("login"));
        String password = String.valueOf(req.getParameter("password"));
        logger.info("authorisation information from Attribute login=%s Attribute password=%S" + login + password);
        HttpSession session = req.getSession();

        if (login.equals(userMock.getLogin())
                && password.equals(userMock.getPassword())) {

            session.setAttribute("role", ClientType.CLIENT);
            req.getRequestDispatcher("/main.jsp").forward(req, resp);
            logger.info("authorisation of user %s from index.jsp" + userMock.getLogin());
        } else {
            session.setAttribute("role", ClientType.GUEST);
            req.getRequestDispatcher("/loginError.jsp").forward(req, resp);
            logger.info("go to loginError.jsp from authorisation mechanism");
        }


    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
        logger.info("doGet to index.jsp");
    }

    /**
     * This method it's point of entry for servlet in  Command Factory pattern
     * for implementation of different commands realization through one servlet.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void processRequest(HttpServletRequest request,
                                HttpServletResponse response) throws ServletException, IOException {
        String page = null;
        HttpSession session = request.getSession(true);
        session.setMaxInactiveInterval(3000);
        if (session.isNew()) {
            session.setAttribute("role", String.valueOf(ClientType.GUEST));
        }
/*        // definition of the command, which came from a JSP
        ActionFactory client = new ActionFactory();
        ActionCommand command = client.defineCommand(request);

		 * Call to implement execute () method and passing parameters
		 * Class-specific command handler
		 *
        page = command.execute(request);
        if (page != null) {
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher(page);
            // Call the page response to the request
            dispatcher.forward(request, response);
        } else {
            page = ConfigurationManager.getProperty("path.page.index");
            request.getSession().setAttribute("nullPage",
                    MessageManager.getProperty("message.nullpage"));
            response.sendRedirect(request.getContextPath() + page); */
    }



}