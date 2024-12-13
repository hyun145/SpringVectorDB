package kopo.poly.service.impl;


import kopo.poly.domain.Documents;
import kopo.poly.dto.DocumentsDTO;
import kopo.poly.repository.DocumentRepository;
import kopo.poly.service.IEmbeddingClient;
import kopo.poly.service.IEmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingService implements IEmbeddingService {

    private static final double SIMILARITY_THRESHOLD = 0.8;

    private static final int TOP_K = 2;

    private final IEmbeddingClient embeddingClient;

    private final DocumentRepository documentRepository;

    private double[] embedQuestion(String text) {
        return generateEmbedding(text).stream()
                .mapToDouble(Double::doubleValue)
                .toArray();
    }

    private List<Documents> getRelevantDocuments(double[] questionEmbedding) {
        return documentRepository.findAll().stream()
                .map(document -> {
                    double similarity = calculateCosineSimilarity(
                            questionEmbedding,
                            document.getEmbedding().stream().mapToDouble(Double::doubleValue).toArray()
                    );
                    document.setSimilarityScore(similarity);
                    return document;
                })
                .filter(doc -> doc.getSimilarityScore() >= SIMILARITY_THRESHOLD)
                .sorted((d1, d2) -> Double.compare(d2.getSimilarityScore(), d1.getSimilarityScore()))
                .limit(TOP_K)
                .collect(Collectors.toList());
    }

    private double calculateCosineSimilarity(double[] vectorA, double[] vectorB) {
        if (vectorA.length != vectorB.length) {
            throw new IllegalArgumentException("Vectors must be of the same length");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i =0; i < vectorA.length; i ++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += vectorA[i] * vectorA[i];
            normB += vectorB[i] * vectorB[i];

        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    @Override
    public List<Double> generateEmbedding(String text) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("text", text);

        Map<String, Object> response = embeddingClient.generateEmbedding(requestBody);


        if (!response.containsKey("embedding")) {
            throw new IllegalStateException("Embedding not found in the response");
        }
        return (List<Double>) response.get("embedding");
    }

    @Override
    public void saveDocument(String content) {
        log.info("saveDocument Start!");
        
        List<Double> embedding = generateEmbedding(content);
        
        
        Documents documentEntity = Documents.builder()
                .content(content)
                .embedding(embedding)
                .build();
        
        documentRepository.save(documentEntity);
        
        log.info("도큐먼트 저장 완료");
        log.info("도큐먼트 저장 서비스 종료");

    }

    @Override
    public List<DocumentsDTO> findSimilarDocuments(DocumentsDTO pDTO) {
        log.info("findSimilarDocuments Start!");

        double[] questionEmbedding = embedQuestion(pDTO.question());

        List<Documents> relevantDocuments = getRelevantDocuments(questionEmbedding);

        List<DocumentsDTO> documentDTOList = relevantDocuments.stream()
                        .map(doc -> DocumentsDTO.builder()
                                .id(doc.getId())
                                .content(doc.getContent())
                                .build())
                                .collect(Collectors.toList());


        log.info("Document DTO list : {}", documentDTOList);

        log.info("findSimilarDocuments End!");
        return documentDTOList;
    }
}
