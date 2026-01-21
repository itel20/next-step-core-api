package com.nextstepsenegal.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.nextstepsenegal.app.domain.Publication;
import com.nextstepsenegal.app.repository.PublicationRepository;
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
 * Spring Data Elasticsearch repository for the {@link Publication} entity.
 */
public interface PublicationSearchRepository extends ElasticsearchRepository<Publication, Long>, PublicationSearchRepositoryInternal {}

interface PublicationSearchRepositoryInternal {
    Page<Publication> search(String query, Pageable pageable);

    Page<Publication> search(Query query);

    @Async
    void index(Publication entity);

    @Async
    void deleteFromIndexById(Long id);
}

class PublicationSearchRepositoryInternalImpl implements PublicationSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final PublicationRepository repository;

    PublicationSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, PublicationRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Publication> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Publication> search(Query query) {
        SearchHits<Publication> searchHits = elasticsearchTemplate.search(query, Publication.class);
        List<Publication> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Publication entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Publication.class);
    }
}
