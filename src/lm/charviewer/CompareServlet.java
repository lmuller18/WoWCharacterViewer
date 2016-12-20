package lm.charviewer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/compare")
public class CompareServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("compName");
        String curName = request.getParameter("curName");
        request.setAttribute("compAttributes", CharacterViewer.getCompareAttributes(name));
        request.setAttribute("curAttributes", CharacterViewer.getCompareAttributes(curName));

        request.setAttribute("compAttack", CharacterViewer.getCompareAttack(name));
        request.setAttribute("curAttack", CharacterViewer.getCompareAttack(curName));

        request.setAttribute("compDefense", CharacterViewer.getCompareDefense(name));
        request.setAttribute("curDefense", CharacterViewer.getCompareDefense(curName));

        request.setAttribute("compEnhancements", CharacterViewer.getCompareEnhancements(name));
        request.setAttribute("curEnhancements", CharacterViewer.getCompareEnhancements(curName));

        request.setAttribute("curName", curName);
        request.setAttribute("compName", name);

        request.setAttribute("curHealth", CharacterViewer.getCompareHealth(curName));
        request.setAttribute("compHealth", CharacterViewer.getCompareHealth(name));

        request.setAttribute("curLevel", CharacterViewer.getCompareLevel(curName));
        request.setAttribute("compLevel", CharacterViewer.getCompareLevel(name));

        request.setAttribute("compared", true);
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}

