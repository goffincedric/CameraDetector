package be.kdg.processor.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;

/**
 * A class that handles successful Authentication url redirections based on the user's roles.
 *
 * @author Cédric Goffin
 * @see AuthenticationSuccessHandler
 */
public class DefaultUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(authentication);
        if (!response.isCommitted()) new DefaultRedirectStrategy().sendRedirect(request, response, targetUrl);
        clearAuthenticationAttributes(request);
    }

    /**
     * Method that determines the url to redirect to based on the user's roles
     * @param authentication built-in authentication class that contains information about the authenticated user.
     * @return a string with the url to redirect to
     */
    private String determineTargetUrl(Authentication authentication) {
        boolean isAdmin = false;
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        authLoop:
        for (GrantedAuthority grantedAuthority : authorities) {
            switch (grantedAuthority.getAuthority()) {
                case "DBADMIN":
                case "WEBADMIN":
                    isAdmin = true;
                    break authLoop;
                default:
                    break;
            }
        }

        if (isAdmin) {
            return "/admin";
        } else {
            return "/";
        }
    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
}
