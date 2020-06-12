package com.exclamationlabs.connid.base.webex.driver.rest;

import com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty;
import com.exclamationlabs.connid.base.connector.driver.rest.BaseRestDriver;
import com.exclamationlabs.connid.base.connector.driver.rest.RestFaultProcessor;
import com.exclamationlabs.connid.base.webex.model.WebexGroup;
import com.exclamationlabs.connid.base.webex.model.WebexUser;
import com.exclamationlabs.connid.base.webex.model.response.ListGroupsResponse;
import com.exclamationlabs.connid.base.webex.model.response.ListUsersResponse;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.exceptions.PermissionDeniedException;

import java.util.List;
import java.util.Set;

public class WebexDriver extends BaseRestDriver<WebexUser, WebexGroup> {

    @Override
    protected RestFaultProcessor getFaultProcessor() {
        return WebexFaultProcessor.getInstance();
    }

    @Override
    protected String getBaseServiceUrl() {
        return "https://webexapis.com/v1";
    }

    @Override
    protected boolean usesBearerAuthorization() {
        return true;
    }

    @Override
    public Set<ConnectorProperty> getRequiredPropertyNames() {
        return null;
    }

    @Override
    public void test() throws ConnectorException {
        // TODO: determine Webex test method

    }

    @Override
    public void close() {
        configuration = null;
        authenticator = null;
    }

    @Override
    public String createUser(WebexUser webexUser) throws ConnectorException {
        WebexUser newUser = executePostRequest("/people", WebexUser.class, webexUser);
        if (newUser == null || newUser.getId() == null) {
            throw new ConnectorException("Response from user creation was invalid");
        }

        return newUser.getId();
    }

    @Override
    public String createGroup(WebexGroup webexGroup) throws ConnectorException {
        throw new PermissionDeniedException("Webex does not allow creation of roles.");
    }

    @Override
    public void updateUser(String userId, WebexUser modifiedUser) throws ConnectorException {
        // Webex requires all fields be present in update, whether they were altered or not
        WebexUser userToUpdate = getUser(userId);
        userToUpdate.prepareForUpdate(modifiedUser);
        executePutRequest("/people/" + userId, WebexUser.class, userToUpdate);
    }

    @Override
    public void updateGroup(String groupId, WebexGroup webexGroup) throws ConnectorException {
        throw new PermissionDeniedException("Webex does not allow modification of roles.");
    }

    @Override
    public void deleteUser(String userId) throws ConnectorException {
        executeDeleteRequest("/people/" + userId, null);
    }

    @Override
    public void deleteGroup(String groupId) throws ConnectorException {
        throw new PermissionDeniedException("Webex does not allow deletion of roles.");
    }

    @Override
    public List<WebexUser> getUsers() throws ConnectorException {
        ListUsersResponse response = executeGetRequest("/people", ListUsersResponse.class);
        return response.getItems();
    }

    @Override
    public List<WebexGroup> getGroups() throws ConnectorException {
        ListGroupsResponse response = executeGetRequest("/roles", ListGroupsResponse.class);
        return response.getItems();
    }

    @Override
    public WebexUser getUser(String userId) throws ConnectorException {
        return executeGetRequest("/people/" + userId, WebexUser.class);
    }

    @Override
    public WebexGroup getGroup(String groupId) throws ConnectorException {
        return executeGetRequest("/roles/" + groupId, WebexGroup.class);
    }

    @Override
    public void addGroupToUser(String s, String s1) throws ConnectorException {

    }

    @Override
    public void removeGroupFromUser(String s, String s1) throws ConnectorException {

    }
}