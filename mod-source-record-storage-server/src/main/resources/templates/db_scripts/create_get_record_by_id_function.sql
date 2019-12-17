
CREATE OR REPLACE FUNCTION get_record_by_id(record_id uuid)
RETURNS json AS $recordDto$
DECLARE
	recordDto json;
BEGIN
  SELECT json_build_object('id', records.jsonb->>'id',
          'snapshotId', records.jsonb->>'snapshotId',
					'matchedProfileId', records.jsonb->>'matchedProfileId',
					'matchedId', records.jsonb->>'matchedId',
					'generation', (records.jsonb->>'generation')::integer,
					'recordType', records.jsonb->>'recordType',
					'deleted', records.jsonb->>'deleted',
					'order', (records.jsonb->>'order')::integer,
					'externalIdsHolder', records.jsonb->'externalIdsHolder',
					'additionalInfo', records.jsonb->'additionalInfo',
					'metadata', records.jsonb->'metadata',
 					'rawRecord', raw_records.jsonb,
 					'parsedRecord', COALESCE(marc_records.jsonb),
					'errorRecord', error_records.jsonb)
					AS jsonb
      INTO recordDto
  FROM records
  JOIN raw_records ON records.jsonb->>'rawRecordId' = raw_records.jsonb->>'id'
  LEFT JOIN marc_records ON records.jsonb->>'parsedRecordId' = marc_records.jsonb->>'id'
  LEFT JOIN error_records ON records.jsonb->>'errorRecordId' = error_records.jsonb->>'id'
  WHERE records._id = record_id;

RETURN recordDto;
END;
$recordDto$ LANGUAGE plpgsql;