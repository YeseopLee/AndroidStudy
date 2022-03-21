## Flow(플로우)

### Flow란?
Coroutine으로 만들어진, Kotlin에서 사용하는 비동기 데이터 스트림. 기존의 Java로 이루어진 RxJava보다 제공되는 기능은 다소 떨어지지만, 훨씬 경량화 되어 있어 가볍게 사용할 수 있으며, suspned 함수를 지원하여 Jetpack의 많은 라이브러리와 쉽게 호환될 수 있다.

### 리액티브 프로그래밍 & 데이터 스트림

리액티브 프로그래밍이란 데이터가 통지 될 때 마다 반응하여 데이터를 처리하는 방식을 말한다.

기존에 대응되는 명령형 프로그래밍에서는, 데이터를 요청 받으면 그 때 마다 응답하여 데이터를 리턴하지만, 리액티브 프로그래밍에서는 데이터의 발행자와 수신자를 나누어, 구독 요청이 들어오면 지속해서 데이터를 발행 (Emit) 하며, 이것을 데이터 스트림이라고 한다.

### Cold & Hot Stream

본격적으로 Flow에 대해 알아보기 전에, Cold Stream과 Hot Stream의 차이를 비교한다.

- Cold: 구독을 요청하면 그때부터 아이템을 발행하기 시작하며, 보통 1:1로 값을 전달한다.
- Hot: 아이템 구독이 시작된 이후로 0개 이상의 모든 구독자에게 동시에 같은 아이템을 발행한다.

Cold 스트림은 일반 동영상을, Hot 스트림은 실시간 방송을 보는것과 유사하다.

일반적으로 기본 Flow는 Cold Stream이다.

### Flow 기본

가장 기본적인 Flow의 사용방법은 다음과 같다.

```kotlin
fun flowBasic(): Flow<Int> = flow {
    repeat(10) {
        emit(Random.nextInt(0, 100))
    }
}

fun main() {
    runBlocking {
        flowBasic().collect { data ->
            println(data)
        }
    }
}
```
출력 결과
```
45
52
84
56
63
96
36
50
5
29
```

Flow 빌더를 이용하여 데이터 스트림을 생성하고, emit 함수를 이용하여 데이터를 흘려보낸다.
기본 Flow는 Cold Stream이기 때문에 구독(collect)을 시작해야 데이터를 발행하기 시작한다.

### Flow builder

기본 flow 외에도 flowOf를 이용하여 고정된 값 리스트를 이용하거나, asFlow를 이용하여 컬렉션,시퀀스를 전달하여 flow를 만들 수 있다.

```kotlin
fun main() = runBlocking {
    flowOf(1,2,3,4,5).collect { println(it) }
}
```
```kotlin
fun main() = runBlocking {
    listOf(1, 2, 3, 4, 5).asFlow().collect { println(it) }
}
```

이 외에도 channelFlow, StateFlow, SharedFlow등의 다양한 Flow 빌더들이 존재한다.

### Flow Context

Flow는 코루틴을 호출한 context에서 동작한다.

```kotlin
fun simpleFlow(): Flow<String> = flow {
    println("${Thread.currentThread().name}에서 데이터 Emit")
    emit("데이터")
}

fun main() = runBlocking<Unit> {
    launch(Dispatchers.IO) {
        simpleFlow().collect { println("${Thread.currentThread().name} : $it 받음") }
    }
}
```
출력 결과
```
DefaultDispatcher-worker-1에서 데이터 Emit
DefaultDispatcher-worker-1 : 데이터 받음
```

그러나 Flow 내부에서 다른 context를 호출하여 사용할 수는 없다.

```kotlin
fun simpleFlow(): Flow<String> = flow {
    withContext(Dispatchers.IO) {
        println("${Thread.currentThread().name}에서 데이터 Emit")
        emit("데이터")
    }
}

fun main() = runBlocking<Unit> {
    simpleFlow().collect { println("${Thread.currentThread().name} : $it 받음") }
}
```
출력 결과
```
Exception in thread "main" java.lang.IllegalStateException: Flow invariant is violated:
		Flow was collected in [BlockingCoroutine{Active}@3ba5108b, BlockingEventLoop@424df6fa],
		but emission happened in [DispatchedCoroutine{Active}@42cbf900, Dispatchers.IO].
		Please refer to 'flow' documentation or use 'flowOn' instead
```

오류코드를 보면 flow대신 flowOn을 사용하라고 명시해준다.
flowOn 연산자를 이용하면 Context를 변경할 수 있다.

```kotlin
fun simpleFlow(): Flow<String> = flow {
    println("${Thread.currentThread().name}에서 데이터 Emit")
    emit("데이터")
}.flowOn(Dispatchers.Default)

fun main() = runBlocking<Unit> {
    simpleFlow().collect { println("${Thread.currentThread().name} : $it 받음") }
}
```
출력 결과
```
DefaultDispatcher-worker-2에서 데이터 Emit
main : 데이터 받음
```

