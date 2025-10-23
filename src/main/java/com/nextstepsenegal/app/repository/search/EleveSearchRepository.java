package com.nextstepsenegal.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.nextstepsenegal.app.domain.Eleve;
import com.nextstepsenegal.app.repository.EleveRepository;
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
 * Spring Data Elasticsearch repository for the {@link Eleve} entity.
 */
public interface EleveSearchRepository extends ElasticsearchRepository<Eleve, Long>, EleveSearchRepositoryInternal {}

interface EleveSearchRepositoryInternal {
    Page<Eleve> search(String query, Pageable pageable);

    Page<Eleve> search(Query query);

    @Async
    void index(Eleve entity);

    @Async
    void deleteFromIndexById(Long id);
}

class EleveSearchRepositoryInternalImpl implements EleveSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final EleveRepository repository;

    EleveSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, EleveRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Eleve> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Eleve> search(Query query) {
        SearchHits<Eleve> searchHits = elasticsearchTemplate.search(query, Eleve.class);
        List<Eleve> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Eleve entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Eleve.class);
    }
}
