package com.fredericvauchelles;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;

public class Connect
{
    public static Drive getDriveService(java.io.File googleClientProperties, MavenCredentialStore store)
        throws IOException, Exception  {
        return getDriveService(googleClientProperties, store, null);
    }

    public static Drive getDriveService(java.io.File googleClientProperties, MavenCredentialStore store, String authToken)
        throws IOException, Exception {

        Properties clientProperties = new Properties();
        clientProperties.load(new FileInputStream(googleClientProperties));
        String clientId = clientProperties.getProperty("clientId");
        String clientSecret = clientProperties.getProperty("clientSecret");

        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();

        if(clientId == null)
            throw new Exception("clientId is not defined in " + googleClientProperties.getAbsolutePath());

        if(clientSecret == null)
            throw new Exception("clientSecret is not defined in " + googleClientProperties.getAbsolutePath());

        Credential credential = new GoogleCredential.Builder()
            .setTransport(httpTransport)
            .setJsonFactory(jsonFactory)
            .setClientSecrets(clientId, clientSecret)
            .build();

        if(!store.load("service", credential)) {
            throw new Exception("There is no credential available, please use the @connect goal");
        }

        return new Drive.Builder(httpTransport, jsonFactory, credential).build();
    }
}
