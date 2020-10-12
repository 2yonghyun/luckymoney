package com.kakao.pay.luckymoney.constant;

public enum ErrorCode {

    //==500==//
    E0000, // "예상치 못한 오류가 발생했습니다."
    E0001, // "토큰 발급에 실패하였습니다."

    //==400==//
    E0101, // "조회할 수 있는 결과가 없습니다."
    E0102, // "존재하지 않는 대상입니다."
    E0103, // "자신이 뿌리기한 건은 자신이 받을 수 없습니다."
    E0104, // "뿌린 건은 10분간만 유효합니다."
    E0105, // "뿌린 건이 전부 소요된 경우 받을 수 없습니다."
    E0106, // "뿌리기 당 한 사용자는 한번만 받을 수 있습니다."
    E0107, // "뿌리기 금액이 나누려는 사람수보다 작을 수 없습니다."

    //==403==//
    E0301, // "뿌린 사람 자신만 조회를 할 수 있습니다."
    E0302 // "뿌리기가 호출된 대화방과 동일한 대화방에 속한 사용자만이 받을 수 있습니다."
}