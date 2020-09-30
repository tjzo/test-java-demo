# 测试 

## 白盒测试 

白盒测试由开发人员编写，以验证代码是否按照编写时的预期执行。 

那么，我自己写的代码，还会不按照我的预期执行？ 
 
当然，大部分情况下肯定不会翻车，但是偶尔会有以下几种情况： 

### 我的想法是错的 

之前在面试时遇到了以下一段代码 

```javascript
const f = (m) => {
    const months = [1, 3, 5, 7, 8, 10, 12]
    if (months.indexOf(m)) {
        // doing something
    }
}
```

面试者想要表达的意思是如果months这些月份里是否包含m，粗看以下貌似没什么问题。 
 
其实稍微细想一下就会发现，indexOf方法返回元素在list中的下标，或者-1。 
 
那么如果元素不存在，则得到-1，而-1是true，正好相反。 
 
如果元素在第一个，则得到0，而0是false，也是相反。 
 
只有元素的第二个或者后面的位置，才是符合预期的。 
 
这是一段很具有欺骗性的代码，但是如果加上测试，则很快就会发现问题。  

### 我的改动影响到了其他的地方 
 
比如我改了class A，而class B依赖了class A。 

class A是符合预期的，但是他的改动意外影响了class B的逻辑，从而引入了新的bug。 

当然出现这种问题的原因还是代码的耦合度过高。 

## 黑盒测试 

黑盒测试是由专门的测试人员进行，以验证整个产品的逻辑是否符合预期。 

黑盒测试更多的像是将产品与产品需求文档进行比对。 

整个测试过程不关注代码运行的细节，只关注对应的输入是否能得到对应的输出。 

那么假如白盒测试已经很完善了，是否就不需要黑盒测试了呢？ 

答案显然是否定的。 

考虑一种情况，假如我们的产品是一款手机app，由于安卓/IOS系统版本不同，手机型号不同，屏幕尺寸不同，同样的业务逻辑，就可能会有不同的结果，同样的页面，也会有不同的显示。 

普通的白盒测试是覆盖不到那么多的手机型号/系统版本的(黑盒测试其实也覆盖不全)，但是黑盒测试则可以校验市面上主流的手机型号/系统版本。 

## Maven配置自动化测试 

maven的生命周期中有一步是test，位于compile之后，package之前。 

test步骤会自动执行./test目录下的所有@Test注解的函数。 

如果test写的很多，为了快速编译，想要跳过test步骤，可以加上-DskipTests。比如 ```mvn clean package -DskipTests```。 

## Jenkins配置自动化CI 

CI(Continuous Integration/持续集成)是指代码提交到仓库后，合并前所执行的一些检查，比如lint、test等，具体可以参考https://www.redhat.com/zh/topics/devops/what-is-ci-cd。 

Jenkins是一款业内比较主流的CI/CD软件。 

在Jenkins上，我们可以预先配置好Job，Jenkins有maven插件，也可以自定义执行脚本。 

所有的job可以通过手动，定时任务，前置任务或者Webhook(比如git仓库有push时触发构建)的方式触发。 

一种通用的工作流可以表示如下： 

```git push``` => ```webhook notify Jenkins``` => ```trig Jenkins job``` => ```result back to git``` => ```merge code or reject/modify``` 

## Java测试的编写 

Java项目的测试一般写在src/test目录下，目录结构与src/main一致，也有java/和resources/。

### 单元测试(Junit4为例)

Java的单元测试需要加上junit的依赖，scope为test。

常用注解：

@BeforeClass

在整个测试前执行，全局只执行一边，比如install module。

@Before

在每个测试前执行，比如准备测试用户/session。

@Test

具体执行的测试。

@After

在每个测试后执行，比如清理数据库。

@AfterClass

在所有测试后执行，比如shutdown server。

@Ignore

不自动执行的测试。

@Rule

统一的测试规则，比如超时时间，预期错误等，也可以自动义rule。

### Mock测试

对于一些外部依赖，比如数据库、redis、rpc、外部服务等，在测试时难以构建环境的，可以使用mock的方式。这里数据库也可以使用内存数据库来实现。

