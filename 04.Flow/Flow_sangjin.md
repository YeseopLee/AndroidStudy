# Android Dev Summit 2021

## Flow

![Untitled](https://user-images.githubusercontent.com/77181865/157009714-0d29d757-793f-4d62-a7e0-a91f38778f28.png)

Flow에서 생산자는 데이터를 Flow에 입력하고, 소비자는 해당 flow로부터 데이터를 수집한다.

Android에서는 Data source 나 Repository가 생산자 역할을 하고, 화면에 데이터를 표시하는 UI layer가 소비자 역할을 한다.

![Untitled 1](https://user-images.githubusercontent.com/77181865/157009518-40f1b95a-0f84-4369-8f40-0b3722cbacb7.png)

대부분의 경우 Flow를 직접 만들 필요가 없고, 데이터 소스 라이브러리는 보통 Flow와 통합되어 있다. DataStore, Retrofit, Room, WorkManager 등이 댐 역할을 수행하며 Flow를 사용하여 데이터를 제공하고, 개발자는 데이터를 받을 파이프라인만 연결해주면 이를 사용할 수 있다.

![Untitled 2](https://user-images.githubusercontent.com/77181865/157009536-95f86ac9-cc5c-4551-85a7-2aa8d0012999.png)

다음은 Room 예시이다. 위의 코드에서 List<Codelab> 타입의 Flow를 통해 DB 변경 사항을 알리고 있고, Room 라이브러리가 생산자 역할을 맡아 업데이트가 있을 시 Query에 대한 내용을 전달한다.

![Untitled 3](https://user-images.githubusercontent.com/77181865/157009542-50b21c1c-2c8f-4775-9f41-07b1104ecdce.png)

수시로 앱에서 온 메시지를 확인하는 예시이다.

![Untitled 4](https://user-images.githubusercontent.com/77181865/157009545-928c23af-fee2-4bf9-909e-41c3dbe08da5.png)

List<Message> 타입의 flow를 통해 사용자 메시지의 변경 사항을 알 수 있다. 이 때 flow를 만들기 위해 flow builder를 사용해야 하는데, **flow builder는 suspend 함수를 호출** 할 수 있다. 이는 **flow가 coroutine context에서 실행**되기 때문이다.

![Untitled 5](https://user-images.githubusercontent.com/77181865/157009548-a8935942-19de-42b5-8810-5c6d0f0e3e36.png)

내부에서 while문 loop이 돌면서 가장 먼저 API를 호출하여 최신의 메시지를 가져온다. 이를 **emit**을 통해 **flow에 메시지를 전달**하고, collector가 해당 메시지를 받을 때까지 **coroutine을 중단**시킨다. 마지막으로 일정시간 corouitine을 중단한다. 

해당 flow는 동일한 coroutine에서 순차적으로 실행되며 관찰자가 사라지고 아이템 수집이 중단되면 멈추게 된다.

![Untitled 6](https://user-images.githubusercontent.com/77181865/157009552-3f4d7762-b193-431a-8f17-d5ddf58e7051.png)

flow builder에 전달된 suspend 블록은 생산자 블록이라고 부른다.

## Collecting flow

Android에서 생산자와 소비자 간의 계층은 요구 사항에 맞게 데이터 스트림을 수정할 수 있으며 flow를 변환하려면 중간 연산자를 사용하면 된다.

![Untitled 7](https://user-images.githubusercontent.com/77181865/157009559-c03ae07c-cf60-4bb3-87eb-e062ab5878c5.png)

latestMessage 스트림이 flow의 시작점이라고 가정한다면 **map 연산자를** 사용하여 데이터를 다른 타입으로 변환할 수 있다.

![Untitled 8](https://user-images.githubusercontent.com/77181865/157009563-428f04e3-450c-495b-9c58-d47b6b21cd28.png)

다음 예는 map 내부 람다식 안에 data source로부터 오는 Room Message를 UiModel로 변환할 수 있는 것을 설명한다. 이를 통해 더 **좋은 추상화를 제공**할 수 있다. 각 연산자는 기능에 따라 데이터를 전송하는 새로운 flow를 생성한다.

![Untitled 9](https://user-images.githubusercontent.com/77181865/157009564-dc1f0d88-990e-4bf5-bb9f-be0451aae6f0.png)

**filter**를 사용하면, 중요한 알림이 포함된 메시지의 flow를 가져올 수도 있다.

![Untitled 10](https://user-images.githubusercontent.com/77181865/157009571-47e65599-6c15-46b4-8840-bc9dc2aefaf0.png)

스트림에서 발생하는 에러는 다음 catch 연산자를 통해 flow에서 발생하는 예외를 찾아낸다.

![Untitled 11](https://user-images.githubusercontent.com/77181865/157009575-5ae5649a-7ee5-4393-a9c2-59a3b0fcede8.png)

Upstream flow란, 생산자 블록에서 생성한 flow이며 현재 연산자(위의 그림에서는 catch) 전에 이들을 호출한다.

![Untitled 12](https://user-images.githubusercontent.com/77181865/157009581-24fd2b0d-3f3e-42c1-834f-71a2c45206f6.png)

마찬가지로 현재 연산자 이후에 해당하는 모든 것들을 Downstream flow라고 한다.

![Untitled 13](https://user-images.githubusercontent.com/77181865/157009584-547484b8-6fe3-4726-89a5-89aa9c3e995e.png)

catch는 필요하거나 새 값을 다시 전송할 때 필요에 따라 에러를 발생시킬 수도 있다. 위의 예시에서는 예외가 발생할 경우 Exception을 발생시키지만, 다른 예외가 발생하면 emptyList를 전달하기도 한다.

## Observing Flows

일반적으로 flow를 collect하는 것은 보통 **화면에 데이터를 표시하기 위해 UI layer**에서 일어난다.

![Untitled 14](https://user-images.githubusercontent.com/77181865/157009586-2dccdb8a-2ac4-4148-974f-000d390692bd.png)

리스트에 최신 메시지를 표시한다는 가정하에 우리는 terminal 연산자를 사용해서 값을 수신해야 한다. 스트림의 모든 값을 전송한 즉시 가져오려면 collect를 사용하면 되지만 **collect는 새로운 값이 생길 때마다 호출되는 함수를 파라미터로 받으며 suspend 연산자**이기 때문에 coroutine 안에서 실행되어야 한다.

![Untitled 15](https://user-images.githubusercontent.com/77181865/157009588-35f99f19-9b9d-4476-8934-1e9ec55a0f3c.png)

위 코드에선 userMessage에서 collect를 호출할 때마다 새로운 flow가 생성되고 있다. 생산자 블록은 정해진 간격에 따라 API에서 메시지를 새로 고침할 것이다.

![Untitled 16](https://user-images.githubusercontent.com/77181865/157009592-c71e6e73-3527-4521-95d7-15f3b59855d1.png)

즉 이러한 flow를 **Cold flow**라고 한다. 이는 필요에 따라 생성되고 관찰되는 중에만 데이터를 전송하기 때문이다.

## Flows in Android UI

이제 Android UI에서 최적의 방법으로 flow를 수집하는 방법을 알아보겠다. 고려해야 할 것은 다음과 같다.

- App이 Background에 있을 때 Resource를 낭비하지 않는 것
- Configuration change

![Untitled 17](https://user-images.githubusercontent.com/77181865/157009595-1274903e-de0e-4dd6-8034-f652073232b3.png)

다음 액티비티에서 화면에 Message를 표시해야 한다고 가정하자.

![Untitled 18](https://user-images.githubusercontent.com/77181865/157009598-a1e0d01d-72f6-4b3d-b400-c0a0ac56ca05.png)

flow는 화면에 UI가 표시되지 않을 때 flow에서 수집을 중단해야 한다. 이때 여러가지 선택지가 있는데, 모든 대안들이 lifecycle을 인지한다.

![Untitled 19](https://user-images.githubusercontent.com/77181865/157009599-ddcad216-5195-4d64-8f28-53b53982eef5.png)

**asLiveData flow 연산자**는 flow를 LiveData로 변환해서 **UI가 화면에 표시되는 동안**에만 아이템을 관찰한다. UI에서는 평소처럼 LiveData를 사용하기만 하면 된다.

![Untitled 20](https://user-images.githubusercontent.com/77181865/157009601-166c512d-9efd-4182-86ac-e4f44e0b3afb.png)

UI layer에서 flow를 collect할 때 **repeatOnLifecycle**을 사용하는게 좋다. 이는 Lifecycle.State를 parameter로 받는 suspend 함수이다. 이 함수는 lifecycle을 인식하며 **수명 주기가 해당 상태에 도달하면 블록을 전달할 새로운 coroutine이 자동으로 시작되고, 해당 수명 주기 아래로 떨어지면 취소**된다. 

repeatOnLifecycle는 suspend function이기 때문에, **coroutine안에서 호출**되야 한다. 또한 이를 사용하기 가장 좋은 방법으로는 수명 주기가 **initialized 될 때(ex. 위의 예시처럼 onCreate 내부) 호출**하는 것이 좋다.

또한 중요한 점으로는 **repeatOnLifecycle을 호출하는 coroutine은 lifecycle이 파괴될때까지 실행을 resume하지 않는다!**

![Untitled 21](https://user-images.githubusercontent.com/77181865/157009604-d9799bf3-b3f6-4c0d-909d-773f3d0108c7.png)

여러 flow를 수집해야 할 경우, repeatOnLifecycle 블록에서 launch를 통해 여러 coroutine을 생성해야 한다.

![Untitled 22](https://user-images.githubusercontent.com/77181865/157009610-f17249bb-55f9-4332-b393-4750d6180e9d.png)

또한 **flowWithLifecycle** 연산자는 수집할 flow가 하나뿐일 경우 repeatOnLifecycle 대신 사용할 수 있다.

![Untitled 23](https://user-images.githubusercontent.com/77181865/157009617-649d117f-f136-4e22-ae2f-0775f3c89efd.png)

왼쪽의 그림은 Android의 생명 주기를 나타낸다. 사용자가 홈 버튼을 눌렀을 때, background에 전송되고 액티비티에서 onStop이 호출이 되고 다시 App을 열었을 경우 onStart가 호출되면서 다시 앱이 열리게 된다. 

Flow의 경우 STATED 상태로 repeatOnLifecycle을 호출하면, **UI가 화면에 표시되는 동안 flow 전송을 처리하고 App이 background로 이동할 경우 수집이 취소된다.**

> ***repeatOnLifecycle***과 **flowWithLifecycle**은 lifecycle-runtime-ktx:2.4.0 라이브러리에 추가된 API이다.
> 

![Untitled 24](https://user-images.githubusercontent.com/77181865/157009621-f667f7d6-3e75-44dd-8ed2-2459ecb6f8ef.png)

만일 화면에 표시되는 동안만 flow를 수집하고 싶을 경우, lifecycleScope를 이용하면 unsafe 할수 있다. 왜냐하면 이는 **background에 앱이 위치해도 flow에서 계속 수집을 진행**하기 때문이다.

![Untitled 25](https://user-images.githubusercontent.com/77181865/157009622-7bbc4413-ae64-4456-b132-2fe9ac7d09fa.png)

사실 위의 방식 뿐만 아니라 **lifecycleCoroutineScope.launchWhenX** API에서도 비슷한 문제가 있다.

![Untitled 26](https://user-images.githubusercontent.com/77181865/157009624-10b74f02-844c-4290-9f3c-46eb776aead7.png)

***lifecycleScope.launch***에서 flow를 수집하는 경우 액티비티가 background에 있을 때도 계속 flow가 업데이트 된다. 이는 낭비일 뿐만 아니라 위험하다. **앱이 background에 있을 때 UI에 대한 작업을 하면 crash가 발생**하기 때문이다.

![Untitled 27](https://user-images.githubusercontent.com/77181865/157009625-2bd9baf1-c5ff-4f32-acc3-3d3805d03193.png)

위의 문제를 해결하기 위해선 onStart에서 수동으로 수집을 시작하고 onStop에서 수집을 중단해야 한다. 그러나 repeatOnLifecycle를 사용할 경우 boilerplate code를 모두 제거할 수 있다. 

다른 방법도 있다. 바로 ***launchWhenStarted***를 사용하면 되는데, 이는 App이 background에 있는 경우 수집을 suspend할 수 있다. 그러나 **이 방식은 flow 생산자가 계속 활성화** 되어 있어서 **background 내에서도 화면에 나타나지 않고 메모리를 가지는 아이템을 전달**할 수도 있어 repeatOnLifecycle을 사용하는 것이 효율적이다.

![Untitled 28](https://user-images.githubusercontent.com/77181865/157009630-b592de33-dcdc-4e38-9e87-2b2f360b8377.png)

UI에서 flow 생산자의 구현 방법을 알 수 없으므로 repeatOnLifecycle 이나 flowWithLifecycle을 안전하게 사용하는 것을 권장한다.

## StateFlow

![Untitled 29](https://user-images.githubusercontent.com/77181865/157009635-da0d9698-8b50-43c1-aab5-7efc87ccd9a0.png)

flow를 view에 노출하면 수명 주기가 서로 다른 두 요소 사이에 데이터를 전달해야 한다는 것을 고려해야 한다. 특히 Activity와 Fragment에서 까다로울 수 있다.

화면이 회전하거나 Configuration Change 이벤트를 수신하면 모든 Activity는 다시 시작하지만, ViewModel은 그렇지 않다.

![Untitled 30](https://user-images.githubusercontent.com/77181865/157009636-5aa00821-fa56-4cf8-b2b8-3c2c50a246ed.png)

ViewModel에서 모든 flow를 노출하는 것은 아니다. 예를 들어 위와 같은 cold flow가 있다고 가정할 때, cold flow는 처음으로 수집될 때마다 다시 시작하기 때문에 repository는 한번 회전 후 다시 호출될 것이다.

따라서 재생성 횟수와 상관없이 데이터를 보관하고 여러 collector 사이에 공유할 수 있는 일종의 **버퍼**가 필요하다.
  
![Untitled 31](https://user-images.githubusercontent.com/77181865/157009640-b49d8f47-0ecb-42dc-9074-29769c470222.png)

***StateFlow***는 위에서 다룬 목적때문에 만들어졌다.

![Untitled 32](https://user-images.githubusercontent.com/77181865/157009642-1930248d-053f-4f44-91f1-795eb89acafe.png)

StateFlow는 collector가 없더라도 데이터를 보관하고 있는데, StateFlow에서 여러번 collect가 가능하므로 Activity나 Fragment에서 함께 사용하는 것이 안전하다.

![Untitled 33](https://user-images.githubusercontent.com/77181865/157009649-515354d6-47cb-4db1-b1f5-22fb3322db8a.png)

위의 예시에서 StateFlow의 여러 버전을 사용하며 필요할때마다 값을 업데이트 하는 것을 볼 수 있다. 하지만 이를 반응형으로 보긴 어렵다.

![Untitled 34](https://user-images.githubusercontent.com/77181865/157009652-ff9531a3-e4a4-4c58-8faf-44a962be0b4b.png)

flow를 StateFlow로 변환하여 위에서 다룬 문제를 개선할 수 있다. 이렇게 하면 StateFlow가 Upstream flow에서 모든 update를 받아 최신 값을 저장하게 되는데, DownStream flow에서 collector가 많거나 없을 수 있으므로 ViewModel에서 사용하기에 적합하다.

![Untitled 35](https://user-images.githubusercontent.com/77181865/157009656-b8ab563c-bc71-442c-a52b-82aef1176ef1.png)

flow를 StateFlow로 변환할 때 stateIn 연산자를 함께 사용할 수 있다. **initialValue는 값이 항상 들어있어야** 하기 때문에 사용할 수 있으며 **scope는 coroutine 공유가 시작되는 시점을 제어**하는데 viewModelScope를 사용할 수 있다.

started parameter는 다음 두가지 시나리오를 살펴보면서 알아보도록 하자.

![Untitled 36](https://user-images.githubusercontent.com/77181865/157009661-aeb20f1a-685f-4f39-ba21-2d787b871133.png)

첫번째 시나리오는 flow의 collector인 Activity가 일정 시간 파괴되었다가 다시 생성되는 화면 회전이다.

![Untitled 37](https://user-images.githubusercontent.com/77181865/157009667-93d846cf-b535-45d5-8f16-60e6a8a0ce33.png)

두 번재 시나리오는 홈 화면으로 이동해서 App을 Background로 보내는 것이다.

![Untitled 38](https://user-images.githubusercontent.com/77181865/157009674-f6cdfe6b-2b4c-4bc7-ad3f-f2d57b6de958.png)

첫번째 시나리오(회전 시나리오)에서 최대한 빠르게 전환하기 위해 flow를 다시 시작해서는 안된다. 하지만 두번째 시나리오(Background 이동)에서는 배터리와 리소스를 아끼기 위해 모든 flow를 중단해야만 한다. 

이러한 두가지 케이스 중 어떤 케이스에 속하는지 선별하는 방법으로는 바로 **시간초과**이다.

StateFlow의 수집이 중단되었을 때 모든 Upstream flow를 중단하진 않는다. 대신에 위의 예시처럼 약 5초정도 시간을 기다린다. 해당 시간이 초과되기 전에 flow가 다시 collect 된다면 Upstream flow는 취소되지 않는다.

즉 **WhileSubscribed(5000)**은 이러한 일을 한다.

![Untitled 39](https://user-images.githubusercontent.com/77181865/157009682-bed2b23c-2719-489d-a670-948ad95c31cc.png)

위 그림에서는 홈버튼을 누르기 전에 view가 업데이트를 수신하고 StateFlow는 정상적으로 update flow를 생성하는 것을 설명한다.

![Untitled 40](https://user-images.githubusercontent.com/77181865/157009689-0c9d3638-4657-4cb1-92fb-f6d37d9c78dc.png)

App이 background로 가면서 view는 중단되었고, collection이 멈추었지만 우리가 위에서 설정한 옵션에 따라 Upstream flow를 중단하는데 5초가 소요된다. 5초라는 시간이 초과된 경우 upstream flow가 취소된다.

![Untitled 41](https://user-images.githubusercontent.com/77181865/157009691-5de49197-8761-4c64-81b6-9ac07d1df55c.png)

사용자가 앱을 다시 열 경우 flow는 자동적으로 다시 시작된다.

![Untitled 42](https://user-images.githubusercontent.com/77181865/157009692-3ec38560-691d-4655-9cdf-394e2e094091.png)

그러나 첫번째 시나리오인 화면 회전 시나리오에서는 view는 잠시 중단된다. 따라서 StateFlow는 절대로 다시 복원되지 않고 모든 Upstream flow를 활성상태로 유지하며 아무 일도 없었던 것처럼 사용자에게 회전 인스턴스를 보낸다.

즉, **StateFlow를 사용하여 ViewModel에서 flow를 노출하거나 asLiveData를 사용하여 이와 동일한 작업을 수행하는 것이 좋다.**

## Testing Flows

flow 테스트는 data stream이라 까다로울 수 있기 때문에, 몇 가지 요령에 대해 살펴보겠다. 먼저 두 가지 시나리오를 살펴보자.

![Untitled 43](https://user-images.githubusercontent.com/77181865/157009694-552fbeea-690f-4199-9e33-d6d8cb214ea7.png)

첫 번째 시나리오는 테스트 대상이 무엇이든 UnitUnderTest가 flow를 받는 것이다.

![Untitled 44](https://user-images.githubusercontent.com/77181865/157009698-2a04e789-7d9d-4eb6-b9d2-4abc4737f7fa.png)

가장 쉬운 방법으로는 종속성을 가짜 생산자로 교체하여 테스트를 하는 것이다. 예를 들어 위의 그림처럼 가짜 repository를 만들어서 여러 testcase에 따라 필요한 데이터들을 전송할 수 있을 것이다.

![Untitled 45](https://user-images.githubusercontent.com/77181865/157009701-35e03cfd-4ab9-4720-96c7-1b95753ba684.png)

간단하게 cold flow의 예시를 살펴보겠다. 테스트 자체는 테스트 결과에 대한 assertion을 만드는데 이는 flow 혹은 다른 것들이 될 수도 있을 것이다.

![Untitled 46](https://user-images.githubusercontent.com/77181865/157009703-1e58e0de-26d1-442b-b751-0ed2aa160ac1.png)

두번째 시나리오로 **UnitUnderTest** 가 flow를 노출하고 이 값이나 stream을 검증하고 싶다면 여러가지 방법으로 수집할 수 있다.

![Untitled 47](https://user-images.githubusercontent.com/77181865/157009706-ab450834-6547-41e9-9752-a9e6b2a9c9d4.png)

flow에서 **first( ) 메소드를 호출하면 첫 아이템을 수신하고 수집을 중단한다.**

![Untitled 48](https://user-images.githubusercontent.com/77181865/157009708-72ebd272-dcce-4c90-b756-d60d45a06f4a.png)

또한 take(5)와 같이 연산자를 사용하면 메시지 5개만 수집할 수도 있다.

## Resource

![Untitled 49](https://user-images.githubusercontent.com/77181865/157009712-c1878c1d-2740-41d7-a623-ef9170f708a3.png)
