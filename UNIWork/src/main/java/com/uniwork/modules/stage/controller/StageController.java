package com.uniwork.modules.stage.controller;

import com.uniwork.modules.auth.dto.Payload;
import com.uniwork.modules.stage.dto.StageDetailDTO;
import com.uniwork.modules.stage.dto.StageSummaryDTO;
import com.uniwork.modules.stage.entity.Stage;
import com.uniwork.modules.stage.request.MoveTasksRequest;
import com.uniwork.modules.stage.request.StageRequest;
import com.uniwork.common.response.ResponseFactory;
import com.uniwork.modules.stage.service.StageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/projects/{projectId}/stages")
@PreAuthorize("hasAuthority('PERM_MANAGE_STAGES')")
public class StageController {

    @Autowired
    private StageService stageService;

    // =====================================================
    // GET ALL STAGES OF A PROJECT (summary view)
    // =====================================================

    @GetMapping
    public ResponseEntity getAllStages(@PathVariable Long projectId,
                                       @RequestAttribute(required = false) Payload payload) {
        log.info("Getting all stages for projectId: {}", projectId);
        List<StageSummaryDTO> stages = stageService.getStagesSummary(projectId);
        return ResponseFactory.success(stages);
    }

    // =====================================================
    // GET STAGE DETAIL (with tasks + stats)
    // =====================================================

    @GetMapping("/{stageId}")
    public ResponseEntity getStageDetail(@PathVariable Long projectId,
                                          @PathVariable Long stageId,
                                          @RequestAttribute(required = false) Payload payload) {
        log.info("Getting stage detail for stageId: {} in projectId: {}", stageId, projectId);
        StageDetailDTO stageDetail = stageService.getStageDetail(stageId);
        return ResponseFactory.success(stageDetail);
    }

    // =====================================================
    // CREATE STAGE
    // =====================================================

    @PreAuthorize("hasAuthority('PERM_CREATE_STAGE')")
    @PostMapping
    public ResponseEntity createStage(@PathVariable Long projectId,
                                       @RequestBody StageRequest stageRequest,
                                       @RequestAttribute(required = false) Payload payload) {
        log.info("Creating stage for projectId: {} with name: {}", projectId, stageRequest.getName());
        Stage stage = stageService.createStage(projectId, stageRequest);
        return ResponseFactory.success(stage);
    }

    // =====================================================
    // UPDATE STAGE
    // =====================================================

    @PreAuthorize("hasAuthority('PERM_CREATE_STAGE')")
    @PutMapping("/{stageId}")
    public ResponseEntity updateStage(@PathVariable Long projectId,
                                       @PathVariable Long stageId,
                                       @RequestBody StageRequest stageRequest,
                                       @RequestAttribute(required = false) Payload payload) {
        log.info("Updating stage: {} for projectId: {}", stageId, projectId);
        Stage stage = stageService.updateStage(stageId, stageRequest);
        return ResponseFactory.success(stage);
    }

    // =====================================================
    // DELETE STAGE (soft delete)
    // =====================================================

    @PreAuthorize("hasAuthority('PERM_CREATE_STAGE')")
    @DeleteMapping("/{stageId}")
    public ResponseEntity deleteStage(@PathVariable Long projectId,
                                       @PathVariable Long stageId,
                                       @RequestAttribute(required = false) Payload payload) {
        log.info("Deleting stage: {} for projectId: {}", stageId, projectId);
        stageService.deleteStage(stageId);
        return ResponseFactory.success("Stage deleted successfully");
    }

    // =====================================================
    // ACTIVATE STAGE
    // =====================================================

    @PreAuthorize("hasAuthority('PERM_ACTIVATE_STAGE')")
    @PostMapping("/{stageId}/activate")
    public ResponseEntity activateStage(@PathVariable Long projectId,
                                         @PathVariable Long stageId,
                                         @RequestAttribute(required = false) Payload payload) {
        log.info("Activating stage: {} for projectId: {}", stageId, projectId);
        stageService.activateStage(stageId);
        return ResponseFactory.success("Stage activated successfully");
    }

    // =====================================================
    // COMPLETE STAGE
    // =====================================================

    @PreAuthorize("hasAuthority('PERM_ACTIVATE_STAGE')")
    @PostMapping("/{stageId}/complete")
    public ResponseEntity completeStage(@PathVariable Long projectId,
                                         @PathVariable Long stageId,
                                         @RequestAttribute(required = false) Payload payload) {
        log.info("Completing stage: {} for projectId: {}", stageId, projectId);
        stageService.completeStage(stageId);
        return ResponseFactory.success("Stage completed successfully");
    }

    // =====================================================
    // MOVE TASKS BETWEEN STAGES
    // =====================================================

    @PreAuthorize("hasAuthority('PERM_ACTIVATE_STAGE')")
    @PostMapping("/{stageId}/move-tasks")
    public ResponseEntity moveTasksToStage(@PathVariable Long projectId,
                                            @PathVariable Long stageId,
                                            @RequestBody MoveTasksRequest moveTasksRequest,
                                            @RequestAttribute(required = false) Payload payload) {
        log.info("Moving tasks to stage: {} in projectId: {}", stageId, projectId);
        moveTasksRequest.setTargetStageId(stageId);
        stageService.moveTasksToStage(moveTasksRequest);
        return ResponseFactory.success("Tasks moved successfully");
    }

}
