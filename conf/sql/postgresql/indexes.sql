--
-- PostgreSQL indexes.
-- You have any reasonable size vireo (more than 5k of submissions) and you are
-- using postgresql then you really want to add these indexes to your database.
-- It will help speed things up a lot.

CREATE UNIQUE INDEX actionlog_idx_id ON actionlog USING btree (id);
CREATE INDEX actionlog_idx_sub  ON actionlog USING btree (submission_id);

CREATE UNIQUE INDEX attachment_idx_id ON attachment USING btree (id);
CREATE INDEX attachment_idx_sub ON attachment USING btree (submission_id);

CREATE UNIQUE INDEX committee_member_idx_id ON committee_member USING btree (id);
CREATE INDEX committee_member_idx_order ON committee_member USING btree (displayorder);
CREATE INDEX committee_member_idx_sub ON committee_member USING btree (submission_id);

CREATE UNIQUE INDEX custom_action_value_idx_id ON custom_action_value USING btree (id);
CREATE INDEX custom_action_value_idx_sub ON custom_action_value USING btree (submission_id);


CREATE UNIQUE INDEX embargo_type_idx_id ON embargo_type USING btree (id);

CREATE UNIQUE INDEX person_idx_id ON person USING btree (id);
CREATE INDEX person_idx_email ON person USING btree (email);
CREATE INDEX person_idx_netid ON person USING btree (netid);
CREATE INDEX person_idx_role ON person USING btree (role) WHERE role >= 2;

CREATE UNIQUE INDEX submission_idx_id ON submission USING btree (id);
CREATE INDEX submission_idx_submitter ON submission USING btree (submitter_id);
CREATE INDEX submission_idx_hash ON submission USING hash (committeeemailhash);
CREATE INDEX submission_idx_colleges ON submission USING btree (college);
CREATE INDEX submission_idx_departments ON submission USING btree (department);
CREATE INDEX submission_idx_majors ON submission USING btree (major);

