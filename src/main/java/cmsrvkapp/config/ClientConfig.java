package cmsrvkapp.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Validated
public final class ClientConfig {
    private String data;

    public ClientConfig(@JsonProperty("data") String data) {
        this.data = data;

    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