### Buffer

데이터 발행자와 수신자가 데이터를 처리하는데에 시간이 걸린다고 가정해본다.

```kotlin
fun simpleFlow(): Flow<Int> = flow {
    for (i in 1..3) {
        delay(100)
        emit(i)
    }
}

fun main() = runBlocking<Unit> {
    val time = measureTimeMillis {
        simpleFlow().collect { value ->
            delay(300)
            println(value)
        }
    }
    println("Collected in $time ms")
}
```
출력 결과
```
1
2
3
Collected in 1263 ms
```
데이터 하나 발행하고(100ms) 처리하는데(300ms) 총 400ms가 소요된다.
데이터를 발행하고 데이터 처리를 기다리고 있기 때문에, 비효율적이라고 볼 수 있다.
이 때 buffer를 추가하여 개선할 수 있다.

```kotlin
fun simpleFlow(): Flow<Int> = flow {
    for (i in 1..3) {
        delay(100)
        emit(i)
    }
}

fun main() = runBlocking<Unit> {
    val time = measureTimeMillis {
        simpleFlow().buffer()
            .collect { value ->
            delay(300)
            println(value)
        }
    }
    println("Collected in $time ms")
}
```
출력 결과
```
1
2
3
Collected in 1110 ms
```
더이상 데이터의 처리를 기다리지 않고, 발행을 이어나가며, buffer가 가득 차면 그 때 대기한다.

### Conflation

buffer와 달리 수신자가 데이터를 처리할 수 있을 때 들어온 데이터만 받는다. (데이터 처리중에 발행된 데이터는 무시하고 버린다.)

```kotlin
fun simpleFlow(): Flow<Int> = flow {
    for (i in 1..3) {
        delay(100)
        emit(i)
    }
}

fun main() = runBlocking<Unit> {
    val time = measureTimeMillis {
        simpleFlow().conflate()
            .collect { value ->
                delay(300)
                println(value)
            }
    }
    println("Collected in $time ms")
}
```
출력 결과
```
1
3
Collected in 782 ms
```

### collectLatest
데이터를 받고 처리하는 도중에 새로운 데이터가 들어오면 종료 후 새로 데이터를 받기 시작한다.

```kotlin
fun simpleFlow(): Flow<Int> = flow {
    for (i in 1..3) {
        delay(100)
        emit(i)
    }
}

fun main() = runBlocking<Unit> {
    val time = measureTimeMillis {
        simpleFlow().collectLatest { value ->
            println("${value} 처리 시작")
            delay(300)
            println("${value} 처리 완료")
        }
    }
    println("Collected in $time ms")
}
```
출력 결과
```
1 처리 시작
2 처리 시작
3 처리 시작
3 처리 완료
Collected in 711 ms
```

### 예외처리

일반적인 try-catch문 외에도 flow에서는 catch 연산자를 사용하여 선언적으로 예외처리를 진행할 수 있다.

```kotlin
fun simpleFlow(): Flow<Int> = flow {
    for (i in 1..3) {
        println("Emitting $i")
        emit(i)
    }
}

fun main() = runBlocking<Unit> {
    simpleFlow()
        .catch { e -> println("Caught $e") } // 업스트림에만 영향을 주며, 다운스트림에는 예외처리에 영향을 주지 않는다.
        .collect { value ->
            check(value <= 1) { "Collected $value" }
            println(value)
        }
}
```
출력 결과
```
Emitting 1
1
Emitting 2
Exception in thread "main" java.lang.IllegalStateException: Collected 2
	at com.seoplee.androidstudy.flow.FlowBasicKt$main$1$invokeSuspend$$inlined$collect$1.emit(Collect.kt:135)
(...생략)
```

### 완료처리

일반적인 try-finally문 외에도 onCompletion 연산자를 이용하여 선언적으로 완료처리를 할 수 있다.

```kotlin
fun simpleFlow(): Flow<Int> = (1..3).asFlow()

fun main() = runBlocking<Unit> {
    simpleFlow()
        .onCompletion { println("Done") }
        .collect { value -> println(value) }
}
```
출력 결과
```
1
2
3
Done
```

onCompletion으로 완료처리를 하는 경우에는 이 완료 처리가 예외가 발생되어 완료되었는지 여부를 알 수 있다는 장점이 있다.

```kotlin
fun simpleFlow(): Flow<*> = (1..3).asFlow()
    .map {
        if( it > 2) throw IllegalStateException()
        it + 1
    }

fun main() = runBlocking<Unit> {
    simpleFlow()
        .onCompletion { cause ->
            if(cause != null) {
                println("Done Exceptionally")
            } else {
                println("Done")
            }
        }
        .catch{ emit("예외 발생") }
        .collect { value -> println(value) }
}
```
출력 결과
```
2
3
Done Exceptionally
예외 발생
```

