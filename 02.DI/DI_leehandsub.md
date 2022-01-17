# Dependency란?
+ Dependency는 **의존성** 이라는 뜻을 가지고 있음
+ 프로그래밍에서의 의존성은 함수에 필요한 클래스 또는 참조변수나 객체에 의존하는 것

### Dependency 예시

```kotlin
class A() {
  fun job()
}
class B() {
  val a = A()
  fun work() {
    a.job()
  }
}
```

+ 위의 두 클래스의 관계를 살펴보면 **class B는 class A를 의존**하고 있다고 할 수 있음.
+ class A를 삭제하면, class B는 컴파일이 되지 못함
+ class A가 변경이 되면, class B도 알맞게 수정이 필요
  * class A를 의존하는 클래스가 늘어날 수록, class A를 수정하면 함께 수정해야할 의존성을 갖는 클래스가 너무 많아짐
# __의존성 주입(Dependency Injection)이란?__

특정 객체의 인스턴스를 직접 생성하지 않고 **외부에서 생성된 객체를 전달하는 기법입니다.** 각 객체는 다른 객체의 생성에는 관여하지 않고, 객체를 필요로 하는 부분과 독립된 별도의 모듈이 객체 생성과 주입을 전담합니다.

### __의존성 주입을 하는 이유?__

+ 첫째, **목적에 따라 동작을 변경하기 쉽습니다.** 특정 객체에 필요한 객체를 외부에서 전달받으므로 **필요에 따라 다른 동작을 하는 객체를 간편하게 생성할 수 있습니다.**

+ 둘째, **생성한 객체를 쉽게 재사용할 수 있습니다.** 객체를 생성하는 작업을 특정 모듈에서 전담하기 때문입니다.

+ 셋째, **객체의 생성 또는 사용할 때 발생하는 실수가 줄어듭니다.** 같은 역할을 하는 객체를 각각 다른 곳에서 별도로 생성하도록 코드를 작성하는 경우 **해당 객체를 생성하는 모든 부분을 수정해야 하므로 실수를 유발시킬 수 있습니다.** 하지만 의존성 주입을 사용하면 **객체를 생성해주는 부분만 수정하면 되므로** 실수가 줄어들 수 있습니다.

### Dependency Injection 예시
1. 생성자 주입 (Constructor Injection) : 생성자를 통해 의존하는 객체를 전달
```kotlin
class A() {
  fun job()
}
class B(a: A) {
  private var a: A
  
  init {
    this.a = a
  }
  
  fun work() {
    a.job()
  }
  
  fun main(args: Array<String>) {
    val b = B(A())
    
    b.work()
  }
}
```

2. 필드 주입 또는 세터 주입 (Field Injection or Setter Injection) : 객체가 초기화된 후, 메서드를 통해 의존하는 객체를 전달
```kotlin
class A() {
  fun job()
}
class B() {
  private var a: A
  
  fun setA(a:A) {
    this.a = a
  }
  
  fun work() {
    a.job()
  }
  
  fun main(args: Array<String>) {
    val b = B()
    b.setA(a())
    
    b.work()
  }
}
```

안드로이드에서 좀 더 효과적으로 DI를 활용하기위하여 Dagger, Koin과 같은 라이브러리가 존재한다. 이것은 다음주에 직접 해보자
