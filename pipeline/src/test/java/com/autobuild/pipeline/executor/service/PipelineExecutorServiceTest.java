package com.autobuild.pipeline.executor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.autobuild.pipeline.definiton.entity.Pipeline;
import com.autobuild.pipeline.definiton.repository.PipelineRepository;
import com.autobuild.pipeline.executor.dto.PipelineBuildDTO;
import com.autobuild.pipeline.executor.dto.PipelineExecuteRequest;
import com.autobuild.pipeline.executor.dto.mapper.PipelineBuildMapper;
import com.autobuild.pipeline.executor.entity.PipelineBuild;
import com.autobuild.pipeline.executor.job.PipelineExecutor;
import com.autobuild.pipeline.executor.repository.PipelineBuildRepository;
import com.autobuild.pipeline.testutility.DummyData;
import com.autobuild.pipeline.utility.file.PipelineFileService;

public class PipelineExecutorServiceTest {

    @Mock
    private PipelineRepository pipelineRepository;

    @Mock
    private PipelineBuildRepository pipelineBuildRepository;

    @Mock
    private PipelineExecutor pipelineExecutor;

    @Mock
    private PipelineBuildMapper pipelineBuildMapper;

    @Mock
    private PipelineFileService pipelineFileService;

    @InjectMocks
    private PipelineExecutorService pipelineExecutorService;

    private PipelineBuildDTO pipelineBuildDTO = DummyData.getPipelineBuildDTO();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void executePipelineTest() throws IOException {
        UUID pipelineId = UUID.randomUUID();

        Pipeline pipeline = DummyData.getPipeline(pipelineId);

        PipelineExecuteRequest pipelineExecuteRequest = new PipelineExecuteRequest(pipelineId);
        PipelineBuild pipelineBuild = DummyData.getPipelineBuild(pipelineId);
        PipelineBuildDTO pipelineBuildDTO = DummyData.getPipelineBuildDTO(pipelineId);

        doReturn(Optional.of(pipeline)).when(pipelineRepository).findById(any(UUID.class));
        doReturn(pipelineBuild).when(pipelineBuildRepository).save(any(PipelineBuild.class));
        doReturn(pipelineBuildDTO).when(pipelineBuildMapper).entityToDto(pipelineBuild);

        try (MockedStatic<TransactionSynchronizationManager> transactionSynMockedStatic = mockStatic(
                TransactionSynchronizationManager.class)) {
            PipelineBuildDTO resultantDTO = pipelineExecutorService.executePipeline(pipelineExecuteRequest);

            assertEquals(pipelineExecuteRequest.getPipelineId(), resultantDTO.getPipelineId());

            verify(pipelineRepository, times(1)).findById(eq(pipelineId));
            verify(pipelineBuildRepository, times(1)).save(any(PipelineBuild.class));

        }
    }
}
