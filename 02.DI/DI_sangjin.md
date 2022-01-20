# Hilt
Hilt는 안드로이드 애플리케이션에 의존성 주입하는 Dagger의 표준적인 방법을 제공한다. Hilt의 목적은 다음과 같다.
- 안드로이드 애플리케이션을 위한 Dagger와 관련 기반 코드들을 간소화
- 쉬운 설정과 가독성/이해도, App간 코드 공유를 위한 표준 component, scope 세트 생성
- 다양한 빌드 유형에 대한 서로 다른 바인딩 제공
</br>

## Setting
- compileSdk : 31     /     minSdk : 21     /    targetSdk : 31
- build.gradle(Module)
```Groovy
plugins {
    ...
    id 'kotlin-kapt'
    id 'dagger.hilt.android.plugin'
}

dependencies{
	...
    //Dagger - Hilt
    def latest_version = "2.38.1"
    implementation "com.google.dagger:hilt-android:$latest_version"
    kapt "com.google.dagger:hilt-compiler:$latest_version"
}
```

- build.gradle(Project)
```Groovy
dependencies {
        ...

        //hilt classpath
        classpath 'com.google.dagger:hilt-android-gradle-plugin:2.38.1'

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
```
</br>

## Hilt Example
### Application

```Kotlin
@HiltAndroidApp
class MyApp : Application() {
}
```

```@HiltAndroidApp```을 통해 Compile 타임 시 표준 Component Building에 필요한 클래스들을 초기화해준다. 따라서 Hilt를 사용하는 모든 App은 다음과 같은 annotation을 반드시 포함해야 한다.

</br>

추가로 ```Manifest``` 에 다음 클래스를 추가해줘야 한다.

```XML
<application
    android:name=".MyApp"
    ...>
</applicaiton>
```

다음으로 Dependency를 받아보고자 하는 Android Component에 ```@AndroidEntryPoint``` 를 작성해줘야 한다. 단, 다음 타입만을 지원한다.

- Application(by using @HiltAndroidApp)
- ViewModel(by using @HiltViewModel)
- Activity
- Fragment
- View
- Service
- BroadcaseReceiver

```Kotlin
@AndroidEntryPoint
class HouseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

				val house = House()
				house.alert()
    }
}
```

### Field Injection
위의 코드는 아직 Dependency가 주입되지 않은 모습이다. 따라서 ```house Field``` 를 ```@Inject``` annotation을 통해 삽입할 수 있다. 
Hilt는 Dependency Graph를 만들어서 필요한 곳에 Dependency를 제공해주는 라이브러리기 때문에 어떤 곳에서 해당 dependency가 필요하다고 annotation이 붙어 있으면, 
해당하는 객체를 어떻게 생성하는지 Hilt가 알고 있어야 한다. 따라서 다음과 같이 ```@Inject``` annotation을 통해 dependency graph를 이어준다.

```Kotlin
@AndroidEntryPoint
class HouseActivity : AppCompatActivity() {

    @Inject
    lateinit var house : House

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        house.alert()
    }
}
```

```Kotlin
class House @Inject constructor() {

    @Inject
    private val alertSystem = AlertSystem()

    fun alert(){
        alertSystem.startAlerting()
    }
}
```

### Constructor Injection
위의 예시를 Constructor Injection으로 변환한 코드는 다음과 같다.

```Kotlin
class House @Inject constructor(private val alertSystem : AlertSystem) {

    fun alert(){
        alertSystem.startAlerting()
    }
}
```

다음과 같이 수정한 뒤 run할 때, 오류가 난다. 이러한 이유는 Hilt에서 주입할 객체가 어떤 것인지 모르기 때문에, 주입하고자 할 ```AlertSystem``` 클래스에도 ```@Inject``` 를 통해 알려줘야 한다.

```Kotlin
class AlertSystem @Inject constructor(){
    fun startAlerting(){
        println("I am alerting...")
    }
}
```

### Module Injection
Module injection을 위해 기존의 AlertSystem 클래스를 interface로 변경한다.

```Kotlin
interface AlertSystem {
    fun startAlerting()
}
```
```AlertSystem``` 의 공통적으로 사용되는 method를 가진 ```NoiseAlertSystem``` 클래스는 다음과 같이 정의할 수 있다. 단, 주의해야 할 점으로는 interface나 
interface를 implement하는 객체는 inject할 수 없다. 따라서 Interface를 implement하는 객체를 constructor inject할 때 오류가 발생한다. 

</br>

