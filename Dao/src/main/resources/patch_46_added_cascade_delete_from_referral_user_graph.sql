ALTER TABLE birzha.REFERRAL_USER_GRAPH DROP FOREIGN KEY referral_user_graph_ibfk_1;
ALTER TABLE birzha.REFERRAL_USER_GRAPH
  ADD CONSTRAINT referral_user_graph_ibfk_1
FOREIGN KEY (child) REFERENCES USER (id) ON DELETE CASCADE ON UPDATE CASCADE;

INSERT INTO DATABASE_PATCH VALUES('patch_46_added_cascade_delete_from_referral_user_graphп',default,1);