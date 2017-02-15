# DELIMITER $$

DROP FUNCTION IF EXISTS AddToSet $$

CREATE FUNCTION AddToSet(NextValues MEDIUMTEXT, CurrentSet MEDIUMTEXT) RETURNS MEDIUMTEXT
DETERMINISTIC
    BEGIN
        IF LENGTH(CurrentSet) = 0 OR CurrentSet is NULL THEN
            RETURN NextValues;
        ELSE
            RETURN CONCAT(CurrentSet,',',NextValues);
        END IF;
    END $$

DROP FUNCTION IF EXISTS GetAllDestinations $$

CREATE FUNCTION GetAllDestinations (InitialQueue MEDIUMTEXT, DescendType LONG, LeafType LONG) RETURNS MEDIUMTEXT
DETERMINISTIC
    BEGIN

        DECLARE rv,q,queue,queue_children,leaves, processed,data_models_with_containment MEDIUMTEXT;
        DECLARE queue_length,front_id,pos LONG;

        SET data_models_with_containment = (select group_concat(DISTINCT src.data_model_id) from relationship r
            join catalogue_element src on src.id = r.source_id
            join catalogue_element dest on dest.id = r.destination_id
            where r.relationship_type_id = LeafType or r.relationship_type_id = DescendType and src.data_model_id <> dest.data_model_id);
        SET rv = '';
        SET queue = InitialQueue;
        SET queue_length = LENGTH(InitialQueue) - LENGTH(REPLACE(InitialQueue, ',', '')) + 1;

        WHILE queue_length > 0 DO
            SET front_id = ROUND(queue,0);

            IF queue_length = 1 THEN
                SET queue = '';
            ELSE
                SET pos = LOCATE(',',queue) + 1;
                SET q = SUBSTR(queue,pos);
                SET queue = q;
            END IF;

            SET queue_length = queue_length - 1;

            IF IFNULL(find_in_set(front_id, processed), 0) <= 0 THEN
                SET processed = AddToSet(front_id, processed);

                SELECT IFNULL(qc,'') INTO queue_children
                FROM (SELECT GROUP_CONCAT(r.destination_id) qc
                      FROM relationship r JOIN catalogue_element ce ON r.destination_id = ce.id WHERE r.source_id = front_id AND r.relationship_type_id = DescendType AND (find_in_set(ce.data_model_id, InitialQueue) OR find_in_set(ce.data_model_id, data_models_with_containment))) A;

                SELECT IFNULL(qc,'') INTO leaves
                FROM (SELECT GROUP_CONCAT(r.destination_id) qc
                      FROM relationship r JOIN catalogue_element ce ON r.destination_id = ce.id WHERE r.source_id = front_id AND r.relationship_type_id = LeafType AND (find_in_set(ce.data_model_id, InitialQueue) OR find_in_set(ce.data_model_id, data_models_with_containment))) A;



                IF LENGTH(queue_children) = 0 THEN
                    IF LENGTH(queue) = 0 THEN
                        SET queue_length = 0;
                    END IF;
                ELSE
                    SET rv = AddToSet(rv, leaves);
                    SET queue = AddToSet(queue_children, queue);
                    SET queue_length = LENGTH(queue) - LENGTH(REPLACE(queue,',','')) + 1;
                END IF;
            END IF;
        END WHILE;

        RETURN rv;

    END $$

DROP FUNCTION IF EXISTS GetTopLevelDataClasses $$
CREATE FUNCTION GetTopLevelDataClasses (DataModelId LONG, DescendType LONG) RETURNS MEDIUMTEXT
DETERMINISTIC
  BEGIN
    RETURN (
      select group_concat(DISTINCT ce.id) from catalogue_element ce join data_class dc on ce.id = dc.id
      where ce.data_model_id = DataModelId
      and ce.id not in (
        select r.destination_id from relationship r join catalogue_element src on r.source_id = src.id
        where src.data_model_id = DataModelId AND r.relationship_type_id = DescendType
      )
  );
  END $$

# DELIMITER ;

