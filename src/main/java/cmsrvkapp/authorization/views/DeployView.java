package cmsrvkapp.authorization.views;

import com.fasterxml.jackson.annotation.JsonProperty;


public final class DeployView {
    private String appName;

    private String serverKey;

    public DeployView(@JsonProperty("appName") String appName,
                           @JsonProperty("serverKey") String serverKey) {
        this.appName = appName;
        this.serverKey = serverKey;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getServerKey() {
        return serverKey;
    }

    public void setServerKey(String serverKey) {
        this.serverKey = serverKey;
    }

}
