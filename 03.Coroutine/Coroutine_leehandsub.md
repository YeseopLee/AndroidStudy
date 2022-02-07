# Coroutine

### 개념
- 스레드와 달리 코틀린은 코루틴을 통해 복잡성을 줄이고도 손쉽게 일시 중단하거나 다시 시작하는 루틴을 만들어낼 수 있다.
- 멀티태스킹을 실현하면서 가벼운 스레드라고도 불림
- 코루틴은 문맥 교환 없이 해당 루틴을 일시 중단(suspended)을 통해 제어

**Kotlin.coroutines의 commom 패키지**

| 기능 | 설명 |
|--------------------|--------------------|
| launch / async | 코루틴 빌더</br>둘의 차이는 실행 결과 반환 여부이다. |
| Job / Deferred | cancellation 지원을 위한 기능</br> ( Job = 생명주기를 가지고 동작하는 작업의 단위 ) |
| Dispatchers | 스케줄러</br>Default는 백그라운드 코루틴을 위한 것이고 Main은 Android 나 Swing, JavaFx를 위해 사용 |
| delay / yield | 상위 레벨 지연(suspending) 함수 |
| Channel / Mutex | 통신과 동기화를 위한 기능 |
| coroutineScope / supervisorScope | 범위 빌더 |
| select | 표현식 지원 |

### lauch
 1. 일단 실행하고 잊어버지는(fire and forget) 형태의 코루틴 ( = 메인 프로그램과 독립적으로 실행할 수 있습니다. )
 2. 기본적으로 즉시 실행하며 블록 내의 **실행 결과는 반환하지 않습니다.**
 3. 상위 코드를 블록시키지 않고(= Non blocking) 관리를 위한 Job 객체를 즉시 반환합니다.
 4. join을 통해 상위 코드가 종료되지 않고 완료를 기다리게 할 수 있습니다.
 5. launch 블록 내에서 코드가 순차적으로 진행됩니다.

```kotlin
import kotlinx.coroutines.*
fun main() { // 메인 스레드 문맥
    GlobalScope.launch { // 새로운 코루틴을 백그라운에 실행
        delay(1000L) // 1 초의 넌블로킹 지연 ( 시간의 기본 단위는 ms )
        println("World!") // 지연 후 출력
    }
    println("Hello ") // main 스레드가 코루틴이 지연되는 동안 계속 실행
    Thread.sleep(2000L) // main 스레드가 JVM에서 바로 종료되지 않게 2초를 기다린다.
}
```
### suspend
```kotlin
import kotlinx.coroutines.*
fun main() {
    GlobalScope.launch {
        delay(1000L)
        println("World!")
        doSomething()
    }
    println("Hello ")
    Thread.sleep(2000L)
}
suspend fun doSomething() {
    println("Do Something")
}
```

