## Dpendency Injection (의존성 주입)
---

#### 의존성이란?

객체지향 프로그래밍에서 클래스간의 영향을 받을 때, 의존성이 존재한다라고 말할 수 있다.

구글 문서에서는 의존성 주입을 다음과 같이 설명하고 있다.
(https://developer.android.com/training/dependency-injection)

```kotlin
class Car {
    private val engine = Engine()
    fun start() {
        engine.start()
    }
}

fun main(args: Array) {
    val car = Car()
    car.start()
}
```
![1](https://user-images.githubusercontent.com/67935576/149666380-f0d0ecfe-5db3-4b74-ad9a-11d9388becf6.png)

위 코드에서 Car 클래스는 Engine클래스에 강하게 의존하고 있다.
Car 클래스를 생성하려고 할 때 마다 Engine 클래스를 생성해서 사용하고 있기 때문에, 가스차, 전기차등 새로운 Car를 위해 새로운 Engine 클래스를 만들어서 사용해야 한다.
또한 그렇게 만든 새로운 Engine에서 내부 정책등의 이유로 start()라는 메서드의 이름을 바꾸거나 삭제해야 한다면, Engine을 가지고 있는 모든 클래스에서 해당 부분을 수정해주어야 한다.

```kotlin
class Car(private val engine: Engine) {
    fun start() {
        engine.start()
    }
}

fun main(args: Array) {
    val engine = Engine()
    val car = Car(engine)
    car.start()
}
```

![2](https://user-images.githubusercontent.com/67935576/149666382-53d30285-fd76-44d7-9cc1-e30de7ac68dc.png)

위 코드는 앞선 코드에서 의존성을 제거한 모습이다.
Car 클래스를 생성할 때 내부에서 새로운 Engine을 초기화하는게 아니라, 외부에서 생성한 Engine클래스를 매개변수로 넘겨주기만 하면 된다.

또한 이 과정에서 Engine을 Interface를 사용하여 생성하면, start() 메서드가 변경되더라도 문제없이 재활용하여 사용할 수 있다.

이처럼 내부에서 객체를 새로 생성해서 사용하는것이 아니라, 외부에서 만들어서 직접 넣어주는것을 의존성 주입, DI 라고 한다.

외부에서 의존성을 주입하게 되면 실제로 있는 Engine이 아니라 Fake Engine등을 임의로 생성하여 로직을 테스트 하는  Unit Test등을 수월하게 할 수 있다.

```kotlin
class Car {
    lateinit var engine: Engine

    fun start() {
        engine.start()
    }
}

fun main(args: Array) {
    val car = Car()
    car.engine = Engine()
    car.start()
}
```

안드로이드에서 Activity, Fragment등은 시스템에서 인스턴스화 하여 사용하므로 생성자 삽입이 불가능하기 때문에 위처럼 필드 삽입등의 방식으로 의존성을 주입할 수 있다.

그러나 실제 개발 환경에서는 수많은 객체들이 서로 의존성을 가지고 있는 경우가 많기 때문에, 이를 전부 수동으로 의존성을 분리하기 어려워, Dagger, Hilt, Koin등의 의존성 주입 라이브러리를 활용할 수 있다.

#### 라이브러리를 사용하지 않는 의존성 주입

MVVM 패턴을 적용한다고 할 때, 다음과 같은 형태의 코드로 의존성을 주입할 수 있을것이다.

LoginActivity
```kotlin
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel : LoginViewModel
    private val userRepository = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this
        viewModel = LoginViewModel(userRepository)
        binding.viewModel = viewModel

        observeData()
    }
```
LoginViewModel
```kotlin
class LoginViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
...
}
```

위 코드에는 여러가지 문제점이 존재하는데,
객체의 생성 순서가 중요하여 가독성을 해치게 되고,
UserRepository는 인스턴스를 재활용 하여 사용하는것이 메모리 측면에서 좋다.
또한 Activity위의 Fragment가 LoginViewModel을 재활용하여 사용할 경우 보일러플레이트코드가 발생하기 쉽다.

때문에 DI는 Container라는 클래스를 사용하여 관리한다.
이 컨테이너는 주입을 위해 외부에서 인스턴스를 생성하는 공간 이라고 볼 수 있다.

```kotlin
class AppContainer {
    val userRepository = UserRepository() 
}
```
이 AppContainer는 전역으로 할당하여 사용한다.

그러나 여전히 이 Container 객체를 직접 관리하여 인스턴스를 수동으로 생성해주어야 한다. 위 예제는 간단하여 문제가 없어 보일 수 있으나, 앱의 규모가 커질수록, 의존성 주입의 의미가 퇴색하기 쉽다.
또한 안드로이드의 경우 각종 뷰의 생명주기가 존재하므로 메모리 효율을 위하여 인스턴스 해제를 직접 해주어야 하는 불편함이 남아있다.

#### DI 라이브러리

안드로이드에서는 대표적으로 Dagger, Hilt, Koin등의 라이브러리를 활용한다.

Hilt는 기존에 자바에서 사용하는 Dagger를 안드로이드의 환경에 맞게 바꾼것이며, 구글에서 공식적으로 지원하는 라이브러리이다.

Koin은 Kotlin을 위해 개발된 매우 경량화된 DI 라이브러리로 러닝커브가 매우 낮아 사용하기 쉽다는 장점이 있다.

Hilt와 Koin의 대표적인 차이라고 하면, Hilt는 컴파일시에 의존성을 주입하고, Koin은 런타임과정에 의존성을 주입한다.

#### Dagger2 구성 요소

안드로이드에 맞춰진 Hilt를 보기전에, 기존버전의 Dagger2의 개념들을 먼저 살펴보자.

- Container(Component)
  앞서 봤던 컨테이너의 역할을 하며(외부에서 클래스의 인스턴스 생성하는 공간), Dagger2에서는 Component라고 부른다.
- Module
  Component에 의존성을 제공하는 역할을 하며, 인스턴스들을 모아놓는 공간. Module을 통해 인스턴스들을 모듈 단위로 관리한다.
- Provider
  주입부의 인스턴스를 제공(Provide)하는 역할을 한다.

![3](https://user-images.githubusercontent.com/67935576/149666384-5662ad76-c50f-42da-8a4c-04f3348ee2e0.png)
(출처: https://kotlinworld.com/102?category=924584)

#### Hilt

Dagger가 제공하는 컴파일 시간 정확성, 런타임 성능, 확장성 및 Android 스튜디오 지원의 이점을 누리기 위해 Dagger를 기반으로 만들었으며,
Hilt는 프로젝트의 모든 Android 클래스에 컨테이너를 제공하고 수명 주기를 자동으로 관리함으로써 애플리케이션에서 DI를 사용하는 표준 방법을 제공한다.

Hilt에서는 내부적으로 Component들을 자동으로 생성하고, 생명주기에 따라 관리해준다.

![4](https://user-images.githubusercontent.com/67935576/149666545-e0f204ef-9779-4f70-8de5-a11f98a50a68.png)
(Hilt의 Component들의 계층 구조)


#### Hilt의 사용
```kotlin
dependencies {
    "com.google.dagger:hilt-android-gradle-plugin:2.38.1"
}
```
```kotlin
plugins {
    id 'kotlin-kapt'
    id "dagger.hilt.android.plugin"
}
dependencies {
    // Hilt
    implementation "com.google.dagger:hilt-android:2.38.1"
    kapt "com.google.dagger:hilt-android-compiler:2.38.1"
}
```
Project단위의 gradle과 module단위의 gradle에 위와 같이 의존성을 추가한다.

```kotlin
@HiltAndroidApp
class MyApp : Application() {
    ...
}
```
Hilt를 사용하는 모든 앱은 @HiltAndroidApp 어노테이션을 사용하여야 한다.

```kotlin
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {}
```
@AndroidEntryPoint 어노테이션을 선언하는것으로 자동으로 생명주기에 따라 적절한 시점에 인스턴스화 하여 사용하며 지원하는 클래스는 다음과 같다.
- Application (by using @HiltAndroidApp)
- ViewModel (by using @HiltViewModel)
- Activity
- Fragment
- View
- Service
- BroadcastReceiver


```Kotlin
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    @Inject lateinit var analytics: AnalyticsAdapter
```
@Inject 어노테이션을 사용하여 의존성을 주입 받으려는 변수에 객체를 주입할 수 있다.

```kotlin
class AnalyticsAdapter @Inject constructor(
  private val service: AnalyticsService
)
```
생성자에 @Inject 어노테이션으로 의존성 인스턴스를 생성하여 의존성을 주입받을 수 있다.
```kotlin
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModels()
```
```kotlin
@HiltViewModel
class LoginViewModel @Inject constructor(private val userRepository: DefaultUserRepository) : ViewModel() {
```
Jetpack의 ViewModel을 사용할 경우 위와 같이 KTX extension과 함께 @HiltViewModel 어노테이션으로 의존성을 주입받을 수 있다.

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object LocalDBModule{
...
}
```
때로는 생성자를 삽입할 수 없는 상황이 존재한다. Interface를 사용하거나, 외부 라이브러리 (Retrofit, Room 등)를 사용할 때에는 @Module 어노테이션을 사용하는 Hilt Module을 통해 의존성을 생성할 수 있다.

@InstallIn 어노테이션을 통해 Hilt의 표준 컴포넌트중 모듈을 설치할 곳을 지정할 수 있다.

```Kotlin
@Module
@InstallIn(SingletonComponent::class)
object LocalDBModule {

  @Provides
  @Singleton
  fun provideLocalDB(): AppDataBase = Room
    .databaseBuilder(MyApp.appContext!!, AppDataBase::class.java, AppDataBase.DB_NAME)
    .allowMainThreadQueries()
    .build()
}
```
@Provides 어노테이션으로 컴포넌트에 제공할 메서드를 정의할 수 있으며 @Singleton 어노테이션으로 인스턴스가 계속 생성되는것을 막는다.
