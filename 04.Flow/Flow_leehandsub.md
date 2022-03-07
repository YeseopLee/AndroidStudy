# Flow
Flow는 순차적으로 여러 값을 emit하는 데이터스트림이며, 단 하나의 값만 반환하는 suspending 함수와는 다르다. flow를 쓰면 DB에서 실시간 없데이트하는 값들을 수신할 수 있다.  
flow는 코루틴을 기반으로 빌드되며, 비동기식으로 계산할 수 있는 데이터 스트림이다.

## Data Stream의 세 가지 요소 : 생산자 + 중개자(option) + 소비자
![image](https://user-images.githubusercontent.com/35682233/157015186-8872c154-b0e4-48a7-8687-bc6c66b26054.png)
생산자(Producer) : 스트림에 추가되는 데이터를 생산함. flow에서 비동기적으로 데이터를 생산함 

중개자(Intermediaries, option) : 스트림에 내보내는 각각의 값을 수정하거나, 스트림 자체를 수정함

소비자(Consumer) : 스트림의 값을 사용함

안드로이드에서 일반적으로 repository는 UI 데이터의 Producer다. 그리고 UI는 최종적으로 데이터를 표시하는 Consumer다. UI 레이어는 User Input Event의 Producer이며, 계층 구조의 다른 레이어가 input event를 사용하기도 한다. 
Producer와 Consumer 사이에 있는 레이어는 일반적으로 중개자의 역할을 하는데, 다음 레이어의 요구사항에 맞춰주기 위해서 데이터 스트림을 수정해준다.

## Flow 생성
Flow는 다음과 같이 생성한다.
``` kotlin
val flow = flow {
    // ..
}
```
이제 Flow에서 정수 1, 2, 3을 방출한다.
``` kotlin
val flow = flow {
    emit(1)
    emit(2)
    emit(3)
}
```
이렇게 방출된 데이터는 collect()메소드를 통해 수신할 수 있다.
``` kotlin
val flow = flow {
    emit(1)
    emit(2)
    emit(3)
}
flow
    .collect { value ->
        println(value)
    }
```
그러나 위 코드를 실행하면 다음과 같은 에러가 발생
```
Suspend function 'collect' should be called only from a coroutine or another suspend function
```
collect()함수는 코루틴에서 호출해야하기 때문에 루틴 내에서 collect()를 호출하도록 수정
``` kotlin
val flow = flow {
    emit(1)
    emit(2)
    emit(3)
}
// 스코프 생성
val scope = CoroutineScope(Dispatchers.IO)
// 코루틴 생성
scope.launch {
    flow
        .collect {
            println(it)
        }
}
```
위 코드는 Flow의 데이터 방출과 데이터 수신을 모두 Dispatchers.IO가 제공하는 스레드에서 처리한다. 만약 데이터 수신만 매인 스레드에서 수행하려면 다음과 같이 코드를 수정할 수 있다.

``` kotlin
val flow = flow {
    emit(1)
    emit(2)
    emit(3)
}
// 스코프 생성
val scope = CoroutineScope(Dispatchers.IO)
// 코루틴 생성
scope.launch {
    flow
        .collect {
            withContext(Dispatchers.Main) {
                println(it)
            }
        }
}
```
delay()함수를 사용하면 데이터 방출 주기를 조절할 수 있다. 1초에 한 번씩 한 개의 데이터를 방출해보았다.
``` kotlin
val flow = flow {
    for (i: Int in 1..10) {
        emit(i)
        delay(1000)
    }
}
``` 

## StateFlow
StateFlow는 상태 기반의 스트림이다. 상태가 변하면 수신자에게 이 사실을 가르쳐준다. 상태 기반의 스트림은 상태가 변한 후에도 값을 유지가된다.
따라서 상태가 변한 후에도 값에 계속 접근할 수 있다. 이러한 점에서 StateFlow은 LiveData와 비슷하다.

버튼을 누르면 StateFlow의 상태를 변화시키고, 이를 탐지하여 로그에 출력하는 예제를 해보았다.

``` kotlin
class MainActivity : AppCompatActivity() {
    private val button: Button by lazy { findViewById(R.id.activity_main_button) }
    // StateFlow 생성
    // StateFlow는 상태를 변화시킬 수 없으며 MutableStateFlow는 상태를 변화시킬 수 있다. 
    val _stateFlow = MutableStateFlow(1) //  초기값 1
    val stateFlow = _stateFlow
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 버튼을 눌렀을 때
        button.setOnClickListener {
            // 값을 1씩 증가
            _stateFlow.value = _stateFlow.value + 1
        }
    }
}    
```


