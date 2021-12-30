# MVVM in Android
---
## CleanArchitecture
![image](https://user-images.githubusercontent.com/67935576/147564987-4589845b-2b81-42a0-a015-59b258ce704a.png)
- �ι�Ʈ C ��ƾ�� ���� ��ȵ� ���α׷��� ö��
- �ٽ� ���ڴ� �� ����Ʈ������ '���ɻ�и�'

## MVC
![image](https://user-images.githubusercontent.com/67935576/147565114-6560ed20-af86-4ab4-b00d-d6a25adf5fd9.png)
- Model, View, Controller�� �̷������ ������ ����
- ��Ʈ�ѷ��� ������� �Է��� �޾� �𵨿� �����͸� ��û, �޾Ƽ� �信 �����͸� ����, �信�� UI�� ��ȭ�� �Ͼ��.
������� ��ư�� ������ ���� �ð��� �����ִ� ���α׷��� ������ ��, ��ư�� Ŭ���ϸ� ��Ʈ�ѷ��� �̸� �Է¹ް� �𵨿� ���� �ð��� ��û, �޾Ƽ� View�� ����, View�� ���� �ð��� ��Ÿ����.
- �ȵ���̵忡���� Activity/Fragment�� View�� Controller�� ������ ���ÿ� �����Ѵ�.
������ ������Ʈ �Ը� Ŀ������ Activity�� ������ ������ ���̰� �ǰ�, �̴� ���������ϱ� ���� ������ ������ ������ �ȴ�.
- Controller�� �ȵ���̵� �����ӿ�ũ�� ���ϰ� �����ϰ� �־� �����׽�Ʈ �����ϱⰡ �����.

## MVP
![image](https://user-images.githubusercontent.com/67935576/147565663-6ac72378-dfa0-4c05-a1eb-93f903b46734.png)
- Model, View, Presenter�� �̷������ ������ ����
- Presenter�� ����Ͻ� ������ ����ϰ�, View�� ������ UI�� ��ȭ�� ����Ѵ�.
- �׷��� ������ View�� Presenter ������ �������� ���ϰ�, �ݵ�� 1:1������ �̷�� ���Ƿ� ���� Presenter�� �ʿ���.


## MVVM
![image](https://user-images.githubusercontent.com/67935576/147565884-19ec0ced-9903-407e-807c-ac95d6c4bd93.png)
- Model, View, ViewModel�� �̷������ ������ ����
- View�� ViewModel��, ViewModel�� Repository(Usecase)��, Repository�� Data�� �޾ƿ��⸸ �ϸ� �Ǵ� ���ɻ� �и��� �̷����.
- MVP�� Presenter�� �޸� ViewModel�� View�� ���� ������ �ʿ� ����.
- �ȵ���̵忡���� AAC-ViewModel�� ����� �����ֱ⿡ �°� �����͸� ������ �� �ִ�.
�׷��� �� ��쿡�� View�� ViewModel�� 1:1���踦 �����ϴ°��� ����. (View�� �����ֱ⶧��)
- �ȵ���̵忡���� DataBinding ����� ����Ͽ� View�� ViewModel ������ �������� ���� ���� �� �ִ�.

## MVVM in Android ����

```kotlin
    dataBinding {
        enabled = true
    }
```
app���� gradle ���Ͽ� databinding�� ����� �� �ְ� �߰��Ѵ�.

```kotlin
    private lateinit var binding: ActivityMainBinding
    private val viewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }
```
view(activity)���� viewModel�� �������ش�.

```kotlin
class MainViewModel : ViewModel() {

    private val myRepository by lazy {
        DefaultMyRepository()
    }
    val data: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    ...(�߷�)
```
viewModel������ repository ��ü�� ����� �����͸� ó���ϰ�, �����͸� ���� LiveData�� ����Ѵ�.

```xml
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools">

    <data>
        <variable
            name="viewModel"
            type="com.seoplee.androidstudy.screen.main.MainViewModel" />
    </data>
    (...)
            <EditText
            android:id="@+id/emailEditText"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:text="@={viewModel.data}"
    (...)
```
xml ���Ͽ��� databinding�� Ȱ���Ͽ� viewModel�� Livedata�� ��������� �����͸� ���ε� �Ͽ� ����Ѵ�.


```kotlin
    viewModel.data.observe(this) {
        ...(ui ó��)
    }
```
view���� �ش� LiveData�� observe�Ͽ� ����Ѵ�.