package cmsrvkapp.authorization.views;

public enum ApplicationState {
    STOPPED(0),
    STARTED(1),
    STARTS(2);

    private final Integer number;

    ApplicationState(Integer number) {
        this.number = number;
    }

    public Integer getNumber() {
        return number;
    }
}
