## 더무비(The Movie) 
```
안드로이드 영화 앱 '더무비' 프로젝트 관련 레포지토리입니다.

더무비는 사용자에게 각종 지표에 따른 영화 정보를 제공하고 사용자가 영화에 대한 
간단한 리뷰를 작성할 수 있도록 도와주는 안드로이드 애플리케이션 개발 프로젝트입니다.
```

## 개발 기간 및 기여도
```
- 2022-09-20 ~ 2022-09-28+ (개인 프로젝트)
```

## 주요 기능
```
🔐 인증
   1. 로그인
   2. 로그아웃
   3. 회원가입

📃 영화 백과사전
   1. 일간 인기 영화 순위
   2. 평점 기반 영화 순위
   3. 영화 검색 
   4. 영화 상세정보
 
🖊 영화 감상문
   1. 영화 감상문 작성
   2. 영화 감상문 삭제
   3. 영화 감상문 수정
```

## 사용 기술
```
   - Room
   - Firebase Authentication
   - Firebase Realtime Database
   - Retrofit
   - Glide
   - Recycler View
   - View Pager
   - Tab Layout
   - Floating Action Button
   - MVVM Design Pattern
   - Singleton Design Pattern
   - Youtube Android Player API
   - TMDB(The Movie Database) API
```

## 사용 방법 및 시연 영상

- 프로젝트의 utils/Credentials.kt 파일에서 {} 부분을 자신의 API 키로 바꿔서 사용하시면 됩니다.
```
{TMDB API 키} -> "자신의 TMDB API 키"
{Youtube Data API 키} -> "자신의 Youtube Data API 키"
```
- 시연 영상은 아래 유튜브 링크에서 확인하실 수 있습니다. 
- [시연 영상 유튜브 링크](https://youtu.be/OQYaZoqhnok)

## 한계 및 개선 방법
- 프로젝트의 MovieDetailActivity에서 영화 동영상 재생을 위한 YouTubePlayerView를 사용하기 위해서는 MovieDetailActivity가 YouTubeBaseActivity를 상속받아야 합니다. 하지만, YouTubeBaseActivity를 상속받은 상태에서는 해당 Activity 내에서 MVVM 패턴을 위한 ViewModel 객체를 사용할 수 없습니다.
- 이에 대한 대안으로는 MovieDetailActivity가 YouTubeBaseActivity를 상속받는 대신 YouTubePlayerFragment를 사용하여 YouTubePlayerView의 기능을 수행하는 방법이 있습니다. 그러나 이 방법을 사용하려면 MovieDetailActivity에서 youtubePlayerFragment 객체를 찾기 위해 deprecated된 fragmentManager를 사용해야 합니다.
- 현재 프로젝트는 YouTubePlayerFragment를 사용하여 영화 동영상을 재생하는 방식으로 진행되었습니다. 하지만, 추후 ExoPlayer 등을 이용하여 Youtube Android Player의 기능을 대신한다면 좀 더 최신 상태로 프로젝트의 코드를 보수할 수 있을 것으로 기대됩니다. 
