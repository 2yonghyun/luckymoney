# 카카오페이 뿌리기 기능 구현하기
## 목차
[개발 환경](#개발-환경)  
[핵심 문제 해결 전략](#핵심-문제-해결-전략)  
[빌드 & 기동 방법](#빌드-&-기동-방법)  
[API 실행 방법](#API-실행-방법)  


## 개발 환경
- Java 11
- Spring boot 2.1.17.RELEASE
- JPA
- H2
- gradle


## 핵심 문제 해결 전략
### 1. 엔티티 정의
뿌리기(Sprinkle) : 줍기(Pickup) = 1 : N 관계로 설정

### 2. token 발행
apache의 RandomStringUtils 이용

### 3. 뿌릴 금액을 인원수에 맞게 분배
1 ≤ 각 줍기 금액 ≤ (뿌릴 금액 - 주울 수 있는 인원 + 1)


## 빌드 & 기동 방법
#### 1. Git clone을 통해 프로젝트 repository를 복사
```zsh
% git clone https://github.com/2yonghyun/luckymoney.git
```

### 2. 빌드
```zsh
% cd luckymoney
% ./gradlew clean build 
```

### 3. jar 기동
```zsh
% java -jar build/libs/luckymoney-0.0.1-SNAPSHOT.jar
```

### 4. 아래의 기동 로그를 확인(예)
```zsh
2020-10-12 23:45:19.164  INFO 28330 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2020-10-12 23:45:19.169  INFO 28330 --- [           main] c.k.p.luckymoney.LuckymoneyApplication   : Started LuckymoneyApplication in 6.86 seconds (JVM running for 7.776)
```


## API 실행 방법
### 1. 뿌리기 API 

> Method : POST  
> URL : http://localhost:8080/luckymoney/v1/sprinkles

[입력 예]  
```zsh
% curl -H 'content-type: application/json' -H 'X-ROOM-ID: abcd' -H 'X-USER-ID: 1234' \
-X POST -d '{ "totalAmount": 10000, "divideNumber": 3 }' 'http://localhost:8080/luckymoney/v1/sprinkles'
```

[출력 예]  
```json
{
    "token": "Jz4"
}
```

### 2. 줍기 API
> Method : PUT  
> URL : http://localhost:8080/luckymoney/v1/sprinkles/pickups/{token}

[입력 예]  
```zsh
% curl -H 'content-type: application/json' -H 'X-ROOM-ID: abcd' -H 'X-USER-ID: 3456' \
-X PUT 'http://localhost:8080/luckymoney/v1/sprinkles/pickups/Jz4'
```
[출력 예]  
```json
{
    "amount": 3411
}
```

### 3. 조회 API
> Method : GET  
> URL : http://localhost:8080/luckymoney/v1/sprinkles/{token}

[입력 예]  
```zsh
% curl -H 'content-type: application/json' -H 'X-ROOM-ID: abcd' -H 'X-USER-ID: 1234' \
-X GET 'http://localhost:8080/luckymoney/v1/sprinkles/Jz4'
```
[출력 예]  
```
{
    "sprinkledTime": "2020-10-12T22:18:17.600438",
    "amount": 10000,
    "pickedAmount": 10000,
    "pickedPickupList": [
        {
            "userId": 2345,
            "amount": 1634
        },
        {
            "userId": 3456,
            "amount": 3411
        },
        {
            "userId": 4567,
            "amount": 4955
        }
    ]
}
```