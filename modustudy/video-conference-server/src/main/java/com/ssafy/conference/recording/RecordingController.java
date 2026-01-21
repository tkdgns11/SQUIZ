package com.ssafy.conference.recording;

import com.ssafy.conference.recording.dto.RecordingCompleteRequest;
import com.ssafy.conference.recording.dto.RecordingDetailResponse;
import com.ssafy.conference.recording.dto.RecordingStartRequest;
import com.ssafy.conference.recording.dto.RecordingStartResponse;
import com.ssafy.conference.recording.dto.RecordingStopRequest;
import com.ssafy.conference.recording.dto.RecordingStopResponse;
import com.ssafy.conference.recording.dto.RecordingSummaryItem;
import com.ssafy.conference.recording.dto.SummaryUpsertRequest;
import com.ssafy.conference.recording.dto.TranscriptUpsertRequest;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recordings")
public class RecordingController {
  private final RecordingService recordingService;

  public RecordingController(RecordingService recordingService) {
    this.recordingService = recordingService;
  }

  @PostMapping("/start")
  public ResponseEntity<RecordingStartResponse> start(@RequestBody RecordingStartRequest request) {
    Recording recording = recordingService.startRecording(request.getRoomId());
    return ResponseEntity.ok(new RecordingStartResponse(recording.getId(), recording.getStatus().name()));
  }

  @PostMapping("/stop")
  public ResponseEntity<RecordingStopResponse> stop(@RequestBody RecordingStopRequest request) {
    Recording recording = recordingService.stopRecording(request.getRecordingId());
    return ResponseEntity.ok(new RecordingStopResponse(recording.getStatus().name()));
  }

  @PostMapping("/complete")
  public ResponseEntity<RecordingStopResponse> complete(@RequestBody RecordingCompleteRequest request) {
    Recording recording = recordingService.handleRecorderComplete(request);
    return ResponseEntity.ok(new RecordingStopResponse(recording.getStatus().name()));
  }

  @PostMapping("/transcripts")
  public ResponseEntity<Void> upsertTranscript(@RequestBody TranscriptUpsertRequest request) {
    recordingService.upsertTranscript(request);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/summaries")
  public ResponseEntity<Void> upsertSummary(@RequestBody SummaryUpsertRequest request) {
    recordingService.upsertSummary(request);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/room/{roomId}")
  public ResponseEntity<List<RecordingSummaryItem>> list(@PathVariable String roomId) {
    return ResponseEntity.ok(recordingService.listByRoom(roomId));
  }

  @GetMapping("/{recordingId}")
  public ResponseEntity<RecordingDetailResponse> detail(@PathVariable Long recordingId) {
    return recordingService.getDetail(recordingId)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
