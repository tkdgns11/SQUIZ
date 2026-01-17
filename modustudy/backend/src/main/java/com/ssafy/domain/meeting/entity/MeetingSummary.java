package com.ssafy.domain.meeting.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "meeting_summary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetingSummary extends BaseEntity {
}