### Job
- 백그라운드에서 실행하는 작업
- 코루틴의 생명주기를 관리하며 생성된 코루틴 작업들은 부모-자식과 같은 관계를 가질 수 있습니다.
- 부모가 취소되거나 실행 실패할 경우 하위 자식들은 모두 취소됩니다.
- 자식의 실행 실패는 해당 부모에게 전달되며 부모 또한 실행 실패합니다. ( 따라서 모든 자식의 실행도 취소됩니다. )
- **SupervisorJob**을 사용할 경우 자식의 실행 실패가 해당 부모에게 전달되지 않으므로 실행을 유지할 수 있습니다. 
### 상황을 판별하기 위한 상태
![image](https://user-images.githubusercontent.com/35682233/152771168-6bb9f964-b8b4-47c5-a27d-bcae53fcfad4.png)
![image](https://user-images.githubusercontent.com/35682233/152771469-e009ecb0-8f20-4fc0-8445-2d0523fe8b42.png)

* ### 중단 ( Coroutine Code 내에서 )
  * `delay(timeValue)` : 일정 시간을 지연(Non-blocking)하며 중단
  * `yield()` : 특정 값을 산출하기 위해 중단
* ### 취소 ( Coroutine Code 외부에서 )
  * `Job.cancel()` : 지정된 코루틴 작업을 즉시 취소
  * `Job.cancelAndJoin()` : 지정된 코루틴 작업을 취소 ( 완료시까지 기다림 )
* ### async
 1. **비동기 호출을 위해** 만든 코루틴으로 결과나 예외를 반환한다.
 2. 실행 결과는 Deffered<T>를 통해 반환하며 await을 통해 받을 수 있다.
 3. await은 작업이 완료될 때까지 기다리게 된다.

```kotlin
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis
suspend fun doWork1(): String {
    delay(1000)
    return "Work1"
}
suspend fun doWork2(): String {
    delay(3000)
    return "Work2"
}
private fun worksInParallel(): Job {
    // Deferred<T> 를 통해 결과값을 반환
    val one = GlobalScope.async {
        doWork1()
    }
    val two = GlobalScope.async {
        doWork2()
    }
    return GlobalScope.launch {
        // 지연된 결과를 받기 위해 await를 사용
        val combined = one.await() + "_" + two.await()
        println("Kotlin Combined : $combined")
    }
}
fun main() = runBlocking<Unit> { // 메인 메서드가 코루틴 환경에서 
    val time = measureTimeMillis {
        val job = worksInParallel()
        job.join()
    }
    println("time: $time")
}
```
  
### Coroutine Context
 * 코루틴을 실행하기 위한 다양한 설정 값을 가진 관리 정보 </br> - 코루틴 이름, 디스패처, 작업 상세사항, 예외 핸들러 등
 * Dispatcher 는 코루틴 문맥을 보고 어떤 스레드에서 실행되고 있는지 식별이 가능해진다.
 * 코루틴 문맥은 + 연산을 통해 조합될 수 있다.
  
* #### CoroutineName : 코루틴에 이름을 부여하며 디버깅을 위해 사용된다.
```kotlin
val someCoroutineName = CoroutineName("someCoroutineName")
```
   * #### Job : 작업 객체를 지정할 수 있으며 취소가능 여부에 따라 SupervisorJob() 사용
```kotlin
val parentJob = SupervisorJob() // or Job()
val someJob = Job(parentJob)
```
   
   * #### CoroutineExceptionHandler
      * 코루틴 문맥을 위한 예외처리를 담당하며 코루틴에서 예외가 던져지면 처리합니다.
      * 예외가 발생한 코루틴은 상위 코루틴에 전달되어 처리될 수 있습니다. 
      * 만일 예외처리가 자식에만 있고 부모에 없는 경우 부모에게도 예외가 전달되므로 앱이 crash 되지 않게 주의가 필요합니다.
      * 예외가 다중으로 발생하면 최초 하나만 처리하고 나머지는 무시됩니다.
### Scope
* 새로운 코루틴 스코프가 생성될 때마다 새로운 Job이 생성되어 만들어진 코루틴 스코프와 연관 지어집니다.
* ### GlobalScope
  * **독립형(Standalone) 코루틴**을 구성합니다.
  * Dispatchers.Unconfined와 함께 작업이 될 경우 서로 무관한 전역 범위에서 실행 가능합니다.
  * 보통 GlobalScope 상에서는 launch나 async 사용이 권장되지 않습니다.
* ### CoroutineScope
  * 특정 목적의 디스패처를 지정한 범위를 블록으로 구성할 수 있습니다.
  * 모든 코루틴 빌더는 CoroutineScope의 인스턴스를 갖습니다.
  * launch {..}와 같이 인자가 없는 경우에는 CoroutineScope에서는 상위의 문맥이 상속되어 결정됩니다.
  * `launch(Dispatchers.옵션인자) {...}`와 같이 디스패처의 스케줄러를 지정 가능합니다.
  * runBlocking과 유사하지만 runBlocking은 단순 함수로 현재 스레드를 **블록킹**한다면, coroutineScope는 단순히 지연(suspend)함수 형태로 구성되어 **넌블로킹**으로 사용됩니다.
  * 만약 자식 코루틴이 실행 실패하면 이 scope도 실패하고 남은 모든 자식을 실행 취소합니다.

 ```kotlin
import kotlinx.coroutines.*
fun main() = runBlocking<Unit> { // 메인 메서드가 코루틴 환경에서 실행
    val request = launch { // 벡그라운드로 코루틴 실행
        GlobalScope.launch { // 프로그램 전역으로 독립적인 수행 ( 부모-자식 관계 없음 )
            println("job1 : before suspend function")
            delay(1000)
            println("job1: after suspend function") // 작업 취소에 영향을 받지 않음
        }
        launch { // 부모의 context을 상속 ( 상위 launch의 자식 )
            // 다른 방법1 : launch(Dispatchers.Default) { // 부모의 context를 상속 ( 상위 launch의 자식 ), 분리된 작업을 수행하지만 request의 영향을 받음
            // 다른 방법2 : CoroutineScope(Dispatchers.Default).launch { // 새로운 scope가 구성되므로 request와는 무관
            delay(100)
            println("job2: before suspend function")
            delay(1000)
            println("job2: after suspend function")
        }
    }
    delay(500)
    request.cancel() // 부모 코루틴이 취소됨
    delay(1000)
}
``` 
## 출처
* [공식문서](https://kotlinlang.org/docs/coroutines-guide.html#additional-references)
* [부스트코스](https://www.boostcourse.org/mo234/lecture/154327)
