package com.autobuild.pipeline.definition.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import com.autobuild.pipeline.definiton.dto.PipelineDTO;
import com.autobuild.pipeline.definiton.dto.StageDTO;
import com.autobuild.pipeline.definiton.dto.mapper.PipelineMapper;
import com.autobuild.pipeline.definiton.dto.mapper.StageMapper;
import com.autobuild.pipeline.definiton.entity.Pipeline;
import com.autobuild.pipeline.definiton.entity.Stage;
import com.autobuild.pipeline.definiton.exceptions.DuplicateEntryException;
import com.autobuild.pipeline.definiton.exceptions.InvalidIdException;
import com.autobuild.pipeline.definiton.repository.PipelineRepository;
import com.autobuild.pipeline.definiton.service.PipelineService;
import com.autobuild.pipeline.testutility.DummyData;
import com.autobuild.pipeline.utility.file.PipelineFileService;

import jakarta.persistence.EntityNotFoundException;

public class PipelineServiceTest {

    private PipelineDTO pipelineDTO = DummyData.getPipelineDTO();
    private Pipeline pipeline = DummyData.getPipeline();

    @Mock
    private PipelineRepository repository;

    @Mock
    private PipelineMapper mapper;

    @Mock
    private PipelineFileService fileService;

    @InjectMocks
    private PipelineService service;

    @Mock
    private StageMapper stageMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Dynamic mapper stub to reflect pipeline mutations
        doAnswer(inv -> {
            Pipeline p = inv.getArgument(0);
            PipelineDTO dto = new PipelineDTO();
            dto.setId(p.getId());
            dto.setName(p.getName());
            List<StageDTO> stages = p.getStages().stream().map(s -> {
                StageDTO sd = new StageDTO();
                sd.setId(s.getId());
                sd.setName(s.getName());
                sd.setScriptType(s.getScriptType());
                sd.setCommand(""); // service will overwrite with file contents
                return sd;
            }).collect(Collectors.toList());
            dto.setStages(stages);
            return dto;
        }).when(mapper).entityToDto(any(Pipeline.class));

        doAnswer(inv -> {
            PipelineDTO dto = inv.getArgument(0);
            Pipeline p = new Pipeline();
            p.setId(dto.getId());
            p.setName(dto.getName());
            p.setStages(dto.getStages().stream().map(st -> {
                Stage s = new Stage();
                s.setId(st.getId());
                s.setName(st.getName());
                s.setScriptType(st.getScriptType());
                return s;
            }).collect(Collectors.toList()));
            return p;
        }).when(mapper).dtoToEntity(any(PipelineDTO.class));

