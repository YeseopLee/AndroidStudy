# Hilt
- Android에서 DI(의존성 주입)를 위한 Jetpack의 권장 라이브러리  
- Dagger가 제공하는 컴파일 시간 정확성, 런타임 성능, 확장성, Android Studio의 이점을 누리기 위해 Dagger를 기반으로 만들어진 라이브러리  
- 모든 Android클래스에 컨테이너 제공, 수명 주기 자동 관리  
<img src="https://user-images.githubusercontent.com/67935576/149666545-e0f204ef-9779-4f70-8de5-a11f98a50a68.png" width="400px">  
Hilt의 Dependency graph  
<br/>
<br/>

## Setting  
***
> build.gradle(Project)  
```gradle
buildscript {
    ...
    dependencies {
        ...
        classpath 'com.google.dagger:hilt-android-gradle-plugin:2.28-alpha'
    }
}
```
> build.gradle(Module)  
```gradle
plugins  {
    ...
    id 'kotlin-kapt'
    id 'dagger.hilt.android.plugin'
}

dependencies {
    ...
    implementation "com.google.dagger:hilt-android:2.38.1"
    kapt "com.google.dagger:hilt-android-compiler:2.38.1"
}
```
<br/>

## Application  
***
Hilt를 사용하는 모든 어플리케이션은 ***@HiltAndroidApp*** annotation을 사용해야한다.  
```kotlin
@HiltAndroidApp
class MyApp : Application() {
}
```  
<br/>

## Android Class
***
DI를 위해 Android Component에 ***@AndroidEntryPoint***를 달아야한다. 단, 다음 타입들만 지원한다.  
- Application (by using ***@HiltAndroidApp***)
- ViewModel (by using ***@HiltViewModel***)
- Activity
- Fragment
- View
- Service
- BroadcastReceiver  
    > Android 클래스에 annotation을 지정하면 이 클래스에 종속된 클래스에도 annotation을 지정해야한다.  
    가령, Fragment에 @AndroidEntryPoint를 지정하면 이 Fragment를 사용하는 Activity에도 @AndroidEntryPoint를 지정해야한다.  

<br/>

## Hilt 결합 정의
***
Hilt는 Dependency graph를 만들어서 필요한 곳에 의존성을 주입해주는 라이브러리이다.  

따라서 ***@Inject***를 사용해 Dependency graph를 이어준다.  
```kotlin
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    @Inject lateinit var analytics: AnalyticsAdapter
}
```  
의존성을 주입 받으려는 변수에 ***@Inject***
```kotlin
class AnalyticsAdapter @Inject constructor(
  private val service: AnalyticsService
)
```  
의존성을 주입 할 생성자 클래스에 ***@Inject***  
<br/>

## Hilt Module
***
때로는 생성자를 삽입할 수 없는 상황이 존재한다. Interface를 사용하거나, 외부 라이브러리(ex. Retrofit, Room)를 사용할 때는 Hilt Module을 사용하여 Hilt에 결합 정보를 제공한다.  
Hilt Module은 ***@Module***로 지정된 클래스이다.  
***@InstallIn***을 지정하여 각 모듈을 사용하거나 설치할 Android Class를 Hilt에 알려야한다.
```kotlin
@Module
@InstallIn(ActivityComponent::class)
object AnalyticsModule {
    ...
}
```

