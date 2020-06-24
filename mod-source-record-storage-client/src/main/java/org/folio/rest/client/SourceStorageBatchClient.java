
package org.folio.rest.client;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientResponse;
import org.folio.rest.tools.utils.VertxUtils;


/**
 * Auto-generated code - based on class org.folio.rest.jaxrs.resource.SourceStorageBatch
 * 
 */
public class SourceStorageBatchClient {

    private final static String GLOBAL_PATH = "/source-storage/batch";
    private String tenantId;
    private String token;
    private String okapiUrl;
    private HttpClientOptions options;
    private HttpClient httpClient;

    public SourceStorageBatchClient(String okapiUrl, String tenantId, String token, boolean keepAlive, int connTO, int idleTO) {
        // Auto-generated code
        // - generated by       org.folio.rest.tools.ClientGenerator
        // - generated based on org.folio.rest.jaxrs.resource.SourceStorageBatchResource
        this.tenantId = tenantId;
        this.token = token;
        this.okapiUrl = okapiUrl;
        options = new HttpClientOptions();
        options.setLogActivity(true);
        options.setKeepAlive(keepAlive);
        options.setConnectTimeout(connTO);
        options.setIdleTimeout(idleTO);
        httpClient = VertxUtils.getVertxFromContextOrNew().createHttpClient(options);
    }

    public SourceStorageBatchClient(String okapiUrl, String tenantId, String token, boolean keepAlive) {
        // Auto-generated code
        // - generated by       org.folio.rest.tools.ClientGenerator
        // - generated based on org.folio.rest.jaxrs.resource.SourceStorageBatchResource
        this(okapiUrl, tenantId, token, keepAlive, 2000, 5000);
    }

    public SourceStorageBatchClient(String okapiUrl, String tenantId, String token) {
        // Auto-generated code
        // - generated by       org.folio.rest.tools.ClientGenerator
        // - generated based on org.folio.rest.jaxrs.resource.SourceStorageBatchResource
        this(okapiUrl, tenantId, token, true, 2000, 5000);
    }

    /**
     * @deprecated  use a constructor that takes a full okapiUrl instead
     * 
     */
    @Deprecated
    public SourceStorageBatchClient(String host, int port, String tenantId, String token, boolean keepAlive, int connTO, int idleTO) {
        // Auto-generated code
        // - generated by       org.folio.rest.tools.ClientGenerator
        // - generated based on org.folio.rest.jaxrs.resource.SourceStorageBatchResource
        this(((("http://"+ host)+":")+ port), tenantId, token, keepAlive, connTO, idleTO);
    }

    /**
     * @deprecated  use a constructor that takes a full okapiUrl instead
     * 
     */
    @Deprecated
    public SourceStorageBatchClient(String host, int port, String tenantId, String token, boolean keepAlive) {
        // Auto-generated code
        // - generated by       org.folio.rest.tools.ClientGenerator
        // - generated based on org.folio.rest.jaxrs.resource.SourceStorageBatchResource
        this(host, port, tenantId, token, keepAlive, 2000, 5000);
    }

    /**
     * @deprecated  use a constructor that takes a full okapiUrl instead
     * 
     */
    @Deprecated
    public SourceStorageBatchClient(String host, int port, String tenantId, String token) {
        // Auto-generated code
        // - generated by       org.folio.rest.tools.ClientGenerator
        // - generated based on org.folio.rest.jaxrs.resource.SourceStorageBatchResource
        this(host, port, tenantId, token, true, 2000, 5000);
    }

    /**
     * Convenience constructor for tests ONLY!<br>Connect to localhost on 8081 as folio_demo tenant.@deprecated  use a constructor that takes a full okapiUrl instead
     * 
     */
    @Deprecated
    public SourceStorageBatchClient() {
        // Auto-generated code
        // - generated by       org.folio.rest.tools.ClientGenerator
        // - generated based on org.folio.rest.jaxrs.resource.SourceStorageBatchResource
        this("localhost", 8081, "folio_demo", "folio_demo", false, 2000, 5000);
    }

    /**
     * Service endpoint "/source-storage/batch/records"+queryParams.toString()
     * 
     */
    public void postSourceStorageBatchRecords(org.folio.rest.jaxrs.model.RecordCollection RecordCollection, Handler<HttpClientResponse> responseHandler)
        throws Exception
    {
        // Auto-generated code
        // - generated by       org.folio.rest.tools.ClientGenerator
        // - generated based on org.folio.rest.jaxrs.resource.SourceStorageBatchResource
        StringBuilder queryParams = new StringBuilder("?");
        Buffer buffer = Buffer.buffer();
        if (RecordCollection!= null) {
            buffer.appendString(org.folio.rest.tools.ClientHelpers.pojo2json(RecordCollection));
        }
        io.vertx.core.http.HttpClientRequest request = httpClient.postAbs(okapiUrl+"/source-storage/batch/records"+queryParams.toString());
        request.handler(responseHandler);
        request.putHeader("Content-type", "application/json");
        request.putHeader("Accept", "application/json,text/plain");
        if (tenantId!= null) {
            request.putHeader("X-Okapi-Token", token);
            request.putHeader("x-okapi-tenant", tenantId);
        }
        if (okapiUrl!= null) {
            request.putHeader("X-Okapi-Url", okapiUrl);
        }
        request.putHeader("Content-Length", buffer.length()+"");
        request.setChunked(true);
        request.write(buffer);
        request.end();
    }

    /**
     * Service endpoint "/source-storage/batch/parsed-records"+queryParams.toString()
     * 
     */
    public void putSourceStorageBatchParsedRecords(org.folio.rest.jaxrs.model.RecordCollection RecordCollection, Handler<HttpClientResponse> responseHandler)
        throws Exception
    {
        // Auto-generated code
        // - generated by       org.folio.rest.tools.ClientGenerator
        // - generated based on org.folio.rest.jaxrs.resource.SourceStorageBatchResource
        StringBuilder queryParams = new StringBuilder("?");
        Buffer buffer = Buffer.buffer();
        if (RecordCollection!= null) {
            buffer.appendString(org.folio.rest.tools.ClientHelpers.pojo2json(RecordCollection));
        }
        io.vertx.core.http.HttpClientRequest request = httpClient.putAbs(okapiUrl+"/source-storage/batch/parsed-records"+queryParams.toString());
        request.handler(responseHandler);
        request.putHeader("Content-type", "application/json");
        request.putHeader("Accept", "application/json,text/plain");
        if (tenantId!= null) {
            request.putHeader("X-Okapi-Token", token);
            request.putHeader("x-okapi-tenant", tenantId);
        }
        if (okapiUrl!= null) {
            request.putHeader("X-Okapi-Url", okapiUrl);
        }
        request.putHeader("Content-Length", buffer.length()+"");
        request.setChunked(true);
        request.write(buffer);
        request.end();
    }

}