        doAnswer(inv -> {
            StageDTO sd = inv.getArgument(0);
            Stage s = new Stage();
            s.setId(sd.getId());
            s.setName(sd.getName());
            s.setScriptType(sd.getScriptType());
            return s;
        }).when(stageMapper).dtoToEntity(any(StageDTO.class));
    }

    @Test
    public void testGetPipelineByIdWithEmptyId() {
        assertThrows(InvalidIdException.class, () -> service.getPipelineById(null));
        assertThrows(InvalidIdException.class, () -> service.getPipelineById(""));
        assertThrows(InvalidIdException.class, () -> service.getPipelineById(" "));
    }

    @Test
    public void testGetPipelineByIdWithInvalidId() {
        try (MockedStatic<UUID> uuid = mockStatic(UUID.class)) {
            uuid.when(() -> UUID.fromString(anyString()))
                    .thenThrow(new IllegalArgumentException("Dummy msg: invalid id given"));

            assertThrows(InvalidIdException.class, () -> service.getPipelineById("1"));
        }
    }

    @Test
    public void testGetPipelineByIdWithValidId() throws InvalidIdException, IOException {
        UUID randomId = UUID.randomUUID();

        try (MockedStatic<UUID> uuid = mockStatic(UUID.class)) {
            uuid.when(() -> UUID.fromString(anyString())).thenReturn(randomId);
            doReturn(Optional.of(pipeline)).when(repository).findById(any(UUID.class));
            doReturn(pipelineDTO).when(mapper).entityToDto(pipeline);

            assertEquals(pipelineDTO, service.getPipelineById("1"));
        }
    }

    @Test
    public void testGetPipelineByIdWithValidIdButNoPipeline() throws InvalidIdException {
        UUID randomId = UUID.randomUUID();

        try (MockedStatic<UUID> uuid = mockStatic(UUID.class)) {
            uuid.when(() -> UUID.fromString(anyString())).thenReturn(randomId);
            doReturn(Optional.ofNullable(null)).when(repository).findById(any(UUID.class));

            assertThrows(EntityNotFoundException.class, () -> service.getPipelineById("1"));
        }
    }

    @Test
    public void testCreatePipelineWithNullPipeline() throws DuplicateEntryException, IOException {
        assertNull(service.createPipeline(null));
    }

    @Test
    public void testCreatePipelineWithValidPipeline() throws DuplicateEntryException, IOException {
        doReturn(pipeline).when(repository).save(any(Pipeline.class));
        doReturn(pipelineDTO).when(mapper).entityToDto(pipeline);

        assertEquals(pipelineDTO, service.createPipeline(pipelineDTO));

        verify(fileService, times(1)).createScriptFiles(pipelineDTO);
    }

    @Test
    public void testCreatePipelineWithValidPipelineWithIOException() throws DuplicateEntryException, IOException {
        doReturn(pipeline).when(repository).save(any(Pipeline.class));
        doReturn(pipelineDTO).when(mapper).entityToDto(pipeline);
        doThrow(new IOException("Dummy Exception")).when(fileService).createScriptFiles(pipelineDTO);

        assertThrows(IOException.class, () -> service.createPipeline(pipelineDTO));

        verify(fileService, times(1)).createScriptFiles(pipelineDTO);
        verify(fileService, times(1)).removeScriptFiles(pipelineDTO);
    }

    @Test
    public void testCreatePipelineWithDuplicatePipelineName() throws Exception {
        doThrow(new DataIntegrityViolationException("Dummy Exception"))
                .when(repository).save(any(Pipeline.class));

        assertThrows(DataIntegrityViolationException.class, () -> service.createPipeline(pipelineDTO));
    }

    @Test
    public void testDeletePipelineById() throws IOException, InvalidIdException {
        doReturn(Optional.of(pipeline)).when(repository).findById(pipeline.getId());
        doNothing().when(repository).delete(any(Pipeline.class));

        service.deletePipelineById(pipeline.getId().toString());

        verify(repository, times(1)).findById(pipeline.getId());
        verify(repository, times(1)).delete(pipeline);
    }

    @Test
    public void testModifyPipelineStagesCreate() throws Exception {
        if (pipeline.getId() == null) {
            pipeline.setId(UUID.randomUUID());
        }
        pipeline.setStages(new ArrayList<>(pipeline.getStages()));
        String pipelineId = pipeline.getId().toString();

        doReturn(Optional.of(pipeline)).when(repository).findById(pipeline.getId());
        doReturn(pipeline).when(repository).save(any(Pipeline.class));

        StageDTO newStage = new StageDTO(null, "deploy", "bash", "#!/bin/bash\necho DEPLOY");
        UUID generatedStageId = UUID.randomUUID();

        try (MockedStatic<UUID> uuid = mockStatic(UUID.class)) {

            // FIX #1: Allow service to parse pipelineId
            uuid.when(() -> UUID.fromString(pipelineId)).thenReturn(pipeline.getId());

            // FIX #2: Only mock randomUUID()
            uuid.when(UUID::randomUUID).thenReturn(generatedStageId);

            doReturn("path/deploy.sh").when(fileService)
                    .createStageScriptFile(any(Pipeline.class), any(StageDTO.class));

            doAnswer(inv -> {
                Map<UUID, String> m = new HashMap<>();
                pipeline.getStages().forEach(s -> m.put(s.getId(), "EXISTING"));
                m.put(generatedStageId, newStage.getCommand());
                return m;
            }).when(fileService).readScriptFiles(any(Pipeline.class));

            int originalSize = pipeline.getStages().size();
            PipelineDTO result = service.modifyPipelineStages(pipelineId, List.of(newStage));

            assertEquals(originalSize + 1, result.getStages().size());
            StageDTO created = result.getStages().stream()
                    .filter(s -> s.getId().equals(generatedStageId))
                    .findFirst().orElseThrow();
            assertEquals(newStage.getCommand(), created.getCommand());
        }
    }

    @Test
    public void testModifyPipelineStagesUpdate() throws Exception {
        if (pipeline.getId() == null) {
            pipeline.setId(UUID.randomUUID());
        }
        pipeline.setStages(new ArrayList<>(pipeline.getStages()));
        String pipelineId = pipeline.getId().toString();

        doReturn(Optional.of(pipeline)).when(repository).findById(pipeline.getId());
        doReturn(pipeline).when(repository).save(any(Pipeline.class));

        Stage existing = pipeline.getStages().get(0);
        StageDTO update = new StageDTO(existing.getId(), "renamed", existing.getScriptType(),
                "#!/bin/bash\necho UPDATED");

        doNothing().when(fileService).updateStageScriptFile(any(Stage.class), anyString());
        doAnswer(inv -> {
            Map<UUID, String> m = new HashMap<>();
            pipeline.getStages().forEach(s -> {
                String cmd = s.getId().equals(existing.getId()) ? update.getCommand() : "EXISTING";
                m.put(s.getId(), cmd);
            });
            return m;
        }).when(fileService).readScriptFiles(any(Pipeline.class));

        PipelineDTO result = service.modifyPipelineStages(pipelineId, List.of(update));
        StageDTO updated = result.getStages().stream()
                .filter(s -> s.getId().equals(existing.getId()))
                .findFirst().orElseThrow();
        assertEquals("renamed", updated.getName());
        assertEquals(update.getCommand(), updated.getCommand());
    }

    @Test
    public void testModifyPipelineStagesDelete() throws Exception {
        if (pipeline.getId() == null) {
            pipeline.setId(UUID.randomUUID());
        }
        pipeline.setStages(new ArrayList<>(pipeline.getStages()));
        String pipelineId = pipeline.getId().toString();

        doReturn(Optional.of(pipeline)).when(repository).findById(pipeline.getId());
        doReturn(pipeline).when(repository).save(any(Pipeline.class));

        Stage toDelete = pipeline.getStages().get(0);
        StageDTO deleteReq = new StageDTO(toDelete.getId(), null, null, null);

        try (MockedStatic<UUID> uuid = mockStatic(UUID.class)) {

            // FIX: allow repository lookup
            uuid.when(() -> UUID.fromString(pipelineId)).thenReturn(pipeline.getId());

            doNothing().when(fileService).removeStageScriptFile(any(Stage.class));
            doAnswer(inv -> {
                Map<UUID, String> m = new HashMap<>();
                pipeline.getStages().stream()
                        .filter(s -> !s.getId().equals(toDelete.getId()))
                        .forEach(s -> m.put(s.getId(), "EXISTING"));
                return m;
            }).when(fileService).readScriptFiles(any(Pipeline.class));

            int originalSize = pipeline.getStages().size();
            PipelineDTO result = service.modifyPipelineStages(pipelineId, List.of(deleteReq));

            assertEquals(originalSize - 1, result.getStages().size());
            boolean present = result.getStages().stream()
                    .anyMatch(s -> s.getId().equals(toDelete.getId()));
            assertEquals(false, present);
        }
    }

    @Test
    public void testModifyPipelineStagesDuplicateCreateName() throws Exception {
        pipeline.setStages(new ArrayList<>(pipeline.getStages()));
        doReturn(Optional.of(pipeline)).when(repository).findById(any(UUID.class));

        String existingName = pipeline.getStages().get(0).getName();
        StageDTO duplicate = new StageDTO(null, existingName, "bash", "#!/bin/bash\necho X");

        assertThrows(DuplicateEntryException.class,
                () -> service.modifyPipelineStages(pipeline.getId().toString(), List.of(duplicate)));
    }

    @Test
    public void testModifyPipelineStagesDuplicateUpdateName() throws Exception {
        pipeline.setStages(new ArrayList<>(pipeline.getStages()));
        if (pipeline.getStages().size() < 2)
            return;

        doReturn(Optional.of(pipeline)).when(repository).findById(any(UUID.class));

        Stage target = pipeline.getStages().get(0);
        String otherName = pipeline.getStages().get(1).getName();
        StageDTO update = new StageDTO(target.getId(), otherName, null, null);

        assertThrows(DuplicateEntryException.class,
                () -> service.modifyPipelineStages(pipeline.getId().toString(), List.of(update)));
    }

    @Test
    public void testModifyPipelineStagesStageNotFound() throws Exception {
        pipeline.setStages(new ArrayList<>(pipeline.getStages()));
        doReturn(Optional.of(pipeline)).when(repository).findById(any(UUID.class));

        StageDTO update = new StageDTO(UUID.randomUUID(), "X", null, null);

        assertThrows(InvalidIdException.class,
                () -> service.modifyPipelineStages(pipeline.getId().toString(), List.of(update)));
    }

    @Test
    public void testModifyPipelineStagesEmptyList() {
        assertThrows(InvalidIdException.class,
                () -> service.modifyPipelineStages(pipeline.getId().toString(), List.of()));
    }
}
