[![Build Status](https://travis-ci.org/Martori/rx-redux.svg?branch=master)](https://travis-ci.org/Martori/rx-redux) [![GitHub release](https://img.shields.io/github/release/martori/rx-redux.svg)](https://github.com/Martori/rx-redux/releases/latest) [![license](https://img.shields.io/github/license/martori/rx-redux.svg)](https://github.com/Martori/rx-redux/blob/master/LICENSE)
# rx-redux
A Redux implementation for Java made with [RxJava v2](https://github.com/ReactiveX/RxJava)

## Installation 
```gradle
repositories {
	maven { url = "https://jitpack.io" }
}

dependencies {
	compile "com.github.martori:rx-redux:0.1.1"
}
```

## Usage
### Define a class for your state
```java
public class State{
  private int count;
  public State(int count){
  	this.count = count;
  }
  public int getCount(){
  	return count;
  }
}
```
### Define a class for your actions
```java
public enum Action{
    INC,
    DEC
}
```
### Define a reducer function or implement the RxReducer interface
```java
public State reduce(State state, Action action) {
  switch (action){
    case INC:
    	return new State(state.getCount + 1);
    case DEC:
    	return new State(state.getCount - 1);
    default:
    	return state;
  }
}
```
### Create your store
```java
RxStore<State, Action> store = new RxStore(initialState, reducer);
```
### Dispatch actions to the store
```java
store.dispatch(Action.INC);
```
### Get the current state
```java
State currentState = store.getState();
```
### Subscribe/Unsubscribe to State changes
```java
Consumer<State> callback = store.subscribe((state) -> System.out.println(state));
store.unsubscribe(callback);
```
### Work with a State Observable
```java
Observable<State> states = store.getStates();
//here you can work with your states in an Rx fashion: mapping, reducing, debouncing, etc.
//simple and absurd example: measure the length of your states when converted to strings
states.map(state -> state.toString())
	.map(s -> s.length())
    .map(l -> "the current state is "+l+" characters long")
    .subscribe(System.out::println);
```

## Consider using
 * [Retrolambda](https://github.com/evant/gradle-retrolambda) To hava lambdas available in Java 6, 7 and early Android versions
 * [Anvil](https://github.com/zserge/anvil): A React-like library for Android, to make easy mobile reactive apps
 * [Immutables](https://immutables.github.io/): A Java annotation processors to generate simple, safe and consistent value objects, to create your immutable states

## License
MIT License

Copyright (c) 2017 Eric Martori López

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
