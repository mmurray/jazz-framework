package org.jazzframework.request;

public class RequestInfo {

  private String controllerName;
  private String actionName;

  public String getControllerName() {
    return controllerName;
  }

  public String getActionName() {
    return actionName;
  }

  public void setControllerName(String controllerName) {
    this.controllerName = controllerName;
  }

  public void setActionName(String actionName) {
    this.actionName = actionName;
  }
}
