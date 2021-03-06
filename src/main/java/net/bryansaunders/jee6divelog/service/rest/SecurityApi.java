/**
 * 
 */
package net.bryansaunders.jee6divelog.service.rest;

/*
 * #%L
 * BSNet-DiveLog
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2012 Bryan Saunders
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.bryansaunders.jee6divelog.model.UserAccount;
import net.bryansaunders.jee6divelog.security.Credentials;
import net.bryansaunders.jee6divelog.security.Identity;
import net.bryansaunders.jee6divelog.security.annotation.HasPermission;
import net.bryansaunders.jee6divelog.security.enumerator.Permission;
import net.bryansaunders.jee6divelog.service.UserAccountService;
import net.bryansaunders.jee6divelog.util.AccountUtils;

import org.codehaus.enunciate.jaxrs.TypeHint;

/**
 * Security REST API.
 * 
 * @author Bryan Saunders <btsaunde@gmail.com>
 * 
 */
@Path("/security")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class SecurityApi {

    /**
     * User Account Service.
     */
    @Inject
    private UserAccountService userAccountService;

    /**
     * User Identity.
     */
    @Inject
    private Identity identity;

    /**
     * User Credentials.
     */
    @Inject
    private Credentials credentials;

    /**
     * Identifies the currently logged in REST user.
     * 
     * <ul>
     * <li>Status 202: Login Successful.</li>
     * <li>Status 401: Login Failed.</li>
     * </ul>
     * 
     * @param incomingCredentials
     *            Users Login Credentials
     * @return User Identity
     */
    @POST
    @Path("login")
    @TypeHint(UserAccount.class)
    public Response login(final Credentials incomingCredentials) {
        Response response;
        this.credentials.setUsername(incomingCredentials.getUsername());
        this.credentials.setPassword(incomingCredentials.getPassword());

        final boolean loginResult = this.identity.restLogin();
        if (loginResult) {
            final UserAccount userAccount = AccountUtils.createCleanUserAccount(this.identity);
            response = Response.ok(userAccount).status(Response.Status.ACCEPTED).build();
        } else {
            response = Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return response;
    }

    /**
     * Identifies the currently logged in REST user.
     * 
     * <ul>
     * <li>Status 200: Request Successful.</li>
     * </ul>
     * 
     * @return User Identity
     */
    @GET
    @Path("identify")
    @TypeHint(UserAccount.class)
    @HasPermission(permission = Permission.REST_ACCESS)
    public Response identify() {
        final UserAccount userAccount = AccountUtils.createCleanUserAccount(this.identity);
        return Response.ok(userAccount).build();
    }

    /**
     * Logout the Current REST User.
     * 
     * <ul>
     * <li>Status 200: Logout Request Successful.</li>
     * </ul>
     * 
     * @return Logout Result
     */
    @POST
    @Path("logout")
    @TypeHint(Boolean.class)
    @HasPermission(permission = Permission.REST_ACCESS)
    public Response logout() {
        final String username = this.identity.getUsername();
        this.identity.logout();
        final boolean result = this.userAccountService.clearApiKey(username);
        return Response.ok(result).build();
    }
}
