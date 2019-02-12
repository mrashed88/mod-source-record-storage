package org.folio.rest.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.folio.dataimport.util.ExceptionHelper;
import org.folio.rest.jaxrs.model.ErrorRecord;
import org.folio.rest.jaxrs.model.ParsedRecord;
import org.folio.rest.jaxrs.model.Record;
import org.folio.rest.jaxrs.model.Snapshot;
import org.folio.rest.jaxrs.model.TestMarcRecordsCollection;
import org.folio.rest.jaxrs.resource.SourceStorage;
import org.folio.rest.tools.utils.TenantTool;
import org.folio.services.RecordService;
import org.folio.services.RecordServiceImpl;
import org.folio.services.SnapshotService;
import org.folio.services.SnapshotServiceImpl;
import org.marc4j.MarcJsonWriter;
import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SourceStorageImpl implements SourceStorage {

  private static final Logger LOG = LoggerFactory.getLogger(SourceStorageImpl.class);
  private static final String TEST_MODE = "test.mode";
  private static final String NOT_FOUND_MESSAGE = "%s with id '%s' was not found";
  private static final String STUB_SNAPSHOT_ID = "00000000-0000-0000-0000-000000000000";
  private SnapshotService snapshotService;
  private RecordService recordService;

  public SourceStorageImpl(Vertx vertx, String tenantId) {
    String calculatedTenantId = TenantTool.calculateTenantId(tenantId);
    this.snapshotService = new SnapshotServiceImpl(vertx, calculatedTenantId);
    this.recordService = new RecordServiceImpl(vertx, calculatedTenantId);
  }

  @Override
  public void getSourceStorageSnapshots(String query, int offset, int limit, String lang,
                                       Map<String, String> okapiHeaders,
                                       Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    vertxContext.runOnContext(v -> {
      try {
        snapshotService.getSnapshots(query, offset, limit)
          .map(GetSourceStorageSnapshotsResponse::respond200WithApplicationJson)
          .map(Response.class::cast)
          .otherwise(ExceptionHelper::mapExceptionToResponse)
          .setHandler(asyncResultHandler);
      } catch (Exception e) {
        LOG.error("Failed to get all snapshots", e);
        asyncResultHandler.handle(Future.succeededFuture(ExceptionHelper.mapExceptionToResponse(e)));
      }
    });
  }

  @Override
  public void postSourceStorageSnapshots(String lang, Snapshot entity, Map<String, String> okapiHeaders,
                                        Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    vertxContext.runOnContext(v -> {
      try {
        snapshotService.saveSnapshot(entity)
          .map((Response) PostSourceStorageSnapshotsResponse
            .respond201WithApplicationJson(entity, PostSourceStorageSnapshotsResponse.headersFor201()))
          .otherwise(ExceptionHelper::mapExceptionToResponse)
          .setHandler(asyncResultHandler);
      } catch (Exception e) {
        LOG.error("Failed to create a snapshot", e);
        asyncResultHandler.handle(Future.succeededFuture(ExceptionHelper.mapExceptionToResponse(e)));
      }
    });
  }

  @Override
  public void getSourceStorageSnapshotsByJobExecutionId(String jobExecutionId, String lang, Map<String, String> okapiHeaders,
                                                       Handler<AsyncResult<Response>> asyncResultHandler,
                                                       Context vertxContext) {
    vertxContext.runOnContext(v -> {
      try {
        snapshotService.getSnapshotById(jobExecutionId)
          .map(optionalSnapshot -> optionalSnapshot.orElseThrow(() ->
            new NotFoundException(String.format(NOT_FOUND_MESSAGE, Snapshot.class.getSimpleName(), jobExecutionId))))
          .map(GetSourceStorageSnapshotsByJobExecutionIdResponse::respond200WithApplicationJson)
          .map(Response.class::cast)
          .otherwise(ExceptionHelper::mapExceptionToResponse)
          .setHandler(asyncResultHandler);
      } catch (Exception e) {
        LOG.error("Failed to get snapshot by jobExecutionId", e);
        asyncResultHandler.handle(Future.succeededFuture(ExceptionHelper.mapExceptionToResponse(e)));
      }
    });
  }

  @Override
  public void putSourceStorageSnapshotsByJobExecutionId(String jobExecutionId, String lang, Snapshot entity,
                                                       Map<String, String> okapiHeaders,
                                                       Handler<AsyncResult<Response>> asyncResultHandler,
                                                       Context vertxContext) {
    vertxContext.runOnContext(v -> {
      try {
        entity.setJobExecutionId(jobExecutionId);
        snapshotService.updateSnapshot(entity)
          .map(updated -> updated ?
            PutSourceStorageSnapshotsByJobExecutionIdResponse.respond200WithApplicationJson(entity) :
            PutSourceStorageSnapshotsByJobExecutionIdResponse.respond404WithTextPlain(
              String.format(NOT_FOUND_MESSAGE, Snapshot.class.getSimpleName(), jobExecutionId))
          )
          .map(Response.class::cast)
          .otherwise(ExceptionHelper::mapExceptionToResponse)
          .setHandler(asyncResultHandler);
      } catch (Exception e) {
        LOG.error("Failed to update a snapshot", e);
        asyncResultHandler.handle(Future.succeededFuture(ExceptionHelper.mapExceptionToResponse(e)));
      }
    });
  }

  @Override
  public void deleteSourceStorageSnapshotsByJobExecutionId(String jobExecutionId, String lang, Map<String, String> okapiHeaders,
                                                          Handler<AsyncResult<Response>> asyncResultHandler,
                                                          Context vertxContext) {
    vertxContext.runOnContext(v -> {
      try {
        snapshotService.deleteSnapshot(jobExecutionId)
          .map(deleted -> deleted ?
            DeleteSourceStorageSnapshotsByJobExecutionIdResponse.respond204WithTextPlain(
              String.format("Snapshot with id '%s' was successfully deleted", jobExecutionId)) :
            DeleteSourceStorageSnapshotsByJobExecutionIdResponse.respond404WithTextPlain(
              String.format(NOT_FOUND_MESSAGE, Snapshot.class.getSimpleName(), jobExecutionId)))
          .map(Response.class::cast)
          .otherwise(ExceptionHelper::mapExceptionToResponse)
          .setHandler(asyncResultHandler);
      } catch (Exception e) {
        LOG.error("Failed to delete a snapshot", e);
        asyncResultHandler.handle(Future.succeededFuture(ExceptionHelper.mapExceptionToResponse(e)));
      }
    });
  }

  @Override
  public void getSourceStorageRecords(String query, int offset, int limit, String lang, Map<String, String> okapiHeaders,
                                     Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    vertxContext.runOnContext(v -> {
      try {
        recordService.getRecords(query, offset, limit)
          .map(GetSourceStorageRecordsResponse::respond200WithApplicationJson)
          .map(Response.class::cast)
          .otherwise(ExceptionHelper::mapExceptionToResponse)
          .setHandler(asyncResultHandler);
      } catch (Exception e) {
        LOG.error("Failed to get all records", e);
        asyncResultHandler.handle(Future.succeededFuture(ExceptionHelper.mapExceptionToResponse(e)));
      }
    });
  }

  @Override
  public void postSourceStorageRecords(String lang, Record entity, Map<String, String> okapiHeaders,
                                      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    vertxContext.runOnContext(v -> {
      try {
        recordService.saveRecord(entity)
          .map((Response) PostSourceStorageRecordsResponse
            .respond201WithApplicationJson(entity, PostSourceStorageRecordsResponse.headersFor201()))
          .otherwise(ExceptionHelper::mapExceptionToResponse)
          .setHandler(asyncResultHandler);
      } catch (Exception e) {
        LOG.error("Failed to create a record", e);
        asyncResultHandler.handle(Future.succeededFuture(ExceptionHelper.mapExceptionToResponse(e)));
      }
    });
  }

  @Override
  public void getSourceStorageRecordsById(String id, String lang, Map<String, String> okapiHeaders,
                                         Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    vertxContext.runOnContext(v -> {
      try {
        recordService.getRecordById(id)
          .map(optionalRecord -> optionalRecord.orElseThrow(() ->
            new NotFoundException(String.format(NOT_FOUND_MESSAGE, Record.class.getSimpleName(), id))))
          .map(GetSourceStorageRecordsByIdResponse::respond200WithApplicationJson)
          .map(Response.class::cast)
          .otherwise(ExceptionHelper::mapExceptionToResponse)
          .setHandler(asyncResultHandler);
      } catch (Exception e) {
        LOG.error("Failed to get record by id", e);
        asyncResultHandler.handle(Future.succeededFuture(ExceptionHelper.mapExceptionToResponse(e)));
      }
    });
  }

  @Override
  public void putSourceStorageRecordsById(String id, String lang, Record entity, Map<String, String> okapiHeaders,
                                         Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    vertxContext.runOnContext(v -> {
      try {
        entity.setId(id);
        recordService.updateRecord(entity)
          .map(updated -> updated ?
            PutSourceStorageRecordsByIdResponse.respond200WithApplicationJson(entity) :
            PutSourceStorageRecordsByIdResponse.respond404WithTextPlain(
              String.format(NOT_FOUND_MESSAGE, Record.class.getSimpleName(), id))
          )
          .map(Response.class::cast)
          .otherwise(ExceptionHelper::mapExceptionToResponse)
          .setHandler(asyncResultHandler);
      } catch (Exception e) {
        LOG.error("Failed to update a record", e);
        asyncResultHandler.handle(Future.succeededFuture(ExceptionHelper.mapExceptionToResponse(e)));
      }
    });
  }

  @Override
  public void deleteSourceStorageRecordsById(String id, String lang, Map<String, String> okapiHeaders,
                                            Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    vertxContext.runOnContext(v -> {
      try {
        recordService.deleteRecord(id)
          .map(deleted -> deleted ?
            DeleteSourceStorageRecordsByIdResponse.respond204WithTextPlain(
              String.format("Record with id '%s' was successfully deleted", id)) :
            DeleteSourceStorageRecordsByIdResponse.respond404WithTextPlain(
              String.format(NOT_FOUND_MESSAGE, Record.class.getSimpleName(), id)))
          .map(Response.class::cast)
          .otherwise(ExceptionHelper::mapExceptionToResponse)
          .setHandler(asyncResultHandler);
      } catch (Exception e) {
        LOG.error("Failed to delete a record", e);
        asyncResultHandler.handle(Future.succeededFuture(ExceptionHelper.mapExceptionToResponse(e)));
      }
    });
  }

  @Override
  public void getSourceStorageSourceRecords(String query, int offset, int limit, Map<String, String> okapiHeaders,
                                     Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    vertxContext.runOnContext(v -> {
      try {
        recordService.getSourceRecords(query, offset, limit)
          .map(GetSourceStorageSourceRecordsResponse::respond200WithApplicationJson)
          .map(Response.class::cast)
          .otherwise(ExceptionHelper::mapExceptionToResponse)
          .setHandler(asyncResultHandler);
      } catch (Exception e) {
        LOG.error("Failed to get results", e);
        asyncResultHandler.handle(Future.succeededFuture(ExceptionHelper.mapExceptionToResponse(e)));
      }
    });
  }

  @Override
  public void postSourceStoragePopulateTestMarcRecords(TestMarcRecordsCollection entity, Map<String, String> okapiHeaders,
                                                       Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    vertxContext.runOnContext(v -> {
      if (Boolean.TRUE.equals(Boolean.valueOf(System.getenv(TEST_MODE)))) {
        List<Future> futures = new ArrayList<>();
        entity.getRawRecords().stream()
          .map(rawRecord -> {
            Record record = new Record()
              .withId(rawRecord.getId())
              .withRawRecord(rawRecord)
              .withSnapshotId(STUB_SNAPSHOT_ID)
              .withRecordType(Record.RecordType.MARC);
            if (rawRecord.getContent().startsWith("{")) {
              record.setParsedRecord(new ParsedRecord().withContent(rawRecord.getContent()));
            } else {
              record = parseRecord(record);
            }
            return record;
          })
          .forEach(marcRecord -> futures.add(recordService.saveRecord(marcRecord)));
        CompositeFuture.all(futures).setHandler(result -> {
          if (result.succeeded()) {
            asyncResultHandler.handle(Future.succeededFuture(PostSourceStoragePopulateTestMarcRecordsResponse.respond204WithTextPlain("MARC records were successfully saved")));
          } else {
            asyncResultHandler.handle(Future.succeededFuture(PostSourceStoragePopulateTestMarcRecordsResponse.respond500WithTextPlain(result.cause().getMessage())));
          }
        });
      } else {
        asyncResultHandler.handle(Future.succeededFuture(PostSourceStoragePopulateTestMarcRecordsResponse.respond400WithTextPlain("Endpoint is available only in test mode")));
      }
    });
  }

  private Record parseRecord(Record record) {
    try {
      MarcReader reader = new MarcStreamReader(new ByteArrayInputStream(record.getRawRecord().getContent().getBytes(StandardCharsets.UTF_8)));
      if (reader.hasNext()) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        MarcJsonWriter writer = new MarcJsonWriter(os);
        org.marc4j.marc.Record marcRecord = reader.next();
        writer.write(marcRecord);
        record.setParsedRecord(new ParsedRecord().withContent(os.toString(StandardCharsets.UTF_8.name())));
      }
    } catch (Exception e) {
      LOG.error("Error parsing MARC record", e);
      record.setErrorRecord(new ErrorRecord().withContent(record.getRawRecord().getContent()).withDescription("Error parsing marc record"));
    }
    return record;
  }

}