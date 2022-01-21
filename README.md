## Lyve
Lyve is event discovery application, using modern Android development techniques. The aim of the application is to connect people through a shared passion for different social activities where individuals can create their own profiles, add people as friends, create social activities, see/search activities around them and send a request to join.

## Tech stack 🏗
* [Kotlin](https://kotlinlang.org/)
* [Firebase](https://firebase.google.com/)
* [Google Cloud Platform](https://cloud.google.com/)
* [MVVM Architecture](https://developer.android.com/jetpack/guide) & [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
* [Dagger Hilt](https://dagger.dev/hilt/)
* [Coroutine](https://developer.android.com/kotlin/coroutines)
* [Jetpack Components](https://developer.android.com/jetpack)
* [Material Design](https://material.io/design)

## Screenshots 📷
<img src="/arts/onboarding-register.png" width="260"> &emsp;<img src="/arts/user-feed.png" width="260"> &emsp;<img src="/arts/create-activity.png" width="260">

## Data modelling structure ☁️
Firestore-root
   |
   --- Users (collection)
   |     |
   |     --- uid (documents) --- Events (sub_collection)
   |          |                                   |
   |          --- uid: "User UID"                 --- uid: "Event UID"
   |          |                                   |
   |          --- name: "User name"               --- eTitle: "Event title"
   |          |                                   |
   |          --- email: "email@email.com"        --- eType: "Event type"
   |          |                                   |
   |          --- avatar: "Some URL"              --- eLocation: "Event location"
   |          |
   |          --- createdAt: "Timestamp"
   |          |
   |          --- followers: [IDs of followers]
   |          |
   |          --- followings: [IDs of followings]
   |
   |
   --- Events (collection)
          |
          --- uid (documents)
               |
               --- euid: "Event UID"
               |
               --- eTitle: "Event title"
               |
               --- eDesc: "Event description"
               |
               --- eType: "Event type"
               |
               --- eCreatedAt: "Timestamp"
               |
               --- eTime: "Event time and date"
               |
               --- eLocation: "Event location"
               |
               --- eCreatedById: "Event owner UID"
               |
               --- eParticipants: [IDs of event participants]
               |
               --- eImgRefs: "Event image URL"

## Contribution 🙌
All contributions are welcome! Feel free to jump to the issues and pick one for yourself! Please write a comment inside of the issue before you start working.
