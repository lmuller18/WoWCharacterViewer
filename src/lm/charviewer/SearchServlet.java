package lm.charviewer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/search")
public class SearchServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("characterName");
        String realm = request.getParameter("realmName");
        CharacterViewer characterViewer = new CharacterViewer();
        characterViewer.getCharacter(name, realm);
        request.setAttribute("curName", characterViewer.getName());
        request.setAttribute("curLevel", characterViewer.getLevel());
        request.setAttribute("curHealth", characterViewer.getHealth());
        request.setAttribute("searched", true);
        request.setAttribute("char", characterViewer);
//        request.setAttribute("itemList", characterViewer.getItemList(name));

        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}

