/*
 *  Copyright (c) 2002-2024, Manorrock.com. All Rights Reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      1. Redistributions of source code must retain the above copyright
 *         notice, this list of conditions and the following disclaimer.
 *
 *      2. Redistributions in binary form must reproduce the above copyright
 *         notice, this list of conditions and the following disclaimer in the
 *         documentation and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 */
package com.manorrock.aegean.rest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

/**
 * The Repository resource.
 * 
 * @author Manfred Riem (mriem@manorrock.com)
 */
@Path("repository")
public class RepositoryResource {
    
    /**
     * Create the given repository.
     * 
     * @param repository the repository to create.
     * @return the created repository.
     */
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    @Path("")
    public Repository create(Repository repository) {
        return repository;
    }
    
    /**
     * Delete the given repository.
     * 
     * @param name the name of the repository.
     */
    @DELETE
    @Path("{name}")
    public void delete(@PathParam("name") String name) {
    }
 
    /**
     * Get details for the given repository.
     * 
     * @param name the name of the repository.
     * @return the repository.
     */
    @Produces("application/json")
    @GET
    @Path("{name}")
    public Repository view(@PathParam("name") String name) {
        Repository result = new Repository();
        result.setName(name);
        return result;
    }
    
    /**
     * List the available repositories.
     * 
     * @return the list of repositories.
     */
    @Produces("application/json")
    @GET
    public Response list(){
        return Response.ok("{}").build();
    }
}
