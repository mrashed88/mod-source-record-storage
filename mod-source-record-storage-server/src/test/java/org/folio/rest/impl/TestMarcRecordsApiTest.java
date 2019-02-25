package org.folio.rest.impl;

import io.restassured.RestAssured;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.apache.http.HttpStatus;
import org.folio.rest.jaxrs.model.RawRecord;
import org.folio.rest.jaxrs.model.TestMarcRecordsCollection;
import org.folio.rest.persist.Criteria.Criterion;
import org.folio.rest.persist.PostgresClient;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;

@RunWith(VertxUnitRunner.class)
public class TestMarcRecordsApiTest extends AbstractRestVerticleTest {

  private static final String POPULATE_TEST_MARK_RECORDS_PATH = "/source-storage/populate-test-marc-records";
  private static final String RECORDS_TABLE_NAME = "records";
  private static final String RAW_RECORDS_TABLE_NAME = "raw_records";
  private static final String ERROR_RECORDS_TABLE_NAME = "error_records";
  private static final String MARC_RECORDS_TABLE_NAME = "marc_records";

  private static RawRecord rawRecord_1 = new RawRecord()
    .withId("9b0ae8c4-2e0c-11e9-b210-d663bd873d93")
    .withContent("01240cas a2200397   4500001000700000005001700007008004100024010001700065022001400082035002600096035002200122035001100144035001900155040004400174050001500218082001100233222004200244245004300286260004700329265003800376300001500414310002200429321002500451362002300476570002900499650003300528650004500561655004200606700004500648853001800693863002300711902001600734905002100750948003700771950003400808\u001E366832\u001E20141106221425.0\u001E750907c19509999enkqr p       0   a0eng d\u001E  \u001Fa   58020553 \u001E  \u001Fa0022-0469\u001E  \u001Fa(CStRLIN)NYCX1604275S\u001E  \u001Fa(NIC)notisABP6388\u001E  \u001Fa366832\u001E  \u001Fa(OCoLC)1604275\u001E  \u001FdCtY\u001FdMBTI\u001FdCtY\u001FdMBTI\u001FdNIC\u001FdCStRLIN\u001FdNIC\u001E0 \u001FaBR140\u001Fb.J6\u001E  \u001Fa270.05\u001E04\u001FaThe Journal of ecclesiastical history\u001E04\u001FaThe Journal of ecclesiastical history.\u001E  \u001FaLondon,\u001FbCambridge University Press [etc.]\u001E  \u001Fa32 East 57th St., New York, 10022\u001E  \u001Fav.\u001Fb25 cm.\u001E  \u001FaQuarterly,\u001Fb1970-\u001E  \u001FaSemiannual,\u001Fb1950-69\u001E0 \u001Fav. 1-   Apr. 1950-\u001E  \u001FaEditor:   C. W. Dugmore.\u001E 0\u001FaChurch history\u001FxPeriodicals.\u001E 7\u001FaChurch history\u001F2fast\u001F0(OCoLC)fst00860740\u001E 7\u001FaPeriodicals\u001F2fast\u001F0(OCoLC)fst01411641\u001E1 \u001FaDugmore, C. W.\u001Fq(Clifford William),\u001Feed.\u001E03\u001F81\u001Fav.\u001Fi(year)\u001E40\u001F81\u001Fa1-49\u001Fi1950-1998\u001E  \u001Fapfnd\u001FbLintz\u001E  \u001Fa19890510120000.0\u001E2 \u001Fa20141106\u001Fbm\u001Fdbatch\u001Felts\u001Fxaddfast\u001E  \u001FlOLIN\u001FaBR140\u001Fb.J86\u001Fh01/01/01 N\u001E\u001D01542ccm a2200361   ");
  private static RawRecord rawRecord_2 = new RawRecord()
    .withId("9b0aec8e-2e0c-11e9-b210-d663bd873d93")
    .withContent("{ \\\"leader\\\": \\\"00508cjm a22001813 4500\\\", \\\"fields\\\": [ { \\\"001\\\": \\\"10062588\\\" }, { \\\"005\\\": \\\"20171013073237.0\\\" }, { \\\"007\\\": \\\"sd fsngnnmmneu\\\" }, { \\\"008\\\": \\\"170825s2017 xx nn n zxx d\\\" }, { \\\"024\\\": { \\\"subfields\\\": [ { \\\"a\\\": \\\"00190295755553\\\" }, { \\\"2\\\": \\\"gtin-14\\\" } ], \\\"ind1\\\": \\\"7\\\", \\\"ind2\\\": \\\" \\\" } }, { \\\"024\\\": { \\\"subfields\\\": [ { \\\"a\\\": \\\"190295755553\\\" } ], \\\"ind1\\\": \\\"1\\\", \\\"ind2\\\": \\\" \\\" } }, { \\\"035\\\": { \\\"subfields\\\": [ { \\\"a\\\": \\\"(OCoLC)1002130878\\\" } ], \\\"ind1\\\": \\\" \\\", \\\"ind2\\\": \\\" \\\" } }, { \\\"035\\\": { \\\"subfields\\\": [ { \\\"a\\\": \\\"10062588\\\" } ], \\\"ind1\\\": \\\" \\\", \\\"ind2\\\": \\\" \\\" } }, { \\\"040\\\": { \\\"subfields\\\": [ { \\\"a\\\": \\\"BTCTA\\\" }, { \\\"b\\\": \\\"eng\\\" }, { \\\"c\\\": \\\"BTCTA\\\" } ], \\\"ind1\\\": \\\" \\\", \\\"ind2\\\": \\\" \\\" } }, { \\\"100\\\": { \\\"subfields\\\": [ { \\\"a\\\": \\\"Rossi, Daniele\\\" } ], \\\"ind1\\\": \\\"1\\\", \\\"ind2\\\": \\\" \\\" } }, { \\\"245\\\": { \\\"subfields\\\": [ { \\\"a\\\": \\\"Saint-Saens: Organ Symphony and Carnival of The Animals\\\" } ], \\\"ind1\\\": \\\"0\\\", \\\"ind2\\\": \\\"0\\\" } }, { \\\"260\\\": { \\\"subfields\\\": [ { \\\"b\\\": \\\"Wea Corp\\\" }, { \\\"c\\\": \\\"2017.\\\" } ], \\\"ind1\\\": \\\" \\\", \\\"ind2\\\": \\\" \\\" } }, { \\\"948\\\": { \\\"subfields\\\": [ { \\\"a\\\": \\\"20171013\\\" }, { \\\"b\\\": \\\"m\\\" }, { \\\"d\\\": \\\"batch\\\" }, { \\\"e\\\": \\\"lts\\\" }, { \\\"x\\\": \\\"deloclcprefix\\\" } ], \\\"ind1\\\": \\\"2\\\", \\\"ind2\\\": \\\" \\\" } } ] }");
  private static TestMarcRecordsCollection recordCollection = new TestMarcRecordsCollection()
    .withRawRecords(Arrays.asList(rawRecord_1, rawRecord_2));

