package sn.uam.polytech.misid.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;
import sn.uam.polytech.misid.domain.User;

/**
 * Spring Data Elasticsearch repository for the User entity.
 */
public interface UserSearchRepository extends ReactiveElasticsearchRepository<User, String>, UserSearchRepositoryInternal {}

interface UserSearchRepositoryInternal {
    Flux<User> search(String query);
}

class UserSearchRepositoryInternalImpl implements UserSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    UserSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<User> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return reactiveElasticsearchTemplate.search(nativeQuery, User.class).map(SearchHit::getContent);
    }
}
