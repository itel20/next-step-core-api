package com.nextstepsenegal.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.nextstepsenegal.app.domain.Conseiller;
import com.nextstepsenegal.app.repository.ConseillerRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Conseiller} entity.
 */
public interface ConseillerSearchRepository extends ElasticsearchRepository<Conseiller, Long>, ConseillerSearchRepositoryInternal {}

interface ConseillerSearchRepositoryInternal {
    Page<Conseiller> search(String query, Pageable pageable);

    Page<Conseiller> search(Query query);

    @Async
    void index(Conseiller entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ConseillerSearchRepositoryInternalImpl implements ConseillerSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ConseillerRepository repository;

    ConseillerSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ConseillerRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Conseiller> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Conseiller> search(Query query) {
        SearchHits<Conseiller> searchHits = elasticsearchTemplate.search(query, Conseiller.class);
        List<Conseiller> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Conseiller entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Conseiller.class);
    }
}
