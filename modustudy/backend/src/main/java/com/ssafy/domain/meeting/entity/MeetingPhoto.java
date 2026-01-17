package com.ssafy.domain.meeting.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "meeting_photo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetingPhoto extends BaseEntity {
}
