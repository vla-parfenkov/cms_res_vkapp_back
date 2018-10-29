package cmsrvkapp.authorization.views;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class ApplicationView {

    private String appName;

    private String creatorLogin;

    private Integer serviceId;

    public ApplicationView(@JsonProperty("appName") String appName, @JsonProperty("creatorLogin") String creatorLogin,
                           @JsonProperty("serviceId") Integer serviceId) {
        this.appName = appName;
        this.creatorLogin = creatorLogin;
        this.serviceId = serviceId;
    }

    @Override
    public String toString() {
        return "appName = " + appName + " creatorLogin = " + creatorLogin + " serviceId = " + serviceId;
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

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
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
        } else if (!creatorLogin.equals(other.creatorLogin)) {
            return false;
        }
        if (serviceId == null) {
            return other.serviceId == null;
        } else {
            return serviceId.equals(other.serviceId);
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
