package cmsrvkapp.authorization.views;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class ApplicationView {

    private String appName;

    private String creatorLogin;

    public ApplicationView(@JsonProperty("appName") String appName, @JsonProperty("creatorLogin") String creatorLogin) {
        this.appName = appName;
        this.creatorLogin = creatorLogin;
    }

    @Override
    public String toString() {
        return "appName = " + appName + " creatorLogin = " + creatorLogin;
    }

    public String getAppName() {
        return appName;
    }

    public String getCreatorLogin() {
        return creatorLogin;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setCreatorLogin(String creatorLogin) {
        this.creatorLogin = creatorLogin;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ApplicationView other = (ApplicationView) obj;
        if (appName == null) {
            if (other.appName != null) {
                return false;
            }
        } else if (!appName.equals(other.appName)) {
            return false;
        }
        if (creatorLogin == null) {
            return other.creatorLogin == null;
        } else {
            return creatorLogin.equals(other.creatorLogin);
        }
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        if (appName == null) {
            result = prime * result;
        } else {
            result = prime * result + appName.hashCode();
        }
        if (creatorLogin == null) {
            result = prime * result;
        } else {
            result = prime * result + creatorLogin.hashCode();
        }

        return result;
    }

}
