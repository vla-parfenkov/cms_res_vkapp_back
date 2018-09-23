package cmsrvkapp.authorization.views;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Validated
public final class AuthorisationView {

    @NotNull
    private String loginEmail;

    @NotNull
    private String password;

    public AuthorisationView(@JsonProperty("loginEmail") String loginEmail, @JsonProperty("password")
            String password) {
        this.loginEmail = loginEmail;
        this.password = password;
    }

    @NotNull
    public String getLoginEmail() {
        return loginEmail;
    }

    @NotNull
    public String getPassword() {
        return password;
    }

}
