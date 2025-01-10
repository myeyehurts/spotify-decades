package com.example.spotifydecades;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.ArrayList;

@Controller
public class IndexController {

    //entrance displays when beginning a new session
    @GetMapping("/")
    public String entrance(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "index";
        }
        //if there is already a user authorized they are redirected to the dashboard
        return "redirect:/dashboard";
    }

    @GetMapping("/authorize")
    public String authorize() {
        //visiting url will allow the user to authorize using the Spotify dialog
        return "redirect:" + UserAuthorization.buildURL();
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(value = "code", required = false) String code, Model model, HttpSession session, RedirectAttributes redir) {
        //if there is no existing token
        if (session.getAttribute("token") == null) {
            //if there was a code provided use it to obtain a token and the user information
            if (code != null) {
                //an exception will automatically be thrown if no code is obtained
                String token = UserAuthorization.getAccessToken(code);
                    String user = APIRequests.getUser(token);
                    if (user == null) {
                        redir.addFlashAttribute("error", "Could not fetch user data.");
                        return "redirect:/server-error";
                    }
                    //keep token and user active in the session
                    session.setAttribute("token", token);
                    session.setAttribute("user", user);
                    model.addAttribute("user", user);
            }
            //if there was no code provided redirect the user to authorize
            else {
                return "redirect:/authorize";
            }
        }
        //if there is already an active token
        else {
            String user = (String) session.getAttribute("user");
            //if there is no user try to refresh the token in case it is expired
            if (user == null) {
                String token = UserAuthorization.refreshToken((String) (session.getAttribute("token")));
                user = APIRequests.getUser(token);
                //if there is still no user, reauthorization is required
                if (user == null) {
                    return "redirect:/authorize";
                }
            }
            model.addAttribute("user", session.getAttribute("user"));
        }
        return "dashboard";
    }

    @GetMapping("/results")
    public String results(Model model, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/authorize";
        }
        //test validity of token
        if (APIRequests.getUser(token) == null) {
            token = UserAuthorization.refreshToken(token);
        }
        DecadeCalculation.populateList(token);
        ArrayList<Decade> decades = DecadeCalculation.decades;
        String user = (String) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("decades", decades);
        model.addAttribute("tally", DecadeCalculation.tally());
        model.addAttribute("numDecades", decades.size());
        model.addAttribute("gradient", DecadeCalculation.generateGradient());
        return "results";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        //remove session variables
        session.invalidate();
        //clear list
        DecadeCalculation.wipe();
        //return to entrance page where user can log in/switch account
        return "redirect:/";
    }


    @ExceptionHandler(Exception.class )
    public String exceptionHandler(Exception e, RedirectAttributes redir) {
        redir.addFlashAttribute("error", e.getMessage());
        return "redirect:/server-error";
    }

    @GetMapping("/server-error")
    public String error() {
        return "error/500";
    }

}
