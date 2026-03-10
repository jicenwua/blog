package com.xcz.article.service.impl;

import com.xcz.article.document.ArticleDocument;
import com.xcz.article.repository.es.ArticleEsRepository;
import com.xcz.article.service.ArticleEsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

/**
 * 文章 Elasticsearch 服务实现
 *
 * @author xcz
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleEsServiceImpl implements ArticleEsService {

    private final ArticleEsRepository articleEsRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public Page<ArticleDocument> searchArticles(String keyword, int page, int size) {
        log.info("搜索文章：keyword={}, page={}, size={}", keyword, page, size);

        Pageable pageable = PageRequest.of(page, size);

        // 使用自定义的 multi_match 查询
        // 标题权重最高 (^3)，标签次之 (^2)，内容最低
        Page<ArticleDocument> result = articleEsRepository.searchArticles(keyword, pageable);

        log.info("搜索结果：总共{}条，返回{}条", result.getTotalElements(), result.getNumberOfElements());

        return result;
    }

    @Override
    public void syncArticle(ArticleDocument document) {
        log.info("同步文章到 Elasticsearch: id={}", document.getId());
        articleEsRepository.save(document);
        log.info("文章同步成功：id={}", document.getId());
    }

    @Override
    public void deleteArticle(String id) {
        log.info("从 Elasticsearch 删除文章：id={}", id);
        articleEsRepository.deleteById(id);
        log.info("文章删除成功：id={}", id);
    }
}
