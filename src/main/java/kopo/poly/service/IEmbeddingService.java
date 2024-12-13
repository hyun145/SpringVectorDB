package kopo.poly.service;

import kopo.poly.dto.DocumentsDTO;

import javax.swing.text.Document;
import java.util.List;

public interface IEmbeddingService {

    List<Double> generateEmbedding(String text);


    void saveDocument(String content);


    List<DocumentsDTO> findSimilarDocuments(DocumentsDTO pDTO);


}
