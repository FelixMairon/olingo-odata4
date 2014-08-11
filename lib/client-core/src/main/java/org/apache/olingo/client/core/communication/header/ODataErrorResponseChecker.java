/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.client.core.communication.header;

import java.io.InputStream;
import org.apache.http.StatusLine;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.ODataServerErrorException;
import org.apache.olingo.commons.api.ODataResponseError;
import org.apache.olingo.commons.api.domain.ODataError;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.serialization.ODataDeserializerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ODataErrorResponseChecker {

  protected static final Logger LOG = LoggerFactory.getLogger(ODataErrorResponseChecker.class);

  private static ODataError getGenericError(final int code, final String errorMsg) {
    final ODataError error = new ODataError();
    error.setCode(String.valueOf(code));
    error.setMessage(errorMsg);
    return error;
  }

  public static ODataResponseError checkResponse(
          final CommonODataClient<?> odataClient, final StatusLine statusLine, final InputStream entity,
          final String accept) {

    ODataResponseError result = null;

    if (entity == null) {
      result = new ODataClientErrorException(statusLine);
    } else {
      final ODataFormat format = accept.contains("xml") ? ODataFormat.XML : ODataFormat.JSON;

      ODataError error;
      try {
        error = odataClient.getReader().readError(entity, format);
      } catch (final RuntimeException e) {
        LOG.warn("Error deserializing error response", e);
        error = getGenericError(
                statusLine.getStatusCode(),
                statusLine.getReasonPhrase());
      } catch (final ODataDeserializerException e) {
        LOG.warn("Error deserializing error response", e);
        error = getGenericError(
                statusLine.getStatusCode(),
                statusLine.getReasonPhrase());
      }

      if (statusLine.getStatusCode() >= 500) {
        result = new ODataServerErrorException(statusLine);
      } else {
        result = new ODataClientErrorException(statusLine, error);
      }
    }

    return result;
  }

  private ODataErrorResponseChecker() {
    // private constructor for static utility class
  }
}