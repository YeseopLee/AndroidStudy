# Coroutine

## Motivation
- Avoid callbacks
- **suspend** Functions
- Structural Concurrency

다음 statement를 Main Thread에서 실행한다고 가정해보자. 이때 loadImage( ) 메소드에서 실행하는데 시간이 오래 걸리고 밑에 있는 statement들은 수행되지 않고 대기하기 때문에, Exception이 발생한다.

```kotlin
val image = loadImage(url)     // time consuming operation
setImage(image)
```

위의 코드에 대한 솔루션은 다음과 같다.

- Solution 1 : callbacks

: Callback method를 호출함으로써 위의 image에 대해 parameter를 삽입하여 이미지를 받아온다. 

```kotlin
loadImageAsync().whenComplete{ image ->
		runOnUIThread {
				setImage(image)
		}
}
```

- Solution 2 : async/wait

: async를 통하여 비동기적으로 실행한다.

```kotlin
async(Main) {
		val image = loadImageAsync(url).await()
		setImage(image)
}
```

- Solution 3 : No callback

: 다음과 같이 작성한다면, 비동기적으로 실행하여 load하는 작업이 끝날 때까지 대기한다. 따라서 Callback을 사용하지 않고도, Main Thread를 Blocking하지 않고 비동기적으로 실행될 수 있다. **따라서 Callback 메소드를 사용하지 않으면 앞서 다뤘던 해결방법들 보다 간결하게 코드를 작성할 수 있다.**

```kotlin
val image = loadImageAsync(url).await()
setImage(image)
```

## Coroutine의 장점

다음과 같이, login 절차에 관련된 method가 있다고 가정해보자.

```kotlin
fun login(email: String) : UserId
fun load(id: UserId): User
fun show(user: User)
```

- 순차적 수행

이를 순차적으로 수행했을 때, 앞의 예처럼 코드 수행이 blocked 될 수 있다.

```kotlin
fun showUser(email: String){
		val id = login(email) // time consuming!!
		val user = load(id)  // time consuming!!
		show(user)
}
```

- CompletableFuture

```kotlin
fun login(email: String) : CompletableFuture<UserId>
fun load(id: UserId) : CompletableFuture<User>
fun show(user: User)

fun showUser(email: String){
		login(email)
				.thenCompose{ load(it) }
				.thenAccept{ show(it) }
}
```

- RxJava

```kotlin
fun login(email: String) : Single<UserId>
fun load(id: UserId) : Single<User>
fun show(user: User)

fun showUser(email: String){
		login(email)
				.flatMap { load(it) }
				.doOnSuccess { show(it) }
				.subscribe()
}
```

- Using async/await in Kotlin

위의 두 라이브러리보다 사용이 복잡하지 않고, 기존 method에 await()함수를 append함으로써 비교적 쉽게 사용이 가능하다.

```kotlin
fun login(email: String) : Deffered<UserId> // Deffered: runs the code asynchronously + Return job
fun load(id: UserId) : Deffered<User>
fun show(user: User)

fun showUser(email: String) = async{
		val id = login(email).await()
		val user = load(id).await()
		show(user)
}
```

Coroutine은 suspend 함수도 지원하기 때문에 다음과 같이 작성할 수도 있다. 이렇게 작성하면 초기 순차적인 수행하는 코드와 비슷하게 구성된 것을 알 수 있다.

단, 이 때 주의해야 할 점은 suspend function은 coroutine 혹은 또 다른 suspend function에 의해만 호출되어야 한다.

```kotlin
suspend fun login(email: String) : UserId
suspend fun load(id: UserId) : User
fun show(user: User)

suspend fun showUser(email: String){
		val id = login(email)
		val user = load(id)
		show(user)
}
```

![1A02DDAB-4565-48ED-9B77-9774A8BA3796.jpeg](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/eb802549-0520-4881-a91d-193429d78c2f/1A02DDAB-4565-48ED-9B77-9774A8BA3796.jpeg)

Coroutine은 다양한 task를 진행할 때 필요한 요소이다. Thread와 다른 점으로는 다음과 같다.