- [Mockito](https://howtodoinjava.com/mockito/)
- [easymock](http://www.easymock.org/)

以下以Mockito为例

mock方法入参是一个class，返回一个该class的mock对象。

when方法入参是一个方法调用，返回一个OngoingStubbing对象。

OngoingStubbing.thenReturn可以指定具体的返回值。

OngoingStubbing.thenThrow可以指定抛出异常。

OngoingStubbing.thenAnswer接受一个lambda(Answer)，指定返回值的生产方法。

使用方法为：

```java
when(mockObj.invoke(arg1, arg2...)).thenReturn(returnValue);

when(mockObj.invoke(arg1, arg2...)).thenThrow(expectedException);

when(mockObj.invoke(arg1, arg2...)).thenAnswer(i -> v);
```

调用验证过程为：

```java
verify(mockObj).invoke(arg1, arg2...); //验证一个方法是否按指定的参数调用

verify(mockObj, times(2)).invoke(arg1, arg2...); //验证一个方法是否按指定的参数调用了两次

verify(mockObj, never()).invoke(arg1, arg2...); //验证一个方法是否没有按指定的参数调用，等价于times(0)

verify(mockObj, atLeast(2)).invoke(arg1, arg2...); //验证一个方法是否至少按指定的参数调用了两次

verify(mockObj, atLeastOnce()).invoke(arg1, arg2...); //验证一个方法是否至少按指定的参数调用了一次，等价于atLeast(1)

verify(mockObj, atMost(3)).invoke(arg1, arg2...); //验证一个方法是否至多按指定的参数调用了三次
```

执行顺序验证过程为：

```java
// 验证mockObj先执行invoke，再执行invoke2
InOrder order = inOrder(mockObj);

order.verify(mockObj).invoke(arg1, arg2...);

order.verify(mockObj).invoke2(arg3, arg4...);

// 验证多个mockObj的执行顺序
InOrder order = inOrder(mockObj1, mockObj2...);

order.verify(mockObj1).invoke(arg1, arg2...);

order.verify(mockObj2).invoke2(arg3, arg4...);
```

注解注入mock对象

@Mock 注入一个mock对象，不会创建真实对象。

@Spy 注入一个spy对象，会创建真实对象，并监听该对象的方法调用。

@Captor 注入一个参数收集器，可以把调用参数收集起来，以便后续的assert。

@InjectMocks 创建一个真实对象，并将mock对象注入到对应的字段。

@InjectMocks的注意点：

- Use @InjectMocks to create class instances which needs to be tested in test class.
- Use @InjectMocks when actual method body needs to be executed for a given class.
- Use @InjectMocks when we need all internal dependencies initialized with mock objects to work method correctly.
- Use @Mock to create mocks which are needed to support testing of class to be tested.
- Annotated class (to be tested) dependencies with @Mock annotation.
- We must define the when-thenRetrun methods for mock objects which class methods will be invoking during actual test execution.
- 一个对象持有多个同类型的字段时，需要将该对象的字段名与测试类的各个字段名意义对应。

初始化注入需要调用MockitoAnnotations.initMocks(this)方法，一般写在@Before方法里，或者在测试类上加上@RunWith(MockitoJUnitRunner.class)。

### 使用Guice注入fake对象

通过Guice可以很方便的将需要mock的对象绑定到一个fake的对象，这样可以省去大量的代码。

但是这种方式和mock测试各有优劣，最明显的不足是它无法验证方法的调用(次数、顺序)。

### Servlet测试

- [HttpUnit](http://httpunit.sourceforge.net/doc/api/index.html) 这个项目基本不怎么维护了。
- 使用Jetty做web容器。

### 压力测试([JMeter](https://jmeter.apache.org/)) 

- TestPlan： 测试计划，每一个测试都为一个测试计划。

- ThreadGroup：线程组，所有的controller、sampler必须在线程组下。不过有一些特许的控件如Listeners可以直接在TestPlan下。

- Sampler：采样器，也就是我们各种性能测试和负载测试的收集器。如http采样器(HTTPSampler)。

- Controller：控制器，主要用于压力测试逻辑的处理，如LoopController，控制线程的循环次数，是永久还是循环压力测试多次。 

JMeter可以直接从jar包启动，这里我们以单元测试的方式来做简单的测试。

## 测试覆盖率

- 类覆盖
- 方法覆盖
- 行覆盖
- 分支覆盖

覆盖率可以用Idea的Run 'All Tests' With Coverage，跑完后会生成coverage报告，并且Project视图里每一个包/文件都会展示覆盖率。

也可以使用maven插件jacoco。

### 行覆盖与分支覆盖的区别

这两个是一样的吗？看以下sample

```javascript
const max = (a, b) => {
    return a > b ? a : b
} 
```

要做到行覆盖率100%，只需要一组case(1, 0)即可，但要做到分支覆盖率100%则至少需要两组case(1, 0) & (0, 1)。

永远无法达到的100%：

```java
public static Integer max(int a, int b) {
    Optional<Integer> max = Lists.newArrayList(a, b).stream().max(Integer::compareTo);
    return max.orElse(null);
}
```

这里无论怎么构造测试用例，都无法返回null。

所以，我们无法要求100%的覆盖率，降低要求，99.99%就够了。

## Practice

订单到期日期的问题：

一个时长为一个月的订购订单，到期日期为下个月的这一天，或者下个月的最后一天。不能使用日期相关的类。

请实现以下接口，并覆盖测试：

```java
public class Date {
    private final int year;
    private final int month;
    private final int day;

    public Date(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }
}

public interface Order {
    Date expire(Date date);
}
```