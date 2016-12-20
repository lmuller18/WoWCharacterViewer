package lm.charviewer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet for handling search requests
 */
@WebServlet("/search")
public class SearchServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("characterName");
        String realm = request.getParameter("realmName");
        CharacterViewer characterViewer = new CharacterViewer();
        if(!characterViewer.getCharacter(name, realm)){
            request.setAttribute("notFound", true);
        } else {
            try{
                int curLevel = characterViewer.getLevel();
                int curHealth = characterViewer.getHealth();
                if(curHealth == -99 || curLevel == -99){
                    request.setAttribute("error", true);
                } else {
                    request.setAttribute("curName", characterViewer.getName());
                    request.setAttribute("curLevel", curLevel);
                    request.setAttribute("curHealth", curHealth);
                    request.setAttribute("searched", true);
                    request.setAttribute("char", characterViewer);
                }
            } catch (NullPointerException e){
                request.setAttribute("error", true);
            }
        }

        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}

