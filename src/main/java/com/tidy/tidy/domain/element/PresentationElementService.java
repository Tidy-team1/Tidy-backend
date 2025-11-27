package com.tidy.tidy.domain.element;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tidy.tidy.api.parsing.dto.ElementDto;
import com.tidy.tidy.api.parsing.dto.PptParseResponse;
import com.tidy.tidy.api.parsing.dto.SlideDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PresentationElementService {

    private final PresentationElementRepository presentationElementRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void saveParsedElements(Long presentationId, PptParseResponse res) throws JsonProcessingException {

        // 기존 요소가 있다면 삭제(재파싱 지원)
        presentationElementRepository.deleteByPresentationId(presentationId);

        for (SlideDto slideDto : res.getSlides()) {
            Integer slideIdx = slideDto.getIndex();

            for (ElementDto el : slideDto.getElements()) {

                // 1) Python → Spring DTO 매핑이 제대로 되었는지 확인
                log.info("[DTO] slideIndex={}, elementIndex={}, zIndex={}, type={}",
                        slideIdx,
                        el.getElementIndex(),
                        el.getZIndex(),
                        el.getType()
                );

                PresentationElement entity = PresentationElement.builder()
                        .presentationId(presentationId)
                        .slideIndex(slideIdx)
                        .elementIndex(el.getElementIndex())
                        .type(el.getType())
                        .leftPos(el.getLeftPos())
                        .topPos(el.getTopPos())
                        .width(el.getWidth())
                        .height(el.getHeight())
                        .zIndex(el.getZIndex())
                        .rotation(el.getRotation())
                        .detailJson(objectMapper.writeValueAsString(el))
                        .build();

                // 2) DTO → Entity 매핑이 제대로 되었는지 확인
                log.info("[ENTITY] slideIndex={}, elementIndex={}, zIndex={}, type={}",
                        entity.getSlideIndex(),
                        entity.getElementIndex(),
                        entity.getZIndex(),
                        entity.getType()
                );

                presentationElementRepository.save(entity);
            }
        }
    }
}