또한 ```retrofit``` 과 같은 외부 라이브러리의 객체를 ```Constructor Injection``` 하는 것은 금지되어 있다. 자신이 만든 클래스가 아닌 곳에 Inject할 수 없기 때문이다. 
따라서 이런 경우에 ```Module``` 을 이용해서 Hilt에게 원하는 Dependency를 생성하는 방법을 알려줄 수 있다. 

```Kotlin
class NoiseAlertSystem : AlertSystem {

    override fun startAlerting() {
        println("Noise Noise Noise Noise")
    }
}
```

Inject할 Module을 설정하기 위해 Object인 AlertSystemModule을 만들었다.

```Kotlin
@Module
@InstallIn(ActivityComponent::class)
object AlertSystemModule {

    @Provides
    fun provideAlertSystem() : AlertSystem {
        return NoiseAlertSystem()
    }

}
```

```@InstallIn``` 은 Hilt의 표준 컴포넌트들 중 어떤 컴포넌트에 이 모듈을 설치할지 결정하고, ```@Provides``` 는 컴포넌트에 제공할 메서드를 정의한다. Hilt의 표준 컴포넌트들은 다음과 같다.

![캡처](https://user-images.githubusercontent.com/77181865/150348115-e99c0de6-9f5f-4edc-96b7-ac22c96aae04.PNG)

</br>

- SingletonComponent
    - Application 전체의 생명주기를 갖는다
    
- ActivityRetainedComponent
    - Activity의 생명주기를 갖는다. 단, Activity configuration change 시 파괴되지 않고 유지된다.
    
- ActivityComponent
    - Activity의 생명주기를 갖고, Activity가 onCreate( ) 시점에 함께 생성되고, onDestroy( )되는 시점에 함께 파괴된다.
    
- FragmentComponent
    - Fragment의 생명주기를 갖는다. Fragment가 Activity에 붙는 onAttach( ) 시점에 함께 생성되고 onDestroy( )되는 시점에 함께 파괴된다.
    
- ViewComponent
    - View의 생명주기를 갖는다. View가 생성되는 시점에 함께 생성되고 파괴되는 시점에 함께 파괴된다.
    
- ViewWithFragmentComponent
    - Fragment의 View 생명주기를 갖는다. View가 생성되는 시점에 함께 생성되고 파괴되는 시점에 함께 파괴된다.
    
- ServiceComponent
    - Service의 생명주기를 갖는다. Service의 onCreate( ) 시점에 함께 생성되고 onDestroy( ) 시점에 함께 파괴된다.

</br>

### Providing Multiple Values
같은 타입의 객체에 대한 Dependency를 주입할 때의 예제이다.

위에서 AlertSystem Interface를 implement하는 LockAlertSystem과 NoiseAlertSystem 클래스가 있다.

```kotlin
class LockAlertSystem : AlertSystem {

    override fun startAlerting() {
        println("I am locking all the doors and the windows")
    }
}

class NoiseAlertSystem() : AlertSystem {

    override fun startAlerting() {
        println("Noise Noise Noise Noise")
    }
}
```

이 때, 위에서 생성한 두 객체의 공통으로 선언이 된 startAlerting( ) 메소드를 호출하려고 할 때 Hilt는 어느 객체의 메소드를 호출할지 알 수 없다. 그러므로 이때 식별자를 annotation을 활용해서 구별할 수 있다. 

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AlertSystemModule {

    @Provides
    @Named("noiseAlert")
    fun provideAlertSystem() : AlertSystem {
        return NoiseAlertSystem()
    }

    @Provides
    @Named("lockAlert")
    fun provideLockAlertSystem() : AlertSystem{
        return LockAlertSystem()
    }

}
```

@Named는 Dagger의 식별자 방식인데, Hilt에서도 적용이 되어 다음과 같이 사용할 수 있다. 

위에서 식별자를 이용하여 이용한 예는 다음과 같다.

```kotlin
class House @Inject constructor(@Named("noiseAlert") private val alertSystem : AlertSystem) {

    fun alert(){
        alertSystem.startAlerting()
    }
}

// or

class House @Inject constructor(@Named("lockAlert") private val alertSystem : AlertSystem) {

    fun alert(){
        alertSystem.startAlerting()
    }
}
```

Hilt에서는 @Qualifier를 이용하여 다음과 같이 사용할 수 있다.

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AlertSystemModule{
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class LockAlertSystem
    
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class NoiseAlertSystem
    
    @NoiseAlertSystem
    @Provides
    fun provideAlertSystem() : AlertSystem {
        return NoiseAlertSystem()
    }
    
    @LockAlertSystem
    @Provides
    fun provideLockAlertSystem() : AlertSystem{
        return LockAlertSystem()
    }
}
```


