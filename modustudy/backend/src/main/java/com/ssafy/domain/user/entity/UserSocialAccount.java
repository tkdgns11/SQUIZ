package com.ssafy.domain.user.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_social_account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSocialAccount extends BaseEntity {

}
