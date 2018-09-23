package cmsrvkapp.authorization.views;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ResponseView {
    ERROR_NOT_LOGGED_IN(0, "You are not currently logged in!"),
    ERROR_BAD_LOGIN_DATA(1, "Wrong login/email or password!"),
    SUCCESS_LOGOUT(3, "You successfully logged out!"),
    ERROR_USER_ALREADY_EXISTS(4, "User already exists!"),
    ERROR_USER_NOT_FOUND(5, "User not found!"),
    ERROR_NO_RIGHTS_TO_CHANGE_USER(6, "You have no rights to change this user data!");


    private final Integer status;
    private final String response;

    ResponseView(Integer status, String response) {
        this.status = status;
        this.response = response;
    }


    @SuppressWarnings("unused")
    public Integer getStatus() {
        return status;
    }

    @SuppressWarnings("unused")
    public String getResponse() {
        return response;
    }

}
