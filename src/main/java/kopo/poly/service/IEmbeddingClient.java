package kopo.poly.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "EmbeddingClient", url = "${embedding.api.url}")
public interface IEmbeddingClient {

    @PostMapping("/embedding")
    Map<String, Object> generateEmbedding(@RequestBody Map<String, String> requestBody);

}
