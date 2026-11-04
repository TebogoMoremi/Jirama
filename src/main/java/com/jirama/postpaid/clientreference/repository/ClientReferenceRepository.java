package com.jirama.postpaid.clientreference.repository;

import com.jirama.postpaid.clientreference.dto.ClientReferenceResponse.ReferenceDetails;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ClientReferenceRepository {

    private final JdbcTemplate jdbcTemplate;

    public ClientReferenceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ReferenceDetails> findValidByMsisdn(String msisdn) {
        String sql = """
            SELECT CLIENT_REF, CLIENT_NUM, BILL_REF, CREATION_DATE
            FROM MB_JIR_REF_DETAILS
            WHERE MSISDN = ?
              AND REF_STATUS = ?
            """;

        return jdbcTemplate.query(
            sql,
            (row, rowNumber) -> new ReferenceDetails(
                row.getString("CLIENT_REF"),
                row.getString("CLIENT_NUM"),
                row.getString("BILL_REF"),
                row.getString("CREATION_DATE")
            ),
            msisdn,
            "VALID"
        );
    }

    public void insertValidReference(
        String msisdn,
        String clientRef,
        String clientNumber,
        String billRef
    ) {
        jdbcTemplate.update("""
            INSERT INTO MB_JIR_REF_DETAILS
                (MSISDN, CLIENT_REF, CLIENT_NUM, BILL_REF, CREATION_DATE, REF_STATUS)
            VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, 'VALID')
            """,
            msisdn,
            clientRef,
            clientNumber,
            billRef
        );
    }

    public boolean existsValidByMsisdnAndClientRef(String msisdn, String clientRef) {
        String sql = """
            SELECT COUNT(*)
            FROM MB_JIR_REF_DETAILS
            WHERE MSISDN = ?
              AND CLIENT_REF = ?
              AND REF_STATUS = ?
            """;

        Long matches = jdbcTemplate.queryForObject(
            sql, Long.class, msisdn, clientRef, "VALID"
        );

        return matches != null && matches > 0;
    }
}