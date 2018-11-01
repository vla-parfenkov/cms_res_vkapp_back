package cmsrvkapp.authorization.views;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class ApplicationView {

    private String appName;

    private String creatorLogin;

    private Integer serviceId;

    private String serverUrl;

    private ApplicationState state;

    private String config;

    public ApplicationView(@JsonProperty("appName") String appName, @JsonProperty("creatorLogin") String creatorLogin,
                           @JsonProperty("serviceId") Integer serviceId) {
        this.appName = appName;
        this.creatorLogin = creatorLogin;
        this.serviceId = serviceId;
    }

    public ApplicationView(String appName, String creatorLogin, Integer serviceId,
                           String serverUrl, ApplicationState state) {
        this.appName = appName;
        this.creatorLogin = creatorLogin;
        this.serviceId = serviceId;
        this.serverUrl = serverUrl;
        this.state = state;
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

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public ApplicationState getState() {
        return state;
    }

    public void setState(ApplicationState state) {
        this.state = state;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
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
        } else if (!serviceId.equals(other.serviceId)) {
            return false;
        }
        if (config == null) {
            return other.config == null;
        } else {
            return config.equals(other.config);
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