  @Override
  public void clearTables(TestContext context) {
    Async async = context.async();
    PostgresClient pgClient = PostgresClient.getInstance(vertx, TENANT_ID);
    pgClient.delete(RECORDS_TABLE_NAME, new Criterion(), event -> {
      pgClient.delete(RAW_RECORDS_TABLE_NAME, new Criterion(), event1 -> {
        pgClient.delete(ERROR_RECORDS_TABLE_NAME, new Criterion(), event2 -> {
          pgClient.delete(MARC_RECORDS_TABLE_NAME, new Criterion(), event3 -> {
            if (event3.failed()) {
              context.fail(event3.cause());
            }
            async.complete();
          });
        });
      });
    });
  }

  @Test
  public void shouldReturnNoContentOnPostRecordCollectionPassedInBody(TestContext testContext) {
    Async async = testContext.async();
    RestAssured.given()
      .spec(spec)
      .body(recordCollection)
      .when()
      .post(POPULATE_TEST_MARK_RECORDS_PATH)
      .then()
      .statusCode(HttpStatus.SC_NO_CONTENT);
    async.complete();
  }

  @Test
  public void shouldReturnUnprocessableEntityOnPostWhenNoRecordCollectionPassedInBody() {
    TestMarcRecordsCollection testMarcRecordsCollection = new TestMarcRecordsCollection()
      .withRawRecords(Collections.singletonList(new RawRecord()));

    RestAssured.given()
      .spec(spec)
      .body(testMarcRecordsCollection)
      .when()
      .post(POPULATE_TEST_MARK_RECORDS_PATH)
      .then()
      .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);

    RestAssured.given()
      .spec(spec)
      .when()
      .post(POPULATE_TEST_MARK_RECORDS_PATH)
      .then()
      .statusCode(HttpStatus.SC_BAD_REQUEST);
  }
}
