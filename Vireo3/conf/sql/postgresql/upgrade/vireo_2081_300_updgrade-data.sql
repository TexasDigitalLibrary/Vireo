INSERT INTO submission_embargotypes (submission_id, embargotypeids) SELECT id submission_id, embargotype_id embargotypeids FROM submission WHERE embargotype_id IS NOT NULL;
