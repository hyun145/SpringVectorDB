package kopo.poly.controller;


import kopo.poly.controller.response.CommonResponse;
import kopo.poly.dto.DocumentsDTO;
import kopo.poly.dto.MsgDTO;
import kopo.poly.service.IEmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping(value = "/vector/v1")
@RequiredArgsConstructor
@RestController
public class VectorController {

    private final IEmbeddingService embeddingService;

    @PostMapping("/generate")
    public ResponseEntity<CommonResponse> generateAnswer(@RequestBody String question) {

        log.info("generate Start");

        DocumentsDTO pDTO = DocumentsDTO.builder()
                .question(question).build();

        List<DocumentsDTO> rList = embeddingService.findSimilarDocuments(pDTO);

        log.info("generate End!");

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rList));

    }
    @PostMapping("/document")
    public ResponseEntity<CommonResponse> saveDocument(@RequestBody String content) {
        log.info("saveDocument Start!");

        embeddingService.saveDocument(content);

        MsgDTO msgDTO = MsgDTO.builder().result(1).msg("저장완료.").build();

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), msgDTO)
        );
    }


    @PostMapping("/batch-documents")
    public ResponseEntity<CommonResponse> saveBatchDocuments(@RequestBody DocumentsDTO pDTO) {
        log.info("saveBatchDocuments Start!");


        pDTO.contents().forEach(embeddingService::saveDocument);

        MsgDTO msgDTO = MsgDTO.builder().result(1).msg("여러 건 저장 완료").build();



        log.info("saveBatchDocuments End!");

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), msgDTO)
        );


    }

}