- Coroutine은 코드를 실행 중일 때 멈출 수 있고(suspendable) 다시 실행할 수 있는(resume) 제어 능력이 있다.
- Thread와 함께 사용되며, 작업을 쉽게 전환하며 Thread를 옮겨다니면서 작업이 가능하다.
- 효율적이고 처리 속도가 빠르다.

## Call stack of a coroutine

![48489328-203D-436B-88CB-9CB74C2C5085.jpeg](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/c198de8b-a7a8-4cc0-a69d-4b229d0ac70b/48489328-203D-436B-88CB-9CB74C2C5085.jpeg)

## Suspended Coroutine

- suspend coroutine은 heap 영역에 저장된다.
- 해당 호출 스택과 모든 변수들의 값들은 저장된다.
- 오직 하나의 객체만 coroutine을 저장되는데 사용된다.

## Resumed coroutine

- 호출 스택은 복구가 되고, 재개할 수 있다.

## 그럼, 어떻게 coroutine을 만드는건데?

- Coroutine builders
    - To start a new computation asynchrounously
      ```Kotlin
      async{...}
      ```
      
    - To start a new computation in a blocking way
      ```Kotlin
      runBlocking{...}
      ```
      
      
## Setting
- compileSdk : 31
    
    minSdk 21
    
    targetSdk 31
    

- **build.gradle(Module)**
```Kotlin
dependencies{
    //coroutine
    def coroutines_version = "1.3.9"
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version"
```

## CoroutineScope

- 해당 coroutine 범위 안에 있는 모든 자식 coroutine들의 실행이 모두 성공할때까지 기다린다
- 자식 coroutine에서 예외가 catch되거나 명시적으로 실행을 중지한다면 모든 자식 coroutine들을 중지한다.

다음과 같이 Image 객체를 overlay하는 function이 있다고 하자.

```kotlin
fun overlay(image1: Image, image2: Image) : Image

suspend fun loadAndOverlay(){
		val first = async{ loadImage("green") }
		val second = async{ loadImage("red") }
		return overlay(first.await(), second.await())
}
```

만약 coroutine이 Exception이 발생하여 실행이 실패가 되면, green을 로드하는 coroutine을 실행을 실패하고, 이에 따라 두번째 red를 load하는 coroutine은 누수(leaking)이 발생한다.

- Solution

위에 해당 문제를 coroutineScope를 사용하면 누수가 발생하는 것을 막을 수 있다. 만약, coroutine에서 Exception이 발생하면, 첫번째 green을 load하는 coroutine 실행이 실패하고, 자동으로 그 이후의 coroutine은 자동으로 멈추게 된다.

```kotlin
suspend fun loadAndOverlay() : Image = 
		coroutineScope {		
				val first = async{ loadImage("green") }
				val second = async{ loadImage("red") }
				overlay(first.await(), second.await())
		}

```

**따라서 coroutine의 생명주기는 부모 범위의 생명주기에 의해 제한된다.**

## GlobalScope & Dispatcher

- GlobalScope
    - GlobalScope.launch( )를 하게 되면 return type은 Job 이다.
    - Job은 background에서 작업을 의미한다.

- Dispatcher 활용하기
    - Dispatchers.Main : MainThread에서 사용되고 **UI 관련** 작업할 때 사용된다.
    - Dispatchers.IO : **Network** 작업에서 사용된다.
    - Dispatchers.Default : **계산 연산**이 많이 필요할 때 사용된다.
    - Dispatchers.Unconfined

```kotlin
class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GlobalScope.launch(Dispatchers.IO){
            Log.d(TAG, "Starting coroutine in thread ${Thread.currentThread().name}")
            val answer = doNetworkCall()
            withContext(Dispatchers.Main){
                Log.d(TAG, "Starting coroutine in thread ${Thread.currentThread().name}")
                tvDummy.text = answer
            }
        }
    }

    suspend fun doNetworkCall(): String {
        delay(3000L)
        return "This is the answer"
    }
}
```

## runBlocking

- 기존의 main Thread에서는 delay( )를 사용하여 멈출 수 없다. 그러나, runBlocking을 사용하면 멈출 수 있다.

