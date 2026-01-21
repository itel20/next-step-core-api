package com.nextstepsenegal.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.nextstepsenegal.app.domain.PublicationShare;
import com.nextstepsenegal.app.repository.PublicationShareRepository;
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
 * Spring Data Elasticsearch repository for the {@link PublicationShare} entity.
 */
public interface PublicationShareSearchRepository
    extends ElasticsearchRepository<PublicationShare, Long>, PublicationShareSearchRepositoryInternal {}

interface PublicationShareSearchRepositoryInternal {
    Page<PublicationShare> search(String query, Pageable pageable);

    Page<PublicationShare> search(Query query);

    @Async
    void index(PublicationShare entity);

    @Async
    void deleteFromIndexById(Long id);
}

class PublicationShareSearchRepositoryInternalImpl implements PublicationShareSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final PublicationShareRepository repository;

    PublicationShareSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, PublicationShareRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<PublicationShare> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<PublicationShare> search(Query query) {
        SearchHits<PublicationShare> searchHits = elasticsearchTemplate.search(query, PublicationShare.class);
        List<PublicationShare> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(PublicationShare entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), PublicationShare.class);
    }
}
