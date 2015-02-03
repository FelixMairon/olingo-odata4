/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.server.core;

import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataTranslatedException;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;

public class MetadataResponse extends ServiceResponse {
  private final ODataSerializer serializer;
  private final ContentType responseContentType;

  public static MetadataResponse getinstance(ServiceRequest request, ODataResponse response)
      throws ContentNegotiatorException, SerializerException {
    return new MetadataResponse(response, request.getSerializer(), request.getResponseContentType());
  }

  private MetadataResponse(ODataResponse response, ODataSerializer serializer, ContentType responseContentType) {
    super(response);
    this.serializer = serializer;
    this.responseContentType = responseContentType;
  }

  public void writeMetadata(ServiceMetadata metadata)throws ODataTranslatedException {
    assert (!isClosed());
    this.response.setContent(this.serializer.metadataDocument(metadata));
    writeOK(this.responseContentType.toContentTypeString());
    close();
  }

  @Override
  public void accepts(ServiceResponseVisior visitor) throws ODataTranslatedException,
      ODataApplicationException {
    visitor.visit(this);
  }
}