```kotlin
class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "Before runBlocking")
        runBlocking {
            launch(Dispatchers.IO) {
                delay(3000L)
                Log.d(TAG, "Finished IO Coroutine 1")
            }
            launch(Dispatchers.IO){
                delay(3000L)
                Log.d(TAG, "Finished IO Coroutine 2")
            }
            Log.d(TAG, "Start of runBlocking")
            delay(5000L)
            Log.d(TAG, "End of runBlocking")
        }
        Log.d(TAG, "After runBlocking")
    }
}
```

Log를 출력시 다음과 같다.

![Log.PNG](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/da0f4e47-86fc-43dc-82aa-c6a3d9b49653/Log.png)

즉, 두 개의 launch( )를 통해 Multi Thread가 동시에 동작하게 되고, runBlocking 내 delay가 실행되며 Block이 끝나고 나서 onCreate( )의 마지막 로그가 출력되는 모습이다.

## Async & Await

Async는 새로운 coroutine을 시작하고 GlobalScope.launch와 비슷하지만, GlobalScope.launch처럼 job을 return하지 않고, Deffered를 return한다. 이때 말하는 Deffered는 직역하면 ‘연기’를 뜻하는데, “결과값 수신을 연기한다”라고 해석할 수 있다. 즉 **결과값을 수신하는 비동기 작업**를 의미한다.

```kotlin
class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GlobalScope.launch(Dispatchers.IO) {
            val time = measureNanoTime {
                val answer1 = async { networkCall1() }
                val answer2 = async { networkCall2() }
                Log.d(TAG, "Answer1 is ${answer1.await()}");
                Log.d(TAG, "Answer2 is ${answer2.await()}");
            }
            Log.d(TAG, "Requests took $time ms.")
        }
    }

    suspend fun networkCall1(): String{
        delay(3000L)
        return "Answer 1"
    }

    suspend fun networkCall2(): String{
        delay(3000L)
        return "Answer 2"
    }
}
```

async를 활용함으로써 동시에 여러 개의 작업을 수행할 수 있으며, await( )를 통해 해당 작업이 끝날때 까지 기다린다.

## LifecycleScope & viewModelScope

추가해줘야 하는 dependency는 다음과 같다.

```groovy
		//life cycle
    def arch_version = '2.2.0-alpha01'
    implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:$arch_version"
    implementation "androidx.lifecycle:lifecycle-runtime-ktx:$arch_version"
```

2개의 Activity가 있다고 가정하자. 이 때 한 개의 Activity에서 다른 한 개의 Acitivity로 넘어가는 상황이라고 할 때, 

```kotlin
class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStartActivity.setOnClickListener{
            GlobalScope.launch{
                while(true){
                    delay(1000L)
                    Log.d(TAG, "Still running...")
                }
            }

            GlobalScope.launch {
                delay(5000L)
                Intent(this@MainActivity, SecondActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }

        }

    }
}
```

다음과 같이 GlobalScope로 작업을 하면 Activity가 넘어가도 job이 계속 실행이 되면서 메모리 누수문제가 발생한다. 따라서 GlobalScope → lifecycleScope로 변경 시 Activity가 넘어가면 기존의 Activity가 destroyed되면서 job의 작업도 마찬가지로 종료가 된다. 

```kotlin
class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStartActivity.setOnClickListener{
            // Coroutine that will be canceled when the activity is cleared
            lifecycleScope.launch{
                while(true){
                    delay(1000L)
                    Log.d(TAG, "Still running...")
                }
            }

            GlobalScope.launch {
                delay(5000L)
                Intent(this@MainActivity, SecondActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }

        }

    }
}
```

ViewModelScope의 사용방식은 lifecycleScope, GlobalScope와 동일하다.


## Coroutines with Retrofit

추가해줘야 하는 gradle은 다음과 같다.

```groovy
//Retrofit
def latest_version = "2.6.2"
implementation "com.squareup.retrofit2:retrofit:$latest_version"
implementation "com.squareup.retrofit2:converter-gson:$latest_version"
```

기존의 Retrofit을 이용할 때 코드 작성은 다음과 같다.

