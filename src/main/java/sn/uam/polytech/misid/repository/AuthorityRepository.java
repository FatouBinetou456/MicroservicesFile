package sn.uam.polytech.misid.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import sn.uam.polytech.misid.domain.Authority;

/**
 * Spring Data R2DBC repository for the Authority entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuthorityRepository extends R2dbcRepository<Authority, String> {}
