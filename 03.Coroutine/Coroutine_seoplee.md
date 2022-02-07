## Coroutine(코루틴)

### Coroutine이란?
스레드와 비슷한 기능을 수행하지만 스레드보다 훨씬 가볍고 쉽게 사용할 수 있는 동 처리 기술중 하나이다.

### 특징 (in Android)
- 실행중인 스레드를 차단하는 것이 아니라, suspend를 지원하여 단일 스레드에서 많은 코루틴을 실행할 수 있으며 리소스를 절약할 수 있다.
- 안드로이드의 생명주기에 따라 관리할 수 있어 메모리 릭을 방지할 수 있다.
- Jetpack의 다양한 라이브러리와 호환되기 때문에 기존의 쉽게 사용할 수 있다.

### vs Thread
- 스레드와 코루틴 모두 동시성을 보장하기 위한 기술
- 코루틴은 스레드와 달리 OS의 영향을 받지 않아 소모되는 리소스가 적다.
- 스레드는 작업1(A 스레드)이 작업2(B스레드)의 작업을 기다려야하면 A를 블로킹하지만, 코루틴은 작업1의 코루틴은 suspend되지만, 스레드가 블로킹 되지는 않는다. (안드로이드에서 UI를 다루는 메인스레드의 블로킹을 막을 수 있다.)
- 개발자가 직접 코루틴을 실행하고, 재개하고, 종료하는것을 지정할 수 있다.
- 코루틴은 전환시에도 시스템의 영향을 받지 않아 비용이 발생하지 않는다.


### Suspend
코루틴은 일시중단될 수 있다.

```kotlin
private fun suspendExample() {
    val task1 = CoroutineScope(Dispatchers.IO).async {
        (1..10000).sortedByDescending { it }
    }
    val task2 = CoroutineScope(Dispatchers.Main).launch {
        println("task2 시작")
        val result = task1.await()
        result.forEach {
            println(it)
        }
    }
}
```
출력 결과
```
I/System.out: task2 시작
    10000
    9999
    9998
    9997
    (...)
```
task2에서 task1의 결과를 기다려야 할 때, task2의 작업은 '일시중단' 된다. task2가 끝나고 결과가 나오면 다시 재개되어 남은 작업을 실행한다.
코루틴 일시 중단은 반드시 코루틴 블록 내부(Coroutine Scope 혹은 suspend fun)에서만 수행가능하다.

### Dispatcher?
코루틴에서는 스레드 풀을 만들고 Dispatcher를 통해 코루틴을 배분하여 사용한다.
Dispatcher는 자신이 관리하는 스레드풀 내의 스레드의 상황에 맞춰 코루틴을 배분해준다.
안드로이드에서는 사전에 정의된 Dispatcher들을 사용하며, 다음과 같다
1. Dispatcher.Main
   메인 스레드에서 코루틴을 실행하는 Dispatcher로, UI와 상호작용하는 작업을 실행할때만 사용한다.
2. Dispatcher.IO
   디스크, 네트워크 I/O작업을 실행하는데 사용한다.
3. Dispatcher.Default
   CPU를 많이 사용하는 작업을 외부에서 실행하도록 되어으며, 정렬작업, JSON파싱 작업등에 사용한다.

### launch-Job

launch() 함수로 시작되는 코루틴 블록은 Job 객체를 반환한다.
반환받은 Job 객체를 cancel() 메소드등으로 취소하거나, join() 메소드로 코루틴 작업이 완료되기까지 함수의 진행을 멈출 수 있다.

#### join 사용 예시
```kotlin
 val task : Job = launch {
     var i = 0
     while (i < 4) {
         println(i)
         delay(1000)
         i++
     }
 }
 task.join()
 println("종료")
```
출력 결과
```
I/System.out: 1 
I/System.out: 2
I/System.out: 3
I/System.out: 종료
```
task1이 끝나고나서야 다음 코드인 println("종료")를 호출하였다.

### async-deffered

async() 함수로 시작되는 코루틴 블록은 Deferred를 반환한다. Deferred는 미래에 올 값을 담는 객체라고 볼 수 있다.
주로 await()메소드와 함께 사용하여 결과 반환을 대기하는데에 사용한다.

#### await 사용 예시
```kotlin
val task : Deferred<String> = async {
    var i = 0
    while (i < 4) {
        println(i)
        delay(300)
        i++
    }
    "task done" // 마지막 줄 반환
}
val msg = task.await()
println(msg) 
```
출력 결과
```
I/System.out: 1
I/System.out: 2
I/System.out: 3
I/System.out: task done
```
task의 결과값을 기다렸다가 반환받아서 출력하는것을 볼 수 있다.

```kotlin
val task1 : Deferred<String> = async {
    var i = 0
    while (i < 4) {
        println(i)
        delay(300)
        i++
    }
    "task1 done"
}

val task2 : Deferred<String> = async {
    var i = 0
    while (i < 6) {
        println(i)
        delay(300)
        i++
    }
    "task2 done"
}

val msg = awaitAll(task1, task2)
println(msg) 
```
출력 결과
```
I/System.out: 1
    1
I/System.out: 2
    2
I/System.out: 3
    3
I/System.out: 4
I/System.out: 5
I/System.out: [task1 done, task2 done]
```
여러 async를 같이 기다렸다가 결과값을 반환받아 사용할 수 있다.


### Ref
https://developer.android.com/kotlin/coroutines

https://kotlinworld.com/141?category=973476