### callbackFlow

Android에서는 이벤트가 발생 될 때 주로 listener등을 이용해 콜백형식으로 응답을 처리하는데, callbackFlow를 이용하면 callback을 flow로 변환하여 사용할 수 있다.

다음 코드는 안드로이드에서 버튼 클릭 리스너를 flow를 이용하여 이벤트를 데이터 스트림으로 바꾸어 동작하는 코드이다.

```kotlin
@ExperimentalCoroutinesApi
fun View.clicks(): Flow<Unit> = callbackFlow {
    setOnClickListener {
        trySend(Unit)
    }
    awaitClose { setOnClickListener(null) }
}
```

동기로 값을 보낸다면 send, 비동기로 값을 보낸다면 trySend를 사용할 수 있다.
awaitClose는 flow가 close될 때 실행되며, 더이상 리스너 스트림이 필요하지 않을 때 null 값을 할당하는 식으로 활용하여 사용한다.

```kotlin
signInButton.clicks()
    .onEach { viewModel.signIn() }
    .launchIn(lifecycleScope)
```

view에서는 위와 같이 사용할 수 있는데, 일반적으로 flow에서 사용하는 collect()가 아닌 launchIn()을 사용하였다.
launchIn은 별도로 지정한 스코프에서 컬렉션을 시작하기 때문에 Android에서 쉽게 생명주기를 알고있는 스코프를 할당하여 사용할 수 있으며, 클릭 이벤트 이후의 ui작업, 네트워킹 작업등을 이어나갈 수 있다.

예를 들어,

```kotlin
signInButton.clicks()
    .onEach { viewModel.signIn() }
    .collect()
doSomeThing()
```

이 경우에는 하나의 scope에서 컬렉션을 하기 때문에, collect가 끝날때까지 doSomeThing()을 실행하지 못한다.
이 경우 만들어진 클릭리스너는 해당 activity가 destroy될 때 까지 살아있기 때문에, 사실상 doSomeThing()은 의도한대로 실행되지 못한다.

```kotlin
signInButton.clicks()
    .onEach { viewModel.signIn() }
    .launchIn(lifecycleScope)
doSomeThing()
```

그러나 이 경우에는 별도의 scope를 이용하여 컬렉션을 하므로, doSomeThing()을 바로 실행할 수 있다.

## Flow in Android (with Room)

이번에는 배운 Flow를 안드로이드에서 어떤 식으로 사용할 수 있는지 알아본다.

앞서 살펴본것 처럼 Flow는 비동기 데이터 스트림이기 때문에 Data Domain에서 사용하기에도 적합하고, 여러 방법을 통해 생명주기와 결합하여 마치 LiveData를 observing하던 것 처럼 사용할 수 있다.

안드로이드에서는 내부 DB로 주로 사용되는 Room과 결합하여, 데이터가 갱신될 때 마다 매번 호출하지 않아도, 자동으로 데이터의 변화를 감지하여 ui에 뿌려주는 방법으로도 사용할 수 있다. (Room은 Rx, Flow등의 비동기 데이터 스트림을 지원한다.)

```kotlin
@Query("SELECT * FROM Todo")
fun getTodos(): Flow<List<Todo>>
```
```kotlin
fun getAllTodos(): Flow<List<Todo>> {
    return db.TodoDao().getTodos()
}
```
Room이 Flow를 지원하기 때문에 리턴타입을 Flow로 지정하여 비동기 스트림으로 데이터를 관리할 수 있다.

```kotlin
fun getAllTodos() : Flow<List<Todo>> {
    return todoRepository.getAllTodos()
}
```
in ViewModel

```kotlin
lifecycleScope.launch {
    viewModel.getAllTodos().collect {
        adapter.submitList(it)
    }
}
```
in View(Activity)

위처럼 ui layer에서 Flow를 collect하여 emit되는 값들을 recyclerview adapter에 넣어줌으로, 데이터가 추가되거나 삭제돼도 별도의 호출 코드 없이 ui를 갱신할 수 있다.

그러나 위 flow는 생명주기와 연결되어 있지 않아서 collect를 하지 않아도 될 때에도 계속 수집하고 있어 리소스 낭비가 일어난다.

이 문제는 다음과 같은 방법들로 해결할 수 있다.

1. asLiveData() 사용
2. repeatOnLifecycle 사용

asLiveData를 사용하여, Flow를 LiveData로 변환하여 기존처럼 사용할 수 있게 해준다.
```kotlin
val response = todoRepository.getAllTodos().asLiveData()
```

혹은 repeatOnLifecycle을 이용하여 flow에 생명주기를 연결시켜주는 방법이 있다.
```kotlin
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED){
        viewModel.getAllTodos().collect {
            adapter.submitList(it)
        }
    }
}
```