```kotlin

const val BASE_URL = "https://jsonplaceholder.typicode.com/"

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MyAPI::class.java)

        api.getComments().enqueue(object: Callback<List<Comment>>{
            override fun onFailure(call: Call<List<Comment>>, t: Throwable){
                Log.e(TAG, "Error: $t")
            }

            override fun onResponse(call: Call<List<Comment>>, response: Response<List<Comment>>){
                if(response.isSuccessful){
                    response.body()?.let{
                        for(comment in it){
                            Log.d(TAG, comment.toString())
                        }
                    }
                }
            }
        })
    }
}
```

기존의 Retrofit에서는 변수 api의 함수인 getComments( )를 통해 read하고, 이에 대한 결과를 enqueue하면서 응답이 성공적으로 올 때 response의 body( )를 호출함으로써 결과값을 가져올 수 있는 구조이다. 마찬가지로 Coroutine을 사용하면 훨씬 간단하게 코드를 구성할 수 있다.

```kotlin
interface MyAPI {
    // 기존
    // fun getComments(): Call<List<Comment>>
    @GET("/comments")
    suspend fun getComments(): Response<List<Comment>>
}
```

```kotlin
const val BASE_URL = "https://jsonplaceholder.typicode.com/"

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MyAPI::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            val response = api.getComments()
            if(response.isSuccessful){
                for(comment in response.body()!!){
                    Log.d(TAG, comment.toString())
                }
            }
        }
    }
}
```

## How to share information between different coroutines? → Using Channel !

다음 그림처럼, 각기 다른 coroutine 끼리 데이터를 주고 받기 위해서는 매개체 역할을 하는 channel을 통해 구현할 수 있다.

![1FB14DC7-2BB1-42BC-B414-CA62992F7A8C.jpeg](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/b93a89f2-93f2-419d-8484-cfac3d0e0071/1FB14DC7-2BB1-42BC-B414-CA62992F7A8C.jpeg)

## Types of Channels:

![73937695-B9F3-4491-847C-199BE84C6242.jpeg](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/db1902a6-f48c-4eeb-92f1-4dde8334ab46/73937695-B9F3-4491-847C-199BE84C6242.jpeg)

- Unbuffered: 버퍼의 제한이 Int.MAX_VALUE인 상태
- Buffered : 시스템이 정해놓은 버퍼값(64개)를 가진 상태
- Rendezvous : Sender가 send 함수를 호출하고, Receiver가 receive를 호출할 때 동시간대로 이루어지면 element가 전송된다.
    - send/receive 함수는 다른 coroutine이 receive/send 함수를 호출할때 까지 suspend할 수 있다.

- Conflated : Receiver가 receive를 하기 전에 Sender에서 element를 여러개 보냈다고 가정해보자. 이 경우 가장 최신의 element를 가져오고, 이전의 element는 전송을 하지 않는다. 즉 가장 최신의 데이터 하나를 유지할 수 있다.

## Send & Receive interface

```kotlin
interface SendChannel<in E> {
		suspend fun send(element: E)
		fun close()
}

interface ReceiveChannel<out E>{
		suspend fun receive(): E
}

interface Channel<E> : SendChannel<E>, ReceiveChannel<E>
```

## Producer-consumer Example : producer

```kotlin
val channel = Channel<Task>()
async{
		channel.send(Task("task1"))
		channel.send(Task("task2"))
		channel.close()
}
```

## Producer-consumer Example : Consumer

```Kotlin
val channel = Channel<Task>()   
...
async{ worker(channel) }    // consumer #1
async{ worker(channel) }    // consumer #2


suspend fun worker(channel: Channel<Task>){
		val task = channel.receive()
		processTask(task)
		//waiting for "send"
}
```

Consumer에서 send 호출이 올 때 까지 기다리고, Producer로부터 send가 호출되어 Rendezvous 시간대가 일치하면 elemenet를 받을 수 있다.

이에 대한 역도 가능하다. Consumer에서 첫번째로 받은 task가 처리하는데 시간이 소요될 때, Producer가 먼저 두번째 task를 보냈고, consumer로부터 receive가 올 때까지 기다린 뒤 도착하면 task를 전송한다.


## REFERENCES
- https://developers.android.com
- https://kotlinlang.org
- https://youtu.be/VPTcj1mU-5c
- Kotlin Coroutine Youtube(Philipp Leckner)


