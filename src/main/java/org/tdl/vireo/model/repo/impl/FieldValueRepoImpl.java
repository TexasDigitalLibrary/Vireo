package org.tdl.vireo.model.repo.impl;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.repo.FieldValueRepo;
import org.tdl.vireo.model.repo.custom.FieldValueRepoCustom;

public class FieldValueRepoImpl extends AbstractWeaverRepoImpl<FieldValue, FieldValueRepo> implements FieldValueRepoCustom {

    final static String VALUES_BY_PREDICATE = "SELECT DISTINCT fv.value AS value FROM field_value fv WHERE fv.field_predicate_id IN " +
                                              "(SELECT fp.id FROM field_predicate fp WHERE fp.value = ?) ORDER BY fv.value ASC";

    @Autowired
    private FieldValueRepo fieldValueRepo;

    private JdbcTemplate jdbcTemplate;

    public FieldValueRepoImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public FieldValue create(FieldPredicate fieldPredicate) {
        return fieldValueRepo.save(new FieldValue(fieldPredicate));
    }

    @Override
    protected String getChannel() {
        return "/channel/field-value";
    }

    @Override
    public List<String> getAllValuesByFieldPredicateValue(String fieldPredicateValue) {
        String query = "SELECT DISTINCT fv.value AS value FROM field_value fv WHERE fv.field_predicate_id IN (select fp.id FROM field_predicate fp WHERE fp.value = ?) ORDER BY fv.value ASC";

        List<String> list = new ArrayList<>();
        jdbcTemplate.queryForList(VALUES_BY_PREDICATE, fieldPredicateValue).forEach(row -> {
            list.add((String) row.get("value"));
        });

        return list;
    }

}
