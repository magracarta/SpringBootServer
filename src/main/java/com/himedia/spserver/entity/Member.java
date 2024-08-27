package com.himedia.spserver.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "memberRoleList")
public class Member {
    @Id
    private String nickname;
    private String email;
    private String pwd;
    private String phone;
    private String provider;
    private String snsid;
    private String profileimg;
    private String profilemsg;

    //사용자의 등급별 권한들이 저장
    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default // Default:new ArrayList<>() 비어있는 리스트로 객체 저장
    private List<MemberRole> memberRoleList = new ArrayList<>();

}
